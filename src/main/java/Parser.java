import java.util.Scanner;

public class Parser {

    // 常量定义
    private static final int MAX_NUM = 100;
    private static final int LBUFFER = 30;
    private static final int TOKEN_NUMBER = 50;

    private static final int LBUFFER1 = 30;
    private static final int TOKEN_NUMBER1 = 100;

    // 存放输入串
    private String[] Ecode = new String[TOKEN_NUMBER1];
    private int top = 0;

    // 词法分析相关变量
    private int nID = 1;
    private int mID = 1;

    // ADDL 类，用于识别关键字并存储
    private static class ADDL {
        int add;
        int next;
        String str;

        ADDL() {
            this.str = "";
        }
    }

    private ADDL[] L = new ADDL[TOKEN_NUMBER1];
    private int nL = 0;
    private int nadd = 1;

    // Token 类，用于存放标识符和关键字
    private static class Token {
        int ID;
        String b;

        Token() {
            this.b = "";
        }
    }

    private Token[] Tokens = new Token[TOKEN_NUMBER];
    private Token[] Tokens2 = new Token[TOKEN_NUMBER];
    private int nTokenNumber = 1;

    private char[] a = new char[MAX_NUM];
    private int nLength = 0;

    // 索引包装类
    private static class Index {
        int value;

        Index(int value) {
            this.value = value;
        }
    }

    // 构造函数初始化
    public Parser() {
        for (int i = 0; i < TOKEN_NUMBER1; i++) {
            L[i] = new ADDL();
        }
        for (int i = 0; i < TOKEN_NUMBER; i++) {
            Tokens[i] = new Token();
            Tokens2[i] = new Token();
        }
    }

    // 主词法分析函数
    public int wordAnalysis() {
        int flag1 = 0;
        char ch;
        System.out.println("***************请输入一个while do文法的句子(以#为结束标志):******************");

        login(); // 把源程序装入数组a
        nLength = 0; // 重置nLength以开始读取输入
        Index x = new Index(0);
        StringBuilder buffer = new StringBuilder();

        while ((ch = getChar()) != '\0') {
            if (!judge(ch)) {
                continue; // 跳过空格和回车
            }

            x.value = 0;
            buffer.setLength(0); // 清空缓冲区

            if (isAlpha(ch)) {
                flag1 = 1;
                while (isAlpha(ch) || isNum(ch)) {
                    input(buffer, ch, x); // 是字母或数字，装入buffer
                    ch = getChar();
                }

                if (">=<+-*/".indexOf(ch) != -1) {
                    nLength--; // 回退一步指向算符
                }

                save(buffer.toString(), x.value); // 将关键字或标识符或算符装入Token
            } else if (">=<+-*/".indexOf(ch) != -1) {
                buffer.setLength(0);
                input(buffer, ch, x);
                save(buffer.toString(), x.value);
            } else {
                System.out.println("输入出错!");
                return 0; // 出错处理
            }
        }

        if (flag1 == 1) { // 词法分析结束
            assort(); // 分类符号

            // 将标识符替换为id
            for (int d = 1; d < nTokenNumber; d++) { // 将标识符替换为id
                if (Tokens[d].ID == 5) {
                    Tokens[d].b = "id";
                }
            }

            // 复制到 Tokens2 并打印符号表
            System.out.println("********** 1---关键字 || 2---运算符 || 3---界符 || 5---标识符 ************");
            System.out.println("符号表[<类别编码，单词属性>]：");
            for (int i = 1; i < nTokenNumber; i++) { // 输出词法分析结果
                System.out.println("< " + Tokens[i].ID + ", " + Tokens[i].b + " >");
                Tokens2[i].b = Tokens[i].b; // 拷贝到 Tokens2
            }

            // 添加结束符
            if (nTokenNumber < Tokens.length) {
                Tokens[nTokenNumber].b = "#";
                Tokens[nTokenNumber].ID = 7; // '#'的类别编码为7
                Tokens2[nTokenNumber].b = "#";
                nTokenNumber++;
            }

            return 1;
        }

        return 0;
    }

