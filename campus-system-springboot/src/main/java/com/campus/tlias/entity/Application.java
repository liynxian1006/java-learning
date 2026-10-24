package com.campus.tlias.entity;

/**
 * 投递记录实体（对应项目二的 application 表）。
 */
public class Application {

    private Integer id;
    private Integer studentId;
    private Integer jobId;
    private String status;

    public Application() {
    }

    public Application(Integer id, Integer studentId, Integer jobId, String status) {
        this.id = id;
        this.studentId = studentId;
        this.jobId = jobId;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Application{id=" + id + ", studentId=" + studentId
                + ", jobId=" + jobId + ", status='" + status + "'}";
    }
}
