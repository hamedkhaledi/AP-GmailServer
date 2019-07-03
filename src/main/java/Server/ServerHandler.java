package Server;


import Model.ALL_MESSAGES;
import Model.ALL_USERS;
import Model.IO.ViewModel.ServerMessage;
import Model.Message;
import Model.User;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ServerHandler {
    public static String respond;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private static final String DB_ROOT = "./src/main/resources/Files";
    private static List<User> users = new ArrayList<>();
    public static Semaphore semaphore = new Semaphore(1);


    ServerHandler(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }


    void handle(ServerMessage serverMessage) throws IOException, InterruptedException {
        User user;

        switch (serverMessage.getMessageType()) {
            case SignUp:
                String ADDRESS = "./src/main/resources/Images/Faces/" + serverMessage.getSender().getImageNumber() + ".png";
                semaphore.acquire();
                ALL_USERS.getAllUsers().add(serverMessage.getSender());
                getOutputStream().writeObject(ALL_USERS.getAllUsers());
                getOutputStream().flush();
                respond = serverMessage.getSender().getUsername() + " register " + ADDRESS + '\n'
                        + "time : " + getCurrentTimeUsingCalendar() + '\n';
                System.out.println(respond);
                semaphore.release();
                break;
            case Connect:
                getOutputStream().writeObject(ALL_USERS.getAllUsers());
                getOutputStream().flush();
                respond = "Connect " + '\n';
                System.out.println(respond);
                break;
            case SignIn:
//                serverMessage.getSender().setOutputStream(outputStream);
//                serverMessage.getSender().setInputStream(inputStream);
                respond = serverMessage.getSender().getUsername() + " SignIn \ntime : " + getCurrentTimeUsingCalendar() + '\n';
                System.out.println(respond);

                break;
            case Disconnect:
                this.getOutputStream().close();
                this.getInputStream().close();
                respond = "Disconnect " + serverMessage.getSender().getUsername() + '\n';
                System.out.println(respond);
                break;
            case Send:
                String ADDRESS2;
                if (serverMessage.getMessage().getFileName() != null) {
                    ADDRESS2 = "./src/main/resources/Files/" + serverMessage.getMessage().getFileName();
                    File file = new File(ADDRESS2);
                    while (!file.exists()) {
                        //wait
                    }
                } else
                    ADDRESS2 = "";
                semaphore.acquire();
                ALL_MESSAGES.getAllMessages().add(serverMessage.getMessage());
                semaphore.release();
                // ALL_USERS.getAllUsers().get(users.indexOf(serverMessage.getReceiver())).getOutputStream().writeObject(serverMessage);
                // ALL_USERS.getAllUsers().get(users.indexOf(serverMessage.getReceiver())).getOutputStream().flush();
                respond = serverMessage.getSender().getUsername() + " send "
                        + serverMessage.getMessage().getSubject() + " " +
                        ADDRESS2 + " to " + serverMessage.getReceiver().getUsername()
                        + " time : " + getCurrentTimeUsingCalendar() + '\n';
                System.out.println(respond);
                break;
            case Reload:
                List<Message> messages2 = new ArrayList<>();
                for (Message MessageT : ALL_MESSAGES.getAllMessages()) {
                    if (MessageT.getReciever().equals(serverMessage.getSender()) || MessageT.getSender().equals(serverMessage.getSender())) {
                        messages2.add(MessageT);
                    }
                }
                getOutputStream().writeObject(messages2);
                getOutputStream().flush();
                // System.out.println("Reload " + serverMessage.getSender() + '\n');
                break;
            case Reply:
                String ADDRESS3;
                if (serverMessage.getMessage().getFileName() != null) {
                    ADDRESS3 = "./src/main/resources/Files/" + serverMessage.getMessage().getFileName();
                    File file = new File(ADDRESS3);
                    while (!file.exists()) {
                        //wait
                    }
                } else
                    ADDRESS3 = "";
                semaphore.acquire();
                ALL_MESSAGES.getAllMessages().add(serverMessage.getMessage());
                semaphore.release();
                respond = serverMessage.getSender().getUsername() + " reply " + '\n'
                        + serverMessage.getMessage().getSubject() + " " +
                        ADDRESS3 + " to " + serverMessage.getReceiver().getUsername()
                        + " time : " + getCurrentTimeUsingCalendar() + '\n';
                System.out.println(respond);
                break;
            case Forward:
                String ADDRESS4;
                if (serverMessage.getMessage().getFileName() != null)
                    ADDRESS4 = "./src/main/resources/Files/" + serverMessage.getMessage().getFileName();
                else
                    ADDRESS4 = "";
                semaphore.acquire();
                ALL_MESSAGES.getAllMessages().add(serverMessage.getMessage());
                semaphore.release();
                respond = serverMessage.getSender().getUsername() + " Forward " + '\n'
                        + serverMessage.getMessage().getSubject() + " " +
                        ADDRESS4 + " to " + serverMessage.getReceiver().getUsername()
                        + " time : " + getCurrentTimeUsingCalendar() + '\n';
                System.out.println(respond);
                break;

            case Star:
                semaphore.acquire();
                Message Temp = ALL_MESSAGES.getAllMessages().get(ALL_MESSAGES.getAllMessages().indexOf(serverMessage.getMessage()));
                Temp.setImportant(!Temp.isImportant());
                semaphore.release();
                respond = serverMessage.getSender().getUsername() + " ChangeImportant " + '\n'
                        + serverMessage.getMessage().getSubject() + '\n'
                        + "time : " + getCurrentTimeUsingCalendar() + '\n';
                System.out.println(respond);
                break;
            case Delete:
                semaphore.acquire();
                Message Temp2 = ALL_MESSAGES.getAllMessages().get(ALL_MESSAGES.getAllMessages().indexOf(serverMessage.getMessage()));
                Temp2.setRemoved(true);
                semaphore.release();
                respond = serverMessage.getSender().getUsername() + " Delete " + '\n'
                        + serverMessage.getMessage().getSubject() + '\n'
                        + "time : " + getCurrentTimeUsingCalendar() + '\n';
                System.out.println(respond);
                break;
            case Read:
                semaphore.acquire();
                Message Temp1 = ALL_MESSAGES.getAllMessages().get(ALL_MESSAGES.getAllMessages().indexOf(serverMessage.getMessage()));
                Temp1.setReaded(true);
                semaphore.release();
                respond = serverMessage.getSender().getUsername() + " Read " + '\n'
                        + serverMessage.getMessage().getSubject() + '\n'
                        + "time : " + getCurrentTimeUsingCalendar() + '\n';
                System.out.println(respond);
                break;
            case Unread:
                semaphore.acquire();
                Message Temp3 = ALL_MESSAGES.getAllMessages().get(ALL_MESSAGES.getAllMessages().indexOf(serverMessage.getMessage()));
                Temp3.setReaded(false);
                semaphore.release();
                respond = serverMessage.getSender().getUsername() + " UnRead " + '\n'
                        + serverMessage.getMessage().getSubject() + '\n'
                        + "time : " + getCurrentTimeUsingCalendar() + '\n';
                System.out.println(respond);
                break;
            case Setting:
                semaphore.acquire();
                User Tempuser = ALL_USERS.getAllUsers().get(ALL_USERS.getAllUsers().indexOf(serverMessage.getSender()));
                Tempuser.setAge(serverMessage.getSender().getAge());
                Tempuser.setFirstName(serverMessage.getSender().getFirstName());
                Tempuser.setPhoneNumber(serverMessage.getSender().getPhoneNumber());
                String Address5 = "./src/main/resources/Images/Faces/" + serverMessage.getSender().getImageNumber() + ".png";
                Tempuser.setGender(serverMessage.getSender().getGender());
                Tempuser.setPassword(serverMessage.getSender().getPassword());
                Tempuser.setLastName(serverMessage.getSender().getLastName());
                Tempuser.setImageNumber(serverMessage.getSender().getImageNumber());
                Tempuser.setImagePath(Address5);
                semaphore.release();
                getOutputStream().writeObject(ALL_USERS.getAllUsers());
                getOutputStream().flush();
                respond = serverMessage.getSender().getUsername() + " Changed " + '\n' +
                        "time : " + getCurrentTimeUsingCalendar() + '\n';
                System.out.println(respond);
                break;
            case Block:
                semaphore.acquire();
                User Tempuser1 = ALL_USERS.getAllUsers().get(ALL_USERS.getAllUsers().indexOf(serverMessage.getSender()));
                Tempuser1.setBlackList(serverMessage.getSender().getBlackList());
                semaphore.release();
                getOutputStream().writeObject(ALL_USERS.getAllUsers());
                getOutputStream().flush();
                respond = serverMessage.getSender().getUsername() + " block " + serverMessage.getReceiver().getUsername()
                        + "time : " + getCurrentTimeUsingCalendar() + '\n';
                System.out.println(respond);
                break;
            case OpenFile:
                server3.sendFile(serverMessage.getMessage());
                System.out.println(serverMessage.getSender() + "Read file \n");
                break;
            case Error:
                Message message = new Message();
                message.setReciever(serverMessage.getSender());
                message.setSender(ALL_USERS.getAllUsers().get(ALL_USERS.getAllUsers().indexOf(new User("mailerdaemon"))));
                message.setSubject("Fail");
                message.setText("Delivery message failed");
                message.setTime(getCurrentTimeUsingCalendar());
                semaphore.acquire();
                ALL_MESSAGES.getAllMessages().add(message);
                semaphore.release();
                System.out.println(serverMessage.getSender().getUsername() + " sendError ");
                System.out.println(serverMessage.getMessage().getSubject());
                System.out.println("time : " + getCurrentTimeUsingCalendar() + '\n');
                break;
            case UnBlock:
                semaphore.acquire();
                User Tempuser2 = ALL_USERS.getAllUsers().get(ALL_USERS.getAllUsers().indexOf(serverMessage.getSender()));
                Tempuser2.setBlackList(serverMessage.getSender().getBlackList());
                semaphore.release();
                getOutputStream().writeObject(ALL_USERS.getAllUsers());
                getOutputStream().flush();
                System.out.println(serverMessage.getSender().getUsername() + " block " + serverMessage.getReceiver().getUsername());
                System.out.println("time : " + getCurrentTimeUsingCalendar() + '\n');
                break;//TODO
        }
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("src/main/resources/users.ser"));
        objectOutputStream.writeObject(ALL_USERS.getAllUsers());
        objectOutputStream.close();
        ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(new FileOutputStream("src/main/resources/messages.ser"));
        objectOutputStream2.writeObject(ALL_MESSAGES.getAllMessages());
        objectOutputStream2.close();


    }

    public static String getCurrentTimeUsingCalendar() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH:mm");
        return dateFormat.format(date);
    }
}
