# Linux 基础命令速查（10 条核心）

> 目标：能看懂并在终端里完成"进入目录 → 看文件 → 建/删/复制/移动文件 → 看内容 → 清屏"。
> 在 Windows 上可用 Git Bash / PowerShell / WSL 练习这几条（语法基本一致）。

---

## 🌟 最重要（导航三件套：先学会这三个）
| 命令 | 作用 | 常用示例 |
|---|---|---|
| `pwd` | 显示当前所在目录（绝对路径） | `pwd` → `/c/Users/34118` |
| `ls` | 列出当前目录的文件/文件夹 | `ls` 平铺；`ls -l` 详情；`ls -a` 含隐藏文件 |
| `cd` | 切换目录 | `cd 项目名` 进入；`cd ..` 返回上一级；`cd ~` 回家目录；`cd /` 到根 |

---

## 文件与目录操作（高频）
| 命令 | 作用 | 常用示例 |
|---|---|---|
| `mkdir` | 新建文件夹 | `mkdir demo`；`mkdir -p a/b/c` 多级创建 |
| `touch` | 新建空文件 | `touch test.txt` |
| `cp` | 复制文件/目录 | `cp a.txt b.txt`；`cp -r dir1 dir2`（目录要加 `-r`） |
| `mv` | 移动 / 重命名 | `mv a.txt /tmp/`（移动）；`mv old.txt new.txt`（改名） |
| `rm` | 删除文件/目录 | `rm a.txt`；`rm -r dir`（删目录）；⚠️ `rm -rf` 强制递归删除，慎用！ |

---

## 查看内容与清屏
| 命令 | 作用 | 常用示例 |
|---|---|---|
| `cat` | 一次性打印文件全部内容 | `cat README.md` |
| `clear` | 清空终端屏幕 | `clear`（或快捷键 `Ctrl+L`） |

---

## 一条最小实操流程（照着敲一遍就懂）
```bash
pwd            # 我现在在哪
cd ~/Desktop   # 进入桌面
mkdir linux-practice   # 建个练习文件夹
cd linux-practice
touch hello.txt        # 建个空文件
ls -l                  # 看看是不是有了
cat hello.txt          # 看内容（空的）
cp hello.txt copy.txt  # 复制一份
mv copy.txt renamed.txt# 改名
rm hello.txt           # 删掉原文件
clear                  # 清屏
```

## 零基础必记的安全提醒
- `rm` 删除后**一般无法恢复**，删目录务必确认路径；不要对 `/`、`Desktop` 等跑 `rm -rf`。
- `cd` 后面路径含空格要用引号：`cd "my folder"`。
- 记不住命令用法时，几乎都能 `命令 --help` 看说明（如 `ls --help`）。
