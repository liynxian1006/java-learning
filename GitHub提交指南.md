# GitHub 提交详细做法（零基础版）

> 目标：把你 `java-learning/` 里的代码（含今天新增的 `Linux命令速查.md`、`java-basics/`）推到 GitHub 私有库 `liynxian1006/java-learning`。
> 仓库和远程连接已建好，你只需做"提交 + 推送"。

---

## ⚠️ 先说一个前提（很重要）
本 WorkBuddy 环境的网络代理会拦截 GitHub，**在这个聊天里的终端推不出去**。
请在你**自己电脑上**打开终端或 IDEA 操作（它们有真实网络）。文件路径是：
```
C:\Users\34118\WorkBuddy\2026-10-23-11-27-11\java-learning
```

---

## 方法一：用 IntelliJ IDEA（推荐，最直观，不用记命令）

### 第 1 步 · 打开 Commit 面板
- 顶部菜单：**Git → Commit**（或按 `Ctrl + K`）。
- 或点左侧/底部那个 **绿色对勾** 图标（Commit 工具窗）。

### 第 2 步 · 选文件 + 写说明
面板里你会看到待提交的文件（未跟踪的显示绿色）：
- `Linux命令速查.md`
- `java-basics/`（展开后有 `JavaBasics.java`、`MethodsDemo.java`、两个 `.md`）
- 注意：`.gitignore` 已自动排除 `out/`（编译产物）、`.idea/`（IDE 配置），**这些不会出现**，放心全选。
- 在 **Commit Message** 框输入：
  ```
  add: Linux命令速查 + Java语法入门与方法详解
  ```
- 点 **Commit** 按钮（先别急着 Push，分两步更清楚）。

### 第 3 步 · 推送到 GitHub
- 顶部菜单：**Git → Push**（或按 `Ctrl + Shift + K`）。
- 弹出窗口显示 `origin / main`，点 **Push**。
- **第一次会要求登录 GitHub**：IDEA 会弹浏览器让你授权，用你的 GitHub 账号登录允许即可（比令牌简单）。

### 第 4 步 · 验证
打开 https://github.com/liynxian1006/java-learning ，能看到新提交和两个新文件夹即成功。

---

## 方法二：用终端（PowerShell / Git Bash / CMD）

### 第 1 步 · 打开终端并进入目录
- 按 `Win` 键，输入 `PowerShell` 或 `Git Bash` 打开。
- 粘贴下面这行（进入项目目录）：
  ```
  cd "C:\Users\34118\WorkBuddy\2026-10-23-11-27-11\java-learning"
  ```

### 第 2 步 · 暂存改动
```
git add .
```
（`add .` 把当前目录所有改动加入暂存区；`.gitignore` 里的文件自动跳过）

### 第 3 步 · 提交（生成版本快照）
```
git commit -m "add: Linux命令速查 + Java语法入门与方法详解"
```

### 第 4 步 · 推送 + 认证
```
git push -u origin main
```
- **GitHub 已不支持账号密码登录**，推送时认证方式二选一：
  - **(推荐) 浏览器登录**：Windows 上一般装了 Git Credential Manager，会弹浏览器让你登录 GitHub，登录一次后续免密。
  - **(备用) 令牌 Token**：若让你输密码，那里要填 **Personal Access Token（不是登录密码）**。
    - 生成令牌：GitHub 网页 → 右上角头像 → **Settings → Developer settings → Personal access tokens → Tokens (classic) → Generate new token** → 勾选 `repo` 权限 → 生成 → **复制**（只显示一次，存好）。
    - 用户名填 GitHub 账号，密码框粘贴这个 token。

### 第 5 步 · 验证
同方法一第 4 步，去仓库页面确认。

---

## 常见报错处理
| 现象 | 原因 | 解决 |
|---|---|---|
| `failed to push some refs` | 远程有本地没有的提交（如在网页端改过 README） | 先 `git pull origin main`，再 `git push` |
| 要求用户名密码但密码不对 | 用了 GitHub 登录密码 | 改用 Personal Access Token（见上） |
| `remote: Repository not found` | 仓库名/权限不对 | 确认库存在且是你账号下的私有库 |
| 推送一直转圈/超时 | 当前网络连不上 GitHub | 换网络，或用 IDEA 的浏览器登录方式 |

---

## 以后的日常流程（记住这四步）
1. 改代码 / 加文件
2. `git add .`
3. `git commit -m "这次改了啥"`
4. `git push`

就这一套，反复用。

---

## 🔒 安全提醒（务必看）
目前 `DBUtil.java` 里数据库密码是占位符 `"你的密码"`，提交安全。
**等你填入真实 MySQL 密码后，提交前请务必改成读环境变量**（或确认该文件不被提交），否则密码会随代码上传，有泄露风险。
需要的话可以让助手帮你把密码改成从环境变量读取。
