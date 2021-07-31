package xyz.oribuin.playerwarps.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.playerwarps.obj.Warp;

public class WarpCreateEvent extends Event implements Cancellable {

    private static final HandlerList list = new HandlerList();
    private final Warp warp;
    private boolean cancelled = false;

    public WarpCreateEvent(Warp warp) {
        super(false);
        this.warp = warp;
    }

    public static HandlerList getHandlerList() {
        return list;
    }

    public Warp getAuction() {
        return warp;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return list;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
