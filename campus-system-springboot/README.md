# 校园实习就业信息管理系统（Spring Boot + MyBatis + MySQL）

> 项目二的**框架化重写版**。项目二用纯 JDBC 手写实现，本项目用 Spring Boot 3.2.5 + MyBatis 3.0.3 重写，
> 同一个业务、同一套表，把「框架到底省掉了什么」看得清清楚楚。

---

## 技术栈

| 组件 | 版本 | 说明 |
|---|---|---|
| JDK | 17 | |
| Spring Boot | 3.2.5 | 内嵌 Tomcat + 自动装配 + 依赖注入 |
| MyBatis | 3.0.3 | XML 动态 SQL + Mapper 代理 |
| MySQL | 8.0.46 | 数据库 `campus_job_db` |
| HikariCP | Spring Boot 内置 | 连接池 |
| Maven | 3.9.16 | 构建工具（**可完全离线构建**） |

---

## 快速开始

### 1. 确保数据库已建表
执行 `../java-jdbc-campus-system/schema.sql`（Navicat 里直接打开运行即可）：
```sql
CREATE DATABASE IF NOT EXISTS campus_job_db DEFAULT CHARACTER SET utf8mb4;
USE campus_job_db;
-- student / company / job / application 四张表
```
可选：插入岗位测试数据
```sql
INSERT INTO job(company_id, title, salary, major_required) VALUES
 (1, 'Java开发实习生', '8000-12000', '计算机'),
 (1, '后端开发工程师', '15000-25000', '计算机'),
 (2, '前端开发实习生', '7000-11000', '软件工程'),
 (2, '测试工程师', '9000-14000', '软件工程'),
 (3, '算法工程师', '25000-40000', '数学');
```

### 2. 启动

**IDEA**：打开本目录 → 等待 Maven 导入 → 右键 `CampusApplication` → Run

**命令行**：
```bash
mvn -o -DskipTests package
java -Dfile.encoding=UTF-8 -jar target/campus-system-springboot-1.0.0.jar
```

### 3. 访问

| 地址 | 内容 |
|---|---|
| http://localhost:8080/ui/index.html | 可视化界面（增删改查 + 实时日志） |
| http://localhost:8080/student/list | 查询全部学生（返回 JSON） |
| http://localhost:8080/job/page?page=1&size=10 | 分页查询岗位 |

> 静态页面放在 `/ui/**` 下（由 `spring.mvc.static-path-pattern: /ui/**` 指定），
> 这样未匹配的接口路径才能正确返回 404 而不是 500。

---

## 接口清单

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/student/list` | 查询全部学生 |
| GET | `/student/{id}` | 按 id 查询 |
| POST | `/student/add` | 新增学生 |
| PUT | `/student/update` | 修改学生 |
| DELETE | `/student/{id}` | 删除学生 |
| GET | `/job/list` | 全部岗位 |
| GET | `/job/page?page=1&size=10&majorRequired=计算机` | 分页 + 条件筛选 |
| GET | `/job/major?major=计算机` | 按专业模糊查 |
| GET | `/job/salary?min=10000&max=20000` | 按薪资区间查 |

所有接口统一返回：
```json
{ "code": 200, "msg": "操作成功", "data": [] }
```

---

## 配置说明

`src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_job_db?...
    username: root
    password: ${DB_PASSWORD:123456}   # 优先读环境变量 DB_PASSWORD
    hikari:
      maximum-pool-size: 10      # 连接池上限（项目二无池，每次新建连接）
      minimum-idle: 2
```

**密码不硬编码**：优先读环境变量 `DB_PASSWORD`，未设置时用默认值。
```bash
# Windows PowerShell
$env:DB_PASSWORD="你的密码"
```

---

## 项目结构

```
campus-system-springboot/
├── pom.xml
└── src/main/
    ├── java/com/campus/tlias/
    │   ├── CampusApplication.java     启动类
    │   ├── SelfCheck.java             离线自检（不依赖 surefire）
    │   ├── common/                    统一响应 + 全局异常
    │   ├── entity/                    实体
    │   ├── mapper/                    Mapper 接口
    │   ├── service/                   业务层
    │   └── controller/                接口层
    └── resources/
        ├── application.yml
        ├── mapper/JobMapper.xml       动态 SQL
        └── static/index.html          可视化界面
```

---

## 与项目二（纯 JDBC）的核心差异

| 关注点 | 项目二 | 本项目 |
|---|---|---|
| 获取 Dao | `new StudentDao()` | `@Autowired` 注入 |
| 获取连接 | `DBUtil.getConn()` 手写 | 连接池自动管理 |
| 关连接 | `finally { close() }` | 框架自动回收 |
| SQL 位置 | Java 字符串 | XML / 注解 |
| 设参 | `ps.setString(1, x)` | `#{name}` |
| 结果映射 | `while(rs.next())` 手工搬 | 自动映射 |
| 异常处理 | 每个方法 try-catch | 全局异常处理器 |
| 参数校验 | 手写 if | `@Valid` + `@NotBlank` |
| 事务 | 手写 commit | `@Transactional` |
| 密码 | 硬编码在 DBUtil | `application.yml` |
| 主键回填 | 自己查 MAX(id) | `@Options(useGeneratedKeys)` |

完整对照讲解见 `../stage3/阶段3-SpringBoot+MyBatis对照详解.md`。

---

## 常见问题

**Q：启动报 `Port 2961 was already in use` / 端口被占用**
A：本机某些端口被其他程序占用。改端口：
```bash
java -jar target/campus-system-springboot-1.0.0.jar --server.port=9090
```

**Q：`mvn test` 报 surefire-junit-platform 缺失**
A：本机 Maven 仓库缺该构件（离线限制）。改用 `SelfCheck` 自检：
在 IDEA 里直接 Run `SelfCheck.java`。

**Q：查询薪资区间结果不对**
A：`salary` 是 varchar 存"8000-12000"区间，字符串比较走字典序。
XML 里已用 `SUBSTRING_INDEX` + `CAST` 修复。生产环境建议拆成 `salary_min` / `salary_max` 两个 int 字段。

**Q：访问不存在的接口返回 500 而不是 404**
A：Spring Boot 3.x 默认走静态资源查找逻辑。已在 `application.yml` 里配置：
```yaml
server.mvc.throw-exception-if-no-handler-found: true
spring.mvc.static-path-pattern: /ui/**
```
副作用是静态页面路径变成 `/ui/index.html`。
