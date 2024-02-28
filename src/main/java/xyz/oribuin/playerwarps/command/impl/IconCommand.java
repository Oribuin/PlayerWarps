package xyz.oribuin.playerwarps.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.playerwarps.command.argument.WarpArgumentHandler;
import xyz.oribuin.playerwarps.manager.LocaleManager;
import xyz.oribuin.playerwarps.model.Warp;

public class IconCommand extends BaseRoseCommand {

    public IconCommand(RosePlugin rosePlugin) {
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

        // If the player is holding air, remove the icon
        ItemStack handIcon = player.getInventory().getItemInMainHand();
        if (handIcon.getType().isAir()) {
            warp.setIcon(null);
            warp.save();
            locale.sendMessage(player, "command-icon-cleared", StringPlaceholders.of("warp", warp.getId()));
            return;
        }

        warp.setIcon(handIcon);
        warp.save();
        locale.sendMessage(player, "command-icon-success", StringPlaceholders.of("warp", warp.getId()));
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("icon")
                .descriptionKey("command-icon-description")
                .permission("playerwarps.icon")
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("warp", new WarpArgumentHandler())
                .build();
    }

}
