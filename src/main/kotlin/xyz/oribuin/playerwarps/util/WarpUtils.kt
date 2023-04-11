package xyz.oribuin.playerwarps.util

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.config.CommentedConfigurationSection
import dev.rosewood.rosegarden.manager.Manager
import dev.rosewood.rosegarden.utils.HexUtils
import dev.rosewood.rosegarden.utils.NMSUtil
import dev.rosewood.rosegarden.utils.StringPlaceholders
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import xyz.oribuin.playerwarps.hook.WarpPlaceholders
import xyz.oribuin.playerwarps.manager.ConfigurationManager.Setting
import xyz.oribuin.playerwarps.manager.LocaleManager
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
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
        Location(this.world, this.blockX.toDouble(), this.blockY.toDouble(), this.blockZ.toDouble(), this.yaw, this.pitch)

    /**
     * @return The location as a center location
     */
    fun Location.center() = Location(this.world, this.blockX + 0.5, this.blockY + 0.5, this.blockZ + 0.5, this.yaw, this.pitch)

    /**
     * Parse an enum from a string
     *
     * @param enumClass The enum class
     * @param value The string value
     * @return The enum value
     */
    fun <T : Enum<T>> parseEnum(enumClass: KClass<T>, value: String?): T {
        try {
            return enumClass.java.enumConstants.first { it.name.equals(value, true) } ?: error("")
        } catch (ex: Exception) {
            error("Invalid ${enumClass.simpleName} value: $value")
        }
    }

    /**
     * Parse an enum from a string with a default value
     *
     * @param enumClass The enum class
     * @param value The string value
     * @param default The default value
     * @return The enum value
     */
    fun <T : Enum<T>> parseEnum(enumClass: KClass<T>, value: String?, default: T): T {
        return try {
            enumClass.java.enumConstants.first { it.name.equals(value, true) } ?: default
        } catch (ex: Exception) {
            default
        }
    }

    /**
     * Parse a color from a hex string
     *
     * @param text The hex string
     * @return The color
     */
    fun hex(text: String?): Color {

        val color = try {
            java.awt.Color.decode(text)
        } catch (ex: Exception) {
            java.awt.Color(0, 0, 0)
        }

        return Color.fromRGB(color.red, color.green, color.blue)
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

    /**
     * Create an ItemStack from the config.
     *
     * @param config The config to get the item from.
     * @param path The path to get the item from.
     * @param player The player to get the item for.
     * @param placeholders The placeholders to apply to the item.
     */
    fun getItem(
        config: CommentedConfigurationSection,
        path: String,
        player: Player? = null,
        placeholders: StringPlaceholders = StringPlaceholders.empty()
    ): ItemStack {

        // Get the material of the item.
        val material = Material.matchMaterial(
            WarpPlaceholders.apply(player, placeholders.apply(config.getString("$path.material") ?: ""))
        ) ?: throw IllegalArgumentException("MenuItem material is not a valid material.")

        val builder = ItemBuilder(material)
            // Set the name of the item.
            .name(format(player, config.getString("$path.name") ?: "", placeholders))

            // Set the lore of the item.
            .lore(ArrayList<String>(config.getStringList("$path.lore"))
                .map { format(player, it, placeholders) }
                .toMutableList())

            // Set the amount of the item.
            .amount(config.getInt("$path.amount", 1))

            // Add item flags.
            .flag(
                config.getStringList("$path.flags")
//                    .map { WarpPlaceholders.apply(player, placeholders.apply(it)) } // Does this really need placeholders? I don't think so.
                    .map { parseEnum(ItemFlag::class, it) }
                    .toTypedArray()
            )

            // Set the durability of the item.
            .texture(config.getString("$path.texture"))

            // Should the item glow?
            .glow(format(player, config.getString("$path.glow") ?: "", placeholders).toBoolean())

            // Set the color of the item.
            .potionColor(hex(format(player, config.getString("$path.potion-color") ?: "", placeholders)))

            // Set the model of the item.
            .model(format(player, config.getString("$path.model") ?: "", placeholders).toIntOrNull() ?: 0)

        // Set the owner of the item using paper's method if it's available.
        config.getString("$path.owner")?.let {
            if (it.equals("self", true)) {
                builder.owner(player)
            }

            if (NMSUtil.isPaper() && Bukkit.getOfflinePlayerIfCached(it) != null) {
                builder.owner(Bukkit.getOfflinePlayerIfCached(it))
            } else {
                builder.owner(Bukkit.getOfflinePlayer(it))
            }
        }

        val enchantSection = config.getConfigurationSection("$path.enchants")
        if (enchantSection != null) {
            val enchants = enchantSection.getKeys(false)
            enchants.forEach { enchant ->
                val enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchant)) ?: return@forEach

                builder.enchant(enchantment, config.getInt("$path.enchants.$enchant"))
            }
        }

        return builder.create()
    }

    /**
     * Update an item with a new name and lore.
     *
     * @param item The item to update.
     * @param config The config to get the item from.
     * @param path The path to get the item from.
     * @param player The player to get the item for.
     * @param placeholders The placeholders to apply to the item.
     * @return The updated item.
     */
    fun updateItem(
        item: ItemStack,
        config: CommentedConfigurationSection,
        path: String,
        player: Player? = null,
        placeholders: StringPlaceholders = StringPlaceholders.empty()
    ): ItemStack {
        return ItemBuilder(item)
            .name(format(player, config.getString("$path.name") ?: "", placeholders))
            .lore(ArrayList(config.getStringList("$path.lore"))
                .map { format(player, it, placeholders) }
                .toMutableList())
            .create()
    }

    /**
     * Format a string with the player's placeholders.
     *
     * @param player The player to get the placeholders for.
     * @param text The text to format.
     * @param placeholders The placeholders to apply to the text.
     * @return The formatted text.
     */
    fun format(player: Player?, text: String, placeholders: StringPlaceholders = StringPlaceholders.empty()): String {
        return HexUtils.colorify(WarpPlaceholders.apply(player, placeholders.apply(text)));
    }

    /**
     * Create a file from the plugin's resources
     *
     * @param rosePlugin The plugin
     * @param fileName   The file name
     * @return The file
     */
    fun createFile(rosePlugin: RosePlugin, fileName: String): File {
        val file = File(rosePlugin.dataFolder, fileName) // Create the file

        if (file.exists())
            return file

        rosePlugin.getResource(fileName).use { inStream ->
            if (inStream == null) {
                file.createNewFile()
                return file
            }

            Files.copy(inStream, Paths.get(file.absolutePath))
        }

        return file
    }

    /**
     * Create a file in a folder from the plugin's resources
     *
     * @param rosePlugin The plugin
     * @param folderName The folder name
     * @param fileName   The file name
     * @return The file
     */
    fun createFile(rosePlugin: RosePlugin, folderName: String, fileName: String): File {
        val folder = File(rosePlugin.dataFolder, folderName) // Create the folder
        val file = File(folder, fileName) // Create the file

        if (!folder.exists())
            folder.mkdirs()

        if (file.exists())
            return file

        rosePlugin.getResource("$folderName/$fileName").use { stream ->
            if (stream == null) {
                file.createNewFile()
                return file
            }

            Files.copy(stream, Paths.get(file.absolutePath))
        }

        return file
    }


    /**
     * @return The date format.
     */
    private val dateFormat: SimpleDateFormat
        get() = SimpleDateFormat(Setting.DATE_FORMAT.string)

    /**
     * Format a date to a string.
     *
     * @return The formatted date.
     */
    fun Long.formatToDate(): String = dateFormat.format(Date(this))

}