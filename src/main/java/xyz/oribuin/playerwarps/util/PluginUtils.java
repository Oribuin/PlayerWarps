package xyz.oribuin.playerwarps.util;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public final class PluginUtils {

    /**
     * Gets the maximum allowed warps a player can have
     *
     * @param player The player
     * @return The amount of warps the player can have.
     */
    public static int getMaxWarps(Player player) {
        int amount = 1;

        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            String target = info.getPermission().toLowerCase();

            if (target.startsWith("playerwarps.max.") && info.getValue()) {
                try {
                    amount = Math.max(amount, Integer.parseInt(target.substring(target.lastIndexOf('.') + 1)));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return amount;
    }

}
