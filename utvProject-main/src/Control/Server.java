package Control;

import Boundary.GuiServer;
import Entity.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
//The server class that is used to handle the server side of the chat application.
public class Server extends Thread {
    private GuiServer gui;
    private Logger logger = new Logger();
    private final String fileName = "files/ServerLog.txt";
    private ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private ContactHandler contactHandler = new ContactHandler();
    private HashMap<String, ArrayList<ConversationMessage>> unsent = new HashMap<>();
    private UserHandler userHandler = new UserHandler();
    private int port;

    //Constructor for the server class
    public Server(int port) {
        gui = new GuiServer();
        this.port = port;
        start();
    }
    //The run method that is used to start the server and handle the clients.
    @Override
    public void run() {
        Socket socket = null;
        System.out.println("Server Startad");
        logger.writeToFile(fileName,"Server have started");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    socket = serverSocket.accept();
                    ClientHandler temp = new ClientHandler(socket);
                    clientHandlers.add(temp);
                } catch (IOException e) {
                    e.printStackTrace();
                    if (socket != null) {
                        socket.close();
                        logger.writeToFile(fileName, "Server have been closed");


                    }
                }
            }
        } catch (IOException e) {
            logger.writeToFile(fileName,"Server could not start");
            e.printStackTrace();
        }
    }

    //A method that is used to write to the logs from the server in different instances.
    public void writeToLogs(Message message){
        if(message instanceof ConversationMessage) {
            ConversationMessage logMessage = (ConversationMessage) message;

            String log = String.format("Server received message from %s that will be sent to: ",
                    logMessage.getSender().getName());

            for (User u : logMessage.getReceivers()) {
                log += u.getName() + ", ";
            }

            logger.writeToFile(fileName, log);
        }
        else if(message instanceof ConnectionMessage){
            ConnectionMessage logMessage = (ConnectionMessage) message;
            logger.writeToFile(fileName, logMessage.getSender().getName() + " logged in");
        }
        else if (message instanceof SocialMessage) {
            SocialMessage logMessage = (SocialMessage) message;
            logger.writeToFile(fileName, "Updated contacts for " + logMessage.getSender().getName());
        }
        else if (message instanceof DisconnectMessage) {
            DisconnectMessage logMessage = (DisconnectMessage) message;
            logger.writeToFile(fileName, logMessage.getSender().getName() + " logged out");
        }
    }

    //Method that is used to update the clients.
    public void updateClients() {
        for(ClientHandler client : clientHandlers) {
            contactHandler.loadContacts();
            HashMap<String, ArrayList<User>> temp = contactHandler.getContacts();
            ArrayList<User> currentOnlineUsers = userHandler.getActiveUsers(client.getCurrentUser(), temp);
            SocialMessage socialMessage = new SocialMessage(currentOnlineUsers);
            client.sendMessage.notifyAllClients(socialMessage);

        }
    }

    //A class that is used to handle the clients and the messages that are sent to the clients.
    private class ClientHandler {
        private Socket socket;
        private User currentUser;
        private Listener listener;
        private SendMessage sendMessage;
        //Constructor for the client handler that starts the listener and the send message threads.
        public ClientHandler(Socket socket) {
            System.out.println("Ansluten klient");
            this.socket = socket;
            sendMessage = new SendMessage(this);
            sendMessage.start();
            listener = new Listener(this);
            listener.start();

        }
        //A method that is used to get the current user.
        public User getCurrentUser() {
            return currentUser;
        }

        //Method to handle the messages that are sent to a user that is offline. The messages are stored in a hashmap and sent when the user logs in.
        public void put(Message message){
            ConversationMessage conversationMessage = (ConversationMessage) message;
            ArrayList<User> allReceivers = conversationMessage.getReceivers();

            for(User user : allReceivers) {
                if(!unsent.containsKey(user.getName())) {
                    unsent.put(user.getName(), new ArrayList<>());
                }
                ArrayList<ConversationMessage> messagesForUser = unsent.get(user.getName());
                if (!messagesForUser.contains(conversationMessage)) {
                    messagesForUser.add(conversationMessage);
                }
            }
        }

        //Method that is used to get the messages that are stored in the hashmap and send them to the user.
        public void get(User user) throws InterruptedException {
            if(unsent.containsKey(user.getName())) {
                for(ConversationMessage message : unsent.get(user.getName())) {
                    sendMessage.notifySpecificClient(message, userHandler.get(user));
                }
            }
            unsent.put(user.getName(), new ArrayList<>());
        }

        //A class that is used to send messages to the clients.
        private class SendMessage extends Thread {
            private ClientHandler clientHandler;
            private ObjectOutputStream oos;
            //Constructor for the send message class.
            public SendMessage(ClientHandler clientHandler) {
                this.clientHandler = clientHandler;

            }

            //Method that sends a message object to a specific client.
            public void notifySpecificClient(Message message, Socket sendSocket) {
                try {
                    if(message instanceof ConversationMessage) {
                        ((ConversationMessage) message).setTimeSentFromServer(LocalDateTime.now());
                    }
                    ObjectOutputStream oos = new ObjectOutputStream(sendSocket.getOutputStream());
                    oos.writeObject(message);
                    oos.flush();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //Method that sends a message object to all clients.
            public void notifyAllClients(Message message) {
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
        //A class that is used to listen for messages from the clients.
        private class Listener extends Thread {
            private ClientHandler clientHandler;

            //Constructor for the listener class.
            public Listener(ClientHandler clientHandler) {
                this.clientHandler = clientHandler;
            }

            //Run method that listens continuously for messages from the clients.
            @Override
            public void run() {

                try  {
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    while (true) {
                        try {
                            Message message = (Message) ois.readObject();
                            if(message != null){
                                if(message instanceof ConversationMessage){
                                    ConversationMessage conversationMessage = (ConversationMessage) message;
                                    conversationMessage.setTimeReceivedServer(LocalDateTime.now());
                                    writeToLogs(conversationMessage);
                                    if (conversationMessage.getPicture() == null) {
                                    }
                                    ArrayList<User> receivers = ((ConversationMessage) message).getReceivers();
                                    for(User u : receivers){
                                        try {
                                            Socket sendSocket = userHandler.get(u);
                                            if (sendSocket != null) {
                                                sendMessage.notifySpecificClient(conversationMessage, sendSocket);
                                                logger.writeToFile(fileName, "Server sending message from " + message.getSender().getName() + " to " + u.getName());
                                            } else {

                                                put(message);
                                            }
                                        }catch (InterruptedException e){e.printStackTrace();}
                                    }
                                }
                                else if(message instanceof ConnectionMessage){
                                    userHandler.put(message.getSender(),socket);
                                    currentUser = message.getSender();
                                    updateClients();
                                    ConnectionMessage connectionMessage = (ConnectionMessage) message;
                                    writeToLogs(connectionMessage);
                                    try {
                                        get(currentUser);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                else if(message instanceof DisconnectMessage){
                                    System.out.println("Disconnecting from server");
                                    clientHandlers.remove(this);
                                    userHandler.remove(currentUser);
                                    updateClients();
                                    DisconnectMessage disconnectMessage = (DisconnectMessage) message;
                                    writeToLogs(disconnectMessage);
                                }
                                else if(message instanceof SocialMessage){
                                    SocialMessage socialMessage = (SocialMessage) message;
                                    boolean check = contactHandler.addContact(socialMessage.getSender(), socialMessage.getContacts());
                                    updateClients();
                                    if(check) {
                                        writeToLogs(socialMessage);
                                    }
                                }
                            }
                        }catch(ClassNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    clientHandlers.remove(this);
                    userHandler.remove(currentUser);
                    updateClients();
                }
            }

        }
    }

    public static void main(String[] args) {
        Server server = new Server(1234);
    }

}