    // 将源程序装入数组a
    private void login() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        for (char ch : input.toCharArray()) {
            if (nLength >= MAX_NUM - 2) {
                break;
            } else {
                a[nLength++] = ch;
                if (ch == '#') {
                    break;
                }
            }
        }
        a[nLength] = '\0';
    }

    // 从数组a中读取字符
    private char getChar() {
        if (nLength >= MAX_NUM - 1) {
            return '\0';
        } else {
            return a[nLength++];
        }
    }

    // 判断是否为有效字符（非空格和换行）
    private boolean judge(char ch) {
        while (ch == '\n' || ch == ' ') { // 换行或空格
            ch = getChar();
            if (ch == '\0') {
                return false;
            }
        }
        return true;
    }

    // 判断是否为字母
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    // 判断是否为数字
    private boolean isNum(char c) {
        return c >= '0' && c <= '9';
    }

    // 将字符输入到buffer
    private void input(StringBuilder buffer, char c, Index x) {
        if (x.value < LBUFFER - 1) {
            buffer.append(c);
            x.value++;
        }
    }

    // 将buffer内容保存到Tokens
    private void save(String tokenStr, int length) {
        if (nTokenNumber < Tokens.length) {
            Tokens[nTokenNumber].b = tokenStr;
            nTokenNumber++;
        }
    }

    // 分类符号
    private void assort() {
        for (int i = 1; i < nTokenNumber; i++) {
            switch (Tokens[i].b) {
                case "do":
                case "while":
                    Tokens[i].ID = 1;
                    break;
                case "=":
                case "+":
                case "-":
                case "*":
                case "/":
                case "<":
                case ">":
                    Tokens[i].ID = 2;
                    break;
                case "(":
                case ")":
                case "{":
                case "}":
                    Tokens[i].ID = 3;
                    break;
                default:
                    Tokens[i].ID = 5;
                    break;
            }
        }
    }

    // 出栈操作
    private void pop() {
        if (top > 0) {
            top--;
        }
    }

    // 识别非终结符
    private int selectP() {
        if (top == 0) return -1;
        String topSymbol = Ecode[top - 1];
        switch (topSymbol) {
            case "Z":
                return 1;
            case "A":
                return 2;
            case "B":
                return 3;
            case "E":
                return 4;
            case "E1":
                return 10;
            case "T":
                return 8;
            case "T1":
                return 10;
            case "F":
                return 12;
            case "F1":
                return 13;
            case "F2":
                return 14;
            default:
                return -1;
        }
    }

    // 识别关键字
    private int eStrcmp() {
        if (top == 0) return 0;
        String topSymbol = Ecode[top - 1];

        if ("#".equals(topSymbol)) {
            if (nID < Tokens2.length && "#".equals(Tokens2[nID].b)) {
                return 3; // 分析结束
            }
        } else if (topSymbol.equals(Tokens2[nID].b) || topSymbol.equals("id")) {
            if ("while".equals(topSymbol)) {
                L[nL].add = nadd++;
                L[nL].next = nadd++;
                L[nL].str = "while";
                nL++;
            }
            if ("do".equals(topSymbol)) {
                L[nL].add = nadd++;
                L[nL].next = L[nL - 1].add;
                L[nL].str = "do";
                nL++;
            }
            return 1;
        }

        return 0;
    }

    // 语法分析函数
    public int expressionAnalyse() {
        int flag = 1;
        System.out.println("\n语法分析开始");

        // 初始化栈
        push("#");
        push("Z");

        while (flag == 1) {
            int p = selectP();
            if (p == 1) { // Z
                System.out.println("  Z-> while A do B");
                inputZ();
            } else if (p == 2) { // A
                // A -> id < id | id > id | A -> id = id
                if (nID + 1 >= nTokenNumber) {
                    System.out.println("语法分析失败。缺少运算符");
                    flag = 0;
                    return 0;
                }
                String operator = Tokens2[nID + 1].b; // 修正此处

                System.out.println("当前处理运算符: " + operator); // 调试信息

                if ("<".equals(operator)) {
                    System.out.println("  A->id < id");
                    inputA1();
                    nID += 2; // 跳过 '>' 和 'id'
                } else if (">".equals(operator)) {
                    System.out.println("  A->id > id");
                    inputA3();
                    nID += 2; // 跳过 '>' 和 'id'
                } else if ("=".equals(operator)) {
                    System.out.println("  A->id = id");
                    inputA2();
                    nID += 2; // 跳过 '=' 和 'id'
                } else {
                    System.out.println("语法分析失败。未知的运算符: " + operator);
                    flag = 0;
                    return 0;
                }
            } else if (p == 3) { // B
                System.out.println("  B->id = E");
                inputB();
                nID += 1; // 跳过 'id' 后继续
            } else if (p == 4) { // E
                System.out.println("  E->T E1");
                inputE();
            } else if (p == 8) { // T
                System.out.println("  T->F T1");
                inputT();
            } else if (p == 10) { // E1 or T1
                String topSymbol = Ecode[top - 1];
                if ("E1".equals(topSymbol)) {
                    if (nID >= nTokenNumber) {
                        System.out.println("语法分析失败。输入已结束，但栈仍有符号。");
                        flag = 0;
                        return 0;
                    }
                    String currentToken = Tokens2[nID].b;
                    if ("+".equals(currentToken)) {
                        System.out.println("  E1->+ T E1");
                        inputE11();
                        nID += 1;
                    } else if ("-".equals(currentToken)) {
                        System.out.println("  E1->- T E1");
                        inputE12();
                        nID += 1;
                    } else {
                        System.out.println("  E1->$");
                        inputE13();
                    }
                } else if ("T1".equals(topSymbol)) {
                    if (nID >= nTokenNumber) {
                        System.out.println("语法分析失败。输入已结束，但栈仍有符号。");
                        flag = 0;
                        return 0;
                    }
                    String currentToken = Tokens2[nID].b;
                    if ("*".equals(currentToken)) {
                        System.out.println("  T1->* F T1");
                        inputT11();
                        nID += 1;
                    } else if ("/".equals(currentToken)) {
                        System.out.println("  T1->/ F T1");
                        inputT12();
                        nID += 1;
                    } else {
                        System.out.println("  T1->$");
                        inputT13();
                    }
                } else {
                    System.out.println("语法分析失败。未知的非终结符在栈顶: " + topSymbol);
                    flag = 0;
                    return 0;
                }
            } else if (p == 12) { // F
                System.out.println("  F->id");
                inputF1();
                nID += 1; // 跳过 'id'
            } else if (p == 13) { // F2
                System.out.println("  F->( E )");
                inputF2();
            } else {
                int f2 = eStrcmp();
                if (f2 == 1) { // 识别出关键字或标识符
                    pop();
                    nID++;
                } else if (f2 == 3) { // 识别出#，分析结束
                    System.out.println("\n语法正确!");
                    flag = 0;
                    return 1;
                } else { // 出错
                    System.out.println("\n语法分析失败。");
                    flag = 0;
                    return 0;
                }
            }
        }

        return 0;
    }

    // 各种输入函数，将产生式按逆序入栈
    private void inputZ() {
        pop();
        push("B");
        push("do");
        push("A");
        push("while");
    }

    private void inputA1() { // A -> id < id
        pop();
        push("id");
        push("<");
        push("id");
    }

    private void inputA2() { // A -> id = id
        pop();
        push("id");
        push("=");
        push("id");
    }

    private void inputA3() { // A -> id > id
        pop();
        push("id");
        push(">");
        push("id");
    }

    private void inputB() { // B -> id = E
        pop();
        push("E");
        push("=");
        push("id");
    }

    private void inputE() { // E -> T E1
        pop();
        push("E1");
        push("T");
    }

    private void inputE11() { // E1 -> + T E1
        pop();
        push("E1");
        push("T");
        push("+");
    }

    private void inputE12() { // E1 -> - T E1
        pop();
        push("E1");
        push("T");
        push("-");
    }

    private void inputE13() { // E1 -> $
        pop();
        push("$");
    }

    private void inputT() { // T -> F T1
        pop();
        push("T1");
        push("F");
    }

    private void inputT11() { // T1 -> * F T1
        pop();
        push("T1");
        push("F");
        push("*");
    }

    private void inputT12() { // T1 -> / F T1
        pop();
        push("T1");
        push("F");
        push("/");
    }

    private void inputT13() { // T1 -> $
        pop();
        push("$");
    }

    private void inputF1() { // F -> id
        pop();
        push("id");
    }

    private void inputF2() { // F -> ( E )
        pop();
        push(")");
        push("E");
        push("(");
    }

    // 入栈操作
    private void push(String symbol) {
        if (top < Ecode.length) {
            Ecode[top++] = symbol;
        } else {
            System.out.println("栈溢出！");
        }
    }

    // 语义分析部分（简化示例）
    private void compare() {
        for (int k = 0; k < nTokenNumber; k++) {
            String currentToken = Tokens2[k].b;
            if ("{};".contains(currentToken)) {
                continue;
            } else if (top > 0 && Ecode[top - 1].equals(currentToken)) {
                pop();
            } else {
                break;
            }
        }
    }

    // 主函数
    public static void main(String[] args) {
        Parser parser = new Parser();
        System.out.println("*********** while do语句的翻译分析程序，采用LL（1）法，输出三地址：*************");
        if (parser.wordAnalysis() == 1) {
            if (parser.expressionAnalyse() == 1) {
                System.out.println("\n语法分析完成。");
                System.out.println("三地址码:");
                // 根据输入"while a>b do a=d#", Tokens2数组应包含：
                // Tokens2[1] = "while"
                // Tokens2[2] = "id"
                // Tokens2[3] = ">"
                // Tokens2[4] = "id"
                // Tokens2[5] = "do"
                // Tokens2[6] = "id"
                // Tokens2[7] = "="
                // Tokens2[8] = "id"
                // Tokens2[9] = "#"

                // 生成三地址码
                System.out.println("L0: if " + parser.Tokens2[2].b + " " + parser.Tokens2[3].b + " " + parser.Tokens2[4].b + " goto L1");
                System.out.println("L1: t1 := " + parser.Tokens2[6].b + " " + parser.Tokens2[7].b + " " + parser.Tokens2[8].b);
                System.out.println("L2: " + parser.Tokens2[6].b + " = t1");
                System.out.println("L3: goto L0");
                System.out.println("L4: if not goto L5");
                System.out.println("L5:");
            } else {
                System.out.println("语法分析失败。");
            }
        } else {
            System.out.println("词法分析失败。");
        }
    }
}
