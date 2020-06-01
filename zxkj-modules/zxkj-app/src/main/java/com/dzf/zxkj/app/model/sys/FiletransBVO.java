package com.dzf.zxkj.app.model.sys;


import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 资料交接（交接历史）
 * 
 * @author dzf
 *
 */
@SuppressWarnings("rawtypes")
public class FiletransBVO extends SuperVO {

	private static final long serialVersionUID = 7977768660608732553L;

	private final String TABLE_NAME = "ynt_filetrans_b";

	@JsonProperty("pk_bid")
	private String pk_filetrans_b; //子表主键
	
	@JsonProperty("pk_hid")
	private String pk_filetrans_h;//主表主键

	@JsonProperty("pk_id")
	private String pk_filetrans; // 资料台账主键

	@JsonProperty("corpid")
	private String pk_corp; // 所属会计公司

	@JsonProperty("ddate")
	private DZFDate ddealdate; // 操作时间

	@JsonProperty("vsta")
	private Integer vstatus; // 借/还 ( 1：借出；2：归还 ；3新增 4：归还客户)

	@JsonProperty("vhand")
	private String vhanderid; // 经手人

	@JsonProperty("vhandna")
	private String vhandname; // 经手人姓名

	@JsonProperty("purp")
	private String purpose; // 用途

	@JsonProperty("memo")
	private String vmemo; // 备注

	@JsonProperty("dr")
	private Integer dr; // 删除标记

	@JsonProperty("ts")
	private DZFDateTime ts; // 时间戳

	@JsonProperty("opid")
	private String coperatorid; // 录入人

	@JsonProperty("dodate")
	private DZFDate doperatedate; // 录入日期
	
	//此次新增字段&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
	
	@JsonProperty("corpkid")
	private String pk_corpk; // 客户公司主键
	
	@JsonProperty("corpkcd")
	private String corpkcode; // 客户公司编码

	@JsonProperty("corpkna")
	private String corpkname; // 客户公司名称
	
	@JsonProperty("pk_file")
	private String pk_filedoc; // 资料主键

	@JsonProperty("vfname")
	private String vfilename; // 资料名称
	
	@JsonProperty("vfcode")
	private String vfilecode; // 资料编码

	@JsonProperty("ftype")
	private String pk_filetype; //资料类型主键
	
	@JsonProperty("ftypenm")
	private String filetypename; //资料类型名称
	
	@JsonProperty("nums")
	private Integer nfilenums;//资料件数
	
	@JsonProperty("vbperiod")
	private String vbegperiod;//开始期间
	
	@JsonProperty("veperiod")
	private String vendperiod;//结束期间
	
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
	
	@JsonProperty("vcaercd")
	private String vcaercode;//接手人编码
	
	@JsonProperty("vcaercp")
	private String vcaercorp;//接手人所属公司
	
	@JsonProperty("vbsta")
	private Integer vbstatus;//状态：1：未确认；2：已确认；3：部分确认
	
	@JsonProperty("vstanm")
	private String vstaname;//状态名称
	
	@JsonProperty("vconid")
	private String vconfirmpsn;// 确认人
	
	@JsonProperty("dcondate")
	private DZFDateTime dconfirmdate;// 确认时间
	
	@JsonProperty("itype")
	private Integer ifiletype; // 0：原件；1：复印件；2：电子资料；3：打印资料； 
	
	@JsonProperty("vnconfirm")
	private String vneedconfirm;//待确认人
	
	@JsonProperty("vnconm")
	private String vneedconame;//待确认人姓名
	
	@JsonProperty("sdate")
	private String vshowdate;//显示日期
	
	@JsonProperty("showmemo")
	private String vshowmemo; // 展示备注（app使用）
	
	public String getVshowmemo() {
		return vshowmemo;
	}

	public void setVshowmemo(String vshowmemo) {
		this.vshowmemo = vshowmemo;
	}

	public String getVshowdate() {
		return vshowdate;
	}

	public void setVshowdate(String vshowdate) {
		this.vshowdate = vshowdate;
	}

	public String getVneedconame() {
		return vneedconame;
	}

