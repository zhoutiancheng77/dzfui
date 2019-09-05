package com.dzf.zxkj.base.framework.exception;

import java.sql.SQLException;

/**
 * @nopublish Created by IntelliJ IDEA.
 * User: 贺扬
 * Date: 2005-1-14
 * Time: 16:37:28
 * 未知的数据库异常
 */
public class UnKnownException extends DbException {
    /**
     * <code>serialVersionUID</code> 的注释
     */
    private static final long serialVersionUID = 7992559253225572055L;


    /**
     * Constructor for HSQLException.
     *
     * @param msg
     * @param e
     */
    public UnKnownException(String msg, SQLException e) {
        super(msg, e);
    }


    public UnKnownException(String msg) {
        super(msg);
    }

    public boolean isDataIntegrityViolation() {
        return false;
    }


    public boolean isBadSQLGrammar() { //-22,-28
        return false;
    }


}

