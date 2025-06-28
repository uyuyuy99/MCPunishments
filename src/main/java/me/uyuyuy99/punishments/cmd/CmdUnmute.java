package me.uyuyuy99.punishments.cmd;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import me.uyuyuy99.punishments.util.Config;
import org.bukkit.OfflinePlayer;

public class CmdUnmute extends Cmd {

    @Override
    public void register() {
        new CommandAPICommand("unmute")
                .withPermission("punishments.admin.unmute")
                .withFullDescription("Unmute a player")
                .withArguments(
                        new OfflinePlayerArgument("player")
                )
                .executes((sender, args) -> {
                    OfflinePlayer player = (OfflinePlayer) args.get("player");

                    if (manager.unmutePlayer(player)) {
                        Config.sendMsg("admin.unmute", sender, "player", args.getRaw("player"));
                    } else {
                        Config.sendMsg("admin.wasnt-muted", sender, "player", args.getRaw("player"));
                    }
                })
                .register();
    }

}
