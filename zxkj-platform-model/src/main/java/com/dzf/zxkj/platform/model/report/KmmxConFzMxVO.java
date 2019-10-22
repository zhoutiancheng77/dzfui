package com.dzf.zxkj.platform.model.report;


import com.dzf.zxkj.common.lang.DZFBoolean;

/**
 * 包含辅助核算的vo
 * 
 * @author zhangj
 *
 */
public class KmmxConFzMxVO extends KmMxZVO {
	// 树状显示
	private String id;
	private String text;
	private String code;
	private String state;// 是否展开
	private DZFBoolean iskmid;// 是否是科目
	private String bchecked;//是否被选中
	private String checked;//是否被选中 tree 使用
	private DZFBoolean bdefault;//是否默认
	
	public String getChecked() {
		return checked;
	}
	public void setChecked(String checked) {
		this.checked = checked;
	}
	public DZFBoolean getBdefault() {
		return bdefault;
	}
	public void setBdefault(DZFBoolean bdefault) {
		this.bdefault = bdefault;
	}
	
	public String getBchecked() {
		return bchecked;
	}
	public void setBchecked(String bchecked) {
		this.bchecked = bchecked;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public DZFBoolean getIskmid() {
		return iskmid;
	}
	public void setIskmid(DZFBoolean iskmid) {
		this.iskmid = iskmid;
	}
	
	
	
}
