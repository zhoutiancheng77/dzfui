package com.dzf.zxkj.platform.model.zncs;

import java.util.List;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 统计分析
 * 跨期票据详情
 * @author ry
 *
 */
public class BillDetailVO extends SuperVO {
	
	private String webid;//图片id
	private String billdm;//发票代码
	private String billhm;//发票代码
	private String uploadperiod;//上传期间
	private String nowperiod;//当前期间
	
	
	

	public String getWebid() {
		return webid;
	}

	public void setWebid(String webid) {
		this.webid = webid;
	}

	public String getBilldm() {
		return billdm;
	}

	public void setBilldm(String billdm) {
		this.billdm = billdm;
	}

	public String getBillhm() {
		return billhm;
	}

	public void setBillhm(String billhm) {
		this.billhm = billhm;
	}

	public String getUploadperiod() {
		return uploadperiod;
	}

	public void setUploadperiod(String uploadperiod) {
		this.uploadperiod = uploadperiod;
	}

	public String getNowperiod() {
		return nowperiod;
	}

	public void setNowperiod(String nowperiod) {
		this.nowperiod = nowperiod;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
