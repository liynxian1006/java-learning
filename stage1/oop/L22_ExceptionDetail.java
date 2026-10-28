package oop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * ════════════════════════════════════════════════════════════
 *  专题 · Java 异常处理全景详解（面试够用版）
 * ════════════════════════════════════════════════════════════
 *
 *  本专题 12 个板块：
 *      ① 异常体系树（Throwable → Error / Exception）
 *      ② 异常的生命周期（抛出 → 传播 → 捕获 → 处理）
 *      ③ 栈追踪（Stack Trace）怎么读
 *      ④ 受检异常 vs 非受检异常（编译器行为的真实差别）
 *      ⑤ 多 catch 的顺序规则
 *      ⑥ multi-catch（一个 catch 接多个异常）
 *      ⑦ finally 的三大坑
 *      ⑧ try-with-resources（自动关资源 + 异常抑制）
 *      ⑨ 异常在项目里的传播链路（Dao → Service → 全局处理器）
 *      ⑩ 自定义业务异常（带 code）
 *      ⑪ 常见异常速查表（每一个都真的抛一次）
 *      ⑫ 实践清单
 *
 *  【运行】
 *      cd java-learning/stage1
 *      javac -encoding UTF-8 oop/L22_ExceptionDetail.java
 *      java -Dfile.encoding=UTF-8 -cp . oop.L22_ExceptionDetail
 */
public class L22_ExceptionDetail {

    // ══════════════════════════════════════════════
    // 准备：自定义异常 + 业务链路
    // ══════════════════════════════════════════════

    /** 自定义业务异常：非受检（继承 RuntimeException），携带业务状态码 */
    static class MyBusinessException extends RuntimeException {
        private final int code;

