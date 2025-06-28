package me.uyuyuy99.punishments.cmd;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import me.uyuyuy99.punishments.util.Config;
import org.bukkit.OfflinePlayer;

public class CmdUnban extends Cmd {

    @Override
    public void register() {
        CommandAPI.unregister("pardon");
        new CommandAPICommand("unban")
                .withAliases("pardon")
                .withPermission("punishments.admin.unban")
                .withFullDescription("Unban a player")
                .withArguments(
                        new OfflinePlayerArgument("player")
                )
                .executes((sender, args) -> {
                    OfflinePlayer player = (OfflinePlayer) args.get("player");

                    if (manager.unbanPlayer(player)) {
                        Config.sendMsg("admin.unban", sender, "player", args.getRaw("player"));
                    } else {
                        Config.sendMsg("admin.wasnt-banned", sender, "player", args.getRaw("player"));
                    }
                })
                .register();
    }

}
