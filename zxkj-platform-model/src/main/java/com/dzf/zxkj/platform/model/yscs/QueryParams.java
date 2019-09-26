package com.dzf.zxkj.platform.model.yscs;

public class QueryParams {

	// 公司编码
	private String bbcx_gsbm;
	// 期间
	private String bbcx_qj;
	// 是否包含未记账凭证
	private boolean bbcx_wjz;
	// 按往来科目明细分析填列
	private boolean bbcx_wlkm;
	// 查询类别
	private int bbcx_type;
	// 是否包含未记账凭证
	private boolean bbcx_jzpz;
	// 年度
	private String bbcx_nd;
	// 季度
	private String bbcx_jd;

	public String getBbcx_gsbm() {
		return bbcx_gsbm;
	}

	public void setBbcx_gsbm(String bbcx_gsbm) {
		this.bbcx_gsbm = bbcx_gsbm;
	}

	public String getBbcx_qj() {
		return bbcx_qj;
	}

	public void setBbcx_qj(String bbcx_qj) {
		this.bbcx_qj = bbcx_qj;
	}

	public boolean isBbcx_wjz() {
		return bbcx_wjz;
	}

	public void setBbcx_wjz(boolean bbcx_wjz) {
		this.bbcx_wjz = bbcx_wjz;
	}

	public boolean isBbcx_wlkm() {
		return bbcx_wlkm;
	}

	public void setBbcx_wlkm(boolean bbcx_wlkm) {
		this.bbcx_wlkm = bbcx_wlkm;
	}

	public int getBbcx_type() {
		return bbcx_type;
	}

	public void setBbcx_type(int bbcx_type) {
		this.bbcx_type = bbcx_type;
	}

	public boolean isBbcx_jzpz() {
		return bbcx_jzpz;
	}

	public void setBbcx_jzpz(boolean bbcx_jzpz) {
		this.bbcx_jzpz = bbcx_jzpz;
	}

	public String getBbcx_nd() {
		return bbcx_nd;
	}

	public void setBbcx_nd(String bbcx_nd) {
		this.bbcx_nd = bbcx_nd;
	}

	public String getBbcx_jd() {
		return bbcx_jd;
	}

	public void setBbcx_jd(String bbcx_jd) {
		this.bbcx_jd = bbcx_jd;
	}

}
