package com.dzf.zxkj.platform.services.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AdjustExrateVO;
import com.dzf.zxkj.platform.model.bdset.ExrateVO;
import com.dzf.zxkj.platform.model.bdset.RemittanceVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.bdset.ICpaccountService;
import com.dzf.zxkj.platform.services.jzcl.IVoucherTemplate;
import com.dzf.zxkj.platform.services.pzgl.IVoucherService;
import com.dzf.zxkj.platform.services.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.services.sys.IAccountService;
import com.dzf.zxkj.platform.services.sys.ICorpService;
import com.dzf.zxkj.platform.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 期末调汇处理逻辑
 * @author Administrator
 *
 */
public class AdjustRateDMO {
	
	private String sourcecode = "HCH10535";
	
	private IYntBoPubUtil yntBoPubUtil = null;
	
	private SingleObjectBO singleObjectBO ;
	
	private IVoucherService gl_tzpzserv;
	
	private ICpaccountService gl_cpacckmserv;
	
	private IVoucherTemplate vouchertempser;
	
	public IVoucherService getGl_tzpzserv() {
		return gl_tzpzserv;
	}

	public void setGl_tzpzserv(IVoucherService gl_tzpzserv) {
		this.gl_tzpzserv = gl_tzpzserv;
	}

	public ICpaccountService getGl_cpacckmserv() {
		return gl_cpacckmserv;
	}

	public void setGl_cpacckmserv(ICpaccountService gl_cpacckmserv) {
		this.gl_cpacckmserv = gl_cpacckmserv;
	}

	public AdjustRateDMO(SingleObjectBO singleObjectBO, IYntBoPubUtil yntBoPubUtil,
                         IVoucherService gl_tzpzserv, ICpaccountService gl_cpacckmserv, IVoucherTemplate vouchertempser) {
		super();
		this.singleObjectBO = singleObjectBO;
		this.yntBoPubUtil = yntBoPubUtil;
		this.gl_tzpzserv = gl_tzpzserv;
		this.gl_cpacckmserv = gl_cpacckmserv;
		this.vouchertempser = vouchertempser;
	}

	/**
	 * 期末调汇
	 * @param vo
	 * @param mapExrate
	 * @return
	 * @throws BusinessException
	 */
	public QmclVO onAdjustRate(QmclVO vo, Map<String, AdjustExrateVO> mapExrate, String userid) throws DZFWarpException {
		String res  = saveAdjustVoucher_hb(vo,mapExrate,userid);
		updateExrateVOs(mapExrate);
		vo.setResvalue(res);
		doSaveQmcl(vo,"ishdsytz", DZFBoolean.TRUE);
		return vo ;
	}
	
	/**
	 * 反期末调汇
	 * @param vo
	 * @param mapExrate
	 * @return
	 * @throws BusinessException
	 */
	public QmclVO cancelAdjustRate(QmclVO vo ) throws DZFWarpException{
		if(vo == null){
			throw new BusinessException("没有需要反期末调汇的数据") ;
		}
		//去掉勾上损益结转
		vo.setIshdsytz(DZFBoolean.FALSE) ;
		//查找已经生成过的损益结转凭证，并删除之
		String pk_qmcl = vo.getPrimaryKey() ;
		String wp = "pk_corp=? and sourcebillid= ? and sourcebilltype=? and nvl(dr,0)=0 ";
		SQLParameter sp1 = new SQLParameter();
		sp1.addParam(vo.getPk_corp());
		sp1.addParam(pk_qmcl);
		sp1.addParam(sourcecode);
		TzpzHVO[] pzHeadVOs = (TzpzHVO[])singleObjectBO.queryByCondition(TzpzHVO.class,wp ,sp1) ;
		if(pzHeadVOs!=null&&pzHeadVOs.length>0){
			for(TzpzHVO headVO:pzHeadVOs){
				if(headVO.getIshasjz()!=null&&headVO.getIshasjz().booleanValue()){
					//已有凭证记账
					throw new BusinessException("凭证号："+headVO.getPzh()+"已记账，不能反操作") ;
				}
				if(headVO.getVbillstatus()==1){
					//已有凭证审核通过
					throw new BusinessException("凭证号："+headVO.getPzh()+"已审核，不能反操作") ;
				}
			}
			//删除现金流量表
			String delsql_xjll = "update ynt_xjll set dr = 1 where pk_tzpz_h in ( select pk_tzpz_h from ynt_tzpz_h where pk_corp = ? and period=?  and sourcebilltype = ?  )";
			//删除数据
			String delsql1 = " update ynt_tzpz_b set dr = 1 where pk_tzpz_h in (select pk_tzpz_h from ynt_tzpz_h where pk_corp = ? and period=?  and sourcebilltype = ? ) ";
			String delsql2 = " update ynt_tzpz_h set dr = 1 where pk_corp = ? and period=?  and sourcebilltype = ? ";
			SQLParameter sp = new SQLParameter();
			sp.addParam(vo.getPk_corp());
			sp.addParam(vo.getPeriod());
			sp.addParam(sourcecode);
			singleObjectBO.executeUpdate(delsql_xjll, sp);
			singleObjectBO.executeUpdate(delsql1, sp);
			singleObjectBO.executeUpdate(delsql2, sp);
		}
		//更新期末结转
		singleObjectBO.update(vo,new String[]{"ishdsytz"}) ;
		return vo ;
	}
	
