package oop;

/**
 * ════════════════════════════════════════════════════════════
 *  专题 · 继承的完整初始化顺序（精确版 + 字段遮蔽实测）
 * ════════════════════════════════════════════════════════════
 *
 *  配套 L15_SuperMustBeFirst（为什么 super 必须是第一句）和 L16_SuperForms（super 三形态）。
 *  本专题把两件事讲【精确】：
 *      ① 完整初始化顺序到底 10 步还是 7 步（分清静态字段 / 实例字段）
 *      ② 父类构造里调子类重写方法，父类看到的值为什么不对
 *         —— 不只是"子类字段没初始化"，还叠加了【字段遮蔽】和【后续覆盖】
 *
 *  【运行】
 *      cd java-learning/stage1
 *      javac -encoding UTF-8 -d out oop/L24_InitializationOrder.java
 *      java -Dfile.encoding=UTF-8 -cp out oop.L24_InitializationOrder
 */
public class L24_InitializationOrder {

    private static int step = 0;

    private static void log(String s) {
        System.out.println("  [" + (++step) + "] " + s);
    }

    private static void reset() {
        step = 0;
    }

    // ══════════════════════════════════════════════
    // ① 完整初始化顺序（10 步）
    // ══════════════════════════════════════════════
    static class Parent {
        static String pStatic = initPStatic();
        String pInstance = initPInstance();

        static {
            System.out.println("  [1] 父类静态代码块");
        }

        {
            System.out.println("  [4] 父类实例代码块");
        }

        Parent() {
            System.out.println("  [5] 父类构造方法体   ← super() 就在这里执行");
        }

        static String initPStatic() {
            System.out.println("  [0] 父类静态字段初始化");
            return "父类静态字段";
        }

        String initPInstance() {
            System.out.println("  [3] 父类实例字段初始化");
            return "父类实例字段";
        }
    }

    static class Child extends Parent {
        static String cStatic = initCStatic();
        String cInstance = initCInstance();

        static {
            System.out.println("  [2] 子类静态代码块");
        }

        {
            System.out.println("  [7] 子类实例代码块");
        }

        Child() {
            super();
            System.out.println("  [8] 子类构造方法体");
        }

        static String initCStatic() {
            System.out.println("  [2] 子类静态字段初始化（与静态代码块按声明顺序）");
            return "子类静态字段";
        }

        String initCInstance() {
            System.out.println("  [6] 子类实例字段初始化");
            return "子类实例字段";
        }
    }

    // ══════════════════════════════════════════════
    // ② 字段遮蔽：父类的 value 和子类的 value 是两个不同的变量
    // ══════════════════════════════════════════════
    static class Base {
        int value = 1;                   // 父类自己的字段

        Base() {
            System.out.println("      Base 构造中，调用 init()…");
            init();                      // 实际执行的是【子类重写后的】版本
            System.out.println("      Base 构造结束，this.value = " + value + "（父类字段）");
        }

        void init() {
            System.out.println("      Base.init()");
        }
    }

    /** A：子类用 this.value = 88 → 写的是子类字段，父类看不见 */
    static class SubThis extends Base {
        int value = 99;                  // 子类自己的字段（遮蔽父类的 value）

        @Override
        void init() {
            value = 88;                  // ← this.value = 子类的字段
            System.out.println("      SubThis.init(): this.value = 88（写进子类字段）");
        }
    }

    /** B：子类用 super.value = 77 → 写的是父类字段，父类能看见 */
    static class SubSuper extends Base {
        int value = 99;

        @Override
        void init() {
            super.value = 77;            // ← 写的是父类字段
            System.out.println("      SubSuper.init(): super.value = 77（写进父类字段）");
        }
    }

    // ══════════════════════════════════════════════
    // ③ 模板方法的安全写法
    // ══════════════════════════════════════════════
    static class SafeBase {
        int value;

        SafeBase() {
            System.out.println("      SafeBase 构造：只做父类自己的事，不调 init()");
        }

        /** 留给子类重写的钩子方法 */
        void init() {
            value = 1;
        }
    }

    static class SafeChild extends SafeBase {
        int extra;

        SafeChild() {
            super();                      // 先让父类构造完
            init();                      // 再由【子类自己】调用重写后的 init()
            extra = value * 2;           // 此时 value 已经是 10
            System.out.println("      SafeChild 构造完成：value = " + value + "，extra = " + extra);
        }

        @Override
        void init() {
            value = 10;
            System.out.println("      SafeChild.init(): value = 10");
        }
    }

    // ══════════════════════════════════════════════
    // ④ 字段初始化表达式的顺序依赖
    // ══════════════════════════════════════════════
    static class OrderChild {
        int a = compute();               // 声明在前：此刻 b 还是 0
        int b = 10;                      // 声明在后

        int compute() {
            return b * 2;
        }
    }

    static class FixedOrder {
        int a;                           // 声明在前，但不在初始化器里赋值
        int b = 10;

        FixedOrder() {
            a = compute();               // 放进构造方法：此刻 b 已经是 10
        }

        int compute() {
            return b * 2;
        }
    }

    // ══════════════════════════════════════════════
    // ⑤ super 与 this 互斥 → 用委托构造绕开
    // ══════════════════════════════════════════════
    static class OnlyArgsParent {
        final String tag;

        OnlyArgsParent(String tag) {
            this.tag = tag;
            System.out.println("      OnlyArgsParent 构造，tag = " + tag);
        }
    }

    static class DelegatingChild extends OnlyArgsParent {
        private final int score;

        DelegatingChild() {
            this("默认", 0);              // ← 委托给本类另一个构造
        }

