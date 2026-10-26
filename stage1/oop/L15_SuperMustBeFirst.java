package oop;

/**
 * ════════════════════════════════════════════════════════════
 *  专题 · 为什么 super(...) 必须是子类构造方法的第一句？
 * ════════════════════════════════════════════════════════════
 *
 *  【你问的代码】
 *      public Student(String name, int age, String studentId, String major) {
 *          // ① super(...) 必须是子类构造方法的第一句，调用父类构造
 *          super(name, age);
 *          ...
 *      }
 *
 *  【两层原因】
 *   第一层（规则层面）：Java 的规定
 *     如果不显式写 super(...)，编译器会【自动插入】一句 super() 到构造方法【最开头】。
 *     你要是先写了别的语句，编译器插入的 super() 就跑到它后面了 → 编译报错：
 *         call to super must be first statement in constructor
 *
 *   第二层（语义层面）：对象的构造是【自顶向下】的
 *     父类的状态必须先建立好，子类才能在它基础上扩展。
 *     如果父类构造方法里调用了被子类重写的方法（模板方法模式），
 *     而父类构造还没执行 → 父类看到的子类字段还是【未初始化的默认值】。
 *     这就是"super 必须第一句"的真正原因。
 *
 *  【运行】右键本文件 → Run
 */
public class L15_SuperMustBeFirst {

    // ══════════════════════════════════════════════
    // 演示初始化顺序
    // ══════════════════════════════════════════════
    static class Parent {
        static String staticField = "父类静态字段";
        String instanceField = "父类实例字段";

        static {
            System.out.println("  ① 父类静态代码块（类加载时，只执行一次）");
        }

        {
            System.out.println("  ④ 父类实例代码块（每个对象都执行）");
        }

        Parent() {
            System.out.println("  ⑤ 父类构造方法体");
        }
    }

    static class Child extends Parent {
        static String staticField = "子类静态字段";
        String instanceField = "子类实例字段";

        static {
            System.out.println("  ② 子类静态代码块");
        }

        {
            System.out.println("  ⑥ 子类实例代码块");
        }

        Child() {
            super();                                 // ← 必须第一句
            System.out.println("  ⑦ 子类构造方法体");
        }
    }

    // ══════════════════════════════════════════════
    // 演示"父类构造里调子类重写方法"的坑
    // ══════════════════════════════════════════════
    static class RiskyBase {
        int value;

        RiskyBase() {
            System.out.println("      RiskyBase 构造中，调用 init()…");
            init();                                   // ← 调用的是【子类重写后的】版本
            System.out.println("      RiskyBase 构造结束，此时 value = " + value);
        }

        void init() {
            System.out.println("      RiskyBase.init()：value = " + value);
        }
    }

    static class RiskyChild extends RiskyBase {
        int value = 99;                               // 实例字段初始化

        RiskyChild() {
            super();                                  // ← 这一句执行完，父类构造才跑完
        }

        @Override
        void init() {
            value = 88;                               // 子类重写：给 value 赋值
            System.out.println("      RiskyChild.init()：把 value 设成 88");
        }
    }

    // ══════════════════════════════════════════════
    // 演示"字段初始化表达式依赖后面才声明的字段"
    // ══════════════════════════════════════════════
    static class OrderBase {
        OrderBase() {
            System.out.println("      OrderBase 构造执行");
        }
    }

    /**
     * 字段初始化器按【声明顺序】执行。
     * a 的初始化器里用到了 b，而 b 声明在 a 后面 → 执行 a 时 b 还是默认值 0。
     */
    static class OrderChild extends OrderBase {
        int a = compute();     // ← 声明在前
        int b = 10;           // ← 声明在后，执行 a 时它还没被赋值

        OrderChild() {
            super();
        }

        int compute() {
            return b * 2;      // ← 此刻 b == 0，返回 0
        }
    }

