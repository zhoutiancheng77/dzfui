package com.dzf.zxkj.base.framework.processor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: 贺扬<br>
 * Date: 2005-1-27<br>
 * Time: 10:18:50<br>
 * Vector集合处理器，返回一个Vector集合，集合中的每一个元素是Vector，每个Vector对应结果集中的一行数据，其中结果集中每一列对应Vector的一个元素。
 */
public class VectorProcessor extends BaseProcessor {
    /**
     * <code>serialVersionUID</code> 的注释
     */
    private static final long serialVersionUID = 5065890184377521414L;

    public Object processResultSet(ResultSet rs) throws SQLException {
        return ProcessorUtils.toVector(rs);
    }
}
