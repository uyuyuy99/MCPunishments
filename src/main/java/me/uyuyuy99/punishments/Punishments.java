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
import me.uyuyuy99.punishments.cmd.*;
import me.uyuyuy99.punishments.db.Database;
import me.uyuyuy99.punishments.db.MongoDatabase;
import me.uyuyuy99.punishments.db.MysqlDatabase;
import me.uyuyuy99.punishments.db.SqliteDatabase;
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
            database = new SqliteDatabase();
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
        new CmdBan().register();
        new CmdBanIp().register();
        new CmdTempBan().register();
        new CmdUnban().register();
        new CmdUnbanIp().register();
        new CmdMute().register();
        new CmdTempMute().register();
        new CmdUnmute().register();
        new CmdKick().register();
        new CmdHistory().register();
    }

    public static Punishments plugin() {
        return plugin;
    }

}
