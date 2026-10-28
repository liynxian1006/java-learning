package oop;

/**
 * ════════════════════════════════════════════════════════════
 *  专题 · try-catch 逐行拆解（setter 抛异常后的「半初始化」陷阱）
 * ════════════════════════════════════════════════════════════
 *
 *  你贴的这段代码：
 *      GoodStudent good = new GoodStudent();
 *      try {
 *          svc.createWithGood(good, "", -5);
 *      } catch (IllegalArgumentException e) {
 *          line("    用 setter 塞入同样的数据：");
 *          line("      抛出异常：" + e.getMessage());
 *          line("      → 数据在入口就被挡住了，系统内部始终是干净的");
 *      }
 *      line("");
 *
 *  这段代码涉及 4 个知识点：
 *      ① try / catch / finally 的作用与执行顺序
 *      ② catch 只能接住【匹配类型】的异常
 *      ③ 异常抛出时，方法会【中断】—— 后续代码不执行
 *      ④ ★ 陷阱：对象处于「半初始化」状态（name 改了、age 没改）
 *
 *  【运行】
 *      cd java-learning/stage1
 *      javac -encoding UTF-8 oop/L21_ExceptionHandling.java
 *      java -Dfile.encoding=UTF-8 -cp . oop.L21_ExceptionHandling
 */
public class L21_ExceptionHandling {

    /** 校验严格的实体：setter 会在数据非法时抛异常 */
    static class GoodStudent {
        private String name;
        private int age;

        public String getName() { return name; }
        public int getAge() { return age; }

