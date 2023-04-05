package xyz.oribuin.playerwarps.util

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.manager.Manager
import dev.rosewood.rosegarden.utils.StringPlaceholders
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import xyz.oribuin.playerwarps.manager.LocaleManager
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.security.Permission
import java.util.*
import kotlin.reflect.KClass

object WarpUtils {

    /**
     * @return The instance of a plugin manager
     */
    inline fun <reified T : Manager> RosePlugin.getManager(): T = this.getManager(T::class.java)

    fun RosePlugin.send(player: Player, messageId: String, placeholders: StringPlaceholders = StringPlaceholders.empty()) {
        this.getManager<LocaleManager>().sendMessage(player, messageId, placeholders)
    }

    /**
     * Shorthand function for adding placeholders.
     *
     * @param key The key of the placeholder
     * @param value The value of the placeholder
     * @return  The string placeholders builder
     */
    fun StringPlaceholders.Builder.add(key: String, value: Any): StringPlaceholders.Builder = this.addPlaceholder(key, value)

    /**
     * @return The location as a string (world,x,y,z)
     */
    fun Location.format(): String = "${this.world?.name}, ${this.x}, ${this.y}, ${this.z}"

    /**
     * @return The location as a block location
     */
    fun Location.block(): Location =
        Location(this.world, this.blockX.toDouble(), this.blockY.toDouble(), this.blockZ.toDouble())

    /**
     * @return The location as a center location
     */
    fun Location.center() = Location(this.world, this.blockX + 0.5, this.blockY + 0.5, this.blockZ + 0.5)

    /**
     * Parse an enum from a string
     *
     * @param enumClass The enum class
     * @param value The string value
     * @return The enum value
     */
    fun <T : Enum<T>> parseEnum(enumClass: KClass<T>, value: String): T {
        try {
            return enumClass.java.enumConstants.first { it.name.equals(value, true) } ?: error("")
        } catch (ex: Exception) {
            error("Invalid ${enumClass.simpleName} value: $value")
        }
    }

    /**
     * Gets the maximum allowed warps a player can have
     *
     * @param player The player
     * @return The amount of warps the player can have.
     */
    fun getMaxWarps(player: Player): Int {
        var amount = 1
        for (info in player.effectivePermissions) {
            val target = info.permission.lowercase(Locale.getDefault())
            if (target.startsWith("playerwarps.max.") && info.value) {
                try {
                    amount = amount.coerceAtLeast(target.substring(target.lastIndexOf('.') + 1).toInt())
                } catch (ignored: NumberFormatException) {
                }
            }
        }
        return amount
    }

    /**
     * Serialize an itemstack
     *
     * @param itemStack The itemstack to serialize
     * @return The serialized itemstack as a byte array
     */
    fun serializeItem(itemStack: ItemStack?): ByteArray {
        if (itemStack == null) return ByteArray(0)
        var data = ByteArray(0)
        try {
            ByteArrayOutputStream().use { stream ->
                BukkitObjectOutputStream(stream).use { oos ->
                    oos.writeObject(itemStack)
                    data = stream.toByteArray()
                }
            }
        } catch (ignored: IOException) {
        }

        return data
    }

    /**
     * Deserialize an itemstack
     *
     * @param data The serialized itemstack
     * @return The deserialized itemstack
     */
    fun deserializeItem(data: ByteArray?): ItemStack? {
        if (data == null || data.isEmpty()) return null

        var itemStack: ItemStack? = null
        try {
            ByteArrayInputStream(data).use { stream ->
                BukkitObjectInputStream(stream).use { ois ->
                    itemStack = ois.readObject() as ItemStack
                }
            }
        } catch (ignored: IOException) {
        } catch (ignored: ClassNotFoundException) {
        }

        return itemStack
    }

    init {
    }
}
