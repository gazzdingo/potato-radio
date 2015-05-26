

import javax.sound.sampled.*;
import java.io.*;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by guylangford-lee on 22/05/15.
 */
public class Client{
    private final String userName;
    RemoteMusicInter remoteServer;
    private boolean checkForElections = true;

    long memory;
    private String ip;
    private boolean playingMusic = true;
    private static String  leftIP;
    private static String rightIP;
    private RMI rmi;

    public Client(String serverURI, String userName) throws RemoteException, NotBoundException, MalformedURLException, UnknownHostException {

        this.userName = userName;
        setLeader(serverURI);
        setMemory(Runtime.getRuntime().freeMemory());

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




    /**
     * this will attempt to set up the leader and all the left and right ip that you require for tcp connection for future elections
     * @param uri the remote server that will be the leader
     * @throws RemoteException
     * @throws NotBoundException
     * @throws MalformedURLException
     */
    public void setLeader(String uri) throws RemoteException, NotBoundException, MalformedURLException {
        // connectiong to the remote rmi
        String connectUrl = String.format("rmi://%s/%s", uri, RMI.BIND_NAME);

         remoteServer = (RemoteMusicInter) Naming.lookup(connectUrl);
        // making sure that the client does not already on the server
//            setUpIP();



    }

    public synchronized void setUpIP()  {
        while(playingMusic) {

            String ip = null;
            try {
                ip = InetAddress.getLocalHost().getHostAddress();


                try {
                    remoteServer.ipAddresses();
                } catch (Exception e) {
                    startElectionMessage();
                }
                if (!remoteServer.ipAddresses().contains(ip)) {
                    System.out.println("test");
                    remoteServer.addHost(ip);
                }

                //looping through setting up the the right and left ip for the leader election
                for (int i = 0; i < remoteServer.ipAddresses().size(); i++) {
                    //checking to make sure that it is not the first client
                    if (remoteServer.ipAddresses().size() != 1) {
                        //checking if it is the end client
                        if (i == 0) {
                            leftIP = remoteServer.ipAddresses().get(remoteServer.ipAddresses().size() - 1);
                            rightIP = remoteServer.ipAddresses().get(i + 1);
                        } else if (remoteServer.ipAddresses().size() - 1 == i) {
                            //assigning the left and right ip
                            leftIP = remoteServer.ipAddresses().get(i - 1);
                            rightIP = remoteServer.ipAddresses().get(0);
                        } else {
                            //assigning  the left and right ip
                            leftIP = remoteServer.ipAddresses().get(i - 1);
                            rightIP = remoteServer.ipAddresses().get(i + 1);
                        }

                    } else {
                        leftIP = (ip);
                        rightIP = (ip);
                    }
                }
                Thread.sleep(20);


            } catch (Exception e) {
                startElectionMessage();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

            }
        }
    }


    /**
     * this will set up and send the init leader election if it can not connect to the remote host
     */
    public void startElectionMessage() {

        //creating the new election when you are not able to connect to the server
        Election election = new Election(this.getIp(),this.getMemory());
        sendMessageLeft(election);

    }

    /**
     * this will be run in a thead to  loop though and check if someone has send you a leader election
     */
    public void checkForElections() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(RMI.TCP_ELECTION_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (checkForElections) {
            try {

            //get the client socket
                serverSocket.setSoTimeout(10000);

                Socket clientSocket = serverSocket.accept();
                System.out.println(clientSocket.getInetAddress());
            //the object input stream
            ObjectInputStream objInStream = new ObjectInputStream(clientSocket.getInputStream());
            // the election object
            Election election = (Election) objInStream.readObject();

            //check if there is a winner
            if (election.getState() == Election.ELECTED_WINNER) {
                //checking if you are the winner
                if (election.getWinnerIP() != this.getIp()) {

                    try {
                        //set the leader to the new leader that is elected
                        setLeader(election.getWinnerIP());
                        sendMessageLeft(election);
                    } catch (NotBoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            //checking if you have been elected as the leader
           else if (election.getWinnerIP() == this.getIp()) {
                election.setState(Election.ELECTED_WINNER);
                startRMIServer();
                sendMessageLeft(election);

            } else {
                election.vote(this.getIp(), this.getMemory());
                sendMessageLeft(election);
            }
        } catch(Exception e){
            }


    }


    }

    /**
     * send the updated leader election to your left ip
     * @param election
     */
    private void sendMessageLeft(Election election) {
        try {
            //setting up the tcp sending socket
            Socket clientSocket = new Socket(getLeftIP(), RMI.TCP_ELECTION_PORT);

            // writing the object to the output buffer
            ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            outToServer.writeObject(election);
            //closing the connections
            clientSocket.close();
            outToServer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * this will set up the rmi server if you have been elected as the leader
     */
    private void startRMIServer() {
        try {
            // create the new rmi server
            rmi = new RMI();
            // start the new rmi server
            rmi.start();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * this will give you you left ip
     * @return
     */
    private static String getLeftIP() {
        return leftIP;
    }


    /**
     * this will attempt to get the streaming music off of the sever and startplaying it
     * todo: i(GUY) need to check if it is working as the server is sending to large files
     */
    public void receiveMusic(){


        while(playingMusic) {
            try {

                byte b[] = null ;
                while(playingMusic )
                {
                    b = receiveThruUDP() ;
                    System.out.println(b);
                    toSpeaker( b ) ;
                }



            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }








    public byte[] receiveThruUDP()
    {
        try
        {
            DatagramSocket sock = new DatagramSocket(RMI.UDP_MUSIC_PORT) ;
            byte soundpacket[] = new byte[RMI.MUSIC_BYTE_SEND_SIZE] ;
            DatagramPacket datagram = new DatagramPacket( soundpacket , soundpacket.length , InetAddress.getByName("localhost") , RMI.UDP_MUSIC_PORT ) ;
            sock.receive( datagram ) ;
            sock.close() ;
            return datagram.getData(); // soundpacket ;
        }
        catch( Exception e )
        {
            System.out.println(" Unable to send soundpacket using UDP " ) ;
            return null ;
        }

    }


    public  void toSpeaker( byte soundbytes[] )
    {

        try{
            DataLine.Info dataLineInfo = new DataLine.Info( SourceDataLine.class , getAudioFormat() ) ;
            SourceDataLine sourceDataLine = (SourceDataLine)AudioSystem.getLine( dataLineInfo );
            sourceDataLine.open( getAudioFormat() ) ;
            sourceDataLine.start();
            int cnt = 0;
            sourceDataLine.write( soundbytes , 0, soundbytes.length );
            sourceDataLine.drain() ;
            sourceDataLine.close() ;
        }
        catch(Exception e )
        {
            System.out.println("not working in speakers " ) ;
        }

    }





    public  AudioFormat getAudioFormat()
    {
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

    public List<String> getMessages(){
        try {
            return remoteServer.messages();
        } catch (Exception e) {
                startElectionMessage();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            }
        return null;
    }
    public void addMessage(String message){
        try {
            remoteServer.addMessage(String.format("potato man %s: %s", userName,message),new VectorClock());
        } catch (Exception e) {
            startElectionMessage();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }


}







