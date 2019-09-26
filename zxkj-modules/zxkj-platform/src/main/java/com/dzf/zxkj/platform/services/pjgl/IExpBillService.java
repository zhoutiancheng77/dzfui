package com.dzf.zxkj.platform.services.pjgl;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.pzgl.ExpBillHVO;
import com.dzf.zxkj.platform.model.pzgl.ExpBillParamVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.List;

public interface IExpBillService {

	// 查询
	public List<ExpBillHVO> query(ExpBillParamVO param) throws DZFWarpException;
	
	public ExpBillHVO queryHeadByID(String id, String pk_corp) throws DZFWarpException;
	
	public ExpBillHVO queryByID(String id, String pk_corp) throws DZFWarpException;
	
	public ExpBillHVO update(ExpBillHVO vo) throws DZFWarpException;
	
	public ExpBillHVO save(ExpBillHVO vo) throws DZFWarpException;
	/**
	 * 报销单生成凭证
	 * @param bills
	 * @param corpVo
	 * @throws BusinessException
	 */
	public ExpBillHVO saveVoucher(String[] bills, CorpVO corpVo, String userid) throws DZFWarpException;
	/**
	 * 报销单取消生成凭证
	 * @param bills
	 * @param pk_corp
	 * @throws BusinessException
	 */
	public void deleteVoucher(String[] bills, String pk_corp, String isqjsy) throws DZFWarpException;
	
	public List<ExpBillHVO> saveImpWbx(String xmlStr, String pk_corp) throws DZFWarpException;
	
	/**
	 * 凭证删除时更新报销单
	 * @param pzvo
	 * @throws BusinessException
	 */
	public void updateFromVch(TzpzHVO pzvo) throws DZFWarpException;
	
	public void updateCwdj(String appSecret, String pk_corp) throws DZFWarpException;
}
