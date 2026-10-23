# 项目一 · Flask 校园信息管理系统（超详细手把手教程）

> 对应简历技能：**「了解 Flask 后端搭建、路由配置、前后端交互」**
> 对应简历项目：**「校园信息管理系统（Python + Flask）」**
>
> 这份教程的目标：让你从「装包」开始，**亲手把这个项目跑起来、用接口测通、并且能讲清楚它为什么这么写**。
> 全程零基础友好，每一步都有可直接复制的命令。

---

## 0. 先看成果：这个项目能干嘛

它是一套**后端接口服务**（没有网页界面，但你可以用工具调它）。包含 3 张表、6 个接口：

| 接口 | 方法 | 作用 | 对应业务 |
|---|---|---|---|
| `/login` | POST | 用户名密码登录 | 用户登录 |
| `/student/add` | POST | 新增一个学生 | 学生管理 |
| `/student/list` | GET | 列出所有学生 | 学生管理 |
| `/student/update` | POST | 修改学生信息 | 学生管理 |
| `/student/delete` | POST | 删除一个学生 | 学生管理 |
| `/course/list` | GET | 列出所有课程 | 课程管理 |

跑起来后，你的程序监听 `http://localhost:5000`，前端（或测试工具）发请求过来，它查数据库再返回 JSON 结果。

---

## 1. 技术栈名词先扫盲（不懂没关系，先有个印象）

| 名词 | 大白话 |
|---|---|
| **Flask** | 一个 Python 写的「Web 框架」，帮你快速接收请求、返回数据 |
| **路由（route）** | 一个网址对应一段代码，比如访问 `/login` 就执行登录逻辑 |
| **接口（API）** | 程序对外提供的「功能入口」，你发请求它给结果 |
| **JSON** | 一种数据格式，长这样 `{"code":200,"msg":"登录成功"}`，前后端都用它传数据 |
| **GET / POST** | HTTP 两种请求方式：GET 用来「取数据」，POST 用来「提交数据」 |
| **MySQL** | 数据库，存你的 user / student / course 数据 |
| **pymysql** | Python 连 MySQL 的驱动（就像 Java 用 JDBC 连库） |
| **CORS** | 解决「前端网页和后端不在同一地址」时的跨域限制 |

---

## 2. 第一步：安装 Python 依赖（装 3 个包）

项目需要 `Flask`、`Flask-CORS`、`PyMySQL` 三个包。你本机已有 Python 3.13，直接用 pip 装：

打开 **PowerShell / Git Bash / CMD**，执行：

```bash
pip install Flask==3.0.0 Flask-CORS==4.0.0 PyMySQL==1.1.0
```

> 如果提示 `pip` 不是内部命令，改用：
> ```bash
> python -m pip install Flask==3.0.0 Flask-CORS==4.0.0 PyMySQL==1.1.0
> ```

✅ 验证装成功（看到版本号即成功）：
```bash
pip show Flask
```

---

## 3. 第二步：用 Navicat 建库建表（按你习惯，走 Navicat）

> 你已要求「数据库操作默认用 Navicat Premium 16」，所以这一步用 Navicat 做。

1. 打开 **Navicat Premium 16** → 左上角 **连接** → **MySQL**。
   - 连接名：随便填（如 `local`）
   - 主机：`localhost`，端口：`3306`
   - 用户名：`root`，密码：你的 MySQL 密码
   - 点 **测试连接** → 成功 → **确定**。
2. 双击左侧刚建的连接，连上后**右键连接名 → 新建数据库**：
   - 数据库名：`campus_db`
   - 字符集：`utf8mb4`（选这个，中文不会乱码）
   - 确定。
3. 双击打开 `campus_db` → 点顶部 **查询** → **新建查询**。
4. 把下面这段 SQL **整段复制** 粘贴进查询窗口，点 **运行**：

```sql
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
```

5. 刷新 `campus_db` 下的「表」，能看到 `user` / `student` / `course` 三张表就成功了。
6. （可选）给 `user` 表插一条登录测试数据：在查询窗口执行
   ```sql
   INSERT INTO user(username, password, role) VALUES('admin','123456','admin');
   ```
   这样你就能用 `admin / 123456` 测试登录接口了。

---

## 4. 第三步：改数据库密码

打开 `flask-campus-system/app.py`，找到第 20–28 行：

