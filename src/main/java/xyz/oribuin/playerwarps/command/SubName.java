package xyz.oribuin.playerwarps.command;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oribuin.orilibrary.command.SubCommand;
import xyz.oribuin.orilibrary.util.HexUtils;
import xyz.oribuin.orilibrary.util.StringPlaceholders;
import xyz.oribuin.playerwarps.PlayerWarps;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.manager.MessageManager;
import xyz.oribuin.playerwarps.obj.Warp;

import java.util.Optional;

@SubCommand.Info(
        names = {"name"},
        permission = "playerwarps.name",
        usage = "/pw name <warp> <new-name>"
)
public class SubName extends SubCommand {

    private final PlayerWarps plugin = (PlayerWarps) this.getOriPlugin();
    private final MessageManager msg = this.plugin.getManager(MessageManager.class);
    private final DataManager data = this.plugin.getManager(DataManager.class);

    public SubName(PlayerWarps plugin, CmdPlayerWarp cmd) {
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
        if (args.length != 3) {
            this.msg.send(sender, "invalid-arguments", StringPlaceholders.single("usage", this.getInfo().usage()));
            return;
        }

        final Optional<Warp> optionalWarp = data.getWarpByName(args[1]);

        // Check if warp exists.
        if (optionalWarp.isEmpty()) {
            this.msg.send(sender, "invalid-warp");
            return;
        }

        final Warp warp = optionalWarp.get();

        // Check if player does not have have permission or sender is player and warp owner !equals sender unique
        if (!player.hasPermission("playerwarps.name.other") && !warp.getOwner().equals((player).getUniqueId())) {
            this.msg.send(sender, "dont-own-warp");
            return;
        }

        // Remove chat colors from message
        String desc = args[1];
        if (this.plugin.getConfig().getBoolean("ignore-desc-colors")) {
            desc = ChatColor.stripColor(HexUtils.colorify(desc));
        }

        // Check length
        if (desc.length() > this.plugin.getConfig().getInt("max-name-length")) {
            this.msg.send(sender, "max-length", StringPlaceholders.single("chars", this.plugin.getConfig().getInt("max-desc-length")));
            return;
        }


        final String name = warp.getName();
        warp.setDisplayName(args[2]);
        this.data.updateWarp(warp);
        this.msg.send(sender, "changed-name", StringPlaceholders.builder("oldName", name).addPlaceholder("newName", args[2]).build());

    }

}
