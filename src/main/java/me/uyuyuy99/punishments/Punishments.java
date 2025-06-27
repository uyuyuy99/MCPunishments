package me.uyuyuy99.punishments;

import de.themoep.inventorygui.InventoryGui;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import lombok.Getter;
import me.uyuyuy99.punishments.db.Database;
import me.uyuyuy99.punishments.db.MongoDatabase;
import me.uyuyuy99.punishments.db.MysqlDatabase;
import me.uyuyuy99.punishments.history.HistoryGui;
import me.uyuyuy99.punishments.util.Config;
import me.uyuyuy99.punishments.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
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
        saveDefaultConfig();

        manager = new PunishmentManager();
        
        String storageType = getConfig().getString("storage-type", "").toLowerCase();
        if (storageType.equals("mysql")) {
            database = new MysqlDatabase(
                    getConfig().getString("mysql.host"),
                    getConfig().getInt("mysql.port"),
                    getConfig().getString("mysql.database"),
                    getConfig().getString("mysql.user"),
                    getConfig().getString("mysql.password")
            );
        } else if (storageType.equals("sqlite")) {
            //TODO
        } else if (storageType.equals("mongodb")) {
            database = new MongoDatabase(
                    getConfig().getString("mongodb.host"),
                    getConfig().getInt("mongodb.port"),
                    getConfig().getString("mongodb.database"),
                    getConfig().getString("mongodb.user"),
                    getConfig().getString("mongodb.password")
            );
        } else {
            getLogger().info("Incorrect storage-type found in config.yml. Please set it to either: mysql, sqlite, or mongodb");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        manager.setDb(database);

        try {
            database.connect();
        } catch (Exception e) {
            getLogger().severe("Could not connect to database! Take a look at your config.yml and make sure your details are correct.");
            getServer().getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
        }
        try {
            database.loadValidPunishments();
        } catch (Exception e) {
            getLogger().severe("Could not load punishments from database!");
            getServer().getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
        }

        registerCommands();
        registerTasks();
        registerListeners();
    }

    @Override
    public void onDisable() {
        database.disconnect();
    }

    private void registerTasks() {
        new TempRemovalTask().runTaskTimer(this, 20L, 20L);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PunishmentListener(), this);
    }

    private void registerCommands() {
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

        CommandAPI.unregister("ban-ip");
        new CommandAPICommand("banip")
                .withAliases("ban-ip")
                .withPermission("punishments.admin.ban")
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

        CommandAPI.unregister("pardon-ip");
        new CommandAPICommand("unbanip")
                .withAliases("pardon-ip")
                .withPermission("punishments.admin.unban")
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

        new CommandAPICommand("tempmute")
                .withPermission("punishments.admin.tempmute")
                .withFullDescription("Temporarily mute a player")
                .withArguments(
                        new OfflinePlayerArgument("player"),
                        TimeUtil.arg("time")
                )
                .withOptionalArguments(
                        new GreedyStringArgument("reason")
                )
                .executes((sender, args) -> {
                    OfflinePlayer player = (OfflinePlayer) args.get("player");
                    int time = ((int) args.get("time"));
                    String reason = (String) args.getOptional("reason").orElse(Config.getMsg("default-reason"));
                    if (checkReasonTooLong(sender, reason)) return;

                    if (manager.tempMutePlayer(player, reason, time)) {
                        Config.sendMsg("admin.temp-mute", sender,
                                "player", player.getName(),
                                "time", TimeUtil.formatTimeAbbr(time));
                    } else {
                        Config.sendMsg("admin.already-muted", sender, "player", args.getRaw("player"));
                    }
                })
                .register();

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

        new CommandAPICommand("history")
                .withPermission("punishments.admin.history")
                .withFullDescription("View a player's punishment history")
                .withArguments(
                        new OfflinePlayerArgument("player")
                )
                .executesPlayer((sender, args) -> {
                    OfflinePlayer player = (OfflinePlayer) args.get("player");
                    database.getPlayerHistory(player).thenAccept((history) -> {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Punishments.this, () -> {
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

    // Returns TRUE if ban/mute/kick reason is too long
    private boolean checkReasonTooLong(CommandSender sender, String reason) {
        int max = getConfig().getInt("misc.max-reason-length", 100);
        if (reason.length() > max) {
            sender.sendMessage(ChatColor.RED + "ERROR: Reason can't be longer than " + max + " characters.");
            return true;
        }
        return false;
    }

    public static Punishments plugin() {
        return plugin;
    }

}
