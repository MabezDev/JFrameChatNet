import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Created by Scott on 16/08/2015.
 */
public class Recieve {

    private Socket clientSocket;
    private String messageServer;
    private String ClientIP;
    private int port;


    public Recieve(String ClientIP, int port){
        this.ClientIP = ClientIP;
        this.port = port;

    }

    public String getDataFromServer() throws IOException{
        clientSocket = new Socket(ClientIP, port);
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        messageServer = inFromServer.readLine();
        System.out.println("Receive Class, Message From server: " + messageServer);
        return messageServer;

    }

}





