package me.uyuyuy99.punishments.db;

import me.uyuyuy99.punishments.Punishments;
import me.uyuyuy99.punishments.history.HistoryRecord;
import me.uyuyuy99.punishments.type.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.checkerframework.checker.units.qual.A;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MysqlDatabase extends SqlDatabase {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    public MysqlDatabase(String host, int port, String database, String username, String password) {
        super();
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.dbType = "MySQL";
        this.playerSql = "CREATE TABLE IF NOT EXISTS `player_punishments` ("
                + "  `record_id` int NOT NULL AUTO_INCREMENT,"
                + "  `uuid` char(36) NOT NULL,"
                + "  `punishment` varchar(8) NOT NULL,"
                + "  `time_start` bigint NOT NULL,"
                + "  `time_end` bigint NOT NULL DEFAULT '0',"
                + "  `reason` varchar(255) NOT NULL,"
                + "  `valid` tinyint NOT NULL DEFAULT '1',"
                + "  PRIMARY KEY (`record_id`)"
                + ")"
        ;
        this.ipSql = "CREATE TABLE IF NOT EXISTS `ip_bans` ("
                + "  `record_id` int NOT NULL AUTO_INCREMENT,"
                + "  `ip_address` varchar(40)NOT NULL,"
                + "  `time_banned` bigint NOT NULL,"
                + "  `reason` varchar(255) NOT NULL,"
                + "  `valid` tinyint NOT NULL DEFAULT '1',"
                + "  PRIMARY KEY (`record_id`)"
                + ")";
    }

    @Override
    public void connect() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(
                "jdbc:mysql://" + host + ":" + port + "/" + database,
                username, password
        );
        super.connect();
    }

}
