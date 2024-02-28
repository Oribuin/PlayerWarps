package xyz.oribuin.playerwarps.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import xyz.oribuin.playerwarps.command.impl.BanCommand;
import xyz.oribuin.playerwarps.command.impl.CreateCommand;
import xyz.oribuin.playerwarps.command.impl.DeleteCommand;
import xyz.oribuin.playerwarps.command.impl.DescCommand;
import xyz.oribuin.playerwarps.command.impl.HelpCommand;
import xyz.oribuin.playerwarps.command.impl.IconCommand;
import xyz.oribuin.playerwarps.command.impl.NameCommand;
import xyz.oribuin.playerwarps.command.impl.OwnerCommand;
import xyz.oribuin.playerwarps.command.impl.PositionCommand;
import xyz.oribuin.playerwarps.command.impl.ReloadCommand;
import xyz.oribuin.playerwarps.command.impl.TeleportCommand;
import xyz.oribuin.playerwarps.command.impl.UnbanCommand;
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
                .descriptionKey("command-playerwarps-description")
                .aliases("pwarps", "pw")
                // todo: playeronly
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder().optionalSub("command",
                new HelpCommand(this.rosePlugin, this),

                // Plugin Commands
                new BanCommand(this.rosePlugin),
                new CreateCommand(this.rosePlugin),
                new DeleteCommand(this.rosePlugin),
                new DescCommand(this.rosePlugin),
                new IconCommand(this.rosePlugin),
                new NameCommand(this.rosePlugin),
                new OwnerCommand(this.rosePlugin),
                new PositionCommand(this.rosePlugin),
                new ReloadCommand(this.rosePlugin),
                new TeleportCommand(this.rosePlugin),
                new UnbanCommand(this.rosePlugin)
        );
    }

}
