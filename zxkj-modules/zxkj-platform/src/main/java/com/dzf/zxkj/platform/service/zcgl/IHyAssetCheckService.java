package com.dzf.zxkj.platform.service.zcgl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.BdTradeAssetCheckVO;
import com.dzf.zxkj.platform.service.common.IBgPubService;

import java.util.List;


public interface  IHyAssetCheckService extends IBgPubService {
	
	

	public List<BdTradeAssetCheckVO>  queryAssCheckVOs(String pk_corp, String kmfaid)throws DZFWarpException;
	
}
