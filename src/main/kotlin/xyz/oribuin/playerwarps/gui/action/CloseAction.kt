package xyz.oribuin.playerwarps.gui.action

import dev.rosewood.rosegarden.utils.StringPlaceholders
import org.bukkit.entity.Player

class CloseAction(message: String) : ActionProvider(message) {

    override fun execute(player: Player, placeholders: StringPlaceholders) {
        player.closeInventory()
    }

}
