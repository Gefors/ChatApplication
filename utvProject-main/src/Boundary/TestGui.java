package Boundary;

import Control.Client;
import Entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class TestGui extends JFrame implements Serializable {
    private JPanel Container;
    private JTextField textField1;
    private JButton sendButton;
    private JButton disconnectButton;
    private JButton addButton;
    private JList list1;
    private JPanel MessageField;
    private JList messages;
    private JButton browseImagesButton;
    private JLabel Test;
    private JPanel Testbild;
    private JLabel profilePicture;
    private JLabel online;
    private JLabel contactList;
    private JButton showPictureButton;
    private Client client;
    private JFileChooser fileChooser = new JFileChooser();
    private ImageIcon picture;
    private JFrame frame;
    private int index;
    private ArrayList<String> messagesArray = new ArrayList<>();
    private User[] onlineUsers;
    private PictureView pictureView;


    public TestGui(Client client) {
        this.client = client;
        profilePicture.setIcon(client.getUser().getPicture());
        frame = new JFrame();
        frame.setTitle(client.getUser().getName());
        frame.setContentPane(this.Container);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setVisible(true);

        showPictureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(pictureView != null){
                    pictureView.dispose();
                }
                if(messages.getSelectedIndex() >= 0 && client.presentImage(messages.getSelectedIndex()) != null) {
                    pictureView = new PictureView();
                    pictureView.setPicture(client.presentImage(messages.getSelectedIndex()));
                }
            }
        });

        sendButton.addActionListener(e -> {
            if(getSelectedUsers().length > 0) {
                client.createMessage(textField1.getText(), picture);
                picture = null;
                browseImagesButton.setText("Select picture");
                Test.setIcon(null);
                textField1.setText("");
            }
        });


        disconnectButton.addActionListener(e -> {
            try {
                client.choices(ButtonType.CLOSE);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            frame.dispose();

        });
        addButton.addActionListener(e ->  {

            User[] selectedUsers = getSelectedUsers();
            ArrayList<User> tempArray = new ArrayList<>();
            for (int i = 0; i < selectedUsers.length; i++) {
                tempArray.add(selectedUsers[i]);
            }
            client.createSocialMessage(tempArray);

        });

        browseImagesButton.addActionListener(e -> {
            int test = fileChooser.showOpenDialog(null);
            if (test == JFileChooser.APPROVE_OPTION) {
                picture = new ImageIcon(String.valueOf(fileChooser.getSelectedFile()));
                Image icon = picture.getImage();
                Image pfp = icon.getScaledInstance(200, 200, 1);
                ImageIcon pfp2 = new ImageIcon(pfp);
                Test.setIcon(pfp2);
                browseImagesButton.setText("Change picture");
            }
        });


    }


    public void updateOnlineList(ArrayList<User> list) {
        onlineUsers = new User[list.size()];
        for(int i = 0; i < onlineUsers.length; i++) {
            onlineUsers[i] = list.get(i);
        }
        SwingUtilities.invokeLater(() -> {
            String[] namesOnlineList = new String[onlineUsers.length];
            for(int i = 0; i <namesOnlineList.length; i++) {
                namesOnlineList[i] = onlineUsers[i].getName();
            }
            list1.setListData(namesOnlineList);
        });
    }


    public int addMessage(String message) {
        messagesArray.add(message);
        populateList(infoArray());
        index++;

        return index;
    }

    public void populateList(String[] message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                messages.setListData(message);
            }
        });
    }

    private String[] infoArray() {
        String[] array = new String[messagesArray.size()];

        for (int i = 0; i < array.length; i++) {
            array[i] = messagesArray.get(i);
        }
        return array;
    }


    public User[] getSelectedUsers() {
        int[] selectedIndices = list1.getSelectedIndices();
        User[] selectedUsers = new User[selectedIndices.length];
        for (int i = 0; i < selectedIndices.length; i++) {
            selectedUsers[i] = onlineUsers[selectedIndices[i]];
        }
        return selectedUsers;
    }

}



