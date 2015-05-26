package LeaderElection;

/**
 * Created by Ben on 26/05/15.
 */
public class DeadNodeException extends Exception {

    public DeadNodeException() {
        super();
    }

    public DeadNodeException(String message) {
        super(message);
    }

}
