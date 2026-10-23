package com.campus.dao;

import com.campus.entity.Application;
import com.campus.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** 投递数据访问层：对应简历接口 投递(apply) / 状态流转(updateStatus) / 按学生查询 */
public class ApplicationDao {

    /** 学生投递岗位 */
    public int apply(int studentId, int jobId) {
        String sql = "INSERT INTO application(student_id, job_id, status) VALUES(?,?, '待审核')";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setInt(2, jobId);
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    /** 状态流转：待审核 → 已通过 / 已拒绝 */
    public int updateStatus(int applicationId, String status) {
        String sql = "UPDATE application SET status=? WHERE id=?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConn();
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, applicationId);
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    /** 查询某学生的全部投递记录 */
    public List<Application> listByStudent(int studentId) {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT id, student_id, job_id, status FROM application WHERE student_id=?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Application a = new Application();
                a.setId(rs.getInt("id"));
                a.setStudentId(rs.getInt("student_id"));
                a.setJobId(rs.getInt("job_id"));
                a.setStatus(rs.getString("status"));
                list.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }
}
