package com.dzf.zxkj.platform.service.qcset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.sys.BdTradeAssetCheckVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zcgl.ZcZzVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.zcgl.IAssetcardReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 固定资产同步
 * 
 */
@Component("gdzcsync")
public class GDZcSyncImpl {


	@Autowired
	private IAssetcardReport am_rep_zczzserv;
	
	@Autowired
	private IYntBoPubUtil yntBoPubUtil = null;
	
	@Autowired
	private ICpaccountService cpaccountService = null;
	
	@Autowired
	private IQmgzService gzservice ;

	@Autowired
	private ICorpService corpService;
	

	public String getCodeRule(String pk_corp){
		String accountrule = cpaccountService.queryAccountRule(pk_corp);
		return accountrule;
	}
	

	public void saveAssetSync(QcYeVO[] qcyevos, String pk_corp, SingleObjectBO singleObjectBO)
			throws DZFWarpException {
		if ((qcyevos == null) || (qcyevos.length == 0))
			return;
		CorpVO vo1 = (CorpVO)singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
		BdTradeAssetCheckVO[] assetcheckVOs = getAssetcheckVOs(pk_corp,singleObjectBO);
		if ((assetcheckVOs == null) || (assetcheckVOs.length == 0))
			return;
		String pk_curr = yntBoPubUtil.getCNYPk();
		Map<String, QcYeVO> maps = hashlizeObject(qcyevos);
		
		DZFDate begdate =new DZFDate(DateUtils.getPreviousMonth(vo1.getBegindate().getMillis()));
		boolean isgz = gzservice.checkLaterMonthGz(pk_corp, DateUtils.getPeriod(begdate));
		
		if(isgz){
			throw new BusinessException("已关账，不能期初同步!");
		}
		
		Map<String, DZFBoolean> iachange = new HashMap<String,DZFBoolean>();
		for (QcYeVO qcyevo : qcyevos){
			iachange.put(qcyevo.getPk_accsubj(), DZFBoolean.FALSE);	
		}
		
		for (BdTradeAssetCheckVO assetcheckVO : assetcheckVOs) {
			String pk_account = assetcheckVO.getPk_glaccount();
			if(IGlobalConstants.DefaultGroup.equals(assetcheckVO.getPk_corp())){//集团级数据
				pk_account = yntBoPubUtil.getCorpAccountPkByTradeAccountPk(
						assetcheckVO.getPk_glaccount(), pk_corp);
			}else{//公司级数据
				YntCpaccountVO vo = (YntCpaccountVO)singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, pk_account);
				if(vo.getIsleaf() == null || !vo.getIsleaf().booleanValue()){
					throw new BusinessException("科目:"+vo.getAccountname()+"，为非末级科目 ！");
				}
			}
			double totalAssetmny = getTotalAssetMny(qcyevos[0].getPk_corp(),
					"", assetcheckVO.getAssetproperty().intValue(),
					assetcheckVO.getPk_assetcategory(), assetcheckVO
							.getAssetaccount().intValue(), true,singleObjectBO);
			for (QcYeVO qcyevo : qcyevos){
				
				if (qcyevo.getPk_accsubj().equals(pk_account)) {
					DZFDate jzdate = vo1.getBegindate();
					qcyevo.setChildren(null);
					DZFDouble oldvalue = qcyevo.getThismonthqc() == null ? DZFDouble.ZERO_DBL : new DZFDouble(qcyevo.getThismonthqc().doubleValue());
					DZFDouble newvalue = new DZFDouble(totalAssetmny);
					DZFDouble thismonthqctemp  = DZFDouble.ZERO_DBL;
					if(iachange.get(qcyevo.getPk_accsubj()).booleanValue()){
						thismonthqctemp = thismonthqctemp.add(oldvalue);
					}
					qcyevo.setThismonthqc(thismonthqctemp.add(newvalue));
					iachange.put(qcyevo.getPk_accsubj(), DZFBoolean.TRUE);
					qcyevo.setPk_currency(pk_curr);
					qcyevo.setDoperatedate(vo1.getBegindate());
					if(jzdate != null){
						qcyevo.setVyear(Integer.valueOf(jzdate.toString().substring(0,4)));
						qcyevo.setPeriod(jzdate.toString().substring(0, 7));
					}
					//反算yearqc，本年期初
					calcYearqc(qcyevo);
					if (qcyevo.getPk_qcye() == null) {
						if (qcyevo.getThismonthqc().compareTo(DZFDouble.ZERO_DBL) > 0
								|| qcyevo.getYearqc().compareTo(
										DZFDouble.ZERO_DBL) > 0) {
							
							singleObjectBO.saveObject(pk_corp, qcyevo);
						}
					} else {
						singleObjectBO.update(qcyevo);
					}
					
					String parentAcccode = getParentAccCode(qcyevo.getVcode(),pk_corp);
					saveIncAccMny(pk_curr,vo1,pk_corp, maps, parentAcccode, oldvalue,
							newvalue,singleObjectBO);
				}
			}
		}
		return;
	}

	//反算yearqc，本年期初
	private void calcYearqc(QcYeVO QcYeVO) {
		int vdirect = QcYeVO.getDirect() == null ? -1 : QcYeVO.getDirect()
				.intValue();
		DZFDouble bnjffs = QcYeVO.getYearjffse() == null ? DZFDouble.ZERO_DBL
				: QcYeVO.getYearjffse();
		DZFDouble bndffs = QcYeVO.getYeardffse() == null ? DZFDouble.ZERO_DBL
				: QcYeVO.getYeardffse();
		DZFDouble thismonthqc = QcYeVO.getThismonthqc() == null ? new DZFDouble(
				0) : QcYeVO.getThismonthqc();
		if (vdirect == 0) {
			QcYeVO.setYearqc(thismonthqc.add(bndffs).sub(bnjffs));
		} else if (vdirect == 1) {
			QcYeVO.setYearqc(thismonthqc.add(bnjffs).sub(bndffs));
		}
	}

	private BdTradeAssetCheckVO[] getAssetcheckVOs(String pk_corp,SingleObjectBO singleObjectBO)
			throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		//先查询公司
		String where = " nvl(dr,0)=0 and pk_corp = ?  order by assetproperty,pk_assetcategory ";
		BdTradeAssetCheckVO[] assetcheckVOs = (BdTradeAssetCheckVO[]) singleObjectBO
				.queryByCondition(BdTradeAssetCheckVO.class, where, sp);
		if(assetcheckVOs == null || assetcheckVOs.length == 0){
			//查询集团
			SQLParameter sp1 = new SQLParameter();
			sp1.addParam(IGlobalConstants.DefaultGroup);
			sp1.addParam(pk_corp);
			where = " nvl(dr,0)=0 and pk_corp = ?  and  pk_trade_accountschema in  (select corptype from bd_corp where pk_corp=? and nvl(dr,0)=0 )  order by assetproperty,pk_assetcategory ";
			assetcheckVOs = (BdTradeAssetCheckVO[]) singleObjectBO
					.queryByCondition(BdTradeAssetCheckVO.class, where, sp1);
		}
		if ((assetcheckVOs == null) || (assetcheckVOs.length == 0)) {
			// String hint = String.format("没有找到公司%s所对应的行业资产与总账对照表记录！",
			// new Object[] { pk_corp });
			// //System.out.print(hint);
			// Logger.warn(hint);
			throw new BusinessException("没有找到公司[" + pk_corp
					+ "]所对应的行业资产科目对照记录！");
		}
		return assetcheckVOs;
	}

	
	public  Map<String, QcYeVO> hashlizeObject(QcYeVO[] qcyevos) 
			throws DZFWarpException {
		Map<String, QcYeVO> result = new HashMap<String, QcYeVO>();
		if (qcyevos == null || qcyevos.length == 0)
			return result;
		String key = null;
		for (int i = 0; i < qcyevos.length; i++) {
			key = qcyevos[i].getVcode();
			result.put(key, qcyevos[i]);
		}
		return result;
	}
	
	//计算取得上级科目（zpm 修改）
	
	public String getParentAccCode(String childCode,String pk_corp){
		String coderule = getCodeRule(pk_corp);
		return DZfcommonTools.getParentCode(childCode, coderule);
	}
	
	
