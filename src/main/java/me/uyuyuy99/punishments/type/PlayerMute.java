package me.uyuyuy99.punishments.type;

import org.bukkit.OfflinePlayer;

public class PlayerMute extends PlayerPunishment {

    public PlayerMute(int id, OfflinePlayer player, String reason) {
        super(id, player, reason);
    }
    public PlayerMute(int id, String uuidString, String reason) {
        super(id, uuidString, reason);
    }
    
}
