package com.dzf.zxkj.base.framework.exception;

import java.sql.SQLException;

/**
 * @nopublish Created by IntelliJ IDEA.
 * User: 贺扬
 * Date: 2005-1-14
 * Time: 16:37:28
 */
public class DB2Exception extends DbException {
    /**
     * <code>serialVersionUID</code> 的注释
     */
    private static final long serialVersionUID = 1938684135450102342L;


    /**
     * Constructor for  DB2Exception.
     *
     * @param s
     * @param e
     */
    public DB2Exception(String msg, SQLException e) {
        super(msg, e);
    }


    public DB2Exception(String msg) {
        super(msg);
    }

    public boolean isDataIntegrityViolation() {
        switch (sqlErrorCode) {
            case -803:
                return (true);
            default:
                return (false);
        }
    }


    public boolean isBadSQLGrammar() { //-204,-206,-301,-408

        switch (sqlErrorCode) {
            case -204:
            case -206:
            case -301:
            case -408:
                return (true); //1722 = invalid number
            default:
                return (false);
        }
    }


}

