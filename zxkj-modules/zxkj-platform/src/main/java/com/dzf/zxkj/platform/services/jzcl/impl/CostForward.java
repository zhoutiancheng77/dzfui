package com.dzf.zxkj.platform.services.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.common.constant.IQmclConstant;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.platform.model.bdset.CpcosttransVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.BdTradeCostTransferVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.pzgl.IVoucherService;
import com.dzf.zxkj.platform.services.report.IYntBoPubUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 比例结转
 *
 */
public class CostForward {
	
	private IYntBoPubUtil yntBoPubUtil = null;
	
	private SingleObjectBO singleObjectBO;
	
	private IVoucherService voucher;
	
	public CostForward(IYntBoPubUtil yntBoPubUtil, SingleObjectBO singleObjectBO, IVoucherService voucher){
		this.yntBoPubUtil = yntBoPubUtil;
		this.singleObjectBO = singleObjectBO;
		this.voucher = voucher;
	}
	
	/**
	 * 保存结转凭证
	 */
	public void saveTZBillVO(CorpVO corpVo, QmclVO vo, DZFDouble ye, String jfkm, String dfkm, String abstracts, String userid) throws BusinessException {
		try{
			//生成结转凭证
			TzpzHVO headVO = new TzpzHVO() ;
			headVO.setPk_corp(vo.getPk_corp()) ;
			headVO.setPzlb(0) ;//凭证类别：记账
			headVO.setJfmny(ye) ;
			headVO.setDfmny(ye) ;
			headVO.setCoperatorid(userid) ;
			headVO.setIshasjz(DZFBoolean.FALSE) ;
//			DZFDate nowDate = DZFDate.getDate(new Long(InvocationInfoProxy.getInstance().getDate())) ;
//			headVO.setDoperatedate(nowDate) ;
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
			
			TzpzBVO dfbodyVO = new TzpzBVO() ;
			dfbodyVO.setPk_accsubj(dfkm) ;//贷方科目
			dfbodyVO.setDfmny(ye) ;
			dfbodyVO.setZy(abstracts) ;//摘要
			//币种，默认人民币
			dfbodyVO.setPk_currency(yntBoPubUtil.getCNYPk()) ;
			dfbodyVO.setNrate(new DZFDouble(1));
			dfbodyVO.setPk_corp(vo.getPk_corp());
			
			
//			HYBillVO billVO = new HYBillVO() ;
//			billVO.setParentVO(headVO) ;
//			billVO.setChildrenVO(new TzpzBVO[]{jfbodyVO , dfbodyVO}) ;
			
			headVO.setChildren(new TzpzBVO[]{jfbodyVO , dfbodyVO});

		//	singleObjectBO.saveObject(headVO.getPk_corp(), headVO);oo////;;
			voucher.saveVoucher(corpVo, headVO);
			//调用动作脚本
//			IplatFormEntry iIplatFormEntry = (IplatFormEntry) NCLocator.getInstance().lookup(IplatFormEntry.class.getName());
//			iIplatFormEntry.processAction("WRITE", IBillTypeCode.HP40, nowDatevalue.toLocalString(), null, billVO,null, null);
		}catch(Exception e){
			throw new BusinessException("保存结转凭证:"+e.getMessage());
		}
	}
	
