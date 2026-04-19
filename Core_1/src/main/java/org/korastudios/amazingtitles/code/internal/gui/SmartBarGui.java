package org.korastudios.amazingtitles.code.internal.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.korastudios.amazingtitles.code.internal.Booter;
import org.korastudios.amazingtitles.code.internal.configuration.CustomConfiguration;

import java.util.Arrays;
import java.util.List;

public class SmartBarGui extends BaseGui {

    public SmartBarGui(Player player, BaseGui parent) {
        super(player, parent);
    }

    @Override
    protected Inventory buildInventory() {
        Inventory inv = Bukkit.createInventory(null, 45, color("&5AmazingTitles &8\u00BB &fSmartBar Settings"));
        for (int i = 0; i < 45; i++) inv.setItem(i, createFiller());

        CustomConfiguration.SmartBar sb = Booter.getCustomConfiguration().getShortcutSmartBar();

        // Row 1 — main toggles
        inv.setItem(10, createToggle(sb.getNotificationsPermission(), "&bSmartBar Notifications",
            "&7Actionbar notification queue is active",
            "&7Actionbar notification queue is disabled"));

        // Row 2 — static bar section
        inv.setItem(19, createToggle(sb.getStaticBarPermission(), "&6Static Bar Enabled",
            "&7A persistent animated bar is shown to all players",
            "&7Static bar is hidden"));
        inv.setItem(21, createToggle(sb.getStaticBarNotificationsPermission(), "&6Static Bar Notifications",
            "&7Notifications will interrupt the static bar",
            "&7Notifications run alongside the static bar"));
        inv.setItem(23, createItem(Material.JUKEBOX, "&d&lAnimation",
            "&7Current: &f" + sb.getStaticBarAnimation(),
            "&eClick to change"));
        inv.setItem(25, createItem(Material.CLOCK, "&e&lFPS",
            "&7Current: &f" + sb.getStaticBarFps() + " fps &8(max 20)",
            "&aLeft-click: &f+1 fps",
            "&cRight-click: &f-1 fps"));

        // Row 3 — text and arguments
        inv.setItem(28, createItem(Material.NAME_TAG, "&a&lStatic Bar Text",
            "&7Current: &f" + truncate(sb.getStaticBarText(), 30),
            "&eClick to edit in chat"));

        List<String> args = sb.getStaticBarArguments();
        String argsDisplay = args.isEmpty() ? "&onone" : String.join(" ", args);
        inv.setItem(30, createItem(Material.FEATHER, "&a&lAnimation Arguments",
            "&7Current: &f" + truncate(argsDisplay, 30),
            "&eClick to edit &7(space-separated)"));

        inv.setItem(32, createItem(Material.EMERALD, "&a&lSave & Apply",
            "&7Saves all settings and reloads the plugin",
            "&eClick to apply"));

        // Navigation
        inv.setItem(36, createBack());
        inv.setItem(44, createClose());
        return inv;
    }

    @Override
    public void handleClick(int slot, ClickType clickType, ItemStack item) {
        CustomConfiguration cfg = Booter.getCustomConfiguration();
        CustomConfiguration.SmartBar sb = cfg.getShortcutSmartBar();

        switch (slot) {
            case 10:
                cfg.save("SmartBar.Notifications", !sb.getNotificationsPermission());
                open();
                break;
            case 19:
                cfg.save("SmartBar.StaticBar.Enabled", !sb.getStaticBarPermission());
                open();
                break;
            case 21:
                cfg.save("SmartBar.StaticBar.Notifications", !sb.getStaticBarNotificationsPermission());
                open();
                break;
            case 23:
                new AnimationPickerGui(player, this, name -> cfg.save("SmartBar.StaticBar.Animation", name)).open();
                break;
            case 25: {
                int fps = sb.getStaticBarFps();
                if (clickType.isLeftClick()) fps = Math.min(20, fps + 1);
                else if (clickType.isRightClick()) fps = Math.max(1, fps - 1);
                cfg.save("SmartBar.StaticBar.Fps", fps);
                open();
                break;
            }
            case 28:
                GuiManager.getInstance().startChatInput(player,
                    "Enter the new static bar text:",
                    input -> cfg.save("SmartBar.StaticBar.Text", input),
                    this);
                break;
            case 30:
                GuiManager.getInstance().startChatInput(player,
                    "Enter animation arguments (space-separated, or leave blank):",
                    input -> cfg.save("SmartBar.StaticBar.Arguments",
                        input.trim().isEmpty() ? java.util.Collections.emptyList()
                            : Arrays.asList(input.trim().split("\\s+"))),
                    this);
                break;
            case 32:
                player.closeInventory();
                Booter.getBooter().reload(player);
                break;
            case 36: goBack(); break;
            case 44: player.closeInventory(); break;
        }
    }
}
