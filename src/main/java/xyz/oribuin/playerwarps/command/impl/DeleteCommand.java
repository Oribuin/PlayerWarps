package xyz.oribuin.playerwarps.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import xyz.oribuin.playerwarps.command.argument.WarpArgumentHandler;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.model.Warp;

import java.util.regex.Pattern;

public class DeleteCommand extends BaseRoseCommand {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{1,16}$");

    public DeleteCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player player = (Player) context.getSender();
        Warp warp = context.get("warp");

        // TODO: Add locale messages

        if (!warp.getOwner().equals(player.getUniqueId())) {
            player.sendMessage("You cannot delete a warp that you do not own.");
            return;
        }

        DataManager manager = this.rosePlugin.getManager(DataManager.class);
        manager.delete(warp.getId());
        player.sendMessage("Warp deleted.");
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("delete")
                .descriptionKey("command-delete-description")
                .permission("playerwarps.delete")
                // todo: playeronly
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("warp", new WarpArgumentHandler())
                .build();
    }

}
