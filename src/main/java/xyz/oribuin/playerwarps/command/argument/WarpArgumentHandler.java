package xyz.oribuin.playerwarps.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import org.bukkit.entity.Player;
import xyz.oribuin.playerwarps.PlayerWarpsPlugin;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.model.Warp;

import java.util.List;

public class WarpArgumentHandler extends ArgumentHandler<Warp> {

    public WarpArgumentHandler() {
        super(Warp.class);
    }

    @Override
    public Warp handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();

        Warp warp = PlayerWarpsPlugin.get().getManager(DataManager.class)
                .getWarps()
                .get(input);

        if (warp == null)
            throw new HandledArgumentException("argument-handler-warp");

        return warp;
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        DataManager manager = PlayerWarpsPlugin.get().getManager(DataManager.class);

        if (context.getSender() instanceof Player player && !player.hasPermission("playerwarps.bypass")) {
            return manager.getOwned(player.getUniqueId())
                    .stream()
                    .map(Warp::getId)
                    .toList();
        }

        return manager.getWarps().keySet().stream().toList();
    }

}