	/**
	 * 行业的成本结转模板
	 */
	public void doCostTradeForward(CorpVO corpVo,QmclVO vo,SingleObjectBO bo,String pk_corp,String userid) throws BusinessException{
		try{
			String where = " pk_trade_accountschema in (select corptype from bd_corp where pk_corp='"+vo.getPk_corp()+"' and nvl(dr,0)=0 )  and nvl(dr,0)=0 ";
			BdTradeCostTransferVO[] hymbVOs = (BdTradeCostTransferVO[])bo.queryByCondition(BdTradeCostTransferVO.class, where,new SQLParameter()) ;
			if(hymbVOs==null||hymbVOs.length < 1){
				throw new BusinessException("公司及行业未设置成本结转模板，请检查！") ;
			}
			for(BdTradeCostTransferVO hymbVO:hymbVOs){
				//借方科目
				String jfkm = hymbVO.getPk_debitaccount() ;
				//根据行业会计科目主键找到公司会计科目主键						
				jfkm = yntBoPubUtil.getCorpAccountPkByTradeAccountPk(jfkm, pk_corp) ;
				
				//贷方科目
				String dfkm = hymbVO.getPk_creditaccount() ;												
				dfkm =yntBoPubUtil.getCorpAccountPkByTradeAccountPk(dfkm, pk_corp) ; ;
				
				//取数科目
				String qskm = hymbVO.getPk_fillaccount() ;						
				qskm = yntBoPubUtil.getCorpAccountPkByTradeAccountPk(qskm, pk_corp) ; ;
				YntCpaccountVO qscorpAccountVO = (YntCpaccountVO)bo.queryByPrimaryKey(YntCpaccountVO.class, qskm)  ;
				Integer fx = qscorpAccountVO.getDirection()==null?0:qscorpAccountVO.getDirection() ;
				
				//校验取数科目当前所有发生凭证是否都已经记账
				StringBuffer sf = new StringBuffer();
				sf.append(" select * from ynt_tzpz_h h ");
				sf.append(" join  ynt_tzpz_b b on h.pk_tzpz_h = b.pk_tzpz_h ");
				sf.append(" where h.doperatedate like '"+vo.getPeriod()+"%'  ");
				sf.append(" and  b.pk_accsubj='"+qskm+"' ");
				sf.append(" and nvl(h.dr,0)=0 ");
				sf.append(" and nvl(b.dr,0)=0 ");
				List<TzpzHVO> pzHVOs = (List<TzpzHVO>)singleObjectBO.executeQuery(sf.toString(), new SQLParameter(),new BeanListProcessor(TzpzHVO.class));
				if(pzHVOs!=null&&pzHVOs.size()>0){
					StringBuffer sf1 = new StringBuffer();
					sf1.append(" select sum(b.jfmny) as jf, sum(b.dfmny) as df from ynt_tzpz_b b ");
					sf1.append("  join ynt_tzpz_h h on b.pk_tzpz_h = h.pk_tzpz_h ");
					sf1.append("  join ynt_cpaccount t on b.pk_accsubj = t.pk_corp_account ");
					sf1.append("  where b.pk_accsubj = '"+qskm+"' and h.pk_corp = '"+pk_corp+"' ");
					sf1.append("  and h.doperatedate like '"+vo.getPeriod()+"%' ");
					sf1.append("  and nvl(h.dr,0)=0 and nvl(b.dr,0) = 0 and nvl(t.dr,0) = 0");
					ArrayList result = (ArrayList)singleObjectBO.executeQuery(sf1.toString(), new SQLParameter(),new BeanListProcessor(KmZzVO.class)) ;
					DZFDouble ye = DZFDouble.ZERO_DBL ;
					
					//本年合计
					DZFDouble jf = DZFDouble.ZERO_DBL ;
					DZFDouble df = DZFDouble.ZERO_DBL ;
											
					if(result!=null&&result.size()>0){
						KmZzVO kmzzVO = (KmZzVO)result.get(0) ;
						jf = kmzzVO.getJf()==null?DZFDouble.ZERO_DBL:kmzzVO.getJf() ;
						df = kmzzVO.getDf()==null?DZFDouble.ZERO_DBL:kmzzVO.getDf();
						
						if(fx == 0){
							//借方
							ye = jf.sub(df) ;					
						}else{
							//贷方
							ye = df.sub(jf) ;					
						}
					}
					
					if(ye.doubleValue()==0){
						//借方、贷方平衡，无需结转
						continue ;
					}
					
					//结转比例
					DZFDouble jzbl = hymbVO.getTransratio() ;
					ye = ye.multiply(jzbl).setScale(2, DZFDouble.ROUND_HALF_UP) ;
					
					saveTZBillVO(corpVo,vo,ye,jfkm,dfkm, hymbVO.getAbstracts(),userid);
					
				}
			}
		}catch(Exception e){
			throw new BusinessException("行业成本结转模板:"+e.getMessage());
		}

	}
	