	/**
	 * 更新汇率档案
	 * @param mapExrate
	 * @throws BusinessException
	 */
	private void updateExrateVOs(Map<String, AdjustExrateVO> mapExrate) throws DZFWarpException{
		ArrayList<ExrateVO> list = new ArrayList<ExrateVO>();
		ExrateVO rateVo = null;
		for(AdjustExrateVO adjustExrateVo : mapExrate.values()){
			rateVo = new ExrateVO();
			rateVo.setPk_exrate(adjustExrateVo.getPk_exrate());
			rateVo.setExrate(adjustExrateVo.getAdjustrate());
			rateVo.setPk_currency(adjustExrateVo.getPk_currency());
			list.add(rateVo);
		}
		ExrateVO[] rateVos = list.toArray(new ExrateVO[0]);
		singleObjectBO.updateAry(rateVos, new String[]{"exrate"});
	}
	
	/**
	 * 保存或更新期末处理数据
	 * @param vo
	 * @param field
	 * @param value
	 * @throws BusinessException
	 */
	private void  doSaveQmcl(QmclVO vo,String field,DZFBoolean value) throws DZFWarpException{
		vo.setAttributeValue(field, value);
		if(StringUtil.isEmpty(vo.getPrimaryKey())){
			singleObjectBO.saveObject(vo.getPk_corp(), vo);;
		}else 
			singleObjectBO.update(vo,new String[]{"ishdsytz"});
	}
	/**
	 * 保存期末调汇凭证(分录不合并)
	 * @throws BusinessException 
	 */
	private void saveAdjustVoucher(QmclVO vo, HashMap<String, AdjustExrateVO> mapExrate, String userid) throws DZFWarpException{
		String pk_curr = yntBoPubUtil.getCNYPk();
		ArrayList<KmZzVO> result = queryWbpz(vo,pk_curr);
		RemittanceVO remittanceVO = queryAdjustTemplet(vo);
		if(remittanceVO == null){
			throw new BusinessException("公司未配置期末调汇模板，请配置！");
		}
		DZFDouble dftotal = DZFDouble.ZERO_DBL;
		DZFDouble jftotal = DZFDouble.ZERO_DBL;
		DZFDouble bbje = DZFDouble.ZERO_DBL;
		DZFDouble ybje = DZFDouble.ZERO_DBL;
		ArrayList<TzpzBVO> listPzb = new ArrayList<TzpzBVO>();
		for(int i=0;i<result.size();i++){
			TzpzBVO bvo = new TzpzBVO();
			KmZzVO kmzzvo = result.get(i);
			//调整汇率
			DZFDouble newRate = mapExrate.get(kmzzvo.getPk_currency()).getAdjustrate();
			int convmode = mapExrate.get(kmzzvo.getPk_currency()).getConvmode();
			int vdirect = 0;//默认借方
			if(kmzzvo.getJf() != null && kmzzvo.getJf().abs().compareTo(DZFDouble.ZERO_DBL) > 0){//借方
//				if(kmzzvo.getJf().abs().compareTo(DZFDouble.ZERO_DBL) > 0){
					bbje = kmzzvo.getJf();
					ybje = kmzzvo.getYbjf();
//				}
			}else{//贷方
				bbje = kmzzvo.getDf();
				ybje = kmzzvo.getYbdf();
				vdirect = 1;
			}
			DZFDouble newBbje = DZFDouble.ZERO_DBL;
			//根据折算模式处理
			if(convmode == 1){//原币金额÷调整汇率=本币金额（新汇率换算后本币金额）
				newBbje = ybje.mod(newRate);
			}else {//原币金额×调整汇率=本币金额（新汇率换算后本币金额）
				newBbje = ybje.multiply(newRate);
			}
			
			if(vdirect == 0){
				if(newBbje.compareTo(bbje) > 0){
					bvo.setVdirect(vdirect);
					bvo.setJfmny(newBbje.sub(bbje));
					jftotal = jftotal.add(newBbje.sub(bbje));
				}else if(newBbje.compareTo(bbje) < 0){
					bvo.setVdirect(1);
					bvo.setDfmny(bbje.sub(newBbje));
					dftotal = dftotal.add(bbje.sub(newBbje));
				}else{
					continue;
				}
			}else if(vdirect == 1){
				if(newBbje.compareTo(bbje) > 0){
					bvo.setVdirect(vdirect);
					bvo.setDfmny(newBbje.sub(bbje));
					dftotal = dftotal.add(newBbje.sub(bbje));
				}else if(newBbje.compareTo(bbje) < 0){
					bvo.setVdirect(0);
					bvo.setJfmny(bbje.sub(newBbje));
					jftotal = jftotal.add(bbje.sub(newBbje));
				}else{
					continue;
				}
			}
			bvo.setPk_accsubj(kmzzvo.getPk_accsubj());
			bvo.setPk_currency(kmzzvo.getPk_currency());
			bvo.setNrate(newRate);
			bvo.setZy(remittanceVO.getMemo());
			listPzb.add(bvo);
		}
		TzpzBVO bvo = new TzpzBVO();
		if(jftotal.compareTo(dftotal) > 0){
			bvo.setVdirect(1);
			bvo.setDfmny(jftotal.sub(dftotal));
		}else if(jftotal.compareTo(dftotal) < 0){
			bvo.setVdirect(0);
			bvo.setJfmny(dftotal.sub(jftotal));
		}else {//无差额，无需调汇
			return;
		}
		bvo.setPk_accsubj(remittanceVO.getPk_corp_account());
		bvo.setPk_currency(pk_curr);
		bvo.setNrate(new DZFDouble(1));
		bvo.setZy(remittanceVO.getMemo());
		listPzb.add(bvo);
		TzpzBVO[] pzBodyVOs = listPzb.toArray(new TzpzBVO[0]);
		TzpzHVO pzHeadVO = getTzpzHeadVo(vo, pzBodyVOs,sourcecode,userid);
		
		for(TzpzBVO bvotemp:pzBodyVOs){
			bvotemp.setPk_corp(pzHeadVO.getPk_corp());
		}
		
		pzHeadVO.setChildren(pzBodyVOs);
		
		//生成凭证
		singleObjectBO.saveObject(pzHeadVO.getPk_corp(), pzHeadVO);
	}
	
