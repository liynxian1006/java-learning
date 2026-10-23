package com.campus.entity;

/** 投递实体，对应表 application（学生投递岗位的状态流转） */
public class Application {
    private int id;
    private int studentId;
    private int jobId;
    private String status; // 待审核 / 已通过 / 已拒绝

    public Application() {}

    public Application(int studentId, int jobId, String status) {
        this.studentId = studentId;
        this.jobId = jobId;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
