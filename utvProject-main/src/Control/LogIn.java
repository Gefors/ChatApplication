package Control;

import Boundary.ButtonType;
import Boundary.LOGINGUI2;
import Entity.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
//A class that handles the login of the user and the creation of a client object.
public class LogIn implements Serializable {
    private LOGINGUI2 loginGui;
    private ArrayList<User> userList = new ArrayList<User>();
    private ArrayList<Client> clientList = new ArrayList<Client>();
    private String userFile = "files/User.dat";
    private String clientFile = "files/Client.dat";
    private UserHandler userHandler;
    private ContactHandler contactHandler;

    //Constructor for the login class
    public LogIn() {
        loginGui = new LOGINGUI2(this);
        readUserFromFile();
        contactHandler = new ContactHandler();
        userHandler = new UserHandler();
    }

    //Method that handles the button presses of the login gui, this includes the sign in and register new account buttons.
    public boolean buttonPressed(ButtonType button) {
        boolean success = false;
        switch (button){
            case SIGNINBUTTON:
                User user = null;
                String name = loginGui.getUsernamefield();
                if (!name.equals("")) {
                    for (User aUser : userList) {
                        if (name.equals(aUser.getName())) {
                            user = aUser;
                            success = true;
                            break;
                        }
                    }
                    if (!success) {
                        System.out.println("User doesn't exist!");
                    } else {
                        Client client = new Client(user, "127.0.0.1", 1234);
                        client.startClient();
                    }
                }
                else {
                    System.out.println("Empty name!");
                }
                break;

            case REGISTERNEWACCOUNTBUTTON:
                name = loginGui.getUsernamefield();
                ImageIcon picture = loginGui.getPicture();
                if(!name.equals("") && picture != null) {
                    success = true;
                    for (User aUser : userList) {
                        if (name.equals(aUser.getName())) {
                            success = false;
                            System.out.println("Username already exists!");
                            break;
                        }
                    }
                    if(success) {
                        picture = scaledImage(picture);
                        user = new User(name, picture);
                        userList.add(user);
                        Client client = new Client(user, "127.0.0.1", 1234);
                        client.startClient();
                        writeUserToFile();
                    }
                }
                else {
                    System.out.println("Something's empty!");
                    success = false;
                }
                break;
        }
        return success;
    }

    //Method that scales the image to a specific size.
    public ImageIcon scaledImage(ImageIcon icon) {
        Image orginal = icon.getImage();
        Image scaledImage = orginal.getScaledInstance(75, 75, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    //Method that writes the users to a file.
    public void writeUserToFile() {
        try (ObjectOutputStream oosUser = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(userFile)));){
            for (int i = 0; i <userList.size() ; i++) {
                oosUser.writeObject(userList.get(i));
            }
            oosUser.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Method that reads the users from a file.
    public void readUserFromFile() {
        try (ObjectInputStream oisUser = new ObjectInputStream(new BufferedInputStream(new FileInputStream(userFile)))) {
            while (true) {
                User user = (User) oisUser.readObject();
                userList.add(user);
            }
        } catch (EOFException e) {
            System.out.println("All users have been read");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Main method that creates a login object and starts the program for a user
    public static void main(String[] args) {
        LogIn logIn = new LogIn();
    }
}
