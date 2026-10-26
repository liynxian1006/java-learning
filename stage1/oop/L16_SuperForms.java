package oop;

/**
 * ════════════════════════════════════════════════════════════
 *  专题 · super(...) 是固定写法吗？（三种形态 + 与 this 的关系）
 * ════════════════════════════════════════════════════════════
 *
 *  【你问的代码】
 *      public Student(String name, int age, String studentId, String major) {
 *          super(name, age);          // ← 这一行是固定的吗？
 *          ...
 *      }
 *
 *  【答案：不固定】
 *   super(...) 有三种形态，父类有几个构造方法，子类就能挑其中任意一个：
 *      ① 隐式：完全不写，编译器自动补 super()
 *      ② 显式无参：super();
 *      ③ 显式带参：super(参数)   ← 参数个数/类型由父类的构造方法决定
 *
 *  【什么时候【必须】写】
 *   父类没有无参构造时 → 编译器补的 super() 找不到匹配 → 编译报错
 *   → 这时子类必须显式写 super(父类有的那个构造的参数)
 *
 *  【super 和 this 互斥】
 *   this(...)  委托给【本类的另一个构造方法】
 *   super(...) 调用【父类的构造方法】
 *   两者都必须是构造方法的第一句，所以【只能二选一】
 *
 *  【运行】右键本文件 → Run
 */
public class L16_SuperForms {

    // ══════════════════════════════════════════════
    // 父类：有三个构造方法（重载）
    // ══════════════════════════════════════════════
    static class Person {
        private String name;
        private int age;

        /** 形态②的目标：父类的无参构造 */
        public Person() {
            this.name = "未命名";
            this.age = 0;
            System.out.println("      → Person() 无参构造");
        }

        /** 形态③的目标：父类的带参构造 */
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
            System.out.println("      → Person(String, int) 构造");
        }

        /** 父类的第三个构造：子类也能选它 */
        public Person(String name) {
            this(name, 0);
            System.out.println("      → Person(String) 构造");
        }

