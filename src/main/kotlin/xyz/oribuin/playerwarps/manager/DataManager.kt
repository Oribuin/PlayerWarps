package xyz.oribuin.playerwarps.manager

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.database.DataMigration
import dev.rosewood.rosegarden.manager.AbstractDataManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import xyz.oribuin.playerwarps.database.migration._1_CreateInitialTables
import xyz.oribuin.playerwarps.util.ListSerializer.deserialize
import xyz.oribuin.playerwarps.util.ListSerializer.serialize
import xyz.oribuin.playerwarps.util.WarpUtils.deserializeItem
import xyz.oribuin.playerwarps.util.WarpUtils.serializeItem
import xyz.oribuin.playerwarps.warp.Warp
import java.sql.Connection
import java.sql.Statement
import java.util.*
import java.util.function.Consumer

class DataManager(rosePlugin: RosePlugin) : AbstractDataManager(rosePlugin) {

    val warpCache = mutableMapOf<Int, Warp>()

    override fun reload() {
        super.reload()

        // Load all warps into the cache
        this.warpCache.clear()
        this.async {
            this.databaseConnector.connect { connection ->
                connection.prepareStatement("SELECT * FROM ${this.tablePrefix}warps").use { statement ->
                    val resultSet = statement.executeQuery()
                    while (resultSet.next()) {
                        val loc = Location(
                            Bukkit.getServer().getWorld(resultSet.getString("world")),
                            resultSet.getDouble("x"),
                            resultSet.getDouble("y"),
                            resultSet.getDouble("z"),
                            resultSet.getFloat("yaw"),
                            resultSet.getFloat("pitch")
                        )

                        val warp = Warp(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            UUID.fromString(resultSet.getString("owner")),
                            loc
                        )

                        warp.displayName = resultSet.getString("display_name")
                        warp.description = deserialize(String::class, resultSet.getString("description")).toMutableList()
                        warp.icon = deserializeItem(resultSet.getBytes("icon"))
                        warp.creationTime = resultSet.getLong("creation_time")
                        warp.isPublic = resultSet.getBoolean("public")
                        warp.teleportFee = resultSet.getDouble("teleportFee")
                        warp.banned = deserialize(UUID::class, resultSet.getString("banned")).toMutableList()
                        warp.visitors = deserialize(UUID::class, resultSet.getString("visitors")).toMutableList()
                        warp.likes = deserialize(UUID::class, resultSet.getString("likes")).toMutableList()
                        warpCache[warp.id] = warp
                    }
                }
            }
        }
    }

    /**
     * Create a new warp with the given name, owner, and location and then run the callback
     *
     * @param name     The name of the warp
     * @param owner    The owner of the warp
     * @param location The location of the warp
     * @param callback The callback to run after the warp is created
     */
    fun createNewWarp(name: String, owner: Player, location: Location, callback: Consumer<Warp>) {
        val warp = Warp(-1, name, owner.uniqueId, location)
        this.async {
            this.databaseConnector.connect { connection ->
                val query =
                    "INSERT INTO ${this.tablePrefix}warps (`id`, `name`, `owner`, `world`, `x`, `y`, `z`, `yaw`, `pitch`, `creation_time`) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?)"

                connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS).use { statement ->
                    statement.setString(1, warp.name)
                    statement.setString(2, warp.owner.toString())
                    statement.setString(3, location.world.name)
                    statement.setDouble(4, location.x)
                    statement.setDouble(5, location.y)
                    statement.setDouble(6, location.z)
                    statement.setFloat(7, location.yaw)
                    statement.setFloat(8, location.pitch)
                    statement.setLong(9, warp.creationTime)
                    statement.executeUpdate()
                    statement.generatedKeys.use {
                        if (it.next()) {
                            warp.id = it.getInt(1)
                            this.saveWarp(warp)
                            this.sync { callback.accept(warp) }
                        }
                    }
                }
            }
        }
    }

    /**
     * Save a warp to the database
     *
     * @param warp The warp to save
     */
    fun saveWarp(warp: Warp) {
        this.warpCache[warp.id] = warp
        this.async {
            databaseConnector.connect { connection: Connection ->
                // oh boy that's ugly.
                val query =
                    "REPLACE INTO ${this.tablePrefix}warps (`id`, `name`, `owner`, `world`, `x`, `y`, `z`, `yaw`, `pitch`, `creation_time`, " +
                            "`display_name`, `description`, `icon`, `public`, `banned`, `visitors`, `likes`) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                connection.prepareStatement(query).use { statement ->
                    statement.setInt(1, warp.id)
                    statement.setString(2, warp.name)
                    statement.setString(3, warp.owner.toString())
                    statement.setString(4, warp.location.world.name)
                    statement.setDouble(5, warp.location.x)
                    statement.setDouble(6, warp.location.y)
                    statement.setDouble(7, warp.location.z)
                    statement.setFloat(8, warp.location.yaw)
                    statement.setFloat(9, warp.location.pitch)
                    statement.setLong(10, warp.creationTime)
                    statement.setString(11, warp.displayName)
                    statement.setString(12, serialize(warp.description))
                    statement.setBytes(13, serializeItem(warp.icon))
                    statement.setBoolean(14, warp.isPublic)
                    statement.setString(15, serialize(warp.banned)) // i expect all of these to not work
                    statement.setString(16, serialize(warp.visitors))
                    statement.setString(17, serialize(warp.likes))
                    statement.executeUpdate()
                }
            }
        }
    }

    /**
     * Delete a warp from the database
     *
     * @param warp The warp to delete
     */
    fun deleteWarp(warp: Warp, callback: Consumer<Warp>) {
        this.warpCache.remove(warp.id)

        this.async {
            this.databaseConnector.connect { connection ->
                connection.prepareStatement("DELETE FROM ${this.tablePrefix}warps WHERE id = ?").use { statement ->
                    statement.setInt(1, warp.id)
                    statement.executeUpdate()

                    this.sync { callback.accept(warp) }
                }
            }
        }
    }

    override fun getDataMigrations(): List<Class<out DataMigration>> {
        return listOf(_1_CreateInitialTables::class.java)
    }


    private fun async(task: () -> Unit) =
        this.rosePlugin.server.scheduler.runTaskAsynchronously(rosePlugin, task)

    // we're using this to run a task a tick later
    private fun sync(task: () -> Unit) =
        this.rosePlugin.server.scheduler.runTask(rosePlugin, task)

}