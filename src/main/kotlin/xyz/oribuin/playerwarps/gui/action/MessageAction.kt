package xyz.oribuin.playerwarps.gui.action

import dev.rosewood.rosegarden.utils.HexUtils
import dev.rosewood.rosegarden.utils.StringPlaceholders
import org.bukkit.entity.Player
import xyz.oribuin.playerwarps.hook.WarpPlaceholders

class MessageAction(message: String) : ActionProvider(message) {

    override fun execute(player: Player, placeholders: StringPlaceholders) {
        player.sendMessage(HexUtils.colorify(WarpPlaceholders.apply(player, placeholders.apply(this.message))))
    }

}
