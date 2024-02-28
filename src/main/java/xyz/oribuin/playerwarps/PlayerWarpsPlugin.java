package xyz.oribuin.playerwarps;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import space.arim.morepaperlib.MorePaperLib;
import xyz.oribuin.playerwarps.manager.CommandManager;
import xyz.oribuin.playerwarps.manager.ConfigurationManager;
import xyz.oribuin.playerwarps.manager.DataManager;
import xyz.oribuin.playerwarps.manager.LocaleManager;

import java.util.List;

public class PlayerWarpsPlugin extends RosePlugin {

    public static MorePaperLib PAPER;
    private static PlayerWarpsPlugin instance;

    public PlayerWarpsPlugin() {
        super(
                -1, // The resource id of the plugin
                -1, // The project id of the plugin
                ConfigurationManager.class, // The configuration manager
                DataManager.class, // The data manager
                LocaleManager.class, // The locale manager
                CommandManager.class // The command manager
        );

        instance = this;
        PAPER = new MorePaperLib(this);
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {
    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of();
    }

    public static PlayerWarpsPlugin get() {
        return instance;
    }

}
