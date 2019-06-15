package lsieun.db.utils;

import static org.junit.Assert.*;

import java.sql.*;

import org.junit.Assert;
import org.junit.Test;

/**
 * CREATE TABLE `dumb_table` (
 * `id` INT PRIMARY KEY AUTO_INCREMENT,
 * `username` VARCHAR(20) NOT NULL,
 * `password` VARCHAR(10) NOT NULL
 * );
 */
public class JDBCUtilsTest {

    @Test
    public void testSelect() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            //1.获取连接
            conn = JDBCUtils.getConnection();

            //2.准备预编译的sql
            String sql = "SELECT * FROM dumb_table";

            //3.执行预编译sql语句(检查语法)
            pstmt = conn.prepareStatement(sql);

            //4.执行sql语句,得到返回结果
            rs = pstmt.executeQuery();

            //5.输出
            while (rs.next()) {
                int id = rs.getInt("Id");
                String username = rs.getString("UsErNaMe");
                String password = rs.getString("PASSWORD");
                System.out.println(id + "\t" + username + "\t" + password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils.close(conn, pstmt, rs);
        }
    }

    @Test
    public void testInsert() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            //1.获取连接
            conn = JDBCUtils.getConnection();

            //2.准备预编译的sql
            String sql = "INSERT INTO dumb_table(`username`, `password`) VALUES(?,?)";

            //3.执行预编译sql语句(检查语法)
            pstmt = conn.prepareStatement(sql);

            //4.设置参数值: 参数位置  从1开始
            pstmt.setString(1, "little明");
            pstmt.setString(2, "123abc");

            //5.发送参数，执行sql
            int count = pstmt.executeUpdate();
            System.out.println("影响了" + count + "行！");
            Assert.assertEquals(1, count);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils.close(conn, pstmt, null);
        }
    }

    @Test
    public void testUpdate() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            //1.获取连接
            conn = JDBCUtils.getConnection();

            //2.准备预编译的sql
            String sql = "UPDATE dumb_table SET UserName=?, PASSWORD=? WHERE Id=?";

            //3.执行预编译sql语句(检查语法)
            pstmt = conn.prepareStatement(sql);

            //4.设置参数值: 参数位置  从1开始
            pstmt.setString(1, "火星人");
            pstmt.setString(2, "456");
            pstmt.setInt(3, 1);

            //5.发送参数，执行sql
            int count = pstmt.executeUpdate();
            System.out.println("影响了" + count + "行！");
            Assert.assertEquals(1, count);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils.close(conn, pstmt, null);
        }
    }

    @Test
    public void testDelete() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            //1.获取连接
            conn = JDBCUtils.getConnection();

            //2.准备预编译的sql
            String sql = "DELETE FROM dumb_table WHERE Id=?";

            //3.执行预编译sql语句(检查语法)
            pstmt = conn.prepareStatement(sql);

            //4.设置参数值: 参数位置  从1开始
            pstmt.setInt(1, 1);

            //5.发送参数，执行sql
            int count = pstmt.executeUpdate();
            System.out.println("影响了" + count + "行！");
            Assert.assertEquals(1, count);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils.close(conn, pstmt, null);
        }
    }

}