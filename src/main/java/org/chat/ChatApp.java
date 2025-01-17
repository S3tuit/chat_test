package org.chat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ChatApp {

    ChatGui chatGui;
    Communicator communicator;
    FileHandler fileHandler;

    public static void main(String[] args) {
        ChatApp chatApp = new ChatApp();
        chatApp.startApp();
    }

    public ChatApp() {
        chatGui = new ChatGui();
        communicator = new Communicator(chatGui);
        fileHandler = new FileHandler();
    }

    public void startApp(){
        SendButtonListener sendAC = new SendButtonListener();
        LoadButtonActionListener loadAC = new LoadButtonActionListener();
        chatGui.buildChatGui(sendAC, loadAC);
        communicator.start();
    }

    public class SendButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String message = chatGui.getMessageAndClean();
            ChatMessage chatMessage;

            if(chatGui.isFileLoaded()){
                byte[] fileData = FileHandler.readFileAsByte(chatGui.getFilePathAndClean());
                String fileName = chatGui.getFileAttachedName();
                chatMessage = new ChatMessage(message, fileData, fileName);
            } else {
                chatMessage = new ChatMessage(message);
            }

            communicator.sendMessage(chatMessage);
        }
    }

    public class LoadButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            File fileLoaded = fileHandler.letUserChooseFile(chatGui.getChatFrame());
            chatGui.showFileLoaded(fileLoaded);
        }
    }

}