package com.dzf.zxkj.platform.model.st;

public interface ReportCallInfo {
	
	public  String[] getConstantFieldNames() ;	
	
	public String[][] getConstantFieldValues() ;
	
	public String[] getVariableFiledNames();
	
	public String[][] getCellformulas(String accountStandard);
	
	public String[] getRowformulas();
	
	public String[] getColformulas();
	
}
