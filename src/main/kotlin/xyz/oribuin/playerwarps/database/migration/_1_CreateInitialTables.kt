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

//
        // holy heckin chonker
        val createTables = "CREATE TABLE IF NOT EXISTS ${tablePrefix}warps (" +
                "`id` INTEGER PRIMARY KEY$autoIncrement, " +
                "`name` TEXT NOT NULL, " +
                "`owner` VARCHAR(36) NOT NULL, " +
                "`x` DOUBLE NOT NULL, " +
                "`y` DOUBLE NOT NULL, " +
                "`z` DOUBLE NOT NULL, " +
                "`yaw` FLOAT NOT NULL, " +
                "`pitch` FLOAT NOT NULL, " +
                "`world` TEXT NOT NULL, " +
                "`display_name` TEXT, " +
                "`description` TEXT, " +
                "`icon` VARBINARY(2456), " +
                "`creation_time` LONG, " +
                "`public` BOOLEAN, " +
                "`teleportFee` DOUBLE, " +
                "`banned` TEXT, " +
                "`visitors` TEXT, " +
                "`likes` TEXT)"

        println(createTables)
        connection.prepareStatement(createTables).use { statement -> statement.executeUpdate() }
    }
}
