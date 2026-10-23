package com.campus.entity;

/** 岗位实体，对应表 job */
public class Job {
    private int id;
    private int companyId;
    private String title;
    private String salary;
    private String majorRequired;

    public Job() {}

    public Job(int companyId, String title, String salary, String majorRequired) {
        this.companyId = companyId;
        this.title = title;
        this.salary = salary;
        this.majorRequired = majorRequired;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }
    public String getMajorRequired() { return majorRequired; }
    public void setMajorRequired(String majorRequired) { this.majorRequired = majorRequired; }
}
