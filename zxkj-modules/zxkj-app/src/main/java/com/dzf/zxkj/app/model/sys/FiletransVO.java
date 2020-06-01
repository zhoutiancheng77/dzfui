package com.dzf.zxkj.app.model.sys;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 资料交接台账
 * 
 * @author dzf
 *
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class FiletransVO extends SuperVO {
	@JsonProperty("pk_id")
	private String pk_filetrans; //台账主键

	@JsonProperty("corpid")
	private String pk_corp; // 所属会计公司

	@JsonProperty("corpkid")
	private String pk_corpk; // 客户公司主键

	@JsonProperty("corpkna")
	private String corpkname; // 客户公司名称(查询用)

	@JsonProperty("corpkco")
	private String corpkcode; // 客户公司编码(查询用)

	@JsonProperty("pk_file")
	private String pk_filedoc; // 资料主键

	@JsonProperty("vfcode")
	private String vfilecode; // 资料编码

	@JsonProperty("vfname")
	private String vfilename; // 资料名称

	@JsonProperty("itype")
	private Integer ifiletype; //1：实物资料，2：电子资料 【新版：0：原件；1：复印件；2：电子资料；3：打印资料】

	@JsonProperty("deposit")
	private DZFDateTime ddepositdate; // 存入时间（记录第一次存入时间）

	@JsonProperty("vkeep")
	private String vkeeperid; // 保管人（记录第一次接收人）

	@JsonProperty("vkeepna")
	private String vkeepname; // 保管人名称(查询用)

	@JsonProperty("vsta")
	private Integer vstatus; // 状态 1：在库；2：已借出；3：已归还

	@JsonProperty("dreturn")
	private DZFDateTime dreturndate; // 归还时间 （修改为最后交接时间，类型为时间戳，）

	@JsonProperty("way")
	private Integer iway; // 归还方式(1 送回、2自取、3快递)

	@JsonProperty("vhand")
	private String vhanderid; // 经手人（记录最后一次接收人，历史数据更新为保管人）

	@JsonProperty("vhandna")
	private String vhandname; // 经手人名称(查询用)

	@JsonProperty("vcust")
	private String vcustomer; // 客户方接收人  （历史数据如果归还客户，记录客户姓名，手工录入，升级有问题。）!!!

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

	private String typedesc; // 资料类型(导出用)

	private String statusdesc; // 状态(导出用)

	private String files; // 选中的资料(打印用)
	
	//此次新增字段&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
	
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
	
	@JsonProperty("vcaerid")
	private String vcatcher;//接手人
	
	@JsonProperty("vcaernm")
	private String vcaername;//接手人名称
	
	@JsonProperty("isback")
	private DZFBoolean isretback;//是否归还客户(此字段没有使用)
	
	@JsonProperty("vbsta")
	private Integer vbstatus;//状态：1：未确认；2：已确认；3：部分确认
	
	@JsonProperty("vtemsta")
	private Integer vtempstatus;//临时状态（仅作跳转资料交接判断使用）：1：已借出；2：在手；3：已归还客户；
	
	@JsonProperty("handcorp")
	private String vhandcorp;//持有人所属公司（仅作跳转资料交接判断使用）

	public String getVhandcorp() {
		return vhandcorp;
	}

	public void setVhandcorp(String vhandcorp) {
		this.vhandcorp = vhandcorp;
	}

	public Integer getVtempstatus() {
		return vtempstatus;
	}

	public void setVtempstatus(Integer vtempstatus) {
		this.vtempstatus = vtempstatus;
	}

	public Integer getVbstatus() {
		return vbstatus;
	}

	public void setVbstatus(Integer vbstatus) {
		this.vbstatus = vbstatus;
	}

	public DZFBoolean getIsretback() {
		return isretback;
	}

	public void setIsretback(DZFBoolean isretback) {
		this.isretback = isretback;
	}

	public DZFDateTime getDdepositdate() {
		return ddepositdate;
	}

	public void setDdepositdate(DZFDateTime ddepositdate) {
		this.ddepositdate = ddepositdate;
	}

	public DZFDateTime getDreturndate() {
		return dreturndate;
	}

	public void setDreturndate(DZFDateTime dreturndate) {
		this.dreturndate = dreturndate;
	}

	public String getVcatcher() {
		return vcatcher;
	}

	public void setVcatcher(String vcatcher) {
		this.vcatcher = vcatcher;
	}

	public String getVcaername() {
		return vcaername;
	}

	public void setVcaername(String vcaername) {
		this.vcaername = vcaername;
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

	public String getPk_filetrans() {
		return pk_filetrans;
	}

	public void setPk_filetrans(String pk_filetrans) {
		this.pk_filetrans = pk_filetrans;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
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

	public String getCorpkcode() {
		return corpkcode;
	}

	public void setCorpkcode(String corpkcode) {
		this.corpkcode = corpkcode;
	}

	public String getPk_filedoc() {
		return pk_filedoc;
	}

	public void setPk_filedoc(String pk_filedoc) {
		this.pk_filedoc = pk_filedoc;
	}

	public String getVfilecode() {
		return vfilecode;
	}

	public void setVfilecode(String vfilecode) {
		this.vfilecode = vfilecode;
	}

	public String getVfilename() {
		return vfilename;
	}

	public void setVfilename(String vfilename) {
		this.vfilename = vfilename;
	}

	public Integer getIfiletype() {
		return ifiletype;
	}

	public void setIfiletype(Integer ifiletype) {
		this.ifiletype = ifiletype;
	}

	public String getVkeeperid() {
		return vkeeperid;
	}

	public void setVkeeperid(String vkeeperid) {
		this.vkeeperid = vkeeperid;
	}

	public String getVkeepname() {
		return vkeepname;
	}

	public void setVkeepname(String vkeepname) {
		this.vkeepname = vkeepname;
	}

	public Integer getVstatus() {
		return vstatus;
	}

	public void setVstatus(Integer vstatus) {
		this.vstatus = vstatus;
	}

	public Integer getIway() {
		return iway;
	}

	public void setIway(Integer iway) {
		this.iway = iway;
	}

	public String getVhanderid() {
		return vhanderid;
	}

	public void setVhanderid(String vhanderid) {
		this.vhanderid = vhanderid;
	}

	public String getVhandname() {
		return vhandname;
	}

	public void setVhandname(String vhandname) {
		this.vhandname = vhandname;
	}

	public String getVcustomer() {
		return vcustomer;
	}

	public void setVcustomer(String vcustomer) {
		this.vcustomer = vcustomer;
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

	public String getTypedesc() {
		return typedesc;
	}

	public void setTypedesc(String typedesc) {
		this.typedesc = typedesc;
	}

	public String getStatusdesc() {
		return statusdesc;
	}

	public void setStatusdesc(String statusdesc) {
		this.statusdesc = statusdesc;
	}

	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_filetrans";
	}

	@Override
	public String getTableName() {
		return "ynt_filetrans";
	}
}
