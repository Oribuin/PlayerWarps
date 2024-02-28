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
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.playerwarps.command.argument.WarpArgumentHandler;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.model.Warp;

public class BanCommand extends BaseRoseCommand {

    public BanCommand(RosePlugin rosePlugin) {
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
        
        if (warp.isBanned(target)) {
            player.sendMessage("Player is already banned from " + warp.getId());
            return;
        }
        
        if (warp.getOwner().equals(target.getUniqueId())) {
            player.sendMessage("You cannot ban the owner of " + warp.getId());
            return;
        }
        
        warp.getBanned().add(target.getUniqueId());
        warp.save();
        player.sendMessage("Banned " + target.getName() + " from " + warp.getId());
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("ban")
                .descriptionKey("command-ban-description")
                .permission("playerwarps.ban")
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
