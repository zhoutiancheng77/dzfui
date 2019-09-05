package com.dzf.zxkj.base.framework.processor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectProcessor extends BaseProcessor {

    public ObjectProcessor() {
        // TODO Auto-generated constructor stub
    }


    @Override
    public Object processResultSet(ResultSet rs) throws SQLException {
        Object obj = null;
        if (rs.next()) {
            obj = rs.getObject(1);
            return obj;
        }
        return null;
    }

}
