package oop;

import java.util.ArrayList;
import java.util.List;

/**
 * ════════════════════════════════════════════════════════════
 *  专题 · 为什么要 getter/setter？（三个理由逐个证明）
 * ════════════════════════════════════════════════════════════
 *
 *  【L2 结尾的三条要点，逐个讲透】
 *   ① 构造方法内部也走 setter，别直接写 this.age = age
 *   ② getter 通常只读不判断，setter 才是校验的重点
 *   ③ 为了控制访问权限 + 加校验 + 未来加逻辑不破坏兼容
 *
 *  【第③条里的「不破坏兼容」是什么意思？】
 *   场景：项目已经写完了，突然新需求 ——「姓名要 trim，且不能超过 20 字」
 *   · 如果字段是 public：所有 s.name = xxx 的地方都得改，一处漏掉就是脏数据
 *   · 如果是 private + setter：只改 setName 内部一个方法，调用方一行都不用动
 *   → 这就是「加逻辑不破坏兼容」
 *
 *  【运行】右键本文件 → Run
 */
public class L13_WhyGetterSetter {

    // ══════════════════════════════════════════════
    // 反面：public 字段
    // ══════════════════════════════════════════════
    /** ❌ 反面教材：字段直接 public，谁都能随便改 */
    static class BadStudent {
        public String name;
        public int age;
    }

    // ══════════════════════════════════════════════
    // 正面：private + getter/setter
    // ══════════════════════════════════════════════
    /** ✅ 正确做法：数据 private，行为 public */
    static class GoodStudent {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            // setter 里做校验
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("姓名不能为空");
            }
            if (name.trim().length() > 20) {
                throw new IllegalArgumentException("姓名长度不能超过 20");
            }
            this.name = name.trim();       // 顺便做数据清洗
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

    // ══════════════════════════════════════════════
    // 模拟"业务代码"（调用方）
    // ══════════════════════════════════════════════
    static class Service {
        /** 用 public 字段的写法 */
        void createWithBad(BadStudent s, String name, int age) {
            s.name = name;                  // ← 直接赋值，没有任何校验
            s.age = age;
        }

        /** 用 getter/setter 的写法 */
        void createWithGood(GoodStudent s, String name, int age) {
            s.setName(name);               // ← 走 setter，校验自动生效
            s.setAge(age);
        }
    }

