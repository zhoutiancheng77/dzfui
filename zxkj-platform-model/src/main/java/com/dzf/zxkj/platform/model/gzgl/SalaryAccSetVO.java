package com.dzf.zxkj.platform.model.gzgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;

public class SalaryAccSetVO extends SuperVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pk_salaryaccset;
	private String pk_corp;
	private Integer dr;
	private DZFBoolean jtqybf;// 计提个人部分
	private DZFBoolean showmore;// 显示明细
	private String jtgz_gzfykm;// 工资费用科目
	private String jtgz_yfgzkm;// 应付工资科目

	private String jtgz_yfsbkm;// 应付社保科目
	private String jitgz_qyyfsbgrbf;// 养老保险科目
	private String jitgz_qyyfyilbxbf;// 医疗保险科目
	private String jitgz_qyyfsybxbf;// 失业保险科目
	private String jitgz_qyyfgjjgrbf;// 公积金部分
	private String jitgz_qyyfgsbxkm;// 工伤保险科目
	private String jitgz_qyyfshybxkm;// 生育保险科目

	private String jtgz_sbfykm;// 社保费用科目
	private String jitgz_qyfysbgrbf;// 养老保险科目
	private String jitgz_qyfyyilbxbf;// 医疗保险科目
	private String jitgz_qyfysybxbf;// 失业保险科目
	private String jitgz_qyfygjjgrbf;// 公积金部分
	private String jitgz_qyfygsbxkm;// 工伤保险科目
	private String jitgz_qyfyshybxkm;// 生育保险科目

	private String ffgz_yfgzkm;// 应付工资科目
	private String ffgz_sbgrbf;// 养老保险科目
	private String ffgz_yilbxbf;// 医疗保险科目
	private String ffgz_sybxbf;// 失业保险科目
	private String ffgz_gjjgrbf;// 公积金部分
	private String ffgz_grsds;// 应缴个税科目
	private String ffgz_xjlkm;// 工资发放科目
	private String ffgz_gsbxkm;// 工伤保险科目
	private String ffgz_shybxkm;// 生育保险科目

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

	private DZFDouble gsbx_js;// 工伤保险基数
	private DZFDouble qygsbx_bl;// 工伤保险比例
	private DZFDouble shybx_js;// 生育保险基数
	private DZFDouble qyshybx_bl;// 生育保险比例

	// 预留字段
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;

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

	private Object[] tableData1;
	private Object[] tableData2;
	private Object[] tableData3;

	public String getPk_salaryaccset() {
		return pk_salaryaccset;
	}

	public void setPk_salaryaccset(String pk_salaryaccset) {
		this.pk_salaryaccset = pk_salaryaccset;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getJtgz_gzfykm() {
		return jtgz_gzfykm;
	}

	public void setJtgz_gzfykm(String jtgz_gzfykm) {
		this.jtgz_gzfykm = jtgz_gzfykm;
	}

	public String getJtgz_yfgzkm() {
		return jtgz_yfgzkm;
	}

	public void setJtgz_yfgzkm(String jtgz_yfgzkm) {
		this.jtgz_yfgzkm = jtgz_yfgzkm;
	}

	public String getFfgz_yfgzkm() {
		return ffgz_yfgzkm;
	}

	public void setFfgz_yfgzkm(String ffgz_yfgzkm) {
		this.ffgz_yfgzkm = ffgz_yfgzkm;
	}

	public String getFfgz_sbgrbf() {
		return ffgz_sbgrbf;
	}

	public void setFfgz_sbgrbf(String ffgz_sbgrbf) {
		this.ffgz_sbgrbf = ffgz_sbgrbf;
	}

	public String getFfgz_gjjgrbf() {
		return ffgz_gjjgrbf;
	}

	public void setFfgz_gjjgrbf(String ffgz_gjjgrbf) {
		this.ffgz_gjjgrbf = ffgz_gjjgrbf;
	}

	public String getFfgz_grsds() {
		return ffgz_grsds;
	}

	public void setFfgz_grsds(String ffgz_grsds) {
		this.ffgz_grsds = ffgz_grsds;
	}

	public String getFfgz_xjlkm() {
		return ffgz_xjlkm;
	}

	public void setFfgz_xjlkm(String ffgz_xjlkm) {
		this.ffgz_xjlkm = ffgz_xjlkm;
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

	public DZFDouble getVdef6() {
		return vdef6;
	}

	public void setVdef6(DZFDouble vdef6) {
		this.vdef6 = vdef6;
	}

	public DZFDouble getVdef7() {
		return vdef7;
	}

	public void setVdef7(DZFDouble vdef7) {
		this.vdef7 = vdef7;
	}

	public DZFDouble getVdef8() {
		return vdef8;
	}

	public void setVdef8(DZFDouble vdef8) {
		this.vdef8 = vdef8;
	}

	public DZFDouble getVdef9() {
		return vdef9;
	}

	public void setVdef9(DZFDouble vdef9) {
		this.vdef9 = vdef9;
	}

	public DZFDouble getVdef10() {
		return vdef10;
	}

	public void setVdef10(DZFDouble vdef10) {
		this.vdef10 = vdef10;
	}

	public DZFDate getVdef11() {
		return vdef11;
	}

	public void setVdef11(DZFDate vdef11) {
		this.vdef11 = vdef11;
	}

	public DZFDate getVdef12() {
		return vdef12;
	}

	public void setVdef12(DZFDate vdef12) {
		this.vdef12 = vdef12;
	}

	public DZFDate getVdef13() {
		return vdef13;
	}

	public void setVdef13(DZFDate vdef13) {
		this.vdef13 = vdef13;
	}

	public DZFBoolean getVdef14() {
		return vdef14;
	}

	public void setVdef14(DZFBoolean vdef14) {
		this.vdef14 = vdef14;
	}

	public DZFBoolean getVdef15() {
		return vdef15;
	}

	public void setVdef15(DZFBoolean vdef15) {
		this.vdef15 = vdef15;
	}

	public DZFBoolean getVdef16() {
		return vdef16;
	}

	public void setVdef16(DZFBoolean vdef16) {
		this.vdef16 = vdef16;
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

	public String getFfgz_yilbxbf() {
		return ffgz_yilbxbf;
	}

	public String getFfgz_sybxbf() {
		return ffgz_sybxbf;
	}

	public void setFfgz_yilbxbf(String ffgz_yilbxbf) {
		this.ffgz_yilbxbf = ffgz_yilbxbf;
	}

	public void setFfgz_sybxbf(String ffgz_sybxbf) {
		this.ffgz_sybxbf = ffgz_sybxbf;
	}

	public String getJtgz_yfsbkm() {
		return jtgz_yfsbkm;
	}

	public String getFfgz_gsbxkm() {
		return ffgz_gsbxkm;
	}

	public String getFfgz_shybxkm() {
		return ffgz_shybxkm;
	}

	public void setJtgz_yfsbkm(String jtgz_yfsbkm) {
		this.jtgz_yfsbkm = jtgz_yfsbkm;
	}

	public void setFfgz_gsbxkm(String ffgz_gsbxkm) {
		this.ffgz_gsbxkm = ffgz_gsbxkm;
	}

	public void setFfgz_shybxkm(String ffgz_shybxkm) {
		this.ffgz_shybxkm = ffgz_shybxkm;
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

	public DZFBoolean getJtqybf() {
		return jtqybf;
	}

	public void setJtqybf(DZFBoolean jtqybf) {
		this.jtqybf = jtqybf;
	}

	public String getJtgz_sbfykm() {
		return jtgz_sbfykm;
	}

	public void setJtgz_sbfykm(String jtgz_sbfykm) {
		this.jtgz_sbfykm = jtgz_sbfykm;
	}

	public Object[] getTableData1() {
		return tableData1;
	}

	public Object[] getTableData2() {
		return tableData2;
	}

	public Object[] getTableData3() {
		return tableData3;
	}

	public void setTableData1(Object[] tableData1) {
		this.tableData1 = tableData1;
	}

	public void setTableData2(Object[] tableData2) {
		this.tableData2 = tableData2;
	}

	public void setTableData3(Object[] tableData3) {
		this.tableData3 = tableData3;
	}

	public DZFBoolean getShowmore() {
		return showmore;
	}

	public void setShowmore(DZFBoolean showmore) {
		this.showmore = showmore;
	}

	public String getJitgz_qyyfsbgrbf() {
		return jitgz_qyyfsbgrbf;
	}

	public String getJitgz_qyyfyilbxbf() {
		return jitgz_qyyfyilbxbf;
	}

	public String getJitgz_qyyfsybxbf() {
		return jitgz_qyyfsybxbf;
	}

	public String getJitgz_qyyfgjjgrbf() {
		return jitgz_qyyfgjjgrbf;
	}

	public String getJitgz_qyyfgsbxkm() {
		return jitgz_qyyfgsbxkm;
	}

	public String getJitgz_qyyfshybxkm() {
		return jitgz_qyyfshybxkm;
	}

	public String getJitgz_qyfysbgrbf() {
		return jitgz_qyfysbgrbf;
	}

	public String getJitgz_qyfyyilbxbf() {
		return jitgz_qyfyyilbxbf;
	}

	public String getJitgz_qyfysybxbf() {
		return jitgz_qyfysybxbf;
	}

	public String getJitgz_qyfygjjgrbf() {
		return jitgz_qyfygjjgrbf;
	}

	public String getJitgz_qyfygsbxkm() {
		return jitgz_qyfygsbxkm;
	}

	public String getJitgz_qyfyshybxkm() {
		return jitgz_qyfyshybxkm;
	}

	public void setJitgz_qyyfsbgrbf(String jitgz_qyyfsbgrbf) {
		this.jitgz_qyyfsbgrbf = jitgz_qyyfsbgrbf;
	}

	public void setJitgz_qyyfyilbxbf(String jitgz_qyyfyilbxbf) {
		this.jitgz_qyyfyilbxbf = jitgz_qyyfyilbxbf;
	}

	public void setJitgz_qyyfsybxbf(String jitgz_qyyfsybxbf) {
		this.jitgz_qyyfsybxbf = jitgz_qyyfsybxbf;
	}

	public void setJitgz_qyyfgjjgrbf(String jitgz_qyyfgjjgrbf) {
		this.jitgz_qyyfgjjgrbf = jitgz_qyyfgjjgrbf;
	}

	public void setJitgz_qyyfgsbxkm(String jitgz_qyyfgsbxkm) {
		this.jitgz_qyyfgsbxkm = jitgz_qyyfgsbxkm;
	}

	public void setJitgz_qyyfshybxkm(String jitgz_qyyfshybxkm) {
		this.jitgz_qyyfshybxkm = jitgz_qyyfshybxkm;
	}

	public void setJitgz_qyfysbgrbf(String jitgz_qyfysbgrbf) {
		this.jitgz_qyfysbgrbf = jitgz_qyfysbgrbf;
	}

	public void setJitgz_qyfyyilbxbf(String jitgz_qyfyyilbxbf) {
		this.jitgz_qyfyyilbxbf = jitgz_qyfyyilbxbf;
	}

	public void setJitgz_qyfysybxbf(String jitgz_qyfysybxbf) {
		this.jitgz_qyfysybxbf = jitgz_qyfysybxbf;
	}

	public void setJitgz_qyfygjjgrbf(String jitgz_qyfygjjgrbf) {
		this.jitgz_qyfygjjgrbf = jitgz_qyfygjjgrbf;
	}

	public void setJitgz_qyfygsbxkm(String jitgz_qyfygsbxkm) {
		this.jitgz_qyfygsbxkm = jitgz_qyfygsbxkm;
	}

	public void setJitgz_qyfyshybxkm(String jitgz_qyfyshybxkm) {
		this.jitgz_qyfyshybxkm = jitgz_qyfyshybxkm;
	}

	@Override
	public String getPKFieldName() {
		return "pk_salaryaccset";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_salaryaccset";
	}

	public static String[] getFileds() {
		String[] strs = new String[] { "jtgz_gzfykm", "jtgz_yfgzkm", "ffgz_yfgzkm", "ffgz_sbgrbf", "ffgz_gjjgrbf",
				"ffgz_grsds", "ffgz_yilbxbf", "ffgz_sybxbf", "ffgz_xjlkm", "jtgz_yfsbkm", "jtgz_sbfykm", "ffgz_gsbxkm",
				"ffgz_shybxkm", "jtqybf","showmore", "jitgz_qyyfsbgrbf", "jitgz_qyyfyilbxbf", "jitgz_qyyfsybxbf",
				"jitgz_qyyfgjjgrbf", "jitgz_qyyfgsbxkm", "jitgz_qyyfshybxkm", "jitgz_qyfysbgrbf", "jitgz_qyfyyilbxbf",
				"jitgz_qyfysybxbf", "jitgz_qyfygjjgrbf", "jitgz_qyfygsbxkm", "jitgz_qyfyshybxkm" };
		return strs;
	}
}
