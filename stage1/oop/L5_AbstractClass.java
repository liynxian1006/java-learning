package oop;

/**
 * ════════════════════════════════════════════════════════════
 *  第 5 关 · 抽象类（abstract class）
 * ════════════════════════════════════════════════════════════
 *
 *  【核心概念】
 *   不能被 new 出来实例化的类，只能被继承。
 *   抽象类可以同时拥有：抽象方法（必须子类实现）+ 具体方法（子类直接继承）。
 *
 *  【真实业务】
 *   岗位 Job 是抽象类：所有岗位都有「薪资计算」但算法不同，
 *   而「显示岗位信息」是所有岗位共通的，由父类统一实现。
 *
 *  【和接口的区别（面试高频）】
 *   抽象类：可以有构造方法、可以有成员变量、可以有具体方法、单继承
 *   接口：不能有构造方法、成员变量默认 public static final、方法默认 public abstract、多实现
 *
 *  【运行】右键本文件 → Run
 */
public class L5_AbstractClass {

    /** 抽象类：岗位 */
    static abstract class Job {
        protected String title;          // 具体成员变量
        protected double baseSalary;

        protected Job(String title, double baseSalary) {
            this.title = title;
            this.baseSalary = baseSalary;
        }

        /** 抽象方法：没有方法体，子类必须实现 */
        public abstract double calcSalary();

        /** 具体方法：所有子类共通，父类统一实现 */
        public void display() {
            System.out.println("      岗位：" + title
                    + "，最终薪资 " + calcSalary() + " 元");
            //            ↑ 这里调用抽象方法，实际执行的是子类版本（多态）
        }

        public String getTitle() {
            return title;
        }
    }

    /** 实习岗位：算法 = 基础薪资 × 0.6 */
    static class InternJob extends Job {
        private int months;

        public InternJob(String title, double baseSalary, int months) {
            super(title, baseSalary);
            this.months = months;
        }

        @Override
        public double calcSalary() {
            return baseSalary * 0.6;
        }
    }

    /** 全职岗位：算法 = 基础薪资 + 绩效 */
    static class FullTimeJob extends Job {
        private double performance;

        public FullTimeJob(String title, double baseSalary, double performance) {
            super(title, baseSalary);
            this.performance = performance;
        }

        @Override
        public double calcSalary() {
            return baseSalary + performance;
        }
    }

    public static void main(String[] args) {
        line("第 5 关 · 抽象类");
        line("");

        // ────────────────────────────────────────
        // ① 抽象类不能被 new
        // ────────────────────────────────────────
        line("① 抽象类不能实例化");
        line("    new Job(\"普通岗位\", 10000)  →  编译报错：");
        line("        Job is abstract; cannot be instantiated");
        line("    → 因为抽象方法没有实现，造出对象也不知道该用哪个算法");
        line("");

        // ────────────────────────────────────────
        // ② 抽象类的价值：统一处理
        // ────────────────────────────────────────
        line("② 抽象类的价值：统一处理不同岗位");
        Job j1 = new InternJob("Java实习生", 8000, 3);
        Job j2 = new FullTimeJob("Java工程师", 20000, 5000);
        j1.display();
        j2.display();
        line("    → 父类的 display() 统一处理，calcSalary() 各不相同（多态）");
        line("");

        // ────────────────────────────────────────
        // ③ 抽象类可以有构造方法
        // ────────────────────────────────────────
        line("③ 抽象类可以有构造方法（供子类 super 调用）");
        line("    Job 的构造方法虽然不能直接 new，但子类 extends 时会通过 super(...) 调用它");
        line("    → 父类的初始化逻辑照样能执行");
        line("");

        // ────────────────────────────────────────
        // ④ 抽象类可以有具体方法
        // ────────────────────────────────────────
        line("④ 抽象类可以混合：抽象方法 + 具体方法");
        line("    抽象方法 calcSalary() → 子类各自实现（强制子类补齐差异）");
        line("    具体方法 display()     → 父类统一实现（避免子类重复写）");
        line("    → 这是抽象类的核心价值：把「不变的」放父类，「变化的」留给子类");
        line("");

        // ────────────────────────────────────────
        // ⑤ 抽象方法不能是 private / static / final
        // ────────────────────────────────────────
        line("⑤ 抽象方法的三条限制（面试常考）");
        line("    · 不能是 private    → 否则子类无法重写");
        line("    · 不能是 static     → 静态方法不参与多态");
        line("    · 不能是 final      → 否则子类无法重写");
        line("");

        line("本关要点：");
        line("  · abstract 类不能 new，只能继承");
        line("  · 抽象方法无方法体，子类必须实现");
        line("  · 抽象类可以有构造方法 + 具体方法 + 成员变量");
        line("  · 价值：不变的放父类（复用），变化的留子类（多态）");
        line("  · 项目映射：项目三的 Service 基类、MyBatis 的 Mapper 接口都用了这个思想");
    }

    static void line(String s) {
        System.out.println(s);
    }
}
