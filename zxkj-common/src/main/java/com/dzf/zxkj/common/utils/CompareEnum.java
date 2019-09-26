package com.dzf.zxkj.common.utils;

/**
 * 比较的枚举类
 * 
 * @author zjy
 */
public enum CompareEnum {
    /**
     * 大于，大于等于，等于，小于等于
     */
    GREATER(">"), GERATEREQ(">="), EQ("="), LESS("<"), LESSEQ("<=");

    /**
     * SQL的字符串
     */
    private String sqlString;

    /**
     * SQL的字符串
     * 
     * @return the sqlString
     */
    public String getSqlString() {
        return this.sqlString;
    }

    /**
     * 使用SQL字符串构造函数
     * 
     * @param sqlString SQL的字符串
     */
    private CompareEnum(String sqlString) {
        this.sqlString = sqlString;
    }

}
