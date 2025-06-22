package me.uyuyuy99.punishments;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Punishments extends JavaPlugin {

    private static Punishments plugin;
    private PunishmentManager manager;
    private Database database;

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
    }

    @Override
    public void onEnable() {
        plugin = this;
        registerCommands();
    }

    @Override
    public void onDisable() {
    }

    private void registerCommands() {
        CommandAPI.unregister("ban");
        new CommandAPICommand("ban")
                .withPermission("punishments.admin.ban")
                .withArguments(
                        new OfflinePlayerArgument("player")
                )
                .withOptionalArguments(
                        new GreedyStringArgument("reason")
                )
                .executes((sender, args) -> {
                    OfflinePlayer player = (OfflinePlayer) args.get("player");
                    Bukkit.getLogger().info("TEST: " + player.getName() + " - " + args.get("reason"));
                    //TODO
                })
                .register();

        CommandAPI.unregister("ipban");
        new CommandAPICommand("ban")
                .withPermission("punishments.admin.ban")
                .withArguments(
                        new StringArgument("ip")
                )
                .withOptionalArguments(
                        new GreedyStringArgument("reason")
                )
                .executes((sender, args) -> {
                    //TODO
                })
                .register();

        new CommandAPICommand("tempban")
                .withPermission("punishments.admin.tempban")
                .withArguments(
                        new OfflinePlayerArgument("player"),
                        new TimeArgument("time")
                )
                .withOptionalArguments(
                        new GreedyStringArgument("reason")
                )
                .executes((sender, args) -> {
                    //TODO
                })
                .register();

        CommandAPI.unregister("pardon");
        new CommandAPICommand("unban")
                .withAliases("pardon")
                .withPermission("punishments.admin.unban")
                .withArguments(
                        new OfflinePlayerArgument("player")
                )
                .withOptionalArguments(
                        new GreedyStringArgument("reason")
                )
                .executes((sender, args) -> {
                    OfflinePlayer player = (OfflinePlayer) args.get("player");
                    //TODO
                })
                .register();

        new CommandAPICommand("mute")
                .withPermission("punishments.admin.mute")
                .withArguments(
                        new OfflinePlayerArgument("player")
                )
                .withOptionalArguments(
                        new GreedyStringArgument("reason")
                )
                .executes((sender, args) -> {
                    //TODO
                })
                .register();

        new CommandAPICommand("tempmute")
                .withPermission("punishments.admin.tempmute")
                .withArguments(
                        new OfflinePlayerArgument("player"),
                        new TimeArgument("time")
                )
                .withOptionalArguments(
                        new GreedyStringArgument("reason")
                )
                .executes((sender, args) -> {
                    //TODO
                })
                .register();

        new CommandAPICommand("unmute")
                .withPermission("punishments.admin.unmute")
                .withArguments(
                        new OfflinePlayerArgument("player")
                )
                .executes((sender, args) -> {
                    //TODO
                })
                .register();

        CommandAPI.unregister("kick");
        new CommandAPICommand("kick")
                .withPermission("punishments.admin.kick")
                .withArguments(
                        new PlayerArgument("player")
                )
                .withOptionalArguments(
                        new GreedyStringArgument("reason")
                )
                .executes((sender, args) -> {
                    Bukkit.getLogger().info("TEST: ");
                    //TODO
                })
                .register();
    }

    public static Punishments plugin() {
        return plugin;
    }

    public PunishmentManager getManager() {
        return manager;
    }

    public Database getDatabase() {
        return database;
    }

}
