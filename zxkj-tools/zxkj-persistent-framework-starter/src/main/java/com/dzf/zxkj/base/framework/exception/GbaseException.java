package com.dzf.zxkj.base.framework.exception;

import java.sql.SQLException;

public class GbaseException extends DbException {

    private static final long serialVersionUID = 7078317477056012707L;

    public GbaseException(String msg, SQLException e) {
        super(msg, e);
    }

    public GbaseException(String msg) {
        super(msg);
    }

    public boolean isDataIntegrityViolation() {
        switch (sqlErrorCode) {

        }
        return false;
    }

    public boolean isBadSQLGrammar() {
        switch (sqlErrorCode) {

        }
        return false;
    }

}
