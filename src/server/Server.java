package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

/**
 * Created by Scott on 17/08/2015.
 */
public class Server implements Runnable {

    private ServerSocket serverSocket;
    private String IP_ADDRESS;
    private int port;
    private Thread run;



    public Server(int port) throws IOException{
        System.out.println("In Server");
        this.port = port;
        run  =  new Thread(this,"Server");
        run.start();
    }

    @Override
    public void run() {
        System.out.println("Running Server On Port: "+ port);
        try {
            serverSocket = new ServerSocket(port);
        } catch(IOException e){
            e.printStackTrace();
        }

        Scanner consoleInput = new Scanner(System.in);


        while(true){
            //main server loop, here we will decide using commands and input fro other users where to send messages
        }


    }
}
