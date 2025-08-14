package Entity;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
//A message object that is a child class of the message class and used to send messages to between users/clients.
public class ConversationMessage extends Message{

    private String message;
    private ImageIcon picture;
    private LocalDateTime timeReceivedServer;
    private LocalDateTime timeSentFromServer;
    private ArrayList<User> receivers;
    //Constructor for the conversation message
    public ConversationMessage(String message, ImageIcon picture, User sender) {
        super(sender);
        this.message = message;
        this.picture = picture;
        receivers = new ArrayList<>();
    }
    //Get the message of the conversation message
    public String getMessage(){ return message; }
    //Set the message of the conversation message
    public void setMessage(String message) {
        this.message = message;
    }
    //Get the picture of the conversation message
    public ImageIcon getPicture(){ return picture; }
    //Set the picture of the conversation message
    public void addReceiver(User receiver){
        receivers.add(receiver);
    }
    //Get the receivers of the conversation message
    public ArrayList<User> getReceivers() {
        return receivers;
    }
    //Set the time the message was sent
    public LocalDateTime getTimeReceivedServer() {return timeReceivedServer;}
    //Get the time the message was sent
    public void setTimeReceivedServer(LocalDateTime timeReceived){
        this.timeReceivedServer = timeReceived;
    }
    //Set the time the message was received
    public LocalDateTime getTimeSentFromServer() {
        return timeSentFromServer;
    }
    //Get the time the message was received
    public void setTimeSentFromServer(LocalDateTime timeSent){
        this.timeSentFromServer = timeSent;
    }
}