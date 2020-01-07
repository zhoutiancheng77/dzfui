package com.dzf.zxkj.report.excel.cwbb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.platform.model.report.LrbquarterlyVO;

import java.util.Date;


/**
 * 利润表季报表季报导出配置
 * 
 * @author zhw
 *
 */
public class LrbQuarterlyExcelField implements IExceport<LrbquarterlyVO> {
	
	public LrbQuarterlyExcelField() {
	}
	
	public LrbQuarterlyExcelField(String curr_jd) {
		this.curr_jd = curr_jd;
	}
	
	private LrbquarterlyVO[] lrbvos = null;

	private String qj = null;
	
	private String curr_jd = null;
	
	private String now = DZFDate.getDate(new Date()).toString();

	private String creator = null;

	private String corpname = null;

	private Fieldelement[] fields = new Fieldelement[] {
			new Fieldelement("xm", "项目", false, 0, false,60,false),
			new Fieldelement("hs", "行次", false, 0, false,10,false),
			new Fieldelement("bnlj", "本年累计", true, 2, true), 
			new Fieldelement("quarterFirst", "第一季度", true, 2, true),
			new Fieldelement("quarterSecond", "第二季度", true, 2, true),
			new Fieldelement("quarterThird", "第三季度", true, 2, true),
			new Fieldelement("quarterFourth", "第四季度", true, 2, true),
			new Fieldelement("sntqs", "上年同期数", true, 2, true)};

	private Fieldelement[] fields_jd1 = new Fieldelement[] { 
			new Fieldelement("xm", "项目", false, 0, false,60,false),
			new Fieldelement("hs", "行次", false, 0, false,10,false),
			new Fieldelement("bnlj", "本年累计", true, 2, true), 
			new Fieldelement("quarterFirst", "第一季度", true, 2, true),
			new Fieldelement("sntqs", "上年同期数", true, 2, true)};
	
	private Fieldelement[] fields_jd2 = new Fieldelement[] { 
			new Fieldelement("xm", "项目", false, 0, false,60,false),
			new Fieldelement("hs", "行次", false, 0, false,10,false),
			new Fieldelement("bnlj", "本年累计", true, 2, true), 
			new Fieldelement("quarterSecond", "第二季度", true, 2, true),
			new Fieldelement("sntqs", "上年同期数", true, 2, true)};
	
	private Fieldelement[] fields_jd3 = new Fieldelement[] { 
			new Fieldelement("xm", "项目", false, 0, false,60,false),
			new Fieldelement("hs", "行次", false, 0, false,10,false),
			new Fieldelement("bnlj", "本年累计", true, 2, true), 
			new Fieldelement("quarterThird", "第三季度", true, 2, true),
			new Fieldelement("sntqs", "上年同期数", true, 2, true)};
	
	private Fieldelement[] fields_jd4 = new Fieldelement[] { 
			new Fieldelement("xm", "项目", false, 0, false,60,false),
			new Fieldelement("hs", "行次", false, 0, false,10,false),
			new Fieldelement("bnlj", "本年累计", true, 2, true), 
			new Fieldelement("quarterFourth", "第四季度", true, 2, true),
			new Fieldelement("sntqs", "上年同期数", true, 2, true)};
	
	@Override
	public String getExcelport2007Name() {
		return "利润表季报("+corpname+")-" + qj.replace("-", "") + ".xlsx";
	}

	@Override
	public String getExcelport2003Name() {
		return "利润表季报("+corpname+")-" + qj.replace("-", "") + ".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "利润表季报";
	}

	@Override
	public String getSheetName() {
		return "利润表季报";
	}

	@Override
	public LrbquarterlyVO[] getData() {
		return lrbvos;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		if(!StringUtil.isEmpty(curr_jd)){
			if("03".equals(curr_jd)){
				return fields_jd1;
			}else if("06".equals(curr_jd)){
				return fields_jd2;
			}else if("09".equals(curr_jd)){
				return fields_jd3;
			}else if("12".equals(curr_jd)){
				return fields_jd4;
			}
		}
		return fields;
	}

	public void setLrbvos(LrbquarterlyVO[] lrbvos) {
		this.lrbvos = lrbvos;
	}

	@Override
	public String getQj() {
		return qj;
	}

	@Override
	public String getCreateSheetDate() {
		return now;
	}

	@Override
	public String getCreateor() {
		return creator;
	}

	public void setQj(String qj) {
		this.qj = qj;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Override
	public String getCorpName() {
		return corpname;
	}

	public void setCorpName(String corpname) {
		this.corpname = corpname;
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[] { true, true, true };
	}

}
