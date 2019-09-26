package com.dzf.zxkj.base.utils;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.tool.SqlBuilder;
import com.dzf.zxkj.common.utils.CompareEnum;
import com.dzf.zxkj.common.utils.SqlUtil;

import java.util.Collection;
import java.util.List;

/**
 * Sql构建类
 * 
 */
public class TempSqlBuilder extends SqlBuilder {

	/**
	 * in的最大长度
	 */
	public final int GROUP_COUNT = 200;

	/**
	 * 构造String数据=的条件，为空时查询的表数据为'~'
	 * 
	 * @param name
	 * @param value
	 */
	public void appendEx(String name, String value) {
		this.append(name);
		if (value != null) {
			this.append("='");
			this.append(value);
			this.append("' ");
		} else {
			this.append("='~' ");
		}
	}

	public void appendFieldEqual(String field1, String field2) {
		this.append(field1);
		this.append(" = ");
		this.append(field2);
	}

	public void appendEqual() {
		this.append(" = ");
	}

	public void appendNotEqual(String name, String value) {
		this.append(name);
		this.append(" <> '");
		this.append(value);
		this.append("' ");
	}

	public void appendNotIn() {
		this.append(" not in ");
	}

	/**
	 * 对于String数组值构造in条件
	 * 
	 * @param name
	 *            sql字段名
	 * @param values
	 *            String数组值
	 */
	public void append(String name,String corp,String userpk, String[] values) {
		if (values == null) {
			return;
		}
		int length = values.length;
		if (length == 1) {
			this.append(name, values[0]);
		} else if (length > this.GROUP_COUNT) {
			SqlInUtil util = new SqlInUtil(values);
			try {
//				super.append(name);
//				SqlUtil.buildSqlForIn(name, values)
//				super.append(util.getInSql(corp,userpk));
				super.append(SqlUtil.buildSqlForIn(name, values));
			} catch (BusinessException e) {
				ExceptionUtils.wrappException(e);
			}
		} else {
			super.appendLess(name, values);
		}
	}

	/**
	 * 对于String数组值构造in条件
	 * 
	 * @param name
	 *            sql字段名
	 * @param values
	 *            String数组值
	 */
	public void append(String name,String corp,String userpk, Collection<String> idList) {
		if (idList == null) {
			return;
		}
		int length = idList.size();
		if (length == 1) {
			this.append(name, idList.iterator().next());
		} else if (length > this.GROUP_COUNT) {
			SqlInUtil util = new SqlInUtil(idList);
			try {
				super.append(name);
				super.append(util.getInSql4Collection(corp,userpk));
			} catch (BusinessException e) {
				ExceptionUtils.wrappException(e);
			}
		} else {
			super.append(name);
			super.append(" in (");

			for (String idTemp : idList) {
				super.append("'");
				super.append(idTemp);
				super.append("'");
				super.append(",");
			}

			super.deleteLastChar();
			super.append(") ");
		}
	}

	/**
	 * 采用临时表的方式，对于String数组值构造in条件 详细说明 无论多少个值，都使用临时表
	 * 
	 * @param name
	 *            sql字段名
	 * @param values
	 *            String数组值
	 */
	public void appendInTemp(String name,String corp,String userpk, String[] values) {
		if (values == null) {
			return;
		}
		SqlInUtil util = new SqlInUtil(values);
		try {
			this.appentIn(name, util.getInSql(corp,userpk));
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}
	}

	/**
	 * 此方法为字符串的连接.
	 * 
	 * @param name
	 *            字段名.
	 * @param inSql
	 *            in('','')语句.
	 */
	public void appentIn(String name, String inSql) {
		StringBuilder sql = new StringBuilder();
		sql.append(name);
		sql.append(inSql);
		super.append(sql.toString());
	}

	/**
	 * 对于String数组值构造in条件 ,同一SQL如果临时表相同会导致前一次的数据被删除
	 * 
	 * @param name字段名
	 * @param valuessqlString数组值
	 * @param tableName
	 *            指定临时表名
	 */
	public void append(String name, String[] values, String tableName,String corp,String userpk) {
		if (values == null) {
			return;
		}
		int length = values.length;
		if (length == 1) {
			this.append(name, values[0]);
		} else if (length > this.GROUP_COUNT) {
			try {
				SqlInUtil util = new SqlInUtil(values);
				super.append(name, util.getInSql(tableName,corp,userpk));
			} catch (BusinessException e) {
				ExceptionUtils.wrappException(e);
			}
		} else {
			super.appendLess(name, values);
		}
	}