	/**
	 * 相同科目、相同币种，分录合并
	 * @param vo
	 * @param mapExrate
	 * @throws BusinessException
	 */
	private String saveAdjustVoucher_hb(QmclVO vo,Map<String, AdjustExrateVO> mapExrate,String userid) throws BusinessException{
		String pk_curr = yntBoPubUtil.getCNYPk();
		
		//获取所有的币种id相关编码
		final Map<String, String> currency_map = new HashMap<String, String>();
		getCurrencyMap(currency_map);
		IAccountService accountService = SpringUtils.getBean(IAccountService.class);
		final Map<String, YntCpaccountVO> accountmap =  accountService.queryMapByPk(vo.getPk_corp());
		
		Set<String> curset = mapExrate.keySet();
		StringBuffer curwhere = new StringBuffer();
		for(String str:curset){
			curwhere.append("'"+str+"',");
			
		}
		
		ArrayList<KmZzVO> result = queryWbpz(vo,curwhere.toString());
		result = queryQcWb(vo,curwhere.toString(),result);
		
		RemittanceVO remittanceVO = queryAdjustTemplet(vo);
		if(remittanceVO == null){
			throw new BusinessException("公司未配置期末调汇模板，请配置！");
		}
		YntCpaccountVO syKM = gl_cpacckmserv.queryById(remittanceVO.getPk_corp_account());
		YntCpaccountVO ssKM = gl_cpacckmserv.queryById(remittanceVO.getPk_out_account());
		if(!AuxiliaryConstant.ACCOUNT_FZHS_DEFAULT.equals(syKM.getIsfzhs())){
			throw new BusinessException(syKM.getAccountcode() +"科目的辅助核算项不允许为空！");
		}
		if(!AuxiliaryConstant.ACCOUNT_FZHS_DEFAULT.equals(ssKM.getIsfzhs())){
			throw new BusinessException(ssKM.getAccountcode() +"科目的辅助核算项不允许为空！");
		}
		if(result == null || result.size() == 0){
			return "该月没有需调汇的数据!";
		}
		DZFDouble dftotal = DZFDouble.ZERO_DBL;
		DZFDouble jftotal = DZFDouble.ZERO_DBL;
		DZFDouble bbje = DZFDouble.ZERO_DBL;
		DZFDouble ybje = DZFDouble.ZERO_DBL;
		DZFDouble newBbje = DZFDouble.ZERO_DBL;
		DZFDouble newRate = DZFDouble.ZERO_DBL;
		KmZzVO kmzzvo = null;
		TzpzBVO bvo = null;
		ArrayList<TzpzBVO> listPzb = new ArrayList<TzpzBVO>();
		for(int i=0;i<result.size();i++){
			bvo = new TzpzBVO();
			kmzzvo = result.get(i);
			//调整汇率
			newRate = mapExrate.get(kmzzvo.getPk_currency()).getAdjustrate();
			
			int convmode = mapExrate.get(kmzzvo.getPk_currency()).getConvmode();
			int vdirect = 0;//默认借方
			if(kmzzvo.getJf() != null && kmzzvo.getJf().abs().compareTo(DZFDouble.ZERO_DBL) > 0){//借方
//				if(kmzzvo.getJf().abs().compareTo(DZFDouble.ZERO_DBL) > 0){
					bbje = kmzzvo.getJf()==null ? DZFDouble.ZERO_DBL:kmzzvo.getJf();
					ybje = kmzzvo.getYbjf()==null ? DZFDouble.ZERO_DBL:kmzzvo.getYbjf();
//				}
			}else{//贷方
				bbje = kmzzvo.getDf()==null ? DZFDouble.ZERO_DBL : kmzzvo.getDf();
				ybje = kmzzvo.getYbdf()==null ? DZFDouble.ZERO_DBL : kmzzvo.getYbdf() ;
				vdirect = 1;
			}
			//根据折算模式处理
			if(convmode == 1){//原币金额÷调整汇率=本币金额（新汇率换算后本币金额）
				newBbje = ybje.div(newRate);//.setScale(2, DZFDouble.ROUND_HALF_UP);;
			}else {//原币金额×调整汇率=本币金额（新汇率换算后本币金额）
				newBbje = ybje.multiply(newRate);//.setScale(2, DZFDouble.ROUND_HALF_UP);
			}
			if(vdirect == 0){
				if(newBbje.compareTo(bbje) > 0){
					bvo.setVdirect(vdirect);
					bvo.setJfmny(newBbje.sub(bbje));
				}else if(newBbje.compareTo(bbje) < 0){
					bvo.setVdirect(1);
					bvo.setDfmny((bbje.sub(newBbje)));
				}else{
					continue;
				}
			}else if(vdirect == 1){
				if(newBbje.compareTo(bbje) > 0){
					bvo.setVdirect(vdirect);
					bvo.setDfmny(newBbje.sub(bbje));
				}else if(newBbje.compareTo(bbje) < 0){
					bvo.setVdirect(0);
					bvo.setJfmny((bbje.sub(newBbje)));
				}else{
					continue;
				}
			}
			bvo.setFzhsx1(kmzzvo.getFzhsx1());
			bvo.setFzhsx2(kmzzvo.getFzhsx2());
			bvo.setFzhsx3(kmzzvo.getFzhsx3());
			bvo.setFzhsx4(kmzzvo.getFzhsx4());
			bvo.setFzhsx5(kmzzvo.getFzhsx5());
			bvo.setFzhsx6(kmzzvo.getFzhsx6());
			bvo.setFzhsx7(kmzzvo.getFzhsx7());
			bvo.setFzhsx8(kmzzvo.getFzhsx8());
			bvo.setFzhsx9(kmzzvo.getFzhsx9());
			bvo.setFzhsx10(kmzzvo.getFzhsx10());
			bvo.setPk_accsubj(kmzzvo.getPk_accsubj());
			bvo.setPk_currency(kmzzvo.getPk_currency());
			bvo.setNrate(newRate);
			bvo.setZy(remittanceVO.getMemo());
			listPzb.add(bvo);
		}
		
		DZFDouble dfje = DZFDouble.ZERO_DBL;
		DZFDouble jfje = DZFDouble.ZERO_DBL;
		List<TzpzBVO> list_group1 = fenzu(listPzb);
		List<TzpzBVO> list = new ArrayList<TzpzBVO>();
		for(TzpzBVO vos : list_group1){
			dfje = vos.getDfmny() == null ? DZFDouble.ZERO_DBL:vos.getDfmny();
			jfje = vos.getJfmny() == null ? DZFDouble.ZERO_DBL:vos.getJfmny();
			
			if(SafeCompute.sub(dfje, jfje).setScale(2, DZFDouble.ROUND_HALF_UP).doubleValue() > 0){
				vos.setDfmny(dfje.sub(jfje).setScale(2, DZFDouble.ROUND_HALF_UP));//最后再进行处理
				vos.setJfmny(DZFDouble.ZERO_DBL);
				dftotal = dftotal.add(vos.getDfmny());
				vos.setDirect(1);
			}else if(SafeCompute.sub(dfje, jfje).setScale(2, DZFDouble.ROUND_HALF_UP).doubleValue()< 0){
				vos.setDirect(0);
				vos.setJfmny(jfje.sub(dfje).setScale(2, DZFDouble.ROUND_HALF_UP));//最后再进行处理
				vos.setDfmny(DZFDouble.ZERO_DBL);
				jftotal = jftotal.add(vos.getJfmny());
			}else{//如果相等则不做分录
				continue;
			}
			list.add(vos);
		}

		bvo = new TzpzBVO();
		bvo.setPk_currency(pk_curr);
		bvo.setNrate(new DZFDouble(1));
		bvo.setZy(remittanceVO.getMemo());
		if(jftotal.compareTo(dftotal) > 0 ){
			Integer fxvalue = syKM.getDirection();
			if(fxvalue==0){
				bvo.setJfmny(dftotal.sub(jftotal));
			}else{
				bvo.setDfmny(jftotal.sub(dftotal));
			}
			bvo.setPk_accsubj(remittanceVO.getPk_corp_account());
			bvo.setVdirect(fxvalue);
			list.add(bvo);
		}else if(jftotal.compareTo(dftotal) < 0){
			Integer fxcorpvalue = ssKM.getDirection();
			if(fxcorpvalue==0){
				bvo.setJfmny(dftotal.sub(jftotal));
			}else{
				bvo.setDfmny(jftotal.sub(dftotal));
			}
			bvo.setPk_accsubj(remittanceVO.getPk_out_account());
			list.add(bvo);
		}else {//无差额，无需调汇
			bvo.setPk_accsubj(remittanceVO.getPk_corp_account());
		}
		if(list.size()==0){
			return "该月没有需调汇的数据!";
		}
		
		Collections.sort(list, new Comparator<TzpzBVO>() {
			@Override
			public int compare(TzpzBVO o1, TzpzBVO o2) {
				int i =0;
				//借贷方
				Integer o1_order = 0;
				Integer o2_order = 0;
				if (o1.getJfmny() != null && o1.getJfmny().doubleValue() != 0) {
					o1_order = 0;
				} else {
					o1_order = 1;
				}
				if (o2.getJfmny() != null && o2.getJfmny().doubleValue() != 0) {
					o2_order = 0;
				} else {
					o2_order = 1;
				}
				i = o1_order.compareTo(o2_order);
				
				if (i == 0) {
					YntCpaccountVO cp1 = accountmap.get(o1.getPk_accsubj());
					YntCpaccountVO cp2 = accountmap.get(o2.getPk_accsubj());
					if (cp1 != null && cp2 != null) {// 先根据编码排序
						i = cp1.getAccountcode().compareTo(cp2.getAccountcode());
					}
				}
				if(i == 0 && !StringUtil.isEmpty(o1.getPk_currency())
						&& !StringUtil.isEmpty(o2.getPk_currency())){
					String bz1 = currency_map.get(o1.getPk_currency());
					String bz2 = currency_map.get(o2.getPk_currency());
					if(!StringUtil.isEmpty(bz1) && !StringUtil.isEmpty(bz2)){
						i = bz1.compareTo(bz2);
					}
				}
				return i;
			}
		});
		
		TzpzBVO[] pzBodyVOs = list.toArray(new TzpzBVO[list.size()]);
		TzpzHVO pzHeadVO = getTzpzHeadVo(vo, pzBodyVOs,sourcecode,userid);
		
		Map<String, YntCpaccountVO> resmap = yntBoPubUtil.querykm(pzHeadVO.getPk_corp());
		
		for(TzpzBVO bvotemp:pzBodyVOs){
			bvotemp.setVcode(resmap.get(bvotemp.getPk_accsubj()).getAccountcode());
			bvotemp.setVname(resmap.get(bvotemp.getPk_accsubj()).getAccountname());
			bvotemp.setPk_corp(pzHeadVO.getPk_corp());
		}
		
		pzHeadVO.setChildren(pzBodyVOs);
		// 生成凭证
//		CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pzHeadVO.getPk_corp());
		ICorpService corpService = SpringUtils.getBean(ICorpService.class);
		CorpVO corpvo = corpService.queryByPk(pzHeadVO.getPk_corp());
		pzHeadVO.setIsqxsy(DZFBoolean.TRUE);
		gl_tzpzserv.saveVoucher(corpvo, pzHeadVO);
//		singleObjectBO.saveObject(pzHeadVO.getPk_corp(), pzHeadVO);
		
		return "调汇成功，生成的凭证号为:"+pzHeadVO.getPzh();
	}

