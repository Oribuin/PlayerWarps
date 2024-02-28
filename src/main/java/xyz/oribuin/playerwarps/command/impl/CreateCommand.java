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
import xyz.oribuin.playerwarps.hook.VaultProvider;
import xyz.oribuin.playerwarps.manager.ConfigurationManager.Setting;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.manager.LocaleManager;
import xyz.oribuin.playerwarps.model.Warp;
import xyz.oribuin.playerwarps.util.WarpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class CreateCommand extends BaseRoseCommand {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");
    private final List<UUID> toConfirm = new ArrayList<>();

    public CreateCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        Player player = (Player) context.getSender();
        String name = context.get("name");

        // Make sure the name is valid
        if (!NAME_PATTERN.matcher(name).matches()) {
            locale.sendMessage(player, "command-create-invalid-name");
            return;
        }

        // Make sure the warp doesn't already exist
        DataManager manager = this.rosePlugin.getManager(DataManager.class);
        if (manager.get(name) != null) {
            locale.sendMessage(player, "command-create-exists");
            return;
        }

        // Make sure the player isn't in a disabled world
        if (Setting.DISABLED_WORLDS.getStringList().contains(player.getWorld().getName())) {
            locale.sendMessage(player, "command-create-disabled-world");
            return;
        }

        // Make sure the player isn't at the max warps
        if (manager.getOwned(player.getUniqueId()).size() >= WarpUtils.getMaxWarps(player)) {
            locale.sendMessage(player, "command-create-max-warps");
            return;
        }

        VaultProvider provider = VaultProvider.get();

        // Only check for confirmation if there is a cost
        if (Setting.WARP_CREATE_COST.getDouble() > 0) {

            // Make sure the player has enough funds
            if (!provider.has(player, Setting.WARP_CREATE_COST.getDouble())) {
                locale.sendMessage(player, "invalid-funds");
                return;
            }

            // If the player has already confirmed the creation
            if (this.toConfirm.remove(player.getUniqueId())) {
                provider.take(player, Setting.WARP_CREATE_COST.getDouble());
            } else {
                // Add the player to the confirm list
                this.toConfirm.add(player.getUniqueId());
                locale.sendMessage(player, "command-create-confirm", StringPlaceholders.of("cost", Setting.WARP_CREATE_COST.getDouble()));
                return;
            }
        }

        // Create the warp
        Warp warp = new Warp(name, player.getUniqueId(), player.getLocation());
        warp.save();
        locale.sendMessage(player, "command-create-success", StringPlaceholders.of("warp", warp.getId()));
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
