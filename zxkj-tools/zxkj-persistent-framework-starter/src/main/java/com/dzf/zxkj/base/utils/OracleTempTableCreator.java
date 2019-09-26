package com.dzf.zxkj.base.utils;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.Random;

/**
 * @nopublish
 * 
 */
@Slf4j
public class OracleTempTableCreator {

	public OracleTempTableCreator() {
		super();

	}

	public String createTempTable(SingleObjectBO singleObjectBO, String TabName, String corp, String userpk, String TabColumn, String... idx)
			throws SQLException {
		String sql;

		TabName =getTableName(singleObjectBO, TabName, corp, userpk);
		try {
			if (!isTableExist(singleObjectBO, TabName)) {
				synchronized (TabName.intern()) {
					if (!isTableExist(singleObjectBO, TabName)) {
						// 建临时表的SQL
						TabColumn = transDataType(TabColumn);
						sql = "create GLOBAL TEMPORARY table " + TabName + "(" + TabColumn
								+ ") ON COMMIT PRESERVE ROWS ";
						log.debug("HH First: " + sql);
						singleObjectBO.executeUpdate(sql, null);
						if (idx != null && idx.length > 0) {
							for (int i = 0; i < idx.length; i++) {
								String IndColumn = idx[i];
								if (IndColumn != null && IndColumn.trim().length() != 0) {
									sql = "create index i_" + TabName + "_" + i + " on " + TabName + "(" + IndColumn
											+ ")";
									singleObjectBO.executeUpdate(sql, null);
								}
							}
						}
					}
				}
			}
			return TabName;

			// existsTable.add(TabName);
		} catch (Exception e) {
			if (isTableExist(singleObjectBO, TabName)) {
				// existsTable.add(TabName);
				return TabName;
			} else {
				log.error("HH First: create temporaty table error: " + TabName, e);
				if (e instanceof SQLException) {
					throw (SQLException) e;
				} else {
					throw new SQLException(e);
				}
			}
		} finally {
		}
		// return TabName;
	}

	private String transDataType(String columns) {
		StringBuffer colStrs = new StringBuffer();
		for (String colStr : columns.split("(\\s)+")) {
			if (colStr.toUpperCase().startsWith("DECIMAL(")) {
				colStr = colStr.toUpperCase().replace("DECIMAL(", "NUMBER(");
			} else if (colStr.toUpperCase().startsWith("NUMERIC(")) {
				colStr = colStr.toUpperCase().replace("NUMERIC(", "NUMBER(");
			}
			colStrs.append(colStr).append(" ");
		}
		colStrs.setLength(colStrs.length() - 1);
		return colStrs.toString();
	}

	private String getTableName(SingleObjectBO singleObjectBO,String tableName,String corp,String userpk) throws SQLException {
		int tryCount = 0;
		while (true) {
			tryCount++;
			tableName ="DZF_TEMP_"+tableName+ corp+userpk+ new Random().nextInt(1024);
			if (!isTableExist(singleObjectBO,tableName)) {
				return tableName;
			}
		}
	}
	
	private boolean isTableExist(SingleObjectBO singleObjectBO, String table) {
		try {
			singleObjectBO.executeUpdate("delete from " + table, null);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
