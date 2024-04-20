package xyz.oribuin.playerwarps;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.oribuin.playerwarps.gui.MenuProvider;
import xyz.oribuin.playerwarps.hook.VaultProvider;
import xyz.oribuin.playerwarps.manager.CommandManager;
import xyz.oribuin.playerwarps.manager.ConfigurationManager;
import xyz.oribuin.playerwarps.manager.ConfigurationManager.Setting;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.manager.LocaleManager;
import xyz.oribuin.playerwarps.model.Warp;
import xyz.oribuin.playerwarps.util.WarpUtils;

import java.util.List;

public class PlayerWarpsPlugin extends RosePlugin {

    private static PlayerWarpsPlugin instance;

    public PlayerWarpsPlugin() {
        super(
                -1, // The resource id of the plugin
                -1, // The project id of the plugin
                ConfigurationManager.class, // The configuration manager
                DataManager.class, // The data manager
                LocaleManager.class, // The locale manager
                CommandManager.class // The command manager
        );

        instance = this;
    }

    @Override
    public void enable() {
        MenuProvider.reload(); // Load the menus
    }

    @Override
    public void reload() {
        super.reload();
        MenuProvider.reload();
        Bukkit.getScheduler().cancelTasks(this);

        if (Setting.WARP_UPKEEP_ENABLED.getBoolean()) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::checkUpkeep, 20 * 60L, 20 * 60L);
        }
    }

    @Override
    public void disable() {
    }

    /**
     * Check for upkeep of warps and remove them if the owner cannot afford the upkeep
     */
    public void checkUpkeep() {
        if (!Setting.WARP_UPKEEP_ENABLED.getBoolean() || Setting.WARP_UPKEEP_COST.getDouble() <= 0) return;
        LocaleManager locale = this.getManager(LocaleManager.class);

        long upkeepDelay = WarpUtils.parseTime(Setting.WARP_UPKEEP_INTERVAL.getString());
        double upkeepCost = Setting.WARP_UPKEEP_COST.getDouble();
        List<Warp> toUpdate = this.getManager(DataManager.class).getWarps().values()
                .stream()
                .filter(warp -> warp.getLastUpkeepTime() + upkeepDelay < System.currentTimeMillis())
                .toList();

        if (toUpdate.isEmpty()) return;

        // Warn the online owners about their warps if they cannot afford the upkeep
        toUpdate.forEach(warp -> {
            Player owner = Bukkit.getPlayer(warp.getOwner());
            if (owner == null) return;

            if (!VaultProvider.get().has(owner, upkeepCost)) {
                locale.sendMessage(owner, "upkeep-warn", StringPlaceholders.of("warp", warp.getId(), "cost", upkeepCost));
            }
        });

        // Take the upkeep cost from the owner of the warp
        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> toUpdate.forEach(warp -> {
            StringPlaceholders placeholders = StringPlaceholders.of("warp", warp.getId(), "cost", upkeepCost);

            OfflinePlayer owner = Bukkit.getOfflinePlayer(warp.getOwner());
            VaultProvider provider = VaultProvider.get();
            if (!owner.hasPlayedBefore()) return;

            Player ownerPlayer = owner.getPlayer();

            // Remove the warp if the owner does not have enough money
            if (!provider.has(owner, upkeepCost)) {
                if (ownerPlayer != null) {
                    locale.sendMessage(ownerPlayer, "upkeep-fail", placeholders);
                }

                this.getManager(DataManager.class).delete(warp.getId());
                return;
            }

            if (ownerPlayer != null) {
                locale.sendMessage(ownerPlayer, "upkeep-success", placeholders);
            }

            provider.take(owner, upkeepCost);
            warp.setLastUpkeepTime(System.currentTimeMillis());
            warp.save();
        }), 20 * 60L);
    }


    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of();
    }

    public static PlayerWarpsPlugin get() {
        return instance;
    }

}
