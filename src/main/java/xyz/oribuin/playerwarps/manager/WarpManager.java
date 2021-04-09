package xyz.oribuin.playerwarps.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import xyz.oribuin.orilibrary.manager.Manager;
import xyz.oribuin.playerwarps.PlayerWarps;
import xyz.oribuin.playerwarps.obj.Warp;

public class WarpManager extends Manager {

    private final PlayerWarps plugin = (PlayerWarps) this.getPlugin();

    public WarpManager(PlayerWarps plugin) {
        super(plugin);
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    public void teleportToWarp(Player player, Warp warp) {
        Location loc = warp.getLocation();

        if (warp.isLocked()) {

        }

        player.teleportAsync(loc, PlayerTeleportEvent.TeleportCause.COMMAND);
    }


}
