package com.dzf.zxkj.platform.model.st;

import com.dzf.zxkj.common.lang.DZFBoolean;

public class NssbCell {
	private String formula;
	private String checkfm;
	private String itemcode;
	private String vno;
	private String vcol;
	private DZFBoolean editable;
	
	public NssbCell(String fm,String cfm,String code,String vno,String vcol,DZFBoolean edit){
		this.formula=fm;
		this.checkfm=cfm;
		this.itemcode=code;
		this.vno=vno;
		this.vcol=vcol;
		this.editable=edit;
	}

	public String getFormula() {
		return formula;
	}

	public String getCheckfm() {
		return checkfm;
	}

	public String getItemcode() {
		return itemcode;
	}
	
	public String getVno() {
		return vno;
	}

	public String getVcol() {
		return vcol;
	}
	
	public DZFBoolean getEditable() {
		return editable;
	}
	
	
}
