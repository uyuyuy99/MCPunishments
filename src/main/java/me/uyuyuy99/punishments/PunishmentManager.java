package me.uyuyuy99.punishments;

import me.uyuyuy99.punishments.type.*;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PunishmentManager {

    private Map<UUID, PlayerBan> playerBans = new HashMap<>();
    private Map<UUID, PlayerTempBan> playerTempBans = new HashMap<>();
    private Map<UUID, PlayerMute> playerMutes = new HashMap<>();
    private Map<UUID, PlayerTempMute> playerTempMutes = new HashMap<>();
    private Map<String, IpBan> ipBans = new HashMap<>();

    public PunishmentManager() {

    }

}
