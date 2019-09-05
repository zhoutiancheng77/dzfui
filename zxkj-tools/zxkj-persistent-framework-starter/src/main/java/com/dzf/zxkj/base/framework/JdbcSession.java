package com.dzf.zxkj.base.framework;

import com.dzf.zxkj.base.framework.exception.DbException;
import com.dzf.zxkj.base.framework.exception.ExceptionFactory;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.framework.util.DBConsts;
import com.dzf.zxkj.base.framework.util.DBUtil;
import com.dzf.zxkj.base.model.SuperVO;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class JdbcSession {
    private DataSource dataSource = null;

    private int maxRows = 100000;

    private int dbType = DBConsts.ORACLE;

    private int fetchSize = 40;


    /**
     * 构造默认JdbcSession该JdbcSession会默认从当前访问的DataSource得到连接
     */
    public JdbcSession() {

    }

    /**
     * 构造JdbcSession，该JdbcSession会从指定的DataSource中得到连接
     *
     * @param dataSourceName 数据源名称
     * @throws DbException 如果访问数据源出错则抛出DbException
     */
    public JdbcSession(DataSource dataSourceName) throws DbException {
        this.dataSource = dataSourceName;
    }

    /**
     * 设置是否自动添加版本(ts)信息
     *
     * @param isAddTimeStamp
     */
    public void setAddTimeStamp(boolean isAddTimeStamp) {

    }

    /**
     * 得到当前连接的FetchSize大小
     *
     * @return int 返回 FetchSize
     */
    public int getFetchSize() {
        return fetchSize;
    }

    /**
     * 设置当前连接的FetchSize大小
     *
     * @param fetchSize
     */
    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    /**
     * 设置执行最大行数
     *
     * @param maxRows
     */
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    /**
     * 得到执行最大行数
     *
     * @return
     */
    public int getMaxRows() {
        return maxRows;
    }

    /**
     * 执行有参数查询
     *
     * @param sql       查询SQL语句
     * @param parameter 查询参数
     * @param processor 结果集处理对象
     * @return 查询对象
     */
    public Object executeQuery(String sql, final SQLParameter parameter,
                               final ResultSetProcessor processor) throws DbException {
        Object result = null;
        try {
            JdbcTemplate jt = new JdbcTemplate(dataSource);

            ResultSetExtractor<Object> rse = new ResultSetExtractor<Object>() {

                @Override
                public Object extractData(ResultSet rs) throws SQLException,
                        DataAccessException {
                    // TODO Auto-generated method stub
                    return processor.handleResultSet(rs);
                }

            };
            PreparedStatementSetter pss = new PreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps) throws SQLException {

                    if (parameter != null) {
                        DBUtil.setStatementParameter(ps, parameter);
                    }

                }

            };
            jt.setMaxRows(maxRows);
            result = jt.query(sql, pss, rse);
        } catch (Exception e) {
            throw ExceptionFactory.getException(1, e.getMessage(), new SQLException(e));
        }
        return result;
    }

    /**
     * 执行无参数查询
     *
     * @param sql       查询SQL语句
     * @param processor 结果集处理对象
     * @return 查询结果对象
     */
    public Object executeQuery(String sql, final ResultSetProcessor processor)
            throws DbException {


        Object result = null;
        try {
            JdbcTemplate jt = new JdbcTemplate(dataSource);
            ResultSetExtractor<Object> rse = new ResultSetExtractor<Object>() {

                @Override
                public Object extractData(ResultSet rs) throws SQLException,
                        DataAccessException {
                    // TODO Auto-generated method stub
                    return processor.handleResultSet(rs);
                }

            };
            jt.setMaxRows(maxRows);
            result = jt.query(sql, rse);
        } catch (Exception e) {
            throw ExceptionFactory.getException(1, e.getMessage(), new SQLException(e));
        }
        return result;
    }

    /**
     * 执行有更新操作
     *
     * @param sql       预编译SQL语句
     * @param parameter 参数对象
     * @return 变化行数
     */
    public int executeUpdate(String sql, final SQLParameter parameter)
            throws DbException {
        int updateRows;
        try {
            JdbcTemplate jt = new JdbcTemplate(dataSource);
            PreparedStatementCallback<Integer> psc = new PreparedStatementCallback<Integer>() {

                @Override
                public Integer doInPreparedStatement(PreparedStatement ps)
                        throws SQLException, DataAccessException {
                    // TODO Auto-generated method stub
                    if (parameter != null) {
                        DBUtil.setStatementParameter(ps, parameter);
                    }
                    int rows = ps.executeUpdate();
                    return rows;
                }
            };

            updateRows = jt.execute(sql, psc);

        } catch (Exception e) {
            throw ExceptionFactory.getException(1, e.getMessage(), new SQLException(e));
        }
        return updateRows;
    }

    public int executeBatch(String sql, final SQLParameter[] parametersArray) throws DbException {
        try {
            JdbcTemplate jt = new JdbcTemplate(dataSource);
            BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i)
                        throws SQLException {
                    SQLParameter sp = parametersArray[i];
                    if (sp != null) {
                        DBUtil.setStatementParameter(ps, sp);
                    }

                }

                @Override
                public int getBatchSize() {
                    // TODO Auto-generated method stub
                    return parametersArray == null ? 0 : parametersArray.length;
                }

            };
            jt.batchUpdate(sql, bpss);

        } finally {

        }
        return parametersArray == null ? 0 : parametersArray.length;
    }

    public int executeBatch(Connection conn, String sql, final SQLParameter[] parametersArray) throws DbException {
        try {
            dzfJDBCTemplet jt = new dzfJDBCTemplet(dataSource);
            BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i)
                        throws SQLException {
                    SQLParameter sp = parametersArray[i];
                    if (sp != null) {
                        DBUtil.setStatementParameter(ps, sp);
                    }

                }

                @Override
                public int getBatchSize() {
                    // TODO Auto-generated method stub
                    return parametersArray == null ? 0 : parametersArray.length;
                }

            };
            int[] r = jt.batchUpdate(conn, sql, bpss);

        } finally {

        }
        return parametersArray == null ? 0 : parametersArray.length;
    }

    public SuperVO execute(ConnectionCallback<SuperVO> action) throws Exception {
        dzfJDBCTemplet djt = new dzfJDBCTemplet(dataSource);
        return djt.execute(action);
    }

    public List<Object[][]> execute1(ConnectionCallback<List<Object[][]>> action) throws Exception {
        dzfJDBCTemplet djt = new dzfJDBCTemplet(dataSource);
        return djt.execute(action);
    }

    public int executeBatch(List<String> sql, final List<SQLParameter[]> pa) throws Exception {
        int ne = 0;
        try {
            int len = sql == null ? 0 : sql.size();
            dzfJDBCTemplet djt = new dzfJDBCTemplet(dataSource);
            List<BatchPreparedStatementSetter> lb = new ArrayList<BatchPreparedStatementSetter>();
            for (int i = 0; i < len; i++) {
                final SQLParameter[] parametersArray = pa.get(i);
                BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i)
                            throws SQLException {
                        SQLParameter sp = parametersArray[i];
                        if (sp != null) {
                            DBUtil.setStatementParameter(ps, sp);
                        }

                    }

                    @Override
                    public int getBatchSize() {
                        // TODO Auto-generated method stub
                        return parametersArray == null ? 0 : parametersArray.length;
                    }

                };
                lb.add(bpss);
            }
            int[] r = djt.batchUpdate(sql, lb);
            if (r != null && r.length > 0)
                ne = r[0];
        } catch (Exception e) {
            throw e;
        }
        return ne;
    }

    /**
     * 关闭数据库连接
     */
    public void closeAll() {

    }

    /**
     * @return 返回 dbType。
     */
    public int getDbType() {
        return dbType;
    }

    private void closeConnection(Connection con) {
        try {
            if (con != null) {
                con.close();
                con = null;
            }
        } catch (SQLException e) {
        }
    }

    private void closeStmt(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        } catch (SQLException e) {
        }
    }

    private void closeRs(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (SQLException e) {
        }
    }
}
