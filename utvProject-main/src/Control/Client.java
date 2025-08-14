package Control;

import Boundary.ButtonType;
import Boundary.TestGui;
import Entity.Message;
import Entity.*;
import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
//A client object that is used to connect to the server and send messages to other clients.
public class Client extends Thread {
    private TestGui gui;
    private Socket socket;
    private User user;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Logger logger = new Logger();
    private ArrayList<ImageIcon> pictures = new ArrayList<>();
    private final String fileName = "files/ServerLog.txt";

    //Constructor for the client with user, ip and port
    public Client(User user, String ip, int port) {
        this.user = user;
        gui = new TestGui(this);
        try {
            socket = new Socket(ip, port);
            oos = new ObjectOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Method that starts the client
    public void startClient() {
        start();
        writeMessage(new ConnectionMessage(user));
    }

    //Method that handles the button presses of the gui and checks if the button is close so it can send a disconnect message and close.
    public ButtonType choices(ButtonType button) throws IOException {
        if (Objects.requireNonNull(button) == ButtonType.CLOSE) {
            writeMessage(new DisconnectMessage(user));
        }
        return button;
    }

    //Method that gets the image from the arraylist of images
    public ImageIcon presentImage(int index){
        return pictures.get(index);
    }

    //Method that creates a message either with just text and user or with text, image and user. Lastly it gets the recivers and sends.
    public void createMessage(String text, ImageIcon image){
        ConversationMessage message;
        if(image == null){
            message = new ConversationMessage(text,null, user);
        }
        else {
            message = new ConversationMessage(text,image, user);
        }
        for (User user : gui.getSelectedUsers()) {
            message.addReceiver(user);
        }
        String names = "";
        for (User u : message.getReceivers()) {
            names += u.getName() + ", ";
        }
        if(!message.getReceivers().isEmpty()) {
            logger.writeToFile(fileName, "Sending message from " + user.getName() + " to: " + names);
            writeMessage(message);
        }
    }

    //Method that creates a social message with the friends of the user and sends it.
    public void createSocialMessage(ArrayList<User> friends){
        if(!friends.isEmpty()) {
            SocialMessage socialMessage = new SocialMessage(friends,user);
            writeMessage(socialMessage);
        }
    }

    //Method that writes a message to the output stream.
    public void writeMessage(Message message){
        try  {
            if (message != null) {
                oos.writeObject(message);
                oos.reset();
                oos.flush();
            }
        }catch(IOException e){}
    }

    //A method that is used to write to the logs from the server in different instances.
    public void writeToLogs(Message message){
        if(message instanceof ConversationMessage) {
            ConversationMessage logMessage = (ConversationMessage) message;

            logger.writeToFile(fileName, this.user.getName() + " received message from: " + logMessage.getSender().getName());
        }
        else if (message instanceof SocialMessage) {
            SocialMessage logMessage = (SocialMessage) message;
            logger.writeToFile(fileName, "Updated contacts for " + logMessage.getSender().getName());
        }
    }

    //Run method that read data from server and then processes the message.
    @Override
    public void run() {
        try {
            while (true) {
                ois = new ObjectInputStream(socket.getInputStream());
                try {
                    Message message = (Message) ois.readObject();
                    processMessage(message);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Method that processes the incoming message.
    private void processMessage(Message message) {
        if (message instanceof ConversationMessage) {
            ConversationMessage messageReceived = (ConversationMessage) message;
            String string = String.format("%s: %s ", messageReceived.getSender().getName(), messageReceived.getMessage());
            if (messageReceived.getPicture() != null) {
                string += "(Picture)";
                gui.addMessage(string);
                pictures.add(messageReceived.getPicture());
            } else {
                pictures.add(null);
                gui.addMessage(string);
            }
            writeToLogs(messageReceived);
        }
        else if (message instanceof SocialMessage) {
            ArrayList<User> tempArray = ((SocialMessage) message).getContacts();
            tempArray.remove(user);
            gui.updateOnlineList(tempArray);
        }
    }

    //Method that gets the user of the client.
    public User getUser() {
        return user;
    }
}



