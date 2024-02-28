package xyz.oribuin.playerwarps.command.defaults;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.HelpCommand;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandInfo;

public class WarpHelpCommand extends HelpCommand {

    public WarpHelpCommand(RosePlugin rosePlugin, BaseRoseCommand parent) {
        super(rosePlugin, parent);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("help")
                .permission("playerwarps.help")
                .build();
    }

}
