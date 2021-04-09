package xyz.oribuin.playerwarps.command.subcommand;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import xyz.oribuin.orilibrary.command.SubCommand;
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.NotNull;
import xyz.oribuin.orilibrary.util.HexUtils;
import xyz.oribuin.playerwarps.PlayerWarps;
import xyz.oribuin.playerwarps.command.CmdPlayerWarp;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.obj.Warp;

@SubCommand.Info(
        names = {"list"},
        permission = "playerwarps.list",
        usage = "/pw list",
        command = CmdPlayerWarp.class
)
public class SubList extends SubCommand {

    private final PlayerWarps plugin = (PlayerWarps) this.getOriPlugin();

    public SubList(PlayerWarps plugin) {
        super(plugin);
    }

    @Override
    public void executeArgument(@NotNull CommandSender sender, @NotNull String[] args) {

        final DataManager data = this.plugin.getManager(DataManager.class);

        sender.sendMessage(HexUtils.colorify("<g:#f953c6:#b91d73>Found Warps: " + data.getCachedWarps().size()));
        int i = 0;
        for (Warp warp : data.getCachedWarps()) {
            sender.sendMessage(HexUtils.colorify("#6A82FBWarp #" + i++ + " &7» #1CB5E0" + warp.getName() + " by " + Bukkit.getOfflinePlayer(warp.getOwner()).getName()));
        }
        data.getCachedWarps().stream()
                .map(warp -> "#6A82FBWarp " + warp.getName() + " by " + Bukkit.getOfflinePlayer(warp.getOwner()).getName())
                .forEach(s -> sender.sendMessage(HexUtils.colorify(s)));

    }

}
