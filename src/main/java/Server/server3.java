package Server;

import
        Model.Message;
import Model.User;


import java.io.*;
import java.net.Socket;

public class server3 {
    private static final int PORT = 2001;
    private static final String IP = "localhost";
    private static Socket socket;
    private static InputStream in;
    private static DataOutputStream out;
    private static String DB_ROOT = "./src/main/resources/Files/";

    public static void sendFile(final Message message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connect();
                    writeData(message);
                    disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void writeData(Message message) throws IOException {

        System.out.println("writing data...");
        out = new DataOutputStream(socket.getOutputStream());
        System.out.println(DB_ROOT + message.getFileName());
        in = new FileInputStream(DB_ROOT + message.getFileName());
        // System.out.println(message.getAttachment());
        File file = new File(DB_ROOT + message.getFileName());
        out.writeUTF(file.getName());
        System.out.println(file.getName());
        int readBytes;
        byte[] buffer = new byte[2048];
        while ((readBytes = in.read(buffer)) > 0) {
            out.write(buffer, 0, readBytes);
            out.flush();
        }

    }

    public static void connect() throws IOException {
        socket = new Socket(IP, PORT);
    }

    private static void disconnect() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

}
