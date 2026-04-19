package org.korastudios.amazingtitles.code.api;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.korastudios.amazingtitles.code.Iintegrations.CMIIntegration;
import org.korastudios.amazingtitles.code.Iintegrations.Integration;
import org.korastudios.amazingtitles.code.api.builders.AnimationBuilder;
import org.korastudios.amazingtitles.code.api.interfaces.AmazingExtension;
import org.korastudios.amazingtitles.code.internal.Booter;
import org.korastudios.amazingtitles.code.internal.PluginServices;
import org.korastudios.amazingtitles.code.internal.commands.commandreaders.CommandHandler;
import org.korastudios.amazingtitles.code.internal.components.AnimationComponent;
import org.korastudios.amazingtitles.code.internal.components.ComponentArguments;
import org.korastudios.amazingtitles.code.internal.interactivemessages.InteractiveMessageHelper;
import org.korastudios.amazingtitles.code.internal.smartbar.SmartBar;
import org.korastudios.amazingtitles.code.internal.smartbar.SmartNotification;

import java.io.File;
import java.util.*;

public class AmazingTitles {

    public static final File EXTENSIONS_FOLDER = new File(Booter.getInstance().getDataFolder(), "Extensions");
    public static final File INTEGRATIONS_FOLDER = new File(Booter.getInstance().getDataFolder(), "Integrations");

    static {
        EXTENSIONS_FOLDER.mkdirs();
        INTEGRATIONS_FOLDER.mkdirs();
    }

    /*
     * Internal
     */

    public static void reloadPlugin(CommandSender sender) { Booter.getBooter().reload(sender); }
    public static void reloadPlugin() { Booter.getBooter().reload(null); }

    /*
     * Extensions
     */

    public static Set<String> getLoadedExtensionFileNames() { return PluginServices.get().loadedExtensions(); }
    public static Set<String> getLoadedExtensionNames() { return PluginServices.get().extensions().keySet(); }

