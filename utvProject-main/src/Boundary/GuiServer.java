package Boundary;

import Entity.Logger;
import Entity.Message;

import javax.sound.midi.SysexMessage;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GuiServer {
    private JPanel panel1;
    private JTextField dateInput1;
    private JTextField dateInput2;
    private JButton getLog;
    private JPanel showLog;
    private JTextArea textArea1;
    private JButton cleanLogButton;
    private JLabel fromDate;
    private JLabel toDate;
    private Logger logger;

    private Date dateFormat1;
    private Date dateFormat2;

    private ArrayList<String> logs;


    public GuiServer(){
        JFrame frame = new JFrame("Server log");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setSize(1200,600);

        logger = new Logger();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e){
                    System.out.println("System is shutting down");
                    logger.writeToFile("files/ServerLog.txt","Server is shutting down");
                    System.exit(0);
                }
        });

        getLog.addActionListener(e -> {
            System.out.println("Get log");

            logs = new ArrayList<>();

            try {
                dateFormat1 = simpleDateFormat.parse(dateInput1.getText());
                dateFormat2  = simpleDateFormat.parse(dateInput2.getText());
            } catch (ParseException pe) {
                throw new RuntimeException(pe);
            }
            logs = logger.getLogBetweenPeriod("files/ServerLog.txt",dateFormat1,dateFormat2);

            System.out.println(dateInput1.getText());
            for (int i = 0; i < logs.size(); i++) {
                textArea1.append("\n"+logs.get(i));
            }

        });
        cleanLogButton.addActionListener(e->{
            System.out.println("Remove all log");
            textArea1.setText("");
        });

    }

    public static void main(String[] args) {
        GuiServer guiServer = new GuiServer();


    }
}
