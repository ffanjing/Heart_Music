import JavaBean.Song;
import Utils.DataBaseManager;
import Utils.PlayListUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * Created by yanzhang2 on 2017/4/19.
 */
public class GetAllSongsServlet extends javax.servlet.http.HttpServlet{


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ArrayList<Song> allSongsList;
        Connection conn = DataBaseManager.getInstance().getConnection();
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("text/html;charset=UTF-8");
        HttpSession session = req.getSession();
        allSongsList = PlayListUtil.getAllSongsPlayList(resp, conn);
        if (null != allSongsList) {
            session.setAttribute("allSongsPlayList", allSongsList);
        }
    }
}
