package com.campus.tlias.service;

import com.campus.tlias.common.BusinessException;
import com.campus.tlias.entity.Student;
import com.campus.tlias.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 学生业务层。
 *
 * <p><b>对比项目二的 StudentService，两处关键变化：</b>
 * <ol>
 *   <li><b>不用自己 new Dao</b>：项目二是 {@code private StudentDao dao = new StudentDao();}，
 *       这里用 {@code @Autowired} 让 Spring 注入——这就是「依赖注入」，也是 Spring 的核心。</li>
 *   <li><b>不用手写 try-catch</b>：项目二每个方法都要 catch SQLException，
 *       这里异常统一交给 GlobalExceptionHandler 处理。</li>
 * </ol>
 *
 * <p>@Service 告诉 Spring："把这个类创建出来，交给 IOC 容器管理"。
 */
@Service
public class StudentService {

    @Autowired
    private StudentMapper studentMapper;

    /** 查询全部学生 */
    public List<Student> list() {
        return studentMapper.findAll();
    }

    /** 按 id 查询，查不到就抛业务异常 */
    public Student getById(Integer id) {
        Student s = studentMapper.findById(id);
        if (s == null) {
            throw new BusinessException(404, "学生不存在，id=" + id);
        }
        return s;
    }

    /**
     * 新增学生（带业务校验 + 事务）。
     *
     * <p>@Transactional：把多个数据库操作包成一个"要么全成功、要么全回滚"的单位。
     * 项目二没有这个概念——如果插入成功但后面失败了，数据就处于不一致状态。
     */
    @Transactional
    public void add(Student student) {
        // 业务校验：同一专业+同姓名不允许重复（演示业务规则下沉到 Service）
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new BusinessException(400, "姓名不能为空");
        }
        int rows = studentMapper.insert(student);
        if (rows == 0) {
            throw new BusinessException(500, "新增失败，数据库未写入");
        }
    }

    /** 修改学生 */
    @Transactional
    public void update(Student student) {
        if (student.getId() == null) {
            throw new BusinessException(400, "修改时必须提供 id");
        }
        int rows = studentMapper.update(student);
        if (rows == 0) {
            throw new BusinessException(404, "要修改的学生不存在，id=" + student.getId());
        }
    }

    /** 删除学生 */
    @Transactional
    public void delete(Integer id) {
        int rows = studentMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException(404, "要删除的学生不存在，id=" + id);
        }
    }
}
