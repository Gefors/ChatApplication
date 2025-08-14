package Entity;


import java.io.Serializable;
//A message object that is a parent class for all messages and contains the sender of the message.
public abstract class Message implements Serializable {
    private User sender;
    //Constructor for the message
    public Message(User sender){
        this.sender = sender;
    }
    //Get the sender of the message
    public User getSender() {
        return sender;
    }
}
