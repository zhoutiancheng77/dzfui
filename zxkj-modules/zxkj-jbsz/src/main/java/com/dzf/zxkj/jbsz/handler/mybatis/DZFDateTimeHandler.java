package com.dzf.zxkj.jbsz.handler.mybatis;

import com.dzf.zxkj.common.lang.DZFDateTime;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;
import java.text.SimpleDateFormat;

/**
 * @Auther: dandelion
 * @Date: 2019-09-03
 * @Description:
 */
@MappedJdbcTypes({JdbcType.CHAR})
@MappedTypes({DZFDateTime.class})
public class DZFDateTimeHandler extends BaseTypeHandler<DZFDateTime> {

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, DZFDateTime parameter, JdbcType jdbcType) throws SQLException {
        if(jdbcType != null && jdbcType.equals(JdbcType.TIMESTAMP)){
            ps.setTimestamp(i, new Timestamp(parameter.getMillis()));
        }else{
            ps.setString(i, parameter.toString());
        }
    }

    @Override
    public DZFDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {

        String value = rs.getString(columnName);

        if(StringUtils.isNotEmpty(value)){
            if(value.length() > 19){
                value = value.substring(0,19);
            }
            return new DZFDateTime(value);
        }
        return null;
    }

    @Override
    public DZFDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {

        String value = rs.getString(columnIndex);

        if(StringUtils.isNotEmpty(value)){
            return new DZFDateTime(value);
        }

        return null;
    }

    @Override
    public DZFDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {

        String value = cs.getString(columnIndex);

        if(StringUtils.isNotEmpty(value)){
            return new DZFDateTime(value);
        }

        return null;
    }
}
