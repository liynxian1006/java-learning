package com.campus.entity;

/** 学生实体，对应表 student */
public class Student {
    private int id;
    private String name;
    private String major;
    private String phone;

    public Student() {}

    public Student(String name, String major, String phone) {
        this.name = name;
        this.major = major;
        this.phone = phone;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
