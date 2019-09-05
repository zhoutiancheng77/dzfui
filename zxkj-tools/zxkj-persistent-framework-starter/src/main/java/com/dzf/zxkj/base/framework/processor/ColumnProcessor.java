package com.dzf.zxkj.base.framework.processor;

import java.sql.ResultSet;
import java.sql.SQLException;


public class ColumnProcessor extends BaseProcessor {

    /**
     * <code>serialVersionUID</code> 的注释
     */
    private static final long serialVersionUID = -2578646856668989095L;


    private int columnIndex = 1;


    private String columnName = null;


    public ColumnProcessor() {
        super();
    }


    public ColumnProcessor(int columnIndex) {
        this.columnIndex = columnIndex;
    }


    public ColumnProcessor(String columnName) {
        this.columnName = columnName;
    }

    public Object processResultSet(ResultSet rs) throws SQLException {

        if (rs.next()) {
            if (this.columnName == null) {
                return rs.getObject(this.columnIndex);
            } else {
                return rs.getObject(this.columnName);
            }

        } else {
            return null;
        }
    }
}
