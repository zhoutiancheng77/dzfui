package com.dzf.zxkj.platform.model.gzgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;

public class SalaryBaseVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1206193835137544290L;

	private String pk_sqlarybase;// 主键
	private String pk_corp;// 公司
	private Integer dr;// 删除标志
	private String ts;// 时间戳
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
	private String vmemo;// 备注
	private String cpersonid;// 人员id
	private String ygbm;
	private String zjlx;
	private String zjbm;
	private String ygname;
	private String qj;// 期间
	private DZFDouble yfbx_js;// 养老保险基数
	private DZFDouble yfbx_bl;// 养老保险比例
	private DZFDouble yfbx_mny;// 养老保险固额
	private DZFDouble qyyfbx_bl;// 养老保险企业比例
	private DZFDouble ylbx_js;// 医疗保险基数
	private DZFDouble ylbx_bl;// 医疗保险比例
	private DZFDouble ylbx_mny;// 医疗保险固额
	private DZFDouble qyylbx_bl;// 医疗保险企业比例
	private DZFDouble sybx_js;// 失业保险基数
	private DZFDouble sybx_bl;// 失业保险比例
	private DZFDouble sybx_mny;// 失业保险固额
	private DZFDouble qysybx_bl;// 失业保险企业比例
	private DZFDouble gjj_js;// 公积金基数
	private DZFDouble gjj_bl;// 公积金比例
	private DZFDouble gjj_mny;// 公积金固额
	private DZFDouble qygjj_bl;// 公积金企业比例
	private DZFDouble gsbx_js;//工伤保险基数
	private DZFDouble qygsbx_bl;//工伤保险比例
	private DZFDouble shybx_js;//生育保险基数
	private DZFDouble qyshybx_bl;//生育保险比例

	public String getPk_sqlarybase() {
		return pk_sqlarybase;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public Integer getDr() {
		return dr;
	}

	public String getTs() {
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

	public String getVmemo() {
		return vmemo;
	}

	public String getCpersonid() {
		return cpersonid;
	}

	public String getQj() {
		return qj;
	}

	public DZFDouble getYfbx_js() {
		return yfbx_js;
	}

	public DZFDouble getYfbx_bl() {
		return yfbx_bl;
	}

	public DZFDouble getYfbx_mny() {
		return yfbx_mny;
	}

	public DZFDouble getQyyfbx_bl() {
		return qyyfbx_bl;
	}

	public DZFDouble getYlbx_js() {
		return ylbx_js;
	}

	public DZFDouble getYlbx_bl() {
		return ylbx_bl;
	}

	public DZFDouble getYlbx_mny() {
		return ylbx_mny;
	}

	public DZFDouble getQyylbx_bl() {
		return qyylbx_bl;
	}

	public DZFDouble getSybx_js() {
		return sybx_js;
	}

	public DZFDouble getSybx_bl() {
		return sybx_bl;
	}

	public DZFDouble getSybx_mny() {
		return sybx_mny;
	}

	public DZFDouble getQysybx_bl() {
		return qysybx_bl;
	}

	public DZFDouble getGjj_js() {
		return gjj_js;
	}

	public DZFDouble getGjj_bl() {
		return gjj_bl;
	}

	public DZFDouble getGjj_mny() {
		return gjj_mny;
	}

	public DZFDouble getQygjj_bl() {
		return qygjj_bl;
	}

	public DZFDouble getQygsbx_bl() {
		return qygsbx_bl;
	}

	public DZFDouble getQyshybx_bl() {
		return qyshybx_bl;
	}

	public void setPk_sqlarybase(String pk_sqlarybase) {
		this.pk_sqlarybase = pk_sqlarybase;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public void setTs(String ts) {
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

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public void setCpersonid(String cpersonid) {
		this.cpersonid = cpersonid;
	}

	public void setQj(String qj) {
		this.qj = qj;
	}

	public void setYfbx_js(DZFDouble yfbx_js) {
		this.yfbx_js = yfbx_js;
	}

	public void setYfbx_bl(DZFDouble yfbx_bl) {
		this.yfbx_bl = yfbx_bl;
	}

	public void setYfbx_mny(DZFDouble yfbx_mny) {
		this.yfbx_mny = yfbx_mny;
	}

	public void setQyyfbx_bl(DZFDouble qyyfbx_bl) {
		this.qyyfbx_bl = qyyfbx_bl;
	}

	public void setYlbx_js(DZFDouble ylbx_js) {
		this.ylbx_js = ylbx_js;
	}

	public void setYlbx_bl(DZFDouble ylbx_bl) {
		this.ylbx_bl = ylbx_bl;
	}

	public void setYlbx_mny(DZFDouble ylbx_mny) {
		this.ylbx_mny = ylbx_mny;
	}

	public void setQyylbx_bl(DZFDouble qyylbx_bl) {
		this.qyylbx_bl = qyylbx_bl;
	}

	public void setSybx_js(DZFDouble sybx_js) {
		this.sybx_js = sybx_js;
	}

	public void setSybx_bl(DZFDouble sybx_bl) {
		this.sybx_bl = sybx_bl;
	}

	public void setSybx_mny(DZFDouble sybx_mny) {
		this.sybx_mny = sybx_mny;
	}

	public void setQysybx_bl(DZFDouble qysybx_bl) {
		this.qysybx_bl = qysybx_bl;
	}

	public void setGjj_js(DZFDouble gjj_js) {
		this.gjj_js = gjj_js;
	}

	public void setGjj_bl(DZFDouble gjj_bl) {
		this.gjj_bl = gjj_bl;
	}

	public void setGjj_mny(DZFDouble gjj_mny) {
		this.gjj_mny = gjj_mny;
	}

	public void setQygjj_bl(DZFDouble qygjj_bl) {
		this.qygjj_bl = qygjj_bl;
	}

	public void setQygsbx_bl(DZFDouble qygsbx_bl) {
		this.qygsbx_bl = qygsbx_bl;
	}

	public void setQyshybx_bl(DZFDouble qyshybx_bl) {
		this.qyshybx_bl = qyshybx_bl;
	}

	public DZFDouble getGsbx_js() {
		return gsbx_js;
	}

	public DZFDouble getShybx_js() {
		return shybx_js;
	}

	public void setGsbx_js(DZFDouble gsbx_js) {
		this.gsbx_js = gsbx_js;
	}

	public void setShybx_js(DZFDouble shybx_js) {
		this.shybx_js = shybx_js;
	}
	
	public String getYgbm() {
		return ygbm;
	}

	public String getZjlx() {
		return zjlx;
	}

	public String getZjbm() {
		return zjbm;
	}

	public String getYgname() {
		return ygname;
	}

	public void setYgbm(String ygbm) {
		this.ygbm = ygbm;
	}

	public void setZjlx(String zjlx) {
		this.zjlx = zjlx;
	}

	public void setZjbm(String zjbm) {
		this.zjbm = zjbm;
	}

	public void setYgname(String ygname) {
		this.ygname = ygname;
	}

	@Override
	public String getPKFieldName() {
		return "pk_sqlarybase";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_salarybase";
	}

}