    public static void main(String[] args) {
        line("专题 · 为什么 super(...) 必须是第一句？");
        line("");

        // ══════════════════════════════════════════════
        // ① 第一层：编译规则
        // ══════════════════════════════════════════════
        line("① 第一层（规则层面）：Java 的规定");
        line("    如果不显式写 super(...)，编译器会【自动插入】一句 super() 到最开头。");
        line("    所以你写：");
        line("        Child() {");
        line("            System.out.println(\"子类里的代码\");   // ← 写在了前面");
        line("            super();                                 // ← 报错");
        line("        }");
        line("    编译器报错：");
        line("        call to super must be first statement in constructor");
        line("    原因：编译器自动插入的 super() 会跑到最前面，");
        line("          你的 super() 变成了【第二句】→ 重复调用父类构造 → 非法。");
        line("    （本文件的 L15_SuperErrorDemo.java 演示这个错误，运行时复现）");
        line("");

        // ══════════════════════════════════════════════
        // ② 第二层：完整初始化顺序
        // ══════════════════════════════════════════════
        line("② 第二层（语义层面）：完整的初始化顺序");
        line("    new Child() 时，JVM 按这个顺序执行：");
        line("      0) Parent 静态字段初始化");
        line("      1) Parent 静态代码块");
        line("      2) Child  静态字段初始化");
        line("      3) Child  静态代码块");
        line("      4) Parent 实例字段初始化 + 实例代码块");
        line("      5) Parent 构造方法体      ← super() 在这里执行");
        line("      6) Child  实例字段初始化 + 实例代码块");
        line("      7) Child  构造方法体       ← super() 之后的代码");
        line("");
        line("    实测输出：");
        new Child();
        line("");

        // ══════════════════════════════════════════════
        // ③ 父类构造里调子类方法 → 看到未初始化的字段
        // ══════════════════════════════════════════════
        line("③ 核心坑：父类构造里调子类重写的方法 → 看到的是未初始化的默认值");
        System.out.println("    new RiskyChild()：");
        new RiskyChild();
        line("    → 父类构造时，子类的实例字段【还没初始化】");
        line("    → 所以父类看到 value = 0（默认值），而不是 88");
        line("    → 这就是为什么 super() 必须是第一句：");
        line("      父类必须先把自己的状态建好，子类才有可靠的起点去扩展");
        line("");

        // ══════════════════════════════════════════════
        // ④ 字段初始化表达式的顺序陷阱
        // ══════════════════════════════════════════════
        line("④ 字段初始化表达式的顺序陷阱（另一个真实坑）");
        OrderChild oc = new OrderChild();
        line("    OrderChild 实例：");
        line("      a = " + oc.a + "   （期望 20，实际是 0）");
        line("      b = " + oc.b + "   （= 10，正常）");
        line("    → 实例字段初始化器按【声明顺序】执行：");
        line("      先执行 a = compute()，此时 b 还没被赋值（仍是默认值 0）");
        line("      → 所以 compute() 里 b * 2 = 0");
        line("    → 教训：字段初始化表达式不要依赖【后面才声明】的字段");
        line("    → 同理，父类构造方法里不要调用可被重写的方法");
        line("");

        line("本专题要点：");
        line("  · 第一层：Java 规定 super(...) 必须是第一句，否则编译报错");
        line("    call to super must be first statement in constructor");
        line("  · 第二层（真正的理由）：对象自顶向下构造，父类状态必须先建立好");
        line("  · 完整初始化顺序：父类静态 → 子类静态 → 父类实例 → 父类构造 → 子类实例 → 子类构造");
        line("  · 父类构造方法里【不要】调用可被重写的方法，否则会看到未初始化的字段");
        line("  · 字段初始化表达式不要依赖其他字段（顺序不保证）");
        line("  · 关联：L8_ConstructorErrorDemo 讲的 super() 隐式调用");
    }

    static void line(String s) {
        System.out.println(s);
    }
}
