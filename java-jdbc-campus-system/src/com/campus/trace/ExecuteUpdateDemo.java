package com.campus.trace;

import com.campus.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * executeUpdate 返回值 与 close 作用 的实证 Demo。
 *
 * 运行：java -cp "out;lib/mysql-connector-j-8.0.33.jar" com.campus.trace.ExecuteUpdateDemo
 */
public class ExecuteUpdateDemo {

    public static void main(String[] args) {
        Connection conn = null;
        Statement st = null;
        try {
            conn = DBUtil.getConn();

            section("① INSERT 一行 —— 看看返回几");
            PreparedStatement psIns = conn.prepareStatement(
                    "INSERT INTO student(name, major, phone) VALUES(?,?,?)");
            psIns.setString(1, "王五");
            psIns.setString(2, "数学与应用数学");
            psIns.setString(3, "13900000000");
            int rows1 = psIns.executeUpdate();
            line("executeUpdate() 返回 = " + rows1);
            line("→ 插入了 " + rows1 + " 行。成功就是 1，失败是 0。");
            psIns.close();

            // 拿到刚插入那行的 id
            int newId = 0;
            PreparedStatement psSel = conn.prepareStatement("SELECT MAX(id) FROM student");
            ResultSet rs = psSel.executeQuery();
            if (rs.next()) newId = rs.getInt(1);
            rs.close();
            psSel.close();
            line("刚插入那行的 id = " + newId);

            section("② UPDATE 存在的 id —— 看看返回几");
            PreparedStatement psUp = conn.prepareStatement(
                    "UPDATE student SET major=? WHERE id=?");
            psUp.setString(1, "统计学");
            psUp.setInt(2, newId);
            int rows2 = psUp.executeUpdate();
            line("executeUpdate() 返回 = " + rows2);
            line("→ 改了 " + rows2 + " 行（WHERE 匹配上了）。");
            psUp.close();

            section("③ UPDATE 不存在的 id —— 关键实验");
            PreparedStatement psUp2 = conn.prepareStatement(
                    "UPDATE student SET major=? WHERE id=?");
            psUp2.setString(1, "不存在的专业");
            psUp2.setInt(2, 999999);
            int rows3 = psUp2.executeUpdate();
            line("executeUpdate() 返回 = " + rows3);
            line("→ 一行都没改到，所以是 " + rows3 + "。");
            line("→ 这就是靠返回值判断「业务上有没有真的生效」的方式。");
            psUp2.close();

            section("④ DELETE 不存在的 id");
            PreparedStatement psDel = conn.prepareStatement("DELETE FROM student WHERE id=?");
            psDel.setInt(1, 999999);
            int rows4 = psDel.executeUpdate();
            line("executeUpdate() 返回 = " + rows4);
            psDel.close();

            section("⑤ 为什么必须有 close —— 数一数连接");
            line("现在这个程序一直占着 1 条数据库连接没还。");
            line("MySQL 默认最大连接数 max_connections = 151。");
            line("每个请求都借了不还 → 到第 152 个就报：");
            line("    java.sql.SQLException: Too many connections");
            line("所以 finally 里那一句 close 是铁律。");

            section("⑥ close(conn, ps, null) 的三个参数分别是什么");
            line("conn → 连接（电话）    必须关");
            line("ps   → 语句（单据）    必须关");
            line("null → 结果集（回执）  这次是查询之外的语句，没有结果集");
            line("close 内部会逐个判空，所以传 null 是安全的。");

            section("⑦ 对比：查询用的 executeQuery 没有「影响行数」");
            Statement simple = conn.createStatement();
            ResultSet rs2 = simple.executeQuery("SELECT COUNT(*) FROM student");
            int cnt = 0;
            if (rs2.next()) cnt = rs2.getInt(1);
            rs2.close();
            simple.close();
            line("当前 student 表共有 " + cnt + " 行（查询用的是 executeQuery）。");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, null, null);
            line("");
            line("finally 执行了 → 连接已归还，程序结束。");
        }
    }

    private static void section(String t) {
        System.out.println();
        System.out.println("--------------------------------");
        System.out.println("  " + t);
        System.out.println("--------------------------------");
    }

    private static void line(String s) {
        System.out.println(s);
    }
}
