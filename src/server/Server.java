package server;


import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


/**
 * Created by Scott on 17/08/2015.
 */
public class Server implements Runnable {

    private ServerSocket serverSocket;
    private int port;
    private Thread run;
    private DataOutputStream outToClient;
    private boolean running;



    private static ArrayList<ServerThread> connectedClients = new ArrayList<ServerThread>();


    public Server(int port) throws IOException {
        System.out.println("In Server");
        this.port = port;
        run = new Thread(this, "Server");
        run.start();
    }



    @Override
    public void run() {
        running = true;
        System.out.println("Running Server On Port: " + port);
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {

            e.printStackTrace();
        }

        //manageClients();

        //Scanner consoleInput = new Scanner(System.in); //gather commands from cli

        //stopAndRetry();
        receive();


            //main server loop, here we will decide using commands and input fro other users where to send messages






    }

    private void manageClients(){
        Thread mng = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }



    private void receive(){
        while(running) {
            try {
                Socket clientSocket = serverSocket.accept();
                ServerThread s = new ServerThread(clientSocket,UniqueID.getIdentifier());
                connectedClients.add(s);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }



    public static ArrayList<ServerThread> getConnectedClients(){
        return connectedClients;
    }

    private static void sendToAll(String data)throws IOException{
        // here send to all clients
        //for now just send back to the 1 connection
        for(ServerThread c:connectedClients){
            c.send(data);
        }


    }

    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static void processData(String data)throws IOException{
        String Data = data;
        if(Data.startsWith("/m/")) {
            sendToAll(Data);
        }
        if(Data.startsWith("/c/")){
            //new connected client, add details
            String name = Data.split("/c/|/e/")[1];
            System.out.println("New user connected :"+name);
        }
        if(Data.startsWith("/d/")){
            String clientToDCString = Data.split("/ID/|/e/")[1].trim();
            int clientID = Integer.parseInt(clientToDCString);
            System.out.println(clientToDCString);
            for(int i=0;i<connectedClients.size();i++){
                ServerThread client = connectedClients.get(i);
                if(client.getID()==clientID){
                    System.out.println("Disconnecting: "+ clientID);
                    ServerThread sv = connectedClients.get(i);
                    sv.send("/d/");
                    sv.closeConnection();
                    connectedClients.remove(i);
                }
            }
        }



    }

    public static void removeTimedOutClient(int ID){ //reserved for serverThread to call when a client times out unexpectedly
        for(int i=0;i<connectedClients.size();i++){
            ServerThread client = connectedClients.get(i);
            if(client.getID()==ID){
                connectedClients.remove(i);
            }
        }
    }








}
