package oop;

import java.lang.reflect.Constructor;

/**
 * ════════════════════════════════════════════════════════════
 *  专题 · 空参构造到底要不要写？
 * ════════════════════════════════════════════════════════════
 *
 *  【一句话结论】
 *   只要你写了 ANY 一个有参构造，Java 就【不再自动生成】空参构造。
 *   此时 new Xxx() 会直接编译报错。
 *
 *  【三种情况】
 *   ① 一个构造都不写  → 编译器自动生成 public Xxx()   ← 无参构造是隐式存在的
 *   ② 只写有参构造    → 编译器【不】再生成无参构造      → new Xxx() 编译报错
 *   ③ 无参 + 有参都写  → 两个都能 new                    ← 项目里的标准写法
 *
 *  【为什么项目里必须写】
 *   框架要「反射 new 一个空对象，再逐个塞值」：
 *     - MyBatis 映射结果 → 对象
 *     - Spring 注入 / Jackson 反序列化 JSON → 对象
 *   这些框架默认调用的是【无参构造】+ setter。
 *
 *  【运行】右键本文件 → Run
 */
public class L8_ConstructorTopic {

    // ══════════════ 情况①：只写有参构造 ══════════════
    /** ❌ 只有有参构造 → 没有空参构造 */
    static class OnlyFullArg {
        String name;

        OnlyFullArg(String name) {
            this.name = name;
        }
    }

    // ══════════════ 情况②：无参 + 有参都写 ══════════════
    /** ✅ 标准写法：两个都有 */
    static class BothConstructors {
        String name;
        int age;

        /** 空参构造：给框架用 */
        public BothConstructors() {
        }

        /** 有参构造：给手动 new 用 */
        public BothConstructors(String name, int age) {
            this.name = name;
            this.age = age;
        }

        // getter/setter：框架第二步要靠 setter 逐个塞值
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    // ══════════════ 情况③：继承时 super() 的隐式调用 ══════════════
    /** 父类只有有参构造 */
    static class BaseOnlyFullArg {
        BaseOnlyFullArg(String s) {
            System.out.println("      BaseOnlyFullArg 有参构造");
        }
    }

    // ⚠️ 反面教材：子类若无参构造，会隐式调用 super()，
    //    而父类没有无参构造 → 【编译报错】。
    //    这个类单独放在 L8_ConstructorErrorDemo.java 里演示错误，
    //    放在本文件会导致整个项目编译失败，所以这里只注释掉。
    /*
    static class ChildBroken extends BaseOnlyFullArg {
        ChildBroken() {
            // 隐式 super()  ← 这里就编译报错了
        }
    }
    */

    // ══════════════ 情况④：子类显式 super(参数) ══════════════
    /** 正确写法：显式调用父类有参构造 */
    static class ChildCorrect extends BaseOnlyFullArg {
        ChildCorrect(String s) {
            super(s);       // 必须显式写，因为父类没有无参构造
            System.out.println("      ChildCorrect 构造完成");
        }
    }

    public static void main(String[] args) {
        line("专题 · 空参构造到底要不要写");
        line("");

        // ────────────────────────────────────────
        // ① 只写有参构造的后果
        // ────────────────────────────────────────
        line("① 只写有参构造 = 没有空参构造");
        OnlyFullArg a = new OnlyFullArg("张三");
        line("    new OnlyFullArg(\"张三\")  → 正常");
        line("    new OnlyFullArg()        → 编译报错：");
        line("        no suitable constructor found for OnlyFullArg()");
        line("    → 原因：Java 只在【一个构造都没写】时才自动补空参构造");
        line("");

        // ────────────────────────────────────────
        // ② 无参 + 有参都写
        // ────────────────────────────────────────
        line("② 无参 + 有参都写（项目里的标准写法）");
        BothConstructors b1 = new BothConstructors();
        b1.name = "李四";
        b1.age = 20;
        line("    new BothConstructors()          → 正常，拿到空对象");
        BothConstructors b2 = new BothConstructors("王五", 21);
        line("    new BothConstructors(\"王五\",21)  → 正常，一次填好");
        line("    → 两种用法都支持，框架和手动 new 都能用");
        line("");

        // ────────────────────────────────────────
        // ③ 框架为什么必须要无参构造（核心原因）
        // ────────────────────────────────────────
        line("③ 框架为什么必须要无参构造（核心原因）");
        line("    MyBatis 查数据库 → 转成 Java 对象，实际做的是：");
        line("        1) 反射 new Student()          ← 必须有无参构造！");
        line("        2) rs.getString(\"name\") 读一列");
        line("        3) student.setName(值)         ← 逐个塞值");
        line("        4) 返回这个对象");
        line("    如果实体类只有有参构造，第 1 步就抛：");
        line("        java.lang.NoSuchMethodException: <init>()");
        line("    同样的道理适用于：");
        line("        · Spring 注入（@Autowired 字段注入）");
        line("        · Jackson 反序列化 JSON 请求体（@RequestBody）");
        line("        · 很多 ORM / 序列化框架");
        line("");

        // ────────────────────────────────────────
        // ④ 反射实测：证明框架就是靠无参构造
        // ────────────────────────────────────────
        line("④ 反射实测：框架怎么造对象");
        try {
            // 模拟 MyBatis：反射调用无参构造
            Constructor<BothConstructors> c = BothConstructors.class.getDeclaredConstructor();
            BothConstructors obj = c.newInstance();
            obj.setName("反射创建的学生");
            line("    反射 newInstance() 成功 → " + obj.getName());
            line("    → 证明：框架确实是用「无参构造 + setter」两步走");
        } catch (Exception e) {
            line("    反射失败：" + e.getClass().getSimpleName());
        }

        try {
            // 模拟框架对「只有有参构造」的类做同样操作
            Constructor<OnlyFullArg> c = OnlyFullArg.class.getDeclaredConstructor();
            c.newInstance();
            line("    OnlyFullArg 无参构造存在（不会走到这）");
        } catch (NoSuchMethodException e) {
            line("    OnlyFullArg 反射找无参构造 → NoSuchMethodException");
            line("    → 这就是框架报错的真实原因");
        } catch (Exception e) {
            line("    其他异常：" + e.getClass().getSimpleName());
        }
        line("");

        // ────────────────────────────────────────
        // ⑤ 继承中的 super 规则
        // ────────────────────────────────────────
        line("⑤ 继承中的 super 规则");
        line("    父类只有有参构造时，子类【必须】显式写 super(参数)");
        line("    否则隐式 super() 找不到父类无参构造 → 编译报错");
        line("    实际运行：");
        new ChildCorrect("参数");
        line("");

        line("本专题要点：");
        line("  · 写了一个有参构造 → 编译器不再自动生成空参构造");
        line("  · 实体类规范：无参构造 + 有参构造，两个都写");
        line("  · 原因：MyBatis / Spring / Jackson 都要「反射 new 空对象 + setter 塞值」");
        line("  · 父类只有有参构造时，子类必须显式 super(参数)");
    }

    static void line(String s) {
        System.out.println(s);
    }
}
