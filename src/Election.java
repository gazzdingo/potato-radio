import java.io.Serializable;

/**
 * Created by guylangford-lee on 22/05/15.
 */

public class Election implements Serializable {

    public static final int ELECTED_WINNER = 1;
    public static final int ELECTING_WINNER = 0;

    private int state;
    private String winningIP;
    private long winningMemory;


    public Election(String ip, long memory){
        this.winningIP = ip;
        this.winningMemory = memory;
    }

     public void checkAndUpDateElection(String ip, long memory){

         if(memory > winningMemory){
             winningMemory = memory;
             winningIP = ip;
         }

     }

    public int getState() {
        return state;
    }

    public long getWinnerMemory() {
        return winningMemory;
    }

    public String getWinnerIP() {
        return winningIP;
    }
}
