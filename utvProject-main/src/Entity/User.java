package Entity;

import javax.swing.*;
import java.io.Serializable;
import java.util.Objects;

//A user object that is used to store the user's name and picture.
public class User implements Serializable {
    private String name;
    private ImageIcon picture;

    public User(String name, ImageIcon picture){
        this.name = name;
        this.picture = picture;
    }

    /**
     * Get the hashcode from a hashmap from the object Object
     * @return
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }


    //Check if the object is equal to the user object
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof User) {
            return name.equals(((User)obj).getName());
        }
        return false;
    }

    //Get the name of the user
    public String getName() {
        return name;
    }
    //Set the name of the user
    public void setName(String name) {
        this.name = name;
    }
    //Get the picture of the user
    public ImageIcon getPicture() {
        return picture;
    }
    //Set the picture of the user
    public void setPicture(ImageIcon picture) {
        this.picture = picture;
    }
}
