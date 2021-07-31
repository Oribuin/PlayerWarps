package xyz.oribuin.playerwarps.manager;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.oribuin.orilibrary.manager.Manager;
import xyz.oribuin.orilibrary.util.FileUtils;
import xyz.oribuin.orilibrary.util.StringPlaceholders;
import xyz.oribuin.playerwarps.PlayerWarps;

import java.io.File;
import java.io.IOException;

import static xyz.oribuin.orilibrary.util.HexUtils.colorify;

public class MessageManager extends Manager {

    private final PlayerWarps plugin = (PlayerWarps) this.getPlugin();

    private FileConfiguration config;

    public MessageManager(PlayerWarps plugin) {
        super(plugin);
    }

    public static String applyPapi(CommandSender sender, String text) {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            return text;

        return PlaceholderAPI.setPlaceholders(sender instanceof Player ? (Player) sender : null, text);
    }

    @Override
    public void enable() {
        this.config = YamlConfiguration.loadConfiguration(FileUtils.createFile(this.plugin, "messages.yml"));

        // Set any values that dont exist
        for (Messages msg : Messages.values()) {

            final String key = msg.name().toLowerCase().replace("_", "-");

            if (config.get(key) == null) {
                config.set(key, msg.value);
            }

        }

        try {
            config.save(new File(plugin.getDataFolder(), "messages.yml"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Send a configuration message without any placeholders
     *
     * @param receiver  The CommandSender who receives the message.
     * @param messageId The message path
     */
    public void send(CommandSender receiver, String messageId) {
        this.send(receiver, messageId, StringPlaceholders.empty());
    }

    /**
     * Send a configuration messageId with placeholders.
     *
     * @param receiver     The CommandSender who receives the messageId.
     * @param messageId    The messageId path
     * @param placeholders The Placeholders
     */
    public void send(CommandSender receiver, String messageId, StringPlaceholders placeholders) {
        final String msg = this.getConfig().getString(messageId);

        if (msg == null) {
            receiver.sendMessage(colorify("&c&lError &7| &fThis is an invalid message in the messages file, Please contact the server owner about this issue. (Id: " + messageId + ")"));
            return;
        }

        final String prefix = this.getConfig().getString("prefix");
        receiver.sendMessage(colorify(prefix + apply(receiver instanceof Player ? receiver : null, placeholders.apply(msg))));
    }

    /**
     * Send a raw message to the receiver without any placeholders
     * <p>
     * Use this to send a message to a player without the message being defined in a config.
     *
     * @param receiver The message receiver
     * @param message  The raw message
     */
    public void sendRaw(CommandSender receiver, String message) {
        this.sendRaw(receiver, message, StringPlaceholders.empty());
    }

    /**
     * Send a raw message to the receiver with placeholders.
     * <p>
     * Use this to send a message to a player without the message being defined in a config.
     *
     * @param receiver     The message receiver
     * @param message      The message
     * @param placeholders Message Placeholders.
     */
    public void sendRaw(CommandSender receiver, String message, StringPlaceholders placeholders) {
        receiver.sendMessage(colorify(apply(receiver instanceof Player ? receiver : null, placeholders.apply(message))));
    }

    public String get(String message) {
        return colorify(this.config.getString(message) != null ? this.config.getString(message) : Messages.valueOf(message.replace("-", "_")).value);
    }

    @Override
    public void disable() {

    }

    public String apply(CommandSender sender, String text) {
        return applyPapi(sender, text);
    }

    public enum Messages {
        PREFIX("&b&lPlayerWarps &8| &f"),
        CREATED_WARP("You have created the warp for $%price%, &b%warp%&f!"),
        DELETED_WARP("You have deleted the warp, &b%warp%&f!"),
        TELEPORTED_TO_WARP("You have been teleported to &b%warp%&f!"),
        WARP_LOCKED("This warp is currently locked!"),
        CHANGED_NAME("You have changed the name of %oldName% to %newName%!"),
        CHANGED_DESC("You have changed the description of %oldDesc% to %newDesc%!"),
        MAX_WARPS("&cYou have reached the max warps you can create."),
        MAX_LENGTH("Your message cannot be over %chars% characters!"),
        WARP_EXISTS("This warp already exists!"),
        DONT_OWN_WARP("You do not own this warp."),

        RELOAD("You have reloaded PlayerWarps!"),
        NO_PERM("&cYou do not have permission to execute this command."),
        INVALID_PLAYER("&cPlease enter a valid player."),
        INVALID_ARGS("&cPlease provide valid arguments. Correct usage: %usage%"),
        INVALID_FUNDS("&cYou do not have enough funds to do this, You need $%price%."),
        INVALID_ITEM("This is not a valid item!"),
        INVALID_WARP("This warp does not exists."),
        UNKNOWN_COMMAND("&cPlease include a valid command."),
        PLAYER_ONLY("&cOnly a player can execute this command."),
        CONSOLE_ONLY("&cOnly console can execute this command.");

        private final String value;

        Messages(final String value) {
            this.value = value;
        }

    }
}
