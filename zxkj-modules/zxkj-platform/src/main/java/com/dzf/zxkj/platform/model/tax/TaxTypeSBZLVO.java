package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;

public class TaxTypeSBZLVO extends SuperVO {
	
    private String pk_taxsbzl;//主键
    private String zsxmcode;//征收项目编号
    private String zsxmname;//征收项目名称
    private String sbcode; //------申报种类编码
    private String sbname;  //-------申报种类名称
    private int sbzq; //-------申报周期
    private String qyxz;  //-------企业性质
    private String kmfa;  //-------科目方案
    private String pk_corp;  //-------公司
    private String dq;//地区说明
    private String vnote;//备注
    private int showorder;//排序
    private String vdef1;
    private String vdef2;
    private String vdef3;
    private String vdef4;
    private String vdef5;
	//时间戳	
	private DZFDateTime ts;
	//删除标志	
	private Integer dr;
	public String getPk_taxsbzl() {
		return pk_taxsbzl;
	}
	public void setPk_taxsbzl(String pk_taxsbzl) {
		this.pk_taxsbzl = pk_taxsbzl;
	}
	public String getSbcode() {
		return sbcode;
	}
	public void setSbcode(String sbcode) {
		this.sbcode = sbcode;
	}
	public String getSbname() {
		return sbname;
	}
	public void setSbname(String sbname) {
		this.sbname = sbname;
	}
	public int getSbzq() {
		return sbzq;
	}
	public void setSbzq(int sbzq) {
		this.sbzq = sbzq;
	}
	public String getQyxz() {
		return qyxz;
	}
	public void setQyxz(String qyxz) {
		this.qyxz = qyxz;
	}
	public String getKmfa() {
		return kmfa;
	}
	public void setKmfa(String kmfa) {
		this.kmfa = kmfa;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getDq() {
		return dq;
	}
	public void setDq(String dq) {
		this.dq = dq;
	}
	public String getVnote() {
		return vnote;
	}
	public void setVnote(String vnote) {
		this.vnote = vnote;
	}
	public int getShoworder() {
		return showorder;
	}
	public void setShoworder(int showorder) {
		this.showorder = showorder;
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
	public DZFDateTime getTs() {
		return ts;
	}
	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	public Integer getDr() {
		return dr;
	}
	public void setDr(Integer dr) {
		this.dr = dr;
	}
	public String getZsxmcode() {
		return zsxmcode;
	}
	public void setZsxmcode(String zsxmcode) {
		this.zsxmcode = zsxmcode;
	}
	public String getZsxmname() {
		return zsxmname;
	}
	public void setZsxmname(String zsxmname) {
		this.zsxmname = zsxmname;
	}
	@Override
	public String getPKFieldName() {
		return "pk_taxsbzl";
	}
	@Override
	public String getParentPKFieldName() {
		return null;
	}
	@Override
	public String getTableName() {
		return "ynt_tax_sbzl";
	}
}