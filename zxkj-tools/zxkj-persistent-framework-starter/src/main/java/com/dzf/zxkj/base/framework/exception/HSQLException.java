package com.dzf.zxkj.base.framework.exception;

import java.sql.SQLException;

/**
 * @nopublish Created by IntelliJ IDEA.
 * User: 贺扬
 * Date: 2005-1-14
 * Time: 16:37:28
 */
public class HSQLException extends DbException {
    /**
     * <code>serialVersionUID</code> 的注释
     */
    private static final long serialVersionUID = 8531564431961311599L;


    /**
     * Constructor for HSQLException.
     *
     * @param s
     * @param e
     */
    public HSQLException(String msg, SQLException e) {
        super(msg, e);
    }


    public HSQLException(String msg) {
        super(msg);
    }

    public boolean isDataIntegrityViolation() {
        switch (sqlErrorCode) {
            case -9:
                return (true); //1722 = invalid number
            default:
                return (false);
        }
    }


    public boolean isBadSQLGrammar() { //-22,-28

        switch (sqlErrorCode) {
            case -22:
            case -28:
                return (true);
            default:
                return (false);
        }
    }


}

