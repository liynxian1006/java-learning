package oop;

import java.util.Arrays;

/**
 * ════════════════════════════════════════════════════════════
 *  专题 · 字符串判空的标准写法
 * ════════════════════════════════════════════════════════════
 *
 *  【你问的代码】
 *      if (name == null || name.trim().isEmpty()) {
 *          throw new IllegalArgumentException("姓名不能为空");
 *      }
 *
 *  【两个条件分别管什么】
 *      name == null              → 引用为空（连对象都没有）
 *      name.trim().isEmpty()     → 字符串存在，但内容全是空白
 *
 *  【⚠ 顺序不能颠倒】（本专题重点）
 *      name == null || name.trim().isEmpty()   ✔ 对
 *      name.trim().isEmpty() || name == null   ✘ 错
 *      后者：name 为 null 时，先执行 trim() → NullPointerException
 *
 *  【⚠ trim() 的盲区】
 *      trim() 只去掉 <= ' ' (0x20) 的字符，全角空格 \u3000 去不掉！
 *      Java 11+ 的 isBlank() 用 Character.isWhitespace，能正确处理
 *
 *  【运行】右键本文件 → Run
 */
public class L18_StringBlankCheck {

    // ══════════════════════════════════════════════
    // 各种"看起来空"的输入
    // ══════════════════════════════════════════════
    static final String[] CASES = {
            null,               // null 引用
            "",                 // 空字符串
            "   ",              // 三个半角空格
            "\t\n",             // tab + 换行
            "　",       // 全角空格（U+3000）
            " 张三 ",           // 前后有空格但有内容
            "张三"              // 正常
    };

    /** 写法 A：正确写法（你问的这段） */
    static boolean isBlankA(String name) {
        return name == null || name.trim().isEmpty();
    }

    /** 写法 B：顺序颠倒（错误示范） */
    static boolean isBlankB(String name) {
        return name.trim().isEmpty() || name == null;   // ← null 时先调 trim() → NPE
    }

    /** 写法 C：Java 11+ 的 isBlank()，用 Character.isWhitespace */
    static boolean isBlankC(String name) {
        return name == null || name.isBlank();
    }

    /** 写法 D：只看空字符串，不看空白（最宽松） */
    static boolean isBlankD(String name) {
        return name == null || name.isEmpty();
    }

