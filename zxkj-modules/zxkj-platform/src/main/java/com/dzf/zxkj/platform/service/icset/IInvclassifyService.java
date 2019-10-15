package com.dzf.zxkj.platform.service.icset;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.icset.InvclassifyVO;
import com.dzf.zxkj.platform.service.common.IBgPubService;

import java.io.File;
import java.util.List;

public interface IInvclassifyService extends IBgPubService {

	public InvclassifyVO save(InvclassifyVO vo)throws DZFWarpException;
	
	public List<InvclassifyVO> query(String pk_corp)throws DZFWarpException;
	
	public void delete(InvclassifyVO vo)throws DZFWarpException;

	public InvclassifyVO queryByPrimaryKey(String primaryKey) throws DZFWarpException;
	
	public String saveImp(File file, String pk_corp, String fileType) throws DZFWarpException;
	
}