	private void getCurrencyMap(Map<String, String> currency_map) {
		StringBuffer currency_sql = new StringBuffer();
		currency_sql.append(" select currencycode,pk_currency from ynt_bd_currency where nvl(dr,0)=0  ");
		List<Object[]> currencylist =  (List<Object[]>) singleObjectBO.executeQuery(currency_sql.toString(), new SQLParameter(), new ArrayListProcessor());
		if(currencylist!= null && currencylist.size()>0){
			for(Object[] objs:currencylist){
				currency_map.put((String)objs[1], (String)objs[0]);
			}
		}
	}
	
	//汇兑损益查询结果按科目pk，币种pk，辅助核算项分组合并
	private List<TzpzBVO> fenzu(List<TzpzBVO> listPzb){
		Map<String, List<TzpzBVO>> map = DZfcommonTools.hashlizeObject(listPzb, new String[]{"pk_accsubj","pk_currency","fzhsx1","fzhsx2","fzhsx3","fzhsx4","fzhsx5","fzhsx6","fzhsx7","fzhsx8","fzhsx9","fzhsx10"});
		Iterator iter = map.entrySet().iterator();
		Map.Entry entry = null;
		List<TzpzBVO> list = new ArrayList<TzpzBVO>();
		List<TzpzBVO> val = new ArrayList<TzpzBVO>();
		while (iter.hasNext()) {
			entry = (Map.Entry) iter.next();
			val = (List<TzpzBVO>) entry.getValue();
			for(int i = 1; i < val.size(); i++){
				val.get(0).setJfmny(SafeCompute.add(val.get(0).getJfmny(), val.get(i).getJfmny()));
				val.get(0).setDfmny(SafeCompute.add(val.get(0).getDfmny(), val.get(i).getDfmny()));
			}
			list.add(val.get(0));
		}
		return list;
	}

