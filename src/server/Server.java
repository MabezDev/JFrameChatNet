package server;


import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by Scott on 17/08/2015.
 */
public class Server implements Runnable {

    private ServerSocket serverSocket;
    private int port;
    private Thread run, send;
    private Socket connectionSocket;
    private DataOutputStream outToClient;
    private InputStreamReader inFromClient;


    public Server(int port) throws IOException {
        System.out.println("In Server");
        this.port = port;
        run = new Thread(this, "Server");
        run.start();


    }

    public void send(String data)throws IOException{//add client id param later
        final String Data = data;
        send = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Sending: " + Data);
                    outToClient.writeBytes(Data);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }){

        };
        send.start();

    }

    @Override
    public void run() {
        System.out.println("Running Server On Port: " + port);
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Scanner consoleInput = new Scanner(System.in); //gather commands from cli





            //main server loop, here we will decide using commands and input fro other users where to send messages
        try {
            connectionSocket = serverSocket.accept();
            setUpStreams();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true) {
            try {
                listen();
            }catch (IOException e){
                e.printStackTrace();
            }
        }




    }

    private void listen() throws IOException{
        String built ="";
        built += (char)inFromClient.read();
        while(!built.contains("/e/")){//handles the end of the message
            built += (char)inFromClient.read();
            System.out.println("Built Progress: "+built);
        }
        //in.close();// not needed as we want to keep the connection open for server replies
        System.out.println("Found end of message: "+ built);
        processData(built);
    }

    private void setUpStreams() throws IOException {
        outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        outToClient.flush();
        inFromClient = new  InputStreamReader(connectionSocket.getInputStream());

    }



    private void processData(String data)throws IOException{
        String Data = data;
        if(Data.startsWith("/m/")) {
            sendToAll(Data);
        }
        sendToAll(Data);


    }

    private void sendToAll(String data)throws IOException{
        // here send to all clients
        //for now just send back to the 1 connection
        send(data);
    }

}
