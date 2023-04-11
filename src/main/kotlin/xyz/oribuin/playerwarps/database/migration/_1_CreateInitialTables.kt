package xyz.oribuin.playerwarps.database.migration

import dev.rosewood.rosegarden.database.DataMigration
import dev.rosewood.rosegarden.database.DatabaseConnector
import dev.rosewood.rosegarden.database.MySQLConnector
import java.sql.Connection
import java.sql.SQLException

class _1_CreateInitialTables : DataMigration(1) {

    @Throws(SQLException::class)
    override fun migrate(connector: DatabaseConnector, connection: Connection, tablePrefix: String) {
        val autoIncrement = if (connector is MySQLConnector) "AUTO_INCREMENT" else ""

        // The primary table for warps, this is where the primary data is stored.
        val primary = "CREATE TABLE IF NOT EXISTS ${tablePrefix}warps (" +
                "`id` INTEGER PRIMARY KEY$autoIncrement, " +
                "`name` TEXT NOT NULL, " +
                "`owner` VARCHAR(36) NOT NULL, " +
                "`owner_name` VARCHAR(24) NULL, " +
                "`x` DOUBLE NOT NULL, " +
                "`y` DOUBLE NOT NULL, " +
                "`z` DOUBLE NOT NULL, " +
                "`yaw` FLOAT NOT NULL, " +
                "`pitch` FLOAT NOT NULL, " +
                "`world` TEXT NOT NULL, " +
                "`creation_time` LONG)"

        connection.prepareStatement(primary).use { it.executeUpdate() }

        // Options mostly for the GUI and other things.
        val settings = "CREATE TABLE IF NOT EXISTS ${tablePrefix}settings (" +
                "`id` INTEGER PRIMARY KEY, " +
                "`display_name` TEXT, " +
                "`description` TEXT, " +
                "`icon` VARBINARY(2456), " +
                "`public` BOOLEAN, " +
                "`teleportFee` DOUBLE)"

        connection.prepareStatement(settings).use { it.executeUpdate() }

        // Large lists of users
        val lists = "CREATE TABLE IF NOT EXISTS ${tablePrefix}lists (" +
                "`id` INTEGER PRIMARY KEY, " +
                "`banned` TEXT, " +
                "`visitors` TEXT, " +
                "`likes` TEXT)"

        connection.prepareStatement(lists).use { it.executeUpdate() }

    }
}