	/**
	 * <p>
	 *   根据key字段的值把CircularlyAccessibleValueObject[] vos分组，返回二位数组
	 * </p>
	 *  @param key
	 * @param vos
	 * @return
	 */
	public static TzpzBVO[][] groupbyVO(String key,TzpzBVO[] vos){
		if(key == null){
			TzpzBVO[][] retvos = new TzpzBVO[1][];
			retvos[0]=vos;
			return retvos;
		}
		Vector<TzpzBVO[]> v = new Vector<TzpzBVO[]>();
		HashSet<Object> set = new HashSet<Object>();//保留已分过组的记录的痕迹
		for(int i=0; i<vos.length; i++){
			Object obj =vos[i].getAttributeValue(key);
			if(set.contains(obj)){//已做过分组
				continue;
			}else{
				set.add(obj);
			}
			TzpzBVO[] selvos = filterVO(key,obj,vos);
			if(selvos!=null && selvos.length>0){
				v.add(selvos);
			}
		}
		TzpzBVO[][] retvos = new TzpzBVO[v.size()][];
		v.copyInto(retvos);
		return retvos;
	}
	
	/**
	 * <p>
	 *   得到key字段对应的值＝value的所有CircularlyAccessibleValueObject[]
	 * </p>
	 * @param key
	 * @param value
	 * @param vos
	 * @return
	 */
	public static TzpzBVO[] filterVO(String key,Object value,TzpzBVO[] vos){
		if(key == null || value == null || vos == null){
			return null;
		}
		Vector<TzpzBVO> v = new Vector<TzpzBVO>();
		for(int i=0 ; i<vos.length; i++){
			if(vos[i] == null){
				continue;
			}
			Object obj = vos[i].getAttributeValue(key);
			if(obj!=null){
				if(obj instanceof String){
					if(value.equals(obj)){
						v.add(vos[i]);
					}
				}else if(obj instanceof DZFDouble){
					
				}else if(obj instanceof Integer){
					
				}
			}
		}
		TzpzBVO[] retvos = new TzpzBVO[v.size()];
		v.copyInto(retvos);
		return retvos;
	}
	
	

