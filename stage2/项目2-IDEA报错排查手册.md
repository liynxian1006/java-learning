# 项目二 · IDEA 报错排查手册（No suitable driver / ClassNotFoundException）

> 这份文档解决你在 IDEA 里跑项目二时遇到的驱动问题，以及这一类"依赖找不到"的通用解法。

---

## 一、你遇到的报错（截图原文）

```
java.lang.ClassNotFoundException: com.mysql.cj.jdbc.Driver
    at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:502)
    at java.base/java.lang.Class.forName(Class.java:478)
    at com.campus.util.DBUtil.<clinit>(DBUtil.java:28)
    at com.campus.trace.JdbcTraceDemo.main(JdbcTraceDemo.java:31)

连接失败: No suitable driver found for jdbc:mysql://localhost:3306/campus_job_db?...
```

### 逐行翻译这个堆栈（重点看最下面两行）

| 堆栈行 | 含义 |
|---|---|
| `ClassLoader.loadClass` | JVM 在 classpath 里找类 |
| `Class.forName` | 代码里那句 `Class.forName("com.mysql.cj.jdbc.Driver")` |
| `DBUtil.<clinit>(DBUtil.java:28)` | **DBUtil 的静态块**（第 28 行就是加载驱动那句） |
| `JdbcTraceDemo.main` | 从 main 进去的 |

### 一句话根因
> **JDBC 驱动 jar 没有被放进项目的 classpath。** JVM 翻遍 classpath 找不到 `com.mysql.cj.jdbc.Driver` 这个类，于是抛 `ClassNotFoundException`；驱动没注册，`DriverManager` 自然也找不到能处理 `jdbc:mysql://` 的驱动，于是抛 `No suitable driver found`。

这是**第二句报错**（`No suitable driver`）的**根本原因**。两句报错是同一个病的两条症状。

> 补充一个概念：`Class.forName` 加载驱动这件事，**MySQL 5.x 时代是必须的**。从 JDBC 4.0 起驱动支持 SPI 自动注册，理论上不写也能用；但保留这句是行业惯例，也能提早暴露"jar 没加"这类问题，**建议保留**。

---

## 二、已为你彻底修好（不用你再操作 GUI）

我做的是**双保险**，两条路都通：

### 方案 A：项目内置 jar + 已配好 classpath（已生效）
- 驱动 jar 已复制到项目内：`java-jdbc-campus-system/lib/mysql-connector-j-8.0.33.jar`
- 模块配置文件 `java-jdbc-campus-system.iml` 已加入 module-library 引用：
  ```xml
  <orderEntry type="module-library">
    <library name="mysql-connector-j">
      <CLASSES>
        <root url="jar://$MODULE_DIR$/lib/mysql-connector-j-8.0.33.jar!/" />
      </CLASSES>
      ...
    </library>
  </orderEntry>
  ```

### 方案 B：Maven 化（已生成 pom.xml，长期方案）
`java-jdbc-campus-system/pom.xml` 已写好：
```xml
<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
  <version>8.3.0</version>
</dependency>
```
IDEA 识别到 pom.xml 会自动把依赖挂进 classpath，**以后换任何依赖都不用手动加 jar**。而且阶段三学 Spring Boot 必须要 Maven，现在打好底正好。

### 方案 C：不想等 IDEA 刷新缓存 → 双击 run.bat
```
java-jdbc-campus-system/run.bat
```
它会自己编译 + 运行，**完全不需要 IDEA**。已实测通过：
```
[1/2] Compiling...
Compile OK.
[2/2] Running...
新增影响行数：1
1 | 张三 | 计算机科学
```

---

## 三、你现在在 IDEA 里该做的（1 分钟）

IDEA 会在你切回窗口时提示"外部文件变更，是否重新加载"，**选 Reload（重新加载）**即可。
如果没提示，手动操作一次：

1. 菜单 **File → Reload All from Disk**（从磁盘全部重新加载）
2. 或者直接关掉 IDEA 再打开项目

之后右键 `JdbcTraceDemo.java` → Run，应该就能看到：
```
连接对象 = com.mysql.cj.jdbc.ConnectionImpl
是否有效 = true   → true 表示电话已接通
```

> 若 IDEA 提示是否把 `java-jdbc-campus-system` 作为 **Maven 项目**加载（Load Maven Project），**选它**——这样依赖就彻底由 Maven 管，最省心。

---

## 四、如果还想手动加一次（学会这个技能很重要）

这是每个 Java 后端都必须会的操作：

1. 菜单 **File → Project Structure**（快捷键 `Ctrl+Alt+Shift+S`）
2. 左侧选 **Libraries**，点 **＋** → **Add JARs or Directories...**
3. 选 `java-jdbc-campus-system/lib/mysql-connector-j-8.0.33.jar`
4. 选 **Apply → OK**
5. 再运行，应该就好了

