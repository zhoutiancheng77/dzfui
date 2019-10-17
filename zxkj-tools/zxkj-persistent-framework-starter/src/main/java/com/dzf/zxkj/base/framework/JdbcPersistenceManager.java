package com.dzf.zxkj.base.framework;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.dzf.zxkj.base.framework.exception.DbException;
import com.dzf.zxkj.base.framework.exception.ExceptionFactory;
import com.dzf.zxkj.base.framework.processor.BaseProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.framework.util.DBConsts;
import com.dzf.zxkj.base.framework.util.DBUtil;
import com.dzf.zxkj.base.framework.util.SQLHelper;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.IDGenerate;
import com.dzf.zxkj.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class JdbcPersistenceManager extends PersistenceManager {
    // 数据库会话
    JdbcSession session;

    // 数据源名称
    DataSource dataSource = null;

    //private DatabaseMetaData dbmd = null;

    private static Map<String, ColCache> colCacheMap = new ConcurrentHashMap<String, ColCache>();

//	private Logger logger = Logger.getLogger(this.getClass());

    class ColCache {
        private Map<String, Object[][]> typeCache = new ConcurrentHashMap<String, Object[][]>();
        private Map<String, Object[][]> sizeCache = new ConcurrentHashMap<String, Object[][]>();
    }

    /**
     * 无参数构造函数
     *
     * @throws DbException
     */
    protected JdbcPersistenceManager() throws DbException {
        init();
    }

    /**
     * 有参数构造函数
     *
     * @param dataSource 数据源名称
     * @throws DbException 如果获得连接发生错误则抛出异常
     */
    protected JdbcPersistenceManager(DataSource dataSource) throws DbException {
        this.dataSource = dataSource;
        init();
    }

    protected JdbcPersistenceManager(JdbcSession session) {
        session.setMaxRows(maxRows);
        this.session = session;
    }

    /**
     * 得到JdbcSession
     *
     * @return 返回JdbcSession
     */
    public JdbcSession getJdbcSession() {
        return session;
    }

    /**
     * 释放资源
     */
//	public void release() {
//		if (dbmd != null)
//			dbmd = null;
//		if (session != null) {
//			session.closeAll();
//			session = null;
//		}
//
//	}

    /*
     * (non-Javadoc)
     *
     * @see nc.jdbc.framework.ee#insertWithPK(nc.vo.pub.SuperVO)
     */
    public String insertWithPK(String pk_corp, final SuperVO vo) throws DbException {
        String pk[] = insertWithPK(pk_corp, new SuperVO[]{vo});
        return pk[0];
    }

    /**
     * 把一个值对象插入到数据库中
     *
     * @param vo 值对象
     * @throws DbException 如果插入过程中发生错误则抛出异常
     */
    public String insert(String pk_corp, final SuperVO vo) throws DbException {
        String pk[] = insert(pk_corp, new SuperVO[]{vo});
        return pk[0];
    }

    /**
     * 把一个值对象集合插入到数据库中
     *
     * @param vos 值对象集合
     * @throws DbException 如果插入过程中发生错误则抛出异常
     */
    public String[] insertWithPK(String pk_corp, final List vos) throws DbException {
        return insertWithPK(pk_corp, (SuperVO[]) vos.toArray(new SuperVO[]{}));
    }

    /**
     * 把一个值对象集合插入到数据库中
     *
     * @param vos 值对象集合
     * @throws DbException 如果插入过程中发生错误则抛出异常
     */
    public String[] insert(String pk_corp, final List vos) throws DbException {

        return insert(null, pk_corp, (SuperVO[]) vos.toArray(new SuperVO[]{}));
    }

    /*
     * (non-Javadoc)
     *
     * @see nc.jdbc.framework.ee#insertWithPK(nc.vo.pub.SuperVO[])
     */
    public String[] insertWithPK(String pk_corp, final SuperVO vos[]) throws DbException {
        return insert(null, pk_corp, vos, true);

    }

    /**
     * 把一个值对象数组插入到数据库中
     *
     * @param vos 值对象数据
     * @throws DbException 如果插入过程中发生错误则抛出异常
     */
    public String[] insert(String pk_corp, final SuperVO vos[]) throws DbException {
        return insert(null, pk_corp, vos, false);
    }

    public String[] insert(Connection conn, String pk_corp, final SuperVO vos[]) throws DbException {
        return insert(conn, pk_corp, vos, false);
    }

    public SuperVO execute(ConnectionCallback<SuperVO> action) throws Exception {
        return session.execute(action);
    }

    private String[] preparePK(final SuperVO vos[], String pk_corp, boolean withPK) {
        String corpPk = pk_corp;// SQLHelper.getCorpPk();
        if (withPK) {
            String[] pks = new String[vos.length];
            int[] idx = new int[vos.length];
            int length = 0;
            for (int i = 0; i < vos.length; i++) {
                if (vos[i] == null) {
                    continue;
                } else {
                    String thePK = vos[i].getPrimaryKey();
                    if (thePK == null || thePK.trim().length() == 0) {
                        idx[length++] = i;
                    } else {
                        pks[i] = thePK;
                    }
                }
            }

            if (length > 0) {
                String[] npks = IDGenerate.getInstance().getNextIDS(corpPk, length);// new SequenceGenerator(dataSource).generate(
                //corpPk, length);
                for (int i = 0; i < length; i++) {
                    vos[idx[i]].setPrimaryKey(npks[i]);
                    pks[idx[i]] = npks[i];
                }
            }
            return pks;

        } else {
            String[] pks = IDGenerate.getInstance().getNextIDS(corpPk, vos.length);// new SequenceGenerator(dataSource).generate(corpPk,
            //vos.length);
            for (int i = 0; i < vos.length; i++) {
                if (vos[i] != null) {
                    vos[i].setPrimaryKey(pks[i]);
                } else {
                    pks[i] = null;
                }
            }
            return pks;
        }
    }

    @SuppressWarnings("unchecked")
    protected String[] insert(Connection conn, String pk_corp, final SuperVO vos[], boolean withPK)
            throws DbException {
        isNull(vos);
        if (vos.length == 0) {
            return new String[0];
        }
        DZFDateTime time = new DZFDateTime();
        for (SuperVO v : vos) {
            v.setUpdatets(time);
        }
        String[] pks = null;
        try {
            String tableName = vos[0].getTableName();

            Object[][] types = getColmnTypes(tableName);
            Object[][] sizes = getColmnSize(tableName);


            Map<String, Integer> map = getMap(types);

            Map<String, Integer> map1 = getMap(sizes);


            String names[] = getValidNames(vos[0], map);

            String sql = SQLHelper.getInsertSQL(tableName, names);

            pks = preparePK(vos, pk_corp, withPK);

            if (vos.length == 1) {
                SQLParameter parameter = getSQLParam(vos[0], names, map, map1);

                if (conn == null)
                    session.executeUpdate(sql, parameter);
                else
                    session.executeBatch(conn, sql, new SQLParameter[]{parameter});
            } else {
                SQLParameter[] parameters = new SQLParameter[vos.length];
                for (int i = 0; i < vos.length; i++) {
                    if (vos[i] == null)
                        continue;
                    parameters[i] = getSQLParam(vos[i], names, map, map1);

                }
                //session.addBatch(sql, parameters);
                if (conn == null)
                    session.executeBatch(sql, parameters);
                else
                    session.executeBatch(conn, sql, parameters);
            }

        } finally {

        }
        return pks;

    }

    private Map<String, Integer> getMap(Object[][] o) {
        int len = o == null ? 0 : o.length;
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (int i = 0; i < len; i++) {
            map.put((String) o[i][0], (Integer) o[i][1]);
        }
        o = null;
        return map;
    }

    /*
     * (non-Javadoc)
     *
     * @see nc.jdbc.framework.ee#insertObjectWithPK(java.lang.Object,
     * nc.jdbc.framework.mapping.IMappingMeta)
     */
//	public String insertObjectWithPK(final Object vo, IMappingMeta meta)
//			throws DbException {
//		return insertObjectWithPK(new Object[] { vo }, meta)[0];
//	}

    /*
     * (non-Javadoc)
     *
     * @see nc.jdbc.framework.ee#insertObjectWithPK(java.lang.Object[],
     * nc.jdbc.framework.mapping.IMappingMeta)
     */
//	public String[] insertObjectWithPK(final Object vo[], IMappingMeta meta)
//			throws DbException {
//		return insertObject(vo, meta, true);
//	}

//	public String insertObject(final Object vo, IMappingMeta meta)
//			throws DbException {
//		return insertObject(new Object[] { vo }, meta)[0];
//	}

    /**
     * 把一个值对象数组插入到数据库中
     *
     * @param vo
     *            值对象数据
     * @throws DAOException
     *             如果插入过程中发生错误则抛出异常
     */
//	public String[] insertObject(final Object vo[], IMappingMeta meta)
//			throws DbException {
//		return insertObject(vo, meta, false);
//	}
//
//	protected String[] insertObject(final Object vo[], IMappingMeta meta,
//			boolean withPK) throws DbException {
//		isNull(vo);
//		if (vo.length == 0)
//			return new String[0];
//		// 如果是VO类型，则属性可以没有Getter,Setter方法
//		if (vo[0] instanceof SuperVO) {
//			SuperVO[] svos = new SuperVO[vo.length];
//			System.arraycopy(vo, 0, svos, 0, vo.length);
//			return insert(svos, meta, withPK);
//		}
//		String[] pk;
//		AttributeMapping map = MappingMetaManager.getMapingMeta(meta);
//		// 得到表名
//		String tableName = meta.getTableName();
//		// 得到公司主键
//		String corpPk = SQLHelper.getCorpPk();
//		pk = new SequenceGenerator(dataSource).generate(corpPk, vo.length);
//		Map<String, Integer> types = getColmnTypes(tableName);
//		Map<String, Integer> sizes = getColmnSize(tableName);
//		// 得到插入的SQL语句
//		// String sql = SQLHelper.getInsertSQL(tableName, names);
//		// 循环插入VO数组
//		for (int i = 0; i < vo.length; i++) {
//			if (vo[i] == null)
//				continue;
//			String beanPkName = map.getAttributeName(meta.getPrimaryKey())
//					.toLowerCase();
//			if (withPK) {// if vo has pk
//				String thePK = (String) BeanHelper.getProperty(vo[i],
//						beanPkName);
//				// BeanHelper.setProperty(vo[i], beanPkName, pk[i]);
//				if (thePK == null || thePK.trim().length() == 0) {
//					BeanHelper.setProperty(vo[i], beanPkName, pk[i]);
//				} else {
//					pk[i] = thePK;
//				}
//			} else {
//				BeanHelper.setProperty(vo[i], beanPkName, pk[i]);
//			}
//			BeanMapping mapping = new BeanMapping(vo[i], meta);
//			if (types != null)
//				mapping.setType(types);
//			if (sizes != null)
//				mapping.setSizes(sizes);
//			SQLParameter parameter = mapping.getInsertParamter();
//			session.addBatch(mapping.getInsertSQL(), parameter);
//		}
//		session.executeBatch();
//		return pk;
//	}

    /**
     * 更新一个在数据库中已经存在值对象
     *
     * @param vo
     * @throws DbException
     */
    public int update(final SuperVO vo) throws DbException {
        if (vo == null) {
            throw new IllegalArgumentException("vo parameter is null");
        }
        return update(new SuperVO[]{vo}, null);
    }

    public int update(final List vos) throws DbException {
        return update((SuperVO[]) vos.toArray(new SuperVO[]{}), null);

    }

    public int update(final SuperVO[] vo) throws DbException {
        return update(vo, null);

    }

    public int update(final SuperVO[] vo, String[] fieldNames)
            throws DbException {
        return update(vo, fieldNames, null, null);
    }

    @Override
    public int update(final SuperVO[] vo, String[] fieldNames,
                      String whereClause, SQLParameter param) throws DbException {
        isNull(vo);
        if (vo.length == 0)
            return 0;
        int row = 0;
        DZFDateTime time = new DZFDateTime();
        for (SuperVO v : vo) {
            v.setUpdatets(time);
        }
        // session.setAddTimeStamp(false);
        // 得到表名
        String tableName = vo[0].getTableName();
        String pkName = vo[0].getPKFieldName();
        // 得到版本
        // UFDateTime ts = new UFDateTime(new SystemTsGenerator().generateTS());
        String[] names;
        Object[][] types = getColmnTypes(tableName);
        Object[][] sizes = getColmnSize(tableName);
        //int len;
        Map<String, Integer> map = getMap(types);
        //len=sizes==null?0:sizes.length;
        Map<String, Integer> map1 = getMap(sizes);
        if (fieldNames != null) {
            if (map.containsKey("UPDATETS")) {
                List<String> list = new ArrayList<String>(Arrays.asList(fieldNames));
                list.add("updatets");
                names = list.toArray(new String[0]);
            } else {
                names = fieldNames; // 指定更新字段
            }
        } else { // 得到插入字段类型的列表
            // 得到合法的字段列表
            names = getUpdateValidNames(vo[0], map, pkName);
        }
        // 得到插入的SQL语句
        String sql = SQLHelper.getUpdateSQL(tableName, names, pkName);
        if (vo.length == 1) {
            SQLParameter parameter = getSQLParam(vo[0], names, map, map1);
            parameter.addParam(vo[0].getPrimaryKey());
            if (whereClause == null)
                row = session.executeUpdate(sql, parameter);
            else {
                addParameter(parameter, param);
                row = session.executeUpdate(sql + " and " + whereClause,
                        parameter);
            }
        } else {
            List<SQLParameter> list = new ArrayList<SQLParameter>();
            for (int i = 0; i < vo.length; i++) {
                if (vo[i] == null)
                    continue;
                // vo[i].setAttributeValue("ts", ts);
                SQLParameter parameter = getSQLParam(vo[i], names, map, map1);
                parameter.addParam(vo[i].getPrimaryKey());
                if (StringUtil.isEmpty(whereClause) == false)

                    addParameter(parameter, param);

                list.add(parameter);
            }
            if (StringUtil.isEmpty(whereClause))
                row = session.executeBatch(sql, list.toArray(new SQLParameter[0]));
                //session.addBatch(sql, parameter);
            else {
                row = session.executeBatch(sql + " and " + whereClause, list.toArray(new SQLParameter[0]));
                //	addParameter(parameter, param);
                //	session.addBatch(sql + " and " + whereClause, parameter);
            }
            //row = session.executeBatch();
        }
        return row;
    }

//	public int updateObject(final Object vo, IMappingMeta meta)
//			throws DbException {
//		return updateObject(new Object[] { vo }, meta);
//	}

    /*
     * （非 Javadoc）
     *
     * @see nc.jdbc.framework.ee#updateObject(java.lang.Object,
     * nc.jdbc.framework.mapping.IMappingMeta, java.lang.String)
     */
//	public int updateObject(final Object vo, IMappingMeta meta,
//			String whereClause) throws DbException {
//		return updateObject(new Object[] { vo }, meta, whereClause);
//	}

    /*
     * （非 Javadoc）
     *
     * @see nc.jdbc.framework.ee#updateObject(java.lang.Object[],
     * nc.jdbc.framework.mapping.IMappingMeta, java.lang.String)
     */
//	public int updateObject(final Object[] vo, IMappingMeta meta,
//			String whereClause) throws DbException {
//		return updateObject(vo, meta, whereClause, null);
//	}

    /*
     * （非 Javadoc）
     *
     * @see nc.jdbc.framework.ee#updateObject(java.lang.Object[],
     * nc.jdbc.framework.mapping.IMappingMeta, java.lang.String)
     */
//	public int updateObject(final Object[] vo, IMappingMeta meta,
//			String whereClause, SQLParameter param) throws DbException {
//
//		isNull(vo);
//		if (vo.length == 0)
//			return 0;
//
//		// 如果是VO类型，则属性可以没有Getter,Setter方法
//		if (vo[0] instanceof SuperVO) {
//			SuperVO[] svos = new SuperVO[vo.length];
//			System.arraycopy(vo, 0, svos, 0, vo.length);
//			return update(svos, meta, whereClause, param);
//		}
//		// 得到表名
//		String tableName = meta.getTableName();
//		Map<String, Integer> types = getColmnTypes(tableName);
//		Map<String, Integer> sizes = getColmnSize(tableName);
//		if (vo.length == 1) {
//			if (vo[0] == null)
//				return -1;
//			BeanMapping mapping = new BeanMapping(vo[0], meta);
//			if (types != null) {
//				mapping.setType(types);
//			}
//			if (sizes != null) {
//				mapping.setSizes(sizes);
//			}
//			SQLParameter parameter = mapping.getUpdateParamter();
//			if (whereClause == null) {
//				if (mapping.isNullPK())
//					return -1;
//				return session.executeUpdate(mapping.getUpdateSQL(), parameter);
//			} else {
//				// 合并参数
//				addParameter(parameter, param);
//				if (mapping.isNullPK())
//					return session.executeUpdate(mapping.getUpdateSQL()
//							+ " WHERE " + whereClause, parameter);
//				else
//					return session.executeUpdate(mapping.getUpdateSQL()
//							+ " AND " + whereClause, parameter);
//			}
//		}
//		for (int i = 0; i < vo.length; i++) {
//			if (vo[i] == null)
//				continue;
//			BeanMapping mapping = new BeanMapping(vo[i], meta);
//			if (types != null)
//				mapping.setType(types);
//			SQLParameter parameter = mapping.getUpdateParamter();
//			if (whereClause == null) {
//				if (mapping.isNullPK())
//					return -1;
//				session.addBatch(mapping.getUpdateSQL(), parameter);
//			} else {
//				// 合并参数
//				addParameter(parameter, param);
//				if (mapping.isNullPK())
//					session.addBatch(mapping.getUpdateSQL() + " WHERE "
//							+ whereClause, parameter);
//				else
//					session.addBatch(mapping.getUpdateSQL() + " AND "
//							+ whereClause, parameter);
//			}
//		}
//		return session.executeBatch();
//	}

    private void addParameter(SQLParameter parameter, SQLParameter addParams) {
        if (addParams != null)
            for (int i = 0; i < addParams.getCountParams(); i++) {
                parameter.addParam(addParams.get(i));
            }
    }

//	/**
//	 * 
//	 */
//	public int updateObject(final Object[] vo, IMappingMeta meta)
//			throws DbException {
//		return updateObject(vo, meta, null);
//	}

    public int delete(final List vos) throws DbException {
        isNull(vos);
        return delete((SuperVO[]) vos.toArray(new SuperVO[]{}));
    }

    public int delete(final SuperVO vo) throws DbException {
        isNull(vo);
        return delete(new SuperVO[]{vo});
    }

    public int delete(final SuperVO vo[]) throws DbException {
        isNull(vo);
        if (vo.length == 0)
            return 0;
        // 得到表名
        String tableName = vo[0].getTableName();
        String sql = " update " + tableName + " set dr = 1 WHERE " + vo[0].getPKFieldName() + "=?";
        Object[][] types = getColmnTypes(tableName);
        Map<String, Integer> map = getMap(types);
        if (map.containsKey("UPDATETS")) {
            sql = " update " + tableName + " set dr = 1,updatets = '" + new DZFDateTime() + "' WHERE " + vo[0].getPKFieldName() + "=?";
        }
        map.clear();
        List<SQLParameter> list = new ArrayList<SQLParameter>();
        for (int i = 0; i < vo.length; i++) {
            if (vo[i] == null)
                continue;
            SQLParameter parameter = new SQLParameter();
            parameter.addParam(vo[i].getPrimaryKey());
            list.add(parameter);
            //session.addBatch(sql, parameter);
        }
        return session.executeBatch(sql, list.toArray(new SQLParameter[0]));
    }

//	public void deleteObject(final Object vo, IMappingMeta meta)
//			throws DbException {
//		deleteObject(new Object[] { vo }, meta);
//	}
//
//	public void deleteObject(final Object vos[], IMappingMeta meta)
//			throws DbException {
//		isNull(vos);
//		if (vos.length == 0)
//			return;
//		for (int i = 0; i < vos.length; i++) {
//			if (vos[i] == null)
//				continue;
//			BeanMapping mapping = new BeanMapping(vos[i], meta);
//			SQLParameter parameter = mapping.getDeleteParamter();
//			session.addBatch(mapping.getDeleteSQL(), parameter);
//		}
//		session.executeBatch();
//
//	}

    /*
     * （非 Javadoc）
     *
     * @see
     * nc.jdbc.framework.ee#deleteByPK(nc.jdbc.framework.mapping.IMappingMeta,
     * java.lang.String)
     */
//	public int deleteByPK(IMappingMeta meta, String pk) throws DbException {
//		return deleteByPKs(meta, new String[] { pk });
//	}

    /**
     *
     * @param meta
     * @param pks
     * @return
     * @throws DbException
     *
     *             modified by cch
     */
//	public int deleteByPKs(IMappingMeta meta, String[] pks) throws DbException {
//		String sql = "DELETE FROM " + meta.getTableName() + " WHERE "
//				+ meta.getPrimaryKey() + "=?";
//		for (int i = 0; i < pks.length; i++) {
//			SQLParameter parameter = new SQLParameter();
//			parameter.addParam(pks[i]);
//			session.addBatch(sql, parameter);
//		}
//		return session.executeBatch();
//	}

    /**
     *
     */
    public int deleteByPK(Class className, String pk) throws DbException {
        return deleteByPKs(className, new String[]{pk});
    }

    /**
     *
     */
    public int deleteByPKs(Class className, String[] pks) throws DbException {
        SuperVO supervo = initSuperVOClass(className);
//		String sql = "DELETE FROM " + supervo.getTableName() + " WHERE "
//				+ supervo.getPKFieldName() + "=?";

        String sql = " update " + supervo.getTableName() + " set dr =1 WHERE "
                + supervo.getPKFieldName() + "=?";
        Object[][] types = getColmnTypes(supervo.getTableName());
        Map<String, Integer> map = getMap(types);
        if (map.containsKey("UPDATETS")) {
            sql = " update " + supervo.getTableName() + " set dr =1,updatets = '" + new DZFDateTime() + "' where "
                    + supervo.getPKFieldName() + "=?";
        }
        map.clear();
        List<SQLParameter> list = new ArrayList<SQLParameter>();
        for (int i = 0; i < pks.length; i++) {
            SQLParameter parameter = new SQLParameter();
            parameter.addParam(pks[i]);
            list.add(parameter);
            //session.addBatch(sql, parameter);
        }
        return session.executeBatch(sql, list.toArray(new SQLParameter[0]));
    }

    /*
     * （非 Javadoc）
     *
     * @see
     * nc.jdbc.framework.ee#deleteByClause(nc.jdbc.framework.mapping.IMappingMeta
     * , java.lang.String)
     */
//	public int deleteByClause(IMappingMeta meta, String wherestr)
//			throws DbException {
//		return deleteByClause(meta, wherestr, null);
//	}

    /**
     * @param className
     * @param wherestr
     * @return
     * @throws DbException
     */
//	public int deleteByClause(Class className, String wherestr)
//			throws DbException {
//		return deleteByClause(className, wherestr, null);
//
//	}
    public int deleteByClause(Class className, String wherestr,
                              SQLParameter params) throws DbException {
        SuperVO supervo = initSuperVOClass(className);
//		String sql = new StringBuffer().append("DELETE FROM ")
//				.append(supervo.getTableName()).toString();

        String sql = " update " + supervo.getTableName() + " set dr = 1  ";
        Object[][] types = getColmnTypes(supervo.getTableName());
        Map<String, Integer> map = getMap(types);
        if (map.containsKey("UPDATETS")) {
            sql = " update " + supervo.getTableName() + " set dr = 1 ,updatets = '" + new DZFDateTime() + "' ";
        }
        map.clear();
        if (wherestr != null) {
            wherestr = wherestr.trim();
            if (wherestr.length() > 0) {
                if (wherestr.toLowerCase().startsWith("WHERE"))
                    wherestr = wherestr.substring(5);
                if (wherestr.length() > 0)
                    sql = sql + " WHERE " + wherestr;
            }
        }
        //if (params != null)
        //	return session.executeUpdate(sql);
        //else
        return session.executeUpdate(sql, params);

    }

    public Collection retrieveByCorp(Class c, String pkCorp) throws DbException {
        return retrieveByCorp(c, pkCorp, null);
    }

    public Collection retrieveByCorp(Class c, String pkCorp,
                                     String[] selectedFields) throws DbException {

        if (pkCorp.equals("000001") || pkCorp.equals("@@@@@@")) {
            SQLParameter param = new SQLParameter();
            param.addParam("000001");
            param.addParam("@@@@@@");
            return retrieveByClause(c, "pk_corp=? or pk_corp=?",
                    selectedFields, param);
        } else {
            SQLParameter param = new SQLParameter();
            param.addParam(pkCorp);
            return retrieveByClause(c, "pk_corp=?", selectedFields, param);
        }
    }

    /*
     * （非 Javadoc）
     *
     * @see nc.jdbc.framework.ee#retrieveByCorp(java.lang.Class,
     * nc.jdbc.framework.mapping.IMappingMeta, java.lang.String)
     */
//	public Collection retrieveByCorp(Class c, IMappingMeta meta, String pkCorp)
//			throws DbException {
//		return retrieveByCorp(c, meta, pkCorp, null);
//	}

    /*
     * （非 Javadoc）
     *
     * @see nc.jdbc.framework.ee#retrieveByCorp(java.lang.Class,
     * nc.jdbc.framework.mapping.IMappingMeta, java.lang.String,
     * java.lang.String[])
     */
//	public Collection retrieveByCorp(Class c, IMappingMeta meta, String pkCorp,
//			String[] selectedFields) throws DbException {
//		if (pkCorp.equals("0001") || pkCorp.equals("@@@@")) {
//			SQLParameter param = new SQLParameter();
//			param.addParam("0001");
//			param.addParam("@@@@");
//			return retrieveByClause(c, meta, "pk_corp=? or pk_corp=?",
//					selectedFields, param);
//		} else {
//			SQLParameter param = new SQLParameter();
//			param.addParam(pkCorp);
//			return retrieveByClause(c, meta, "pk_corp=?", selectedFields, param);
//		}
//	}

    /**
     *
     */
    public Object retrieveByPK(Class className, String pk) throws DbException {
        return retrieveByPK(className, pk, null);
    }

    /**
     *
     */
    public Object retrieveByPK(Class className, String pk,
                               String[] selectedFields) throws DbException {
        SuperVO vo = initSuperVOClass(className);
        if (pk == null)
            throw new IllegalArgumentException("pk is null");
        SQLParameter param = new SQLParameter();
        param.addParam(pk.trim());
        List results = (List) retrieveByClause(className, vo.getPKFieldName() + "=? and nvl(dr,0) = 0 ", selectedFields, param);
        if (results.size() >= 1)
            return results.get(0);
        return null;

    }

    /*
     * （非 Javadoc）
     *
     * @see nc.jdbc.framework.ee#retrieveByPK(java.lang.Class,
     * nc.jdbc.framework.mapping.IMappingMeta, java.lang.String)
     */
//	public Object retrieveByPK(Class className, IMappingMeta meta, String pk)
//			throws DbException {
//		return retrieveByPK(className, meta, pk, null);
//	}

    /*
     * （非 Javadoc）
     *
     * @see nc.jdbc.framework.ee#retrieveByPK(java.lang.Class,
     * nc.jdbc.framework.mapping.IMappingMeta, java.lang.String,
     * java.lang.String[])
     */
//	public Object retrieveByPK(Class className, IMappingMeta meta, String pk,
//			String[] selectedFields) throws DbException {
//		if (pk == null)
//			throw new IllegalArgumentException("pk is null");
//		SQLParameter param = new SQLParameter();
//		param.addParam(pk.trim());
//		List results = (List) retrieveByClause(className, meta,
//				meta.getPrimaryKey() + "=?", selectedFields, param);
//		if (results.size() >= 1)
//			return results.get(0);
//		return null;
//	}

    /**
     *
     */
    public Collection retrieve(SuperVO vo, boolean isAnd) throws DbException {
        return retrieve(vo, isAnd, null);

    }

//	public Collection retrieve(Object vo, IMappingMeta meta) throws DbException {
//		isNull(vo);
//		BeanMapping mapping = new BeanMapping(vo, meta);
//		// 得到插入的SQL语句
//		String sql = mapping.getSelectwithParamSQL();
//		SQLParameter param = mapping.getSelectParameter();
//		// session.setReadOnly(true);
//		return (Collection) session.executeQuery(sql, param,
//				new BeanMappingListProcessor(vo.getClass(), meta));
//	}

    public Collection retrieve(SuperVO vo, boolean isAnd, String[] fields)
            throws DbException {
        return (Collection) retrieve(vo, isAnd, fields, new BeanListProcessor(
                vo.getClass()));
    }

    public Collection retrieve(SuperVO vo, boolean isAnd, String[] fields,
                               String[] orderbyFields) throws DbException {
        isNull(vo);
        String tableName = vo.getTableName();
        // 得到插入字段类型的列表
        Object[][] types = getColmnTypes(tableName);

        //int len;
        Map<String, Integer> map = getMap(types);

        // 得到合法的字段列表
        String names[] = getNotNullValidNames(vo, map);
        // 得到插入的SQL语句
        String sql = SQLHelper.getSelectSQL(tableName, names, isAnd, fields);

        sql = appendOrderBy(sql, orderbyFields);

        SQLParameter param = getSQLParam(vo, names);
        // session.setReadOnly(true);
        return (Collection) session.executeQuery(sql, param,
                new BeanListProcessor(vo.getClass()));
    }

    private String appendOrderBy(String sql, String[] orderBys) {
        if (sql == null) {
            throw new RuntimeException("sql is null");
        }

        if (orderBys == null || orderBys.length == 0) {
            return sql;
        }

        StringBuffer orderClause = new StringBuffer(" ORDER BY ");

        int len = orderClause.length();

        for (String s : orderBys) {
            if (s != null) {
                orderClause.append(s).append(',');
            }
        }

        if (orderClause.length() > len) {
            orderClause.setLength(orderClause.length() - 1);

            return sql + orderClause;
        } else {
            return sql;
        }

    }

    public Object retrieve(SuperVO vo, boolean isAnd, String[] fields,
                           ResultSetProcessor processor) throws DbException {
        isNull(vo);
        String tableName = vo.getTableName();
        // 得到插入字段类型的列表
        Object[][] types = getColmnTypes(tableName);

        int len;
        Map<String, Integer> map = getMap(types);

        // 得到合法的字段列表
        String names[] = getNotNullValidNames(vo, map);
        // 得到插入的SQL语句
        String sql = SQLHelper.getSelectSQL(tableName, names, isAnd, fields);
        SQLParameter param = getSQLParam(vo, names);
        // session.setReadOnly(true);
        return session.executeQuery(sql, param, processor);
    }

    /**
     * @param className
     * @param meta
     * @return
     * @throws DbException
     */
//	public Collection retrieveAll(Class className, IMappingMeta meta)
//			throws DbException {
//		Object vo = InitClass(className);
//		BeanMapping mapping = new BeanMapping(vo, meta);
//		// session.setReadOnly(true);
//		return (Collection) session.executeQuery(mapping.getSelectSQL(),
//				new BeanMappingListProcessor(className, meta));
//
//	}

    /**
     *
     */
    public Collection retrieveAll(Class className) throws DbException {

        SuperVO vo = initSuperVOClass(className);
        String tableName = vo.getTableName();
        String sql = "SELECT * FROM " + tableName;
        // session.setReadOnly(true);
        return (Collection) session.executeQuery(sql, new BeanListProcessor(
                className));

    }

    /**
     *
     */
    public Collection retrieveByClause(Class className, String condition)
            throws DbException {
        return retrieveByClause(className, condition, null);
    }

    public Collection retrieveByClause(Class className, String condition,
                                       String[] fields, SQLParameter parameters) throws DbException {
        BaseProcessor processor = new BeanListProcessor(className);
        return (Collection) session.executeQuery(
                buildSql(className, condition, fields), parameters, processor);

    }

    public Collection retrieveByClause(Class className, String condition,
                                       String[] fields) throws DbException {
        return retrieveByClause(className, condition, fields, null);
    }

    /**
     * @param className
     * @param meta
     * @param condition
     * @param fields
     * @return
     * @throws DbException
     */
//	public Collection retrieveByClause(Class className, IMappingMeta meta,
//			String condition, String[] fields) throws DbException {
//		return retrieveByClause(className, meta, condition, fields, null);
//	}

//	public int getDBType() {
//		return session.getDbType();
//	}

    /**
     * @return
     * @throws SQLException
     */
//	public DatabaseMetaData getMetaData() {
//		if (dbmd == null)
//			dbmd = getJdbcSession().getMetaData();
//		return dbmd;
//	}

    /**
     * @param isAddTimeStamp
     */
    public void setAddTimeStamp(boolean isAddTimeStamp) {
        session.setAddTimeStamp(isAddTimeStamp);
    }

    /**
     * @param isTranslator
     */
    public void setSQLTranslator(boolean isTranslator) {
        //session.setSQLTranslator(isTranslator);
    }

//	public String getCatalog() {
//		String catalog = null;
//		switch (getDBType()) {
//		case DBConsts.GBASE:
//		case DBConsts.POSTGRESQL:
//			try {
//				catalog = getConnection().getCatalog();
//			} catch (SQLException e) {
//			}
//			break;
//		case DBConsts.SQLSERVER:
//		case DBConsts.DB2:
//			// null means drop catalog name from the selection criteria
//			catalog = null;
//			break;
//		case DBConsts.ORACLE:
//			catalog = null;
//			break;
//		case DBConsts.OSCAR:
//			// "" retrieves those without a catalog
//			catalog = "";
//			break;
//		}
//		return catalog;
//	}

    public String getSchema(DatabaseMetaData dMeta) {
        String strSche = null;
        try {
            String schema = dMeta.getUserName();
            switch (getDBType()) {
                case DBConsts.POSTGRESQL:
                    strSche = null;
                    break;
                case DBConsts.SQLSERVER:
                    strSche = "dbo";
                    break;
                case DBConsts.ORACLE:
                case DBConsts.OSCAR:
                case DBConsts.DB2: {
                    if (schema == null || schema.length() == 0)
                        throw new IllegalArgumentException(
                                "ORACLE Database mode does not allow to be null!!");
                    // ORACLE需将模式名大写
                    strSche = schema.toUpperCase();
                    break;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return strSche;
    }

//	public void setReadOnly(boolean isReadOnly) throws DbException {
//		session.setReadOnly(isReadOnly);
//	}

    /**
     * 初始化数据库会话连接
     *
     * @throws DbException
     */
    private void init() throws DbException {
        if (dataSource == null)
            session = new JdbcSession();
        else
            session = new JdbcSession(dataSource);

        session.setMaxRows(maxRows);
    }

    private void isNull(Object vo) {
        if (vo == null) {
            throw new IllegalArgumentException("vo object parameter is null!!");
        }
    }

    public void setMaxRows(int maxRows) {
        super.setMaxRows(maxRows);
        session.setMaxRows(maxRows);
    }

    /**
     * @param className
     * @param meta
     * @param condition
     * @return
     * @throws DbException
     */
//	public Collection retrieveByClause(Class className, IMappingMeta meta,
//			String condition) throws DbException {
//		return retrieveByClause(className, meta, condition, meta.getColumns());
//	}

    /**
     * 得到参数类型对象
     *
     * @param vo
     * @param names
     * @param types
     * @return
     */
    private SQLParameter getSQLParam(SuperVO vo, String names[],
                                     Map<String, Integer> types, Map<String, Integer> size) {
        SQLParameter params = new SQLParameter();
        for (int i = 0; i < names.length; i++) {
            if (names[i].equalsIgnoreCase("ts"))
                continue;
            int type = types.get(names[i].toUpperCase());
            Object value = vo.getAttributeValue(names[i]);
            if (value == null && type == Types.VARCHAR) {
                Integer length = size.get(names[i].toUpperCase());
                if (length != null
                        && (length == 20 || length == 36 || length == 101)) {
                    params.addParam(DBConsts.NULL_WAVE);
                    continue;
                }
            }
            if (value == null && type == Types.NVARCHAR) {
                Integer length = size.get(names[i].toUpperCase());
                if (length != null
                        && (length == 20 || length == 36 || length == 101)) {
                    params.addParam(DBConsts.NULL_WAVE);
                    continue;
                }
            }
            if (value == null) {
                params.addNullParam(type);
                continue;
            }
            if (type == Types.BLOB || type == Types.LONGVARBINARY
                    || type == Types.VARBINARY || type == Types.BINARY) {
                params.addBlobParam(value);
                continue;
            }
            if (type == Types.CLOB || type == Types.LONGVARCHAR) {
                params.addClobParam(String.valueOf(value));
                continue;
            }
            params.addParam(value);

        }
        return params;
    }

    /**
     * 得到有效的列名称
     *
     * @param vo
     * @param types
     * @return
     */
    private String[] getValidNames(final SuperVO vo, Map types) {
        String names[] = vo.getAttributeNames();

        List nameList = new ArrayList();
        for (int i = 0; i < names.length; i++) {
            if (types.get(names[i].toUpperCase()) != null
                    && !names[i].equalsIgnoreCase("ts")) {
                nameList.add(names[i]);
            }
        }
        return (String[]) nameList.toArray(new String[]{});
    }

    /**
     * 得到有效列名称
     *
     * @param vo
     * @param types
     * @param pkName
     * @return
     */
    private String[] getUpdateValidNames(SuperVO vo, Map types, String pkName) {
        String names[] = vo.getAttributeNames();
        List nameList = new ArrayList();
        for (int i = 0; i < names.length; i++) {
            if (types.get(names[i].toUpperCase()) != null
                    && !names[i].equalsIgnoreCase(pkName)
                    && !names[i].equalsIgnoreCase("ts")) {
                nameList.add(names[i]);
            }
        }
        return (String[]) nameList.toArray(new String[]{});
    }

    /**
     * @param vo
     * @param type
     * @return
     */
    private String[] getNotNullValidNames(SuperVO vo, Map type) {
        String names[] = vo.getAttributeNames();
        List nameList = new ArrayList();
        for (int i = 0; i < names.length; i++) {
            if (type.get(names[i].toUpperCase()) != null
                    && vo.getAttributeValue(names[i]) != null) {
                nameList.add(names[i]);
            }
        }
        if (nameList.size() == 0)
            return new String[0];
        return (String[]) nameList.toArray(new String[]{});
    }

    /**
     * @param vo
     * @param names
     * @return
     */
    private SQLParameter getSQLParam(SuperVO vo, String[] names) {
        if (names == null || names.length == 0) {
            return null;
        }
        SQLParameter parameter = new SQLParameter();
        for (int i = 0; i < names.length; i++) {
            parameter.addParam(vo.getAttributeValue(names[i]));
        }
        return parameter;
    }

//	public Connection getConnection() {
//		if (session != null)
//			return session.getConnection();
//		return null;
//	}

    private ColCache getColCache() {
        DataSource ds1 = ds();
        //String ds=ds1.toString();
        String ds = null;
        if (ds1 instanceof DruidXADataSource) {
            ds = ((DruidXADataSource) ds1).getName();
        } else {
            ds = "DataSource-" + System.identityHashCode(this);
        }
        //String ds=ds1.toString();
        synchronized (colCacheMap) {
            ColCache colCache = colCacheMap.get(ds);
            if (colCache == null) {
                colCache = new ColCache();
                colCacheMap.put(ds, colCache);
            }
            return colCache;
        }
    }

    private DataSource ds() {
        return dataSource;
    }

    public int getDBType() {
        return DBConsts.ORACLE;
    }

    public List<Object[][]> executeQuery(final String table) throws DbException {

        ConnectionCallback<List<Object[][]>> ccb = new ConnectionCallback<List<Object[][]>>() {

            @Override
            public List<Object[][]> doInConnection(Connection con)
                    throws SQLException, DataAccessException {
                List<Object[][]> list = new ArrayList<Object[][]>();
                Map<String, Integer> typeMap = new HashMap<String, Integer>();
                Map<String, Integer> sizeMap = new HashMap<String, Integer>();
                ResultSet rsColumns = null;
                try {
                    if (getDBType() == DBConsts.SQLSERVER && table.startsWith("#")) {
                        Statement stmt = null;
                        try {
                            stmt = con.createStatement();
                            rsColumns = stmt.executeQuery("select top 0 * from " + table);
                            ResultSetMetaData rsMeta = rsColumns.getMetaData();
                            int count = rsMeta.getColumnCount();

                            for (int i = 1; i < count + 1; i++) {
                                typeMap.put(rsMeta.getColumnName(i),
                                        rsMeta.getColumnType(i));

                                sizeMap.put(rsMeta.getColumnName(i)
                                        .toUpperCase(), rsMeta.getPrecision(i));

                            }
                            if (typeMap.size() > 0) {
                                Object[][] ov = null;
                                List<Object[]> lt = new ArrayList<Object[]>();
                                for (String key : typeMap.keySet()) {
                                    lt.add(new Object[]{key, typeMap.get(key)});
                                }
                                list.add((Object[][]) lt.toArray(new Object[0][]));
                                lt.clear();
                                ;
                                for (String key : sizeMap.keySet()) {
                                    lt.add(new Object[]{key, sizeMap.get(key)});
                                }
                                list.add((Object[][]) lt.toArray(new Object[0][]));
                            } else {
                                throw new SQLException("no column info for: " + table + " at datasource: " + ds());

                            }

                            return list;
                        } finally {
                            if (stmt != null) {
                                stmt.close();
                            }
                        }

                    } else {
                        DatabaseMetaData dMeta = con.getMetaData();
                        if (getDBType() == DBConsts.SQLSERVER)
                            rsColumns = dMeta.getColumns(null, null,
                                    table.toUpperCase(), "%");
                        else if (getDBType() == DBConsts.POSTGRESQL)
                            rsColumns = dMeta.getColumns(null, null,
                                    table.toLowerCase(), "%");
                        else
                            rsColumns = dMeta.getColumns(null, getSchema(dMeta),
                                    table.toUpperCase(), "%");
                        while (rsColumns.next()) {
                            String columnName = rsColumns.getString("COLUMN_NAME")
                                    .toUpperCase();
                            int columnType = rsColumns.getShort("DATA_TYPE");
                            typeMap.put(columnName, columnType);

                            sizeMap.put(rsColumns.getString("COLUMN_NAME")
                                    .toUpperCase(), rsColumns
                                    .getInt("COLUMN_SIZE"));

                        }

                        if (typeMap.size() > 0) {
                            Object[][] ov = null;
                            List<Object[]> lt = new ArrayList<Object[]>();
                            for (String key : typeMap.keySet()) {
                                lt.add(new Object[]{key, typeMap.get(key)});
                            }
                            list.add((Object[][]) lt.toArray(new Object[0][]));
                            lt.clear();
                            ;
                            for (String key : sizeMap.keySet()) {
                                lt.add(new Object[]{key, sizeMap.get(key)});
                            }
                            list.add((Object[][]) lt.toArray(new Object[0][]));
                        } else {
                            throw new SQLException("no column info for: " + table + " at datasource: " + ds());

                        }

                        return list;
                    }
                } catch (SQLException e) {
                    log.error("get table metadata error", e);
                    throw new SQLException("get table metadata error", e);
                } finally {
                    DBUtil.closeRs(rsColumns);
                }
            }
        };

        try {
            return session.execute1(ccb);
        } catch (Exception e) {
            throw ExceptionFactory.getException(1, e.getMessage());
        }

    }

    private Object[][] getColmnSize(String table) throws DbException {
        ColCache cache = getColCache();

        Object[][] result = cache.sizeCache.get(table);
        if (result == null || result.length == 0) {
            List<Object[][]> list = executeQuery(table);
            if (list != null && list.size() == 2) {
                result = list.get(1);
                //cache.typeCache.put(table, result);
                cache.sizeCache.put(table, result);
            }
        }

        return result;
    }

    /**
     * 得到列的类型
     *
     * @param table
     * @return
     */
    private Object[][] getColmnTypes(String table) throws DbException {

        ColCache cache = getColCache();

        Object[][] result = cache.typeCache.get(table);

        // 同时查出列的长度信息
        //Object[][] sizeMap = cache.sizeCache.get(table);
        if (result == null || result.length == 0) {
            List<Object[][]> list = executeQuery(table);//	session.executeQuery(table);
            if (list != null && list.size() == 2) {
                result = list.get(0);
                cache.typeCache.put(table, result);
                cache.sizeCache.put(table, list.get(1));
            }
        }

        return result;
    }

    public static void clearColumnTypes(String table) {
        if (colCacheMap.size() == 0) {
            return;
        }
        for (ColCache colCache : colCacheMap.values()) {
            colCache.typeCache.remove(table);
            colCache.sizeCache.remove(table);
        }
    }

    private Object InitClass(Class className) {
        try {
            return className.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(
                    "Parameter Class can not be instantiated!!");
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Illegal Parameter!!");
        }
    }

    private SuperVO initSuperVOClass(Class className) {
        Object vo;
        try {
            vo = className.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(
                    "Parameter Class can not be instantiated!!");
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(" Illegal Parameter!!");
        }
        if (!(vo instanceof SuperVO))
            throw new IllegalArgumentException("Parameter Class is not SuperVO");
        return (SuperVO) vo;
    }

    private String buildSql(Class className, String condition, String[] fields) {
        SuperVO vo = (SuperVO) InitClass(className);
        String pkName = vo.getPKFieldName();
        boolean hasPKField = false;
        StringBuffer buffer = new StringBuffer();
        String tableName = vo.getTableName();
        if (fields == null)
            buffer.append("SELECT * FROM ").append(tableName);
        else {
            buffer.append("SELECT ");
            for (int i = 0; i < fields.length; i++) {
                if (fields[i] != null) {
                    buffer.append(fields[i]).append(",");
                    if (fields[i].equalsIgnoreCase(pkName))
                        hasPKField = true;
                }
            }
            if (!hasPKField)
                buffer.append(pkName).append(",");
            buffer.setLength(buffer.length() - 1);
            buffer.append(" FROM ").append(tableName);
        }
        if (condition != null && condition.length() != 0) {
            if (condition.toUpperCase().trim().startsWith("ORDER "))
                buffer.append(" ").append(condition);
            else
                buffer.append(" WHERE ").append(condition);
        }

        return buffer.toString();
    }


    /**
     * 构造参数对象
     */
    private SQLParameter getSQLParam(SuperVO vo, String[] attribNames,
                                     String[] columnNames, Map<String, Integer> types,
                                     Map<String, Integer> size) {
        SQLParameter params = new SQLParameter();
        for (int i = 0; i < attribNames.length; i++) {
            if (attribNames[i].equalsIgnoreCase("ts"))
                continue;
            // int type = types.get(columnNames[i].toUpperCase());
            int type = -1;
            try {
                type = types.get(columnNames[i].toUpperCase());
            } catch (NullPointerException e) {
                log.error("get param type error, types=" + types
                        + ",columnNames=" + columnNames[i] + ",i=" + i, e);
                throw e;
            }
            Object value = vo.getAttributeValue(attribNames[i]);
            if (value == null && type == Types.VARCHAR) {
                int length = size.get(columnNames[i].toUpperCase());
                if (length == 20 || length == 36 || length == 101) {
                    params.addParam(DBConsts.NULL_WAVE);
                    continue;
                }
            }
            if (value == null && type == Types.NVARCHAR) {
                int length = size.get(columnNames[i].toUpperCase());
                if (length == 20 || length == 36 || length == 101) {
                    params.addParam(DBConsts.NULL_WAVE);
                    continue;
                }
            }
            if (value == null) {
                params.addNullParam(type);
                continue;
            }
            if (type == Types.BLOB || type == Types.LONGVARBINARY
                    || type == Types.VARBINARY || type == Types.BINARY) {
                params.addBlobParam(value);
                continue;
            }
            if (type == Types.CLOB || type == Types.LONGVARCHAR) {
                params.addClobParam(String.valueOf(value));
                continue;
            }
            params.addParam(value);

        }
        return params;
    }

    @Override
    public void release() {
        // TODO Auto-generated method stub

    }


}
