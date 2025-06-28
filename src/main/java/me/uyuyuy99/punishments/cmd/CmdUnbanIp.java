package me.uyuyuy99.punishments.cmd;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import me.uyuyuy99.punishments.util.Config;

public class CmdUnbanIp extends Cmd {

    @Override
    public void register() {
        CommandAPI.unregister("pardon-ip");
        new CommandAPICommand("unbanip")
                .withAliases("pardon-ip")
                .withPermission("punishments.admin.unbanip")
                .withFullDescription("Unban an IP address")
                .withArguments(
                        new StringArgument("ip")
                )
                .executes((sender, args) -> {
                    String ip = (String) args.get("ip");

                    if (manager.unbanIp(ip)) {
                        Config.sendMsg("admin.unban-ip", sender, "ip", ip);
                    } else {
                        Config.sendMsg("admin.wasnt-ip-banned", sender, "ip", ip);
                    }
                })
                .register();
    }

}
