package com.dzf.zxkj.common.model;

public interface IExAggVO {
	public abstract CircularlyAccessibleValueObject[] getAllChildrenVO();

	/**
	 * 此处插入方法说明。 创建日期：(01-3-20 17:36:56)
	 * 
	 * @return nc.vo.pub.ValueObject[]
	 */
	public abstract CircularlyAccessibleValueObject[] getChildrenVO();

//	public SuperVO[] getChildVOsByParentId(String tableCode, String parentid);

	/**
	 * 缺省的TableCode， 如 return getTableCodes[0];
	 */
	// public String getDefaultTableCode();
	// public String getParentId(SuperVO item);
	/**
	 * 此处插入方法说明。 创建日期：(01-3-20 17:32:28)
	 * 
	 * @return nc.vo.pub.ValueObject
	 */
	public abstract CircularlyAccessibleValueObject getParentVO();

	/**
	 * 返回各个子表的编码。 创建日期：(01-3-20 17:36:56)
	 * 
	 * @return String[]
	 */
	public abstract String[] getTableCodes();

	/**
	 * 返回各个子表的中文名称。 创建日期：(01-3-20 17:36:56)
	 * 
	 * @return String[]
	 */
	public abstract String[] getTableNames();

	/**
	 * 返回某个子表的VO数组。 创建日期：(01-3-20 17:36:56)
	 * 
	 * @return nc.vo.pub.ValueObject[]
	 * @param tableCode
	 *            String 子表的编码
	 */
	public abstract CircularlyAccessibleValueObject[] getTableVO(
            String tableCode);

	// public void setParentId(SuperVO item,String id);
	/**
	 * 母表VO的setter方法。 创建日期：(01-3-20 17:32:28)
	 *
	 * @param parent
	 *            nc.vo.pub.ValueObject 母表VO
	 */
	public abstract void setParentVO(CircularlyAccessibleValueObject parent);

	/**
	 * 某个子表VO数组的setter方法。 创建日期：(01-3-20 17:36:56)
	 *
	 * @param tableCode
	 *            String 子表编码
	 * @param values
	 *            nc.vo.pub.ValueObject[] 子表VO数组
	 */
	public abstract void setTableVO(String tableCode,
                                    CircularlyAccessibleValueObject[] values);
}
