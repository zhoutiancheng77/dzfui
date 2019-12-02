package com.dzf.zxkj.platform.service.zncs.image.impl;

import java.io.Serializable;

import com.dzf.zxkj.platform.model.image.DcModelHVO;

public class ModelSelectVO implements Serializable,Comparable<ModelSelectVO>{
	
	public static String pipeistyle_0 = "户间转账";
	public static String pipeistyle_1 = "税项";
	public static String pipeistyle_2 = "备注";
	public static String pipeistyle_3 = "收付款（公司个人）";
	public static String pipeistyle_4 = "收付款";
	public static String pipeistyle_5 = "自动";

	private DcModelHVO defaultmodel;
	private int level;//级别
	private String pipeistyle;//匹配方式
	public DcModelHVO getDefaultmodel() {
		return defaultmodel;
	}
	public void setDefaultmodel(DcModelHVO defaultmodel) {
		this.defaultmodel = defaultmodel;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getPipeistyle() {
		return pipeistyle;
	}
	public void setPipeistyle(String pipeistyle) {
		if(ModelSelectVO.pipeistyle_0.equals(pipeistyle)){
			setLevel(6);
		}else if(ModelSelectVO.pipeistyle_1.equals(pipeistyle)){
			setLevel(5);
		}else if(ModelSelectVO.pipeistyle_2.equals(pipeistyle)){
			setLevel(4);
		}else if(ModelSelectVO.pipeistyle_3.equals(pipeistyle)){
			setLevel(3);
		}else if(ModelSelectVO.pipeistyle_4.equals(pipeistyle)){
			setLevel(2);
		}else if(ModelSelectVO.pipeistyle_5.equals(pipeistyle)){
			setLevel(1);
		}
		//
		this.pipeistyle = pipeistyle;
	}
	@Override
	public int compareTo(ModelSelectVO o) {
		int i = this.getLevel() - o.getLevel();
		if (i == 0) {
			int j = this.getDefaultmodel().getPk_corp().compareTo(o.getDefaultmodel().getPk_corp());
			return j;
		}
		return i;
	}
	
}