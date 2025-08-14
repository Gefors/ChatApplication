package Boundary;

import Control.Client;
import Control.LogIn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

public class LOGINGUI2 {
    private JPanel panel;
    private JButton signInButton;
    private JButton registerNewAccountButton;
    private JButton uploadProfilePictureButton;
    private JLabel image;
    private JLabel Username;
    private LogIn logIn;
    private JFrame frame;
    private ImageIcon picture;
    private JTextField usernamefield;
    private JFileChooser fileChooser = new JFileChooser();

    public LOGINGUI2(LogIn logIn) {
        this.logIn = logIn;
        frame = new JFrame("LoginWindow");
        frame.setContentPane(this.panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600,400);
        frame.setVisible(true);

        uploadProfilePictureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int test = fileChooser.showOpenDialog(null);
                if (test == JFileChooser.APPROVE_OPTION){
                    picture = new ImageIcon(String.valueOf(fileChooser.getSelectedFile()));
                    Image icon = picture.getImage();
                    Image pfp = icon.getScaledInstance(200,200,1);
                    ImageIcon pfp2 = new ImageIcon(pfp);
                    image.setIcon(pfp2);
                }
            }
        });

        registerNewAccountButton.addActionListener(e -> {
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    System.out.println("Processing registration...");
                    return logIn.buttonPressed(ButtonType.REGISTERNEWACCOUNTBUTTON);
                }

                @Override
                protected void done() {
                    try {
                        Boolean success = get();
                        System.out.println("Register: " + (success ? "Successful" : "Failed"));
                    } catch (InterruptedException | ExecutionException ex) {
                        ex.printStackTrace();
                    }
                }
            }.execute();
        });

        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        System.out.println("Processing sign in...");
                        return logIn.buttonPressed(ButtonType.SIGNINBUTTON);
                    }

                    @Override
                    protected void done() {
                        try {
                            Boolean success = get();
                            System.out.println("Sign in: " + (success ? "Successful" : "Failed"));
                        } catch (InterruptedException | ExecutionException ex) {
                            ex.printStackTrace();
                        }
                    }
                }.execute();
            }
        });

    }

    public ImageIcon getPicture() {
        return picture;
    }
    public String getUsernamefield() {
        return usernamefield.getText();
    }
}
