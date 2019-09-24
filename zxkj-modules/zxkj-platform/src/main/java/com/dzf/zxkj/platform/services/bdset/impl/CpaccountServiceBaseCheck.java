package com.dzf.zxkj.platform.services.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.services.bdset.ICpaccountService;
import com.dzf.zxkj.platform.util.SpringUtils;

import java.util.Collection;
import java.util.List;

/**
 * 科目校验类
 *
 */
public class CpaccountServiceBaseCheck {

	protected SingleObjectBO singleObjectBO;

	public CpaccountServiceBaseCheck(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	/**
	 * 校验----编码规则校验
	 */
	protected void checkCodeRule(YntCpaccountVO vo, String accoutrule)
			throws BusinessException {
		String[] codeRule = accoutrule.split("/");
		int[] iCodeRules = new int[codeRule.length];
		for (int i = 0; i < codeRule.length; i++) {
			int iCodeRule = Integer.parseInt(codeRule[i]);
			iCodeRules[i] = iCodeRule;
		}
		String acccode = vo.getAccountcode();
		isAccountCodeValid(acccode, iCodeRules);
	}

	protected void isAccountCodeValid(String accountCode, int[] codeRuleArray)
			throws BusinessException {
		if (StringUtil.isEmpty(accountCode))
			throw new BusinessException("科目编码或者名称不能为空！");
		int totallen = 0;
		boolean falg = true;
		for (int i = 0; i < codeRuleArray.length; i++) {
			totallen += codeRuleArray[i];
			if (accountCode.length() == totallen) {
				falg = false;
				break;
			}
		}
		if (falg) {
			throw new BusinessException("当前编码不符合科目编码规则！");
		}
	}

	/**
	 * 校验----会计科目编码是否已经存在
	 */
	protected boolean isAccountCodeExist(String accountCode, String pk_corp)
			throws BusinessException {
		String condtion = new String(
				"(dr=0 or dr is null) and accountcode=? and pk_corp=? ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(accountCode);
		sp.addParam(pk_corp);
		Collection co = singleObjectBO.retrieveByClause(YntCpaccountVO.class,
				condtion, sp);
		return co != null && co.size() > 0;
	}

	/**
	 * 校验----会计同级科目名称是否已经存在
	 */
	protected boolean isAccountNameExist(String accountCode,
			String accountname, String pk_corp) throws BusinessException {
		String condtion = new String(
				"(dr=0 or dr is null) and accountcode like ? and accountname = ? and pk_corp=? ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(accountCode.substring(0, accountCode.length() - 2) + "%");
		sp.addParam(accountname);
		sp.addParam(pk_corp);
		Collection co = singleObjectBO.retrieveByClause(YntCpaccountVO.class,
				condtion, sp);
		return co != null && co.size() > 0;
	}

	protected YntCpaccountVO getParentVOByID(YntCpaccountVO svo)
			throws BusinessException {
		ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils
				.getBean("gl_cpacckmserv");
		String accrule = gl_cpacckmserv.queryAccountRule(svo.getPk_corp());
		YntCpaccountVO vo = null;
		if (svo.getAccountcode().equals(
				DZfcommonTools.getFirstCode(svo.getAccountcode(), accrule)))
			return vo;
		String qrysql = " SELECT * FROM YNT_CPACCOUNT WHERE PK_CORP = ? AND ACCOUNTCODE = ? AND NVL(DR,0)=0 ";
		SQLParameter SP = new SQLParameter();
		SP.addParam(svo.getPk_corp());
		String parentcode = DZfcommonTools.getParentCode(svo.getAccountcode(),
				accrule);
		SP.addParam(parentcode);
		List<YntCpaccountVO> cpvos = (List<YntCpaccountVO>) singleObjectBO
				.executeQuery(qrysql, SP, new BeanListProcessor(
						YntCpaccountVO.class));
		if (cpvos != null && cpvos.size() > 0) {
			vo = cpvos.get(0);
		}
		return vo;
	}

	protected boolean isExists(String pk_corp, String sql, SQLParameter sqlp)
			throws BusinessException {
		Object countvalue = singleObjectBO.executeQuery(
				"select count(1) from dual where exists(" + sql + ")", sqlp,
				new ColumnProcessor());
		if (countvalue != null && new Integer(countvalue.toString()) > 0) {
			return true;
		}
		return false;
	}

	protected YntCpaccountVO queryYntvoByid(String id) throws BusinessException {
		YntCpaccountVO vo = (YntCpaccountVO) singleObjectBO.queryByPrimaryKey(
				YntCpaccountVO.class, id);
		return vo;
	}

	/**
	 * 是否有子节点(除了本节点之外)
	 */
	protected boolean hasChildren(String pk_corp, YntCpaccountVO vo)
			throws BusinessException {
		SQLParameter sqlp = new SQLParameter();
		sqlp.addParam((vo.getAccountlevel().intValue() + 1));
		sqlp.addParam(vo.getAccountcode() + "%");
		sqlp.addParam(vo.getPk_corp());
		String sql = " select 1 from ynt_cpaccount where accountlevel= ?  and accountcode like ? and pk_corp= ? and nvl(dr,0)=0 ";
		boolean b = isExists(pk_corp, sql, sqlp);
		return b;
	}
}
