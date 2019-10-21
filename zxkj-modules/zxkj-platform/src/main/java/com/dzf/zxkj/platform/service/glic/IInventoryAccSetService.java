package com.dzf.zxkj.platform.service.glic;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

public interface IInventoryAccSetService {
	
	public InventorySetVO query(String pk_corp) throws DZFWarpException;

	public InventorySetVO save(String userid,String pk_corp,InventorySetVO vo1,boolean ischeck) throws DZFWarpException;
	
	public InventorySetVO getDefaultValue(String userid, CorpVO cpvo) throws DZFWarpException;
	
	public String checkInventorySet(String userid,String pk_corp,InventorySetVO vo) throws DZFWarpException;

}
