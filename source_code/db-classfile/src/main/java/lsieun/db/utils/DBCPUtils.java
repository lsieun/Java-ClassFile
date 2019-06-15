package lsieun.db.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;

public class DBCPUtils {
    private static final DataSource dataSource;

    static {
        try {
            // 获取文件流
            InputStream inStream = DBCPUtils.class.getClassLoader().getResourceAsStream("db.properties");
            //创建Properties对象
            Properties prop = new Properties();
            // 加载属性配置文件
            prop.load(inStream);

            // 根据prop配置，直接创建数据源对象
            dataSource = BasicDataSourceFactory.createDataSource(prop);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("创建DBCP数据库连接池失败!");
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            System.out.println("获取数据库连接出错");
            throw new RuntimeException(e);
        }
    }
}
