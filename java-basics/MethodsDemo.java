/**
 * 方法（Method）详细示例
 * 右键 Run 'MethodsDemo' 看每个方法的输出与调用效果
 */
public class MethodsDemo {

    // 程序入口
    public static void main(String[] args) {

        // ① 调用"无返回值"方法：只做事，不拿结果
        sayHello("Sakura");

        // ② 调用"有返回值"方法：用变量接住结果再用
        int sum = add(3, 5);
        System.out.println("3 + 5 = " + sum);

        // 返回值也可以直接嵌到别的表达式里
        System.out.println("10 和 4 的较大值 = " + max(10, 4));

        // ③ 多个参数
        printUser("小明", 20, 89.5);

        // ④ 方法重载：同名，但参数个数/类型不同，Java 当成不同方法
        System.out.println("三个 int 求和 = " + add(1, 2, 3));     // 调 3 参数版本
        System.out.println("两个 double 求和 = " + add(1.1, 2.2)); // 调 double 版本

        // ⑤ 无参数方法
        printLine();
    }

    // ── 1. 无返回值方法：void 表示"不返回任何东西"，只执行一段逻辑 ──
    static void sayHello(String name) {
        System.out.println("你好，" + name + "！");
    }

    // ── 2. 有返回值方法：返回 int，调用方可以拿到这个数 ──
    static int add(int x, int y) {
        return x + y;   // return 把结果交回去，并立刻结束本方法
    }

    // ── 3. 有返回值 + 分支逻辑 ──
    static int max(int a, int b) {
        if (a > b) {
            return a;   // 命中就返回，方法结束
        } else {
            return b;
        }
    }

    // ── 4. 多个参数：类型 变量名，逗号分隔 ──
    static void printUser(String name, int age, double score) {
        System.out.println("姓名：" + name + "，年龄：" + age + "，成绩：" + score);
    }

    // ── 5. 方法重载（overload）：方法名相同，参数列表不同即可共存 ──
    static int add(int x, int y, int z) {     // 参数个数不同
        return x + y + z;
    }
    static double add(double x, double y) {   // 参数类型不同
        return x + y;
    }

    // ── 6. 无参数方法 ──
    static void printLine() {
        System.out.println("----------");
    }
}
