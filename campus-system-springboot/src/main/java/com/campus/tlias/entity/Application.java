package com.campus.tlias.entity;

import java.util.Objects;

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

    /**
     * 重写 equals：按 id 判断是不是同一条投递记录。
     * 不重写会导致 HashSet 去重失效、List.contains() 找不到。
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Application other = (Application) o;
        return id != null && id.equals(other.id);
    }

    /**
     * 重写 hashCode：与 equals 保持一致（只依赖 id）。
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Application{id=" + id + ", studentId=" + studentId
                + ", jobId=" + jobId + ", status='" + status + "'}";
    }
}
