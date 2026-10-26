package oop;

import java.util.ArrayList;
import java.util.List;

/**
 * ════════════════════════════════════════════════════════════
 *  专题 · instanceof（判断对象的真实类型）
 * ════════════════════════════════════════════════════════════
 *
 *  【你问的代码（L3 里的）】
 *      if (p2 instanceof Teacher) {
 *          System.out.println(((Teacher) p2).getTitle());
 *      }
 *
 *  【作用】
 *   判断一个【引用】实际指向的对象，是不是某个类型（或其子类）的实例。
 *   语法：引用 instanceof 类型   →  返回 boolean
 *
 *  【三个关键点】
 *   ① 判断的是【运行时真实类型】，不是变量的声明类型
 *   ② 【包含子类】：Student 对象 instanceof Person → true（Student is-a Person）
 *   ③ null instanceof 任何类型 → false（不会抛异常）
 *
 *  【标准用法：instanceof + 向下转型】
 *      多态场景下，父类引用看不见子类特有属性，
 *      必须先 instanceof 判断，再强转，才能取出来。
 *
 *  【运行】右键本文件 → Run
 */
public class L17_InstanceOf {

    // ══════════════════════════════════════════════
    // 继承体系
    // ══════════════════════════════════════════════
    static class Person {
        private String name;

        Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String role() {
            return "普通用户";
        }
    }

    static class Student extends Person {
        private String studentId;

        Student(String name, String studentId) {
            super(name);
            this.studentId = studentId;
        }

        public String getStudentId() {
            return studentId;
        }
    }

    static class Teacher extends Person {
        private String title;

        Teacher(String name, String title) {
            super(name);
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    // ══════════════════════════════════════════════
    // 接口
    // ══════════════════════════════════════════════
    interface Payable {
        void pay();
    }

    static class Salary implements Payable {
        @Override
        public void pay() {
            System.out.println("      发放工资");
        }
    }

    static class Bonus implements Payable {
        @Override
        public void pay() {
            System.out.println("      发放奖金");
        }
    }

    public static void main(String[] args) {
        line("专题 · instanceof");
        line("");

        // ══════════════════════════════════════════════
        // ① 基本用法
        // ══════════════════════════════════════════════
        line("① 基本用法：判断真实类型");
        Person zhang = new Student("张三", "20230101");
        Person li = new Teacher("李四", "讲师");
        line("    声明类型都是 Person，实际类型不同：");
        line("      zhang.getClass() = " + zhang.getClass().getSimpleName());
        line("      li.getClass()    = " + li.getClass().getSimpleName());
        line("    instanceof 判断：");
        line("      zhang instanceof Student ? " + (zhang instanceof Student));
        line("      zhang instanceof Teacher ? " + (zhang instanceof Teacher));
        line("      zhang instanceof Person  ? " + (zhang instanceof Person) + "  ← 父类也算！");
        line("      li    instanceof Teacher ? " + (li instanceof Teacher));
        line("      li    instanceof Student ? " + (li instanceof Student));
        line("    → instanceof 判断的是【运行时真实类型】，且【包含父类】");
        line("");

        // ══════════════════════════════════════════════
        // ② null 的情况
        // ══════════════════════════════════════════════
        line("② null instanceof 任何类型 → false（不抛异常）");
        Person nothing = null;
        line("      nothing instanceof Student ? " + (nothing instanceof Student));
        line("      nothing instanceof Person  ? " + (nothing instanceof Person));
        line("    → 所以可以放心写：if (obj != null && obj instanceof X)");
        line("    → 实际上 instanceof 本身已经处理了 null，前面那个 null 判断是多余的");
        line("");

        // ══════════════════════════════════════════════
        // ③ 标准用法：instanceof + 向下转型
        // ══════════════════════════════════════════════
        line("③ 标准用法：instanceof + 向下转型（多态场景取子类特有属性）");
        Object obj = new Teacher("王五", "副教授");
        if (obj instanceof Teacher) {
            Teacher t = (Teacher) obj;            // 强转
            line("      转成 Teacher 后能读到 title = " + t.getTitle());
        } else if (obj instanceof Student) {
            System.out.println("      是学生");
        }
        line("    → 父类/接口引用看不见子类特有属性，必须先判断再强转");
        line("");

        // ══════════════════════════════════════════════
        // ④ 不判断就强转 → ClassCastException
        // ══════════════════════════════════════════════
        line("④ 不判断就强转会怎样");
        Person p = new Student("赵六", "20230102");
        try {
            Teacher bad = (Teacher) p;             // 实际是 Student，硬转成 Teacher
            System.out.println("      转换成功（不会走到这）");
        } catch (ClassCastException e) {
            line("      抛 ClassCastException: " + e.getMessage());
            line("      → 所以必须先 instanceof 判断");
        }
        line("");

        // ══════════════════════════════════════════════
        // ⑤ Java 16+ 模式匹配（推荐写法）
        // ══════════════════════════════════════════════
        line("⑤ Java 16+ 模式匹配：不用再强转一次（推荐）");
        Object obj2 = new Teacher("钱七", "助理教授");
        if (obj2 instanceof Teacher teacher) {       // 直接在 instanceof 里声明变量
            line("      title = " + teacher.getTitle() + "  ← 不用再写 (Teacher) obj2");
        }
        line("    老写法（Java 14 及以前）对比：");
        Object obj3 = new Teacher("孙八", "讲师");
        if (obj3 instanceof Teacher) {
            Teacher teacher3 = (Teacher) obj3;      // 老写法要多写一行
            line("      title = " + teacher3.getTitle());
        }
        line("    → 项目用的是 JDK 17，可以直接用模式匹配写法");
        line("");

        // ══════════════════════════════════════════════
        // ⑥ instanceof 也能判断接口
        // ══════════════════════════════════════════════
        line("⑥ instanceof 也能判断接口（多态最常见的用法）");
        List<Payable> list = new ArrayList<>();
        list.add(new Salary());
        list.add(new Bonus());
        for (Payable item : list) {
            if (item instanceof Salary) {
                System.out.println("      发薪流程：");
                item.pay();
            } else if (item instanceof Bonus) {
                System.out.println("      奖金流程：");
                item.pay();
            }
        }
        line("    → 接口引用 + instanceof 是处理【多种实现】的经典手法");
        line("");

        // ══════════════════════════════════════════════
        // ⑦ instanceof vs getClass() ==（容易混）
        // ══════════════════════════════════════════════
        line("⑦ instanceof vs getClass() == 的区别");
        Person s = new Student("周九", "20230103");
        line("      s instanceof Person   ? " + (s instanceof Person) + "   ← 子类也算");
        line("      s.getClass() == Person.class ? "
                + (s.getClass() == Person.class) + "   ← 精确匹配，子类不算");
        line("    → 要判断「是不是这个类型或它的子类」→ 用 instanceof");
        line("    → 要判断「精确是不是这个类型」→ 用 getClass() ==");
        line("");

        line("本专题要点：");
        line("  · instanceof 判断【运行时真实类型】，包含子类");
        line("  · null instanceof → false，不会抛异常");
        line("  · 标准用法：先 instanceof 再强转，否则 ClassCastException");
        line("  · Java 16+ 推荐用模式匹配：if (o instanceof Type t) { 直接用 t }");
        line("  · 接口也能 instanceof —— 处理多种实现的标准手法");
        line("  · instanceof（含子类） vs getClass() ==（精确匹配）要分清");
    }

    static void line(String s) {
        System.out.println(s);
    }
}
