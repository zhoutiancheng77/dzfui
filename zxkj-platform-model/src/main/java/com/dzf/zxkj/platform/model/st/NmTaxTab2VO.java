package com.dzf.zxkj.platform.model.st;

import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * @author asoka
 * 一般纳税人
 *  城建税、教育费附加、地方教育附加税（费）申报表
 *
 */
public class NmTaxTab2VO extends NtBaseVO implements ReportCallInfo{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String pk_nt_tab2;
	
	
	/***
	 * 增值税
	 * 一般增值税
	 */
	private DZFDouble mny1;//一般增值税
	/***
	 * 增值税
	 * 免抵税额
	 */
	private DZFDouble mny2;//免抵税额
	
	/***
	 * 消费税
	 */
	private DZFDouble mny3;//消费税
	
	/***
	 * 营业税
	 */
	private DZFDouble mny4;//金额
	/***
	 * 合计
	 */
	private DZFDouble mny5;//金额
	
	/***
	 * 税率    （征收率）
	 */
	private DZFDouble mny6;//金额
	
	/***
	 * 本期应纳税额
	 */
	private DZFDouble mny7;//金额
	/***
	 * 减免性质代码
	 */
	private String cmny8;//
	
	/***
	 * 减免额
	 */
	private DZFDouble mny9;//金额
	
	/***
	 *本期已缴税（费）额
	 */
	private DZFDouble mny10;//金额
	/***
	 *本期应补（退）税（费）额
	 */
	private DZFDouble mny11;//金额
	
	
	private String rp_mny1;
	private String rp_mny2;
	private String rp_mny3;
	private String rp_mny4;
	private String rp_mny5;
	private String rp_mny6;
	private String rp_mny7;
	private String rp_cmny8;
	private String rp_mny9;
	private String rp_mny10;
	private String rp_mny11;
	
	
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

	public String getRp_mny5() {
		return rp_mny5;
	}

	public void setRp_mny5(String rp_mny5) {
		this.rp_mny5 = rp_mny5;
	}

	public String getRp_mny6() {
		return rp_mny6;
	}

	public void setRp_mny6(String rp_mny6) {
		this.rp_mny6 = rp_mny6;
	}

	public String getRp_mny7() {
		return rp_mny7;
	}

	public void setRp_mny7(String rp_mny7) {
		this.rp_mny7 = rp_mny7;
	}

	public String getRp_cmny8() {
		return rp_cmny8;
	}

	public void setRp_cmny8(String rp_cmny8) {
		this.rp_cmny8 = rp_cmny8;
	}

	public String getRp_mny9() {
		return rp_mny9;
	}

	public void setRp_mny9(String rp_mny9) {
		this.rp_mny9 = rp_mny9;
	}

	public String getRp_mny10() {
		return rp_mny10;
	}

	public void setRp_mny10(String rp_mny10) {
		this.rp_mny10 = rp_mny10;
	}

	public String getRp_mny11() {
		return rp_mny11;
	}

	public void setRp_mny11(String rp_mny11) {
		this.rp_mny11 = rp_mny11;
	}

	public String getPk_nt_tab2() {
		return pk_nt_tab2;
	}

