package Entity;

import java.net.Socket;
import java.util.*;

//A class that is used to handle the users and give information about the users.
public class UserHandler {

    private HashMap<String, SocketHandler> allUsers;

    //Constructor for the user handler.
    public UserHandler() {
        allUsers = new HashMap<String, SocketHandler>();
    }
    //Put the user and socket into the hashmap.
    public synchronized void put(User user, Socket socket){
        allUsers.put(user.getName(),new SocketHandler(user, socket));
        notifyAll();
    }
    public synchronized Socket get(User user) throws InterruptedException{
        while(allUsers.isEmpty()){
            wait();
        }
        for(String theUser : allUsers.keySet()){
            if(user.getName().equals(theUser)){
                return allUsers.get(theUser).getSocket();
            }
        }
        return null;

    }

    public synchronized ArrayList<User> getActiveUsers(User currentUser, HashMap<String, ArrayList<User>> allContacts) {
        ArrayList<User> activeUsers = new ArrayList<>();

        for (SocketHandler handler : allUsers.values()) {
            if (handler.getSocket() != null && !handler.getSocket().isClosed()) {
                User activeUser = handler.getUser();
                if (!containsUser(activeUsers, activeUser.getName())) {
                    activeUsers.add(activeUser);
                }
            }
        }

        if (allContacts.containsKey(currentUser.getName())) {
            for (User contact : allContacts.get(currentUser.getName())) {
                if (!containsUser(activeUsers, contact.getName())) {
                    activeUsers.add(contact);
                }
            }
        }

        return activeUsers;
    }
    
    private boolean containsUser(ArrayList<User> users, String userName) {
        for (User user : users) {
            if (user.getName().equals(userName)) {
                return true;
            }
        }
        return false;
    }


    public synchronized void remove(User user){
        while(allUsers.isEmpty()){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if(allUsers.containsKey(user.getName())){
            allUsers.replace(user.getName(),new SocketHandler(user,null));
        }
    }
}
