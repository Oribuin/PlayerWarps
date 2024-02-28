package xyz.oribuin.playerwarps.gui.menu;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.playerwarps.PlayerWarpsPlugin;
import xyz.oribuin.playerwarps.gui.MenuItem;
import xyz.oribuin.playerwarps.gui.PluginMenu;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.manager.LocaleManager;
import xyz.oribuin.playerwarps.model.Warp;
import xyz.oribuin.playerwarps.util.ItemBuilder;
import xyz.oribuin.playerwarps.util.WarpUtils;

import java.util.ArrayList;
import java.util.List;

public class WarpsMenu extends PluginMenu {

    private final DataManager manager = this.rosePlugin.getManager(DataManager.class);
    private final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

    public WarpsMenu() {
        super(PlayerWarpsPlugin.get());
    }

    /**
     * Open the warps menu for the player
     *
     * @param player The player to open the menu for
     */
    public void open(Player player) {
        PaginatedGui gui = this.createPagedGUI(player);
        this.sync(() -> gui.open(player));

        CommentedConfigurationSection extraItems = this.config.getConfigurationSection("extra-items");
        if (extraItems != null) {
            extraItems.getKeys(false).forEach(s -> MenuItem.create(this.config)
                    .path("extra-items." + s)
                    .player(player)
                    .place(gui));
        }

        // Add paginated items to the GUI
        MenuItem.create(this.config)
                .path("next-page")
                .player(player)
                .placeholders(this.getPagePlaceholders(gui))
                .action(event -> gui.next())
                .place(gui);

        MenuItem.create(this.config)
                .path("previous-page")
                .player(player)
                .placeholders(this.getPagePlaceholders(gui))
                .action(event -> gui.previous())
                .place(gui);

        // Add all the warps to the gui
        this.async(() -> {
            this.manager.getWarps().forEach((s, warp) -> {
                StringPlaceholders placeholders = this.getWarpPlaceholders(warp);
                ItemStack warpIcon = WarpUtils.deserialize(this.config, player, "warp-icon", placeholders);
                if (warp.getIcon() != null) warpIcon = warp.getIcon();
                if (warpIcon == null) return;

                List<String> configLore = this.config.getStringList("warp-icon.lore");
                List<String> newLore = new ArrayList<>();

                for (String configLine : configLore) {
                    if (!configLine.contains("%warp_description%")) {
                        newLore.add(this.locale.format(player, configLine, placeholders));
                        continue;
                    }

                    if (warp.getDescription().isEmpty()) continue;

                    // Add the description to the lore
                    warp.getDescription().forEach(line -> newLore.add(this.locale.format(player, line, placeholders)));
                }

                ItemStack newIcon = new ItemBuilder(warpIcon)
                        .name(this.locale.format(player, this.config.getString("warp-icon.name"), placeholders))
                        .lore(newLore)
                        .build();

                gui.addItem(new GuiItem(newIcon, event -> warp.teleport(player)));
            });

            // Update the gui with the new items
            gui.update();

            if (this.reloadTitle()) {
                this.sync(() -> gui.updateTitle(this.locale.format(player, this.config.getString("gui-settings.title"), this.getPagePlaceholders(gui))));
            }
        });
    }

    private StringPlaceholders getWarpPlaceholders(Warp warp) {
        return StringPlaceholders.builder()
                .add("warp_id", warp.getId())
                .add("warp_name", warp.getDisplayName())
                .add("warp_owner", warp.getOwnerName())
                .add("warp_price", String.valueOf(warp.getTeleportFee()))
                .add("warp_location", this.formatLocation(warp.getPosition()))
                .build();
    }

    private String formatLocation(Location location) {
        return String.format("%s, %s, %s", location.getBlockX() + 0.5, location.getBlockY() + 0.5, location.getBlockZ() + 0.5);
    }

    /**
     * @return The name of the GUI
     */
    @Override
    public String getMenuName() {
        return "warps-menu";
    }

}
