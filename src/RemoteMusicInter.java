import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

/**
 * Created by guylangford-lee on 22/05/15.
 */
public interface RemoteMusicInter extends Remote, Serializable {


    List<String> messages() throws  RemoteException;
    void addMessage(String message, VectorTimeStamp timeStamp) throws RemoteException;
    Set<String> ipAddresses() throws RemoteException;
    void addHost(String host) throws RemoteException;



}
