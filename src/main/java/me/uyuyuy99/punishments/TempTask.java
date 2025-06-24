package me.uyuyuy99.punishments;

import me.uyuyuy99.punishments.type.PlayerTempBan;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class TempTask extends BukkitRunnable {

    private PunishmentManager manager;

    public TempTask() {
        this.manager = Punishments.plugin().getManager();
    }

    @Override
    public void run() {
        manager.getPlayerTempBans().entrySet().removeIf(entry -> !entry.getValue().isStillValid());
        manager.getPlayerTempMutes().entrySet().removeIf(entry -> !entry.getValue().isStillValid());
        //TODO remove from sql
    }

}
