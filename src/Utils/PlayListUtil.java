package Utils;

import Constants.Constant;
import JavaBean.Song;
import JavaBean.Tag;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by yanzhang2 on 2017/4/18.
 */
public class PlayListUtil {

    public static ArrayList<Song> getRecommendList(HttpServletResponse resp, Connection conn, String userName) {
        if (check(resp, conn, userName).equals(Constant.CHECK_FAIL))
            return null;
        ArrayList<Song> songList;
        String tagDesc = UserUtil.getUserTags(conn, userName);
        if (tagDesc == null || tagDesc.equals(""))
            songList = getRandomList(conn);
        else if (tagDesc.equals(Constant.DATABASE_ERR))
            return null;
        else
            songList = getSpecificRecommendList(conn, tagDesc);
        if (null != songList) {
            String randomRecommend = JSON.toJSONString(songList);
            JSONArray jsonArray = JSON.parseArray(randomRecommend);
            writeResponseResult(resp, "success", jsonArray);
        }
        return songList;
    }

    private static ArrayList<Song> getRandomList(Connection conn) {
        ArrayList<Song> songList = new ArrayList<>();
        Statement st = null;
        ResultSet set = null;
        try {
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            set = st.executeQuery("select * from tb_music order by rand() limit 10");
            while (set.next()) {
                Song song = SongUtil.createSongFromResultSet(set);
                if (null != song)
                    songList.add(song);
            }
            DataBaseManager.getInstance().close(conn, st, set);
            return songList;
        } catch (SQLException e) {
            e.printStackTrace();
            DataBaseManager.getInstance().close(conn, st, set);
            return null;
        }
    }


    private static ArrayList<Song> getSpecificRecommendList(Connection conn, String tagDesc) {
        ArrayList<Song> songList = new ArrayList<>();
        Statement st = null;
        ResultSet set = null;
        try {
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            Tag tag = UserUtil.getFavouriteTag(tagDesc);
            set = st.executeQuery("select * from tb_music where v_tags like '%" + tag.getTagName() + "%' ORDER BY rand() limit 10");
            while (set.next()) {
                Song song = SongUtil.createSongFromResultSet(set);
                if (null != song)
                    songList.add(song);
            }
            DataBaseManager.getInstance().close(conn, st, set);
            return songList;
        } catch (SQLException e) {
            e.printStackTrace();
            DataBaseManager.getInstance().close(conn, st, set);
            return null;
        }
    }

    public static ArrayList<Song> getMayLikeList(HttpServletResponse resp, Connection conn, String userName) {
        if (check(resp, conn, userName).equals(Constant.CHECK_FAIL))
            return null;
        ArrayList<Song> songList = null;
        String tagDesc = UserUtil.getUserTags(conn, userName);
        if (tagDesc == null || tagDesc.equals(""))
            songList = getRandomList(conn);
        else
            songList = getSpecificMayLikeList(conn, tagDesc);
        if (null != songList) {
            String mayLike = JSON.toJSONString(songList);
            JSONArray jsonArray = JSON.parseArray(mayLike);
            writeResponseResult(resp, "success", jsonArray);
        }
        return songList;
    }


    private static ArrayList<Song> getSpecificMayLikeList(Connection conn, String tagDesc) {
        ArrayList<Song> songList = new ArrayList<>();
        Statement st = null;
        ResultSet set = null;
        try {
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            Tag tag = UserUtil.getFavouriteTag(tagDesc);
            String temp = tagDesc.replace("\"", "").replace(":", "").replaceAll("\\d+", "")
                    .replace("{", "").replace("}", "");
            String[] tags = temp.split(",");
            String sql = "select * from tb_music where v_tags not like ";
            for (int i = 0; i < tags.length; i++) {
                if (i != tags.length - 1)
                    sql = sql + "'%" + tags[i] + "%'" + "and v_tags not like";
                else
                    sql = sql + "'%" + tags[i] + "%'" + "limit 10";
            }
            set = st.executeQuery(sql);
            while (set.next()) {
                Song song = SongUtil.createSongFromResultSet(set);
                if (null != song)
                    songList.add(song);
            }
            DataBaseManager.getInstance().close(conn, st, set);
            return songList;
        } catch (SQLException e) {
            e.printStackTrace();
            DataBaseManager.getInstance().close(conn, st, set);
            return null;
        }
    }

    private static String check(HttpServletResponse resp, Connection conn, String userName) {
        if (UserUtil.checkUserName(userName).equals(Constant.CHECK_FAIL)) {
            writeResponseResult(resp, "error", Constant.USER_NULL);
            return Constant.CHECK_FAIL;
        }
        if (null == conn) {
            writeResponseResult(resp, "error", Constant.DATABASE_ERR);
            return Constant.CHECK_FAIL;
        }
        String nResult = UserUtil.checkUserInfo(conn, userName);
        if (!nResult.equals(Constant.CHECK_SUCCEED)) {
            writeResponseResult(resp, "error", nResult);
            return Constant.CHECK_FAIL;
        }
        return Constant.CHECK_SUCCEED;
    }


    private static void writeResponseResult(HttpServletResponse resp, String status, Object data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("data", data);
        try {
            resp.getWriter().println(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
