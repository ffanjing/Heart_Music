import Utils.DataBaseManager;
import Utils.PlayListUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;

/**
 * Created by yanzhang2 on 2017/4/19.
 */
public class PlaySongServlet extends javax.servlet.http.HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       int songPosition = Integer.parseInt(req.getParameter("songPosition"));
        Connection conn = DataBaseManager.getInstance().getConnection();
        String type = req.getParameter("type");
        HttpSession session = req.getSession();
        PlayListUtil.PlaySong(session,conn,type,songPosition);
    }
}
