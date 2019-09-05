package com.dzf.zxkj.base.framework.processor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BeanPagedListProcessor extends BaseProcessor {

    /**
     * <code>serialVersionUID</code> 的注释
     */
    private static final long serialVersionUID = -6448041138120901285L;

    private Class type = null;

    private int skip = 0;

    private int size = 10;

    public BeanPagedListProcessor(Class type, int skip, int size) {
        this.type = type;
        this.skip = skip;
        this.size = size;
    }

    public Object processResultSet(ResultSet rs) throws SQLException {
        return ProcessorUtils.toBeanList(rs, this.type, skip, size);
    }

}
