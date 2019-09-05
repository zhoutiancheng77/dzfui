package com.dzf.zxkj.base.framework.exception;

import java.sql.SQLException;

public class PostgresSqlException extends DbException {
    private static final long serialVersionUID = 7992559253225572055L;

    public PostgresSqlException(String msg, SQLException e) {
        super(msg, e);
    }

    public PostgresSqlException(String msg) {
        super(msg);
    }

    public boolean isDataIntegrityViolation() {
        return false;
    }

    public boolean isBadSQLGrammar() {
        return false;
    }

}
