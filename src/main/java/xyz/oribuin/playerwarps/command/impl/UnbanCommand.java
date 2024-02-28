package xyz.oribuin.playerwarps.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.oribuin.playerwarps.command.argument.WarpArgumentHandler;
import xyz.oribuin.playerwarps.manager.LocaleManager;
import xyz.oribuin.playerwarps.model.Warp;

public class UnbanCommand extends BaseRoseCommand {

    public UnbanCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        Player player = (Player) context.getSender();
        OfflinePlayer target = context.get("target");
        Warp warp = context.get("warp");

        // Check if the player is the owner of the warp
        if (!warp.getOwner().equals(player.getUniqueId()) && !player.hasPermission("playerwarps.bypass")) {
            locale.sendMessage(player, "not-warp-owner");
            return;
        }

        StringPlaceholders placeholders = StringPlaceholders.of("target", target.getName(), "warp", warp.getId());

        // Check if the player is already banned
        if (!warp.isBanned(target)) {
            locale.sendMessage(player, "command-unban-not-banned", placeholders);
            return;
        }

        warp.getBanned().remove(target.getUniqueId());
        warp.save();
        locale.sendMessage(player, "command-unban-success", placeholders);
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
