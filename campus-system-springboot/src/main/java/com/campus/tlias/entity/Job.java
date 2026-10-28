package com.campus.tlias.entity;

import java.util.Objects;

/**
 * 岗位实体（对应项目二的 job 表）。
 */
public class Job {

    private Integer id;
    private Integer companyId;
    private String title;
    private String salary;
    private String majorRequired;

    public Job() {
    }

    public Job(Integer id, Integer companyId, String title, String salary, String majorRequired) {
        this.id = id;
        this.companyId = companyId;
        this.title = title;
        this.salary = salary;
        this.majorRequired = majorRequired;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getMajorRequired() {
        return majorRequired;
    }

    public void setMajorRequired(String majorRequired) {
        this.majorRequired = majorRequired;
    }

    /**
     * 重写 equals：按 id 判断是不是同一个岗位。
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
        Job other = (Job) o;
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
        return "Job{id=" + id + ", companyId=" + companyId + ", title='" + title
                + "', salary='" + salary + "', majorRequired='" + majorRequired + "'}";
    }
}
