package com.dzf.zxkj.platform.services.qcset;


import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.icset.IcbalanceVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.services.common.IBgPubService;

import java.io.File;
import java.util.List;

public interface IQcService extends IBgPubService {
	// //查询
	// public List<IcbalanceVO> quyerInfovo(int pageNo, int pageSize,String
	// order) throws BusinessException ;
	// //删除
	// public void delteInfovo(IcbalanceVO bean) throws BusinessException ;
	//
	// //获取总行数
	// public int getTotalRow(String sql) throws BusinessException ;
	//
	// //新增保存
	// public IcbalanceVO saveNew(IcbalanceVO vo) throws BusinessException;
	//
	// //更新
	// public void update(IcbalanceVO vo) throws BusinessException;

	public List<IcbalanceVO> quyerInfovoic(String pk_corp) throws DZFWarpException;

	public IcbalanceVO queryByPrimaryKey(String primaryKey) throws DZFWarpException;

	// 批量导入商品
	public String saveImp(File file, String pk_corp, String fileType, String userid, DZFDate icdate)
			throws DZFWarpException;

	// 批量删除
	public String deleteBatch(String[] ids, String pk_corp) throws DZFWarpException;

	// 保存(参照新增保存)
	public String save(String pk_corp, IcbalanceVO[] vos, String userid) throws DZFWarpException;

	//  同步期初 库存到总账
	public void saveIcSync(String userid, String pk_corp)  throws DZFWarpException;

	//  同步期初 总账到库存
	public void saveGL2KcSync(String userid, String pk_corp, List<FzhsqcVO> fzhsqcList, StringBuffer msg)  throws DZFWarpException;

	public List<IcbalanceVO> quyerInfovoic(String pk_corp, String ids) throws DZFWarpException;
}
