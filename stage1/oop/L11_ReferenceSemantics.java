package oop;

import java.util.Arrays;

/**
 * ════════════════════════════════════════════════════════════
 *  专题 · 引用变量的本质（Java 内存模型入门）
 * ════════════════════════════════════════════════════════════
 *
 *  【要讲透的三行代码】
 *      Student s3 = s1;      // 赋值的是「地址」，不是复制对象
 *      s3.age = 99;
 *      System.out.println(s1.age);   // → 99，s1 也变了
 *
 *  【核心结论】
 *   Java 有两类变量，存的东西完全不同：
 *     · 基本类型（int / double / char / boolean）→ 变量里【存值本身】
 *     · 引用类型（对象、数组、String）           → 变量里【存对象的地址】
 *
 *  【所以】
 *   Student s3 = s1;  →  复制了 s1 里那个地址 → 两个变量指向【同一个对象】
 *   s3.age = 99;      →  改的是那个对象      → s1 看到的是同一个对象 → 也变 99
 *
 *  【内存模型（简化版）】
 *      栈（方法栈帧）        堆
 *      ┌──────────┐        ┌─────────────────────┐
 *      │ s1: 0x1A │───────▶│  Student 对象        │
 *      │ s3: 0x1A │───────▶│  name/age/major     │
 *      └──────────┘        └─────────────────────┘
 *       两个变量存的地址都是 0x1A，所以指向同一个对象
 *
 *  【运行】右键本文件 → Run
 */
public class L11_ReferenceSemantics {

    static class Student {
        String name;
        int age;
        String major;

        Student(String name, int age, String major) {
            this.name = name;
            this.age = age;
            this.major = major;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public String toString() {
            return name + "(" + age + "岁)@" + Integer.toHexString(hashCode());
        }
    }

