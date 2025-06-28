package me.uyuyuy99.punishments.cmd;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import me.uyuyuy99.punishments.util.Config;
import me.uyuyuy99.punishments.util.TimeUtil;
import org.bukkit.OfflinePlayer;

public class CmdTempMute extends Cmd {

    @Override
    public void register() {
        new CommandAPICommand("tempmute")
                .withPermission("punishments.admin.tempmute")
                .withFullDescription("Temporarily mute a player")
                .withArguments(
                        new OfflinePlayerArgument("player"),
                        TimeUtil.arg("time")
                )
                .withOptionalArguments(
                        new GreedyStringArgument("reason")
                )
                .executes((sender, args) -> {
                    OfflinePlayer player = (OfflinePlayer) args.get("player");
                    int time = ((int) args.get("time"));
                    String reason = (String) args.getOptional("reason").orElse(Config.getMsg("default-reason"));
                    if (checkReasonTooLong(sender, reason)) return;

                    if (manager.tempMutePlayer(player, reason, time)) {
                        Config.sendMsg("admin.temp-mute", sender,
                                "player", args.getRaw("player"),
                                "time", TimeUtil.formatTimeAbbr(time));
                    } else {
                        Config.sendMsg("admin.already-muted", sender, "player", args.getRaw("player"));
                    }
                })
                .register();
    }

}
