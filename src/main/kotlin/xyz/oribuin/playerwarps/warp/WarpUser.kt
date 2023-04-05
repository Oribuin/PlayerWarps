package xyz.oribuin.playerwarps.warp

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

// TODO: Add more parameters to this class
data class WarpUser(val player: UUID) {

    var username: String? = null // The username of the user
    var cachedPlayer: Player? = Bukkit.getPlayer(player) // The player object of the user

    // Cooldowns for each command.
    var lastCreateTime: Long = 0 // The cooldown for creating a warp
    var lastDeleteTime: Long = 0 // The cooldown for deleting a warp
    var lastTeleportTime: Long = 0 // The cooldown for teleporting to a warp

}