package Utils;

import Constants.Constant;

import java.sql.*;

/**
 * Created by yanzhang2 on 2017/4/18.
 */
public class DataBaseManager {

    private static DataBaseManager instance;

    public static synchronized DataBaseManager getInstance() {
        if (instance == null) {
            instance = new DataBaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        Connection conn;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(Constant.MYSQL_URL, Constant.MYSQL_USERNAME, Constant.MYSQL_PASSWORD);
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close(Connection conn, Statement st, ResultSet rs) {
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        if (st != null)
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        if (rs != null)
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

    }

}
