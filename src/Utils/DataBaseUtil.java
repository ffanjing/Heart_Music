package Utils;

import Constants.Constant;

import java.sql.Connection;
import java.sql.DriverManager;


/**
 * Created by yanzhang2 on 2017/4/17.
 */
public class DataBaseUtil {

    public static Connection connectDataBase() {
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

    public static String checkDataBaseConnection(Connection conn) {                        //检查数据库连接
        if (null == conn) {
            return Constant.CHECK_FAIL;
        } else
            return Constant.CHECK_SUCCEED;
    }


}
