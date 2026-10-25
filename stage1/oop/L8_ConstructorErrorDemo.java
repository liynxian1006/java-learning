package oop;

/**
 * ════════════════════════════════════════════════════════════
 *  ⚠️ 反面教材文件 —— 这个文件【故意编译不过】！
 * ════════════════════════════════════════════════════════════
 *
 *  目的：让你亲眼看到"子类隐式 super() 找不到父类无参构造"的报错。
 *
 *  【预期报错】
 *      L8_ConstructorErrorDemo.java:[xx,xx] constructor BaseOnlyFullArg in class BaseOnlyFullArg
 *      cannot be applied to given types;
 *        required: java.lang.String
 *        found:    no arguments
 *        reason: actual and formal argument lists differ in length
 *
 *  【怎么验证】
 *    IDEA 里直接右键本文件 → Run，会看到编译错误提示。
 *    命令行：
 *        javac -encoding UTF-8 -d out L8_ConstructorErrorDemo.java
 *
 *  【怎么修】
 *    三种任选其一：
 *      ① 给父类加一个无参构造（最常用）
 *      ② 子类显式写 super("参数")
 *      ③ 删掉子类的无参构造，只保留带参的
 *
 *  ⚠️ 注意：因为本文件编译不过，批量编译整个目录时会连带失败。
 *     正常学习时建议把它改名成 .java.txt 保存，或直接看 IDEA 的报错提示。
 */
public class L8_ConstructorErrorDemo {

    /** 父类：只有有参构造，没有无参构造 */
    static class BaseOnlyFullArg {
        BaseOnlyFullArg(String s) {
            System.out.println("      BaseOnlyFullArg 有参构造: " + s);
        }
    }

    /** ❌ 错误：子类写无参构造 → 隐式 super() → 父类没有无参构造 → 编译报错 */
    static class ChildBroken extends BaseOnlyFullArg {
        ChildBroken() {
            // ← 编译器在这里自动插入 super()，但父类需要 String 参数
            System.out.println("      ChildBroken 构造");
        }
    }

    /** ✅ 正确示范：显式 super(参数) */
    static class ChildCorrect extends BaseOnlyFullArg {
        ChildCorrect(String s) {
            super(s);                       // 显式调用父类有参构造
            System.out.println("      ChildCorrect 构造完成");
        }
    }

    public static void main(String[] args) {
        System.out.println("这个类本意是编译失败，用来演示错误。");
        System.out.println("如果你能看到这行输出，说明编译器没报错（那就没达到演示目的）。");
        new ChildCorrect("正常参数");
    }
}
