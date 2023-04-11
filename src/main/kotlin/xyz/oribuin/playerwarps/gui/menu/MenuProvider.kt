package xyz.oribuin.playerwarps.gui.menu

import xyz.oribuin.playerwarps.gui.WarpsGUI
import kotlin.reflect.KClass

object MenuProvider {
    //    ; // Empty enum
    private val menuCache: MutableMap<KClass<out PluginMenu>, PluginMenu> = HashMap()

    fun reload() {
        menuCache[WarpsGUI::class] = WarpsGUI()

        menuCache.forEach { it.value.load() }
    }

    /**
     * Get the instance of the menu.
     *
     * @param T the type of the menu.
     * @return the instance of the menu.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : PluginMenu> get(menuClass: KClass<T>): T {
        if (menuCache.containsKey(menuClass)) {
            return menuCache[menuClass] as T
        }

        try {
            val menu = menuClass.java.getDeclaredConstructor().newInstance()
            menu.load()
            menuCache[menuClass] = menu
            return menu
        } catch (e: Exception) {
            throw RuntimeException("Failed to create instance of ${menuClass.java.name}", e)
        }
    }

    init {
        reload()
    }

}