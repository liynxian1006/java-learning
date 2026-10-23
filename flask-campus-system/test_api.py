"""
项目一接口自测脚本（无需 curl，避免中文乱码）
运行前提：app.py 已启动（python app.py，监听 5000）
用法：python test_api.py
"""
import json
import urllib.request
import urllib.error

BASE = "http://localhost:5000"


def call(method, path, payload=None):
    url = BASE + path
    data = json.dumps(payload).encode("utf-8") if payload else None
    req = urllib.request.Request(
        url, data=data, method=method,
        headers={"Content-Type": "application/json"},
    )
    try:
        with urllib.request.urlopen(req, timeout=5) as r:
            body = r.read().decode("utf-8")
    except urllib.error.HTTPError as e:
        body = e.read().decode("utf-8")
    try:
        return json.loads(body)
    except Exception:
        return {"raw": body}


def show(title, resp):
    print(f"===== {title} =====")
    print(json.dumps(resp, ensure_ascii=False, indent=2))
    print()


show("1) 登录 admin/123456", call("POST", "/login", {"username": "admin", "password": "123456"}))

add_resp = call("POST", "/student/add", {"name": "张三", "gender": "男", "major": "计算机", "grade": "2023"})
show("2) 新增学生", add_resp)
sid = add_resp.get("id")

show("3) 列出学生(新增后)", call("GET", "/student/list"))

if sid:
    show("4) 修改学生", call("POST", "/student/update",
                            {"id": sid, "name": "张三丰", "gender": "男", "major": "软件工程", "grade": "2023"}))
    show("5) 列出学生(修改后)", call("GET", "/student/list"))
    show("6) 删除学生", call("POST", "/student/delete", {"id": sid}))
    show("7) 列出学生(删除后)", call("GET", "/student/list"))

show("8) 列出课程", call("GET", "/course/list"))
