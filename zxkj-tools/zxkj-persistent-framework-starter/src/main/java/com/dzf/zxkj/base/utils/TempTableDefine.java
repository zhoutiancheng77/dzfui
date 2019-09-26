package com.dzf.zxkj.base.utils;

import com.dzf.zxkj.common.utils.JavaType;

import java.util.ArrayList;
import java.util.List;

/**
 * 业务框架默认的ID临时表定义
 * <p>
 * 将临时表的定义完全放于此处（每一个临时表的定义应该都是唯一的）。同时，当不同数量的ID被插入到临时表时，通过阈值
 * 选择不同的临时表名。这样可以有效避免因为数据量的不同导致执行计划的错误。否则，lky会要求你在查询语句中写hint
 * 来强制标明具体的ID数量（这样可以让数据库在执行sql的时候会重新解析执行计划）
 * <ol>
 * <li>一列ID的临时表</li>
 * <li>两列ID的临时表</li>
 * <li>三列ID的临时表</li>
 * </ol>
 */
public class TempTableDefine {
	/**
	 * 阈值为1万
	 */
	private static final int FOUR_THRESHOLD = 10000;

	/**
	 * 阈值为五百
	 */
	private static final int ONE_THRESHOLD = 500;

	private static final String TEMP_TABLE_PREFIX = "TEMP_PUBAPP_";

	/**
	 * 阈值为5千
	 */
	private static final int TRHREE_THRESHOLD = 5000;

	/**
	 * 阈值为1千
	 */
	private static final int TWO_THRESHOLD = 1000;

	private String tempTablePrefix = TempTableDefine.TEMP_TABLE_PREFIX;

	/**
	 * 包含一列ID字段的临时表，传入的ID的值不能重复，也不能为空。
	 * 
	 * @param ids
	 *            主键数组
	 * @return 一列主键临时表
	 */
	public String get(String[] ids, String corppk, String userpk) {
		String tableName = null;
		int length = ids.length;
		if (length <= TempTableDefine.ONE_THRESHOLD) {
			tableName = this.get(ids, this.getTempTablePrefix() + "5H1", corppk, userpk);
		} else if (length <= TempTableDefine.TWO_THRESHOLD) {
			tableName = this.get(ids, this.getTempTablePrefix() + "1T1", corppk, userpk);
		} else if (length <= TempTableDefine.TRHREE_THRESHOLD) {
			tableName = this.get(ids, this.getTempTablePrefix() + "5T1", corppk, userpk);
		} else if (length <= TempTableDefine.FOUR_THRESHOLD) {
			tableName = this.get(ids, this.getTempTablePrefix() + "10T1", corppk, userpk);
		} else {
			tableName = this.get(ids, this.getTempTablePrefix() + "L1", corppk, userpk);
		}

		return tableName;
	}

	/**
	 * 包含两列ID字段的临时表。两组主键的长度必须一致
	 * 
	 * @param id1
	 *            主键1数组
	 * @param id2
	 *            主键2数组
	 * @return 两列主键临时表
	 */
	public String get(String[] id1, String[] id2, String corppk, String userpk) {
		List<List<Object>> data = new ArrayList<List<Object>>();

		int length = id1.length;
		for (int i = 0; i < length; i++) {
			List<Object> row = new ArrayList<Object>();
			data.add(row);
			row.add(id1[i]);
			row.add(id2[i]);
		}
		String[] columns = { "id1 ", "id2" };
		String[] columnTypes = { "CHAR(20)", "CHAR(20)" };
		JavaType[] types = new JavaType[] { JavaType.String, JavaType.String };

		TempTableTool dao = new TempTableTool();
		String tableName = this.getTempTablePrefix() + "2ID";
		tableName = dao.getTempTable(tableName, corppk, userpk, columns, columnTypes, types, data);
		return tableName;
	}

	/**
	 * 包含三列ID字段的临时表。三组主键的长度必须一致
	 * 
	 * @param id1
	 *            主键1数组
	 * @param id2
	 *            主键2数组
	 * @param id3
	 *            主键3数组
	 * @return 三列主键临时表
	 */
	public String get(String[] id1, String[] id2, String[] id3, String corppk, String userpk) {
		List<List<Object>> data = new ArrayList<List<Object>>();

		int length = id1.length;
		for (int i = 0; i < length; i++) {
			List<Object> row = new ArrayList<Object>();
			data.add(row);
			row.add(id1[i]);
			row.add(id2[i]);
			row.add(id3[i]);
		}
		String[] columns = { "id1 ", "id2", "id3" };
		String[] columnTypes = { "CHAR(20)", "CHAR(20)", "CHAR(20)" };
		JavaType[] types = new JavaType[] { JavaType.String, JavaType.String, JavaType.String };

		TempTableTool dao = new TempTableTool();
		String tableName = this.getTempTablePrefix() + "3ID";
		tableName = dao.getTempTable(tableName, corppk, userpk, columns, columnTypes, types, data);
		return tableName;
	}

	/**
	 * 包含一列ID字段的临时表。在我们的程序中，常常会出现要查询两组ID。此时，只有 一个临时表是不够用的。
	 * 这里再默认一个新的临时表，以便业务代码使用。传入的ID的值不能重复，也不能为空
	 * 
	 * @param ids
	 *            主键数组
	 * @return 一列ID字段的临时表
	 */
	public String getAnother(String[] ids, String corppk, String userpk) {
		String tableName = null;
		int length = ids.length;
		if (length <= TempTableDefine.ONE_THRESHOLD) {
			tableName = this.get(ids, this.getTempTablePrefix() + "5H1A", corppk, userpk);
		} else if (length <= TempTableDefine.TWO_THRESHOLD) {
			tableName = this.get(ids, this.getTempTablePrefix() + "1T1A", corppk, userpk);
		} else if (length <= TempTableDefine.TRHREE_THRESHOLD) {
			tableName = this.get(ids, this.getTempTablePrefix() + "5T1A", corppk, userpk);
		} else if (length <= TempTableDefine.FOUR_THRESHOLD) {
			tableName = this.get(ids, this.getTempTablePrefix() + "10T1A", corppk, userpk);
		} else {
			tableName = this.get(ids, this.getTempTablePrefix() + "L1A", corppk, userpk);
		}

		return tableName;
	}

	public void setTempTablePrefix(String tempTablePrefix) {
		this.tempTablePrefix = tempTablePrefix;
	}

	private String get(String[] ids, String tableName, String corppk, String userpk) {
		List<List<Object>> data = new ArrayList<List<Object>>();

		int length = ids.length;
		for (int i = 0; i < length; i++) {
			List<Object> row = new ArrayList<Object>();
			data.add(row);
			row.add(ids[i]);
		}
		String[] columns = { "id1" };
		String[] columnTypes = { "CHAR(20)" };
		JavaType[] types = new JavaType[] { JavaType.String };

		TempTableTool dao = new TempTableTool();
		String name = dao.getTempTable(tableName, corppk, userpk, columns, columnTypes, columns, types, data);
		return name;
	}

	private String getTempTablePrefix() {
		return this.tempTablePrefix;
	}

}
