package oop;

import java.util.ArrayList;
import java.util.List;

/**
 * ════════════════════════════════════════════════════════════
 *  第 7 关 · 综合实战（把前 6 关串起来）
 * ════════════════════════════════════════════════════════════
 *
 *  这是一个「迷你版校园招聘系统」，把你简历上两个项目的核心设计
 *  用纯 OOP 实现了一遍：
 *
 *    封装     → Student / Company 的 private 字段 + setter 校验
 *    继承     → Job 抽象类 → InternshipJob / FullTimeJob
 *    多态     → Job 引用统一处理，calcSalary() 各不相同
 *    抽象类   → Job 不能 new，只能被继承
 *    接口     → Rechargeable（可充值角色）、Searchable（可搜索）
 *    组合     → 一个类"持有"另一个对象（Student 持有 List<Application>）
 *
 *  【对照关系】
 *    这个迷你系统 ≈ 你项目三 campus-system-springboot 的"纯 Java 版"
 *    差别在于：项目三用 MyBatis 查库、用 Spring 注入，而这里全在内存里。
 *
 *  【运行】右键本文件 → Run
 */
public class L7_OopFinal {

    // ══════════════ ① 封装 ══════════════
    /** 学生：数据 private + setter 校验 */
    static class Student {
        private final String id;
        private String name;
        private String major;
        private int age;
        /** 组合：持有自己投过的岗位记录 */
        private final List<Application> applications = new ArrayList<>();

        public Student(String id, String name, int age, String major) {
            this.id = id;
            setName(name);
            setAge(age);
            setMajor(major);
        }

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

        public void setMajor(String major) {
            this.major = major;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getMajor() {
            return major;
        }

        public List<Application> getApplications() {
            return applications;
        }

        public void show() {
            System.out.println("      " + id + " " + name + "，" + age + "岁，"
                    + major + "专业，已投 " + applications.size() + " 个岗位");
        }
    }

    /** 企业 */
    static class Company {
        private final String name;
        private final String industry;
        private final List<Job> jobs = new ArrayList<>();

        public Company(String name, String industry) {
            this.name = name;
            this.industry = industry;
        }

        public void publish(Job job) {
            jobs.add(job);
            System.out.println("      " + name + " 发布岗位：" + job.getTitle());
        }

        public List<Job> getJobs() {
            return jobs;
        }

        public String getName() {
            return name;
        }
    }

    // ══════════════ ② 抽象类 + ③ 多态 ══════════════
    /** 岗位：抽象类 */
    static abstract class Job {
        protected final String title;
        protected final double baseSalary;
        protected final String majorRequired;

        protected Job(String title, double baseSalary, String majorRequired) {
            this.title = title;
            this.baseSalary = baseSalary;
            this.majorRequired = majorRequired;
        }

        /** 抽象方法：各岗位薪资算法不同 */
        public abstract double calcSalary();

        /** 具体方法：所有岗位共通 */
        public void display() {
            System.out.println("      " + title + " | 薪资 " + calcSalary()
                    + " | 要求专业 " + majorRequired);
        }

        public boolean match(String major) {
            return majorRequired.contains(major);
        }

        public String getTitle() {
            return title;
        }

        public String getMajorRequired() {
            return majorRequired;
        }
    }

    /** 实习岗位 */
    static class InternshipJob extends Job {
        private final int months;

        public InternshipJob(String title, double baseSalary, String majorRequired, int months) {
            super(title, baseSalary, majorRequired);
            this.months = months;
        }

        @Override
        public double calcSalary() {
            return baseSalary * 0.6;      // 实习生打六折
        }

        public int getMonths() {
            return months;
        }
    }

    /** 全职岗位 */
    static class FullTimeJob extends Job {
        private final double performance;

        public FullTimeJob(String title, double baseSalary, String majorRequired, double performance) {
            super(title, baseSalary, majorRequired);
            this.performance = performance;
        }

        @Override
        public double calcSalary() {
            return baseSalary + performance;
        }
    }

    // ══════════════ ④ 接口 ══════════════
    /** 可被搜索的：岗位 */
    interface Searchable {
        /** 返回匹配度描述 */
        String matchInfo(String keyword);
    }

    /** 可充值的：钱包（演示 default 方法） */
    interface Rechargeable {
        void recharge(double amount);

        default String describe() {
            return "支持充值";
        }
    }

    /** 实现接口：岗位也能被搜索 */
    static class SearchableJob extends FullTimeJob implements Searchable {
        public SearchableJob(String title, double baseSalary, String majorRequired, double performance) {
            super(title, baseSalary, majorRequired, performance);
        }

        @Override
        public String matchInfo(String keyword) {
            if (title.contains(keyword) || majorRequired.contains(keyword)) {
                return "【强匹配】" + title;
            }
            return "【弱匹配】" + title;
        }
    }

    /** 钱包：实现 Rechargeable */
    static class Wallet implements Rechargeable {
        private double balance;

        @Override
        public void recharge(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("充值金额必须大于 0");
            }
            balance += amount;
        }

        public double getBalance() {
            return balance;
        }
    }

