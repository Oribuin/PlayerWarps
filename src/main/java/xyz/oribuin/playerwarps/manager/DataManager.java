package xyz.oribuin.playerwarps.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import xyz.oribuin.orilibrary.database.DatabaseConnector;
import xyz.oribuin.orilibrary.database.MySQLConnector;
import xyz.oribuin.orilibrary.database.SQLiteConnector;
import xyz.oribuin.orilibrary.manager.Manager;
import xyz.oribuin.orilibrary.util.FileUtils;
import xyz.oribuin.playerwarps.PlayerWarps;
import xyz.oribuin.playerwarps.obj.Warp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DataManager extends Manager {

    private final PlayerWarps plugin = (PlayerWarps) this.getPlugin();
    private final List<Warp> cachedWarps = new ArrayList<>();
    private DatabaseConnector connector;

    public DataManager(PlayerWarps plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        FileConfiguration config = this.plugin.getConfig();

        if (config.getBoolean("mysql.enabled")) {
            // Define all the MySQL Values.
            String hostName = config.getString("mysql.host");
            int port = config.getInt("mysql.port");
            String dbname = config.getString("mysql.dbname");
            String username = config.getString("mysql.username");
            String password = config.getString("mysql.password");
            boolean ssl = config.getBoolean("mysql.ssl");

            // Connect to MySQL.
            this.connector = new MySQLConnector(this.plugin, hostName, port, dbname, username, password, ssl);
            this.plugin.getLogger().info("Using MySQL for Database ~ " + hostName + ":" + port);
        } else {

            // Create the database File
            FileUtils.createFile(this.plugin, "playerwarps.db");

            // Connect to SQLite
            this.connector = new SQLiteConnector(this.plugin, "playerwarps.db");
            this.getPlugin().getLogger().info("Using SQLite for Database ~ playerwarps.db");
        }

        this.loadWarps();

    }

    /**
     * Create all the required tables for Data Saving.
     */
    private void loadWarps() {

        // Create required tables for the plugin.
        this.async(task -> {
            this.connector.connect(connection -> {

                try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS playerwarps_warps (owner VARCHAR(200), world TXT, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT, `name` TXT, `desc` TXT, icon TXT, locked BOOLEAN)")) {
                    statement.executeUpdate();
                }

            });

            this.cacheWarps();
        });
    }

    /**
     * Load all the warps and cache them.
     */
    private void cacheWarps() {
        this.cachedWarps.clear();

        List<Warp> list = new ArrayList<>();
        this.connector.connect(connection -> {

            String query = "SELECT * FROM playerwarps_warps";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet result = statement.executeQuery();

                while (result.next()) {
                    Location loc = new Location(
                            Bukkit.getWorld(result.getString("world")),
                            result.getDouble("x"),
                            result.getDouble("y"),
                            result.getDouble("z"),
                            result.getFloat("yaw"),
                            result.getFloat("pitch")
                    );

                    Warp warp = new Warp(UUID.fromString(result.getString("owner")), loc, result.getString("name"))
                            .setDescription(result.getString("desc"))
                            .setIcon(Material.valueOf(result.getString("icon")))
                            .setLocked(result.getBoolean("locked"));

                    list.add(warp);
                }

            }
        });

        this.cachedWarps.addAll(list);
    }

    /**
     * Create and save a warp into the database
     *
     * @param player The warp owner
     * @param name   The name of the warp
     * @return The new warp.
     */
    public Warp createWarp(Player player, Location location, String name) {
        Warp warp = new Warp(player.getUniqueId(), location, name);
        Location loc = warp.getLocation();

        this.cachedWarps.add(warp);

        // Add to db
        this.async(task -> this.connector.connect(connection -> {
            String query = "INSERT INTO playerwarps_warps (owner, world, x, y, z, yaw, pitch, `name`, `desc`, icon, locked) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, warp.getOwner().toString());
                statement.setString(2, loc.getWorld().getName());
                statement.setDouble(3, loc.getX());
                statement.setDouble(4, loc.getY());
                statement.setDouble(5, loc.getZ());
                statement.setFloat(6, loc.getYaw());
                statement.setFloat(7, loc.getPitch());
                statement.setString(8, warp.getName());
                statement.setString(9, warp.getDescription());
                statement.setString(10, warp.getIcon().name());
                statement.setBoolean(11, warp.isLocked());
                statement.executeUpdate();

            }

        }));

        return warp;
    }

    /**
     * Delete a warp from the database
     *
     * @param warp The warp
     */
    public void deleteWarp(Warp warp) {
        this.cachedWarps.remove(warp);

        String query = "DELETE FROM playerwarps_warps WHERE owner = ? AND world = ? AND x = ? AND y = ? AND z = ? AND yaw = ? AND pitch = ? AND `name` = ? AND `desc` = ?";
        this.async(task -> this.connector.connect(connection -> {
            final Location loc = warp.getLocation();

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, warp.getOwner().toString());
                statement.setString(2, loc.getWorld().getName());
                statement.setDouble(3, loc.getX());
                statement.setDouble(4, loc.getY());
                statement.setDouble(5, loc.getZ());
                statement.setFloat(6, loc.getYaw());
                statement.setFloat(7, loc.getPitch());
                statement.setString(8, warp.getName());
                statement.setString(9, warp.getDescription());
                statement.executeUpdate();
            }

        }));

    }

    public List<Warp> getPlayersWarps(OfflinePlayer player) {
        return this.cachedWarps.stream().filter(warp -> warp.getOwner().equals(player.getUniqueId())).collect(Collectors.toList());
    }

    @Override
    public void disable() {
        this.connector.closeConnection();
    }

    public void async(Consumer<BukkitTask> callback) {
        new Thread(() -> this.plugin.getServer().getScheduler().runTaskAsynchronously(plugin, callback)).start();
    }

    public List<Warp> getCachedWarps() {
        return cachedWarps;
    }
}
