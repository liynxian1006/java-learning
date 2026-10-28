package oop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * ════════════════════════════════════════════════════════════
 *  专题 · equals 与 hashCode（对象「相等」的本质）
 * ════════════════════════════════════════════════════════════
 *
 *  你项目里的 Student / Job / Application 三个实体都只写了 toString，
 *  没有重写 equals 和 hashCode —— 这是一个真实存在的坑。
 *  本专题讲清楚：为什么必须重写、怎么写、写了之后有什么坑。
 *
 *  本专题 9 个板块：
 *      ① == 和 equals 到底比什么
 *      ② 不重写 equals 的三大后果（Set / List / Map 全废）
 *      ③ equals 的 5 条契约
 *      ④ hashCode 的契约
 *      ⑤ HashSet 内部到底怎么找元素的
 *      ⑥ 正确写法（Objects.equals + Objects.hash）
 *      ⑦ ★ 坑：可变字段放进 HashMap 后 key 失效
 *      ⑧ 两个典型错误写法（破坏对称性 / 破坏 hashCode 契约）
 *      ⑨ 该用 id 还是业务字段判断相等
 *
 *  【运行】
 *      cd java-learning/stage1
 *      javac -encoding UTF-8 oop/L23_EqualsHashCode.java
 *      java -Dfile.encoding=UTF-8 -cp . oop.L23_EqualsHashCode
 */
public class L23_EqualsHashCode {

    // ══════════════════════════════════════════════
    // 反面：没重写 equals/hashCode 的实体（= 你项目现在的样子）
    // ══════════════════════════════════════════════
    static class RawStudent {
        private Integer id;
        private String name;
        private String major;

        RawStudent(Integer id, String name, String major) {
            this.id = id;
            this.name = name;
            this.major = major;
        }

        public Integer getId() { return id; }
        public String getName() { return name; }
        public String getMajor() { return major; }
        public void setName(String name) { this.name = name; }

        @Override
        public String toString() {
            return "RawStudent{id=" + id + ", name='" + name + "', major='" + major + "'}";
        }
    }

    // ══════════════════════════════════════════════
    // 正面：重写了 equals/hashCode 的实体
    // ══════════════════════════════════════════════
    static class Student {
        private Integer id;
        private String name;
        private String major;

        Student(Integer id, String name, String major) {
            this.id = id;
            this.name = name;
            this.major = major;
        }

        public Integer getId() { return id; }
        public String getName() { return name; }
        public String getMajor() { return major; }
        public void setName(String name) { this.name = name; }

        /**
         * equals：判断「业务上是不是同一个对象」
         * 标准写法四要素：
         *   ① this == o → 同一引用，直接 true
         *   ② o == null → false
         *   ③ getClass() != o.getClass() → false（类型不同直接 false）
         *   ④ 逐个字段比较
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Student other = (Student) o;
            return Objects.equals(id, other.id)
                    && Objects.equals(name, other.name)
                    && Objects.equals(major, other.major);
        }

        /**
         * hashCode：必须和 equals 保持一致
         * 用【参与 equals 比较的字段】来算，否则会破坏契约。
         */
        @Override
        public int hashCode() {
            return Objects.hash(id, name, major);
        }

        @Override
        public String toString() {
            return "Student{id=" + id + ", name='" + name + "', major='" + major + "'}";
        }
    }

    // ══════════════════════════════════════════════
    // 错误示范类
    // ══════════════════════════════════════════════

    /** 破坏【对称性】：对 String 返回 true，但 String 不认你 */
    static class BadSymmetry {
        private final int id;

        BadSymmetry(int id) { this.id = id; }

        @Override
        public boolean equals(Object o) {
            if (o instanceof BadSymmetry) {
                return id == ((BadSymmetry) o).id;
            }
            if (o instanceof String) {
                return true;             // ★ 错误：单方面承认相等
            }
            return false;
        }
    }

    /** 破坏【hashCode 契约】：equals 只比 name，hashCode 却把 id 也算进去 */
    static class BadHash {
        private final String name;
        private final int id;

        BadHash(String name, int id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof BadHash && name.equals(((BadHash) o).name);
        }

        @Override
        public int hashCode() {
            return id;                   // ★ 错误：把不参与 equals 的字段算进 hashCode
        }
    }

    static void line(String s) {
        System.out.println(s);
    }

