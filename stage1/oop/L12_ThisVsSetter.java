package oop;

/**
 * ════════════════════════════════════════════════════════════
 *  专题 · 构造方法里用 setXxx() 还是 this.xxx = xxx？
 * ════════════════════════════════════════════════════════════
 *
 *  【你问的两段代码】
 *      public Student(String name, int age, String major) {
 *          setName(name);        // 写法 A：走 setter
 *          setAge(age);
 *          setMajor(major);
 *      }
 *
 *      public Student(String name, int age, String major) {
 *          this.name = name;     // 写法 B：直接给字段赋值
 *          this.age = age;
 *          this.major = major;
 *      }
 *
 *  【一句话结论】
 *   最终字段值【完全一样】，但 setter 多了「执行逻辑」这一步。
 *   关键差别只有一个：【setter 里的代码会不会被执行】。
 *
 *  【四个实际差异（下面全部实测）】
 *   ① setter 里的校验会被触发，this. 赋值会绕过它
 *   ② setter 里的额外逻辑（转换、记录、通知）会被触发
 *   ③ 子类重写了 setter 时，构造方法里调用的是【子类版本】← 隐藏陷阱
 *   ④ final 字段只能用 this. 赋值，setter 编译报错
 *
 *  【怎么选】
 *   · 有校验要求的实体类（你的 L2、项目实体类）→ 走 setter
 *   · 简单的 POJO / 不可变对象（final 字段）→ 用 this. 赋值（更快、意图更明确）
 *
 *  【运行】右键本文件 → Run
 */
public class L12_ThisVsSetter {

    // ══════════════════════════════════════════════
    // ① 校验：setter 触发，this. 绕过
    // ══════════════════════════════════════════════
    static class ValidatedStudent {
        private String name;
        private int age;

        ValidatedStudent(String name, int age, boolean useSetter) {
            if (useSetter) {
                setName(name);            // 写法 A：会走校验
            } else {
                this.name = name;         // 写法 B：绕过校验
            }
            this.age = age;
        }

