package com.dzf.zxkj.platform.service.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ObjectProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.common.constant.QmjzByDzfInfo;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 大账房结转
 *
 */
public class DZFForward {

	private IYntBoPubUtil yntBoPubUtil = null;
	
	private SingleObjectBO singleObjectBO;
	
	private IVoucherService voucher;
	
	private ICpaccountService gl_cpacckmserv;
	
	private ICpaccountCodeRuleService gl_accountcoderule;
	
	private IAuxiliaryAccountService gl_fzhsserv;

	private IAccountService accountService;
	
	public DZFForward(IYntBoPubUtil yntBoPubUtil, SingleObjectBO singleObjectBO, IVoucherService voucher,
                      ICpaccountService gl_cpacckmserv, ICpaccountCodeRuleService gl_accountcoderule,
                      IAuxiliaryAccountService gl_fzhsserv){
		this.yntBoPubUtil = yntBoPubUtil;
		this.singleObjectBO = singleObjectBO;
		this.voucher = voucher;
		this.gl_cpacckmserv = gl_cpacckmserv;
		this.gl_accountcoderule = gl_accountcoderule;
		this.gl_fzhsserv = gl_fzhsserv;
		this.accountService = SpringUtils.getBean(IAccountService.class);
	}

	/**
	 * 保存结转凭证
	 */
	public void saveTZBillVO(CorpVO corpVo, QmclVO vo, DZFDouble ye, YntCpaccountVO jfcpa, YntCpaccountVO dfcpa, String abstracts, String userid) throws BusinessException {
		try{
			String jfkm = jfcpa.getPk_corp_account();
			String dfkm = dfcpa.getPk_corp_account();
			
			//生成结转凭证
			TzpzHVO headVO = new TzpzHVO() ;
			headVO.setPk_corp(vo.getPk_corp()) ;
			headVO.setPzlb(0) ;//凭证类别：记账
			headVO.setJfmny(ye) ;
			headVO.setDfmny(ye) ;
			headVO.setCoperatorid(userid) ;
			headVO.setIshasjz(DZFBoolean.FALSE) ;
			DZFDate nowDatevalue =  getPeroidDZFDate(vo) ;
			headVO.setDoperatedate(nowDatevalue) ;
			headVO.setPzh(yntBoPubUtil.getNewVoucherNo(vo.getPk_corp(), nowDatevalue)) ;
			headVO.setVbillstatus(8) ;//默认自由态					
			//记录单据来源
			headVO.setSourcebillid(vo.getPk_qmcl()) ;
			headVO.setSourcebilltype(IBillTypeCode.HP34) ;
			headVO.setPeriod(vo.getPeriod());
			headVO.setVyear(Integer.valueOf(vo.getPeriod().substring(0, 4)));
			headVO.setIsfpxjxm(new DZFBoolean("N"));
			
			TzpzBVO jfbodyVO = new TzpzBVO() ;
			jfbodyVO.setPk_accsubj(jfkm) ;//借方科目
			jfbodyVO.setJfmny(ye) ;
			jfbodyVO.setZy(abstracts) ;//摘要
			//币种，默认人民币
			jfbodyVO.setPk_currency(DzfUtil.PK_CNY) ;
			jfbodyVO.setNrate(new DZFDouble(1));
			jfbodyVO.setPk_corp(vo.getPk_corp());
			//判断存在辅助核算的情况 部门、项目
			setEntryFzhs(jfbodyVO, jfcpa, vo.getPk_corp());
			
			TzpzBVO dfbodyVO = new TzpzBVO() ;
			dfbodyVO.setPk_accsubj(dfkm) ;//贷方科目
			dfbodyVO.setDfmny(ye) ;
			dfbodyVO.setZy(abstracts) ;//摘要
			//币种，默认人民币
			dfbodyVO.setPk_currency(yntBoPubUtil.getCNYPk()) ;
			dfbodyVO.setNrate(new DZFDouble(1));
			dfbodyVO.setPk_corp(vo.getPk_corp());
			
			headVO.setChildren(new TzpzBVO[]{jfbodyVO , dfbodyVO});

			voucher.saveVoucher(corpVo, headVO);
		}catch(Exception e){
			throw new BusinessException("保存结转凭证:"+e.getMessage());
		}
	}
	
