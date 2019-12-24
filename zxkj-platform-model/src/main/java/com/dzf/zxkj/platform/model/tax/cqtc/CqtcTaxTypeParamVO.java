package com.dzf.zxkj.platform.model.tax.cqtc;


import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@SuppressWarnings("rawtypes")
public class CqtcTaxTypeParamVO extends SuperVO {
	
	private static final long serialVersionUID = 5429649143310643677L;

	public String pk_corp;//公司
	
	public String openId;
	
	//纳税人识别号
	@JsonProperty("vscode")
	public String vsoccrecode;//纳税人识别号
	
	//申报种类编号	
	private String sb_zlbh;
	//期间	
	private String period;
	//税款所属时间起	
	private String periodfrom;
	//税款所属时间止	
	private String periodto;

	private List<CqtcMessageVO> message;
	
	
	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getVsoccrecode() {
		return vsoccrecode;
	}

	public void setVsoccrecode(String vsoccrecode) {
		this.vsoccrecode = vsoccrecode;
	}

	public String getSb_zlbh() {
		return sb_zlbh;
	}

	public void setSb_zlbh(String sb_zlbh) {
		this.sb_zlbh = sb_zlbh;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getPeriodfrom() {
		return periodfrom;
	}

	public void setPeriodfrom(String periodfrom) {
		this.periodfrom = periodfrom;
	}

	public String getPeriodto() {
		return periodto;
	}

	public void setPeriodto(String periodto) {
		this.periodto = periodto;
	}

	public List<CqtcMessageVO> getMessage() {
		return message;
	}

	public void setMessage(List<CqtcMessageVO> message) {
		this.message = message;
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
