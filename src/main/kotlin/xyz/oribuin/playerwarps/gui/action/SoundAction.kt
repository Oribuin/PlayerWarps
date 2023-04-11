package xyz.oribuin.playerwarps.gui.action

import dev.rosewood.rosegarden.utils.StringPlaceholders
import org.bukkit.Sound
import org.bukkit.entity.Player
import xyz.oribuin.playerwarps.util.WarpUtils
import java.util.regex.Pattern

class SoundAction(message: String) : ActionProvider(message) {

    private val volumeRegex = Pattern.compile("volume:([0-9]+)")

    override fun execute(player: Player, placeholders: StringPlaceholders) {
        val args = this.message.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        var volume = 100f
        if (args.size <= 2) {
            val volumeMatch = volumeRegex.matcher(args[1])
            if (volumeMatch.find()) {
                volume = volumeMatch.group(1).toFloat()
            }
        }

        val sound = WarpUtils.parseEnum(Sound::class, args[0]) ?: return
        player.playSound(player.location, sound, volume, 1f)
    }

}
