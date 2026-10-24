package com.campus.tlias;

import com.campus.tlias.entity.Student;
import com.campus.tlias.service.StudentService;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 离线自检程序 —— 不依赖 surefire，用普通 main 验证整个 Spring 容器 + 数据库链路。
 *
 * <p>为什么不用 JUnit 跑：本机 Maven 仓库缺少 surefire 的 JUnit provider，
 * 离线环境下 mvn test 跑不起来。本类用最朴素的方式验证同样的东西：
 * 容器能启动 → Bean 能注入 → Service 能真实读写数据库。
 *
 * <p>注意：本类<strong>不能</strong>加 @SpringBootApplication，否则会和 CampusApplication
 * 抢「谁是主类」，导致 spring-boot-maven-plugin 打包失败。
 * 这里改为手动 new SpringApplicationBuilder(CampusApplication.class)。
 *
 * <p>运行（在 IDEA 里直接 Run 这个类即可）：
 * <pre>
 *   java -cp "target/classes;（所有依赖 jar）" com.campus.tlias.SelfCheck
 * </pre>
 * 或用项目根的 run.bat（本项目已配好 classpath）。
 */
public class SelfCheck {

    public static void main(String[] args) {
        // 启动 Spring 上下文（复用 CampusApplication 作为启动类）
        // web(NONE)：不启动内嵌 Tomcat —— 自检只关心容器装配与数据库链路，不需要 HTTP 服务，
        // 也避免了本机端口被占用的问题。
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(CampusApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);

        try {
            // 注意：SelfCheck 自己不是 Spring Bean（没加 @Component），
            // 所以不能 ctx.getBean(SelfCheck.class)。它的 studentService 字段
            // 是 Spring 在创建容器时注入进来的——但 SelfCheck 不是 Bean，
            // 因此这里改为直接从容器里取 Service Bean 来用。
            StudentService svc = ctx.getBean(StudentService.class);

            System.out.println();
            System.out.println("==================================================");
            System.out.println("  阶段三自检（Spring Boot + MyBatis + MySQL）");
            System.out.println("==================================================");

            // ① 容器与依赖注入
            checkBeanInjection(svc);

            // ② 查询
            checkQuery(svc);

            // ③ 写入 + 主键回填
            checkInsert(svc);

            System.out.println();
            System.out.println("==================================================");
            System.out.println("  全部自检通过");
            System.out.println("==================================================");
        } finally {
            ctx.close();
        }
    }

    private static void checkBeanInjection(StudentService svc) {
        if (svc == null) {
            throw new IllegalStateException("StudentService 未被注入，依赖注入失败");
        }
        System.out.println("[1] 依赖注入   : StudentService 已由 Spring 自动注入（无需 new）");
    }

    private static void checkQuery(StudentService svc) {
        java.util.List<Student> list = svc.list();
        System.out.println("[2] 查询学生   : 共 " + list.size() + " 条");
        for (int i = 0; i < Math.min(3, list.size()); i++) {
            System.out.println("                   " + list.get(i));
        }
    }

    private static void checkInsert(StudentService svc) {
        Student s = new Student(null, "自检同学", "软件工程", "13511112222");
        svc.add(s);
        if (s.getId() == null) {
            throw new IllegalStateException("插入后未回填自增主键，@Options(useGeneratedKeys) 未生效");
        }
        System.out.println("[3] 新增学生   : 成功，数据库回填主键 id = " + s.getId());

        svc.delete(s.getId());
        System.out.println("[4] 删除学生   : 成功，已清理测试数据 id = " + s.getId());
    }
}
