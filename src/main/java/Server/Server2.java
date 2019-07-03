package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Server2 {
    private static final int PORT = 2000;


    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        while (true) {
            new DBThread(server.accept()).start();
        }
    }
}

class DBThread extends Thread {

    private static String DB_ROOT = "./src/main/resources/";
    private Socket socket;
    private DataInputStream in;
    private OutputStream out;
    private String fileName;

    public DBThread(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        fileName = in.readUTF();
        out = new FileOutputStream(DB_ROOT + fileName);
        System.out.println(DB_ROOT + fileName);
    }

    @Override
    public void run() {
        System.err.println("yes");
        try {
            int readBytes;
            byte[] buffer = new byte[2048];
            while ((readBytes = in.read(buffer)) > 0) {
                out.write(buffer, 0, readBytes);
                System.out.println();
                out.flush();
            }
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() throws IOException {
        in.close();
        out.close();
        socket.close();
        Path temp = Files.move
                (Paths.get(DB_ROOT + fileName),
                        Paths.get(DB_ROOT + "Files/" + fileName));
        File file = new File(DB_ROOT + fileName);
        if (file.exists())
            file.delete();
    }
}