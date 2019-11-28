package com.dzf.zxkj.platform.service.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.exception.ExBusinessException;
import com.dzf.zxkj.platform.model.bdset.CpcosttransVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.InvAccSetVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.jzcl.TempInvtoryVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.BdTradeCostTransferVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.icset.IInvAccSetService;
import com.dzf.zxkj.platform.service.icbill.IPurchInService;
import com.dzf.zxkj.platform.service.jzcl.ICbComconstant;
import com.dzf.zxkj.platform.service.jzcl.IndustryForward;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.icreport.IQueryLastNum;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;

import java.util.*;

/**
 * 商贸结转 ---启用库存走，无论老模式库存，还是新模式库存
 * @author zpm
 *
 */
public class Cbjz_3 {

	private SingleObjectBO singleObjectBO;
	
	private IYntBoPubUtil yntBoPubUtil = null;
	
	private IVoucherService voucher;
	
	private IQueryLastNum ic_rep_cbbserv;
	
	private ICbComconstant gl_cbconstant;
	
	private IParameterSetService parameterserv;

	private IAccountService accountService;

	public Cbjz_3(ICbComconstant gl_cbconstant,IYntBoPubUtil yntBoPubUtil,SingleObjectBO singleObjectBO,
			IVoucherService voucher,IQueryLastNum ic_rep_cbbserv,IParameterSetService parameterserv){
		this.singleObjectBO = singleObjectBO;
		this.yntBoPubUtil = yntBoPubUtil;
		this.voucher = voucher;
		this.ic_rep_cbbserv = ic_rep_cbbserv;
		this.gl_cbconstant = gl_cbconstant;
		this.parameterserv =parameterserv;
		this.accountService = SpringUtils.getBean(IAccountService.class);
	}
	
	// 启用库存 老模式---由凭证 生成  【出库】单据。
	public QmclVO save(CorpVO corpVo, QmclVO vo, String userid) throws BusinessException {
		if (vo == null)
			throw new BusinessException("没有需要成本结转的数据！");
		// 处理暂估数据
		if (vo.getZgdata() != null && vo.getZgdata().length > 0) {
			ZgVoucher zg = new ZgVoucher(gl_cbconstant,singleObjectBO,yntBoPubUtil,parameterserv);
			TzpzHVO billvo = zg.createPzvos(vo, vo.getZgdata(),userid);
			TzpzHVO nextvo = zg.queryNextcode(vo, billvo);
			//
			voucher.saveVoucher(corpVo, billvo);
			voucher.saveVoucher(corpVo, nextvo);
		}
		// 公司
//		String pk_corp = vo.getPk_corp();
		//  在 Cbjz_1  类中已做校验
//		// 检查公司是否需要成本结转
//		CostForward cf = new CostForward();
//		// 校验
//		cf.onCostForwardCheck(corpVo, vo, singleObjectBO);
		// 公司级成本结转模板
		SQLParameter sp=new SQLParameter();
		sp.addParam(vo.getPk_corp());
		String where = " pk_corp= ? and nvl(dr,0)=0 ";
		CpcosttransVO[] mbvos = (CpcosttransVO[]) singleObjectBO.queryByCondition(CpcosttransVO.class, where,sp);
		// 销售结转
		if(mbvos == null || mbvos.length == 0){//查找行业级成本结转模板
			//将行业成本结转翻译成公司级模板
			mbvos = translateCpcosttransVO(vo.getPk_corp());
			if(mbvos == null || mbvos.length == 0){
				throw new BusinessException("当前公司、行业成本结转模板都为空！请设置！");
			}
		}
		NumberForward nf = new NumberForward(yntBoPubUtil,singleObjectBO,ic_rep_cbbserv,voucher,parameterserv);
		ExBusinessException ex = new ExBusinessException("");
		List<TempInvtoryVO> zlist = null;
		Map<String, List<TempInvtoryVO>> map =null;
		Map<String,TempInvtoryVO> mapinv = nf.queryInventoryVO(corpVo.getPk_corp());
//		for (int i = 0; i < mbvos.length; i++) {
			zlist = nf.numberForward(mbvos, vo, corpVo,mapinv,userid);
			ss(zlist,ex,map);
//		}
		if (ex.getLmap()!= null && ex.getLmap().size()>0) {
			throw ex;
		}
		// 更新状态
		Cbjz_1 cb = new Cbjz_1(yntBoPubUtil,singleObjectBO,voucher);
		cb.save(corpVo,vo);
		return vo;
	}
	
