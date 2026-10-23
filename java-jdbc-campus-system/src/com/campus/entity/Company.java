package com.campus.entity;

/** 企业实体，对应表 company */
public class Company {
    private int id;
    private String name;
    private String industry;

    public Company() {}

    public Company(String name, String industry) {
        this.name = name;
        this.industry = industry;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
}
