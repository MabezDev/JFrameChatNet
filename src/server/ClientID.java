package server;

import java.io.DataOutputStream;
import java.io.InputStreamReader;


/**
 * Created by Scott on 17/08/2015.
 */
public class ClientID {

    public  String USER_NAME;

    public DataOutputStream o;
    private final int ID;
    private InputStreamReader i;

    public ClientID(String UN,DataOutputStream o,InputStreamReader i,int ID){
        this.USER_NAME=UN;
        this.o = o;
        this.i = i;
        this.ID = ID;
    }

    public DataOutputStream getOutputStream(){
        return o;
    }
    public InputStreamReader getInputStream(){
        return i;
    }

    public int getID(){
        return this.ID;
    }





}
