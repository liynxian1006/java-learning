package com.campus.trace;

import com.campus.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * JDBC 逐步追踪 Demo —— 把 Dao 层「看不见的过程」全部打印出来。
 *
 * 运行方式（项目根目录 java-jdbc-campus-system 下）：
 *   编译：javac -encoding UTF-8 -cp "<driver.jar>" -d out src/com/campus/trace/*.java src/com/campus/util/DBUtil.java
 *   运行：java -cp "out;<driver.jar>" com.campus.trace.JdbcTraceDemo
 *
 * 观察重点：
 *   1) 拿连接前后的变化
 *   2) ? 占位符填值后，数据库真正执行的 SQL 长什么样
 *   3) ResultSet 如何一行行把表格搬进 Java 对象
 */
public class JdbcTraceDemo {

    public static void main(String[] args) {
        section("【第0步】JDBC 是什么：一句话");
        line("JDBC = Java 访问数据库的一套「标准插座」。");
        line("MySQL 有自己的驱动 jar，JDBC 只是统一的接口，所以换数据库代码几乎不用改。");

        section("【第1步】拿连接 Connection（相当于拨通数据库电话）");
        Connection conn = null;
        try {
            conn = DBUtil.getConn();
            line("连接对象 = " + conn.getClass().getName());
            line("是否有效 = " + conn.isValid(3) + "   → true 表示电话已接通");
            line("数据库产品 = " + conn.getMetaData().getDatabaseProductVersion());
        } catch (Exception e) {
            line("连接失败：" + e.getMessage());
            return;
        }

        section("【第2步】准备语句 PreparedStatement（把带空格的 SQL 交给数据库）");
        String sql = "INSERT INTO student(name, major, phone) VALUES(?,?,?)";
        line("我们交给 JDBC 的 SQL（注意有 3 个 ?）：");
        line("    " + sql);
        line("此时数据库还没收到任何数据，? 就是 3 个空格。");

        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            line("语句对象 = " + ps.getClass().getName());
            line("参数个数 = " + ps.getParameterMetaData().getParameterCount() + "  → 与 ? 的个数一致");

            section("【第3步】设参 setXxx（往 ? 里填数据，注意位置从 1 开始）");
            ps.setString(1, "李四");
            ps.setString(2, "软件工程");
            ps.setString(3, "13800001111");
            line("setString(1, \"李四\")     → 填第 1 个 ? = name");
            line("setString(2, \"软件工程\") → 填第 2 个 ? = major");
            line("setString(3, \"1380...\") → 填第 3 个 ? = phone");
            line("注意：位置从 1 开始，写成 0 会报错 SQLException。");

            section("【第4步】执行 executeUpdate（改数据 → 返回影响行数）");
            int rows = ps.executeUpdate();
            line("executeUpdate() 返回 = " + rows);
            line("含义：成功插入了 " + rows + " 行。若返回 0，说明一行都没插进去。");

            section("【第5步】查询 executeQuery（查数据 → 返回结果表 ResultSet）");
            PreparedStatement qps = conn.prepareStatement(
                    "SELECT id, name, major, phone FROM student ORDER BY id");
            ResultSet rs = qps.executeQuery();
            line("结果集对象 = " + rs.getClass().getName());
            line("它像一张「可以一行行往下走的指针」，初始停在第一行之前。");

            section("【第6步】while(rs.next()) 一行行把表格搬进 Java 对象");
            int count = 0;
            while (rs.next()) {
                count++;
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String major = rs.getString("major");
                String phone = rs.getString("phone");
                line("第 " + count + " 行 → id=" + id + ", name=" + name
                        + ", major=" + major + ", phone=" + phone);
            }
            line("一共读到 " + count + " 行。rs.next() 返回 false 时循环自然结束。");
            rs.close();
            qps.close();

            section("【第7步】关连接（在 finally 里做，漏了就撑爆数据库）");
            ps.close();
            line("关闭语句 = 已关闭");
            conn.close();
            line("关闭连接 = 已关闭");
            line("连接已归还数据库。下次操作会重新借一条 —— 这就是「借 → 用 → 还」的循环。");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, null);
        }

        section("【一句话总结】");
        line("拿连接 → 设参 → 执行 → 关连接，四步走完，一行数据就落库了。");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("  " + title);
        System.out.println("==================================================");
    }

    private static void line(String s) {
        System.out.println(s);
    }
}
