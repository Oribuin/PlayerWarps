package xyz.oribuin.playerwarps.command

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper

class WarpsCommandWrapper(rosePlugin: RosePlugin) : RoseCommandWrapper(rosePlugin) {

    override fun getDefaultName(): String = "warps"

    override fun getDefaultAliases(): List<String> = listOf("playerwarps", "pw")

    override fun getCommandPackages(): MutableList<String> = mutableListOf("xyz.oribuin.playerwarps.command.command")

    override fun includeBaseCommand(): Boolean = true

    override fun includeHelpCommand(): Boolean = true

    override fun includeReloadCommand(): Boolean = false

}
