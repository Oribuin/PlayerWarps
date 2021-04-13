package xyz.oribuin.playerwarps.command.subcommand;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oribuin.orilibrary.command.SubCommand;
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.NotNull;
import xyz.oribuin.orilibrary.util.HexUtils;
import xyz.oribuin.orilibrary.util.StringPlaceholders;
import xyz.oribuin.playerwarps.PlayerWarps;
import xyz.oribuin.playerwarps.command.CmdPlayerWarp;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.manager.MessageManager;
import xyz.oribuin.playerwarps.obj.Warp;

import java.util.Optional;

@SubCommand.Info(
        names = {"desc"},
        permission = "playerwarps.name",
        usage = "/pw desc <warp> <new-desc>",
        command = CmdPlayerWarp.class
)
public class SubDescription extends SubCommand {

    private final PlayerWarps plugin = (PlayerWarps) this.getOriPlugin();
    private final MessageManager msg = this.plugin.getManager(MessageManager.class);
    private final DataManager data = this.plugin.getManager(DataManager.class);

    public SubDescription(PlayerWarps plugin, CmdPlayerWarp cmd) {
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
        if (!player.hasPermission("playerwarps.name.other") && !warp.getOwner().equals((player).getUniqueId())) {
            this.msg.sendMessage(sender, "dont-own-warp");
            return;
        }

        // Remove chat colors from message
        String desc = String.join(" ", args).substring(args[0].length() + args[1].length() + 2);
        if (this.plugin.getConfig().getBoolean("ignore-desc-colors")) {
            desc = ChatColor.stripColor(HexUtils.colorify(desc));
        }

        // Check length
        if (desc.length() > this.plugin.getConfig().getInt("max-desc-length")) {
            this.msg.sendMessage(sender, "max-length", StringPlaceholders.single("chars", this.plugin.getConfig().getInt("max-desc-length")));
            return;
        }

        final String oldDesc = warp.getDescription();

        warp.setDescription(args[2]);


        this.data.updateWarp(warp);
        this.msg.sendMessage(sender, "changed-desc", StringPlaceholders.builder("oldDesc", oldDesc).addPlaceholder("newName", desc).build());

    }

}