    public static void main(String[] args) {
        // ══════════════════════════════════════════════
        // ① == 和 equals 到底比什么
        // ══════════════════════════════════════════════
        line("① == 和 equals 到底比什么");
        line("────────────────────────────────────────");
        RawStudent a = new RawStudent(1, "张三", "计算机");
        RawStudent b = new RawStudent(1, "张三", "计算机");
        line("  a = " + a);
        line("  b = " + b);
        line("  a == b           → " + (a == b) + "   （比【引用/地址】：两个不同对象 → false）");
        line("  a.equals(b)      → " + a.equals(b) + "   （比【内容】：没重写 equals → 其实是比引用 → false）");
        line("  \"张三\".equals(\"张三\") → " + "张三".equals("张三") + "   （String 重写了 equals → 比内容）");
        line("");
        line("  ★ 关键结论：");
        line("    没重写 equals 的类，equals 和 == 行为【完全一样】（都比引用）");
        line("    只有重写了 equals，equals 才有「业务相等」的意义");
        line("");

        // ══════════════════════════════════════════════
        // ② 不重写 equals 的三大后果
        // ══════════════════════════════════════════════
        line("② 不重写 equals 的三大后果（你项目现在就是这个状态）");
        line("────────────────────────────────────────");

        // 后果 1：Set 去重失效
        Set<RawStudent> rawSet = new HashSet<>();
        rawSet.add(new RawStudent(1, "张三", "计算机"));
        rawSet.add(new RawStudent(1, "张三", "计算机"));
        line("  后果1  Set 去重失效：");
        line("    往 HashSet 里 add 两个【内容完全相同】的学生");
        line("    结果 size = " + rawSet.size() + "  ← 应该是 1，实际是 2！");
        line("    → 因为 HashSet 内部是拿 hashCode 定位、拿 equals 比较的，");
        line("      没重写 → 两个对象被当成【两个人】，重复数据进库后统计全乱");
        line("");

        // 后果 2：List.contains 失效
        List<RawStudent> rawList = new ArrayList<>();
        rawList.add(new RawStudent(1, "张三", "计算机"));
        line("  后果2  List.contains() 失效：");
        line("    list.contains(同内容的另一个对象) → " + rawList.contains(new RawStudent(1, "张三", "计算机")));
        line("    → 明明 list 里有这个学生，却说「没有」");
        line("");

        // 后果 3：Map 的 key 失效
        Map<RawStudent, String> rawMap = new HashMap<>();
        rawMap.put(new RawStudent(1, "张三", "计算机"), "张三的简历");
        line("  后果3  Map 的 key 失效：");
        line("    map.get(同内容的另一个对象) → " + rawMap.get(new RawStudent(1, "张三", "计算机")));
        line("    → 用对象当 key 存进去，用「看起来一样」的对象取不回来");
        line("");

        // 重写之后，同样的三件事
        Set<Student> goodSet = new HashSet<>();
        goodSet.add(new Student(1, "张三", "计算机"));
        goodSet.add(new Student(1, "张三", "计算机"));
        line("  重写 equals/hashCode 之后，同样的三件事：");
        line("    Set.size()               = " + goodSet.size() + "  ← 正确去重");
        List<Student> goodList = new ArrayList<>();
        goodList.add(new Student(1, "张三", "计算机"));
        line("    List.contains(同内容对象) = " + goodList.contains(new Student(1, "张三", "计算机")) + "  ← 找得到");
        Map<Student, String> goodMap = new HashMap<>();
        goodMap.put(new Student(1, "张三", "计算机"), "张三的简历");
        line("    Map.get(同内容对象)       = " + goodMap.get(new Student(1, "张三", "计算机")) + "  ← 取得到");
        line("");

        // ══════════════════════════════════════════════
        // ③ equals 的 5 条契约
        // ══════════════════════════════════════════════
        line("③ equals 的 5 条契约（面试会问，背下来）");
        line("────────────────────────────────────────");
        Student x = new Student(1, "张三", "计算机");
        Student y = new Student(1, "张三", "计算机");
        Student z = new Student(2, "李四", "软件工程");
        line("  ① 自反性：x.equals(x) 必须为 true");
        line("       实测 → " + x.equals(x));
        line("  ② 对称性：x.equals(y) 必须 == y.equals(x)");
        line("       实测 → " + x.equals(y) + " / " + y.equals(x));
        line("  ③ 传递性：x.equals(y) && y.equals(z) → x.equals(z)");
        line("       实测 → " + (x.equals(y) && y.equals(z)) + " / " + x.equals(z));
        line("  ④ 一致性：只要没修改字段，重复调用结果必须一样");
        line("       实测 → " + x.equals(y) + " / " + x.equals(y) + " / " + x.equals(y));
        line("  ⑤ 与 null 比较：x.equals(null) 必须是 false（不能抛 NPE）");
        line("       实测 → " + x.equals(null));
        line("  ⑥ 与其他类型比较：x.equals(\"字符串\") 必须是 false（不能抛 ClassCastException）");
        line("       实测 → " + x.equals("张三"));
        line("");

        // ══════════════════════════════════════════════
        // ④ hashCode 的契约
        // ══════════════════════════════════════════════
        line("④ hashCode 的契约");
        line("────────────────────────────────────────");
        line("  规则：equals 相等的两个对象，hashCode 必须【相等】");
        line("  反过来不要求：hashCode 相等的，不一定 equals 相等（允许哈希冲突）");
        line("  为什么？→ 因为 HashMap/HashSet 先用 hashCode 定位桶，");
        line("              再用 equals 判断是不是同一个。hashCode 不一致 → 根本不在一个桶里，equals 都没机会比。");
        line("");
        line("  实测（Student 是正确写法）：");
        line("    x.hashCode() = " + x.hashCode() + "，y.hashCode() = " + y.hashCode()
                + "  → 相等？ " + (x.hashCode() == y.hashCode()));
        line("    z.hashCode() = " + z.hashCode() + "  → 与 x 不同（内容不同，hash 多半也不同）");
        line("");

        // ══════════════════════════════════════════════
        // ⑤ HashSet 内部怎么找元素
        // ══════════════════════════════════════════════
        line("⑤ HashSet 内部到底怎么找元素（一张图讲清）");
        line("────────────────────────────────────────");
        line("  add(o) 的流程：");
        line("    ① 算 o.hashCode() → h");
        line("    ② 找到桶下标：h & (数组长度-1)");
        line("    ③ 如果桶是空 → 直接放进去");
        line("    ④ 如果桶里有东西 → 逐个调【它们的 equals(o)】");
        line("         · 有 equals 返回 true → 认为是重复，【不添加】");
        line("         · 全都不等 → 添加到桶里（链表/红黑树）");
        line("");
        line("  所以：hashCode 决定「去哪个桶找」，equals 决定「桶里到底是不是它」");
        line("  实测两个内容相同的 Student：");
        line("    hashCode 相同？ " + (x.hashCode() == y.hashCode()) + "  → 会进同一个桶 → 再用 equals 确认 → 判定为重复");
        line("");

        // ══════════════════════════════════════════════
        // ⑥ 正确写法
        // ══════════════════════════════════════════════
        line("⑥ 正确写法：Objects.equals + Objects.hash");
        line("────────────────────────────────────────");
        line("  @Override");
        line("  public boolean equals(Object o) {");
        line("      if (this == o) return true;");
        line("      if (o == null || getClass() != o.getClass()) return false;");
        line("      Student other = (Student) o;");
        line("      return Objects.equals(id, other.id)");
        line("          && Objects.equals(name, other.name)");
        line("          && Objects.equals(major, other.major);");
        line("  }");
        line("");
        line("  @Override");
        line("  public int hashCode() {");
        line("      return Objects.hash(id, name, major);   // 字段顺序和 equals 保持一致");
        line("  }");
        line("");
        line("  ★ 为什么用 Objects.equals(a, b) 而不是 a.equals(b)？");
        line("    因为 a 可能是 null → a.equals(b) 会 NPE，Objects.equals(a, b) 内部做了判空。");
        line("  ★ 为什么用 Objects.hash(...)？");
        line("    它内部就是 Arrays.hashCode(new Object[]{a, b, c})，");
        line("    自动处理 null，比手写 `31 * h + (a == null ? 0 : a.hashCode())` 省事且不易写错。");
        line("  ★ IDEA 里可以直接生成：右键文件 → Generate → equals and hashCode。");
        line("  ★ 用 Lombok 的话：@Data 或 @EqualsAndHashCode 就自动生成。");
        line("");

        // ══════════════════════════════════════════════
        // ⑦ ★ 可变字段的坑
        // ══════════════════════════════════════════════
        line("⑦ ★ 经典大坑：对象放进 HashMap 后改了字段，key 就失效了");
        line("────────────────────────────────────────");
        Student key = new Student(1, "张三", "计算机");
        Map<Student, String> map = new HashMap<>();
        map.put(key, "张三的简历");
        line("  put 之后：");
        line("    map.get(key)                    → " + map.get(key) + "   ← 能取到");
        line("    map.get(新对象 同内容)          → " + map.get(new Student(1, "张三", "计算机")) + "   ← 也能取到");
        line("");
        key.setName("李四");     // ★ 改了参与 equals/hashCode 的字段
        line("  执行 key.setName(\"李四\") 之后：");
        line("    key.getName() = " + key.getName());
        line("    map.get(key)                    → " + map.get(key) + "   ← ★ 取不到了！");
        line("    map.get(new Student(1, \"张三\", \"计算机\")) → " + map.get(new Student(1, "张三", "计算机")) + "   ← 也取不到（key 成了孤儿）");
        line("  原因：hashCode 是根据【当前字段】实时算出来的，字段一改，桶位置就变了 ——");
        line("        它还挂在旧桶的链表上，但按新 hashCode 去找，连旧桶都进不去");
        line("  → 结论：");
        line("     · 放进 HashMap/HashSet 的 key，【字段就不能再改】");
        line("     · 或者：参与 equals/hashCode 的字段全部设成 final（构造后不可变）");
        line("     · 你的 Student 实体如果参与 Map key，建议 id/name 用 final 或加注释说明不可变");
        line("");

        // ══════════════════════════════════════════════
        // ⑧ 两个典型错误写法
        // ══════════════════════════════════════════════
        line("⑧ 两个典型错误写法");
        line("────────────────────────────────────────");
        line("  错误1：破坏对称性");
        BadSymmetry bs1 = new BadSymmetry(1);
        String str = "我是字符串";
        line("    bs1.equals(str) = " + bs1.equals(str));
        line("    str.equals(bs1) = " + str.equals(bs1) + "   ← 两者结果【不一致】 → 破坏对称性");
        line("    → 后果：某些集合的行为会变得不可预测");
        line("");
        line("  错误2：破坏 hashCode 契约");
        BadHash bh1 = new BadHash("张三", 1);
        BadHash bh2 = new BadHash("张三", 2);
        Set<BadHash> badSet = new HashSet<>();
        badSet.add(bh1);
        badSet.add(bh2);
        line("    bh1.equals(bh2) = " + bh1.equals(bh2) + "   ← 它们「相等」");
        line("    但 hashCode：   " + bh1.hashCode() + " vs " + bh2.hashCode() + "   ← 不相等，违反契约");
        line("    放进 HashSet → size = " + badSet.size() + "  ← 明明相等却存了 2 份（去重失效）");
        line("    → 原因：hashCode 不同 → 进了不同的桶 → equals 根本没机会比");
        line("");

        // ══════════════════════════════════════════════
        // ⑨ 用 id 还是业务字段
        // ══════════════════════════════════════════════
        line("⑨ 该用 id 还是业务字段判断相等？");
        line("────────────────────────────────────────");
        line("  两种写法都正确，但适用场景不同：");
        line("");
        line("  写法A：用数据库主键 id（★ 你的实体类推荐用这个）");
        line("    @Override");
        line("    public boolean equals(Object o) {");
        line("        if (this == o) return true;");
        line("        if (o == null || getClass() != o.getClass()) return false;");
        line("        Student other = (Student) o;");
        line("        return id != null && id.equals(other.id);");
        line("    }");
        line("    @Override");
        line("    public int hashCode() { return Objects.hash(id); }");
        line("    优点：id 唯一、稳定（改姓名不影响相等性）");
        line("    注意：id 为 null（还没入库）的两个对象【不相等】——这是合理的，");
        line("          因为它们本来就不是同一条数据库记录");
        line("");
        line("  写法B：用业务唯一字段（比如「姓名 + 专业」）");
        line("    适合：DTO、查询条件对象、Java 16 的 record");
        line("    风险：如果业务上允许重名，就必须再加别的字段");
        line("");
        line("  你的项目建议：");
        line("    · 实体类（Student/Job/Application）→ 用 id");
        line("    · 查询 DTO → 用业务字段");
        line("");

        line("本专题要点：");
        line("  · == 比引用，equals 比内容（但要先重写才有内容可言）");
        line("  · 不重写 = Set 去重失效、List.contains 失效、Map key 失效");
        line("  · equals 5 条契约：自反、对称、传递、一致、与 null/异类型安全");
        line("  · hashCode 契约：equals 相等 ⇒ hashCode 必须相等");
        line("  · HashSet 先 hash 定位桶，再 equals 定身份");
        line("  · 写法：Objects.equals + Objects.hash，或用 IDEA 生成 / Lombok @Data");
        line("  · ★ 作为 Map key 的对象，字段改了 key 就失效");
        line("  · 实体类用 id 判断相等，DTO 用业务字段");
    }
}
