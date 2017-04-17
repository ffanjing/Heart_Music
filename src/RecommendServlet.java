import Constants.Constant;
import JavaBean.Song;
import JavaBean.Tag;
import Utils.DataBaseUtil;
import Utils.SongUtil;
import Utils.UserUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by yanzhang2 on 2017/4/15.
 */
public class RecommendServlet extends javax.servlet.http.HttpServlet {

    private HttpServletResponse resp;
    private HttpServletRequest req;

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
        String nResult = "";
        nResult = UserUtil.checkUserID(userID);
        if (!Constant.CHECK_SUCCEED.equals(nResult)) {
            writeResponseResult("error", Constant.USER_NULL);
            return;
        }
        Connection conn = DataBaseUtil.connectDataBase();
        nResult = DataBaseUtil.checkDataBaseConnection(conn);
        if (!Constant.CHECK_SUCCEED.equals(nResult)) {
            writeResponseResult("error", Constant.DATABASE_ERR);
        }
        nResult = UserUtil.checkUserInfo(conn, userID);
        if (!Constant.CHECK_SUCCEED.equals(nResult)) {
            writeResponseResult("error", nResult);
            closeDataBaseConnection(conn);
            return;
        }
        String tagDesc = UserUtil.getUserTags(conn, userID);
        if (Constant.CHECK_FAIL.equals(tagDesc))
            writeResponseResult("error", Constant.DATABASE_ERR);
        else {
            if (tagDesc == null || tagDesc.equals(""))
                getRandomRecommend(conn);
            else
                getSpecificRecommend(conn, tagDesc);
        }

    }

    private void getSpecificRecommend(Connection conn, String tagDesc) {
        Statement st;
        ArrayList<Song> songList = new ArrayList<>();
        try {
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            Tag tag = UserUtil.getFavouriteTag(tagDesc);
            ResultSet set = st.executeQuery("select * from tb_music where v_tags like '%" + tag.getTagName() +"%' ORDER BY rand() limit 10");
            while (set.next()) {
                Song song = SongUtil.createSongFromResultSet(set);
                if (null != song)
                    songList.add(song);
            }
            String randomRecommend = JSON.toJSONString(songList);
            JSONArray jsonArray = JSON.parseArray(randomRecommend);
            writeResponseResult("success", jsonArray);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void getRandomRecommend(Connection conn) {
        Statement st;
        ArrayList<Song> songList = new ArrayList<>();
        try {
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet set = st.executeQuery("select * from tb_music order by rand() limit 10");
            while (set.next()) {
                Song song = SongUtil.createSongFromResultSet(set);
                if (null != song)
                    songList.add(song);
            }
            String randomRecommend = JSON.toJSONString(songList);
            JSONArray jsonArray = JSON.parseArray(randomRecommend);
            writeResponseResult("success", jsonArray);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    private void writeResponseResult(String status, Object data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("data", data);
        try {
            resp.getWriter().println(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
