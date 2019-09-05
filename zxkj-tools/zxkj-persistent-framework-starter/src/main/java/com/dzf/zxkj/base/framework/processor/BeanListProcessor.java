package com.dzf.zxkj.base.framework.processor;

import java.sql.ResultSet;
import java.sql.SQLException;


public class BeanListProcessor extends BaseProcessor {
    /**
     * <code>serialVersionUID</code> 的注释
     */
    private static final long serialVersionUID = 2260963403278654726L;
    private Class type = null;

    public BeanListProcessor(Class type) {
        this.type = type;
    }

    public Object processResultSet(ResultSet rs) throws SQLException {
        return ProcessorUtils.toBeanList(rs, type);
    }
}
