package com.dzf.zxkj.platform.util;

import com.dzf.zxkj.common.utils.StringUtil;

public class NumberToCN {
    /**
     * 汉语中数字大写
     */
    private static final String[] CN_UPPER_NUMBER = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    /**
     * 汉语中货币单位大写，这样的设计类似于占位符
     */
    private static final String[] CN_UPPER_MONETRAY_UNIT1 = {"元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿"};
    private static final String[] CN_UPPER_MONETRAY_UNIT = {"分", "角",};

    public static void main(String[] args) {
        String str = "1250.03";
        StringBuffer sb = convertZs(str.split("\\.")[0]);
        StringBuffer sb1 = convertXs(str.split("\\.")[1]);
        System.out.println(sb.toString() + sb1.toString());
    }

    public static String getZnValue(String value) {
        if (!StringUtil.isEmpty(value) && value.startsWith("-")) {
            value = value.substring(1);
        }
        StringBuffer sb = convertZs(value.split("\\.")[0]);
        StringBuffer sb1 = convertXs(value.split("\\.")[1]);
//		System.out.println(sb.toString()+sb1.toString());
        return sb.toString() + sb1.toString();
    }

    private static StringBuffer convertXs(String str) {
        char[] chars = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = chars.length - 1; i >= 0; i--) {
            int index = new Integer(new String(new char[]{chars[i]}));
            sb.insert(0, CN_UPPER_NUMBER[index] + "" + CN_UPPER_MONETRAY_UNIT[(chars.length - 1) - i]);
        }
        if ("零角零分".equals(sb.toString())) {
            sb = new StringBuffer("整");
        }
        return sb;
    }

    private static StringBuffer convertZs(String str) {
        char[] chars = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = chars.length - 1; i >= 0; i--) {
            int index = new Integer(new String(new char[]{chars[i]}));
            sb.insert(0, CN_UPPER_NUMBER[index] + "" + CN_UPPER_MONETRAY_UNIT1[(chars.length - 1) - i]);
        }
//		for(int i = chars.length;i<CN_UPPER_MONETRAY_UNIT1.length;i++ ){
//			sb.insert(0, "  "+CN_UPPER_MONETRAY_UNIT1[i]+"  ");
//		}
        return sb;
    }

}
