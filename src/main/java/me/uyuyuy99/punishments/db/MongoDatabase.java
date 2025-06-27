package me.uyuyuy99.punishments.db;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import me.uyuyuy99.punishments.Punishments;
import me.uyuyuy99.punishments.history.HistoryRecord;
import me.uyuyuy99.punishments.type.*;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MongoDatabase extends Database {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    private MongoClient client;
    private com.mongodb.client.MongoDatabase db;
    private MongoCollection<Document> counters;
    private MongoCollection<Document> playerRecords;
    private MongoCollection<Document> ipRecords;

    public MongoDatabase(String host, int port, String database, String username, String password) {
        super();
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    private int getNextId() {
        Document result = counters.findOneAndUpdate(
                Filters.eq("_id", "record_id"),
                Updates.inc("seq", 1),
                new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        );
        return result.getInteger("seq");
    }

    @Override
    public void connect() throws Exception {
        client = new MongoClient(host, port);
        db = client.getDatabase(database);
        counters = db.getCollection("counters");
        playerRecords = db.getCollection("player_punishments");
        ipRecords = db.getCollection("ip_bans");

        counters.updateOne(
                Filters.eq("_id", "record_id"),
                Updates.setOnInsert("seq", 0),
                new UpdateOptions().upsert(true)
        );
    }

    @Override
    public void disconnect() {
        client.close();
    }

    @Override
    public void loadValidPunishments() throws SQLException {
        int bans = 0, mutes = 0, ipbans = 0;

        for (Document doc : playerRecords.find(Filters.eq("valid", 1))) {
            int id = doc.getInteger("_id");
            String type = doc.getString("punishment");
            String uuid = doc.getString("uuid");
            long validUntil = doc.getLong("time_end");
            String reason = doc.getString("reason");

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

        for (Document doc : ipRecords.find(Filters.eq("valid", 1))) {
            int id = doc.getInteger("_id");
            String address = doc.getString("ip_address");
            String reason = doc.getString("reason");

            IpBan ban = new IpBan(id, address, reason);
            manager.getIpBans().put(address, ban);
            ipbans++;
        }

        Punishments.plugin().getLogger().info(
                "Successfully loaded " + bans + " active bans, " + mutes + " active mutes, and " + ipbans + " active IP bans from MongoDB.");
    }

    @Override
    public CompletableFuture<List<HistoryRecord>> getPlayerHistory(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            FindIterable<Document> result = playerRecords.find(Filters.eq("uuid", player.getUniqueId().toString()));
            List<HistoryRecord> history = new ArrayList<>();

            for (Document doc : result) {
                String type = doc.getString("punishment");
                long start = doc.getLong("time_start");
                long end = doc.getLong("time_end");
                String reason = doc.getString("reason");
                int valid = doc.getInteger("valid");

                history.add(new HistoryRecord(type, start, end, reason, valid));
            }

            return history;
        });
    }

    @Override
    protected CompletableFuture<Integer> addPlayerPunishment(String type, OfflinePlayer player, String reason, long validUntil) {
        return CompletableFuture.supplyAsync(() -> {
            int id = getNextId();
            playerRecords.insertOne(new Document("_id", id)
                    .append("uuid", player.getUniqueId().toString())
                    .append("punishment", type)
                    .append("time_start", System.currentTimeMillis())
                    .append("time_end", validUntil)
                    .append("reason", reason)
                    .append("valid", type.equals("kick") ? 0 : 1)
            );
            return id;
        });
    }

    @Override
    public void removePlayerPunishment(PlayerPunishment punishment) {
        Bukkit.getScheduler().runTaskAsynchronously(Punishments.plugin(), () -> {
            playerRecords.updateOne(
                    Filters.eq("_id", punishment.getId()),
                    Updates.set("valid", 0)
            );
        });
    }

    @Override
    public CompletableFuture<Integer> addIpBan(String ip, String reason) {
        return CompletableFuture.supplyAsync(() -> {
            int id = getNextId();
            ipRecords.insertOne(new Document("_id", id)
                    .append("ip_address", ip)
                    .append("time_banned", System.currentTimeMillis())
                    .append("reason", reason)
                    .append("valid", 1)
            );
            return id;
        });
    }

    @Override
    public void removeIpBan(IpPunishment punishment) {
        Bukkit.getScheduler().runTaskAsynchronously(Punishments.plugin(), () -> {
            ipRecords.updateOne(
                    Filters.eq("_id", punishment.getId()),
                    Updates.set("valid", 0)
            );
        });
    }

}
