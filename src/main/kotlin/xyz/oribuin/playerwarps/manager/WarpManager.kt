package xyz.oribuin.playerwarps.manager

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.manager.Manager
import dev.rosewood.rosegarden.utils.NMSUtil
import dev.rosewood.rosegarden.utils.StringPlaceholders
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import xyz.oribuin.playerwarps.hook.economy.EconomyPlugin
import xyz.oribuin.playerwarps.manager.ConfigurationManager.Setting
import xyz.oribuin.playerwarps.util.WarpUtils.add
import xyz.oribuin.playerwarps.util.WarpUtils.center
import xyz.oribuin.playerwarps.util.WarpUtils.getManager
import xyz.oribuin.playerwarps.util.WarpUtils.getMaxWarps
import xyz.oribuin.playerwarps.util.WarpUtils.parseEnum
import xyz.oribuin.playerwarps.util.WarpUtils.send
import xyz.oribuin.playerwarps.warp.Warp
import xyz.oribuin.playerwarps.warp.WarpUser
import java.util.*

class WarpManager(rosePlugin: RosePlugin) : Manager(rosePlugin) {

    val userCache = mutableMapOf<UUID, WarpUser>()
    lateinit var economyPlugin: EconomyPlugin // The economy plugin to use

    override fun reload() {
        this.userCache.clear()

        // Check if the economy plugin is valid
        try {
            this.economyPlugin = parseEnum(EconomyPlugin::class, Setting.ECONOMY_PLUGIN.string)
        } catch (ex: IllegalArgumentException) {
            this.rosePlugin.logger.severe("We could not find the economy plugin you specified. Please check your config.yml.")
            ex.printStackTrace()
        }
    }

    override fun disable() {

    }

    /**
     * @param id The id of the warp to get
     * @return  The warp with the given id
     */
    fun getWarp(id: Int): Warp? = this.rosePlugin.getManager<DataManager>().warpCache[id]

    /**
     * @param name The name of the warp to get
     * @return The warp with the given name
     */
    fun getWarp(name: String): Warp? =
        this.rosePlugin.getManager<DataManager>().warpCache.values.firstOrNull { it.name.equals(name, true) }

    /**
     * @param owner The owner of the warps to get
     * @return A list of all warps owned by the given player
     */
    fun getWarps(owner: UUID): List<Warp> =
        this.rosePlugin.getManager<DataManager>().warpCache.values.filter { it.owner == owner }

    /**
     * @return A list of all warps in the cache
     */
    fun getWarps(): List<Warp> = this.rosePlugin.getManager<DataManager>().warpCache.values.toList()

    /**
     * @param user The user to get
     * @return The warp user
     */
    fun getUser(user: UUID): WarpUser = this.userCache[user] ?: WarpUser(user).also { this.userCache[user] = it }

    /**
     * @param user The user to get
     * @return The warp user
     */
    fun getUser(user: Player): WarpUser =
        this.getUser(user.uniqueId).also { it.cachedPlayer = user } // may not need the also

    /**
     * Create a new player warp with the given name, owner, and location
     *
     * @param name The name of the warp
     * @param owner The owner of the warp
     * @param where The location of the warp
     */
    @Suppress("DEPRECATION")
    fun createWarp(name: String, owner: Player, where: Location) {

        val formattedName = net.md_5.bungee.api.ChatColor.stripColor(name) // Remove color codes from the name

        // Check if the warp already exists
        if (this.getWarp(formattedName) != null) {
            this.rosePlugin.send(owner, "command-create-warp-exists", StringPlaceholders.single("warp", name))
            return
        }

        // Check if the world is disabled
        if (Setting.DISABLED_WORLDS.stringList.contains(where.world.name.lowercase())) {
            this.rosePlugin.send(owner, "command-create-disabled-world", StringPlaceholders.single("world", where.world.name))
            return
        }

        // Check if the player has reached the max amount of warps
        val count = this.getWarps(owner.uniqueId).size // The amount of warps the player has
        val max = getMaxWarps(owner) // Max warps the player can have
        if (count >= max) {
            this.rosePlugin.send(owner, "command-create-limit", StringPlaceholders.builder("limit", max).add("count", count).build())
            return
        }

        // Check if the player has enough money to create a warp
        if (this.economyPlugin.checkBalance(owner) < Setting.CREATE_WARP_PRICE.double) {
            this.rosePlugin.send(owner, "command-create-cost", StringPlaceholders.single("cost", Setting.CREATE_WARP_PRICE.double))
            return
        }

        // Check if the player is on cooldown
        val user = this.getUser(owner)
        val current = System.currentTimeMillis()

        if (current - user.lastCreateTime < Setting.COOLDOWNS_CREATE.int * 1000) {
            val cooldown = (Setting.COOLDOWNS_CREATE.int - ((current - user.lastCreateTime) / 1000)).toInt()
            this.rosePlugin.send(owner, "command-create-cooldown", StringPlaceholders.single("cooldown", cooldown))
            return
        }

        this.rosePlugin.getManager<DataManager>().createNewWarp(formattedName, owner, where.center()) {
            this.economyPlugin.withdraw(owner, Setting.CREATE_WARP_PRICE.double)
            user.lastCreateTime = it.creationTime
            this.rosePlugin.send(owner, "command-create-success", StringPlaceholders.single("warp", formattedName))
        }

    }

