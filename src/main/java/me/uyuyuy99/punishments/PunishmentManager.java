package me.uyuyuy99.punishments;

import lombok.Getter;
import me.uyuyuy99.punishments.db.Database;
import me.uyuyuy99.punishments.type.*;
import me.uyuyuy99.punishments.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PunishmentManager {

    private Database db;

    private Map<UUID, PlayerBan> playerBans = new HashMap<>();
    private Map<UUID, PlayerTempBan> playerTempBans = new HashMap<>();
    private Map<UUID, PlayerMute> playerMutes = new HashMap<>();
    private Map<UUID, PlayerTempMute> playerTempMutes = new HashMap<>();
    private Map<String, IpBan> ipBans = new HashMap<>();

    public PunishmentManager() {
        this.db = Punishments.plugin().getDatabase();
    }

    public boolean isBanned(OfflinePlayer player) {
        return playerBans.containsKey(player.getUniqueId()) || playerTempBans.containsKey(player.getUniqueId());
    }

    public boolean isMuted(OfflinePlayer player) {
        return playerMutes.containsKey(player.getUniqueId()) || playerTempMutes.containsKey(player.getUniqueId());
    }

    public boolean isIpBanned(String ip) {
        return ipBans.containsKey(ip);
    }

    // Returns true if player was banned, false if already banned
    public boolean banPlayer(OfflinePlayer player, String reason) {
        if (isBanned(player)) return false;

        if (player instanceof Player) {
            ((Player) player).kickPlayer(Config.getMsg("user.banned", "reason", reason));
        }

        int id = db.addBan(player, reason);
        PlayerBan ban = new PlayerBan(id, player, reason);
        playerBans.put(player.getUniqueId(), ban);

        return true;
    }

    // Returns true if player was tempbanned, false if already banned
    public boolean tempBanPlayer(OfflinePlayer player, String reason, long validUntil) {
        if (isBanned(player)) return false;

        if (player instanceof Player) {
            ((Player) player).kickPlayer(Config.getMsg("user.temp-banned",
                    "time", TimeUtil.formatTime(validUntil / 1000),
                    "reason", reason));
        }

        int id = db.addBan(player, reason, validUntil);
        PlayerTempBan ban = new PlayerTempBan(id, player, reason, System.currentTimeMillis() + validUntil);
        playerTempBans.put(player.getUniqueId(), ban);

        return true;
    }

    // Returns true if player was unbanned, false if he wasn't banned already
    public boolean unbanPlayer(OfflinePlayer player) {
        if (!isBanned(player)) return false;

        PlayerBan ban = playerBans.remove(player.getUniqueId());
        PlayerTempBan tempBan = playerTempBans.remove(player.getUniqueId());

        if (ban != null) db.removePlayerPunishment(ban);
        if (tempBan != null) db.removePlayerPunishment(tempBan);

        return true;
    }

    // Returns true if player was muted, false if already muted
    public boolean mutePlayer(OfflinePlayer player, String reason) {
        if (isMuted(player)) return false;

        if (player instanceof Player) {
            Config.sendMsg("user.muted", ((Player) player), "reason", reason);
        }

        int id = db.addMute(player, reason);
        PlayerMute mute = new PlayerMute(id, player, reason);
        playerMutes.put(player.getUniqueId(), mute);

        return true;
    }

    // Returns true if player was tempmuted, false if already muted
    public boolean tempMutePlayer(OfflinePlayer player, String reason, long validUntil) {
        if (isMuted(player)) return false;

        if (player instanceof Player) {
            Config.sendMsg("user.temp-muted", ((Player) player),
                    "time", TimeUtil.formatTime(validUntil),
                    "reason", reason);
        }

        int id = db.addMute(player, reason, validUntil);
        PlayerTempMute mute = new PlayerTempMute(id, player, reason, System.currentTimeMillis() + validUntil);
        playerTempMutes.put(player.getUniqueId(), mute);

        return true;
    }

    // Returns true if player was unmuted, false if he wasn't muted already
    public boolean unmutePlayer(OfflinePlayer player) {
        if (!isMuted(player)) return false;

        if (player instanceof Player) {
            Config.sendMsg("user.unmuted", ((Player) player));
        }

        PlayerMute mute = playerMutes.remove(player.getUniqueId());
        PlayerTempMute tempMute = playerTempMutes.remove(player.getUniqueId());

        if (mute != null) db.removePlayerPunishment(mute);
        if (tempMute != null) db.removePlayerPunishment(tempMute);

        return true;
    }

    // Returns true if IP was banned, false if already muted
    public boolean banIp(String ip, String reason) {
        if (isIpBanned(ip)) return false;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getAddress().getAddress().getHostAddress().equals(ip)) {
                p.kickPlayer(Config.getMsg("user.banned", "reason", reason));
            }
        }

        int id = db.addIpBan(ip, reason);
        IpBan ban = new IpBan(id, ip, reason);
        ipBans.put(ip, ban);

        return true;
    }

    // Returns true if IP was unbanned, false if it wasn't banned already
    public boolean unbanIp(String ip) {
        if (!isIpBanned(ip)) return false;

        db.removeIpBan(ipBans.remove(ip));

        return true;
    }

}
