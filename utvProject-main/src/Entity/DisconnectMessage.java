package Entity;
//A message object that is a child class of the message class and used to send message when a user disconnects.
public class DisconnectMessage extends Message {
    public DisconnectMessage(User sender){
        super(sender);
    }
}
