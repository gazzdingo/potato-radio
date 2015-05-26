import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by guylangford-lee on 22/05/15.
 */
public interface RemoteMusicInter extends Remote, Serializable {


    List<String> messages() throws  RemoteException;
    void addMessage(String message, VectorClock timeStamp) throws RemoteException;
    List<String> ipAddresses() throws RemoteException;
    void addHost(String host) throws RemoteException;



}