        public MyBusinessException(int code, String msg) {
            super(msg);              // 调用父类构造，把 msg 交给 Throwable
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    /** 自定义受检异常：继承 Exception，编译器会强制你处理 */
    static class MyCheckedException extends Exception {
        public MyCheckedException(String msg) {
            super(msg);
        }
    }

    /** 受检异常的典型场景：方法声明了 throws，调用方必须 catch 或继续声明 */
    static void readFile() throws MyCheckedException {
        throw new MyCheckedException("受检异常：我必须被处理");
    }

    /** 业务链路：Dao 抛 → Service 不管 → Controller 接住 */
    static class Dao {
        void insert(String name) {
            if ("张三".equals(name)) {
                throw new MyBusinessException(409, "姓名已存在：" + name);
            }
        }
    }

    static class Service {
        private final Dao dao = new Dao();

        void create(String name) {
            dao.insert(name);      // 不 catch —— 让异常【继续往上抛】
        }
    }

    static class Controller {
        private final Service service = new Service();

        /** 这就是 GlobalExceptionHandler 在干的事 */
        String handle(String name) {
            try {
                service.create(name);
                return "{\"code\":200,\"msg\":\"成功\"}";
            } catch (MyBusinessException e) {
                return "{\"code\":" + e.getCode() + ",\"msg\":\"" + e.getMessage() + "\"}";
            }
        }
    }

    // ══════════════════════════════════════════════
    // 演示用的资源类
    // ══════════════════════════════════════════════

    /** 关闭时会抛异常的 AutoCloseable，用来演示【异常抑制】 */
    static class BrokenResource implements AutoCloseable {
        private final String name;

        BrokenResource(String name) {
            this.name = name;
        }

        @Override
        public void close() {
            throw new IllegalStateException("关闭 [" + name + "] 时出错");
        }
    }

    static void line(String s) {
        System.out.println(s);
    }

    public static void main(String[] args) {
        // ══════════════════════════════════════════════
        // ① 异常体系树
        // ══════════════════════════════════════════════
        line("① 异常体系树");
        line("────────────────────────────────────────");
        line("  java.lang.Throwable  ← 所有异常的【根】");
        line("    ├── Error            【错误】：JVM 级问题，程序【不该】catch");
        line("    │     OutOfMemoryError（内存溢出）、StackOverflowError（栈溢出）");
        line("    └── Exception         【异常】：程序级问题，【应该】catch");
        line("          ├── RuntimeException            ← 非受检（unchecked）");
        line("          │     NullPointerException、IllegalArgumentException、");
        line("          │     IllegalStateException、IndexOutOfBoundsException、");
        line("          │     ArithmeticException、ClassCastException...");
        line("          └── 其他具体异常                 ← 受检（checked）");
        line("                IOException、SQLException、CloneNotSupportedException...");
        line("");
        line("  记忆法：Exception 的子类，除了 RuntimeException 那一片，");
        line("        其余基本都是受检异常。");
        line("  ★ Error 和 Exception 都继承 Throwable，Throwable 也有第三种用法：");
        line("    直接 new Throwable()（不推荐，Exception 就够了）");
        line("");

        // ══════════════════════════════════════════════
        // ② 异常的生命周期
        // ══════════════════════════════════════════════
        line("② 异常的生命周期：抛出 → 传播 → 捕获 → 处理");
        line("────────────────────────────────────────");
        line("  1) 抛出 throw：制造异常对象并交给 JVM");
        line("       throw new IllegalArgumentException(\"姓名不能为空\");");
        line("  2) 传播：JVM 沿着【调用栈】往上找能接住它的 catch");
        line("       找不到 → 一路抛到 main → 打印堆栈 → 程序终止");
        line("  3) 捕获 catch：类型匹配 → 抓住，对象塞进变量 e");
        line("  4) 处理：可以记录、可以补救、可以【继续往上抛】(rethrow)");
        line("");
        line("  实测：看它是怎么一级一级往上冒的");
        try {
            level3();
        } catch (IllegalStateException e) {
            line("  → 在 main 里接住了：" + e.getMessage());
        }
        line("");

        // ══════════════════════════════════════════════
        // ③ 栈追踪怎么读
        // ══════════════════════════════════════════════
        line("③ 栈追踪（Stack Trace）怎么读");
        line("────────────────────────────────────────");
        line("  我在 level3 里真的抛一个异常，看真实输出：");
        line("  --------------------------------------------------");
        try {
            level1();
        } catch (IllegalStateException e) {
            StackTraceElement[] st = e.getStackTrace();
            for (int i = 0; i < st.length && i < 5; i++) {
                line("    at " + st[i]);
            }
        }
        line("  --------------------------------------------------");
        line("  注意这几行是【一层一层嵌套】的：main → level1 → level2 → level3");
        line("  读法：【从下往上】看 —— 最下面那行是出事的地方（最内层调用），");
        line("        往上依次是「谁调用了它」；最上面是 main。");
        line("  定位 bug 时：找到【最上面那个属于你自己代码】的帧，那行就是出错位置。");
        line("  实战命令：java -ea 打开断言；IDE 里点堆栈链接直接跳到源码行。");
        line("");

        // ══════════════════════════════════════════════
        // ④ 受检 vs 非受检
        // ══════════════════════════════════════════════
        line("④ 受检异常 vs 非受检异常（★ 面试高频）");
        line("────────────────────────────────────────");
        line("  非受检（RuntimeException 及其子类）：");
        line("    · 编译器【不强制】你处理，不写 try-catch 也能编译通过");
        line("    · 通常代表【程序逻辑错误】（空指针、越界、参数非法）");
        line("    · 处理方式：在【能补救的最近层】catch，或者干脆不处理让它崩");
        line("");
        line("  受检（除 RuntimeException 外的 Exception 子类）：");
        line("    · 编译器【强制】你 catch 或在方法签名上声明 throws");
        line("    · 通常代表【外部环境问题】（文件不存在、网络超时、SQL 语法错）");
        line("    · 写起来「烦」，但好处是：不会忘");
        line("");
        line("  实测：readFile() 声明了 throws MyCheckedException，");
        line("        如果调用方既不 catch 也不声明 throws，【编译器直接报错】。");
        line("        我用反射绕过编译期检查，模拟「如果编译器不管会怎样」：");
        try {
            Method m = L22_ExceptionDetail.class.getDeclaredMethod("readFile");
            m.setAccessible(true);
            m.invoke(null);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            line("    反射调用 → 运行时抛出：" + cause.getClass().getName());
            line("    原因：" + cause.getMessage());
            line("    → 非受检异常（RuntimeException）就是这种「没人管就直接终止」的效果；");
            line("    → 受检异常的区别是：编译器会在你【写代码时】就拦下你。");
        } catch (NoSuchMethodException | IllegalAccessException e) {
            line("    反射失败：" + e.getMessage());
        }
        line("");

        // ══════════════════════════════════════════════
        // ⑤ 多 catch 的顺序规则
        // ══════════════════════════════════════════════
        line("⑤ 多个 catch 的顺序规则");
        line("────────────────────────────────────────");
        line("  规则：【子类异常必须写在父类异常前面】");
        line("  正确：");
        line("    try { ... }");
        line("    catch (IllegalArgumentException e) { ... }   // 子类，先写");
        line("    catch (RuntimeException e) { ... }            // 父类，后写");
        line("");
        line("  错误（编译器报错 unreachable）：");
        line("    catch (RuntimeException e) { ... }            // 父类先写");
        line("    catch (IllegalArgumentException e) { ... }   // 子类后写 → 永远走不到");
        line("  → 因为父类能接住所有子类异常，先写父类就把子类也接走了");
        line("");
        line("  实测（正确顺序）：");
        try {
            throw new IllegalArgumentException("我是子类异常");
        } catch (IllegalArgumentException e) {
            line("    被【第一个】catch 接住：" + e.getMessage());
        } catch (RuntimeException e) {
            line("    被第二个 catch 接住（这次不会走到）");
        }
        line("");

        // ══════════════════════════════════════════════
        // ⑥ multi-catch
        // ══════════════════════════════════════════════
        line("⑥ multi-catch：一个 catch 接多个【无关】的异常");
        line("────────────────────────────────────────");
        line("  catch (IllegalArgumentException | IllegalStateException e) { ... }");
        line("  · Java 7 引入，用 | 分隔");
        line("  · 多个类型之间不能有【继承关系】（有的话前面的就包含了后面的，编译器报错）");
        line("  · 变量 e 的类型是它们的【共同父类】");
        line("");
        try {
            throw new IllegalStateException("我被 multi-catch 接住了");
        } catch (IllegalArgumentException | IllegalStateException e) {
            line("    multi-catch 接住：" + e.getMessage());
            line("    e 的类型是 RuntimeException：" + (e instanceof RuntimeException));
        }
        line("");

        // ══════════════════════════════════════════════
        // ⑦ finally 的三大坑
        // ══════════════════════════════════════════════
        line("⑦ finally 的三大坑（★ 面试爱问）");
        line("────────────────────────────────────────");

        line("  坑1：try 里 return 了，finally 照样执行");
        line("    返回值 = " + returnWithFinally());
        line("    → 顺序是：先算返回值 → 执行 finally → 再真正 return");
        line("");

        line("  坑2：finally 里的 return 会【覆盖】前面的返回值，并吞掉异常");
        line("    返回值 = " + swallowByFinally());
        line("    → 原始异常被彻底丢弃，连堆栈都没了（最危险的一种）");
        line("");

        line("  坑3：try 里 return 后，finally 里的语句仍会改写共享状态");
        line("    → 所以 finally 里【不要写 return】，也不要覆盖业务变量");
        line("");

        // ══════════════════════════════════════════════
        // ⑧ try-with-resources
        // ══════════════════════════════════════════════
        line("⑧ try-with-resources：自动关资源 + 异常抑制");
        line("────────────────────────────────────────");
        line("  老写法（Java 6 风格，容易忘 close）：");
        line("    BufferedReader br = new BufferedReader(new StringReader(\"文本\"));");
        line("    try { ... } finally { br.close(); }");
        line("");
        line("  新写法（Java 7+）：");
        line("    try (BufferedReader br = new BufferedReader(new StringReader(\"文本\"))) { ... }");
        line("    → 括号里的资源【自动关闭】，不用手写 finally，");
        line("      而且【关闭时抛的异常不会盖掉主异常】，会被【抑制】起来");
        line("");
        line("  实测1 —— 正常读取：");
        try (BufferedReader br = new BufferedReader(new StringReader("第一行\n第二行"))) {
            String line2;
            while ((line2 = br.readLine()) != null) {
                line("    读出一行：" + line2);
            }
        } catch (IOException e) {
            line("    IOException（受检异常！编译器强制要求你写 catch）");
        }
        line("");
        line("  实测2 —— 主流程和 close 都出错时：");
        try (BrokenResource r = new BrokenResource("连接池")) {
            throw new RuntimeException("主流程出错");
        } catch (RuntimeException e) {
            line("    主异常（你看到的那个）：" + e.getMessage());
            line("    被抑制的异常数量：" + e.getSuppressed().length);
            for (Throwable t : e.getSuppressed()) {
                line("      抑制异常 → " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
            line("    → 两个异常都保住了，信息不丢失（老写法做不到）");
        }
        line("");

        // ══════════════════════════════════════════════
        // ⑨ 异常在项目里的传播链路
        // ══════════════════════════════════════════════
        line("⑨ 异常在项目里的传播链路（对照你的 Tlias 项目）");
        line("────────────────────────────────────────");
        Controller controller = new Controller();
        line("  正常请求 create(\"李四\")：");
        line("    " + controller.handle("李四"));
        line("  重复姓名 create(\"张三\")：");
        line("    " + controller.handle("张三"));
        line("");
        line("  链路对照：");
        line("    Dao 层    → 抛 MyBusinessException（业务规则不满足）");
        line("    Service 层→ 【不 catch】，让异常原样上抛（不要吞掉）");
        line("    Controller → 本该由 @RestControllerAdvice 全局接住");
        line("    全局处理器 → 统一转成 {\"code\":409,\"msg\":\"...\"}");
        line("");
        line("  ★ 为什么 Service 不该 catch？");
        line("    因为 Service 不知道该给前端返回什么（它没有 HTTP 上下文）。");
        line("    在 Service 里 catch 掉 = 异常信息丢了 = 前端只看到「操作失败」。");
        line("    → 原则：【在能补救的层处理，在没能力处理的层继续抛】");
        line("");

        // ══════════════════════════════════════════════
        // ⑩ 自定义业务异常
        // ══════════════════════════════════════════════
        line("⑩ 自定义业务异常（带 code）");
        line("────────────────────────────────────────");
        line("  public class BusinessException extends RuntimeException {");
        line("      private final int code;");
        line("      public BusinessException(int code, String msg) {");
        line("          super(msg);");
        line("          this.code = code;");
        line("      }");
        line("      public int getCode() { return code; }");
        line("  }");
        line("");
        line("  · 为什么继承 RuntimeException 而不是 Exception？");
        line("    → 非受检：Service/Controller 里【不强制】写 catch，代码干净");
        line("    → 但你的 @ExceptionHandler(BusinessException.class) 会接住它");
        line("  · 为什么继承 Exception 就【不行】？");
        line("    → 受检异常会强制每一层都写 throws 或 catch，Service 层会很丑");
        line("  · 你的项目里已经有一个 BusinessException，思路是对的。");
        line("");

        // ══════════════════════════════════════════════
        // ⑪ 常见异常速查表（每一个都真的抛一次）
        // ══════════════════════════════════════════════
        line("⑪ 常见异常速查表（每一个都真的抛一次）");
        line("────────────────────────────────────────");

        show("NullPointerException", "空指针：对象是 null 却调了它的方法",
                () -> { String s = null; s.length(); });

        show("ClassCastException", "类型转换：把对象转成了不相容的类型",
                () -> { Object o = "我是字符串"; Integer i = (Integer) o; System.out.println(i); });

        show("NumberFormatException", "数字格式：把非数字字符串转 int",
                () -> Integer.parseInt("abc"));

        show("ArrayIndexOutOfBoundsException", "数组越界：下标超过长度",
                () -> { int[] arr = new int[3]; System.out.println(arr[5]); });

        show("IndexOutOfBoundsException", "集合越界：List.get(999)",
                () -> { java.util.List<String> list = new java.util.ArrayList<>(); list.get(0); });

        show("ArithmeticException", "算术：除以 0",
                () -> { int a = 10, b = 0; System.out.println(a / b); });

        show("IllegalArgumentException", "参数非法：年龄不合法",
                () -> { if (true) throw new IllegalArgumentException("年龄不合法: -5"); });

        show("IllegalStateException", "状态不对：在不该调用的时机调用",
                () -> { if (true) throw new IllegalStateException("订单已关闭，不能支付"); });

        show("ConcurrentModificationException", "并发修改：边遍历边改集合",
                () -> {
                    java.util.List<String> list = new java.util.ArrayList<>(java.util.List.of("a", "b", "c"));
                    for (String s : list) {
                        list.remove(0);      // 边遍历边删第一个 → 下次 next() 就炸
                    }
                });

        show("NullPointerException（数组版）", "数组为 null",
                () -> { int[] arr = null; System.out.println(arr[0]); });

        line("  受检异常（IO 系列）：");
        show("FileNotFoundException", "文件不存在",
                () -> { new java.io.FileInputStream("不存在的文件.txt"); });
        show("IOException", "IO 读写出问题",
                () -> { throw new IOException("磁盘 IO 错误"); });

        line("");

        // ══════════════════════════════════════════════
        // ⑫ 实践清单
        // ══════════════════════════════════════════════
        line("⑫ 实践清单（照着做就对了）");
        line("────────────────────────────────────────");
        line("  1. 什么时候 catch？");
        line("     · 能补救 → catch（记日志 + 给友好提示）");
        line("     · 不能补救 → 不 catch，让它往上抛");
        line("     · 绝不要 catch 后什么都不干（吞异常 = 埋雷）");
        line("  2. catch 之后要么处理，要么【继续抛】（throw e），别默默吞掉");
        line("  3. 绝不在 finally 里写 return");
        line("  4. 关资源用 try-with-resources，不用手写 finally");
        line("  5. 业务异常统一用自定义 RuntimeException + 全局 @ExceptionHandler");
        line("  6. 日志用 logger.error(\"出错了\", e)，不要只 e.printStackTrace()");
        line("  7. 异常信息要带上下文（哪个字段、哪个值），便于定位");
        line("  8. 单个 catch 不要超过 3 个分支，逻辑复杂就拆方法");
        line("");

        line("一句话总纲：");
        line("  异常 = 程序正常流程被打断。");
        line("  你的职责不是「消灭所有异常」，而是【让每个异常都有明确的归属层级】：");
        line("  能修的当场修，修不了的往上抛，兜底的交给全局处理器。");
    }

    // ══════════════════════════════════════════════
    // 辅助方法
    // ══════════════════════════════════════════════

    /** 演示异常一级一级往上冒 */
    static void level3() {
        throw new IllegalStateException("我在 level3 出事 → level2 → level1 → main");
    }

    static void level2() {
        level3();
    }

    static void level1() {
        level2();
    }

    /** 坑1：try 里 return 了，finally 照样执行 */
    static int returnWithFinally() {
        try {
            return 1;
        } finally {
            line("      （finally 执行了，但它没有 return，所以不影响返回值）");
        }
    }

    /** 坑2：finally 里的 return 吞掉异常 */
    static String swallowByFinally() {
        try {
            throw new IllegalStateException("这个异常本该被看到");
        } catch (IllegalStateException e) {
            return "catch 里准备返回";
        } finally {
            return "finally 的 return 生效了 → 上面那句 return 和异常都被【吞掉】";
        }
    }

    /**
     * 允许抛受检异常的函数式接口。
     * ★ 注意：JDK 自带的 Runnable / Function【不允许】抛受检异常，
     *   所以 `() -> new FileInputStream("x.txt")` 编译不过 —— 报错信息是
     *   「未报告的异常 FileNotFoundException; 必须捕获或声明为抛出」。
     *   这就是受检异常「编译器强制」的活证据。
     */
    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }

    /** 统一演示某个异常：真的抛一次，然后接住 */
    static void show(String name, String desc, ThrowingRunnable r) {
        try {
            r.run();
            line("    " + name + " → 没抛异常（不符合预期）");
        } catch (Exception e) {
            line("    " + name);
            line("      说明：" + desc);
            line("      实际抛出：" + e.getClass().getName());
            line("      信息：" + e.getMessage());
        }
    }
}