        public String getName() {
            return name;
        }
    }

    // ══════════════════════════════════════════════
    // 子类 A：形态① 隐式（不写 super）
    // ══════════════════════════════════════════════
    static class ImplicitChild extends Person {
        ImplicitChild() {
            // 什么都没写 → 编译器自动在第一句插入 super()
            System.out.println("      → ImplicitChild 构造（隐式 super()）");
        }
    }

    // ══════════════════════════════════════════════
    // 子类 B：形态② 显式 super()
    // ══════════════════════════════════════════════
    static class ExplicitNoArgChild extends Person {
        ExplicitNoArgChild() {
            super();                               // 显式无参
            System.out.println("      → ExplicitNoArgChild 构造");
        }
    }

    // ══════════════════════════════════════════════
    // 子类 C：形态③ 显式 super(参数)
    // ══════════════════════════════════════════════
    static class ExplicitArgsChild extends Person {
        private String studentId;

        ExplicitArgsChild(String name, int age, String studentId) {
            super(name, age);                     // 显式带参
            this.studentId = studentId;
            System.out.println("      → ExplicitArgsChild 构造");
        }

        public String getStudentId() {
            return studentId;
        }
    }

    // ══════════════════════════════════════════════
    // 子类 D：父类有多个构造时，子类可以「换着用」
    // ══════════════════════════════════════════════
    static class FlexibleChild extends Person {
        FlexibleChild() {
            super("默认姓名", 18);                // 选 Person(String,int)
            System.out.println("      → FlexibleChild() 用了父类双参构造");
        }

        FlexibleChild(String name) {
            super(name);                          // 选 Person(String)
            System.out.println("      → FlexibleChild(String) 用了父类单参构造");
        }

        FlexibleChild(String name, int age) {
            super(name, age);                     // 选 Person(String,int)
            System.out.println("      → FlexibleChild(String,int) 用了父类双参构造");
        }
    }

    // ══════════════════════════════════════════════
    // 父类只有一个有参构造 → 子类【必须】显式 super(参数)
    // ══════════════════════════════════════════════
    static class OnlyArgsParent {
        OnlyArgsParent(String required) {
            System.out.println("      → OnlyArgsParent(String) 构造，参数=" + required);
        }
    }

    static class MustCallSuper extends OnlyArgsParent {
        MustCallSuper(String required) {
            super(required);                      // ← 必须写，父类没有无参构造
            System.out.println("      → MustCallSuper 构造");
        }
    }

    // ══════════════════════════════════════════════
    // this(...) 委托构造：让无参构造复用有参构造的逻辑
    // ══════════════════════════════════════════════
    static class DelegatingChild extends Person {
        private String studentId;

        DelegatingChild() {
            this("默认学号", "默认姓名", 18);      // ← 委托给另一个构造
            System.out.println("      → DelegatingChild() 委托构造");
        }

        DelegatingChild(String studentId, String name, int age) {
            super(name, age);                     // 真正干活的构造
            this.studentId = studentId;
            System.out.println("      → DelegatingChild(String,String,int) 实际构造");
        }

        public String getStudentId() {
            return studentId;
        }
    }

    public static void main(String[] args) {
        line("专题 · super(...) 是固定写法吗？");
        line("");

        // ══════════════════════════════════════════════
        // ① 三种形态
        // ══════════════════════════════════════════════
        line("① super(...) 的三种形态");
        System.out.println("    形态① 隐式（子类不写 super）：");
        new ImplicitChild();
        System.out.println("    形态② 显式 super()：");
        new ExplicitNoArgChild();
        System.out.println("    形态③ 显式 super(参数)：");
        new ExplicitArgsChild("张三", 21, "20230101");
        line("    → 三种都对，取决于父类有哪些构造方法");
        line("");

        // ══════════════════════════════════════════════
        // ② 父类有多个构造，子类可以换着用
        // ══════════════════════════════════════════════
        line("② 父类有多个构造方法时，子类可以「换着用」");
        new FlexibleChild();
        new FlexibleChild("李四");
        new FlexibleChild("王五", 22);
        line("    → 子类的每个构造方法，都可以挑父类的任意一个构造来调");
        line("    → 常见于「便捷构造」：一个全参 + 若干个简化版");
        line("");

        // ══════════════════════════════════════════════
        // ③ 父类只有有参构造 → 子类必须显式 super(参数)
        // ══════════════════════════════════════════════
        line("③ 父类【没有】无参构造时，子类必须显式写 super(参数)");
        new MustCallSuper("必须传的东西");
        line("    → 如果这里不写 super(...)，编译器补的 super() 找不到匹配 → 编译报错");
        line("");

        // ══════════════════════════════════════════════
        // ④ this(...) 委托构造
        // ══════════════════════════════════════════════
        line("④ this(...)：委托给本类的另一个构造方法");
        DelegatingChild dc = new DelegatingChild();
        line("    → 无参构造里写 this(...) 委托给全参构造，逻辑只写一份");
        line("    → studentId = " + dc.getStudentId());
        line("    → 好处：避免构造方法之间重复代码（也避免调用本类其它方法时字段未初始化的坑）");
        line("");

        // ══════════════════════════════════════════════
        // ⑤ super 与 this 互斥
        // ══════════════════════════════════════════════
        line("⑤ super(...) 与 this(...) 互斥（只能二选一）");
        line("    this(...)  → 委托给【本类】的另一个构造方法");
        line("    super(...) → 调用【父类】的构造方法");
        line("    两者都必须是构造方法的第一句 → 一句话只能有一个 → 互斥");
        line("    ✘ 编译报错：cannot both be invoked in the same constructor");
        line("    → 但可以这样绕：this(...) 委托的那个构造里再写 super(...)");
        line("       （见 ④ 的 DelegatingChild）");
        line("");

        // ══════════════════════════════════════════════
        // ⑥ super 不能用在静态上下文
        // ══════════════════════════════════════════════
        line("⑥ super 不能用在静态方法里");
        line("    static void bad() { super.toString(); }   → 编译报错：");
        line("        non-static method cannot be referenced from a static context");
        line("    → super 只能用在【实例方法】和【构造方法】里");
        line("    → super.XXX  访问父类的成员；super(...)  调用父类构造");
        line("");

        line("本专题要点：");
        line("  · super(...) 不固定：隐式 / super() / super(参数) 三种形态");
        line("  · 父类有几个构造方法（重载），子类就能挑任意一个来调");
        line("  · 父类没有无参构造时，子类必须显式 super(参数)，否则编译报错");
        line("  · this(...) 与 super(...) 互斥，但可通过「this 委托 → 那个构造里写 super」绕开");
        line("  · this(...) 委托构造：避免重复代码，也是避免「构造中调用可重写方法」的安全写法");
        line("  · super 不能在静态上下文使用");
    }

    static void line(String s) {
        System.out.println(s);
    }
}
