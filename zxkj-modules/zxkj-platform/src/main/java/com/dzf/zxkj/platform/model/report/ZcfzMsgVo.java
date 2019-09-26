package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;

import java.util.List;

public class ZcfzMsgVo extends ReportBaseVo {

	private String jyxname;// 校验项名字
	private DZFBoolean bph;
	private DZFDouble zcvalue;
	private DZFDouble fzvalue;
	private DZFDouble qyvalue;//所有则权益
	private DZFDouble wfpvlaue;//为分配利润
	private DZFDouble jlrvalue;//净利润
	private DZFBoolean bshowjyx1;
	private DZFBoolean bshowjyx2;//是否显示校验项2
	private List<Result> leftvo;
	private List<Result> rightvo;
	
	public DZFBoolean getBshowjyx1() {
		return bshowjyx1;
	}

	public void setBshowjyx1(DZFBoolean bshowjyx1) {
		this.bshowjyx1 = bshowjyx1;
	}

	public DZFBoolean getBshowjyx2() {
		return bshowjyx2;
	}

	public void setBshowjyx2(DZFBoolean bshowjyx2) {
		this.bshowjyx2 = bshowjyx2;
	}

	public DZFDouble getQyvalue() {
		return qyvalue;
	}

	public void setQyvalue(DZFDouble qyvalue) {
		this.qyvalue = qyvalue;
	}

	public DZFDouble getZcvalue() {
		return zcvalue;
	}

	public void setZcvalue(DZFDouble zcvalue) {
		this.zcvalue = zcvalue;
	}

	public DZFDouble getFzvalue() {
		return fzvalue;
	}

	public void setFzvalue(DZFDouble fzvalue) {
		this.fzvalue = fzvalue;
	}

	public DZFDouble getWfpvlaue() {
		return wfpvlaue;
	}

	public void setWfpvlaue(DZFDouble wfpvlaue) {
		this.wfpvlaue = wfpvlaue;
	}

	public DZFDouble getJlrvalue() {
		return jlrvalue;
	}

	public void setJlrvalue(DZFDouble jlrvalue) {
		this.jlrvalue = jlrvalue;
	}

	public static class Result extends ReportBaseVo {
		public String name;
		public String value;
		public String tips;// 提示

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getTips() {
			return tips;
		}

		public void setTips(String tips) {
			this.tips = tips;
		}
		
		

	}

	public String getJyxname() {
		return jyxname;
	}

	public void setJyxname(String jyxname) {
		this.jyxname = jyxname;
	}

	public DZFBoolean getBph() {
		return bph;
	}

	public void setBph(DZFBoolean bph) {
		this.bph = bph;
	}

	public List<Result> getLeftvo() {
		return leftvo;
	}

	public void setLeftvo(List<Result> leftvo) {
		this.leftvo = leftvo;
	}

	public List<Result> getRightvo() {
		return rightvo;
	}

	public void setRightvo(List<Result> rightvo) {
		this.rightvo = rightvo;
	}

}
