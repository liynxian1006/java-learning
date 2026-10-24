# 项目二 · Dao 层初学者详解（零基础视角）

> 本文档从"第一次见到这段代码"的角度，把 Dao 层逐行讲明白。配合 `java-jdbc-campus-system/` 源码阅读效果更好。

## 一、Dao 到底是什么？

把数据库想象成一个**仓库**，里面一格格货架放着数据。你的 Java 程序是"老板"，想存/取东西，但老板不能直接冲进仓库乱翻——所以专门请了一个**仓库管理员，叫 Dao（Data Access Object，数据访问对象）**。

- 老板（Main / Service）只说："新增一个学生张三"。
- 管理员（StudentDao）负责：拨通仓库电话 → 填好入库单 → 存进去 → 挂电话 → 回报"存了 1 行"。

**为什么要有这个中间人？** 因为"拨电话、填单子"的细节很繁琐，集中让 Dao 管，代码清晰、好维护——这就是简历里写的"分层、可读性与可维护性"。

## 二、StudentDao.add 逐行翻译（人话版）

```java
Connection conn = null; PreparedStatement ps = null;
```
- `Connection` = 一条"连到数据库的通话线路"，先声明为空（还没拨通）。
- `PreparedStatement` = "一条准备好的 SQL 语句"，也先空着。

```java
try {
    conn = DBUtil.getConn();
```
- 调用之前讲过的 `DBUtil.getConn()`，**借一条数据库连接**（拨通电话）。

```java
    ps = conn.prepareStatement("INSERT INTO student(name,major,phone) VALUES(?,?,?)");
```
- 准备一条 SQL："往 student 表插入一行，三个字段的值先用 `?` 占个位"。

```java
    ps.setString(1, s.getName());   // 第1个? 填 姓名
    ps.setString(2, s.getMajor());  // 第2个? 填 专业
    ps.setString(3, s.getPhone());  // 第3个? 填 电话
```
- `setString(位置, 值)`：把值填进对应的 `?`。**位置从 1 开始数**（不是 0，新手最常错）。
- `s` 是传进来的学生对象，`s.getName()` 取它的姓名。

```java
    return ps.executeUpdate();       // 执行，返回"影响了几行"
```
- `executeUpdate()` = "这是要**改动**数据库的语句"，执行后返回整数（影响了几行）。新增成功返回 1。

```java
} catch (Exception e) { e.printStackTrace(); return 0; }
```
- 中间任何一步出错（比如库没连上），进 catch，打印错误，返回 0（表示没成功）。

```java
finally { DBUtil.close(conn, ps, null); }
```
- 不管成功失败都走 finally，把连接和语句关掉（**挂断电话**）。否则连接一直占着，数据库会被撑爆——这是 JDBC 最容易漏、也最重要的习惯。

## 三、为什么用 `?` 占位，不直接拼字符串？

- ❌ 危险：`"INSERT ... VALUES('" + s.getName() + "')"` —— 名字里若被人写 `' OR '1'='1`，就篡改了 SQL（**SQL 注入攻击**）。
- ✅ 安全：`VALUES(?)` + `setString` —— 数据库把 `?` 里的东西**只当数据、不当命令**，从根上防住注入，且不用自己加引号拼接，代码更干净。

## 四、查询怎么写（executeQuery + ResultSet）

查询用 `executeQuery()`（不改变数据，只是"问"）：

```java
ps = conn.prepareStatement("SELECT * FROM student");
ResultSet rs = ps.executeQuery();      // 返回一张"结果表"
List<Student> list = new ArrayList<>();
while (rs.next()) {                    // 光标移到下一行；没数据就停
    Student stu = new Student();
    stu.setId(rs.getInt("id"));
    stu.setName(rs.getString("name")); // 读当前行 name 列的值
    stu.setMajor(rs.getString("major"));
    stu.setPhone(rs.getString("phone"));
    list.add(stu);                     // 这一行变成一个对象，装进列表
}
return list;
```

- `ResultSet` = 查询返回的"结果集"，像一张临时表。
- `rs.next()` = 光标移到下一行；还有数据返回 true，没了返回 false，循环结束。
- `rs.getString("name")` = 读当前行里 name 列的值。
- 把每行"翻译"成一个 Student 对象装进 List —— 这就是**"结果集 → 对象"映射**。调用方拿到的是 Java 对象，不是原始表格。

### executeUpdate vs executeQuery 一句话
- `executeUpdate()`：`INSERT / UPDATE / DELETE`（改动数据）→ 返回"影响了几行"（int）
- `executeQuery()`：`SELECT`（查询）→ 返回 `ResultSet`（结果表）

## 五、三个 Dao 分别管什么

| Dao | 管哪张表 | 提供的方法 |
|---|---|---|
| `StudentDao` | student（学生） | add 增 / list 查全部 / update 改 / delete 删 |
| `JobDao` | job（岗位） | add / pageQuery 分页 / listByMajor 按专业 / listBySalary 按薪资 |
| `ApplicationDao` | application（投递记录） | apply 投递(默认"待审核") / updateStatus 改状态 / listByStudent 查某人全部投递 |

### 各方法关键点
- `JobDao.pageQuery`：分页用 `LIMIT ?,?`，页码换算 `(page-1)*size`。
- `JobDao.listByMajor`：用 `LIKE` 模糊匹配专业。
- `ApplicationDao.apply`：插入投递记录，状态默认 `'待审核'`。
- `ApplicationDao.updateStatus`：把某条投递的状态从"待审核"改成"已通过/已拒绝"（状态流转）。

## 六、用一句话串起来

老板(Main)说"新增学生" → 业务主管(Service) → 仓库管理员(StudentDao) → 拨通数据库电话、填好单子、存进去、挂电话 → 回报"存了 1 行"。

## 七、对应简历写法

> "熟悉 JDBC，采用 entity/dao/service 分层设计；使用 PreparedStatement 防 SQL 注入，实现学生/岗位/投递的增删改查，以及分页查询、按专业/薪资筛选、投递状态流转。"
