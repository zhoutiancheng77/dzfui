package com.dzf.zxkj.base.framework.processor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: 贺扬<br>
 * Date: 2005-1-14<br>
 * Time: 15:17:32<br>
 * HashMap集合处理器，返回一个ArrayList集合，集合中的每一个元素是一个HashMap，每个HashMap对应结果集中的一行数据, 其中结果集合中每一列的列名和列值对应HashMap的一个关键字和相应的值
 */
public class MapListProcessor extends BaseProcessor {


    /**
     * <code>serialVersionUID</code> 的注释
     */
    private static final long serialVersionUID = -8235754267454898488L;

    public Object processResultSet(ResultSet rs) throws SQLException {
        List results = new ArrayList();
        while (rs.next()) {
            results.add(ProcessorUtils.toMap(rs));
        }
        return results;
    }

}
