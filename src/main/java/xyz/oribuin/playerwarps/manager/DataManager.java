package xyz.oribuin.playerwarps.manager;

import com.google.gson.Gson;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import xyz.oribuin.playerwarps.PlayerWarpsPlugin;
import xyz.oribuin.playerwarps.database.migration._1_CreateInitialTables;
import xyz.oribuin.playerwarps.model.Warp;
import xyz.oribuin.playerwarps.model.serializer.TextSerialized;
import xyz.oribuin.playerwarps.model.serializer.UUIDSerialized;
import xyz.oribuin.playerwarps.util.WarpUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataManager extends AbstractDataManager {

    private static final Gson GSON = new Gson();
    private final Map<String, Warp> warps = new HashMap<>();

    public DataManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        super.reload();

        this.loadWarps();
    }

    /**
     * Update the cache with the warp
     *
     * @param warp The warp to cache
     */
    public void cache(Warp warp) {
        if (warp == null) return;

        this.warps.put(warp.getId(), warp);
    }

    /**
     * Get a warp by its id from the cache
     *
     * @param id The id of the warp
     * @return The warp
     */
    public Warp get(String id) {
        return this.warps.get(id);
    }

    /**
     * Get a list of warps by their owner uuid
     *
     * @param owner The owner of the warps
     * @return The warp
     */
    public List<Warp> getOwned(UUID owner) {
        return this.warps.values()
                .stream()
                .filter(warp -> warp.getOwner().equals(owner))
                .toList();
    }

    /**
     * Update the database with the warp's data
     * In times like this we wish we were using an ORM
     *
     * @param warp The warp to update
     */
    public void save(Warp warp) {
        this.warps.put(warp.getId(), warp);

        this.async(() -> this.databaseConnector.connect(connection -> {
            String updatePrimary = "REPLACE INTO " + this.getTablePrefix() + "warps (`id`, `owner`, `owner_name`, `created`, `x`, `y`, `z`, `yaw`, `pitch`, `world`, `last_upkeep`)" +
                                   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            String updateSettings = "REPLACE INTO " + this.getTablePrefix() + "settings (`id`, `display_name`, `description`, `public`, `teleport_cost`, `icon`)" +
                                    "VALUES (?, ?, ?, ?, ?, ?)";

            String updateLists = "REPLACE INTO " + this.getTablePrefix() + "lists (`id`, `banned`, `visitors`) VALUES (?, ?, ?)";

            try (
                    PreparedStatement primaryStatement = connection.prepareStatement(updatePrimary);
                    PreparedStatement settingsStatement = connection.prepareStatement(updateSettings);
                    PreparedStatement listsStatement = connection.prepareStatement(updateLists)
            ) {

                // Update the primary table with the warp's data
                primaryStatement.setString(1, warp.getId());
                primaryStatement.setString(2, warp.getOwner().toString());
                primaryStatement.setString(3, warp.getOwnerName());
                primaryStatement.setLong(4, warp.getCreationTime());
                primaryStatement.setDouble(5, warp.getPosition().getX());
                primaryStatement.setDouble(6, warp.getPosition().getY());
                primaryStatement.setDouble(7, warp.getPosition().getZ());
                primaryStatement.setFloat(8, warp.getPosition().getYaw());
                primaryStatement.setFloat(9, warp.getPosition().getPitch());
                primaryStatement.setString(10, warp.getPosition().getWorld().getName());
                primaryStatement.setLong(11, warp.getLastUpkeepTime());
                primaryStatement.executeUpdate();

                // Update the settings table with the warp's settings
                settingsStatement.setString(1, warp.getId());
                settingsStatement.setString(2, warp.getDisplayName());
                settingsStatement.setString(3, GSON.toJson(new TextSerialized(warp.getDescription())));
                settingsStatement.setBoolean(4, warp.isPublic());
                settingsStatement.setDouble(5, warp.getTeleportFee());
                settingsStatement.setBytes(6, WarpUtils.serializeItem(warp.getIcon()));
                settingsStatement.executeUpdate();

                // Update the lists table with the warp's lists
                listsStatement.setString(1, warp.getId());
                listsStatement.setString(2, GSON.toJson(new UUIDSerialized(warp.getBanned())));
                listsStatement.setString(3, GSON.toJson(new UUIDSerialized(warp.getVisitors())));
                listsStatement.executeUpdate();
            }
        }));

    }

    /**
     * Delete a warp from the database and cache
     *
     * @param id The id of the warp to delete
     */
    public void delete(String id) {
        this.warps.remove(id);

        this.async(() -> this.databaseConnector.connect(connection -> {
            String deletePrimary = "DELETE FROM " + this.getTablePrefix() + "warps WHERE id = ?";
            String deleteSettings = "DELETE FROM " + this.getTablePrefix() + "settings WHERE id = ?";
            String deleteLists = "DELETE FROM " + this.getTablePrefix() + "lists WHERE id = ?";

            try (
                    PreparedStatement primaryStatement = connection.prepareStatement(deletePrimary);
                    PreparedStatement settingsStatement = connection.prepareStatement(deleteSettings);
                    PreparedStatement listsStatement = connection.prepareStatement(deleteLists)
            ) {
                primaryStatement.setString(1, id);
                settingsStatement.setString(1, id);
                listsStatement.setString(1, id);

                primaryStatement.executeUpdate();
                settingsStatement.executeUpdate();
                listsStatement.executeUpdate();
            }
        }));
    }

    /**
     * Load all the warps from the plugin database
     * This method is called when the plugin is enabled
     */
    private void loadWarps() {
        this.warps.clear();

        this.async(() -> this.databaseConnector.connect(connection -> {

            String selectPrimary = "SELECT * FROM " + this.getTablePrefix() + "warps";
            String selectSettings = "SELECT * FROM " + this.getTablePrefix() + "settings";
            String selectLists = "SELECT * FROM " + this.getTablePrefix() + "lists";

            try (PreparedStatement statement = connection.prepareStatement(selectPrimary)) {
                ResultSet result = statement.executeQuery();

                while (result.next()) {

                    String ownerName = result.getString("owner_name");
                    Location position = new Location(
                            Bukkit.getWorld(result.getString("world")),
                            result.getDouble("x"),
                            result.getDouble("y"),
                            result.getDouble("z"),
                            result.getFloat("yaw"),
                            result.getFloat("pitch")
                    );

                    Warp warp = new Warp(result.getString("id"), UUID.fromString(result.getString("owner")), position);
                    warp.setOwnerName(ownerName);
                    warp.setCreationTime(result.getLong("created"));
                    warp.setLastUpkeepTime(result.getLong("last_upkeep"));

                    this.warps.put(warp.getId(), warp);
                }
            }

            try (PreparedStatement statement = connection.prepareStatement(selectSettings)) {
                ResultSet result = statement.executeQuery();

                while (result.next()) {
                    Warp warp = this.warps.get(result.getString("id"));
                    if (warp == null) continue;

                    warp.setDisplayName(result.getString("display_name"));
                    warp.setDescription(GSON.fromJson(result.getString("description"), TextSerialized.class).result());
                    warp.setPublic(result.getBoolean("public"));
                    warp.setTeleportFee(result.getDouble("teleport_cost"));
                    warp.setIcon(WarpUtils.deserializeItem(result.getBytes("icon")));

                    this.warps.put(warp.getId(), warp);
                }
            }

            try (PreparedStatement statement = connection.prepareStatement(selectLists)) {
                ResultSet result = statement.executeQuery();

                while (result.next()) {
                    Warp warp = this.warps.get(result.getString("id"));
                    if (warp == null) continue;

                    warp.setBanned(GSON.fromJson(result.getString("banned"), UUIDSerialized.class).result());
                    warp.setVisitors(GSON.fromJson(result.getString("visitors"), UUIDSerialized.class).result());

                    this.warps.put(warp.getId(), warp);
                }
            }
        }));
    }

    @Override
    public List<Class<? extends DataMigration>> getDataMigrations() {
        return List.of(_1_CreateInitialTables.class);
    }

    /**
     * Execute a runnable async off the main thread
     *
     * @param runnable The runnable to run async
     */
    private void async(Runnable runnable) {
        PlayerWarpsPlugin.SCHEDULER
                .scheduling()
                .asyncScheduler()
                .run(runnable);
    }

    public Map<String, Warp> getWarps() {
        return warps;
    }

}
