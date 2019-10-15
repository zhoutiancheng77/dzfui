package com.dzf.zxkj.platform.service.zcgl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.zcgl.ValuemodifyVO;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;

import java.util.List;

public interface IYzbgService {

	/**
	 * 保存
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public ValuemodifyVO save(ValuemodifyVO vo) throws DZFWarpException;
	
	/**
	 * 查询所有数据
	 * @return
	 * @throws BusinessException
	 */
	public List<ValuemodifyVO> query(QueryParamVO paramvo) throws DZFWarpException;
	
	/**
	 * 根据主键查询
	 * @param id
	 * @return
	 * @throws BusinessException
	 */
	public ValuemodifyVO queryById(String id) throws DZFWarpException;

	/**
	 * 修改
	 * @param vo
	 * @throws BusinessException
	 */
	public void update(ValuemodifyVO vo) throws DZFWarpException;
	
	/**
	 * 更新资产原值变更单是否转总账标记
	 * @param pk_assetvalueChange
	 * @param istogl
	 * @param pk_voucher
	 * @throws BusinessException
	 */
	public void updateAVToGLState(String pk_assetvalueChange, boolean istogl, String pk_voucher) throws DZFWarpException;

	public void delete(ValuemodifyVO data) throws DZFWarpException;
	
	/**
	 * 通过原值变更的ID 拼接凭证vo
	 * @param id
	 * @return
	 * @throws DZFWarpException
	 */
	public TzpzHVO createTzpzVoById(String id, String coperatorid, DZFDate currDate) throws DZFWarpException;
	

}
