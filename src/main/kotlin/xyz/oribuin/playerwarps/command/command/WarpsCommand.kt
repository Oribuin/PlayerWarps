package xyz.oribuin.playerwarps.command.command

import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.command.command.BaseCommand
import dev.rosewood.rosegarden.command.framework.CommandContext
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable
import org.bukkit.entity.Player
import xyz.oribuin.playerwarps.gui.menu.MenuProvider
import xyz.oribuin.playerwarps.gui.WarpsGUI

class WarpsCommand(rosePlugin: RosePlugin, parent: RoseCommandWrapper) : BaseCommand(rosePlugin, parent) {

    @RoseExecutable
    override fun execute(context: CommandContext) = MenuProvider.get(menuClass = WarpsGUI::class).open(context.sender as Player)

    override fun getRequiredPermission(): String = "playerwarps.use"

    override fun isPlayerOnly(): Boolean = true

}
