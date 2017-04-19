package Utils;

import JavaBean.Tag;
import JavaBean.UserInfo;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static Constants.Constant.*;


/**
 * Created by yanzhang2 on 2017/4/17.
 */
public class UserUtil {


    public static String checkUserName(String userName) {
        if (userName == null) {
            return CHECK_FAIL;
        } else
            return CHECK_SUCCEED;
    }


    public static UserInfo getUserInfo(Connection conn, String userName , HttpSession session) {                   //检查userInfo表中是否存在user记录
        Statement st = null;
        ResultSet set = null;
        try {
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            set = st.executeQuery("select * from tb_userinfo where v_username ='" + userName +"'");
            set.last();
            if (set.getRow() == 0) {
                return null;
            } else{
                UserInfo userInfo = createUserInfoFromResultSet(set);
                session.setAttribute("userInfo",userInfo);
                return userInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
            DataBaseManager.getInstance().close(conn, st, set);
            return null;
        }
    }

    public static String getUserTags(Connection conn, HttpSession session) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        return JSONObject.toJSONString(userInfo.getUserTags());
    }


    public static Tag getFavouriteTag(HttpSession session) {
        Tag tag = new Tag();
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        Map<String, Integer> map = sortByValue(userInfo.getUserTags());
        tag.setTagName(map.entrySet().iterator().next().getKey());
        tag.setTagCount(map.entrySet().iterator().next().getValue());
        return tag;
    }


    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }



    public static UserInfo createUserInfoFromResultSet(ResultSet set) {
        UserInfo userInfo = new UserInfo();
        try {
            userInfo.setUserName(set.getString(2));
            JSONObject jsonObject = JSONObject.parseObject(set.getString(3));
            Map<String, Integer> map = (Map) jsonObject;
            userInfo.setUserTags(map);
            return userInfo;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }


}