> 注意：加完要确认 **加对了模块**。IDEA 右侧有模块列表（`java-learning` / `java-jdbc-campus-system`），要确保库挂在 `java-jdbc-campus-system` 模块下。

---

## 五、同类错误的通用排查顺序（以后自己就能查）

遇到 `ClassNotFoundException` / `No suitable driver`，按这个顺序查：

| 顺序 | 检查项 | 怎么查 |
|---|---|---|
| 1 | jar 到底在不在 | 项目 `lib/` 目录、或 `~/.m2/repository` 里有没有 |
| 2 | jar 有没有进 classpath | IDEA 里 **File → Project Structure → Modules → Dependencies** 看列表里有没有 |
| 3 | 类名写对没有 | jar 包里的类路径要和 `Class.forName("...")` 完全一致 |
| 4 | JDK 版本对不对 | jar 编译版本不能高于你项目用的 JDK（你项目 JDK 17，驱动 8.x 支持 8+） |
| 5 | 数据库服务起没起 | `Windows 服务` 里 MySQL80 是否 Running |

### 怎么确认 jar 里的类名到底叫什么
解压 jar 看目录结构即可。`mysql-connector-j-8.0.33.jar` 里的实际类：
```
com/mysql/cj/jdbc/Driver.class        ← 这个才是要加载的
com/mysql/cj/jdbc/NonRegisteringDriver.class
com/mysql/jdbc/Driver.class           ← 旧版兼容入口
```
所以 `Class.forName("com.mysql.cj.jdbc.Driver")` 是对的。

---

## 六、本次修复涉及的文件清单

| 文件 | 改动 | 作用 |
|---|---|---|
| `java-jdbc-campus-system/lib/mysql-connector-j-8.0.33.jar` | 新增 | 项目内置驱动 |
| `java-jdbc-campus-system/java-jdbc-campus-system.iml` | 修改 | 挂载 module-library 到 classpath |
| `java-jdbc-campus-system/pom.xml` | 新增 | Maven 化，声明驱动依赖 |
| `java-jdbc-campus-system/run.bat` | 新增 | 一键编译运行，不依赖 IDEA |
| `java-learning/java-learning.iml` | 修改 | 把 `stage1` 加入源码目录（之前只有 java-basics） |
| `java-learning/.gitignore` | 修改 | 忽略 `target/` 与 `lib/` |
| `src/com/campus/Main.java` | 修改 | 更新过时提示文案 |

---

## 七、顺带修复的其他问题

**① `stage1/` 里的示例在 IDEA 里不能 Run**
原因：根模块 `java-learning.iml` 的 sourceFolder 只有 `java-basics`，`stage1` 不在源码目录里，IDEA 把它当普通文件。已把 `stage1` 加入，现在 `Stage1Demo.java`、`Stage1OopExtra.java` 都能直接 Run。

**② Maven 离线构建报插件缺失**
```
Cannot access alimaven ... in offline mode and the artifact 
org.apache.maven.plugins:maven-resources-plugin:jar:3.4.0 has not been downloaded
```
原因：Maven 3.9.16 默认插件版本比本机仓库里的新。已在 pom 里把插件版本**钉死**到本地已有版本：
`maven-resources-plugin:3.3.1`、`maven-compiler-plugin:3.13.0`、`maven-surefire-plugin:3.1.2`、`maven-jar-plugin:3.4.2`、`maven-install-plugin:3.1.4`。

**③ 驱动依赖 protobuf 缺失**
```
dependency: com.google.protobuf:protobuf-java:jar:3.25.1 (compile) ... has not been downloaded
```
原因：`mysql-connector-j:8.3.0` 传递依赖 protobuf-java，但本机仓库没有。已在 pom 中 **exclude 掉 protobuf-java**——它只在 MySQL X DevAPI（实验性协议）里用，普通 JDBC 用不到，不影响功能。

> 已验证：`mvn -o -DskipTests compile` → **BUILD SUCCESS**（11 个源文件全部编译通过）。

---

## 八、命令速查

```bash
# Maven 编译（离线可用）
export PATH="/d/apache-maven-3.9.16-bin/apache-maven-3.9.16/bin:$PATH"
cd java-jdbc-campus-system
mvn -o -DskipTests compile

# 手动编译运行（Git Bash，注意 classpath 分隔符是 ; ，路径要用 C:/ 格式）
JAR="C:/Users/34118/.m2/repository/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar"
javac -encoding UTF-8 -cp "$JAR" -d out $(find src -name "*.java")
java -Dfile.encoding=UTF-8 -cp "out;$JAR" com.campus.Main
java -Dfile.encoding=UTF-8 -cp "out;$JAR" com.campus.trace.JdbcTraceDemo
```

> ⚠️ Git Bash 里给 Java 的 classpath 路径必须写成 `C:/Users/...` 这种 **Windows 格式**；
> 写 `/c/Users/...` Java 认不出，会报 `ClassNotFoundException`（这是另一个和本次报错长得像、但原因不同的问题）。