//	private String getParentAccCode(String acccode) {
//		if ((acccode == null) || (acccode.equals("")))
//			return "";
//		if (acccode.length() < 4)
//			return "";
//		return acccode.substring(0, acccode.length() - 2);
//	}

	private void saveIncAccMny(String pk_curr,CorpVO vo1,String pk_corp, Map<String,QcYeVO> maps,
			String acccode, DZFDouble oldvalue, DZFDouble newvalue,SingleObjectBO singleObjectBO)
			throws DZFWarpException {
		if (acccode == null || "".equals(acccode))
			return;
		QcYeVO qcyevo = maps.get(acccode);
		if (qcyevo != null) {
			DZFDate jzdate = vo1.getBegindate();
			qcyevo.setChildren(null);
			DZFDouble mny = qcyevo.getThismonthqc() == null ? new DZFDouble(
					0) : qcyevo.getThismonthqc();
			// qcyevo.setThismonthqc(mny.sub(oldvalue).add(newvalue));
			qcyevo.setThismonthqc(SafeCompute.add(
					SafeCompute.sub(mny, oldvalue), newvalue));
			qcyevo.setDoperatedate(vo1.getBegindate());
			qcyevo.setPk_currency(pk_curr);
			if(jzdate != null){
				qcyevo.setVyear(Integer.valueOf(jzdate.toString().substring(0,4)));
				qcyevo.setPeriod(jzdate.toString().substring(0, 7));
			}
			calcYearqc(qcyevo);
			if (qcyevo.getPk_qcye() == null) {
				if (qcyevo.getThismonthqc().compareTo(DZFDouble.ZERO_DBL) > 0
						|| qcyevo.getYearqc().compareTo(DZFDouble.ZERO_DBL) > 0) {
					singleObjectBO.saveObject(pk_corp, qcyevo);
				}
			} else {
				singleObjectBO.update(qcyevo);
			}
			// getHYPubBO().update(qcyevo);

			String parentAcccode = getParentAccCode(qcyevo.getVcode(),pk_corp);
			saveIncAccMny(pk_curr,vo1,pk_corp, maps, parentAcccode, oldvalue,
					newvalue,singleObjectBO);
		}
	}

	private double getTotalAssetMny(String pk_corp, String period,
			int assetproperty, String pk_assetcategory, int assetacckind,
			boolean isinitPeriod,SingleObjectBO singleObjectBO) throws DZFWarpException {
		if (isinitPeriod) {
			String sumfield = "";
			switch (assetacckind) {
			case 0:
				sumfield = "assetmny";
				break;
			case 1:
				sumfield = "depreciation";
				break;
			case 2:
				sumfield = "assetnetvalue";
				break;
			case 3:
				sumfield = "qcnetvalue";//取期初净值
				break;
			}
			CorpVO cpvo = corpService.queryByPk(pk_corp);
			if(cpvo!=null && cpvo.getBusibegindate()!=null){
				String catefilter = "";
				if ((pk_assetcategory != null) && (!pk_assetcategory.equals(""))) {
					catefilter = "assetcategory='" + pk_assetcategory + "'";
				}
				SQLParameter sp =  new SQLParameter();
				StringBuffer sql = new StringBuffer();
				sql.append(" select sum("+sumfield+") as assetmny from ynt_assetcard ");
				sql.append(" where pk_corp=? and nvl(isperiodbegin,'N')='Y' ");
				sql.append(" and substr(accountdate,0,7) = ?  and nvl(dr,0)=0 ");
				sql.append("  and assetcategory in (select pk_assetcategory from ynt_category where assetproperty=?) ");
				if(!StringUtil.isEmpty(catefilter)){
					sql.append(" and "+catefilter);
				}
				sp.addParam(pk_corp);
				sp.addParam(DateUtils.getPeriod(cpvo.getBusibegindate()));
				sp.addParam(Integer.valueOf(assetproperty));
				
				Object[] values = (Object[]) singleObjectBO.executeQuery(sql.toString(),
						sp, new ArrayProcessor());
				return (values == null) || (values.length == 0)
						|| (values[0] == null) ? 0.0D : Double
								.parseDouble(values[0].toString());
			}else{
				return 0.0D;
			}
		}
		DZFDate periodBeginDate = DZFDate.getDate(period + "-01");
		DZFDate periodEndDate = periodBeginDate.getDateAfter(periodBeginDate
				.getDaysMonth() - 1);
		String where = String.format("b.assetproperty=%d",
				new Object[] { Integer.valueOf(assetproperty) });
		if ((pk_assetcategory != null) && (!pk_assetcategory.equals("")))
			where = where
					+ String.format(" and b.pk_assetcategory='%s'",
							new Object[] { pk_assetcategory });
		ZcZzVO[] zczzVOs = am_rep_zczzserv.queryAssetcardTotal(pk_corp,
				periodBeginDate, periodEndDate, where, new SQLParameter(),null,null,null);
		if ((zczzVOs == null) || (zczzVOs.length == 0))
			return 0.0D;

		ZcZzVO zczzVO = zczzVOs[(zczzVOs.length - 1)];
		switch (assetacckind) {
		case 0:
			return zczzVO.getYzye() == null ? 0.0D : zczzVO.getYzye()
					.getDouble();
		case 1:
			return zczzVO.getLjye() == null ? 0.0D : zczzVO.getLjye()
					.getDouble();
		case 2:
			return zczzVO.getJzye() == null ? 0.0D : zczzVO.getJzye()
					.getDouble();
		}
		return 0.0D;
	}
}
