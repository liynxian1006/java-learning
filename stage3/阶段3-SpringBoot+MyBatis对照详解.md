# 阶段三 · Spring Boot + MyBatis 重写项目二（对照讲解）

> 同一个「校园实习就业信息管理系统」，项目二是纯 JDBC 手写版，本文是 Spring Boot + MyBatis 框架版。
> **逐层对照着看**，你会清楚看到框架到底帮你省掉了什么。

---

## 一、为什么要有阶段三？

项目二写的是"手工版"：连接自己管、SQL 自己拼、结果自己搬、异常自己 catch。
这套东西在真实企业项目里会重复几百遍，于是出现了框架——**Spring Boot 管对象和配置，MyBatis 管 SQL 和对象映射**。

一句话记住两者分工：
- **Spring Boot**：管「对象怎么创建、配置怎么读、HTTP 怎么收发、异常怎么处理」
- **MyBatis**：管「SQL 怎么写、结果怎么变成 Java 对象」

---

## 二、逐层对照表（核心）

| 关注点 | 项目二（纯 JDBC） | 项目三（Spring Boot + MyBatis） |
|---|---|---|
| **启动** | `new Main()` 手动调用 | `@SpringBootApplication` + `SpringApplication.run()` |
| **Web 服务器** | 无（只能控制台） | 内嵌 Tomcat 自动启动 |
| **获取 Dao** | `private StudentDao dao = new StudentDao();` | `@Autowired` 注入（**依赖注入**） |
| **获取连接** | `conn = DBUtil.getConn()` 手写 | Spring 自动从 HikariCP 连接池取 |
| **关连接** | `finally { close() }` 手写 | 框架自动回收 |
| **SQL 位置** | 写在 Java 字符串里 | 写在 XML 或注解里（**SQL 与代码分离**） |
| **设参** | `ps.setString(1, x)` | `#{name}` 自动映射 |
| **执行** | `ps.executeUpdate()` | 方法名 + 注解自动决定 |
| **结果映射** | `while(rs.next())` 手工搬 | 自动映射成实体 |
| **异常处理** | 每个方法 try-catch | 全局异常处理器统一处理 |
| **参数校验** | 手写 if 判断 | `@NotBlank` + `@Valid` 声明式 |
| **事务** | 手写 commit/rollback | `@Transactional` 注解 |
| **数据库密码** | 硬编码在 DBUtil | 写在 `application.yml` |
| **主键回填** | 自己查 MAX(id) | `@Options(useGeneratedKeys=true)` 自动回填 |
| **分页** | 手写 `LIMIT ?,?` + 算 offset | XML 动态 SQL |

---

## 三、代码对照：新增一个学生

### 项目二（JDBC）—— 40 行

```java
public int add(Student s) {
    String sql = "INSERT INTO student(name, major, phone) VALUES(?,?,?)";
    Connection conn = null;
    PreparedStatement ps = null;
    try {
        conn = DBUtil.getConn();
        ps = conn.prepareStatement(sql);
        ps.setString(1, s.getName());
        ps.setString(2, s.getMajor());
        ps.setString(3, s.getPhone());
        return ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
        return 0;
    } finally {
        DBUtil.close(conn, ps, null);
    }
}
```

### 项目三（MyBatis）—— 4 行

```java
@Insert("INSERT INTO student(name, major, phone) VALUES(#{name}, #{major}, #{phone})")
@Options(useGeneratedKeys = true, keyProperty = "id")
int insert(Student student);
```

**少了什么？** 连接、语句、设参、执行、异常、关闭——**全部不见了**。
**多了什么？** 一行 `@Options` 让数据库把自增主键回填到 `student.getId()`。

---

## 四、四层结构对照

### 项目二
```
Main (演示入口)
  └─ StudentService (业务)
       └─ StudentDao (SQL)
            └─ DBUtil (连接工具)
                 └─ MySQL
```

### 项目三
```
CampusApplication (@SpringBootApplication 启动类)
  └─ StudentController (@RestController 接 HTTP)
       └─ StudentService (@Service 业务)
            └─ StudentMapper (@Mapper 接口，MyBatis 生成代理实现)
                 └─ MySQL（HikariCP 连接池自动管理）
```

**多了 Controller 层**（HTTP 入口），**少了 DBUtil 层**（连接由 Spring 管）。

---

## 五、五个核心注解（必须背下来）

