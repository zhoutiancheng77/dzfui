package com.dzf.zxkj.platform.service.icset;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.icset.InventorySetVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

public interface IInventoryAccSetService {

    InventorySetVO query(String pk_corp) throws DZFWarpException;

    InventorySetVO save(String userid, String pk_corp, InventorySetVO vo1, boolean ischeck) throws DZFWarpException;

    InventorySetVO getDefaultValue(String userid, CorpVO cpvo) throws DZFWarpException;

    String checkInventorySet(String userid, String pk_corp, InventorySetVO vo) throws DZFWarpException;

}
