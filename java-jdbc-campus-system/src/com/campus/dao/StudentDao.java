package com.campus.dao;

import com.campus.entity.Student;
import com.campus.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** 学生数据访问层：对应简历接口 /student/add /list /update /delete */
public class StudentDao {

    /** 新增学生 */
    public int add(Student s) {
        String sql = "INSERT INTO student(name, major, phone) VALUES(?,?,?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConn();
            ps = conn.prepareStatement(sql);
            ps.setString(1, s.getName());
            ps.setString(2, s.getMajor());
            ps.setString(3, s.getPhone());
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    /** 查询全部学生 */
    public List<Student> list() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT id, name, major, phone FROM student";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConn();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setMajor(rs.getString("major"));
                s.setPhone(rs.getString("phone"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /** 修改学生 */
    public int update(Student s) {
        String sql = "UPDATE student SET name=?, major=?, phone=? WHERE id=?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConn();
            ps = conn.prepareStatement(sql);
            ps.setString(1, s.getName());
            ps.setString(2, s.getMajor());
            ps.setString(3, s.getPhone());
            ps.setInt(4, s.getId());
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    /** 删除学生 */
    public int delete(int id) {
        String sql = "DELETE FROM student WHERE id=?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }
}
