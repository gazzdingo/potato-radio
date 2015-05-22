import java.nio.file.FileSystems;
import java.rmi.RemoteException;

/**
 * Created by guylangford-lee on 21/05/15.
 */
public class hello {
    public static void main(String[] args){
        try {
            RmiServer rmiServer= new RmiServer("hello");
            rmiServer.start(1099);
            RmiClient c = new RmiClient("hello","localhost");
            c.connect();
            System.out.println();FileSystems.getDefault().getPath("").getRoot();


        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
