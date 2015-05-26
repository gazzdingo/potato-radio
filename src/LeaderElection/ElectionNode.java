package LeaderElection;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Created by Ben on 26/05/15.
 */
public interface ElectionNode extends Remote {

    // Node methods
    public String startElection(String senderName) throws RemoteException, DeadNodeException;
    public void newLeader(String newLeaderName) throws RemoteException;
    public String recvMsg(String senderName, String msg) throws RemoteException;

    // Election Driver methods
    public void makeChaos(String newName, int ignore) throws RemoteException;

}
