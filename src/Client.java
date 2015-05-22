import java.io.IOException;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by guylangford-lee on 22/05/15.
 */
public class Client{
    RemoteMusicInter remoteServer;
    private boolean checkForElections = true;

    long memory;
    String ip;
    public Client(String serverURI) throws RemoteException, NotBoundException, MalformedURLException, UnknownHostException {
//        setLeader(serverURI);
//        setMemory(Runtime.getRuntime().freeMemory());
//        setIp(InetAddress.getLocalHost().getHostAddress());
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    public long getMemory() {
        return memory;
    }

    public String getIp() {
        return ip;
    }

    public void setLeader(String uri) throws RemoteException, NotBoundException, MalformedURLException {
        String connectUrl = String.format("rmi://%s/%s", uri, RMI.BIND_NAME);
         remoteServer = (RemoteMusicInter) Naming.lookup(connectUrl);
    }

    public void sendElectionMessage() {
//        Election election = new Election(this.getIp(),this.getMemory());
        try {

            ServerSocket serverSocket = new ServerSocket(RMI.TCP_ELECTION_PORT);
            bla = serverSocket.accept();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void checkForElections(){
        Socket MyClient;
        MyClient = new Socket(leftIP(), RMI.TCP_ELECTION_PORT);
        


    }

    private String leftIP() {
        return "";
    }


}
