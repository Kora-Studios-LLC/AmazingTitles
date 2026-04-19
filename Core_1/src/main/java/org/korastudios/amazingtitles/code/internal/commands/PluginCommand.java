package org.korastudios.amazingtitles.code.internal.commands;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;
import org.korastudios.amazingtitles.code.internal.commands.commandreaders.CommandHandler;
import org.korastudios.amazingtitles.code.internal.commands.commandreaders.subs.CHAnimations;
import org.korastudios.amazingtitles.code.internal.commands.commandreaders.subs.CHGui;
import org.korastudios.amazingtitles.code.internal.commands.commandreaders.subs.CHMessages;
import org.korastudios.amazingtitles.code.internal.commands.commandreaders.subs.CHNotifications;
import org.korastudios.amazingtitles.code.internal.commands.commandreaders.subs.CHPluginActions;
import org.korastudios.amazingtitles.code.internal.utils.CommandUtils;
import org.korastudios.amazingtitles.code.internal.utils.MessageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginCommand implements CommandExecutor, TabExecutor {

    private final Map<String, CommandHandler> handlers = new HashMap<>();

    public PluginCommand(Plugin plugin) {
        handlers.put("sendAnimation", new CHAnimations());
        handlers.put("sendNotification", new CHNotifications());
        handlers.put("sendMessage", new CHMessages());
        handlers.put("pluginActions", new CHPluginActions());
        handlers.put("gui", new CHGui());
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (Map.Entry<String, CommandHandler> entry : handlers.entrySet()) {
                if (s.hasPermission(entry.getValue().permission())) {
                    result.add(entry.getKey());
                }
            }
            return CommandUtils.copyAllStartingWith(result, args[0]);
        }
        if (args.length > 1) {
            CommandHandler handler = handlers.get(args[0]);
            if (handler != null) {
                String[] handlerArgs = new String[args.length - 1];
                System.arraycopy(args, 1, handlerArgs, 0, handlerArgs.length);
                return handler.readAndReturn(s, handlerArgs);
            }
            result.add("Invalid argument (Use wiki for help)");
        }
        return result;
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        return parseCommand(s, args);
    }

    public boolean parseCommand(CommandSender s, String[] args) {
        if (args.length == 0) {
            s.spigot().sendMessage(MessageUtils.getPluginHelp());
            return true;
        }
        CommandHandler handler = handlers.get(args[0]);
        if (handler == null) {
            s.spigot().sendMessage(MessageUtils.getPluginHelp());
            return true;
        }
        String[] handlerArgs = new String[args.length - 1];
        System.arraycopy(args, 1, handlerArgs, 0, handlerArgs.length);
        boolean result = handler.readAndExecute(s, handlerArgs);
        if (!result) s.spigot().sendMessage(handler.helpMessage());
        return result;
    }

    public Map<String, CommandHandler> getHandlers() { return handlers; }
    public void addHandler(String argument, CommandHandler handler) { handlers.put(argument, handler); }
    public void removeHandler(String argument) { handlers.remove(argument); }
}
