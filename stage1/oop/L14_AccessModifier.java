package oop;

/**
 * ════════════════════════════════════════════════════════════
 *  专题 · 子类的字段为什么用 private？（访问控制的边界）
 * ════════════════════════════════════════════════════════════
 *
 *  【你问的代码】
 *      static class Student extends Person {
 *          private String studentId;    // 子类专属属性
 *          private String major;
 *      }
 *
 *  【一句话答案】
 *   private 是【本类内部私有】——子类不是父类的一部分，它是个独立类，
 *   所以子类也访问不到父类的 private 字段。
 *   而子类自己的字段用 private，意思是「只有 Student 自己知道这些字段怎么用」，
 *   外面的人（包括父类）想看想改，都得走 Student 提供的 getter/setter。
 *
 *  【关键认知：继承 ≠ 包含】
 *      Student is-a Person  （是一个）
 *      Student has-a Person （拥有一个）← 这个才用组合
 *      「是一个」不代表「能直接扒开对方的数据看」
 *
 *  【四个访问修饰符的可见性】
 *      修饰符      同类    同包    子类    其他包
 *      private     ✔       ✘       ✘       ✘
 *      (默认)      ✔       ✔       ✘       ✘
 *      protected   ✔       ✔       ✔       ✘
 *      public      ✔       ✔       ✔       ✔
 *
 *  【运行】右键本文件 → Run
 */
public class L14_AccessModifier {

    // ══════════════════════════════════════════════
    // 父类 A：字段用 private（推荐做法）
    // ══════════════════════════════════════════════
    static class PersonPrivate {
        private String name;          // 只有 PersonPrivate 自己能直接访问
        private int age;

