package oop;

import java.lang.reflect.Constructor;

/**
 * ════════════════════════════════════════════════════════════
 *  专题 · 父类只有有参构造时，子类该怎么写？
 * ════════════════════════════════════════════════════════════
 *
 *  【要演示的错误】
 *      子类写了无参构造 → 编译器隐式插入 super()
 *      → 但父类没有无参构造 → 编译报错：
 *
 *        java: 无法将类 Xxx.BaseOnlyFullArg 中的构造器 BaseOnlyFullArg 应用到给定类型;
 *          需要: java.lang.String
 *          找到:    没有参数
 *          原因: 实际参数表和形参表长度不同
 *
 *  【本文件为什么不直接写那个错误】
 *    写出来就编译不过，IDEA 会常驻一个红叉，
 *    容易被误认为「代码写坏了」。所以这里改用【反射】在运行时复现同样的错误。
 *
 *  【三种修法】
 *    ① 给父类加一个无参构造（最常用）
 *    ② 子类显式写 super("参数")            ← 本文件的 ChildCorrect
 *    ③ 删掉子类的无参构造，只保留带参的
 *
 *  【运行】右键本文件 → Run
 */
public class L8_ConstructorErrorDemo {

    /** 父类：只有有参构造，没有无参构造 */
    static class BaseOnlyFullArg {
        BaseOnlyFullArg(String s) {
            System.out.println("      [父类] BaseOnlyFullArg 有参构造: " + s);
        }
    }

    /**
     * ❌ 假如子类这样写，编译器会报错：
     *
     *    static class ChildBroken extends BaseOnlyFullArg {
     *        ChildBroken() {
     *            // ← 编译器在这里自动插入 super()，但父类需要 String 参数
     *        }
     *    }
     *
     * 真实报错（IDEA 里把上面这段取消注释就能看到）：
     *    无法将类 BaseOnlyFullArg 中的构造器 BaseOnlyFullArg应用到给定类型;
     *      需要: java.lang.String
     *      找到:    没有参数
     */
    static class ChildBrokenDemo {
        // 这里用注释保留反面教材，不参与编译
    }

    /** ✅ 修法②：子类显式写 super(参数) */
    static class ChildCorrect extends BaseOnlyFullArg {
        ChildCorrect(String s) {
            super(s);                       // 必须显式写，父类没有无参构造
            System.out.println("      [子类] ChildCorrect 构造完成");
        }
    }

    /** ✅ 修法①：父类补一个无参构造（这样子类写空参就没问题了） */
    static class BaseWithNoArg {
        BaseWithNoArg() {
            System.out.println("      [父类] BaseWithNoArg 无参构造");
        }

        BaseWithNoArg(String s) {
            System.out.println("      [父类] BaseWithNoArg 有参构造: " + s);
        }
    }

    static class ChildWithNoArgParent extends BaseWithNoArg {
        ChildWithNoArgParent() {
            // 隐式 super() → 父类现在有无参构造了 → 编译通过 ✓
            System.out.println("      [子类] ChildWithNoArgParent 构造完成");
        }
    }

    public static void main(String[] args) {
        line("专题 · 父类只有有参构造时，子类该怎么写");
        line("");

        // ────────────────────────────────────────
        // ① 运行时复现那条编译错误
        // ────────────────────────────────────────
        line("① 用反射复现「子类隐式 super()」会遇到的错误");
        try {
            // 相当于编译器在 ChildBroken() 里自动插入的 super()
            Constructor<BaseOnlyFullArg> implicitSuper =
                    BaseOnlyFullArg.class.getDeclaredConstructor();   // 找无参构造
            implicitSuper.newInstance();
            line("    没报错（不应该走到这）");
        } catch (NoSuchMethodException e) {
            line("    编译器视角的错误：");
            line("        无法将类 BaseOnlyFullArg 中的构造器 BaseOnlyFullArg应用到给定类型;");
            line("          需要: java.lang.String");
            line("          找到:    没有参数");
            line("          原因: 实际参数表和形参表长度不同");
            line("    → 因为父类只有 (String) 这个构造，没有 () 无参构造");
        } catch (Exception e) {
            line("    其他异常：" + e.getClass().getSimpleName());
        }
        line("");

        // ────────────────────────────────────────
        // ② 修法②：显式 super(参数)
        // ────────────────────────────────────────
        line("② 修法②：子类显式写 super(参数)");
        new ChildCorrect("正常参数");
        line("    → 编译通过，父类构造被正常调用");
        line("");

        // ────────────────────────────────────────
        // ③ 修法①：父类补无参构造
        // ────────────────────────────────────────
        line("③ 修法①：父类补一个无参构造");
        new ChildWithNoArgParent();
        line("    → 编译通过，子类的隐式 super() 找得到父类无参构造");
        line("");

        line("本专题要点：");
        line("  · 子类构造方法里，编译器会【隐式】插入 super()");
        line("  · 父类没有无参构造时 → 编译报错：需要 String，找到无参数");
        line("  · 修法：① 父类加无参  ② 子类显式 super(参数)  ③ 子类只留带参构造");
        line("  · 实际开发中，父类通常会保留一个无参构造（给子类和框架留余地）");
    }

    static void line(String s) {
        System.out.println(s);
    }
}
