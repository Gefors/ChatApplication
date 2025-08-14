package Entity;

import java.util.ArrayList;
//A message object that is a child class of the message class and ussed to send messages to contacts.
public class SocialMessage extends Message {

    private ArrayList<User> contacts;
    //Constructor for the social message
    public SocialMessage(ArrayList<User> contacts) {
        super(new User("Filler", null));
        this.contacts = contacts;
    }

    //
    public SocialMessage(ArrayList<User> contacts, User sender){
        super(sender);
        this.contacts = contacts;
    }

    //Get the contacts of the message.
    public ArrayList<User> getContacts() {
        return contacts;
    }
}