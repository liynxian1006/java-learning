"""
校园信息管理系统 - Flask 后端骨架
对应简历项目：校园信息管理系统（Python + Flask）
数据表：user / student / course
接口：/login  /student/add  /student/list  /student/update  /student/delete  /course/list

运行步骤：
  1. pip install -r requirements.txt
  2. 在 MySQL 中执行 db_init.sql 建库建表
  3. 修改下方 DB_CONFIG 的 password 为你的数据库密码
  4. python app.py   （默认 http://localhost:5000）
"""
import os
import pymysql
from flask import Flask, request, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # 允许前端跨域调用

# ===== 数据库配置（请改成你自己的）=====
DB_CONFIG = {
    "host": "localhost",
    "user": "root",
    "password": os.getenv("DB_PASSWORD", "123456"),
    "db": "campus_db",
    "charset": "utf8mb4",
    "cursorclass": pymysql.cursors.DictCursor,
}


def get_conn():
    """获取数据库连接（模板方法，后续可替换为连接池）"""
    return pymysql.connect(**DB_CONFIG)


@app.route("/login", methods=["POST"])
def login():
    data = request.get_json(silent=True) or {}
    username = data.get("username")
    password = data.get("password")
    conn = get_conn()
    try:
        with conn.cursor() as cur:
            cur.execute(
                "SELECT id, username, role FROM user WHERE username=%s AND password=%s",
                (username, password),
            )
            user = cur.fetchone()
        if user:
            return jsonify({"code": 200, "msg": "登录成功", "data": user})
        return jsonify({"code": 401, "msg": "用户名或密码错误"})
    finally:
        conn.close()


@app.route("/student/add", methods=["POST"])
def student_add():
    data = request.get_json(silent=True) or {}
    conn = get_conn()
    try:
        with conn.cursor() as cur:
            cur.execute(
                "INSERT INTO student(name, gender, major, grade) VALUES(%s,%s,%s,%s)",
                (data.get("name"), data.get("gender"), data.get("major"), data.get("grade")),
            )
            conn.commit()
            return jsonify({"code": 200, "msg": "新增成功", "id": cur.lastrowid})
    except Exception as e:
        conn.rollback()
        return jsonify({"code": 500, "msg": str(e)})
    finally:
        conn.close()


@app.route("/student/list", methods=["GET"])
def student_list():
    conn = get_conn()
    try:
        with conn.cursor() as cur:
            cur.execute("SELECT * FROM student")
            rows = cur.fetchall()
        return jsonify({"code": 200, "data": rows})
    finally:
        conn.close()


@app.route("/student/update", methods=["POST"])
def student_update():
    data = request.get_json(silent=True) or {}
    conn = get_conn()
    try:
        with conn.cursor() as cur:
            cur.execute(
                "UPDATE student SET name=%s, gender=%s, major=%s, grade=%s WHERE id=%s",
                (data.get("name"), data.get("gender"), data.get("major"), data.get("grade"), data.get("id")),
            )
            conn.commit()
            return jsonify({"code": 200, "msg": "更新成功", "rows": cur.rowcount})
    except Exception as e:
        conn.rollback()
        return jsonify({"code": 500, "msg": str(e)})
    finally:
        conn.close()


@app.route("/student/delete", methods=["POST"])
def student_delete():
    data = request.get_json(silent=True) or {}
    conn = get_conn()
    try:
        with conn.cursor() as cur:
            cur.execute("DELETE FROM student WHERE id=%s", (data.get("id"),))
            conn.commit()
            return jsonify({"code": 200, "msg": "删除成功", "rows": cur.rowcount})
    except Exception as e:
        conn.rollback()
        return jsonify({"code": 500, "msg": str(e)})
    finally:
        conn.close()


@app.route("/course/list", methods=["GET"])
def course_list():
    conn = get_conn()
    try:
        with conn.cursor() as cur:
            cur.execute("SELECT * FROM course")
            rows = cur.fetchall()
        return jsonify({"code": 200, "data": rows})
    finally:
        conn.close()


if __name__ == "__main__":
    app.run(debug=True, port=5000)