    /**
     * Teleport a player to a warp
     *
     * @param player The player to teleport
     * @param warp The warp to teleport to
     * @param adminBypass Whether to bypass the cooldown and disabled worlds
     */
    fun teleportToWarp(player: Player, warp: Warp, adminBypass: Boolean = false) {

        // Check if the world is disabled
        if (!adminBypass && Setting.DISABLED_WORLDS.stringList.contains(player.location.world.name.lowercase())) {
            this.rosePlugin.send(player, "command-teleport-disabled-world", StringPlaceholders.single("world", player.location.world.name))
            return
        }

        // Check if the player is on cooldown
        val user = this.getUser(player)
        val current = System.currentTimeMillis()

        if (!adminBypass && current - user.lastTeleportTime < Setting.COOLDOWNS_TELEPORT.int * 1000) {
            val cooldown = (Setting.COOLDOWNS_TELEPORT.int * 1000 - (current - user.lastTeleportTime)) / 1000
            this.rosePlugin.send(player, "command-teleport-cooldown", StringPlaceholders.single("cooldown", cooldown))
            return
        }

        // Check if the player has enough money to teleport to a warp
        if (!adminBypass && warp.teleportFee >= 1 && this.economyPlugin.checkBalance(player) < warp.teleportFee) {
            this.rosePlugin.send(player, "command-teleport-cost", StringPlaceholders.single("cost", warp.teleportFee))
            return
        }

        // Check if the player is banned from the warp
        if (!adminBypass && warp.banned.contains(player.uniqueId)) {
            this.rosePlugin.send(player, "command-teleport-banned")
            return
        }

        // TODO: Add a delay to the teleportation
        // TODO: Add teleportation sound
        // TODO: Add teleportation particle

        this.rosePlugin.send(player, "command-teleport-success", StringPlaceholders.single("warp", warp.name))
        this.economyPlugin.withdraw(player, warp.teleportFee)

        user.lastTeleportTime = current
        this.userCache[player.uniqueId] = user

        // Add the player to the warp's visitors
        if (!warp.visitors.contains(player.uniqueId)) {
            warp.visitors.add(player.uniqueId)
            this.rosePlugin.getManager<DataManager>().warpCache[warp.id] = warp
        }

        // Teleport the player, async if possible (Why would you not be using paper?)
        if (NMSUtil.isPaper())
            player.teleportAsync(warp.location)
        else
            player.teleport(warp.location)
    }

    /**
     * Delete a warp
     *
     * @param warp The warp to delete
     * @param adminBypass Whether or not to bypass the cooldown and disabled worlds
     */
    fun deleteWarp(who: Player, warp: Warp, adminBypass: Boolean = false) {

        val user = this.getUser(who)
        if (!adminBypass) {
            // Check if the player is on cooldown
            val current = System.currentTimeMillis()

            if (current - user.lastDeleteTime < Setting.COOLDOWNS_DELETE.int * 1000) {
                val cooldown = Setting.COOLDOWNS_DELETE.int - ((current - user.lastDeleteTime) / 1000)
                this.rosePlugin.send(who, "command-delete-cooldown", StringPlaceholders.single("warp", cooldown))
                return
            }
        }

        // Check if the player has enough money to delete a warp
        if (!adminBypass && this.economyPlugin.checkBalance(who) < Setting.DELETE_WARP_PRICE.double) {
            this.rosePlugin.send(who, "command-delete-cost", StringPlaceholders.single("cost", Setting.DELETE_WARP_PRICE.double))
            return
        }

        // Check if the player is the owner of the warp
        if (!adminBypass && who.uniqueId != warp.owner) {
            this.rosePlugin.send(who, "command-delete-not-owner")
            return
        }

        this.rosePlugin.getManager<DataManager>().deleteWarp(warp) {

            // Update the user's last delete time
            if (!adminBypass) {
                user.lastDeleteTime = System.currentTimeMillis()
                this.userCache[who.uniqueId] = user
            }

            this.economyPlugin.withdraw(who, Setting.DELETE_WARP_PRICE.double)
            this.rosePlugin.send(who, "command-delete-warp-success", StringPlaceholders.single("name", warp.name))
        }

    }

}
