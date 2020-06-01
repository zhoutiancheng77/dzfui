package com.dzf.zxkj.app.model.org;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

public class ServiceOrgVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String pk_svorg ;
	private String pk_corp ;
	private String orgcode;
	private String orgname ;
	private DZFDouble longitude ;
	private DZFDouble latitude  ;
	private String addr ;
	private String tel ;
	private String principal ;//负责人
	private String memo ;
	private Integer dr ;
	private DZFDateTime ts ;
	private DZFDouble distance;
	private String orgshortname;
	
	
	
	
	public String getOrgshortname() {
		return orgshortname;
	}
	public void setOrgshortname(String orgshortname) {
		this.orgshortname = orgshortname;
	}
	
	public DZFDouble getDistance() {
		return distance;
	}
	public void setDistance(DZFDouble distance) {
		this.distance = distance;
	}
	public String getPk_svorg() {
		return pk_svorg;
	}
	public void setPk_svorg(String pk_svorg) {
		this.pk_svorg = pk_svorg;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getOrgcode() {
		return orgcode;
	}
	public void setOrgcode(String orgcode) {
		this.orgcode = orgcode;
	}
	public String getOrgname() {
		return orgname;
	}
	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}
	public DZFDouble getLongitude() {
		return longitude;
	}
	public void setLongitude(DZFDouble longitude) {
		this.longitude = longitude;
	}
	public DZFDouble getLatitude() {
		return latitude;
	}
	public void setLatitude(DZFDouble latitude) {
		this.latitude = latitude;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getPrincipal() {
		return principal;
	}
	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Integer getDr() {
		return dr;
	}
	public void setDr(Integer dr) {
		this.dr = dr;
	}
	public DZFDateTime getTs() {
		return ts;
	}
	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	

	public java.lang.String getParentPKFieldName() {
		return "pk_corp";
	} 
	
	@Override
	public String getPKFieldName() {
		return "pk_svorg";
	}
	@Override
	public String getTableName() {
		return "bd_svorg";
	}
}
