package org.chat.communication;

import java.util.ArrayList;
import java.util.List;

public class OnlineUsersMessage extends ServerMessage{

    List<String> onlineUsers = new ArrayList<String>();

    public OnlineUsersMessage(List<String> onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    @Override
    public void process() {
        super.getChatGui().updateOnlineUsername(onlineUsers);
    }

    @Override
    public String getMessage() {
        return String.join(", ", onlineUsers);
    }
}
