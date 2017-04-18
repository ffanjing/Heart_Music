package Utils;

import JavaBean.Tag;
import com.alibaba.fastjson.JSONObject;

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


    public static String checkUserInfo(Connection conn, String userName) {                   //检查userInfo表中是否存在user记录
        Statement st = null;
        ResultSet set = null;
        try {
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            set = st.executeQuery("select * from tb_userinfo where v_username ='" + userName +"'");
            set.last();
            if (set.getRow() == 0) {
                return NO_USERNAME;
            } else
                return CHECK_SUCCEED;
        } catch (Exception e) {
            e.printStackTrace();
            DataBaseManager.getInstance().close(conn, st, set);
            return DATABASE_ERR;
        }
    }

    public static String getUserTags(Connection conn, String userName) {
        try {
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet set = st.executeQuery("select v_usertags from tb_userinfo where v_username ='" + userName +"'");
            set.next();
            return set.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return DATABASE_ERR;
        }
    }


    public static Tag getFavouriteTag(String tagDesc) {
        ArrayList<Tag> tagList = new ArrayList<>();
        Tag tag = new Tag();
        JSONObject jsonObject = JSONObject.parseObject(tagDesc);
        Map<String, Integer> map = sortByValue((Map) jsonObject);
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


}
