import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Scott on 20/08/2015.
 */
public class Login extends JFrame {
    private JPanel Main;
    private JTextField portTextField;
    private JTextField ipText;
    private JButton connectToServerButton;
    private JTextField usernameTextField;

    public Login(){
        //new Client("Scott","localhost",7777);
        Main.setLayout(Main.getLayout());
        this.setContentPane(Main);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        setUpComponents();
    }

    private void setUpComponents(){
        connectToServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String UN = usernameTextField.getText();
                String ip = ipText.getText();
                int port = Integer.parseInt(portTextField.getText());
                new Client(UN,ip,port);
                killLogin();
            }
        });

        usernameTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    System.out.println("Enter pressed");
                    connectToServerButton.doClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        ipText.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    System.out.println("Enter pressed");
                    connectToServerButton.doClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        portTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    System.out.println("Enter pressed");
                    connectToServerButton.doClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private void killLogin(){
        this.dispose();
    }
}
