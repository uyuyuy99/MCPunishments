package me.uyuyuy99.punishments.cmd;

import de.themoep.inventorygui.InventoryGui;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import me.uyuyuy99.punishments.history.HistoryGui;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class CmdHistory extends Cmd {

    @Override
    public void register() {
        new CommandAPICommand("history")
                .withPermission("punishments.admin.history")
                .withFullDescription("View a player's punishment history")
                .withArguments(
                        new OfflinePlayerArgument("player")
                )
                .executesPlayer((sender, args) -> {
                    OfflinePlayer player = (OfflinePlayer) args.get("player");
                    database.getPlayerHistory(player).thenAccept((history) -> {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                            InventoryGui gui = new HistoryGui(sender, player, args.getRaw("player"), history);
                            gui.show(sender);
                        }, 0L);
                    }).exceptionally((e) -> {
                        e.printStackTrace();
                        return null;
                    });
                })
                .register();
    }

}
