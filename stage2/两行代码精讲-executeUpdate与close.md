# 两行关键代码精讲：executeUpdate 的返回值 & close 的作用

> 对应 `StudentDao.add` 里的两行：
> ```java
> int rows = ps.executeUpdate();      // ← 点运行 ▶（返回影响行数）
> DBUtil.close(conn, ps, null);        // ← 挂断电话
> ```

---

## 一、`int rows = ps.executeUpdate();` 逐部分拆解

```java
int    rows    =  ps    .executeUpdate()  ;
 ↑      ↑       ↑         ↑
类型   变量名  变量   要调用的方法
```

### 1. `ps` 是什么
`ps` 是上面那行准备好的语句对象，它肚子里装着一句 SQL 和三个已经填好的值：
```java
ps = conn.prepareStatement("INSERT INTO student(name, major, phone) VALUES(?,?,?)");
ps.setString(1, "张三");
ps.setString(2, "计算机科学");
ps.setString(3, "13800000000");
```
**此时 SQL 还没执行**，只是在 Java 内存里"备好了单子"。

### 2. `executeUpdate()` 做了什么
把单子**真的送到数据库执行**，然后拿回一个数字：**这次操作影响了多少行**。

> 名字里的 `Update` 不只指 UPDATE 语句，而是 JDBC 对"所有改数据的语句"（INSERT / UPDATE / DELETE）的统一叫法。

### 3. 返回值对照表（真实运行结果）

| 你执行的 SQL | 数据库实际做了什么 | `executeUpdate()` 返回 |
|---|---|---|
| `INSERT INTO student(...)` | 插入了 1 行 | **1** |
| `INSERT` 批量插 3 行 | 插了 3 行 | **3** |
| `UPDATE ... WHERE id=6`（存在） | 改了 1 行 | **1** |
| `UPDATE ... WHERE id=999999`（不存在） | 一行都没改到 | **0** ← 关键 |
| `DELETE FROM student WHERE id=999999` | 一行都没删到 | **0** |
| `DELETE FROM student WHERE id=6` | 删了 1 行 | **1** |

### 4. 实证输出（我刚在你电脑上跑的）
```
① INSERT 一行
executeUpdate() 返回 = 1
刚插入那行的 id = 6

② UPDATE 存在的 id
executeUpdate() 返回 = 1

③ UPDATE 不存在的 id —— 关键实验
executeUpdate() 返回 = 0
→ 一行都没改到，所以是 0。

④ DELETE 不存在的 id
executeUpdate() 返回 = 0
```

### 5. 为什么代码里要 `int rows =` 接住它
因为**这个数字是判断"业务上有没有真生效"的唯一依据**。典型用法：
```java
int rows = ps.executeUpdate();
if (rows > 0) {
    return true;      // 真的插入成功了
} else {
    return false;     // 一行都没插进去（比如唯一键冲突被吞了）
}
```
在 `StudentDao.add` 里，这个值被 `return` 出去，一路传到 `Main`：
```java
int n = service.add(s);
System.out.println("新增影响行数：" + n);   // 输出 "新增影响行数：1"
```
**如果这里返回 0，说明数据根本没进库**，但程序不会报错——这就是为什么必须检查返回值。

### 6. 常见误解
| 误解 | 事实 |
|---|---|
| 返回 1 是"SQL 执行成功" | 返回 0 也可能是"执行成功但没影响任何行"（比如 WHERE 没匹配上） |
| 返回值是 id | 不是。id 要另外用 `SELECT MAX(id)` 或主键回填机制获取 |
| 返回值一定是 1 | 批量插入可能返回 3、10 等 |

---

## 二、`DBUtil.close(conn, ps, null);` 逐部分拆解

```java
DBUtil.close( conn , ps , null );
             ↑     ↑     ↑
           连接  语句  结果集
```

### 1. 三个参数分别是什么

| 参数 | 类型 | 对应现实 | 这次有没有 |
|---|---|---|---|
| `conn` | Connection | 电话线路 | 有，必须关 |
| `ps` | PreparedStatement | 填好的单据 | 有，必须关 |
| `null` | ResultSet | 服务器回给你的结果表 | 无（这次是 INSERT，没有结果集可读） |

**为什么第三个能传 null**：因为 `DBUtil.close` 内部对每个参数都判了空：
```java
public static void close(Connection conn, Statement stmt, ResultSet rs) {
    try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
    try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
    try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
}
```
所以**你可以只传你手上有的资源**，其余传 null，不用担心空指针异常。

### 2. 关闭顺序为什么是"反向"
获取顺序是 `conn → ps → rs`，关闭就反过来 `rs → ps → conn`。
类比：先取杯子（conn）→ 拿单据（ps）→ 拿回执（rs），收的时候先放回执，再放单据，最后还杯子。

### 3. 不关会怎样（真实后果）

MySQL 默认 `max_connections = 151`（最大同时连接数）。

```
第 1 个请求   → 借连接，用完不还 → 占用 1
第 2 个请求   → 借连接，用完不还 → 占用 2
...
第 151 个请求 → 占用 151
第 152 个请求 → 报错：
   java.sql.SQLException: Too many connections
```

**一旦某个请求漏了 close，这一条连接就永久泄漏**（没人能再拿到它），MySQL 重启前一直占着。跑久了整个数据库就"打不进电话"了。

### 4. 为什么必须写在 `finally` 里
```java
try {
    conn = DBUtil.getConn();
    ps = conn.prepareStatement(sql);
    ps.setString(1, ...);
    return ps.executeUpdate();      // ← 这里 return 会跳走！
} catch (Exception e) {
    e.printStackTrace();
    return 0;
} finally {
    DBUtil.close(conn, ps, null);   // ← 无论怎么走都会执行
}
```
关键点：**`return` 不会立刻结束方法，它会先去执行 `finally` 块**。所以即使第 26 行 `return` 了，第 31 行的 close 依然会跑。

如果把 close 写在 try 里（而不是 finally）：
```java
try {
    ...
    return ps.executeUpdate();
    DBUtil.close(conn, ps, null);   // ← 永远执行不到！被 return 跳过了
}
```
**这就是漏 close 最常见的写法错误。** 三条路径（成功 return、异常 return、正常结束）必须都走到 close，所以只能放 finally。

### 5. 完整的资源生命周期
```
借连接（getConn） → 准备语句 → 设参 → 执行 → 关连接（close）
   ↑ 相当于借电话            ↑ 打电话        ↑ 挂电话
```
DAO 层每个方法都是这个模式，一句都不能少。

---

## 三、一句话总结这两行

| 代码 | 一句话 |
|---|---|
| `int rows = ps.executeUpdate();` | **把单子送去执行，拿回"影响了多少行"**——用来判断业务是否真的生效 |
| `DBUtil.close(conn, ps, null);` | **把电话挂掉、把单据归还**，不还就会占满连接数导致数据库打不进电话 |

---

## 四、动手验证（5 分钟）

打开 `src/com/campus/trace/ExecuteUpdateDemo.java` 在 IDEA 里 Run，看上面那些真实输出。然后自己动手改：
1. 把 ③ 里的 `999999` 改成**真实存在**的 id → 看返回值从 0 变 1
2. 故意把 `close` 那行注释掉 → 改成连续插入 200 次，体会连接耗尽（会报 `Too many connections`）
3. 把 `executeUpdate()` 换成 `executeQuery()` → 体会编译期类型不匹配的报错

> 第 2 步是**必做实验**——亲手制造一次连接泄漏，你就永远不会忘写 close 了。
