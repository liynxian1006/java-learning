import java.util.*;

/**
 * 阶段 1 综合示例：面向对象 + 集合 + 异常
 * 右键 Run 'Stage1Demo' 查看输出
 */
public class Stage1Demo {

    public static void main(String[] args) {

        // ===== A. 面向对象 =====
        // 对象：用类 new 出来的具体实例
        Student s = new Student("小明", 20);
        s.setAge(21); // 通过 setter 受控修改（封装）
        System.out.println("学生：" + s.getName() + "，年龄：" + s.getAge());

        // 多态：父类引用指向子类对象，调用的是子类重写的方法
        Person p = new Student("小红", 19);
        p.introduce();

        // ===== B. 集合框架 =====
        List<String> names = new ArrayList<>();
        names.add("张三");
        names.add("李四");
        System.out.print("名单：");
        for (String n : names) {
            System.out.print(n + " ");
        }
        System.out.println();

        Map<String, Integer> scoreMap = new HashMap<>();
        scoreMap.put("数学", 90);
        scoreMap.put("英语", 85);
        System.out.println("数学分数=" + scoreMap.get("数学"));

        // 遍历 Map
        for (Map.Entry<String, Integer> entry : scoreMap.entrySet()) {
            System.out.println(entry.getKey() + " → " + entry.getValue());
        }

        // ===== C. 异常处理 =====
        try {
            int r = divide(10, 0); // 这里会抛异常
            System.out.println("结果=" + r);
        } catch (ArithmeticException e) {
            System.out.println("捕获到异常：" + e.getMessage());
        } finally {
            System.out.println("finally：无论是否异常都会执行（常用于关闭资源）");
        }
    }

    // 主动抛异常的方法
    static int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("除数不能为 0");
        }
        return a / b;
    }
}

// 父类
class Person {
    protected String name;

    public Person(String name) { // 构造方法
        this.name = name;
    }

    public void introduce() {
        System.out.println("我是 " + name);
    }
}

// 子类继承父类
class Student extends Person {
    private int age; // 封装：private 字段

    public Student(String name, int age) {
        super(name); // 调用父类构造方法
        this.age = age;
    }

    @Override // 重写父类方法（多态的基础）
    public void introduce() {
        System.out.println("我是学生 " + name + "，年龄 " + age);
    }

    // 封装：对外的读写通道
    public String getName() { return name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}
