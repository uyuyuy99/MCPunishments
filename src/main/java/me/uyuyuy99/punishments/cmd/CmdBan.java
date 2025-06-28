package me.uyuyuy99.punishments.cmd;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import me.uyuyuy99.punishments.util.Config;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class CmdBan extends Cmd {

    @Override
    public void register() {
        CommandAPI.unregister("ban");
        new CommandAPICommand("ban")
                .withPermission("punishments.admin.ban")
                .withFullDescription("Permanently ban a player")
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

                    if (manager.banPlayer(player, reason)) {
                        Config.sendMsg("admin.ban", sender, "player", args.getRaw("player"));
                    } else {
                        Config.sendMsg("admin.already-banned", sender, "player", args.getRaw("player"));
                    }
                })
                .register();
    }

}