| 注解 | 位置 | 作用 | 记忆点 |
|---|---|---|---|
| `@SpringBootApplication` | 启动类 | 一键启动 + 自动装配 + 组件扫描 | 启动类上就它一个 |
| `@RestController` | Controller 类 | 接收 HTTP 请求，返回 JSON | = `@Controller` + `@ResponseBody` |
| `@Service` | Service 类 | 标记业务组件，交给容器管理 | 业务层用这个 |
| `@Mapper` | Mapper 接口 | MyBatis 扫描并生成代理实现 | **接口不用写实现类** |
| `@Autowired` | 字段上 | 让 Spring 注入对象 | 替代 `new XxxDao()` |

### 加在方法上的
| 注解 | 作用 |
|---|---|
| `@GetMapping` / `@PostMapping` / `@PutMapping` / `@DeleteMapping` | 绑定 HTTP 方法 + 路径 |
| `@RequestMapping("/student")` | 类上，统一定义路径前缀 |
| `@RequestBody` | 把 JSON 请求体转成 Java 对象 |
| `@RequestParam` | 接收 URL 查询参数 |
| `@PathVariable` | 接收路径变量（`/student/{id}`） |
| `@Valid` | 触发参数校验 |
| `@Transactional` | 声明式事务 |

---

## 六、MyBatis 的两个核心机制

### 机制 1：`#{}` 占位符（防 SQL 注入）

```xml
<select id="findByMajor" resultMap="jobMap">
    SELECT ... FROM job WHERE major_required LIKE CONCAT('%', #{major}, '%')
</select>
```
- `#{}` → 编译成 JDBC 的 `?`，**自动防注入**
- `${}` → 直接字符串拼接，**有注入风险**，本项目只用于可控场景

⚠️ **重点**：模糊查询的 `%` 必须写在 `CONCAT('%', #{x}, '%')` 里，
**绝不能**写成 `LIKE '%${x}%'` —— 那就等于把用户输入直接拼进 SQL。

### 机制 2：动态 SQL（`<if>` / `<where>` / `<choose>`）

项目二里，按薪资查要写**三个方法**（只有 min / 只有 max / 两个都有）：
```java
List<Job> listBySalaryOnlyMin(String min);
List<Job> listBySalaryOnlyMax(String max);
List<Job> listBySalaryBetween(String min, String max);
```
项目三里，**一个动态 SQL 全搞定**：
```xml
<select id="listBySalary" resultMap="jobMap">
    SELECT ... FROM job
    <where>
        <if test="min != null and min != '' and max != null and max != ''">
            CAST(SUBSTRING_INDEX(salary,'-',1) AS DECIMAL(10,2)) &gt;= CAST(#{min} AS DECIMAL(10,2))
            AND CAST(SUBSTRING_INDEX(salary,'-',-1) AS DECIMAL(10,2)) &lt;= CAST(#{max} AS DECIMAL(10,2))
        </if>
        <if test="min != null and min != '' and (max == null or max == '')">
            CAST(SUBSTRING_INDEX(salary,'-',-1) AS DECIMAL(10,2)) &gt;= CAST(#{min} AS DECIMAL(10,2))
        </if>
        <if test="(min == null or min == '') and max != null and max != ''">
            CAST(SUBSTRING_INDEX(salary,'-',1) AS DECIMAL(10,2)) &lt;= CAST(#{max} AS DECIMAL(10,2))
        </if>
    </where>
    ORDER BY id
</select>
```
- `<where>`：自动去掉多余的 `AND` / `OR`
- `<if>`：条件成立才拼进 SQL
- `<choose>/<when>/<otherwise>`：if / else if / else

---

## 七、实战踩坑记录（真实踩过的，看完能少走弯路）

### 坑 1：薪资是 varchar，比较会走字典序 ⚠️ 重要

**现象**：查 `min=25000` 却返回了薪资 "8000-12000" 的岗位。

**原因**：`salary` 字段是 `varchar(50)`，存的是 "8000-12000" 这种区间字符串。
`salary >= '25000'` 在字符串上比较走**字典序**——'8' > '2'，所以 "8000-12000" > "25000" 成立！

**修复**：拆出上下限并转数字：
```sql
CAST(SUBSTRING_INDEX(salary, '-', 1)  AS DECIMAL(10,2))   -- 下限 "8000"
CAST(SUBSTRING_INDEX(salary, '-', -1) AS DECIMAL(10,2))   -- 上限 "12000"
```
- `SUBSTRING_INDEX(s, '-', 1)`：取第 1 个 '-' 之前的部分
- `SUBSTRING_INDEX(s, '-', -1)`：取最后 1 个 '-' 之后的部分

**更彻底的方案**：生产环境应该把薪资拆成 `salary_min` / `salary_max` 两个 `int` 字段，**不要用字符串存数值区间**。这是设计问题，不是查询问题。

### 坑 2：Spring Boot 3.2 的 404 不走异常处理器

