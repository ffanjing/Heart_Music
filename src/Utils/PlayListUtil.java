package Utils;

import Constants.Constant;
import JavaBean.Song;
import JavaBean.Tag;
import JavaBean.UserInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanzhang2 on 2017/4/18.
 */
public class PlayListUtil {

    public static ArrayList<Song> getRecommendList(HttpServletResponse resp, Connection conn, String userName,HttpSession session) {
        if (check(resp, conn, userName,session).equals(Constant.CHECK_FAIL))
            return null;
        ArrayList<Song> songList;
        String tagDesc = UserUtil.getUserTags(conn, session);
        if (tagDesc.equals("null") || tagDesc.equals(""))
            songList = getRandomList(conn);
        else if (tagDesc.equals(Constant.DATABASE_ERR))
            return null;
        else
            songList = getSpecificRecommendList(conn, session);
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


    private static ArrayList<Song> getSpecificRecommendList(Connection conn, HttpSession session) {
        ArrayList<Song> songList = new ArrayList<>();
        Statement st = null;
        ResultSet set = null;
        try {
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            Tag tag = UserUtil.getFavouriteTag(session);
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

    public static ArrayList<Song> getMayLikeList(HttpServletResponse resp, Connection conn, String userName,HttpSession session) {
        if (check(resp, conn, userName,session).equals(Constant.CHECK_FAIL))
            return null;
        ArrayList<Song> songList = null;
        String tagDesc = UserUtil.getUserTags(conn, session);
        if (tagDesc == null || tagDesc.equals(""))
            songList = getRandomList(conn);
        else
            songList = getSpecificMayLikeList(conn, session);
        if (null != songList) {
            String mayLike = JSON.toJSONString(songList);
            JSONArray jsonArray = JSON.parseArray(mayLike);
            writeResponseResult(resp, "success", jsonArray);
        }
        return songList;
    }


    private static ArrayList<Song> getSpecificMayLikeList(Connection conn,HttpSession session) {
        ArrayList<Song> songList = new ArrayList<>();
        Statement st = null;
        ResultSet set = null;
        try {
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            UserInfo userInfo = (UserInfo)session.getAttribute("userInfo");
            String tagDesc = JSONObject.toJSONString(userInfo.getUserTags());
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

    private static String check(HttpServletResponse resp, Connection conn, String userName,HttpSession session) {
        if (UserUtil.checkUserName(userName).equals(Constant.CHECK_FAIL)) {
            writeResponseResult(resp, "error", Constant.USER_NULL);
            return Constant.CHECK_FAIL;
        }
        if (null == conn) {
            writeResponseResult(resp, "error", Constant.DATABASE_ERR);
            return Constant.CHECK_FAIL;
        }
        UserInfo userInfo = UserUtil.getUserInfo(conn, userName,session);
        if (null == userInfo) {
            writeResponseResult(resp, "error", Constant.NO_USERINFO);
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


    public static ArrayList<Song> getAllSongsPlayList(HttpServletResponse resp, Connection conn) {
        if (null == conn) {
            writeResponseResult(resp, "error", Constant.DATABASE_ERR);
            return null;
        }
        ArrayList<Song> songList = new ArrayList<>();
        Statement st = null;
        ResultSet set = null;
        try {
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            set = st.executeQuery("select * from tb_music ");
            while (set.next()) {
                Song song = SongUtil.createSongFromResultSet(set);
                if (null != song)
                    songList.add(song);
            }
            DataBaseManager.getInstance().close(conn, st, set);
            if (songList.size() > 0){
                String allSongs = JSON.toJSONString(songList);
                JSONArray jsonArray = JSON.parseArray(allSongs);
                writeResponseResult(resp, "success", jsonArray);
            }
            return songList;
        } catch (SQLException e) {
            e.printStackTrace();
            DataBaseManager.getInstance().close(conn, st, set);
            return null;
        }
    }


    public static void PlaySong(HttpSession session,Connection conn ,String type, int songPosition){
        ArrayList<Song> playList = new ArrayList<>();
        switch(type){
            case "1" : playList = (ArrayList<Song>) session.getAttribute("recommendPlayList");
                break;
            case "2" : playList = (ArrayList<Song>) session.getAttribute("mayLikePlayList");
                break;
            case "3" : playList = (ArrayList<Song>) session.getAttribute("allSongsPlayList");
                break;
            default :
                break;
        }
        Song song = playList.get(songPosition);
        String songTags[] = song.getSongTags().split(",");
        Statement st = null;
        try {
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            st.execute("update tb_music set n_count = n_count + 1 where n_id = " + song.getSongID());
            UserInfo userInfo = (UserInfo)session.getAttribute("userInfo");
            Map<String,Integer> tagsMap = userInfo.getUserTags();
            tagsMap = addUserTags(tagsMap,songTags);
            userInfo.setUserTags(tagsMap);
            String tagDesc = JSONObject.toJSONString(tagsMap);
            st.execute("update tb_userinfo set v_usertags = '" + tagDesc + "'" + "where v_username = '" + userInfo.getUserName() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                conn.close();
                if (st != null)
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static Map<String,Integer> addUserTags(Map<String,Integer> tagsMap,String [] tags){
        if (null == tagsMap){
            tagsMap = new HashMap<>();
            for (int i = 0;i<tags.length;i++){
                tagsMap.put(tags[i],1);
            }
        }else{
            for (int i = 0;i<tags.length;i++){
                if (null == tagsMap.get(tags[i]))
                    tagsMap.put(tags[i],1);
                else
                    tagsMap.put(tags[i],tagsMap.get(tags[i]) + 1);
            }
        }

        return tagsMap;
    }

}
