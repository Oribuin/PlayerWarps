package xyz.oribuin.playerwarps.manager

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper
import dev.rosewood.rosegarden.manager.AbstractCommandManager
import xyz.oribuin.playerwarps.command.WarpsCommandWrapper

class CommandManager(rosePlugin: RosePlugin) : AbstractCommandManager(rosePlugin) {

    override fun getRootCommands(): List<Class<out RoseCommandWrapper>> = listOf(WarpsCommandWrapper::class.java)

    override fun getArgumentHandlerPackages(): List<String> = listOf("xyz.oribuin.playerwarps.command.argument")

}
