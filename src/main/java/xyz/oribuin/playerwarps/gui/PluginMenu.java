package xyz.oribuin.playerwarps.gui;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.triumphteam.gui.components.ScrollType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import dev.triumphteam.gui.guis.ScrollingGui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.playerwarps.PlayerWarpsPlugin;
import xyz.oribuin.playerwarps.manager.LocaleManager;
import xyz.oribuin.playerwarps.util.WarpUtils;

import java.io.File;

public abstract class PluginMenu {

    protected final RosePlugin rosePlugin;
    protected CommentedFileConfiguration config;

    public PluginMenu(RosePlugin rosePlugin) {
        this.rosePlugin = rosePlugin;
    }

    /**
     * @return The name of the GUI
     */
    public abstract String getMenuName();

    /**
     * Create the menu file if it doesn't exist and add the default values
     */
    public void load() {
        File menuFile = WarpUtils.createFile(this.rosePlugin, "menus", this.getMenuName() + ".yml");
        this.config = CommentedFileConfiguration.loadConfiguration(menuFile);
        this.config.save(menuFile);
    }


    /**
     * Create a paged GUI for the given player
     *
     * @param player The player to create the GUI for
     * @return The created GUI
     */
    protected final @NotNull PaginatedGui createPagedGUI(Player player) {

        int rows = this.config.getInt("gui-settings.rows");
        String preTitle = this.config.getString("gui-settings.pre-title", "PlayerWarps");

        return Gui.paginated()
                .rows(rows == 0 ? 6 : rows)
                .title(this.format(player, preTitle))
                .disableAllInteractions()
                .create();
    }

    /**
     * Create a GUI for the given player without pages
     *
     * @param player The player to create the GUI for
     * @return The created GUI
     */
    protected final @NotNull Gui createGUI(Player player) {
        int rows = this.config.getInt("gui-settings.rows");
        String preTitle = this.config.getString("gui-settings.pre-title", "PlayerWarps");

        return Gui.gui()
                .rows(rows == 0 ? 6 : rows)
                .title(this.format(player, preTitle))
                .disableAllInteractions()
                .create();
    }

    /**
     * Scrolling gui for the given player
     *
     * @param player The player to create the GUI for
     * @return The created GUI
     */
    protected final @NotNull ScrollingGui createScrollingGui(Player player, ScrollType scrollType) {

        int rows = this.config.getInt("gui-settings.rows");
        String preTitle = this.config.getString("gui-settings.pre-title", "PlayerWarps");

        return Gui.scrolling()
                .scrollType(scrollType)
                .rows(rows == 0 ? 6 : rows)
                .pageSize(0)
                .title(this.format(player, preTitle))
                .disableAllInteractions()
                .create();
    }

    /**
     * Format a string with placeholders and color codes
     *
     * @param player The player to format the string for
     * @param text   The string to format
     * @return The formatted string
     */
    protected final Component format(Player player, String text) {
        return Component.text(
                this.rosePlugin
                        .getManager(LocaleManager.class)
                        .format(player, text, StringPlaceholders.empty())
        );
    }

    /**
     * Format a string with placeholders and color codes
     *
     * @param player       The player to format the string for
     * @param text         The text to format
     * @param placeholders The placeholders to replace
     * @return The formatted string
     */
    protected final Component format(Player player, String text, StringPlaceholders placeholders) {
        return Component.text(
                this.rosePlugin
                        .getManager(LocaleManager.class)
                        .format(player, text, placeholders)
        );
    }

    /**
     * Get the page placeholders for the gui
     *
     * @param gui The gui
     * @return The page placeholders
     */
    protected StringPlaceholders getPagePlaceholders(PaginatedGui gui) {
        return StringPlaceholders.builder()
                .add("page", gui.getCurrentPageNum())
                .add("total", Math.max(gui.getPagesNum(), 1))
                .add("next", gui.getNextPageNum())
                .add("previous", gui.getPrevPageNum())
                .build();

    }

    /**
     * Run a task synchronously
     *
     * @param runnable The task to run
     */
    public final void sync(Runnable runnable) {
        PlayerWarpsPlugin.SCHEDULER.scheduling()
                .globalRegionalScheduler()
                .run(runnable);
    }

    /**
     * Run a task asynchronously
     *
     * @param runnable The task to run
     */
    public final void async(Runnable runnable) {
        PlayerWarpsPlugin.SCHEDULER.scheduling()
                .asyncScheduler()
                .run(runnable);
    }

    /**
     * @return Whether the title should be updated (Used for page placeholders)
     */
    public boolean reloadTitle() {
        return this.config.getBoolean("gui-settings.update-title", true);
    }

    /**
     * @return Whether the gui should be updated asynchronously
     */
    public boolean addPagesAsynchronously() {
        return this.config.getBoolean("gui-settings.add-pages-asynchronously", true);
    }

}
