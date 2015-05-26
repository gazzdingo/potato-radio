import javax.sound.sampled.*;
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
import java.util.*;

/**
 * Created by guylangford-lee on 22/05/15.
 */
public class RMI extends UnicastRemoteObject implements RemoteMusicInter{

    public static final String BIND_NAME = "potatoserver";
    public static final int TCP_ELECTION_PORT = 8787 ;
    public static final int UDP_MUSIC_PORT = 8182 ;
    public static final int MUSIC_BYTE_SEND_SIZE = 2500 ;
    public static final int RMI_PORT = 1099;
    private String address;
    private Registry registry;
    private List<String> hosts;
    private List<String> messages;
    private boolean sendingMusic = true;

    protected RMI() throws RemoteException {
        super();
        hosts = new LinkedList<>();
        messages = new LinkedList<>();
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
    public List<String> messages() throws RemoteException {
        return messages;
    }

    @Override
    public void addMessage(String message, VectorClock timeStamp) throws RemoteException {
        messages.add(message);
    }

    @Override
    public List<String> ipAddresses() throws RemoteException {
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
        if (AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
            try {

                DataLine.Info dataLineInfo = new DataLine.Info( TargetDataLine.class , Client.AUDIO_FORMAT ) ;
                TargetDataLine targetDataLine = (TargetDataLine)AudioSystem.getLine( dataLineInfo  ) ;
                targetDataLine.open( Client.AUDIO_FORMAT );
                targetDataLine.start();
                byte tempBuffer[] = new byte[this.MUSIC_BYTE_SEND_SIZE] ;
                while(sendingMusic)
                {

                    targetDataLine.read( tempBuffer , 0 , tempBuffer.length );
                    udpConnection(tempBuffer) ;
                }

            }
            catch(Exception e )
            {
            }
        }else{
        }



    }




    public  void udpConnection(byte soundpacket[]){
        try
        {
            DatagramSocket sock = new DatagramSocket() ;
            for(String host : ipAddresses()) {
                sock.send(new DatagramPacket(soundpacket, soundpacket.length, InetAddress.getByName(host), this.UDP_MUSIC_PORT));

            }
            sock.close() ;
        }
        catch( Exception e )
        {
        }

    }


}