    public static void loadExtension(AmazingExtension extension) {
        PluginServices services = PluginServices.get();
        services.extensions().put(extension.extension_name(), extension);
        try {
            extension.load();
            services.loadedExtensions().add(extension.getAsFile().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AmazingExtension unloadExtension(String extensionName) {
        PluginServices services = PluginServices.get();
        AmazingExtension extension = services.extensions().remove(extensionName);
        if (extension == null) return null;
        services.loadedExtensions().remove(extension.getAsFile().getName());
        extension.unload();
        unregisterExtensionListeners(extensionName);
        services.animations().entrySet().removeIf(e -> {
            AmazingExtension owner = e.getValue().getOwner();
            return owner != null && owner.extension_name().equalsIgnoreCase(extensionName);
        });
        return extension;
    }

    public static void registerExtensionListener(AmazingExtension extension, Listener listener) {
        PluginServices services = PluginServices.get();
        List<Listener> listeners = services.extensionListeners()
                .computeIfAbsent(extension.extension_name(), k -> new ArrayList<>());
        Bukkit.getPluginManager().registerEvents(listener, Booter.getInstance());
        listeners.add(listener);
    }

    public static List<Listener> getExtensionListeners(String extension) {
        return PluginServices.get().extensionListeners().getOrDefault(extension, Collections.emptyList());
    }

    public static void unregisterExtensionListeners(String extension) {
        List<Listener> listeners = PluginServices.get().extensionListeners().remove(extension);
        if (listeners == null) return;
        listeners.forEach(HandlerList::unregisterAll);
    }

    public static void unloadAllExtensions() {
        for (String name : new ArrayList<>(PluginServices.get().extensions().keySet())) {
            unloadExtension(name);
        }
    }

    /*
     * System
     */

    public static void executeAmazingTitlesCommand(String command) {
        executeAmazingTitlesCommand(command, false);
    }

    public static void executeAmazingTitlesCommand(String command, boolean handlers) {
        if (command.startsWith("/") && command.length() > 1) command = command.substring(1);
        if (handlers) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        } else {
            String[] args = command.split(" ");
            String[] builtArgs = args.length > 0 ? new String[args.length - 1] : new String[0];
            System.arraycopy(args, 1, builtArgs, 0, builtArgs.length);
            Booter.getPluginCommand().parseCommand(Bukkit.getConsoleSender(), builtArgs);
        }
    }

    /*
     * Command handlers
     */

    public static void registerCommandHandler(String argument, CommandHandler commandHandler) {
        Booter.getPluginCommand().addHandler(argument, commandHandler);
    }

    public static void unregisterCommandHandler(String argument) {
        Booter.getPluginCommand().removeHandler(argument);
    }

    /*
     * Animations
     */

    public static void registerCustomAnimation(String name, AnimationBuilder builder) {
        if (name == null || builder == null) {
            System.out.println("[AT] Name or AnimationBuilder is null!");
            return;
        }
        PluginServices.get().animations().put(name.replace(" ", "_").toUpperCase(), builder);
    }

    public static void unregisterCustomAnimation(String name) {
        PluginServices.get().animations().remove(name);
    }

    public static AnimationBuilder getCustomAnimation(String name) {
        return PluginServices.get().animations().get(name);
    }

    public static boolean isCustomAnimationExists(String name) { return getCustomAnimation(name) != null; }
    public static boolean isCustomAnimationEnabled(String name) { return PluginServices.get().animations().containsKey(name); }
    public static Collection<AnimationBuilder> getAnimations() { return PluginServices.get().animations().values(); }
    public static Set<String> getAnimationNames() { return PluginServices.get().animations().keySet(); }

    public static void broadcastAnimation(String animation, ComponentArguments arguments, String[] args) {
        sendAnimation(animation, arguments, args, Bukkit.getOnlinePlayers());
    }

    public static void sendAnimation(String animation, ComponentArguments arguments, String[] args, Collection<? extends Player> players) {
        AnimationBuilder builder = PluginServices.get().animations().get(animation);
        if (builder == null) return;
        AnimationComponent component = builder.createComponent(arguments, args);
        component.prepare();
        component.addReceivers(new ArrayList<>(players));
        component.run();
    }

    public static void sendAnimation(String animation, ComponentArguments arguments, String[] args, Player... players) {
        AnimationBuilder builder = PluginServices.get().animations().get(animation);
        if (builder == null) return;
        AnimationComponent component = builder.createComponent(arguments, args);
        component.prepare();
        component.addReceivers(players);
        component.run();
    }

    /*
     * Components
     */

    public static void insertAnimation(Player player, AnimationComponent component) {
        PluginServices.get().components().put(player.getUniqueId(), component);
    }

    public static AnimationComponent getAnimationBy(Player player) { return getAnimationBy(player.getUniqueId()); }
    public static AnimationComponent getAnimationBy(UUID uuid) { return PluginServices.get().components().get(uuid); }

    public static void removeAnimation(Player player) {
        AnimationComponent component = getAnimationBy(player);
        if (component != null) component.removeReceivers(player);
    }

    public static void removeAnimation(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) removeAnimation(player);
    }

    public static boolean hasAnimation(Player player) { return hasAnimation(player.getUniqueId()); }
    public static boolean hasAnimation(UUID uuid) { return PluginServices.get().components().containsKey(uuid); }
    public static void removeAnimationFromCache(UUID uuid) { PluginServices.get().components().remove(uuid); }

    /*
     * Smart bar
     */

    public static SmartBar getSmartBar(Player player) { return Booter.getSmartBarManager().getBar(player); }

    public static void broadcastNotification(SmartNotification notification) {
        sendNotification(UUID.randomUUID().toString(), notification, Bukkit.getOnlinePlayers());
    }

    public static void broadcastNotification(String id, SmartNotification notification) {
        sendNotification(id, notification, Bukkit.getOnlinePlayers());
    }

    public static void sendNotification(String id, SmartNotification notification, Collection<? extends Player> players) {
        for (Player p : players) {
            SmartBar bar = getSmartBar(p);
            if (bar != null) bar.setNotification(id, notification);
        }
    }

    public static void sendNotification(SmartNotification notification, Collection<? extends Player> players) {
        sendNotification(UUID.randomUUID().toString(), notification, players);
    }

    public static void sendNotification(String id, SmartNotification notification, Player... players) {
        for (Player p : players) {
            SmartBar bar = getSmartBar(p);
            if (bar != null) bar.setNotification(id, notification);
        }
    }

    public static void sendNotification(SmartNotification notification, Player... players) {
        sendNotification(UUID.randomUUID().toString(), notification, players);
    }

    public static void hideSmartBar(Player player) {
        SmartBar bar = getSmartBar(player);
        if (bar != null) bar.setHide(true);
    }

    public static void showSmartBar(Player player) {
        SmartBar bar = getSmartBar(player);
        if (bar != null) bar.setHide(false);
    }

    /*
     * Messages
     */

    public static BaseComponent[] getInteractiveMessageFromRaw(String rawMessage) {
        return InteractiveMessageHelper.getMessageFromRaw(rawMessage);
    }

    public static void sendInteractiveMessage(String rawMessage, Player... players) {
        BaseComponent[] message = getInteractiveMessageFromRaw(rawMessage);
        for (Player p : players) p.spigot().sendMessage(message);
    }

    public static void sendInteractiveMessage(String rawMessage, Collection<? extends Player> players) {
        BaseComponent[] message = getInteractiveMessageFromRaw(rawMessage);
        for (Player p : players) p.spigot().sendMessage(message);
    }

    public static void broadcastInteractiveMessage(String rawMessage) {
        Bukkit.spigot().broadcast(getInteractiveMessageFromRaw(rawMessage));
    }

    /*
     * System (Internal)
     */

    public static void clearCacheInternally() {
        PluginServices services = PluginServices.get();
        if (services == null) return;
        services.clearComponents();
        unloadAllExtensions();
        services.animations().clear();
        services.loadedExtensions().clear();
        services.extensionListeners().clear();
    }

    public static void loadIntegrations() {
        List<Integration> integrations = new ArrayList<>();
        if (isPluginPresent("CMI")) integrations.add(new CMIIntegration());
        integrations.forEach(Integration::reload);
    }

    private static boolean isPluginPresent(String name) {
        return Bukkit.getPluginManager().getPlugin(name) != null;
    }
}
