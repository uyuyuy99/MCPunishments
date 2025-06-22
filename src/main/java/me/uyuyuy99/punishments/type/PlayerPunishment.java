package me.uyuyuy99.punishments.type;

import org.bukkit.OfflinePlayer;

import java.util.UUID;

public abstract class PlayerPunishment {

    private UUID uuid;
    private String reason;

    public PlayerPunishment(OfflinePlayer player, String reason) {
        this.uuid = player.getUniqueId();
        this.reason = reason;
    }

    public PlayerPunishment(String uuidString, String reason) {
        this.uuid = UUID.fromString(uuidString);
        this.reason = reason;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getReason() {
        return reason;
    }

}