	/**
	 * 对于String数组值构造in条件
	 * 
	 * @param name
	 * @param ids
	 */
	public void append(String name,String corp,String userpk, List<String> ids) {
		if (ids == null || ids.size() == 0) {
			return;
		}
		this.append(name,corp,userpk, ids.toArray(new String[ids.size()]));
	}

	/**
	 * 添加from连接字符串
	 * 
	 * @return
	 */
	public void from() {
		this.append(" from ");
	}

	/**
	 * 添加update连接字符串
	 */
	public void update() {
		this.append(" update ");
	}

	/**
	 * 添加set连接字符串
	 */
	public void set() {
		this.append(" set ");
	}

	/**
	 * 添加delete from字符串
	 */
	public void delete() {
		this.append(" delete from ");
	}

	/**
	 * 添加delete from字符串
	 */
	public void delete(String tableName) {
		this.append(" delete from " + tableName);
	}

	/**
	 * 添加去除重复语句
	 */
	public void distinct() {
		this.append(" distinct ");
	}

	/**
	 * 插入表
	 * 
	 * @param tableName
	 */
	public void insertinto(String tableName) {
		this.append(" insert into " + tableName);
	}

	/**
	 * from
	 * 
	 * @param table
	 *            表名
	 * @return 本身
	 */
	public void from(String table) {
		this.append(" from " + table);
	}

	/**
	 * dr
	 * 
	 * @return 本身
	 */
	public void appendDr() {
		this.append(" and nvl(dr,0) = 0 ");
	}

	public void appendDr(String defaultTableName) {
		this.connectNum(defaultTableName, "DR", Integer.valueOf(0));
	}

	/**
	 * And
	 * 
	 * @return 本身
	 */
	public void and() {
		this.append(" And ");
	}

	/**
	 * inner join
	 * 
	 * @return 本身
	 */
	public void innerjoin() {
		this.append(" inner join ");
	}

	/**
	 * not exists
	 */
	public void notExists() {
		this.append(" not exists ");
	}

	/**
	 * exists
	 */
	public void exists() {
		this.append(" exists ");
	}

	/**
	 * Inner join
	 * 
	 * @param table
	 *            表名
	 * @return 本身
	 */
	public void innerjoin(String tableName) {
		this.innerjoin();
		this.append(tableName);

	}

	/**
	 * on
	 * 
	 * @return 本身
	 */
	public void on() {
		this.append(" on ");
	}

	/**
	 * WHERE
	 * 
	 * @return 本身
	 */
	public void where() {
		this.append(" WHERE ");
	}

	/**
	 * On 连接
	 * 
	 * @param tableName1
	 *            tableName1
	 * @param filed1
	 *            tableName1的字段
	 * @param tableName2
	 *            tableName2
	 * @param filed2
	 *            tableName2的字段
	 * @return 本身
	 */
	public void on(String tableName1, String filed1, String tableName2, String filed2) {
		this.on();
		this.fieldEq(tableName1, filed1, tableName2, filed2);

	}

	public void on(String field1, String field2) {
		this.on();
		this.append(field1);
		this.append("=");
		this.append(field2);
	}

	private void fieldEq(String tableName1, String filed1, String tableName2, String filed2) {
		this.append(tableName1);
		this.append(".");
		this.append(filed1);
		this.append(" = ");
		this.append(tableName2);
		this.append(".");
		this.append(filed2);
	}

	/**
	 * Select
	 * 
	 * @return 本身
	 */
	public void select() {
		this.append(" Select ");
	}

	/**
	 * OR
	 * 
	 * @return 本身
	 */
	public void or() {
		this.append(" OR ");

	}

	/**
	 * 拼接Select字段
	 * 
	 * @param fields
	 *            将要选择的字段
	 * @return 本身
	 */
	public void select(String[] fields) {
		this.select();
		this.joint(null, fields);
	}

	/**
	 * 拼接字段
	 * 
	 * @param tableName
	 *            表名
	 * @param fields
	 *            将要选择的字段
	 * @return 本身
	 */
	public void joint(String tableName, String[] fields) {
		this.append(" ");
		for (int i = 0; i < fields.length; i++) {
			if (DZFStringUtil.isNotEmpty(tableName)) {
				this.append(tableName);
				this.append(".");
			}
			this.append(fields[i]);
			if (i != fields.length - 1) {
				this.append(",");
			}
		}
		this.append(" ");
	}

	/**
	 * 拼接Select字段
	 * 
	 * @param fields
	 *            将要选择的字段
	 * @param tableName
	 *            表名
	 * @return 本身
	 */
	public void select(String tableName, String[] fields) {
		this.append(" ");
		for (int i = 0; i < fields.length; i++) {
			if (DZFStringUtil.isNotEmpty(tableName)) {
				this.append(tableName);
				this.append(".");
			}
			this.append(fields[i]);
			if (i != fields.length - 1) {
				this.append(",");
			}
		}

		this.append(" ");
	}

