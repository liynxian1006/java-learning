package oop;

/**
 * ════════════════════════════════════════════════════════════
 *  专题 · createWithBad vs createWithGood（public 字段 vs setter）
 * ════════════════════════════════════════════════════════════
 *
 *  你贴的这段代码：
 *      void createWithBad(BadStudent s, String name, int age) {
 *          s.name = name;          // ← 直接赋值，没有任何校验
 *          s.age  = age;
 *      }
 *      void createWithGood(GoodStudent s, String name, int age) {
 *          s.setName(name);        // ← 走 setter，校验自动生效
 *          s.setAge(age);
 *      }
 *
 *  区别只有一句话：
 *      Bad  写法 = 数据【直接】进对象，跳过所有检查
 *      Good 写法 = 数据【先过 setter】，不合法当场被拦住
 *
 *  【为什么会有这种区别？】
 *      BadStudent  的 name/age 是 public  → 外部可以直接「点」进去赋值
 *      GoodStudent 的 name/age 是 private → 外部【只能】调 setName/setAge
 *      而 setName/setAge 内部写了校验逻辑
 *
 *  【运行】右键本文件 → Run，或：
 *      cd java-learning/stage1 && javac oop/L20_FieldVsSetter.java && java oop.L20_FieldVsSetter
 */
public class L20_FieldVsSetter {

    // ════════════════════════════════════════════════
    // 反面：public 字段（任何人都能直接改，没有任何守卫）
    // ════════════════════════════════════════════════
    /** ❌ 字段 public，等于「家门大开」 */
    static class BadStudent {
        public String name;      // 谁都能 s.name = "随便什么"
        public int age;          // 谁都能 s.age = -999
    }

    // ════════════════════════════════════════════════
    // 正面：private 字段 + setter（门口有保安）
    // ════════════════════════════════════════════════
    /** ✅ 字段 private，外部只能走 setName/setAge */
    static class GoodStudent {
        private String name;
        private int age;

        public String getName() { return name; }
        public int getAge() { return age; }

        public void setName(String name) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("姓名不能为空");
            }
            if (name.trim().length() > 20) {
                throw new IllegalArgumentException("姓名长度不能超过 20");
            }
            this.name = name.trim();     // 顺便清洗空白
        }

        public void setAge(int age) {
            if (age < 0 || age > 150) {
                throw new IllegalArgumentException("年龄不合法: " + age);
            }
            this.age = age;
        }
    }

    // ════════════════════════════════════════════════
    // 业务方法（就是你贴的那两段）
    // ════════════════════════════════════════════════
    static class Service {
        /** 用 public 字段的写法：直接赋值，没有任何校验 */
        void createWithBad(BadStudent s, String name, int age) {
            s.name = name;               // ← 直接怼进去
            s.age = age;                 // ← 直接怼进去
        }

        /** 用 getter/setter 的写法：走 setter，校验自动生效 */
        void createWithGood(GoodStudent s, String name, int age) {
            s.setName(name);            // ← 过安检
            s.setAge(age);               // ← 过安检
        }
    }

    public static void main(String[] args) {
        line("专题 · createWithBad vs createWithGood");
        line("");

        Service svc = new Service();

        // ══════════════════════════════════════════════
        // 场景一：同样的「脏数据」分别走两种写法
        // ══════════════════════════════════════════════
        line("场景一：传入 姓名=\"\"（空串）、年龄=-5（负数）");
        line("────────────────────────────────────────");

        BadStudent bad = new BadStudent();
        svc.createWithBad(bad, "", -5);
        line("  createWithBad：");
        line("    姓名 = \"" + bad.name + "\"  年龄 = " + bad.age);
        line("    → 脏数据【顺利】进系统了，没人拦它");
        line("");

        GoodStudent good = new GoodStudent();
        try {
            svc.createWithGood(good, "", -5);
            line("  createWithGood：竟然没报错？这不可能");
        } catch (IllegalArgumentException e) {
            line("  createWithGood：");
            line("    抛异常：" + e.getMessage());
            line("    → 数据在【入口】就被挡住了，对象内部始终是干净的");
        }
        line("");

        // ══════════════════════════════════════════════
        // 场景二：带空格姓名 + 超长年龄
        // ══════════════════════════════════════════════
        line("场景二：传入 姓名=\"  张三  \"（带空格）、年龄=200（超范围）");
        line("────────────────────────────────────────");

        BadStudent bad2 = new BadStudent();
        svc.createWithBad(bad2, "  张三  ", 200);
        line("  createWithBad：");
        line("    存入姓名 = \"" + bad2.name + "\"  年龄 = " + bad2.age);
        line("    → 空格没被清掉、超龄也没拦 → 脏数据 +1");
        line("");

        GoodStudent good2 = new GoodStudent();
        try {
            svc.createWithGood(good2, "  张三  ", 200);
        } catch (IllegalArgumentException e) {
            line("  createWithGood：");
            line("    抛异常：" + e.getMessage());
            line("    → 拦住了。但注意：姓名空格本来会被 trim，只是年龄先挂了");
        }
        // 换一个合法年龄再测 trim 效果
        try {
            good2.setAge(22);
            good2.setName("  张三  ");
            line("  createWithGood（年龄合法时）：");
            line("    存入姓名 = \"" + good2.getName() + "\"  年龄 = " + good2.getAge());
            line("    → 空格被自动 trim 成 \"张三\"，数据干净");
        } catch (IllegalArgumentException e) {
            line("    " + e.getMessage());
        }
        line("");

        // ══════════════════════════════════════════════
        // 场景三：正常数据，两种写法都能用
        // ══════════════════════════════════════════════
        line("场景三：正常数据 姓名=\"李四\" 年龄=20");
        line("────────────────────────────────────────");
        BadStudent bad3 = new BadStudent();
        svc.createWithBad(bad3, "李四", 20);
        GoodStudent good3 = new GoodStudent();
        svc.createWithGood(good3, "李四", 20);
        line("  两种写法【都能】存进合法数据 → 所以坏写法「平时看不出来」");
        line("  → 这正是 public 字段最危险的地方：合法时没问题，一旦有脏输入就崩");
        line("");

        // ══════════════════════════════════════════════
        // 总结对比表
        // ══════════════════════════════════════════════
        line("═══════════ 一句话总结 ═════════════");
        line("  createWithBad  →  s.name = name");
        line("      字段是 public，外部直接赋值，【跳过】所有校验");
        line("      脏数据（空串/负数/超长）能直接进对象");
        line("");
        line("  createWithGood →  s.setName(name)");
        line("      字段是 private，外部【只能】调 setter");
        line("      setter 内部做校验 + 清洗，不合法当场抛异常");
        line("      对象内部始终保持干净");
        line("");
        line("  本质区别 = 把「校验逻辑放在哪」：");
        line("      Bad  ：校验逻辑【无处可放】（字段谁都能改）");
        line("      Good ：校验逻辑【集中在一个方法里】（setter），调用方零成本自动享受");
        line("");
        line("  所以写实体类时，字段一律 private，用 getter/setter 暴露 ——");
        line("  这是 Java 最基础的「封装」实践，面试必问。");
    }

    static void line(String s) {
        System.out.println(s);
    }
}
