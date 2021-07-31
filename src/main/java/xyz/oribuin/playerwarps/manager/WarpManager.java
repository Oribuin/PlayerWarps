package xyz.oribuin.playerwarps.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import xyz.oribuin.orilibrary.manager.Manager;
import xyz.oribuin.orilibrary.util.StringPlaceholders;
import xyz.oribuin.playerwarps.PlayerWarps;
import xyz.oribuin.playerwarps.obj.Warp;

public class WarpManager extends Manager {

    private final PlayerWarps plugin = (PlayerWarps) this.getPlugin();
    private final MessageManager msg = this.plugin.getManager(MessageManager.class);

    public WarpManager(PlayerWarps plugin) {
        super(plugin);
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    /**
     * Teleport a player to a warp
     *
     * @param player The player.
     * @param warp   The warp.
     */
    public void teleportToWarp(Player player, Warp warp) {
        Location loc = warp.getLocation();

        if (warp.isLocked() && !player.hasPermission("playerwarps.bypass")) {
            msg.send(player, "warp-locked");
            return;
        }

        msg.send(player, "teleported-to-warp", StringPlaceholders.single("warp", warp.getName()));
        if (!plugin.isPaperSpigot()) {
            player.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
            return;
        }

        player.teleportAsync(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }


}
