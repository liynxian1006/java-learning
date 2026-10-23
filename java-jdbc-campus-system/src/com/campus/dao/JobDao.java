package com.campus.dao;

import com.campus.entity.Job;
import com.campus.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** 岗位数据访问层：对应简历接口 分页查询 / 按专业筛选 / 按薪资筛选 */
public class JobDao {

    /** 新增岗位 */
    public int add(Job j) {
        String sql = "INSERT INTO job(company_id, title, salary, major_required) VALUES(?,?,?,?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, j.getCompanyId());
            ps.setString(2, j.getTitle());
            ps.setString(3, j.getSalary());
            ps.setString(4, j.getMajorRequired());
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    /** 分页查询：page 从 1 开始，size 每页条数 */
    public List<Job> pageQuery(int page, int size) {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT id, company_id, title, salary, major_required FROM job LIMIT ?,?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, (page - 1) * size);
            ps.setInt(2, size);
            rs = ps.executeQuery();
            while (rs.next()) {
                Job j = new Job();
                j.setId(rs.getInt("id"));
                j.setCompanyId(rs.getInt("company_id"));
                j.setTitle(rs.getString("title"));
                j.setSalary(rs.getString("salary"));
                j.setMajorRequired(rs.getString("major_required"));
                list.add(j);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /** 按专业筛选岗位 */
    public List<Job> listByMajor(String major) {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT id, company_id, title, salary, major_required FROM job WHERE major_required LIKE ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConn();
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + major + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                Job j = new Job();
                j.setId(rs.getInt("id"));
                j.setCompanyId(rs.getInt("company_id"));
                j.setTitle(rs.getString("title"));
                j.setSalary(rs.getString("salary"));
                j.setMajorRequired(rs.getString("major_required"));
                list.add(j);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /** 按薪资筛选（简化：模糊匹配薪资字段） */
    public List<Job> listBySalary(String keyword) {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT id, company_id, title, salary, major_required FROM job WHERE salary LIKE ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConn();
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                Job j = new Job();
                j.setId(rs.getInt("id"));
                j.setCompanyId(rs.getInt("company_id"));
                j.setTitle(rs.getString("title"));
                j.setSalary(rs.getString("salary"));
                j.setMajorRequired(rs.getString("major_required"));
                list.add(j);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }
}
