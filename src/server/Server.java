package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Scott on 17/08/2015.
 */
public class Server implements Runnable {

    private ServerSocket serverSocket;
    private int port;
    private Thread run;
    private BufferedReader inFromClient;


    public Server(int port) throws IOException {
        System.out.println("In Server");
        this.port = port;
        run = new Thread(this, "Server");
        run.start();
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


        while (true) {
            //main server loop, here we will decide using commands and input fro other users where to send messages
            try {
                setUpSocket();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    private void setUpSocket() throws IOException {
        String built ="";
        Socket connectionSocket = serverSocket.accept();
        System.out.println(connectionSocket.isConnected());
        InputStreamReader in = new  InputStreamReader(connectionSocket.getInputStream());
        built += (char)in.read();
        System.out.println(built);
        while(!built.contains("/e/")){//handles the end of the message
            built += (char)in.read();
        }
        in.close();
        System.out.println("Found end of message: "+ built);

    }

    private void processData(String data){
        String Data = data;
        if(Data.startsWith("/m/")) {
            sendToAll();
        }


    }

    private void sendToAll(){
        // here send to all clients
        //for now just send back to the 1 connection
    }

}