        public void setName(String name) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("姓名不能为空");
            }
            this.name = name.trim();
        }

        public void setAge(int age) {
            if (age < 0 || age > 150) {
                throw new IllegalArgumentException("年龄不合法: " + age);
            }
            this.age = age;
        }
    }

    /** 业务方法：先 setName，再 setAge */
    static class Service {
        void createWithGood(GoodStudent s, String name, int age) {
            s.setName(name);      // 第 1 步
            s.setAge(age);        // 第 2 步
        }
    }

    static void line(String s) {
        System.out.println(s);
    }

    public static void main(String[] args) {
        Service svc = new Service();

        // ══════════════════════════════════════════════
        // ① 逐行拆解你贴的那段代码
        // ══════════════════════════════════════════════
        line("① 逐行拆解");
        line("────────────────────────────────────────");
        line("  GoodStudent good = new GoodStudent();");
        line("    → 造一个空对象。此时 name = null，age = 0（int 默认值）");
        line("    → 注意：对象已经【存在】了，只是里面还是默认值");
        line("");
        line("  try {");
        line("    → \"我打算执行下面这段，可能会出问题\"");
        line("    → 编译器在这里开始【监控】异常");
        line("");
        line("      svc.createWithGood(good, \"\", -5);");
        line("    → 传进去的姓名是空串、年龄是 -5");
        line("    → 进入 createWithGood 后：");
        line("        第 1 步 s.setName(\"\") → 校验不通过 → 抛 IllegalArgumentException");
        line("        第 2 步 s.setAge(-5)  → ★ 根本【没执行到】");
        line("    → 异常一旦抛出，方法【立即中断】，后面的代码全部跳过");
        line("");
        line("  } catch (IllegalArgumentException e) {");
        line("    → 捕获这个特定类型的异常，把异常对象存进变量 e");
        line("    → e.getMessage() 就是异常里那句 \"姓名不能为空\"");
        line("    → 注意：能进入 catch，说明 setAge 那行【没跑】");
        line("");
        line("  line(\"\");");
        line("    → 这行在 try-catch 【外面】，无论有没有异常【都会执行】");
        line("    → 如果这行写在 try 块里，异常一抛它就被跳过了");
        line("");

        // ══════════════════════════════════════════════
        // ② ★ 陷阱：半初始化状态
        // ══════════════════════════════════════════════
        line("② ★ 核心陷阱：catch 之后，对象是「半初始化」的");
        line("────────────────────────────────────────");
        GoodStudent half = new GoodStudent();
        try {
            svc.createWithGood(half, "王五", -5);     // 姓名合法、年龄非法
        } catch (IllegalArgumentException e) {
            line("  传入 姓名=\"王五\"（合法）、年龄=-5（非法）：");
            line("    捕获异常：" + e.getMessage());
        }
        line("  ★ 此刻对象的状态：");
        line("      getName() = " + half.getName() + "   ← 【改成功了】");
        line("      getAge()  = " + half.getAge() + "    ← 【还是默认值 0，从没被赋值】");
        line("  → 也就是说：setName 成功了，setAge 根本没执行");
        line("  → 对象【不是\"没创建\"，也不是\"完整的\"，而是改了一半】");
        line("  → 如果业务代码 catch 之后【继续用这个对象】，就会拿到半成品");
        line("");
        line("  对比：姓名非法时（setName 就炸了）");
        GoodStudent half2 = new GoodStudent();
        try {
            svc.createWithGood(half2, "", 20);
        } catch (IllegalArgumentException e) {
            line("    捕获异常：" + e.getMessage());
        }
        line("      getName() = " + half2.getName() + "   ← 完全没改");
        line("      getAge()  = " + half2.getAge() + "    ← 完全没改");
        line("  → 这次是「完全没动」，因为第 1 步就炸了");
        line("");
        line("  ★ 实践建议：");
        line("    catch 之后【不要复用那个对象】，直接丢弃，重新 new 一个");
        line("    或者：先【全部校验】，全部通过后再赋值（先验证后写入）");
        line("");

        // ══════════════════════════════════════════════
        // ③ catch 只能接【匹配类型】的异常
        // ══════════════════════════════════════════════
        line("③ catch 只能接住【类型匹配】的异常");
        line("────────────────────────────────────────");
        try {
            try {
                String s = null;
                s.length();                    // 空指针 → NullPointerException
            } catch (IllegalArgumentException e) {
                line("    这里的 catch 没接住（类型不匹配）");
            }
        } catch (NullPointerException e) {
            line("    外层的 catch 接住了：NullPointerException");
            line("    → 结论：catch 写窄了，异常会【往上冒泡】直到有人接住");
            line("    → 如果最外层也没人接 → 程序终止 + 打印异常堆栈");
        }
        line("");

        // ══════════════════════════════════════════════
        // ④ 不写 catch 会怎样
        // ══════════════════════════════════════════════
        line("④ 如果连 catch 都不写会怎样？");
        line("────────────────────────────────────────");
        try {
            svc.createWithGood(new GoodStudent(), "", -5);
        } catch (IllegalArgumentException e) {
            line("    这里是演示用的 catch，避免程序直接崩掉");
            line("    真实场景下如果没有 catch，你会看到这样的东西：");
            line("    --------------------------------------------------");
            line("    Exception in thread \"main\" java.lang.IllegalArgumentException: 姓名不能为空");
            line("        at oop.L21_ExceptionHandling$GoodStudent.setName(L21_ExceptionHandling.java:38)");
            line("        at oop.L21_ExceptionHandling$Service.createWithGood(L21_ExceptionHandling.java:52)");
            line("        at oop.L21_ExceptionHandling.main(L21_ExceptionHandling.java:75)");
            line("    --------------------------------------------------");
            line("    → 「Exception in thread main」= 没人接住，程序直接终止");
            line("    → 堆栈信息从下往上读：main → createWithGood → setName（出事的地方）");
        }
        line("");

        // ══════════════════════════════════════════════
        // ⑤ finally：无论有没有异常都会执行
        // ══════════════════════════════════════════════
        line("⑤ finally：不管有没有异常，【一定】会执行");
        line("────────────────────────────────────────");
        try {
            svc.createWithGood(new GoodStudent(), "", -5);
        } catch (IllegalArgumentException e) {
            line("    catch 里执行了");
        } finally {
            line("    finally 里【也】执行了（哪怕上面抛了异常）");
            line("    → 典型用途：关连接、关文件、释放锁（这些必须做，不能因为异常就漏掉）");
        }
        line("");

        // ══════════════════════════════════════════════
        // ⑥ IllegalArgumentException 是「非受检异常」
        // ══════════════════════════════════════════════
        line("⑥ IllegalArgumentException 是【非受检异常】（unchecked）");
        line("────────────────────────────────────────");
        line("  · 继承链：IllegalArgumentException → RuntimeException → Exception");
        line("  · 编译器【不强制】你写 try-catch（不写也能编译通过）");
        line("  · 但业务代码通常还是会 catch，转成友好提示给前端");
        line("  · 这正是你在 Spring Boot 项目里 GlobalExceptionHandler 的作用：");
        line("    Controller 里直接抛 → 全局处理器统一转成 {\"code\":400,\"msg\":\"姓名不能为空\"}");
        line("  → 记住这条：参数不合法 → IllegalArgumentException（最常用）");
        line("      状态不对 → IllegalStateException");
        line("      找不到对象 → NoSuchElementException / 自定义 BusinessException");
        line("      空指针 → NullPointerException（说明你少判了 null）");
        line("");

        line("本专题要点：");
        line("  · try{可能出错的代码} catch(具体异常){处理} finally{必做的事}");
        line("  · catch 只能接【匹配类型】的异常，接不住就往上冒泡");
        line("  · 异常一抛，方法立刻中断，后续代码不执行");
        line("  · ★ 陷阱：对象可能处于「半初始化」状态，catch 后别复用");
        line("  · IllegalArgumentException 是非受检异常，编译器不强制 catch");
    }
}