    // ══════════════ ⑤ 组合：投递记录 ══════════════
    /** 投递记录：持有 student + job 两个对象的引用 */
    static class Application {
        private final Student student;
        private final Job job;
        private String status = "待审核";

        public Application(Student student, Job job) {
            this.student = student;
            this.job = job;
        }

        public void approve() {
            status = "已通过";
        }

        public void reject() {
            status = "已拒绝";
        }

        public String getStatus() {
            return status;
        }

        public Student getStudent() {
            return student;
        }

        public Job getJob() {
            return job;
        }
    }

    // ══════════════ 业务服务 ══════════════
    /** 招聘服务：统一处理各种岗位（多态） */
    static class RecruitmentService {
        private final List<Job> allJobs = new ArrayList<>();

        public void publish(Job job) {
            allJobs.add(job);
        }

        /** 多态：不管什么类型的岗位，统一处理 */
        public List<Job> findByMajor(String major) {
            List<Job> result = new ArrayList<>();
            for (Job job : allJobs) {
                if (job.match(major)) {
                    result.add(job);
                }
            }
            return result;
        }

        /** 统计所有岗位的平均薪资 —— 多态调用 calcSalary() */
        public double averageSalary() {
            if (allJobs.isEmpty()) {
                return 0;
            }
            double sum = 0;
            for (Job job : allJobs) {
                sum += job.calcSalary();     // ← 这里自动多态
            }
            return sum / allJobs.size();
        }

        public List<Job> getAllJobs() {
            return allJobs;
        }
    }

    // ════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        line("第 7 关 · 面向对象综合实战");
        line("");

        // ── ① 封装：非法数据被拦住 ──
        line("① 封装 —— 非法输入当场抛异常");
        try {
            new Student("S001", "", 20, "计算机");
        } catch (IllegalArgumentException e) {
            System.out.println("      捕获到异常：" + e.getMessage());
        }
        try {
            new Student("S002", "李四", -5, "计算机");
        } catch (IllegalArgumentException e) {
            System.out.println("      捕获到异常：" + e.getMessage());
        }
        line("");

        // ── ② 抽象类 + 继承 + 多态 ──
        line("② 抽象类 + 继承 + 多态 —— 统一处理不同岗位");
        Student zhang = new Student("S001", "张三", 21, "计算机");
        Company byteDance = new Company("字节跳动", "互联网");
        System.out.println("    企业：");
        InternshipJob intern = new InternshipJob("Java开发实习生", 8000, "计算机", 3);
        FullTimeJob fullTime = new FullTimeJob("后端开发工程师", 20000, "计算机", 5000);
        SearchableJob searchable = new SearchableJob("算法工程师", 30000, "数学", 8000);
        byteDance.publish(intern);
        byteDance.publish(fullTime);
        byteDance.publish(searchable);
        line("");

        line("    多态：Job 引用统一处理，薪资算法各不相同");
        for (Job job : byteDance.getJobs()) {
            job.display();
        }
        line("");

        // ── ③ 组合：学生投递 ──
        line("③ 组合 —— 学生持有投递记录");
        Application app = new Application(zhang, intern);
        zhang.getApplications().add(app);
        System.out.println("    投递后状态：" + app.getStatus());
        app.approve();
        System.out.println("    企业审核后状态：" + app.getStatus());
        zhang.show();
        line("");

        // ── ④ 接口：多态 + default 方法 ──
        line("④ 接口 —— 能力契约与多态");
        List<Searchable> searchables = new ArrayList<>();
        searchables.add(searchable);
        searchables.add(new SearchableJob("前端开发", 18000, "软件工程", 3000));
        for (Searchable s : searchables) {
            System.out.println("      " + s.matchInfo("计算机"));
        }
        Wallet wallet = new Wallet();
        wallet.recharge(100);
        System.out.println("    钱包充值后余额：" + wallet.getBalance() + "，" + wallet.describe());
        line("");

        // ── ⑤ 业务服务：多态的实际收益 ──
        line("⑤ 多态的实际收益：服务层只写一份代码");
        RecruitmentService service = new RecruitmentService();
        for (Job job : byteDance.getJobs()) {
            service.publish(job);
        }
        System.out.println("    按专业查「计算机」：");
        for (Job job : service.findByMajor("计算机")) {
            System.out.println("      " + job.getTitle());
        }
        System.out.printf("    全部岗位平均薪资：%.2f 元%n", service.averageSalary());
        line("    → 服务层完全不知道有实习岗/全职岗/算法岗的区别，全靠多态");
        line("    → 这就是「面向接口编程」：新增岗位类型，服务层一行不改");
        line("");

        line("本关要点（面向对象四大特性回顾）：");
        line("  · 封装 —— 数据 private + 校验，非法数据进不来");
        line("  · 继承 —— 抽象 Job 派生出实习/全职/算法岗");
        line("  · 多态 —— 一个 Job 引用处理所有岗位类型，薪资算法各不同");
        line("  · 抽象 —— Job 只声明 calcSalary()，不规定怎么算");
        line("  · 接口 —— Searchable/Rechargeable 定义能力，可多实现");
        line("  · 组合 —— Application 持有 Student + Job，比继承更灵活");
    }

    static void line(String s) {
        System.out.println(s);
    }
}
