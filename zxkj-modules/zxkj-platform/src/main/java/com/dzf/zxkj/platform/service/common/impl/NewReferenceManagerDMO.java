package com.dzf.zxkj.platform.service.common.impl;


import com.dzf.zxkj.base.dao.BaseDAO;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.exception.DAOException;
import com.dzf.zxkj.platform.service.common.IInSqlBatchCallBack;
import com.dzf.zxkj.platform.service.common.InSqlBatchCaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 基础数据引用表。 创建日期：(2001-7-17 15:44:24)
 * 
 * @author：赵继江
 */
@Slf4j
public class NewReferenceManagerDMO /* extends DataManageObject */
{

	private SingleObjectBO singleObjectBO;
	
	
	
	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}
	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	
	class BD_Realtion {
		String corpfieldName;

		String fieldName;

		String selfPkName;

		String tableName;

		/**
		 * @param tableName
		 * @param fieldName
		 */
		public BD_Realtion(String tableName, String fieldName,
				String selfPkName, String corpfieldName) {
			super();
			this.tableName = tableName;
			this.fieldName = fieldName;
			this.selfPkName = selfPkName;
			this.corpfieldName = corpfieldName;
		}
	}

	private static final String WITH_CORP_FLAG = "$";
	private final ResultSetProcessor ResultSetHasDataJudger = new ResultSetProcessor() {
		private static final long serialVersionUID = 8347151336502175602L;

		public Object handleResultSet(ResultSet rs) throws SQLException {
			rs.next();
			return rs.getInt(1) > 0;

			// if (rs.next())
			// return Boolean.TRUE;
			// else
			// return Boolean.FALSE;
		}
	};

	private HashMap<String, List<BD_Realtion>> tableName_ReferenceTables_Map;

	/**
	 * ReferenceManagerDMO 构造子注解。
	 * 
	 * @exception javax.naming.NamingException
	 *                异常说明。
	 * @exception nc.bs.pub.SystemException
	 *                异常说明。
	 */
	public NewReferenceManagerDMO(SingleObjectBO singleObjectBO) {
		super();
		if(singleObjectBO==null)
			throw new DAOException("SingleObjectBO未成功初始化");
		this.singleObjectBO=singleObjectBO;
	}

	private Boolean checkBasePKsIsRefrencedInCorp(final String tableName,
			String pk_corp, String inSql, boolean isModifyCheck)
			throws DAOException {
		List<BD_Realtion> relationList = getRelationListByTableNameAndWithcorp(
				tableName, true, isModifyCheck);
		for (BD_Realtion relation : relationList) {
			String sqlQuery = getInSqlQeuryWithCorp(inSql, pk_corp, relation);
			try {
				SQLParameter param = new SQLParameter();
				param.addParam(pk_corp);
				Boolean referenced = (Boolean) getSingleObjectBO().executeQuery(
						sqlQuery, param, ResultSetHasDataJudger);
				if (referenced.booleanValue())
					return Boolean.TRUE;

			} catch (DAOException e) {// 如果发生了错误认为是引用注册表中注册了不存在表(或对应产品没有安装).
				log.warn("查询数据库表" + relation.tableName + "对表" + tableName
						+ "的引用时出错,可能是对应产品没安装");
				log.warn(e.getMessage(), e);
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * 查询指定表的多个PK是否被引用。
	 * 
	 * @param tableName
	 *            查询引用的表
	 * @param inSql
	 *            多个PK拼成的InSql. 形如('PK1','PK2',...'PKn')
	 * @return 任意一个PK被引用就返回true,所有PK都未被引用则返回false.
	 * @throws DAOException
	 */
	private Boolean checkPKsIsRefrenced(final String tableName, String inSql,
			boolean isModifyCheck) throws DAOException {
		List<BD_Realtion> relationList = getRelationListByTableName(tableName,
				isModifyCheck);
		for (int i = 0; i < relationList.size(); i++) {
			BD_Realtion relation = (BD_Realtion) relationList.get(i);
			String sqlQuery = getInSqlQeury(inSql, relation);
			try {
				Boolean referenced = (Boolean)getSingleObjectBO().executeQuery(
						sqlQuery,null, ResultSetHasDataJudger);
				if (referenced.booleanValue())
					return Boolean.TRUE;

			} catch (DAOException e) {// 如果发生了错误认为是引用注册表中注册了不存在表(或对应产品没有安装).
				log.warn("查询数据库表" + relation.tableName + "对表" + tableName
						+ "的引用时出错,可能是对应产品没安装");
				log.warn(e.getMessage(), e);
			}
		}
		return Boolean.FALSE;
	}

//	private boolean checkReferenceBySql(String tableName, BD_Realtion relation,
//			String key, String sql) {
//		return checkReferenceBySql(tableName, null, relation, key, sql);
//	}

	private boolean checkReferenceBySql(String tableName, String pk_corp,
			BD_Realtion relation, String key, String sql) {

		boolean referenced = false;
		try {
			SQLParameter para = new SQLParameter();
			para.addParam(key);
			if (relation.corpfieldName != null
					&& relation.corpfieldName.trim().length() > 0)
				para.addParam(pk_corp);
			referenced = (Boolean) getSingleObjectBO().executeQuery(sql, para,
					ResultSetHasDataJudger);
		} catch (DAOException e) {// 如果发生了错误一般是引用注册表中注册了不存在表(或对应产品没有安装).忽略.
			log.warn("查询数据库表" + relation.tableName + "对表" + tableName
					+ "的引用时出错,可能是对应产品没安装");
			log.warn(e.getMessage(), e);
		}
		return referenced;
	}

	private boolean checkReferenceHelper(String tableName, String key,
			List<BD_Realtion> relationList) {
		return checkReferenceHelper(tableName, null, key, relationList);
	}

	private boolean checkReferenceHelper(String tableName, String pk_corp,
			String key, List<BD_Realtion> relationList) {
		// 没有引用该表的情况：
		if (relationList.size() == 0)
			return false;
		for (BD_Realtion relation : relationList) {
			String checkSqlWithoutDr = getSqlQeury(key, relation);
			String checkSqlWithDr = getSqlQeuryWithDr(key, relation);
			// 先不包含dr进行查询，尽量利用上索引，如果被引用了，则加上dr条件确认一下。
			boolean referenced = checkReferenceBySql(tableName, pk_corp, relation, key,
					checkSqlWithoutDr)
					&& checkReferenceBySql(tableName, pk_corp, relation, key,
							checkSqlWithDr);
			if (referenced) {
//			    throw new BusinessException(relation.errorMsg);
				return true;
			}
		}
		return false;
	}

	private List<BD_Realtion> filterValidRelations(String[] excludedTableNames,
			List<BD_Realtion> relationList) {
		Set<String> excludeSet = new HashSet<String>();
		if (excludedTableNames != null && excludedTableNames.length > 0)
			excludeSet.addAll(Arrays.asList(excludedTableNames));

		List<BD_Realtion> fieltedList = new ArrayList<BD_Realtion>();
		for (BD_Realtion rel : relationList) {
			if (!excludeSet.contains(rel.tableName.toLowerCase())) {
				fieltedList.add(rel);
			}
		}
		return fieltedList;
	}

	/**
	 * @param key
	 * @param relation
	 * @return
	 */
	private String getInSqlQeury(String inSql, BD_Realtion relation) {
		StringBuffer buf = new StringBuffer();
		buf.append("select count(1) from dual where exists (");
		buf.append("select ");
		// buf.append(relation.fieldName);
		buf.append("1");
		buf.append(" from ");
		buf.append(relation.tableName);
		buf.append(" where ");
		buf.append(relation.fieldName);
		buf.append(" in ");
		buf.append(inSql);
		buf.append(" and dr=0");
		buf.append(")");
		String insql = buf.toString();
		return insql;
	}

	private String getInSqlQeuryWithCorp(String inSql, String pk_corp,
			BD_Realtion relation) {
		StringBuffer buf = new StringBuffer();
		buf.append("select count(1) from dual where exists (");
		buf.append("select ");
		// buf.append(relation.fieldName);
		buf.append("1");
		buf.append(" from ");
		buf.append(relation.tableName);
		buf.append(" where ");
		buf.append(relation.fieldName);
		buf.append(" in ");
		buf.append(inSql);
		if (relation.corpfieldName != null
				&& relation.corpfieldName.trim().length() > 0) {
			buf.append(" and ");
			buf.append(relation.corpfieldName);
			buf.append(" = ? ");
		}
		buf.append(" and dr=0");
		buf.append(")");
		String insql = buf.toString();
		return insql;
	}

	public Set<String> getReferencedBasePksInCorp(String tableName,
			List<String> basePks, final String pk_corp)throws DAOException {
		if (tableName == null)
			throw new IllegalArgumentException("talbeName cann't be null");
		final Set<String> referencedPkSet = new HashSet<String>();
		if (basePks == null || basePks.size() == 0)
			return referencedPkSet;
		
		Set<String> unReferencedPkSet = new HashSet<String>(basePks);
		// 查询所有引用tableName的表和字段
		ArrayList<BD_Realtion> relationList = getRelationListByTableNameAndWithcorp(
				tableName, true, false);
		// 如果没有被其它表引用,直接返回null
		if (relationList == null || relationList.size() == 0)
			return referencedPkSet;
		final BaseDAO baseDAO = new BaseDAO(getSingleObjectBO().getDataSource());
		for (Iterator<BD_Realtion> iterator = relationList.iterator(); iterator
				.hasNext();) {
			BD_Realtion relation = (BD_Realtion) iterator.next();
			// 构造查询语句
			StringBuilder sql = new StringBuilder();
			sql.append("select distinct ");
			sql.append(relation.fieldName);
			sql.append(", dr ");
			sql.append("from ");
			sql.append(relation.tableName);
			sql.append(" where ");
			sql.append(relation.corpfieldName);
			sql.append(" = ? ");
			sql.append(" and ");
			sql.append(relation.fieldName);
			sql.append(" in ");
			//TODO
			try {
				final String selectSql = sql.toString();
				InSqlBatchCaller caller = new InSqlBatchCaller(
						unReferencedPkSet.toArray(new String[0]));
				final SQLParameter param = new SQLParameter();
				param.addParam(pk_corp);
				caller.execute(new IInSqlBatchCallBack() {
					@SuppressWarnings("unchecked")
					public Object doWithInSql(String inSql)
							throws DAOException {
						List<Object[]> rs = (List<Object[]>) baseDAO.executeQuery(new StringBuilder(
												selectSql).append(inSql)
												.toString(), param, new ArrayListProcessor());
						for (Iterator iterator2 = rs.iterator(); iterator2
								.hasNext();) {
							Object[] row = (Object[]) iterator2.next();
							if(row[1] == null || Integer.valueOf(row[1].toString()) == 0)
								referencedPkSet.add(row[0].toString());
						}
						return null;
					}});
			}  catch (DAOException e) {
				log.warn("查询数据库表" + relation.tableName + "对表" + tableName
						+ "的引用时出错,可能是对应产品没安装");
				log.warn(e.getMessage(), e);
			}
		}
		return referencedPkSet;
	}
	/**
	 * 查询指定的主键中被引用的主键,返回被引用主键数组. 如果所有主键均未被引用,返回null.
	 * 
	 * @param tableName
	 * @param keys
	 * @param isModifyCheck
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public String[] getReferencedKeys(String tableName, String[] keys,
			boolean isModifyCheck) throws DAOException {
		if (tableName == null)
			throw new IllegalArgumentException("talbeName cann't be null");
		if (keys == null || keys.length == 0)
			return null;
		Set<String> referencedPkSet = new HashSet<String>();
		Set<String> unReferencedPkSet = new HashSet<String>(Arrays.asList(keys));
		// 查询所有引用tableName的表和字段
		ArrayList<BD_Realtion> relationList = getRelationListByTableName(
				tableName, isModifyCheck);
		// 如果没有被其它表引用,直接返回null
		if (relationList == null || relationList.size() == 0)
			return null;
		final BaseDAO baseDAO = new BaseDAO(getSingleObjectBO().getDataSource());
		for (Iterator<BD_Realtion> iterator = relationList.iterator(); iterator
				.hasNext();) {
			BD_Realtion relation = (BD_Realtion) iterator.next();
			// 构造查询语句
			StringBuilder sql = new StringBuilder();
			sql.append("select distinct ");
			sql.append(relation.fieldName);
			sql.append(" from ");
			sql.append(relation.tableName);
			sql.append(" where ");
			sql.append(relation.fieldName);
			sql.append(" in ");
			//TODO
			try {
				// 先不检查dr=0,查询被引用的主键(因为nvl无法使用索引,因此先不加dr=0的条件)
				final String selectSql = sql.toString();
				InSqlBatchCaller caller = new InSqlBatchCaller(
						unReferencedPkSet.toArray(new String[0]));
				List<String> tempList = (List<String>) caller
						.execute(new IInSqlBatchCallBack() {
							List<String> l = new ArrayList<String>();

							@SuppressWarnings("unchecked")
							public Object doWithInSql(String inSql)
									throws DAOException {
								List<String> tempList = (List<String>)getSingleObjectBO().executeQuery
										(new StringBuilder(
												selectSql).append(inSql)
												.toString(),null,
												new ColumnListProcessor());
								l.addAll(tempList);
								return l;
							}
						});

				if (!tempList.isEmpty()) {
					// 检查被引用的主键,是否被有效单据引用(即检查dr=0)
					InSqlBatchCaller checkCaller = new InSqlBatchCaller(
							tempList.toArray(new String[0]));
					List<String> checkedList = (List<String>) checkCaller
							.execute(new IInSqlBatchCallBack() {
								List<String> l = new ArrayList<String>();

								public Object doWithInSql(String inSql)
										throws DAOException {
									String checkSql = new StringBuilder(
											selectSql).append(inSql).append(
											" and nvl(dr,0) = 0 ")
											.toString();
									List<String> tempList = (List<String>)getSingleObjectBO().executeQuery(checkSql,null,
													new ColumnListProcessor());
									l.addAll(tempList);
									return l;
								}
							});
					if (!checkedList.isEmpty()) {
						referencedPkSet.addAll(checkedList);
						unReferencedPkSet.removeAll(checkedList);
						if (unReferencedPkSet.isEmpty())
							break;
					}
				}
			} catch (DAOException e) {
				log.warn("查询数据库表" + relation.tableName + "对表" + tableName
						+ "的引用时出错,可能是对应产品没安装");
				log.warn(e.getMessage(), e);
			}
		}
		return referencedPkSet.size() == 0 ? null : referencedPkSet
				.toArray(new String[0]);
	}

	private ArrayList<BD_Realtion> getRelationListByTableName(String tableName,
			boolean isModifyCheck) throws DAOException {
		return getRelationListByTableNameAndWithcorp(tableName, false,
				isModifyCheck);
	}

	/**
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<BD_Realtion> getRelationListByTableNameAndWithcorp(
			String tableName, boolean isWithCorp, boolean isModifyCheck)
			throws DAOException {
		String newTableName = isWithCorp ? WITH_CORP_FLAG + tableName
				: tableName;

		ArrayList<BD_Realtion> al = (ArrayList<BD_Realtion>) this
				.getTableName_ReferenceTables_Map().get(newTableName);
		if (al == null) {
			// 选择所有引用该表的表：
			String sql = "select referencedtablekey, referencingtablename, referencingtablecolumn, referencingcorpfield from bd_ref_relation where referencedtablename = '"
					+ tableName + "'";
			if (isWithCorp) {
				sql += " and referencingcorpfield is not null ";
			} else {
				sql += " and referencingcorpfield is null ";
			}
			if (isModifyCheck) {
				sql += " and ismodifycheck = 'Y'";
			}
			ResultSetProcessor p = new ResultSetProcessor() {
				private static final long serialVersionUID = 3730593948830478187L;

				public Object handleResultSet(ResultSet rs) throws SQLException {
					ArrayList<BD_Realtion> result = new ArrayList<BD_Realtion>();
					while (rs.next()) {
						String ReferencedTableKey = rs
								.getString("referencedtablekey");
						String ReferencingTableName = rs
								.getString("referencingtablename");
						String ReferencingTableColumn = rs
								.getString("referencingtablecolumn");
						String referencingcorpfield = rs
								.getString("referencingcorpfield");
						BD_Realtion r = new BD_Realtion(ReferencingTableName,
								ReferencingTableColumn, ReferencedTableKey,
								referencingcorpfield);
						result.add(r);
					}
					return result;
				}
			};

			al = (ArrayList<BD_Realtion>) getSingleObjectBO().executeQuery(sql,null, p);
			getTableName_ReferenceTables_Map().put(newTableName, al);
		}
		return al;
	}

	private String getSqlForReferenceInfo(BD_Realtion ref,
			String referencingTablePkFieldName) {
		StringBuffer buf = new StringBuffer();
		buf.append("select ");
		buf.append(referencingTablePkFieldName);
		buf.append(" from");
		buf.append(ref.tableName);
		buf.append(" where ");
		buf.append(ref.fieldName);
		buf.append("=");
		buf.append(" ? ");

		return buf.toString();
	}

	/**
	 * @param key
	 * @param relation
	 * @return
	 */
	private String getSqlQeury(String key, BD_Realtion relation) {
		// 利用where exists的短路特性来提高效率, 选用bd_currtype是因为
		// 对于NC来说该表一定存在而且数据量够小。 以后可也考虑建立一个专门的辅助表.
		StringBuilder buf = new StringBuilder();
		buf.append("select count(1) from dual where exists (");
		buf.append("select ");
		buf.append("1");
		buf.append(" from ");
		buf.append(relation.tableName);
		buf.append(" where ");
		buf.append(relation.fieldName);
		buf.append("=");
		buf.append("?");
		if (relation.corpfieldName != null
				&& relation.corpfieldName.trim().length() > 0) {
			buf.append(" and ");
			buf.append(relation.corpfieldName);
			buf.append(" = ? ");
		}
		buf.append(")");
		String sql = buf.toString();
		return sql;
	}

	private String getSqlQeuryWithDr(String key, BD_Realtion relation) {
		StringBuffer buf = new StringBuffer();
		buf.append("select count(1) from dual where exists (");
		buf.append("select ");
		buf.append("1");
		buf.append(" from ");
		buf.append(relation.tableName);
		buf.append(" where ");
		buf.append(relation.fieldName);
		buf.append("=");
		buf.append("?");
		if (relation.corpfieldName != null
				&& relation.corpfieldName.trim().length() > 0) {
			buf.append(" and ");
			buf.append(relation.corpfieldName);
			buf.append(" = ? ");
		}
		buf.append(" and nvl(dr,0)=0");
		buf.append(")");
		String sql = buf.toString();
		return sql;
	}

	protected HashMap<String, List<BD_Realtion>> getTableName_ReferenceTables_Map() {
		if (this.tableName_ReferenceTables_Map == null) {
			this.tableName_ReferenceTables_Map = new HashMap<String, List<BD_Realtion>>();
		}
		return this.tableName_ReferenceTables_Map;
	}

	public boolean isBasePkReferencedInCorp(String tableName, String pk_corp,
			String key, boolean isModifyCheck) throws DAOException {
		List<BD_Realtion> relationList = getRelationListByTableNameAndWithcorp(
				tableName, true, isModifyCheck);
		return checkReferenceHelper(tableName, pk_corp, key, relationList);
	}

	public boolean isBasePkReferencedInCorp(String tableName, String pk_corp,
			String key, String[] excludedTableNames, boolean isModifyCheck)
			throws DAOException {
		List<BD_Realtion> relationList = getRelationListByTableNameAndWithcorp(
				tableName, true, isModifyCheck);
		List<BD_Realtion> fieltedList = filterValidRelations(
				excludedTableNames, relationList);
		return checkReferenceHelper(tableName, pk_corp, key, fieltedList);
	}

	public boolean isBasePksReferencedInCorp(final String tableName,
			final String pk_corp, List<String> keys, final boolean isModifyCheck)
			throws DAOException {
		if (tableName == null || keys == null || keys.size() == 0)
			return false;
		if (keys.size() == 1)
			return isBasePkReferencedInCorp(tableName, pk_corp, keys.get(0),
					isModifyCheck);
//TODO
		InSqlBatchCaller caller = new InSqlBatchCaller((ArrayList<String>) keys);

		IInSqlBatchCallBack callback = new IInSqlBatchCallBack() {
			Boolean referenced = Boolean.FALSE;

			public Object doWithInSql(String inSql) throws DAOException {
				Boolean referenceFlag = checkBasePKsIsRefrencedInCorp(
						tableName, pk_corp, inSql, isModifyCheck);
				referenced = Boolean.valueOf(referenced.booleanValue()
						|| referenceFlag.booleanValue());
				return referenced;
			}
		};
		try {
			Boolean result = (Boolean) caller.execute(callback);
			return result.booleanValue();

		} catch (DAOException e)// 忽略查询引用时发生的异常
		{
			log.warn("查询引用发生异常,可以忽略.");
			log.warn(e.getMessage(), e);
		} catch (Exception e)// 忽略查询引用时发生的异常
		{
			log.warn("查询引用发生异常,可以忽略.");
			log.warn(e.getMessage(), e);
		}
		return false;
	}

	// /**
	// * 检查tableName中主键值为keys的基类是否被引用
	// * 任何一个记录被引用将返回true
	// * 没有一个记录被引用将返回false
	// * @param tableName
	// * @param keys
	// * @return
	// * @throws DAOException
	// */
	// public boolean isReferenced(final String tableName, String[] keys) throws
	// DAOException
	// {
	// if(tableName==null||keys==null||keys.length==0)
	// return false;
	// if(keys.length==1)
	// return isReferenced(tableName,keys[0]);
	// ArrayList al = new ArrayList();
	// al.addAll(Arrays.asList(keys));
	// return isReferenced(tableName,al);
	// }
	/**
	 * 检查tableName中主键值为keys的记录是否被引用 任何一个记录被引用将返回true 没有一个记录被引用将返回false
	 * 
	 * @param tableName
	 * @param keys
	 * @return
	 * @throws DAOException
	 */
	public boolean isReferenced(final String tableName, List<String> keys,
			final boolean isModifyCheck) throws DAOException {

		if (tableName == null || keys == null || keys.size() == 0)
			return false;
		if (keys.size() == 1)
			return isReferenced(tableName, (String) keys.get(0), isModifyCheck);
//TODO
		InSqlBatchCaller caller = new InSqlBatchCaller((ArrayList<String>) keys);

		IInSqlBatchCallBack callback = new IInSqlBatchCallBack() {
			Boolean referenced = Boolean.FALSE;

			public Object doWithInSql(String inSql) throws DAOException {
				Boolean referenceFlag = checkPKsIsRefrenced(tableName, inSql,
						isModifyCheck);
				referenced = Boolean.valueOf(referenced.booleanValue()
						|| referenceFlag.booleanValue());
				return referenced;
			}
		};
		try {
			Boolean result = (Boolean) caller.execute(callback);
			return result.booleanValue();

		} catch (DAOException e)// 忽略查询引用时发生的异常
		{
			log.warn("查询引用发生异常,可以忽略.");
			log.warn(e.getMessage(), e);
		} catch (Exception e)// 忽略查询引用时发生的异常
		{
			log.warn("查询引用发生异常,可以忽略.");
			log.warn(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * 输入表名和需要检查被引用了的主键的值，返回是否被引用
	 * 
	 * @param tableName
	 *            表名
	 * @param key
	 *            需要检查被引用了的主键的值
	 * @return
	 * @throws SQLException
	 */
	public boolean isReferenced(String tableName, String key,
			boolean isModifyCheck) throws DAOException {

		List<BD_Realtion> relationList = getRelationListByTableName(tableName,
				isModifyCheck);

		return checkReferenceHelper(tableName, key, relationList);
	}

	public boolean isReferenced(String tableName, String key,
			String[] excludedTableNames, boolean isModifyCheck)
			throws DAOException {
		List<BD_Realtion> relationList = getRelationListByTableName(tableName,
				isModifyCheck);
		List<BD_Realtion> fieltedList = filterValidRelations(
				excludedTableNames, relationList);
		return checkReferenceHelper(tableName, key, fieltedList);
	}
}