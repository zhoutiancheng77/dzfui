package com.dzf.zxkj.platform.service.jzcl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.jzcl.KMQMJZVO;

import java.io.Serializable;
import java.util.List;

/**
 * 科目期末结账接口
 * 
 * @author zhangj
 * 
 */
public interface IKmQmQueryService {

	/**
	 *  保存
	 * @param vo
	 * @return
	 * @throws DZFWarpException
	 */
	public Serializable save(Serializable vo) throws DZFWarpException;

	/**
	 *  查询
	 * @param queryParamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<KMQMJZVO> query(QueryParamVO queryParamvo) throws DZFWarpException;

	/**
	 *  删除
	 * @param vo
	 * @throws DZFWarpException
	 */
	public void delete(Serializable vo) throws DZFWarpException;
	
	
	/**
	 * 更新科目信息
	 * @param pk_kmqmjz
	 * @param pk_accsubj
	 * @return
	 * @throws BusinessException
	 */
	public KMQMJZVO updatekm(String pk_kmqmjz, String pk_accsubj) throws DZFWarpException;

}