        DelegatingChild(String tag, int score) {
            super(tag);                  // ← 真正干活的那个构造里写 super
            this.score = score;
            System.out.println("      DelegatingChild 委托构造完成，score = " + score);
        }
    }

    public static void main(String[] args) {
        // ══════════════════════════════════════════════
        // ① 完整初始化顺序
        // ══════════════════════════════════════════════
        line("① 完整初始化顺序（new Child() 时）");
        line("────────────────────────────────────────");
        line("  阶段一：类加载（只发生一次，与 new 几次无关）");
        line("    父类静态字段 → 父类静态代码块 → 子类静态字段 → 子类静态代码块");
        line("  阶段二：每次 new 都会走一遍");
        line("    父类实例字段 → 父类实例代码块 → 父类构造体（super 执行点）");
        line("    → 子类实例字段 → 子类实例代码块 → 子类构造体");
        line("");
        line("  实测：");
        reset();
        new Child();
        line("");
        line("  → 关键：父类的【实例字段】在父类构造体【之前】就初始化完了，");
        line("    所以父类构造体里可以放心用父类自己的字段；");
        line("    但此时【子类的实例字段还没初始化】，子类字段一律是默认值。");
        line("");

        // ══════════════════════════════════════════════
        // ② 字段遮蔽实测
        // ══════════════════════════════════════════════
        line("② 父类构造里调子类重写方法 —— 精确原因（不是单一的\"没初始化\"）");
        line("────────────────────────────────────────");
        line("  A. 子类写 this.value = 88（写子类自己的字段）");
        SubThis a = new SubThis();
        line("    最终 a.value = " + a.value);
        line("    → 88 被【后续的实例字段初始化 value = 99 覆盖】了");
        line("    → 父类构造结束时父类 value = 1（父类字段，压根没被子类碰过）");
        line("    → 结论：父类的 value 和子类的 value 是【两个不同的变量】，互不可见");
        line("");
        line("  B. 子类写 super.value = 77（写父类的字段）");
        SubSuper b = new SubSuper();
        line("    最终 b.value = " + b.value + "（子类字段，被 99 覆盖）");
        line("    → 父类构造结束时父类 value = 77（父类确实被改了）");
        line("    → 结论：父类构造【确实调用了】子类的重写方法，问题是时机太早");
        line("");
        line("  ★ 所以这个坑的正确说法是：");
        line("    ① 字段遮蔽：同名字段是父子【各自独立】的变量（subtype shadowing）");
        line("    ② 时机太早：父类构造时，子类的实例字段【一个都还没初始化】");
        line("    ③ 后续覆盖：子类实例字段初始化会在父类构造【之后】执行，");
        line("                把子类重写方法里改过的值再次覆盖掉");
        line("");

        // ══════════════════════════════════════════════
        // ③ 安全写法
        // ══════════════════════════════════════════════
        line("③ 安全写法：钩子方法由【子类自己】在 super() 之后调用");
        line("────────────────────────────────────────");
        new SafeChild();
        line("    → value = 10，extra = 20，顺序完全可控");
        line("    → 模板方法模式的标准姿势：");
        line("        父类构造只负责父类的初始化；");
        line("        父类提供一个可重写的 init()；");
        line("        子类构造里 super() 之后自己调 init()");
        line("");

        // ══════════════════════════════════════════════
        // ④ 字段初始化表达式的顺序依赖
        // ══════════════════════════════════════════════
        line("④ 字段初始化表达式依赖了【后面才声明】的字段");
        line("────────────────────────────────────────");
        OrderChild oc = new OrderChild();
        line("    OrderChild（初始化器里算 a）：");
        line("      a = " + oc.a + "   ← 期望 20，实际 0");
        line("      b = " + oc.b);
        line("    → 实例字段初始化器严格按【声明顺序】执行，a 先跑，此时 b 还是默认值 0");
        line("");
        FixedOrder fo = new FixedOrder();
        line("    FixedOrder（把赋值挪进构造方法）：");
        line("      a = " + fo.a + "   ← 正确得到 20");
        line("      b = " + fo.b);
        line("    → 构造方法体在【所有】实例字段初始化之后才执行，所以 b 已经是 10");
        line("    → 经验：字段之间有依赖时，把赋值放进构造方法，别放初始化器");
        line("");

        // ══════════════════════════════════════════════
        // ⑤ super / this 互斥
        // ══════════════════════════════════════════════
        line("⑤ super(...) 与 this(...) 为什么互斥");
        line("────────────────────────────────────────");
        line("    this(...)  → 委托给【本类】的另一个构造方法");
        line("    super(...) → 调用【父类】的构造方法");
        line("    两者都必须是构造方法的第一条语句 → 一句话只能有一个 → 互斥");
        line("    ✘ 编译报错：cannot both be invoked in the same constructor");
        line("");
        line("    绕法：this(...) 委托的那个构造里再写 super(...)");
        new DelegatingChild();
        line("    → 无参构造 → this(\"默认\", 0) → 委托构造 → super(\"默认\")");
        line("");

        line("本专题要点：");
        line("  · 初始化顺序 = 静态（父→子，各一次） + 实例（父字段→父块→父构造→子字段→子块→子构造）");
        line("  · 父类构造里【不要】调可被重写的方法：字段遮蔽 + 时机太早 + 后续覆盖，三重坑");
        line("  · 安全姿势：钩子方法由子类在 super() 之后自己调");
        line("  · 字段之间有依赖时，把赋值放进构造方法，不要放实例初始化器");
        line("  · super 与 this 互斥，但可用「this 委托 → 目标构造里 super」绕开");
    }

    static void line(String s) {
        System.out.println(s);
    }
}
