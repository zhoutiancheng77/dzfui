package com.dzf.zxkj.app.model.sys;


import com.dzf.zxkj.common.model.SuperVO;

/**
 * 当面收-查询VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class FileHandinVO extends SuperVO {

	private static final long serialVersionUID = 7702260596888232525L;
	
	private FiletransHVO trans;
	
	private FiletransBVO[] files;

	public FiletransHVO getTrans() {
		return trans;
	}

	public void setTrans(FiletransHVO trans) {
		this.trans = trans;
	}

	public FiletransBVO[] getFiles() {
		return files;
	}

	public void setFiles(FiletransBVO[] files) {
		this.files = files;
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
