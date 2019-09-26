package com.dzf.zxkj.platform.model.st;

import com.dzf.zxkj.common.enums.AccountEnum;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 文化事业建设费申报表
 *
 */
public class CltcTaxTab1VO extends NtBaseVO implements ReportCallInfo{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String pk_cltc_tab1;
	
	/***
	 * 项目大类
	 */
	private String vprojectclass;
	
	/****
	 * 项目
	 */
	private String vprojectname2;
	
	/***
	 * 栏次
	 */
	private String itemno;
	
	/***
	 * 
	 * 本月数
	 */
	private DZFDouble mny1;//金额
	
	/***
	 *
	 * 本年累计
	 */
	private DZFDouble mny2;//金额
	
	
	private String rp_mny1;
	private String rp_mny2;
	
	
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;


	
	

	public String getRp_mny1() {
		return rp_mny1;
	}

	public void setRp_mny1(String rp_mny1) {
		this.rp_mny1 = rp_mny1;
	}

	public String getRp_mny2() {
		return rp_mny2;
	}

	public void setRp_mny2(String rp_mny2) {
		this.rp_mny2 = rp_mny2;
	}


	public String getVprojectclass() {
		return vprojectclass;
	}

	public void setVprojectclass(String vprojectclass) {
		this.vprojectclass = vprojectclass;
	}

	public String getItemno() {
		return itemno;
	}

	public void setItemno(String itemno) {
		this.itemno = itemno;
	}

	public String getVdef1() {
		return vdef1;
	}

	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}

	public String getVdef2() {
		return vdef2;
	}

	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}

	public String getVdef3() {
		return vdef3;
	}

	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}

	public String getVdef4() {
		return vdef4;
	}

	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}

	public String getVdef5() {
		return vdef5;
	}

	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}


	public String getPk_cltc_tab1() {
		return pk_cltc_tab1;
	}

	public void setPk_cltc_tab1(String pk_cltc_tab1) {
		this.pk_cltc_tab1 = pk_cltc_tab1;
	}

	public String getVprojectname2() {
		return vprojectname2;
	}

	public void setVprojectname2(String vprojectname2) {
		this.vprojectname2 = vprojectname2;
	}

	public DZFDouble getMny1() {
		return mny1;
	}

	public void setMny1(DZFDouble mny1) {
		this.mny1 = mny1;
	}

	public DZFDouble getMny2() {
		return mny2;
	}

	public void setMny2(DZFDouble mny2) {
		this.mny2 = mny2;
	}


	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_cltc_tab1";
	}

	@Override
	public String getTableName() {
	   return "ynt_cltc_tab1";
	}

	@Override
	public String[] getConstantFieldNames() {
		// TODO Auto-generated method stub
		return new String[]{"vprojectclass","vprojectname","vprojectname2","itemno"};
	}

	@Override
	public String[][] getConstantFieldValues() {
		
		String[] col0_const_fieldvalues = new String[]{"按适用费率征<br>收的计费收入","按适用费率征收的计费收入","费额计算","费额计算","费额计算","费额计算","费额计算","费额计算","费额计算","费额计算",
				"费额缴纳","费额缴纳","费额缴纳","费额缴纳","费额缴纳","费额缴纳","费额缴纳","费额缴纳","费额缴纳"};
		
		// 增 值 税 纳 税 申 报 表   项     目
		String[] col1_const_fieldvalues = new String[]{
				"",
				"",
				"扣除项目期初金额",
				"扣除项目本期发生额",
				"本期<br>减除额",
				"本期<br>减除额",
				"扣除项目期末余额",
				"计费销售额",
				"费率",
				"应缴费额",
				"期初未缴费额（多缴为负）",
				"本期已缴费额",
				"　　	其中：本期预缴费额",
				"　　	　　	本期缴纳上期费额",
				"　　	　　	本期缴纳欠费额",
				"期末未缴费额（多缴为负）",
				"　　	其中：欠缴费额（≧0）",
				"本期应补（退）费额",
				"本期检查已补缴费额"									
		};
		
		
		String[] col2_const_fieldvalues = new String[]{
				"应征收入",
				"免征收入",
				"",
				"",
				"应征收入减除额",
				"免征收入减除额",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				""									
		};
		
		// 增 值 税 纳 税 申 报 表   栏次
	    String[] col3_const_fieldvalues=new String[]{
			"1",				
			"2",				
			"3",				
			"4",				
			"5",				
			"6",				
			"7=3+4-5-6",				
			"8=1-5",				
			"9",				
			"10=8×9",				
			"11",			
			"12=13+14+15",				
			"13",				
			"14",				
			"15",				
			"16=10+11-12",				
			"17=11-14-15",				
			"18=10-13",				
			"19"			
		};
		
	    return new String[][]{col0_const_fieldvalues,col1_const_fieldvalues,col2_const_fieldvalues,col3_const_fieldvalues};
	}

	@Override
	public String[] getVariableFiledNames() {
		return new String[]{"mny1","mny2"};
	}

	@Override
	public String[][] getCellformulas(String accountStandard) {
		/***
		 * 一般货物、劳务和应税服务--> 本月数
		 */
		String[] col1_cellformulas = new String[]{
				"NA","NA","NA","NA",
				"=IF(('3#mny1'+'4#mny1')<='1#mny1','3#mny1'+'4#mny1','1#mny1')",
				"=IF(('3#mny1'+'4#mny1')<='2#mny1','3#mny1'+'4#mny1','2#mny1')",
				"=('3#mny1'+'4#mny1'-'5#mny1'-'6#mny1')",
				"FM","NA","FM"
				,"NA","FM","NA","NA","NA",
				"=('10#mny1'+'11#mny1'-'12#mny1')",
				"FM","FM","NA"		
		};
		
		
		/***
		 * 一般货物、劳务和应税服务--> 本年累计
		 */
	    String[] col2_cellformulas = new String[]{
				"NA","NA","STA","NA",
				"=IF(('3#mny2'+'4#mny2')<='1#mny2','3#mny2'+'4#mny2','1#mny2')",
				"=IF(('3#mny2'+'4#mny2')<='2#mny2','3#mny2'+'4#mny2','2#mny2')",
				"=('7#mny1')",
				"FM","STA","FM"
				,"STA","FM","NA","NA","NA",
				"=('16#mny1')",
				"STA","STA","NA"		
		};
		
	     
	    if(AccountEnum.STANDARD_2007.getCode().equals(accountStandard)){
	    	
	    	
	    }
		
		return new String[][]{col1_cellformulas,col2_cellformulas};
	}

	
	@Override
	public String[] getRowformulas() {
		return  new String[]{
				//"'7'=('3'+'4'-'5'-'6')" , 
				"'8'=('1'-'5')" , "'10'=('8'*'9')",
				"'12'=('13'+'14'+'15')" ,
			//	"'16'=('10'+'11'-'12')" ,
				"'17'=('11'-'14'-'15')",
				"'18'=('10'-'13')"
		};
	}

	
	@Override
	public String[] getColformulas() {
		// TODO Auto-generated method stub
		return null;
	}
}
