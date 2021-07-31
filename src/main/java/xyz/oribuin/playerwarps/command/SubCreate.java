package xyz.oribuin.playerwarps.command;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oribuin.orilibrary.command.SubCommand;
import xyz.oribuin.orilibrary.util.StringPlaceholders;
import xyz.oribuin.playerwarps.PlayerWarps;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.manager.MessageManager;
import xyz.oribuin.playerwarps.obj.Warp;
import xyz.oribuin.playerwarps.util.PluginUtils;

@SubCommand.Info(
        names = {"create"},
        permission = "playerwarps.create",
        usage = "/pw create <name>"
)
public class SubCreate extends SubCommand {

    private final PlayerWarps plugin = (PlayerWarps) this.getOriPlugin();
    private final MessageManager msg = this.plugin.getManager(MessageManager.class);
    private final DataManager data = this.plugin.getManager(DataManager.class);

    public SubCreate(PlayerWarps plugin, CmdPlayerWarp cmd) {
        super(plugin, cmd);
    }

    @Override
    public void executeArgument(CommandSender sender, String[] args) {

        // Check if Player
        if (!(sender instanceof Player player)) {
            this.msg.send(sender, "player-only");
            return;
        }

        if (args.length != 2) {
            this.msg.send(sender, "invalid-arguments", StringPlaceholders.single("usage", this.getInfo().usage()));
            return;
        }

        // Check if the player is too broke to do this
        double creationCost = this.plugin.getConfig().getDouble("creation-cost");

        if (creationCost > 0 && !PlayerWarps.getEconomy().has(player, creationCost)) {
            this.msg.send(sender, "invalid-funds", StringPlaceholders.single("price", creationCost));
            return;
        }

        // Calm down sarah, you can't have too many warps
        if (data.getPlayersWarps(player).size() > PluginUtils.getMaxWarps(player) && !player.hasPermission("playerwarps.max.unlimited")) {
            this.msg.send(sender, "max-warps");
            return;
        }

        // yo, the warp exists, why you tryna make a new one??
        if (data.getWarpByName(args[1]).isPresent()) {
            this.msg.send(sender, "warp-exists");
            return;
        }

        // Modern day capitalism is poggers
        if (creationCost > 0)
            PlayerWarps.getEconomy().withdrawPlayer(player, creationCost);

        // Create the warp susan
        final Location location = player.getLocation().clone().add(0.5, 0.0, 0.5);

        final Warp warp = new Warp(player.getUniqueId(), location, args[1]);

        // Creates the warp
        this.plugin.getManager(DataManager.class).updateWarp(warp);
        msg.send(sender, "created-warp", StringPlaceholders.builder("warp", warp.getName()).addPlaceholder("price", creationCost).build());

    }

}
