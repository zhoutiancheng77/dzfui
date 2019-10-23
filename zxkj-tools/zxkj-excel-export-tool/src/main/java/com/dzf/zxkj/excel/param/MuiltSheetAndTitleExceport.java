package com.dzf.zxkj.excel.param;


import java.util.List;

public abstract class MuiltSheetAndTitleExceport<T> extends MuiltSheetExceport<T> {

	public abstract TitleColumnExcelport getTitleColumns();
	
	public abstract List<TitleColumnExcelport> getHeadColumns();

}
