package xyz.oribuin.playerwarps.listener

import dev.rosewood.rosegarden.RosePlugin
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import xyz.oribuin.playerwarps.manager.DataManager
import xyz.oribuin.playerwarps.manager.WarpManager
import xyz.oribuin.playerwarps.util.WarpUtils.getManager

class PlayerListener(private val rosePlugin: RosePlugin) : Listener {

    val manager = this.rosePlugin.getManager<WarpManager>()
    val dataManager = this.rosePlugin.getManager<DataManager>()

    fun PlayerJoinEvent.onJoin() {
        // This should not be a regular occurrence, but it can happen if the player changes their name.
        val warps = manager.getWarps(player.uniqueId)

        if (warps.isEmpty()) return

        // If the player has warps, check if the owner name matches their current name.
        // If it doesn't, update the owner name.
        warps.filter { it.ownerName != player.name }.forEach {
            it.ownerName = player.name
            dataManager.saveWarp(it)
        }
    }

}