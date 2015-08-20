import javax.swing.*;
import javax.swing.tree.ExpandVetoException;
import java.awt.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

/**
 * Created by Scott on 17/08/2015.
 */
public class Client extends JFrame  {

    private String USER_NAME;
    private String IP_ADDRESS;
    private int ID;
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
    private final static int MAX_ATTEMPTS = 5;

    private Thread starter;
    private boolean isConnected;

    private String messageToSend;

    public Client(String UN, String IP, int port)  {
        super("IRC Chat");

        this.USER_NAME = UN;
        this.IP_ADDRESS = IP;
        this.PORT = port;
        ID = 0;




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

                    while(true) {
                        listen();
                    }

            }
        });

        /*
        Establish Connection with server
         */
        starter = new Thread(new Runnable() {
            @Override
            public void run() {
                startConnection();
            }
        });
        starter.start();



        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                String disconnect = "/d/ " + USER_NAME + " /ID/ " + ID + " /e/";
                send(disconnect);
                listen.interrupt();
                try {
                    Thread.sleep(2000);
                }catch (Exception e1){
                    e1.printStackTrace();
                }
                //running = false;
                //add stream closing func
            }
        });

    }



    private void startConnection(){
        try {
            attemptConnection();
            setUpStreams();
            //send a packet to the server to tell it out details
            send("/c/ "+USER_NAME + " /ID/ " + ID +" /e/");
        }catch (IOException e){
            e.printStackTrace();
        }
        listen.start();
        starter.interrupt();

        //need to add timout and recon but this is throwing a weird threadexception
        /*int numOfAttempts = 0;

        while(numOfAttempts < MAX_ATTEMPTS) {
            isConnected = attemptConnection();
            if(isConnected){
                //open IO streams to receive and send data
                try {
                    setUpStreams();
                }catch (IOException e){
                    e.printStackTrace();
                }
                //start Listening thread;
                listen.start();
                //kill starter once connected
                starter.interrupt();
            } else {
                numOfAttempts++;
                System.out.println("Didnt Connect");
                //
            }
        }*/

    }

    private boolean attemptConnection(){
        try {
            clientSocket = new Socket(IP_ADDRESS, PORT);
            if(clientSocket.isConnected()){
                //show that user is connected
                messageBox.append("Connected as :" + USER_NAME + " At: "+IP_ADDRESS+":"+PORT+ "\n\r");
                return true;
            }
            else{
                messageBox.append("Connection to server failed.");
                return false;
            }
        }catch(IOException e){
            System.out.println("In socket catch");
            e.printStackTrace();
            return false;
        }
    }


    private void listen(){
        try {
            String built = "";
            built += (char) inFromServer.read();
            while (!built.contains("/e/")) {//handles the end of the message
                built += (char) inFromServer.read();
                System.out.println("Built Progress: " + built);
            }

            if(ID==0){
                String clientToDCString = built.split("/ID/|/e/")[1].trim();
                int clientID = Integer.parseInt(clientToDCString);
                this.ID = clientID;
            }
            System.out.println("Found end of message: " + built);
            String finishedData = built.split("/m/|/e/")[1];
            messageBox.append(finishedData + "\n\r");
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Listen function  - Client Disconnect.");
        }
    }

    private void dealWithText(){
        String messageOut = ("/m/ "+USER_NAME+": "+messageText.getText()+ " /ID/ " + ID +" /e/");

        send(messageOut);
        messageText.setText("");

    }

    /*
    this is where the text would be sent to the send class to actually go to the server
     */

    private void send(String text){
        final String messageOut = text;
        System.out.println("Attempting to send: " + messageOut);
        Thread Send = new Thread("SendingThread"){
            public  void run(){
                try {
                    //Send sendIt = new Send(clientSocket,messageToSend);

                    outToServer.writeBytes(messageOut);
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