	/**
	 * 组织凭证主表数据
	 * @param vo
	 * @param headJfmny
	 * @param headDfmny
	 * @throws BusinessException
	 */
	public TzpzHVO getTzpzHeadVo(QmclVO vo,TzpzBVO[] pzBodyVOs,String sourcebilltype,String userid) throws DZFWarpException{
		DZFDouble headJfmny = DZFDouble.ZERO_DBL ;
		DZFDouble headDfmny = DZFDouble.ZERO_DBL ;
		for(TzpzBVO bodyVO:pzBodyVOs){
			headJfmny = headJfmny.add(bodyVO.getJfmny()==null?DZFDouble.ZERO_DBL:bodyVO.getJfmny()) ;
			headDfmny = headDfmny.add(bodyVO.getDfmny()==null?DZFDouble.ZERO_DBL:bodyVO.getDfmny()) ;
		}
		//凭证表头VO
		TzpzHVO pzHeadVO = new TzpzHVO() ;
		pzHeadVO.setPk_corp(vo.getPk_corp()) ;
		pzHeadVO.setPzlb(0) ;//凭证类别：记账
		pzHeadVO.setVyear(Integer.parseInt((String)vo.getPeriod().substring(0, 4)));
		pzHeadVO.setPeriod(vo.getPeriod());
		pzHeadVO.setJfmny(headJfmny) ;
		pzHeadVO.setDfmny(headDfmny) ;
		
		pzHeadVO.setCoperatorid(userid) ;
		pzHeadVO.setIshasjz(DZFBoolean.FALSE) ;
		
//		DZFDate nowDate = DZFDate.getDate(new Long(InvocationInfoProxy.getInstance().getDate())) ;

		DZFDate doperatedate = getPeroidDZFDate(vo) ;
		pzHeadVO.setDoperatedate(doperatedate) ;
		pzHeadVO.setPzh(yntBoPubUtil.getNewVoucherNo(vo.getPk_corp(), doperatedate)) ;
		pzHeadVO.setVbillstatus(8) ;
		
		//记录来源单据
		pzHeadVO.setSourcebilltype(sourcebilltype) ;
		pzHeadVO.setSourcebillid(vo.getPk_qmcl()) ;
		return pzHeadVO;
	}
	
