package oop;

/**
 * ════════════════════════════════════════════════════════════
 *  专题 · 「声明并初始化」vs「先声明后赋值」
 * ════════════════════════════════════════════════════════════
 *
 *  【你问的两行】
 *      s1 = new Student("张三", 21, "计算机科学与技术");
 *      Student s2 = new Student("李四", 20, "软件工程");
 *
 *  【表面区别】
 *      s1 那行：= 左边【已经有 s1 这个变量了】，这行只是给它赋新值 → 赋值
 *      s2 那行：= 左边【第一次出现 s2 这个名字】，声明 + 赋值一步到位 → 声明并初始化
 *
 *  【本质区别：几乎没有】
 *      运行时做的事完全一样：new 一个对象 → 调构造方法 → 把对象地址存进变量。
 *      区别只在【编译期】：Java 要求"先声明，后使用"。
 *
 *  【什么时候用哪种】
 *      · 声明并初始化（`Student s2 = new Student(...)`）—— 首选，一句搞定
 *      · 先声明后赋值 —— 当初值暂时拿不到时
 *          Student s3;            // 先声明
 *          s3 = new Student(...); // 后面的某行再赋值
 *
 *  【运行】右键本文件 → Run
 */
public class L9_DeclarationVsInit {

    static class Student {
        String name;
        int age;
        String major;

        Student() {
        }

        Student(String name, int age, String major) {
            this.name = name;
            this.age = age;
            this.major = major;
        }
    }

    public static void main(String[] args) {
        line("专题 · 声明并初始化 vs 先声明后赋值");
        line("");

        // ══════════════════════════════════════════════════
        // ① 两种写法，运行时完全等价
        // ══════════════════════════════════════════════════
        line("① 两种写法的运行时行为完全一样");

        // 写法 A：先声明，后赋值（对应你问的 s1）
        Student s1;
        s1 = new Student("张三", 21, "计算机科学与技术");

        // 写法 B：声明并初始化（对应你问的 s2）
        Student s2 = new Student("李四", 20, "软件工程");

        line("    写法A：Student s1;  s1 = new Student(...)   ← 声明 + 赋值");
        line("    写法B：Student s2 = new Student(...)         ← 声明并初始化");
        line("    s1 = " + s1.getClass().getSimpleName() + "，姓名 " + s1.name);
        line("    s2 = " + s2.getClass().getSimpleName() + "，姓名 " + s2.name);
        line("    → 运行时都是：new 对象 → 调构造 → 存地址，一模一样");
        line("");

        // ══════════════════════════════════════════════════
        // ② 拆开写，等价于合并写
        // ══════════════════════════════════════════════════
        line("② 写法B 拆开写，完全等价于写法A");
        Student s2b;
        s2b = new Student("李四", 20, "软件工程");
        line("    s2  == s2b ? " + (s2 == s2b)
                + "  → false，因为是两个不同的对象（各自 new 的）");
        line("    但内容相同：" + s2.name.equals(s2b.name)
                + "（值相等但引用不同）");
        line("");

        // ══════════════════════════════════════════════════
        // ③ 什么时候该用「先声明后赋值」
        // ══════════════════════════════════════════════════
        line("③ 什么时候用「先声明后赋值」");
        line("    场景：初值要等条件判断后才确定");
        Student s3 = null;
        int score = 85;
        if (score >= 60) {
            s3 = new Student("及格同学", 20, "计算机");
        } else {
            s3 = new Student("不及格同学", 20, "计算机");
        }
        line("    → 先声明为 null，两个分支里再赋值，代码更清晰");
        line("    → 这就是「先声明后赋值」的典型场景");
        line("");

        // ══════════════════════════════════════════════════
        // ④ 坑 1：重复声明
        // ══════════════════════════════════════════════════
        line("④ 坑1：同一个作用域里不能重复声明");
        line("    Student s4 = new Student(...);");
        line("    Student s4 = new Student(...);   ← 编译报错！");
        line("        变量 s4 已经被定义了");
        line("    → 正确：直接赋值 s4 = new Student(...);");
        line("");

        // ══════════════════════════════════════════════════
        // ⑤ 坑 2：局部变量必须先赋值才能用
        // ══════════════════════════════════════════════════
        line("⑤ 坑2：局部变量声明后必须先赋值才能用");
        Student s5;
        line("    Student s5;");
        line("    s5.name;          ← 编译报错：变量 s5 可能尚未初始化");
        line("    → 局部变量没有默认值 unlike 成员变量");
        line("    → 对比：成员变量（字段）不赋值也有默认值 null / 0");
        line("");

        // ══════════════════════════════════════════════════
        // ⑥ 成员变量的默认值（面试常考）
        // ══════════════════════════════════════════════════
        line("⑥ 成员变量 vs 局部变量的默认值");
        Defaults d = new Defaults();
        line("    成员变量未显式赋值时：");
        line("      引用类型（String name）→ " + d.name);
        line("      int（int age）        → " + d.age);
        line("      boolean（flag）       → " + d.flag);
        line("      char（ch）            → " + (int) d.ch + "（即 '\\u0000'）");
        line("    局部变量：没有默认值，必须赋过值才能用");
        line("");

        line("本专题要点：");
        line("  · 「Student s2 = new ...」= 声明并初始化（一句搞定，首选）");
        line("  · 「s1 = new ...」= 赋值（s1 之前已声明过）");
        line("  · 运行时行为完全一样，区别只在编译期的「先声明后使用」");
        line("  · 同作用域不能重复声明；局部变量必须先赋值才能用");
        line("  · 成员变量有默认值（null/0/false），局部变量没有");
    }

    /** 演示成员变量的默认值 */
    static class Defaults {
        String name;      // 默认 null
        int age;          // 默认 0
        boolean flag;     // 默认 false
        char ch;          // 默认 '\u0000'
    }

    static void line(String s) {
        System.out.println(s);
    }
}
