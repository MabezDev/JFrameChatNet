import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Scott on 16/08/2015.
 */
public class Send {


    public Send(Socket clientSocket,String data)throws IOException{
        System.out.println("Preparing to Transmit");
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        outToServer.writeBytes(data);
    }
}
