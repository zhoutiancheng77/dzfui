package com.dzf.zxkj.report.excel.cwzb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.MuiltSheetAndTitleExceport;
import com.dzf.zxkj.excel.param.TitleColumnExcelport;
import com.dzf.zxkj.excel.param.UnitExceport;
import com.dzf.zxkj.platform.model.report.FzKmmxVO;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;


/**
 * 辅助明细表导出配置
 * 
 * @author zhangj
 *
 */
public class FzmxMuiltSheetExcelField  extends MuiltSheetAndTitleExceport<FzKmmxVO> implements UnitExceport {

	private FzKmmxVO[] zcfzvos = null;
	
	private List<FzKmmxVO[]> allsheetzcvos = null;
	
	private String pk_currency;//币种
	
	private String currencyname;//币种
	
	private String[] periods = null;
	
	private String[] allsheetname = null;

	private String qj = null;

	private String now = DZFDate.getDate(new Date()).toString();

	private String creator = null;

	private String corpname = null;
	
	private Fieldelement[] getFileWbs() {
		List<Fieldelement> list = new ArrayList<Fieldelement>();
		list.add(new Fieldelement("fzlb", "辅助类别", false, 0, true, 2, 1));
		list.add(new Fieldelement("text", "项目名称", false, 0, true, 40, false, 2, 1));
		list.add(new Fieldelement("rq", "日期", false, 0, true, 2, 1));
		list.add(new Fieldelement("pzh", "凭证号", false, 0, true, 2, 1));
		list.add(new Fieldelement("zy", "摘要", false, 0, true, 2, 1));
		list.add(new Fieldelement("bz", "币别", false, 0, true, 2, 1));
		Fieldelement jfelement = new Fieldelement("", "借方金额", true, 2, true, 1, 2);
		jfelement.setChilds(new Fieldelement[] { new Fieldelement("ybjf", "原币", true, 4, true),
				new Fieldelement("jf", "本位币", true, 2, true) });
		list.add(jfelement);
		Fieldelement dfelement = new Fieldelement("", "贷方金额", true, 2, true, 1, 2);
		dfelement.setChilds(new Fieldelement[] { new Fieldelement("ybdf", "原币", true, 4, true),
				new Fieldelement("df", "本位币", true, 2, true) });
		list.add(dfelement);
		Fieldelement yeelement = new Fieldelement("", "余额", true, 2, true, 1, 3);
		yeelement.setChilds(new Fieldelement[] { new Fieldelement("fx", "方向", false, 0, true),
				new Fieldelement("ybye", "原币", true, 4, true), new Fieldelement("ye", "本位币", true, 2, true) });
		list.add(yeelement);

		return list.toArray(new Fieldelement[0]);
	}
	
	private Fieldelement[] fields = new Fieldelement[] {
			new Fieldelement("fzlb", "辅助类别", false, 0, true),
			new Fieldelement("text", "项目名称", false, 0, true,80,false),
			new Fieldelement("rq", "日期", false, 0, true),
			new Fieldelement("pzh", "凭证号", false, 0, true),
			new Fieldelement("zy", "摘要", false, 0, true),
			new Fieldelement("jf", "借方", true, 2, true),
			new Fieldelement("df", "贷方", true, 2, true),
			new Fieldelement("fx", "方向", false, 0, true),
			new Fieldelement("ye", "余额", true, 2, true) };

	@Override
	public String getExcelport2007Name() {
		return "辅助明细表("+corpname+")_" + now + ".xlsx";
	}

	@Override
	public String getExcelport2003Name() {
		return "辅助明细表("+corpname+")_" + now + ".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "辅助明细表";
	}

	@Override
	public String getSheetName() {
		return "辅助明细表";
	}

	@Override
	public FzKmmxVO[] getData() {
		return zcfzvos;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		if (!StringUtil.isEmpty(pk_currency) && !pk_currency.equals(IGlobalConstants.RMB_currency_id)) {
			return getFileWbs();
		} else {
			return fields;
		}
	}

	public void setZcfzvos(FzKmmxVO[] zcfzvos) {
		this.zcfzvos = zcfzvos;
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
	
	@Override
	public List<FzKmmxVO[]> getAllSheetData() {
		return allsheetzcvos;
	}

	@Override
	public String[] getAllSheetName() {
		return allsheetname;
	}
	
	public List<FzKmmxVO[]> getAllsheetzcvos() {
		return allsheetzcvos;
	}

	public void setAllsheetzcvos(List<FzKmmxVO[]> allsheetzcvos) {
		this.allsheetzcvos = allsheetzcvos;
	}

	public String[] getAllsheetname() {
		return allsheetname;
	}

	public void setAllsheetname(String[] allsheetname) {
		this.allsheetname = allsheetname;
	}

	
	
	public String[] getPeriods() {
		return periods;
	}

	public void setPeriods(String[] periods) {
		this.periods = periods;
	}

	@Override
	public String[] getAllPeriod() {
		return periods;
	}
	


	@Override
	public List<TitleColumnExcelport> getHeadColumns() {
		List<TitleColumnExcelport> lists = new ArrayList<TitleColumnExcelport>();
		return lists;
	}

	@Override
	public TitleColumnExcelport getTitleColumns() {
		TitleColumnExcelport column1 = new TitleColumnExcelport(1, getSheetName(), HorizontalAlignment.RIGHT);
		return column1;
	}

	public String getPk_currency() {
		return pk_currency;
	}

	public void setPk_currency(String pk_currency) {
		this.pk_currency = pk_currency;
	}
	
	

	public String getCurrencyname() {
		return currencyname;
	}

	public void setCurrencyname(String currencyname) {
		this.currencyname = currencyname;
	}

	@Override
	public String getDw() {
		if (StringUtil.isEmpty(currencyname)) {
			return "元";
		} else {
			return currencyname;
		}
	}

	
}
