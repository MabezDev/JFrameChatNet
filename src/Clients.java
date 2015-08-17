import sun.awt.VariableGridLayout;

import javax.swing.*;
import javax.swing.text.html.MinimalHTMLWriter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Scott on 17/08/2015.
 */
public class Clients extends JFrame{
    private JPanel MainPanel;
    private JButton sendButton;
    private JTextField messageText;
    private JTextArea messageBox;
    private String messageToSend;

    public Clients() {
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


        messageBox = new JTextArea();
        messageBox.setBounds(0,0,800,450);
        messageBox.setEditable(false);

        messageText = new JTextField();
        messageText.setBounds(50,500,300,50);
        messageText.grabFocus();

        MainPanel.add(sendButton);

        MainPanel.add(messageBox);

        MainPanel.add(messageText);




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

    private void setMessageBox(String text){
        messageBox.append(text + "\n\r");

    }






}
