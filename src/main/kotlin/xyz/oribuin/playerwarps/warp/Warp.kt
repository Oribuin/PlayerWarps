package xyz.oribuin.playerwarps.warp

import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import java.util.*

data class Warp(var id: Int, var name: String, var owner: UUID, var location: Location) {
    var displayName: String = name // The display name of the warp
    var ownerName: String? = null // The name of the owner (For when the owner is offline)
    var description: MutableList<String> = mutableListOf() // The description of the warp
    var icon: ItemStack? = null // The icon of the warp
    var creationTime: Long = System.currentTimeMillis() // The time the warp was created
    var isPublic: Boolean = false // If the warp is public
    var teleportFee: Double = 0.0 // The teleport fee of the warp
    var banned: MutableList<UUID> = mutableListOf() // The banned users of the warp
    var visitors: MutableList<UUID> = mutableListOf() // The visitors of the warp
    var likes: MutableList<UUID> = mutableListOf() // The likes of the warp
}
