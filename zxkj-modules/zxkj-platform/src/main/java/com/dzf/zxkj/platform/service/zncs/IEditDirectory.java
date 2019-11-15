package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;
import com.dzf.zxkj.platform.model.zncs.CategorysetBVO;
import com.dzf.zxkj.platform.model.zncs.CategorysetVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 编辑目录相关
 * @author mfz
 *
 */
public interface IEditDirectory {
	/**
	 * 查辅助核算项
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<AuxiliaryAccountHVO> queryAuxiliaryAccountHVOs(String pk_corp)throws DZFWarpException;
	/**
	 * 查辅助核算值
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<AuxiliaryAccountBVO> queryAuxiliaryAccountBVOs(String pk_corp, String pk_head)throws DZFWarpException;

	/**
	 * 保存编辑目录
	 * @param pk_corp
	 * @param headVO
	 * @param bodyVOs
	 * @throws DZFWarpException
	 */
	public void saveAuxiliaryAccountVO(String pk_corp, CategorysetVO headVO, CategorysetBVO[] bodyVOs)throws DZFWarpException;
	
	/**
	 * 查询编辑目录
	 * @param pk_category
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public CategorysetVO queryCategorysetVO(String pk_category, String pk_corp)throws DZFWarpException;
	/**
	 * 复制上级编辑目录属性
	 * @param headVO
	 * @param childList
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	public void saveCopyParent(CategorysetVO headVO, ArrayList<Object[]> childList, String pk_corp)throws DZFWarpException;
}
