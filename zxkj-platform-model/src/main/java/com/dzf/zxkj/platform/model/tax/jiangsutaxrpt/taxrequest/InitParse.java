package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest;

import java.util.Map;

/**
 * 江苏初始化解析
 * 
 * @author lbj
 *
 */
public interface InitParse {

	/**
	 * 获取系统字段与接口字段映射
	 * 
	 * @param ewblxh
	 * @return <系统字段,接口字段>
	 */
	public Map<String, String> getNameMap(String ewblxh);

	/**
	 * 数组类型的索引字段
	 * 
	 * @param vname
	 * @return
	 */
	public String getIndexField(String vname);

	/**
	 * 要解析的字段
	 * 
	 * @return
	 */
	public String[] getFields();

	/**
	 * 获取字段类型
	 * 
	 * @return
	 */
	public Class<?> getFieldType(String fieldName);
}
