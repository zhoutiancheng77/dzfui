package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 现金流量查询季报
 * 
 * @author zhangj
 *
 */
public class XjllquarterlyVo extends SuperVO {

	private String xm;
	private String hc;
	private String hc_id;//(不允许修改)
	private DZFDouble bnlj;// 本年累计
	private DZFDouble bf_bnlj;// 上年本年累计数
	private DZFDouble jd1;// 第一季度
	private DZFDouble jd2;// 第二季度
	private DZFDouble jd3;// 第三季度
	private DZFDouble jd4;// 第四季度
	
	//去年同期季度数据
	private DZFDouble jd1_last;
	private DZFDouble jd2_last;
	private DZFDouble jd3_last;
	private DZFDouble jd4_last;//

	// 打印时 标题显示的区间区间
	private String titlePeriod;
	// 公司
	private String gs;

	private String period;// 期间

	private String pk_corp;
	
	

	public String getHc_id() {
		return hc_id;
	}

	public void setHc_id(String hc_id) {
		this.hc_id = hc_id;
	}

	public DZFDouble getBf_bnlj() {
		return bf_bnlj;
	}

	public void setBf_bnlj(DZFDouble bf_bnlj) {
		this.bf_bnlj = bf_bnlj;
	}

	public String getTitlePeriod() {
		return titlePeriod;
	}

	public void setTitlePeriod(String titlePeriod) {
		this.titlePeriod = titlePeriod;
	}

	public String getGs() {
		return gs;
	}

	public void setGs(String gs) {
		this.gs = gs;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getHc() {
		return hc;
	}

	public void setHc(String hc) {
		this.hc = hc;
	}

	public DZFDouble getBnlj() {
		return bnlj;
	}

	public void setBnlj(DZFDouble bnlj) {
		this.bnlj = bnlj;
	}

	public DZFDouble getJd1() {
		return jd1;
	}

	public void setJd1(DZFDouble jd1) {
		this.jd1 = jd1;
	}

	public DZFDouble getJd2() {
		return jd2;
	}

	public void setJd2(DZFDouble jd2) {
		this.jd2 = jd2;
	}

	public DZFDouble getJd3() {
		return jd3;
	}

	public void setJd3(DZFDouble jd3) {
		this.jd3 = jd3;
	}

	public DZFDouble getJd4() {
		return jd4;
	}

	public void setJd4(DZFDouble jd4) {
		this.jd4 = jd4;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

	public DZFDouble getJd1_last() {
		return jd1_last;
	}

	public void setJd1_last(DZFDouble jd1_last) {
		this.jd1_last = jd1_last;
	}

	public DZFDouble getJd2_last() {
		return jd2_last;
	}

	public void setJd2_last(DZFDouble jd2_last) {
		this.jd2_last = jd2_last;
	}

	public DZFDouble getJd3_last() {
		return jd3_last;
	}

	public void setJd3_last(DZFDouble jd3_last) {
		this.jd3_last = jd3_last;
	}

	public DZFDouble getJd4_last() {
		return jd4_last;
	}

	public void setJd4_last(DZFDouble jd4_last) {
		this.jd4_last = jd4_last;
	}
}