        public PersonPrivate(String name, int age) {
            setName(name);
            setAge(age);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("姓名不能为空");
            }
            this.name = name.trim();
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            if (age < 0 || age > 150) {
                throw new IllegalArgumentException("年龄不合法: " + age);
            }
            this.age = age;
        }
    }

    /** 子类：只能通过父类的 getter/setter 访问父类数据 */
    static class StudentOfPrivate extends PersonPrivate {
        private String studentId;      // ← 子类自己的字段用 private
        private String major;

        public StudentOfPrivate(String name, int age, String studentId, String major) {
            super(name, age);
            this.studentId = studentId;
            this.major = major;
        }

        /** 子类想读父类的 name → 调父类给的 getter */
        public void report() {
            System.out.println("      学生 " + getName() + "（学号 " + studentId
                    + "，专业 " + major + "）");
        }

        public String getStudentId() {
            return studentId;
        }

        public String getMajor() {
            return major;
        }
    }

    // ══════════════════════════════════════════════
    // 父类 B：字段用 protected（教学常见，但有坑）
    // ══════════════════════════════════════════════
    static class PersonProtected {
        protected String name;         // 子类能直接访问
        protected int age;

        public PersonProtected(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    /** 子类可以直接改父类字段 → 绕过了父类的校验 */
    static class StudentOfProtected extends PersonProtected {
        private String studentId;

        public StudentOfProtected(String name, int age, String studentId) {
            super(name, age);
            this.studentId = studentId;
        }

        /** 子类直接改父类字段（能绕过父类校验） */
        public void cheatAge(int newAge) {
            this.age = newAge;        // ← 直接写字段，父类管不着
        }

        public int getAge() {
            return age;
        }
    }

    // ══════════════════════════════════════════════
    // 父类 C：子类字段用 protected 会怎样
    // ══════════════════════════════════════════════
    static class PersonC {
        private String name;

        public PersonC(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /** 子类的字段用 protected → 外部同包代码能直接改，封装就漏了 */
    static class StudentOfProtectedField extends PersonC {
        protected String studentId;    // ← 同包的人能直接改

        public StudentOfProtectedField(String name, String studentId) {
            super(name);
            this.studentId = studentId;
        }
    }

    /** 模拟"同包的别的类" */
    static class SneakyHelper {
        /** 直接改子类的 protected 字段——完全绕过了子类的意图 */
        void tamper(StudentOfProtectedField s) {
            s.studentId = "被偷偷改过的学号";
        }
    }

    public static void main(String[] args) {
        line("专题 · 子类的字段为什么用 private？");
        line("");

        // ══════════════════════════════════════════════
        // ① private 的真正含义
        // ══════════════════════════════════════════════
        line("① private 是「本类内部私有」，不是「子类不可见」");
        line("    准确说法：private 成员只有【 declaring class 自己】能直接访问");
        line("    · 子类访问不到父类的 private 字段");
        line("    · 父类也访问不到子类的 private 字段");
        line("    · 外部类更访问不到");
        line("    → 所以子类要用父类数据，只能走父类【提供的方法】");
        line("");

        // ══════════════════════════════════════════════
        // ② 继承 ≠ 包含（关键认知）
        // ══════════════════════════════════════════════
        line("② 关键认知：继承 ≠ 包含");
        line("    Student extends Person  的意思是「Student 是一个 Person」");
        line("    它【不】意味着「Student 可以随便扒开 Person 的私有数据」");
        line("    类与类之间只能通过【公开的方法】交互，这是 Java 的基本纪律");
        line("    （想直接持有对方对象 → 用【组合】，不是继承）");
        line("");

        // ══════════════════════════════════════════════
        // ③ 四个修饰符的可见性
        // ══════════════════════════════════════════════
        line("③ 四个访问修饰符的可见性");
        line("      修饰符        同类   同包   子类   其他包");
        line("      private       ✔      ✘      ✘      ✘");
        line("      (默认/包级)   ✔      ✔      ✘      ✘");
        line("      protected     ✔      ✔      ✔      ✘");
        line("      public        ✔      ✔      ✔      ✔");
        line("      → 子类能看到的只有 protected 和 public");
        line("      → private 和包级默认的，子类都看不到");
        line("");

        // ══════════════════════════════════════════════
        // ④ 父类 private + getter 的正确姿势
        // ══════════════════════════════════════════════
        line("④ 正确姿势：父类 private + getter，子类通过方法访问");
        StudentOfPrivate sp = new StudentOfPrivate("张三", 21, "20230101", "计算机");
        sp.report();
        line("    → 子类没碰父类的 private 字段，走的是 getName()");
        line("    → 好处：父类的校验规则依然生效，子类无法绕过");
        line("");

        // ══════════════════════════════════════════════
        // ⑤ protected 字段的坑：子类能绕过父类校验
        // ══════════════════════════════════════════════
        line("⑤ protected 字段的坑：子类能绕过父类的校验");
        StudentOfProtected sbp = new StudentOfProtected("李四", 20, "20230102");
        line("    正常年龄 = " + sbp.getAge());
        sbp.cheatAge(999);
        line("    子类 cheatAge(999) 之后 = " + sbp.getAge());
        line("    → 如果父类字段是 private + setter，这里根本改不了，");
        line("      父类可以完全控制「年龄必须在 0-150 之间」这条规则");
        line("    → 这就是为什么生产代码的父类字段更常用 private，而不是 protected");
        line("");

        // ══════════════════════════════════════════════
        // ⑥ 子类字段用 protected 的坑
        // ══════════════════════════════════════════════
        line("⑥ 子类字段如果用 protected，封装会漏");
        StudentOfProtectedField spf = new StudentOfProtectedField("王五", "20230103");
        System.out.println("      改之前 studentId = " + spf.studentId);
        new SneakyHelper().tamper(spf);
        System.out.println("      改之后 studentId = " + spf.studentId);
        line("      → 同包的其他类能直接改你的 protected 字段，");
        line("        完全绕过了子类自己的校验和意图");
        line("      → 所以子类字段用 private，外部只能通过 getter/setter");
        line("");

        // ══════════════════════════════════════════════
        // ⑦ 那外部怎么访问子类的特有属性？
        // ══════════════════════════════════════════════
        line("⑦ 那外部怎么访问子类的特有属性？");
        line("    通过子类自己提供的 public getter/setter：");
        line("        sp.getStudentId()  → " + sp.getStudentId());
        line("        sp.getMajor()      → " + sp.getMajor());
        line("    → 每个类只【开放自己愿意开放的部分】，互不越界");
        line("");

        // ══════════════════════════════════════════════
        // ⑧ 什么时候才用 protected
        // ══════════════════════════════════════════════
        line("⑧ 什么时候才用 protected？");
        line("    【给子类访问父类内部的口子，但不给外部】");
        line("    典型场景：模板方法模式 ——");
        line("        父类定义流程骨架（用 protected 方法），子类填具体步骤");
        line("    但即便如此，也更推荐用「protected final 方法」或「protected getter」");
        line("    → 直接暴露 protected 字段是初级写法，隐患较大");
        line("");

        line("本专题要点：");
        line("  · private = 本类内部私有，父类子类都访问不到");
        line("  · 继承(is-a) ≠ 包含(has-a)，类之间只通过公开方法交互");
        line("  · 子类字段用 private：外部（含父类）只能通过 getter/setter 访问");
        line("  · 父类字段用 private + protected/public getter：子类受控访问，校验不失效");
        line("  · protected 字段的隐患：子类/同包代码能绕过你的校验");
        line("  · 外部访问子类特有属性 → 子类自己提供 public getter/setter");
    }

    static void line(String s) {
        System.out.println(s);
    }
}
