package com.dzf.zxkj.common.query;


import com.dzf.zxkj.common.model.SuperVO;

public class PrintParamVO extends SuperVO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String list;
	private String type;
	private String pageOrt;
	private String left;
	private String top;
	private String printdate;
	private String font;
	private String pageNum;
	private String ismerge;
	private String ishidepzh;
	//字段显示的属性
	private String columnslist ;
	private String corpName;
	private String period;
	private String titleperiod;
	private String isPaging;//是否分页
	private String lineHeight;//行高
	private String isbilateralprint;//是否双向打印
	private String projectname;//项目名称
	private String print_all;
	private String showlb;
	private String fzlb_name;
	private String showbm;//
	private String isxshl;// 是否显示汇率
	private String xmmcid;
	private String columnOrder;//列表顺序
	private String currjd;//当前季度
	private String showAmount;
	private String extra;//汇率
	private String currencyname;//币种名称

	private String showjf;// 是否展示借方
	private String showdf;// 是否展示贷方
	private String showdj;// 是否展示单价
	private String numstr;// 数量精度
	private String pricestr;// 单价精度

	public String getShowjf() {
		return showjf;
	}

	public void setShowjf(String showjf) {
		this.showjf = showjf;
	}

	public String getShowdf() {
		return showdf;
	}

	public void setShowdf(String showdf) {
		this.showdf = showdf;
	}

	public String getShowdj() {
		return showdj;
	}

	public void setShowdj(String showdj) {
		this.showdj = showdj;
	}

	public String getNumstr() {
		return numstr;
	}

	public void setNumstr(String numstr) {
		this.numstr = numstr;
	}

	public String getPricestr() {
		return pricestr;
	}

	public void setPricestr(String pricestr) {
		this.pricestr = pricestr;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getCurrencyname() {
		return currencyname;
	}

	public void setCurrencyname(String currencyname) {
		this.currencyname = currencyname;
	}

	public String getShowAmount() {
		return showAmount;
	}

	public void setShowAmount(String showAmount) {
		this.showAmount = showAmount;
	}

	public String getCurrjd() {
		return currjd;
	}

	public void setCurrjd(String currjd) {
		this.currjd = currjd;
	}

	public String getColumnOrder() {
		return columnOrder;
	}

	public void setColumnOrder(String columnOrder) {
		this.columnOrder = columnOrder;
	}

	public String getXmmcid() {
		return xmmcid;
	}

	public void setXmmcid(String xmmcid) {
		this.xmmcid = xmmcid;
	}

	public String getIsxshl() {
		return isxshl;
	}

	public void setIsxshl(String isxshl) {
		this.isxshl = isxshl;
	}

	public String getPrint_all() {
		return print_all;
	}

	public void setPrint_all(String print_all) {
		this.print_all = print_all;
	}

	public String getShowlb() {
		return showlb;
	}

	public void setShowlb(String showlb) {
		this.showlb = showlb;
	}

	public String getFzlb_name() {
		return fzlb_name;
	}

	public void setFzlb_name(String fzlb_name) {
		this.fzlb_name = fzlb_name;
	}

	public String getShowbm() {
		return showbm;
	}

	public void setShowbm(String showbm) {
		this.showbm = showbm;
	}

	public String getProjectname() {
		return projectname;
	}

	public void setProjectname(String projectname) {
		this.projectname = projectname;
	}

	public String getIsbilateralprint() {
		return isbilateralprint;
	}

	public void setIsbilateralprint(String isbilateralprint) {
		this.isbilateralprint = isbilateralprint;
	}

	public String getLineHeight() {
		return lineHeight;
	}

	public void setLineHeight(String lineHeight) {
		this.lineHeight = lineHeight;
	}

	public String getIsPaging() {
		return isPaging;
	}

	public void setIsPaging(String isPaging) {
		this.isPaging = isPaging;
	}

	public String getTitleperiod() {
		return titleperiod;
	}

	public void setTitleperiod(String titleperiod) {
		this.titleperiod = titleperiod;
	}

	public String getCorpName() {
		return corpName;
	}

	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getColumnslist() {
		return columnslist;
	}

	public void setColumnslist(String columnslist) {
		this.columnslist = columnslist;
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}



	public String getLeft() {
		return left;
	}

	public void setLeft(String left) {
		this.left = left;
	}

	public String getTop() {
		return top;
	}

	public void setTop(String top) {
		this.top = top;
	}

	public String getPrintdate() {
		return printdate;
	}

	public void setPrintdate(String printdate) {
		this.printdate = printdate;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}




	public String getPageOrt() {
		return pageOrt;
	}

	public void setPageOrt(String pageOrt) {
		this.pageOrt = pageOrt;
	}


	public String getPageNum() {
		return pageNum;
	}

	public void setPageNum(String pageNum) {
		this.pageNum = pageNum;
	}

	public String getIsmerge() {
		return ismerge;
	}

	public void setIsmerge(String ismerge) {
		this.ismerge = ismerge;
	}

	public String getIshidepzh() {
		return ishidepzh;
	}

	public void setIshidepzh(String ishidepzh) {
		this.ishidepzh = ishidepzh;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
