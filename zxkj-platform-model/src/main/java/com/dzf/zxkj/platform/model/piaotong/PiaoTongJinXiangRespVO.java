package com.dzf.zxkj.platform.model.piaotong;


import com.dzf.zxkj.common.model.SuperVO;

public class PiaoTongJinXiangRespVO extends SuperVO {
	
	private PiaoTongJinXiangDataVO data;

	public PiaoTongJinXiangDataVO getData() {
		return data;
	}

	public void setData(PiaoTongJinXiangDataVO data) {
		this.data = data;
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
