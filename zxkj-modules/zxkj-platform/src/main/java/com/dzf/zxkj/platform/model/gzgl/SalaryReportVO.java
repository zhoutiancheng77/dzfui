package com.dzf.zxkj.platform.model.gzgl;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

public class SalaryReportVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String pk_salaryreport;
	private String pk_corp;
	private String qj;
	private Integer dr;
	private String ygbm;
	private String zjlx;
	private String zjbm;
	private String ygname;
	private String cpersonid;// 员工主键
	private DZFDouble yfgz;
	private DZFDouble yanglaobx;
	private DZFDouble yiliaobx;
	private DZFDouble shiyebx;
	private DZFDouble zfgjj;
	private DZFDouble zxkcxj;//专项扣除小计
	private DZFDouble ynssde;
	private DZFDouble shuilv;
	private DZFDouble grsds;
	private DZFDouble sfgz;
	private String coperatorid;// 制单人
	private String cdeptid;// 部门
	private String vdeptname;// 部门名称
	private String fykmid;// 费用科目id
	private String fykmname;// 费用科目名称
	private String fykmcode;// 费用科目编码
	private String fullname;// 费用科目全称
	private Integer fykmkind;// 费用科目业签
	private String vproject;// 费用科目全称

	private String ryzt;// 人员状态 正常 不正常
	private String vphone;// 电话号码
	private String billtype;// 工资表类型
	private int impmodeltype;// 导入模板类型
	private String varea;// 地区
	private String lhtype;// 来华适用公式
	private DZFDate lhdate;// 来华时间
	private String sfyg;// 是否残疾烈属孤老
	private String sfgy;// 是否雇员

	private DZFDouble znjyzc;// 子女教育支出
	private DZFDouble jxjyzc;// 继续教育支出
	private DZFDouble zfdkzc;// 住房贷款利息支出
	private DZFDouble zfzjzc;// 住房租金支出
	private DZFDouble sylrzc;// 赡养老人支出
	private DZFDouble zxkcfjxj;// 专项扣除附加小计
	private DZFDouble ljsre;// 累计收入额
	private DZFDouble ljznjyzc;// 累计子女教育支出
	private DZFDouble ljjxjyzc;// 累计继续教育支出
	private DZFDouble ljzfdkzc;// 累计住房贷款利息支出
	private DZFDouble ljzfzjzc;// 累计住房租金支出
	private DZFDouble ljsylrzc;// 累计赡养老人支出
	private DZFDouble ljynse;// 累计应纳税额
	private DZFDouble yyjse;// 已预缴税额
	private DZFDouble ljjcfy;// 累计减除费用(5000)
	private DZFDouble ljzxkc;// 累计专项扣除(五险一金)

	// 养老保险基数/比例
	private DZFDouble yfbx_js;
	private DZFDouble yfbx_bl;
	private DZFDouble yfbx_mny;
	private DZFDouble qyyfbx_bl;
	// 医疗保险基数/比例
	private DZFDouble ylbx_js;
	private DZFDouble ylbx_bl;
	private DZFDouble ylbx_mny;
	private DZFDouble qyylbx_bl;
	// 失业保险基数/比例
	private DZFDouble sybx_js;
	private DZFDouble sybx_bl;
	private DZFDouble sybx_mny;
	private DZFDouble qysybx_bl;
	// 公积金基数/比例
	private DZFDouble gjj_js;
	private DZFDouble gjj_bl;
	private DZFDouble gjj_mny;
	private DZFDouble qygjj_bl;

	private DZFDouble gsbx_js;//工伤保险基数
	private DZFDouble qygsbx_bl;//工伤保险比例
	private DZFDouble shybx_js;//生育保险基数
	private DZFDouble qyshybx_bl;//生育保险比例
	private DZFDateTime ts;
	
	//----------企业承担费用-----------
	private DZFDouble qyyanglaobx;//企业养老保险
	private DZFDouble qyyiliaobx;//企业医疗保险
	private DZFDouble qyshiyebx;//企业失业保险
	private DZFDouble qyzfgjj;//企业住房公积金
	private DZFDouble qygsbx;//企业工伤保险
	private DZFDouble qyshybx;//企业生育保险

	// --------------加密字段-----------
	private String yfgz_en;
	private String yanglaobx_en;
	private String yiliaobx_en;
	private String shiyebx_en;
	private String zfgjj_en;
	private String ynssde_en;
	private String shuilv_en;
	private String grsds_en;
	private String sfgz_en;
	private String zjbm_en;
	private String ygname_en;

	private String znjyzc_en;// 子女教育支出
	private String jxjyzc_en;// 继续教育支出
	private String zfdkzc_en;// 住房贷款利息支出
	private String zfzjzc_en;// 住房租金支出
	private String sylrzc_en;// 赡养老人支出
	private String ljsre_en;// 累计收入额
	private String ljznjyzc_en;// 累计子女教育支出
	private String ljjxjyzc_en;// 累计继续教育支出
	private String ljzfdkzc_en;// 累计住房贷款利息支出
	private String ljzfzjzc_en;// 累计住房租金支出
	private String ljsylrzc_en;// 累计赡养老人支出
	private String ljynse_en;// 累计应纳税额
	private String yyjse_en;// 已预缴税额
	private String ljjcfy_en;// 累计减除费用(5000)
	private String ljzxkc_en;// 累计专项扣除(五险一金)
	
	private String qyyanglaobx_en;//企业养老保险
	private String qyyiliaobx_en;//企业医疗保险
	private String qyshiyebx_en;//企业失业保险
	private String qyzfgjj_en;//企业住房公积金
	private String qygsbx_en;//企业工伤保险
	private String qyshybx_en;//企业生育保险

	// 预留字段
	private String vdef1;// 任职受雇日期
	private String vdef2;// 离职日期
	private String vdef3;// 出生日期
	private String vdef4;// 性别
	private String vdef5;// 出生国家(地区)
	private String vdef21;// 首次入境时间
	private String vdef22;// 预计离境时间
	private String vdef23;
	private String vdef24;
	private String vdef25;

	private DZFDouble vdef6;
	private DZFDouble vdef7;
	private DZFDouble vdef8;
	private DZFDouble vdef9;
	private DZFDouble vdef10;

	private DZFDate vdef11;
	private DZFDate vdef12;
	private DZFDate vdef13;

	private DZFBoolean vdef14;
	private DZFBoolean vdef15;
	private DZFBoolean vdef16;
	
	//---------不存库------------------
	private Integer sffc;// 添加封存标识

	public String getZjbm_en() {
		return zjbm_en;
	}

	public void setZjbm_en(String zjbm_en) {
		this.zjbm_en = zjbm_en;
	}

	public String getYgname_en() {
		return ygname_en;
	}

	public void setYgname_en(String ygname_en) {
		this.ygname_en = ygname_en;
	}

	public String getYfgz_en() {
		return yfgz_en;
	}

	public void setYfgz_en(String yfgz_en) {
		this.yfgz_en = yfgz_en;
	}

	public String getYanglaobx_en() {
		return yanglaobx_en;
	}

	public void setYanglaobx_en(String yanglaobx_en) {
		this.yanglaobx_en = yanglaobx_en;
	}

	public String getYiliaobx_en() {
		return yiliaobx_en;
	}

	public void setYiliaobx_en(String yiliaobx_en) {
		this.yiliaobx_en = yiliaobx_en;
	}

	public String getShiyebx_en() {
		return shiyebx_en;
	}

	public void setShiyebx_en(String shiyebx_en) {
		this.shiyebx_en = shiyebx_en;
	}

	public String getZfgjj_en() {
		return zfgjj_en;
	}

	public void setZfgjj_en(String zfgjj_en) {
		this.zfgjj_en = zfgjj_en;
	}

	public String getYnssde_en() {
		return ynssde_en;
	}

	public void setYnssde_en(String ynssde_en) {
		this.ynssde_en = ynssde_en;
	}

	public String getShuilv_en() {
		return shuilv_en;
	}

	public void setShuilv_en(String shuilv_en) {
		this.shuilv_en = shuilv_en;
	}

	public String getGrsds_en() {
		return grsds_en;
	}

	public void setGrsds_en(String grsds_en) {
		this.grsds_en = grsds_en;
	}

	public String getSfgz_en() {
		return sfgz_en;
	}

	public void setSfgz_en(String sfgz_en) {
		this.sfgz_en = sfgz_en;
	}

	public String getVphone() {
		return vphone;
	}

	public void setVphone(String vphone) {
		this.vphone = vphone;
	}

	public String getRyzt() {
		return ryzt;
	}

	public void setRyzt(String ryzt) {
		this.ryzt = ryzt;
	}

	public String getFykmcode() {
		return fykmcode;
	}

	public void setFykmcode(String fykmcode) {
		this.fykmcode = fykmcode;
	}

	public Integer getFykmkind() {
		return fykmkind;
	}

	public void setFykmkind(Integer fykmkind) {
		this.fykmkind = fykmkind;
	}

	public String getFykmid() {
		return fykmid;
	}

	public void setFykmid(String fykmid) {
		this.fykmid = fykmid;
	}

	public String getFykmname() {
		return fykmname;
	}

	public void setFykmname(String fykmname) {
		this.fykmname = fykmname;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public String getPk_salaryreport() {
		return pk_salaryreport;
	}

	public void setPk_salaryreport(String pk_salaryreport) {
		this.pk_salaryreport = pk_salaryreport;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getQj() {
		return qj;
	}

	public void setQj(String qj) {
		this.qj = qj;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getYgbm() {
		return ygbm;
	}

	public void setYgbm(String ygbm) {
		this.ygbm = ygbm;
	}

	public String getZjlx() {
		return zjlx;
	}

	public void setZjlx(String zjlx) {
		this.zjlx = zjlx;
	}

	public String getZjbm() {
		return zjbm;
	}

	public void setZjbm(String zjbm) {
		this.zjbm = zjbm;
	}

	public String getYgname() {
		return ygname;
	}

	public void setYgname(String ygname) {
		this.ygname = ygname;
	}

	public DZFDouble getYfgz() {
		return yfgz;
	}

	public void setYfgz(DZFDouble yfgz) {
		this.yfgz = yfgz;
	}

	public DZFDouble getYanglaobx() {
		return yanglaobx;
	}

	public void setYanglaobx(DZFDouble yanglaobx) {
		this.yanglaobx = yanglaobx;
	}

	public DZFDouble getYiliaobx() {
		return yiliaobx;
	}

	public void setYiliaobx(DZFDouble yiliaobx) {
		this.yiliaobx = yiliaobx;
	}

	public DZFDouble getShiyebx() {
		return shiyebx;
	}

	public void setShiyebx(DZFDouble shiyebx) {
		this.shiyebx = shiyebx;
	}

	public DZFDouble getZfgjj() {
		return zfgjj;
	}

	public void setZfgjj(DZFDouble zfgjj) {
		this.zfgjj = zfgjj;
	}

	public DZFDouble getYnssde() {
		return ynssde;
	}

	public void setYnssde(DZFDouble ynssde) {
		this.ynssde = ynssde;
	}

	public DZFDouble getShuilv() {
		return shuilv;
	}

	public void setShuilv(DZFDouble shuilv) {
		this.shuilv = shuilv;
	}

	public DZFDouble getGrsds() {
		return grsds;
	}

	public void setGrsds(DZFDouble grsds) {
		this.grsds = grsds;
	}

	public DZFDouble getSfgz() {
		return sfgz;
	}

	public void setSfgz(DZFDouble sfgz) {
		this.sfgz = sfgz;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public DZFDouble getYfbx_js() {
		return yfbx_js;
	}

	public void setYfbx_js(DZFDouble yfbx_js) {
		this.yfbx_js = yfbx_js;
	}

	public DZFDouble getYfbx_bl() {
		return yfbx_bl;
	}

	public void setYfbx_bl(DZFDouble yfbx_bl) {
		this.yfbx_bl = yfbx_bl;
	}

	public DZFDouble getYlbx_js() {
		return ylbx_js;
	}

	public void setYlbx_js(DZFDouble ylbx_js) {
		this.ylbx_js = ylbx_js;
	}

	public DZFDouble getYlbx_bl() {
		return ylbx_bl;
	}

	public void setYlbx_bl(DZFDouble ylbx_bl) {
		this.ylbx_bl = ylbx_bl;
	}

	public DZFDouble getSybx_js() {
		return sybx_js;
	}

	public void setSybx_js(DZFDouble sybx_js) {
		this.sybx_js = sybx_js;
	}

	public DZFDouble getSybx_bl() {
		return sybx_bl;
	}

	public void setSybx_bl(DZFDouble sybx_bl) {
		this.sybx_bl = sybx_bl;
	}

	public DZFDouble getGjj_js() {
		return gjj_js;
	}

	public void setGjj_js(DZFDouble gjj_js) {
		this.gjj_js = gjj_js;
	}

	public DZFDouble getGjj_bl() {
		return gjj_bl;
	}

	public void setGjj_bl(DZFDouble gjj_bl) {
		this.gjj_bl = gjj_bl;
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

	public String getVdef5() {
		return vdef5;
	}

	public DZFDouble getVdef6() {
		return vdef6;
	}

	public DZFDouble getVdef7() {
		return vdef7;
	}

	public DZFDouble getVdef8() {
		return vdef8;
	}

	public DZFDouble getVdef9() {
		return vdef9;
	}

	public DZFDouble getVdef10() {
		return vdef10;
	}

	public DZFDate getVdef11() {
		return vdef11;
	}

	public DZFDate getVdef12() {
		return vdef12;
	}

	public DZFDate getVdef13() {
		return vdef13;
	}

	public DZFBoolean getVdef14() {
		return vdef14;
	}

	public DZFBoolean getVdef15() {
		return vdef15;
	}

	public DZFBoolean getVdef16() {
		return vdef16;
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

	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}

	public void setVdef6(DZFDouble vdef6) {
		this.vdef6 = vdef6;
	}

	public void setVdef7(DZFDouble vdef7) {
		this.vdef7 = vdef7;
	}

	public void setVdef8(DZFDouble vdef8) {
		this.vdef8 = vdef8;
	}

	public void setVdef9(DZFDouble vdef9) {
		this.vdef9 = vdef9;
	}

	public void setVdef10(DZFDouble vdef10) {
		this.vdef10 = vdef10;
	}

	public void setVdef11(DZFDate vdef11) {
		this.vdef11 = vdef11;
	}

	public void setVdef12(DZFDate vdef12) {
		this.vdef12 = vdef12;
	}

	public void setVdef13(DZFDate vdef13) {
		this.vdef13 = vdef13;
	}

	public void setVdef14(DZFBoolean vdef14) {
		this.vdef14 = vdef14;
	}

	public void setVdef15(DZFBoolean vdef15) {
		this.vdef15 = vdef15;
	}

	public void setVdef16(DZFBoolean vdef16) {
		this.vdef16 = vdef16;
	}

	public String getCdeptid() {
		return cdeptid;
	}

	public String getVdeptname() {
		return vdeptname;
	}

	public void setCdeptid(String cdeptid) {
		this.cdeptid = cdeptid;
	}

	public void setVdeptname(String vdeptname) {
		this.vdeptname = vdeptname;
	}

	public String getBilltype() {
		return billtype;
	}

	public void setBilltype(String billtype) {
		this.billtype = billtype;
	}

	public int getImpmodeltype() {
		return impmodeltype;
	}

	public void setImpmodeltype(int impmodeltype) {
		this.impmodeltype = impmodeltype;
	}

	public String getVproject() {
		return vproject;
	}

	public void setVproject(String vproject) {
		this.vproject = vproject;
	}

	public String getVarea() {
		return varea;
	}

	public void setVarea(String varea) {
		this.varea = varea;
	}

	public String getLhtype() {
		return lhtype;
	}

	public void setLhtype(String lhtype) {
		this.lhtype = lhtype;
	}

	public String getSfyg() {
		return sfyg;
	}

	public String getSfgy() {
		return sfgy;
	}

	public void setSfyg(String sfyg) {
		this.sfyg = sfyg;
	}

	public void setSfgy(String sfgy) {
		this.sfgy = sfgy;
	}

	public String getCpersonid() {
		return cpersonid;
	}

	public void setCpersonid(String cpersonid) {
		this.cpersonid = cpersonid;
	}

	public DZFDate getLhdate() {
		return lhdate;
	}

	public void setLhdate(DZFDate lhdate) {
		this.lhdate = lhdate;
	}

	public DZFDouble getYfbx_mny() {
		return yfbx_mny;
	}

	public DZFDouble getYlbx_mny() {
		return ylbx_mny;
	}

	public DZFDouble getSybx_mny() {
		return sybx_mny;
	}

	public DZFDouble getGjj_mny() {
		return gjj_mny;
	}

	public void setYfbx_mny(DZFDouble yfbx_mny) {
		this.yfbx_mny = yfbx_mny;
	}

	public void setYlbx_mny(DZFDouble ylbx_mny) {
		this.ylbx_mny = ylbx_mny;
	}

	public void setSybx_mny(DZFDouble sybx_mny) {
		this.sybx_mny = sybx_mny;
	}

	public void setGjj_mny(DZFDouble gjj_mny) {
		this.gjj_mny = gjj_mny;
	}

	public String getZnjyzc_en() {
		return znjyzc_en;
	}

	public String getJxjyzc_en() {
		return jxjyzc_en;
	}

	public String getZfdkzc_en() {
		return zfdkzc_en;
	}

	public String getZfzjzc_en() {
		return zfzjzc_en;
	}

	public String getSylrzc_en() {
		return sylrzc_en;
	}

	public String getLjsre_en() {
		return ljsre_en;
	}

	public String getLjznjyzc_en() {
		return ljznjyzc_en;
	}

	public String getLjjxjyzc_en() {
		return ljjxjyzc_en;
	}

	public String getLjzfdkzc_en() {
		return ljzfdkzc_en;
	}

	public String getLjzfzjzc_en() {
		return ljzfzjzc_en;
	}

	public String getLjsylrzc_en() {
		return ljsylrzc_en;
	}

	public String getLjynse_en() {
		return ljynse_en;
	}

	public String getYyjse_en() {
		return yyjse_en;
	}

	public DZFDouble getZnjyzc() {
		return znjyzc;
	}

	public DZFDouble getJxjyzc() {
		return jxjyzc;
	}

	public DZFDouble getZfdkzc() {
		return zfdkzc;
	}

	public DZFDouble getZfzjzc() {
		return zfzjzc;
	}

	public DZFDouble getSylrzc() {
		return sylrzc;
	}

	public DZFDouble getLjsre() {
		return ljsre;
	}

	public DZFDouble getLjznjyzc() {
		return ljznjyzc;
	}

	public DZFDouble getLjjxjyzc() {
		return ljjxjyzc;
	}

	public DZFDouble getLjzfdkzc() {
		return ljzfdkzc;
	}

	public DZFDouble getLjzfzjzc() {
		return ljzfzjzc;
	}

	public DZFDouble getLjsylrzc() {
		return ljsylrzc;
	}

	public DZFDouble getLjynse() {
		return ljynse;
	}

	public DZFDouble getYyjse() {
		return yyjse;
	}

	public void setZnjyzc_en(String znjyzc_en) {
		this.znjyzc_en = znjyzc_en;
	}

	public void setJxjyzc_en(String jxjyzc_en) {
		this.jxjyzc_en = jxjyzc_en;
	}

	public void setZfdkzc_en(String zfdkzc_en) {
		this.zfdkzc_en = zfdkzc_en;
	}

	public void setZfzjzc_en(String zfzjzc_en) {
		this.zfzjzc_en = zfzjzc_en;
	}

	public void setSylrzc_en(String sylrzc_en) {
		this.sylrzc_en = sylrzc_en;
	}

	public void setLjsre_en(String ljsre_en) {
		this.ljsre_en = ljsre_en;
	}

	public void setLjznjyzc_en(String ljznjyzc_en) {
		this.ljznjyzc_en = ljznjyzc_en;
	}

	public void setLjjxjyzc_en(String ljjxjyzc_en) {
		this.ljjxjyzc_en = ljjxjyzc_en;
	}

	public void setLjzfdkzc_en(String ljzfdkzc_en) {
		this.ljzfdkzc_en = ljzfdkzc_en;
	}

	public void setLjzfzjzc_en(String ljzfzjzc_en) {
		this.ljzfzjzc_en = ljzfzjzc_en;
	}

	public void setLjsylrzc_en(String ljsylrzc_en) {
		this.ljsylrzc_en = ljsylrzc_en;
	}

	public void setLjynse_en(String ljynse_en) {
		this.ljynse_en = ljynse_en;
	}

	public void setYyjse_en(String yyjse_en) {
		this.yyjse_en = yyjse_en;
	}

	public void setZnjyzc(DZFDouble znjyzc) {
		this.znjyzc = znjyzc;
	}

	public void setJxjyzc(DZFDouble jxjyzc) {
		this.jxjyzc = jxjyzc;
	}

	public void setZfdkzc(DZFDouble zfdkzc) {
		this.zfdkzc = zfdkzc;
	}

	public void setZfzjzc(DZFDouble zfzjzc) {
		this.zfzjzc = zfzjzc;
	}

	public void setSylrzc(DZFDouble sylrzc) {
		this.sylrzc = sylrzc;
	}

	public void setLjsre(DZFDouble ljsre) {
		this.ljsre = ljsre;
	}

	public void setLjznjyzc(DZFDouble ljznjyzc) {
		this.ljznjyzc = ljznjyzc;
	}

	public void setLjjxjyzc(DZFDouble ljjxjyzc) {
		this.ljjxjyzc = ljjxjyzc;
	}

	public void setLjzfdkzc(DZFDouble ljzfdkzc) {
		this.ljzfdkzc = ljzfdkzc;
	}

	public void setLjzfzjzc(DZFDouble ljzfzjzc) {
		this.ljzfzjzc = ljzfzjzc;
	}

	public void setLjsylrzc(DZFDouble ljsylrzc) {
		this.ljsylrzc = ljsylrzc;
	}

	public void setLjynse(DZFDouble ljynse) {
		this.ljynse = ljynse;
	}

	public void setYyjse(DZFDouble yyjse) {
		this.yyjse = yyjse;
	}

	public DZFDouble getLjjcfy() {
		return ljjcfy;
	}

	public DZFDouble getLjzxkc() {
		return ljzxkc;
	}

	public String getLjjcfy_en() {
		return ljjcfy_en;
	}

	public String getLjzxkc_en() {
		return ljzxkc_en;
	}

	public void setLjjcfy(DZFDouble ljjcfy) {
		this.ljjcfy = ljjcfy;
	}

	public void setLjzxkc(DZFDouble ljzxkc) {
		this.ljzxkc = ljzxkc;
	}

	public void setLjjcfy_en(String ljjcfy_en) {
		this.ljjcfy_en = ljjcfy_en;
	}

	public void setLjzxkc_en(String ljzxkc_en) {
		this.ljzxkc_en = ljzxkc_en;
	}

	public String getVdef21() {
		return vdef21;
	}

	public String getVdef22() {
		return vdef22;
	}

	public String getVdef23() {
		return vdef23;
	}

	public String getVdef24() {
		return vdef24;
	}

	public String getVdef25() {
		return vdef25;
	}

	public void setVdef21(String vdef21) {
		this.vdef21 = vdef21;
	}

	public void setVdef22(String vdef22) {
		this.vdef22 = vdef22;
	}

	public void setVdef23(String vdef23) {
		this.vdef23 = vdef23;
	}

	public void setVdef24(String vdef24) {
		this.vdef24 = vdef24;
	}

	public void setVdef25(String vdef25) {
		this.vdef25 = vdef25;
	}

	public DZFDouble getQyyfbx_bl() {
		return qyyfbx_bl;
	}

	public DZFDouble getQyylbx_bl() {
		return qyylbx_bl;
	}

	public DZFDouble getQysybx_bl() {
		return qysybx_bl;
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

	public void setQyyfbx_bl(DZFDouble qyyfbx_bl) {
		this.qyyfbx_bl = qyyfbx_bl;
	}

	public void setQyylbx_bl(DZFDouble qyylbx_bl) {
		this.qyylbx_bl = qyylbx_bl;
	}

	public void setQysybx_bl(DZFDouble qysybx_bl) {
		this.qysybx_bl = qysybx_bl;
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
	
	public DZFDouble getQyyanglaobx() {
		return qyyanglaobx;
	}

	public DZFDouble getQyyiliaobx() {
		return qyyiliaobx;
	}

	public DZFDouble getQyshiyebx() {
		return qyshiyebx;
	}

	public DZFDouble getQyzfgjj() {
		return qyzfgjj;
	}

	public DZFDouble getQygsbx() {
		return qygsbx;
	}

	public DZFDouble getQyshybx() {
		return qyshybx;
	}

	public void setQyyanglaobx(DZFDouble qyyanglaobx) {
		this.qyyanglaobx = qyyanglaobx;
	}

	public void setQyyiliaobx(DZFDouble qyyiliaobx) {
		this.qyyiliaobx = qyyiliaobx;
	}

	public void setQyshiyebx(DZFDouble qyshiyebx) {
		this.qyshiyebx = qyshiyebx;
	}

	public void setQyzfgjj(DZFDouble qyzfgjj) {
		this.qyzfgjj = qyzfgjj;
	}

	public void setQygsbx(DZFDouble qygsbx) {
		this.qygsbx = qygsbx;
	}

	public void setQyshybx(DZFDouble qyshybx) {
		this.qyshybx = qyshybx;
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
	
	public String getQyyanglaobx_en() {
		return qyyanglaobx_en;
	}

	public String getQyyiliaobx_en() {
		return qyyiliaobx_en;
	}

	public String getQyshiyebx_en() {
		return qyshiyebx_en;
	}

	public String getQyzfgjj_en() {
		return qyzfgjj_en;
	}

	public String getQygsbx_en() {
		return qygsbx_en;
	}

	public String getQyshybx_en() {
		return qyshybx_en;
	}

	public void setQyyanglaobx_en(String qyyanglaobx_en) {
		this.qyyanglaobx_en = qyyanglaobx_en;
	}

	public void setQyyiliaobx_en(String qyyiliaobx_en) {
		this.qyyiliaobx_en = qyyiliaobx_en;
	}

	public void setQyshiyebx_en(String qyshiyebx_en) {
		this.qyshiyebx_en = qyshiyebx_en;
	}

	public void setQyzfgjj_en(String qyzfgjj_en) {
		this.qyzfgjj_en = qyzfgjj_en;
	}

	public void setQygsbx_en(String qygsbx_en) {
		this.qygsbx_en = qygsbx_en;
	}

	public void setQyshybx_en(String qyshybx_en) {
		this.qyshybx_en = qyshybx_en;
	}
	
	public Integer getSffc() {
		return sffc;
	}

	public void setSffc(Integer sffc) {
		this.sffc = sffc;
	}
	
	public DZFDouble getZxkcxj() {
		return zxkcxj;
	}

	public void setZxkcxj(DZFDouble zxkcxj) {
		this.zxkcxj = zxkcxj;
	}
	
	public DZFDouble getZxkcfjxj() {
		return zxkcfjxj;
	}

	public void setZxkcfjxj(DZFDouble zxkcfjxj) {
		this.zxkcfjxj = zxkcfjxj;
	}

	@Override
	public String getPKFieldName() {
		return "pk_salaryreport";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_salaryreport";
	}

}
