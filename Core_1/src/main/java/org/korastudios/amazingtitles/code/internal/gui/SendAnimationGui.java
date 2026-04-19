package org.korastudios.amazingtitles.code.internal.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.korastudios.amazingtitles.code.api.AmazingTitles;
import org.korastudios.amazingtitles.code.api.builders.AnimationBuilder;
import org.korastudios.amazingtitles.code.api.enums.DisplayType;
import org.korastudios.amazingtitles.code.internal.commands.commandreaders.readers.ArgsHelper;
import org.korastudios.amazingtitles.code.internal.components.AnimationComponent;
import org.korastudios.amazingtitles.code.internal.components.ComponentArguments;
import org.korastudios.amazingtitles.code.internal.utils.ColorTranslator;

import java.util.Arrays;
import java.util.List;

public class SendAnimationGui extends BaseGui {

    private static final DisplayType[] DISPLAY_TYPES = DisplayType.values();
    private static final BarColor[] BAR_COLORS = BarColor.values();

    private String targetPlayers = "all";
    private String animationName = "NONE";
    private String animArgs = "";
    private DisplayType displayType = DisplayType.ACTION_BAR;
    private BarColor barColor = BarColor.PURPLE;
    private int duration = 5;
    private int fps = 10;
    private String mainText = "Hello World!";
    private String subText = "";

    public SendAnimationGui(Player player, BaseGui parent) {
        super(player, parent);
    }

    @Override
    protected Inventory buildInventory() {
        Inventory inv = Bukkit.createInventory(null, 54, color("&5AmazingTitles &8\u00BB &fSend Animation"));
        for (int i = 0; i < 54; i++) inv.setItem(i, createFiller());

        // Row 1 — who and what
        inv.setItem(10, createItem(Material.COMPASS, "&e&lTarget Players",
            "&7Current: &f" + targetPlayers,
            "&7Supports: all, PlayerName,",
            "&7-p:permission, -w:world",
            "&eClick to edit"));
        inv.setItem(13, createItem(Material.PAPER, "&d&lAnimation",
            "&7Current: &f" + animationName,
            "&eClick to pick"));
        inv.setItem(16, createItem(Material.FEATHER, "&a&lAnimation Arguments",
            "&7Current: &f" + truncate(animArgs.isEmpty() ? "&onone" : animArgs, 25),
            "&eClick to edit &7(space-separated)"));

        // Row 2 — how it looks
        inv.setItem(19, createItem(Material.COMPARATOR, "&b&lDisplay Type",
            "&7Current: &f" + displayType.name(),
            "&eClick to cycle"));
        inv.setItem(22, createItem(Material.CLOCK, "&a&lDuration",
            "&7Current: &f" + duration + " seconds",
            "&aLeft-click: &f+1s &8| &cRight-click: &f-1s"));
        inv.setItem(25, createItem(Material.REPEATER, "&a&lFPS",
            "&7Current: &f" + fps + " fps &8(max 20)",
            "&aLeft-click: &f+1 &8| &cRight-click: &f-1"));

        // Row 3 — text content
        inv.setItem(28, createItem(Material.NAME_TAG, "&f&lMain Text",
            "&7Current: &f" + truncate(mainText, 25),
            "&eClick to edit"));

        if (displayType == DisplayType.TITLE || displayType == DisplayType.SUBTITLE) {
            inv.setItem(31, createItem(Material.NAME_TAG, "&7&lSub Text",
                "&7Current: &f" + truncate(subText, 25),
                "&eClick to edit"));
        }
        if (displayType == DisplayType.BOSS_BAR) {
            inv.setItem(31, createItem(Material.MAGENTA_DYE, "&c&lBossBar Color",
                "&7Current: &f" + barColor.name(),
                "&eClick to cycle"));
        }

        // Row 5 — actions
        inv.setItem(45, createBack());
        inv.setItem(49, createItem(Material.EMERALD, "&a&l\u25BA Send Animation",
            "&7Target: &f" + targetPlayers,
            "&7Animation: &f" + animationName,
            "&eClick to send"));
        inv.setItem(53, createClose());

        return inv;
    }

    @Override
    public void handleClick(int slot, ClickType clickType, ItemStack item) {
        switch (slot) {
            case 10:
                GuiManager.getInstance().startChatInput(player,
                    "Enter targets: all / PlayerName / -p:permission / -w:world",
                    input -> targetPlayers = input, this);
                break;
            case 13:
                new AnimationPickerGui(player, this, name -> animationName = name).open();
                break;
            case 16:
                GuiManager.getInstance().startChatInput(player,
                    "Enter animation arguments (space-separated, or type 'none'):",
                    input -> animArgs = input.equalsIgnoreCase("none") ? "" : input, this);
                break;
            case 19: {
                int idx = (Arrays.asList(DISPLAY_TYPES).indexOf(displayType) + 1) % DISPLAY_TYPES.length;
                displayType = DISPLAY_TYPES[idx];
                open();
                break;
            }
            case 22:
                if (clickType.isLeftClick()) duration = Math.min(3600, duration + 1);
                else if (clickType.isRightClick()) duration = Math.max(1, duration - 1);
                open();
                break;
            case 25:
                if (clickType.isLeftClick()) fps = Math.min(20, fps + 1);
                else if (clickType.isRightClick()) fps = Math.max(1, fps - 1);
                open();
                break;
            case 28:
                GuiManager.getInstance().startChatInput(player,
                    "Enter the main animation text (supports &color codes):",
                    input -> mainText = input, this);
                break;
            case 31:
                if (displayType == DisplayType.TITLE || displayType == DisplayType.SUBTITLE) {
                    GuiManager.getInstance().startChatInput(player,
                        "Enter the sub text:",
                        input -> subText = input, this);
                } else if (displayType == DisplayType.BOSS_BAR) {
                    int idx = (Arrays.asList(BAR_COLORS).indexOf(barColor) + 1) % BAR_COLORS.length;
                    barColor = BAR_COLORS[idx];
                    open();
                }
                break;
            case 45: goBack(); break;
            case 49: sendAnimation(); break;
            case 53: player.closeInventory(); break;
        }
    }

    private void sendAnimation() {
        try {
            List<Player> players = ArgsHelper.readPlayers(targetPlayers);
            if (players.isEmpty()) {
                player.sendMessage(color("&5AmazingTitles &f| &cNo players matched: &f" + targetPlayers));
                return;
            }
            AnimationBuilder builder = AmazingTitles.getCustomAnimation(animationName);
            if (builder == null) {
                player.sendMessage(color("&5AmazingTitles &f| &cAnimation not found: &f" + animationName));
                return;
            }
            String[] parsedArgs = animArgs.trim().isEmpty()
                ? new String[0]
                : animArgs.trim().split("\\s+");
            int needed = builder.getTotalArguments();
            String[] finalArgs = new String[Math.max(0, needed)];
            for (int i = 0; i < finalArgs.length; i++) {
                finalArgs[i] = i < parsedArgs.length ? parsedArgs[i] : "";
            }
            String sub = subText.isEmpty() ? "" : ColorTranslator.colorize(subText);
            AnimationComponent component = builder.createComponent(
                ComponentArguments.create(mainText, sub, barColor, duration, fps, displayType),
                finalArgs
            );
            component.addReceivers(players);
            component.prepare();
            component.run();
            player.sendMessage(color("&5AmazingTitles &f| &aSent &f" + animationName + " &ato &f" + players.size() + " &aplayer(s)!"));
            player.closeInventory();
        } catch (Exception e) {
            player.sendMessage(color("&5AmazingTitles &f| &cFailed to send: &f" + e.getMessage()));
        }
    }
}
