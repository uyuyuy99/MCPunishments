package me.uyuyuy99.punishments.cmd;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import me.uyuyuy99.punishments.util.Config;
import org.bukkit.OfflinePlayer;

public class CmdMute extends Cmd {

    @Override
    public void register() {
        new CommandAPICommand("mute")
                .withPermission("punishments.admin.mute")
                .withFullDescription("Permanently mute a player")
                .withArguments(
                        new OfflinePlayerArgument("player")
                )
                .withOptionalArguments(
                        new GreedyStringArgument("reason")
                )
                .executes((sender, args) -> {
                    OfflinePlayer player = (OfflinePlayer) args.get("player");
                    String reason = (String) args.getOptional("reason").orElse(Config.getMsg("default-reason"));
                    if (checkReasonTooLong(sender, reason)) return;

                    if (manager.mutePlayer(player, reason)) {
                        Config.sendMsg("admin.mute", sender, "player", args.getRaw("player"));
                    } else {
                        Config.sendMsg("admin.already-muted", sender, "player", args.getRaw("player"));
                    }
                })
                .register();
    }

}
