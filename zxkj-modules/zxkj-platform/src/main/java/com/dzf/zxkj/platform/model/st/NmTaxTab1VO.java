package com.dzf.zxkj.platform.model.st;


import com.dzf.zxkj.common.enums.AccountEnum;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * @author asoka
 * 一般纳税人
 *  增 值 税 纳 税 申 报 表
 *
 */
public class NmTaxTab1VO extends NtBaseVO implements ReportCallInfo{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String pk_nt_tab1;
	
	/***
	 * 项目大类
	 */
	private String vprojectclass;
	
	/***
	 * 栏次
	 */
	private String itemno;
	
	/***
	 * 一般货物、劳务和应税服务
	 * 本月数
	 */
	private DZFDouble mny1;//金额
	
	/***
	 * 一般货物、劳务和应税服务
	 * 本年累计
	 */
	private DZFDouble mny2;//金额
	
	/***
	 * 即征即退货物、劳务和应税服务
	 * 本月数
	 */
	private DZFDouble mny3;//金额
	
	/***
	 * 即征即退货物、劳务和应税服务
	 * 本年累计
	 */
	private DZFDouble mny4;//金额
	
	
	private String rp_mny1;
	private String rp_mny2;
	private String rp_mny3;
	private String rp_mny4;
	
	
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

	public String getRp_mny3() {
		return rp_mny3;
	}

	public void setRp_mny3(String rp_mny3) {
		this.rp_mny3 = rp_mny3;
	}

	public String getRp_mny4() {
		return rp_mny4;
	}

