package com.dzf.zxkj.platform.service.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.constant.IIncomeWarningConstants;
import com.dzf.zxkj.common.constant.TaxRptConst;
import com.dzf.zxkj.common.constant.TaxRptConstPub;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.bdset.IncomeHistoryVo;
import com.dzf.zxkj.platform.model.bdset.IncomeWarningVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.IIncomeWarningService;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.util.ReportUtil;
import com.dzf.zxkj.report.service.IZxkjReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("gl_incomewarningerv")
@Slf4j
public class IncomeWarningServiceImpl implements IIncomeWarningService {
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Reference(version = "1.0.0")
	private IZxkjReportService zxkjReportService;
	@Autowired
	private IBDCorpTaxService corpTaxService;
	@Autowired
	private IAccountService accountService;

	@Autowired
	private ICorpService corpService;
	
	@Override
	public IncomeWarningVO[] query(String pk_corp) throws DZFWarpException {
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(IDefaultValue.DefaultGroup);
		StringBuffer sql = new StringBuffer();
		sql.append(" select yi.* from ynt_IncomeWarning yi ");
		sql.append(" where nvl(yi.dr,0)=0 and (yi.pk_corp = ? or yi.pk_corp = ?)");//order by yi.ts
		List<IncomeWarningVO> listVO = (List<IncomeWarningVO>) singleObjectBO.executeQuery(sql.toString(), params,new BeanListProcessor(IncomeWarningVO.class));

		params.clearParams();
		params.addParam(pk_corp);
		IncomeHistoryVo[] his = (IncomeHistoryVo[]) singleObjectBO.queryByCondition(IncomeHistoryVo.class,
				"pk_corp = ? and nvl(dr, 0) = 0", params);

		Map<String, List<IncomeHistoryVo>> incomeHistoryVoGroupByPkSryj = Arrays.asList(his).stream().collect(Collectors.groupingBy(IncomeHistoryVo::getPk_sryj));

		if (listVO != null && listVO.size() > 0) {
			Map<String, YntCpaccountVO> accountMap = accountService.queryMapByPk(pk_corp);
			for (IncomeWarningVO incomeWarningVO : listVO) {
				String pk_subj = incomeWarningVO.getPk_accsubj();
				if (pk_subj != null && (pk_subj.length() == 24 || pk_subj.indexOf(",") > 23)) {
					String[] subjArray = pk_subj.split(",");
					StringBuilder kmbm = new StringBuilder();
					StringBuilder kmmc = new StringBuilder();
					for (String subj : subjArray) {
						YntCpaccountVO account = accountMap.get(subj);
						if (account != null) {
							if (kmbm.length() != 0) {
								kmbm.append(",");
							}
							kmbm.append(account.getAccountcode());
							if (kmmc.length() != 0) {
								kmmc.append(",");
							}
							kmmc.append(account.getAccountname());
						}
					}
					if (kmmc.length() > 0) {
						incomeWarningVO.setKm(kmmc.toString());
					}
					if (kmbm.length() > 0) {
						incomeWarningVO.setKmbm(kmbm.toString());
					}
				}
				if (incomeWarningVO.getHas_history() != null && incomeWarningVO.getHas_history().booleanValue()) {
					List<IncomeHistoryVo> incomeHistoryList =  incomeHistoryVoGroupByPkSryj.get(incomeWarningVO.getPk_sryj());
					Optional.ofNullable(incomeHistoryList).ifPresent(list ->{
						incomeWarningVO.setChildren(list.stream().toArray(IncomeHistoryVo[]::new));
					});
				}
			}
			
			listVO = specPacking(listVO, pk_corp, null, null);
		}
		return listVO.toArray(new IncomeWarningVO[listVO.size()]);
	}
	
