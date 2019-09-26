package com.dzf.zxkj.platform.model.pjgl;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;

public class TicketNssbhVO extends SuperVO {
	
	private String pk_ticket_nssbh;//主键
	private String coperatorid;
	private DZFDate doperatedate;
	private Integer dr;
	private String fathercorp;
	private String pk_corp;
	private String nssbh;
	private String token;
	private String curtimestamp;
	private String activestamp;
	private DZFDateTime modifydatetime;
	private DZFDateTime ts;

	public String getPk_ticket_nssbh() {
		return pk_ticket_nssbh;
	}

	public void setPk_ticket_nssbh(String pk_ticket_nssbh) {
		this.pk_ticket_nssbh = pk_ticket_nssbh;
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

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}
	
	public String getFathercorp() {
		return fathercorp;
	}

	public void setFathercorp(String fathercorp) {
		this.fathercorp = fathercorp;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getNssbh() {
		return nssbh;
	}

	public void setNssbh(String nssbh) {
		this.nssbh = nssbh;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getCurtimestamp() {
		return curtimestamp;
	}

	public void setCurtimestamp(String curtimestamp) {
		this.curtimestamp = curtimestamp;
	}

	public String getActivestamp() {
		return activestamp;
	}

	public void setActivestamp(String activestamp) {
		this.activestamp = activestamp;
	}

	public DZFDateTime getModifydatetime() {
		return modifydatetime;
	}

	public void setModifydatetime(DZFDateTime modifydatetime) {
		this.modifydatetime = modifydatetime;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_ticket_nssbh";
	}

	@Override
	public String getTableName() {
		return "ynt_ticket_nssbh";
	}

}
