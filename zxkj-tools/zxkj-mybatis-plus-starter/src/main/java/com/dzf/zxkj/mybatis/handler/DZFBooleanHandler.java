package com.dzf.zxkj.mybatis.handler;

import com.dzf.zxkj.common.lang.DZFBoolean;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes({JdbcType.CHAR})
@MappedTypes({DZFBoolean.class})
public class DZFBooleanHandler extends BaseTypeHandler<DZFBoolean> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, DZFBoolean parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toString());
    }

    @Override
    public DZFBoolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);

        if (StringUtils.isNotEmpty(value)) {

            return new DZFBoolean(value);
        }
        return null;
    }

    @Override
    public DZFBoolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);

        if (StringUtils.isNotEmpty(value)) {
            return new DZFBoolean(value);
        }

        return null;
    }

    @Override
    public DZFBoolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);

        if (StringUtils.isNotEmpty(value)) {
            return new DZFBoolean(value);
        }

        return null;
    }
}
