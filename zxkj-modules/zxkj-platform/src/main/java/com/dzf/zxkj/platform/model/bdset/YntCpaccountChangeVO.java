package com.dzf.zxkj.platform.model.bdset;


import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;

/***
 *科目编码规则变化记录VO
 * @author asoka
 *
 */
@SuppressWarnings("rawtypes")
public class YntCpaccountChangeVO extends SuperVO {
	

	private static final long serialVersionUID = 1L;
	
	private String pk_sjwh_dataupgrade;

	/***
	 * 旧编码
	 */
	private String oldcode;
	
	/***
	 * 旧名称
	 */
	private String oldname;
	/***
	 * 新编码
	 */
	private String newcode;
	/***
	 * 新名称
	 */
	private String newname;
	/***
	 * 公司
	 */
	private String pk_corp;
	
	/***
	 * 是否发生改变
	 */
	private DZFBoolean ischanged;
	
	/***
	 * 版本
	 * 1为最新  越大数据约旧
	 */
	private Integer changeversion;
	
	/***
	 * 备注
	 */
	private String memo;
	
	private String vdef1;
	
	private String vdef2;
	
	private String vdef3;
	
	private String vdef4;
	
	private String vdef5;
	
	
	

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_sjwh_dataupgrade";
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "ynt_sjwh_dataupgrade";
	}

	public String getOldcode() {
		return oldcode;
	}

	public void setOldcode(String oldcode) {
		this.oldcode = oldcode;
	}

	public String getOldname() {
		return oldname;
	}

	public void setOldname(String oldname) {
		this.oldname = oldname;
	}

	public String getNewcode() {
		return newcode;
	}

	public void setNewcode(String newcode) {
		this.newcode = newcode;
	}

	public String getNewname() {
		return newname;
	}

	public void setNewname(String newname) {
		this.newname = newname;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public DZFBoolean getIschanged() {
		return ischanged;
	}

	public void setIschanged(DZFBoolean ischanged) {
		this.ischanged = ischanged;
	}

	public String getPk_sjwh_dataupgrade() {
		return pk_sjwh_dataupgrade;
	}

	public void setPk_sjwh_dataupgrade(String pk_sjwh_dataupgrade) {
		this.pk_sjwh_dataupgrade = pk_sjwh_dataupgrade;
	}

	public String getVdef1() {
		return vdef1;
	}

	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}

	public String getVdef2() {
		return vdef2;
	}

	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}

	public String getVdef3() {
		return vdef3;
	}

	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}

	public String getVdef4() {
		return vdef4;
	}

	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}

	public String getVdef5() {
		return vdef5;
	}

	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getChangeversion() {
		return changeversion;
	}

	public void setChangeversion(Integer changeversion) {
		this.changeversion = changeversion;
	}


	
	

}
