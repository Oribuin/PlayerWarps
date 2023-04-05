package xyz.oribuin.playerwarps.command.command

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.framework.CommandContext
import dev.rosewood.rosegarden.command.framework.RoseCommand
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable
import dev.rosewood.rosegarden.command.framework.types.GreedyString
import org.bukkit.entity.Player
import xyz.oribuin.playerwarps.manager.WarpManager
import xyz.oribuin.playerwarps.util.WarpUtils.center
import xyz.oribuin.playerwarps.util.WarpUtils.getManager

class CreateCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : RoseCommand(rosePlugin, parent) {

    @RoseExecutable
    fun execute(context: CommandContext, name: GreedyString) {
        val player = context.sender as Player
        this.rosePlugin.getManager<WarpManager>().createWarp(name.get(), player, player.location.center())
    }

    override fun getDefaultName(): String = "create"

    override fun getDescriptionKey(): String = "command-create-description"

    override fun getRequiredPermission(): String = "playerwarps.create"

    override fun isPlayerOnly(): Boolean = true

}
