import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by guylangford-lee on 22/05/15.
 */
public interface RemoteMusicInter extends Remote, Serializable {

    String currentPlaying() throws RemoteException;
    int requestSong(String songName) throws  RemoteException;
    List<String> requestSongPlaylist() throws RemoteException;
    List<String> ipAddresses() throws RemoteException;
    void addHost(String host) throws RemoteException;



}
