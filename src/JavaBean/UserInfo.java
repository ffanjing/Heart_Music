package JavaBean;

import java.util.HashMap;

/**
 * Created by yanzhang2 on 2017/4/15.
 */
public class UserInfo {

    private String userID;
    private String userName;
    private HashMap<String,Integer> userTags;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public HashMap<String, Integer> getUserTags() {
        return userTags;
    }

    public void setUserTags(HashMap<String, Integer> userTags) {
        this.userTags = userTags;
    }
}
