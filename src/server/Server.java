package server;


import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;


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

        mngServer();

        receive();


            //main server loop, here we will decide using commands and input fro other users where to send messages






    }

    private void manageServer(){
        Scanner io = new Scanner(System.in);
        while(true){
            String command = io.nextLine();
            System.out.println(command);
            if(command.startsWith("/")){
                String splitCmd = command.split("/")[1];
                if(splitCmd.equals("quit")){
                    if(connectedClients.size()>0) {
                        for (int j = 0; j < connectedClients.size(); j++) {
                            System.out.println("connectedArraySize: " + connectedClients.size() + "Position: " + j);
                            kick(connectedClients.get(j).getID(), "Server is shutting down. ");

                        }
                    }
                    /*try {
                        serverSocket.close();
                    }catch (IOException i){
                        i.printStackTrace();
                    }*/
                    System.exit(0);
                }
                if(splitCmd.equals("help")){
                    System.out.println("=====-Help-=====");
                    System.out.println("/kick [ClientID or Username] - kicks the selected client");
                    System.out.println("/quit - disconnects all clients and closes server.");
                }
                if(splitCmd.startsWith("kick")){
                    String clienttoKick = splitCmd.split(" ")[1].trim();
                    String[] reasonArr = splitCmd.split(" ");
                    System.out.println("Length: "+reasonArr.length);
                    String reason ="";
                    if(reasonArr.length>=2){
                        for(int i=2;i<reasonArr.length;i++){
                            reason += " "+reasonArr[i];
                        }
                    }

                    System.out.println("Start kick:" +clienttoKick);
                    if(clienttoKick.matches(".*\\d.*")){
                        // contains a number
                        kick(Integer.parseInt(clienttoKick),reason);
                        System.out.println("Kicking: " + Integer.parseInt(clienttoKick));
                    } else{
                        kick(clienttoKick,reason);
                        System.out.println("Kicking: "+ clienttoKick);
                        // does not contain a number
                    }
                }
            }

        }

    }

    public void kick(int ID,String reason){
        for(int i = 0; i<connectedClients.size();i++){
            ServerThread client = connectedClients.get(i);
            if(client.getID()==ID){
                client.send("/k/ You have been kicked. Reason: "+reason+" /ID/ "+client.getID()+" /e/");
                client.closeConnection();
                connectedClients.remove(i);

            }
            System.out.println("No such client found on server.");
        }
    }

    public void kick(String UserName, String reason){
        for(int i = 0; i<connectedClients.size();i++){
            ServerThread client = connectedClients.get(i);
            if(client.getUserName().trim().equals(UserName.trim())){
                client.send("/k/ You have been kicked. Reason: "+reason+" /ID/ "+client.getID()+" /e/");
                client.closeConnection();
                connectedClients.remove(i);

            }
        }
    }

    private void mngServer(){
        Thread mng = new Thread(new Runnable() {
            @Override
            public void run() {
                manageServer();
            }
        });
        mng.start();
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
                    //sv.send("/d/");
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
