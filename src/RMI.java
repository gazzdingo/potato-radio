import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by guylangford-lee on 22/05/15.
 */
public class RMI extends UnicastRemoteObject implements RemoteMusicInter{

    public static final String BIND_NAME = "potatoserver";
    public static final int TCP_ELECTION_PORT = 8181 ;

    protected RMI() throws RemoteException {
        super();
    }


}