    public static void main(String[] args) {
        line("专题 · 为什么要 getter/setter？");
        line("");

        // ══════════════════════════════════════════════
        // 理由一：控制访问权限
        // ══════════════════════════════════════════════
        line("理由一：控制访问权限");
        line("    public 字段  → 任何人都能读、都能改");
        line("    private 字段 → 外部【只能】通过你开放的 getter/setter 访问");
        line("    你可以：只给读不给改（只写 getter）、只给改不给读、读写都给、都不给");
        line("    → 访问权限是你【设计】出来的，不是默认全开的");
        line("");

        // ══════════════════════════════════════════════
        // 理由二：加校验
        // ══════════════════════════════════════════════
        line("理由二：加校验（把非法数据挡在系统之外）");
        Service svc = new Service();
        BadStudent bad = new BadStudent();
        svc.createWithBad(bad, "", -5);
        line("    用 public 字段塞入 姓名=\"\" 年龄=-5：");
        line("      结果 name = \"" + bad.name + "\"，age = " + bad.age);
        line("      → 非法数据就这么进系统了，没有任何人拦它");

        GoodStudent good = new GoodStudent();
        try {
            svc.createWithGood(good, "", -5);
        } catch (IllegalArgumentException e) {
            line("    用 setter 塞入同样的数据：");
            line("      抛出异常：" + e.getMessage());
            line("      → 数据在入口就被挡住了，系统内部始终是干净的");
        }
        line("");

        // ══════════════════════════════════════════════
        // 理由三：未来加逻辑不破坏兼容（重点）
        // ══════════════════════════════════════════════
        line("理由三：未来加逻辑，不破坏兼容（★ 面试最爱问）");
        line("");

        line("  场景：项目已经写完了，突然来新需求");
        line("    「姓名要 trim 空白，且长度不能超过 20 字」");
        line("");

        // 3-1 public 字段的代价
        line("  ① 如果字段是 public —— 所有赋值点都得改");
        List<String> publicCallSites = new ArrayList<>();
        publicCallSites.add("s1.name = name;              // Controller 层");
        publicCallSites.add("s2.name = name;              // Service 层");
        publicCallSites.add("s3.name = name;              // 定时任务里");
        publicCallSites.add("entity.setName(name) 里再赋值  // 工具类");
        publicCallSites.add("... 还有 12 处，散落在 6 个文件里");
        for (int i = 0; i < publicCallSites.size(); i++) {
            line("      " + publicCallSites.get(i));
        }
        line("      → 要改 16 处。漏掉任何一处，新需求就失效（那处还会塞脏数据）");
        line("");

        // 3-2 setter 的代价
        line("  ② 如果是 private + setter —— 只改 1 处");
        line("      只改 GoodStudent.setName() 一个方法：");
        line("          public void setName(String name) {");
        line("              if (name == null || name.trim().isEmpty()) throw ...;");
        line("              if (name.trim().length() > 20) throw ...;");
        line("              this.name = name.trim();     // ← 只加这一处");
        line("          }");
        line("      → 调用方【一行都不用改】，全部自动生效");
        line("      → 这就是「不破坏兼容」：对外接口没变，内部实现随便改");
        line("");

        // 3-3 实际演示：改一处 vs 改多处
        line("  ③ 实测演示：改一个 setter，看调用方受不受影响");
        GoodStudent s1 = new GoodStudent();
        s1.setName("  张三  ");
        line("      setName(\"  张三  \") → 存入的是 \"" + s1.getName() + "\"（trim 生效）");
        try {
            s1.setName("这是一个非常非常非常非常非常长的名字超过二十个字了");
        } catch (IllegalArgumentException e) {
            line("      setName(超长名字) → 抛异常：" + e.getMessage());
        }
        line("      → 上面这些规则全在 setName 内部，调用方只管调 setName，不用知道规则");
        line("");

        // ══════════════════════════════════════════════
        // 为什么 getter 通常不校验
        // ══════════════════════════════════════════════
        line("为什么 getter 通常不做校验？");
        line("    · setter 管的是【输入】—— 输入可能来自用户，必须校验");
        line("    · getter 管的是【输出】—— 数据进来时已经校验过了，是干净的");
        line("    · 想象一下：getName() 读到脏数据时该干嘛？抛异常？返回 null？");
        line("      都很别扭。所以 getter 一般就【原样返回】");
        line("    · 例外：如果 getter 涉及【计算】（比如 getTotal() = price * count），");
        line("      那就要考虑计算结果是否合理");
        line("");

        // ══════════════════════════════════════════════
        // 构造方法为什么也走 setter
        // ══════════════════════════════════════════════
        line("为什么构造方法内部也走 setter？");
        line("    因为【构造也是数据进入系统的入口】。");
        line("    如果构造方法绕过 setter 直接 this.age = age，");
        line("    那 new Student(\"张三\", -5, \"计算机\") 就能塞进非法年龄 —— 校验形同虚设。");
        line("    → 原则：所有能让数据「流进来」的地方，都要过校验。");
        line("");

        line("本专题要点：");
        line("  · 理由一：控制访问权限（读/写开放与否由你设计）");
        line("  · 理由二：加校验（把非法数据挡在系统之外）");
        line("  · 理由三：未来加逻辑不破坏兼容（改内部 1 处，不改调用方 N 处）");
        line("  · getter 一般不校验（管输出），setter 才是校验重点（管输入）");
        line("  · 构造方法也是数据入口，也要走 setter");
        line("  · 面试话术见下");
    }

    static void line(String s) {
        System.out.println(s);
    }
}
