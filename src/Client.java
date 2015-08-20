import server.Server;

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
    private String lastMessage;

    private Thread starter;
    private boolean isConnected;

    private String messageToSend;

    public Client(String UN, String IP, int port)  {
        super("IRC Chat");
        lastMessage ="";
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


        /*
        Establish Connection with server
         */

        starter = new Thread("Starter-Thread"){
            public void run() {
                startConnection();
                //retryOrTimeout();
            }
        };
        starter.start();




        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                String disconnect = "/d/ " + USER_NAME + " /ID/ " + ID + " /e/";
                send(disconnect);
                lastMessage = "/d/";


                //running = false;
                //add stream closing func
            }
        });



    }
    private void buildListenThread() {
        listen = new Thread("Listen Thread") {
            public void run() {
                isConnected = true;
                listen();

            }
        };
        listen.start();
    }

    private void closeConnection(){
        try {
            //listen.interrupt();
            listen = null;
            inFromServer.close();
            outToServer.close();
            clientSocket.close();
            isConnected = false;
        }catch (IOException e){

        }
    }



    private void startConnection(){
        try {
            attemptConnection();
            setUpStreams();
            //send a packet to the server to tell it out details
            //send("/c/ "+USER_NAME + " /ID/ " + ID +" /e/");
        }catch (IOException e){
            e.printStackTrace();
        }
        buildListenThread();
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
                //starter.interrupt();
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
            e.printStackTrace();
            return false;
        }
    }


    private void listen(){
        while(isConnected) {
            try {
                String built = "";
                built += (char) inFromServer.read();
                while (!built.contains("/e/")) {//handles the end of the message
                    if(inFromServer.ready()) {
                        built += (char) inFromServer.read();
                    }else{
                        closeConnection();
                    }

                }

                if(built.startsWith("/ID/")){
                    String ID = built.split("/ID/|/e/")[1].trim();
                    int clientID = Integer.parseInt(ID);
                    System.out.println("Setting ID to: "+clientID);
                    this.ID = clientID;
                }
                if(built.startsWith("/m/")){
                    System.out.println("Found end of message: " + built);
                    String finishedData = built.split("/m/|/ID/")[1];
                    messageBox.append(finishedData + "\n\r");
                }
                if(lastMessage.startsWith("/d/")){
                    try {
                        Thread.sleep(2000);
                    }catch (Exception e){

                    }
                    closeConnection();
                    this.dispose();
                }


            } catch (IOException e) {
                System.out.println("Disconnected from server.");
                closeConnection();
                if(!lastMessage.startsWith("/d/")) {// add a clause for banned?
                    startConnection();
                }
            }
        }
    }

    private void retryOrTimeout(){
        int numOfTries = 0;
        while(numOfTries<MAX_ATTEMPTS){
            if(attemptConnection()){
                System.out.println("Re established connection to Server");

                try {
                    setUpStreams();
                }catch (IOException e){

                }
                buildListenThread();
                break;
            }
            numOfTries++;
            try {
                Thread.sleep(5000);
            }catch (Exception e){

            }
        }
        System.out.println("Couldn't connect connection timed out.");
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
