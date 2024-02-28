package xyz.oribuin.playerwarps.database.migration;

import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.DatabaseConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class _1_CreateInitialTables extends DataMigration {

    public _1_CreateInitialTables() {
        super(1);
    }

    @Override
    public void migrate(DatabaseConnector connector, Connection connection, String tablePrefix) throws SQLException {
        String primary = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "warps(" +
                         "`id` TEXT NOT NULL PRIMARY KEY, " +
                         "`owner` VARCHAR(36) NULL, " +
                         "`owner_name` TEXT NOT NULL, " +
                         "`created` LONG NOT NULL DEFAULT 0, " +
                         "`x` DOUBLE NOT NULL, " +
                         "`y` DOUBLE NOT NULL, " +
                         "`z` DOUBLE NOT NULL, " +
                         "`yaw` FLOAT NOT NULL, " +
                         "`pitch` FLOAT NOT NULL, " +
                         "`world` TEXT NOT NULL)";

        String settings = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "settings(" +
                          "`id` TEXT NOT NULL PRIMARY KEY, " +
                          "`display_name` TEXT NOT NULL, " +
                          "`description` TEXT NOT NULL, " +
                          "`public` BOOLEAN NOT NULL DEFAULT 0, " +
                          "`teleport_cost` DOUBLE NOT NULL DEFAULT 0, " +
                          "`icon` VARBINARY(2456))";

        String lists = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "lists(" +
                       "`id` TEXT NOT NULL PRIMARY KEY, " +
                       "`banned` TEXT NOT NULL, " +
                       "`visitors` TEXT NOT NULL)";

        try (Statement statement = connection.createStatement()) {
            statement.addBatch(primary);
            statement.addBatch(settings);
            statement.addBatch(lists);
            statement.executeBatch();
        }

    }


}
