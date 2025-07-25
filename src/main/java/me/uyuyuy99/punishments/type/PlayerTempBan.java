package me.uyuyuy99.punishments.type;

import lombok.Getter;
import org.bukkit.OfflinePlayer;

@Getter
public class PlayerTempBan extends PlayerPunishment {

    private long validUntil;

    public PlayerTempBan(int id, OfflinePlayer player, String reason, long validUntil) {
        super(id, player, reason);
        this.validUntil = validUntil;
    }
    public PlayerTempBan(int id, String uuidString, String reason, long validUntil) {
        super(id, uuidString, reason);
        this.validUntil = validUntil;
    }

    public boolean isStillValid() {
        return validUntil > System.currentTimeMillis();
    }

    public long getValidFor() {
        return validUntil - System.currentTimeMillis();
    }

}
