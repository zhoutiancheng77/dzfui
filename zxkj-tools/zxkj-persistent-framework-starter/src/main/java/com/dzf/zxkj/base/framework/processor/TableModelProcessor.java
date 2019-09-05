package com.dzf.zxkj.base.framework.processor;

import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

public class TableModelProcessor extends BaseProcessor {

    /**
     *
     */
    public Object processResultSet(ResultSet rs) throws SQLException {
        DefaultTableModel model = new DefaultTableModel();
        // the table headers
        ResultSetMetaData rsMeta = rs.getMetaData();
        int cols = rsMeta.getColumnCount();
        for (int i = 1; i <= cols; i++) {
            model.addColumn(rsMeta.getColumnName(i));
        }
        // the table data
        while (rs.next()) {
            Vector<Object> data = new Vector<Object>();
            for (int i = 1; i <= cols; i++) {
                data.add(rs.getObject(i));
            }
            model.addRow(data);
        }

        return model;
    }


}