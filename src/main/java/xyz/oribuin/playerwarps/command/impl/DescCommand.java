package xyz.oribuin.playerwarps.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import xyz.oribuin.playerwarps.command.argument.WarpArgumentHandler;
import xyz.oribuin.playerwarps.manager.ConfigurationManager.Setting;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.model.Warp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DescCommand extends BaseRoseCommand {

    public DescCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player player = (Player) context.getSender();
        Warp warp = context.get("warp");
        String desc = context.get("desc");

        if (!warp.getOwner().equals(player.getUniqueId())) {
            player.sendMessage("You don't own this warp");
            return;
        }

        if (desc == null) {
            warp.setDescription(new ArrayList<>());
            warp.save();
            player.sendMessage("Removed description for " + warp.getId());
            return;
        }

        if (this.getPatterns().stream().anyMatch(p -> p.matcher(desc).matches())) {
            player.sendMessage("Invalid name");
            return;
        }

        warp.setDescription(Arrays.stream(desc.split("\n")).toList());
        warp.save();
        player.sendMessage("Set description for " + warp.getId());
    }

    public List<Pattern> getPatterns() {
        return Setting.WARP_DESC_FILTERS.getStringList().stream()
                .map(Pattern::compile)
                .collect(Collectors.toList());
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("desc")
                .descriptionKey("command-desc-description")
                .permission("playerwarps.desc")
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("warp", new WarpArgumentHandler())
                .optional("desc", ArgumentHandlers.GREEDY_STRING)
                .build();
    }

}
