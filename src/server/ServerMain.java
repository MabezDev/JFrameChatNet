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
        int port = 0;
        if(args.length>0){
            port = Integer.parseInt(args[0]);
        }else {
            System.out.println("Requires Port argument, Correct Usage: - server.jar [port]");
            try {
                Thread.sleep(2000);
            }catch (Exception e){

            }
            System.exit(0);
        }
        System.out.println("Starting Server. Binding to port: "+port);
        new ServerMain(port);

    }
}
