package xyz.oribuin.playerwarps.command.subcommand;

import org.bukkit.Location;
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
import xyz.oribuin.playerwarps.util.PluginUtils;

@SubCommand.Info(
        names = {"create"},
        permission = "playerwarps.create",
        usage = "/pw create <name>",
        command = CmdPlayerWarp.class
)
public class SubCreate extends SubCommand {

    private final PlayerWarps plugin = (PlayerWarps) this.getOriPlugin();
    private final MessageManager msg = this.plugin.getManager(MessageManager.class);
    private final DataManager data = this.plugin.getManager(DataManager.class);

    public SubCreate(PlayerWarps plugin, CmdPlayerWarp cmd) {
        super(plugin, cmd);
    }

    @Override
    public void executeArgument(@NotNull CommandSender sender, @NotNull String[] args) {

        // Check if Player
        if (!(sender instanceof Player)) {
            this.msg.sendMessage(sender, "player-only");
            return;
        }

        Player player = (Player) sender;
        if (args.length != 2) {
            this.msg.sendMessage(sender, "invalid-arguments", StringPlaceholders.single("usage", this.getAnnotation().usage()));
            return;
        }

        // Check if the player is too broke to do this
        double creationCost = this.plugin.getConfig().getDouble("creation-cost");

        if (creationCost > 0 && !PlayerWarps.getEconomy().has(player, creationCost)) {
            this.msg.sendMessage(sender, "invalid-funds", StringPlaceholders.single("price", creationCost));
            return;
        }

        // Calm down sarah, you can't have too many warps
        if (data.getPlayersWarps(player).size() > PluginUtils.getMaxWarps(player) && !player.hasPermission("playerwarps.max.unlimited")) {
            this.msg.sendMessage(sender, "max-warps");
            return;
        }

        // yo, the warp exists, why you tryna make a new one??
        if (data.getCachedWarps().stream().anyMatch(warp -> warp.getName().equalsIgnoreCase(args[1]))) {
            this.msg.sendMessage(sender, "warp-exists");
            return;
        }

        // Modern day capitalism is poggers
        if (creationCost > 0) PlayerWarps.getEconomy().withdrawPlayer(player, creationCost);

        // Create the warp susan
        final Location location = player.getLocation();

        Warp warp = new Warp(player.getUniqueId(), location, args[1]);

        // Creates the warp
        this.plugin.getManager(DataManager.class).updateWarp(warp);
        msg.sendMessage(sender, "created-warp", StringPlaceholders.builder("warp", warp.getName()).addPlaceholder("price", creationCost).build());

    }

}
