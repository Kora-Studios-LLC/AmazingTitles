package org.korastudios.amazingtitles.code.internal;

import org.bukkit.event.Listener;
import org.korastudios.amazingtitles.code.api.builders.AnimationBuilder;
import org.korastudios.amazingtitles.code.api.interfaces.AmazingExtension;
import org.korastudios.amazingtitles.code.internal.components.AnimationComponent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class PluginServices {

    private static volatile PluginServices current;

    private final Map<String, AnimationBuilder> animations = new ConcurrentHashMap<>();
    private final Map<UUID, AnimationComponent> components = new ConcurrentHashMap<>();
    private final Map<String, AmazingExtension> extensions = new ConcurrentHashMap<>();
    private final Map<String, List<Listener>> extensionListeners = new ConcurrentHashMap<>();
    private final Set<String> loadedExtensions = ConcurrentHashMap.newKeySet();

    public static void install(PluginServices services) {
        current = services;
    }

    public static PluginServices get() {
        return current;
    }

    public Map<String, AnimationBuilder> animations() { return animations; }
    public Map<UUID, AnimationComponent> components() { return components; }
    public Map<String, AmazingExtension> extensions() { return extensions; }
    public Map<String, List<Listener>> extensionListeners() { return extensionListeners; }
    public Set<String> loadedExtensions() { return loadedExtensions; }

    public void clearComponents() {
        List<AnimationComponent> active = new ArrayList<>(components.values());
        components.clear();
        active.forEach(AnimationComponent::end);
    }
}
