package xyz.oribuin.playerwarps.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.playerwarps.command.argument.WarpArgumentHandler;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.model.Warp;

public class IconCommand extends BaseRoseCommand {

    public IconCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player player = (Player) context.getSender();
        Warp warp = context.get("warp");

        if (!warp.getOwner().equals(player.getUniqueId())) {
            player.sendMessage("You don't own this warp");
            return;
        }

        ItemStack handIcon = player.getInventory().getItemInMainHand();
        if (handIcon.getType().isAir()) {
            warp.setIcon(null);
            warp.save();
            player.sendMessage("removed icon from " + warp.getId());
            return;
        }

        warp.setIcon(handIcon);
        warp.save();
        player.sendMessage("set icon for " + warp.getId());
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
