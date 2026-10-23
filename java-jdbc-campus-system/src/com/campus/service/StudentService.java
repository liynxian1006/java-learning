package com.campus.service;

import com.campus.dao.StudentDao;
import com.campus.entity.Student;

import java.util.List;

/**
 * 学生业务层（service）：在 dao 之上做业务校验与编排。
 * 这是简历强调的「entity / dao / service 分层」中的 service 层示例。
 */
public class StudentService {
    private StudentDao dao = new StudentDao();

    /** 简单新增 */
    public int add(Student s) {
        return dao.add(s);
    }

    /** 带业务校验的新增：姓名不能为空 */
    public int addWithCheck(Student s) {
        if (s.getName() == null || s.getName().isEmpty()) {
            return 0; // 校验不通过
        }
        return dao.add(s);
    }

    public List<Student> list() {
        return dao.list();
    }

    public int update(Student s) {
        return dao.update(s);
    }

    public int delete(int id) {
        return dao.delete(id);
    }
}
