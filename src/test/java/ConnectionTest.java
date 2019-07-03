import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import Model.ALL_MESSAGES;
import Model.ALL_USERS;
import Model.IO.Connection.Connection;
import Model.IO.ViewModel.MessageType;
import Model.IO.ViewModel.ServerMessage;
import Model.Message;
import Model.User;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Server.ServerHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import Server.Server;

public class ConnectionTest {
    static Connection connection;
    static User Hamed;
    static User Ali;
    static Connection HamedConnection;
    static Connection AliConnection;

    @BeforeClass
    public static void First() throws InterruptedException {
        Server.start();
        Thread.sleep(300);
        Hamed = new User("Hamed", "1234");
        HamedConnection = new Connection(Hamed);
        ALL_USERS.getAllUsers().add(Hamed);
        Ali = new User("Ali", "1234");
        ALL_USERS.getAllUsers().add(Ali);
        AliConnection = new Connection(Ali);
        Thread.sleep(300);
    }

    @Before
    public void initialize() throws IOException, ClassNotFoundException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("src/test/java/users.ser"));
        objectOutputStream.writeObject(ALL_USERS.getAllUsers());
        objectOutputStream.close();
        ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(new FileOutputStream("src/test/java/messages.ser"));
        objectOutputStream2.writeObject(ALL_MESSAGES.getAllMessages());
        objectOutputStream2.close();
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("src/test/java/messages.ser"));
        ALL_MESSAGES.setAllMessages((ArrayList<Message>) objectInputStream.readObject());
        objectInputStream.close();
        ObjectInputStream objectInputStreamUser = new ObjectInputStream(new FileInputStream("src/test/java/users.ser"));
        ALL_USERS.setAllUsers((List<User>) objectInputStreamUser.readObject());
        objectInputStreamUser.close();
    }

    @Test
    public void connectTest() throws InterruptedException, IOException {
        connection = new Connection(Ali);
        Thread.sleep(300);
        connection.sendRequest(new ServerMessage(MessageType.SignIn, Ali, null, null));
        Thread.sleep(300);
        assertEquals("Ali SignIn \ntime : " + getCurrentTimeUsingCalendar() + '\n', ServerHandler.respond);
        Thread.sleep(300);
        connection.sendRequest(new ServerMessage(MessageType.SignIn, Hamed, null, null));
        Thread.sleep(300);
        assertEquals("Hamed SignIn \ntime : " + getCurrentTimeUsingCalendar() + '\n', ServerHandler.respond);
        Thread.sleep(300);
    }

    @Test
    public void textTest() throws InterruptedException {
        Message AliToHamed = new Message(Ali, Hamed, getCurrentTimeUsingCalendar(), "Subject", "Salam. Khobi?");
        AliConnection.sendRequest(new ServerMessage(MessageType.Send, Ali, Hamed, AliToHamed));
        Thread.sleep(300);
        assertEquals("Ali" + " send "
                + "Subject" + " " +
                " to " + "Hamed"
                + " time : " + getCurrentTimeUsingCalendar() + '\n', ServerHandler.respond);
        Thread.sleep(300);
        Message HamedtoAli = new Message(Hamed, Ali, getCurrentTimeUsingCalendar(), "Subject2", "Kheili Khobam");
        HamedConnection.sendRequest(new ServerMessage(MessageType.Send, Hamed, Ali, HamedtoAli));
        Thread.sleep(300);
        assertEquals("Hamed" + " send "
                + "Subject2" + " " +
                " to " + "Ali"
                + " time : " + getCurrentTimeUsingCalendar() + '\n', ServerHandler.respond);
        Thread.sleep(300);
    }

    @Test
    public void blockTest() throws InterruptedException {
        HamedConnection.sendRequest(new ServerMessage(MessageType.Block, Hamed, Ali, null));
        Thread.sleep(300);
        assertEquals("Hamed" + " block " + "Ali"
                + "time : " + getCurrentTimeUsingCalendar() + '\n', ServerHandler.respond);
        Thread.sleep(300);
        AliConnection.sendRequest(new ServerMessage(MessageType.Block, Ali, Hamed, null));
        Thread.sleep(300);
        assertEquals("Ali" + " block " + "Hamed"
                + "time : " + getCurrentTimeUsingCalendar() + '\n', ServerHandler.respond);
    }

    @Test
    public void replyTest() throws InterruptedException {
        Message AlitoHamed = new Message(Ali, Hamed, getCurrentTimeUsingCalendar(), "Subject", "Salam. Khobi?");
        AliConnection.sendRequest(new ServerMessage(MessageType.Send, Ali, Hamed, AlitoHamed));
        Thread.sleep(300);
        Message HamedToAli = new Message(Hamed, Ali, getCurrentTimeUsingCalendar(), "Subject", "Kheili Khobam");
        HamedConnection.sendRequest(new ServerMessage(MessageType.Reply, Hamed, Ali, HamedToAli));
        Thread.sleep(300);
        assertEquals("Hamed" + " reply " + '\n'
                + "Subject" + " " +
                " to " + "Ali"
                + " time : " + getCurrentTimeUsingCalendar() + '\n', ServerHandler.respond);
    }

    @Test
    public void forwardTest() throws InterruptedException {
        User Hassan = new User("Hassan", "1234");
        Message HamedToAli = new Message(Hamed, Ali, getCurrentTimeUsingCalendar(), "Subject", "Salam. Khobi?");
        Thread.sleep(300);
        HamedConnection.sendRequest(new ServerMessage(MessageType.Send, Hamed, Ali, HamedToAli));
        Thread.sleep(300);
        Message AlitoHassan = new Message(Ali, Hassan, getCurrentTimeUsingCalendar(), "Subject", "Salam. Khobi?");
        Thread.sleep(300);
        AliConnection.sendRequest(new ServerMessage(MessageType.Forward, Ali, Hassan, AlitoHassan));
        Thread.sleep(300);
        assertEquals("Ali" + " Forward " + '\n'
                + "Subject" + " " +
                " to " + "Hassan"
                + " time : " + getCurrentTimeUsingCalendar() + '\n', ServerHandler.respond);
        Thread.sleep(300);
    }

    @Test
    public void importantTest() throws InterruptedException {
        Message AlitoHamed = new Message(Ali, Hamed, getCurrentTimeUsingCalendar(), "Subject", "Salam. Khobi?");
        AliConnection.sendRequest(new ServerMessage(MessageType.Send, Ali, Hamed, AlitoHamed));
        Thread.sleep(300);
        AliConnection.sendRequest(new ServerMessage(MessageType.Star, Ali, null, AlitoHamed));
        Thread.sleep(300);
        assertEquals("Ali" + " ChangeImportant " + '\n'
                + "Subject" + '\n'
                + "time : " + getCurrentTimeUsingCalendar() + '\n', ServerHandler.respond);
    }

    @Test
    public void readTest() throws InterruptedException {
        Message AlitoHamed = new Message(Ali, Hamed, getCurrentTimeUsingCalendar(), "Subject", "Salam. Khobi?");
        AliConnection.sendRequest(new ServerMessage(MessageType.Send, Ali, Hamed, AlitoHamed));
        Thread.sleep(300);
        AliConnection.sendRequest(new ServerMessage(MessageType.Read, Ali, null, AlitoHamed));
        Thread.sleep(300);
        assertEquals("Ali" + " Read " + '\n'
                + "Subject" + '\n'
                + "time : " + getCurrentTimeUsingCalendar() + '\n', ServerHandler.respond);
        AliConnection.sendRequest(new ServerMessage(MessageType.Unread, Ali, null, AlitoHamed));
        Thread.sleep(300);
        assertEquals("Ali" + " UnRead " + '\n'
                + "Subject" + '\n'
                + "time : " + getCurrentTimeUsingCalendar() + '\n', ServerHandler.respond);
    }

    @Test
    public void removeTest() throws InterruptedException {
        Message AlitoHamed = new Message(Ali, Hamed, getCurrentTimeUsingCalendar(), "Subject", "Salam. Khobi?");
        AliConnection.sendRequest(new ServerMessage(MessageType.Send, Ali, Hamed, AlitoHamed));
        Thread.sleep(300);
        AliConnection.sendRequest(new ServerMessage(MessageType.Delete, Ali, null, AlitoHamed));
        Thread.sleep(500);
        assertEquals("Ali" + " Delete " + '\n'
                + "Subject" + '\n'
                + "time : " + getCurrentTimeUsingCalendar() + '\n', ServerHandler.respond);

    }

    @Test
    public void signupTest() throws InterruptedException {
        Thread.sleep(300);
        User mamad = new User("Mamad", "1234");
        mamad.setImageNumber(2);
        Thread.sleep(300);
        AliConnection.sendRequest(new ServerMessage(MessageType.SignUp, mamad, null, null));
        Thread.sleep(300);
        assertEquals("Mamad" + " register " + "./src/main/resources/Images/Faces/" + 2 + ".png" + '\n'
                + "time : " + getCurrentTimeUsingCalendar() + '\n', ServerHandler.respond);
    }

