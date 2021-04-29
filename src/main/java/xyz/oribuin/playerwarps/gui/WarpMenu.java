package xyz.oribuin.playerwarps.gui;

import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import xyz.oribuin.playerwarps.PlayerWarps;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.manager.WarpManager;
import xyz.oribuin.playerwarps.obj.Warp;

import java.util.ArrayList;
import java.util.List;

import static xyz.oribuin.orilibrary.util.HexUtils.colorify;

public class WarpMenu {

    private final List<Warp> warps = new ArrayList<>();
    private final PlayerWarps plugin;
    private final DataManager data;

    private final List<Player> viewers = new ArrayList<>();

    public WarpMenu(final PlayerWarps plugin) {
        this.plugin = plugin;
        this.data = this.plugin.getManager(DataManager.class);
    }

    public void createMenu(Player player) {
        if (!viewers.contains(player)) viewers.add(player);

        this.warps.clear();
        this.warps.addAll(this.data.getCachedWarps());

        final PaginatedGui gui = new PaginatedGui(6, "Player Warps");
        gui.setUpdating(true);
        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);

            ((Player) event.getWhoClicked()).updateInventory();
        });

        this.addAllItems(gui, player);
        gui.open(player);
    }

    private void addAllItems(PaginatedGui gui, Player player) {

        for (Warp warp : this.warps) {
            final ItemBuilder builder = ItemBuilder.from(warp.getIcon())
                    .setName(colorify("#0a258a&l" + warp.getName()))
                    .setLore(colorify("&7" + warp.getDescription()));

            gui.addItem(builder.asGuiItem(event -> this.plugin.getManager(WarpManager.class).teleportToWarp((Player) event.getWhoClicked(), warp)));
        }
    }

}
