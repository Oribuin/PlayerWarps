package xyz.oribuin.playerwarps.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.oribuin.playerwarps.command.argument.WarpArgumentHandler;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.model.Warp;

public class UnbanCommand extends BaseRoseCommand {

    public UnbanCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player player = (Player) context.getSender();
        Warp warp = context.get("warp");
        OfflinePlayer target = context.get("target");

        if (!warp.getOwner().equals(player.getUniqueId())) {
            player.sendMessage("You don't own this warp");
            return;
        }

        if (!warp.isBanned(target)) {
            player.sendMessage("Player is not banned from " + warp.getId());
            return;
        }

        warp.getBanned().remove(target.getUniqueId());
        warp.save();
        player.sendMessage("Unbanned " + target.getName() + " from " + warp.getId());
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("unban")
                .descriptionKey("command-unban-description")
                .permission("playerwarps.unban")
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("warp", new WarpArgumentHandler())
                .required("target", ArgumentHandlers.OFFLINE_PLAYER)
                .build();
    }

}
