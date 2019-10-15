package com.dzf.zxkj.platform.service.bdset;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountChangeVO;

import java.util.Map;

public interface ICpaccountCodeRuleService {

	
    /*****
     * 更新科目编码规则
     * @param pk_corp
     * @param oldrule
     * @param newrule
     * @param acckind
     * @return
     * @throws BusinessException
     */
	public YntCpaccountChangeVO[] updateCodeRule(String pk_corp, String oldrule, String newrule, Object... Otherparams) throws DZFWarpException;


	/****
	 * 获取新编码规则下的的新编码
	 * @param oldCode
	 * @param oldrule
	 * @param newrule
	 * @return
	 * @throws BusinessException
	 */
	public String getNewRuleCode(String oldCode, String oldrule, String newrule)throws DZFWarpException;



	/**
	 * 获取新编码规则下的的新编码 （通过数组）
	 * @param oldcode
	 * @param oldrule
	 * @param newrule
	 * @return
	 * @throws BusinessException
	 */
	public String[] getNewCodes(String[] oldcode, String oldrule, String newrule) throws DZFWarpException;

	/**
	 * 获取新编码规则下的的新编码map （通过数组）
	 * @param oldcode
	 * @param oldrule
	 * @param newrule
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String,String> getNewCodeMap(String[] oldcode, String oldrule, String newrule) throws DZFWarpException;
	
	
	/****
	 * 获取公公司会计科目变更数据
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public YntCpaccountChangeVO[] loadData(String pk_corp)throws BusinessException;
}
