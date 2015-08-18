import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Created by Scott on 16/08/2015.
 */
public class Recieve {


    private String messageServer;
    private Socket clientSocket;


    public Recieve(Socket clientSocket){
        this.clientSocket = clientSocket;

    }

    public String getDataFromServer() throws IOException{
        System.out.println("GetDataICalled");
        /*BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        messageServer = inFromServer.readLine();
        System.out.println("Receive Class, Message From server: " + messageServer);
        */
        String built="";
        InputStreamReader in = new  InputStreamReader(clientSocket.getInputStream());
        built += (char)in.read();
        while(!built.contains("/e/")){//handles the end of the message
            built += (char)in.read();
            System.out.println("Built Progress: "+built);

        }
        messageServer = built;
        return messageServer;

    }

}





