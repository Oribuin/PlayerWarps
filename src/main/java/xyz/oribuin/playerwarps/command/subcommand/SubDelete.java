package xyz.oribuin.playerwarps.command.subcommand;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oribuin.orilibrary.command.SubCommand;
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.NotNull;
import xyz.oribuin.orilibrary.util.StringPlaceholders;
import xyz.oribuin.playerwarps.PlayerWarps;
import xyz.oribuin.playerwarps.command.CmdPlayerWarp;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.manager.MessageManager;
import xyz.oribuin.playerwarps.obj.Warp;

import java.util.Optional;

@SubCommand.Info(
        names = {"delete"},
        permission = "playerwarps.delete",
        usage = "/pw delete <name>",
        command = CmdPlayerWarp.class
)
public class SubDelete extends SubCommand {

    private final PlayerWarps plugin = (PlayerWarps) this.getOriPlugin();
    private final MessageManager msg = this.plugin.getManager(MessageManager.class);
    private final DataManager data = this.plugin.getManager(DataManager.class);

    public SubDelete(PlayerWarps plugin, CmdPlayerWarp cmd) {
        super(plugin, cmd);
    }

    @Override
    public void executeArgument(@NotNull CommandSender sender, @NotNull String[] args) {

        // Check arguments
        if (args.length != 2) {
            this.msg.sendMessage(sender, "invalid-arguments", StringPlaceholders.single("usage", this.getAnnotation().usage()));
            return;
        }

        final Optional<Warp> optionalWarp = data.getCachedWarps().stream().filter(x -> x.getName().equalsIgnoreCase(args[1])).findAny();

        // Check if warp exists.
        if (!optionalWarp.isPresent()) {
            this.msg.sendMessage(sender, "invalid-warp");
            return;
        }

        Warp warp = optionalWarp.get();

        // Check if player does not have have permission or sender is player and warp owner !equals sender unique
        if (sender instanceof Player && !warp.getOwner().equals(((Player) sender).getUniqueId())) {
            this.msg.sendMessage(sender, "dont-own-warp");
            return;
        }

        this.msg.sendMessage(sender, "deleted-warp", StringPlaceholders.single("warp", warp.getName()));
        this.data.deleteWarp(warp);
    }

}