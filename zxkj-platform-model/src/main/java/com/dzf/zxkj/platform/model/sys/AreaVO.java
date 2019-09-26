package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 区域vo信息
 * 
 * @author zhangj
 *
 */
public class AreaVO extends SuperVO {

	@JsonProperty("vaname")
	private String vareaname;// 区域名称(全称)
	private Integer dr;// 删除标记
	private DZFDateTime ts;// 时间戳
	@JsonProperty("oid")
	private String coperatorid;// 审核人
	@JsonProperty("odate")
	private DZFDate doperatedate;// 审核日期
	@JsonProperty("is")
	private DZFBoolean iservice;// 开通服务

	private Integer region_id;
	private String region_code;
	private String region_name;
	private Integer parenter_id;
	private Integer region_level;
	private Integer region_order;
	private String region_name_en;
	private String region_shortname_en;
	private String pk_corp;
	private DZFBoolean isbaoshui;//是否报税地区

	public Integer getRegion_id() {
		return region_id;
	}

	public void setRegion_id(Integer region_id) {
		this.region_id = region_id;
	}

	public String getRegion_code() {
		return region_code;
	}

	public void setRegion_code(String region_code) {
		this.region_code = region_code;
	}

	public String getRegion_name() {
		return region_name;
	}

	public void setRegion_name(String region_name) {
		this.region_name = region_name;
	}

	public Integer getParenter_id() {
		return parenter_id;
	}

	public void setParenter_id(Integer parenter_id) {
		this.parenter_id = parenter_id;
	}

	public Integer getRegion_level() {
		return region_level;
	}

	public void setRegion_level(Integer region_level) {
		this.region_level = region_level;
	}

	public Integer getRegion_order() {
		return region_order;
	}

	public void setRegion_order(Integer region_order) {
		this.region_order = region_order;
	}

	public String getRegion_name_en() {
		return region_name_en;
	}

	public void setRegion_name_en(String region_name_en) {
		this.region_name_en = region_name_en;
	}

	public String getRegion_shortname_en() {
		return region_shortname_en;
	}

	public void setRegion_shortname_en(String region_shortname_en) {
		this.region_shortname_en = region_shortname_en;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVareaname() {
		return vareaname;
	}

	public void setVareaname(String vareaname) {
		this.vareaname = vareaname;
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

	public DZFBoolean getIservice() {
		return iservice;
	}

	public void setIservice(DZFBoolean iservice) {
		this.iservice = iservice;
	}
	
	public DZFBoolean getIsbaoshui() {
		return isbaoshui;
	}

	public void setIsbaoshui(DZFBoolean isbaoshui) {
		this.isbaoshui = isbaoshui;
	}

	@Override
	public String getPKFieldName() {
		return "region_id";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_area";
	}

}
