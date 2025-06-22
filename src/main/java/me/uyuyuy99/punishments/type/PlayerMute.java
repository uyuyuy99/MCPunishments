package me.uyuyuy99.punishments.type;

import org.bukkit.OfflinePlayer;

public class PlayerMute extends PlayerPunishment {

    public PlayerMute(OfflinePlayer player, String reason) {
        super(player, reason);
    }
    public PlayerMute(String uuidString, String reason) {
        super(uuidString, reason);
    }
    
}
