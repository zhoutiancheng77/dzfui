package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings({ "serial"})
public class DepartmentVO extends SuperVO<DepartmentVO> {

	@JsonProperty("pk_dept")
	private String pk_department; 	// 主键
	
	@JsonProperty("corp")
	private String pk_corp;			// 公司主键
	
	@JsonProperty("corpna")
	private String corp_name;		// 公司名称(查询用)
	
	@JsonProperty("dept_code")
	private String deptcode;		// 部门编码
	
	@JsonProperty("dept_name")
	private String deptname;		// 部门名称
	
	@JsonProperty("showOrder")
	private Integer iorder;			// 显示顺序
	
	@JsonProperty("leader")
	private String pk_leader;		// 负责人
	
	@JsonProperty("leaderna")
	private String leadername;		// 负责人名称(查询用)

	@JsonProperty("operator")
	private String coperatorid;		// 制单人
	
	@JsonProperty("operatedate")
	private DZFDate doperatedate;	// 制单日期
	
	@JsonProperty("isleaf")
	private DZFBoolean isleaf;		// 是否末级
	
	@JsonProperty("level")
	private Integer deptlevel;		// 层级
	
	@JsonProperty("memo")
	private String vmemo;			// 备注
	
	private Integer dr;

	private DZFDateTime ts;
	
	@JsonProperty("parentid")
	private String pk_parent;
	
	@JsonProperty("preset")
	private DZFBoolean ispreset;
	
	public DZFBoolean getIspreset() {
        return ispreset;
    }
    public void setIspreset(DZFBoolean ispreset) {
        this.ispreset = ispreset;
    }
    public String getPk_parent() {
        return pk_parent;
    }
    public void setPk_parent(String pk_parent) {
        this.pk_parent = pk_parent;
    }
    public String getVmemo() {
		return vmemo;
	}
	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}
	public DZFBoolean getIsleaf() {
		return isleaf;
	}
	public void setIsleaf(DZFBoolean isleaf) {
		this.isleaf = isleaf;
	}
	public Integer getDeptlevel() {
		return deptlevel;
	}
	public void setDeptlevel(Integer deptlevel) {
		this.deptlevel = deptlevel;
	}
	public String getLeadername() {
		return leadername;
	}
	public void setLeadername(String leadername) {
		this.leadername = leadername;
	}
	public String getPk_leader() {
		return pk_leader;
	}
	public void setPk_leader(String pk_leader) {
		this.pk_leader = pk_leader;
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
	public String getCorp_name() {
		return corp_name;
	}
	public void setCorp_name(String corp_name) {
		this.corp_name = corp_name;
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
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getDeptcode() {
		return deptcode;
	}
	public void setDeptcode(String deptcode) {
		this.deptcode = deptcode;
	}
	public String getDeptname() {
		return deptname;
	}
	public void setDeptname(String deptname) {
		this.deptname = deptname;
	}
	public Integer getIorder() {
		return iorder;
	}
	public void setIorder(Integer iorder) {
		this.iorder = iorder;
	}
	public String getPk_department() {
		return pk_department;
	}
	public void setPk_department(String pk_department) {
		this.pk_department = pk_department;
	}
	@Override
	public String getPKFieldName() {
		return "pk_department";
	}
	@Override
	public String getParentPKFieldName() {
		return null;
	}
	@Override
	public String getTableName() {
		return "ynt_department";
	}
}
