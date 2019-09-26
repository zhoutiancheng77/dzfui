package com.dzf.zxkj.base.utils;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.List;

/**
 * 此处插入类型说明。 创建日期：(2004-2-23 13:49:03)
 * 
 */
@Slf4j
public class TempTable {

	private SingleObjectBO singleObjectBO = null;

	public String createTempTable(String tableName, String corp, String userpk, String columns, String... idx)
			throws SQLException {
		if (tableName == null || columns == null || getSingleObjectBO() == null)
			return null;
		String m_tabname = null;
		// 获得数据库类型
		OracleTempTableCreator creator = new OracleTempTableCreator();
		m_tabname = creator.createTempTable(getSingleObjectBO(), tableName, corp, userpk, columns, idx);
		return m_tabname;
	}

	public void dropTempTable(String TabName) throws SQLException {
		try {
			// 获得数据库类型
			// 取得数据库的临时表
			if (TabName == null || TabName.length() == 0) {
				List<String> list = (List<String>) getSingleObjectBO().executeQuery(
						"select TABLE_NAME from user_tables where TEMPORARY='Y'", null, new ColumnListProcessor());

				if (DZFValueCheck.isNotEmpty(list)) {
					for (String tablename : list) {
						String sql = "drop table " + tablename;
						getSingleObjectBO().executeUpdate(sql, null);
					}
				}

			} else {
				String sql = "drop table " + TabName.trim();
				getSingleObjectBO().executeUpdate(sql, null);
			}
		} catch (Exception e) {
			log.error("删除临时表异常!", e);
		} finally {
		}
	}

	public void dropUserTables(String userPk) throws SQLException {
		try {
			// 获得数据库类型
			// 取得数据库的临时表
			userPk = userPk.toUpperCase();
			List<String> list = (List<String>) getSingleObjectBO().executeQuery(
					"select TABLE_NAME from user_tables where TEMPORARY='Y' and TABLE_NAME like '%" + userPk + "%'",
					null, new ColumnListProcessor());

			if (DZFValueCheck.isNotEmpty(list)) {
				for (String tablename : list) {
					String sql = "drop table " + tablename;
					getSingleObjectBO().executeUpdate(sql, null);
				}
			}
		} catch (Exception e) {
			log.error("删除临时表异常!", e);
		} finally {
		}
	}

	private SingleObjectBO getSingleObjectBO() {
		if (this.singleObjectBO == null) {
			this.singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
		}
		return this.singleObjectBO;
	}
}