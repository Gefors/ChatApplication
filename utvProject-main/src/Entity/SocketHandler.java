package Entity;

import java.net.Socket;

//A class that is used to handle the socket and user object.
public class SocketHandler {
    User user;
    Socket socket;
    //Constructor for the socket handler.
    public SocketHandler(User user, Socket socket) {
        this.user = user;
        this.socket = socket;
    }

    //Get the user object.
    public User getUser() {
        return user;
    }
    //Get the socket object.
    public Socket getSocket() {
        return socket;
    }
}
