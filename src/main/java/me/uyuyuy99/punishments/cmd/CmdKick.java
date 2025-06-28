package me.uyuyuy99.punishments.cmd;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import me.uyuyuy99.punishments.util.Config;
import org.bukkit.entity.Player;

public class CmdKick extends Cmd {

    @Override
    public void register() {
        CommandAPI.unregister("kick");
        new CommandAPICommand("kick")
                .withPermission("punishments.admin.kick")
                .withFullDescription("Kick a player")
                .withArguments(
                        new PlayerArgument("player")
                )
                .withOptionalArguments(
                        new GreedyStringArgument("reason")
                )
                .executes((sender, args) -> {
                    Player player = (Player) args.get("player");
                    String reason = (String) args.getOptional("reason").orElse(Config.getMsg("default-reason"));
                    if (checkReasonTooLong(sender, reason)) return;

                    player.kickPlayer(Config.getMsg("user.kicked", "reason", reason));
                    database.addKick(player, reason);
                    Config.sendMsg("admin.kick", sender, "player", args.getRaw("player"), "reason", reason);
                })
                .register();
    }

}
