package org.chat;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    private final String message;
    private byte[] fileAttached;

    public ChatMessage(String message) {
        this.message = message;
    }

    public ChatMessage(String message, byte[] fileAttached) {
        this.message = message;
        this.fileAttached = fileAttached;
    }

    public boolean isFileAttached() {
        return fileAttached != null;
    }

    public byte[] getFileAttachedCopy() {
        byte[] fileAttachedCopy = new byte[fileAttached.length];
        System.arraycopy(fileAttached, 0, fileAttachedCopy, 0, fileAttached.length);
        return fileAttachedCopy;
    }

    public String getMessage() {
        return message;
    }
}
