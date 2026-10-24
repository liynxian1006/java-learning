package com.campus;

import com.campus.entity.Student;
import com.campus.service.StudentService;

import java.util.List;

/**
 * 控制台演示入口。
 * 真正对外提供 HTTP 接口时，这一层会换成 Spring Boot 的 Controller（阶段 3）。
 */
public class Main {
    public static void main(String[] args) {
        StudentService service = new StudentService();

        // 示例：新增一名学生
        Student s = new Student("张三", "计算机科学", "13800000000");
        int n = service.add(s);
        System.out.println("新增影响行数：" + n);

        // 示例：查询并打印全部学生
        List<Student> list = service.list();
        for (Student stu : list) {
            System.out.println(stu.getId() + " | " + stu.getName() + " | " + stu.getMajor());
        }

        System.out.println("提示：数据来自 MySQL 的 campus_job_db.student 表；"
                + "数据库密码读环境变量 DB_PASSWORD，默认 123456。");
    }
}
