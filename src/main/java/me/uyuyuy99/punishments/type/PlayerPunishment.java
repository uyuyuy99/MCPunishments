package me.uyuyuy99.punishments.type;

import lombok.Getter;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@Getter
public abstract class PlayerPunishment {

    private final int id;
    private UUID uuid;
    private String reason;

    public PlayerPunishment(int id, OfflinePlayer player, String reason) {
        this.id = id;
        this.uuid = player.getUniqueId();
        this.reason = reason;
    }

    public PlayerPunishment(int id, String uuidString, String reason) {
        this.id = id;
        this.uuid = UUID.fromString(uuidString);
        this.reason = reason;
    }

}
