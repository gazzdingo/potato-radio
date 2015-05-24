import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.*;
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
    private boolean playingMusic = true;
    private AdvancedPlayer player;
    private String leftIP;
    private String rightIP;
    private RMI rmi;

    public Client(String serverURI) throws RemoteException, NotBoundException, MalformedURLException, UnknownHostException {
        setLeader(serverURI);
        setMemory(Runtime.getRuntime().freeMemory());
        setIp(InetAddress.getLocalHost().getHostAddress());
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
            if(remoteServer.ipAddresses().contains(getIp())) {
                //adding this clients ip to the remote server
                remoteServer.addHost(getIp());
                //looping through setting up the the right and left ip for the leader election
                for (int i =0 ; i< remoteServer.ipAddresses().size(); i++){
                    //checking to make sure that it is not the first client
                        if(i != 0){
                            //checking if it is the end client
                            if(remoteServer.ipAddresses().size()-1 == i){
                                //assigning the left and right ip
                                leftIP = remoteServer.ipAddresses().get(i-1);
                                rightIP = remoteServer.ipAddresses().get(0);
                            }else{
                                //assigning  the left and right ip
                                leftIP = remoteServer.ipAddresses().get(i-1);
                                rightIP = remoteServer.ipAddresses().get(i+1);
                            }

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

        while (checkForElections) {
            try {
            ServerSocket serverSocket = new ServerSocket(RMI.TCP_ELECTION_PORT);

            //get the client socket
            Socket clientSocket = serverSocket.accept();
            //the object input stream
            ObjectInputStream objInStream = new ObjectInputStream(clientSocket.getInputStream());
            // the election object
            Election election = (Election) objInStream.readObject();

            //check if there is a winner
            if (election.getState() == Election.ELECTED_WINNER) {
                //checking if you are the winner
                if (election.getWinnerIP() == this.getIp()) {
                    startRMIServer();
                } else {
                    try {
                        //set the leader to the new leader that is elected
                        setLeader(election.getWinnerIP());
                    } catch (NotBoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            //checking if you have been elected as the leader
            if (election.getWinnerIP() == this.getIp()) {
                election.setState(Election.ELECTED_WINNER);
            } else {
                election.vote(this.getIp(), this.getMemory());
                sendMessageLeft(election);
            }
        } catch(Exception e){
                System.out.println(e.getMessage());
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
    private String getLeftIP() {
        return leftIP;
    }


    /**
     * this will attempt to get the streaming music off of the sever and startplaying it
     * todo: i(GUY) need to check if it is working as the server is sending to large files
     */
    public void receiveMusic(){
        while(playingMusic) {
            try {

                //setting up the udp receivers
                DatagramSocket socket = new DatagramSocket(RMI.UDP_MUSIC_PORT);
                byte[] buffer = new byte[RMI.MUSIC_BYTE_SEND_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                while (playingMusic) {
                    // Wait to receive a datagram


                    socket.receive(packet);
                    // Convert the contents to a string, and display them
                    String msg = new String(buffer, 0, packet.getLength());

                    byte[] data = packet.getData();

                    ByteArrayInputStream in = new ByteArrayInputStream(data);


                    this.player = new AdvancedPlayer(in);
                    player.play();

                    // Reset the length of the packet before reusing it.
                    packet.setLength(buffer.length);


                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}
