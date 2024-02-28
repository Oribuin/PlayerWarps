package xyz.oribuin.playerwarps.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import xyz.oribuin.playerwarps.command.impl.CreateCommand;
import xyz.oribuin.playerwarps.command.impl.DeleteCommand;
import xyz.oribuin.playerwarps.command.impl.TeleportCommand;

public class BaseCommand extends BaseRoseCommand {

    public BaseCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {

    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("playerwarps")
                .aliases("pwarps", "pw")
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder().optionalSub("command",
                new CreateCommand(this.rosePlugin),
                new DeleteCommand(this.rosePlugin),
                new TeleportCommand(this.rosePlugin)
        );
    }

}
