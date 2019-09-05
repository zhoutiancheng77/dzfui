/*
 * 创建日期 2005-10-10
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.dzf.zxkj.base.framework.util;

import com.dzf.zxkj.base.framework.SQLParameter;

import java.util.Arrays;
import java.util.List;


public class SQLHelper {
    public static String getInSQL(List<String> list) {
        int len = list == null ? 0 : list.size();
        char[] cs = new char[len];
        Arrays.fill(cs, 0, len, '?');
        StringBuffer s = new StringBuffer();
        s.append(new String(cs).replaceAll("\\?", "?,"));
        s.deleteCharAt(s.length() - 1);
        return "(" + s + ")";
    }

    public static SQLParameter getSQLParameter(List<String> list) {
        int len = list == null ? 0 : list.size();
        SQLParameter s = new SQLParameter();
        for (int i = 0; i < len; i++) {
            s.addParam(list.get(i));
        }
        return s;
    }

    /**
     * 根据表名和列名称得到插入语句
     *
     * @param table
     * @param names
     * @return
     */
    public static String getInsertSQL(String table, String names[]) {
        StringBuffer buffer = new StringBuffer("INSERT INTO " + table + " (");
        for (int i = 0; i < names.length; i++) {
            if (names[i].equalsIgnoreCase("ts"))
                continue;
            buffer.append(names[i] + ",");
        }
        buffer.setLength(buffer.length() - 1);
        buffer.append(") VALUES (");
        for (int i = 0; i < names.length; i++) {
            if (names[i].equalsIgnoreCase("ts"))
                continue;
            buffer.append("?,");
        }
        buffer.setLength(buffer.length() - 1);
        buffer.append(")");
        return buffer.toString();
    }

    /**
     * 根据表名和列名称得到更新语句
     *
     * @param tableName
     * @param names
     * @return
     */
    public static String getUpdateSQL(String tableName, String[] names,
                                      String pkName) {
        StringBuffer sql = new StringBuffer("UPDATE " + tableName + " SET  ");
        for (int i = 0; i < names.length; i++) {
            if (names[i].equalsIgnoreCase("ts"))
                continue;
            sql.append(names[i] + "=?,");
        }
        sql.setLength(sql.length() - 1);
        sql.append(" WHERE ").append(pkName).append("=?");
        return sql.toString();
    }

    public static String getUpdateSQL(String tableName, String[] names) {
        StringBuffer sql = new StringBuffer("UPDATE " + tableName + " SET  ");
        for (int i = 0; i < names.length; i++) {
            if (names[i].equalsIgnoreCase("ts"))
                continue;
            sql.append(names[i] + "=?,");
        }
        sql.setLength(sql.length() - 1);
        return sql.toString();
    }

    public static String getDeleteByPKSQL(String tableName, String pkName) {
//		return "DELETE FROM " + tableName + " WHERE " + pkName + "=?";
        return " update " + tableName + " set dr = 1 WHERE " + pkName + "=?";
    }

    public static String getDeleteSQL(String tableName, String[] names) {
//		StringBuffer sql = new StringBuffer("DELETE FROM " + tableName
//				+ " WHERE ");

        StringBuffer sql = new StringBuffer();
        sql.append(" update " + tableName + " set dr = 1 where ");
        for (int i = 0; i < names.length; i++) {
            sql.append(names[i] + "=? AND ");
        }
        sql.setLength(sql.length() - 4);
        return sql.toString();
    }

    /**
     * @param tableName
     * @param names
     * @param isAnd
     * @param fields
     * @return
     */
    public static String getSelectSQL(String tableName, String[] names,
                                      boolean isAnd, String[] fields) {
        StringBuffer sql = new StringBuffer();
        if (fields == null)
            sql.append("SELECT * FROM " + tableName);
        else {

            sql.append("SELECT ");
            for (int i = 0; i < fields.length; i++) {
                sql.append(fields[i] + ",");

            }
            sql.setLength(sql.length() - 1);
            sql.append(" FROM " + tableName);
        }
        String append = "AND ";
        if (!isAnd)
            append = "OR ";
        if (names == null || names.length == 0)
            return sql.toString();
        sql.append(" WHERE ");
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            sql.append(name + "=? ");
            if (i != names.length - 1)
                sql.append(append);
        }
        return sql.toString();

    }

    public static String getSelectSQL(String tableName, String[] fields) {
        StringBuffer sql = new StringBuffer();
        if (fields == null)
            sql.append("SELECT * FROM " + tableName);
        else {

            sql.append("SELECT ");
            for (int i = 0; i < fields.length; i++) {
                sql.append(fields[i] + ",");

            }
            sql.setLength(sql.length() - 1);
            sql.append(" FROM " + tableName);
        }

        return sql.toString();

    }


    public static String getSelectSQL(String tableName, String[] fields,
                                      String[] names) {
        StringBuffer sql = new StringBuffer();
        if (fields == null)
            sql.append("SELECT * FROM " + tableName);
        else {

            sql.append("SELECT ");
            for (int i = 0; i < fields.length; i++) {
                sql.append(fields[i] + ",");

            }
            sql.setLength(sql.length() - 1);
            sql.append(" FROM " + tableName);
        }
        String append = "AND ";

        if (names == null || names.length == 0)
            return sql.toString();
        sql.append(" WHERE ");
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            sql.append(name + "=? ");
            if (i != names.length - 1)
                sql.append(append);
        }
        return sql.toString();

    }
}