	public void setRp_mny4(String rp_mny4) {
		this.rp_mny4 = rp_mny4;
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
	
	public String getPk_nt_tab1() {
		return pk_nt_tab1;
	}

	public void setPk_nt_tab1(String pk_nt_tab1) {
		this.pk_nt_tab1 = pk_nt_tab1;
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

	public DZFDouble getMny3() {
		return mny3;
	}

	public void setMny3(DZFDouble mny3) {
		this.mny3 = mny3;
	}

	public DZFDouble getMny4() {
		return mny4;
	}

	public void setMny4(DZFDouble mny4) {
		this.mny4 = mny4;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_nt_tab1";
	}

	@Override
	public String getTableName() {
	   return "ynt_nt_tab1";
	}

	@Override
	public String[] getConstantFieldNames() {
		// TODO Auto-generated method stub
		return new String[]{"vprojectclass","vprojectname","itemno"};
	}

	@Override
	public String[][] getConstantFieldValues() {
		
		String[] col0_const_fieldvalues = new String[]{"销售额","销售额","销售额","销售额","销售额","销售额","销售额","销售额","销售额","销售额",
				"税款计算","税款计算","税款计算","税款计算","税款计算","税款计算","税款计算","税款计算","税款计算","税款计算","税款计算","税款计算","税款计算","税款计算",
				"税款缴纳","税款缴纳","税款缴纳","税款缴纳","税款缴纳","税款缴纳","税款缴纳","税款缴纳","税款缴纳","税款缴纳","税款缴纳","税款缴纳","税款缴纳","税款缴纳"};
		
		// 增 值 税 纳 税 申 报 表   项     目
		String[] col1_const_fieldvalues = new String[]{
			"（一）按适用税率计税销售额",											
			"其中：应税货物销售额",											
			"      &nbsp;&nbsp;应税劳务销售额",											
			"      			纳税检查调整的销售额",											
			"（二）按简易办法计税销售额",											
			"其中：纳税检查调整的销售额",											
			"（三）免、抵、退办法出口销售额	",										
			"（四）免税销售额",											
			"其中：免税货物销售额",											
			 "           免税劳务销售额",											
			"销项税额",											
			"进项税额	",										
			"上期留抵税额",											
			"进项税额转出",											
			"免、抵、退应退税额",											
			"按适用税率计算的纳税检查应补缴税额	",										
			"应抵扣税额合计",											
			"实际抵扣税额",											
			"应纳税额	",										
			"期末留抵税额",											
			"简易计税办法计算的应纳税额",											
			"按简易计税办法计算的纳税检查应补缴税额",											
			"应纳税额减征额	",										
			"应纳税额合计",											
			"期初未缴税额（多缴为负数）",											
			"实收出口开具专用缴款书退税额",											
			"本期已缴税额",											
			"①分次预缴税额	",										
			"②出口开具专用缴款书预缴税额",											
			"③本期缴纳上期应纳税额",											
			"④本期缴纳欠缴税额",											
			"期末未缴税额（多缴为负数）",											
			"其中：欠缴税额（≥0）",											
			"本期应补(退)税额",											
			"即征即退实际退税额",											
			"期初未缴查补税额",											
			"本期入库查补税额",											
			"期末未缴查补税额"											
		};
		
		// 增 值 税 纳 税 申 报 表   栏次
	    String[] col2_const_fieldvalues=new String[]{
			"1",				
			"2",				
			"3",				
			"4",				
			"5",				
			"6",				
			"7",				
			"8",				
			"9",				
			"10",				
			"11",			
			"12",				
			"13",				
			"14",				
			"15",				
			"16",				
			"17=12+13-14-15+16",				
			"18（如17<11，则为17，否则为11）",				
			"19=11-18",				
			"20=17-18",				
			"21",				
			"22",				
			"23",				
			"24=19+21-23",				
			"25",				
			"26",				
			"27=28+29+30+31",				
			"28",				
			"29",				
			"30",				
			"31",				
			"32=24+25+26-27",				
			"33=25+26-27",				
			"34=24-28-29",				
			"35",				
			"36",				
			"37",				
			"38=16+22+36-37"				
		};
		
	    return new String[][]{col0_const_fieldvalues,col1_const_fieldvalues,col2_const_fieldvalues};
	}

	@Override
	public String[] getVariableFiledNames() {
		return new String[]{"mny1","mny2","mny3","mny4"};
	}

	@Override
	public String[][] getCellformulas(String accountStandard) {
		/***
		 * 一般货物、劳务和应税服务--> 本月数
		 */
		String[] col1_cellformulas = new String[]{
				"NA","500101_BYDF","500102_BYDF+500103_BYDF+500104_BYDF+500105_BYDF","NA","NA","NA","NA","NA","NA","NA"
				,"22210102_BYDF","22210101_BYJF","NA","NA","NA","NA","FM","=IF('17#vmny'<'11#vmny','17#vmny','11#vmny')","FM","FM",			
				"NA","NA","NA","FM","NA","NA","FM","NA","NA","NA",
				"NA","FM","FM","FM","STA","NA","NA","FM"			
		};
		
		
		/***
		 * 一般货物、劳务和应税服务--> 本年累计
		 */
	    String[] col2_cellformulas = new String[]{
				"NA","NA","600102_BNLJDF+600103_BNLJDF+600104_BNLJDF+600105_BNLJDF","NA","NA","NA","NA","NA","NA","NA"
				,"22210102_BNLJDF","22210101_BNLJJF","NA","NA","NA","NA","STA","=IF('17#vmny'<'11#vmny','17#vmny','11#vmny')","FM","FM",			
				"NA","NA","NA","FM","NA","NA","FM","STA","STA","NA",
				"NA","FM","STA","STA","STA","NA","NA","FM"			
		};
		
		
		/***
		 * 即征即退货物、劳务和应税服务--> 本月数
		 */
	   String[] col3_cellformulas = new String[]{
				"NA","NA","NA","NA","NA","NA","STA","STA","STA","STA"
				,"NA","NA","NA","NA","STA","STA","FM","=IF('17#vmny'<'11#vmny','17#vmny','11#vmny')","FM","FM",			
				"NA","STA","NA","FM","NA","STA","FM","NA","STA","NA",
				"NA","FM","FM","FM","NA","STA","STA","STA"			
		};
		
		
		/***
		 * 即征即退货物、劳务和应税服务--> 本年累计
		 */
	     String[] col4_cellformulas = new String[]{
				"NA","NA","NA","NA","NA","NA","STA","STA","STA","STA"
				,"NA","NA","STA","NA","STA","STA","STA","=IF('17#vmny'<'11#vmny','17#vmny','11#vmny')","FM","STA",			
				"NA","STA","NA","FM","NA","STA","FM","STA","STA","NA",
				"NA","FM","STA","STA","NA","STA","STA","STA"			
		};
	     
	    if(AccountEnum.STANDARD_2007.getCode().equals(accountStandard)){
	    	
	    	col1_cellformulas[1] = "600101_BYDF";
	    	col1_cellformulas[2] = "600101_BYDF+600102_BYDF+600103_BYDF+600104_BYDF+600105_BYDF";
	    	

	    	col2_cellformulas[2] = "600101_BNLJDF+600102_BNLJDF+600103_BNLJDF+600104_BNLJDF+600105_BNLJDF";

	    	
	    }
		
		return new String[][]{col1_cellformulas,col2_cellformulas,
				   col3_cellformulas,col4_cellformulas};
	}

	@Override
	public String[] getRowformulas() {
		return  new String[]{
				"'17'=('12'+'13'-'14'-'15'+'16')" , "'24'=('19'+'21'-'23')" , "'27'=('28'+'29'+'30'+'31')",
				"'32'=('24'+'25'+'26'-'27')" , "'33'=('25'+'26'-'27')" , "'34'=('24'-'28'-'29')",
				"'38'=('16'+'22'+'36'-'37')"
		};
	}

	@Override
	public String[] getColformulas() {
		// TODO Auto-generated method stub
		return null;
	}
}
