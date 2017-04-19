import JavaBean.Song;
import JavaBean.UserInfo;
import Utils.DataBaseManager;
import Utils.PlayListUtil;
import Utils.UserUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * Created by yanzhang2 on 2017/4/15.
 */
public class RecommendServlet extends javax.servlet.http.HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ArrayList<Song> recommendList;
        Connection conn = DataBaseManager.getInstance().getConnection();
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("text/html;charset=UTF-8");
        String userName = req.getParameter("userName");
        HttpSession session = req.getSession();
        recommendList = PlayListUtil.getRecommendList(resp, conn, userName,session);
        if (null != recommendList) {
            session.setAttribute("recommendPlayList", recommendList);
        }

    }

}
