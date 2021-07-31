package xyz.oribuin.playerwarps.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oribuin.orilibrary.command.SubCommand;
import xyz.oribuin.orilibrary.util.StringPlaceholders;
import xyz.oribuin.playerwarps.PlayerWarps;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.manager.MessageManager;
import xyz.oribuin.playerwarps.obj.Warp;

import java.util.Optional;

@SubCommand.Info(
        names = {"delete"},
        permission = "playerwarps.delete",
        usage = "/pw delete <name>"
)
public class SubDelete extends SubCommand {

    private final PlayerWarps plugin = (PlayerWarps) this.getOriPlugin();
    private final MessageManager msg = this.plugin.getManager(MessageManager.class);
    private final DataManager data = this.plugin.getManager(DataManager.class);

    public SubDelete(PlayerWarps plugin, CmdPlayerWarp cmd) {
        super(plugin, cmd);
    }

    @Override
    public void executeArgument(CommandSender sender, String[] args) {

        // Check if Player
        if (!(sender instanceof final Player player)) {
            this.msg.send(sender, "player-only");
            return;
        }

        // Check arguments
        if (args.length != 2) {
            this.msg.send(sender, "invalid-arguments", StringPlaceholders.single("usage", this.getInfo().usage()));
            return;
        }

        final Optional<Warp> optionalWarp = data.getWarpByName(args[1]);

        // Check if warp exists.
        if (optionalWarp.isEmpty()) {
            this.msg.send(sender, "invalid-warp");
            return;
        }

        Warp warp = optionalWarp.get();

        // Check if player does not have have permission or sender is player and warp owner !equals sender unique
        if (!player.hasPermission("playerwarps.delete.other") && !warp.getOwner().equals((player).getUniqueId())) {
            this.msg.send(sender, "dont-own-warp");
            return;
        }

        this.msg.send(sender, "deleted-warp", StringPlaceholders.single("warp", warp.getName()));
        this.data.deleteWarp(warp);
    }

}