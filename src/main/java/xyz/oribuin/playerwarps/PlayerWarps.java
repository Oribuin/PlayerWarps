package xyz.oribuin.playerwarps;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import xyz.oribuin.orilibrary.OriPlugin;
import xyz.oribuin.playerwarps.command.CmdPlayerWarp;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.manager.MessageManager;
import xyz.oribuin.playerwarps.manager.WarpManager;

public class PlayerWarps extends OriPlugin {

    public static Economy getEconomy() {
        return Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
    }

    @Override
    public void enablePlugin() {

        // Detect Vault
        if (!hasPlugin("Vault"))
            return;

        // Register Managers Async
        this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
            this.getManager(DataManager.class);
            this.getManager(MessageManager.class);
            this.getManager(WarpManager.class);
        });

        final MessageManager msg = this.getManager(MessageManager.class);

        // Register Command
        new CmdPlayerWarp(this).register(sender -> msg.send(sender, "player-only"), sender -> msg.send(sender, "no-perm"));

    }

    @Override
    public void disablePlugin() {

    }

}