	/**
	 * 连接一起
	 * 
	 * @param code
	 *            编码
	 * @param code2
	 * @param value
	 *            值
	 */
	private void connectNum(String tableName, String code, Number value) {
		this.connectNum(tableName, code, value, CompareEnum.EQ);
	}

	/**
	 * 连接一起
	 * 
	 * @param tableName
	 *            tableName
	 * @param code
	 *            编码
	 * @param value
	 *            值
	 * @param compare
	 *            比较符
	 */
	private void connectNum(String tableName, String code, Number value, CompareEnum compare) {
		this.append(" ");
		if (DZFStringUtil.isNotEmpty(tableName)) {
			this.append(tableName);
			this.append(".");
		}
		this.append(code);
		this.append(" ");
		this.append(compare.getSqlString());
		this.append(" ");
		this.append(value);
		this.append(" ");
	}

	/**
	 * Sum操作
	 * 
	 * @param tableName
	 *            表明
	 * @param filed
	 *            字段
	 * @param alias
	 *            别名
	 * @return 自身
	 */
	public void sum(String tableName, String filed, String alias) {
		this.append(" sum(");
		if (DZFStringUtil.isNotEmpty(tableName)) {
			this.append(tableName);
			this.append(".");
		}
		this.append(filed);
		this.append(") ");
		this.append(alias);

	}

	/**
	 * 左括号
	 * 
	 * @return
	 */
	public void l() {
		this.append("(");
	}

	/**
	 * 右括号
	 * 
	 * @return
	 */
	public void r() {
		this.append(")");

	}

	/**
	 * IN
	 * 
	 * @return
	 */
	public void in() {
		this.append(" IN ");
	}

	public void leftjoin() {
		this.append(" left join ");
	}

	public void orderBy() {
		this.append(" order by ");
	}

	/**
	 * Group By
	 * 
	 * @param tableName
	 *            表名
	 * @param codes
	 *            编码
	 * @return 自身
	 */
	public void groupBy(String tableName, String[] codes) {
		this.append(" Group by ");
		this.contactBy2(tableName, codes);

	}

	private void contactBy2(String tableName, String[] values) {
		for (int i = 0; i < values.length - 1; i++) {
			if (DZFStringUtil.isNotEmpty(tableName)) {
				this.append(tableName);
				this.append(".");
			}
			this.append(values[i]);
			this.append(",");
		}
		if (DZFStringUtil.isNotEmpty(tableName)) {
			this.append(tableName);
			this.append(".");
		}
		this.append(values[values.length - 1]);
	}

	/**
	 * 批量查询表名定义类
	 * 
	 * @since 6.0
	 * @author liubq
	 */
	public static class TempTableDefine {
		/**
		 * 阈值和对应表名
		 */
		private static final int L10000 = 10000;

		private static final String L10000_TALBE_NAME = "TEMP_MM_SQLBUILDERQUERY_10000";

		/**
		 * 阈值和对应表名
		 */
		private static final int L5000 = 5000;

		private static final String L5000_TALBE_NAME = "TEMP_MM_SQLBUILDERQUERY_5000";

		/**
		 * 阈值和对应表名
		 */
		private static final int L1000 = 1000;

		private static final String L1000_TALBE_NAME = "TEMP_MM_SQLBUILDERQUERY_1000";

		/**
		 * 阈值和对应表名
		 */
		private static final int L300 = 300;

		private static final String L300_TALBE_NAME = "TEMP_MM_SQLBUILDERQUERY_300";

		/**
		 * 对应表名
		 */
		private static final String LALL_TALBE_NAME = "TEMP_MM_SQLBUILDERQUERY_ALL";

		/**
		 * 包含一列ID字段的临时表，传入的ID的值不能重复，也不能为空。
		 * 
		 * @param ids
		 *            主键数组
		 * @return 一列主键临时表
		 */
		public static String get(int length) {
			String tableName = null;
			if (length <= TempTableDefine.L300) {
				tableName = TempTableDefine.L300_TALBE_NAME;
			} else if (length <= TempTableDefine.L1000) {
				tableName = TempTableDefine.L1000_TALBE_NAME;
			} else if (length <= TempTableDefine.L5000) {
				tableName = TempTableDefine.L5000_TALBE_NAME;
			} else if (length <= TempTableDefine.L10000) {
				tableName = TempTableDefine.L10000_TALBE_NAME;
			} else {
				tableName = TempTableDefine.LALL_TALBE_NAME;
			}
			return tableName;
		}
	}
}
