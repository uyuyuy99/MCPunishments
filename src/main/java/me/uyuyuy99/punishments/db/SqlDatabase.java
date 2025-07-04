package me.uyuyuy99.punishments.db;

import me.uyuyuy99.punishments.Punishments;
import me.uyuyuy99.punishments.history.HistoryRecord;
import me.uyuyuy99.punishments.type.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class SqlDatabase extends Database {

    protected Connection connection;
    protected String playerSql;
    protected String ipSql;
    protected String dbType;

    @Override
    public void connect() throws Exception {
        Statement playerStatement = connection.createStatement();
        playerStatement.execute(playerSql);

        Statement ipStatement = connection.createStatement();
        ipStatement.execute(ipSql);
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
        Statement playerStatement = connection.createStatement();
        ResultSet playerResult = playerStatement.executeQuery("SELECT * FROM player_punishments WHERE valid = 1");

        int bans = 0, mutes = 0, ipbans = 0;

        while (playerResult.next()) {
            int id = playerResult.getInt("record_id");
            String type = playerResult.getString("punishment");
            String uuid = playerResult.getString("uuid");
            long validUntil = playerResult.getLong("time_end");
            String reason = playerResult.getString("reason");

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
        playerResult.close();
        playerStatement.close();

        Statement ipStatement = connection.createStatement();
        ResultSet ipResult = ipStatement.executeQuery("SELECT * FROM ip_bans WHERE valid = 1");

        while (ipResult.next()) {
            int id = ipResult.getInt("record_id");
            String address = ipResult.getString("ip_address");
            String reason = ipResult.getString("reason");

            IpBan ban = new IpBan(id, address, reason);
            manager.getIpBans().put(address, ban);
            ipbans++;
        }
        ipResult.close();
        ipStatement.close();

        Punishments.plugin().getLogger().info(
                "Successfully loaded " + bans + " active bans, " + mutes + " active mutes, and " + ipbans + " active IP bans from " + dbType + ".");
    }

    @Override
    public CompletableFuture<List<HistoryRecord>> getPlayerHistory(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM player_punishments WHERE uuid = ?");
                statement.setString(1, player.getUniqueId().toString());
                ResultSet result = statement.executeQuery();

                List<HistoryRecord> history = new ArrayList<>();
                while (result.next()) {
                    String type = result.getString("punishment");
                    long start = result.getLong("time_start");
                    long end = result.getLong("time_end");
                    String reason = result.getString("reason");
                    int valid = result.getInt("valid");

                    history.add(new HistoryRecord(type, start, end, reason, valid));
                }

                result.close();
                statement.close();

                return history;
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    protected CompletableFuture<Integer> addPlayerPunishment(String type, OfflinePlayer player, String reason, long validUntil) {
        return CompletableFuture.supplyAsync(() -> {
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
                statement.setInt(6, type.equals("kick") ? 0 : 1);

                statement.executeUpdate();
                ResultSet key = statement.getGeneratedKeys();
                key.next();
                int id = key.getInt(1);
                key.close();
                statement.close();
                return id;
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void removePlayerPunishment(PlayerPunishment punishment) {
        Bukkit.getScheduler().runTaskAsynchronously(Punishments.plugin(), () -> {
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE `player_punishments` SET `valid` = 0 WHERE `record_id` = ?"
                );
                statement.setInt(1, punishment.getId());
                statement.execute();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Integer> addIpBan(String ip, String reason) {
        return CompletableFuture.supplyAsync(() -> {
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
                ResultSet key = statement.getGeneratedKeys();
                key.next();
                int id = key.getInt(1);
                key.close();
                statement.close();
                return id;
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void removeIpBan(IpPunishment punishment) {
        Bukkit.getScheduler().runTaskAsynchronously(Punishments.plugin(), () -> {
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE `ip_bans` SET `valid` = 0 WHERE `record_id` = ?"
                );
                statement.setInt(1, punishment.getId());
                statement.execute();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}
