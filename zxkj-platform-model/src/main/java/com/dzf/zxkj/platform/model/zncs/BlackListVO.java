package com.dzf.zxkj.platform.model.zncs;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 黑名单
 * @author mfz
 *
 */
public class BlackListVO extends SuperVO {

	@JsonProperty("id")
	private String pk_blacklist;
	@JsonProperty("name")
	private String blacklistname;
	@JsonProperty("corpid")
	private String pk_corp;
	private Integer dr;
	private DZFDateTime ts;
	
	public String getPk_blacklist() {
		return pk_blacklist;
	}

	public void setPk_blacklist(String pk_blacklist) {
		this.pk_blacklist = pk_blacklist;
	}

	public String getBlacklistname() {
		return blacklistname;
	}

	public void setBlacklistname(String blacklistname) {
		this.blacklistname = blacklistname;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
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

	@Override
	public String getPKFieldName() {
		return "pk_blacklist";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_blacklist";
	}

}
