# 校园实习就业信息管理系统（Java + JDBC 骨架）

对应简历项目：**基于 Java 的校园实习就业信息管理系统**
技术栈：Java 17、JDBC、MySQL 8；分层：entity / dao / service /（后续）controller

## 目录结构
```
src/com/campus/
  util/DBUtil.java        数据库连接工具（统一封装连接与异常）
  entity/                 实体类：Student / Company / Job / Application
  dao/                    数据访问层：StudentDao / JobDao / ApplicationDao
  service/                业务层（示例 StudentService）
  Main.java               控制台演示入口
```

## 运行前准备
1. 安装 MySQL 8，执行 `schema.sql` 建库建表。
2. 下载 `mysql-connector-j-8.x.jar`（MySQL 官方 JDBC 驱动）。
3. 修改 `DBUtil.java` 中的数据库密码。
4. 编译并运行（把驱动 jar 加到 classpath）：
   ```
   javac -encoding UTF-8 -cp mysql-connector-j-8.x.jar -d out src/com/campus/**/*.java
   java  -cp out;mysql-connector-j-8.x.jar com.campus.Main
   ```
   - 注意：源码含中文注释，命令行编译务必加 `-encoding UTF-8`；用 IntelliJ IDEA 打开则默认 UTF-8，无需此参数。
   - Windows 上 classpath 分隔符用 `;` 而不是 `:`。

## 已涵盖的接口（8+）
- 学生：add / list / update / delete
- 岗位：add / 分页查询 pageQuery / 按专业筛选 / 按薪资筛选
- 投递：apply / 状态流转 updateStatus / 按学生查询

## 下一步（阶段 3）
把本项目用 **Spring Boot + MyBatis-Plus** 重写，即可从简历里的「了解」升级为「实战项目」。
