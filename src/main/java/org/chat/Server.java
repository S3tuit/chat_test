package org.chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {

    private List<ObjectOutputStream> clientOutputStreams;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public Server() {
        this.clientOutputStreams = Collections.synchronizedList(new ArrayList<>());
    }

    public void start(){
        try{
            ServerSocket serverSocket = new ServerSocket(4242);

            while(true){
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
                clientOutputStreams.add(objectOutput);

                // Thread to read msg sent by clients
                Thread thread = new Thread(new ClientHandler(clientSocket, objectOutput));
                thread.start();
                System.out.println("Client connected! \nClients connected: " + clientOutputStreams.size());
            }

        } catch (IOException ex){
            System.out.println("Server Error!");
            ex.printStackTrace();
        }
    }

    // thread to handle the messages sent by client
    public class ClientHandler implements Runnable {
        final ObjectInputStream objectInput;
        final Socket socket;
        final ObjectOutputStream objectOutput;

        public ClientHandler(Socket socket, ObjectOutputStream objectOutputStream){
            // Used tmp for variable assignment to make objectInput final
            ObjectInputStream tmpObjectInput = null;
            try{
                tmpObjectInput = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex){
                System.out.println("Error opening input stream for thread!");
                ex.printStackTrace();
            }
            this.socket = socket;
            this.objectOutput = objectOutputStream;
            this.objectInput = tmpObjectInput;
            this.broadcastOnlineCount();
        }

        // Broadcast a serverMessage to all the clients, if a client ObjectOutputStream gets a SocketException
        // and removes it
        public void broadcastMessage(ServerMessage serverMessage){
            synchronized(clientOutputStreams){
                for(ObjectOutputStream stream : clientOutputStreams){
                    try{
                        stream.writeObject(serverMessage);
                        stream.flush();
                    } catch(SocketException ex){
                        System.out.println("Removing client.");
                        clientOutputStreams.remove(stream);
                    } catch (Exception ex){
                        System.out.println("Error sending message!");
                        ex.printStackTrace();
                    }
                }
            }
        }

        // For the future: to add asynchronous broadcasting techniques to process msg in a separate thread
        public void broadcastOnlineCount() {
            int currOnlineUser;
            synchronized (clientOutputStreams){
                currOnlineUser = clientOutputStreams.size();
            }

            OnlineCountMessage onlineCountMessage = new OnlineCountMessage(currOnlineUser);
            this.broadcastMessage(onlineCountMessage);
        }

        // Reads incoming messages from clients
        @Override
        public void run() {
            ServerMessage serverMessage;
            Object message;

            // If the object read is an instance of ChatMessage broadcast it, else ignore it.
            try(objectInput; objectOutput; socket){
                while((message = objectInput.readObject()) != null){

                    if(message instanceof ChatMessage){
                        serverMessage = (ServerMessage) message;
                        System.out.println("Server reads: " + serverMessage.getMessage());
                        this.broadcastMessage(serverMessage);
                    } else {
                        System.out.println("Unexpected message type: " + message.getClass().getName());
                    }
                }
            } catch (SocketException se) {
                System.out.println("Client disconnected.");
            } catch (Exception ex){
                System.out.println("Error reading messages!");
                ex.printStackTrace();
            } finally {
                // Remove client's outputStream on disconnection
                clientOutputStreams.remove(objectOutput);
                this.broadcastOnlineCount();
            }
        }
    }
}