	private List<IncomeWarningVO> specPacking(List<IncomeWarningVO> list, String pk_corp, String filflg, CorpTaxVo ctvo){
		List<IncomeWarningVO> newlist = new ArrayList<>();
		if("Y".equals(filflg)){
			for(IncomeWarningVO vo : list){
				if(vo.getSpeflg() != null && vo.getSpeflg().booleanValue()){
					continue;
				}
				newlist.add(vo);
			}
		}else{
			if (ctvo == null) {
				ctvo = corpTaxService.queryCorpTaxVO(pk_corp);
			}
			String yhzc = ctvo.getVyhzc();
			if(!"0".equals(yhzc)){//判断  如果不是
				for(IncomeWarningVO vo : list){
					if(IDefaultValue.DefaultGroup.equals(vo.getPk_corp())){
						continue;
					}
					newlist.add(vo);
				}
			}else{
				Map<String, IncomeWarningVO> map = new HashMap<String, IncomeWarningVO>();
				String name;
				for(IncomeWarningVO vo : list){
					name = vo.getXmmc();
					if(map.containsKey(name)){
						if(IDefaultValue.DefaultGroup.equals(vo.getPk_corp())){
							continue;
						}
					}
					
					map.put(name, vo);
				}
				
				for(Map.Entry<String, IncomeWarningVO> entry : map.entrySet()){
					newlist.add(entry.getValue());
				}
			}
		}
		
		VOUtil.ascSort(newlist, new String[]{"ts"});
		
		return newlist;
	}
	//	private IncomeWarningVO[] queryKMBM(IncomeWarningVO[] ivos){
//		if(ivos == null || ivos.length==0){
//			return ivos;
//		}
//		StringBuffer sql = null;
//		SQLParameter sp = new SQLParameter();
//		for(IncomeWarningVO ivo : ivos){
//			sql = new StringBuffer();
//			sp.addParam(ivo.getKmbm());
//			sp.addParam(ivo.getPk_corp());
//			sql.append(" select a.accountcode as kmbm , a.accountname as km ");
//			sql.append(" from ynt_cpaccount a ");
//			sql.append(" where nvl(a.dr,0) =0 and a.pk_corp_account = ?  and a.pk_corp = ? ");
//			List<IncomeWarningVO> listVO = (List<IncomeWarningVO>) singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(IncomeWarningVO.class));
//			if(listVO != null && listVO.size()>0){
//				ivo.setKm(listVO.get(0).getKm());
//				ivo.setKmbm(listVO.get(0).getKmbm());
//			}
//			sp.clearParams();
//		}
//		return ivos;
//	}
	@Override
	public void save(String isLoginRemind, String isInputRemind,IncomeWarningVO vo, String pk_corp) throws DZFWarpException {
		IncomeWarningVO ivo = new IncomeWarningVO();
		ivo.setPk_sryj(vo.getPk_sryj());
//		ivo.setDr(0);
		ivo.setPk_corp(pk_corp);
		ivo.setXmmc(vo.getXmmc());
		if (vo.getPk_accsubj() != null && !vo.getPk_accsubj().contains(pk_corp)) {
			ivo.setKmbm(vo.getKmbm());
			ivo.setPk_accsubj(vo.getPk_accsubj());
			ivo.setKm(vo.getKm());
		} else {
			ivo.setPk_accsubj(filterAccount(vo.getKmbm(), vo.getPk_accsubj()));
		}
		ivo.setSrsx(vo.getSrsx());
		ivo.setYjz(vo.getYjz());
		ivo.setHas_history(vo.getHas_history());
		ivo.setPeriod_type(vo.getPeriod_type());
		ivo.setSpeflg(vo.getSpeflg());
		if(!StringUtil.isEmpty(isLoginRemind)){
			ivo.setIsloginremind(isLoginRemind);
		}else{
			ivo.setIsloginremind("N");
		}
		if(!StringUtil.isEmpty(isInputRemind)){
			ivo.setIsinputremind(isInputRemind);
		}else{
			ivo.setIsinputremind("N");
		}
		if (ivo.getPk_accsubj().length() > 1250) {
			throw new BusinessException("最多可选择50个科目");
		}
		if (StringUtil.isEmpty(vo.getPk_sryj())) {
			ivo.setChildren(vo.getChildren());
			singleObjectBO.saveObject(pk_corp, ivo);
		} else {
			SQLParameter parameter = new SQLParameter();
			parameter.addParam(pk_corp);
			parameter.addParam(vo.getPk_sryj());
			singleObjectBO.executeUpdate("delete from ynt_income_history where pk_corp = ? and pk_sryj = ? ", parameter);
			if (vo.getChildren() != null) {
				singleObjectBO.insertVOArr(pk_corp, vo.getChildren());
			}
			singleObjectBO.update(ivo);
		}


	}

