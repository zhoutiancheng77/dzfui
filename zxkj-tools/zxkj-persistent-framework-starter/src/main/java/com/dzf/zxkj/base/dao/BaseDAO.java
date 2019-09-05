package com.dzf.zxkj.base.dao;

import com.dzf.zxkj.base.framework.JdbcSession;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.exception.DAOException;
import com.dzf.zxkj.base.framework.exception.DbException;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.framework.util.DBConsts;
import lombok.extern.slf4j.Slf4j;
import com.dzf.zxkj.base.framework.PersistenceManager;
import org.springframework.jdbc.core.ConnectionCallback;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author hey
 * <p/>
 * 数据库访问帮助类封装了常用的持久层访问操作
 */

@Slf4j
final public class BaseDAO {
    private static String[] tablenames = {"bdcurrtype", "yntcpcosttransvo", "yntremittance", "ynt_tdaccschema"};
    private DataSource dataSource = null;

    int maxRows = 100000;

    boolean addTimestamp = true;

    /**
     * 有参构造函数，将使用指定数据源
     *
     * @param dataSource 数据源名称
     */
    public BaseDAO(DataSource dataSource) {
        super();
        this.dataSource = dataSource;
    }

    /**
     * 根据指定SQL 执行有参数的数据库查询,并返回ResultSetProcessor处理后的对象
     *
     * @param sql       查询的SQL
     * @param parameter 查询参数
     * @param processor 结果集处理器
     */
    public Object executeQuery(String sql, SQLParameter parameter,
                               ResultSetProcessor processor) throws DAOException {
        PersistenceManager manager = null;
        Object value = null;
        try {
            manager = createPersistenceManager(dataSource);

            JdbcSession session = manager.getJdbcSession();
            value = session.executeQuery(sql, parameter, processor);

        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
        return value;
    }

    /**
     * 根据指定SQL 执行有参数的数据库更新操作
     *
     * @param sql       更新的sql
     * @param parameter 更新参数
     * @return
     * @throws DAOException 更新发生错误抛出DAOException
     */
    public int executeUpdate(String sql, SQLParameter parameter)
            throws DAOException {
        PersistenceManager manager = null;
        int value;
        try {
            manager = createPersistenceManager(dataSource);
            JdbcSession session = manager.getJdbcSession();
            value = session.executeUpdate(sql, parameter);

        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
        return value;
    }

    /**
     * 根据VO类名查询该VO对应表的所有数据
     *
     * @param className SuperVo类名
     * @return
     * @throws DAOException 发生错误抛出DAOException
     */
    public Collection retrieveAll(Class className) throws DAOException {
        PersistenceManager manager = null;
        Collection values = null;
        try {
            SuperVO svo = (SuperVO) className.newInstance();
            String tabname = svo.getTableName();
            int index = Arrays.binarySearch(tablenames, tabname.toLowerCase());
            if (index < 0) throw new DAOException("该表不具有全表查询条件");
            manager = createPersistenceManager(dataSource);
            values = manager.retrieveAll(className);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
        return values;
    }

    public Collection retrieveByClause(Class className, String condition,
                                       SQLParameter params) throws DAOException {
        return retrieveByClause(className, condition, (String[]) null, params);
    }


    public Collection retrieveByClause(Class className, String condition,
                                       String orderBy, SQLParameter params) throws DAOException {
        return retrieveByClause(className, appendOrderBy(condition, orderBy),
                (String[]) null, params);
    }


    public Collection retrieveByClause(Class className, String condition,
                                       String[] fields, SQLParameter params) throws DAOException {
        PersistenceManager manager = null;
        Collection values = null;
        try {
            manager = createPersistenceManager(dataSource);
            values = manager.retrieveByClause(className, condition, fields,
                    params);
        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
        return values;
    }


    public Collection retrieveByClause(Class className, String condition,
                                       String orderBy, String[] fields, SQLParameter params)
            throws DAOException {
        return retrieveByClause(className, appendOrderBy(condition, orderBy),
                fields, params);
    }

    private String appendOrderBy(String condition, String orderBy) {
        StringBuffer clause = new StringBuffer();
        if (condition != null)
            clause.append(condition);
        if (orderBy != null && condition == null)
            clause.append("ORDER BY ").append(orderBy);
        if (orderBy != null && condition != null) {
            clause.append(" ORDER BY ").append(orderBy);
        }

        return clause.toString();
    }

    /**
     * 根据VO中的属性值作为匹配条件从数据库中查询该VO对应的表的数据
     *
     * @param vo    要查询的VO对象
     * @param isAnd 指定匹配条件的逻辑（true代表&&,flase代表||）
     * @return
     * @throws DAOException 如果查询出错抛出DAOException
     */
    public Collection retrieve(SuperVO vo, boolean isAnd) throws DAOException {
        PersistenceManager manager = null;
        Collection values = null;
        try {
            manager = createPersistenceManager(dataSource);
            values = manager.retrieve(vo, isAnd);

        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }

        return values;
    }

    public Collection retrieveOrderBy(SuperVO vo, boolean isAnd,
                                      String[] orderbyFields) throws DAOException {

        return retrieve(vo, isAnd, null, orderbyFields);

    }

    public Collection retrieve(SuperVO vo, boolean isAnd, String[] fields,
                               String[] orderbyFields) throws DAOException {
        PersistenceManager manager = null;
        Collection values = null;
        try {
            manager = createPersistenceManager(dataSource);
            values = manager.retrieve(vo, isAnd, fields, orderbyFields);
        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
        return values;
    }

    /**
     * 根据指定VO的值以及逻辑条件返回指定字段的VO集合
     *
     * @param vo    条件VO
     * @param isAnd 逻辑条件，true代表与运算false代表或运算
     */
    public Collection retrieve(SuperVO vo, boolean isAnd, String[] fields)
            throws DAOException {
        PersistenceManager manager = null;
        Collection values = null;
        try {
            manager = createPersistenceManager(dataSource);
            values = manager.retrieve(vo, isAnd, fields);

        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
        return values;
    }

    /**
     * 根据公司名和指定字段返回VO集合
     *
     * @param className      VO类名
     * @param pkCorp         公司主键
     * @param selectedFields 查询字段
     */
    public Collection retrieveByCorp(Class className, String pkCorp,
                                     String[] selectedFields) throws DAOException {
        PersistenceManager manager = null;
        Collection values = null;
        try {
            manager = createPersistenceManager(dataSource);

            values = manager.retrieveByCorp(className, pkCorp, selectedFields);

        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
        return values;
    }

    /**
     * 根据公司PK返回指定VO集合
     *
     * @param className VO名称
     * @param pkCorp
     */
    public Collection retrieveByCorp(Class className, String pkCorp)
            throws DAOException {
        PersistenceManager manager = null;
        Collection values = null;
        try {
            manager = createPersistenceManager(dataSource);
            values = manager.retrieveByCorp(className, pkCorp);
        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
        return values;
    }

    /**
     * 根据PK查询指定VO
     *
     * @param className
     * @param pk        主键
     */
    public Object retrieveByPK(Class className, String pk) throws DAOException {
        PersistenceManager manager = null;
        Object values = null;
        try {
            manager = createPersistenceManager(dataSource);
            values = manager.retrieveByPK(className, pk);

        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
        return values;
    }

    /**
     * 根据主键返回指定列的VO对象
     */
    public Object retrieveByPK(Class className, String pk,
                               String[] selectedFields) throws DAOException {
        PersistenceManager manager = null;
        Object values = null;
        try {
            manager = createPersistenceManager(dataSource);
            values = manager.retrieveByPK(className, pk, selectedFields);

        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
        return values;
    }

    /**
     * 插入一个VO对象，如果该VO的主键值非空则插入VO的原有主键
     *
     * @param vo
     * @return
     * @throws DAOException
     */
    public String insertVOWithPK(String pk_corp, SuperVO vo) throws DAOException {
        PersistenceManager manager = null;
        String pk = null;
        try {
            manager = createPersistenceManager(dataSource);

            pk = manager.insertWithPK(pk_corp, vo);

        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
        return pk;
    }

    /**
     * 插入一个VO对象
     *
     * @param vo SuperVO对象
     */
    public String insertVO(String pk_corp, SuperVO vo) throws DAOException {
        PersistenceManager manager = null;
        String pk = null;
        try {
            manager = createPersistenceManager(dataSource);

            pk = manager.insert(pk_corp, vo);

        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
        return pk;

    }

    /**
     * 插入一个VO数组如果该VO的主键值非空则插入VO的原有主键
     *
     * @param vo
     * @return
     * @throws DAOException
     */
    public String[] insertVOArrayWithPK(String pk_corp, SuperVO[] vo) throws DAOException {
        PersistenceManager manager = null;
        String pk[] = null;
        try {
            manager = createPersistenceManager(dataSource);

            pk = manager.insertWithPK(pk_corp, vo);

        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
        return pk;
    }

    /**
     * 插入VO数组
     *
     * @param vo VO数组
     */
    public String[] insertVOArray(String pk_corp, SuperVO[] vo) throws DAOException {
        PersistenceManager manager = null;
        String pk[] = null;
        try {
            manager = createPersistenceManager(dataSource);

            pk = manager.insert(pk_corp, vo);

        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
        return pk;
    }

    /*
     * 该方法只能被singleObejctBO调用
     * */
    public SuperVO execute(ConnectionCallback<SuperVO> action) throws Exception {
        PersistenceManager manager = null;
        SuperVO svo = null;
        try {
            manager = createPersistenceManager(dataSource);
            svo = manager.execute(action);
        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
        return svo;
    }

    /*
     * 该方法只能被singleObejctBO调用
     * */
    public String[] insertVOArray(Connection conn, String pk_corp, SuperVO[] vo) throws DAOException {
        PersistenceManager manager = null;
        String pk[] = null;
        try {
            manager = createPersistenceManager(dataSource);

            pk = manager.insert(conn, pk_corp, vo);// .insert(pk_corp,vo);

        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
        return pk;
    }

    /**
     * 插入VO集合
     *
     * @param vos VO集合
     */
    public String[] insertVOList(String pk_corp, List vos) throws DAOException {
        PersistenceManager manager = null;
        String pk[] = null;
        try {
            manager = createPersistenceManager(dataSource);
            ;
            pk = manager.insert(pk_corp, vos);

        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
        return pk;
    }

    /**
     * 根据VO对象更新数据库
     *
     * @param vo VO对象
     */
    public int updateVO(SuperVO vo) throws DAOException {
        return updateVOArray(new SuperVO[]{vo});
    }

    /**
     * 根据VO对象中指定列更新数据库
     *
     * @param vo         VO对象
     * @param fieldNames 指定列
     * @throws DAOException
     */
    public void updateVO(SuperVO vo, String[] fieldNames) throws DAOException {
        updateVOArray(new SuperVO[]{vo}, fieldNames);
    }

    /**
     * 根据VO对象数组更新数据库
     *
     * @param vos VO对象
     */
    public int updateVOArray(SuperVO[] vos) throws DAOException {
        return updateVOArray(vos, null);
    }

    /**
     * 2016-06-06增加
     * 批量更新
     *
     * @param sqls       更新的sql集合
     * @param parameters 更新参数
     * @return
     * @throws DAOException 更新发生错误抛出DAOException
     */
    public int executeBatchUpdate(String sqls, SQLParameter[] parameters) throws DAOException {
        PersistenceManager manager = null;
        int value;
        try {
            manager = createPersistenceManager(dataSource);
            JdbcSession session = manager.getJdbcSession();
            value = session.executeBatch(sqls, parameters);

        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
        return value;
    }

    /**
     * 根据VO对象数组中指定列更新数据库
     *
     * @param vos        VO对象
     * @param fieldNames 指定列
     */
    public int updateVOArray(SuperVO[] vos, String[] fieldNames)
            throws DAOException {
        return updateVOArray(vos, fieldNames, null, null);

    }

    /**
     * 根据VO对象集合更新数据库
     *
     * @paramvos VO对象集合
     */
    public void updateVOList(List vos) throws DAOException {
        PersistenceManager manager = null;
        try {
            manager = createPersistenceManager(dataSource);
            manager.update(vos);
        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }

    }

    /**
     * 在数据库中删除一个VO对象。
     *
     * @param vo VO对象
     * @throws DAOException 如果删除出错抛出DAOException
     */
    public void deleteVO(SuperVO vo) throws DAOException {
        PersistenceManager manager = null;
        try {
            manager = createPersistenceManager(dataSource);
            manager.delete(vo);
        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }

    }

    /**
     * 在数据库中删除一组VO对象。
     *
     * @param vos VO数组对象
     * @throws DAOException 如果删除出错抛出DAOException
     */
    public void deleteVOArray(SuperVO[] vos) throws DAOException {
        PersistenceManager manager = null;
        try {
            manager = createPersistenceManager(dataSource);
            manager.delete(vos);

        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }

    }

    /**
     * 在数据库中根据类名和PK数组删除一组VO对象集合
     *
     * @param className 要删除的VO类名
     * @param pks       PK数组
     * @throws DAOException 如果删除出错抛出DAOException
     */
    public void deleteByPKs(Class className, String[] pks) throws DAOException {
        PersistenceManager manager = null;
        try {
            manager = createPersistenceManager(dataSource);
            manager.deleteByPKs(className, pks);

        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }

    }


    public void deleteByClause(Class className, String wherestr,
                               SQLParameter params) throws DAOException {
        PersistenceManager manager = null;
        try {
            manager = createPersistenceManager(dataSource);
            manager.deleteByClause(className, wherestr, params);
        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }

    }

    /**
     * 在数据库中根据类名和PK删除一个VO对象集合
     *
     * @param className VO类名
     * @param pk        PK值
     * @throws DAOException 如果删除出错抛出DAOException
     */
    public int deleteByPK(Class className, String pk) throws DAOException {
        PersistenceManager manager = null;
        try {
            manager = createPersistenceManager(dataSource);
            return manager.deleteByPK(className, pk);
        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }

    }

    /**
     * 在数据库中删除一组VO对象集合
     *
     * @param vos VO对象集合
     * @throws DAOException 如果删除出错抛出DAOException
     */
    public void deleteVOList(List vos) throws DAOException {
        PersistenceManager manager = null;
        try {
            manager = createPersistenceManager(dataSource);
            manager.delete(vos);
        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }

    }


    public int getDBType() {
        return DBConsts.ORACLE;
    }


    protected String getTableName(int dbType, String tableName) {
        String strTn = tableName;
        switch (dbType) {
            case DBConsts.POSTGRESQL:
                strTn = tableName;
                break;
            case DBConsts.ORACLE:
            case DBConsts.OSCAR:
            case DBConsts.DB2:
                // ORACLE需将表名大写
                strTn = tableName.toUpperCase();
                break;
        }
        return strTn;
    }


    public boolean isTableExisted(String tableName) throws DAOException {
        return false;
    }

    public int getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }


    public void setAddTimeStamp(boolean addTimeStamp) {
        this.addTimestamp = addTimeStamp;
    }

    public boolean getAddTimeStamp() {
        return addTimestamp;
    }

    private PersistenceManager createPersistenceManager(DataSource ds)
            throws DbException {
        PersistenceManager manager = PersistenceManager.getInstance(ds);
        manager.setMaxRows(maxRows);
        manager.setAddTimeStamp(addTimestamp);
        return manager;
    }

    public int updateVOArray(final SuperVO[] vos, String[] fieldNames,
                             String whereClause, SQLParameter param) throws DAOException {
        PersistenceManager manager = null;
        try {
            manager = createPersistenceManager(dataSource);
            return manager.update(vos, fieldNames, whereClause, param);

        } catch (DbException e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
    }

}
