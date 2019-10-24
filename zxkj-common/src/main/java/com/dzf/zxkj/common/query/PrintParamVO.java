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