	//  库存生成凭证流程 成本结转，-------由于已经是 出库单据 生成  收入凭证单据。
	//  因此在，期末结转自动生成成本结转凭证，然后回写出库单据的库存成本。
	// 启用库存，新模式
	public QmclVO savemode2(CorpVO corpVo, QmclVO vo, String userid) throws BusinessException {
		if (vo == null)
			throw new BusinessException("没有需要成本结转的数据！");
		checkIsJz(vo);
		// 处理暂估数据
		if (vo.getZgdata() != null && vo.getZgdata().length > 0) {
			//调用 生成 暂估入库单  生成凭证
			IPurchInService ic_purchserv  = (IPurchInService) SpringUtils.getBean("ic_purchinserv");
			// 存货关系设置
			IInvAccSetService ic_chkmszserv = (IInvAccSetService) SpringUtils.getBean("ic_chkmszserv");
			InvAccSetVO setvo = ic_chkmszserv.query(corpVo.getPk_corp());
			if (setvo == null) {
				throw new BusinessException("该公司存货关系设置不存在,请设置后再试!");
			}
			
			if (StringUtil.isEmpty(setvo.getZgrkdfkm())) {
				throw new BusinessException("暂估入账科目为空，请先到“库存入账设置”节点维护暂估入账科目!");
			}
			Map<String, YntCpaccountVO> ccountMap = accountService.queryMapByPk(corpVo.getPk_corp());
			YntCpaccountVO cvo = ccountMap.get(setvo.getZgrkdfkm());
			if (cvo.getIsfzhs().charAt(1) == '1') {
				if (StringUtil.isEmpty(setvo.getZgkhfz())) {
					throw new BusinessException("暂估入库贷方科目启用供应商辅助,入账设置供应商辅助核算不存在,请设置后再试!");
				}
			}else{
				setvo.setZgkhfz(null);
			}
			ic_purchserv.saveZg(vo.getZgdata(), corpVo, userid,setvo.getZgkhfz());
		}
		Map<String, YntCpaccountVO> accmap = accountService.queryMapByPk(corpVo.getPk_corp());
		CpcosttransVO[] mbvos = getCpcosttransVO(corpVo,vo,accmap);
//
//		if (mbvos.length != 1) {
//			throw new BusinessException("销售成本结转模板只能设置一个！");
//		}
		
		ExBusinessException ex = new ExBusinessException("");
		List<TempInvtoryVO> zlist = null;

		// 暂估数据
		Map<String, List<TempInvtoryVO>> map = null;
		NumberForward nf = new NumberForward(yntBoPubUtil, singleObjectBO, ic_rep_cbbserv, voucher, parameterserv);
		zlist = nf.numberJizhuanmode2(mbvos,vo, corpVo, userid,accmap);

		// 处理暂估信息
		ss(zlist, ex, map);
		if (ex.getLmap() != null && ex.getLmap().size() > 0) {
			throw ex;
		}
		// 更新状态
		Cbjz_1 cb = new Cbjz_1(yntBoPubUtil, singleObjectBO, voucher);
		cb.save(corpVo, vo);
		return vo;
	}
	
	private CpcosttransVO[] getCpcosttransVO(CorpVO corpVo, QmclVO vo, Map<String, YntCpaccountVO> accmap) {
		// 公司级成本结转模板
		// SQLParameter sp = new SQLParameter();
		// sp.addParam(vo.getPk_corp());
		// String where = " pk_corp= ? and nvl(dr,0)=0 and jztype =3 ";
		// // 公司商贸销售成本结转模板
		// CpcosttransVO[] mbvos = (CpcosttransVO[])
		// singleObjectBO.queryByCondition(CpcosttransVO.class, where, sp);
		// // 销售结转
		// if (mbvos == null || mbvos.length == 0) {// 查找行业级成本结转模板
		// // 将行业成本结转翻译成公司级模板
		// mbvos = translateCpcosttransVO(vo.getPk_corp());
		// if (mbvos == null || mbvos.length == 0) {
		// throw new BusinessException("当前公司、行业成本结转模板都为空！请设置！");
		// }
		// }

		IndustryForward gl_industryserv = (IndustryForward) SpringUtils.getBean("gl_industryserv");
		boolean is2007 = gl_industryserv.is2007(vo.getPk_corp());

		String clcode = null;
		String kcspcode = null;
		if (is2007) {
			clcode = gl_cbconstant.getJzcb_ycl2007(vo.getPk_corp());
			kcspcode = gl_cbconstant.getJzcb_kcsp2007(vo.getPk_corp());
		} else {
			clcode = gl_cbconstant.getJzcb_ycl2013(vo.getPk_corp());
			kcspcode = gl_cbconstant.getJzcb_kcsp2013(vo.getPk_corp());
		}

		CpcosttransVO[] mbvos = new CpcosttransVO[2];

		// 贷方科目
		YntCpaccountVO dfkm = getYntCpaccountVO(accmap, gl_cbconstant.getKcsp_code());
		// 借方科目
		YntCpaccountVO jfkm = getYntCpaccountVO(accmap, kcspcode);
		CpcosttransVO mbvo  =new CpcosttransVO();
		mbvo.setAbstracts("商品销售成本结转");// 540101 540201
		mbvo.setPk_creditaccount(dfkm.getPk_corp_account());
		mbvo.setPk_debitaccount(jfkm.getPk_corp_account());
		mbvos[0]=mbvo;
		
		mbvo  =new CpcosttransVO();
		dfkm = getYntCpaccountVO(accmap, gl_cbconstant.getYcl_code());
		jfkm = getYntCpaccountVO(accmap, clcode);
		mbvo.setAbstracts("材料销售成本结转");
		mbvo.setPk_creditaccount(dfkm.getPk_corp_account());
		mbvo.setPk_debitaccount(jfkm.getPk_corp_account());
		mbvos[1]=mbvo;
		
		return mbvos;
	}
	
