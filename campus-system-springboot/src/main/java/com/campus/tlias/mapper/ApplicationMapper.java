package com.campus.tlias.mapper;

import com.campus.tlias.entity.Application;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 投递记录 Mapper（对应项目二的 ApplicationDao）。
 */
@Mapper
public interface ApplicationMapper {

    /**
     * 学生投递岗位：插入一条 status = '待审核' 的记录。
     *
     * <p>对比项目二：那边 status 是写在 SQL 里的默认值，这里显式传参，更清晰。
     */
    @Insert("INSERT INTO application(student_id, job_id, status) VALUES(#{studentId}, #{jobId}, #{status})")
    int apply(Application application);

    /**
     * 更新投递状态（状态流转：待审核 → 已通过 / 已拒绝）。
     */
    @Update("UPDATE application SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status);

    /** 查某个学生的全部投递记录（XML 实现，带连表） */
    List<Application> listByStudent(@Param("studentId") Integer studentId);
}
