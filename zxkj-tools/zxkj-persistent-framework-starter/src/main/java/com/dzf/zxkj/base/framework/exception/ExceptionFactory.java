package com.dzf.zxkj.base.framework.exception;

import com.dzf.zxkj.base.framework.util.DBConsts;

import java.sql.SQLException;

/**
 * @nopublish Created by IntelliJ IDEA. User: 贺扬 Date: 2005-1-14 Time: 16:32:08
 */
public class ExceptionFactory {

    public static DbException getException(int databaseType, String msg,
                                           SQLException e) {
        switch (databaseType) {
            case DBConsts.UNKOWNDATABASE:
                return new UnKnownException(msg, e);
            case DBConsts.ORACLE:
                return new OracleException(msg, e);
            case DBConsts.OSCAR:
                return new OscarException(msg, e);
            case DBConsts.DB2:
                return new DB2Exception(msg, e);
            case DBConsts.SQLSERVER:
                return new MSSQLException(msg, e);
            case DBConsts.HSQL:
                return new HSQLException(msg, e);
            case DBConsts.POSTGRESQL:
                return new PostgresSqlException(msg, e);
            case DBConsts.GBASE:
                return new GbaseException(msg, e);
            default:
                return new UnKnownException(msg, e);
        }
    }

    public static DbException getException(int databaseType, String msg) {
        switch (databaseType) {
            case DBConsts.UNKOWNDATABASE:
                return new UnKnownException(msg);
            case DBConsts.ORACLE:
                return new OracleException(msg);
            case DBConsts.OSCAR:
                return new OscarException(msg);
            case DBConsts.DB2:
                return new DB2Exception(msg);
            case DBConsts.SQLSERVER:
                return new MSSQLException(msg);
            case DBConsts.HSQL:
                return new HSQLException(msg);
            case DBConsts.POSTGRESQL:
                return new PostgresSqlException(msg);
            case DBConsts.GBASE:
                return new GbaseException(msg);
            default:
                return new UnKnownException(msg);
        }
    }
}
