-- 校园实习就业信息管理系统 建库建表脚本
-- 对应简历项目：基于 Java 的校园实习就业信息管理系统（Java + JDBC + MySQL）
-- 角色：学生 / 企业 / 管理员；核心表：student / company / job / application

CREATE DATABASE IF NOT EXISTS campus_job_db DEFAULT CHARACTER SET utf8mb4;
USE campus_job_db;

CREATE TABLE IF NOT EXISTS student (
  id    INT PRIMARY KEY AUTO_INCREMENT,
  name  VARCHAR(50),
  major VARCHAR(50),
  phone VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS company (
  id       INT PRIMARY KEY AUTO_INCREMENT,
  name     VARCHAR(100),
  industry VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS job (
  id              INT PRIMARY KEY AUTO_INCREMENT,
  company_id      INT,
  title           VARCHAR(100),
  salary          VARCHAR(50),
  major_required  VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS application (
  id         INT PRIMARY KEY AUTO_INCREMENT,
  student_id INT,
  job_id     INT,
  status     VARCHAR(20) DEFAULT '待审核'
);
