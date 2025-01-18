package org.chat;

public class OnlineCountMessage extends ServerMessage{

    int currOnlineUsers;

    public OnlineCountMessage(int currOnlineUsers){
        this.currOnlineUsers = currOnlineUsers;
    }

    @Override
    public void process() {
        super.getChatGui().updateOnlineUserCount(currOnlineUsers);
    }

    @Override
    public String getMessage() {
        return "Current Online Users: " + currOnlineUsers;
    }
}
