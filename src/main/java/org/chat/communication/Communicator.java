package org.chat.communication;

import org.chat.ChatApp;
import org.chat.db_obj.UserSession;
import org.chat.gui.ChatGui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Communicator {

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Socket socket;
    private ChatGui chatGui;
    private ChatApp chatApp;

    public Communicator(ChatGui chatGui, ChatApp chatApp) {
        this.chatGui = chatGui;
        this.chatApp = chatApp;
    }

    public void start(UserSession userSession){
        this.setUpNetworking(userSession);
        Thread thread = new Thread(new IncomingReader());
        chatApp.addAppThread(thread);
        thread.start();
    }

    private void setUpNetworking(UserSession userSession){
        try{
            // Establish a connection with the server
            socket = new Socket("127.0.0.1", 4242);
            inputStream = new ObjectInputStream(socket.getInputStream());

            // Send the username to the server
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(userSession.getUsername());
            outputStream.flush();

            System.out.println("Connected to server");

        } catch (IOException e){
            System.out.println("Socket could not be created for the chat");
            e.printStackTrace();
        }
    }

    public void sendMessage(ChatMessage chatMessage){
        if(!chatApp.authenticate()){
            chatApp.logout();
            return;
        }

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
            ServerMessage serverMessage;

            try{
                while (!Thread.currentThread().isInterrupted() &&
                        (serverMessage = (ServerMessage) inputStream.readObject()) != null){

                    if(!chatApp.authenticate()){
                        Thread.currentThread().interrupt();

                        // make server understand it should disconnect the client before showing the gui
                        // solves bug: the connection remain opened until chatApp.logout(); returns.
                        closeResources();
                        chatApp.logout();
                        return;
                    }

                    serverMessage.setChatGui(chatGui);
                    serverMessage.process();

                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error reading the message in the chat");
                e.printStackTrace();
            } finally {
                // Perform cleanup
                closeResources();
            }
        }

        private void closeResources(){
            try{
                if(inputStream != null){
                    inputStream.close();
                }
            } catch (IOException e){
                System.out.println("Error closing the input stream");
                e.printStackTrace();
            }
        }
    }
}
