import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by Ben on 25/05/15.
 */
public class StartScreen {

    private JFrame jframe;
    private RMI rmi;

    public  StartScreen(){
        jframe = new JFrame();
        Dimension d = new Dimension(600,400);
        jframe.setSize(d);
        init();
       jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       jframe.setLocationRelativeTo(null);
       jframe.setVisible(true);
    }

    private void init() {
        JPanel pane = new JPanel();
        JButton btnStartServer = new JButton("Start Server");
        JTextField serverHost = new JTextField();
        serverHost.setPreferredSize(new Dimension(160, 20));
        JButton btnStartClient = new JButton("StartClient");
            btnStartClient.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(serverHost.getText().length() > 0){
                        try {
                            System.out.println("started Client");
                            System.out.println(serverHost.getText());
                            Client client = new Client(serverHost.getText());
                            new Thread(client::checkForElections).start();
                            new Thread(client::receiveMusic).start();
                            new Thread(rmi::send).start();



                        } catch (RemoteException e1) {
                            e1.printStackTrace();
                        } catch (NotBoundException e1) {
                            e1.printStackTrace();
                        } catch (MalformedURLException e1) {
                            e1.printStackTrace();
                        } catch (UnknownHostException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
        btnStartServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                     rmi = new RMI();
                    rmi.start();

                    System.out.println("started Server");
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        pane.add(serverHost);

        pane.add(btnStartClient);
        pane.add(btnStartServer);
        jframe.add(pane);
    }
}
