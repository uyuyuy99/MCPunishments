package me.uyuyuy99.punishments.db;

import me.uyuyuy99.punishments.PunishmentManager;
import me.uyuyuy99.punishments.Punishments;
import me.uyuyuy99.punishments.type.IpPunishment;
import me.uyuyuy99.punishments.type.PlayerPunishment;
import org.bukkit.OfflinePlayer;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public abstract class Database {

    protected PunishmentManager manager;

    public Database() {
        this.manager = Punishments.plugin().getManager();
    }

    public abstract void connect() throws Exception;

    public abstract void disconnect();

    public abstract void loadValidPunishments() throws SQLException;

    protected abstract CompletableFuture<Integer> addPlayerPunishment(String type, OfflinePlayer player, String reason, long validUntil);

    public abstract void removePlayerPunishment(PlayerPunishment punishment);

    public abstract CompletableFuture<Integer> addIpBan(String ip, String reason);

    public abstract void removeIpBan(IpPunishment punishment);

    public CompletableFuture<Integer> addBan(OfflinePlayer player, String reason, long validUntil) {
        return addPlayerPunishment("ban", player, reason, validUntil);
    }
    public CompletableFuture<Integer> addBan(OfflinePlayer player, String reason) {
        return addBan(player, reason, 0);
    }

    public CompletableFuture<Integer> addMute(OfflinePlayer player, String reason, long validUntil) {
        return addPlayerPunishment("mute", player, reason, validUntil);
    }
    public CompletableFuture<Integer> addMute(OfflinePlayer player, String reason) {
        return addMute(player, reason, 0);
    }

    public void addKick(OfflinePlayer player, String reason) {
        addPlayerPunishment("kick", player, reason, 0);
    }

}
