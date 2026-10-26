package oop;

/**
 * ════════════════════════════════════════════════════════════
 *  专题 · this.name = name.trim();（this 的用法 + 参数遮蔽）
 * ════════════════════════════════════════════════════════════
 *
 *  【你问的代码】
 *      public void setName(String name) {
 *          this.name = name.trim();      // ← 这一行
 *      }
 *
 *  【左边和右边完全是两个东西】
 *      this.name   →  当前对象的【成员变量】（字段），存在堆里的对象上
 *      name        →  方法的【形参】（局部变量），存在栈帧里
 *
 *  【为什么必须写 this.】
 *      因为形参名和字段名【同名】，方法体内裸写 name 指的是【形参】。
 *      不写 this. 的话：
 *          name = name.trim();     → 把形参自己赋给自己，等于什么都没做！
 *          （Java 会直接编译报错：variable name might not have been initialized）
 *
 *  【this 的三大用途】
 *      ① 区分同名的字段和形参     this.name = name
 *      ② 调用本类的其它方法       this.calcAge()
 *      ③ 构造方法委托             this(参数)
 *
 *  【运行】右键本文件 → Run
 */
public class L19_ThisUsage {

    /** 演示字段与形参同名时的遮蔽（shadowing） */
    static class Student {
        private String name;         // 字段
        private int age;

        // 构造方法：形参 name 遮蔽了字段 name
        Student(String name, int age) {
            this.name = name;        // ← 左边字段，右边形参
            this.age = age;
        }

        // setter：同样遮蔽
        public void setName(String name) {
            this.name = name.trim(); // ← 左边字段，右边形参 + trim
        }

        public void setNameRaw(String name) {
            this.name = name;        // ← 不做 trim
        }

        public String getName() {
            return name;             // 这里没有形参遮蔽，name 就是字段
        }

        public int getAge() {
            return age;
        }
    }

    public static void main(String[] args) {
        line("专题 · this.name = name.trim();");
        line("");

        // ══════════════════════════════════════════════
        // ① 左字段、右形参
        // ══════════════════════════════════════════════
        line("① 左边和右边是两个不同的东西");
        line("    this.name   →  字段（堆里的对象上）");
        line("      name      →  形参（栈帧里）");
        line("    → 形参名和字段名【同名】时，方法体内裸写 name 指的是形参");
        line("      这叫【遮蔽 shadowing】");
        line("");

        // ══════════════════════════════════════════════
        // ② 为什么必须写 this.
        // ══════════════════════════════════════════════
        line("② 为什么必须写 this.");
        line("    如果漏写 this.：");
        line("        name = name.trim();");
        line("    → 左边是形参、右边也是形参 → 自我赋值，等于什么都没做！");
        line("    → Java 编译器会直接报错：");
        line("        variable 'name' might not have been initialized");
        line("    → 也就是说：漏写 this. 编译器会拦住你，不会静默出错");
        line("");

        // ══════════════════════════════════════════════
        // ③ 加上 trim() 的意义
        // ══════════════════════════════════════════════
        line("③ trim() 的作用：数据清洗");
        Student s = new Student("  张三  ", 21);
        line("    构造时不 trim → getName() = \"" + s.getName() + "\"  （带空格）");
        s.setName("  李四  ");
        line("    setter 里 trim → getName() = \"" + s.getName() + "\"  （干净了）");
        line("    → 作用：把用户输入的 \"  张三  \" 规范化成 \"张三\"，避免脏数据入库");
        line("");

        // ══════════════════════════════════════════════
        // ④ String 不可变：trim() 返回新对象
        // ══════════════════════════════════════════════
        line("④ 注意：String 是【不可变】的，trim() 返回的是新对象");
        String original = "  王五  ";
        String trimmed = original.trim();
        line("    original = \"" + original + "\"   （没变！）");
        line("    trimmed  = \"" + trimmed + "\"    （是新对象）");
        line("    original == trimmed ? " + (original == trimmed));
        line("    → 字符串不能被\"就地修改\"，所有看起来像修改的方法都返回新对象");
        line("    → 同理：replace / substring / toUpperCase 都是返回新对象");
        line("");

        // ══════════════════════════════════════════════
        // ⑤ this 的三大用途
        // ══════════════════════════════════════════════
        line("⑤ this 的三大用途");
        line("    ① 区分同名的字段和形参");
        line("        this.name = name;");
        line("    ② 调用本类的【其它】方法（this 可以省略，但写出来更清楚）");
        line("        this.calcAge();");
        line("    ③ 构造方法【委托】给本类另一个构造");
        line("        this(\"默认姓名\", 18);");
        line("    → 注意：this 不能在 static 方法里用（没有\"当前对象\"）");
        line("");

        // ══════════════════════════════════════════════
        // ⑥ 实战：this 让代码更清晰
        // ══════════════════════════════════════════════
        line("⑥ 实战建议：this 写上更清晰");
        line("    省略 this 的写法（能编译，但容易看错）：");
        line("        name = name.trim();        // 哪边是字段？哪边是参数？");
        line("    写上 this 的写法（推荐）：");
        line("        this.name = name.trim();   // 一眼看出左边是字段、右边是参数");
        line("    → 特别在【参数名和字段名相同】时，this 是必需的");
        line("    → 即使名字不同，写上 this 也能提升可读性（尤其在 setter 里）");
        line("");

        // ══════════════════════════════════════════════
        // ⑦ 常见变体：this 调本类方法
        // ══════════════════════════════════════════════
        line("⑦ this 的用法②：调用本类其它方法");
        Calculator calc = new Calculator();
        calc.setBase(100);
        line("    Calculator: base = " + calc.getBase()
                + "，result = " + calc.calc(3));
        line("    → calc() 内部用 this.base 取字段");
        line("");

        line("本专题要点：");
        line("  · this.name 是【字段】，name 是【形参】，两个不同的东西");
        line("  · 形参与字段同名 → 遮蔽，方法体内裸写名字指的是形参");
        line("  · 所以 setter 里必须写 this.name = name;（漏写编译器会报错）");
        line("  · trim() 返回【新】String（String 不可变）");
        line("  · this 三大用途：区分字段/形参、调本类方法、构造委托");
        line("  · this 不能在 static 方法里使用");
    }

    /** 演示 this 调本类方法 */
    static class Calculator {
        private int base;

        public void setBase(int base) {
            this.base = base;
        }

        public int getBase() {
            return base;
        }

        /** this 调本类其它方法 */
        public int calc(int factor) {
            return this.base * factor;
        }
    }

    static void line(String s) {
        System.out.println(s);
    }
}
