package xyz.oribuin.playerwarps.command.subcommand;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oribuin.orilibrary.command.SubCommand;
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.NotNull;
import xyz.oribuin.playerwarps.PlayerWarps;
import xyz.oribuin.playerwarps.command.CmdPlayerWarp;
import xyz.oribuin.playerwarps.manager.DataManager;

@SubCommand.Info(
        names = {"create"},
        permission = "playerwarps.create",
        usage = "/pw create <name>",
        command = CmdPlayerWarp.class
)
public class SubCreate extends SubCommand {

    private final PlayerWarps plugin = (PlayerWarps) this.getOriPlugin();

    public SubCreate(PlayerWarps plugin, CmdPlayerWarp cmd) {
        super(plugin, cmd);
    }

    @Override
    public void executeArgument(@NotNull CommandSender sender, @NotNull String[] args) {

        Player player = (Player) sender;

        // Todo message
        if (args.length != 2) return;

        this.plugin.getManager(DataManager.class).createWarp(player, args[1]);
        player.sendMessage("Created Warp.");
    }

}
