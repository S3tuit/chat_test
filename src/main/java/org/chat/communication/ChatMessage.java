package org.chat.communication;


import org.chat.gui.FileHandler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatMessage extends ServerMessage {
    private final String message;
    private byte[] fileAttached;
    private String fileName;
    private String senderName;

    public ChatMessage(String message, String senderName) {
        this.message = message;
        this.senderName = senderName;
    }

    public ChatMessage(String message, byte[] fileAttached, String fileName, String senderName) {
        this.message = message;
        this.fileAttached = fileAttached;
        this.fileName = fileName;
        this.senderName = senderName;
    }

    public String getSenderName(){
        return this.senderName;
    }

    public boolean isFileAttached() {
        return fileAttached != null;
    }

    public byte[] getFileAttached() {
        return fileAttached;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public void process() {
        ActionListener saveFileAC = new SaveFileButtonListener();
        getChatGui().appendMessage(this, saveFileAC);
        System.out.println("Chat reads: " + message);
    }

    // Action listener for the "Save File" button next to incoming messages.
    // When the user clicks it saves the byte[] stored in the ChatMessage at root/filename.
    public class SaveFileButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(isFileAttached()) {
                String fileName = getFileName();
                byte[] fileData = getFileAttached();
                FileHandler.saveBytesToFile(fileName, fileData);
            } else {
                System.out.println("File not attached");
            }
        }
    }
}
