package org.korastudios.amazingtitles.code.internal.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.korastudios.amazingtitles.code.api.AmazingTitles;
import org.korastudios.amazingtitles.code.api.builders.AnimationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AnimationPickerGui extends BaseGui {

    private static final int CONTENT_SIZE = 45;

    private final Consumer<String> onSelect;
    private int page = 0;
    private List<String> names;

    public AnimationPickerGui(Player player, BaseGui parent, Consumer<String> onSelect) {
        super(player, parent);
        this.onSelect = onSelect;
    }

    @Override
    protected Inventory buildInventory() {
        names = new ArrayList<>(AmazingTitles.getAnimationNames());
        int totalPages = Math.max(1, (int) Math.ceil(names.size() / (double) CONTENT_SIZE));

        Inventory inv = Bukkit.createInventory(null, 54,
            color("&5AmazingTitles &8\u00BB &fPick Animation &7(" + (page + 1) + "/" + totalPages + ")"));

        for (int i = 0; i < 45; i++) inv.setItem(i, null);
        for (int i = 45; i < 54; i++) inv.setItem(i, createFiller());

        int start = page * CONTENT_SIZE;
        for (int i = 0; i < CONTENT_SIZE && start + i < names.size(); i++) {
            String name = names.get(start + i);
            AnimationBuilder builder = AmazingTitles.getCustomAnimation(name);
            int totalArgs = builder != null ? builder.getTotalArguments() : 0;
            inv.setItem(i, createItem(Material.PAPER, "&d" + name,
                "&7Required args: &f" + totalArgs,
                "&eClick to select"));
        }

        inv.setItem(45, createBack());
        if (page > 0)
            inv.setItem(48, createItem(Material.ARROW, "&7\u00AB Previous Page"));
        if ((page + 1) * CONTENT_SIZE < names.size())
            inv.setItem(50, createItem(Material.ARROW, "&7Next Page \u00BB"));
        inv.setItem(53, createClose());

        return inv;
    }

    @Override
    public void handleClick(int slot, ClickType clickType, ItemStack item) {
        if (slot < 45) {
            int index = page * CONTENT_SIZE + slot;
            if (names != null && index < names.size()) {
                onSelect.accept(names.get(index));
                goBack();
            }
            return;
        }
        switch (slot) {
            case 45: goBack(); break;
            case 48: if (page > 0) { page--; open(); } break;
            case 50: if ((page + 1) * CONTENT_SIZE < names.size()) { page++; open(); } break;
            case 53: player.closeInventory(); break;
        }
    }
}
