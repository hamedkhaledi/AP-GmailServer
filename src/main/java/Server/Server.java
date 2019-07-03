package Server;


import Model.ALL_MESSAGES;
import Model.ALL_USERS;
import Model.IO.ViewModel.MessageType;
import Model.IO.ViewModel.ServerMessage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    public static final int requestPort = 1378;
    public static final String serverIP = "localhost";
    private static ServerSocket requestServerSocket;
    private static final String USERS_FILE_URL = "src/main/resources/users.ser";

    public static void main(String[] args) {
        Server.start();
    }

    public static void start() {
        try {
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(USERS_FILE_URL));
//            objectOutputStream.writeObject(ALL_USERS.getAllUsers());
//            objectOutputStream.close();
//            ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(new FileOutputStream("src/main/resources/messages.ser"));
//            objectOutputStream2.writeObject(ALL_MESSAGES.getAllMessages());
//            objectOutputStream2.close();
            ALL_USERS.init();
            ALL_MESSAGES.init();
            requestServerSocket = new ServerSocket(requestPort);
            Thread serverThread = new Thread(new Server(), "Server Thread");
            serverThread.start();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!requestServerSocket.isClosed()) {
            try {
                new Thread(new ServerRunner(requestServerSocket.accept()), "Server Runner").start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class ServerRunner implements Runnable {
    private Socket serverSocket;
    private ServerHandler serverHandler;

    public ServerRunner(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        ServerMessage clientRequest = null;
        try {
            serverHandler = new ServerHandler(serverSocket,
                    new ObjectInputStream(serverSocket.getInputStream()),
                    new ObjectOutputStream(serverSocket.getOutputStream()));
            while (clientRequest == null || clientRequest.getMessageType() != MessageType.Disconnect) {
                clientRequest = (ServerMessage) serverHandler.getInputStream().readObject();
                serverHandler.handle(clientRequest);
            }

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            System.out.println("Exception");
        } finally {
            userDisconnect();
        }
    }

    public void userDisconnect() {
        try {
            serverHandler.getOutputStream().close();
            serverHandler.getInputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

