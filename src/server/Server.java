package server;


import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Created by Scott on 17/08/2015.
 */
public class Server implements Runnable {

    private ServerSocket serverSocket;
    private int port;
    private Thread run, send;
    private Socket clientSocket;
    private DataOutputStream outToClient;
    private InputStreamReader inFromClient;
    private boolean running;

    private ArrayList<ClientID> connectedClients = new ArrayList<ClientID>();


    public Server(int port) throws IOException {
        System.out.println("In Server");
        this.port = port;
        run = new Thread(this, "Server");
        run.start();


    }

    public void send(String data,String USERNAME)throws IOException{//add client id param later
        final String Data = data;
        final DataOutputStream strP = connectedClients.get(0).getOutputStream();

        send = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Sending: " + Data);
                    strP.writeBytes(Data);

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }){

        };
        send.start();

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

        stopAndRetry();



            //main server loop, here we will decide using commands and input fro other users where to send messages
        try {
            clientSocket = serverSocket.accept();
            setUpStreams(clientSocket);
            receive();
        } catch (IOException e) {
            e.printStackTrace();
        }





    }

    private void manageClients(){
        Thread mng = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    private void stopAndRetry(){

    }

    private void receive(){
        while(running) {
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

                processData(built);

            }catch (IOException e){
                e.printStackTrace();
                System.out.println("Listen function  - Client Disconnect.");
                closeStreams();
                try{
                    clientSocket.close();
                }catch (IOException e2){

                }

            }
        }

    }


    private void setUpStreams(Socket clientSocket) throws IOException {
        outToClient = new DataOutputStream(clientSocket.getOutputStream());
        outToClient.flush();
        inFromClient = new  InputStreamReader(clientSocket.getInputStream());
    }

    private void closeStreams(){
        try {
            outToClient.close();
            inFromClient.close();
        }catch (IOException e){

        }
    }



    private void processData(String data)throws IOException{
        String Data = data;
        if(Data.startsWith("/m/")) {
            sendToAll(Data);
        }
        if(Data.startsWith("/c/")){
            //new connected client, add details
            String name = Data.split("/c/|/e/")[1];
            connectedClients.add(new ClientID(name,outToClient,inFromClient,0));
            System.out.println("New user connected :"+name);
        }
        if(Data.startsWith("/d/")){
            String clientToDC = Data.split("/c/|/e/")[0];
            for(int i=0;i<connectedClients.size();i++){
                ClientID client = connectedClients.get(i);
                if(client.USER_NAME.equals(clientToDC)){
                    System.out.println("Disconnecting: "+ clientToDC);
                    running =false;
                    closeStreams();
                    clientSocket.close();
                    connectedClients.remove(i);
                }
            }
        }



    }

    private void sendToAll(String data)throws IOException{
        // here send to all clients
        //for now just send back to the 1 connection
        for(int i=0;i<connectedClients.size();i++){
            ClientID client = connectedClients.get(i);
            send(data,client.USER_NAME);
        }

    }

}
