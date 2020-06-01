package com.dzf.zxkj.app.model.sys;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 资料交接 主表VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class FiletransHVO extends SuperVO {

	private static final long serialVersionUID = 3869152218478924277L;
	
	@JsonProperty("pk_hid")
	private String pk_filetrans_h;//主键

	@JsonProperty("corpid")
	private String pk_corp;	//主键
	
	@JsonProperty("vcode")
	private String vbillcode;//单号
	
	@JsonProperty("dttime")
	private DZFDateTime dtranstime;//交出时间
	
	@JsonProperty("vsuerid")
	private String vsurrender;//交出人
	
	@JsonProperty("vsuernm")
	private String vsuername;//交出人名称
	
	@JsonProperty("vsuercp")
	private String vsuercorp;//交出人所属公司
	
	@JsonProperty("vcaerid")
	private String vcatcher;//接手人
	
	@JsonProperty("vcaernm")
	private String vcaername;//接手人名称
	
	@JsonProperty("vcaercp")
	private String vcaercorp;//接手人所属公司
	
	@JsonProperty("isvcou")
	private DZFBoolean vcourier;//快递
	
	@JsonProperty("vcouno")
	private String vcourierno;//快递公司及单号
	
	@JsonProperty("purp")
	private String purpose;// 用途
	
	@JsonProperty("memo")
	private String vmemo;//备注	
	
	@JsonProperty("vsta")
	private Integer vstatus;//状态：1：待确认；2：已确认；3：部分确认
	
	@JsonProperty("vstanm")
	private String vstaname;//状态名称
	
	@JsonProperty("nums")
	private Integer nfilenums;//资料件数
	
	@JsonProperty("opid")
	private String coperatorid;// 录入人
	
	@JsonProperty("copernm")
	private String copername;// 录入人姓名
	
	@JsonProperty("dodate")
	private DZFDate doperatedate;// 录入日期
	
	@JsonProperty("vconid")
	private String vconfirmpsn;// 确认人
	
	@JsonProperty("vconfinm")
	private String vconfiname;// 确认人姓名
	
	@JsonProperty("dcondate")
	private DZFDateTime dconfirmdate;// 确认时间
	
	@JsonProperty("vnconfirm")
	private String vneedconfirm;//待确认人
	
	@JsonProperty("dr")
	private Integer dr;// 删除标记

	@JsonProperty("ts")
	private DZFDateTime ts;// 时间戳
	
	@JsonProperty("corpkid")
	private String pk_corpk; // 客户公司主键

	@JsonProperty("corpkna")
	private String corpkname; // 客户公司名称
	
    @JsonProperty("tstp")
    private DZFDateTime tstamp;//时间戳
    
	@JsonProperty("bdate")
	private DZFDate beginDate;//开始日期
	
	@JsonProperty("edate")
	private DZFDate endDate;//结束日期
	
	@JsonProperty("errmsg")
	private String verrmsg;//错误信息
	
	@JsonProperty("isour")
	private Integer isource;//来源： 1或空：web端生成；2：app端转交；3：app端当面交；
	
	public Integer getIsource() {
		return isource;
	}

	public void setIsource(Integer isource) {
		this.isource = isource;
	}

	public String getVerrmsg() {
		return verrmsg;
	}

	public void setVerrmsg(String verrmsg) {
		this.verrmsg = verrmsg;
	}

	public String getVsuercorp() {
		return vsuercorp;
	}

	public void setVsuercorp(String vsuercorp) {
		this.vsuercorp = vsuercorp;
	}

	public String getVcaercorp() {
		return vcaercorp;
	}

	public void setVcaercorp(String vcaercorp) {
		this.vcaercorp = vcaercorp;
	}

	public String getVneedconfirm() {
        return vneedconfirm;
    }

    public void setVneedconfirm(String vneedconfirm) {
        this.vneedconfirm = vneedconfirm;
    }

    public String getVconfiname() {
		return vconfiname;
	}

	public void setVconfiname(String vconfiname) {
		this.vconfiname = vconfiname;
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

	public String getVstaname() {
		return vstaname;
	}

	public void setVstaname(String vstaname) {
		this.vstaname = vstaname;
	}

	public DZFDateTime getTstamp() {
		return tstamp;
	}

	public void setTstamp(DZFDateTime tstamp) {
		this.tstamp = tstamp;
	}

	public String getVsuername() {
		return vsuername;
	}

	public void setVsuername(String vsuername) {
		this.vsuername = vsuername;
	}

	public String getVcaername() {
		return vcaername;
	}

	public void setVcaername(String vcaername) {
		this.vcaername = vcaername;
	}

	public String getCopername() {
		return copername;
	}

	public void setCopername(String copername) {
		this.copername = copername;
	}

	public String getPk_corpk() {
		return pk_corpk;
	}

	public void setPk_corpk(String pk_corpk) {
		this.pk_corpk = pk_corpk;
	}

	public String getCorpkname() {
		return corpkname;
	}

	public void setCorpkname(String corpkname) {
		this.corpkname = corpkname;
	}

	public String getPk_filetrans_h() {
		return pk_filetrans_h;
	}

	public void setPk_filetrans_h(String pk_filetrans_h) {
		this.pk_filetrans_h = pk_filetrans_h;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVbillcode() {
		return vbillcode;
	}

	public void setVbillcode(String vbillcode) {
		this.vbillcode = vbillcode;
	}

	public DZFDateTime getDtranstime() {
		return dtranstime;
	}

	public void setDtranstime(DZFDateTime dtranstime) {
		this.dtranstime = dtranstime;
	}

	public String getVsurrender() {
		return vsurrender;
	}

	public void setVsurrender(String vsurrender) {
		this.vsurrender = vsurrender;
	}

	public String getVcatcher() {
		return vcatcher;
	}

	public void setVcatcher(String vcatcher) {
		this.vcatcher = vcatcher;
	}

	public DZFBoolean getVcourier() {
		return vcourier;
	}

	public void setVcourier(DZFBoolean vcourier) {
		this.vcourier = vcourier;
	}

	public String getVcourierno() {
		return vcourierno;
	}

	public void setVcourierno(String vcourierno) {
		this.vcourierno = vcourierno;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public Integer getVstatus() {
		return vstatus;
	}

	public void setVstatus(Integer vstatus) {
		this.vstatus = vstatus;
	}

	public Integer getNfilenums() {
		return nfilenums;
	}

	public void setNfilenums(Integer nfilenums) {
		this.nfilenums = nfilenums;
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

	public String getVconfirmpsn() {
		return vconfirmpsn;
	}

	public void setVconfirmpsn(String vconfirmpsn) {
		this.vconfirmpsn = vconfirmpsn;
	}

	public DZFDateTime getDconfirmdate() {
		return dconfirmdate;
	}

	public void setDconfirmdate(DZFDateTime dconfirmdate) {
		this.dconfirmdate = dconfirmdate;
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
		return "pk_filetrans_h";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_filetrans_h";
	}

}
