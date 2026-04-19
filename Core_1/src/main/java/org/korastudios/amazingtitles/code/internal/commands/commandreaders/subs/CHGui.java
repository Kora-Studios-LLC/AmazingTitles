package org.korastudios.amazingtitles.code.internal.commands.commandreaders.subs;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.korastudios.amazingtitles.code.internal.commands.commandreaders.CommandHandler;
import org.korastudios.amazingtitles.code.internal.commands.commandreaders.HandlerType;
import org.korastudios.amazingtitles.code.internal.commands.commandreaders.InternalHandlerType;
import org.korastudios.amazingtitles.code.internal.gui.MainMenuGui;
import org.korastudios.amazingtitles.code.internal.utils.ColorTranslator;
import org.korastudios.amazingtitles.code.internal.utils.TextComponentBuilder;

import java.util.Collections;
import java.util.List;

public class CHGui implements CommandHandler {

    @Override
    public BaseComponent[] helpMessage() {
        TextComponentBuilder builder = new TextComponentBuilder();
        if (ColorTranslator.isHexSupport()) {
            builder.appendLegacy("\n<#a217ff>AmazingTitles ✎ </#ff7ae9> &fIn-Game GUI\n");
            builder.appendLegacy(" &7> <#dedede>/at gui</#c7c7c7> &8- &7Opens the configuration GUI\n");
        } else {
            builder.appendLegacy("\n&5AmazingTitles ✎ &fIn-Game GUI\n");
            builder.appendLegacy(" &7> &7/at gui &8- &7Opens the configuration GUI\n");
        }
        builder.appendLegacy("§f");
        return builder.createMessage();
    }

    @Override
    public String permission() {
        return "at.plugin";
    }

    @Override
    public HandlerType handlerType() {
        return new InternalHandlerType();
    }

    @Override
    public boolean readAndExecute(CommandSender s, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage("§cThis command can only be used by players.");
            return true;
        }
        new MainMenuGui((Player) s).open();
        return true;
    }

    @Override
    public List<String> readAndReturn(CommandSender s, String[] args) {
        return Collections.emptyList();
    }
}
