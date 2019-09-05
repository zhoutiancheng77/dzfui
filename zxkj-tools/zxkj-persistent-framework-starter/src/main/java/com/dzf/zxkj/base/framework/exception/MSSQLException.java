package com.dzf.zxkj.base.framework.exception;

import java.sql.SQLException;

/**
 * @nopublish Created by IntelliJ IDEA.
 * User: 贺扬
 * Date: 2005-1-14
 * Time: 16:37:28
 */

public class MSSQLException extends DbException {
    /**
     * <code>serialVersionUID</code> 的注释
     */
    private static final long serialVersionUID = 8593535298933741800L;


    /**
     * Constructor for HSQLException.
     *
     * @param msg
     * @param e
     */
    public MSSQLException(String msg, SQLException e) {
        super(msg, e);
    }


    public MSSQLException(String msg) {
        super(msg);
    }

    public boolean isDataIntegrityViolation() { //2627,8114,8115
        switch (sqlErrorCode) {
            case 2627:
            case 8114:
            case 8115:
                return (true);
            default:
                return (false);
        }
    }


    public boolean isBadSQLGrammar() { //9207,208

        switch (sqlErrorCode) {
            case 9207:
            case 208:
                return (true); //1722 = invalid number
            default:
                return (false);
        }
    }


}

