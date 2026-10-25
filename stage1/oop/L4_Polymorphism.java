package oop;

import java.util.ArrayList;
import java.util.List;

/**
 * ════════════════════════════════════════════════════════════
 *  第 4 关 · 多态（Polymorphism）
 * ════════════════════════════════════════════════════════════
 *
 *  【核心概念】
 *   同一个方法调用，因为「实际对象类型」不同而表现出不同行为。
 *
 *  【三个前提，缺一不可】
 *   1. 父类引用指向子类对象（向上转型）
 *   2. 父类方法被子类重写（@Override）
 *   3. 运行时根据对象的真实类型决定执行哪个版本
 *
 *  【面试金句】
 *   "编译看左边（变量声明的类型决定能调哪些方法），
 *    运行看右边（对象真实类型决定执行哪个方法）。"
 *
 *  【这正是你项目二 JobService 的原理】
 *   Dao 层用父类/接口引用接收不同子类，Spring 靠多态调用不同的实现。
 *
 *  【运行】右键本文件 → Run
 */
public class L4_Polymorphism {

    /** 支付方式抽象父类 —— 真实业务：投递简历时的支付/审核渠道 */
    static abstract class Channel {
        protected String channelName;

        protected Channel(String channelName) {
            this.channelName = channelName;
        }

        /** 抽象方法：子类必须各自实现 */
        public abstract void process();

        /** 普通方法：所有子类共享，也可以被重写 */
        public void describe() {
            System.out.println("      [" + channelName + "] 正在处理…");
        }
    }

    static class OnlineChannel extends Channel {
        private double amount;

        public OnlineChannel(double amount) {
            super("线上渠道");
            this.amount = amount;
        }

        @Override
        public void process() {
            System.out.println("      线上渠道处理中，金额 " + amount + " 元，即时到账");
        }

        @Override
        public void describe() {
            super.describe();               // 先调父类版本，再加自己的
            System.out.println("      线上渠道额外做了风控校验");
        }

        /** 子类特有方法：父类引用看不到它 */
        public double getAmount() {
            return amount;
        }
    }

    static class OfflineChannel extends Channel {
        private String address;

        public OfflineChannel(String address) {
            super("线下渠道");
            this.address = address;
        }

        @Override
        public void process() {
            System.out.println("      线下渠道处理中，网点地址：" + address);
        }
    }

    public static void main(String[] args) {
        line("第 4 关 · 多态");
        line("");

        // ────────────────────────────────────────
        // ① 三个前提一起演示
        // ────────────────────────────────────────
        line("① 多态三要素：父类引用 + 子类对象 + 方法重写");
        Channel c1 = new OnlineChannel(100.0);
        Channel c2 = new OfflineChannel("清华路 88 号");
        line("    Channel c1 = new OnlineChannel(...)");
        line("      c1 的声明类型 = " + Channel.class.getSimpleName());
        line("      c1 的真实类型 = " + c1.getClass().getSimpleName());
        line("    Channel c2 = new OfflineChannel(...)");
        line("      c2 的真实类型 = " + c2.getClass().getSimpleName());
        line("");

        // ────────────────────────────────────────
        // ② 同一句代码，不同结果 —— 多态的精髓
        // ────────────────────────────────────────
        line("② 同一句代码，不同结果（这就是多态）");
        c1.process();
        c2.process();
        line("    → 调用的是同一个 process()，执行的是各自子类的版本");
        line("");

        // ────────────────────────────────────────
        // ③ 编译看左，运行看右
        // ────────────────────────────────────────
        line("③ 编译看左，运行看右");
        line("    编译期：c1 声明为 Channel，只能看到 Channel 里的方法");
        line("    运行期：c1 实际是 OnlineChannel，执行 OnlineChannel.process()");
        line("    验证：");
        c1.describe();
        c2.describe();
        line("    → describe() 被重写，c1 走 OnlineChannel 的版本（含 super 调用）");
        line("");

        // ────────────────────────────────────────
        // ④ 多态的真实用途：一个数组装不同子类型
        // ────────────────────────────────────────
        line("④ 多态的真正价值：统一处理不同子类型");
        List<Channel> channels = new ArrayList<>();
        channels.add(new OnlineChannel(200.0));
        channels.add(new OfflineChannel("海淀路 1 号"));
        channels.add(new OnlineChannel(50.0));
        line("    List<Channel> 里装了 3 个不同子类型的对象");
        for (Channel ch : channels) {
            ch.process();                  // 一行代码搞定全部
        }
        line("    → 如果没有多态，这里要写 if-else 判断类型，很丑");
        line("");

        // ────────────────────────────────────────
        // ⑤ 向下转型：取子类特有属性
        // ────────────────────────────────────────
        line("⑤ 向下转型：拿回子类特有的属性");
        if (c1 instanceof OnlineChannel) {
            OnlineChannel oc = (OnlineChannel) c1;   // 强转后才能访问 amount
            System.out.println("      转成 OnlineChannel 后能读到 amount = " + oc.getAmount());
        }
        line("    → 注意：父类引用指向子类对象时，子类特有成员「看不见」");
        line("    → 用 instanceof 判断 + 强转才能取回，这就是向下转型");
        line("");

        // ────────────────────────────────────────
        // ⑥ 多态的代价：父类特有方法不可见
        // ────────────────────────────────────────
        line("⑥ 多态的代价");
        line("    c1.getAmount()  →  编译报错！因为 Channel 类型没有这个方法");
        line("    → 所以要向下转型后才能调用（Java 16+ 可用 pattern matching 简化）");
        line("");

        line("本关要点：");
        line("  · 多态 = 父类引用 + 子类对象 + 方法重写");
        line("  · 编译看左（决定能调什么），运行看右（决定执行什么）");
        line("  · 真实价值：统一处理多种子类型，少写 if-else");
        line("  · instanceof + 强转 = 向下转型，取子类特有属性");
        line("  · 面试话术：框架（Spring/MyBatis）大量依赖多态，靠代理对象实现不同行为");
    }

    static void line(String s) {
        System.out.println(s);
    }
}
