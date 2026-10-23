/**
 * 阶段 1 补充示例：抽象类 + 接口 + 多态
 * 重点演示"父类/接口引用指向子类对象"的多态效果
 * 右键 Run 'Stage1OopExtra' 查看输出
 */
public class Stage1OopExtra {

    public static void main(String[] args) {

        // 1. 抽象类多态：Animal 引用指向 Cat / Dog
        Animal a1 = new Cat();
        Animal a2 = new Dog();
        a1.sound();  // 运行的是 Cat 的 sound()
        a2.sound();  // 运行的是 Dog 的 sound()
        a1.breathe();// 抽象类里的普通方法，子类直接继承

        // 2. 接口多态：Flyable 引用指向 Duck
        Flyable f = new Duck();
        f.fly();     // 运行的是 Duck 的 fly()

        // 3. 一个类同时实现多个接口（弥补单继承）
        Duck d = new Duck();
        d.fly();
        d.swim();
    }
}

// ===== 抽象类：不能 new，含抽象方法 =====
abstract class Animal {
    abstract void sound();        // 抽象方法（无实现），子类必须实现
    void breathe() {              // 普通方法，子类直接继承
        System.out.println("呼吸");
    }
}

class Cat extends Animal {
    @Override
    void sound() { System.out.println("喵~"); }
}

class Dog extends Animal {
    @Override
    void sound() { System.out.println("汪!"); }
}

// ===== 接口：能力的契约 =====
interface Flyable {
    void fly();
}

interface Swimmable {
    void swim();
}

// Duck 同时具备"会飞"和"会游"两种能力（多实现）
class Duck implements Flyable, Swimmable {
    @Override
    public void fly() { System.out.println("鸭子飞"); }

    @Override
    public void swim() { System.out.println("鸭子游"); }
}
