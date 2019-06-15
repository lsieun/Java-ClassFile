package lsieun.db.utils;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Test;

public class DBCPUtilsTest {

    @Test
    public void testSelect_Raw() {
        Connection conn = null;
        try {
            String sql = "SELECT * FROM T_Dogs WHERE Id=?";
            // 获取连接
            conn = JDBCUtils.getConnection();
            // 创建DbUtils核心工具类对象
            QueryRunner qr = new QueryRunner();
            // 查询
            DogInfo dog = qr.query(conn, sql, new ResultSetHandler<DogInfo>() {
                // 如何封装一个DogInfo对象
                @Override
                public DogInfo handle(ResultSet rs) throws SQLException {
                    DogInfo dog = null;
                    if (rs.next()) {
                        dog = new DogInfo();
                        dog.setId(rs.getInt("Id"));
                        dog.setName(rs.getString("Name"));
                        dog.setAge(rs.getInt("Age"));
                        String strBirthDay = rs.getDate("BirthDay").toString();
                        Date birthDay = null;
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            birthDay = sdf.parse(strBirthDay);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        dog.setBirthday(birthDay);
                    }
                    return dog;
                }
            }, 2);
            // 打印输出结果
            System.out.println(dog);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            //关闭Connection对象
            DbUtils.closeQuietly(conn);
        }
    }

    @Test
    public void testSelect_BeanHandler() {
        Connection conn = null;
        try {
            String sql = "SELECT * FROM T_Dogs WHERE Id=?";
            // 获取连接
            conn = JDBCUtils.getConnection();
            // 创建DbUtils核心工具类对象
            QueryRunner qr = new QueryRunner();
            // 查询返回单个对象
            DogInfo dog = qr.query(conn, sql, new BeanHandler<DogInfo>(DogInfo.class), 2);
            //打印结果
            System.out.println(dog);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    @Test
    public void testSelect_BeanListHandler() {
        Connection conn = null;
        try {
            String sql = "SELECT * FROM T_Dogs";
            // 获取连接
            conn = JDBCUtils.getConnection();
            // 创建DbUtils核心工具类对象
            QueryRunner qr = new QueryRunner();
            // 查询全部数据
            List<DogInfo> list = qr.query(conn, sql, new BeanListHandler<DogInfo>(DogInfo.class));
            //打印结果
            for (DogInfo dog : list) {
                System.out.println(dog);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    @Test
    public void testSelect_ArrayHandler() {

    }

    @Test
    public void testScalarHandler() {
        Connection conn = null;
        try {
            String sql = "SELECT count(*) FROM T_Dogs";
            // 获取连接
            conn = JDBCUtils.getConnection();
            // 创建DbUtils核心工具类对象
            QueryRunner qr = new QueryRunner();
            // 查询数量
            Long count = qr.query(conn, sql, new ScalarHandler<Long>(1));//索引从1开始
            //打印结果
            System.out.println("共有" + count.intValue() + "个记录");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    @Test
    public void testInsertOnce() {
        Connection conn = null;
        try {
            //SQL语句
            String sql = "INSERT INTO T_Dogs(Name,Age,BirthDay) VALUES(?,?,?)";
            // 获取连接对象
            conn = JDBCUtils.getConnection();
            // 创建DbUtils核心工具类对象
            QueryRunner qr = new QueryRunner();
            //执行新增操作
            qr.update(conn, sql, "旺财", "2", "2015-08-08");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // 关闭
            DbUtils.closeQuietly(conn);
        }
    }

    @Test
    public void testBatchInsert() {
        Connection conn = null;
        try {
            //SQL语句
            String sql = "INSERT INTO T_Dogs(Name,Age,BirthDay) VALUES(?,?,?)";
            // 获取连接对象
            conn = JDBCUtils.getConnection();
            // 创建DbUtils核心工具类对象
            QueryRunner qr = new QueryRunner();
            // 执行批量新增
            qr.batch(conn, sql, new Object[][]{{"小狗1", "1", "2015-08-09"}, {"小狗2", "1", "2015-08-10"}});
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    @Test
    public void testDelete() {

    }

}