	public void setPk_nt_tab2(String pk_nt_tab2) {
		this.pk_nt_tab2 = pk_nt_tab2;
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

	public DZFDouble getMny5() {
		return mny5;
	}

	public void setMny5(DZFDouble mny5) {
		this.mny5 = mny5;
	}

	public DZFDouble getMny6() {
		return mny6;
	}

	public void setMny6(DZFDouble mny6) {
		this.mny6 = mny6;
	}

	public DZFDouble getMny7() {
		return mny7;
	}

	public void setMny7(DZFDouble mny7) {
		this.mny7 = mny7;
	}



	public String getCmny8() {
		return cmny8;
	}

	public void setCmny8(String cmny8) {
		this.cmny8 = cmny8;
	}

	public DZFDouble getMny9() {
		return mny9;
	}

	public void setMny9(DZFDouble mny9) {
		this.mny9 = mny9;
	}

	public DZFDouble getMny10() {
		return mny10;
	}

	public void setMny10(DZFDouble mny10) {
		this.mny10 = mny10;
	}

	public DZFDouble getMny11() {
		return mny11;
	}

	public void setMny11(DZFDouble mny11) {
		this.mny11 = mny11;
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

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_nt_tab2";
	}

	@Override
	public String getTableName() {
		  return "ynt_nt_tab2";
	}

	@Override
	public String[] getConstantFieldNames() {
		return new String[]{"vprojectname"};
	}

	@Override
	public String[][] getConstantFieldValues() {
		
		String[] cols1 =new  String[]{
				"城建税（增值税）",
				"城建税（消费税）",
				"教育费附加（增值税）",
				"教育费附加（消费税）",
				"地方教育附加（增值税）",
				"地方教育附加（消费税）",
				"合计"
		};
		
		return new String[][]{cols1};
	}

	@Override
	public String[] getVariableFiledNames() {
		// TODO Auto-generated method stub
		return new String[]{"mny1","mny2","mny3","mny4","mny5","mny6","mny7","cmny8","mny9","mny10","mny11"};
	}

	
	@Override
	public String[][] getCellformulas(String pk_corp) {
		
		
		/***
		 * 增值税--> 一般增值税
		 */
		 String[] col1_cellformulas = new String[]{
				"222109_BYDF","STA","222109_BYDF","STA","222109_BYDF","STA","STA"
		};
				
		/***
		 * 增值税--> 免抵税额
		 */
       String[] col2_cellformulas = new String[]{
				"NA","STA","NA","STA","NA","STA","STA"
		};
		
		/***
		 * 消费税
		 */
		String[] col3_cellformulas = new String[]{
				"STA","222108_BYDF","STA","222108_BYDF","STA","222108_BYDF","STA"
		};
				
		/***
		 * 营业税
		 */
		 String[] col4_cellformulas = new String[]{
				"STA","STA","STA","STA","STA","STA","STA"
		};
				
		/***
		 * 合计
		 */
		String[] col5_cellformulas = new String[]{
				"FM","FM","FM","FM","FM","FM","FM"
		};
				
		/***
		 *  税率    （征收率）
		 */
		 String[] col6_cellformulas = new String[]{
				"NA","NA","NA","NA","NA","NA","STA"
		};
				
		/***
		 *  本期应纳税额
		 */
		String[] col7_cellformulas = new String[]{
				"FM","FM","FM","FM","FM","FM","FM"
		};
				
		/***
		 *  本期减免税（费）额   -->减免性质代码
		 */
		 String[] col8_cellformulas = new String[]{
				"NA","NA","NA","NA","NA","NA","STA"
		};
				
		/***
		 * 本期减免税（费）额  -> 减免额
		 */
		String[] col9_cellformulas = new String[]{
				"NA","NA","NA","NA","NA","NA","FM"
		};
		
		/***
		 * 本期已缴税（费）额
		 */
		 String[] col10_cellformulas = new String[]{
				"NA","NA","NA","NA","NA","NA","FM"
		};
		
		/***
		 * 本期应补（退）税（费）额
		 */
		String[] col11_cellformulas = new String[]{
				"FM","FM","FM","FM","FM","FM","FM"
		};
		
		 return new String[][]{col1_cellformulas,col2_cellformulas,col3_cellformulas,
				   col4_cellformulas,col5_cellformulas,col6_cellformulas,
				   col7_cellformulas,col8_cellformulas,col9_cellformulas,
				   col10_cellformulas,col11_cellformulas
				   };
	}

	@Override
	public String[] getRowformulas() {
		// TODO Auto-generated method stub
	    return  new String[]{"'7'=('1'+'2'+'3'+'4'+'5'+'6')"};
	}

	@Override
	public String[] getColformulas() {
		return  new String[]{
					"'5'=('1'+'2'+'3'+'4')" , "'7'=('5'*'6')" , "'11'=('7'-'9'-'10')"
			};
	}



}
