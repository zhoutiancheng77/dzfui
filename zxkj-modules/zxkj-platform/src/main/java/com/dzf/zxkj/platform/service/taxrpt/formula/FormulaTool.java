package com.dzf.zxkj.platform.service.taxrpt.formula;

import com.dzf.zxkj.secret.CorpSecretUtil;
import com.google.gson.Gson;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class FormulaTool {
    // 上下文
    public HashMap<String, Object> context;

    public String sheetName; // 当前sheet页的名称
    // 取数sheet页（支持使用R1C2之类的表达式从sheet取数）
    public LinkedHashMap sheet;

    // 明细行序号（mxxh）计数器
    public int mxxh = 0;

    public FormulaTool() {
        addObject("this", this); // 表达式中使用this可以调用FormulaTool中的成员方法，如：this.getXH()
    }

    public FormulaTool(LinkedHashMap sheet) {
        this();
        this.sheet = sheet;
    }

    /**
     * 设置从报表取数的sheet页
     * @param sheet
     */
    public void setDataSheet(LinkedHashMap sheet)
    {
        this.sheetName = (String)sheet.get("name");
        this.sheet = sheet;
    }

    /**
     * 将对象加入上下文，在脚本中可以使用，如：obj.abc、obj.func()等
     * @param objName
     * @param obj
     */
    public void addObject(String objName, Object obj)
    {
        if (context == null)
            context = new HashMap<>();
        context.put(objName, obj);
    }

    /**
     * 取上下文中的对象
     * @param objName
     * @return
     */
    public Object getObject(String objName)
    {
        if (context != null && context.containsKey(objName))
            return context.get(objName);
        return null;
    }

    /**
     * 计算表达式
     * @param expression
     * @return
     */
    public Object evaluate(String expression) {
        //region 旧代码
        /*
        // 1、数值常量和字符串常量的识别

        //匹配数值，如：123、12.34
        if (expression.matches("\\d+")) {
            return Integer.valueOf(expression);
        }
        if (expression.matches("\\d*\\.\\d*")) {
            return Double.valueOf(expression);
        }
        //匹配字符串常量，如："ABC"或者'ABC'
        if (expression.matches("(\"[^\"]*\"|'[^']*')")) {
            return expression.substring(1, expression.length() - 1);
        }

        //上下文中的原始类型对象，直接返回其值
        if (context != null && context.containsKey(expression)) {
            return context.get(expression);
        }

        //4、Excel单元格取数特别处理，不走上下文中对象的属性或函数
        Pattern p = Pattern.compile("[Rr](\\d+)[Cc](\\d+)");
        Matcher matcher = p.matcher(expression);
        if (matcher.matches()) {
            int rowno = Integer.parseInt(matcher.group(1));
            int colno = Integer.parseInt(matcher.group(2));
            Object value = getCellValue(sheet, rowno, colno);
            // 把单元格中的空格去掉
            if (value instanceof String) {
                value = ((String)value).trim();
            }
            return value;
        }
        */
        //endregion

        // java正则表达式不支持平衡组。改用antlr来做表达式解析，以支持函数嵌套
        /*
        测试表达式：
        1+2*3
        'abc' + "def" + 12 + 1.3
        'abc' + "def" + (12 - 1.3)
        qcLines.get(splitString(R1C1,2)).get("sl") + report.vsoccrecode
        mi(c('zz'), d.e(f.xx, h(1)))
        */

        TaxFormulaParser parser = getParser(expression);
        TaxFormulaBaseVisitor vistor = new TaxFormulaBaseVisitor(this);
        Object value = vistor.visitExpr(parser.expr());
        return value;
    }

    /**
     * 按格式串格式化表达式计算结果，返回String
     * @param expression
     * @param formatstring
     * @return
     */
    public String evaluate(String expression, String formatstring) {
        return null;
    }

    /**
     * 得到 antlr Parser
     * @param formula
     * @return
     */
    public static TaxFormulaParser getParser(String formula) {
        //ANTLRInputStream antlrInputStream = new ANTLRInputStream(code);
        CharStream charStream = CharStreams.fromString(formula);
        TaxFormulaLexer lexer = new TaxFormulaLexer(charStream);
        TaxFormulaParser parser = new TaxFormulaParser(new CommonTokenStream(lexer));
        return parser;
    }

    /**
     * 得到明细行的序号（mxxh）
     * @return
     */
    public int getXH() {
        return ++mxxh;
    }

    /**
     * 从当前sheet页中取出iRow行、iColumn列的单元格的值
     * @param iRow
     * @param iColumn
     * @return
     */
    public Object getCellValue(int iRow, int iColumn)
    {
        if (this.sheet == null)
            return null;

        Object objValue = null;
        LinkedHashMap data = (LinkedHashMap)this.sheet.get("data");
        LinkedHashMap dataTable = (LinkedHashMap)data.get("dataTable");
        if (dataTable.containsKey("" + iRow)) {
            LinkedHashMap row = (LinkedHashMap)dataTable.get("" + iRow);
            if (row.containsKey("" + iColumn)) {
                LinkedHashMap cell = (LinkedHashMap)row.get("" + iColumn);
                objValue = cell.get("value");
            }
        }
        return objValue;
    }

    /**
     * 从指定sheet页中取出iRow行、iColumn列的单元格的值
     * @param sheet 指定sheet页
     * @param iRow
     * @param iColumn
     * @return
     */
    public static Object getCellValue(LinkedHashMap sheet, int iRow, int iColumn)
    {
        Object objValue = null;
        LinkedHashMap data = (LinkedHashMap)sheet.get("data");
        LinkedHashMap dataTable = (LinkedHashMap)data.get("dataTable");
        if (dataTable.containsKey("" + iRow)) {
            LinkedHashMap row = (LinkedHashMap)dataTable.get("" + iRow);
            if (row.containsKey("" + iColumn)) {
                LinkedHashMap cell = (LinkedHashMap)row.get("" + iColumn);
                objValue = cell.get("value");
            }
        }
//        if (objValue == null)
//            objValue = "";
        return objValue;
    }

    // 静态方法。可以在表达式中直接引用，如：splitString(RXC10,1)

    // 取当前日期或时间
    public static String getCurrDate() {
        return getCurrDate("yyyy-MM-dd");
    }

    /**
     * 取当前日期或时间。用于填报日期、申请日期、受理日期等
     * @param fmtstr 格式串
     * @return
     */
    public static String getCurrDate(String fmtstr) {
        SimpleDateFormat df = new SimpleDateFormat(fmtstr);
        return df.format(new Date());
    }

    // 字符串拆分
    public static String splitString(String srcString, Integer num)
    {
        return splitString(srcString, "\\||-", num);
    }

    /**
     * 字符串拆分，拆“编码|名称”等
     * @param srcString
     * @param separator 分隔符
     * @param num
     * @return
     */
    public static String splitString(String srcString, String separator, Integer num)
    {
        String[] values = srcString.split(separator);
        if (num < 0 || num > values.length)
            return "";
        else
            return values[num - 1].trim();
    }

    // 对编码名称等进行解码
    public static String deCode(String code) {
        return CorpSecretUtil.deCode(code);
    }

    //public static String iif(boolean condition, String truepart, String falsepart)
}
