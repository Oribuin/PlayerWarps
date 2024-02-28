package xyz.oribuin.playerwarps.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import xyz.oribuin.playerwarps.command.argument.WarpArgumentHandler;
import xyz.oribuin.playerwarps.hook.VaultProvider;
import xyz.oribuin.playerwarps.manager.ConfigurationManager.Setting;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.manager.LocaleManager;
import xyz.oribuin.playerwarps.model.Warp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeleteCommand extends BaseRoseCommand {

    private final List<UUID> toConfirm = new ArrayList<>();

    public DeleteCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        Player player = (Player) context.getSender();
        Warp warp = context.get("warp");

        // Check if the player is the owner of the warp
        if (!warp.getOwner().equals(player.getUniqueId()) && !player.hasPermission("playerwarps.bypass")) {
            locale.sendMessage(player, "not-warp-owner");
            return;
        }

        // TODO: Add deletion cost
        VaultProvider provider = VaultProvider.get();

        // Make sure the player has enough funds
        if (Setting.WARP_DELETE_COST.getDouble() > 0 && !provider.has(player, Setting.WARP_DELETE_COST.getDouble())) {

            // Only require the player to have enough funds if they are not the owner of the warp
            if (warp.getOwner().equals(player.getUniqueId())) {
                locale.sendMessage(player, "invalid-funds");
                return;
            }
        }

        // If the player has already confirmed the deletion
        if (this.toConfirm.remove(player.getUniqueId())) {
            DataManager manager = this.rosePlugin.getManager(DataManager.class);
            manager.delete(warp.getId());
            locale.sendMessage(player, "command-delete-success");

            if (Setting.WARP_DELETE_COST.getDouble() > 0) {
                provider.take(player, Setting.WARP_DELETE_COST.getDouble());
            }

            return;
        }

        this.toConfirm.add(player.getUniqueId());
        locale.sendMessage(player, "command-delete-confirm", StringPlaceholders.of("warp", warp.getId()));
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