	/**
	 * 查询公司期末调汇模板
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	private RemittanceVO queryAdjustTemplet(QmclVO vo) throws DZFWarpException {
		// String wp = " pk_corp= ? and nvl(dr,0)=0 ";
		// SQLParameter sp = new SQLParameter();
		// sp.addParam(vo.getPk_corp());
		// RemittanceVO[] vos =
		// (RemittanceVO[])singleObjectBO.queryByCondition(RemittanceVO.class,wp,sp)
		// ;
		// if(vos != null && vos.length > 0){
		// return vos[0];
		// }else{
		// //查询集团级模板
		//
		// }
		//
		// return null;
		SuperVO[] svos = vouchertempser.queryTempateByName(RemittanceVO.class.getName(), vo.getPk_corp());
		if(svos !=null && svos.length>0){
			return (RemittanceVO)svos[0];
		}
		return null;
	}
	
	/**
	 * 查询期间外币凭证分录(取出上个月的期末值)
	 * @throws BusinessException 
	 */
	public ArrayList<KmZzVO> queryWbpz(QmclVO vo,String curwhere) throws DZFWarpException{
		SQLParameter sp = new SQLParameter();
		StringBuffer sqlKm = new StringBuffer();
		//sqlKm.append(" select b.pk_accsubj,b.pk_currency,sum(b.ybjfmny) ybjf,sum(b.jfmny) jf,sum(b.ybdfmny) ybdf,sum(b.dfmny) df,  ");
		sqlKm.append(" select b.pk_accsubj,b.pk_currency,b.ybjfmny ybjf,b.jfmny jf,b.ybdfmny ybdf,b.dfmny df,  ");
		sqlKm.append(" fzhsx1,fzhsx2,fzhsx3,fzhsx4,fzhsx5,fzhsx6,fzhsx7,fzhsx8,fzhsx9,fzhsx10 from ynt_tzpz_b b ");
		sqlKm.append(" inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h ");
		sqlKm.append(" inner join ynt_cpaccount cp on b.pk_accsubj = cp.pk_corp_account ");
		sqlKm.append(" where substr(h.doperatedate,1,7) <=  ?  " );
		sp.addParam(vo.getPeriod());
		sqlKm.append(" and nvl(cp.bunhdtz,'N') = 'N'  ");//只是查询不进行汇兑的凭证
		sqlKm.append(" and (b.pk_currency  in("+curwhere.subSequence(0, curwhere.length()-1)+")  and b.pk_currency is not null)");
		sqlKm.append(" and h.pk_corp = ? ");
		sp.addParam(vo.getPk_corp());
//		sqlKm.append(" and nvl(b.dr,0)=0  and nvl(h.ishasjz,'N') = 'Y'  ");
		sqlKm.append(" and nvl(b.dr,0)=0 ");
		//sqlKm.append(" group by fzhsx1,fzhsx2,fzhsx3,fzhsx4,fzhsx5,fzhsx6,fzhsx7,fzhsx8,fzhsx9,fzhsx10,b.pk_accsubj, b.pk_currency ");
		
		ArrayList<KmZzVO> result = (ArrayList<KmZzVO>)singleObjectBO.executeQuery(sqlKm.toString(), sp,new BeanListProcessor(KmZzVO.class)) ;
		if(result ==null || result.size() == 0){
			return null;
		}
		return result;
	}
	
