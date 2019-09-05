/*
 * 创建日期 2005-7-13
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.dzf.zxkj.base.framework;

import com.dzf.zxkj.base.framework.exception.DbException;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.model.SuperVO;
import org.springframework.jdbc.core.ConnectionCallback;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Collection;
import java.util.List;


abstract public class PersistenceManager {
    protected int maxRows = 100000;

    /**
     * 释放资源
     */

    abstract public void release();

    /**
     * 得到JdbcSession
     *
     * @return 返回JdbcSession
     */
    abstract public JdbcSession getJdbcSession();

    /**
     * 把一个值对象插入到数据库中
     *
     * @param vo 值对象
     * @throws DbException 如果插入过程中发生错误则抛出异常
     */
    abstract public String insert(String pk_corp, final SuperVO vo) throws DbException;

    /**
     * 把一个值对象集合插入到数据库中
     *
     * @param vos 值对象集合
     * @throws DbException 如果插入过程中发生错误则抛出异常
     */
    abstract public String[] insert(String pk_corp, final List vos) throws DbException;

    /**
     * 把一个值对象数组插入到数据库中
     *
     * @param vo 值对象数组
     * @throws DbException 如果插入过程中发生错误则抛出异常
     */
    abstract public String[] insert(String pk_corp, final SuperVO vo[]) throws DbException;

    abstract public String[] insert(Connection conn, String pk_corp, final SuperVO vos[]) throws DbException;

    /**
     * 更新一个在数据库中已经存在值对象
     *
     * @param vo 值对象
     * @throws DbException 如果插入过程中发生错误则抛出异常
     */
    abstract public int update(final SuperVO vo) throws DbException;

    abstract public int update(final List vos) throws DbException;

    abstract public int update(final SuperVO vo[]) throws DbException;

    abstract public int update(final SuperVO[] vo, String[] fieldNames)
            throws DbException;

    abstract public SuperVO execute(ConnectionCallback<SuperVO> action) throws Exception;

    /**
     * @param vo
     * @param fieldNames
     * @param whereClause
     * @param param
     * @return
     * @throws DbException
     * @since5.5 进行update更新增加条件的处理
     */
    abstract public int update(final SuperVO[] vo, String[] fieldNames,
                               String whereClause, SQLParameter param) throws DbException;

    /**
     * 删除一个在数据库中已经存在值对象
     *
     * @param vo
     * @throws DbException
     */

    abstract public int delete(final SuperVO vo) throws DbException;

    abstract public int delete(final SuperVO vo[]) throws DbException;

    abstract public int delete(final List vos) throws DbException;

    abstract public int deleteByPK(Class className, String pk)
            throws DbException;

    abstract public int deleteByPKs(Class className, String[] pks)
            throws DbException;

//	abstract public int deleteByClause(Class className, String wherestr)
//			throws DbException;

    abstract public int deleteByClause(Class className, String wherestr,
                                       SQLParameter params) throws DbException;

    abstract public Collection retrieveByCorp(Class c, String pkCorp,
                                              String[] selectedFields) throws DbException;

    abstract public Collection retrieveByCorp(Class c, String pkCorp)
            throws DbException;

    abstract public Object retrieveByPK(Class className, String pk)
            throws DbException;

    abstract public Object retrieveByPK(Class className, String pk,
                                        String[] selectedFields) throws DbException;

    /**
     * 根据VO的字段值条件查询数据
     *
     * @param vo
     * @return
     * @throws DbException
     */

    abstract public Collection retrieve(SuperVO vo, boolean isAnd)
            throws DbException;

    abstract public Collection retrieve(SuperVO vo, boolean isAnd,
                                        String[] fields) throws DbException;

    abstract public Object retrieve(SuperVO vo, boolean isAnd, String[] fields,
                                    ResultSetProcessor processor) throws DbException;

    abstract public Collection retrieve(SuperVO vo, boolean isAnd,
                                        String[] fields, String[] orderbyFields) throws DbException;

    /**
     * 查询VO对应表所有数据
     *
     * @param className
     * @return
     * @throws DbException
     */

    abstract public Collection retrieveAll(Class className) throws DbException;

    /**
     * 根据条件查询VO对应表数据
     *
     * @param className
     * @param condition
     * @return
     * @throws DbException
     */
    abstract public Collection retrieveByClause(Class className,
                                                String condition, String[] fields) throws DbException;

    abstract public Collection retrieveByClause(Class className,
                                                String condition) throws DbException;

    /**
     * 得到数据库类型
     *
     * @return
     */
//
    abstract public int getDBType();

    /**
     * 是否自动添加时间戳
     *
     * @param isAddTimeStamp
     */
    abstract public void setAddTimeStamp(boolean isAddTimeStamp);

    public abstract String[] insertWithPK(String pk_corp, final List vos) throws DbException;

    /**
     * 把一个值对象插入到数据库中
     *
     * @param vo 值对象
     * @throws DbException 如果插入过程中发生错误则抛出异常
     */
    public abstract String insertWithPK(String pk_corp, final SuperVO vo) throws DbException;

    public abstract String[] insertWithPK(String pk_corp, final SuperVO vos[])
            throws DbException;

    public abstract Collection retrieveByClause(Class className,
                                                String condition, String[] fields, SQLParameter parameters)
            throws DbException;

    /**
     * 根据默认数据源得到PersistenceManager实例
     *
     * @return
     * @throws DbException 如果出错则抛出DbException
     */
    static public PersistenceManager getInstance() throws DbException {
        return new JdbcPersistenceManager();
    }

    /**
     * 根据传入的数据源参数得到PersistenceManager实例
     *
     * @param dataSourceName 数据源名称
     * @return
     * @throws DbException 如果出错则抛出DbException
     */
    static public PersistenceManager getInstance(DataSource dataSourceName)
            throws DbException {
        return new JdbcPersistenceManager(dataSourceName);

    }

    /**
     * 根据传入的JdbcSession参数得到PersistenceManager实例
     *
     * @param session JdbcSession参数
     * @return
     * @throws DbException 如果出错则抛出DbException
     */
    static public PersistenceManager getInstance(JdbcSession session) {
        return new JdbcPersistenceManager(session);

    }

    public int getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

}