package oop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ════════════════════════════════════════════════════════════
 *  专题 · 数组/集合怎么批量创建对象
 * ════════════════════════════════════════════════════════════
 *
 *  【你的问题】
 *      空参构造的用途是「先造空对象再逐个赋值」，
 *      那数组能不能直接 students[1] = new Student("赵六", 22, "数学") 这样赋值好几个？
 *
 *  【答案：完全可以，而且这才是推荐写法】
 *      手动写代码时优先用【有参构造】一次填好，别用空参构造再逐个 setter。
 *      空参构造的真正价值在【框架反射】——MyBatis/Spring 是「反射 new 空对象 + setter 塞值」，
 *      框架没法知道你的有参构造长什么样（参数个数、顺序都可能变）。
 *
 *  【三种数组写法】
 *      ① 先 new 出长度，再逐个赋值
 *      ② new 时直接给全部元素
 *      ③ 最简写法（省掉 new 和长度）
 *
 *  【运行】右键本文件 → Run
 */
public class L10_ArrayInit {

    static class Student {
        private final String name;
        private final int age;
        private final String major;

        // 有参构造：手动创建时首选
        public Student(String name, int age, String major) {
            this.name = name;
            this.age = age;
            this.major = major;
        }

        // 空参构造：给框架用（反射 new 空对象 + setter 塞值）
        public Student() {
            this.name = null;
            this.age = 0;
            this.major = null;
        }

        public void setName(String name) {
            throw new UnsupportedOperationException("final 字段不能改");
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public String getMajor() {
            return major;
        }

        @Override
        public String toString() {
            return name + "(" + age + "岁," + major + ")";
        }
    }

    public static void main(String[] args) {
        line("专题 · 数组/集合怎么批量创建对象");
        line("");

        // ══════════════════════════════════════════════
        // ① 写法一：先 new 出长度，再逐个赋值
        // ══════════════════════════════════════════════
        line("① 写法一：先 new 出长度，再逐个赋值");
        Student[] s1 = new Student[3];           // 长度固定为 3，元素初始全是 null
        line("    Student[] s1 = new Student[3];");
        line("    刚创建时 s1[0] = " + s1[0] + "   ← 引用类型默认 null");
        s1[0] = new Student("张三", 21, "计算机科学与技术");
        s1[1] = new Student("李四", 20, "软件工程");
        s1[2] = new Student("王五", 19, "统计学");
        show(s1);
        line("    → 没问题，但要先知道数组长度");
        line("");

        // ══════════════════════════════════════════════
        // ② 写法二：new 时直接给全部元素
        // ══════════════════════════════════════════════
        line("② 写法二：new 时直接给全部元素");
        Student[] s2 = new Student[]{
                new Student("张三", 21, "计算机科学与技术"),
                new Student("李四", 20, "软件工程"),
                new Student("王五", 19, "统计学")
        };
        show(s2);
        line("    → 长度由元素个数决定，不用自己数");
        line("");

        // ══════════════════════════════════════════════
        // ③ 写法三：最简写法
        // ══════════════════════════════════════════════
        line("③ 写法三：最简写法（省掉 new 和长度）");
        Student[] s3 = {
                new Student("张三", 21, "计算机科学与技术"),
                new Student("李四", 20, "软件工程"),
                new Student("王五", 19, "统计学")
        };
        show(s3);
        line("    → 【推荐】最简洁，元素一眼看得清");
        line("");

        // ══════════════════════════════════════════════
        // ④ 两种写法完全等价
        // ══════════════════════════════════════════════
        line("④ 写法一 和 写法三 完全等价");
        line("    长度：      " + s1.length + " vs " + s3.length);
        line("    第2个元素： " + s1[1] + " vs " + s3[1]);
        line("    → 只是写法不同，运行时数据完全一样");
        line("");

        // ══════════════════════════════════════════════
        // ⑤ 集合的对应写法
        // ══════════════════════════════════════════════
        line("⑤ 集合的对应写法（实际项目更常用）");
        List<Student> list1 = new ArrayList<>();
        list1.add(new Student("张三", 21, "计算机科学与技术"));
        list1.add(new Student("李四", 20, "软件工程"));
        line("    List<Student> list1 = new ArrayList<>();  → 逐个 add");
        line("    元素数：" + list1.size());

        List<Student> list2 = Arrays.asList(
                new Student("张三", 21, "计算机科学与技术"),
                new Student("李四", 20, "软件工程"));
        line("    List.of(...) / Arrays.asList(...)             → 一次性给");
        line("    元素数：" + list2.size());
        line("    → 集合的好处：长度可变，可以 add / remove，数组做不到");
        line("");

        // ══════════════════════════════════════════════
        // ⑥ 数组的两个限制
        // ══════════════════════════════════════════════
        line("⑥ 数组的两个硬限制");
        line("    ① 长度创建后不能改：");
        line("        s1[3] = new Student(...);   → 数组越界异常 ArrayIndexOutOfBoundsException");
        try {
            s1[3] = new Student("越界", 20, "测试");
        } catch (ArrayIndexOutOfBoundsException e) {
            line("        实际结果：抛 " + e.getClass().getSimpleName());
        }
        line("    ② 不能 add：数组没有 add 方法，只有固定长度");
        line("    → 所以实际项目里，集合(ArrayList)比数组用得多");
        line("");

        // ══════════════════════════════════════════════
        // ⑦ 重要澄清：空参构造到底给谁用
        // ══════════════════════════════════════════════
        line("⑦ 重要澄清：空参构造到底给谁用");
        line("    【手动写代码】→ 用有参构造，一次填好，最简洁（上面①②③都是）");
        line("    【框架反射】  → 用空参构造，因为框架是这么干的：");
        line("        1) 反射 new Student()      ← 需要无参构造");
        line("        2) student.setName(值)     ← 需要 setter");
        line("        3) student.setAge(值)");
        line("        4) 返回这个对象");
        line("    框架【不知道】你的有参构造要几个参数、什么顺序，");
        line("    它只会用「无参构造 + setter」这套通用方式。");
        line("");

        line("本专题要点：");
        line("  · 数组可以直接批量赋值：new Student[]{...} 或最简的 {...}");
        line("  · 手动创建对象优先用【有参构造】，别先空对象再逐个 setter");
        line("  · 空参构造的真正价值是给框架（MyBatis/Spring/Jackson）反射用");
        line("  · 数组长度固定、不能 add；实际项目用 List 更好");
    }

    static void show(Student[] arr) {
        StringBuilder sb = new StringBuilder("    ");
        for (Student s : arr) {
            sb.append(s).append("  ");
        }
        System.out.println(sb);
    }

    static void line(String s) {
        System.out.println(s);
    }
}
