/**
 * Created by guylangford-lee on 21/05/15.
 */
public class User {
    private String name;
    private String ip;

    public User(String name, String ip){
        this.ip = ip;
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }
}
