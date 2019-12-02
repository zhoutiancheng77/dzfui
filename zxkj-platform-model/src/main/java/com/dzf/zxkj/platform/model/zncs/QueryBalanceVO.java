package com.dzf.zxkj.platform.model.zncs;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;

public class QueryBalanceVO extends SuperVO {
	private String pk_dzfservicedes;// 增值服务类型
	private DZFDate beginDate;//开始日期
	private DZFDate endDate;//结束日期
	private String[] pk_corpkjgs;//会计公司
	
	public String getPk_dzfservicedes() {
		return pk_dzfservicedes;
	}

	public void setPk_dzfservicedes(String pk_dzfservicedes) {
		this.pk_dzfservicedes = pk_dzfservicedes;
	}

	public DZFDate getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(DZFDate beginDate) {
		this.beginDate = beginDate;
	}

	public DZFDate getEndDate() {
		return endDate;
	}

	public void setEndDate(DZFDate endDate) {
		this.endDate = endDate;
	}

	public String[] getPk_corpkjgs() {
		return pk_corpkjgs;
	}

	public void setPk_corpkjgs(String[] pk_corpkjgs) {
		this.pk_corpkjgs = pk_corpkjgs;
	}

	@Override
	public String getParentPKFieldName() {
		return "";
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
