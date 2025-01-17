package org.chat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Communicator {

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Socket socket;
    private ChatGui chatGui;

    public Communicator(ChatGui chatGui) {
        this.chatGui = chatGui;
    }

    public void start(){
        this.setUpNetworking();
        Thread thread = new Thread(new IncomingReader());
        thread.start();
    }

    private void setUpNetworking(){
        try{
            socket = new Socket("127.0.0.1", 4242);
            inputStream = new ObjectInputStream(socket.getInputStream());

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Connected to server");

        } catch (IOException e){
            System.out.println("Socket could not be created for the chat");
            e.printStackTrace();
        }
    }

    public void sendMessage(ChatMessage chatMessage){
        try{
            outputStream.writeObject(chatMessage);
            outputStream.flush();

        } catch (Exception ex) {
            System.out.println("Error sending the message from the chat");
            ex.printStackTrace();
        }
    }

    public class IncomingReader implements Runnable{

        @Override
        public void run() {
            ChatMessage chatMessage;
            SaveFileButtonListener saveFileAC;

            try{
                while ((chatMessage = (ChatMessage) inputStream.readObject()) != null){

                    saveFileAC = new SaveFileButtonListener(chatMessage);
                    chatGui.appendMessage(chatMessage, saveFileAC);
                    System.out.println("Chat reads: " + chatMessage.getMessage());
                }
            } catch (Exception e) {
                System.out.println("Error reading the message in the chat");
                e.printStackTrace();
            }
        }
    }

    // Action listener for the "Save File" button. Stores an instance of ChatMessage.
    // When the user clicks it saves the byte[] stored in the ChatMessage at root/filename.
    public class SaveFileButtonListener implements ActionListener {

        ChatMessage chatMessage;

        public SaveFileButtonListener(ChatMessage message) {
            this.chatMessage = message;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(chatMessage.isFileAttached()) {
                String fileName = chatMessage.getFileName();
                byte[] fileData = chatMessage.getFileAttached();
                FileHandler.saveBytesToFile(fileName, fileData);
            } else {
                System.out.println("File not attached");
            }
        }
    }
}