	public void doCostForward(CorpVO corpVo, CpcosttransVO gsmbVO, QmclVO vo, SingleObjectBO bo, String pk_corp, String userid)throws BusinessException{
		try{
			//借方科目
			String jfkm = gsmbVO.getPk_debitaccount() ;						
			//贷方科目
			String dfkm = gsmbVO.getPk_creditaccount() ;					
			//取数科目
			String qskm = gsmbVO.getPk_fillaccount() ;						
			YntCpaccountVO gsqskmVO = (YntCpaccountVO)bo.queryByPrimaryKey(YntCpaccountVO.class, qskm) ;
			if(gsqskmVO==null){
				throw new BusinessException("公司成本结转模板的取数科目主键为"+qskm+"的科目不存在，请检查！") ;
			}
			Integer fx = gsqskmVO.getDirection()==null?0:gsqskmVO.getDirection() ;
			
			//校验取数科目当前所有发生凭证是否都已经记账
			SQLParameter sp = new SQLParameter();
			sp.addParam(qskm);
			sp.addParam(pk_corp);
			StringBuffer sf1 = new StringBuffer();
			sf1.append(" select sum(b.jfmny) as jf, sum(b.dfmny) as df from ynt_tzpz_b b ");
			sf1.append("  join ynt_tzpz_h h on b.pk_tzpz_h = h.pk_tzpz_h ");
			sf1.append("  join ynt_cpaccount t on b.pk_accsubj = t.pk_corp_account ");
			sf1.append("  where b.pk_accsubj = ? and h.pk_corp = ? ");
			sf1.append("  and h.doperatedate like '"+vo.getPeriod()+"%' ");
			sf1.append("  and nvl(h.dr,0)=0 and nvl(b.dr,0) = 0 and nvl(t.dr,0) = 0");
			List<KmZzVO> result = (List<KmZzVO>)singleObjectBO.executeQuery(sf1.toString(), sp,new BeanListProcessor(KmZzVO.class)) ;
			DZFDouble ye = DZFDouble.ZERO_DBL ;
			
			if(result!=null&&result.size()>0){
				KmZzVO kmzzVO = (KmZzVO)result.get(0) ;
				DZFDouble jf = kmzzVO.getJf()==null?DZFDouble.ZERO_DBL:kmzzVO.getJf() ;
				DZFDouble df = kmzzVO.getDf()==null?DZFDouble.ZERO_DBL:kmzzVO.getDf();
				
				if(fx == 0){
					//借方
					ye = jf.sub(df) ;					
				}else{
					//贷方
					ye = df.sub(jf) ;					
				}
			}
			if(ye.doubleValue()==0){
				//借方、贷方平衡，无需结转
				return;
			}
			
			//结转比例
			DZFDouble jzbl = gsmbVO.getTransratio() ;
			ye = ye.multiply(jzbl).setScale(2, DZFDouble.ROUND_HALF_UP) ;
			
			saveTZBillVO(corpVo,vo,ye,jfkm,dfkm, gsmbVO.getAbstracts(),userid);
		}catch(Exception e){
			throw new BusinessException("成本结转:"+e.getMessage());
		}
	}
	
	/**
	 * 根据公司制定的成本结转生成凭证
	 */
	public void doCostForward(CorpVO corpVo,QmclVO vo,SingleObjectBO bo,CpcosttransVO[] mbvos,String pk_corp,String userid)throws BusinessException{
		for(CpcosttransVO gsmbVO:mbvos){
			//比例成本结转
			doCostForward(corpVo,gsmbVO,vo,bo,pk_corp,userid);
		}
	}
	/**
	 * 校验
	 */
	public void onCostForwardCheck(CorpVO corpVO,QmclVO vo,SingleObjectBO bo) throws BusinessException {
		//判断上一期是否成本结转，如果上一期没有成本结转，则提示本期不能成本结转
		//DZFDate nowPeriod = new DZFDate(vo.getPeriod()+"-01") ;
//		String lastPeriod = "" ;
		//查询本期是否已经结转
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(vo.getPeriod());
//		sp.addParam(vo.getPk_corp());
//		String where1 = " period=? and pk_corp=? and nvl(dr,0)=0 ";
//		QmclVO[] clvos = (QmclVO[])bo.queryByCondition(QmclVO.class, where1,sp) ;
//		if(clvos != null && clvos.length > 0 && clvos[0].getIscbjz() != null && clvos[0].getIscbjz().booleanValue()){
//			throw new BusinessException("当前公司本期已进行成本结转，不能操作!") ;
//		}
		String lastPeriod= DateUtils.getPreviousPeriod(vo.getPeriod());
//		if(nowPeriod.getMonth()==1){
//			//上一期
//			lastPeriod = (nowPeriod.getYear()-1)+"-12" ;
//		}else{
//			lastPeriod = (nowPeriod.getYear())+"-"+((nowPeriod.getMonth()-1)<10?"0"+(nowPeriod.getMonth()-1):(nowPeriod.getMonth()-1)) ;
//		}
		DZFDate lastPeriodDate = new DZFDate(lastPeriod+"-01") ;
		DZFDate corpdate = corpVO.getBegindate() ;
		Integer style = corpVO.getIcostforwardstyle();
//		if(corpdate.getYear()<=lastPeriodDate.getYear()&&corpdate.getMonth()<=lastPeriodDate.getMonth()){
		if(!corpdate.after(lastPeriodDate)
				&& style != IQmclConstant.z0
				&& style != IQmclConstant.z1){
			SQLParameter sp = new SQLParameter();
			sp.addParam(lastPeriod);
			sp.addParam(vo.getPk_corp());
			String where = " period= ?  and pk_corp= ? and nvl(dr,0)=0 ";
			QmclVO[] dbVOs = (QmclVO[])bo.queryByCondition(QmclVO.class, where,sp) ;
			if(dbVOs==null||dbVOs.length<1){
				throw new BusinessException("上一期"+lastPeriod+"尚未进行成本结转，不能操作！") ;
			}
			if(dbVOs[0].getIscbjz()==null||!dbVOs[0].getIscbjz().booleanValue()){
				throw new BusinessException("上一期"+lastPeriod+"尚未进行成本结转，不能操作！") ;
			}
		}
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
