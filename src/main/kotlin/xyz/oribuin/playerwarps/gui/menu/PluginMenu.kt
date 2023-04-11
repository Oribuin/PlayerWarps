package xyz.oribuin.playerwarps.gui.menu

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.config.CommentedFileConfiguration
import dev.rosewood.rosegarden.utils.StringPlaceholders
import dev.triumphteam.gui.components.ScrollType
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.PaginatedGui
import dev.triumphteam.gui.guis.ScrollingGui
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import xyz.oribuin.playerwarps.util.WarpUtils.add
import xyz.oribuin.playerwarps.util.WarpUtils.createFile
import xyz.oribuin.playerwarps.util.WarpUtils.format
import xyz.oribuin.playerwarps.util.WarpUtils.parseEnum
import kotlin.math.max

abstract class PluginMenu(val rosePlugin: RosePlugin, private val menuName: String) {

    lateinit var config: CommentedFileConfiguration

    fun load() {
        val menuFile = createFile(this.rosePlugin, "guis", "$menuName.yml")
        this.config = CommentedFileConfiguration.loadConfiguration(menuFile)
        this.config.save(menuFile)
    }

    /**
     * Create a paginated gui for the player.
     *
     * @param player The player to create the gui for.
     * @return The paginated gui.
     */
    protected fun paged(player: Player): PaginatedGui {
        val rows = this.config.getInt("gui-settings.rows", 6)
        val preTitle = this.config.getString("gui-settings.pre-title") ?: "PlayerWarps..."

        return Gui.paginated()
            .rows(if (rows > 6) 6 else rows)
            .title(this.component(player, preTitle))
            .disableAllInteractions()
            .create()
    }

    /**
     * Create a scrolling gui for the player.
     *
     * @param player The player to create the gui for.
     * @return The scrolling gui.
     */
    protected fun scrolling(player: Player): ScrollingGui {
        val rows = this.config.getInt("gui-settings.rows", 6)
        val preTitle = this.config.getString("gui-settings.pre-title") ?: "PlayerWarps..."
        val scrollType = parseEnum(ScrollType::class, this.config.getString("gui-settings.scrolling-type"), ScrollType.HORIZONTAL)

        return Gui.scrolling()
            .scrollType(scrollType)
            .rows(if (rows > 6) 6 else rows)
            .title(this.component(player, preTitle))
            .pageSize(0)
            .disableAllInteractions()
            .create()
    }

    /**
     * Create a basic gui for the player.
     *
     * @param player The player to create the gui for.
     * @return The basic gui.
     */
    fun basic(player: Player): Gui {
        val rows = this.config.getInt("gui-settings.rows", 6)
        val preTitle = this.config.getString("gui-settings.pre-title") ?: "PlayerWarps..."

        return Gui.gui()
            .rows(if (rows > 6) 6 else rows)
            .title(this.component(player, preTitle))
            .disableAllInteractions()
            .create()
    }

    /**
     * Create a placeholder for the page placeholders.
     *
     * @param gui The gui to get the placeholders from.
     * @return The placeholders.
     */
    fun pagePlaceholders(gui: PaginatedGui): StringPlaceholders {
        return StringPlaceholders.builder("page", gui.currentPageNum)
            .add("total", max(gui.pagesNum, 1))
            .add("next", gui.nextPageNum)
            .add("previous", gui.prevPageNum)
            .build()
    }

    /**
     * Create a text component with placeholders.
     *
     * @param player The player to apply placeholders to.
     * @param text The text to apply placeholders to.
     * @param placeholders The placeholders to apply.
     */
    private fun component(player: Player, text: String, placeholders: StringPlaceholders = StringPlaceholders.empty()): TextComponent {
        return Component.text(format(player, text, placeholders))
    }

    /**
     * Get the reload title setting.
     *
     * @return True if the title should be reloaded.
     */
    val reloadTitle: Boolean
        get() = this.config.getBoolean("gui-settings.update-title", true)

    /**
     * Get the async pages setting.
     *
     * @return True if async pages are enabled.
     */
    val asyncPages: Boolean
        get() = this.config.getBoolean("gui-settings.async-pages", true)

    fun async(runnable: Runnable) =
        Bukkit.getScheduler().runTaskAsynchronously(rosePlugin, runnable)

    fun sync(runnable: Runnable) =
        Bukkit.getScheduler().runTask(rosePlugin, runnable)

}