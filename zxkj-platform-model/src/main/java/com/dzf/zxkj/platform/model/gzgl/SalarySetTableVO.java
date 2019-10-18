package com.dzf.zxkj.platform.model.gzgl;


import com.dzf.zxkj.common.model.SuperVO;

public class SalarySetTableVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String xh;// 序号
	private String zy;// 摘要
	private String fx;// 方向
	private String kmsz;// 科目设置
	private String kjkm;// 会计科目
	private String jfje = "-";// 借方金额
	private String dfje = "-";// 贷方金额
	
	public String getXh() {
		return xh;
	}

	public String getZy() {
		return zy;
	}

	public String getFx() {
		return fx;
	}

	public String getKmsz() {
		return kmsz;
	}

	public String getKjkm() {
		return kjkm;
	}

	public String getJfje() {
		return jfje;
	}

	public String getDfje() {
		return dfje;
	}

	public void setXh(String xh) {
		this.xh = xh;
	}

	public void setZy(String zy) {
		this.zy = zy;
	}

	public void setFx(String fx) {
		this.fx = fx;
	}

	public void setKmsz(String kmsz) {
		this.kmsz = kmsz;
	}

	public void setKjkm(String kjkm) {
		this.kjkm = kjkm;
	}

	public void setJfje(String jfje) {
		this.jfje = jfje;
	}

	public void setDfje(String dfje) {
		this.dfje = dfje;
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

}