**现象**：访问不存在的接口，返回 `{"code":500,"msg":"服务器内部错误：No static resource not-exist."}`，而不是 404。

**原因**：Spring Boot 3.x 默认把未匹配的路径当"静态资源找不到"处理，抛的是
`NoResourceFoundException` 而不是 `NoHandlerFoundException`，我们的处理器收不到。

**修复**（application.yml）：
```yaml
server:
  mvc:
    throw-exception-if-no-handler-found: true   # 未匹配时抛 NoHandlerFoundException
spring:
  mvc:
    static-path-pattern: /ui/**                 # 静态资源只暴露在 /ui/** 下
```
修复后：`{"code":404,"msg":"接口不存在：/not-exist"}`，HTTP 状态码也是 404。

> ⚠️ **注意副作用**：用了 `static-path-pattern` 后，静态页面路径变成 `/ui/index.html`。
> 如果改用 `spring.web.resources.add-mappings: false`，静态页面会**完全访问不到**（404）——
> 这个坑我踩过一次。所以正确做法是用 `static-path-pattern` 精确限定范围，而不是一刀切关闭。

### 坑 3：不要给第二个类加 `@SpringBootApplication`

**现象**：`spring-boot-maven-plugin` 打包失败：
```
Unable to find a single main class from the following candidates
[com.campus.tlias.CampusApplication, com.campus.tlias.SelfCheck]
```
**原因**：`@SpringBootApplication` 隐含 `@SpringBootConfiguration`，有两个类带它，插件不知道该用哪个当入口。

**修复**：自检类改用 `new SpringApplicationBuilder(CampusApplication.class)` 启动，不加注解。

### 坑 4：`@SpringBootApplication` 的扫描范围

注解在 `com.campus.tlias` 包上 → 只扫描这个包及**子包**。
所以 `entity / mapper / service / controller / common` 都能被扫到，
**但如果新建的类放在 `com.campus.tlias` 之外，就不会被管理**（会报 `Not a managed bean`）。

### 坑 5：本地 Maven 仓库缺 surefire 的 JUnit provider

**现象**：`mvn -o test` 报
```
surefire-junit-platform:jar:3.1.2 has not been downloaded from it before
```
**原因**：本机 `D:\maven\repository` 里没有 surefire 的 JUnit provider 构件（离线环境限制）。

**绕法**：写 `SelfCheck` 类用普通 `main` 验证（见项目内 `SelfCheck.java`），
等以后有网络环境再回到 JUnit 测试。

---

## 八、统一响应与全局异常（企业项目标配）

### 统一响应体
```java
public class Result<T> {
    private Integer code;   // 200 成功 / 400 参数错 / 404 不存在 / 500 服务器错
    private String msg;
    private T data;
}
```
所有接口都返回这个结构 → 前端解析逻辑统一，不用为每个接口写不同的解析代码。

### 全局异常处理
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)  // 参数校验失败
    @ExceptionHandler(BusinessException.class)                 // 业务异常
    @ExceptionHandler(NoHandlerFoundException.class)           // 404
    @ExceptionHandler(Exception.class)                         // 兜底
}
```
`@RestControllerAdvice` = 给所有 Controller 兜底 + 返回值直接转 JSON。

**没有它会怎样**：Spring Boot 默认返回一段 HTML 错误页，前端 `JSON.parse` 会崩。
**有了它**：任何异常都转成 `{code, msg, data}`，前端永远能解析。

---

## 九、参数校验对比

### 项目二：手写 if
```java
if (student.getName() == null || student.getName().trim().isEmpty()) {
    throw new BusinessException(400, "姓名不能为空");
}
```
### 项目三：声明式
```java
// 实体类
@NotBlank(message = "姓名不能为空")
private String name;

