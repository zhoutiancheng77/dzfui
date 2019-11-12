package com.dzf.zxkj.platform.service.common.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DAOException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 基础数据引用表。 创建日期：(2001-7-17 15:44:24)
 * 
 * @author：赵继江
 */
@Slf4j
public class ReferenceManagerDMO /* extends DataManageObject */
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
		
		String errorMsg;

		/**
		 * @param tableName
		 * @param fieldName
		 */
		public BD_Realtion(String tableName, String fieldName,
				String selfPkName, String corpfieldName,String errorMsg) {
			super();
			this.tableName = tableName;
			this.fieldName = fieldName;
			this.selfPkName = selfPkName;
			this.corpfieldName = corpfieldName;
			this.errorMsg = errorMsg;
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
	public ReferenceManagerDMO(SingleObjectBO singleObjectBO) {
		super();
		if(singleObjectBO==null)
			throw new DAOException("SingleObjectBO未成功初始化");
		this.singleObjectBO=singleObjectBO;
	}

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
			String sql = "select referencedtablekey, referencingtablename, referencingtablecolumn, referencingcorpfield,refmsg from bd_ref_relation where referencedtablename = '"
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
						String errormsg = rs.getString("refmsg");
						BD_Realtion r = new BD_Realtion(ReferencingTableName,
								ReferencingTableColumn, ReferencedTableKey,
								referencingcorpfield,errormsg);
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

   public void isReferencedRefmsg(String tableName, String key,
            boolean isModifyCheck) throws DZFWarpException {

        List<BD_Realtion> relationList = getRelationListByTableName(tableName,
                isModifyCheck);

        checkReferenceHelper(tableName, key, relationList);
    }

	
	private void checkReferenceHelper(String tableName, String key,
            List<BD_Realtion> relationList) throws DZFWarpException{
        checkReferenceHelper(tableName, null, key, relationList);
    }

    private void checkReferenceHelper(String tableName, String pk_corp,
            String key, List<BD_Realtion> relationList) throws DZFWarpException{
        // 没有引用该表的情况：
        if (relationList.size() == 0)
            return ;
        for (BD_Realtion relation : relationList) {
            String checkSqlWithoutDr = getSqlQeury(key, relation);
            String checkSqlWithDr = getSqlQeuryWithDr(key, relation);
            // 先不包含dr进行查询，尽量利用上索引，如果被引用了，则加上dr条件确认一下。
            boolean referenced = checkReferenceBySql(tableName, pk_corp, relation, key,
                    checkSqlWithoutDr)
                    && checkReferenceBySql(tableName, pk_corp, relation, key,
                            checkSqlWithDr);
            if (referenced) {
                if(!StringUtil.isEmpty(relation.errorMsg)){
                    throw new BusinessException(relation.errorMsg);
                }else{
                    throw new BusinessException("数据已被引用，不允许删除！");
                }
            }
        }
        return ;
    }
}