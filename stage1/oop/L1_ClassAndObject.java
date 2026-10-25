package oop;

/**
 * ════════════════════════════════════════════════════════════
 *  第 1 关 · 类与对象
 * ════════════════════════════════════════════════════════════
 *
 *  【核心概念】
 *   类（Class） = 模板 / 蓝图纸："学生"这类事物长什么样
 *   对象（Object）= 用模板造出来的具体东西："张三"这个具体学生
 *
 *  【一句话】
 *   类是"一类事物的抽象描述"，对象是"符合这个描述的具体实例"。
 *
 *  【怎么区分】
 *   Student  → 类
 *   new Student("张三", 21) → 对象
 *   Student s1, s2;      → 只是声明了两个"学生位置的变量"，此刻还没造对象
 *
 *  【运行】右键本文件 → Run，看控制台输出
 */
public class L1_ClassAndObject {

    /** 一个"学生"类 = 模板 */
    static class Student {
        // 成员变量（也叫属性 / 字段）：描述这类事物有什么
        String name;
        int age;
        String major;

        // 构造方法：造对象的"流水线"
        Student(String name, int age, String major) {
            this.name = name;      // this.xxx 左边是字段，右边是参数
            this.age = age;
            this.major = major;
        }

        // 普通方法：这类事物能做什么
        void study() {
            System.out.println("    " + name + " 正在学习 " + major + " 专业");
        }

        void introduce() {
            System.out.println("    大家好，我叫" + name + "，今年" + age + "岁");
        }
    }

    public static void main(String[] args) {
        line("第 1 关 · 类与对象");
        line("");

        // ────────────────────────────────────────
        // ① 声明变量 ≠ 创建对象
        // ────────────────────────────────────────
        line("① 声明变量不等于创建对象");
        Student s1 = null;               // 只声明并置空，内存里还没有具体对象
        line("    刚声明时 s1 = " + s1);
        line("    → 变量存在，但还没有具体对象（引用是 null）");
        line("    → 若此时调用 s1.introduce() 会抛 NullPointerException");
        line("");

        // ────────────────────────────────────────
        // ② 用 new 造对象
        // ────────────────────────────────────────
        line("② 用 new 创建对象");
        s1 = new Student("张三", 21, "计算机科学与技术");
        Student s2 = new Student("李四", 20, "软件工程");
        line("    s1 = " + s1);
        line("    s2 = " + s2);
        line("    → 两个独立的对象，各有各的数据");
        line("");

        // ────────────────────────────────────────
        // ③ 对象之间互不影响
        // ────────────────────────────────────────
        line("③ 改 s1 的数据，不影响 s2");
        s1.age = 22;
        line("    s1.age = " + s1.age + "，s2.age = " + s2.age);
        line("    → 每个对象的数据是独立的（这是封装的前提）");
        line("");

        // ────────────────────────────────────────
        // ④ 对象调方法
        // ────────────────────────────────────────
        line("④ 对象调用方法");
        s1.introduce();
        s2.introduce();
        s1.study();
        line("");

        // ────────────────────────────────────────
        // ⑤ 同一类造多个对象
        // ────────────────────────────────────────
        line("⑤ 同一个类可以造任意多个对象");
        Student[] students = new Student[2];
        students[0] = new Student("王五", 19, "统计学");
        students[1] = new Student("赵六", 22, "数学");
        for (Student stu : students) {
            stu.introduce();
        }
        line("");

        // ────────────────────────────────────────
        // ⑥ 引用变量的本质
        // ────────────────────────────────────────
        line("⑥ 引用变量：变量里存的是「对象的地址」");
        Student s3 = s1;                 // 赋值的是地址，不是复制对象
        s3.age = 99;
        line("    s3 和 s1 指向同一个对象 → s1.age 也变成 " + s1.age);
        line("    → 这就是「引用传递」的直观体现");
        line("");

        line("本关要点：");
        line("  · 类 = 模板，对象 = 实例，new 造对象");
        line("  · 成员变量描述「有什么」，方法描述「能做什么」");
        line("  · 变量里存的是对象地址，赋值 = 共享同一个对象");
    }

    static void line(String s) {
        System.out.println(s);
    }
}
