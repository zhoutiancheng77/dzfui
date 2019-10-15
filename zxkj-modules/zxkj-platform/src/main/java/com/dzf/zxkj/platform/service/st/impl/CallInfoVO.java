package com.dzf.zxkj.platform.service.st.impl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.enums.CellTypeEnum;
import com.dzf.zxkj.base.query.QueryParamVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/****
 * 
 * 
 * 一个页签（一张表）对应的信息   allReportvos 除外
 * @author asoka
 *
 */
public class CallInfoVO {

	
   //唯一标示	
   private String tabCode;
   //是否使用用户数据计算
   private boolean isUseUserDataCalcute = false;
   
   private QueryParamVO queryParamVO;
   
   private  Map<String,Object> otherParam;
   
   @SuppressWarnings("rawtypes")
   private Class clazz;
   
   //借贷方向
   private String debitCreditDriection;
   
   //AccountEnum
   private String accountStandard = "2013";
   
   //常量字段名称 constantFieldNames=new String[]{"id","name"}
   private String[] constantFieldNames;
   
   /****
    * 常量字段值和常量字段名称顺序对应  constantFieldValues = new String[][]
    * 如：constantFieldValues = new String[][]{
    * 								          {"1","zhangsan"},//第一行id和name字段的值	
    * 									      {"2","lisi"},//第 二行id和name字段的值
    *                                          ........   //第 n行id和name字段的值		
    * 										}											
    */
   
   private String[][] constantFieldValues;
   
    //变量字段名
    private String[] variableFiledNames;
  
    //单元格公式
    private String[][] cellformulas;
    //行公式
    private String[] rowformulas;
    //列公式
    private String[] colformulas;
    
    //一个节点的页签和class对照 
    @SuppressWarnings("rawtypes")
	private Map<String,Class> tabCodeClassMap;
    
    @SuppressWarnings("rawtypes")
	private Map<String, SuperVO[]> allReportvos = new ConcurrentHashMap<String,SuperVO[]>();
	
	
	/****
	 * 获得当前报表vo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public SuperVO[] getCurrentReportVOs(){
		
		if(allReportvos!=null&&allReportvos.containsKey(tabCode)){
			
			return allReportvos.get(tabCode);
		}
		
		return null;
	}
		
    
    @SuppressWarnings("rawtypes")
	public Map<String, Class> getTabCodeClassMap() {
		return tabCodeClassMap;
	}

    
	@SuppressWarnings("rawtypes")
	public void setTabCodeClassMap(Map<String, Class> tabCodeClassMap) {
		this.tabCodeClassMap = tabCodeClassMap;
	}



	/****
     * 获得无关项的单元格信息
     * @return
     */
    public String[] getSTAcells(){

    	return getContainField(true,new String[]{CellTypeEnum.STA.getCode()});
    }
    
    /****
     * 获取不可编辑单元格信息
     * @return
     */
    public String[] getNotEditcells(){

    	return getContainField(false,new String[]{CellTypeEnum.NA.getCode()});
    }
    
    
    /****
     * 获取可编辑单元格信息
     * @return
     */
    public String[] getEditcells(){

    	return getContainField(true,new String[]{CellTypeEnum.NA.getCode()});
    }
   
    
    /***
     * 获取满足条件的单元格信息 如  1#fieldname    表示fieldname列的第1行所在的单元格
     * 
     * @param  isContains  是否为包含
     * @param containstrs  满足单元格包含公式的集合
     * @return
     */
    public String[] getContainField(boolean isContains,String[] containstrs){
    	
    	if(variableFiledNames!=null&&variableFiledNames.length>0
    			&&cellformulas!=null&&cellformulas.length>0){
    		
        	//格式  行号（从1开始）+ "#" + 列明
        	List<String> stafields = new ArrayList<String>();
    		for(int i=0;i<variableFiledNames.length;i++){
		    	String[] cols = cellformulas[i];//cols为当前循环列所有行的公式
		    //	System.out.println(Arrays.toString(cols));
				for(int row=0;row<cols.length;row++){
					for(String con : containstrs){
						if(isContains){
							if(cols[row].indexOf(con)>=0){
								stafields.add((row+1)+"#"+variableFiledNames[i]);
								break;
							}
						}else{
							if(cols[row].indexOf(con)<0){
								stafields.add((row+1)+"#"+variableFiledNames[i]);
								break;
							}
						}

					}
				}
    		}   
    		
    		return stafields.toArray(new String[0]);
    	}
    	
    	return null;
    }
    
    
	public boolean isUseUserDataCalcute() {
		return isUseUserDataCalcute;
	}

