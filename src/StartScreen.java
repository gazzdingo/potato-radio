import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
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
    private Client client;

    public  StartScreen(){
        jframe = new JFrame("Potato Radio Talk-Back");
        Dimension d = new Dimension(780,450);
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
        JTextField username = new JTextField("");
            btnStartClient.addActionListener(e -> {
                if(serverHost.getText().length() > 0){
                    try {
                        System.out.println("started Client");
                        System.out.println(serverHost.getText());
                         client = new Client(serverHost.getText(), username.getText());
                        new Thread(client::setUpIP).start();

                        new Thread(client::checkForElections).start();
                        new Thread(client::receiveMusic).start();



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
            });
        btnStartServer.addActionListener(e -> {
            try {
                rmi = new RMI();
                rmi.start();
                new Thread(rmi::send).start();
                client = new Client("localhost", username.getText());



                System.out.println("started Server");
            } catch (RemoteException e1) {
                e1.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        JTextArea sendMessage = new JTextArea();
        sendMessage.setLineWrap(true);

        JTextArea messages = new JTextArea();
        messages.setLineWrap(true);
        new Thread(()->{
             while(true){
               try {
                   StringBuilder s = new StringBuilder();
                   client.getMessages().forEach(e -> s.append(e + "\n"));
                   messages.setText(s.toString());

               }catch(Exception e){
                }
            }
        }).start();
        messages.setPreferredSize(new Dimension(300, 300));

        sendMessage.setPreferredSize(new Dimension(100,100));

        username.setPreferredSize(new Dimension(60,20));

        JButton btnSendMessage = new JButton("send message");
        btnSendMessage.addActionListener(e -> {

            if(sendMessage.getText().length() > 0){
                if(client != null){
                    client.addMessage(sendMessage.getText());
                    sendMessage.setText("");

                }
            }
        });

        messages.setEditable(false);




        pane.add(btnSendMessage);
        pane.add(sendMessage);
        pane.add(messages);
        pane.add(username);

        pane.add(serverHost);

        pane.add(btnStartClient);
        pane.add(btnStartServer);
        //add image
        JLabel label = new JLabel("");
        Image img = new ImageIcon(this.getClass().getResource("/img/potato.jpg")).getImage();
//        Dimension dimension = new Dimension(img.getWidth(null), img.getHeight(null));
//        label.setPreferredSize(dimension);
        label.setIcon(new ImageIcon(img));
        label.setSize(100,100);
        jframe.getContentPane().add(label);
        jframe.add(pane);
    }

    public BufferedImage resizeImg(BufferedImage img, int newW, int newH)
    {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }
}
