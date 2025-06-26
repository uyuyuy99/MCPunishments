package me.uyuyuy99.punishments.util;

import me.uyuyuy99.punishments.Punishments;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.ListIterator;

public class Config {

    public static String getString(String key, Object... args) {
        String msg = Punishments.plugin().getConfig().getString(key);

        for (int i = 0; i < args.length; i += 2) {
            msg = msg.replace("{" + args[i] + "}", args[i + 1].toString());
        }

        return CC.translate(msg);
    }

    public static List<String> getStringList(String key, Object... args) {
        List<String> list = Punishments.plugin().getConfig().getStringList(key);

        ListIterator<String> iter = list.listIterator();
        while (iter.hasNext()) {
            String msg = iter.next();
            for (int i = 0; i < args.length; i += 2) {
                msg = CC.translate(msg.replace("{" + args[i] + "}", args[i + 1].toString()));
            }
            iter.set(msg);
        }

        return list;
    }

    public static String[] getStringArray(String key, Object... args) {
        return getStringList(key, args).toArray(new String[]{});
    }

    public static String getMsg(String key, Object... args) {
        return getString("messages." + key, args);
    }

    public static void sendMsg(String key, CommandSender recipient, Object... args) {
        recipient.sendMessage(getMsg(key, args));
    }

    public static ItemStack getIcon(String key) {
        return new ItemStack(Material.valueOf(Punishments.plugin().getConfig().getString(key).toUpperCase()), 1);
    }

}
