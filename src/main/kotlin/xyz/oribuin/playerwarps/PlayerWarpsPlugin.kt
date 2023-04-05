package xyz.oribuin.playerwarps

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.manager.Manager
import dev.rosewood.rosegarden.utils.NMSUtil
import xyz.oribuin.playerwarps.database.migration._1_CreateInitialTables
import xyz.oribuin.playerwarps.manager.*
import kotlin.reflect.KClass

class PlayerWarpsPlugin : RosePlugin(
    -1,
    -1,
    ConfigurationManager::class.java,
    DataManager::class.java,
    LocaleManager::class.java,
    CommandManager::class.java
) {

    init {
        instance = this
    }

    override fun enable() {
        // Check if 1.16+
        val pluginManager = this.server.pluginManager
        if (NMSUtil.getVersionNumber() < 16) {
            this.logger.severe("This plugin only supports version 1.16.5 and above, Please update your server if you wish to use this plugin.")
            this.logger.severe("Disabling plugin...")
            pluginManager.disablePlugin(this)
            return
        }

        // Register Plugin Events

        // Register Placeholder Expansion
    }

    override fun disable() {

    }


    override fun getManagerLoadPriority(): List<Class<out Manager>> {
        return listOf(WarpManager::class.java)
    }



    companion object {
        lateinit var instance: PlayerWarpsPlugin
    }

}
