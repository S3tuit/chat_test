package org.chat;

import java.io.Serial;
import java.io.Serializable;

public abstract class ServerMessage implements Serializable {

    private ChatGui chatGui = null;

    @Serial
    private static final long serialVersionUID = 1L;

    public abstract void process();

    public abstract String getMessage();

    public void setChatGui(ChatGui chatGui) {
        this.chatGui = chatGui;
    }

    public ChatGui getChatGui() {
        return chatGui;
    }
}
