package xyz.oribuin.playerwarps.model;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.playerwarps.PlayerWarpsPlugin;
import xyz.oribuin.playerwarps.manager.DataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Warp {

    private final @NotNull String id;
    private @NotNull String displayName;
    private @NotNull UUID owner;
    private @NotNull Location position;
    private @NotNull List<String> description;
    private @Nullable ItemStack icon;
    private @Nullable String ownerName;
    private @NotNull List<UUID> banned;
    private @NotNull List<UUID> visitors;
    private boolean isPublic;
    private long creationTime;
    private double teleportFee;

    /**
     * Create a new instance of a playerwarp object
     *
     * @param id       The name of the warp
     * @param owner    The owner of the warp
     * @param position The position of the warp
     */
    public Warp(@NotNull String id, @NotNull UUID owner, @NotNull Location position) {
        this.id = id;
        this.displayName = id;
        this.owner = owner;
        this.position = position;
        this.description = new ArrayList<>();
        this.icon = null;
        this.ownerName = Bukkit.getOfflinePlayer(owner).getName();
        this.banned = new ArrayList<>();
        this.visitors = new ArrayList<>();
        this.isPublic = true;
        this.creationTime = System.currentTimeMillis();
        this.teleportFee = 0.0;
    }

    /**
     * Update the warp in the database
     */
    public void save() {
        PlayerWarpsPlugin.get().getManager(DataManager.class).save(this);
    }

    /**
     * Teleport a player to the designated player warp
     *
     * @param player The player to teleport
     * @return If the teleport was successful
     */
    public boolean teleport(Player player) {
        if (this.banned.contains(player.getUniqueId())) {
            // TODO: Player is banned
            return false;
        }

        if (this.teleportFee > 0) {
            // TODO: Charge the player
            return true;
        }

        // Teleport Player
        player.setFallDistance(0);
        player.setVelocity(player.getVelocity().setY(0));
        return PaperLib.teleportAsync(player, this.position, PlayerTeleportEvent.TeleportCause.PLUGIN).isDone();
    }

    /**
     * Check if a player is banned from teleporting to a warp.
     *
     * @param player The player to check
     */
    public boolean isBanned(OfflinePlayer player) {
        return this.banned.contains(player.getUniqueId());
    }

    public @NotNull String getId() {
        return id;
    }

    public @NotNull String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@NotNull String displayName) {
        this.displayName = displayName;
    }

    public @NotNull UUID getOwner() {
        return owner;
    }

    public void setOwner(@NotNull UUID owner) {
        this.owner = owner;
    }

    public @NotNull Location getPosition() {
        return position;
    }

    public void setPosition(@NotNull Location position) {
        this.position = position;
    }

    public @NotNull List<String> getDescription() {
        return description;
    }

    public void setDescription(@NotNull List<String> description) {
        this.description = description;
    }

    public @Nullable ItemStack getIcon() {
        return icon;
    }

    public void setIcon(@Nullable ItemStack icon) {
        this.icon = icon;
    }

    public @Nullable String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(@Nullable String ownerName) {
        this.ownerName = ownerName;
    }

    public @NotNull List<UUID> getBanned() {
        return banned;
    }

    public void setBanned(@NotNull List<UUID> banned) {
        this.banned = banned;
    }

    public @NotNull List<UUID> getVisitors() {
        return visitors;
    }

    public void setVisitors(@NotNull List<UUID> visitors) {
        this.visitors = visitors;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public double getTeleportFee() {
        return teleportFee;
    }

    public void setTeleportFee(double teleportFee) {
        this.teleportFee = teleportFee;
    }

}
