package xyz.oribuin.playerwarps.command;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xyz.oribuin.orilibrary.command.Command;
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.NotNull;
import xyz.oribuin.playerwarps.PlayerWarps;
import xyz.oribuin.playerwarps.command.subcommand.*;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.manager.MessageManager;
import xyz.oribuin.playerwarps.manager.WarpManager;
import xyz.oribuin.playerwarps.obj.Warp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Command.Info(
        name = "playerwarps",
        description = "Main command for PlayerWarps",
        aliases = {"pw", "warps"},
        permission = "playerwarps.use",
        playerOnly = true,
        usage = "/pw <create> <name>",
        subcommands = {SubCreate.class, SubDelete.class, SubDescription.class, SubHelp.class, SubIcon.class, SubMenu.class, SubName.class}

)
public class CmdPlayerWarp extends Command {

    private final PlayerWarps plugin = (PlayerWarps) this.getOriPlugin();

    public CmdPlayerWarp(PlayerWarps plugin) {
        super(plugin);
    }

    @Override
    public void runFunction(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        final DataManager data = this.plugin.getManager(DataManager.class);

        if (args.length == 0) {
            new SubHelp(this.plugin, this).executeArgument(sender, args);
            return;
        }

        final Optional<Warp> optionalWarp = data.getCachedWarps().stream().filter(x -> x.getName().equalsIgnoreCase(args[0])).findAny();

        if (!optionalWarp.isPresent()) {
            FileConfiguration config = this.plugin.getManager(MessageManager.class).getMessageConfig();

            this.runSubCommands(sender, args, config.getString("unknown-command"), config.getString("invalid-permission"));
            return;
        }

        final Warp warp = optionalWarp.get();
        this.plugin.getManager(WarpManager.class).teleportToWarp((Player) sender, warp);
    }

    @Override
    public @NotNull List<String> completeString(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {

        final List<String> tabComplete = new ArrayList<>();
        final DataManager data = this.plugin.getManager(DataManager.class);

        if (this.getAnnotation().permission().length() > 0 && !sender.hasPermission(this.getAnnotation().permission()))
            return playerList(sender);

        switch (args.length) {
            case 1: {
                tabComplete.addAll(Arrays.asList("create", "delete", "help", "name", "desc", "icon"));
                tabComplete.addAll(data.getCachedWarps().stream().filter(warp -> !warp.isLocked()).map(Warp::getName).collect(Collectors.toList()));

                break;
            }

            case 2: {
                if (args[0].equalsIgnoreCase("create")) tabComplete.add("<name>");
                if (args[0].equalsIgnoreCase("delete")) tabComplete.addAll(this.getWarpNames(sender, "playerwarps.delete.other"));
                if (args[0].equalsIgnoreCase("name")) tabComplete.addAll(this.getWarpNames(sender, "playerwarps.name.other"));
                if (args[0].equalsIgnoreCase("desc")) tabComplete.addAll(this.getWarpNames(sender, "playerwarps.desc.other"));
                if (args[0].equalsIgnoreCase("icon")) tabComplete.addAll(this.getWarpNames(sender, "playerwarps.icon.other"));
                break;
            }

            case 3: {
                if (args[0].equalsIgnoreCase("name")) tabComplete.add("<new-name>");
                if (args[0].equalsIgnoreCase("desc")) tabComplete.add("<new-desc>");
                if (args[0].equalsIgnoreCase("icon")) tabComplete.addAll(Arrays.stream(Material.values()).filter(Material::isItem).filter(it -> !it.name().contains("AIR")).map(x -> x.name().toLowerCase()).collect(Collectors.toList()));
            }

            default:
                tabComplete.addAll(playerList(sender));

        }

        return tabComplete;
    }

    private List<String> getWarpNames(CommandSender sender, String permission) {

        final List<String> tabComplete = new ArrayList<>();
        final DataManager data = this.plugin.getManager(DataManager.class);

        if (sender.hasPermission(permission))
            tabComplete.addAll(data.getCachedWarps().stream().map(Warp::getName).collect(Collectors.toList()));
        else if (sender instanceof Player)
            tabComplete.addAll(data.getCachedWarps().stream().filter(warp -> warp.getOwner().equals(((Player) sender).getUniqueId())).map(Warp::getName).collect(Collectors.toList()));

        return tabComplete;
    }

}
