package org.chat.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Server {

    private List<ServerClient> serverClients;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public Server() {
        this.serverClients = Collections.synchronizedList(new ArrayList<>());
    }

    public void start(){
        try{
            ServerSocket serverSocket = new ServerSocket(4242);

            while(true){
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());

                // Read the username of the client
                ObjectInputStream objectInput = new ObjectInputStream(clientSocket.getInputStream());
                String username = (String) objectInput.readObject();
                ServerClient serverClient = new ServerClient(objectOutput, username);

                serverClients.add(serverClient);

                // Thread to read msg sent by clients
                Thread thread = new Thread(new ClientHandler(clientSocket, serverClient, objectInput));
                thread.start();
                System.out.println("Client connected! \nClients connected: " + serverClients.size());
            }

        } catch (IOException ex){
            System.out.println("Server Error!");
            ex.printStackTrace();
        } catch (ClassNotFoundException | ClassCastException ex) {
            System.out.println("Server got a message, during client registration, that was not a String!");
            ex.printStackTrace();
        }
    }

    // thread to handle the messages sent by client
    public class ClientHandler implements Runnable {
        final ObjectInputStream objectInput;
        final Socket socket;
        final ServerClient serverClient;

        public ClientHandler(Socket socket, ServerClient serverClient, ObjectInputStream objectInput) {
            this.socket = socket;
            this.serverClient = serverClient;
            this.objectInput = objectInput;
            this.broadcastOnlineUsers();
        }

        // Broadcast a serverMessage to all the clients, if a client ObjectOutputStream gets a SocketException and
        // removes it
        public void broadcastMessage(ServerMessage serverMessage){
            synchronized(serverClients){
                for(ServerClient client : serverClients){
                    try{
                        client.getObjectOut().writeObject(serverMessage);
                        client.getObjectOut().flush();
                    } catch(SocketException ex){
                        System.out.println("Removing client.");
                        serverClients.remove(client);
                    } catch (Exception ex){
                        System.out.println("Error sending message!");
                        ex.printStackTrace();
                    }
                }
            }
        }

        // For the future: to add asynchronous broadcasting to process msg in a separate thread
        public void broadcastOnlineCount() {
            int currOnlineUser;
            synchronized (serverClients){
                currOnlineUser = serverClients.size();
            }

            OnlineCountMessage onlineCountMessage = new OnlineCountMessage(currOnlineUser);
            this.broadcastMessage(onlineCountMessage);
        }

        // send the username of all active users to all clients
        public void broadcastOnlineUsers(){
            // sends the num of active users to all clients
            this.broadcastOnlineCount();

            List<String> usernames;
            synchronized (serverClients){
                usernames = serverClients.stream()
                        .map(ServerClient::getUsername)
                        .toList();
            }

            OnlineUsersMessage onlineUsersMessage = new OnlineUsersMessage(usernames);
            this.broadcastMessage(onlineUsersMessage);
        }

        // Reads incoming messages from clients
        @Override
        public void run() {
            ServerMessage serverMessage;
            Object message;

            // If the object read is an instance of ChatMessage broadcast it, else ignore it.
            try(objectInput; socket){
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
                try {
                    serverClient.getObjectOut().close();
                } catch (IOException e) {
                    System.out.println("Error closing output stream!");
                    e.printStackTrace();
                }
                // Remove client's outputStream on disconnection
                serverClients.remove(serverClient);
                this.broadcastOnlineUsers();
            }
        }
    }
}
