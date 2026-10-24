package com.campus.tlias;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类 —— 整个 Spring Boot 应用的入口。
 *
 * <p>对比项目二（纯 JDBC）：那里需要自己 new Main()，还要手工创建连接、写 SQL。
 * 这里只要加一个 @SpringBootApplication 注解，Spring Boot 就会：
 * 1. 启动内嵌 Tomcat（不用单独装 Tomcat）
 * 2. 扫描本包及子包下的 @Component / @Service / @Mapper 等注解，创建对象并管理生命周期
 * 3. 根据 application.yml 自动装配数据源、MyBatis 等组件
 *
 * <p>@SpringBootApplication 其实是三个注解的组合：
 * - @SpringBootConfiguration：这是一个配置类
 * - @EnableAutoConfiguration：开启自动装配（数据源、MyBatis 全自动配好）
 * - @ComponentScan：扫描当前包及子包，交给 IOC 容器管理
 */
@SpringBootApplication
public class CampusApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusApplication.class, args);
        System.out.println("""

                ========================================================
                  校园实习就业信息管理系统 启动成功
                  接口地址: http://localhost:8080
                  接口清单: GET http://localhost:8080/student/list
                ========================================================
                """);
    }
}
