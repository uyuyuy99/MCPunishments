package me.uyuyuy99.punishments.type;

import org.bukkit.OfflinePlayer;

public class PlayerBan extends PlayerPunishment {

    public PlayerBan(int id, OfflinePlayer player, String reason) {
        super(id, player, reason);
    }
    public PlayerBan(int id, String uuidString, String reason) {
        super(id, uuidString, reason);
    }

}
