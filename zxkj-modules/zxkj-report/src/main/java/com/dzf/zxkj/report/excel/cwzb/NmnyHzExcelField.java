package com.dzf.zxkj.report.excel.cwzb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.platform.model.report.NumMnyGlVO;
import com.dzf.zxkj.report.utils.ReportUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 数量金额总账导出配置
 * @author zpm
 *
 */
public class NmnyHzExcelField implements IExceport<NumMnyGlVO> {
	
	private NumMnyGlVO[] NumMnyGlVOs = null;
	
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private Fieldelement[] fields = null;
	
	private int numPrecision;//数量精度
	private int pricePrecision;//单价精度
	
	private String showbnljjf;//本年累计借方，显示
	private String showbnljdf;//本年累计贷方，显示

	private String showqcprice;//期初单价,展示
	private String showjfprice;//借方单价,展示
	private String showdfprice;//贷方单价,展示
	private String showyeprice;//余额单价,展示
	
//	public NmnyHzExcelField(){
//		numPrecision = 4;
//		pricePrecision = 4;
//	}
	
	public NmnyHzExcelField(int numPrecision, int pricePrecision,
							String showbnljjf,String showbnljdf,
							String showqcprice, String showjfprice,
							String showdfprice, String showyeprice){
		this.numPrecision = numPrecision;
		this.pricePrecision = pricePrecision;
		this.showbnljjf = showbnljjf;
		this.showbnljdf = showbnljdf;

		this.showqcprice = showqcprice;
		this.showjfprice = showjfprice;
		this.showdfprice = showdfprice;
		this.showyeprice = showyeprice;
	}
	
	@Override
	public String getExcelport2007Name() {
		return "数量金额总账-"+ ReportUtil.formatQj(qj)+".xlsx";
	}
	
	@Override
	public String getExcelport2003Name() {
		return "数量金额总账-"+ReportUtil.formatQj(qj)+".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "数量金额总账";
	}

	@Override
	public String getSheetName() {
		return "数量金额总账";
	}

	@Override
	public NumMnyGlVO[] getData() {
		return NumMnyGlVOs;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return fields;
	}

	public void setNumMnyGlVOs(NumMnyGlVO[] NumMnyGlVOs) {
		this.NumMnyGlVOs = NumMnyGlVOs;
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
	
	public void setCorpName(String corpname){
		this.corpname = corpname;
	}
	
	public Fieldelement[] getFields1() {
		List<Fieldelement> fieldlist = new ArrayList<Fieldelement>();
		fieldlist.add(new Fieldelement("kmbm", "科目编码", false, 0, false));
		fieldlist.add(new Fieldelement("kmbm", "科目编码", false, 0, false));
		fieldlist.add(new Fieldelement("kmmc", "科目名称", false, 0, false));
		fieldlist.add(new Fieldelement("dw", "计量单位", false, 0, false));
		fieldlist.add(new Fieldelement("dir", "方向", false, 0, false));
		fieldlist.add(new Fieldelement("qcnum", "期初数量", true, numPrecision, true));

		if("Y".equals(showqcprice)) {
			fieldlist.add(new Fieldelement("qcprice", "期初单价", true, pricePrecision, true));
		}

		fieldlist.add(new Fieldelement("qcmny", "期初金额", true, 2, true));
		fieldlist.add(new Fieldelement("bqjfnum", "本期借方数量", true, numPrecision, true));

		if("Y".equals(showjfprice)) {
			fieldlist.add(new Fieldelement("bqjfprice", "本期借方单价", true, pricePrecision, true));
		}

		fieldlist.add(new Fieldelement("bqjfmny", "本期借方金额", true, 2, true));
		fieldlist.add(new Fieldelement("bqdfnum", "本期贷方数量", true, numPrecision, true));

		if("Y".equals(showdfprice)) {
			fieldlist.add(new Fieldelement("bqdfprice", "本期贷方单价", true, pricePrecision, true));
		}

		fieldlist.add(new Fieldelement("bqdfmny", "本期贷方金额", true, 2, true));

		if("Y".equals(showbnljjf)){
			fieldlist.add(new Fieldelement("bnjfnum", "本年借方数量", true, numPrecision, true));
			fieldlist.add(new Fieldelement("bnjfmny", "本年借方金额", true, 2, true));
		}
		if("Y".equals(showbnljdf)){
			fieldlist.add(new Fieldelement("bndfnum", "本年贷方数量", true, numPrecision, true));
			fieldlist.add(new Fieldelement("bndfmny", "本年贷方金额", true, 2, true));
		}
		fieldlist.add(new Fieldelement("qmnum", "期末数量", true, numPrecision, true));

		if("Y".equals(showyeprice)) {
			fieldlist.add(new Fieldelement("qmprice", "期末单价", true, pricePrecision, true));
		}

		fieldlist.add(new Fieldelement("qmmny", "期末金额", true, 2, true));
		return fieldlist.toArray(new Fieldelement[0]);
	}

	public Fieldelement[] getFields2() {

		return new Fieldelement[]{
				new Fieldelement("beginqj", "起始时间",false,0,false),
				new Fieldelement("endqj", "截止时间",false,0,false),
				new Fieldelement("kmmc", "科目名称",false,0,false),
				new Fieldelement("spmc", "存货名称",false,0,false),
				new Fieldelement("qcnum", "期初数量",true, numPrecision,true),
				new Fieldelement("qcprice", "期初单价",true, pricePrecision,true),
				new Fieldelement("qcmny", "期初金额",true,2,true),
				new Fieldelement("bqjfnum", "本期借方数量",true, numPrecision,true),
				new Fieldelement("bqjfmny", "本期借方金额",true,2,true),
				new Fieldelement("bqdfnum", "本期贷方数量",true, numPrecision,true),
				new Fieldelement("bqdfmny", "本期贷方金额",true,2,true),
				new Fieldelement("bnjfnum", "本年借方数量",true, numPrecision,true),
				new Fieldelement("bnjfmny", "本年借方金额",true,2,true),
				new Fieldelement("bndfnum", "本年贷方数量",true, numPrecision,true),
				new Fieldelement("bndfmny", "本年贷方金额",true,2,true),
				new Fieldelement("qmnum", "期末数量",true, numPrecision,true),
				new Fieldelement("qmprice", "期末单价",true, pricePrecision,true),
				new Fieldelement("qmmny", "期末金额",true,2,true)
		};
	}

	public String getShowqcprice() {
		return showqcprice;
	}

	public void setShowqcprice(String showqcprice) {
		this.showqcprice = showqcprice;
	}

	public String getShowjfprice() {
		return showjfprice;
	}

	public void setShowjfprice(String showjfprice) {
		this.showjfprice = showjfprice;
	}

	public String getShowdfprice() {
		return showdfprice;
	}

	public void setShowdfprice(String showdfprice) {
		this.showdfprice = showdfprice;
	}

	public String getShowyeprice() {
		return showyeprice;
	}

	public void setShowyeprice(String showyeprice) {
		this.showyeprice = showyeprice;
	}

	public String getShowbnljjf() {
		return showbnljjf;
	}

	public void setShowbnljjf(String showbnljjf) {
		this.showbnljjf = showbnljjf;
	}

	public String getShowbnljdf() {
		return showbnljdf;
	}

	public void setShowbnljdf(String showbnljdf) {
		this.showbnljdf = showbnljdf;
	}

	public void setFields(Fieldelement[] fields) {
		this.fields = fields;
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[]{true,true,false};
	}
}