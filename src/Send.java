import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Socket;

/**
 * Created by Scott on 16/08/2015.
 */
public class Send {

    private Socket clientSocket;
    private String data;




    public Send(String data, String ip, int port)throws IOException{
        clientSocket = new Socket(ip,port);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        outToServer.writeBytes(data);

    }
}
