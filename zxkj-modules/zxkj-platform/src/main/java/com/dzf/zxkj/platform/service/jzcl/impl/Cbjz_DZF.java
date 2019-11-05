package com.dzf.zxkj.platform.service.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;

/**
 * 大账房成本结转
 * @author wangzhn
 *
 */
public class Cbjz_DZF {

	private SingleObjectBO singleObjectBO;
	
	private IYntBoPubUtil yntBoPubUtil = null;
	
	private IVoucherService voucher;
	
	private ICpaccountService gl_cpacckmserv;
	
	private ICpaccountCodeRuleService gl_accountcoderule;
	
	private IAuxiliaryAccountService gl_fzhsserv;
	
	public Cbjz_DZF(IYntBoPubUtil yntBoPubUtil,SingleObjectBO singleObjectBO,IVoucherService voucher,
			ICpaccountService gl_cpacckmserv, ICpaccountCodeRuleService gl_accountcoderule,
			IAuxiliaryAccountService gl_fzhsserv){
		this.singleObjectBO = singleObjectBO;
		this.yntBoPubUtil = yntBoPubUtil;
		this.voucher = voucher;
		this.gl_cpacckmserv = gl_cpacckmserv;
		this.gl_accountcoderule = gl_accountcoderule;
		this.gl_fzhsserv = gl_fzhsserv;
	}
	
	public QmclVO save(CorpVO corpVo, QmclVO vo, String userid) throws BusinessException {
		if (vo == null)
			throw new BusinessException("没有需要成本结转的数据！");
		save(corpVo, vo);
		// 公司
		String pk_corp = vo.getPk_corp();

		DZFForward cf = new DZFForward(yntBoPubUtil,singleObjectBO,voucher,gl_cpacckmserv,gl_accountcoderule,gl_fzhsserv);
		
		cf.doTradeForward(corpVo,vo, singleObjectBO, pk_corp,userid);
		return vo;
	}
	
	private QmclVO save(CorpVO corpVo,QmclVO vo) throws BusinessException {
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