// Controller
public Result<Void> add(@RequestBody @Valid Student student) { ... }
```
好处：**校验规则写在字段旁边**，一眼就知道这个字段有什么约束；改规则不用改业务逻辑。

---

## 十、实测结果（全部接口已验证）

### 启动
```
Tomcat started on port 8080 (http)
Started CampusApplication in 2.1 seconds
```

### 接口实测
| 请求 | 返回 |
|---|---|
| `GET /student/list` | `{"code":200,"msg":"操作成功","data":[{...},...]}` |
| `GET /student/1` | `{"code":200,"msg":"操作成功","data":{"id":1,"name":"张三",...}}` |
| `POST /student/add` | `{"code":200,"msg":"操作成功","data":null}` |
| `POST /student/add`（name 空） | `{"code":400,"msg":"参数校验失败：姓名不能为空","data":null}` |
| `PUT /student/update` | `{"code":200,...}` → 再查 id=1 确认已改为"张三丰" |
| `DELETE /student/{id}` | `{"code":200,...}` |
| `DELETE /student/99999` | `{"code":404,"msg":"要删除的学生不存在，id=99999","data":null}` |
| `GET /student/99999` | `{"code":404,"msg":"学生不存在，id=99999","data":null}` |
| `GET /not-exist` | `{"code":404,"msg":"接口不存在：/not-exist"}` + HTTP 404 |
| `GET /job/page?page=1&size=2` | 分页正常，返回 2 条 |
| `GET /job/major?major=计` | 模糊查询正常，返回 2 条 |
| `GET /job/salary?min=10000&max=20000` | 修复后返回 `[]`（无完全落在区间的岗位，正确） |
| `GET /job/salary?min=25000` | 修复后返回 2 条（上限≥25000 的岗位） |

### 自检程序输出
```
[1] 依赖注入   : StudentService 已由 Spring 自动注入（无需 new）
[2] 查询学生   : 共 6 条
[3] 新增学生   : 成功，数据库回填主键 id = 8
[4] 删除学生   : 成功，已清理测试数据 id = 8
全部自检通过
```

---

## 十一、怎么跑起来

### 方式 A：IDEA（推荐）
1. IDEA 打开 `campus-system-springboot` 目录
2. 等待 Maven 导入完成（右下角进度条）
3. 右键 `CampusApplication` → Run
4. 浏览器打开 `http://localhost:8080/index.html` 看可视化界面

### 方式 B：命令行
```bash
cd campus-system-springboot
mvn -o -DskipTests package
java -Dfile.encoding=UTF-8 -jar target/campus-system-springboot-1.0.0.jar
```

### 方式 C：只跑自检（不开 HTTP 服务）
```bash
java -cp "target/classes;<所有依赖>" com.campus.tlias.SelfCheck
```
或直接在 IDEA 里 Run `SelfCheck.java`。

---

## 十二、对应简历写法（可直接用）

> **基于 Spring Boot + MyBatis 搭建校园实习就业信息管理系统**
> - 采用 Controller / Service / Mapper 分层架构，使用 Spring Boot 自动装配数据源与 MyBatis Mapper 代理
> - 基于 MyBatis XML 实现动态 SQL，支持岗位分页查询、按专业与薪资区间模糊筛选
> - 引入统一响应体 `Result` 与 `@RestControllerAdvice` 全局异常处理器，统一错误码与错误信息
> - 使用 `@Valid` + JSR-303 完成参数声明式校验，密码与连接配置外置到 `application.yml` 并支持环境变量覆盖
> - 使用 `@Transactional` 保证操作原子性，`@Options(useGeneratedKeys)` 实现主键自动回填
> - 使用 HikariCP 连接池优化数据库连接复用效率

---

## 十三、下一阶段该学什么

阶段三完成后，你的技能栈已经覆盖简历上的 Java / MySQL / Spring Boot / MyBatis / Git / Linux。
接下来按性价比排序：

| 优先级 | 内容 | 理由 |
|---|---|---|
| ★★★ | **Redis 缓存** | 校招几乎必问。把岗位列表加缓存 → 简历多一个"高并发优化"亮点 |
| ★★★ | **MySQL 索引与事务原理** | 八股重灾区。B+ 树、MVCC、隔离级别 |
| ★★ | **JVM + 并发（JUC）** | 校招八股核心 |
| ★★ | **登录鉴权（JWT / Spring Security）** | 任何真实项目都要 |
| ★ | **前后端分离完整版** | Vue/React + Axios，把项目一的前端补全 |

---

## 附：文件结构

```
campus-system-springboot/
├── pom.xml                                   ← Maven 配置（依赖、插件版本）
├── src/main/java/com/campus/tlias/
│   ├── CampusApplication.java                ← 启动类
│   ├── SelfCheck.java                        ← 离线自检程序
│   ├── common/
│   │   ├── Result.java                       ← 统一响应体
│   │   ├── BusinessException.java            ← 业务异常
│   │   └── GlobalExceptionHandler.java       ← 全局异常处理
│   ├── entity/Student.java, Job.java, Application.java
│   ├── mapper/StudentMapper.java, JobMapper.java, ApplicationMapper.java
│   ├── service/StudentService.java, JobService.java
│   └── controller/StudentController.java, JobController.java
├── src/main/resources/
│   ├── application.yml                       ← 数据源与 MyBatis 配置
│   ├── mapper/JobMapper.xml                  ← 动态 SQL
│   └── static/index.html                     ← 可视化界面
└── src/test/java/com/campus/tlias/CampusApplicationTests.java  ← JUnit 测试（需联网环境）
```
