package com.campus.tlias.controller;

import com.campus.tlias.common.Result;
import com.campus.tlias.entity.Job;
import com.campus.tlias.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 岗位接口层。
 */
@RestController
@RequestMapping("/job")
public class JobController {

    @Autowired
    private JobService jobService;

    /** 查询全部岗位：GET /job/list */
    @GetMapping("/list")
    public Result<List<Job>> list() {
        return Result.success(jobService.list());
    }

    /**
     * 分页查询：GET /job/page?page=1&size=10&majorRequired=计算机
     *
     * <p>@RequestParam(defaultValue = "1")：参数不传时用默认值，省得判空。
     */
    @GetMapping("/page")
    public Result<List<Job>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String majorRequired,
            @RequestParam(required = false) String title) {
        Job condition = new Job();
        condition.setMajorRequired(majorRequired);
        condition.setTitle(title);
        return Result.success(jobService.pageQuery(page, size, condition));
    }

    /** 按专业模糊查询：GET /job/major?major=计算机 */
    @GetMapping("/major")
    public Result<List<Job>> listByMajor(@RequestParam String major) {
        return Result.success(jobService.listByMajor(major));
    }

    /** 按薪资区间查询：GET /job/salary?min=5000&max=15000 */
    @GetMapping("/salary")
    public Result<List<Job>> listBySalary(@RequestParam(required = false) String min,
                                          @RequestParam(required = false) String max) {
        return Result.success(jobService.listBySalary(min, max));
    }
}