	public void setVneedconame(String vneedconame) {
		this.vneedconame = vneedconame;
	}

	public Integer getIfiletype() {
		return ifiletype;
	}

	public void setIfiletype(Integer ifiletype) {
		this.ifiletype = ifiletype;
	}

	public String getCorpkcode() {
		return corpkcode;
	}

	public void setCorpkcode(String corpkcode) {
		this.corpkcode = corpkcode;
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

	public String getVfilecode() {
		return vfilecode;
	}

	public void setVfilecode(String vfilecode) {
		this.vfilecode = vfilecode;
	}

	public String getVconfirmpsn() {
        return vconfirmpsn;
    }

    public void setVconfirmpsn(String vconfirmpsn) {
        this.vconfirmpsn = vconfirmpsn;
    }

    public String getVneedconfirm() {
        return vneedconfirm;
    }

    public void setVneedconfirm(String vneedconfirm) {
        this.vneedconfirm = vneedconfirm;
    }

    public DZFDateTime getDconfirmdate() {
		return dconfirmdate;
	}

	public void setDconfirmdate(DZFDateTime dconfirmdate) {
		this.dconfirmdate = dconfirmdate;
	}

	public String getVstaname() {
		return vstaname;
	}

	public void setVstaname(String vstaname) {
		this.vstaname = vstaname;
	}

	public String getVcaercode() {
		return vcaercode;
	}

	public void setVcaercode(String vcaercode) {
		this.vcaercode = vcaercode;
	}

	public Integer getVbstatus() {
		return vbstatus;
	}

	public void setVbstatus(Integer vbstatus) {
		this.vbstatus = vbstatus;
	}

	public String getPk_filetrans_h() {
		return pk_filetrans_h;
	}

	public void setPk_filetrans_h(String pk_filetrans_h) {
		this.pk_filetrans_h = pk_filetrans_h;
	}

	public String getPk_filedoc() {
		return pk_filedoc;
	}

	public void setPk_filedoc(String pk_filedoc) {
		this.pk_filedoc = pk_filedoc;
	}

	public String getVfilename() {
		return vfilename;
	}

	public void setVfilename(String vfilename) {
		this.vfilename = vfilename;
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

	public String getPk_filetype() {
		return pk_filetype;
	}

	public void setPk_filetype(String pk_filetype) {
		this.pk_filetype = pk_filetype;
	}

	public String getFiletypename() {
		return filetypename;
	}

	public void setFiletypename(String filetypename) {
		this.filetypename = filetypename;
	}

	public Integer getNfilenums() {
		return nfilenums;
	}

	public void setNfilenums(Integer nfilenums) {
		this.nfilenums = nfilenums;
	}

	public String getVbegperiod() {
		return vbegperiod;
	}

	public void setVbegperiod(String vbegperiod) {
		this.vbegperiod = vbegperiod;
	}

	public String getVendperiod() {
		return vendperiod;
	}

	public void setVendperiod(String vendperiod) {
		this.vendperiod = vendperiod;
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

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getVhandname() {
		return vhandname;
	}

	public void setVhandname(String vhandname) {
		this.vhandname = vhandname;
	}

	public String getPk_filetrans_b() {
		return pk_filetrans_b;
	}

	public void setPk_filetrans_b(String pk_filetrans_b) {
		this.pk_filetrans_b = pk_filetrans_b;
	}

	public DZFDate getDdealdate() {
		return ddealdate;
	}

	public void setDdealdate(DZFDate ddealdate) {
		this.ddealdate = ddealdate;
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

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public String getPk_filetrans() {
		return pk_filetrans;
	}

	public void setPk_filetrans(String pk_filetrans) {
		this.pk_filetrans = pk_filetrans;
	}

	public Integer getVstatus() {
		return vstatus;
	}

	public void setVstatus(Integer vstatus) {
		this.vstatus = vstatus;
	}

	public String getVhanderid() {
		return vhanderid;
	}

	public void setVhanderid(String vhanderid) {
		this.vhanderid = vhanderid;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_filetrans_h";
	}

	@Override
	public String getPKFieldName() {
		return "pk_filetrans_b";
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

}
