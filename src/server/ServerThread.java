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
                        // need to set up a stream every time
                        //serviceClients();
                        String built = "";
                        built += (char) inFromClient.read();
                        while (!built.contains("/e/")) {//handles the end of the message
                            built += (char) inFromClient.read();
                            System.out.println("Built Progress: " + built);
                        }
                        //in.close();// not needed as we want to keep the connection open for server replies
                        System.out.println("Found end of message: " + built);
                        if(isIDZero(built)){
                             built = addId(built);
                        }
                        Server.processData(built);

                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Listen function  - Client Disconnect.");

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

        System.out.println("Is Zero String: "+clientToDCString);
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
            inFromClient.close();
            outToClient.close();
            clientSocket.close();
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
