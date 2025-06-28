package me.uyuyuy99.punishments.cmd;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.uyuyuy99.punishments.util.Config;

public class CmdBanIp extends Cmd {

    @Override
    public void register() {
        CommandAPI.unregister("ban-ip");
        new CommandAPICommand("banip")
                .withAliases("ban-ip")
                .withPermission("punishments.admin.banip")
                .withFullDescription("Ban an IP address")
                .withArguments(
                        new StringArgument("ip")
                )
                .withOptionalArguments(
                        new GreedyStringArgument("reason")
                )
                .executes((sender, args) -> {
                    String ip = (String) args.get("ip");
                    String reason = (String) args.getOptional("reason").orElse(Config.getMsg("default-reason"));
                    if (checkReasonTooLong(sender, reason)) return;

                    if (manager.banIp(ip, reason)) {
                        Config.sendMsg("admin.ip-ban", sender, "ip", ip);
                    } else {
                        Config.sendMsg("admin.already-ip-banned", sender, "ip", ip);
                    }
                })
                .register();
    }

}