    public static void main(String[] args) {
        line("专题 · 字符串判空的标准写法");
        line("");

        // ══════════════════════════════════════════════
        // ① 两个条件分别管什么
        // ══════════════════════════════════════════════
        line("① 两个条件分别管什么");
        line("    name == null           → 引用为空（连对象都没有，调任何方法都是 NPE）");
        line("    name.trim().isEmpty()  → 字符串存在，但去掉首尾空白后长度是 0");
        line("    所以要【两个都判】，只判一个会漏");
        line("");

        // ══════════════════════════════════════════════
        // ② 各种输入的判定结果
        // ══════════════════════════════════════════════
        line("② 各种输入的判定结果");
        System.out.printf("    %-22s %-8s %-8s %-8s %-8s%n",
                "输入", "A正确", "C isBlank", "D isEmpty", "trim后");
        System.out.println("    " + "-".repeat(62));
        for (String c : CASES) {
            String desc = describe(c);
            String trimmed;
            try {
                trimmed = "\"" + c.trim() + "\"";
            } catch (Exception e) {
                trimmed = "NPE";
            }
            System.out.printf("    %-22s %-8s %-8s %-8s %-8s%n",
                    desc, isBlankA(c), isBlankC(c), isBlankD(c), trimmed);
        }
        line("");
        line("    → 注意「全角空格」那行：A 和 C 判定不同！");
        line("      trim() 去不掉全角空格 \\u3000，isBlank() 可以");
        line("");

        // ══════════════════════════════════════════════
        // ③ 顺序颠倒的后果
        // ══════════════════════════════════════════════
        line("③ ⚠ 顺序颠倒的后果（重要）");
        System.out.println("    正确写法 A：name == null || name.trim().isEmpty()");
        System.out.println("      name = null → " + isBlankA(null) + "   ✔ 正常返回");
        System.out.println("    错误写法 B：name.trim().isEmpty() || name == null");
        try {
            isBlankB(null);
            System.out.println("      name = null → 没报错（不该走到这）");
        } catch (NullPointerException e) {
            line("      name = null → 抛 NullPointerException  ✘");
            line("      原因：|| 是【短路求值】，从左往右逐个判断，");
            line("            左边先执行 trim()，null 上调方法直接炸");
        }
        line("");

        // ══════════════════════════════════════════════
        // ④ 为什么用 || 而不是 &
        // ══════════════════════════════════════════════
        line("④ 为什么用 || 不是 &");
        line("    || 短路求值：左边为 true 就【不执行右边】");
        line("       name == null 为 true → 直接短路，不会去调 trim() → 不会 NPE");
        line("    & 不短路：左右【都执行】→ name 为 null 时照样调 trim() → NPE");
        line("    → 所以必须用 ||，这不只是风格问题，是正确性问题");
        line("");

        // ══════════════════════════════════════════════
        // ⑤ 异常类型的选择
        // ══════════════════════════════════════════════
        line("⑤ 为什么抛 IllegalArgumentException");
        line("    · IllegalArgumentException = 【参数不合法】");
        line("      正好对应「调用方传了个不该传的值」这个语义");
        line("    · 对比其它选择：");
        line("        NullPointerException  → NPE 表示【程序内部有空指针 bug】，语义不对");
        line("        RuntimeException      → 太泛，丢失了「参数错误」这个信息");
        line("        自定义业务异常         → 项目里更常见（你的项目三就有 BusinessException）");
        line("    → 面试话术：校验失败要抛【语义明确】的异常，方便上层分类处理");
        line("");

        // ══════════════════════════════════════════════
        // ⑥ 实际项目里的标准写法
        // ══════════════════════════════════════════════
        line("⑥ 实际项目推荐写法");
        line("    · JDK 17 项目（你现在的项目）→ 直接用 isBlank()：");
        line("        if (name == null || name.isBlank()) { throw ...; }");
        line("    · 需要兼容 JDK 8 → 用 trim() 版本，并额外处理全角空格：");
        line("        if (name == null || name.trim().replace(\"\\u3000\", \"\").isEmpty()) { ... }");
        line("    · 想知道「纯空白」但不关心 null → String.isBlank() 本身对 null 会 NPE，");
        line("      所以 null 判断永远不能省");
        line("");

        // ══════════════════════════════════════════════
        // ⑦ 顺带一提：String 的一些易混点
        // ══════════════════════════════════════════════
        line("⑦ 顺带一提：String 的易混点");
        line("    \"\".isEmpty()      = " + "".isEmpty() + "   长度 0");
        line("    \" \".isEmpty()     = " + " ".isEmpty() + "   长度 1，不是空串");
        line("    \" \".isBlank()     = " + " ".isBlank() + "   全是空白 → true");
        line("    \" \".trim()        = \"" + " ".trim() + "\"   去掉后长度 0");
        line("    → isEmpty 看【长度】，isBlank 看【是否全是空白】");
        line("");

        line("本专题要点：");
        line("  · null 判断和空白判断【都要做】，两者管的事不一样");
        line("  · 顺序必须是 name == null || name.trim().isEmpty()（|| 短路求值保护了 trim）");
        line("  · trim() 去不掉全角空格 \\u3000，JDK 11+ 用 isBlank() 更稳");
        line("  · 校验失败抛 IllegalArgumentException（参数语义明确）");
        line("  · isEmpty 看长度，isBlank 看是否全空白");
    }

    /** 把输入描述得可读一些 */
    static String describe(String s) {
        if (s == null) {
            return "null";
        }
        if (s.isEmpty()) {
            return "\"\" (空串)";
        }
        return "\"" + s.replace("\t", "\\t").replace("\n", "\\n")
                .replace("　", "\\u3000") + "\"";
    }

    static void line(String s) {
        System.out.println(s);
    }
}
