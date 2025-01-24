package org.chat;

import org.chat.communication.ChatMessage;
import org.chat.communication.Communicator;
import org.chat.db_obj.UserSession;
import org.chat.gui.ChatGui;
import org.chat.gui.FileHandler;
import org.chat.gui.LoginGui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatApp {

    ChatGui chatGui;
    LoginGui loginGui;
    Communicator communicator;
    FileHandler fileHandler;
    UserSession currUserSession;
    List<Thread> appThreads = new ArrayList<Thread>();

    public static void main(String[] args) {
        ChatApp chatApp = new ChatApp();
        chatApp.startLogin();
    }

    public ChatApp() {
        fileHandler = new FileHandler();
        currUserSession = new UserSession(this);
    }

    public void startLogin(){
        LoginButtonActionListener loginAC = new LoginButtonActionListener();
        loginGui = new LoginGui();
        loginGui.buildGui(loginAC);
    }

    public void startChat(){
        chatGui = new ChatGui(currUserSession);
        communicator = new Communicator(chatGui, this);

        SendButtonListener sendAC = new SendButtonListener();
        LoadButtonActionListener loadAC = new LoadButtonActionListener();
        chatGui.buildChatGui(sendAC, loadAC);
        communicator.start(currUserSession);
    }

    public boolean authenticate(){
        return currUserSession.authenticate();
    }

    private void stopAllThreads(){
        for (Thread t : appThreads) {
            if (t != null && t.isAlive()) {t.interrupt();}
        }
        appThreads.clear();
    }

    public void addAppThread(Thread appThread){
        synchronized (appThreads){
            appThreads.add(appThread);
        }
    }

    public void logout(){
        this.stopAllThreads();


        // Clear the interrupted status of the current thread
        if (Thread.currentThread().isInterrupted()) {
            Thread.interrupted();
        }

        chatGui.dispose();

        currUserSession = new UserSession(this);
        this.startLogin();
        JOptionPane.showMessageDialog(loginGui, "Sorry, you've been logged out :(", "Re-login, please", JOptionPane.WARNING_MESSAGE);
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

            // Assign token and username to currUserSession if login is valid
            int loginValidity = currUserSession.validateLogin(username, password, UUID.randomUUID());

            if (loginValidity == 1 || loginValidity == 2) {
                // valid credentials and session
                loginGui.dispose();

                startChat();
                JOptionPane.showMessageDialog(chatGui, "Login Successful!");
            } else if (loginValidity == 0) {
                // invalid login
                JOptionPane.showMessageDialog(loginGui, "Invalid username or password :(");
            } else if (loginValidity == 3) {
                // there already is an active session with a different token, asks the user if he wants to invalidate
                // the active session
                if (loginGui.askToInvalidateCurrSession()){
                    currUserSession.invalidateOtherSessions();

                    loginGui.dispose();

                    startChat();
                    JOptionPane.showMessageDialog(chatGui, "Login Successful!");
                }
            }

        }


    }
}