import JavaBean.Song;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yanzhang2 on 2017/4/15.
 */
public class RecommendServlet extends javax.servlet.http.HttpServlet {

    private static final String CHECK_FAIL = "CHECK_FAIL";
    private static final String CHECK_SUCCEED = "CHECK_SUCCEED";
    private static final String NO_USERID = "NO_USERID";
    private static final String NO_USERTAGS = "NO_USERTAGS";
    private static final String USER_NULL = "USER_NULL";
    private static final String DATABASE_ERR = "DATABASE_ERR";
    private HttpServletResponse resp;
    private HttpServletRequest req;
    private static final String MYSQL_URL = "jdbc:mysql://120.55.98.138:3306/heart_music";
    private static final String MYSQL_USERNAME = "zdhdqkz";
    private static final String MYSQL_PASSWORD = "zdhdqkz";


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.req = req;
        this.resp = resp;
        resp.setContentType("text/html;charset=UTF-8");
        String userID = req.getParameter("userID");
        getRecommendSongs(userID);
    }

    private void getRecommendSongs(String userID) {
        if (CHECK_FAIL.equals(checkUserID(userID)))
            return;
        Connection conn = connectDataBase();
        if (CHECK_FAIL.equals(checkDataBaseConnection(conn)))
            return;
        if (CHECK_FAIL.equals(checkUserInfo(conn, userID))) {
            closeDataBaseConnection(conn);
            return;
        }
        String tagDesc = getUserTags(conn, userID);
        if (CHECK_FAIL.equals(tagDesc))
            return;
        else {
            if (tagDesc == null)
                getRandomRecommend(conn);
            else
                getSpecificRecommend();
        }

    }

    private void getSpecificRecommend() {
    }

    private void getRandomRecommend(Connection conn) {
        Statement st = null;
        ArrayList<Song> songList = new ArrayList<>();
        try {
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet set = st.executeQuery("select * from tb_music order by rand() limit 10");
            while (set.next()){
                Song song  = createSongFromResultSet(set);
                if (null != song)
                    songList.add(song);
            }
            String randomRecommend = JSON.toJSONString(songList);
            JSONArray jsonArray = JSON.parseArray(randomRecommend);
            JSONObject json = new JSONObject();
            json.put("status","ok");
            json.put("data",jsonArray);
            resp.getWriter().println(json);
            //writeResponseResult("success",randomRecommend);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private Song createSongFromResultSet(ResultSet set){
        Song song = new Song();
        try {
            song.setSongID(set.getInt(1));
            song.setSongName(set.getString(2));
            song.setSongArtist(set.getString(3));
            song.setSongAlbum(set.getString(4));
            song.setSongURL(set.getString(5));
            song.setSongCover(set.getString(6));
            song.setSongTags(set.getString(7));
            song.setSongCount(set.getInt(8));
            return song;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    private String checkUserID(String userID) {
        if (userID == null) {
            writeResponseResult(USER_NULL, "");
            return CHECK_FAIL;
        } else
            return CHECK_SUCCEED;
    }

    private String checkDataBaseConnection(Connection conn) {                        //检查数据库连接
        if (null == conn) {
            writeResponseResult(DATABASE_ERR, "");
            return CHECK_FAIL;
        } else
            return CHECK_SUCCEED;
    }

    private String checkUserInfo(Connection conn, String userID) {                   //检查userInfo表中是否存在user记录
        try {
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet set = st.executeQuery("select * from tb_userinfo where n_userid = " + Integer.parseInt(userID));
            set.last();
            if (set.getRow() == 0) {
                writeResponseResult(NO_USERID, "");
                return CHECK_FAIL;
            } else
                return CHECK_SUCCEED;
        } catch (Exception e) {
            e.printStackTrace();
            writeResponseResult(DATABASE_ERR, "");
            return CHECK_FAIL;
        }
    }

    private String getUserTags(Connection conn, String userID) {
        try {
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet set = st.executeQuery("select v_usertags from tb_userinfo where  n_userid = " + Integer.parseInt(userID));
            set.next();
            return set.getString(1);
        } catch (SQLException e) {
            writeResponseResult(DATABASE_ERR, "");
            e.printStackTrace();
            return CHECK_FAIL;
        }
    }

    private void writeResponseResult(String status, String data) {
        HashMap<String, String> resultMap = new HashMap<>();
        resultMap.put("status", status);
        resultMap.put("data", data);
        try {
            String result = JSON.toJSONString(resultMap);
            resp.getWriter().println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Connection connectDataBase() {
        Connection conn;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USERNAME, MYSQL_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return conn;
    }

    private void closeDataBaseConnection(Connection conn) {
        try {
            conn.rollback();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