	private String filterAccount(String codes, String ids) {
		if (ids == null || ids.length() <= 24) {
			return ids;
		}
		String[] codeArray = codes.split(",");
		String[] idArray = ids.split(",");
		if (idArray.length <= 1) {
			return ids;
		}
		StringBuilder filteredId = new StringBuilder();
		for (int i = 0; i < codeArray.length; i++) {
			String code = codeArray[i];
			boolean hasParent = false;
			for (int j = 0; j < codeArray.length; j++) {
				if (code.startsWith(codeArray[j])
						&& !code.equals(codeArray[j])) {
					hasParent = true;
					break;
				}
			}
			if (!hasParent) {
				if (filteredId.length() != 0) {
					filteredId.append(",");
				}
				filteredId.append(idArray[i]);
			}
		}
		return filteredId.toString();
	}

	@Override
	public void delete(IncomeWarningVO vo) throws DZFWarpException {
		if (vo.getHas_history() != null && vo.getHas_history().booleanValue()) {
			SQLParameter parameter = new SQLParameter();
			parameter.addParam(vo.getPk_corp());
			parameter.addParam(vo.getPk_sryj());
			singleObjectBO.executeUpdate("delete from ynt_income_history where pk_corp = ? and pk_sryj = ? ", parameter);
		}
		singleObjectBO.deleteObject(vo);

	}
	@Override
	public IncomeWarningVO[] queryByPrimaryKey(String primaryKey)
			throws DZFWarpException {
		SQLParameter params = new SQLParameter();
		String condition = " pk_sryj = ? and nvl(dr,0) = 0 ";
		params.addParam(primaryKey);
		IncomeWarningVO[] vos=(IncomeWarningVO[])singleObjectBO.queryByCondition(IncomeWarningVO.class, condition, params);
		return vos;
	}

	@Override
	public FseJyeVO[] queryFseInfo(IncomeWarningVO[] ivos, String pk_corp,
								   String enddate) throws DZFWarpException {
		if (ivos == null || ivos.length == 0) {
			return null;
		}

		Map<Integer, List<IncomeWarningVO>> periodMap = new HashMap<Integer, List<IncomeWarningVO>>();
		for (IncomeWarningVO ivo : ivos) {
			Integer period_type = ivo.getPeriod_type() == null ? 3 : ivo.getPeriod_type();
			List<IncomeWarningVO> list = periodMap.get(period_type);
			if (list == null) {
				list = new ArrayList<IncomeWarningVO>();
				periodMap.put(period_type, list);
			}
			list.add(ivo);
		}

		CorpVO corpvo = corpService.queryByPk(pk_corp);
		DZFDate jzdate = corpvo.getBegindate();
		for (Map.Entry<Integer, List<IncomeWarningVO>> entry : periodMap.entrySet()) {
			DZFDate endDate = new DZFDate(enddate);
			DZFDate beginDate = null;
			Integer period_type = entry.getKey();
			List<IncomeWarningVO> warningList = entry.getValue();
			if (period_type == 0) {
				beginDate = new DZFDate(DateUtils.getPeriod(endDate) + "-01");
			} else if (period_type == 1) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(endDate.toDate());
				int mon = calendar.get(Calendar.MONTH);
				int quarter = mon / 3 + 1;
				int endMon = 3 * quarter - 1;
				int beginMon = endMon - 2;
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.set(Calendar.MONTH, beginMon);
				beginDate = new DZFDate(calendar.getTime());
				calendar.set(Calendar.MONTH, endMon);
				endDate = new DZFDate(calendar.getTime());
			} else if (period_type == 2) {
				int year = endDate.getYear();
				beginDate = new DZFDate(year + "-01-01");
				endDate = new DZFDate(year + "-12-31");
			} else if (period_type == 3) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(endDate.toDate());
				calendar.add(Calendar.MONTH, -11);
				beginDate = new DZFDate(calendar.getTime());
			}
			String beginPeriod = DateUtils.getPeriod(beginDate);
			String endPeriod = DateUtils.getPeriod(endDate);

