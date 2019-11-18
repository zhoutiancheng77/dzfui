package com.dzf.zxkj.platform.model.zncs;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;

public class OcrAuxiliaryAccountBVO extends SuperVO {
	AuxiliaryAccountBVO datainfo ;
	private String subjname;
	private String showname;
	

	public AuxiliaryAccountBVO getDatainfo() {
		return datainfo;
	}

	public void setDatainfo(AuxiliaryAccountBVO datainfo) {
		this.datainfo = datainfo;
	}

	public String getShowname() {
		return showname;
	}

	public void setShowname(String showname) {
		this.showname = showname;
	}

	public String getSubjname() {
		return subjname;
	}

	public void setSubjname(String subjname) {
		this.subjname = subjname;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}

