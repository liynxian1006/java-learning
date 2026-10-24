# 项目二 · Dao 层零基础详解（以 Navicat 手写 SQL 做对照）

> 这份文档的核心思路：**你在 Navicat 里已经会写 SQL 了**。Java 里做同样的事，只多了一个"借连接"的动作。把这层差别看懂，Dao 层就通了。



---

## 一、先做一个关键对照（最重要）

### 你在 Navicat 里做的事（你已会的部分）

```sql
-- 1. 新建查询窗口，输入：
INSERT INTO student(name, major, phone) VALUES('张三', '计算机科学', '13800000000');

-- 2. 点运行 ▶
-- 3. 看到提示：影响行数 1
```

### Java 里做同一件事（多了一步"借电话"）

```java
Connection conn = DBUtil.getConn();                              // ← 多出来的这一步
PreparedStatement ps = conn.prepareStatement(
        "INSERT INTO student(name,major,phone) VALUES(?,?,?)");  // ← 同样的 SQL，只是值换成 ?
ps.setString(1, "张三");                                        // ← 把 '张三' 填进第1个?
ps.setString(2, "计算机科学");                                   // ← 把 '计算机科学' 填进第2个?
ps.setString(3, "13800000000");                                 // ← 把 '13800000000' 填进第3个?
int rows = ps.executeUpdate();                                  // ← 点运行 ▶（返回影响行数）
DBUtil.close(conn, ps, null);                                    // ← 多出来的这一步：挂电话
```

**结论：Java 版比 Navicat 版只多了「借连接」和「关连接」两步，SQL 本身完全一样。**

| 动作    | Navicat          | Java                                              |
| ----- | ---------------- | ------------------------------------------------- |
| 打开数据库 | 点一下连接            | `DBUtil.getConn()`                                |
| 写 SQL | 在查询窗口打字          | 写在 `prepareStatement("...")` 里                    |
| 填值    | 直接写在 SQL 里（引号包住） | 用 `setString(位置, 值)` 填进 `?`                       |
| 执行    | 点运行 ▶            | `executeUpdate()` / `executeQuery()`              |
| 看结果   | 看下方结果表格          | `executeQuery()` 返回 `ResultSet`，用 `rs.next()` 逐行读 |
| 收尾    | 关窗口              | `finally { close(...) }`                          |

---

## 二、为什么必须"借连接"？（Connection 到底是什么）

### 现实类比

打**电话**给数据库：

- 你不能隔着空气喊话 → 必须先**拨号接通**（= Connection）
- 通了才能说话 → 才能发 SQL
- 说完要**挂断** → 否则一直占线，别人打不通

### 代码里怎么体现

```java
Connection conn = null;      // 1. 先准备一个"电话"变量，此时是空的
try {
    conn = DBUtil.getConn(); // 2. 拨号接通 → 得到一个真实的连接对象
    // ... 在这里发 SQL、拿数据 ...
} finally {
    DBUtil.close(conn, ps, null); // 3. 无论成败都挂断
}
```

### 真实运行输出（我已在你电脑上跑过）

```
连接对象 = com.mysql.cj.jdbc.ConnectionImpl
是否有效 = true   → true 表示电话已接通
数据库产品 = 8.0.46
```

> `Connection` 是 JDBC 标准接口，`com.mysql.cj.jdbc.ConnectionImpl` 是 MySQL 驱动提供的实现。  
> **接口 = 插座标准，驱动 = 具体的插头**。这就是"JDBC = 标准插座"的意思。

### 漏掉 `close()` 会怎样？

连接不还回去 → 一直占着数据库的可用连接 → 连接数超过上限（MySQL 默认 151）→ **后来的程序全都连不上，数据库被"撑爆"**。

所以 `finally { DBUtil.close(...) }` 是**铁律**：不管中途是否报错，都必须关。

---

## 三、`?` 占位符到底是什么？（为什么不用直接拼字符串）

### 先看两种写法的对比

**❌ 拼接写法（有 SQL 注入风险）**

```java
String sql = "INSERT INTO student(name,major,phone) VALUES('"
        + s.getName() + "','" + s.getMajor() + "','" + s.getPhone() + "')";
```

如果有人把姓名填成 `张三' OR '1'='1`，拼出来就是：

```sql
INSERT INTO student(...) VALUES('张三' OR '1'='1','软件工程','138...')
```

→ 语义完全被篡改，可能拖走整张表。**这就是 SQL 注入攻击。**

**✅ 占位写法（安全）**

```java
String sql = "INSERT INTO student(name,major,phone) VALUES(?,?,?)";
ps.setString(1, s.getName());
```

