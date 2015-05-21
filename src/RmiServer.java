import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Created by guylangford-lee on 21/05/15.
 */
public class RmiServer extends UnicastRemoteObject
        implements ServerOutput, ServerRemote {

    Registry registry;
    List<User> userList;
    Map<String,byte[]> fileList;
    private String bindName;
    private String address;
    private int port;
    private ServerRemote leader;
    private LinkedList<ServerRemote> remoteList;


    public RmiServer(String bindName) throws RemoteException {
        super();
        this.bindName = bindName;
        this.userList = new ArrayList<>();
        fileList = new HashMap<>();
//
    }

    public void start(int port) throws Exception {

        this.address  = InetAddress.getLocalHost().getHostAddress();
        this.port = port;
        registry = LocateRegistry.createRegistry(getport());
        registry.rebind("potatoman", this);
    }

    public void stop() throws Exception {
        registry.unbind("server");
        unexportObject(this, true);
        unexportObject(registry, true);
        System.out.println("Server stopped");
    }


    @Override
    public List<User> userList() {
        return userList;
    }

    @Override
    public void upload(byte[] file, String fileName) throws RemoteException {
         fileList.put(fileName, file);
    }

    @Override
    public void addUser(String name, String ip) throws RemoteException {
        userList.add(new User(name,ip));
    }

    @Override
    public Map<String,byte[]> files() throws RemoteException {
        return fileList;
    }

    @Override
    public byte[] getFile(String fileName) {
        return fileList.get(fileName);

    }

    @Override
    public void setBindName(String bindName) {

        this.bindName = bindName;
    }

    @Override
    public String getBindName() {
        return this.bindName;
    }



    @Override
    public String getAddress() {
        return this.address;
    }



    @Override
    public int getport() {
        return this.port;
    }

    @Override
    public void setLeader(ServerRemote leader) {
        this.leader = leader;
    }

    @Override
    public ServerRemote getLeader() {
        return leader;
    }

    @Override
    public void setLeader() {
        leader = this;
    }

    @Override
    public void addremote(ServerRemote remote) {
        if(!remoteList.isEmpty()) {
            remoteList.addLast(remote);
            remoteList.
        }else {
            remoteList.add(remote);
        }
    }

    @Override
    public boolean isLeader() {
        return false;
    }

    @Override
    public List<ServerRemote> getRemote() {
        return null;
    }
}

