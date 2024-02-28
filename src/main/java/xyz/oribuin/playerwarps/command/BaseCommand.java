package xyz.oribuin.playerwarps.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import xyz.oribuin.playerwarps.command.defaults.WarpHelpCommand;
import xyz.oribuin.playerwarps.command.defaults.WarpReloadCommand;
import xyz.oribuin.playerwarps.command.impl.CreateCommand;
import xyz.oribuin.playerwarps.command.impl.DeleteCommand;
import xyz.oribuin.playerwarps.command.impl.TeleportCommand;
import xyz.oribuin.playerwarps.gui.MenuProvider;
import xyz.oribuin.playerwarps.gui.menu.WarpsMenu;

public class BaseCommand extends BaseRoseCommand {

    public BaseCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        MenuProvider.get(WarpsMenu.class).open((Player) context.getSender());
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("playerwarps")
                .aliases("pwarps", "pw")
                // todo: playeronly
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder().optionalSub("command",

                // Default Commands
                new WarpHelpCommand(this.rosePlugin, this),
                new WarpReloadCommand(this.rosePlugin),

                // Plugin Commands
                new CreateCommand(this.rosePlugin),
                new DeleteCommand(this.rosePlugin),
                new TeleportCommand(this.rosePlugin)
        );
    }

}
