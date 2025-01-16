package org.chat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatApp {

    ChatGui chatGui;
    Communicator communicator;

    public static void main(String[] args) {
        ChatApp chatApp = new ChatApp();
        chatApp.startApp();
    }

    public ChatApp() {
        chatGui = new ChatGui();
        communicator = new Communicator(chatGui);
    }

    public void startApp(){
        SendButtonListener sendAC = new SendButtonListener();
        chatGui.buildChatGui(sendAC);
        communicator.start();
    }

    public class SendButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String message = chatGui.getMessageAndClean();
            communicator.sendMessage(message);
        }
    }
}