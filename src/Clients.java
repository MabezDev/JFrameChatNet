

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Scott on 17/08/2015.
 */
public class Clients extends JFrame{

    private String USER_NAME;
    private String IP_ADDRESS;
    private int PORT;

    private JPanel MainPanel;
    private JButton sendButton;
    private JTextField messageText;
    private JTextArea messageBox;
    private JScrollPane scroll;
    private String messageToSend;

    public Clients(String US,String IP, int PORT) {
        this.USER_NAME = US;
        this.IP_ADDRESS = IP;
        this.PORT = PORT;


        setVisible(true);
        setResizable(false);
        setSize(800, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        MainPanel = new JPanel();
        MainPanel.setVisible(true);
        MainPanel.setSize(800, 600);
        this.setContentPane(MainPanel);
        MainPanel.setLayout(null);


        sendButton = new JButton("Send");
        sendButton.setBounds(650,500,100,50);

        /*
        Message Box goes inside the scroll pane container to allow for a history of messages to build up
         */

        messageBox = new JTextArea();
        messageBox.setEditable(false);
        scroll = new JScrollPane(messageBox);
        scroll.setBounds(0,0,800,500);

        /*
        MessageText Is a text field the user uses to write messages
         */
        messageText = new JTextField();
        messageText.setBounds(50,500,300,50);
        messageText.grabFocus();

        /*
        Add the components to the main panel
         */

        MainPanel.add(sendButton);

        MainPanel.add(scroll);

        MainPanel.add(messageText);


        /*
        add listeners for the button and the text field (enter always sens message, maybe add tick box to have that on or not)
         */

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
                if(e.getKeyCode()== KeyEvent.VK_ENTER){
                    if(messageText.getText().toString()!="\n\r"){///need to stop enter spam (blank messages)
                        System.out.println("TestBegin"+messageText.getText().toString()+"TestEnd");
                        sendButton.doClick();
                    }

                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

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

    }






}
