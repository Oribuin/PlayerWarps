package xyz.oribuin.playerwarps.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import xyz.oribuin.playerwarps.command.argument.WarpArgumentHandler;
import xyz.oribuin.playerwarps.manager.ConfigurationManager.Setting;
import xyz.oribuin.playerwarps.manager.LocaleManager;
import xyz.oribuin.playerwarps.model.Warp;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NameCommand extends BaseRoseCommand {

    public NameCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        Player player = (Player) context.getSender();
        Warp warp = context.get("warp");
        String name = context.get("name");

        // Check if the player is the owner of the warp
        if (!warp.getOwner().equals(player.getUniqueId()) && !player.hasPermission("playerwarps.bypass")) {
            locale.sendMessage(player, "not-warp-owner");
            return;
        }

        // Make sure the name is not too long
        if (this.getPatterns().stream().anyMatch(p -> p.matcher(name).matches())) {
            locale.sendMessage(player, "command-name-invalid");
            return;
        }

        // Change the name
        warp.setDisplayName(name);
        warp.save();
        locale.sendMessage(player, "command-name-success", StringPlaceholders.of("warp", warp.getId(), "name", name));
    }

    public List<Pattern> getPatterns() {
        return Setting.WARP_NAME_FILTERS.getStringList().stream()
                .map(Pattern::compile)
                .collect(Collectors.toList());
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("name")
                .descriptionKey("command-name-description")
                .permission("playerwarps.name")
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("warp", new WarpArgumentHandler())
                .required("name", ArgumentHandlers.GREEDY_STRING)
                .build();
    }

}