//    @After
//    public void disconnectTest() throws InterruptedException {
//        AliConnection.sendRequest(new ServerMessage(MessageType.Disconnect, Ali, null, null));
//        Thread.sleep(300);
//        assertEquals("Disconnect " + "Ali" + '\n', ServerHandler.respond);
//    }

    @Test
    public void refreshTest() throws InterruptedException, ClassNotFoundException, IOException {

        Message AlitoHamed = new Message(Ali, Hamed, getCurrentTimeUsingCalendar(), "Subject", "Salam. Khobi?");
        Message HamedToAli = new Message(Hamed, Ali, getCurrentTimeUsingCalendar(), "Subject", "Salam. Khobi?");
        AliConnection.sendRequest(new ServerMessage(MessageType.Send, Hamed, Ali, HamedToAli));
        Thread.sleep(300);
        AliConnection.sendRequest(new ServerMessage(MessageType.Reload, Ali, null, null));
        assertTrue(AliConnection.getIn().readObject() instanceof ArrayList);
    }

    @Test
    public void settingTest() throws InterruptedException, IOException, ClassNotFoundException {
        Ali.setFirstName("ALII");
        AliConnection.sendRequest(new ServerMessage(MessageType.Setting, Ali, null, null));
        Thread.sleep(300);
        assertEquals("Ali" + " Changed " + '\n' +
                "time : " + getCurrentTimeUsingCalendar() + '\n', ServerHandler.respond);
    }

    public static String getCurrentTimeUsingCalendar() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH:mm");
        return dateFormat.format(date);
    }
}
