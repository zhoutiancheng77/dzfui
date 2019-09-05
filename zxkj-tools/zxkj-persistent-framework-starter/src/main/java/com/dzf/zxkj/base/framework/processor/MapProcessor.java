package com.dzf.zxkj.base.framework.processor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: 贺扬<br>
 * Date: 2005-1-14<br>
 * Time: 15:16:17<br>
 * HashMap处理器，返回一个HashMap, 结果集中只有一行数据，其中结果集合中每一列的列名和列值对应HashMap的一个关键字和相应的值
 */
public class MapProcessor extends BaseProcessor {
    /**
     * <code>serialVersionUID</code> 的注释
     */

    private static final long serialVersionUID = 1401425123064791536L;

    public Object processResultSet(ResultSet rs) throws SQLException {
        return rs.next() ? ProcessorUtils.toMap(rs) : null;
    }
}

