package JavaBean;

import java.util.Map;

/**
 * Created by yanzhang2 on 2017/4/15.
 */
public class UserInfo {

    private String userName;
    private Map<String,Integer> userTags;



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<String, Integer> getUserTags() {
        return userTags;
    }

    public void setUserTags(Map<String, Integer> userTags) {
        this.userTags = userTags;
    }
}
