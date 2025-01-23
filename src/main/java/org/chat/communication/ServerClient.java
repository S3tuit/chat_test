package org.chat.communication;

import java.io.ObjectOutputStream;

public class ServerClient {

    final private ObjectOutputStream objectOut;
    private String username;

    public ServerClient(ObjectOutputStream objectOut, String username) {
        this.objectOut = objectOut;
        this.username = username;
    }

    public ObjectOutputStream getObjectOut() {
        return objectOut;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
