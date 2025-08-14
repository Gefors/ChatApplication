package Entity;

import java.io.*;
import java.util.*;
//A class that is used to handle the contacts of the user.
public class ContactHandler implements Serializable {

    private HashMap<String, ArrayList<User>> contacts;
    private String directory = "files/";
    //A method that is used to add contacts to the user.
    public synchronized boolean addContact(User user, ArrayList<User> newContacts) {
        ArrayList<User> currentContacts = contacts.get(user.getName());
        if (currentContacts == null) {
            currentContacts = new ArrayList<>();
        }
        int sizeBefore = currentContacts.size();

        for (User newContact : newContacts) {
            boolean alreadyExists = false;

            for (User existingUser : currentContacts) {
                if (existingUser.getName().equals(newContact.getName())) {
                    alreadyExists = true;
                    break;
                }
            }


            if (!alreadyExists) {
                currentContacts.add(newContact);
            }
        }
        int sizeAfter = currentContacts.size();

        contacts.put(user.getName(), currentContacts);
        saveContacts();
        return sizeAfter != sizeBefore;
    }

    //A method that is used to save the contacts in a dat file
    public synchronized void saveContacts() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(directory + "Contacts.dat"));
            oos.writeObject(contacts);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //A method that is used to load the contacts from a dat file.
    public synchronized void loadContacts() {
        File file = new File(directory + "Contacts.dat");
        if (file.length() == 0) {
            contacts = new HashMap<>();
            System.out.println("Contact file is empty.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            contacts = (HashMap<String, ArrayList<User>>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            contacts = new HashMap<>();
            e.printStackTrace();
        }
    }

    //A method that is used to get the contacts of the user.
    public HashMap<String, ArrayList<User>> getContacts() {
        return contacts;
    }
}
