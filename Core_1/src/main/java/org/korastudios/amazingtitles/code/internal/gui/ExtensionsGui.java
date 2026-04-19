package org.korastudios.amazingtitles.code.internal.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.korastudios.amazingtitles.code.api.AmazingTitles;
import org.korastudios.amazingtitles.code.api.interfaces.AmazingExtension;
import org.korastudios.amazingtitles.code.internal.Booter;
import org.korastudios.amazingtitles.code.internal.loaders.PluginLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExtensionsGui extends BaseGui {

    private static final int CONTENT_SIZE = 45;

    private int page = 0;
    private List<String> names;

    public ExtensionsGui(Player player, BaseGui parent) {
        super(player, parent);
    }

    @Override
    protected Inventory buildInventory() {
        names = new ArrayList<>(AmazingTitles.getLoadedExtensionNames());
        int totalPages = Math.max(1, (int) Math.ceil(names.size() / (double) CONTENT_SIZE));

        Inventory inv = Bukkit.createInventory(null, 54,
            color("&5AmazingTitles &8\u00BB &fExtensions &7(" + (page + 1) + "/" + totalPages + ")"));

        for (int i = 0; i < 45; i++) inv.setItem(i, null);
        for (int i = 45; i < 54; i++) inv.setItem(i, createFiller());

        if (names.isEmpty()) {
            inv.setItem(22, createItem(Material.BARRIER, "&c&lNo Extensions Loaded",
                "&7Drop extension JARs into:",
                "&7plugins/AmazingTitles/Extensions/"));
        } else {
            int start = page * CONTENT_SIZE;
            for (int i = 0; i < CONTENT_SIZE && start + i < names.size(); i++) {
                inv.setItem(i, createItem(Material.CHEST, "&6" + names.get(start + i),
                    "&7Status: &aLoaded",
                    "&aLeft-click: &fReload",
                    "&cRight-click: &fUnload"));
            }
        }

        inv.setItem(45, createBack());
        inv.setItem(47, createItem(Material.NETHER_STAR, "&e&lReload All Extensions",
            "&7Unloads and reloads all extension JARs",
            "&eClick to reload all"));
        if (page > 0)
            inv.setItem(48, createItem(Material.ARROW, "&7\u00AB Previous Page"));
        if ((page + 1) * CONTENT_SIZE < names.size())
            inv.setItem(50, createItem(Material.ARROW, "&7Next Page \u00BB"));
        inv.setItem(53, createClose());

        return inv;
    }

    @Override
    public void handleClick(int slot, ClickType clickType, ItemStack item) {
        if (slot < 45 && names != null && !names.isEmpty()) {
            int index = page * CONTENT_SIZE + slot;
            if (index < names.size()) {
                String name = names.get(index);
                if (clickType.isLeftClick()) {
                    reloadExtension(name);
                    player.sendMessage(color("&5AmazingTitles &f| &aReloaded: &f" + name));
                } else if (clickType.isRightClick()) {
                    AmazingTitles.unloadExtension(name);
                    player.sendMessage(color("&5AmazingTitles &f| &cUnloaded: &f" + name));
                }
                open();
            }
            return;
        }
        switch (slot) {
            case 45: goBack(); break;
            case 47:
                AmazingTitles.unloadAllExtensions();
                PluginLoader.loadExtensions(Booter.getBooter());
                player.sendMessage(color("&5AmazingTitles &f| &aAll extensions reloaded!"));
                open();
                break;
            case 48: if (page > 0) { page--; open(); } break;
            case 50: if ((page + 1) * CONTENT_SIZE < names.size()) { page++; open(); } break;
            case 53: player.closeInventory(); break;
        }
    }

    private void reloadExtension(String name) {
        AmazingExtension ext = AmazingTitles.unloadExtension(name);
        if (ext == null) return;
        File file = new File(AmazingTitles.EXTENSIONS_FOLDER, ext.getAsFile().getName());
        if (!file.exists()) return;
        AmazingExtension reloaded = PluginLoader.getExtension(Booter.getBooter().getClass().getClassLoader(), file);
        if (reloaded != null) AmazingTitles.loadExtension(reloaded);
    }
}
