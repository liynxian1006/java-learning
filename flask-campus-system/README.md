# 校园信息管理系统（Python + Flask 骨架）

对应简历项目：**校园信息管理系统**

## 技术栈
- Python 3、Flask、PyMySQL、MySQL 8

## 运行步骤
1. `pip install -r requirements.txt`
2. 在 MySQL 中执行 `db_init.sql` 建库建表
3. 修改 `app.py` 中 `DB_CONFIG` 的 `password` 为你的数据库密码
4. `python app.py` → 访问 http://localhost:5000

## 已提供的接口
| 方法 | 路径 | 说明 |
|---|---|---|
| POST | /login | 登录校验 |
| POST | /student/add | 新增学生 |
| GET  | /student/list | 学生列表 |
| POST | /student/update | 修改学生 |
| POST | /student/delete | 删除学生 |
| GET  | /course/list | 课程列表 |

> 这是阶段 2 的「Flask 项目」骨架，业务逻辑已可直接运行，你可在其上继续扩展（如加权限、加分页）。
