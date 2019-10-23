package com.dzf.zxkj.excel.param;

import java.util.List;

/**
 * excel多页签导出
 * 要求：每个页签的内容一致。
 *
 */
public abstract class MuiltSheetExceport<T> implements IExceport<T>{


	final public boolean isMuiltSheet(){
		return true;
	}
	
	public abstract String[] getAllSheetName();
	
	public abstract List<T[]> getAllSheetData();
	
	//获取期间---假如报表每个页签要求展示期间，并且期间并不相同。
	public abstract String[] getAllPeriod();
	
}
