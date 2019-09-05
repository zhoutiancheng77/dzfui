package com.dzf.zxkj.base.framework.processor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ColumnListProcessor extends BaseProcessor {
    /**
     * <code>serialVersionUID</code> 的注释
     */
    private static final long serialVersionUID = -851727907824262100L;


    private int columnIndex = 1;


    private String columnName = null;


    public ColumnListProcessor() {
        super();
    }


    public ColumnListProcessor(int columnIndex) {
        this.columnIndex = columnIndex;
    }


    public ColumnListProcessor(String columnName) {
        this.columnName = columnName;
    }

    public Object processResultSet(ResultSet rs) throws SQLException {
        List result = new ArrayList();
        while (rs.next()) {
            if (this.columnName == null) {
                result.add(rs.getObject(this.columnIndex));
            } else {
                result.add(rs.getObject(this.columnName));
            }
        }
        return result;
    }
}

