package org.chat;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    private final String message;
    private byte[] fileAttached;
    private String fileName;

    public ChatMessage(String message) {
        this.message = message;
    }

    public ChatMessage(String message, byte[] fileAttached, String fileName) {
        this.message = message;
        this.fileAttached = fileAttached;
        this.fileName = fileName;
    }

    public boolean isFileAttached() {
        return fileAttached != null;
    }

    public byte[] getFileAttached() {
        return fileAttached;
    }

    public String getMessage() {
        return message;
    }

    public String getFileName() {
        return fileName;
    }
}
