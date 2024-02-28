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
import xyz.oribuin.playerwarps.manager.LocaleManager;
import xyz.oribuin.playerwarps.model.Warp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeleportCommand extends BaseRoseCommand {

    private final List<UUID> toConfirm = new ArrayList<>();

    public TeleportCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        Player player = (Player) context.getSender();
        Warp warp = context.get("warp");

        // Check if the player is banned
        if (warp.isBanned(player)) {
            locale.sendMessage(player, "command-teleport-banned");
            return;
        }

        // Check if the player can afford to teleport
        VaultProvider provider = VaultProvider.get();
        if (warp.getTeleportFee() > 0 && !warp.getOwner().equals(player.getUniqueId()) && !player.hasPermission("playerwarps.bypass")) {

            // Make sure the player has enough funds
            if (!provider.has(player, warp.getTeleportFee())) {
                locale.sendMessage(player, "invalid-funds");
                return;
            }

            // The player has enough funds, check if they have already confirmed the teleport
            if (toConfirm.remove(player.getUniqueId())) {
                provider.take(player, warp.getTeleportFee());
            } else {

                // Add the player to the confirmation list
                toConfirm.add(player.getUniqueId());
                locale.sendMessage(player, "command-teleport-confirm", StringPlaceholders.of("warp", warp.getId()));
                return;
            }
        }

        warp.teleport(player);
        locale.sendMessage(player, "command-teleport-success", StringPlaceholders.of("warp", warp.getId()));
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
