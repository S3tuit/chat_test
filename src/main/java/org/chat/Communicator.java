package org.chat;

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
            ServerMessage serverMessage;

            try{
                while ((serverMessage = (ServerMessage) inputStream.readObject()) != null){

                    serverMessage.setChatGui(chatGui);
                    serverMessage.process();

                }
            } catch (Exception e) {
                System.out.println("Error reading the message in the chat");
                e.printStackTrace();
            }
        }
    }
}
