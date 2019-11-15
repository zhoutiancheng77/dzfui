package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.zncs.VouchertempletHVO;

import java.util.List;

/**
 * 自定义凭证模板保存
 * @author mfz
 *
 */
public interface IVoucherTemplet {

	/**
	 * 保存
	 * @param templetList
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	public void saveVoucherTempletList(List<VouchertempletHVO> templetList, String pk_corp)throws DZFWarpException;
	
	/**
	 * 查询
	 * @param pk_corp
	 * @param pk_category
	 * @return
	 * @throws DZFWarpException
	 */
	public List<VouchertempletHVO> queryVoucherTempletList(String pk_corp, String pk_category)throws DZFWarpException;
}