	private void setEntryFzhs(TzpzBVO entry, YntCpaccountVO cpavo, String pk_corp){
		AuxiliaryAccountBVO bvo;
		//产品项目 0000100010
		if(cpavo.getIsfzhs().charAt(8) == '1'){
			//先找辅助核算大类
			AuxiliaryAccountHVO hvo = gl_fzhsserv.queryHByName(pk_corp, "产品项目");
			if(hvo != null){
				bvo = gl_fzhsserv.queryBByCode(pk_corp,
						QmjzByDzfInfo.projcode, hvo.getPrimaryKey());
				if(bvo != null){
					entry.setFzhsx9(bvo.getPk_auacount_b());
				}
			}
			
		}
		
		//部门 0000100000
		if(cpavo.getIsfzhs().charAt(4) == '1'){
			bvo = gl_fzhsserv.queryBByCode(pk_corp, 
					QmjzByDzfInfo.deptcode, AuxiliaryConstant.ITEM_DEPARTMENT);
			
			if(bvo != null){
				entry.setFzhsx5(bvo.getPk_auacount_b());
			}
		}
	}
	
	/**
	 * 成本结转模板
	 */
	public void doTradeForward(CorpVO corpVo,QmclVO vo,SingleObjectBO bo,String pk_corp,String userid) throws BusinessException{
		try{
			String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);
			//借方科目
			String jfcode = gl_accountcoderule.getNewRuleCode(QmjzByDzfInfo.jfcode, 
					DZFConstant.ACCOUNTCODERULE, newrule);
			
			//贷方科目
			String dfcode = gl_accountcoderule.getNewRuleCode(QmjzByDzfInfo.dfcode, 
					DZFConstant.ACCOUNTCODERULE, newrule);
			
			Map<String, YntCpaccountVO> map = getAccountMap(pk_corp);
			
			YntCpaccountVO jfcpa = map.get(jfcode);
			if(jfcpa == null){
				throw new BusinessException("公司成本结转模板的取数科目编码为"+jfcode+"的科目不存在或不是末级科目，请检查！") ;
			}
			YntCpaccountVO dfcpa = map.get(dfcode);
			if(dfcpa == null){
				throw new BusinessException("公司成本结转模板的取数科目编码为"+dfcode+"的科目不存在或不是末级科目，请检查！") ;
			}
			
			DZFDouble dbvalue = getTotalCost(vo.getPeriod());
			if(dbvalue.doubleValue() != 0){
				saveTZBillVO(corpVo, vo, dbvalue, jfcpa, dfcpa, "成本结转", userid);
			}
		}catch(Exception e){
			throw new BusinessException("成本结转模板:"+e.getMessage());
		}

	}
	
	private DZFDouble getTotalCost(String period){
		SQLParameter sp = new SQLParameter();
		sp.addParam(period);
		
		StringBuffer sf = new StringBuffer();
		sf.append(" select sum(nvl(b.amount, 0) * nvl(co.ncost, 0)) ");
		sf.append("   from cn_goodsbill_b b ");
		sf.append("   left join cn_goodsbill l ");
		sf.append("     on b.pk_goodsbill = l.pk_goodsbill ");
		sf.append("   left join cn_goodsbill_s s ");
		sf.append("     on l.pk_goodsbill = s.pk_goodsbill ");
		sf.append("    and s.vstatus = 1 ");
		sf.append("   left join cn_goodscost co ");
		sf.append("     on b.pk_goodsspec = co.pk_goodsspec ");
		sf.append("    and substr(s.doperatetime, 0, 7) = co.period ");
		sf.append("  where nvl(b.dr, 0) = 0 ");
		sf.append("    and nvl(l.dr, 0) = 0 ");
		sf.append("    and nvl(s.dr, 0) = 0 ");
		sf.append("    and nvl(co.dr, 0) = 0 ");
		sf.append("    and l.vstatus in (1, 2, 3) ");
		sf.append("    and substr(s.doperatetime, 0, 7) = ? ");//'2019-03'
		sf.append("  group by b.dr ");
		
		BigDecimal s1 = (BigDecimal) singleObjectBO.executeQuery(sf.toString(),
				sp, new ObjectProcessor());
		
		DZFDouble d1 = s1 == null ? DZFDouble.ZERO_DBL : new DZFDouble(s1);
        d1 = d1.setScale(2, DZFDouble.ROUND_HALF_UP);
		return d1;
	}
	
	private Map<String, YntCpaccountVO> getAccountMap(String pk_corp){
		Map<String, YntCpaccountVO> map = new HashMap<String, YntCpaccountVO>();
		
		YntCpaccountVO[] vos = accountService.queryByPk(pk_corp);
		if(vos != null && vos.length > 0){
			for(YntCpaccountVO vo : vos){
				if(vo.getIsleaf() != null && vo.getIsleaf().booleanValue()){
					map.put(vo.getAccountcode(), vo);
				}
			}
		}
		
		return map;
	}
	
	/**
	 * 取期间所属月的最后一天
	 * @param vo
	 * @return
	 */
	public DZFDate getPeroidDZFDate(QmclVO vo){
		DZFDate period = new DZFDate(vo.getPeriod() + "-01");
		period = new DZFDate(vo.getPeriod() + "-" + period.getDaysMonth()) ;
		return period;
	}
}
