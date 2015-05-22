import java.util.List;

/**
 * Created by guylangford-lee on 21/05/15.
 */
public interface ServerRemote {
    /**
     * the unique value of the client or server
     * @param bindName
     */
    void setBindName(String bindName);

    /**
     * return the unique bind  name of the server/client
     * @return BindName
     */
    String getBindName();

    /**
     * get the address of the client/server
     * eg 192.168.1.2, localhost
     * @return address
     */
    String getAddress();


    /**
     * returns the port that the client/server is on
     * @return
     */
    int getport();

    /**
     * for when a leader is leaving add the new leader
     * @param leader
     */
    void setLeader(ServerRemote leader);

    /**
     * will return the leader
     * @return ServerRemote of the server leader
     */
    ServerRemote getLeader();

    /**
     * sets yourself as a leader
     */
    void setLeader();

    /**
     * add a new client to the the network
     * @param remote client.
     */
    void addremote(ServerRemote remote);

    /**
     * check if it is the leader
     */
    boolean isLeader();
    List<ServerRemote> getRemote();

}
