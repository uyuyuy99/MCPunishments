package me.uyuyuy99.punishments;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Config {

    public static String getMsg(String key, Object... args) {
        String msg = Punishments.plugin().getConfig().getString("messages." + key);

        for (int i = 0; i < args.length; i += 2) {
            msg = msg.replace("{" + args[i] + "}", args[i + 1].toString());
        }

        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static void sendMsg(String key, CommandSender recipient, Object... args) {
        recipient.sendMessage(getMsg(key, args));
    }

}
