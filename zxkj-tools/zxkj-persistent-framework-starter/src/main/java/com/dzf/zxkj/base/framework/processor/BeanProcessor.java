package com.dzf.zxkj.base.framework.processor;

import java.sql.ResultSet;
import java.sql.SQLException;


public class BeanProcessor extends BaseProcessor {

    /**
     * <code>serialVersionUID</code> 的注释
     */
    private static final long serialVersionUID = -6448041138120901285L;
    private Class type = null;

    public BeanProcessor(Class type) {
        this.type = type;
    }

    public Object processResultSet(ResultSet rs) throws SQLException {
        return ProcessorUtils.toBean(rs, this.type);
    }
}
