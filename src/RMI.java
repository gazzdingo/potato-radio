import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guylangford-lee on 22/05/15.
 */
public class RMI extends UnicastRemoteObject implements RemoteMusicInter{

    public static final String BIND_NAME = "potatoserver";
    public static final int TCP_ELECTION_PORT = 8181 ;
    public static final int UDP_MUSIC_PORT = 8182 ;
    public static final int MUSIC_BYTE_SEND_SIZE = 2000000000 ;
    public static final int RMI_PORT = 1099;
    private String address;
    private Registry registry;
    private List<String> hosts;
    private List<String> songPlayList;

    protected RMI() throws RemoteException {
        super();
        hosts = new ArrayList<>();
        songPlayList = new ArrayList<>();

    }

       public void start() throws Exception {

           this.address  = InetAddress.getLocalHost().getHostAddress();

           try {
               registry = LocateRegistry.createRegistry(RMI_PORT);
           }
           catch (Exception e){
               registry = LocateRegistry.getRegistry(RMI_PORT);

           }
           registry.rebind(BIND_NAME, this);
       }

    public void stop() throws RemoteException, NotBoundException {
        registry.unbind(BIND_NAME);
        unexportObject(this, true);
        unexportObject(registry, true);
    }



    @Override
    public String currentPlaying() throws RemoteException {
        return null;
    }

    @Override
    public int requestSong(String songName) throws RemoteException {
        return 0;
    }

    @Override
    public List<String> requestSongPlaylist() throws RemoteException {
        return  songPlayList;
    }

    @Override
    public List<String> ipAddresses() throws RemoteException {
        hosts.forEach(System.out::println);
        return hosts;

    }

    @Override
    public void addHost(String host) throws RemoteException {
        this.hosts.add(host);
    }

    /**
     * will send out the music to all of the clients to listen to
     */
    public void broadcastMusic(){
        try {
            int port = this.UDP_MUSIC_PORT;
            // this is the path that the music is hosted
            Path path = Paths.get("wana.mp3");
            byte[] data = Files.readAllBytes(path);
            // loop though all the hosts so that is can send out the music to them
            for(String host : ipAddresses()) {
                // the size of the byte that will be sent out
                int splitSize =  2000;
                // get the host address
                InetAddress address = InetAddress.getByName(host);
                DatagramSocket dsocket = new DatagramSocket();
                //loop through splitting up the byte array
                for(int i = 0; i < data.length ; ) {
                    byte[] temp = new byte[splitSize];

                    System.arraycopy(data,i,temp,0,temp.length-1);
                    i += splitSize;
                    // Initialize a datagram packet with data and address
                    DatagramPacket packet = new DatagramPacket(temp, temp.length,
                            address, port);
                    dsocket.send(packet);
                }

                dsocket.close();
            }
        } catch (Exception e) {
        e.printStackTrace();
      }

    }

}
