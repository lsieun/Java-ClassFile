package lsieun.db.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class JDBCUtils {
    private static final String url;
    private static final String user;
    private static final String password;
    private static final String driverClass;

    /**
     * 静态代码块中（只加载一次）
     */
    static {
        try {
            //读取db.properties文件
            InputStream inStream = JDBCUtils.class.getClassLoader().getResourceAsStream("db.properties");

            Properties props = new Properties();
            //加载文件
            props.load(inStream);
            //读取信息
            url = props.getProperty("url");
            user = props.getProperty("username");
            password = props.getProperty("password");
            driverClass = props.getProperty("driverClassName");

            //注册驱动程序
            Class.forName(driverClass);
        } catch (IOException e) {
            System.out.println("读取数据库配置文件出错");
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.out.println("数据库驱程程序注册出错");
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取数据库的连接
     *
     * @return 数据库连接
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("获取数据库连接出错");
            throw new RuntimeException(e);
        }
    }

    /**
     * 关闭Connection、Statement和ResultSet
     *
     * @param conn 数据库连接
     * @param stmt 执行SQL语句的命令
     * @param rs   结果集
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        closeQuietly(rs);
        closeQuietly(stmt);
        closeQuietly(conn);
    }

    /**
     * 安静的关闭数据库资源
     *
     * @param ac 实现了AutoCloseable接口的对象
     */
    public static void closeQuietly(AutoCloseable ac) {
        if (ac != null) {
            try {
                ac.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
