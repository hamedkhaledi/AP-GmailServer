package Model.IO.Connection;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


//for Read file


public class Connection2 {
    private static final int PORT = 2001;

    public static void main(String[] args) throws IOException {
        System.out.println("Yes");
        ServerSocket server = new ServerSocket(PORT);
        new DBThread(server.accept()).start();
    }
}

class DBThread extends Thread {
    private static String DB_ROOT = "./src/main/resources/Files/";
    private Socket socket;
    private DataInputStream in;
    private OutputStream out;

    public DBThread(Socket socket) throws IOException {
        System.out.println("yes2");
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        String fileName = in.readUTF();
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
    }
}