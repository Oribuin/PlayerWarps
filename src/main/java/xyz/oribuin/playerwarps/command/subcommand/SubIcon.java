package xyz.oribuin.playerwarps.command.subcommand;

import org.bukkit.Material;
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
        names = {"icon"},
        permission = "playerwarps.icon",
        usage = "/pw icon <warp> <icon>",
        command = CmdPlayerWarp.class
)
public class SubIcon extends SubCommand {

    private final PlayerWarps plugin = (PlayerWarps) this.getOriPlugin();
    private final MessageManager msg = this.plugin.getManager(MessageManager.class);
    private final DataManager data = this.plugin.getManager(DataManager.class);

    public SubIcon(PlayerWarps plugin, CmdPlayerWarp cmd) {
        super(plugin, cmd);
    }

    @Override
    public void executeArgument(@NotNull CommandSender sender, @NotNull String[] args) {

        // Check if Player
        if (!(sender instanceof Player)) {
            this.msg.sendMessage(sender, "player-only");
            return;
        }

        final Player player = (Player) sender;

        // Check arguments
        if (args.length != 3) {
            this.msg.sendMessage(sender, "invalid-arguments", StringPlaceholders.single("usage", this.getAnnotation().usage()));
            return;
        }

        final Optional<Warp> optionalWarp = data.getCachedWarps().stream().filter(x -> x.getName().equalsIgnoreCase(args[1])).findAny();

        // Check if warp exists.
        if (!optionalWarp.isPresent()) {
            this.msg.sendMessage(sender, "invalid-warp");
            return;
        }

        final Warp warp = optionalWarp.get();

        // Check if player does not have have permission or sender is player and warp owner !equals sender unique
        if (!player.hasPermission("playerwarps.icon.other") && !warp.getOwner().equals((player).getUniqueId())) {
            this.msg.sendMessage(sender, "dont-own-warp");
            return;
        }

        // Remove chat colors from message
        final Material item = Material.matchMaterial(args[2].toUpperCase());

        // Check length
        if (item == null || this.plugin.getConfig().getStringList("disabled-icons").contains(item.name())) {
            this.msg.sendMessage(sender, "invalid-item");
            return;
        }

        warp.setIcon(item);

        this.data.updateWarp(warp);
        this.msg.sendMessage(sender, "changed-icon", StringPlaceholders.single("icon", item.name()));

    }

}
