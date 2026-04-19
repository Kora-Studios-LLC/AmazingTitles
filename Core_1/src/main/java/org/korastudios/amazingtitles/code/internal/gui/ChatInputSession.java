package org.korastudios.amazingtitles.code.internal.gui;

import java.util.function.Consumer;

public class ChatInputSession {
    private final Consumer<String> callback;
    private final BaseGui returnGui;

    public ChatInputSession(Consumer<String> callback, BaseGui returnGui) {
        this.callback = callback;
        this.returnGui = returnGui;
    }

    public Consumer<String> getCallback() { return callback; }
    public BaseGui getReturnGui() { return returnGui; }
}
