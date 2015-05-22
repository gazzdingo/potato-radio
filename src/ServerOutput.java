import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Created by guylangford-lee on 21/05/15.
 */
public interface ServerOutput extends Remote {

    List<User> userList() throws RemoteException;
    void upload(byte[] file, String fileName)throws RemoteException;
    void addUser(String name, String ip) throws RemoteException;
    Map<String,byte[]> files()  throws RemoteException;
    byte[] getFile(String fileName) throws RemoteException;

}
