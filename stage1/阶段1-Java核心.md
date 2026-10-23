# 阶段 1 · Java 核心（第 4–8 周）· 详细教程

> 这是「零基础到简历达成」五阶段路线的**第 1 阶段**（阶段 0 已完成：Linux 10 命令、Java 语法入门、方法、访问修饰符）。
> 本阶段目标：真正理解面向对象，会用集合和异常，能用 Java 通过 JDBC 连 MySQL 做增删改查。

---

## 前置回顾（你已掌握，这里直接调用）
- Linux：`cd/ls/pwd/mkdir/touch/cp/mv/rm/cat/clear`
- Java 语法：变量（int/double/String/boolean）、运算符、`if/else`、`for/while`、数组、方法
- 访问修饰符：`public`（谁都能用）、`private`（只有自己）、默认（同包）、`protected`（同包+子类）

---

## 本阶段四大块
1. **A. 面向对象 OOP**（最重要，后端灵魂）
2. **B. 集合框架**（替代数组，日常必用）
3. **C. 异常处理**（让程序不崩）
4. **D. MySQL + JDBC**（连数据库，阶段 2 项目地基）

---

# A. 面向对象（OOP）—— 后端灵魂

## A1 类与对象
- **类（class）= 模板/图纸**：描述"这一类东西有什么属性、能干什么"。
- **对象（object）= 按图纸造出的具体实体**：`new` 出来的。
```java
class Student {                 // 类（图纸）
    String name;
    void study() { System.out.println(name + " 在学习"); }
}
Student s = new Student();      // 对象（实例）
s.name = "小明";
s.study();
```
直觉：`new` 会在内存（堆）里开辟一块空间存放这个学生的数据。

## A2 构造方法
- 名字**和类名完全相同**、**没有返回类型**，在 `new` 时自动调用，用来初始化。
- 可以重载（参数不同）；**不写时 Java 自动给一个无参构造**，你一写有参的，无参的就没了（需要就自己补）。
```java
class Student {
    String name;
    int age;
    Student(String name, int age) { this.name = name; this.age = age; } // 有参构造
}
Student s = new Student("小明", 20); // new 时直接传参
```
`this` = "当前这个对象"，用来区分"形参 name"和"字段 name"。

## A3 封装（复习+巩固）
字段 `private`，只通过 `public` 的 getter/setter 读写，可在 setter 里加校验。
```java
class Student {
    private int age;                       // 锁起来
    public int getAge() { return age; }
    public void setAge(int age) {
        if (age < 0 || age > 150) return;  // 不合法就不设
        this.age = age;
    }
}
```

## A4 继承 `extends`
- 子类复用父类的属性和方法，减少重复代码。**Java 只支持单继承**（一个类只能有一个直接父类）。
- `super` 调用父类构造/方法；`@Override` 表示重写父类方法（签名必须一致）。
```java
class Person {                 // 父类
    String name;
    void say() { System.out.println("我是" + name); }
}
class Student extends Person { // 子类
    @Override
    void say() { System.out.println("我是学生" + name); } // 重写
}
```

## A5 多态（框架大量使用）
- **父类引用指向子类对象**：`Person p = new Student();`
- 口诀：**编译看左边，运行看右边**——调用方法时，实际执行的是子类重写后的版本。
- 好处：写通用代码。比如方法参数用父类，就能接收任意子类对象。
```java
Person p = new Student();  // 多态
p.say();                   // 运行的是 Student 的 say()
```

## A6 抽象类 `abstract`
- 用 `abstract` 修饰，**不能 `new`**，可包含"没有方法体的抽象方法"，子类**必须实现**这些抽象方法。
- 适合"有部分通用实现 + 有部分必须子类自己写"的场景。
```java
abstract class Animal {
    abstract void sound();        // 抽象方法，无实现
    void breathe() { System.out.println("呼吸"); } // 普通方法
}
class Cat extends Animal {
    void sound() { System.out.println("喵"); } // 必须实现
}
```

## A7 接口 `interface`
- 定义"能做什么"的**契约**，里面方法默认 `public abstract`，字段默认 `public static final`（常量）。
- 用 `implements` 实现，**一个类可实现多个接口**（弥补单继承）。
- Java 8+ 接口可有 `default` 默认方法（有实现）。
```java
interface Flyable { void fly(); }            // 契约：能飞
interface Swimmable { void swim(); }
class Duck implements Flyable, Swimmable {   // 多实现
    public void fly() { System.out.println("飞"); }
    public void swim() { System.out.println("游"); }
}
```

### 接口 vs 抽象类（面试常问）
| | 抽象类 abstract | 接口 interface |
|---|---|---|
| 能否 new | 不能 | 不能 |
| 方法 | 可有实现也可抽象 | 默认抽象（Java 8+ 可 default） |
| 继承/实现 | 单继承 `extends` | 多实现 `implements` |
| 设计意图 | "是不是"（is-a，父子） | "能不能"（能力契约） |

## A 常见坑
- 忘记写无参构造（写了有参后无参消失）。
- `this` 用错，把形参当字段。
- 重写时方法签名（名字+参数）不一致，变成重载而非重写。
- 接口方法忘了写 `public`（接口里默认就是 public，实现时必须 public）。

---

# B. 集合框架（替代数组）

