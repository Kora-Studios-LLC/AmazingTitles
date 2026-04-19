package org.korastudios.amazingtitles.code.internal.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MainMenuGui extends BaseGui {

    public MainMenuGui(Player player) {
        super(player, null);
    }

    @Override
    protected Inventory buildInventory() {
        Inventory inv = Bukkit.createInventory(null, 27, color("&5AmazingTitles &8\u00BB &fMain Menu"));
        for (int i = 0; i < 27; i++) inv.setItem(i, createFiller());

        inv.setItem(10, createItem(Material.COMPARATOR, "&d&lSettings",
            "&7Manage general plugin settings",
            "&7e.g. Update Notifier, reload"));
        inv.setItem(12, createItem(Material.BEACON, "&b&lSmartBar",
            "&7Configure SmartBar & static animation",
            "&7Controls actionbar behaviour"));
        inv.setItem(14, createItem(Material.CHEST, "&6&lExtensions",
            "&7Manage loaded extensions",
            "&7Reload or unload extension packs"));
        inv.setItem(16, createItem(Material.FIREWORK_ROCKET, "&a&lSend Animation",
            "&7Send an animation to players",
            "&7Admin tool for live testing"));

        inv.setItem(22, createClose());
        return inv;
    }

    @Override
    public void handleClick(int slot, ClickType clickType, ItemStack item) {
        switch (slot) {
            case 10: new SettingsGui(player, this).open(); break;
            case 12: new SmartBarGui(player, this).open(); break;
            case 14: new ExtensionsGui(player, this).open(); break;
            case 16: new SendAnimationGui(player, this).open(); break;
            case 22: player.closeInventory(); break;
        }
    }
}
