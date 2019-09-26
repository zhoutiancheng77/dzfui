package com.dzf.zxkj.common.utils;

import java.util.ArrayList;
import java.util.List;

public class SqlUtil {
    private static final int SQL_IN_LIST_LIMIT = 100;

    public static String[] buildSqlCondForInByGroup(final String[] value,
                                                    int groupNum) {
        if (value == null || value.length == 0) {
            return new String[0];
        }
        if (groupNum <= 0) {
            throw new IllegalArgumentException("groupNum must > 0");
        }
        List<String> retValues = new ArrayList<String>();
        String[] subpk = new String[groupNum];
        int first = value.length / groupNum;
        int mode = value.length % groupNum;
        for (int i = 0; i < first; i++) {

            final int jStart = i * groupNum;
            final int jEnd = (i + 1) * groupNum;
            for (int j = jStart; j < jEnd; j++) {
                subpk[j % groupNum] = value[j];
            }
            retValues.add(buildSqlConditionForIn(subpk));
        }
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < mode; i++) {
            list.add(value[first * groupNum + i]);
        }
        if (list.size() > 0) {
            retValues.add(buildSqlConditionForIn(list.toArray(new String[0])));
        }
        return retValues.toArray(new String[0]);
    }

    /**
     * <p>
     * 把一组值拼成sql语句使用的in形式,按照字符串形式
     * <p>
     * 作者：hzguo <br>
     * 日期：2006-7-5
     *
     * @param value
     * @return
     */
    public static String buildSqlConditionForIn(final String[] value) {
        int length = ArrayUtil.getArrayLength(value);
        if (length == 0) {
            return "";
        }
        StringBuffer retValue = new StringBuffer();
        for (int i = 0; i < length - 1; i++) {
            retValue.append("'");
            retValue.append(value[i]);
            retValue.append("'");
            retValue.append(",");
        }
        retValue.append("'");
        retValue.append(value[value.length - 1]);
        retValue.append("'");
        return retValue.toString();
    }

    public static String buildSqlConditionForInWithoutDot(final String[] value) {
        int length = ArrayUtil.getArrayLength(value);
        if (length == 0) {
            return "";
        }
        StringBuffer retValue = new StringBuffer();
        for (int i = 0; i < length - 1; i++) {
            retValue.append(value[i]);
            retValue.append(",");
        }
        retValue.append(value[value.length - 1]);
        return retValue.toString();
    }

    public static String buildSqlForIn(final String fieldname,
                                       final String[] fieldvalue) {
        StringBuffer sbSQL = new StringBuffer();
        sbSQL.append("(" + fieldname + " IN ( ");
        int len = fieldvalue.length;
        // 循环写入条件
        for (int i = 0; i < len; i++) {
            String value = fieldvalue[i];
            if (StringUtil.isEmptyWithTrim(value)) {
                value = "NULL~~null";
            }
            //zpm修改，防止返回null,造成sql 错误。
//			if (value != null && value.trim().length() > 0) {
            sbSQL.append("'").append(value).append("'");
            // 单独处理 每个取值后面的",", 对于最后一个取值后面不能添加"," 并且兼容 oracle 的 IN 254 限制。每
            // 200 个 数据 or 一次。时也不能添加","
            if (i != (fieldvalue.length - 1)
                    && !(i > 0 && (i + 1) % SQL_IN_LIST_LIMIT == 0)) {
                sbSQL.append(",");
            }
//			} else {
//				return null;
//			}

            // 兼容 oracle 的 IN 254 限制。每 200 个 数据 or 一次。
            if (i > 0
                    && (i + 1) % SQL_IN_LIST_LIMIT == 0
                    && i != (fieldvalue.length - 1)) {
                sbSQL.append(" ) OR ").append(fieldname).append(" IN ( ");
            }
        }
        sbSQL.append(" )) ");

        return sbSQL.toString();
    }

    private SqlUtil() {

    }
}
