import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by guylangford-lee on 22/05/15.
 */
public class Main {

    public static void main(String[] args){
        try {

            StartScreen startScreen = new StartScreen();
//            RMI rmi = new RMI();
//            rmi.start();
//            Client client = new Client("localhost");
//
//            rmi.broadcastMusic();
//           new  Thread(client::receiveMusic);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
