package me.uyuyuy99.punishments.history;

import de.themoep.inventorygui.*;
import me.uyuyuy99.punishments.PunishmentManager;
import me.uyuyuy99.punishments.Punishments;
import me.uyuyuy99.punishments.type.PlayerTempBan;
import me.uyuyuy99.punishments.type.PlayerTempMute;
import me.uyuyuy99.punishments.util.CC;
import me.uyuyuy99.punishments.util.Config;
import me.uyuyuy99.punishments.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class HistoryGui extends InventoryGui {

    private Player viewer;
    private String playerName;
    private int curPage = 0;
    private int maxPages;
    private GuiStateElement prevPageElement;
    private GuiStateElement nextPageElement;

    public HistoryGui(Player viewer, OfflinePlayer player, String playerName, List<HistoryRecord> history) {
        super(
                Punishments.plugin(),
                viewer,
                " ",
                buildGui()
        );
        this.viewer = viewer;
        this.playerName = playerName;
        PunishmentManager manager = Punishments.plugin().getManager();

        setFiller(Config.getIcon("history-gui.filler-icon"));

        GuiElementGroup group = new GuiElementGroup('b');
        for (HistoryRecord record : history) {
            String[] lore = Config.getStringArray("history-gui." + record.getType().getId() + "-text",
                    "start", TimeUtil.formatDate(record.getStart()),
                    "end", TimeUtil.formatDate(record.getEnd(), "Permanent"),
                    "reason", record.getReason());
            group.addElement(new StaticGuiElement('c',
                    Config.getIcon("history-gui." + record.getType().getId() + "-icon"),
                    lore));
        }
        addElement(group);
        this.maxPages = Math.max(1, ((group.size() - 1) / (Punishments.plugin().getConfig().getInt("history-gui.rows-per-page") * 9)) + 1);

        final String banString;
        if (manager.isBanned(player)) {
            if (manager.getPlayerBans().containsKey(player.getUniqueId())) {
                banString = "Permanently";
            } else {
                PlayerTempBan tempBan = manager.getPlayerTempBans().get(player.getUniqueId());
                banString = "Until " + TimeUtil.formatDate(tempBan.getValidUntil());
            }
        } else {
            banString = "No";
        }

        String muteString;
        if (manager.isMuted(player)) {
            if (manager.getPlayerMutes().containsKey(player.getUniqueId())) {
                muteString = "Permanently";
            } else {
                PlayerTempMute tempMute = manager.getPlayerTempMutes().get(player.getUniqueId());
                muteString = "Until " + TimeUtil.formatDate(tempMute.getValidUntil());
            }
        } else {
            muteString = "No";
        }

        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwnerProfile(player.getPlayerProfile());
        head.setItemMeta(skullMeta);
        addElement(new StaticGuiElement('a', head, Config.getStringArray("history-gui.player-text",
                "player", playerName,
                "banned", banString,
                "muted", muteString)));

//        addElement(new GuiPageElement('p',
//                Config.getIcon("history-gui.previous-page-icon"),
//                GuiPageElement.PageAction.PREVIOUS,
//                Config.getStringArray("history-gui.previous-page-text")));
//        addElement(new GuiPageElement('n',
//                Config.getIcon("history-gui.next-page-icon"),
//                GuiPageElement.PageAction.NEXT,
//                Config.getStringArray("history-gui.next-page-text")));

//        addElement(new StaticGuiElement('p', Config.getIcon("history-gui.previous-page-icon"), 1, click -> {
//            setPageNumber(curPage = Math.max(0, curPage - 1));
//            return true;
//        }, Config.getStringArray("history-gui.previous-page-text")));
//
//        addElement(new StaticGuiElement('n', Config.getIcon("history-gui.next-page-icon"), 1, click -> {
//            setPageNumber(curPage = Math.min(maxPages, curPage + 1));
//            return true;
//        }, Config.getStringArray("history-gui.next-page-text")));

        prevPageElement = new GuiStateElement('p',
                () -> curPage == 0 ? "off" : "on",
                new GuiStateElement.State(
                        change -> {},
                        "on",
                        Config.getIcon("history-gui.previous-page-icon"),
                        Config.getStringArray("history-gui.previous-page-text")
                ),
                new GuiStateElement.State(
                        change -> {},
                        "off",
                        Config.getIcon("history-gui.filler-icon"),
                        " "
                )
        );
        nextPageElement = new GuiStateElement('n',
                () -> curPage + 1 >= maxPages ? "off" : "on",
                new GuiStateElement.State(
                        change -> {},
                        "on",
                        Config.getIcon("history-gui.next-page-icon"),
                        Config.getStringArray("history-gui.next-page-text")
                ),
                new GuiStateElement.State(
                        change -> {},
                        "off",
                        Config.getIcon("history-gui.filler-icon"),
                        " "
                )
        );
        addElement(prevPageElement);
        addElement(nextPageElement);
        prevPageElement.setAction(click -> {
            if (prevPageElement.getState().getKey().equals("off")) return true;
            setPageNumber(curPage = Math.max(0, curPage - 1));
            updateTitle();
            show(viewer);
            return true;
        });
        nextPageElement.setAction(click -> {
            if (nextPageElement.getState().getKey().equals("off")) return true;
            setPageNumber(curPage = Math.min(maxPages, curPage + 1));
            updateTitle();
            show(viewer);
            return true;
        });
    }

    private void updateTitle() {
        setTitle(CC.translate(Config.getString("history-gui.title",
                "player", playerName,
                "page", curPage + 1,
                "maxpages", maxPages
        )));
    }

    private static String[] buildGui() {
        int rows = Math.min(6, Math.max(2, Punishments.plugin().getConfig().getInt("history-gui.rows-per-page") + 1));
        String[] guiSetup = new String[rows];
        guiSetup[0] = "p   a   n";
        for (int i = 1; i < rows; i++) {
            guiSetup[i] = "bbbbbbbbb";
        }
        return guiSetup;
    }

    @Override
    public void show(HumanEntity player, boolean checkOpen) {
        updateTitle();
        super.show(player, checkOpen);
    }

    @Override
    public void draw(HumanEntity who, boolean updateDynamic, boolean recreateInventory) {
        updateTitle();
        Bukkit.getLogger().info("TEST");
        super.draw(who, updateDynamic, recreateInventory);
    }

}