    public static void main(String[] args) {
        line("专题 · 引用变量的本质");
        line("");

        // ══════════════════════════════════════════════
        // ① 两类变量：存值 vs 存地址
        // ══════════════════════════════════════════════
        line("① Java 有两类变量，存的东西完全不同");

        int num = 10;                       // 基本类型：变量里存值本身
        int num2 = num;                     // 复制值
        num2 = 20;
        line("    int num = 10; int num2 = num; num2 = 20;");
        line("    num = " + num + "，num2 = " + num2);
        line("    → 基本类型：复制的是【值】，改 num2 不影响 num ✓");
        line("");

        Student s1 = new Student("张三", 21, "计算机");   // 引用类型：变量里存地址
        Student s2 = s1;                                   // 复制地址
        s2.age = 99;
        line("    Student s1 = new Student(...); Student s2 = s1; s2.age = 99;");
        line("    s1.age = " + s1.age + "，s2.age = " + s2.age);
        line("    → 引用类型：复制的是【地址】，改 s2 会影响 s1 ✗");
        line("");

        // ══════════════════════════════════════════════
        // ② 内存图
        // ══════════════════════════════════════════════
        line("② 内存里到底发生了什么");
        line("    栈（方法栈帧）              堆");
        line("    ┌───────────────┐          ┌──────────────────────┐");
        line("    │ s1 → 0x" + Integer.toHexString(System.identityHashCode(s1)) + "  │─────────▶│  Student 对象          │");
        line("    │ s2 → 0x" + Integer.toHexString(System.identityHashCode(s2)) + "  │─────────▶│  name = 张三           │");
        line("    └───────────────┘          │  age  = 99  ← 被改了  │");
        line("     两个变量存的地址相同          │  major= 计算机        │");
        line("     → 所以指向同一个对象          └──────────────────────┘");
        line("    s1 == s2 ? " + (s1 == s2) + "   ← == 比较的是地址");
        line("    s1.equals(s2) ? " + s1.equals(s2) + "  ← equals 默认也比地址");
        line("");

        // ══════════════════════════════════════════════
        // ③ 那怎么才能造出「互不影响」的两个对象？
        // ══════════════════════════════════════════════
        line("③ 怎么造出「互不影响」的两个对象");
        Student a = new Student("李四", 20, "软件工程");
        Student b = new Student("李四", 20, "软件工程");    // 各自 new
        b.age = 25;
        line("    a.age = " + a.age + "，b.age = " + b.age + "  → 互不影响 ✓");
        line("    a == b ? " + (a == b) + "  → 地址不同");
        line("    → 关键：必须 new 出【两个不同的对象】");
        line("");

        // ══════════════════════════════════════════════
        // ④ 三个常见误解
        // ══════════════════════════════════════════════
        line("④ 三个常见误解");
        line("    误解1：「Student s3 = s1 是复制了一份对象」");
        line("      → 错。复制的是地址，不是对象本身。");
        line("    误解2：「Java 有指针」");
        line("      → 不准确。Java 不暴露指针语法，但底层机制与 C++ 的引用类似，");
        line("        都是「变量里存地址」。");
        line("    误解3：「Java 是值传递」");
        line("      → 说法有歧义。准确说法：");
        line("        基本类型 → 值传递（复制值）");
        line("        引用类型 → 传的是【地址的副本】，所以表现为「共享对象」");
        line("");

        // ══════════════════════════════════════════════
        // ⑤ 方法传参的真相（面试高频）
        // ══════════════════════════════════════════════
        line("⑤ 方法传参的真相（面试高频）");
        Student x = new Student("王五", 19, "统计学");
        changeAge(x, 30);
        line("    changeAge(x, 30) 之后 x.age = " + x.age);
        line("    → 变了！因为方法里改的是【同一个对象】");
        line("");

        Student y = new Student("赵六", 22, "数学");
        replaceWithNewObject(y);
        line("    replaceWithNewObject(y) 之后 y.name = " + y.name);
        line("    → 没变！因为方法里是把【形参指向了新对象】，");
        line("      但形参和实参只是两个变量，改形参不影响实参的指向");
        line("");

        int n = 5;
        changeInt(n, 100);
        line("    changeInt(n, 100) 之后 n = " + n);
        line("    → 没变！基本类型传的是【值的副本】");
        line("");

        // ══════════════════════════════════════════════
        // ⑥ 数组同理
        // ══════════════════════════════════════════════
        line("⑥ 数组也一样：传的是地址");
        Student[] arr = {new Student("孙七", 21, "物理")};
        changeFirst(arr);
        line("    changeFirst(arr) 之后 arr[0].age = " + arr[0].age);
        line("    → 变了！数组变量里存的也是地址");
        line("");

        // ══════════════════════════════════════════════
        // ⑦ null 是什么
        // ══════════════════════════════════════════════
        line("⑦ null：引用变量的「空」状态");
        Student z = null;
        line("    Student z = null;");
        line("    z == null ? " + (z == null));
        line("    → null 表示「这个地址是空的」，不是「有个空对象」");
        line("    → 调 z.getName() 会抛 NullPointerException");
        try {
            z.getName();
        } catch (NullPointerException e) {
            line("    实际结果：抛 " + e.getClass().getSimpleName());
        }
        line("    → 所以 List.get(i) 之后要先判空再用");
        line("");

        line("本专题要点：");
        line("  · 基本类型变量存【值】，引用类型变量存【地址】");
        line("  · 赋值给引用类型 = 复制地址 → 两者共享同一个对象");
        line("  · 要独立对象必须各自 new");
        line("  · 方法传参：引用类型改内容会影响调用方，重新赋值不会");
        line("  · == 比地址，equals 默认也比地址（需重写才是比内容）");
        line("  · null 是「地址为空」，不是「空对象」");
    }

    /** 改对象内容 → 影响调用方 */
    static void changeAge(Student s, int newAge) {
        s.age = newAge;
    }

    /** 让形参指向新对象 → 不影响调用方的指向 */
    static void replaceWithNewObject(Student s) {
        s = new Student("新对象", 0, "无");
    }

    /** 基本类型传参 → 值副本，不影响调用方 */
    static void changeInt(int n, int newValue) {
        n = newValue;
    }

    /** 数组传参 → 传地址，改元素影响调用方 */
    static void changeFirst(Student[] arr) {
        arr[0].age = 88;
    }

    static void line(String s) {
        System.out.println(s);
    }
}
