package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * 我的客户-角色信息表
 *
 */
public class CorpRoleVO extends SuperVO {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    @JsonProperty("croleid")
	private String pk_corprole; // 主键

	@JsonProperty("pk_gs")
	private String pk_corp; // 客户主键
	
	@JsonProperty("incode")
	private String innercode;//客户编码
	
	@JsonProperty("uname")
	private String unitname;//客户名称

	@JsonProperty("roleid")
	private String pk_role; // 角色主键     

	@JsonProperty("rname")
	private String role_name; // 角色名称
	
    @JsonProperty("rcode")
    private String role_code;//角色编码

	@JsonProperty("cuid")
	private String cuserid; // 用户主键
	
	@JsonProperty("cuname")
	private String user_name;//用户名称
	
    @JsonProperty("cucode")
    private String user_code;//用户编码
    
    @JsonProperty("deptid")
    private String pk_department;   // 部门主键
    
    @JsonProperty("dcode")
    private String deptcode;        // 部门编码
    
    @JsonProperty("dname")
    private String deptname;        // 部门名称
    
    @JsonProperty("cduid")
    private String cduserid;//转派人
    
	@JsonProperty("curid")
	private String pk_user_role;//用户角色中间表主键

	private Integer dr; // 删除标记

	private DZFDateTime ts; // 时间
	
	private String loginCorpID;

	public String getLoginCorpID() {
        return loginCorpID;
    }

    public void setLoginCorpID(String loginCorpID) {
        this.loginCorpID = loginCorpID;
    }

    public String getCduserid() {
        return cduserid;
    }

    public void setCduserid(String cduserid) {
        this.cduserid = cduserid;
    }

    public String getInnercode() {
        return innercode;
    }

    public void setInnercode(String innercode) {
        this.innercode = innercode;
    }

    public String getUnitname() {
        return unitname;
    }

    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }

    public String getRole_code() {
        return role_code;
    }

    public void setRole_code(String role_code) {
        this.role_code = role_code;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getPk_department() {
        return pk_department;
    }

    public void setPk_department(String pk_department) {
        this.pk_department = pk_department;
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

    public String getPk_corprole() {
        return pk_corprole;
    }

    public void setPk_corprole(String pk_corprole) {
        this.pk_corprole = pk_corprole;
    }

    public String getPk_role() {
        return pk_role;
    }

    public void setPk_role(String pk_role) {
        this.pk_role = pk_role;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    public String getCuserid() {
        return cuserid;
    }

    public void setCuserid(String cuserid) {
        this.cuserid = cuserid;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPk_user_role() {
        return pk_user_role;
    }

    public void setPk_user_role(String pk_user_role) {
        this.pk_user_role = pk_user_role;
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


	@Override
	public String getParentPKFieldName() {
		return "pk_corp";
	}

	@Override
	public String getPKFieldName() {
		return "pk_corprole";
	}

	@Override
	public String getTableName() {
		return "ynt_corprole";
	}
	
	public boolean equals(Object obj) {   
        if (obj instanceof CorpRoleVO) {   
            CorpRoleVO u = (CorpRoleVO) obj;   
            return this.cuserid.equals(u.getCuserid())   
                    && this.pk_role.equals(u.getPk_role());   
        }   
        return super.equals(obj); 
	}
}
