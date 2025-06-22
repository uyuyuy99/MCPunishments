package me.uyuyuy99.punishments.type;

import org.bukkit.OfflinePlayer;

public class PlayerBan extends PlayerPunishment {

    public PlayerBan(OfflinePlayer player, String reason) {
        super(player, reason);
    }
    public PlayerBan(String uuidString, String reason) {
        super(uuidString, reason);
    }

}