→ 数据库把 `setString` 传进去的内容**只当数据看，永远不当命令执行**。注入从根本上不可能发生。

### 附带好处

不用自己拼字符串、不用操心引号和转义，代码更干净。

### `?` 的位置从 1 开始

```java
ps.setString(1, ...);   // 第 1 个 ?   ✔
ps.setString(2, ...);   // 第 2 个 ?   ✔
ps.setString(0, ...);   // ✘ 报错！位置从 1 开始
```

> 真实运行输出：`参数个数 = 3  → 与 ? 的个数一致`（JDBC 能告诉你有几个 `?`）

---

## 四、执行方法：`executeUpdate` vs `executeQuery`

| 方法                | 用于                            | 返回什么                | 真实输出示例                                           |
| ----------------- | ----------------------------- | ------------------- | ------------------------------------------------ |
| `executeUpdate()` | INSERT / UPDATE / DELETE（改数据） | `int` = 影响了多少行      | `executeUpdate() 返回 = 1`                         |
| `executeQuery()`  | SELECT（查数据）                   | `ResultSet` = 一张结果表 | `结果集对象 = com.mysql.cj.jdbc.result.ResultSetImpl` |

**判断口诀**：想**改**数据 → `executeUpdate`；想**看**数据 → `executeQuery`。

---

## 五、`ResultSet` 怎么把表格搬进 Java？

### 它是什么

查询回来的结果，像一张表，但**你只能从上往下、一行一行地走**，不能随便跳转。

```java
ResultSet rs = ps.executeQuery();
```

刚拿到时，光标停在**第一行之前**。

### `rs.next()` = 往下走一行

```java
while (rs.next()) {
    // 只有这里面的代码才会执行
}
// rs.next() 返回 false（没有下一行了）时，循环自然结束
```

注意：**第一次调用 `next()` 才是"跳到第 1 行"**，不是"检查有没有第 1 行"。这是新手最常见的困惑点。

### `rs.getString("name")` = 读当前行某一列的值

```java
int id = rs.getInt("id");            // id 是 int  → 用 getInt
String name = rs.getString("name");  // name 是文本 → 用 getString
```

列名可以直接写字符串（也可以用 `rs.getInt(1)` 按列序号取，序号同样从 1 开始）。

### 完整映射流程（这就是"结果集 → 对象"）

```java
while (rs.next()) {
    Student s = new Student();       // 1. 建一个空对象
    s.setId(rs.getInt("id"));        // 2. 把每一列的值塞进去
    s.setName(rs.getString("name"));
    s.setMajor(rs.getString("major"));
    s.setPhone(rs.getString("phone"));
    list.add(s);                     // 3. 装进列表
}
```

真实运行输出：

```
第 1 行 → id=1, name=张三, major=计算机科学, phone=13800000000
第 2 行 → id=2, name=李四, major=软件工程, phone=13800001111
一共读到 2 行。rs.next() 返回 false 时循环自然结束。
```

---

## 六、Dao 层存在的意义（为什么不让 Service 直接写 SQL）

用**仓库管理员**来类比：

| 角色    | 对应类              | 干什么                      |
| ----- | ---------------- | ------------------------ |
| 老板    | `Main`           | 演示"新增一个学生"               |
| 业务主管  | `StudentService` | 检查业务规则（姓名不能为空），决定调哪个 Dao |
| 仓库管理员 | `StudentDao`     | 具体怎么连库、怎么写 SQL、怎么关资源     |

**好处（这就是简历说的"可读性与可维护性"）**：

- 改 SQL（优化索引、改表名）→ 只动 `StudentDao`，Service 一行不用改
- 改业务规则（姓名为空就不许加）→ 只动 `StudentService`
- 换数据库（MySQL → PostgreSQL）→ 只动 `DBUtil`
- **各层互不干扰，改动范围可控**

如果不分层，全塞进一个类里，几百行 SQL 和业务逻辑混在一起，改一个功能就可能弄坏另一个。

---

## 七、三个 Dao 管什么（业务视角）

先看业务故事：

> 学生张三看到"字节跳动 · Java 开发实习生 · 8000-12000/天"这个岗位，点了投递 → 生成一条投递记录，状态"待审核" → 企业查看后把状态改成"已通过"或"已拒绝"。

| 表             | 存什么       | 对应 Dao           | 方法                                           |
| ------------- | --------- | ---------------- | -------------------------------------------- |
| `student`     | 学生        | `StudentDao`     | add / list / update / delete                 |
| `company`     | 企业        | （预留）             | —                                            |
| `job`         | 岗位        | `JobDao`         | add / pageQuery / listByMajor / listBySalary |
| `application` | 投递记录（含状态） | `ApplicationDao` | apply / updateStatus / listByStudent         |

