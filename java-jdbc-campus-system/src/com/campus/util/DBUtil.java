package com.campus.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库连接工具：统一加载驱动、获取连接、释放资源。
 * 对应简历描述：dao 层「统一封装连接与异常处理」。
 */
public class DBUtil {
    // 改成你自己的数据库名、用户名、密码
    private static final String URL = "jdbc:mysql://localhost:3306/campus_job_db?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8";
    private static final String USER = "root";
    /** 数据库密码：优先读环境变量 DB_PASSWORD，未设置时用本机默认值（仅本地学习用） */
    private static final String PASSWORD = env("DB_PASSWORD", "123456");

    private static String env(String key, String defaultValue) {
        String v = System.getenv(key);
        return (v == null || v.isEmpty()) ? defaultValue : v;
    }

    // 静态块：类加载时注册一次 JDBC 驱动
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** 获取数据库连接 */
    public static Connection getConn() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /** 统一释放资源（反向关闭：结果集 → 语句 → 连接） */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
