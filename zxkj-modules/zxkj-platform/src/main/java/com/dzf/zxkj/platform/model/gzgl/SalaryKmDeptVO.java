package com.dzf.zxkj.platform.model.gzgl;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;

public class SalaryKmDeptVO extends SuperVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = -12061938351324290L;
	private String pk_kmdept;// 主键
	private String ckjkmid;// 科目id
	private String kjkmcode;// 科目编码(不存库)
	private String kjkmname;// 科目名称(不存库)
	private String cdeptid;// 部门id
	private String vdeptcode;// 部门编码(不存库)
	private String vdeptname;// 部门名称(不存库)
	private String memo;// 备注
	private String pk_corp;
	private DZFDateTime ts;
	private Integer dr;
	private String vdef1;// 自定义项1
	private String vdef2;// 自定义项2
	private String vdef3;// 自定义项3
	private String vdef4;// 自定义项4
	private DZFBoolean vdef5;// 自定义项5
	private DZFBoolean vdef6;// 自定义项6
	private Integer vdef7;// 自定义项7
	private Integer vdef8;// 自定义项8
	private DZFDate vdef9;// 自定义项9
	private DZFDate vdef10;// 自定义项10
	
	public String getPk_kmdept() {
		return pk_kmdept;
	}

	public String getCkjkmid() {
		return ckjkmid;
	}

	public String getKjkmcode() {
		return kjkmcode;
	}

	public String getKjkmname() {
		return kjkmname;
	}

	public String getCdeptid() {
		return cdeptid;
	}

	public String getVdeptcode() {
		return vdeptcode;
	}

	public String getVdeptname() {
		return vdeptname;
	}

	public String getMemo() {
		return memo;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public Integer getDr() {
		return dr;
	}

	public String getVdef1() {
		return vdef1;
	}

	public String getVdef2() {
		return vdef2;
	}

	public String getVdef3() {
		return vdef3;
	}

	public String getVdef4() {
		return vdef4;
	}

	public DZFBoolean getVdef5() {
		return vdef5;
	}

	public DZFBoolean getVdef6() {
		return vdef6;
	}

	public Integer getVdef7() {
		return vdef7;
	}

	public Integer getVdef8() {
		return vdef8;
	}

	public DZFDate getVdef9() {
		return vdef9;
	}

	public DZFDate getVdef10() {
		return vdef10;
	}

	public void setPk_kmdept(String pk_kmdept) {
		this.pk_kmdept = pk_kmdept;
	}

	public void setCkjkmid(String ckjkmid) {
		this.ckjkmid = ckjkmid;
	}

	public void setKjkmcode(String kjkmcode) {
		this.kjkmcode = kjkmcode;
	}

	public void setKjkmname(String kjkmname) {
		this.kjkmname = kjkmname;
	}

	public void setCdeptid(String cdeptid) {
		this.cdeptid = cdeptid;
	}

	public void setVdeptcode(String vdeptcode) {
		this.vdeptcode = vdeptcode;
	}

	public void setVdeptname(String vdeptname) {
		this.vdeptname = vdeptname;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}

	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}

	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}

	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}

	public void setVdef5(DZFBoolean vdef5) {
		this.vdef5 = vdef5;
	}

	public void setVdef6(DZFBoolean vdef6) {
		this.vdef6 = vdef6;
	}

	public void setVdef7(Integer vdef7) {
		this.vdef7 = vdef7;
	}

	public void setVdef8(Integer vdef8) {
		this.vdef8 = vdef8;
	}

	public void setVdef9(DZFDate vdef9) {
		this.vdef9 = vdef9;
	}

	public void setVdef10(DZFDate vdef10) {
		this.vdef10 = vdef10;
	}

	@Override
	public String getPKFieldName() {
		return "pk_kmdept";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_salarykmdept";
	}

}
