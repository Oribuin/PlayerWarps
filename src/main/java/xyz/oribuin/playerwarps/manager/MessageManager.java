package xyz.oribuin.playerwarps.manager;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.oribuin.orilibrary.manager.Manager;
import xyz.oribuin.orilibrary.util.FileUtils;
import xyz.oribuin.orilibrary.util.HexUtils;
import xyz.oribuin.orilibrary.util.StringPlaceholders;
import xyz.oribuin.playerwarps.PlayerWarps;
import xyz.oribuin.playerwarps.hook.PlaceholderAPIHook;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MessageManager extends Manager {

    private final PlayerWarps plugin = (PlayerWarps) this.getPlugin();

    private static FileConfiguration messageConfig;

    public MessageManager(PlayerWarps plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        FileUtils.createFile(this.getPlugin(), "messages.yml");
        messageConfig = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), "messages.yml"));

        for (MsgSetting value : MsgSetting.values()) {
            if (messageConfig.get(value.key) == null) {
                messageConfig.set(value.key, value.defaultValue);
            }

            value.load(messageConfig);
        }

        try {
            messageConfig.save(new File(plugin.getDataFolder(), "messages.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disable() {
        // Unused
    }

    public void sendMessage(CommandSender sender, String message) {
        this.sendMessage(sender, message, StringPlaceholders.empty());
    }

    public void sendMessage(CommandSender sender, String message, StringPlaceholders placeholders) {
        if (messageConfig.getString(message) == null) {
            sender.sendMessage(HexUtils.colorify(messageConfig.getString("prefix") + placeholders.apply(message)));
            return;
        }

        if (messageConfig.getString(message) != null && !messageConfig.getString(message).isEmpty()) {
            final String msg = messageConfig.getString("prefix") + placeholders.apply(messageConfig.getString(message));

            sender.sendMessage(HexUtils.colorify(this.parsePlaceholders(sender, msg)));
        }
    }

    public FileConfiguration getMessageConfig() {
        return messageConfig;
    }

    private String parsePlaceholders(CommandSender sender, String message) {
        if (sender instanceof Player)
            return PlaceholderAPIHook.apply((Player) sender, message);

        return message;
    }

    public enum MsgSetting {
        PREFIX("prefix", "&b&lPlayerWarps &8| &f"),

        RELOAD("reload", "You have reloaded PlayerWarps!"),
        MAX_AUCTIONS("max-warps", "&cYou have reached the max warps."),
        INVALID_PERMISSION("invalid-permission", "&cYou do not have permission to execute this command."),
        INVALID_PLAYER("invalid-player", "&cPlease enter a valid player."),
        INVALID_ARGUMENTS("invalid-arguments", "&cPlease provide valid arguments. Correct usage: %usage%"),
        INVALID_FUNDS("invalid-funds", "&cYou do not have enough funds to do this, You need $%price%."),
        INVALID_ITEM("invalid-item", "This is not a valid item!"),
        UNKNOWN_COMMAND("unknown-command", "&cPlease include a valid command."),
        PLAYER_ONLY("player-only", "&cOnly a player can execute this command."),
        CONSOLE_ONLY("console-only", "&cOnly console can execute this command.");


        private final String key;
        private final Object defaultValue;
        private Object value = null;

        MsgSetting(String key, Object defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }


        /**
         * Gets the setting as a boolean
         *
         * @return The setting as a boolean
         */
        public boolean getBoolean() {
            return (boolean) this.value;
        }

        /**
         * @return the setting as an int
         */
        public int getInt() {
            return (int) this.getNumber();
        }

        /**
         * @return the setting as a long
         */
        public long getLong() {
            return (long) this.getNumber();
        }

        /**
         * @return the setting as a double
         */
        public double getDouble() {
            return this.getNumber();
        }

        /**
         * @return the setting as a float
         */
        public float getFloat() {
            return (float) this.getNumber();
        }

        /**
         * @return the setting as a String
         */
        public String getString() {
            return (String) this.value;
        }

        private double getNumber() {
            if (this.value instanceof Integer) {
                return (int) this.value;
            } else if (this.value instanceof Short) {
                return (short) this.value;
            } else if (this.value instanceof Byte) {
                return (byte) this.value;
            } else if (this.value instanceof Float) {
                return (float) this.value;
            }

            return (double) this.value;
        }

        /**
         * @return the setting as a string list
         */
        @SuppressWarnings("unchecked")
        public List<String> getStringList() {
            return (List<String>) this.value;
        }

        /**
         * Loads the value from the config and caches it
         */
        private void load(FileConfiguration config) {
            this.value = config.get(this.key);
        }
    }
}
