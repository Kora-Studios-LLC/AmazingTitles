package org.korastudios.amazingtitles.code.internal.components;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.korastudios.amazingtitles.code.api.AmazingTitles;
import org.korastudios.amazingtitles.code.api.enums.DisplayType;
import org.korastudios.amazingtitles.code.internal.utils.BossBarUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class BaseAnimationComponent implements AnimationComponent {

    protected BukkitTask task;
    protected Runnable runnable;
    protected BossBar bossBar;
    protected final List<Player> receivers = new CopyOnWriteArrayList<>();
    protected final Plugin plugin;
    protected final String mainText;
    protected final String subText;
    protected final int duration;
    protected final DisplayType displayType;
    protected final BarColor componentColor;

    protected BaseAnimationComponent(Plugin plugin, String mainText, String subText,
                                     int duration, DisplayType displayType, BarColor componentColor) {
        this.plugin = plugin;
        this.mainText = mainText;
        this.subText = subText;
        this.duration = duration;
        this.displayType = displayType;
        this.componentColor = componentColor;
    }

    @Override
    public String mainText() { return mainText; }

    @Override
    public Optional<String> subText() { return Optional.of(subText); }

    @Override
    public int duration() { return duration; }

    @Override
    public DisplayType display() { return displayType; }

    @Override
    public Optional<BarColor> componentColor() { return Optional.ofNullable(componentColor); }

    @Override
    public void addReceivers(Player... players) {
        for (Player p : players) {
            AmazingTitles.removeAnimation(p);
            AmazingTitles.insertAnimation(p, this);
            receivers.add(p);
            if (bossBar != null) bossBar.addPlayer(p);
        }
    }

    @Override
    public void addReceivers(Collection<Player> players) {
        for (Player p : players) {
            AmazingTitles.removeAnimation(p);
            AmazingTitles.insertAnimation(p, this);
            receivers.add(p);
            if (bossBar != null) bossBar.addPlayer(p);
        }
    }

    @Override
    public void removeReceivers(Player... players) {
        receivers.removeAll(Arrays.asList(players));
    }

    @Override
    public void removeReceivers(Collection<Player> players) {
        receivers.removeAll(players);
    }

    @Override
    public void run() {
        if (BossBarUtils.resolveDisplayType(displayType) == DisplayType.BOSS_BAR && bossBar != null) {
            bossBar.setVisible(true);
        }
        startTask();
    }

    protected abstract void startTask();

    @Override
    public void end() {
        for (Player p : receivers) {
            AmazingTitles.removeAnimationFromCache(p.getUniqueId());
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (bossBar != null) {
            bossBar.setVisible(false);
            bossBar.removeAll();
            bossBar = null;
        }
        receivers.clear();
    }
}
