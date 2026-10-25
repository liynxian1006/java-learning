package oop;

/**
 * ════════════════════════════════════════════════════════════
 *  第 2 关 · 封装（Encapsulation）
 * ════════════════════════════════════════════════════════════
 *
 *  【核心概念】
 *   把数据（成员变量）藏起来用 private，只通过 public 方法对外提供受控的读写入口。
 *
 *  【为什么必须封装】
 *   如果 age 是 public，任何人都能写 s.age = -5 或 999，程序就崩了。
 *   封装后，你在 setter 里加校验，非法数据进不来。
 *
 *  【这正是你项目二里 Student.java 的写法】
 *   private String name;  +  public String getName()  +  public void setName(...)
 *
 *  【运行】右键本文件 → Run
 */
public class L2_Encapsulation {

    /** 封装后的学生类 —— 和项目二的 Student.java 结构一致 */
    static class Student {
        // ① 数据一律 private：外部无法直接修改
        private String name;
        private int age;
        private String major;

        public Student() {
        }

        public Student(String name, int age, String major) {
            setName(name);
            setAge(age);
            setMajor(major);
        }

        // ② 对外提供读取入口
        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public String getMajor() {
            return major;
        }

        // ③ 对外提供写入入口，里面做校验
        public void setName(String name) {
            if (name == null || name.trim().isEmpty()) {
                System.out.println("    [校验不通过] 姓名不能为空，已忽略本次设置");
                return;                       // 直接返回，不写入
            }
            this.name = name.trim();
        }

        public void setAge(int age) {
            if (age < 0 || age > 150) {
                System.out.println("    [校验不通过] 年龄必须在 0-150 之间，收到 " + age + "，已忽略");
                return;
            }
            this.age = age;
        }

        public void setMajor(String major) {
            this.major = major;
        }

        public void introduce() {
            System.out.println("    " + name + "，" + age + "岁，" + major + "专业");
        }
    }

    public static void main(String[] args) {
        line("第 2 关 · 封装");
        line("");

        // ────────────────────────────────────────
        // ① 正常赋值：走 setter，一切正常
        // ────────────────────────────────────────
        line("① 正常赋值");
        Student s = new Student();
        s.setName("张三");
        s.setAge(21);
        s.setMajor("计算机科学与技术");
        s.introduce();
        line("");

        // ────────────────────────────────────────
        // ② 非法值：被 setter 拦截
        // ────────────────────────────────────────
        line("② 非法值被拦截");
        System.out.println("    尝试 setAge(-5)：");
        s.setAge(-5);
        System.out.println("    尝试 setAge(999)：");
        s.setAge(999);
        System.out.println("    尝试 setName(\"\")：");
        s.setName("");
        System.out.println("  → 现在的数据还是：");
        s.introduce();
        line("    → 非法值全部被挡在外面，内存里的数据始终合法");
        line("");

        // ────────────────────────────────────────
        // ③ 如果不封装会怎样？
        // ────────────────────────────────────────
        line("③ 对比：如果字段是 public 会怎样");
        line("    假设 age 是 public，外部可以直接写：");
        line("        s.age = -5;      // 编译通过，运行也不报错，但数据已经错了");
        line("    → 错误数据进入系统，问题往往在几周后才暴露");
        line("    → 封装 + setter 校验 = 错误数据根本进不来");
        line("");

        // ────────────────────────────────────────
        // ④ 构造方法里也走 setter
        // ────────────────────────────────────────
        line("④ 构造方法内部同样调用 setter（保证校验不被绕过）");
        Student s2 = new Student("李四", -10, "软件工程");
        s2.introduce();
        line("    → 传了 -10，但被 setter 拦下，年龄保持默认值 0");
        line("");

        line("本关要点：");
        line("  · 数据 private，行为 public —— 这是 Java 的铁律");
        line("  · setter 里做校验，把「非法数据」挡在系统之外");
        line("  · 构造方法内部也走 setter，别直接写 this.age = age");
        line("  · getter 通常只读不判断，setter 才是校验的重点");
        line("  · 面试常问：为什么要 getter/setter？→ 为了控制访问权限 + 加校验 + 未来加逻辑不破坏兼容");
    }

    static void line(String s) {
        System.out.println(s);
    }
}
