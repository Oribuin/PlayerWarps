package xyz.oribuin.playerwarps.command.subcommand;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oribuin.orilibrary.command.SubCommand;
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.NotNull;
import xyz.oribuin.playerwarps.PlayerWarps;
import xyz.oribuin.playerwarps.command.CmdPlayerWarp;

@SubCommand.Info(
        names = {"menu"},
        permission = "playerwarps.menu",
        usage = "/pw menu",
        command = CmdPlayerWarp.class
)
public class SubMenu extends SubCommand {

    private final PlayerWarps plugin = (PlayerWarps) this.getOriPlugin();

    public SubMenu(PlayerWarps plugin, CmdPlayerWarp cmd) {
        super(plugin, cmd);
    }

    @Override
    public void executeArgument(@NotNull CommandSender sender, @NotNull String[] args) {

        this.plugin.getWarpMenu().createMenu((Player) sender);

    }

}
