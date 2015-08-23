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
import java.net.SocketException;
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
    private int numOfAttempts;
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
        this.ID = 0;
        numOfAttempts =0;




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
                attemptConnection();
            }
        };
        starter.start();




        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                String disconnect = "/d/ " + USER_NAME + " /ID/ " + ID + " /e/";
                send(disconnect);
                //lastMessage = "/d/";


                //running = false;
                //add stream closing func
            }
        });



    }
    private void buildListenThread() {
        if(listen!=null){
            listen.interrupt();
            listen=null;
        }
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
            inFromServer = null;

            outToServer = null;

            clientSocket.close();

            isConnected = false;
        }catch (IOException e){

        }
    }


    private boolean attemptConnection(){
        try {
            clientSocket = new Socket(IP_ADDRESS, PORT);
            setUpStreams();
            if(outToServer!=null && inFromServer!=null){
                //show that user is connected
                messageBox.append("Connected as :" + USER_NAME + " At: "+IP_ADDRESS+":"+PORT+ "\n\r");
                numOfAttempts = 0;
                buildListenThread();
                starter.interrupt();
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
                    built += (char) inFromServer.read();
                }

                if(built.startsWith("/ID/")){
                    String ID = built.split("/ID/|/e/")[1].trim();
                    int clientID = Integer.parseInt(ID);
                    System.out.println("Setting ID to: "+clientID);
                    this.ID = clientID;
                    send("/u/ "+USER_NAME+" /ID/ "+this.ID+" /e/");//send username to server
                }
                if(built.startsWith("/m/")){
                    System.out.println("Found end of message: " + built);
                    String finishedData = built.split("/m/|/ID/")[1];
                    messageBox.append(finishedData + "\n\r");
                }
                if(built.startsWith("/k/")){
                    System.out.println("Kicked!");
                    String toDisplay = built.split("/k/|/ID/")[1];
                    messageBox.append(toDisplay + "\n\r");
                    closeConnection();
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
                e.printStackTrace();
                isConnected = false;

                if(!lastMessage.startsWith("/d/")) {// add a clause for banned?
                    closeConnection();
                    // add if statement to check timeouts
                    while(numOfAttempts<MAX_ATTEMPTS){
                        try {
                            Thread.sleep(5000);
                        }catch (Exception e2){

                        }
                        if(attemptConnection()==true){
                            System.out.println("Reconnected");
                            messageBox.append("Reconnected."+"\n\r");
                        }
                        numOfAttempts++;
                        messageBox.append("Disconnected. Retrying connection. Attempt: "+numOfAttempts+"\n\r");
                    }
                    System.out.println("Couldn't reconnect");
                    messageBox.append("Server connection failed. The remote host closed the connection."+"\n\r");





                }
            }
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
                    outToServer.writeBytes(messageOut);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

        };
        Send.start();

    }

    private void setUpStreams() throws IOException{
        try {
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.flush();
            inFromServer = new InputStreamReader(clientSocket.getInputStream());
        }catch (SocketException socketClosed){
            socketClosed.printStackTrace();
            closeConnection();
        }
    }






}
