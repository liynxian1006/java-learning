package com.campus.tlias.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 学生实体。
 *
 * <p>对比项目二：那边是手写的 POJO（每个字段配 getter/setter，120 行）。
 * 这里用 Lombok 风格的简化写法——但为了不引入 Lombok 依赖（离线环境不一定有），
 * 仍然手写 getter/setter，原理与项目二完全一致。
 *
 * <p>jakarta.validation 是 Spring Boot 3.x 用的包名（2.x 是 javax.validation），
 * 这里用于 @NotBlank 等参数校验注解。
 */
public class Student {

    private Integer id;

    @NotBlank(message = "姓名不能为空")
    private String name;

    private String major;

    private String phone;

    public Student() {
    }

    public Student(Integer id, String name, String major, String phone) {
        this.id = id;
        this.name = name;
        this.major = major;
        this.phone = phone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', major='" + major + "', phone='" + phone + "'}";
    }
}