        public void setName(String name) {
            if (name == null || name.trim().isEmpty()) {
                System.out.println("      [setter] 校验不通过：姓名不能为空 → 字段保持 \"" + this.name + "\"");
                return;
            }
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    // ══════════════════════════════════════════════
    // ② setter 里的额外逻辑会被触发
    // ══════════════════════════════════════════════
    static class LoggedStudent {
        private String email;

        LoggedStudent(String email, boolean useSetter) {
            if (useSetter) {
                setEmail(email);
            } else {
                this.email = email;
            }
        }

        public void setEmail(String email) {
            // setter 里除了赋值，还做了别的事
            this.email = email == null ? null : email.trim().toLowerCase();
            System.out.println("      [setter] 已记录日志：设置邮箱 " + this.email);
        }

        public String getEmail() {
            return email;
        }
    }

    // ══════════════════════════════════════════════
    // ③ 陷阱：子类重写 setter
    // ══════════════════════════════════════════════
    static class Base {
        protected String code;

        Base(String code) {
            setCode(code);              // ← 构造方法里调 setter
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    static class Sub extends Base {
        private boolean validated;

        Sub(String code) {
            super(code);                 // → Base 构造方法里调的 setCode
        }

        @Override
        public void setCode(String code) {
            // 子类重写了 setter：加了额外逻辑
            this.validated = code != null && code.length() == 6;
            this.code = validated ? code : "INVALID";
        }

        public boolean isValidated() {
            return validated;
        }

        public String getCode() {
            return code;
        }
    }

    // ══════════════════════════════════════════════
    // ④ final 字段：只能用 this. 赋值
    // ══════════════════════════════════════════════
    static class ImmutableStudent {
        private final String name;
        private final int age;

        ImmutableStudent(String name, int age) {
            this.name = name;            // final 字段必须这样赋值
            this.age = age;
            // setName(name);            // ← 编译报错：无法给 final 字段赋值
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    public static void main(String[] args) {
        line("专题 · 构造方法里用 setXxx() 还是 this.xxx = xxx？");
        line("");

        // ══════════════════════════════════════════════
        // ① 校验差异
        // ══════════════════════════════════════════════
        line("① 差异一：setter 里的校验，this. 赋值会绕过");
        System.out.println("    用 setter 构造（姓名传空串）：");
        ValidatedStudent v1 = new ValidatedStudent("", 20, true);
        line("      结果 name = \"" + v1.getName() + "\"  ← 校验拦住了");

        System.out.println("    用 this. 构造（姓名传空串）：");
        ValidatedStudent v2 = new ValidatedStudent("", 20, false);
        line("      结果 name = \"" + v2.getName() + "\"  ← 校验被绕过了，脏数据进来了！");
        line("");

        // ══════════════════════════════════════════════
        // ② 额外逻辑差异
        // ══════════════════════════════════════════════
        line("② 差异二：setter 里的额外逻辑会不会执行");
        System.out.println("    用 setter 构造（邮箱 \"  ZHANG@ABC.COM  \"）：");
        LoggedStudent l1 = new LoggedStudent("  ZHANG@ABC.COM  ", true);
        line("      结果 email = \"" + l1.getEmail() + "\"  ← 走了 trim + toLowerCase + 日志");

        System.out.println("    用 this. 构造（同样的输入）：");
        LoggedStudent l2 = new LoggedStudent("  ZHANG@ABC.COM  ", false);
        line("      结果 email = \"" + l2.getEmail() + "\"  ← 原样保留，没转换也没日志");
        line("");

        // ══════════════════════════════════════════════
        // ③ 子类重写 setter 的陷阱
        // ══════════════════════════════════════════════
        line("③ 差异三（隐藏陷阱）：子类重写了 setter");
        Sub sub = new Sub("AB1234");
        line("    new Sub(\"AB1234\") 结果：");
        line("      code = " + sub.getCode() + "，validated = " + sub.isValidated());
        line("      → Base 构造方法里调的是【子类重写后的 setCode】");
        line("      → 父类构造还没跑完，子类的字段就已经被改了！");
        line("      → 这是 Java 构造顺序的经典坑（字段初始化在构造方法之后执行）");
        line("");

        // ══════════════════════════════════════════════
        // ④ final 字段
        // ══════════════════════════════════════════════
        line("④ 差异四：final 字段只能用 this. 赋值");
        ImmutableStudent im = new ImmutableStudent("张三", 21);
        line("    ImmutableStudent 用 this. 赋值 → 正常，name = " + im.getName());
        line("    如果改成 setName(name) → 编译报错：");
        line("        cannot assign a value to final variable name");
        line("    → 不可变对象（不可变对象 = 不可变类）只能用 this. 赋值");
        line("");

        // ══════════════════════════════════════════════
        // ⑤ 结论与选择
        // ══════════════════════════════════════════════
        line("⑤ 怎么选？");
        line("    【走 setter】当且仅当 setter 里有【必须执行】的逻辑：");
        line("        · 参数校验（拦非法数据）");
        line("        · 数据转换（trim / toLowerCase / 格式化）");
        line("        · 派生字段联动（改一个字段，另一个跟着变）");
        line("        · 触发事件/通知");
        line("    【用 this.】当字段是 final，或 setter 只是单纯的赋值：");
        line("        · 性能更好（省一次方法调用）");
        line("        · 意图更明确（明确表示「就是赋这个值」）");
        line("");
        line("    你的项目现状：");
        line("        L2 教学版      → 走 setter（有校验，正确的示范）");
        line("        项目二/三实体类 → 用 this.（字段是普通字段、无校验）");
        line("        → 两种都对，取决于该类有没有校验逻辑");
        line("");
        line("    ⚠ 最佳实践：构造方法里【不要】调用可被重写的 setter");
        line("      （更安全的做法是让父类构造方法不调 setter，子类构造后自己赋值）");
    }

    static void line(String s) {
        System.out.println(s);
    }
}
