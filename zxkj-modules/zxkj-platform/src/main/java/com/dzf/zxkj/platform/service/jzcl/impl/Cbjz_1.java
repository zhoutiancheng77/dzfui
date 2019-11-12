package com.dzf.zxkj.platform.service.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;

/**
 * 直接结转
 * @author zpm
 *
 */
public class Cbjz_1 {
	
	private SingleObjectBO singleObjectBO;
	
	private IYntBoPubUtil yntBoPubUtil = null;

	private IVoucherService voucher;
	
	public Cbjz_1(IYntBoPubUtil yntBoPubUtil,SingleObjectBO singleObjectBO,IVoucherService voucher){
		this.singleObjectBO = singleObjectBO;
		this.yntBoPubUtil = yntBoPubUtil;
		this.voucher = voucher;
	}

	
	public QmclVO save(CorpVO corpVo, QmclVO vo) throws BusinessException {
		CostForward cf = new CostForward(yntBoPubUtil,singleObjectBO,voucher);
		cf.onCostForwardCheck(corpVo, vo, singleObjectBO);
		// 成本结转处理
		vo.setIscbjz(DZFBoolean.TRUE);
		// 保存期末处理VO
		if (StringUtil.isEmpty(vo.getPrimaryKey())) {
			vo = (QmclVO) singleObjectBO.saveObject(vo.getPk_corp(), vo);
		} else {
			singleObjectBO.update(vo);
		}
		return vo;
	}
}