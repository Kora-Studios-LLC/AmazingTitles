package org.korastudios.amazingtitles.code.internal.components.implementations;

import org.bukkit.boss.BarColor;
import org.bukkit.plugin.Plugin;
import org.korastudios.amazingtitles.code.api.enums.DisplayType;
import org.korastudios.amazingtitles.code.internal.components.FramedAnimationComponent;

import java.util.LinkedList;

public class FadeInAnimationComponent extends FramedAnimationComponent {

    public FadeInAnimationComponent(Plugin plugin, LinkedList<String> frames, String mainText,
                                    String subText, int fps, int duration,
                                    DisplayType displayType, BarColor componentColor) {
        super(plugin, frames, mainText, subText, fps, duration, displayType, componentColor);
    }

    @Override
    protected boolean next() {
        if (framesCounter + 1 < frames.size()) ++framesCounter;
        if (loopedFrames >= fps) {
            if (loopedSeconds >= duration) return false;
            ++loopedSeconds;
            loopedFrames = 0;
        }
        ++loopedFrames;
        return true;
    }
}
