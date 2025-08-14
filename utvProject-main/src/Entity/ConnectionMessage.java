package Entity;


import Entity.Message;

import java.util.ArrayList;
//A message object that is a child class of the message class and used to send messages when a user connects.
public class ConnectionMessage extends Message {
    //Constructor for the connection message
    public ConnectionMessage( User sender){
        super(sender);
    }

}