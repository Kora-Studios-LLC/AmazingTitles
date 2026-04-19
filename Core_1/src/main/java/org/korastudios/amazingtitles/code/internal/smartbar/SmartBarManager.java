package org.korastudios.amazingtitles.code.internal.smartbar;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SmartBarManager {

    private final Plugin plugin;
    private final Map<Player, SmartBar> bars = new ConcurrentHashMap<>();
    private volatile List<String> staticAnimationContent = Collections.emptyList();

    public SmartBarManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public Map<Player, SmartBar> getBars() { return bars; }

    public void insertBar(Player player, SmartBar bar) { bars.put(player, bar); }

    public SmartBar getBar(Player player) { return bars.get(player); }

    public void removeBar(Player player) { bars.remove(player); }

    public List<String> getStaticAnimationContent() { return staticAnimationContent; }

    public void setStaticAnimationContent(List<String> frames) {
        this.staticAnimationContent = new ArrayList<>(frames);
    }
}
