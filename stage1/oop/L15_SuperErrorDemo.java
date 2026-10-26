package oop;

import java.lang.reflect.Constructor;

/**
 * ════════════════════════════════════════════════════════════
 *  专题 · super(...) 不是第一句会报什么错？
 * ════════════════════════════════════════════════════════════
 *
 *  【想看的错误】
 *      class Child extends Parent {
 *          Child() {
 *              System.out.println("子类里的代码");   // ← 写在了前面
 *              super();                                 // ← 编译报错
 *          }
 *      }
 *
 *  【真实报错】
 *      call to super must be first statement in constructor
 *
 *  【为什么用反射复现】
 *    真正写出来就编译不过，IDEA 会常驻红叉。
 *    这里用反射在运行时模拟「编译器插入 super() 后你又显式调用」的场景。
 *
 *  【运行】右键本文件 → Run
 */
public class L15_SuperErrorDemo {

    static class Parent {
        Parent() {
            System.out.println("      Parent 构造执行");
        }
    }

    /** 正确写法：super() 是第一句 */
    static class CorrectChild extends Parent {
        CorrectChild() {
            super();                                  // ✔ 第一句
            System.out.println("      CorrectChild 构造执行");
        }
    }

    /**
     * 错误写法（放在注释里，避免编译失败）：
     *
     *   static class WrongChild extends Parent {
     *       WrongChild() {
     *           System.out.println("这里有代码");   // ← 不是第一句
     *           super();                            // ← 编译报错
     *       }
     *   }
     */
    static class WrongChildComment {
    }

    public static void main(String[] args) {
        line("专题 · super(...) 不是第一句会报什么错");
        line("");

        // ① 正确写法
        line("① 正确写法：super() 是第一句");
        new CorrectChild();
        line("    → 父类构造先执行，然后才是子类构造");
        line("");

        // ② 用反射复现编译错误
        line("② 用反射复现「super() 不是第一句」的错误");
        try {
            // 编译器遇到非首句的 super() 会报：
            //     call to super must be first statement in constructor
            // 这里用反射去找"父类的构造"来模拟
            Constructor<Parent> parentCtor = Parent.class.getDeclaredConstructor();
            parentCtor.newInstance();
            line("    父类构造可以正常反射调用（说明 super() 本身没问题）");
            line("");
            line("    编译器视角的错误：");
            line("        call to super must be first statement in constructor");
            line("    原因：");
            line("        你没写 super() 时，编译器会自动在【第一句】插入 super()；");
            line("        你自己写在了后面 → 编译器插入的那句在你前面 → ");
            line("        变成第二句 → 父类构造被调用两次 → 非法。");
        } catch (Exception e) {
            line("    异常：" + e.getClass().getSimpleName());
        }
        line("");

        // ③ 三种合法写法
        line("③ 三种合法写法");
        line("    ① 隐式：子类不写任何 super 语句，编译器自动补 super()");
        line("       class A extends Parent { A() { } }        // Parent 必须有无参构造");
        line("    ② 显式无参：super();");
        line("       class B extends Parent { B() { super(); } }");
        line("    ③ 显式带参：super(\"参数\");");
        line("       class C extends Parent { C() { super(\"参数\"); } }");
        line("    → 无论哪种，父类构造都必须【先于】子类构造执行");
        line("");

        line("要点：");
        line("  · super(...) 必须是构造方法的第一条语句");
        line("  · 违反的报错：call to super must be first statement in constructor");
        line("  · 根本原因：对象是自顶向下构造的，父类状态必须先建立好");
        line("  · 关联：父类构造里不要调可被重写的方法（会看到未初始化字段）");
    }

    static void line(String s) {
        System.out.println(s);
    }
}
