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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OwnerCommand extends BaseRoseCommand {

    private final List<UUID> toConfirm = new ArrayList<>();

    public OwnerCommand(RosePlugin rosePlugin) {
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

        StringPlaceholders placeholders = StringPlaceholders.of("warp", warp.getId(), "target", target.getName());

        // If the player has already confirmed the deletion
        if (toConfirm.remove(player.getUniqueId())) {
            warp.setOwner(target.getUniqueId());
            warp.setOwnerName(target.getName());
            warp.save();
            locale.sendMessage(player, "command-owner-success", placeholders);
            return;
        }

        // Add the player to the confirmation list
        toConfirm.add(player.getUniqueId());
        locale.sendMessage(player, "command-owner-confirm", placeholders);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("owner")
                .descriptionKey("command-owner-description")
                .permission("playerwarps.owner")
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
