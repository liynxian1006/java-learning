package oop;

/**
 * ════════════════════════════════════════════════════════════
 *  第 3 关 · 继承（Inheritance）
 * ════════════════════════════════════════════════════════════
 *
 *  【核心概念】
 *   子类 extends 父类，继承父类的属性和方法，可以再扩展/修改。
 *
 *  【真实业务】
 *   父类 Person（人）→ 子类 Student（学生）、Teacher（老师）
 *   学生、老师都是"人"，有共同的属性（姓名、年龄）和行为（自我介绍），
 *   但各自有专属属性（学生有学号，老师有职称）。
 *
 *  【三个必会】
 *   1. extends 继承
 *   2. super(...) 调用父类构造方法 / super.xxx 访问父类成员
 *   3. @Override 方法重写
 *
 *  【运行】右键本文件 → Run
 */
public class L3_Inheritance {

    /** 父类：人 */
    static class Person {
        // 字段用 private：只有 Person 自己能直接访问
        // 子类要读数据，走下面 protected 的 getter（不给外部）
        private String name;
        private int age;

        public Person(String name, int age) {
            System.out.println("      → Person 构造方法执行（父类构造先于子类）");
            this.name = name;
            this.age = age;
        }

        public void introduce() {
            System.out.println("      我叫" + name + "，" + age + "岁");
        }

        /** 说话：子类可以重写它 */
        public void speak() {
            System.out.println("      " + name + " 说：大家好");
        }

        public String getInfo() {
            return name + "(" + age + "岁)";
        }

        /** protected 方法：子类可以调，外部类调不了 —— protected 的正确用法 */
        protected String getName() {
            return name;
        }

        protected int getAge() {
            return age;
        }
    }

    /** 子类：学生 */
    static class Student extends Person {
        // 子类自己的字段用 private：外部只能通过下面的 getter 访问
        private String studentId;    // 子类专属属性
        private String major;

        public Student(String name, int age, String studentId, String major) {
            // ① super(...) 必须是子类构造方法的第一句，调用父类构造
            super(name, age);
            System.out.println("      → Student 构造方法执行（父类构造已先完成）");
            this.studentId = studentId;
            this.major = major;
        }

        // ② @Override：重写父类的 speak()
        @Override
        public void speak() {
            // 父类字段是 private → 子类要读必须调父类的 protected getter
            System.out.println("      " + getName() + " 说：我是" + major + "专业的学生");
        }

        // ③ 调父类的方法：super.方法()
        public void introduceWithMajor() {
            introduce();                       // 继承来的，直接调
            System.out.println("      学号：" + studentId + "，专业：" + major);
        }

        public String getStudentId() {
            return studentId;
        }
    }

    /** 子类：老师 */
    static class Teacher extends Person {
        private String title;       // 职称

        public Teacher(String name, int age, String title) {
            super(name, age);
            this.title = title;
        }

        @Override
        public void speak() {
            System.out.println("      " + getName() + " 说：同学们好，我是" + title);
        }
    }

    public static void main(String[] args) {
        line("第 3 关 · 继承");
        line("");

        // ────────────────────────────────────────
        // ① 构造顺序：父类先，子类后
        // ────────────────────────────────────────
        line("① 构造顺序：先父类，再子类");
        new Student("张三", 21, "20230101", "计算机");
        line("");

        // ────────────────────────────────────────
        // ② 继承来的属性和方法直接能用
        // ────────────────────────────────────────
        line("② 继承：属性和方法都能直接用");
        Student s = new Student("李四", 20, "20230102", "软件工程");
        s.introduceWithMajor();
        line("");

        // ────────────────────────────────────────
        // ③ @Override 方法重写
        // ────────────────────────────────────────
        line("③ 方法重写：子类改写父类的行为");
        s.speak();
        new Teacher("王老师", 45, "副教授").speak();
        line("    → 同一个 speak() 方法，不同对象表现不同");
        line("");

        // ────────────────────────────────────────
        // ④ 父类引用可以指向子类对象
        // ────────────────────────────────────────
        line("④ 父类类型引用，可以指向子类对象（向上转型）");
        Person p = s;                       // 编译通过
        line("    p.getClass() = " + p.getClass().getSimpleName());
        line("    → 变量声明的类型是 Person，实际对象是 Student");
        line("    → 调 speak() 会执行 Student 的版本（多态，下一关详讲）");
        p.speak();
        line("");

        // ────────────────────────────────────────
        // ⑤ 方法重写 vs 方法重载（高频面试题）
        // ────────────────────────────────────────
        line("⑤ 重写(Override) vs 重载(Overload) —— 必须分清");
        line("    重写：子类改父类已有的方法");
        line("         · 相同方法名、相同参数列表");
        line("         · 只能发生在父子类之间");
        line("         · 返回值类型必须相同（或协变）");
        line("    重载：同一个类里多个同名但参数不同的方法");
        line("         · 方法名相同、参数列表不同");
        line("         · 与继承无关");
        line("    下面这个叫重载（add 参数个数不同）：");
        System.out.println("      add(1,2)      = " + add(1, 2));
        System.out.println("      add(1,2,3)    = " + add(1, 2, 3));
        System.out.println("      add(1.5,2.5)  = " + add(1.5, 2.5));
        line("");

        // ────────────────────────────────────────
        // ⑥ super 的两个用法
        // ────────────────────────────────────────
        line("⑥ super 的两个用法");
        line("    super(name, age)   → 调用父类构造方法（必须在构造方法第一句）");
        line("    super.speak()      → 调用父类版本的方法");
        line("    → 忘记写 super(...) 时，父类会走无参构造（所以父类最好保留无参构造）");
        line("");

        // ────────────────────────────────────────
        // ⑦ instanceof：判断对象真实类型
        // ────────────────────────────────────────
        line("⑦ instanceof：判断某个对象到底是什么类型");
        Person p2 = new Teacher("赵老师", 38, "讲师");
        if (p2 instanceof Student) {
            System.out.println("      是学生：" + ((Student) p2).getStudentId());
        } else if (p2 instanceof Teacher) {
            System.out.println("      是老师，类型为 Teacher");
        }
        line("");

        line("本关要点：");
        line("  · extends 继承，super(...) 调父类构造（必须第一句）");
        line("  · @Override 表示重写父类方法");
        line("  · 重写 = 改父类已有的；重载 = 同类多个同名不同参");
        line("  · protected：子类可访问，外部不可访问");
        line("  · instanceof + 强制转换 = 判断真实类型后再取子类特有属性");
    }

    // 重载演示：三个同名不同参的方法
    static int add(int a, int b) {
        return a + b;
    }

    static int add(int a, int b, int c) {
        return a + b + c;
    }

    static double add(double a, double b) {
        return a + b;
    }

    static void line(String s) {
        System.out.println(s);
    }
}
