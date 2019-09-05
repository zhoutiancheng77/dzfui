package com.dzf.zxkj.base.framework.util;

import com.dzf.zxkj.common.lang.*;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.type.BlobParamType;
import com.dzf.zxkj.base.framework.type.ClobParamType;
import com.dzf.zxkj.base.framework.type.NullParamType;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Calendar;


/**
 * @nopublish
 */
public class DBUtil {
    private static final BigDecimal d = new BigDecimal("0.00000001");

    public static boolean needToInt(BigDecimal b) {
        b = b.abs();
        BigDecimal c = b.subtract(new BigDecimal(b.intValue()));
        return c.compareTo(d) < 0;
    }

    public static void setStatementParameter(PreparedStatement statement,
                                             SQLParameter params) throws SQLException {
        if (statement == null || params == null)
            throw new IllegalArgumentException("不能传入空的SQLParameter!");
        for (int i = 0; i < params.getCountParams(); i++) {
            Object param = params.get(i);
            if (param == null)
                throw new IllegalArgumentException("SQLParameter中的参数值不能为空");
            if (param instanceof NullParamType) {
                statement.setNull(i + 1, ((NullParamType) param).getType());
            } else if (param instanceof Integer) {
                statement.setInt(i + 1, ((Integer) param).intValue());
            } else if (param instanceof Short) {
                statement.setShort(i + 1, ((Short) param).shortValue());
            } else if (param instanceof Timestamp) {
                statement.setTimestamp(i + 1, (Timestamp) param);
            } else if (param instanceof Time) {
                statement.setTime(i + 1, (Time) param);
            } else if (param instanceof String) {
                String s = (String) param;
                statement.setString(i + 1, s);
            } else if (param instanceof Calendar) {
                Calendar s = (Calendar) param;
                statement.setString(i + 1, new DZFDateTime(s.getTime())
                        .toString());
            } else if (param instanceof DZFTime) {
                statement.setString(i + 1, ((DZFTime) param).toString());
            } else if (param instanceof DZFBoolean) {
                statement.setString(i + 1, ((DZFBoolean) param).toString());
            } else if (param instanceof DZFDate) {
                statement.setString(i + 1, ((DZFDate) param).toString());
            } else if (param instanceof DZFDateTime) {
                statement.setString(i + 1, ((DZFDateTime) param).toString());
            } else if (param instanceof Double) {
                statement.setDouble(i + 1, ((Double) param).doubleValue());
            } else if (param instanceof DZFDouble) {
                statement.setBigDecimal(i + 1, ((DZFDouble) param)
                        .toBigDecimal());
            } else if (param instanceof Float) {
                statement.setFloat(i + 1, ((Float) param).floatValue());
            } else if (param instanceof Long) {
                statement.setFloat(i + 1, ((Long) param).longValue());
            } else if (param instanceof Boolean) {
                statement.setBoolean(i + 1, ((Boolean) param).booleanValue());
            } else if (param instanceof java.sql.Date) {
                statement.setDate(i + 1, (java.sql.Date) param);
            }
            // 如果是BLOB
            else if (param instanceof BlobParamType) {
                statement.setBytes(i + 1, ((BlobParamType) param).getBytes());
            }
            // 如果是CLOB
            else if (param instanceof ClobParamType) {
                ClobParamType clob = (ClobParamType) param;
                statement.setCharacterStream(i + 1, clob.getReader(), clob
                        .getLength());
            } else {
                statement.setObject(i + 1, param);
            }

        }
    }


    public static void closeConnection(Connection con) {
        try {
            if (con != null) {
                con.close();
                con = null;
            }
        } catch (SQLException e) {
        }
    }

    public static void closeStmt(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        } catch (SQLException e) {
        }
    }

    public static void closeRs(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (SQLException e) {
        }
    }


}