```python
DB_CONFIG = {
    "host": "localhost",
    "user": "root",
    "password": "你的密码",   # ← 把这行改成你的真实 MySQL 密码
    "db": "campus_db",
    "charset": "utf8mb4",
    "cursorclass": pymysql.cursors.DictCursor,
}
```

把 `"你的密码"` 改成你的真实密码，例如 `"123456"`。

> ⚠️ 安全提醒：这个文件现在密码是占位符，提交 GitHub 安全；一旦填了真实密码，**公开仓库前请改成读环境变量**（教程末尾有方法），避免泄露。

---

## 5. 第四步：读懂代码结构（逐块讲）

`app.py` 一共 136 行，拆成 5 块理解：

### 块 A：导入（第 1–15 行）
```python
import pymysql
from flask import Flask, request, jsonify
from flask_cors import CORS

app = Flask(__name__)   # 创建一个 Flask 应用实例
CORS(app)               # 允许前端跨域访问
```
- `Flask(__name__)`：初始化一个 Web 应用，`__name__` 是当前文件名。
- `CORS(app)`：开放跨域，否则浏览器会拦截前端请求。

### 块 B：数据库配置（第 20–28 行）
见第 4 步。这是一个**字典**，集中放连接信息，`get_conn()` 会用到它。

### 块 C：连接函数（第 31–33 行）
```python
def get_conn():
    return pymysql.connect(**DB_CONFIG)
```
- 每次要操作数据库就调用它，返回一个连接对象。
- `**DB_CONFIG` 是把字典拆成关键字参数传进去（Python 语法）。

### 块 D：6 个接口（核心，下面逐一讲）
每个接口 = 一个 `@app.route` 装饰器 + 一个函数。结构都遵循同一条「安全模板」：

```python
@app.route("/xxx", methods=["POST"])   # ① 声明网址和方法
def xxx():
    ...                                # ② 取请求数据
    conn = get_conn()                  # ③ 拿连接
    try:
        with conn.cursor() as cur:     # ④ 开游标（执行 SQL 的句柄）
            cur.execute("SQL", (参数,)) # ⑤ 执行（参数化，防注入）
            conn.commit()              #    写操作要提交
        return jsonify({...})          # ⑥ 返回 JSON
    except Exception as e:
        conn.rollback()                # 出错回滚
        return jsonify({"code": 500, "msg": str(e)})
    finally:
        conn.close()                   # ⑦ 无论如何关闭连接（关键！）
```

**为什么要 `try/finally/close`？** 因为数据库连接是有限资源，不关会耗尽。无论成功失败，`finally` 里的 `conn.close()` 都会执行——这是**后端必备的稳健写法**，面试常考。

### 块 E：启动（第 134–135 行）
```python
if __name__ == "__main__":
    app.run(debug=True, port=5000)
```
- `debug=True`：改代码自动重启、出错给详细提示（**生产环境要关掉**）。
- `port=5000`：监听 5000 端口。

---

## 6. 第五步：运行项目

在终端进入项目目录并运行：

```bash
cd C:\Users\34118\WorkBuddy\2026-10-23-11-27-11\java-learning\flask-campus-system
python app.py
```

看到类似输出说明启动成功：
```
 * Running on http://127.0.0.1:5000
 * Debug mode: on
```
**保持这个窗口开着不要关**，它就是你的服务器。

> 也可以在 IDEA 里运行：IDEA 打开文件夹 → 右键 `app.py` → Run 'app'。

---

## 7. 第六步：测试每个接口（最关键，验证你会用）

下面给你两种测试方式，任选其一。

### 方式一：curl（推荐在 Git Bash 里跑，对引号最友好）
> Git Bash 里单引号不会被 Windows 转义，最简单。

```bash
# 1) 登录（POST） 记得先按第 3 步插了 admin/123456
curl -X POST http://localhost:5000/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 2) 新增学生（POST）
curl -X POST http://localhost:5000/student/add \
  -H "Content-Type: application/json" \
  -d '{"name":"张三","gender":"男","major":"计算机","grade":"2023"}'

# 3) 列出学生（GET，浏览器直接访问也行）
curl http://localhost:5000/student/list

# 4) 修改学生（POST，把 id 改成你新增返回的 id）
curl -X POST http://localhost:5000/student/update \
  -H "Content-Type: application/json" \
  -d '{"id":1,"name":"张三丰","gender":"男","major":"软件工程","grade":"2023"}'

# 5) 删除学生（POST）
curl -X POST http://localhost:5000/student/delete \
  -H "Content-Type: application/json" \
  -d '{"id":1}'

# 6) 列出课程（GET）
curl http://localhost:5000/course/list
```

