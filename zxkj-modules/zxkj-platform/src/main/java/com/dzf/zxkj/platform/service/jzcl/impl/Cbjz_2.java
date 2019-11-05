package com.dzf.zxkj.platform.service.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.platform.model.bdset.CpcosttransVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;

/**
 * 比例结转
 * @author zpm
 *
 */
public class Cbjz_2 {

	
	private SingleObjectBO singleObjectBO;
	
	private IYntBoPubUtil yntBoPubUtil = null;
	
	private IVoucherService voucher;
	
	public Cbjz_2(IYntBoPubUtil yntBoPubUtil,SingleObjectBO singleObjectBO,IVoucherService voucher){
		this.singleObjectBO = singleObjectBO;
		this.yntBoPubUtil = yntBoPubUtil;
		this.voucher = voucher;
	}
	
	public QmclVO save(CorpVO corpVo, QmclVO vo, String userid) throws BusinessException {
		if (vo == null)
			throw new BusinessException("没有需要成本结转的数据！");
		//先保存数据
		Cbjz_1 cb = new Cbjz_1(yntBoPubUtil,singleObjectBO,voucher);
		cb.save(corpVo,vo);
		// 公司
		String pk_corp = vo.getPk_corp();
		//Cbjz_1类已做校验
		CostForward cf = new CostForward(yntBoPubUtil,singleObjectBO,voucher);
//		cf.onCostForwardCheck(corpVo, vo, singleObjectBO);
		// 根据公司查找公司成本结转模板
		SQLParameter sp=new SQLParameter();
		sp.addParam(vo.getPk_corp());
		String where = " pk_corp=? and nvl(dr,0)=0 ";
		CpcosttransVO[] mbvos = (CpcosttransVO[]) singleObjectBO.queryByCondition(CpcosttransVO.class, where,sp);
		if (mbvos == null || mbvos.length < 1) {
			cf.doCostTradeForward(corpVo,vo, singleObjectBO, pk_corp,userid);
		} else {
			cf.doCostForward(corpVo,vo, singleObjectBO, mbvos, pk_corp,userid);
		}
		return vo;
	}
	
}
