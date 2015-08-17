import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

/**
 * Created by Scott on 17/08/2015.
 */
public class Client extends JFrame {

    private String USER_NAME;
    private String IP_ADDRESS;
    private int PORT;

    private Thread listen;


    private JPanel rootPanel;
    private JButton sendButton;
    private JTextArea messageBox;
    private JTextField messageText;
    private JScrollPane scroll;

    private String messageToSend;

    public Client(String UN, String IP, int PORT) {
        super("IRC Chat");

        this.USER_NAME = UN;
        this.IP_ADDRESS = IP;
        this.PORT = PORT;


        rootPanel.setLayout(rootPanel.getLayout());
        rootPanel.setBounds(400, 400, 800, 600);
        this.setContentPane(rootPanel);


        setSize(800, 600);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);


        messageBox.setEditable(false);


        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dealWithText();
            }
        });

        messageText.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (messageText.getText().toString() != "\n\r") {///need to stop enter spam (blank messages)
                        System.out.println("TestBegin" + messageText.getText().toString() + "TestEnd");
                        sendButton.doClick();
                    }

                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        final Recieve recClass = new Recieve(IP_ADDRESS, PORT);
        System.out.println("Binding Reception on PORT: " + PORT);

        listen = new Thread(new Runnable() {
            @Override
            public void run() {

                System.out.println("Thread Starting");
                String message = "";
                try {
                    while (message == "") {
                        message = recClass.getDataFromServer();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void dealWithText(){
        messageToSend = messageText.getText();
        System.out.println("Attempting to send: " + messageToSend);
        setMessageBox(messageToSend);
        messageText.setText("");

    }

    /*
    this is where the text would be sent to the send class to actually go to the server
     */

    private void setMessageBox(String text){
        messageBox.append(USER_NAME+": " + text + "\n\r");
        Thread Send = new Thread("SendingThread"){
            public  void run(){
                try {
                    Send sendIt = new Send(messageToSend, IP_ADDRESS, PORT);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

        };
        Send.start();

    }






}
