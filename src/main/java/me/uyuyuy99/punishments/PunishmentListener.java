package me.uyuyuy99.punishments;

import me.uyuyuy99.punishments.type.PlayerBan;
import me.uyuyuy99.punishments.type.PlayerMute;
import me.uyuyuy99.punishments.type.PlayerTempBan;
import me.uyuyuy99.punishments.type.PlayerTempMute;
import me.uyuyuy99.punishments.util.Config;
import me.uyuyuy99.punishments.util.TimeUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class PunishmentListener implements Listener {

    private PunishmentManager manager;

    public PunishmentListener() {
        this.manager = Punishments.plugin().getManager();
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        if (manager.isBanned(player)) {
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);

            PlayerBan ban = manager.getPlayerBans().get(player.getUniqueId());
            if (ban != null) {
                event.setKickMessage(Config.getMsg("user.banned", "reason", ban.getReason()));
            } else {
                PlayerTempBan tempBan = manager.getPlayerTempBans().get(player.getUniqueId());
                event.setKickMessage(Config.getMsg("user.temp-banned",
                        "time", TimeUtil.formatTimeAbbr(tempBan.getValidFor() / 1000),
                        "reason", tempBan.getReason()));
            }
        }
        else if (manager.isIpBanned(event.getAddress().getHostAddress())) {
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (manager.isMuted(player)) {
            event.setCancelled(true);

            PlayerMute mute = manager.getPlayerMutes().get(player.getUniqueId());
            if (mute != null) {
                Config.sendMsg("user.muted", player, "reason", mute.getReason());
            } else {
                PlayerTempMute tempMute = manager.getPlayerTempMutes().get(player.getUniqueId());
                Config.sendMsg("user.temp-muted", player,
                        "time", TimeUtil.formatTimeAbbr(tempMute.getValidFor() / 1000),
                        "reason", tempMute.getReason());
            }
        }
    }

}
