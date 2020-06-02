package com.dzf.zxkj.app.model.resp.bean;

public class ImageBeanVO extends UserBeanVO {

	private String imagekey;
	private String filepath;
	private String begindate;
	private String enddate;
	private String imagemsgkeys;
	private String imageparams;
	private String op_msg;//手机操作消息标记，0-已读，1-删除
	
	
	
	public String getOp_msg() {
		return op_msg;
	}

	public void setOp_msg(String op_msg) {
		this.op_msg = op_msg;
	}

	public String getImageparams() {
		return imageparams;
	}

	public void setImageparams(String imageparams) {
		this.imageparams = imageparams;
	}

	public String getImagemsgkeys() {
		return imagemsgkeys;
	}

	public void setImagemsgkeys(String imagemsgkeys) {
		this.imagemsgkeys = imagemsgkeys;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}


	public String getBegindate() {
		return begindate;
	}

	public void setBegindate(String begindate) {
		this.begindate = begindate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getImagekey() {
		return imagekey;
	}

	public void setImagekey(String imagekey) {
		this.imagekey = imagekey;
	}
}
