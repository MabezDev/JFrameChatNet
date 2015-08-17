import java.awt.*;

public class Main {

    public static void main(String[] args) {


        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                System.out.println("Hello World!");
                //new Clients("Scott","192.168.0.22",7777);//garbage will get this data from a,ogin screen eventually
                new Client("Scott","localhost",7777);
            }
        });
    }
    }


