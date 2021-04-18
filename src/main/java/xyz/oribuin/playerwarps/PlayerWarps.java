package xyz.oribuin.playerwarps;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import xyz.oribuin.orilibrary.OriPlugin;
import xyz.oribuin.playerwarps.command.CmdPlayerWarp;
import xyz.oribuin.playerwarps.gui.WarpMenu;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.manager.MessageManager;
import xyz.oribuin.playerwarps.manager.WarpManager;

public class PlayerWarps extends OriPlugin {

    private WarpMenu warpMenu;

    @Override
    public void enablePlugin() {

        // Detect Vault
        if (!hasPlugin("Vault")) return;

        // Register Managers Async
        this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
            this.getManager(DataManager.class);
            this.getManager(MessageManager.class);
            this.getManager(WarpManager.class);
        });

        FileConfiguration config = this.getManager(MessageManager.class).getMessageConfig();

        // Register Command
        new CmdPlayerWarp(this).register(config.getString("player-only"), config.getString("invalid-permission"));

        // Register GUI
        this.warpMenu = new WarpMenu(this);
    }

    @Override
    public void disablePlugin() {

    }

    /**
     * Check if the server has a plugin enabled.
     *
     * @param pluginName The plugin
     * @return true if plugin is enabled.
     */
    private boolean hasPlugin(String pluginName) {
        Plugin plugin = this.getServer().getPluginManager().getPlugin(pluginName);

        if (plugin == null || !plugin.isEnabled()) {
            this.getLogger().severe("Please install " + pluginName + " to use this plugin.");
            this.getLogger().severe("Disabling...");
            this.getServer().getPluginManager().disablePlugin(this);
            return false;
        }

        return true;
    }

    public static Economy getEconomy() {
        return Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
    }

    public WarpMenu getWarpMenu() {
        return warpMenu;
    }
}
