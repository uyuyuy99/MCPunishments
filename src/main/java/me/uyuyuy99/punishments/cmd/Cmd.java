package me.uyuyuy99.punishments.cmd;

import me.uyuyuy99.punishments.PunishmentManager;
import me.uyuyuy99.punishments.Punishments;
import me.uyuyuy99.punishments.db.Database;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class Cmd {

    protected Punishments plugin;
    protected PunishmentManager manager;
    protected Database database;

    public Cmd() {
        this.plugin = Punishments.plugin();
        this.manager = plugin.getManager();
        this.database = plugin.getDatabase();
    }

    public abstract void register();

    // Returns TRUE if ban/mute/kick reason is too long
    protected boolean checkReasonTooLong(CommandSender sender, String reason) {
        int max = plugin.getConfig().getInt("misc.max-reason-length", 100);
        if (reason.length() > max) {
            sender.sendMessage(ChatColor.RED + "ERROR: Reason can't be longer than " + max + " characters.");
            return true;
        }
        return false;
    }

}
