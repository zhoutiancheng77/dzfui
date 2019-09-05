package com.dzf.zxkj.base.framework.processor;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: 贺扬
 * Date: 2005-1-14
 * Time: 13:41:47
 * 结果集处理接口
 */
public interface ResultSetProcessor extends Serializable {
    /**
     * 处理结果集并返回需要的数据结构
     *
     * @param rs 数据库结果集
     * @return 结果对象
     * @throws SQLException
     */
    public Object handleResultSet(ResultSet rs) throws SQLException;
}
