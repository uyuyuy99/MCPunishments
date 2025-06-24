package me.uyuyuy99.punishments.db;

import me.uyuyuy99.punishments.Punishments;
import me.uyuyuy99.punishments.type.*;
import org.bukkit.OfflinePlayer;

import java.sql.*;

public class MysqlDatabase extends Database {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    private Connection connection;

    public MysqlDatabase(String host, int port, String database, String username, String password) {
        super();
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    @Override
    public void connect() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(
                "jdbc:mysql://" + host + ":" + port + "/" + database,
                username, password
        );

        Statement playerStatement = connection.createStatement();
        playerStatement.execute(
                "CREATE TABLE IF NOT EXISTS `player_punishments` ("
                + "  `record_id` int NOT NULL AUTO_INCREMENT,"
                + "  `uuid` char(36) NOT NULL,"
                + "  `punishment` varchar(8) NOT NULL,"
                + "  `time_start` bigint NOT NULL,"
                + "  `time_end` bigint NOT NULL DEFAULT '0',"
                + "  `reason` varchar(255) NOT NULL,"
                + "  `valid` tinyint NOT NULL DEFAULT '1',"
                + "  PRIMARY KEY (`record_id`)"
                + ")"
        );

        Statement ipStatement = connection.createStatement();
        ipStatement.execute(
                "CREATE TABLE IF NOT EXISTS `ip_bans` ("
                + "  `record_id` int NOT NULL AUTO_INCREMENT,"
                + "  `ip_address` varchar(40)NOT NULL,"
                + "  `time_banned` bigint NOT NULL,"
                + "  `reason` varchar(255) NOT NULL,"
                + "  `valid` tinyint NOT NULL DEFAULT '1',"
                + "  PRIMARY KEY (`record_id`)"
                + ")"
        );
    }

    @Override
    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadValidPunishments() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM player_punishments WHERE valid = 1");

        int bans = 0, mutes = 0;

        while (result.next()) {
            String type = result.getString("punishment");
            int id = result.getInt("record_id");
            String uuid = result.getString("uuid");
            long validUntil = result.getInt("time_end");
            String reason = result.getString("reason");

            if (type.equals("ban")) {
                if (validUntil == 0) {
                    PlayerBan ban = new PlayerBan(id, uuid, reason);
                    manager.getPlayerBans().put(ban.getUuid(), ban);
                } else {
                    PlayerTempBan tempBan = new PlayerTempBan(id, uuid, reason, validUntil);
                    manager.getPlayerTempBans().put(tempBan.getUuid(), tempBan);
                }
                bans++;
            }
            else if (type.equals("mute")) {
                if (validUntil == 0) {
                    PlayerMute mute = new PlayerMute(id, uuid, reason);
                    manager.getPlayerMutes().put(mute.getUuid(), mute);
                } else {
                    PlayerTempMute tempMute = new PlayerTempMute(id, uuid, reason, validUntil);
                    manager.getPlayerTempMutes().put(tempMute.getUuid(), tempMute);
                }
                mutes++;
            }
        }
        result.close();
        statement.close();

        Punishments.plugin().getLogger().info(
                "Successfully loaded " + bans + " active bans and " + mutes + " active mutes from MySQL.");
    }

    @Override
    protected int addPlayerPunishment(String type, OfflinePlayer player, String reason, long validUntil) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO `player_punishments`"
                        + " (`uuid`, `punishment`, `time_start`, `time_end`, `reason`, `valid`)"
                        + " VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, type);
            statement.setLong(3, System.currentTimeMillis());
            statement.setLong(4, validUntil);
            statement.setString(5, reason);
            statement.setInt(6, 1);

            statement.executeUpdate();
            ResultSet key = statement.getGeneratedKeys();
            key.next();
            return key.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removePlayerPunishment(PlayerPunishment punishment) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE `player_punishments` SET `valid` = 0 WHERE `record_id` = ?"
            );
            statement.setInt(1, punishment.getId());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int addIpBan(String ip, String reason) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO `ip_bans`"
                        + " (`ip_address`, `time_banned`, `reason`, `valid`)"
                        + " VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, ip);
            statement.setLong(2, System.currentTimeMillis());
            statement.setString(3, reason);
            statement.setInt(4, 1);
            statement.executeUpdate();

            statement.executeUpdate();
            ResultSet key = statement.getGeneratedKeys();
            key.next();
            return key.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeIpBan(IpPunishment punishment) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE `ip_bans` SET `valid` = 0 WHERE `record_id` = ?"
            );
            statement.setInt(1, punishment.getId());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
