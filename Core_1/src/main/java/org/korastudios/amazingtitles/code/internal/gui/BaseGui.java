package org.korastudios.amazingtitles.code.internal.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class BaseGui {
    protected final Player player;
    protected Inventory inventory;
    protected final BaseGui parent;

    protected BaseGui(Player player, BaseGui parent) {
        this.player = player;
        this.parent = parent;
    }

    protected abstract Inventory buildInventory();

    public void open() {
        inventory = buildInventory();
        player.openInventory(inventory);
        GuiManager.getInstance().setOpenGui(player, this);
    }

    public abstract void handleClick(int slot, ClickType clickType, ItemStack item);

    protected ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(color(name));
        if (lore.length > 0) {
            meta.setLore(Arrays.stream(lore).map(this::color).collect(Collectors.toList()));
        }
        item.setItemMeta(meta);
        return item;
    }

    protected ItemStack createToggle(boolean state, String name, String enabledLore, String disabledLore) {
        Material mat = state ? Material.LIME_DYE : Material.GRAY_DYE;
        String status = state ? "&a&lENABLED" : "&8&lDISABLED";
        return createItem(mat, name, "&7Status: " + status, state ? enabledLore : disabledLore, "&7Click to toggle");
    }

    protected ItemStack createFiller() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }

    protected ItemStack createBack() {
        return createItem(Material.ARROW, "&7\u00AB &fBack");
    }

    protected ItemStack createClose() {
        return createItem(Material.BARRIER, "&c&lClose");
    }

    protected String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    protected void goBack() {
        if (parent != null) parent.open();
        else player.closeInventory();
    }

    protected String truncate(String s, int max) {
        if (s == null || s.isEmpty()) return "&onone";
        return s.length() > max ? s.substring(0, max) + "..." : s;
    }
}
