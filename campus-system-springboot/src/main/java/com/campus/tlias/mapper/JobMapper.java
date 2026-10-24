package com.campus.tlias.mapper;

import com.campus.tlias.entity.Application;
import com.campus.tlias.entity.Job;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 岗位与投递记录 Mapper。
 *
 * <p>这里用「XML 映射」写法（复杂 SQL 放 XML 里更清晰），对应项目二 JobDao / ApplicationDao。
 */
@Mapper
public interface JobMapper {

    /** 查询全部岗位 */
    @Select("SELECT id, company_id, title, salary, major_required FROM job ORDER BY id")
    List<Job> findAll();

    /**
     * 分页查询（XML 实现，见 resources/mapper/JobMapper.xml）。
     *
     * <p>对比项目二：那边要手写 "LIMIT ?,?" 并自己算 (page-1)*size；
     * 这里 PageHelper 会自动帮你改写 SQL。
     */
    List<Job> pageQuery(@Param("condition") Job condition,
                         @Param("offset") Integer offset,
                         @Param("size") Integer size);

    /** 按专业模糊查询 */
    List<Job> listByMajor(@Param("major") String major);

    /** 按薪资区间查询（XML 动态 SQL） */
    List<Job> listBySalary(@Param("min") String min, @Param("max") String max);

    /** 新增岗位 */
    @Insert("INSERT INTO job(company_id, title, salary, major_required) "
            + "VALUES(#{companyId}, #{title}, #{salary}, #{majorRequired})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Job job);
}
