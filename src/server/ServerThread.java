package server;

import javax.xml.crypto.Data;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Scott on 20/08/2015.
 */
public class ServerThread {

    private Socket clientSocket;
    private boolean clientConnected;
    private Thread send, receive;
    private InputStreamReader inFromClient;
    private DataOutputStream outToClient;
    private int ID;


    public ServerThread(Socket client){
        clientConnected = true;
        clientSocket = client;
        System.out.println("In Server thread");
        setUpStreams();
        listen();
    }




    private void setUpStreams(){
        try {
            outToClient = new DataOutputStream(clientSocket.getOutputStream());
            outToClient.flush();
            inFromClient = new InputStreamReader(clientSocket.getInputStream());
            //send ID packet to tell client its ID
            send("/ID/ "+Integer.toString(getID())+" /e/");
        }catch (IOException e){

        }
    }


    public void send(String data){//add client id param later

            final String Data = data;
            send = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("Sending: " + Data);
                        outToClient.writeBytes(Data);

                    } catch (IOException e) {
                        //need timeout tryer here then disconnect if it cant be reached
                        e.printStackTrace();
                    }
                }
            }) {

            };
            send.start();

    }


    private void listen(){
        receive = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Now Listening");
                while(clientConnected){
                    try {
                        String built = "";
                        built += (char) inFromClient.read();
                        while (!built.contains("/e/")) {//handles the end of the message
                            built += (char) inFromClient.read();
                        }
                        if(isIDZero(built)){
                             built = addId(built);
                            //send a notification message to tell client its ID
                        }
                        Server.processData(built);

                    } catch (IOException e) {
                        e.printStackTrace();
                        /*
                        add time out function try to connect back tot eh client a certain amount of time then just close the connection
                        */
                        System.out.println("Listen function  - Client Disconnect. ID: "+ getID());
                        closeConnection();
                        try {
                            clientSocket.close();
                        } catch (IOException e2) {

                        }

                    }
                }

            }
        });
        receive.start();


    }

    private boolean isIDZero(String built){
        String clientToDCString = built.split("/ID/|/e/")[1].trim();
        int clientID = Integer.parseInt(clientToDCString);
        if(clientID==0){
            System.out.println("ID is zero");
            return true;
        }
        return false;
    }

    public String addId(String data){
        String Data = data;
        String Message = Data.split("/c/|/ID/")[0];
        System.out.println("Message: "+Message);
        String rebuilt = Message + " /ID/ "+ getID() + " /e/";
        return rebuilt;
    }

    public void closeConnection(){
        try{
            receive.interrupt();
            inFromClient.close();
            outToClient.close();
            clientSocket.close();
            clientConnected = false;

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void setID(int id){
        this.ID = id;
    }

    public int getID(){
        return ID;
    }





}
