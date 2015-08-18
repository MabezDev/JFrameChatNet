import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Scott on 17/08/2015.
 */
public class Client extends JFrame  {

    private String USER_NAME;
    private String IP_ADDRESS;
    private int PORT;
    private Socket clientSocket;

    private Thread listen;


    private JPanel rootPanel;
    private JButton sendButton;
    private JTextArea messageBox;
    private JTextField messageText;
    private JScrollPane scroll;
    private DataOutputStream outToServer;
    private InputStreamReader inFromServer;

    private String messageToSend;

    public Client(String UN, String IP, int port)  {
        super("IRC Chat");

        this.USER_NAME = UN;
        this.IP_ADDRESS = IP;
        this.PORT = port;




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
                        sendButton.doClick();
                    }

                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        listen = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        listen();
                    }
                    } catch (IOException e) {
                    e.printStackTrace();


                }
            }
        });

        /*
        Establish Connection with server
         */

        try {
            clientSocket = new Socket(IP_ADDRESS, PORT);
            if(clientSocket.isConnected()){
                messageBox.append("Connected as :"+USER_NAME+" At: "+IP_ADDRESS+":"+PORT+ "\n\r");
                setUpStreams();
                //start Listening thread;
                listen.start();
            }
            else{
                messageBox.append("Connection to server failed");
            }
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    private void listen() throws IOException{
        String built ="";
        built += (char)inFromServer.read();
        while(!built.contains("/e/")){//handles the end of the message
            built += (char)inFromServer.read();
            System.out.println("Built Progress: "+built);
        }
        //in.close();// not needed as we want to keep the connection open for server replies
        System.out.println("Found end of message: "+ built);
        messageBox.append(built + "\n\r");
    }

    private void dealWithText(){
        messageToSend = messageText.getText();

        setMessageBox(messageToSend);
        messageText.setText("");

    }

    /*
    this is where the text would be sent to the send class to actually go to the server
     */

    private void setMessageBox(String text){
        messageToSend = USER_NAME+": " + text + " /e/";
        //messageBox.append(USER_NAME+": " + text + "/e/"); //"\n\r"
        System.out.println("Attempting to send: " + messageToSend);
        Thread Send = new Thread("SendingThread"){
            public  void run(){
                try {
                    //Send sendIt = new Send(clientSocket,messageToSend);

                    outToServer.writeBytes(messageToSend);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

        };
        Send.start();

    }

    private void setUpStreams() throws IOException{
        outToServer = new DataOutputStream(clientSocket.getOutputStream());
        outToServer.flush();
        inFromServer = new  InputStreamReader(clientSocket.getInputStream());
    }






}
