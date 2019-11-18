package com.dzf.zxkj.platform.model.zncs;


import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;

/**
 * 客户
 *
 */
public class CustomerReferenceVO extends AuxiliaryAccountBVO {

	private String pyname;//拼音名称

	public String getPyname() {
		return pyname;
	}

	public void setPyname(String pyname) {
		this.pyname = pyname;
	}
	
	
	
}
