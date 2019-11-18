package com.dzf.zxkj.platform.model.tax.workbench;


import com.dzf.zxkj.common.model.SuperVO;

public class TaxDeclareResult extends SuperVO {
	// 状态
	private boolean success = false;
	private int status = 200;
	// 返回信息
	private String msg;
	private String pk_corp;
	// 公司名称
	private String unitname;
	// 征收项目代码
	private String zsxm_dm;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getUnitname() {
		return unitname;
	}

	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}

	public String getZsxm_dm() {
		return zsxm_dm;
	}

	public void setZsxm_dm(String zsxm_dm) {
		this.zsxm_dm = zsxm_dm;
	}

    @Override
    public String getParentPKFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPKFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTableName() {
        // TODO Auto-generated method stub
        return null;
    }
}
