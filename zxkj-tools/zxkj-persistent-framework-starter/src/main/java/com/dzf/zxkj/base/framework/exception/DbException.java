package com.dzf.zxkj.base.framework.exception;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: 贺扬<br>
 * Date: 2005-1-14<br>
 * Time: 16:32:42<br>
 * DbException是一个抽象异常类， 它被用来处理SQLException和我们的数据访问异常之间的转换。在数据访问框架中提供了不同数据库对不同DbException的实现并对每种数据库的SQLErrorCode进行了处理，统一转化成不同的方法，通过DbException能够精确的指定出数据访问过程中所出现的问题。
 */
public abstract class DbException extends Exception {

    protected int sqlErrorCode = 0;


    protected String SQLState = null;


    public abstract boolean isDataIntegrityViolation();


    public abstract boolean isBadSQLGrammar();

    protected SQLException realException;


    public DbException(String msg, SQLException e) {
        super(msg, e);
        realException = e;
        sqlErrorCode = e.getErrorCode();
        SQLState = e.getSQLState();
    }


    public DbException(String msg) {
        super(msg);
        sqlErrorCode = -1;
        SQLState = null;
    }


    public int getSQLErrorCode() {
        return (sqlErrorCode);
    }


    public String getSQLState() {
        return (SQLState);
    }

    public SQLException getRealException() {
        return realException;
    }
}

