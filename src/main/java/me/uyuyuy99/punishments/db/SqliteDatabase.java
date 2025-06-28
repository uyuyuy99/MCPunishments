package me.uyuyuy99.punishments.db;

import me.uyuyuy99.punishments.Punishments;

import java.io.File;
import java.sql.DriverManager;

public class SqliteDatabase extends SqlDatabase {

    public SqliteDatabase() {
        super();
        this.dbType = "SQLite";
        this.playerSql = "CREATE TABLE IF NOT EXISTS `player_punishments` ("
                + "  `record_id` INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "  `uuid` char(36) NOT NULL,"
                + "  `punishment` varchar(8) NOT NULL,"
                + "  `time_start` bigint NOT NULL,"
                + "  `time_end` bigint NOT NULL DEFAULT '0',"
                + "  `reason` varchar(255) NOT NULL,"
                + "  `valid` tinyint NOT NULL DEFAULT '1'"
                + ")"
        ;
        this.ipSql = "CREATE TABLE IF NOT EXISTS `ip_bans` ("
                + "  `record_id` INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "  `ip_address` varchar(40)NOT NULL,"
                + "  `time_banned` bigint NOT NULL,"
                + "  `reason` varchar(255) NOT NULL,"
                + "  `valid` tinyint NOT NULL DEFAULT '1'"
                + ")";
    }

    @Override
    public void connect() throws Exception {
        File dataFolder = Punishments.plugin().getDataFolder();
        //noinspection ResultOfMethodCallIgnored
        dataFolder.mkdirs();
        connection = DriverManager.getConnection(
                "jdbc:sqlite:" + new File(dataFolder, "punishments.db").getPath()
        );
        super.connect();
    }

}
