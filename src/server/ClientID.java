package server;

/**
 * Created by Scott on 17/08/2015.
 */
public class ClientID {

    public  String USER_NAME;
    public String IP_ADDRESS;
    public int port;
    private final int ID;

    public ClientID(String UN,String IP,int port,int ID){
        this.USER_NAME=UN;
        this.IP_ADDRESS = IP;
        this.port = port;
        this.ID = ID;
    }

    public int getID(){
        return this.ID;
    }


}
