package com.dzf.zxkj.base.framework.processor;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class OutputStreamProcessor extends BaseProcessor {

    /**
     *
     */
    protected OutputStream outputStreamPtr;

    /**
     * @param outputStreamPtr
     */
    public void setOutputStream(OutputStream outputStreamPtr) {
        this.outputStreamPtr = outputStreamPtr;
    }


    public Object processResultSet(ResultSet rs) throws SQLException {
        if (this.outputStreamPtr != null) {
            return handleRS(rs);
        }
        throw new NullPointerException("The OutputStream to write to can not be null");

    }

    /**
     * @param rs
     * @return
     * @throws SQLException
     */
    public abstract Object handleRS(ResultSet rs) throws SQLException;

}
