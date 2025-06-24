package me.uyuyuy99.punishments;

import me.uyuyuy99.punishments.db.Database;
import me.uyuyuy99.punishments.type.PlayerTempBan;
import me.uyuyuy99.punishments.type.PlayerTempMute;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TempRemovalTask extends BukkitRunnable {

    private PunishmentManager manager;
    private Database db;

    public TempRemovalTask() {
        this.manager = Punishments.plugin().getManager();
        this.db = Punishments.plugin().getDatabase();
    }

    @Override
    public void run() {
        Iterator<Map.Entry<UUID, PlayerTempBan>> banIter = manager.getPlayerTempBans().entrySet().iterator();
        while (banIter.hasNext()) {
            PlayerTempBan ban = banIter.next().getValue();

            if (!ban.isStillValid()) {
                banIter.remove();
                db.removePlayerPunishment(ban);
            }
        }

        Iterator<Map.Entry<UUID, PlayerTempMute>> muteIter = manager.getPlayerTempMutes().entrySet().iterator();
        while (muteIter.hasNext()) {
            PlayerTempMute mute = muteIter.next().getValue();

            if (!mute.isStillValid()) {
                muteIter.remove();
                db.removePlayerPunishment(mute);
            }
        }
    }

}
