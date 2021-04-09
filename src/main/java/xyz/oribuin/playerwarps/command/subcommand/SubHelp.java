package xyz.oribuin.playerwarps.command.subcommand;

import org.bukkit.command.CommandSender;
import xyz.oribuin.orilibrary.command.SubCommand;
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.NotNull;
import xyz.oribuin.orilibrary.util.HexUtils;
import xyz.oribuin.playerwarps.PlayerWarps;
import xyz.oribuin.playerwarps.command.CmdPlayerWarp;
import xyz.oribuin.playerwarps.manager.DataManager;

import java.util.ArrayList;
import java.util.List;

@SubCommand.Info(
        names = {"help"},
        permission = "playerwarps.help",
        usage = "/pw help",
        command = CmdPlayerWarp.class
)
public class SubHelp extends SubCommand {

    private final PlayerWarps plugin = (PlayerWarps) this.getOriPlugin();

    public SubHelp(PlayerWarps plugin, CmdPlayerWarp cmd) {
        super(plugin, cmd);
    }

    @Override
    public void executeArgument(@NotNull CommandSender sender, @NotNull String[] args) {

        List<String> helpMessage = new ArrayList<>();

        for (SubCommand cmd : getCommand().getSubCommands()) {
            Info info = cmd.getAnnotation();

            if (info.permission().length() > 0 && !sender.hasPermission(info.permission())) continue;

            helpMessage.add(HexUtils.colorify("&b» &f" + info.usage()));
        }

        helpMessage.forEach(sender::sendMessage);

    }

}
