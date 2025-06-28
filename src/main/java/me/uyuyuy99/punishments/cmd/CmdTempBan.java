package me.uyuyuy99.punishments.cmd;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import me.uyuyuy99.punishments.util.Config;
import me.uyuyuy99.punishments.util.TimeUtil;
import org.bukkit.OfflinePlayer;

public class CmdTempBan extends Cmd {

    @Override
    public void register() {
        new CommandAPICommand("tempban")
                .withPermission("punishments.admin.tempban")
                .withFullDescription("Temporarily ban a player")
                .withArguments(
                        new OfflinePlayerArgument("player"),
                        TimeUtil.arg("time")
                )
                .withOptionalArguments(
                        new GreedyStringArgument("reason")
                )
                .executes((sender, args) -> {
                    OfflinePlayer player = (OfflinePlayer) args.get("player");
                    int secs = ((int) args.get("time"));
                    String reason = (String) args.getOptional("reason").orElse(Config.getMsg("default-reason"));
                    if (checkReasonTooLong(sender, reason)) return;

                    if (manager.tempBanPlayer(player, reason, secs)) {
                        Config.sendMsg("admin.temp-ban", sender,
                                "player", args.getRaw("player"),
                                "time", TimeUtil.formatTimeAbbr(secs));
                    } else {
                        Config.sendMsg("admin.already-banned", sender, "player", args.getRaw("player"));
                    }
                })
                .register();
    }

}
