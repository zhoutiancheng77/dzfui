package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;

//ocr银行业务类型
public class ImageBankTypeVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8169953253560291125L;

	private String vbanktypename; // 银行回单类型
	private String busitypetempname;// 业务类型名称
	private String vspstylecode;// 发票类型编码
	private String vspstylename;// 发票类型名称
	private String szstylecode;// 结算方式编码
	private String szstylename;// 结算方式名称
	private String pk_model_h;// 模板主键
	private String pk_corp;// 公司
	private Integer dr;// 删除标志
	private DZFDateTime ts;// 时间戳
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
	private String pk_image_banktype;// 主键
	private String vmemo;// 备注
	private int imatch;// 0 -----不匹配 1-----公司匹配 2-----摘要匹配 3----其他匹配

	public String getVbanktypename() {
		return vbanktypename;
	}

	public String getBusitypetempname() {
		return busitypetempname;
	}

	public String getVspstylecode() {
		return vspstylecode;
	}

	public String getVspstylename() {
		return vspstylename;
	}

	public String getSzstylecode() {
		return szstylecode;
	}

	public String getSzstylename() {
		return szstylename;
	}

	public String getPk_model_h() {
		return pk_model_h;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public Integer getDr() {
		return dr;
	}

	public DZFDateTime getTs() {
		return ts;
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

	public String getPk_image_banktype() {
		return pk_image_banktype;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVbanktypename(String vbanktypename) {
		this.vbanktypename = vbanktypename;
	}

	public void setBusitypetempname(String busitypetempname) {
		this.busitypetempname = busitypetempname;
	}

	public void setVspstylecode(String vspstylecode) {
		this.vspstylecode = vspstylecode;
	}

	public void setVspstylename(String vspstylename) {
		this.vspstylename = vspstylename;
	}

	public void setSzstylecode(String szstylecode) {
		this.szstylecode = szstylecode;
	}

	public void setSzstylename(String szstylename) {
		this.szstylename = szstylename;
	}

	public void setPk_model_h(String pk_model_h) {
		this.pk_model_h = pk_model_h;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
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

	public void setPk_image_banktype(String pk_image_banktype) {
		this.pk_image_banktype = pk_image_banktype;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public int getImatch() {
		return imatch;
	}

	public void setImatch(int imatch) {
		this.imatch = imatch;
	}

	@Override
	public String getPKFieldName() {
		return "pk_image_banktype";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_image_banktype";
	}

}
