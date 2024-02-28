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

public class TeleportCommand extends BaseRoseCommand {
    public TeleportCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player player = (Player) context.getSender();
        Warp warp = context.get("warp");

        // TODO: Add locale messages

        DataManager manager = this.rosePlugin.getManager(DataManager.class);
        // TODO: Check if the player is banned
        // TODO: Check if the player can afford to teleport
        // TODO: Check if the player is in cooldown?


        warp.teleport(player);
        player.sendMessage("teleported to " + warp.getId());
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("teleport")
                .descriptionKey("command-teleport-description")
                .permission("playerwarps.teleport")
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
