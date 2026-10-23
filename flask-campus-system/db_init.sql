-- 校园信息管理系统 建库建表脚本
-- 对应简历项目：校园信息管理系统（Python + Flask）
-- 在 MySQL 客户端执行：source db_init.sql  或整段复制执行

CREATE DATABASE IF NOT EXISTS campus_db DEFAULT CHARACTER SET utf8mb4;
USE campus_db;

-- 用户表（登录用）
CREATE TABLE IF NOT EXISTS user (
  id       INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50)  NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  role     VARCHAR(20)  DEFAULT 'student'
);

-- 学生表
CREATE TABLE IF NOT EXISTS student (
  id     INT PRIMARY KEY AUTO_INCREMENT,
  name   VARCHAR(50)  NOT NULL,
  gender VARCHAR(10),
  major  VARCHAR(50),
  grade  VARCHAR(20)
);

-- 课程表
CREATE TABLE IF NOT EXISTS course (
  id      INT PRIMARY KEY AUTO_INCREMENT,
  name    VARCHAR(100) NOT NULL,
  teacher VARCHAR(50),
  credit  INT
);
