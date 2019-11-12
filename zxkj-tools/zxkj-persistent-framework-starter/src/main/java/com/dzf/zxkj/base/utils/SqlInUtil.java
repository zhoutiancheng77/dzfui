package com.dzf.zxkj.base.utils;

import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.exception.BusinessException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 创建inSQl.
 * <p>
 * 1.根据传入的值返回inSQL.
 * <p>
 * 2.阈值200,id在200以内直接返回in(id1,id2,....)如果超过200则以临时表形式返回SQL.
 * <p>
 * 3.如果在需要在同一事务中多次使用需要提供自己的临时表名，后续会做相关支持.
 * 
 * @since 6.5
 * @version 2012-8-2 下午03:51:04
 * @author zhaoshb
 */
public class SqlInUtil {

	private final int MAX_COUNT = 200;

	private final String ID_LENGTH = "200";

	private final String TEMP_NAME = "general_in";

	private String[] idArr = null;

	private Collection<String> idCollection;

	public SqlInUtil(String[] ids) {
		this.idArr = ids;
	}

	public SqlInUtil(Collection<String> ids) {
		this.idCollection = ids;
	}

	public String getInSql(String corp, String userpk) throws BusinessException {
		return this.getInSql(this.TEMP_NAME, corp, userpk);
	}

	public String getInSql4Collection(String corp, String userpk) throws BusinessException {
		return this.getInSql4Collection(this.TEMP_NAME, corp, userpk);
	}

	public String getInSql(String tempTableName, String corp, String userpk) throws BusinessException {
		if (this.idArr == null || this.idArr.length == 0) {
			return null;
		}
		if (this.idArr.length < this.MAX_COUNT) {
			return this.getCommonInSql();
		}
		return this.getTempTableInSql(tempTableName, corp, userpk);
	}

	public String getInSql4Collection(String tempTableName, String corp, String userpk) throws BusinessException {
		if (this.idCollection == null || this.idCollection.isEmpty()) {
			return null;
		}
		if (this.idCollection.size() < this.MAX_COUNT) {
			return this.getCommonInSql4Collection();
		}
		return this.getTempTableInSql4Collection(tempTableName, corp, userpk);
	}

	private String getCommonInSql() {
		StringBuilder inSql = new StringBuilder();
		inSql.append(" in ( ");
		for (String idTemp : this.getIds()) {
			inSql.append(" '");
			inSql.append(idTemp);
			inSql.append("'  , ");
		}
		String subString = inSql.substring(0, inSql.length() - 3);
		String finalInSql = subString + " ) ";
		return finalInSql;
	}

	private String getCommonInSql4Collection() {
		StringBuilder inSql = new StringBuilder();
		inSql.append(" in ( ");
		for (String idTemp : this.idCollection) {
			inSql.append(" '");
			inSql.append(idTemp);
			inSql.append("'  , ");
		}
		String subString = inSql.substring(0, inSql.length() - 3);
		String finalInSql = subString + " ) ";
		return finalInSql;
	}

	private String getTempTableInSql(String tableName, String corp, String userpk) throws BusinessException {
		String rtnTableName = this.createTempTable(tableName, corp, userpk);
		this.insertData(rtnTableName);
		StringBuilder inSql = new StringBuilder();
		inSql.append(" in ( select id from ");
		// inSql.append(tableName);
		inSql.append(rtnTableName);// 临时表名取新表名
		inSql.append(" ) ");
		return inSql.toString();
	}

	private String getTempTableInSql4Collection(String tableName, String corp, String userpk) throws BusinessException {
		String rtnTableName = this.createTempTable(tableName, corp, userpk);
		this.insertData4Collection(rtnTableName);
		StringBuilder inSql = new StringBuilder();
		inSql.append(" in ( select id from ");
		inSql.append(tableName);
		inSql.append(" ) ");
		return inSql.toString();
	}

	private String createTempTable(String tableName, String corp, String userpk) {
		TempTableTool tempTableUtil = new TempTableTool();
		String[] columns = this.getColumns();
		String rtnTableName = tempTableUtil.getTempTable(tableName, corp, userpk, columns, this.getColuntTypes());
		return rtnTableName;
	}

	private void insertData(String tableName) throws BusinessException {
		String insertSql = this.getInsertSql(tableName);
		List<SQLParameter> paraList = this.getParaList();
		DataAccessUtils dao = new DataAccessUtils();
		dao.updateList(insertSql.toString(), paraList);
	}

	private void insertData4Collection(String tableName) throws BusinessException {
		String insertSql = this.getInsertSql(tableName);
		List<SQLParameter> paraList = this.getCollectionParaList();
		DataAccessUtils dao = new DataAccessUtils();
		dao.updateList(insertSql.toString(), paraList);
	}

	private String getInsertSql(String tableName) {
		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" insert into ");
		insertSql.append(tableName);
		insertSql.append("( id ) values( ? )");
		return insertSql.toString();
	}

	private List<SQLParameter> getParaList() {
		List<SQLParameter> paraList = new ArrayList<SQLParameter>();
		for (String idTemp : this.getIds()) {
			SQLParameter para = new SQLParameter();
			para.addParam(idTemp);
			paraList.add(para);
		}
		return paraList;
	}

	private List<SQLParameter> getCollectionParaList() {
		List<SQLParameter> paraList = new ArrayList<SQLParameter>();
		for (String idTemp : this.idCollection) {
			SQLParameter para = new SQLParameter();
			para.addParam(idTemp);
			paraList.add(para);
		}
		return paraList;
	}

	private String[] getColumns() {
		return new String[] { "id" };
	}

	private String[] getColuntTypes() {
		return new String[] { "VARCHAR(" + this.ID_LENGTH + ")" };
	}

	public void setIds(String[] ids) {
		this.idArr = ids;
	}

	public String[] getIds() {
		return this.idArr;
	}

}