	/**
	 * 查询期间外币凭证分录
	 * @throws BusinessException 
	 */
	public ArrayList<KmZzVO> queryQcWb(QmclVO vo,String curwhere,ArrayList<KmZzVO> list) throws DZFWarpException{
		SQLParameter sp = new SQLParameter();
		StringBuffer sqlKm = new StringBuffer();
		sqlKm.append(" select b.pk_accsubj,b.pk_currency,sum(b.ybthismonthqc) ybjf,sum(b.thismonthqc) as jf,cp.direction as fx ");
		sqlKm.append(" from ynt_qcye b ");
		sqlKm.append(" inner join  ynt_cpaccount cp on cp.pk_corp_account = b.pk_accsubj ");
		sqlKm.append(" where  (b.pk_currency  in("+curwhere.subSequence(0, curwhere.length()-1)+")  and b.pk_currency is not null)");
		sqlKm.append(" and nvl(cp.isfzhs,'0000000000') = '0000000000'  ");//只是找没启用辅助核算的
		sqlKm.append(" and nvl(cp.bunhdtz,'N')='N' ");
		sqlKm.append(" and b.pk_corp = ? ");
		sp.addParam(vo.getPk_corp());
		sqlKm.append(" and nvl(b.dr,0)=0 and nvl(cp.isleaf,'N') = 'Y'");
		sqlKm.append(" group by b.pk_accsubj,b.pk_currency, cp.direction ");
		ArrayList<KmZzVO> result = (ArrayList<KmZzVO>)singleObjectBO.executeQuery(sqlKm.toString(), sp,new BeanListProcessor(KmZzVO.class)) ;
		
		
		//查询辅助期初
		StringBuffer sqlKm_fz = new StringBuffer();
		sqlKm_fz.append(" select b.pk_accsubj,b.pk_currency,b.ybthismonthqc ybjf,b.thismonthqc as jf,cp.direction as fx, ");
		sqlKm_fz.append("  b.fzhsx1,b.fzhsx2,b.fzhsx3,b.fzhsx4,b.fzhsx5,b.fzhsx6,b.fzhsx7,b.fzhsx8,b.fzhsx9,b.fzhsx10 ");
		sqlKm_fz.append(" from ynt_fzhsqc b ");
		sqlKm_fz.append(" inner join  ynt_cpaccount cp on cp.pk_corp_account = b.pk_accsubj ");
		sqlKm_fz.append(" where  (b.pk_currency  in("+curwhere.subSequence(0, curwhere.length()-1)+")  and b.pk_currency is not null)");
		sqlKm_fz.append(" and b.pk_corp = ? ");
		sqlKm_fz.append(" and  nvl(cp.bunhdtz,'N')='N'  ");
		sp.clearParams();
		sp.addParam(vo.getPk_corp());
		sqlKm_fz.append(" and nvl(b.dr,0)=0 and nvl(cp.isleaf,'N') = 'Y'");
		//sqlKm_fz.append(" group by b.pk_accsubj,b.pk_currency, cp.direction ");
		ArrayList<KmZzVO> result_fz = (ArrayList<KmZzVO>)singleObjectBO.executeQuery(sqlKm_fz.toString(), sp,new BeanListProcessor(KmZzVO.class)) ;
		
		list = putResQcList(list, result);//不包含辅助核算的
		
		list = putResQcList(list, result_fz);//包含辅助核算
		
		return list;
	}

	private ArrayList<KmZzVO> putResQcList(ArrayList<KmZzVO> list, ArrayList<KmZzVO> result) {
		if(result !=null && result.size() > 0){
			KmZzVO[] vos = result.toArray(new KmZzVO[0]);
			if(list == null){
				list = new ArrayList<KmZzVO>();
			}
			for(KmZzVO kmvo : vos){
				if(Integer.valueOf(kmvo.getFx()) == 0){
					if(kmvo.getJf().compareTo(DZFDouble.ZERO_DBL) < 0){
						kmvo.setDf(kmvo.getJf().abs());
						kmvo.setYbdf(kmvo.getYbjf().abs());
						kmvo.setJf(DZFDouble.ZERO_DBL);
						kmvo.setYbjf(DZFDouble.ZERO_DBL);
					}
				}else if(Integer.valueOf(kmvo.getFx()) == 1){
					if(kmvo.getJf().compareTo(DZFDouble.ZERO_DBL) < 0){
						kmvo.setDf(DZFDouble.ZERO_DBL);
						kmvo.setYbdf(DZFDouble.ZERO_DBL);
						kmvo.setJf(kmvo.getJf().abs());
						kmvo.setYbjf(kmvo.getYbjf().abs());
					}else{
						kmvo.setDf(kmvo.getJf());
						kmvo.setYbdf(kmvo.getYbjf());
						kmvo.setJf(DZFDouble.ZERO_DBL);
						kmvo.setYbjf(DZFDouble.ZERO_DBL);
					}
				}
				list.add(kmvo);
			}
		}
		return list;
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
	
	/**
	 * 判断公司是否多币种公司：需要期末调汇
	 * @return
	 * @throws BusinessException 
	 */
	private boolean isAdjustRate(String pk_corp) throws DZFWarpException{
		CorpVO corpVO = (CorpVO)singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp) ;
		if(corpVO==null){
			throw new BusinessException("公司主键为"+pk_corp+"的公司已被删除") ;
		}
		if(corpVO.getIscurr() != null && corpVO.getIscurr().booleanValue()){
			return true;
		}
		return false;
	}

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}
	
}
