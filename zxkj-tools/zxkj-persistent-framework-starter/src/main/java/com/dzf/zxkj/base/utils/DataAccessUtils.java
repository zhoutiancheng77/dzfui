package com.dzf.zxkj.base.utils;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据访问工具类。将数据库访问操作封装起来
 * 
 */
public class DataAccessUtils {

	private SingleObjectBO singleObjectBO = null;

	/**
	 * 数据访问工具类默认构造函数
	 */
	public DataAccessUtils() {
		// 默认构造函数
	}

	/**
	 * 更新数据库
	 * 
	 * @param sql
	 *            更新数据库sql
	 * @return 更新的纪录数
	 */
	public int update(String sql) {
		int result = -1;
		try {
			result = getSingleObjectBO().executeUpdate(sql, null);
		} catch (Exception ex) {
			ExceptionUtils.wrappException(ex);
		} finally {
		}
		return result;
	}

	/**
	 * 用参数sql语句更新数据库
	 * 
	 * @param sql
	 *            参数化的更新sql语句
	 * @param types
	 *            参数类型
	 * @param data
	 *            参数值列表
	 */
	public void updateList(String sql, List<SQLParameter> list) {
		try {
			getSingleObjectBO().executeBatchUpdate(sql, list.toArray(new SQLParameter[list.size()]));

		} catch (Exception ex) {
			ExceptionUtils.wrappException(ex);
		} finally {
		}
	}

	/**
	 * 用参数sql语句更新数据库
	 * 
	 * @param sql
	 *            参数化的更新sql语句
	 * @param types
	 *            参数类型
	 * @param data
	 *            参数值列表
	 */
	public void updateListObject(String sql, List<List<Object>> data) {
		try {
			List<SQLParameter> list = new ArrayList<>();
			for (List<Object> object : data) {
				SQLParameter sp = new SQLParameter();
				for (Object o : object) {
					sp.addParam(o);
				}
				list.add(sp);
			}
			getSingleObjectBO().executeBatchUpdate(sql, list.toArray(new SQLParameter[list.size()]));

		} catch (Exception ex) {
			ExceptionUtils.wrappException(ex);
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
