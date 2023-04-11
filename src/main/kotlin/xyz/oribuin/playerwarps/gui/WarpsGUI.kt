package xyz.oribuin.playerwarps.gui

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.utils.StringPlaceholders
import dev.triumphteam.gui.components.GuiAction
import dev.triumphteam.gui.guis.GuiItem
import dev.triumphteam.gui.guis.PaginatedGui
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import xyz.oribuin.playerwarps.PlayerWarpsPlugin
import xyz.oribuin.playerwarps.gui.enum.SortType
import xyz.oribuin.playerwarps.gui.menu.MenuItem
import xyz.oribuin.playerwarps.gui.menu.PluginMenu
import xyz.oribuin.playerwarps.manager.WarpManager
import xyz.oribuin.playerwarps.util.WarpUtils.add
import xyz.oribuin.playerwarps.util.WarpUtils.format
import xyz.oribuin.playerwarps.util.WarpUtils.formatToDate
import xyz.oribuin.playerwarps.util.WarpUtils.getItem
import xyz.oribuin.playerwarps.util.WarpUtils.getManager
import xyz.oribuin.playerwarps.util.WarpUtils.parseEnum
import xyz.oribuin.playerwarps.util.WarpUtils.updateItem
import xyz.oribuin.playerwarps.warp.Warp
import java.util.function.Predicate

class WarpsGUI(rosePlugin: RosePlugin = PlayerWarpsPlugin.instance) : PluginMenu(rosePlugin, "warps-gui") {

    private val cachedWarps = mutableMapOf<Int, GuiItem>() // Cache the warp items to reduce load on gui open.

    /**
     * Open the warp gui for a player.
     *
     * @param player The player to open the gui for.
     */
    fun open(player: Player) {
        val title = this.config.getString("gui-settings.title") ?: "PlayerWarps | %page%/%total%"
        val shouldScroll = this.config.getBoolean("gui-settings.should-scroll", false)

        val gui = if (shouldScroll) this.scrolling(player) else this.paged(player)

        // Add the extra items to the gui.
        this.extras(gui, player)

        // Add the navigation items to the gui.
        this.navigation(gui, player, title)

        // Open the gui.
        gui.open(player)

        val task = Runnable {
            this.addWarps(gui, player)

            if (this.reloadTitle)
                this.sync { gui.updateTitle(format(player, title, this.pagePlaceholders(gui))) }
        }

        if (this.asyncPages)
            this.async(task)
        else
            task.run()

        // Add the warp items to the gui.
        this.addWarps(gui, player)
    }

    /**
     * Add the warp items to the gui.
     *
     * @param gui The gui to add the items to.
     * @param player The player to add the items for.
     * @param filter The filter to use when adding the items.
     */
    private fun addWarps(gui: PaginatedGui, player: Player, filter: Predicate<Warp> = Predicate { true }) {
        gui.clearPageItems()

        this.getWarps(player, filter).forEach { warp ->
            val placeholders = this.warpPlaceholders(warp)

            val action = GuiAction<InventoryClickEvent> {

                if (!warp.isPublic) {
                    // Tell the player the warp is private.
                    return@GuiAction
                }

                if (warp.banned.contains(player.uniqueId)) {
                    // Tell the player they are banned from the warp.
                    return@GuiAction
                }

                this.rosePlugin.getManager<WarpManager>().teleportToWarp(player, warp)
            }

            // Get the item from the cache or create a new one.
            val itemStack = this.cachedWarps.computeIfAbsent(warp.id) {
                GuiItem(warp.icon ?: getItem(this.config, "warp-item", player, placeholders))
            }.itemStack

            // Update the item with the placeholders.
            val updatedItem = GuiItem(updateItem(itemStack, config, "warp-item", player, placeholders), action)

            gui.addItem(updatedItem) // Add the item to the gui.
            this.cachedWarps[warp.id] = updatedItem // Update the cache.
        }

        gui.update()
    }


    /**
     * Get the warps to display in the gui.
     *
     * @param player The player to get the warps for.
     * @param filter The filter to use when getting the warps.
     * @return A list of warps.
     */
    private fun getWarps(player: Player, filter: Predicate<Warp> = Predicate { true }): List<Warp> {
        val warps = this.rosePlugin.getManager<WarpManager>().getWarps().toMutableList()
        warps.removeIf { !it.isPublic || it.banned.contains(player.uniqueId) } // Remove all private warps and warps the player is banned from.

        parseEnum(SortType::class, this.config.getString("gui-settings.sort-type"), SortType.LIKES_HIGH_LOW)
            .sort(warps) // Sort the warps by the sort type.

        return warps.filter { warp -> filter.test(warp) }
    }

    /**
     * Add the extra items to the gui.
     *
     * @param gui The gui to add the items to.
     * @param player The player to add the items for.
     */
    private fun extras(gui: PaginatedGui, player: Player) {
        this.config.getConfigurationSection("extra-items")?.let { config ->
            config.getKeys(false).forEach {
                MenuItem.create(this.config)
                    .path("extra-items.$it")
                    .player(player)
                    .place(gui)
            }
        }
    }

    /**
     * Add the navigation items to the gui.
     *
     * @param gui The gui to add the items to.
     * @param player The player to add the items for.
     * @param title The title of the gui.
     */
    private fun navigation(gui: PaginatedGui, player: Player, title: String) {
        MenuItem.create(config)
            .path("next-page")
            .player(player)
            .condition { this.config.getBoolean("next-page.hide-if-last-page", false) && gui.currentPageNum < gui.pagesNum }
            .function {
                gui.next()
                this.navigation(gui, player, title)
                this.sync { gui.updateTitle(format(player, title, this.pagePlaceholders(gui))) }
            }
            .place(gui)

        MenuItem.create(config)
            .path("previous-page")
            .player(player)
            .condition { this.config.getBoolean("previous-page.hide-if-first-page", false) && gui.currentPageNum > 1 }
            .function {
                gui.previous()
                this.navigation(gui, player, title)
                this.sync { gui.updateTitle(format(player, title, this.pagePlaceholders(gui))) }
            }
            .place(gui)
    }


    /**
     * Create all the placeholders for a warp icon
     *
     * @param warp The warp to create the placeholders for.
     * @return A StringPlaceholders object.
     */
    private fun warpPlaceholders(warp: Warp): StringPlaceholders {
        return StringPlaceholders.builder()
            .add("name", warp.name)
            .add("display_name", warp.displayName)
            .add("owner", warp.ownerName ?: "Unknown")
            .add("owner_uuid", warp.owner.toString())
            .add("teleport_cost", String.format("%.2f", warp.teleportFee))
            .add("visitors", warp.visitors.size.toString())
            .add("banned", warp.banned.size.toString())
            .add("likes", warp.likes.size.toString())
            .add("public", warp.isPublic.toString())
            .add("location", warp.location.format())
            .add("creation_date", warp.creationTime.formatToDate())
            .build()
    }
}
