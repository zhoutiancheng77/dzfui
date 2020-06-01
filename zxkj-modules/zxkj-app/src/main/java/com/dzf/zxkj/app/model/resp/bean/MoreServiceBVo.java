package com.dzf.zxkj.app.model.resp.bean;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 更多服务
 * 
 * @author zhangj
 *
 */
public class MoreServiceBVo extends SuperVO {

	private String vprovincename;// 省
	private String vcityname;// 市
	private String vareaname;// 区
	@JsonProperty("fwxm")
	private String vbusitypename;// 服务项目
	@JsonProperty("lxdh")
	private String vphone;// 联系电话
	@JsonProperty("jdrmc")
	private String user_name;// 接单人名称
	private DZFDouble nprice;// 价格
	private String jg;// 价格
	@JsonProperty("fgsmc")
	private String unitname;// 公司名称
	@JsonProperty("tpdz")
	private String vimageurl;// 图片地址
	@JsonProperty("xq")
	private String detais;// 详情
	@JsonProperty("xlid")
	private String pk_busitype;// 小类id
	@JsonProperty("id")
	private String pk_prorelease_b;// 详情id

	private Integer iprovince;// 省ID
	private Integer icity;// 市ID
	private Integer iarea;// 区ID

	public Integer getIprovince() {
		return iprovince;
	}

	public void setIprovince(Integer iprovince) {
		this.iprovince = iprovince;
	}

	public Integer getIcity() {
		return icity;
	}

	public void setIcity(Integer icity) {
		this.icity = icity;
	}

	public Integer getIarea() {
		return iarea;
	}

	public void setIarea(Integer iarea) {
		this.iarea = iarea;
	}

	public String getPk_prorelease_b() {
		return pk_prorelease_b;
	}

	public void setPk_prorelease_b(String pk_prorelease_b) {
		this.pk_prorelease_b = pk_prorelease_b;
	}

	public String getPk_busitype() {
		return pk_busitype;
	}

	public void setPk_busitype(String pk_busitype) {
		this.pk_busitype = pk_busitype;
	}

	public String getDetais() {
		return detais;
	}

	public void setDetais(String detais) {
		this.detais = detais;
	}

	public String getVprovincename() {
		return vprovincename;
	}

	public void setVprovincename(String vprovincename) {
		this.vprovincename = vprovincename;
	}

	public String getVcityname() {
		return vcityname;
	}

	public void setVcityname(String vcityname) {
		this.vcityname = vcityname;
	}

	public String getVareaname() {
		return vareaname;
	}

	public void setVareaname(String vareaname) {
		this.vareaname = vareaname;
	}

	public String getVbusitypename() {
		return vbusitypename;
	}

	public void setVbusitypename(String vbusitypename) {
		this.vbusitypename = vbusitypename;
	}

	public String getVphone() {
		return vphone;
	}

	public void setVphone(String vphone) {
		this.vphone = vphone;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public DZFDouble getNprice() {
		return nprice;
	}

	public void setNprice(DZFDouble nprice) {
		this.nprice = nprice;
	}

	public String getJg() {
		return jg;
	}

	public void setJg(String jg) {
		this.jg = jg;
	}

	public String getUnitname() {
		return unitname;
	}

	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}

	public String getVimageurl() {
		return vimageurl;
	}

	public void setVimageurl(String vimageurl) {
		this.vimageurl = vimageurl;
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
