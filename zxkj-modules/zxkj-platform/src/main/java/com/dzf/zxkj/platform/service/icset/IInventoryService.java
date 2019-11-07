package com.dzf.zxkj.platform.service.icset;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IInventoryService {

//	 保存
	 String save(String pk_corp, InventoryVO[] vo, List<InventoryVO> listAll) throws DZFWarpException;

	// 保存
	 String save(String pk_corp, InventoryVO[] vos) throws DZFWarpException;

	// 保存(参照新增保存)
	 InventoryVO[] save1(String pk_corp, InventoryVO[] vo) throws DZFWarpException;

	//批量修改
	 String updateBatch(String pk_corp, String ids, InventoryVO updatevo) throws DZFWarpException ;

	// 查询
	 List<InventoryVO> queryInfo(String pk_corp, String ininvclids) throws DZFWarpException;

	// 查询
	 List<InventoryVO> queryInfo(String pk_corp, String ininvclids, InventoryVO param) throws DZFWarpException;

	 List<InventoryVO> query(String pk_corp) throws DZFWarpException;

	// 删除
	 void delete(InventoryVO vo) throws DZFWarpException;

	 String deleteBatch(String[] ids, String pk_corp) throws DZFWarpException;

	 List<InventoryVO> querySpecialKM(String pk_corp) throws DZFWarpException;

	 List<InventoryVO> query(String pk_corp, String kmid) throws DZFWarpException;

	// 只查询商品
	 List<InventoryVO> querysp(String pk_corp) throws DZFWarpException;

	 InventoryVO queryByPrimaryKey(String pk) throws DZFWarpException;

	// 批量导入商品
	 String saveImp(MultipartFile file, String pk_corp, String fileType, String userid) throws DZFWarpException;

	 Map<String, InventoryVO> queryInventoryVOs(String pk_corp, String[] pks) throws DZFWarpException;

	 List<InventoryVO> queryByIDs(String pk_corp, String ids) throws DZFWarpException;

	 InventoryVO  saveMergeData(String pk_corp, String id, InventoryVO[] vos) throws DZFWarpException;

	 InventoryVO  createPrice(String pk_corp, String priceway,String bili,String vdate,InventoryVO[] vos) throws DZFWarpException;
}
