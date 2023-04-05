package xyz.oribuin.playerwarps.command.argument

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.ArgumentParser
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo
import dev.rosewood.rosegarden.utils.StringPlaceholders
import xyz.oribuin.playerwarps.manager.WarpManager
import xyz.oribuin.playerwarps.util.WarpUtils.getManager
import xyz.oribuin.playerwarps.warp.Warp

class WarpArgumentHandler(rosePlugin: RosePlugin) : RoseCommandArgumentHandler<Warp>(rosePlugin, Warp::class.java) {

    @Throws(HandledArgumentException::class)
    override fun handleInternal(argumentInfo: RoseCommandArgumentInfo, argumentParser: ArgumentParser): Warp {
        val input = argumentParser.next()

        return rosePlugin.getManager<WarpManager>()
            .getWarp(input.lowercase())
            ?: throw HandledArgumentException("argument-handler-warps", StringPlaceholders.single("input", input))
    }

    override fun suggestInternal(argumentInfo: RoseCommandArgumentInfo, argumentParser: ArgumentParser): List<String> {
        argumentParser.next()

        return this.rosePlugin.getManager<WarpManager>()
            .getWarps()
            .map { it.name.lowercase() } // Get the name of each warp
            .ifEmpty { listOf("<no loaded warps>") } // If there are no warps, return a placeholder
    }
}
