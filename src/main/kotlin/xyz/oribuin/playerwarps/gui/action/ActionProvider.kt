package xyz.oribuin.playerwarps.gui.action

import dev.rosewood.rosegarden.utils.StringPlaceholders
import org.bukkit.entity.Player

abstract class ActionProvider(var message: String) {

    /**
     * Execute the action function.
     *
     * @param player The player who clicked the item.
     * @param placeholders The placeholders to apply to the message.
     */
    abstract fun execute(player: Player, placeholders: StringPlaceholders)

}