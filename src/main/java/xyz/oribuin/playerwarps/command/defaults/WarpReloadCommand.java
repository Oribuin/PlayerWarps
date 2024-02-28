package xyz.oribuin.playerwarps.command.defaults;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.ReloadCommand;
import dev.rosewood.rosegarden.command.framework.CommandInfo;

public class WarpReloadCommand extends ReloadCommand {

    public WarpReloadCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("reload")
                .permission("playerwarps.reload")
                .build();
    }

}