### 方式二：Postman / 浏览器（不想记命令时用）
- **GET 接口**（`/student/list`、`/course/list`）：直接浏览器打开 `http://localhost:5000/student/list` 就能看到 JSON。
- **POST 接口**：用 Postman，选 POST，Body → raw → JSON，粘贴上面的 `{}` 内容发送。

✅ 每个接口返回里带 `"code":200` 就是成功。

---

## 8. 第七步：常见报错与排查（零基础必看）

| 现象 | 原因 | 解决 |
|---|---|---|
| `ModuleNotFoundError: No module named 'flask'` | 没装包 | 回到第 2 步重装 |
| `Access denied for user 'root'@'localhost'` | 密码错 | 改 `app.py` 里的 `password` |
| `Unknown database 'campus_db'` | 没建库 | 回到第 3 步用 Navicat 建库建表 |
| 接口返回 500 且 msg 含 SQL 语法 | SQL 写错/字段名错 | 检查表字段与代码是否一致 |
| 浏览器访问 GET 正常，POST 报 405 | methods 没写 POST | 检查 `@app.route(..., methods=["POST"])` |
| 中文返回乱码 | 数据库或连接字符集问题 | 确保建库用 `utf8mb4`，连接 `charset` 也是 |

---

## 9. 这个项目体现了哪些「简历技能点」

| 简历写法 | 本项目对应的真实做法 |
|---|---|
| 了解 Flask 后端搭建 | 用 Flask 起了 Web 服务、注册了 6 个路由 |
| 路由配置 | `@app.route("/student/add", methods=["POST"])` 等 |
| 前后端交互 | 前端发 JSON → 后端 `request.get_json` 收 → `jsonify` 返回 JSON |
| 库表设计 | 设计了 user / student / course 三张表及字段 |
| SQL / CRUD | 每个接口对应增删改查，用 `pymysql` 执行 |
| 异常处理 | 每个接口 `try/except/finally`，出错回滚、必关连接 |

---

## 10. 面试怎么讲这个项目（背这套话术）

> 「我做了一个校园信息管理系统，用 Python 的 Flask 框架写后端，MySQL 存数据。
> 设计了三张表：用户表、学生表、课程表。
> 提供了登录、学生增删改查、课程查询共 6 个接口。
> 用 pymysql 操作数据库，每个接口都做了异常捕获和连接关闭，保证稳定。
> 前后端通过 JSON 交互，并用 Flask-CORS 解决了跨域问题。」

能讲清上面这段，简历上「了解 Flask 后端搭建、路由配置、前后端交互」就**坐实了**。

---

## 11. 进阶加分项（做完基础再挑战）

1. **密码加密**：现在密码是明文，可学 `bcrypt` 哈希存储。
2. **连接池**：把 `get_conn()` 换成连接池，提升并发性能。
3. **加前端页面**：用简单的 HTML+JS 调这些接口，做出能点的界面。
4. **参数校验**：用 `flask-wtf` 或手写校验，防止空数据入库。
5. **JWT 登录态**：登录后发 token，后续接口校验 token（校招加分）。

---

## 12. 本周打勾清单

- [ ] 装好 Flask / Flask-CORS / PyMySQL 三个包
- [ ] 用 Navicat 建好 `campus_db` 和 3 张表
- [ ] 在 `user` 表插入一条测试账号
- [ ] 改好 `app.py` 里的数据库密码
- [ ] 运行 `app.py` 看到 `Running on http://127.0.0.1:5000`
- [ ] 用 curl/Postman 把 6 个接口逐个测通（返回 code:200）
- [ ] 能口头讲出「这个项目怎么设计的」

---

## 附：把密码改成读环境变量（公开仓库前必做）

把 `app.py` 第 24 行改成：
```python
import os
...
"password": os.getenv("DB_PASSWORD", "你的密码"),
```
然后在本机设置环境变量 `DB_PASSWORD=真实密码`。这样代码里不再写死密码，提交 GitHub 也安全。