	private YntCpaccountVO getYntCpaccountVO(Map<String, YntCpaccountVO> accmap, String code) {
		YntCpaccountVO accvo = null;
		for (Iterator i = accmap.values().iterator(); i.hasNext();) {
			accvo = (YntCpaccountVO) i.next();
			if (accvo.getAccountcode().equals(code)) {
				break;
			}
		}
		return accvo;
	}
	
	// 校验是否存在没有转总账的数据
	private void checkIsJz( QmclVO vo) {
		StringBuffer sf1 = new StringBuffer();
		sf1.append(" select cbilltype from  ynt_ictrade_h  h ");
		sf1.append(" where nvl(h.isjz,'N') = 'N' and nvl(h.dr,0) = 0  ");
		sf1.append("  and h.pk_corp = ? and h.dbilldate like ? ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getPeriod()+"%");
		List list =(List)singleObjectBO.executeQuery(sf1.toString(), sp, new ColumnListProcessor());
		
		if(list == null || list.size()==0)
			return;
		
		if(list.contains("HP70")){
			throw new BusinessException("存在未转总账的入库单,请全部转总账后再结转!");
		}
		
		if(list.contains("HP75")){
			throw new BusinessException("存在未转总账的出库单,请全部转总账后再结转!");
		}
	}
	
	public DZFDate getPeroidDZFDate(QmclVO vo){
		DZFDate period = new DZFDate(vo.getPeriod() + "-01");
		period = new DZFDate(vo.getPeriod() + "-" + period.getDaysMonth()) ;
		return period;
	}
	
	public void ss(List<TempInvtoryVO> zlist,ExBusinessException ex,Map<String, List<TempInvtoryVO>> map){
		if (zlist != null && zlist.size() > 0) {
			if (ex.getLmap() == null) {
				map = new HashMap<String, List<TempInvtoryVO>>();
				map.put(UUID.randomUUID().toString(), zlist);
				ex.setLmap(map);
			} else {
				ex.getLmap().put(UUID.randomUUID().toString(), zlist);
			}
		}
	}
	
	/**
	 * 将行业成本结转翻译成公司级模板
	 */
	public CpcosttransVO[] translateCpcosttransVO(String pk_corp) throws BusinessException{
		CpcosttransVO[] vos = null;
		try{
			String where = " pk_trade_accountschema in (select corptype from bd_corp where pk_corp='"+pk_corp+"' and nvl(dr,0)=0 )  and nvl(dr,0)=0 ";
			BdTradeCostTransferVO[] hymbVOs = (BdTradeCostTransferVO[])singleObjectBO.queryByCondition(BdTradeCostTransferVO.class, where,new SQLParameter()) ;
			if(hymbVOs==null||hymbVOs.length < 1){
				throw new BusinessException("公司及行业未设置成本结转模板，请检查！") ;
			}
			vos = new CpcosttransVO[hymbVOs.length];
			for(int i = 0; i < hymbVOs.length; i++){
				BdTradeCostTransferVO hymbVO = hymbVOs[i];
				//借方科目
				String jfkm = hymbVO.getPk_debitaccount();
				//根据行业会计科目主键找到公司会计科目主键						
				jfkm = yntBoPubUtil.getCorpAccountPkByTradeAccountPk(jfkm, pk_corp);
				
				//贷方科目
				String dfkm = hymbVO.getPk_creditaccount();												
				dfkm =yntBoPubUtil.getCorpAccountPkByTradeAccountPk(dfkm, pk_corp);
				
				//取数科目
				String qskm = hymbVO.getPk_fillaccount();						
				qskm = yntBoPubUtil.getCorpAccountPkByTradeAccountPk(qskm, pk_corp);
				
				vos[i] = new CpcosttransVO();
				vos[i].setPk_debitaccount(jfkm);
				vos[i].setPk_creditaccount(dfkm);
				vos[i].setPk_fillaccount(qskm);
				vos[i].setAbstracts(hymbVO.getAbstracts());
			}
		}catch(Exception e){
			throw new BusinessException(e.getMessage());
		}
		return vos;
	}
}