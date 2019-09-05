package com.dzf.zxkj.base.framework;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class dzfJDBCTemplet extends JdbcTemplate {

    public dzfJDBCTemplet() {
        // TODO Auto-generated constructor stub
    }

    public dzfJDBCTemplet(DataSource dataSource) {
        super(dataSource);
        // TODO Auto-generated constructor stub
    }

    public dzfJDBCTemplet(DataSource dataSource, boolean lazyInit) {
        super(dataSource, lazyInit);
        // TODO Auto-generated constructor stub
    }

    private static class SimplePreparedStatementCreator implements PreparedStatementCreator, SqlProvider {

        private final String sql;

        public SimplePreparedStatementCreator(String sql) {
            Assert.notNull(sql, "SQL must not be null");
            this.sql = sql;
        }

        @Override
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            return con.prepareStatement(this.sql);
        }

        @Override
        public String getSql() {
            return this.sql;
        }
    }


    private static String getSql(Object sqlProvider) {
        if (sqlProvider instanceof SqlProvider) {
            return ((SqlProvider) sqlProvider).getSql();
        } else {
            return null;
        }
    }

    public <T> T execute(Connection con, String sql, PreparedStatementCallback<T> action)
            throws DataAccessException {
        return execute(con, new SimplePreparedStatementCreator(sql), action);
    }

    public <T> T execute(Connection con, PreparedStatementCreator psc, PreparedStatementCallback<T> action)
            throws DataAccessException {

        Assert.notNull(psc, "PreparedStatementCreator must not be null");
        Assert.notNull(action, "Callback object must not be null");
        if (logger.isDebugEnabled()) {
            String sql = getSql(psc);
            logger.debug("Executing prepared SQL statement" + (sql != null ? " [" + sql + "]" : ""));
        }

        PreparedStatement ps = null;
        try {
            ps = psc.createPreparedStatement(con);
            applyStatementSettings(ps);
            PreparedStatement psToUse = ps;
            T result = action.doInPreparedStatement(psToUse);
            handleWarnings(ps);
            return result;
        } catch (SQLException ex) {
            if (psc instanceof ParameterDisposer) {
                ((ParameterDisposer) psc).cleanupParameters();
            }
            String sql = getSql(psc);
            psc = null;
            JdbcUtils.closeStatement(ps);
            ps = null;
            throw getExceptionTranslator().translate("PreparedStatementCallback", sql, ex);
        } finally {
            if (psc instanceof ParameterDisposer) {
                ((ParameterDisposer) psc).cleanupParameters();
            }
            JdbcUtils.closeStatement(ps);
        }
    }

    public int[] batchUpdate(Connection conn, String sql, final BatchPreparedStatementSetter pss)
            throws DataAccessException {

        return execute(conn, sql, new PreparedStatementCallback<int[]>() {
            @Override
            public int[] doInPreparedStatement(PreparedStatement ps) throws SQLException {
                try {
                    int batchSize = pss.getBatchSize();
                    InterruptibleBatchPreparedStatementSetter ipss =
                            (pss instanceof InterruptibleBatchPreparedStatementSetter ?
                                    (InterruptibleBatchPreparedStatementSetter) pss : null);
                    if (JdbcUtils.supportsBatchUpdates(ps.getConnection())) {
                        for (int i = 0; i < batchSize; i++) {
                            pss.setValues(ps, i);
                            if (ipss != null && ipss.isBatchExhausted(i)) {
                                break;
                            }
                            ps.addBatch();
                        }
                        return ps.executeBatch();
                    } else {
                        List<Integer> rowsAffected = new ArrayList<Integer>();
                        for (int i = 0; i < batchSize; i++) {
                            pss.setValues(ps, i);
                            if (ipss != null && ipss.isBatchExhausted(i)) {
                                break;
                            }
                            rowsAffected.add(ps.executeUpdate());
                        }
                        int[] rowsAffectedArray = new int[rowsAffected.size()];
                        for (int i = 0; i < rowsAffectedArray.length; i++) {
                            rowsAffectedArray[i] = rowsAffected.get(i);
                        }
                        return rowsAffectedArray;
                    }
                } finally {
                    if (pss instanceof ParameterDisposer) {
                        ((ParameterDisposer) pss).cleanupParameters();
                    }
                }
            }
        });


    }

    public int[] batchUpdate(final List<String> sql, final List<BatchPreparedStatementSetter> pss1)
            throws DataAccessException {
        final int len0 = sql == null ? 0 : sql.size();
        int len1 = pss1 == null ? 0 : pss1.size();
        if (len0 != len1 || len0 == 0)
            return new int[0];
        ConnectionCallback<int[]> ccb = new ConnectionCallback<int[]>() {

            @Override
            public int[] doInConnection(Connection con) throws SQLException,
                    DataAccessException {
                int[] r = null;
                for (int i = 0; i < len0; i++) {
                    final BatchPreparedStatementSetter pss = pss1.get(i);
                    r = i > 0 ? null : execute(con, new SimplePreparedStatementCreator(sql.get(i)), new PreparedStatementCallback<int[]>() {
                        @Override
                        public int[] doInPreparedStatement(PreparedStatement ps) throws SQLException {
                            try {
                                int batchSize = pss.getBatchSize();
                                InterruptibleBatchPreparedStatementSetter ipss =
                                        (pss instanceof InterruptibleBatchPreparedStatementSetter ?
                                                (InterruptibleBatchPreparedStatementSetter) pss : null);
                                if (JdbcUtils.supportsBatchUpdates(ps.getConnection())) {
                                    for (int i = 0; i < batchSize; i++) {
                                        pss.setValues(ps, i);
                                        if (ipss != null && ipss.isBatchExhausted(i)) {
                                            break;
                                        }
                                        ps.addBatch();
                                    }
                                    return ps.executeBatch();
                                } else {
                                    List<Integer> rowsAffected = new ArrayList<Integer>();
                                    for (int i = 0; i < batchSize; i++) {
                                        pss.setValues(ps, i);
                                        if (ipss != null && ipss.isBatchExhausted(i)) {
                                            break;
                                        }
                                        rowsAffected.add(ps.executeUpdate());
                                    }
                                    int[] rowsAffectedArray = new int[rowsAffected.size()];
                                    for (int i = 0; i < rowsAffectedArray.length; i++) {
                                        rowsAffectedArray[i] = rowsAffected.get(i);
                                    }
                                    return rowsAffectedArray;
                                }
                            } finally {
                                if (pss instanceof ParameterDisposer) {
                                    ((ParameterDisposer) pss).cleanupParameters();
                                }
                            }
                        }
                    });
                }

                return r;
            }
        };

        return execute(ccb);


    }
}
