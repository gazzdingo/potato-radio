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
            Client client = new Client("localhost");


            new Thread(client::sendElectionMessage).start();
            new Thread(client::checkForElections).start();

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
