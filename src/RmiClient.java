
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by guylangford-lee on 21/05/15.
 */
public class RmiClient extends UnicastRemoteObject{
    private final String serverIp;
    private final String name;
    private Registry regClient;
    private ServerOutput server;

    public  RmiClient(String name,String serverIp) throws RemoteException {
        super();
      this.name = name;
      this.serverIp = serverIp;

  }
    public boolean connect() {
        try {

//            regClient = LocateRegistry.getRegistry(1099);
//            regClient = LocateRegistry.createRegistry(1099);

            User user = new User(name, InetAddress.getLocalHost().getHostAddress());
//            regClient.bind(user.getName(), this);
            String s = String.format("rmi://%s/%s", serverIp,"potatoman");
            System.out.println(s);
            server = (ServerOutput) Naming.lookup(s);
            server.addUser(user.getName(),user.getIp());


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public byte[] getFile(String name) throws RemoteException {
        return server.getFile(name);
    }

}
