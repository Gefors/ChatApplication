package Boundary;

import javax.swing.*;
import java.awt.*;

public class PictureView extends JFrame {
    private JPanel picturepanel;
    private JLabel picture;

    public PictureView(){
        super("Picture");
        setLocation(0,0);
        setSize(400, 400);
        setContentPane(picturepanel);
        setVisible(true);
        setResizable(true);
        setLocationRelativeTo(null);
        pack();
    }

    public void setPicture(ImageIcon imageicon){
        Image image = imageicon.getImage().getScaledInstance(400,400,Image.SCALE_SMOOTH);
        ImageIcon imageicon2 = new ImageIcon(image);
        this.setSize(imageicon2.getIconWidth(), imageicon2.getIconHeight());
        picture.setIcon(imageicon2);
    }
}