			// 开始日期在建账日期前则取建账日期为开始日期
			if (beginDate.before(jzdate)) {
				beginDate = jzdate;
			}

			List<String> kmList = new ArrayList<String>();
			for (IncomeWarningVO vo : warningList) {
				if(!StringUtil.isEmpty(vo.getKmbm())){
					kmList.addAll(Arrays.asList(vo.getKmbm().split(",")));
				}
			}

			FseJyeVO[] fsVos = null;
			if (kmList.size() > 0) {
				QueryParamVO pramavo = ReportUtil.getFseQueryParamVO(corpvo, beginDate,
						endDate, kmList.toArray(new String[0]), true);
				fsVos = zxkjReportService.getFsJyeVOs(pramavo, 1);
			}

			for (IncomeWarningVO ivo : warningList) {
				DZFDouble fsTotal = null;
				if(ivo.getSpeflg() != null && ivo.getSpeflg().booleanValue()){
					fsTotal = getSpecFsValue(beginPeriod, endPeriod, pk_corp, ivo);
				}else if (fsVos != null) {
					for (FseJyeVO fseJyeVO : fsVos) {
						if (!StringUtil.isEmpty(ivo.getKmbm())
								&& ivo.getKmbm().matches("(^|.*,)" + fseJyeVO.getKmbm() + "($|,.*)")) {
							fsTotal = SafeCompute.add(fsTotal, "借".equals(fseJyeVO.getFx()) ? fseJyeVO.getFsjf()
									: fseJyeVO.getFsdf());
						}
					}
				}
				if (fsTotal == null) {
					fsTotal = DZFDouble.ZERO_DBL;
				}
				if (ivo.getHas_history() != null
						&& ivo.getHas_history().booleanValue()) {
					IncomeHistoryVo[] hisVos = (IncomeHistoryVo[]) ivo
							.getChildren();
					if (hisVos != null) {
						DZFDouble his_occur = DZFDouble.ZERO_DBL;
						for (IncomeHistoryVo incomeHistoryVo : hisVos) {
							if (incomeHistoryVo.getPeriod().compareTo(beginPeriod) > -1
									&& incomeHistoryVo.getPeriod().compareTo(
									endPeriod) < 1) {
								his_occur = his_occur.add(incomeHistoryVo
										.getOccur_mny());
							}
						}
						fsTotal = fsTotal.add(his_occur);
					}
				}
				ivo.setFstotal(fsTotal);
				ivo.setInfonumber(new DZFDouble(ivo.getSrsx()).sub(fsTotal));
			}
		}
		return null;
	}

	public String[] getPeriodRangeSpe(String period, Integer periodType){
		DZFDate endDate = new DZFDate(period);
		DZFDate beginDate = null;
		if (periodType == 0) {
			beginDate = new DZFDate(DateUtils.getPeriod(endDate) + "-01");
		} else if (periodType == 1) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate.toDate());
			int mon = calendar.get(Calendar.MONTH);
			int quarter = mon / 3 + 1;
			int endMon = 3 * quarter - 1;
			int beginMon = endMon - 2;
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, beginMon);
			beginDate = new DZFDate(calendar.getTime());
			calendar.set(Calendar.MONTH, endMon);
			endDate = new DZFDate(calendar.getTime());
		} else if (periodType == 2) {
			int year = endDate.getYear();
			beginDate = new DZFDate(year + "-01-01");
			endDate = new DZFDate(year + "-12-31");
		} else if (periodType == 3) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate.toDate());
			calendar.add(Calendar.MONTH, -11);
			beginDate = new DZFDate(calendar.getTime());
		}
		String beginPeriod = DateUtils.getPeriod(beginDate);
		String endPeriod = DateUtils.getPeriod(endDate);

		return new String[]{beginPeriod, endPeriod};
	}

	@Override
	public IncomeWarningVO[] queryIncomeWaringVos(String pk_corp, String period, String filflg) throws DZFWarpException {
		//查询公司预警条目
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(IDefaultValue.DefaultGroup);
		IncomeWarningVO[] incomeWarningVOS = (IncomeWarningVO[]) singleObjectBO.queryByCondition(IncomeWarningVO.class,
				" (pk_corp = ? or pk_corp = ?)   and nvl(dr,0) = 0 order by period_type desc", sp);

		if(incomeWarningVOS == null || incomeWarningVOS.length == 0){
			return new IncomeWarningVO[0];
		}
		CorpTaxVo ctvo = corpTaxService.queryCorpTaxVO(pk_corp);
		if ("一般纳税人".equals(ctvo.getChargedeptname())) {
			// 过滤小规模预警
			incomeWarningVOS = Arrays.asList(incomeWarningVOS).stream()
					.filter(vo -> !IIncomeWarningConstants.SRYJ.equals(vo.getXmmc()) && !IIncomeWarningConstants.ZZSYJ.equals(vo.getXmmc()))
					.toArray(IncomeWarningVO[]::new);
		}
		//设置优惠政策
        incomeWarningVOS = specPacking(Arrays.asList(incomeWarningVOS), pk_corp, filflg, ctvo).stream().toArray(IncomeWarningVO[]::new);
        //设置查询条件
		String previousYearPeriod = DateUtils.getPreviousYearPeriod(period.substring(0,7));
		String endPeriod = Integer.toString(new DZFDate(period.substring(0,7) + "-01").getYear())+"-12"; ///1
		String codeStr = Arrays.asList(incomeWarningVOS).stream().map(IncomeWarningVO::getPk_accsubj).filter(v-> !StringUtil.isEmpty(v)).collect(Collectors.joining(","));
		Set<String> pkAccsubjSet = new HashSet<>(Arrays.asList(codeStr.split(",")));
		//查询科目主键对应的科目编码 注：预警条目里的kmbm不能使用 存在空值
		YntCpaccountVO[] yntCpaccountVOS = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class,
				" PK_CORP_ACCOUNT in ( " + SqlUtil.buildSqlConditionForIn(pkAccsubjSet.toArray(new String[pkAccsubjSet.size()])) + " )  and nvl(dr,0) = 0", null);

		//构建科目主键和编码的映射关系
		Map<String,String> codeMap = new HashMap();
		for(YntCpaccountVO yntCpaccountVO : yntCpaccountVOS){
			codeMap.put(yntCpaccountVO.getPk_corp_account(), yntCpaccountVO.getAccountcode());
		}
        //查询指定公司 科目 时间区间内的发生额(不包括余额)
		Map<String,Map<String,Double>> fseMap = zxkjReportService.getVoucherFseQryVOListByPkCorpAndKmBetweenPeriod(pk_corp, yntCpaccountVOS, previousYearPeriod, endPeriod);

		//预警信息中的历史发生金额
		IncomeHistoryVo[] incomeHistoryVos = (IncomeHistoryVo[]) singleObjectBO.queryByCondition(IncomeHistoryVo.class,
				" PK_CORP = '"+pk_corp+"'  and nvl(dr,0) = 0", null);
		Map<String, DZFDouble> hisMap = Arrays.asList(incomeHistoryVos).stream().collect(Collectors.toMap(IncomeHistoryVo::getPk_sryj,IncomeHistoryVo::getOccur_mny, (key1, key2)->key2));
        //按预警条目统计总的发生额
		for(IncomeWarningVO incomeWarningVO : incomeWarningVOS){
			DZFDouble fseTotal = Optional.ofNullable(incomeWarningVO.getFstotal()).orElse(DZFDouble.ZERO_DBL);
			if(incomeWarningVO.getSpeflg() != null && incomeWarningVO.getSpeflg().booleanValue()){//特殊处理
				String[] periodRange = getPeriodRangeSpe(period, incomeWarningVO.getPeriod_type());
				fseTotal = getSpecFsValue(periodRange[0], periodRange[1], pk_corp, incomeWarningVO);
			}else{
				List<String> periodRange = getPeriodRange(period.substring(0,7), incomeWarningVO.getPeriod_type());

				Double hisFse = Arrays.asList(incomeHistoryVos).stream().filter(v-> periodRange.contains(v.getPeriod()) && v.getPk_sryj().equals(incomeWarningVO.getPk_sryj())).mapToDouble(v->v.getOccur_mny().doubleValue()).sum();

				DZFDouble infonumber = Optional.ofNullable(incomeWarningVO.getInfonumber()).orElse(DZFDouble.ZERO_DBL);
				if(!StringUtil.isEmpty(incomeWarningVO.getPk_accsubj())){
					List<String> codeList = Arrays.asList(incomeWarningVO.getPk_accsubj().split(",")).stream().filter(k->codeMap.containsKey(k)).map(k->codeMap.get(k)).collect(Collectors.toList());
					List<Map<String,Double>> fse = fseMap.entrySet().stream().filter(map -> codeList.contains(map.getKey())).map(map->map.getValue()).collect(Collectors.toList());

					for(Map<String,Double> v : fse){
						Double d = v.entrySet().stream().filter(k->periodRange.contains(k.getKey())).map(m->m.getValue()).reduce(Double::sum).orElse(0.00);
						fseTotal = SafeCompute.add(fseTotal, new DZFDouble(d));
					}

					if(StringUtil.isEmpty(incomeWarningVO.getKmbm())){
						String kmbm = Arrays.asList(incomeWarningVO.getPk_accsubj().split(",")).stream().map(v->codeMap.get(v)).collect(Collectors.joining(","));
						incomeWarningVO.setKmbm(kmbm);
					}
				}

				fseTotal = SafeCompute.add(fseTotal,new DZFDouble(hisFse));

			}

			incomeWarningVO.setFstotal(fseTotal);
			incomeWarningVO.setInfonumber(SafeCompute.sub(incomeWarningVO.getSrsx(), fseTotal));
		}
		return incomeWarningVOS;
	}

	/**
	 * @param period 期间
	 * @param periodType 预警周期
	 * @return [期间]
	 * @description 根据预警周期和区间获取期间结合
	 */
	private List<String> getPeriodRange(String period, Integer periodType){
		periodType = periodType == null ? 3 : periodType;
		switch (periodType){
			case 0 :
				return Arrays.asList(new String[]{ period });
			case 1 :
				DZFDate date = DateUtils.getPeriodStartDate(period);
				int month = date.getMonth();
				int year = date.getYear();
				if(month >= 1 && month <= 3){
					return Arrays.asList(new String[]{ year+"-01",year+"-02",year+"-03"});
				}else if(month >= 4 && month <= 6){
					return Arrays.asList(new String[]{ year+"-04",year+"-05",year+"-06"});
				}else if(month >= 7 && month <= 9){
					return Arrays.asList(new String[]{ year+"-07",year+"-08",year+"-09"});
				}else if(month >= 10 && month <= 12){
					return Arrays.asList(new String[]{ year+"-10",year+"-11",year+"-12"});
				}
			case 2 :
				int thisYear = DateUtils.getPeriodStartDate(period).getYear();
				return Arrays.asList(new String[]{ thisYear+"-01",thisYear+"-02",thisYear+"-03", thisYear+"-04",thisYear+"-05",thisYear+"-06", thisYear+"-07",thisYear+"-08",thisYear+"-09", thisYear+"-10",thisYear+"-11",thisYear+"-12"});
			default:
				String[] result = new String[12];
				result[0] = period;
				for(int i = 1; i < 12; i++){
					result[i] = DateUtils.getPreviousPeriod(result[i-1]);
				}
				return Arrays.asList(result);
		}
	}

	public DZFDouble getSpecFsValue(String beginPeriod, String endPeriod, 
			String pk_corp, IncomeWarningVO vo) throws DZFWarpException{
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		DZFDate begindate = corpvo.getBegindate();//建账日期
		String corptype = corpvo.getCorptype();
		if(!(TaxRptConst.KJQJ_2013.equals(corptype)
				|| TaxRptConst.KJQJ_2007.equals(corptype)
				|| TaxRptConst.KJQJ_QYKJZD.equals(corptype))){
			return null;
		}
		
		//开始取数
		DZFDouble result = null;
		try {
			if(IIncomeWarningConstants.QYZCZE.equals(vo.getXmmc())){
				
				beginPeriod = DateUtils.getPreviousPeriod(beginPeriod);
				boolean flag = false;
				DZFDouble bf = null;
				DZFDouble ef = null;
				ZcFzBVO[] zfbvos = null;
				if(beginPeriod.compareTo(DateUtils.getPeriod(begindate)) >= 0){
					zfbvos = zxkjReportService.getZCFZBVOsConXmids(beginPeriod, pk_corp, "N",
							new String[] { "N", "N", "N", "N","N" }, null);
					bf = getZcValueByMc(zfbvos, "资产总计");
					flag = true;
				}
				
				zfbvos = zxkjReportService.getZCFZBVOsConXmids(endPeriod, pk_corp, "N",
						new String[] { "N", "N", "N", "N","N" }, null);
				ef = getZcValueByMc(zfbvos, "资产总计");
				
				result = SafeCompute.div(SafeCompute.add(bf, ef), flag ? new DZFDouble(2) : new DZFDouble(1));
			} else if(IIncomeWarningConstants.QYNDNS.equals(vo.getXmmc())){
				QueryParamVO paramVO = new QueryParamVO();
				paramVO.setPk_corp(pk_corp);
				paramVO.setIshasjz(new DZFBoolean("N"));//
				DZFDate beginDate = new DZFDate(endPeriod + "-01");
				DZFDate enddate = new DZFDate(endPeriod + "-" + beginDate.getDaysMonth());
				paramVO.setBegindate1(beginDate);
				paramVO.setEnddate(enddate);
				paramVO.setQjq(endPeriod);
				paramVO.setQjz(endPeriod);
				LrbVO[] lrbvos = zxkjReportService.getLRBVOsConXm(paramVO, null);
				
				CorpTaxVo ctvo = corpTaxService.queryCorpTaxVO(pk_corp);
				Integer type = ctvo.getTaxlevytype();
				if(type == null || type == TaxRptConstPub.TAXLEVYTYPE_CZZS){
					result = getLrValueByMc(lrbvos, "三、利润总额（亏损总额以“-”号填列）");
				}else{
					DZFDouble bf = getLrValueByMc(lrbvos, "一、营业收入");
					DZFDouble ef = getLrValueByMc(lrbvos, "加：营业外收入");
					DZFDouble rate = ctvo.getIncometaxrate();//应纳税所得率
					result = SafeCompute.multiply(SafeCompute.add(bf, ef), SafeCompute.div(rate, new DZFDouble(100)));
				}
				
			}
		} catch (Exception e) {
			log.error("错误",e);
		}
		
		return result;
	}
	
	private DZFDouble getZcValueByMc(ZcFzBVO[] zcvos, String mc){
		DZFDouble result = null;
		if(zcvos == null || zcvos.length == 0){
			return result;
		}
		
		for(ZcFzBVO zcvo : zcvos){
			if(mc.equals(zcvo.getZc())){
				result = zcvo.getQmye1();
				break;
			} else if(mc.equals(zcvo.getFzhsyzqy())){
				result = zcvo.getQmye2();
				break;
			}
		}
		
		return result;
	}
	
	private DZFDouble getLrValueByMc(LrbVO[] lrbvos, String mc){
		DZFDouble result = null;
		if(lrbvos == null || lrbvos.length == 0){
			return result;
		}
		
		for(LrbVO lrvo : lrbvos){
			if(mc.equals(lrvo.getXm())){
				result = lrvo.getBnljje();
				break;
			}
		}
		
		return result;
	}
}
