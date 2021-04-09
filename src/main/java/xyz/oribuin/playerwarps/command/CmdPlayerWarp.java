package xyz.oribuin.playerwarps.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oribuin.orilibrary.command.Command;
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.NotNull;
import xyz.oribuin.playerwarps.PlayerWarps;
import xyz.oribuin.playerwarps.command.subcommand.SubHelp;
import xyz.oribuin.playerwarps.manager.DataManager;
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
        usage = "/pw <create> <name>"
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
            this.runSubCommands(sender, args, null, null);
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
                tabComplete.addAll(Arrays.asList("create", "help", "list"));
                tabComplete.addAll(data.getCachedWarps().stream().filter(warp -> !warp.isLocked()).map(Warp::getName).collect(Collectors.toList()));

                break;
            }

            case 2: {
                if (args[1].equalsIgnoreCase("create")) tabComplete.add("<name>");
            }

            default:
                tabComplete.addAll(playerList(sender));

        }

        return tabComplete;
    }
}
