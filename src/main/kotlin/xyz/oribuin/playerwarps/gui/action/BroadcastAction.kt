package xyz.oribuin.playerwarps.gui.action

import dev.rosewood.rosegarden.utils.HexUtils
import dev.rosewood.rosegarden.utils.StringPlaceholders
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import xyz.oribuin.playerwarps.hook.WarpPlaceholders

class BroadcastAction(message: String) : ActionProvider(message) {

    @Suppress("DEPRECATION")
    override fun execute(player: Player, placeholders: StringPlaceholders) {
        Bukkit.broadcastMessage(HexUtils.colorify(WarpPlaceholders.apply(player, placeholders.apply(this.message))))
    }

}
