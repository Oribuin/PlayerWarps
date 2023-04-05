package xyz.oribuin.playerwarps.command.command

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.CommandContext
import dev.rosewood.rosegarden.command.framework.RoseCommand
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable
import org.bukkit.entity.Player
import xyz.oribuin.playerwarps.manager.WarpManager
import xyz.oribuin.playerwarps.util.WarpUtils.getManager
import xyz.oribuin.playerwarps.warp.Warp

class DeleteCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute(context: CommandContext, warp: Warp) = this.rosePlugin.getManager<WarpManager>().deleteWarp(context.sender as Player, warp)

    override fun getDefaultName(): String = "delete"

    override fun getDescriptionKey(): String = "command-delete-description"

    override fun getRequiredPermission(): String = "playerwarps.delete"

    override fun isPlayerOnly(): Boolean = true

}
