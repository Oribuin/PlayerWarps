package xyz.oribuin.playerwarps.gui.action

import dev.rosewood.rosegarden.utils.StringPlaceholders
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import xyz.oribuin.playerwarps.hook.WarpPlaceholders

class PlayerAction(message: String) : ActionProvider(message) {

    override fun execute(player: Player, placeholders: StringPlaceholders) {
        Bukkit.dispatchCommand(player, WarpPlaceholders.apply(player, placeholders.apply(this.message)))
    }

}
