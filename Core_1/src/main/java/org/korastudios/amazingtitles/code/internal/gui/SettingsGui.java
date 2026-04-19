package org.korastudios.amazingtitles.code.internal.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.korastudios.amazingtitles.code.internal.Booter;

public class SettingsGui extends BaseGui {

    public SettingsGui(Player player, BaseGui parent) {
        super(player, parent);
    }

    @Override
    protected Inventory buildInventory() {
        Inventory inv = Bukkit.createInventory(null, 27, color("&5AmazingTitles &8\u00BB &fSettings"));
        for (int i = 0; i < 27; i++) inv.setItem(i, createFiller());

        boolean updateNotifier = Booter.getCustomConfiguration().getShortcutOptions().getUpdateNotifier();
        inv.setItem(11, createToggle(updateNotifier, "&eUpdate Notifier",
            "&7Admins are notified of new releases on join",
            "&7Update notifications are disabled"));

        inv.setItem(15, createItem(Material.NETHER_STAR, "&a&lReload Plugin",
            "&7Reload the plugin to apply any file changes",
            "&eClick to reload"));

        inv.setItem(18, createBack());
        inv.setItem(26, createClose());
        return inv;
    }

    @Override
    public void handleClick(int slot, ClickType clickType, ItemStack item) {
        switch (slot) {
            case 11: {
                boolean current = Booter.getCustomConfiguration().getShortcutOptions().getUpdateNotifier();
                Booter.getCustomConfiguration().save("UpdateNotifier", current ? 0 : 1);
                open();
                break;
            }
            case 15: {
                player.closeInventory();
                Booter.getBooter().reload(player);
                break;
            }
            case 18: goBack(); break;
            case 26: player.closeInventory(); break;
        }
    }
}
