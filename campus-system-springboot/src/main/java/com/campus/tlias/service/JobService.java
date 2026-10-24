package com.campus.tlias.service;

import com.campus.tlias.entity.Job;
import com.campus.tlias.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 岗位业务层。
 */
@Service
public class JobService {

    @Autowired
    private JobMapper jobMapper;

    /** 查询全部岗位 */
    public List<Job> list() {
        return jobMapper.findAll();
    }

    /**
     * 分页查询。
     *
     * <p>对比项目二：那边在 Dao 里手写 offset = (page-1)*size，
     * 这里在 Service 层做换算，Dao 只管执行——职责更清晰。
     */
    public List<Job> pageQuery(Integer page, Integer size, Job condition) {
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 10 : size;
        int offset = (p - 1) * s;
        return jobMapper.pageQuery(condition, offset, s);
    }

    /** 按专业模糊查询 */
    public List<Job> listByMajor(String major) {
        return jobMapper.listByMajor(major);
    }

    /** 按薪资区间查询 */
    public List<Job> listBySalary(String min, String max) {
        return jobMapper.listBySalary(min, max);
    }
}
