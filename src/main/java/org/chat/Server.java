package org.chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

public class Server {

    private ArrayList<ObjectOutputStream> clientOutputStreams;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start(){
        clientOutputStreams = new ArrayList<>();
        try{
            ServerSocket serverSocket = new ServerSocket(4242);

            while(true){
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
                clientOutputStreams.add(objectOutput);

                // Thread to read msg sent by clients
                Thread thread = new Thread(new ClientHandler(clientSocket));
                thread.start();
                System.out.println("Client connected! \nClients connected: " + clientOutputStreams.size());
            }

        } catch (IOException ex){
            System.out.println("Server Error!");
            ex.printStackTrace();
        }
    }

    // thread to handle the messages send by client
    public class ClientHandler implements Runnable {
        ObjectInputStream objectInput;
        Socket socket;

        public ClientHandler(Socket socket){
            try{
                this.socket = socket;
                objectInput = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex){
                System.out.println("Error opening input stream for thread!");
                ex.printStackTrace();
            }
        }

        public void tellEveryone(ChatMessage chatMessage){

            Iterator<ObjectOutputStream> iterator = clientOutputStreams.iterator();
            while(iterator.hasNext()){
                try{
                    ObjectOutputStream objectOutput = iterator.next();
                    objectOutput.writeObject(chatMessage);
                    objectOutput.flush();

                } catch(SocketException ex){
                    System.out.println("Removing client.");
                    iterator.remove();
                } catch (Exception ex){
                    System.out.println("Error sending message!");
                    ex.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            ChatMessage chatMessage;

            try{
                while((chatMessage = (ChatMessage) objectInput.readObject()) != null){
                    System.out.println("Server reads: " + chatMessage.getMessage());
                    this.tellEveryone(chatMessage);

                }
            } catch (SocketException se) {
                System.out.println("Client disconnected.");
            } catch (Exception ex){
                System.out.println("Error reading messages!");
                ex.printStackTrace();
            }
        }
    }
}
