package xyz.oribuin.playerwarps

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.manager.Manager
import dev.rosewood.rosegarden.utils.NMSUtil
import xyz.oribuin.playerwarps.gui.menu.MenuProvider
import xyz.oribuin.playerwarps.hook.WarpPlaceholders
import xyz.oribuin.playerwarps.listener.PlayerListener
import xyz.oribuin.playerwarps.manager.*

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

        // Check if the server is running 1.16.5 or above
        if (NMSUtil.getVersionNumber() < 16) {
            this.logger.severe("This plugin only supports version 1.16.5 and above, Please update your server if you wish to use this plugin.")
            this.logger.severe("Disabling plugin...")
            this.server.pluginManager.disablePlugin(this)
            return
        }

        // Register Plugin Events
        this.server.pluginManager.registerEvents(PlayerListener(this), this)


        // Register Placeholder Expansion
        if (this.server.pluginManager.isPluginEnabled("PlaceholderAPI")) {
            WarpPlaceholders(this).register() // Register the placeholders
        }

    }

    override fun reload() {
        super.reload()

        MenuProvider.reload() // Reload the GUIs

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
