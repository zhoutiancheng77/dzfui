package com.dzf.zxkj.platform.service.icset;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.icset.InventoryAliasVO;

public interface IInventoryAccAliasService {
	
	public InventoryAliasVO[] query(String pk_corp, String pk_inventory) throws DZFWarpException;

	public InventoryAliasVO save(InventoryAliasVO vo1) throws DZFWarpException;

	public void delete(String pk_icalias, String pk_corp) throws DZFWarpException;

	public InventoryAliasVO[] insertAliasVOS(InventoryAliasVO[] vos, String pk_corp) throws DZFWarpException;

	public void deleteByPks(String[] pk_icaliass, String pk_corp) throws DZFWarpException;

	public void deleteByInvs(String[] pk_icaliass, String pk_corp) throws DZFWarpException;

	public InventoryAliasVO[] updateAliasVOS(InventoryAliasVO[] vos, String pk_corp, String[] fields) throws DZFWarpException;

}