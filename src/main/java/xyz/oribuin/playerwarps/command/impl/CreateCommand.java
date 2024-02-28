package xyz.oribuin.playerwarps.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.model.Warp;

import java.util.regex.Pattern;

public class CreateCommand extends BaseRoseCommand {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{1,16}$");

    public CreateCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player player = (Player) context.getSender();
        String name = context.get("name");

        // TODO: Add locale messages
        if (!NAME_PATTERN.matcher(name).matches()) {
            player.sendMessage("Invalid name");
            return;
        }

        DataManager manager = this.rosePlugin.getManager(DataManager.class);
        if (manager.get(name) != null) {
            player.sendMessage("Warp already exists");
            return;
        }

        // TODO: Check for disabled worlds
        // TODO: Check for max warps
        // TODO: Check for if player is in cooldown
        // TODO: Check if the player has enough to make it

        Warp warp = new Warp(name, player.getUniqueId(), player.getLocation());
        manager.update(warp);

        player.sendMessage("Warp created wahoo");
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("create")
                .descriptionKey("command-create-description")
                .permission("playerwarps.create")
                // todo: playeronly
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("name", ArgumentHandlers.STRING)
                .build();
    }

}
