package com.campus.tlias.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

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

    /**
     * 重写 equals：判断「是不是同一条学生记录」。
     *
     * <p>为什么必须重写：不重写的话 equals 和 == 一样（比引用），
     * 会导致 HashSet 去重失效、List.contains() 找不到、Map 用对象当 key 取不回来。
     *
     * <p>标准写法四要素：
     * <ol>
     *   <li>this == o → 同一引用，直接 true</li>
     *   <li>o == null → false</li>
     *   <li>getClass() != o.getClass() → 类型不同，false</li>
     *   <li>比较业务唯一标识</li>
     * </ol>
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Student other = (Student) o;
        // id 为 null（还没入库）时视为不相等：它们本来就不是同一条数据库记录
        return id != null && id.equals(other.id);
    }

    /**
     * 重写 hashCode：必须与 equals 保持一致。
     *
     * <p>契约：equals 相等的对象，hashCode 必须相等。
     * 因为 HashMap/HashSet 是「先按 hashCode 定位桶，再按 equals 判断是不是同一个」，
     * hashCode 不一致就进不了同一个桶，equals 根本没机会比。
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', major='" + major + "', phone='" + phone + "'}";
    }
}
