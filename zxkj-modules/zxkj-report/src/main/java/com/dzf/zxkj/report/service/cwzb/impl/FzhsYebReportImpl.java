package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DAOException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;
import com.dzf.zxkj.platform.model.bdset.GxhszVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FzYebVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwzb.IFzhsYebReport;
import com.dzf.zxkj.report.utils.VoUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 辅助核算余额表
 * 
 * @author llh
 * 
 */
@SuppressWarnings("all")
@Service("gl_rep_fzyebserv")
public class FzhsYebReportImpl implements IFzhsYebReport {

	@Reference
	private IZxkjPlatformService zxkjPlatformService;

	private SingleObjectBO singleObjectBO = null;


	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}



	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	/**
	 * 取指定期间之前的最近的结账期间
	 * 
	 * @param pk_corp
	 * @param period
	 * @param singleObjectBO
	 * @return
	 */
	public static String getLastClosePeriod(String pk_corp, String period, SingleObjectBO singleObjectBO)
			throws DAOException {
		String sql = "select max(period) from ynt_qmjz where pk_corp=? and period<? and nvl(jzfinish,'N')='Y' and nvl(dr,0)=0 ";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_corp);
		parameter.addParam(period);
		Object obj = singleObjectBO.executeQuery(sql, parameter, new ColumnProcessor());
		String closePeriod = null;
		if (obj != null)
			closePeriod = (String) obj;
		return closePeriod;
	}

	/**
	 * 辅助余额表查询数据
	 * 
	 * @param paramVo
	 * @return
	 * @throws BusinessException 
		
	 */
	@Override
	public List<FzYebVO> getFzYebVOs(KmReoprtQueryParamVO paramVo) throws DZFWarpException {
		
		/** 直接传辅助项的序号 */
		Integer fzlb = Integer.valueOf(paramVo.getFzlb());
		final String fzlb_str = fzlb.toString();
		
		String pk_corp = paramVo.getPk_corp();
		CorpVO corp = zxkjPlatformService.queryCorpByPk(pk_corp);
		if (corp == null)
			return null;
		AuxiliaryAccountHVO hvo = zxkjPlatformService.queryHByCode(pk_corp, paramVo.getFzlb());
		/**  库存 */  
		boolean isictogl = corp != null && corp.getBbuildic() != null && IcCostStyle.IC_ON.equals(corp.getBbuildic())&& fzlb == 6;
		List<FzYebVO>  list= null;
		
		List<FzYebVO>  list_res= null;
		
		if(isictogl){
			list =getFzyebVoICFz6(paramVo);
		}else{
			list =getFzyebVoICFz(paramVo);
		}
		/** 合并所有的辅助项目，包含未发生的数据 */
		addNoDataFzxm(fzlb_str, pk_corp, list);
		
		/** 按照编码排序 */
		Collections.sort(list, new Comparator<FzYebVO>() {
			@Override
			public int compare(FzYebVO o1, FzYebVO o2) {
				String code1=  (String) o1.getAttributeValue("fzhsx"+fzlb_str+"Code");
				String code2=  (String) o2.getAttributeValue("fzhsx"+fzlb_str+"Code");
				int res = code1.compareTo(code2);
				if(res == 0){
					String secendcode1 = code1+ (o1.getAttributeValue("accCode") == null ? "" :(String)o1.getAttributeValue("accCode"));
					String secendcode2 = code2+ (o2.getAttributeValue("accCode") == null ? "" :(String)o2.getAttributeValue("accCode"));
					res = secendcode1.compareTo(secendcode2);
				}
				
				return res;
			}
		});
		
		if(((paramVo.getXswyewfs()!=null && paramVo.getXswyewfs().booleanValue())
				|| (paramVo.getIshowfs() !=null && !paramVo.getIshowfs().booleanValue()))
				&& list!=null && list.size()>0){
			Set<String> parentset = new HashSet<String>();
			list_res = new ArrayList<FzYebVO>();
			for(FzYebVO vo:list){
				if (hvo != null) {
					vo.setPk_fzlb(hvo.getPk_auacount_h());
				}
				if(VoUtils.getDZFDouble(vo.getBqfsjf()).doubleValue()==0
						&& VoUtils.getDZFDouble(vo.getBqfsdf()).doubleValue() ==0
						&& VoUtils.getDZFDouble(vo.getQmye()).doubleValue() ==0
						){/** 无余额无发生不显示 */
					continue;
				}
				
				/** 有余额无发生不显示 */
				if(paramVo.getIshowfs() !=null && !paramVo.getIshowfs().booleanValue()){
					if(VoUtils.getDZFDouble(vo.getBqfsjf()).doubleValue()==0
							&& VoUtils.getDZFDouble(vo.getBqfsdf()).doubleValue() ==0
							&& VoUtils.getDZFDouble(vo.getQmye()).doubleValue() !=0
							){
						continue;
					}
				}
				/** 找对应的上级，如果上级没有，则显示出来 */
				String fzhsid  = (String) vo.getAttributeValue("fzhsx"+fzlb_str);
				
				if(!StringUtil.isEmpty(fzhsid)){
					if(!StringUtil.isEmpty(vo.getPk_acc())){
						/** 如果上级没有，则显示出来 */
						if(!parentset.contains(fzhsid)){
							for(FzYebVO vo1:list){
								if(StringUtil.isEmpty(vo1.getPk_acc())
										 && fzhsid.equals((String) vo1.getAttributeValue("fzhsx"+fzlb_str))){
									list_res.add(vo1);
									break;
								}
							}
							parentset.add(fzhsid);
						}
					}else{
						parentset.add(fzhsid);
					}
				}
				vo.setPk_currency(paramVo.getPk_currency());
				list_res.add(vo);
			}
			return list_res;
		}
		
		return list;
	}

	private void addNoDataFzxm(final String fzlb_str, String pk_corp, List<FzYebVO> list) {
		AuxiliaryAccountBVO[] bvos = zxkjPlatformService.queryBByFzlb(pk_corp, fzlb_str);
		
		List<String> ids = new ArrayList<String>();
		
		if(list!=null && list.size()>0){
			for(FzYebVO yevo:list){
				ids.add((String) yevo.getAttributeValue("fzhsx"+fzlb_str));
			}
		}
		
		if(bvos!=null && bvos.length>0){
			for(AuxiliaryAccountBVO vo:bvos){
				if(!ids.contains(vo.getPk_auacount_b())){
					FzYebVO tvo = new FzYebVO();
					tvo.setAttributeValue("fzhsx"+fzlb_str, vo.getPk_auacount_b());
					tvo.setAttributeValue("fzhsx"+fzlb_str+"Code", vo.getCode());
					tvo.setAttributeValue("fzhsx"+fzlb_str+"Name", vo.getName());
					tvo.setAttributeValue("fzhsxCode", vo.getCode());
					tvo.setAttributeValue("fzhsxName", vo.getName());
					tvo.setAccLevel(0);
					list.add(tvo);
				}
			}
		}
	}
	
	private List<FzYebVO> getFzyebVoICFz(KmReoprtQueryParamVO paramVo) {
		String pk_corp = paramVo.getPk_corp();
		/** 查询开始期间，如2016-02 */
		String beginPeriod = paramVo.getQjq(); 
		/** 查询结束期间，如2016-05 */
		String endPeriod = paramVo.getQjz(); 
		if (beginPeriod == null)
			throw new BusinessException("查询开始期间不能为空！");
		if (endPeriod == null)
			throw new BusinessException("查询结束期间不能为空！");
		if (beginPeriod.compareTo(endPeriod) > 0)
			throw new BusinessException("查询开始期间不能大于结束期间！");
		/** 查询开始日期，如2016-02-01  */
		DZFDate beginDate = DZFDate.getDate(beginPeriod + "-01");
		/** llh：需求确定，跨年查时，本期累计从查询结束期间所在年初进行累计 */
		/** 查询结束期间所在年初 */
		String yearsumPeriod = endPeriod.substring(0, 4) + "-01"; 

		/** 取beginPeriod之前最大已结账期间（暂时注掉，因为辅助核算的年结还没有做） */
		String lastClosePeriod = null;

		String kmFrom = paramVo.getKms_first();
		String kmTo = paramVo.getKms_last();
		int levelFrom = paramVo.getCjq().intValue();
		int levelTo = paramVo.getCjz().intValue();
		if (levelFrom > levelTo)
			throw new BusinessException("起始级次不能大于截止级次！");
		DZFBoolean isHasJZ = paramVo.getIshasjz();

		/** 辅助类别 */
		/** 直接传辅助项的序号 */
		Integer fzlb = Integer.valueOf(paramVo.getFzlb());

		String fzxFrom = paramVo.getFzxm();
		/** 是否显示科目 */
		DZFBoolean isDispKM = paramVo.getXskm(); 

		/** 取公司建账日期、科目编码规则 */
		String corpsql = "select a.begindate,a.accountcoderule,b.coderule from bd_corp a left join ynt_tdaccschema b on a.corptype=b.pk_trade_accountschema where nvl(a.dr,0)=0 and a.pk_corp=?";
		SQLParameter sqlparameter = new SQLParameter();
		sqlparameter.addParam(pk_corp);
		Object[] arr1 = (Object[]) singleObjectBO.executeQuery(corpsql, sqlparameter, new ArrayProcessor());
		DZFDate corpDate = null;
		String codeRule = "";
		if (arr1[0] != null)
			corpDate = DZFDate.getDate(arr1[0].toString());
		if (arr1[1] != null) /** 从公司档案上取编码规则，不从科目方案上取 */
			codeRule = (String) arr1[1];
		else /** null为升级前的数据，默认为4/2/2/2/2 */
			codeRule = "4/2/2/2/2";

		if(corpDate == null){
			 throw new BusinessException("公司建账日期为空");
		}
		/** 建账年初期间，如2015-01 */
		String corpYear = String.format("%d-%02d", corpDate.getYear(), 1);
		/** 建账期间，如2015-03 */
		String corpPeriod = String.format("%d-%02d", corpDate.getYear(), corpDate.getMonth()); 

		/** 余额表最早从建账年初开始查，如果 beginPeriod<corpYear，则将beginPeriod直接调整为corpYear */
		if (beginPeriod.compareTo(corpYear) < 0) {
			throw new BusinessException(String.format("查询开始期间不能在建账年度(%d)之前！", corpDate.getYear()));
		}

		String[] arr2 = codeRule.split("/");
		int[] levelLens = new int[arr2.length];
		int len = 0;
		for (int i = 0; i < arr2.length; i++) {
			len += Integer.parseInt(arr2[i]);
			levelLens[i] = len;
		}
		/** 输入的截止级次不能超过当前科目编码规则范围 */
		if (levelTo > arr2.length)
			levelTo = arr2.length;

		List<FzYebVO> qcdata1_1 = new ArrayList<FzYebVO>();
		List<FzYebVO> qcdata1_2 = qcdata1_1;
		List<FzYebVO> qcdata2_1 = qcdata1_1;
		List<FzYebVO> qcdata2_2 = qcdata1_1;
		List<FzYebVO> qcdata3_1 = qcdata1_1;
		List<FzYebVO> qcdata3_2 = qcdata1_1;

		/** 公共的sql拼串部分 */
		String kmcond = this.getKmCondStr(pk_corp, kmFrom, kmTo, levelFrom, "c",paramVo.getPk_currency());
		String fzxcond = this.getFzxCondStr(pk_corp, fzxFrom, "b");
		String accLevelCode = String.format("substr(c.accountcode,1,%d)", levelLens[levelTo - 1]);

		String fzhsx = "fzhsx" + fzlb;
		String fzhsx1 = String.format("%1$s,b.code as %1$sCode,b.name as %1$sName", fzhsx); //"fzhsx1,b.code as fzhsx1Code,b.name as fzhsx1Name";

		/** select分组项字段 */
		StringBuilder sb = new StringBuilder();
		/** 当不显示科目时，行上科目为空 */
		if (isDispKM.booleanValue())
			sb.append(accLevelCode).append(" as accCode, ");
		else
			sb.append("null as accCode, ");
		sb.append(fzhsx1).append(", ");
		String selectgroupstr = sb.toString();

		/** join部分 */
		sb = new StringBuilder();
		sb.append("left join ynt_fzhs_b b on a.");  
		sb.append(fzhsx);
		sb.append("=b.pk_auacount_b ");
		sb.append("inner join ynt_cpaccount c on a.pk_accsubj=c.pk_corp_account ");
		String joinstr_fz_acc = sb.toString();

		/** 公共条件部分 */
		sb = new StringBuilder();
		sb.append("a.pk_corp=?");
		sb.append(" and ").append(kmcond);
		if (!StringUtil.isEmpty(fzxcond))
			sb.append(" and ").append(fzxcond);
		sb.append(" and nvl(a.dr,0)=0 ");
		String commoncondstr = sb.toString();

		/** group by部分 */
		sb = new StringBuilder();
		sb.append(fzhsx);
		sb.append(",b.code,b.name");
		if (isDispKM.booleanValue()) {
			sb.append(',').append(accLevelCode);
		}
		String groupstr = sb.toString();

		/** A: 期初 */
		/** 当前科目名称、当前科目级次、当前科目方向等，最后从会计科目map中取 */
		SQLParameter sp;
		SQLParameter sp1 = new SQLParameter();
		sp1.addParam(pk_corp);
		if(!StringUtil.isEmpty(paramVo.getPk_currency())){
			sp1.addParam(paramVo.getPk_currency());
		}
		/** 查询开始期间在首次已结账期间及之前，需查找建账期初或建账年初 */
		if (lastClosePeriod == null) { 
			/** 如果 beginPeriod<corpPeriod */
			if (beginPeriod.compareTo(corpPeriod) < 0) { 
				/** A.1: 最近期初（建账年初） */
				sb = new StringBuilder();
				sb.append("select '' as rq, '' as period, ");
				sb.append(selectgroupstr);
				sb.append(" nvl(sum((case when c.direction=1 then -1 else 1 end)*yearqc),0) as qcye,  ");
				sb.append(" nvl(sum((case when c.direction=1 then -1 else 1 end)*ybyearqc),0) as ybqcye, ");//原币数据
				sb.append(" 0 as bqfsjf, 0 as bqfsdf, 0 as bnljjf, 0 as bnljdf, ");
				sb.append(" 0 as ybbqfsjf, 0 as ybbqfsdf, 0 as ybbnljjf, 0 as ybbnljdf ");//原币数据
				sb.append("from ynt_fzhsqc a ");
				sb.append(joinstr_fz_acc);
				sb.append(" where ").append(commoncondstr);
				sb.append(" and c.isleaf='Y' "); //支持辅助核算后，只能查最明细数据，然后自己汇
				if(!StringUtil.isEmpty(paramVo.getPk_currency())){
					sb.append(" and a.pk_currency = ? ");
				}
				sb.append("   ");
				sb.append(" group by ").append(groupstr);

				qcdata1_1 = (List<FzYebVO>) singleObjectBO.executeQuery(sb.toString(), sp1,
						new BeanListProcessor(FzYebVO.class));

				/** 查询开始期间在建账期间前，但不是建账年初的，累借累贷计入期初余额 */
				if (beginPeriod.compareTo(corpYear) > 0) { 
					/** A.2: 建账年初的借方发生和贷方发生合计 */
					sb = new StringBuilder();
					sb.append("select '' as rq, '' as period, ");
					sb.append(selectgroupstr);
					sb.append("nvl(sum(yearjffse),0)-nvl(sum(yeardffse),0) as qcye, ");
					sb.append("nvl(sum(ybyearjffse),0)-nvl(sum(ybyeardffse),0) as ybqcye, ");
					sb.append(" 0 as bqfsjf, 0 as bqfsdf, 0 as bnljjf, 0 as bnljdf , ");
					sb.append(" 0 as ybbqfsjf, 0 as ybbqfsdf, 0 as ybbnljjf, 0 as ybbnljdf  ");
					sb.append("from ynt_fzhsqc a ");
					sb.append(joinstr_fz_acc);
					sb.append(" where ").append(commoncondstr);
					sb.append(" and c.isleaf='Y' "); /** 支持辅助核算后，只能查最明细数据，然后自己汇 */
					if(!StringUtil.isEmpty(paramVo.getPk_currency())){
						sb.append(" and a.pk_currency = ? ");
					}
					sb.append(" group by ").append(groupstr);

					qcdata1_2 = (List<FzYebVO>) singleObjectBO.executeQuery(sb.toString(), sp1,
							new BeanListProcessor(FzYebVO.class));
				}
			} else { /** 如果 beginPeriod>=corpPeriod，但<=最近结账期间  //if (beginPeriod.compareTo(corpPeriod) >= 0) */
				/** A.1: 最近期初（建账期间月初） */
				sb = new StringBuilder();
				sb.append("select '' as rq, '' as period, ");
				sb.append(selectgroupstr);
				sb.append("nvl(sum((case when c.direction=1 then -1 else 1 end)*thismonthqc),0) as qcye, ");
				sb.append(" nvl(sum((case when c.direction=1 then -1 else 1 end)*ybthismonthqc),0) as ybqcye,  ");
				sb.append("  0 as bqfsjf, 0 as bqfsdf, 0 as bnljjf, 0 as bnljdf, ");
				sb.append("  0 as ybbqfsjf, 0 as ybbqfsdf, 0 as ybbnljjf, 0 as ybbnljdf ");
				sb.append("from ynt_fzhsqc a ");
				sb.append(joinstr_fz_acc);
				sb.append(" where ").append(commoncondstr);
				sb.append(" and c.isleaf='Y' "); //支持辅助核算后，只能查最明细数据，然后自己汇
				if(!StringUtil.isEmpty(paramVo.getPk_currency())){
					sb.append(" and a.pk_currency = ? ");
				}
				sb.append(" group by ").append(groupstr);

				qcdata1_1 = (List<FzYebVO>) singleObjectBO.executeQuery(sb.toString(), sp1,
						new BeanListProcessor(FzYebVO.class));
				/** A.2: 建账期间到查询开始期间上一期间之间的凭证发生净额 */
				sb = new StringBuilder();
				sb.append("select '' as rq, '' as period, ");
				sb.append(selectgroupstr);
				sb.append(" nvl(sum(a.jfmny),0)-nvl(sum(a.dfmny),0) as qcye, ");
				sb.append(" nvl(sum(a.ybjfmny),0)-nvl(sum(a.ybdfmny),0) as ybqcye, ");
				sb.append(" 0 as bqfsjf, 0 as bqfsdf, 0 as bnljjf, 0 as bnljdf,  ");
				sb.append(" 0 as ybbqfsjf, 0 as ybbqfsdf, 0 as ybbnljjf, 0 as ybbnljdf  ");
				sb.append("from ynt_tzpz_b a inner join ynt_tzpz_h a1 on a.pk_tzpz_h=a1.pk_tzpz_h ");
				sb.append(joinstr_fz_acc);
				sb.append(" where ").append(commoncondstr);
				/** 不包含未记账凭证 */
				if (!isHasJZ.booleanValue())
					sb.append(" and a1.ishasjz='Y' and a1.vbillstatus=1 ");
				sb.append(" and a1.period>=? and a1.period<?");
				if(!StringUtil.isEmpty(paramVo.getPk_currency())){
					sb.append(" and a.pk_currency = ? ");
				}
				sb.append(" group by ").append(groupstr);

				sp = new SQLParameter();
				sp.addParam(pk_corp);
				sp.addParam(corpPeriod);
				sp.addParam(beginPeriod);
				if(!StringUtil.isEmpty(paramVo.getPk_currency())){
					sp.addParam(paramVo.getPk_currency());
				}
				qcdata1_2 = (List<FzYebVO>) singleObjectBO.executeQuery(sb.toString(), sp,
						new BeanListProcessor(FzYebVO.class));
			}
		} else { /** 如果 beginPeriod > lastClosePeriod  //if (beginPeriod.compareTo(lastClosePeriod) > 0) */
			/** A.1: 最近期初（最近结账期间的期末） */
			sb = new StringBuilder();
			sb.append("select '' as rq, '' as period, ");
			sb.append(selectgroupstr);
			sb.append(" nvl(sum((case when c.direction=1 then -1 else 1 end)*thismonthqm),0) as qcye, ");
			sb.append(" nvl(sum((case when c.direction=1 then -1 else 1 end)*ybthismonthqm),0) as ybqcye, ");
			sb.append("  0 as bqfsjf, 0 as bqfsdf, 0 as bnljjf, 0 as bnljdf, ");
			sb.append("  0 as ybbqfsjf, 0 as ybbqfsdf, 0 as ybbnljjf, 0 as ybbnljdf ");
			sb.append("from ynt_kmqmjz a ");
			sb.append(joinstr_fz_acc);
			sb.append(" where ").append(commoncondstr);
			sb.append(" and a.period=?");
			if(!StringUtil.isEmpty(paramVo.getPk_currency())){
				sb.append(" and a.pk_currency = ? ");
			}
			sb.append(" group by ").append(groupstr);

			sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(lastClosePeriod);
			if(!StringUtil.isEmpty(paramVo.getPk_currency())){
				sp.addParam(paramVo.getPk_currency());
			}
			qcdata1_1 = (List<FzYebVO>) singleObjectBO.executeQuery(sb.toString(), sp,
					new BeanListProcessor(FzYebVO.class));

			/** A.2: 最近结账期间下一期间到查询开始期间上一期间之间的凭证发生净额 */
			sb = new StringBuilder();
			sb.append("select '' as rq, '' as period, ");
			sb.append(selectgroupstr);
			sb.append("nvl(sum(a.jfmny),0)-nvl(sum(a.dfmny),0) as qcye, ");
			sb.append("nvl(sum(a.ybjfmny),0)-nvl(sum(a.ybdfmny),0) as ybqcye, ");
			sb.append(" 0 as bqfsjf, 0 as bqfsdf, 0 as bnljjf, 0 as bnljdf, ");
			sb.append(" 0 as ybbqfsjf, 0 as ybbqfsdf, 0 as ybbnljjf, 0 as ybbnljdf ");
			sb.append("from ynt_tzpz_b a inner join ynt_tzpz_h a1 on a.pk_tzpz_h=a1.pk_tzpz_h ");
			sb.append(joinstr_fz_acc);
			sb.append(" where ").append(commoncondstr);
			/** 不包含未记账凭证 */
			if (!isHasJZ.booleanValue())
				sb.append(" and a1.ishasjz='Y' and a1.vbillstatus=1 ");
			sb.append(" and a1.period>? and a1.period<?");
			if(!StringUtil.isEmpty(paramVo.getPk_currency())){
				sb.append(" and a.pk_currency = ? ");
			}
			sb.append(" group by ").append(groupstr);

			sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(lastClosePeriod);
			sp.addParam(beginPeriod);
			if(!StringUtil.isEmpty(paramVo.getPk_currency())){
				sp.addParam(paramVo.getPk_currency());
			}
			qcdata1_2 = (List<FzYebVO>) singleObjectBO.executeQuery(sb.toString(), sp,
					new BeanListProcessor(FzYebVO.class));
		}

		/** B: 本期合计 */

		/** B.1: 建账年初的累借累贷 */
		String sqlCorpYearfs = null;
		if (beginPeriod.equals(corpYear)) {
			sqlCorpYearfs = getCorpYearfseSql(selectgroupstr, joinstr_fz_acc, commoncondstr, groupstr,paramVo.getPk_currency());
			qcdata2_1 = (List<FzYebVO>) singleObjectBO.executeQuery(sqlCorpYearfs, sp1,
					new BeanListProcessor(FzYebVO.class));
		}
		/** B.2: 查询范围内的凭证汇总 */
		sb = new StringBuilder();
		sb.append("select ");
		sb.append(selectgroupstr);
		sb.append(" 0 as qcye, 0 as ybqcye, ");
		sb.append("  nvl(sum(a.jfmny),0) as bqfsjf, nvl(sum(a.dfmny),0) as bqfsdf, 0 as bnljjf, 0 as bnljdf, ");
		sb.append("  nvl(sum(a.ybjfmny),0) as ybbqfsjf, nvl(sum(a.ybdfmny),0) as ybbqfsdf, 0 as ybbnljjf, 0 as ybbnljdf ");
		sb.append("from ynt_tzpz_b a inner join ynt_tzpz_h a1 on a.pk_tzpz_h=a1.pk_tzpz_h ");
		sb.append(joinstr_fz_acc);
		sb.append(" where ").append(commoncondstr);
		/** 不包含未记账凭证 */
		if (!isHasJZ.booleanValue())
			sb.append(" and a1.ishasjz='Y' and a1.vbillstatus=1 ");
		sb.append(" and a1.period>=? and a1.period<=?");
		if(!StringUtil.isEmpty(paramVo.getPk_currency())){
			sb.append(" and  a.pk_currency = ? ");
		}
		sb.append(" group by ").append(groupstr);

		sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(beginPeriod);
		sp.addParam(endPeriod);
		if(!StringUtil.isEmpty(paramVo.getPk_currency())){
			sp.addParam(paramVo.getPk_currency());
		}
		qcdata2_2 = (List<FzYebVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(FzYebVO.class));

		/** C: 本年累计 */
		if (yearsumPeriod.equals(beginPeriod)) {
			/** 当查询开始期间就是年初时，本年累计没有必要再查一遍，直接用合计的结果即可 */
			qcdata3_1 = qcdata2_1;
			qcdata3_2 = qcdata2_2;
		} else {
			/** C.1: 建账年初的累借累贷 */
			if (yearsumPeriod.equals(corpYear)) {
				sqlCorpYearfs = getCorpYearfseSql(selectgroupstr, joinstr_fz_acc, commoncondstr, groupstr,paramVo.getPk_currency());
				qcdata3_1 = (List<FzYebVO>) singleObjectBO.executeQuery(sqlCorpYearfs, sp1,
						new BeanListProcessor(FzYebVO.class));
			}

			/** C.2: 查询范围内的凭证汇总 */
			sb = new StringBuilder();
			sb.append("select ");
			sb.append(selectgroupstr);
			sb.append("0 as qcye,0 as ybqcye, nvl(sum(a.jfmny),0) as bqfsjf, nvl(sum(a.dfmny),0) as bqfsdf, 0 as bnljjf, 0 as bnljdf , ");
			sb.append(" nvl(sum(a.ybjfmny),0) as ybbqfsjf, nvl(sum(a.ybdfmny),0) as ybbqfsdf, 0 as ybbnljjf, 0 as ybbnljdf ");
			sb.append("from ynt_tzpz_b a inner join ynt_tzpz_h a1 on a.pk_tzpz_h=a1.pk_tzpz_h ");
			sb.append(joinstr_fz_acc);
			sb.append(" where ").append(commoncondstr);
			/** 不包含未记账凭证 */
			if (!isHasJZ.booleanValue())
				sb.append(" and a1.ishasjz='Y' and a1.vbillstatus=1 ");
			sb.append(" and a1.period>=? and a1.period<=?");
			if(!StringUtil.isEmpty(paramVo.getPk_currency())){
				sb.append(" and a.pk_currency = ? ");
			}
			sb.append(" group by ").append(groupstr);

			sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(yearsumPeriod);
			sp.addParam(endPeriod);
			if(!StringUtil.isEmpty(paramVo.getPk_currency())){
				sp.addParam(paramVo.getPk_currency());
			}
			qcdata3_2 = (List<FzYebVO>) singleObjectBO.executeQuery(sb.toString(), sp,
					new BeanListProcessor(FzYebVO.class));
		}

		/** 查询并缓存本次查询涉及的所有科目 */
		Map<String, YntCpaccountVO> mapAcc = getAllAccMap(pk_corp, kmFrom, kmTo, levelFrom, levelTo);

		/** 用dic分组汇总上述结果（相同分组值的期初行、合计行、累计行，压缩合并为一行） */
		Map<String, FzYebVO> mapFzyeb = new HashMap<String, FzYebVO>();
		groupSum(qcdata1_1, mapFzyeb, mapAcc, isDispKM, fzlb, 1);
		groupSum(qcdata1_2, mapFzyeb, mapAcc, isDispKM, fzlb, 1);
		groupSum(qcdata2_1, mapFzyeb, mapAcc, isDispKM, fzlb, 2);
		groupSum(qcdata2_2, mapFzyeb, mapAcc, isDispKM, fzlb, 2);
		groupSum(qcdata3_1, mapFzyeb, mapAcc, isDispKM, fzlb, 3);
		groupSum(qcdata3_2, mapFzyeb, mapAcc, isDispKM, fzlb, 3);

		/** D: 计算期末余额，汇总科目级次(important)，并对结果进行排序 */
		TreeMap<String, FzYebVO> sortMap = new TreeMap<String, FzYebVO>();

		String key;
		for (FzYebVO fzyeVo : mapFzyeb.values()) {
			/** 这里计算期末余额，=期初余额+本期借方发生-本期贷方发生 */
			fzyeVo.setQmye(fzyeVo.getQcye().add(fzyeVo.getBqfsjf()).sub(fzyeVo.getBqfsdf()));
			
			fzyeVo.setYbqmye(fzyeVo.getYbqcye().add(fzyeVo.getYbbqfsjf()).sub(fzyeVo.getYbbqfsdf()));//原币
			
			/** 插入当前明细科目行 */
			String fzxId = fzyeVo.getAttributeValue(fzhsx) == null ? "" : (String) fzyeVo.getAttributeValue(fzhsx); //当前辅助项值ID
			String accCode = fzyeVo.getAccCode(); //当前科目编码
			key = String.format("%s#@#%s", fzxId, accCode == null ? "" : accCode);
			sortMap.put(key, fzyeVo);

			/** 当是否显示科目=是时，计算生成汇总行 */
			if (isDispKM.booleanValue()) {
				/** 汇总科目编码 */
				String hzkmCode = null; 
				/** 汇总科目 */
				YntCpaccountVO hzkm; 
				/** 汇总行（上级科目汇总行、辅助项汇总行） */
				FzYebVO hzvo; 
				for (int level = fzyeVo.getAccLevel() - 1; level >= 0; level--) {
					 /** 查2-3级时，需求暂定不出1级汇总行 */
					if (level < levelFrom && level != 0)
						continue;
					sb = new StringBuilder(fzxId);
					if (level > 0) {
						hzkmCode = accCode.substring(0, levelLens[level - 1]);
						sb.append("#@#").append(hzkmCode);
					}
					key = sb.toString();
					/** 汇总行已存在，则将当前明细科目数据汇上去 */
					if (sortMap.containsKey(key)) { 
						hzvo = sortMap.get(key);
						hzvo.setQcye(hzvo.getQcye().add(fzyeVo.getQcye()));
						hzvo.setQmye(hzvo.getQmye().add(fzyeVo.getQmye()));
						hzvo.setBqfsjf(hzvo.getBqfsjf().add(fzyeVo.getBqfsjf()));
						hzvo.setBqfsdf(hzvo.getBqfsdf().add(fzyeVo.getBqfsdf()));
						hzvo.setBnljjf(hzvo.getBnljjf().add(fzyeVo.getBnljjf()));
						hzvo.setBnljdf(hzvo.getBnljdf().add(fzyeVo.getBnljdf()));
						
						/** 原币赋值 */
						hzvo.setYbqcye(hzvo.getYbqcye().add(fzyeVo.getYbqcye()));
						hzvo.setYbqmye(hzvo.getYbqmye().add(fzyeVo.getYbqmye()));
						hzvo.setYbbqfsjf(hzvo.getYbbqfsjf().add(fzyeVo.getYbbqfsjf()));
						hzvo.setYbbqfsdf(hzvo.getYbbqfsdf().add(fzyeVo.getYbbqfsdf()));
						hzvo.setYbbnljjf(hzvo.getYbbnljjf().add(fzyeVo.getYbbnljjf()));
						hzvo.setYbbnljdf(hzvo.getYbbnljdf().add(fzyeVo.getYbbnljdf()));
					} else { /** 否则创建汇总行 */
						hzvo = fzyeVo.clone(fzlb);
						if (level > 0) { /** 科目汇总行 */
							/** 从缓存中按上级编码取出上级科目 */
							hzkm = mapAcc.get(hzkmCode);
							hzvo.setPk_acc(hzkm.getPk_corp_account());
							hzvo.setAccCode(hzkm.getAccountcode());
							hzvo.setAccName(hzkm.getAccountname());
							hzvo.setAccLevel(hzkm.getAccountlevel());
							hzvo.setAccDirection(hzkm.getDirection());
						} else { /** 辅助项汇总行 */
							hzvo.setPk_acc(null);
							hzvo.setAccCode(null);
							hzvo.setAccName(null);
							hzvo.setAccLevel(0);
							hzvo.setAccDirection(-1);
						}
						sortMap.put(key, hzvo);
					}
				}
			}
			/** 当是否显示科目=否时，查询结果已经是辅助项汇总行(无科目级)，无需汇总至上级 */
		}

		/** E: 调整余额的方向和余额、处理余额分借贷两栏显示、辅助项目和科目合并为一列显示 */
		/** 汇总上级后，再统一处理余额方向 */
		for (FzYebVO fzyeVo : sortMap.values()) {
			Object fzxCode = fzyeVo.getAttributeValue(fzhsx + "Code");
			Object fzxName = fzyeVo.getAttributeValue(fzhsx + "Name");
			fzyeVo.setFzhsxCode(
					fzyeVo.getAccLevel() == 0 ? fzxCode != null ? fzxCode.toString() : "" : fzyeVo.getAccCode());
			fzyeVo.setFzhsxName(
					fzyeVo.getAccLevel() == 0 ? fzxName != null ? fzxName.toString() : "" : fzyeVo.getAccName());
			/** 期初余额 */
			/** 余额为负时，放入贷方，金额取相反数或绝对值 */
			if (fzyeVo.getQcye().compareTo(DZFDouble.ZERO_DBL) < 0) {
				fzyeVo.setQcyedf(fzyeVo.getQcye().abs());
				fzyeVo.setQcyejf(DZFDouble.ZERO_DBL);
				
				/** 原币赋值 */
				fzyeVo.setYbqcyedf(fzyeVo.getYbqcye().abs());
				fzyeVo.setYbqcyejf(DZFDouble.ZERO_DBL);
			} else { /** 为正时，放入借方 */
				fzyeVo.setQcyejf(fzyeVo.getQcye());
				fzyeVo.setQcyedf(DZFDouble.ZERO_DBL);
				
				/** 原币赋值 */
				fzyeVo.setYbqcyejf(fzyeVo.getYbqcye());
				fzyeVo.setYbqcyedf(DZFDouble.ZERO_DBL);
			}
			/** 期末余额 */
			/** 余额为负时，放入贷方，金额取相反数或绝对值 */
			if (fzyeVo.getQmye().compareTo(DZFDouble.ZERO_DBL) < 0) {
				fzyeVo.setQmyedf(fzyeVo.getQmye().abs());
				fzyeVo.setQmyejf(DZFDouble.ZERO_DBL);
				
				/** 原币赋值 */
				fzyeVo.setYbqmyedf(fzyeVo.getYbqmye().abs());
				fzyeVo.setYbqmyejf(DZFDouble.ZERO_DBL);
			} else { /** 为正时，放入借方 */
				fzyeVo.setQmyejf(fzyeVo.getQmye());
				fzyeVo.setQmyedf(DZFDouble.ZERO_DBL);
				
				/** 原币赋值 */
				fzyeVo.setYbqmyejf(fzyeVo.getYbqmye());
				fzyeVo.setYbqmyedf(DZFDouble.ZERO_DBL);
			}
		}

		return new ArrayList<FzYebVO>(sortMap.values());
				
	}


	private List<FzYebVO> getFzyebVoICFz6(KmReoprtQueryParamVO paramVo) {
		String pk_corp = paramVo.getPk_corp();
		/** 查询开始期间，如2016-02 */
		String beginPeriod = paramVo.getQjq(); 
		/** 查询结束期间，如2016-05 */
		String endPeriod = paramVo.getQjz(); 
		if (beginPeriod == null)
			throw new BusinessException("查询开始期间不能为空！");
		if (endPeriod == null)
			throw new BusinessException("查询结束期间不能为空！");
		if (beginPeriod.compareTo(endPeriod) > 0)
			throw new BusinessException("查询开始期间不能大于结束期间！");

		/** 查询开始日期，如2016-02-01 //vo.getBegindate1() */
		DZFDate beginDate = DZFDate.getDate(beginPeriod + "-01"); 
		/** llh：需求确定，跨年查时，本期累计从查询结束期间所在年初进行累计 */
		/** 查询结束期间所在年初 */
		String yearsumPeriod = endPeriod.substring(0, 4) + "-01"; 

		/** 取beginPeriod之前最大已结账期间（暂时注掉，因为辅助核算的年结还没有做） */
		String lastClosePeriod = null; 

		String kmFrom = paramVo.getKms_first();
		String kmTo = paramVo.getKms_last();
		int levelFrom = paramVo.getCjq().intValue();
		int levelTo = paramVo.getCjz().intValue();
		if (levelFrom > levelTo)
			throw new BusinessException("起始级次不能大于截止级次！");
		DZFBoolean isHasJZ = paramVo.getIshasjz();

		/** 辅助类别 */
		/** 直接传辅助项的序号 */
		Integer fzlb = Integer.valueOf(paramVo.getFzlb());

		String fzxFrom = paramVo.getFzxm();
		/** 是否显示科目 */
		DZFBoolean isDispKM = paramVo.getXskm(); 

		/** 取公司建账日期、科目编码规则 */
		String corpsql = "select a.begindate,a.accountcoderule,b.coderule from bd_corp a left join ynt_tdaccschema b on a.corptype=b.pk_trade_accountschema where nvl(a.dr,0)=0 and a.pk_corp=?";
		SQLParameter sqlparameter = new SQLParameter();
		sqlparameter.addParam(pk_corp);
		Object[] arr1 = (Object[]) singleObjectBO.executeQuery(corpsql, sqlparameter, new ArrayProcessor());
		DZFDate corpDate = null;
		String codeRule = "";
		if (arr1[0] != null)
			corpDate = DZFDate.getDate(arr1[0].toString());
		/** 从公司档案上取编码规则，不从科目方案上取 */
		if (arr1[1] != null) 
			codeRule = (String) arr1[1];
		else /** null为升级前的数据，默认为4/2/2/2/2 */
			codeRule = "4/2/2/2/2";

		if(corpDate == null){
			throw new BusinessException("公司建账日期为空");
		}
		/** 建账年初期间，如2015-01 //corpDate.getYear()+ "-01"; */
		String corpYear = String.format("%d-%02d", corpDate.getYear(), 1);
		/** 建账期间，如2015-03 */
		String corpPeriod = String.format("%d-%02d", corpDate.getYear(), corpDate.getMonth());

		/** 余额表最早从建账年初开始查，如果 beginPeriod<corpYear，则将beginPeriod直接调整为corpYear */
		if (beginPeriod.compareTo(corpYear) < 0) {
			throw new BusinessException(String.format("查询开始期间不能在建账年度(%d)之前！", corpDate.getYear()));
		}

		String[] arr2 = codeRule.split("/");
		int[] levelLens = new int[arr2.length];
		int len = 0;
		for (int i = 0; i < arr2.length; i++) {
			len += Integer.parseInt(arr2[i]);
			levelLens[i] = len;
		}
		/** 输入的截止级次不能超过当前科目编码规则范围 */
		if (levelTo > arr2.length)
			levelTo = arr2.length;

		List<FzYebVO> qcdata1_1 = new ArrayList<FzYebVO>();
		List<FzYebVO> qcdata1_2 = qcdata1_1;
		List<FzYebVO> qcdata2_1 = qcdata1_1;
		List<FzYebVO> qcdata2_2 = qcdata1_1;
		List<FzYebVO> qcdata3_1 = qcdata1_1;
		List<FzYebVO> qcdata3_2 = qcdata1_1;

		/** 公共的sql拼串部分 */
		String kmcond = this.getKmCondStr(pk_corp, kmFrom, kmTo, levelFrom, "c",paramVo.getPk_currency());
		String fzxcond = this.getFzxCondStr(pk_corp, fzxFrom, "b");
		String accLevelCode = String.format("substr(c.accountcode,1,%d)", levelLens[levelTo - 1]);

		/**  期初部分 */
		String fzhsx = "fzhsx" + fzlb;
		String fzhsx1 = String.format("%1$s,b.code as %1$sCode,b.name as %1$sName", fzhsx); //"fzhsx1,b.code as fzhsx1Code,b.name as fzhsx1Name";

		/** select分组项字段 */
		StringBuilder sbqc = new StringBuilder();
		/** 当不显示科目时，行上科目为空 */
		if (isDispKM.booleanValue())
			sbqc.append(accLevelCode).append(" as accCode, ");
		else
			sbqc.append("null as accCode, ");
		sbqc.append(fzhsx1).append(", ");
		String selectgroupstrqc = sbqc.toString();

		/** join部分 */
		sbqc = new StringBuilder();
		sbqc.append(" left join ynt_inventory b on a.fzhsx6=b.pk_inventory ");
		sbqc.append("inner join ynt_cpaccount c on a.pk_accsubj=c.pk_corp_account ");
		String joinstr_fz_accqc = sbqc.toString();

		/** 公共条件部分 */
		sbqc = new StringBuilder();
		sbqc.append("a.pk_corp=?");
		sbqc.append(" and ").append(kmcond);
		if (!StringUtil.isEmpty(fzxcond))
			sbqc.append(" and ").append(fzxcond);
		sbqc.append(" and nvl(a.dr,0)=0 ");
		String commoncondstrqc = sbqc.toString();

		/** group by部分 */
		sbqc = new StringBuilder();
		sbqc.append(fzhsx);
		sbqc.append(",b.code,b.name");
		if (isDispKM.booleanValue()) {
			sbqc.append(',').append(accLevelCode);
		}
		String groupstrqc = sbqc.toString();

		/**  发生部分 */
		/** select分组项字段 */
		String fzhsx3 = "b.pk_inventory";
		String fzhsx4 = " b.pk_inventory as  fzhsx6,b.code as fzhsx6Code,b.name as fzhsx6Name"; //"fzhsx1,b.code as fzhsx1Code,b.name as fzhsx1Name";
		
		StringBuilder sb = new StringBuilder();
		/** 当不显示科目时，行上科目为空 */
		if (isDispKM.booleanValue())
			sb.append(accLevelCode).append(" as accCode, ");
		else
			sb.append("null as accCode, ");
		sb.append(fzhsx4).append(", ");
		String selectgroupstr = sb.toString();

		/** join部分 */
		sb = new StringBuilder();
		sb.append(" left join ynt_inventory b on a.pk_inventory = b.pk_inventory "); 
		sb.append(" inner join ynt_cpaccount c on a.pk_accsubj=c.pk_corp_account ");
		String joinstr_fz_acc = sb.toString();

		/** 公共条件部分 */
		sb = new StringBuilder();
		sb.append("a.pk_corp=?");
		sb.append(" and ").append(kmcond);
		if (!StringUtil.isEmpty(fzxcond))
			sb.append(" and ").append(fzxcond);
		sb.append(" and nvl(a.dr,0)=0 ");
		String commoncondstr = sb.toString();

		/** group by部分 */
		sb = new StringBuilder();
		sb.append(fzhsx3);
		sb.append(",b.code,b.name");
		if (isDispKM.booleanValue()) {
			sb.append(',').append(accLevelCode);
		}
		String groupstr = sb.toString();

		/** A: 期初 */
		/** 当前科目名称、当前科目级次、当前科目方向等，最后从会计科目map中取 */
		SQLParameter sp;
		SQLParameter sp1 = new SQLParameter();
		sp1.addParam(pk_corp);
		if(!StringUtil.isEmpty(paramVo.getPk_currency())){
			sp1.addParam(paramVo.getPk_currency());
		}
		/** 查询开始期间在首次已结账期间及之前，需查找建账期初或建账年初 */
		if (lastClosePeriod == null) { 

			 /** 如果 beginPeriod<corpPeriod */
			if (beginPeriod.compareTo(corpPeriod) < 0) {
				/** A.1: 最近期初（建账年初） */
				sb = new StringBuilder();
				sb.append("select '' as rq, '' as period, ");
				sb.append(selectgroupstrqc);
				sb.append("nvl(sum((case when c.direction=1 then -1 else 1 end)*yearqc),0) as qcye, ");
				sb.append("nvl(sum((case when c.direction=1 then -1 else 1 end)*ybyearqc),0) as ybqcye, ");
				sb.append(" 0 as bqfsjf, 0 as bqfsdf, 0 as bnljjf, 0 as bnljdf , ");
				sb.append(" 0 as ybbqfsjf, 0 as ybbqfsdf, 0 as ybbnljjf, 0 as ybbnljdf  ");
				sb.append("from ynt_fzhsqc a ");
				sb.append(joinstr_fz_accqc);
				sb.append(" where ").append(commoncondstrqc);
				 /** 支持辅助核算后，只能查最明细数据，然后自己汇 */
				sb.append(" and c.isleaf='Y' ");
				if(!StringUtil.isEmpty(paramVo.getPk_currency())){
					sb.append(" and a.pk_currency = ? ");
				}
				sb.append(" group by ").append(groupstrqc);

				qcdata1_1 = (List<FzYebVO>) singleObjectBO.executeQuery(sb.toString(), sp1,
						new BeanListProcessor(FzYebVO.class));

				/** 查询开始期间在建账期间前，但不是建账年初的，累借累贷计入期初余额 */
				if (beginPeriod.compareTo(corpYear) > 0) { 
					/** A.2: 建账年初的借方发生和贷方发生合计 */
					sb = new StringBuilder();
					sb.append("select '' as rq, '' as period, ");
					sb.append(selectgroupstrqc);
					sb.append("nvl(sum(yearjffse),0)-nvl(sum(yeardffse),0) as qcye, ");
					sb.append("nvl(sum(ybyearjffse),0)-nvl(sum(ybyeardffse),0) as ybqcye, ");
					sb.append("  0 as bqfsjf, 0 as bqfsdf, 0 as bnljjf, 0 as bnljdf, ");
					sb.append("  0 as ybbqfsjf, 0 as ybbqfsdf, 0 as ybbnljjf, 0 as ybbnljdf ");
					sb.append("from ynt_fzhsqc a ");
					sb.append(joinstr_fz_accqc);
					sb.append(" where ").append(commoncondstrqc);
					sb.append(" and c.isleaf='Y' "); /** 支持辅助核算后，只能查最明细数据，然后自己汇 */
					if(!StringUtil.isEmpty(paramVo.getPk_currency())){
						sb.append("  and a.pk_currency = ? ");
					}
					sb.append(" group by ").append(groupstrqc);

					qcdata1_2 = (List<FzYebVO>) singleObjectBO.executeQuery(sb.toString(), sp1,
							new BeanListProcessor(FzYebVO.class));
				}
			} else { /** 如果 beginPeriod>=corpPeriod，但<=最近结账期间  //if (beginPeriod.compareTo(corpPeriod) >= 0) */
				/** A.1: 最近期初（建账期间月初） */
				sb = new StringBuilder();
				sb.append("select '' as rq, '' as period, ");
				sb.append(selectgroupstrqc);
				sb.append("nvl(sum((case when c.direction=1 then -1 else 1 end)*thismonthqc),0) as qcye, ");
				sb.append("nvl(sum((case when c.direction=1 then -1 else 1 end)*ybthismonthqc),0) as ybqcye, ");
				sb.append("  0 as bqfsjf, 0 as bqfsdf, 0 as bnljjf, 0 as bnljdf, ");
				sb.append("  0 as ybbqfsjf, 0 as ybbqfsdf, 0 as ybbnljjf, 0 as ybbnljdf ");
				sb.append("from ynt_fzhsqc a ");
				sb.append(joinstr_fz_accqc);
				sb.append(" where ").append(commoncondstrqc);
				sb.append(" and c.isleaf='Y' "); /** 支持辅助核算后，只能查最明细数据，然后自己汇 */
				if(!StringUtil.isEmpty(paramVo.getPk_currency())){
					sb.append("  and a.pk_currency = ? ");
				}
				sb.append(" group by ").append(groupstrqc);

				qcdata1_1 = (List<FzYebVO>) singleObjectBO.executeQuery(sb.toString(), sp1,
						new BeanListProcessor(FzYebVO.class));
				/** A.2: 建账期间到查询开始期间上一期间之间的凭证发生净额 */
				sb = new StringBuilder();
				sb.append("select '' as rq, '' as period, ");
				sb.append(selectgroupstr);
				sb.append("nvl(sum(a.jfmny),0)-nvl(sum(a.dfmny),0) as qcye,");
				sb.append("nvl(sum(a.ybjfmny),0)-nvl(sum(a.ybdfmny),0) as ybqcye,");
				sb.append("  0 as bqfsjf, 0 as bqfsdf, 0 as bnljjf, 0 as bnljdf , ");
				sb.append("  0 as ybbqfsjf, 0 as ybbqfsdf, 0 as ybbnljjf, 0 as ybbnljdf  ");
				sb.append("from ynt_tzpz_b a inner join ynt_tzpz_h a1 on a.pk_tzpz_h=a1.pk_tzpz_h ");
				sb.append(joinstr_fz_acc);
				sb.append(" where ").append(commoncondstr);
				/** 不包含未记账凭证 */
				if (!isHasJZ.booleanValue())
					sb.append(" and a1.ishasjz='Y' and a1.vbillstatus=1 ");
				sb.append(" and a1.period>=? and a1.period<?");
				if(!StringUtil.isEmpty(paramVo.getPk_currency())){
					sb.append(" and a.pk_currency = ?  ");
				}
				sb.append(" group by ").append(groupstr);

				sp = new SQLParameter();
				sp.addParam(pk_corp);
				sp.addParam(corpPeriod);
				sp.addParam(beginPeriod);
				if(!StringUtil.isEmpty(paramVo.getPk_currency())){
					sp.addParam(paramVo.getPk_currency());
				}
				qcdata1_2 = (List<FzYebVO>) singleObjectBO.executeQuery(sb.toString(), sp,
						new BeanListProcessor(FzYebVO.class));
			}
		} else { /** 如果 beginPeriod > lastClosePeriod  //if (beginPeriod.compareTo(lastClosePeriod) > 0) */
			/** A.1: 最近期初（最近结账期间的期末） */
			sb = new StringBuilder();
			sb.append("select '' as rq, '' as period, ");
			sb.append(selectgroupstrqc);
			sb.append("nvl(sum((case when c.direction=1 then -1 else 1 end)*thismonthqm),0) as qcye,  ");
			sb.append("nvl(sum((case when c.direction=1 then -1 else 1 end)*ybthismonthqm),0) as ybqcye,  ");
			sb.append(" 0 as bqfsjf, 0 as bqfsdf, 0 as bnljjf, 0 as bnljdf, ");
			sb.append(" 0 as ybbqfsjf, 0 as ybbqfsdf, 0 as ybbnljjf, 0 as ybbnljdf ");
			sb.append("from ynt_kmqmjz a ");
			sb.append(joinstr_fz_accqc);
			sb.append(" where ").append(commoncondstrqc);
			sb.append(" and a.period=?");
			if(!StringUtil.isEmpty(paramVo.getPk_currency())){
				sb.append(" and a.pk_currency = ? ");
			}
			sb.append(" group by ").append(groupstrqc);

			sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(lastClosePeriod);
			if(!StringUtil.isEmpty(paramVo.getPk_currency())){
				sp.addParam(paramVo.getPk_currency());
			}
			qcdata1_1 = (List<FzYebVO>) singleObjectBO.executeQuery(sb.toString(), sp,
					new BeanListProcessor(FzYebVO.class));
			/** A.2: 最近结账期间下一期间到查询开始期间上一期间之间的凭证发生净额 */
			sb = new StringBuilder();
			sb.append("select '' as rq, '' as period, ");
			sb.append(selectgroupstr);
			sb.append("nvl(sum(a.jfmny),0)-nvl(sum(a.dfmny),0) as qcye, ");
			sb.append("nvl(sum(a.ybjfmny),0)-nvl(sum(a.ybdfmny),0) as ybqcye, ");
			sb.append(" 0 as bqfsjf, 0 as bqfsdf, 0 as bnljjf, 0 as bnljdf , ");
			sb.append(" 0 as ybbqfsjf, 0 as ybbqfsdf, 0 as ybbnljjf, 0 as ybbnljdf  ");
			sb.append("from ynt_tzpz_b a inner join ynt_tzpz_h a1 on a.pk_tzpz_h=a1.pk_tzpz_h ");
			sb.append(joinstr_fz_acc);
			sb.append(" where ").append(commoncondstr);
			/** 不包含未记账凭证 */
			if (!isHasJZ.booleanValue())
				sb.append(" and a1.ishasjz='Y' and a1.vbillstatus=1 ");
			sb.append(" and a1.period>? and a1.period<?");
			if(!StringUtil.isEmpty(paramVo.getPk_currency())){
				sb.append(" and a.pk_currency = ?  ");
			}
			sb.append(" group by ").append(groupstr);

			sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(lastClosePeriod);
			sp.addParam(beginPeriod);
			if(!StringUtil.isEmpty(paramVo.getPk_currency())){
				sp.addParam(paramVo.getPk_currency());
			}
			qcdata1_2 = (List<FzYebVO>) singleObjectBO.executeQuery(sb.toString(), sp,
					new BeanListProcessor(FzYebVO.class));
		}

		/** B: 本期合计 */
		/** B.1: 建账年初的累借累贷 */
		String sqlCorpYearfs = null;
		if (beginPeriod.equals(corpYear)) {
			sqlCorpYearfs = getCorpYearfseSql(selectgroupstrqc, joinstr_fz_accqc, commoncondstrqc, groupstrqc,paramVo.getPk_currency());
			qcdata2_1 = (List<FzYebVO>) singleObjectBO.executeQuery(sqlCorpYearfs, sp1,
					new BeanListProcessor(FzYebVO.class));
		}

		/** B.2: 查询范围内的凭证汇总 */
		sb = new StringBuilder();
		sb.append("select ");
		sb.append(selectgroupstr);
		sb.append("0 as qcye, nvl(sum(a.jfmny),0) as bqfsjf, nvl(sum(a.dfmny),0) as bqfsdf, 0 as bnljjf, 0 as bnljdf, ");
		sb.append("0 as ybqcye, nvl(sum(a.ybjfmny),0) as ybbqfsjf, nvl(sum(a.ybdfmny),0) as ybbqfsdf, 0 as ybbnljjf, 0 as ybbnljdf ");
		sb.append("from ynt_tzpz_b a inner join ynt_tzpz_h a1 on a.pk_tzpz_h=a1.pk_tzpz_h ");
		sb.append(joinstr_fz_acc);
		sb.append(" where ").append(commoncondstr);
		/** 不包含未记账凭证 */
		if (!isHasJZ.booleanValue())
			sb.append(" and a1.ishasjz='Y' and a1.vbillstatus=1 ");
		sb.append(" and a1.period>=? and a1.period<=?");
		if(!StringUtil.isEmpty(paramVo.getPk_currency())){
			sb.append(" and a.pk_currency = ? ");
		}
		sb.append(" group by ").append(groupstr);

		sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(beginPeriod);
		sp.addParam(endPeriod);
		if(!StringUtil.isEmpty(paramVo.getPk_currency())){
			sp.addParam(paramVo.getPk_currency());
		}
		qcdata2_2 = (List<FzYebVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(FzYebVO.class));

		/** C: 本年累计 */
		if (yearsumPeriod.equals(beginPeriod)) {
			/** 当查询开始期间就是年初时，本年累计没有必要再查一遍，直接用合计的结果即可 */
			qcdata3_1 = qcdata2_1;
			qcdata3_2 = qcdata2_2;
		} else {
			/** C.1: 建账年初的累借累贷 */
			if (yearsumPeriod.equals(corpYear)) {
				sqlCorpYearfs = getCorpYearfseSql(selectgroupstrqc, joinstr_fz_accqc, commoncondstrqc, groupstrqc,paramVo.getPk_currency());
				qcdata3_1 = (List<FzYebVO>) singleObjectBO.executeQuery(sqlCorpYearfs, sp1,
						new BeanListProcessor(FzYebVO.class));
			}

			/** C.2: 查询范围内的凭证汇总 */
			sb = new StringBuilder();
			sb.append("select ");
			sb.append(selectgroupstr);
			sb.append("0 as qcye, nvl(sum(a.jfmny),0) as bqfsjf, nvl(sum(a.dfmny),0) as bqfsdf, 0 as bnljjf, 0 as bnljdf, ");
			sb.append("0 as ybqcye, nvl(sum(a.ybjfmny),0) as ybbqfsjf, nvl(sum(a.ybdfmny),0) as ybbqfsdf, 0 as ybbnljjf, 0 as ybbnljdf ");
			sb.append("from ynt_tzpz_b a inner join ynt_tzpz_h a1 on a.pk_tzpz_h=a1.pk_tzpz_h ");
			sb.append(joinstr_fz_acc);
			sb.append(" where ").append(commoncondstr);
			/** 不包含未记账凭证 */
			if (!isHasJZ.booleanValue())
				sb.append(" and a1.ishasjz='Y' and a1.vbillstatus=1 ");
			sb.append(" and a1.period>=? and a1.period<=?");
			if(!StringUtil.isEmpty(paramVo.getPk_currency())){
				sb.append(" and a.pk_currency = ? ");
			}
			sb.append(" group by ").append(groupstr);

			sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(yearsumPeriod);
			sp.addParam(endPeriod);
			if(!StringUtil.isEmpty(paramVo.getPk_currency())){
				sp.addParam(paramVo.getPk_currency());
			}
			qcdata3_2 = (List<FzYebVO>) singleObjectBO.executeQuery(sb.toString(), sp,
					new BeanListProcessor(FzYebVO.class));
		}

		/** 查询并缓存本次查询涉及的所有科目 */
		Map<String, YntCpaccountVO> mapAcc = getAllAccMap(pk_corp, kmFrom, kmTo, levelFrom, levelTo);

		/** 用dic分组汇总上述结果（相同分组值的期初行、合计行、累计行，压缩合并为一行） */
		Map<String, FzYebVO> mapFzyeb = new HashMap<String, FzYebVO>();
		groupSum(qcdata1_1, mapFzyeb, mapAcc, isDispKM, fzlb, 1);
		groupSum(qcdata1_2, mapFzyeb, mapAcc, isDispKM, fzlb, 1);
		groupSum(qcdata2_1, mapFzyeb, mapAcc, isDispKM, fzlb, 2);
		groupSum(qcdata2_2, mapFzyeb, mapAcc, isDispKM, fzlb, 2);
		groupSum(qcdata3_1, mapFzyeb, mapAcc, isDispKM, fzlb, 3);
		groupSum(qcdata3_2, mapFzyeb, mapAcc, isDispKM, fzlb, 3);

		/** D: 计算期末余额，汇总科目级次(important)，并对结果进行排序 */
		TreeMap<String, FzYebVO> sortMap = new TreeMap<String, FzYebVO>();

		String key;
		for (FzYebVO fzyeVo : mapFzyeb.values()) {
			/** 这里计算期末余额，=期初余额+本期借方发生-本期贷方发生 */
			fzyeVo.setQmye(fzyeVo.getQcye().add(fzyeVo.getBqfsjf()).sub(fzyeVo.getBqfsdf()));
			fzyeVo.setYbqmye(fzyeVo.getYbqcye().add(fzyeVo.getYbbqfsjf()).sub(fzyeVo.getYbbqfsdf()));//原币

			/** 插入当前明细科目行 */
			String fzxId = fzyeVo.getAttributeValue(fzhsx) == null ? "" : (String) fzyeVo.getAttributeValue(fzhsx); //当前辅助项值ID
			String accCode = fzyeVo.getAccCode(); //当前科目编码
			key = String.format("%s#@#%s", fzxId, accCode == null ? "" : accCode);
			sortMap.put(key, fzyeVo);

			/** 当是否显示科目=是时，计算生成汇总行 */
			if (isDispKM.booleanValue()) {
				/** 汇总科目编码 */
				String hzkmCode = null; 
				/** 汇总科目 */
				YntCpaccountVO hzkm; 
				/** 汇总行（上级科目汇总行、辅助项汇总行） */
				FzYebVO hzvo; 
				for (int level = fzyeVo.getAccLevel() - 1; level >= 0; level--) {
					/** 查2-3级时，需求暂定不出1级汇总行 */
					if (level < levelFrom && level != 0) 
						continue;
					sb = new StringBuilder(fzxId);
					if (level > 0) {
						hzkmCode = accCode.substring(0, levelLens[level - 1]);
						sb.append("#@#").append(hzkmCode);
					}
					key = sb.toString();
					/** 汇总行已存在，则将当前明细科目数据汇上去 */
					if (sortMap.containsKey(key)) { 
						hzvo = sortMap.get(key);
						hzvo.setQcye(hzvo.getQcye().add(fzyeVo.getQcye()));
						hzvo.setQmye(hzvo.getQmye().add(fzyeVo.getQmye()));
						hzvo.setBqfsjf(hzvo.getBqfsjf().add(fzyeVo.getBqfsjf()));
						hzvo.setBqfsdf(hzvo.getBqfsdf().add(fzyeVo.getBqfsdf()));
						hzvo.setBnljjf(hzvo.getBnljjf().add(fzyeVo.getBnljjf()));
						hzvo.setBnljdf(hzvo.getBnljdf().add(fzyeVo.getBnljdf()));
						
						/** --------------原币 */
						hzvo.setYbqcye(hzvo.getYbqcye().add(fzyeVo.getYbqcye()));
						hzvo.setYbqmye(hzvo.getYbqmye().add(fzyeVo.getYbqmye()));
						hzvo.setYbbqfsjf(hzvo.getYbbqfsjf().add(fzyeVo.getYbbqfsjf()));
						hzvo.setYbbqfsdf(hzvo.getYbbqfsdf().add(fzyeVo.getYbbqfsdf()));
						hzvo.setYbbnljjf(hzvo.getYbbnljjf().add(fzyeVo.getYbbnljjf()));
						hzvo.setYbbnljdf(hzvo.getYbbnljdf().add(fzyeVo.getYbbnljdf()));
					} else { /** 否则创建汇总行 */
						hzvo = fzyeVo.clone(fzlb);
						if (level > 0) { /** 科目汇总行 */
							/** 从缓存中按上级编码取出上级科目 */
							hzkm = mapAcc.get(hzkmCode);
							hzvo.setPk_acc(hzkm.getPk_corp_account());
							hzvo.setAccCode(hzkm.getAccountcode());
							hzvo.setAccName(hzkm.getAccountname());
							hzvo.setAccLevel(hzkm.getAccountlevel());
							hzvo.setAccDirection(hzkm.getDirection());
						} else { /** 辅助项汇总行 */
							hzvo.setPk_acc(null);
							hzvo.setAccCode(null);
							hzvo.setAccName(null);
							hzvo.setAccLevel(0);
							hzvo.setAccDirection(-1);
						}
						sortMap.put(key, hzvo);
					}
				}
			}
			/** 当是否显示科目=否时，查询结果已经是辅助项汇总行(无科目级)，无需汇总至上级 */
		}

		/** E: 调整余额的方向和余额、处理余额分借贷两栏显示、辅助项目和科目合并为一列显示 */
		/** 汇总上级后，再统一处理余额方向 */
		for (FzYebVO fzyeVo : sortMap.values()) {
			Object fzxCode = fzyeVo.getAttributeValue(fzhsx + "Code");
			Object fzxName = fzyeVo.getAttributeValue(fzhsx + "Name");
			fzyeVo.setFzhsxCode(
					fzyeVo.getAccLevel() == 0 ? fzxCode != null ? fzxCode.toString() : "" : fzyeVo.getAccCode());
			fzyeVo.setFzhsxName(
					fzyeVo.getAccLevel() == 0 ? fzxName != null ? fzxName.toString() : "" : fzyeVo.getAccName());

			/** 期初余额 */
			if (fzyeVo.getQcye().compareTo(DZFDouble.ZERO_DBL) < 0) { /** 余额为负时，放入贷方，金额取相反数或绝对值 */
				fzyeVo.setQcyedf(fzyeVo.getQcye().abs());
				fzyeVo.setQcyejf(DZFDouble.ZERO_DBL);
			} else { /** 为正时，放入借方 */
				fzyeVo.setQcyejf(fzyeVo.getQcye());
				fzyeVo.setQcyedf(DZFDouble.ZERO_DBL);
			}
			/** 原币期初 */
			if (fzyeVo.getYbqcye().compareTo(DZFDouble.ZERO_DBL) < 0) { /** 余额为负时，放入贷方，金额取相反数或绝对值 */
				/** 原币 */
				fzyeVo.setYbqcyedf(fzyeVo.getYbqcye().abs());
				fzyeVo.setYbqcyejf(DZFDouble.ZERO_DBL);
			} else { /** 为正时，放入借方 */
				/** 原币 */
				fzyeVo.setYbqcyejf(fzyeVo.getYbqcye());
				fzyeVo.setYbqcyedf(DZFDouble.ZERO_DBL);
			}
			/** 期末余额 */
			/** 余额为负时，放入贷方，金额取相反数或绝对值 */
			if (fzyeVo.getQmye().compareTo(DZFDouble.ZERO_DBL) < 0) { 
				fzyeVo.setQmyedf(fzyeVo.getQmye().abs());
				fzyeVo.setQmyejf(DZFDouble.ZERO_DBL);
			} else { /** 为正时，放入借方 */
				fzyeVo.setQmyejf(fzyeVo.getQmye());
				fzyeVo.setQmyedf(DZFDouble.ZERO_DBL);
			}
			
			/** 原币期末余额 */
			if (fzyeVo.getYbqmye().compareTo(DZFDouble.ZERO_DBL) < 0) { /** 余额为负时，放入贷方，金额取相反数或绝对值 */
				fzyeVo.setYbqmyedf(fzyeVo.getYbqmye().abs());
				fzyeVo.setYbqmyejf(DZFDouble.ZERO_DBL);
			} else { /** 为正时，放入借方 */
				fzyeVo.setYbqmyejf(fzyeVo.getYbqmye());
				fzyeVo.setYbqmyedf(DZFDouble.ZERO_DBL);
			}
		}

		return new ArrayList<FzYebVO>(sortMap.values());
	}

	/**
	 * 得到取建账年初的累借累贷发生额的sql
	 * 
	 * @param selectgroupstr
	 * @param joinstr_fz_acc
	 * @param commoncondstr
	 * @param groupstr
	 * @return
	 */
	private String getCorpYearfseSql(String selectgroupstr, String joinstr_fz_acc, String commoncondstr,
			String groupstr,String pk_currency) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append(selectgroupstr);
		sb.append("0 as qcye, 0 as ybqcye,  ");
		sb.append(" nvl(sum(a.yearjffse),0) as bqfsjf, nvl(sum(a.yeardffse),0) as bqfsdf, 0 as bnljjf, 0 as bnljdf ,");
		sb.append(" nvl(sum(a.ybyearjffse),0) as ybbqfsjf, nvl(sum(a.ybyeardffse),0) as ybbqfsdf, 0 as ybbnljjf, 0 as ybbnljdf ");
		sb.append("from ynt_fzhsqc a ");
		sb.append(joinstr_fz_acc);
		sb.append(" where ").append(commoncondstr);
		sb.append(" and c.isleaf='Y' "); /** 支持辅助核算后，只能查最明细数据，然后自己汇 */
		if(!StringUtil.isEmpty(pk_currency)){
			sb.append(" and  a.pk_currency = ? ");
		}
		sb.append(" group by ").append(groupstr);

		return sb.toString();
	}

	/**
	 * 查询并缓存本次查询涉及的所有科目，按code做key。用于更新科目名称方向、汇总上级科目等。
	 * 1、包含查询条件中可能涉及的上级科目，如：科目条件=160101、级次条件为1~x，则结果将包含1601；
	 * 2、只查级次条件范围内的科目，如级次条件2~3，将不包含1级和4级。
	 * 
	 * @param pk_corp
	 * @param kmcond
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, YntCpaccountVO> getAllAccMap(String pk_corp, String kmFrom, String kmTo, int levelFrom,
			int levelTo) throws BusinessException {
		/** 个性化设置vo */
		GxhszVO gxhset = zxkjPlatformService.queryGxhszVOByPkCorp(pk_corp);
		 /** 科目显示方式，默认显示本级 */
		Integer dispMethod = gxhset.getSubjectShow();

		/** 包含涉及的上级科目 */
		String kmcond = this.getKmCondStr(pk_corp, StringUtil.isEmptyWithTrim(kmFrom) ? null : kmFrom.substring(0, 4),
				StringUtil.isEmptyWithTrim(kmTo) ? null : kmTo.substring(0, 4), levelFrom, levelTo, "a");
		String sql = "select * from ynt_cpaccount a where nvl(a.dr,0)=0 and " + kmcond + " order by accountcode ";
		List<YntCpaccountVO> kmVOs = (List<YntCpaccountVO>) singleObjectBO.executeQuery(sql, null,
				new BeanListProcessor(YntCpaccountVO.class));
		Map<String, YntCpaccountVO> mp = new HashMap<String, YntCpaccountVO>();
		if (kmVOs != null && kmVOs.size() > 0) {
			for (YntCpaccountVO cpaccVo : kmVOs) {
				/** 显示本级 */
				if (dispMethod.intValue() == 0) { 
					
				} else if (dispMethod.intValue() == 1) { /** 显示一级+本级 */
					String firstname = "";
					if (cpaccVo.getAccountcode().length() > 4
							&& mp.get(cpaccVo.getAccountcode().substring(0, 4)) != null){/** 非一级 */
						firstname = mp.get(cpaccVo.getAccountcode().substring(0, 4)).getAccountname() +"_";
					}
					cpaccVo.setAccountname(firstname+cpaccVo.getAccountname());
				} else {/** 逐级显示 */
					StringBuffer parentfullname = new StringBuffer();
					for(YntCpaccountVO cpaccVo1 : kmVOs){
						if(cpaccVo.getAccountcode().startsWith(cpaccVo1.getAccountcode()) && 
								((cpaccVo.getAccountcode().length() - cpaccVo1.getAccountcode().length()) ==2
								  || (cpaccVo.getAccountcode().length() - cpaccVo1.getAccountcode().length()) ==3)
								 ){
							parentfullname.append(cpaccVo1.getAccountname() + "_" );
						}
					}
					cpaccVo.setAccountname(parentfullname.toString() + cpaccVo.getAccountname());
				}
				mp.put(cpaccVo.getAccountcode(), cpaccVo);
			}
		}
		return mp;
	}

	private void groupSum(List<FzYebVO> qcdata, Map<String, FzYebVO> mapFzyeb, Map<String, YntCpaccountVO> mapAcc,
			DZFBoolean isDispKM, int fzlb, int mnyType) {
		if (qcdata == null || qcdata.size() == 0)
			return;

		StringBuilder sb;
		String key;
		YntCpaccountVO cpacc;
		FzYebVO fzyeVo;
		for (FzYebVO vo : qcdata) {
			Object fzxObj = vo.getAttributeValue("fzhsx" + fzlb);
			sb = new StringBuilder(fzxObj == null ? "" : fzxObj.toString());
			if (isDispKM.booleanValue())
				sb.append("#@#").append(vo.getAccCode());
			key = sb.toString();  

			if (mapFzyeb.containsKey(key)) {
				fzyeVo = mapFzyeb.get(key);
				/** 金额相加 */
				switch (mnyType) {
				case 1:
					fzyeVo.setQcye(fzyeVo.getQcye().add(vo.getQcye()));
					fzyeVo.setYbqcye(fzyeVo.getYbqcye().add(vo.getYbqcye()));/** 原币期初 */
					break;
				case 2:
					fzyeVo.setBqfsjf(fzyeVo.getBqfsjf().add(vo.getBqfsjf()));
					fzyeVo.setBqfsdf(fzyeVo.getBqfsdf().add(vo.getBqfsdf()));
					//原币
					fzyeVo.setYbbqfsjf(fzyeVo.getYbbqfsjf().add(vo.getYbbqfsjf()));
					fzyeVo.setYbbqfsdf(fzyeVo.getYbbqfsdf().add(vo.getYbbqfsdf()));
					break;
				case 3:
					/** 由于querydata3可能直接取自querydata2，所以累计金额没有放在bnlj字段上而是也放在bqfs字段上了 */
					fzyeVo.setBnljjf(fzyeVo.getBnljjf().add(vo.getBqfsjf())); 
					fzyeVo.setBnljdf(fzyeVo.getBnljdf().add(vo.getBqfsdf()));
					
					/** 原币 */
					/** 由于querydata3可能直接取自querydata2，所以累计金额没有放在bnlj字段上而是也放在bqfs字段上了 */
					fzyeVo.setYbbnljjf(fzyeVo.getYbbnljjf().add(vo.getYbbqfsjf())); 
					fzyeVo.setYbbnljdf(fzyeVo.getYbbnljdf().add(vo.getYbbqfsdf()));
					break;
				}

			} else {
				/** 重写的clone只复制必要的属性 */
				fzyeVo = vo.clone(fzlb); 
				mapFzyeb.put(key, fzyeVo);

				/** 根据科目编码从缓存中取出科目档案 */
				/** 显示科目时，行上才有科目。不显示科目时，查询结果是辅助项汇总行，没有科目 */
				if (isDispKM.booleanValue()) { 
					cpacc = mapAcc.get(vo.getAccCode());
					/** 维护科目Key、名称、级次、方向，以及辅助核算项名称等 */
					fzyeVo.setPk_acc(cpacc.getPk_corp_account());
					fzyeVo.setAccName(cpacc.getAccountname());
					fzyeVo.setAccLevel(cpacc.getAccountlevel());
					fzyeVo.setAccDirection(cpacc.getDirection());
				} else {
					fzyeVo.setAccLevel(0);
					fzyeVo.setAccDirection(-1);
				}
			}
		}
	}

	/**
	 * 得到会计科目条件串。返回的条件串没有使用SQL动态参数(?)，否则不好调用
	 * 
	 * @param pk_corp
	 * @param kmFrom
	 * @param kmTo
	 * @param levelFrom
	 * @param prefix
	 * @return
	 */
	public String getKmCondStr(String pk_corp, String kmFrom, String kmTo, int levelFrom, String prefix,String pk_currency) {
		StringBuilder sb = new StringBuilder();
		sb.append("%1$s.pk_corp='%2$s'");
		if (!StringUtil.isEmptyWithTrim(kmFrom)) {
			sb.append(" and %1$s.accountcode>='%3$s'");
		}
		if (!StringUtil.isEmptyWithTrim(kmTo)) {
			sb.append(" and (%1$s.accountcode<='%4$s' or %1$s.accountcode like '%4$s%%')");
		}
		sb.append(" and %1$s.accountlevel>=%5$d ");  
		return String.format(sb.toString(), prefix, pk_corp, kmFrom, kmTo, levelFrom,pk_currency);
	}

	/**
	 * 得到会计科目条件串， 限制结束级次（levelFrom~levelTo）
	 * 
	 * @param pk_corp
	 * @param kmFrom
	 * @param kmTo
	 * @param levelFrom
	 * @param levelTo
	 * @param prefix
	 * @return
	 */
	public String getKmCondStr(String pk_corp, String kmFrom, String kmTo, int levelFrom, int levelTo, String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append("%1$s.pk_corp='%2$s'");
		if (!StringUtil.isEmptyWithTrim(kmFrom)) {
			sb.append(" and %1$s.accountcode>='%3$s'");
		}
		if (!StringUtil.isEmptyWithTrim(kmTo)) {
			sb.append(" and (%1$s.accountcode<='%4$s' or %1$s.accountcode like '%4$s%%')");
		}
		sb.append(" and %1$s.accountlevel between %5$d and %6$d "); //>=%5$d
		return String.format(sb.toString(), prefix, pk_corp, kmFrom, kmTo, levelFrom, levelTo);
	}

	/**
	 * 得到辅助核算项的条件。用户输入条件格式类似于：002,005-007,009
	 * 
	 * @param fzxFrom
	 * @param prefix
	 * @return
	 */
	public String getFzxCondStr(String pk_corp, String fzxFrom, String prefix) {
		StringBuilder sb = new StringBuilder();
		/** 只查维护了辅助项的数据 */
		sb.append("%1$s.pk_corp='%2$s'"); 
		if (!StringUtil.isEmptyWithTrim(fzxFrom)) {
			String[] arr1 = fzxFrom.split(",");
			StringBuilder sb1 = new StringBuilder();
			for (String fzx1 : arr1) {
				if (StringUtil.isEmptyWithTrim(fzx1))
					continue;

				sb1.append("%1$s.code='");
				sb1.append(fzx1);
				sb1.append("'");
				sb1.append(" or ");
			}
			if (sb1.length() > 0) {
				sb.append(" and (");
				sb.append(sb1.substring(0, sb1.length() - 4));
				sb.append(")");
			}
		}
		return String.format(sb.toString(), prefix, pk_corp);
	}


}
