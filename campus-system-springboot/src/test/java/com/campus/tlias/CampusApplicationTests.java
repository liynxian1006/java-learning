package com.campus.tlias;

import com.campus.tlias.entity.Student;
import com.campus.tlias.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 集成测试：验证 Spring 容器能装配好、Service 能正常操作数据库。
 *
 * <p>@SpringBootTest 会真正启动 Spring 上下文（包括数据源、MyBatis Mapper 代理），
 * 所以它能验证"整个应用能不能跑起来"，而不只是单个方法。
 *
 * <p>运行：mvn -o test
 */
@SpringBootTest
class CampusApplicationTests {

    @Autowired
    private StudentService studentService;

    @Test
    void contextLoads() {
        assertNotNull(studentService, "StudentService 应被 Spring 自动注入");
        System.out.println("[测试] Spring 上下文启动成功，StudentService 已注入");
    }

    @Test
    void listStudents() {
        List<Student> list = studentService.list();
        assertNotNull(list, "查询结果不应为 null");
        System.out.println("[测试] 查询到 " + list.size() + " 名学生");
    }

    @Test
    void insertAndDeleteStudent() {
        Student s = new Student(null, "测试同学", "软件工程", "13611112222");
        studentService.add(s);
        assertNotNull(s.getId(), "插入后应回填自增主键 id");
        System.out.println("[测试] 新增成功，数据库生成 id = " + s.getId());

        studentService.delete(s.getId());
        System.out.println("[测试] 删除成功 id = " + s.getId());
    }
}
