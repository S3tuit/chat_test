package org.chat.communication;

import java.util.ArrayList;
import java.util.List;

public class OnlineUsersMessage extends ServerMessage{

    List<String> onlineUsers = new ArrayList<String>();

    @Override
    public void process() {
        super.getChatGui().updateOnlineUser(onlineUsers);
    }

    @Override
    public String getMessage() {
        return "";
    }
}
