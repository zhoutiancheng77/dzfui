package com.dzf.zxkj.app.model.report;


import com.dzf.zxkj.common.model.SuperVO;

public class CwglInfoAppVO extends SuperVO {

	private String xm;
	private String byje;
	private String bnljje;

	// ------------预警-----------
	private String yjxm1;//预警项目
	private String yjxm2;
	private String yjxm3;

	private String yjvalue1;//预警值
	private String yjvalue2;
	private String yjvalue3;

	private String scale1;// 比例1
	private String scale2;// 比例2
	private String scale3;// 比例3

	public String getYjxm1() {
		return yjxm1;
	}

	public void setYjxm1(String yjxm1) {
		this.yjxm1 = yjxm1;
	}

	public String getYjxm2() {
		return yjxm2;
	}

	public void setYjxm2(String yjxm2) {
		this.yjxm2 = yjxm2;
	}

	public String getYjxm3() {
		return yjxm3;
	}

	public void setYjxm3(String yjxm3) {
		this.yjxm3 = yjxm3;
	}

	public String getYjvalue1() {
		return yjvalue1;
	}

	public void setYjvalue1(String yjvalue1) {
		this.yjvalue1 = yjvalue1;
	}

	public String getYjvalue2() {
		return yjvalue2;
	}

	public void setYjvalue2(String yjvalue2) {
		this.yjvalue2 = yjvalue2;
	}

	public String getYjvalue3() {
		return yjvalue3;
	}

	public void setYjvalue3(String yjvalue3) {
		this.yjvalue3 = yjvalue3;
	}

	public String getScale3() {
		return scale3;
	}

	public void setScale3(String scale3) {
		this.scale3 = scale3;
	}

	public String getScale2() {
		return scale2;
	}

	public void setScale2(String scale2) {
		this.scale2 = scale2;
	}

	public String getScale1() {
		return scale1;
	}

	public void setScale1(String scale1) {
		this.scale1 = scale1;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getByje() {
		return byje;
	}

	public void setByje(String byje) {
		this.byje = byje;
	}

	public String getBnljje() {
		return bnljje;
	}

	public void setBnljje(String bnljje) {
		this.bnljje = bnljje;
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
