package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 行业模板
 * 
 */
public class BDTradeVO extends SuperVO<BDTradeVO> {
	@JsonProperty("id")
	private String pk_trade;
	@JsonProperty("cpid")
	private String coperatorid;
	@JsonProperty("dpdate")
	private DZFDate doperatedate;
	private DZFDateTime ts;
	private Integer dr; 
	@JsonProperty("code")
	private String tradecode;
	@JsonProperty("name")
	private String tradename;
	@JsonProperty("transfer")
	private DZFBoolean iscosttransfer;
	private String memo;
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;
	private String vdef6;
	private String vdef7;
	private String vdef8;
	private String vdef9;
	private String vdef10;
	@JsonProperty("transferrate")
	private String costtransferrate;
	@JsonProperty("corp")
	private String pk_corp;
	 // 增值税税负率
    private DZFDouble warning_rate;
	private String state;
	
	public DZFDouble getWarning_rate() {
		return warning_rate;
	}
	public void setWarning_rate(DZFDouble warning_rate) {
		this.warning_rate = warning_rate;
	}
	public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public BDTradeVO(){
		super();
	}
	public String getPk_trade() {
		return pk_trade;
	}
	public void setPk_trade(String pk_trade) {
		this.pk_trade = pk_trade;
	}
	public String getCoperatorid() {
		return coperatorid;
	}
	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}
	public DZFDate getDoperatedate() {
		return doperatedate;
	}
	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}
	public DZFDateTime getTs() {
		return ts;
	}
	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	public Integer getDr() {
		return dr;
	}
	public void setDr(Integer dr) {
		this.dr = dr;
	}
	public String getTradecode() {
		return tradecode;
	}
	public void setTradecode(String tradecode) {
		this.tradecode = tradecode;
	}
	public String getTradename() {
		return tradename;
	}
	public void setTradename(String tradename) {
		this.tradename = tradename;
	}
	public DZFBoolean getIscosttransfer() {
		return iscosttransfer;
	}
	public void setIscosttransfer(DZFBoolean iscosttransfer) {
		this.iscosttransfer = iscosttransfer;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getVdef1() {
		return vdef1;
	}
	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}
	public String getVdef2() {
		return vdef2;
	}
	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}
	public String getVdef3() {
		return vdef3;
	}
	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}
	public String getVdef4() {
		return vdef4;
	}
	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}
	public String getVdef5() {
		return vdef5;
	}
	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}
	public String getVdef6() {
		return vdef6;
	}
	public void setVdef6(String vdef6) {
		this.vdef6 = vdef6;
	}
	public String getVdef7() {
		return vdef7;
	}
	public void setVdef7(String vdef7) {
		this.vdef7 = vdef7;
	}
	public String getVdef8() {
		return vdef8;
	}
	public void setVdef8(String vdef8) {
		this.vdef8 = vdef8;
	}
	public String getVdef9() {
		return vdef9;
	}
	public void setVdef9(String vdef9) {
		this.vdef9 = vdef9;
	}
	public String getVdef10() {
		return vdef10;
	}
	public void setVdef10(String vdef10) {
		this.vdef10 = vdef10;
	}
	public String getCosttransferrate() {
		return costtransferrate;
	}
	public void setCosttransferrate(String costtransferrate) {
		this.costtransferrate = costtransferrate;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_trade";
	}
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_bd_trade";
	}

}