	public void setUseUserDataCalcute(boolean isUseUserDataCalcute) {
		this.isUseUserDataCalcute = isUseUserDataCalcute;
	}

	public String getDebitCreditDriection() {
		return debitCreditDriection;
	}

	public void setDebitCreditDriection(String debitCreditDriection) {
		this.debitCreditDriection = debitCreditDriection;
	}

	public String getAccountStandard() {
		return accountStandard;
	}

	public void setAccountStandard(String accountStandard) {
		this.accountStandard = accountStandard;
	}

	public String getTabCode() {
		return tabCode;
	}

	public void setTabCode(String tabCode) {
		this.tabCode = tabCode;
	}

	public QueryParamVO getQueryParamVO() {
		return queryParamVO;
	}

	public void setQueryParamVO(QueryParamVO queryParamVO) {
		this.queryParamVO = queryParamVO;
	}

	public Map<String, Object> getOtherParam() {
		return otherParam;
	}

	public void setOtherParam(Map<String, Object> otherParam) {
		this.otherParam = otherParam;
	}

	@SuppressWarnings("rawtypes")
	public Class getClazz() {
		return clazz;
	}
	@SuppressWarnings("rawtypes")
	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}
	public String[] getConstantFieldNames() {
		return constantFieldNames;
	}
	public void setConstantFieldNames(String[] constantFieldNames) {
		this.constantFieldNames = constantFieldNames;
	}
	public String[][] getConstantFieldValues() {
		return constantFieldValues;
	}
	

	public String[] getVariableFiledNames() {
		return variableFiledNames;
	}
	public void setVariableFiledNames(String[] variableFiledNames) {
		this.variableFiledNames = variableFiledNames;
	}
	public void setConstantFieldValues(String[][] constantFieldValues) {
		this.constantFieldValues = constantFieldValues;
	}

	public String[][] getCellformulas() {
		return cellformulas;
	}
	public void setCellformulas(String[][] cellformulas) {
		this.cellformulas = cellformulas;
	}
	public String[] getRowformulas() {
		return rowformulas;
	}
	public void setRowformulas(String[] rowformulas) {
		this.rowformulas = rowformulas;
	}
	public String[] getColformulas() {
		return colformulas;
	}
	public void setColformulas(String[] colformulas) {
		this.colformulas = colformulas;
	}
	
	@SuppressWarnings("rawtypes")
	public Map<String, SuperVO[]> getAllReportvos() {
		return allReportvos;
	}

	@SuppressWarnings("rawtypes")
	public void setAllReportvos(Map<String, SuperVO[]> allReportvos) {
		this.allReportvos = allReportvos;
	}

	@Override
	public String toString() {
		
	    StringBuffer sb = new StringBuffer();
	    sb.append("tabCode:");
	    if(tabCode!=null){
	    	  sb.append(tabCode);
	    }
	    sb.append("\n");
	    
	    sb.append("clazz:");
	    if(clazz!=null){
	    	  sb.append(clazz.getName());
	    }
	    sb.append("\n");
	    
	    sb.append("constantFieldNames:");
	    if(constantFieldNames!=null&&constantFieldNames.length>0){
	    	  sb.append(Arrays.toString(constantFieldNames));
	    }
	    sb.append("\n");
	    
	    sb.append("constantFieldValues:");
	    if(constantFieldValues!=null&&constantFieldValues.length>0){
	    	  for(String[] ss : constantFieldValues){
		    	  sb.append(Arrays.toString(ss)).append(",");
	    	  }

	    }
	    sb.append("\n");
	    
	    sb.append("cellformulas:");
	    if(cellformulas!=null&&cellformulas.length>0){
	    	  for(String[] ss : cellformulas){
		    	  sb.append(Arrays.toString(ss)).append(",");
	    	  }

	    }
	    sb.append("\n");
	    
	    sb.append("rowformulas:");
	    if(rowformulas!=null&&rowformulas.length>0){
	    	  sb.append(Arrays.toString(rowformulas));
	    }
	    sb.append("\n");
	    	    
	    sb.append("colformulas:");
	    if(colformulas!=null&&colformulas.length>0){
	    	  sb.append(Arrays.toString(colformulas));
	    }
	    sb.append("\n");
	    
	    return sb.toString();    
	    
	}

}
