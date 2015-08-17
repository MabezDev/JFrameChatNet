package server;

import java.io.IOException;

/**
 * Created by Scott on 17/08/2015.
 */
public class ServerMain {

    private int port;

    public ServerMain(int port) throws IOException{

        this.port = port;
        new Server(port);
    }


    public static void main(String[] args) throws IOException{
        int port;

        port = 7777;
        System.out.println("Starting Server. Binding to port: "+port);
        new ServerMain(port);

    }
}
