package com.dzf.zxkj.platform.model.yscs;

import com.dzf.zxkj.base.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings({ "rawtypes", "serial" })
public class YspzscRepVO extends SuperVO {
	
	@JsonProperty("YSPZ_TPID")
	private String imageId;
	

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
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
