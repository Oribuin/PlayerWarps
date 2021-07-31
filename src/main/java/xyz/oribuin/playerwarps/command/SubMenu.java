package xyz.oribuin.playerwarps.command;

import org.bukkit.command.CommandSender;
import xyz.oribuin.orilibrary.command.SubCommand;
import xyz.oribuin.playerwarps.PlayerWarps;

@SubCommand.Info(
        names = {"menu"},
        permission = "playerwarps.menu",
        usage = "/pw menu"
)
public class SubMenu extends SubCommand {

    private final PlayerWarps plugin = (PlayerWarps) this.getOriPlugin();

    public SubMenu(PlayerWarps plugin, CmdPlayerWarp cmd) {
        super(plugin, cmd);
    }

    @Override
    public void executeArgument(CommandSender sender, String[] args) {
        // todo
    }

}
