package org.chat;

import org.chat.communication.ChatMessage;
import org.chat.communication.Communicator;
import org.chat.db_obj.ChatJDBC;
import org.chat.db_obj.UserProfile;
import org.chat.db_obj.UserSession;
import org.chat.gui.ChatGui;
import org.chat.gui.FileHandler;
import org.chat.gui.LoginGui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.UUID;

public class ChatApp {

    ChatGui chatGui;
    LoginGui loginGui;
    Communicator communicator;
    FileHandler fileHandler;
    UserSession currUserSession;

    public static void main(String[] args) {
        ChatApp chatApp = new ChatApp();
        chatApp.startLogin();
    }

    public ChatApp() {
        chatGui = new ChatGui();
        communicator = new Communicator(chatGui);
        fileHandler = new FileHandler();
    }

    public void startLogin(){
        LoginButtonActionListener loginAC = new LoginButtonActionListener();
        loginGui = new LoginGui();
        loginGui.buildGui(loginAC);
    }

    public void startChat(){
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
                chatMessage = new ChatMessage(message, fileData, fileName, currUserSession.getUsername());
            } else {
                chatMessage = new ChatMessage(message, currUserSession.getUsername());
            }

            communicator.sendMessage(chatMessage);
        }
    }

    public class LoadButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            File fileLoaded = fileHandler.letUserChooseFile(chatGui);
            chatGui.showFileLoaded(fileLoaded);
        }
    }

    public class LoginButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = loginGui.getUsername();
            String password = loginGui.getPassword();

            UserSession userSession = ChatJDBC.validateLogin(username, password, UUID.randomUUID());

            // valid credentials for login
            if(userSession != null){
                loginGui.dispose();

                currUserSession = userSession;
                startChat();
                JOptionPane.showMessageDialog(chatGui, "Login Successful!");
            } else {
                // invalid login
                JOptionPane.showMessageDialog(chatGui, "Invalid username or password :(");
            }
        }
    }
}