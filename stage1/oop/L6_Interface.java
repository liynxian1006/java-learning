package oop;

import java.util.ArrayList;
import java.util.List;

/**
 * ════════════════════════════════════════════════════════════
 *  第 6 关 · 接口（interface）
 * ════════════════════════════════════════════════════════════
 *
 *  【核心概念】
 *   描述"一类事物应该具备什么能力"，只说能做什麼，不规定怎么做。
 *   实现类必须实现接口里所有的抽象方法。
 *
 *  【真实业务】
 *   投递状态流转：任何"状态"对象都必须能被「推进」和「查询」——
 *   这就是接口。状态流转的算法由实现类自己决定。
 *
 *  【接口的规则（Java 8 之后）】
 *   · 接口不能有构造方法（不能 new）
 *   · 成员变量默认 public static final（常量）
 *   · 方法默认 public abstract；也可以有 default / static 具体方法
 *
 *  【和抽象类的区别（面试必背）】
 *   抽象类：单继承、可有构造方法、可有成员变量、可有具体方法
 *   接口：多实现、无构造方法、变量是常量、方法默认抽象（可加 default）
 *
 *  【运行】右键本文件 → Run
 */
public class L6_Interface {

    /** 状态接口：只声明能力，不规定实现 */
    interface ApplicationStatus {
        // 接口里的变量默认 public static final
        String PENDING = "待审核";

        /** 推进到下一状态 */
        void advance();

        /** 当前状态描述 */
        String describe();

        /** default 方法：接口可以带默认实现（Java 8+） */
        default boolean isFinal() {
            return false;
        }
    }

    /** 实现类：待审核 */
    static class PendingStatus implements ApplicationStatus {
        @Override
        public void advance() {
            System.out.println("      待审核 → 已通过");
        }

        @Override
        public String describe() {
            return "投递待企业审核中";
        }
    }

    /** 实现类：已通过（终态） */
    static class ApprovedStatus implements ApplicationStatus {
        @Override
        public void advance() {
            System.out.println("      已通过是终态，无法再推进");
        }

        @Override
        public String describe() {
            return "企业已通过该投递";
        }

        @Override
        public boolean isFinal() {
            return true;               // 重写 default 方法
        }
    }

    /** 实现类：已拒绝（终态） */
    static class RejectedStatus implements ApplicationStatus {
        @Override
        public void advance() {
            System.out.println("      已拒绝是终态，无法再推进");
        }

        @Override
        public String describe() {
            return "企业已拒绝该投递";
        }

        @Override
        public boolean isFinal() {
            return true;
        }
    }

    /** 多接口演示：一个类可以实现多个接口 */
    interface Auditable {
        void audit();
    }

    interface Notifiable {
        void notifyUser();
    }

    /** 审核 + 通知，两个能力 */
    static class ReviewAction implements Auditable, Notifiable {
        @Override
        public void audit() {
            System.out.println("      执行审核动作");
        }

        @Override
        public void notifyUser() {
            System.out.println("      发送通知给学生");
        }
    }

    public static void main(String[] args) {
        line("第 6 关 · 接口");
        line("");

        // ────────────────────────────────────────
        // ① 接口不能 new
        // ────────────────────────────────────────
        line("① 接口不能实例化");
        line("    new ApplicationStatus()  →  编译报错");
        line("    → 接口是「能力契约」，不是能造出来的东西");
        line("");

        // ────────────────────────────────────────
        // ② 实现类必须实现全部抽象方法
        // ────────────────────────────────────────
        line("② 实现类必须实现接口的所有抽象方法");
        ApplicationStatus pending = new PendingStatus();
        ApplicationStatus approved = new ApprovedStatus();
        pending.advance();
        approved.advance();
        line("");

        // ────────────────────────────────────────
        // ③ 多态：接口引用统一处理
        // ────────────────────────────────────────
        line("③ 接口引用 + 多态，统一处理不同实现");
        List<ApplicationStatus> list = new ArrayList<>();
        list.add(new PendingStatus());
        list.add(new ApprovedStatus());
        list.add(new RejectedStatus());
        for (ApplicationStatus st : list) {
            System.out.println("      " + st.describe() + "，是否终态 = " + st.isFinal());
        }
        line("    → 新增一种状态时，这个循环一行都不用改（开闭原则）");
        line("");

        // ────────────────────────────────────────
        // ④ 一个类可以实现多个接口
        // ────────────────────────────────────────
        line("④ 一个类可以实现多个接口（抽象类做不到）");
        ReviewAction action = new ReviewAction();
        action.audit();
        action.notifyUser();
        line("    → 类的单继承限制，不影响接口的多实现");
        line("");

        // ────────────────────────────────────────
        // ⑤ 接口的常量
        // ────────────────────────────────────────
        line("⑤ 接口里的变量是常量");
        line("    ApplicationStatus.PENDING = \"" + ApplicationStatus.PENDING + "\"");
        line("    → 默认 public static final，定义时就固定死了");
        line("");

        // ────────────────────────────────────────
        // ⑥ 抽象类 vs 接口 总结对比
        // ────────────────────────────────────────
        line("⑥ 抽象类 vs 接口（面试必背）");
        line("    ┌──────────────┬─────────────────┬─────────────────┐");
        line("    │              │   抽象类        │   接口          │");
        line("    ├──────────────┼─────────────────┼─────────────────┤");
        line("    │ 构造方法     │ 可以有          │ 不能有          │");
        line("    │ 成员变量     │ 可以有          │ 只有常量        │");
        line("    │ 具体方法     │ 可以有          │ 可有 default    │");
        line("    │ 继承数量     │ 单继承          │ 多实现          │");
        line("    │ 设计意图     │ 「is-a」共性抽象 │ 「has-a」能力契约 │");
        line("    └──────────────┴─────────────────┴─────────────────┘");
        line("");

        line("本关要点：");
        line("  · 接口 = 能力契约，只说「能做什么」");
        line("  · 实现类必须实现全部抽象方法");
        line("  · 一个类可实现多个接口，接口之间也可多继承");
        line("  · 变量默认 public static final；可加 default 具体方法");
        line("  · 项目映射：你项目三的 StudentMapper 就是接口，MyBatis 动态生成实现类");
    }

    static void line(String s) {
        System.out.println(s);
    }
}
