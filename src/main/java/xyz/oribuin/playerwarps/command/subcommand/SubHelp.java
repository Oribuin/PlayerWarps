package xyz.oribuin.playerwarps.command.subcommand;

import org.bukkit.command.CommandSender;
import xyz.oribuin.orilibrary.command.SubCommand;
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.NotNull;
import xyz.oribuin.orilibrary.util.HexUtils;
import xyz.oribuin.playerwarps.PlayerWarps;
import xyz.oribuin.playerwarps.command.CmdPlayerWarp;
import xyz.oribuin.playerwarps.manager.MessageManager;

import java.util.ArrayList;
import java.util.Comparator;
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

        final String prefix = this.plugin.getManager(MessageManager.class).getMessageConfig().getString("prefix");
        final List<String> helpMessage = new ArrayList<>();
        final List<SubCommand> subCommands = this.getCommand().getSubCommands();

        // Sort it out barry
        subCommands.sort(Comparator.comparing(cmd -> cmd.getAnnotation().names()[0]));

        // Put your back into it gerald (Add all commands to help message)
        for (SubCommand cmd : subCommands) {
            Info info = cmd.getAnnotation();

            // John i told you, You can't do that (Check if has perrmission)
            if (info.permission().length() > 0 && !sender.hasPermission(info.permission())) continue;

            helpMessage.add(HexUtils.colorify(prefix + "&b» &f" + info.usage()));
        }

        // Clearly these are the commands, linda
        helpMessage.forEach(sender::sendMessage);

    }

}
