package org.korastudios.amazingtitles.code.internal.components;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.korastudios.amazingtitles.code.api.enums.DisplayType;
import org.korastudios.amazingtitles.code.internal.Booter;
import org.korastudios.amazingtitles.code.internal.utils.BossBarUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public abstract class FramedAnimationComponent extends BaseAnimationComponent {

    protected int framesCounter = 0;
    protected int loopedSeconds = 0;
    protected int loopedFrames = 0;
    protected final LinkedList<String> frames;
    protected final int fps;

    protected FramedAnimationComponent(Plugin plugin, LinkedList<String> frames, String mainText,
                                       String subText, int fps, int duration,
                                       DisplayType displayType, BarColor componentColor) {
        super(plugin, mainText, subText, duration, displayType, componentColor);
        this.frames = frames;
        this.fps = fps;
    }

    @Override
    public List<String> frames() { return frames; }

    @Override
    public int fps() { return fps; }

    @Override
    public String callCurrentFrame() { return frames.get(0); }

    @Override
    public void prepare() {
        DisplayType resolved = BossBarUtils.resolveDisplayType(displayType);
        if (resolved == DisplayType.BOSS_BAR) {
            bossBar = BossBarUtils.createBossBar("", componentColor);
            bossBar.setVisible(false);
        }
        if (resolved == DisplayType.TITLE) {
            runnable = () -> {
                if (!next()) { end(); return; }
                String frame = frames.get(framesCounter);
                Object[] packets = Booter.getNmsProvider().createTitlePacket(frame, subText, 0, 20, 0);
                for (Player p : receivers) {
                    if (p != null) Booter.getNmsProvider().sendTitles(p, packets);
                }
            };
        } else if (resolved == DisplayType.SUBTITLE) {
            runnable = () -> {
                if (!next()) { end(); return; }
                String frame = frames.get(framesCounter);
                Object[] packets = Booter.getNmsProvider().createTitlePacket(subText, frame, 0, 20, 0);
                for (Player p : receivers) {
                    if (p != null) Booter.getNmsProvider().sendTitles(p, packets);
                }
            };
        } else if (resolved == DisplayType.ACTION_BAR) {
            runnable = () -> {
                if (!next()) { end(); return; }
                String frame = frames.get(framesCounter);
                Object packet = Booter.getNmsProvider().createActionbarPacket(frame);
                for (Player p : receivers) {
                    if (p != null) Booter.getNmsProvider().sendActionbar(p, packet);
                }
            };
        } else if (resolved == DisplayType.BOSS_BAR) {
            runnable = () -> {
                if (!next()) { end(); return; }
                bossBar.setTitle(frames.get(framesCounter));
            };
        }
    }

    @Override
    protected void startTask() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, 0, 20 / fps);
    }

    protected abstract boolean next();
}