### 三个值得单独说的方法

**`JobDao.pageQuery`（分页）**

```sql
SELECT ... FROM job LIMIT ?,?
```

MySQL 的 `LIMIT` 要的是"从第几条开始、取几条"，而页码是"第几页"。所以要换算：

```java
int offset = (page - 1) * size;   // 第1页(1) → offset=0；第2页(2) → offset=10（每页10条）
ps.setInt(1, offset);
ps.setInt(2, size);
```

**`JobDao.listByMajor`（模糊查询）**

```sql
SELECT ... FROM job WHERE major_required LIKE ?
ps.setString(1, "%" + major + "%");
```

`%` 是 SQL 的通配符（匹配任意字符），两边加 `%` 就是"包含"。**注意：`%` 要写在 Java 字符串里，不能写在 SQL 里**，否则用户输入的 `%` 就变成了注入通道。

**`ApplicationDao.apply`（默认状态）**

```sql
INSERT INTO application(student_id, job_id, status) VALUES(?,?,'待审核')
```

状态直接写在 SQL 里，插入出来就是"待审核"。

---

## 八、你在 IDEA 里怎么跑（不用命令行）

1. **建库建表**：Navicat 连 MySQL → 新建查询 → 粘贴 `schema.sql` 内容 → 运行。
   > ⚠️ 用 Navicat 不会遇到中文乱码问题；用命令行 `mysql` 需要加 `--default-character-set=utf8mb4`（Windows 下 `mysql.exe` 默认用系统 ANSI 编码，会导致 `status DEFAULT '待审核'` 报 `ERROR 1067 Invalid default value`）。
2. **加驱动**：IDEA → File → Project Structure → Libraries → ＋ → 加 `mysql-connector-j-8.0.33.jar`  
   （你本机已有：`C:\Users\34118\.m2\repository\com\mysql\mysql-connector-j\8.0.33\mysql-connector-j-8.0.33.jar`）
3. **密码**：已改成 `DBUtil.java` 里读环境变量 `DB_PASSWORD`，默认值 `123456`——你直接跑就能连上，仓库公开也不会泄露。
4. **运行**：右键 `Main.java` → Run。控制台应输出：
   ```
   新增影响行数：1
   1 | 张三 | 计算机科学
   ```
5. **看追踪过程**：右键 `src/com/campus/trace/JdbcTraceDemo.java` → Run，能看到本文档里所有"真实运行输出"。

---

## 九、对应简历写法与面试话术

**简历写法：**

> 熟悉 JDBC，采用 entity/dao/service 分层设计；使用 PreparedStatement 防 SQL 注入，实现学生/岗位/投递的增删改查，以及分页查询、按专业/薪资筛选、投递状态流转。

**面试话术：**

> 我用 Java + JDBC 做了个校园实习就业系统，采用 entity/dao/service 分层。所有 SQL 都走 PreparedStatement 的 `?` 占位加 setXxx 设参，避免 SQL 注入；dao 层用工具类统一管理连接获取和资源关闭（反向关闭：结果集→语句→连接）；实现了岗位的分页查询、按专业和薪资的模糊筛选，以及投递记录的状态流转。

---

## 十、自测题（能答出来就是真懂了）

1. 为什么不能把密码硬编码在 `DBUtil.java` 里？现在是怎么解决的？
2. `setString(1, ...)` 的 1 能写成 0 吗？为什么？
3. `executeUpdate()` 和 `executeQuery()` 分别返回什么？分别用在什么 SQL 上？
4. `rs.next()` 返回 true 之后，`rs` 指向哪一行？
5. 如果忘了写 `finally { DBUtil.close(...) }`，长时间运行会发生什么？
6. `JobDao.listByMajor` 里为什么 `%` 要写在 Java 字符串里而不是 SQL 里？
7. 为什么项目二要分 entity / dao / service 三层？改成一层有什么坏处？

---

## 附：本项目的运行命令（备用）

```bash
cd java-jdbc-campus-system
JAR="C:/Users/34118/.m2/repository/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar"

# 建库建表（命令行方式；Navicat 用户忽略）
mysql --default-character-set=utf8mb4 -u root -p123456 < schema.sql

# 编译 + 运行
javac -encoding UTF-8 -cp "$JAR" -d out $(find src -name "*.java")
java -Dfile.encoding=UTF-8 -cp "out;$JAR" com.campus.Main
java -Dfile.encoding=UTF-8 -cp "out;$JAR" com.campus.trace.JdbcTraceDemo
```

> Windows 下 classpath 分隔符用 `;`；Git Bash 里路径要写成 `C:/...` 这种 Windows 格式，否则 Java 找不到 jar。
