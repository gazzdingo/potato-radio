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
    public static final int TCP_ELECTION_PORT = 8181 ;
    public static final int UDP_MUSIC_PORT = 8182 ;
    public static final int MUSIC_BYTE_SEND_SIZE = 2500 ;
    public static final int RMI_PORT = 1099;
    private String address;
    private Registry registry;
    private List<String> hosts;
    private List<String> messages;

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
        try {
            int port = this.UDP_MUSIC_PORT;
            // this is the path that the music is hosted

            Path path = Paths.get("wana.mp3");
            byte[] data = Files.readAllBytes(path);
            // loop though all the hosts so that is can send out the music to them
            for(String host : ipAddresses()) {
                // the size of the byte that will be sent out
                int splitSize =  MUSIC_BYTE_SEND_SIZE;
                // get the host address
                InetAddress address = InetAddress.getByName(host);
                DatagramSocket dsocket = new DatagramSocket();
                //loop through splitting up the byte array

                for(int i = 0; i < data.length ; ) {

                    byte[] temp = new byte[splitSize];
                    try {
                        System.arraycopy(data, i, temp, 0, temp.length - 1);
                    }
                    catch(Exception e){
//                        System.out.println(e.getMessage());
                    }
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















    public  void send() {
        Mixer.Info minfo[] = AudioSystem.getMixerInfo() ;
        for( int i = 0 ; i < minfo.length ; i++ )
        {
        }


        if (AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
            try {


                DataLine.Info dataLineInfo = new DataLine.Info( TargetDataLine.class , getAudioFormat() ) ;
                TargetDataLine targetDataLine = (TargetDataLine)AudioSystem.getLine( dataLineInfo  ) ;
                targetDataLine.open( getAudioFormat() );
                targetDataLine.start();
                byte tempBuffer[] = new byte[this.MUSIC_BYTE_SEND_SIZE] ;
                int cnt = 0 ;
                while( true )
                {

                    targetDataLine.read( tempBuffer , 0 , tempBuffer.length );
                    sendThruUDP( tempBuffer ) ;
                }

            }
            catch(Exception e )
            {
                System.out.println(" not correct ") ;
            }
        }else{
            System.out.println("NOT SUPPORTED");
        }



    }


    public  AudioFormat getAudioFormat(){
        float sampleRate = 8000.0F;
        //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;
        //8,16
        int channels = 1;
        //1,2
        boolean signed = true;
        //true,false
        boolean bigEndian = false;
        //true,false
        return new AudioFormat( sampleRate, sampleSizeInBits, channels, signed, bigEndian );
    }


    public  void sendThruUDP( byte soundpacket[] )
    {
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
            e.printStackTrace() ;
            System.out.println(" Unable to send soundpacket using UDP ") ;
        }

    }


}