## B1 ArrayList（有序、可重复、索引查得快）
```java
List<String> list = new ArrayList<>();
list.add("A"); list.add("B");
list.get(0);          // 取第 0 个
list.remove(1);       // 删第 1 个
list.size();          // 长度
// 三种遍历：
for (int i=0;i<list.size();i++) System.out.println(list.get(i)); // 索引
for (String s : list) System.out.println(s);                    // 增强 for
list.forEach(System.out::println);                              // Lambda
```

## B2 HashMap（键值对，按 key 查得快）
```java
Map<String, Integer> map = new HashMap<>();
map.put("数学", 90); map.put("英语", 85);
map.get("数学");            // 90
map.containsKey("数学");    // true
for (Map.Entry<String,Integer> e : map.entrySet())
    System.out.println(e.getKey() + "=" + e.getValue());
```

## B3 泛型 `<>`
`List<String>` 限定只能放 String，编译期就防错，取出来不用强转。

## 选用场景
| 需求 | 用 |
|---|---|
| 一组有序对象，按位置取 | `ArrayList` |
| 键值映射（如 学号→学生） | `HashMap` |
| 去重集合 | `HashSet` |

---

# C. 异常处理（让程序不崩）

## C1 异常体系
- `Throwable` 分两支：`Error`（严重，如内存溢出，管不了）和 `Exception`。
- `Exception` 又分：**受检异常**（编译强制处理，如 IO）、**非受检/RuntimeException**（运行时才爆，如空指针、除零）。

## C2 try-catch-finally
```java
try {
    int x = 1/0;
} catch (ArithmeticException e) {   // 捕获特定异常
    System.out.println(e.getMessage());
} catch (Exception e) {             // 多个 catch：小异常放前面
    e.printStackTrace();
} finally {
    System.out.println("一定执行");  // 除 System.exit 外都会执行，常用来关资源
}
```

## C3 throw vs throws
- `throw new 异常()`：在方法内**主动抛出**一个异常。
- `throws 异常`：在方法签名上**声明**可能抛出的异常，交给调用方处理。
```java
static int divide(int a, int b) throws ArithmeticException {
    if (b == 0) throw new ArithmeticException("除数为0");
    return a / b;
}
```

## C4 自定义异常（业务报错）
```java
class AgeException extends Exception {       // 继承 Exception（受检）
    public AgeException(String msg) { super(msg); }
}
// 使用：if (age<0) throw new AgeException("年龄非法");
```

## 面试常问
- `finally` 一定执行吗？（除 `System.exit`）
- `throw` 和 `throws` 区别？（主动抛 vs 声明抛）
- 多个 catch 顺序？（子类在前，父类在后）

---

# D. MySQL + JDBC（连数据库）

## D1 SQL 基础（用 Navicat 写）
```sql
SELECT * FROM student WHERE age > 18;        -- 查
INSERT INTO student(name,age) VALUES('小明',20); -- 增
UPDATE student SET age=21 WHERE name='小明';  -- 改
DELETE FROM student WHERE name='小明';        -- 删
```

## D2 JDBC 步骤（对应你已有的 java-jdbc-campus-system 骨架）
1. 注册驱动：`Class.forName("com.mysql.cj.jdbc.Driver")`
2. 获取连接：`Connection conn = DriverManager.getConnection(url, user, pwd)`
3. 创建语句：`PreparedStatement ps = conn.prepareStatement("SELECT ...")`
4. 执行：`ResultSet rs = ps.executeQuery()`
5. 遍历结果：`while (rs.next()) { rs.getString("name"); }`
6. **关闭**资源（ finally 里关，顺序 rs→ps→conn）
- `PreparedStatement` 用 `?` 占位，能防 SQL 注入，优于 `Statement`。

---

## 配套可运行示例
- `Stage1Demo.java`：串起 OOP（继承+多态+封装）+ 集合 + 异常，可直接 Run。
- `Stage1OopExtra.java`：专门演示**抽象类 + 接口 + 多态**，补足上面 A6/A7 的运行效果。

在 IDEA 里分别右键 Run 这两个文件看输出。

---

## 8 周节奏（每天 1.5–2 小时）
| 周 | 内容 |
|---|---|
| 第 4 周 | 类与对象、构造方法、封装巩固 |
| 第 5 周 | 继承、方法重写 |
| 第 6 周 | 多态、抽象类、接口 |
| 第 7 周 | 集合 ArrayList / HashMap + 遍历 |
| 第 8 周 | 异常处理 + MySQL/SQL + JDBC 连库 CRUD |

---

## 阶段 1 打勾清单
- [ ] 能说出"类"和"对象"区别，会用 `new` 造对象
- [ ] 会写构造方法（有参/无参），理解 `this`
- [ ] 能在 setter 里加校验（封装）
- [ ] 理解 `extends` 继承与 `@Override` 重写
- [ ] 理解多态（父类引用指向子类对象）
- [ ] 能定义抽象类与接口，说清两者区别
- [ ] 会用 `ArrayList` / `HashMap`，知道各自场景
- [ ] 会写 `try/catch/finally`，区分 throw/throws
- [ ] 能自定义一个异常类
- [ ] 用 Navicat 写 CRUD 的 SQL
- [ ] 用 JDBC（PreparedStatement）连 MySQL 查询
- [ ] 跑通 `Stage1Demo.java` 与 `Stage1OopExtra.java`

---

## 下一步
阶段 1 之后进入 **阶段 2 · 双项目实战**：亲手做出简历上的两个项目（Flask + Java JDBC），把本阶段知识全部用起来。
