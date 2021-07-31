package xyz.oribuin.playerwarps.obj;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.UUID;

public class Warp {

    private final UUID owner;
    private final String name;
    private Location location;
    private String displayName;
    private String description = null;
    private Material icon = Material.PAPER;
    private boolean locked = false;

    public Warp(final UUID owner, final Location location, String name) {
        this.owner = owner;
        this.location = location;
        this.name = name;
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getLocation() {
        return location;
    }

    public Warp setLocation(Location location) {
        this.location = location;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Warp setDescription(String description) {
        this.description = description;
        return this;
    }

    public Material getIcon() {
        return icon;
    }

    public Warp setIcon(Material icon) {
        this.icon = icon;
        return this;
    }

    public boolean isLocked() {
        return locked;
    }

    public Warp setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Warp setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

}
