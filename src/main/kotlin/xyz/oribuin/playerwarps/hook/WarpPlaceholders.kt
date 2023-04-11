package xyz.oribuin.playerwarps.hook

import dev.rosewood.rosegarden.RosePlugin
import me.clip.placeholderapi.PlaceholderAPI
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import xyz.oribuin.playerwarps.manager.WarpManager
import xyz.oribuin.playerwarps.util.WarpUtils.getManager

class WarpPlaceholders(private val rosePlugin: RosePlugin) : PlaceholderExpansion() {

    private val manager = this.rosePlugin.getManager<WarpManager>()

    override fun onRequest(offlinePlayer: OfflinePlayer?, params: String): String? {
        val player = offlinePlayer?.player ?: return null

        // TODO: Add more placeholders
        when (params) {
            "warps" -> return this.manager.getWarps(player.uniqueId).size.toString()
            "max_warps" -> return this.manager.getMaxWarps(player).toString()
            "total_warps" -> return this.manager.getWarps().size.toString()
        }

        return null
    }

    companion object {
        private val enabled: Boolean = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")

        fun apply(player: Player? = null, message: String): String = if (enabled)
            PlaceholderAPI.setPlaceholders(player, message)
        else
            message
    }

    override fun getIdentifier(): String = "playerwarps"

    override fun getAuthor(): String = "Oribuin"

    @Suppress("DEPRECATION") // I sure do hate paper deprecation sometimes
    override fun getVersion(): String = this.rosePlugin.description.version

    override fun persist(): Boolean = true

    override fun canRegister(): Boolean = true

}
