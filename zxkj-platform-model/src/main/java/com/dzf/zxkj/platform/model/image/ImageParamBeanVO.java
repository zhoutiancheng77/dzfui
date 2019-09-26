package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.common.model.SuperVO;

public class ImageParamBeanVO  extends SuperVO {
	private static final long serialVersionUID = -1703626335057580359L;
	
	public static final int ImageBlurProcessor = 0;
	public static final int ImageCompressProcessor=1;
	public static final int ImageRotateProcessor=2;
	public static final int ImageScaleProcessor=3;
	public static final int ImageClipProcessor=4;
	
	private int processorKind;
	private String param1;
	private String param2;
	private String param3;
	private String param4;
	
	public int getProcessorKind() {
		return processorKind;
	}
	public void setProcessorKind(int processorKind) {
		this.processorKind = processorKind;
	}
	public String getParam1() {
		return param1;
	}
	public void setParam1(String param1) {
		this.param1 = param1;
	}
	public String getParam2() {
		return param2;
	}
	public void setParam2(String param2) {
		this.param2 = param2;
	}
	public String getParam3() {
		return param3;
	}
	public void setParam3(String param3) {
		this.param3 = param3;
	}
	public String getParam4() {
		return param4;
	}
	public void setParam4(String param4) {
		this.param4 = param4;
	}
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}
}
