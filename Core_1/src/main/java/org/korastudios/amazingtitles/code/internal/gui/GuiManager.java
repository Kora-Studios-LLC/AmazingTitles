package org.korastudios.amazingtitles.code.internal.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.korastudios.amazingtitles.code.internal.Booter;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class GuiManager implements Listener {

    private static GuiManager instance;

    private final Map<UUID, BaseGui> openGuis = new ConcurrentHashMap<>();
    private final Map<UUID, ChatInputSession> chatSessions = new ConcurrentHashMap<>();
    private final Set<UUID> awaitingInput = ConcurrentHashMap.newKeySet();

    public static GuiManager getInstance() {
        if (instance == null) instance = new GuiManager();
        return instance;
    }

    public void setOpenGui(Player player, BaseGui gui) {
        openGuis.put(player.getUniqueId(), gui);
    }

    public void startChatInput(Player player, String prompt, Consumer<String> callback, BaseGui returnGui) {
        awaitingInput.add(player.getUniqueId());
        chatSessions.put(player.getUniqueId(), new ChatInputSession(callback, returnGui));
        player.closeInventory();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5AmazingTitles &f| &7" + prompt));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Type &fcancel &7to cancel."));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player player = (Player) e.getWhoClicked();
        BaseGui gui = openGuis.get(player.getUniqueId());
        if (gui == null) return;
        if (gui.inventory == null || e.getView().getTopInventory() != gui.inventory) return;
        e.setCancelled(true);
        if (e.getClickedInventory() == null) return;
        if (e.getClickedInventory().getType() == InventoryType.PLAYER) return;
        if (e.getCurrentItem() == null) return;
        int slot = e.getSlot();
        ClickType click = e.getClick();
        org.bukkit.inventory.ItemStack item = e.getCurrentItem().clone();
        Bukkit.getScheduler().runTask(Booter.getInstance(), () -> gui.handleClick(slot, click, item));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player)) return;
        Player player = (Player) e.getPlayer();
        if (!awaitingInput.contains(player.getUniqueId())) {
            openGuis.remove(player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (!awaitingInput.contains(player.getUniqueId())) return;
        e.setCancelled(true);
        String input = e.getMessage();
        ChatInputSession session = chatSessions.remove(player.getUniqueId());
        awaitingInput.remove(player.getUniqueId());
        openGuis.remove(player.getUniqueId());
        if (session == null) return;
        Bukkit.getScheduler().runTask(Booter.getInstance(), () -> {
            if (!input.equalsIgnoreCase("cancel")) {
                session.getCallback().accept(input);
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5AmazingTitles &f| &7Input cancelled."));
            }
            BaseGui returnGui = session.getReturnGui();
            if (returnGui != null) returnGui.open();
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        UUID uid = e.getPlayer().getUniqueId();
        openGuis.remove(uid);
        chatSessions.remove(uid);
        awaitingInput.remove(uid);
    }
}
