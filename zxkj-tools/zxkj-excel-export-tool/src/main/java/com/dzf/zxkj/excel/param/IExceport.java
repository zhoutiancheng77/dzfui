package com.dzf.zxkj.excel.param;

/**
 * 暂不支持分多sheet导出，接口定义
 *
 */
public interface IExceport<T> {
	
	/**
	 * 导出excel文件的2007名称
	 */
	public String getExcelport2007Name();
	
	/**
	 * 导出excel文件的2003名称
	 */
	public String getExcelport2003Name();
	
	/**
	 * 导出excel--head名称,即标题栏名称
	 */
	public String getExceportHeadName();
	
	/**
	 * 导出excel的sheet的名称
	 */
	public String getSheetName();
	
	/**
	 * 导出excel数据
	 *  ArrayList内部是拿数组存储，那么上限就是Integer.MAX_VALUE
		LinkedList内部是个链表，理论上是无限的
	 */
	public T[] getData();
	
	/**
	 * excel字段信息
	 */
	public Fieldelement[] getFieldInfo();
	/**
	 * 导出excel期间
	 */
	public String getQj();
	
	/**
	 * 制表时间
	 */
	public String getCreateSheetDate();
	
	/**
	 * 制表人
	 */
	public String getCreateor();
	
	/**
	 * 当前操作公司
	 */
	public String getCorpName();
	
	/**
	 * 说明：：长度为 3
	 * 1、公司是否显示
	 * 2、期间是否显示
	 * 3、单位：元是否显示
	 * 
	 */
	public boolean[] isShowTitDetail();
}
