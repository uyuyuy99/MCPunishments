package me.uyuyuy99.punishments.type;

import org.bukkit.OfflinePlayer;

public class PlayerTempBan extends PlayerPunishment {

    private long validUntil;

    public PlayerTempBan(OfflinePlayer player, String reason) {
        super(player, reason);
    }
    public PlayerTempBan(String uuidString, String reason) {
        super(uuidString, reason);
    }

    public long getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(long validUntil) {
        this.validUntil = validUntil;
    }

    public boolean isStillValid() {
        return validUntil > System.currentTimeMillis();
    }

}
