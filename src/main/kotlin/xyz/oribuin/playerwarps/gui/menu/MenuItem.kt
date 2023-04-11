package xyz.oribuin.playerwarps.gui.menu

import dev.rosewood.rosegarden.config.CommentedConfigurationSection
import dev.rosewood.rosegarden.utils.HexUtils
import dev.rosewood.rosegarden.utils.StringPlaceholders
import dev.triumphteam.gui.guis.BaseGui
import dev.triumphteam.gui.guis.GuiItem
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import xyz.oribuin.playerwarps.gui.action.ActionHandler
import xyz.oribuin.playerwarps.gui.action.ActionProvider
import xyz.oribuin.playerwarps.hook.WarpPlaceholders
import xyz.oribuin.playerwarps.util.WarpUtils.getItem
import java.util.function.Consumer
import java.util.function.Predicate

class MenuItem(val config: CommentedConfigurationSection) {

    private var path: String? = null
    private var customItem: ItemStack? = null
    private var placeholders: StringPlaceholders = StringPlaceholders.empty()
    private var player: Player? = null
    private var function: Consumer<InventoryClickEvent> = Consumer { }
    private var slots: MutableList<Int> = mutableListOf()
    private var condition: Predicate<MenuItem> = Predicate { true }
    private var actions: MutableMap<ClickType, List<ActionProvider>> = mutableMapOf()

    companion object {

        /**
         * Create a new MenuItem from a config.
         *
         * @param config The config to create the MenuItem from.
         * @return The new MenuItem.
         */
        fun create(config: CommentedConfigurationSection): MenuItem = MenuItem(config)

    }

    fun place(gui: BaseGui) {

        // Check for custom item or path.
        if (this.customItem == null && this.path == null)
            throw IllegalArgumentException("MenuItem must have a custom item or path.")


        if (this.path?.let { this.config.get(it) } == null && this.customItem == null)
            throw IllegalArgumentException("MenuItem path does not exist in the config (${this.path}).")

        if (!this.config.getBoolean("$path.enabled"))
            return // Don't place the item if it's disabled.

        if (!this.condition.test(this))
            return // Don't place the item if the condition is not met.

        if (this.slots.isEmpty()) {

            // Get the slots from the config.
            val slots = this.config.getList("$path.slots")

            // If the slots are empty, throw an exception.
            if (slots == null || slots.isEmpty())
                throw IllegalArgumentException("MenuItem must have at least one slot.")

            // Add the slots to the list.
            for (slot in slots) {
                // Add as a number if it's a number.
                if (slot is Number)
                    this.slots.add(slot.toInt())

                // add as a range if it's a string.
                if (slot is String) {
                    val range = slot.split("-")
                    if (range.size != 2)
                        throw IllegalArgumentException("MenuItem slot range must be in the format of 'start-end'.")

                    val start = range[0].toInt()
                    val end = range[1].toInt()

                    for (i in start..end)
                        this.slots.add(i)
                }
            }


            val item = this.customItem
                ?: this.path?.let { getItem(this.config, it, player, placeholders) }
                ?: throw IllegalArgumentException("MenuItem must have a custom item or path.")

            // Place the item in the slots.

            this.parseActions()
            gui.setItem(this.slots, GuiItem(item) {
                val actions = this.actions[it.click]
                if (!actions.isNullOrEmpty()) {
                    player?.let { player -> actions.forEach { provider -> provider.execute(player, placeholders) } }
                    return@GuiItem
                }

                this.function.accept(it)
            })
            gui.update()
        }
    }

    private fun parseActions() {
        // Get the actions from the config.
        val actions = this.config.getConfigurationSection("$path.actions") ?: return

        // Loop through the actions.
        for (action in actions.getKeys(false)) {
            val actionConfig = actions.getConfigurationSection(action) ?: continue

            // Get the click type.
            val clickType = ClickType.valueOf(actionConfig.getString("click-type") ?: "LEFT")

            // Get the actions.
            val commands = actionConfig.getStringList("commands")

            // Create a list of ActionProviders.
            val providers = mutableListOf<ActionProvider>()

            // Loop through the commands.
            for (command in commands) {
                // Get the action provider.
                val provider = ActionHandler.parse(command) ?: continue

                // Add the provider to the list.
                providers.add(provider)
            }

            // Add the actions to the map.
            this.actions[clickType] = providers
        }
    }

    /**
     * Format a string with the player's placeholders.
     *
     * @param player The player to get the placeholders for.
     * @param text The text to format.
     * @param placeholders The placeholders to apply to the text.
     * @return The formatted text.
     */
    private fun format(player: Player?, text: String, placeholders: StringPlaceholders = StringPlaceholders.empty()): String {
        return HexUtils.colorify(WarpPlaceholders.apply(player, placeholders.apply(text)));
    }

    fun path(path: String): MenuItem {
        this.path = path
        return this
    }

    fun customItem(item: ItemStack): MenuItem {
        this.customItem = item
        return this
    }

    fun placeholders(placeholders: StringPlaceholders): MenuItem {
        this.placeholders = placeholders
        return this
    }

    fun player(player: Player): MenuItem {
        this.player = player
        return this
    }

    fun function(function: Consumer<InventoryClickEvent>): MenuItem {
        this.function = function
        return this
    }

    fun condition(condition: Predicate<MenuItem>): MenuItem {
        this.condition = condition
        return this
    }

    fun slots(slots: List<Int>): MenuItem {
        this.slots = slots.toMutableList()
        return this
    }

    fun slots(vararg slots: Int): MenuItem {
        this.slots = slots.toMutableList()
        return this
    }

}