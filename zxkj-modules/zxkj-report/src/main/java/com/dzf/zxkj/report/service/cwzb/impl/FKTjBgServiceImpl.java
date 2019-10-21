package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.base.utils.CalculationUtil;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.platform.model.report.FkTjBgVo.FktjBgBvo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.enums.FkTjBgEnum;
import com.dzf.zxkj.report.service.cwbb.ILrbQuarterlyReport;
import com.dzf.zxkj.report.service.cwzb.IFkTjBgService;
import com.dzf.zxkj.report.service.cwzb.IFsYeReport;
import com.dzf.zxkj.report.service.cwzb.IKMMXZReport;
import com.dzf.zxkj.report.utils.VoUtils;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("gl_fktjbgserv")
@Slf4j
@SuppressWarnings("all")
public class FKTjBgServiceImpl implements IFkTjBgService {

	@Reference
	private IZxkjPlatformService zxkjPlatformService;

	@Autowired
	private IFsYeReport gl_rep_fsyebserv;

	@Autowired
	private ILrbQuarterlyReport gl_rep_lrbquarterlyserv;//利润表季报数据

	@Autowired
	private IKMMXZReport gl_rep_kmmxjserv;// 科目明细账数据

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public Object[] queryZzsBg(String period, CorpVO cpvo) throws DZFWarpException {
		String year = period.substring(0, 4);
		ZzsBgVo[] zzsbgvos = new ZzsBgVo[12];
		// 获取科目明细表的数据
		Object[] kmmxobjs = getBaseData(year + "-12", cpvo);
		Object[] fsobjs = gl_rep_fsyebserv.getEveryPeriodFsJyeVOs(cpvo.getBegindate(),
				DateUtils.getPeriodEndDate(year + "-12"), cpvo.getPk_corp(), kmmxobjs,"",null);
		Map<String, List<FseJyeVO>> monthmap = (Map<String, List<FseJyeVO>>) fsobjs[0];

		String zyywsrcode = getZyywsr(cpvo);

		if (StringUtil.isEmpty(zyywsrcode)) {// 非07 和13的
			return null;
		}
		String charname = cpvo.getChargedeptname();

		String queryAccountRule = zxkjPlatformService.queryAccountRule(cpvo.getPk_corp());

		String xxcode = zxkjPlatformService.getNewRuleCode("22210102", DZFConstant.ACCOUNTCODERULE, queryAccountRule);// 销项

		String jxcode = zxkjPlatformService.getNewRuleCode("22210101", DZFConstant.ACCOUNTCODERULE, queryAccountRule);// 进项

		String ldcode = zxkjPlatformService.getNewRuleCode("222109", DZFConstant.ACCOUNTCODERULE, queryAccountRule);// 留底

		// 主营业务收入
		for (int i = 1; i <= 12; i++) {
			zzsbgvos[i - 1] = new ZzsBgVo();
			// 发生额余额表数据
			List<FseJyeVO> curr_fsvos = getFsvos(year +"-" +String.format("%02d", i), monthmap);// 当期数据值
			DZFDouble[] values = getZzsValue(curr_fsvos, xxcode, jxcode, ldcode, zyywsrcode, charname);
			zzsbgvos[i - 1].setMonth(String.format("%02d", i));
			zzsbgvos[i - 1].setYysr(Common.format(values[1]));
			zzsbgvos[i - 1].setZzs(Common.format(values[0]));
			zzsbgvos[i - 1].setSf(Common.format(values[2]) + "%");
			zzsbgvos[i - 1].setLjsf(Common.format(values[3])+"%");//累计税负
		}
		
		String tips = getTips(monthmap,period,xxcode,jxcode,ldcode,charname);
		
		
		return new Object[]{ tips, zzsbgvos};
	}
	
	
	private DZFDouble[] getZzsValue(List<FseJyeVO> curr_fsvos, String xxcode, String jxcode, String ldcode,
			String zyywsrcode,String charname) {
		DZFDouble xx = DZFDouble.ZERO_DBL;// 销项金额
		DZFDouble jx = DZFDouble.ZERO_DBL;// 进项金额
		DZFDouble ld = DZFDouble.ZERO_DBL;// 本期留底
		DZFDouble zzs = DZFDouble.ZERO_DBL;// 增值税
		DZFDouble ywsr = DZFDouble.ZERO_DBL;// 主营业务收入
		DZFDouble sl = DZFDouble.ZERO_DBL;// 增值税税负率
		
		DZFDouble xx_total = DZFDouble.ZERO_DBL;// 累计销项金额
		DZFDouble jx_total = DZFDouble.ZERO_DBL;// 累计进项金额
		DZFDouble ld_total = DZFDouble.ZERO_DBL;// 累计本期留底
		DZFDouble zzs_total = DZFDouble.ZERO_DBL;// 累计增值税
		DZFDouble ywsr_total = DZFDouble.ZERO_DBL;// 累计主营业务收入
		DZFDouble sl_total = DZFDouble.ZERO_DBL;// 累计增值税税负率

		if (curr_fsvos != null && curr_fsvos.size() > 0) {
			for (FseJyeVO vo : curr_fsvos) {
				if (vo.getKmbm().equals(xxcode)) {
					xx = VoUtils.getDZFDouble(vo.getFsdf());
					xx_total = VoUtils.getDZFDouble(vo.getDftotal());
				}
				if (vo.getKmbm().equals(jxcode)) {
					jx =VoUtils.getDZFDouble(vo.getFsjf());
					jx_total = VoUtils.getDZFDouble(vo.getJftotal());
				}

				if (vo.getKmbm().equals(ldcode)) {
					ld = VoUtils.getDZFDouble(vo.getQcdf()).doubleValue() < 0 ? VoUtils.getDZFDouble(vo.getQcdf()) : DZFDouble.ZERO_DBL;
				}

				if (!StringUtil.isEmpty(zyywsrcode) && vo.getKmbm().equals(zyywsrcode)) {
					ywsr = VoUtils.getDZFDouble(vo.getFsdf());
					ywsr_total  = VoUtils.getDZFDouble(vo.getDftotal());
				}
			}

			if ("小规模纳税人".equals(charname)) {
				zzs = xx.doubleValue()<0?DZFDouble.ZERO_DBL:xx;
				sl = zzs.div(ywsr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
				
				//累计税负
				zzs_total = xx_total.doubleValue()<0?DZFDouble.ZERO_DBL:xx_total;
				sl_total = zzs_total.div(ywsr_total).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
			} else {
				zzs = xx.sub(jx).add(ld).doubleValue()<0?DZFDouble.ZERO_DBL:xx.sub(jx).add(ld);
				sl = (zzs).div(ywsr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
				
				//累计税负
				zzs_total = xx_total.sub(jx_total).doubleValue()<0?DZFDouble.ZERO_DBL:xx_total.sub(jx_total);
				sl_total = (zzs_total).div(ywsr_total).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
			}
		}
		
		return new DZFDouble[]{zzs,ywsr,sl,sl_total};
	}

	private String getTips(Map<String, List<FseJyeVO>> monthmap, String period, String xxcode, String jxcode,
			String ldcode, String charname) {
		DZFDouble zzs1 = DZFDouble.ZERO_DBL;
		DZFDouble zzs2 = DZFDouble.ZERO_DBL;
		//查询增值税
		zzs1 = getZzsHj(period, monthmap, xxcode, jxcode, ldcode, charname);
		zzs2 =  getZzsHj(DateUtils.getPreviousYearPeriod(period), monthmap, xxcode, jxcode, ldcode, charname);
		int year = Integer.parseInt(period.substring(0, 4));
		int month = Integer.parseInt(period.substring(5, 7));
		DZFDouble ce = SafeCompute.sub(zzs1, zzs2);
		DZFDouble sl = zzs2.doubleValue()==0?DZFDouble.ZERO_DBL:ce.div(zzs2).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
		return String.format(
				"%s年%s月应缴增值税%s元,去年同期应缴增值税%s元,同比等于%s元,同比变化率%s",
				new Object[] {year+"",month+"",Common.format(zzs1),Common.format(zzs2),Common.format(ce),sl.setScale(2, DZFDouble.ROUND_HALF_UP).toString()+"%"});
	}
	
	
	private DZFDouble getZzsHj(String period,Map<String, List<FseJyeVO>> monthmap,String xxcode, String jxcode,
			String ldcode, String charname){
		DZFDouble res = DZFDouble.ZERO_DBL;
		DZFDouble[] tvalue =null;
//		for(String str:periods){
		List<FseJyeVO> currfs = getFsvos(period, monthmap);
		tvalue =  getZzsValue(currfs, xxcode, jxcode, ldcode, "", charname);
		if(tvalue!=null ){
			res = SafeCompute.add(res, tvalue[0]);
		}
//		}
		return res;
	}

	private List<FseJyeVO> getFsvos(String period, Map<String, List<FseJyeVO>> monthmap) {

		List<FseJyeVO> fsvos = monthmap.get(period);

		if (fsvos == null) {
			return new ArrayList<FseJyeVO>();
		}

		return fsvos;
	}

	/**
	 * 查询科目明细账数据
	 * 
	 * @param qj
	 * @param paramvo
	 * @param vo
	 * @param cpvo
	 * @return
	 */
	private Object[] getBaseData(String period, CorpVO cpvo) {
		Object[] obj;
		DZFDate begdate = cpvo.getBegindate();
		QueryParamVO paramvo = new QueryParamVO();
		paramvo.setPk_corp(cpvo.getPk_corp());
		paramvo.setBegindate1(begdate);
		paramvo.setEnddate(DateUtils.getPeriodEndDate(period));
		paramvo.setIshasjz(DZFBoolean.FALSE);
		paramvo.setXswyewfs(DZFBoolean.FALSE);
		paramvo.setBtotalyear(DZFBoolean.TRUE);// 本年累计
		paramvo.setCjq(1);
		paramvo.setCjz(6);
		obj = gl_rep_kmmxjserv.getKMMXZVOs1(paramvo, false);// 获取基础数据(科目明细账)
		return obj;
	}

	private String getZyywsr(CorpVO cpvo) {
		String zyywsrcode = "";
		Integer corpschema = zxkjPlatformService.getAccountSchema(cpvo.getPk_corp());
		if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {// 2007会计准则
			zyywsrcode = "6001";
		} else if (corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {// 2013会计准则
			zyywsrcode = "5001";
		} else {
			return "";
		}
		return zyywsrcode;
	}

	@Override
	public Object[] querySdsBg(String period, CorpVO cpvo) throws DZFWarpException {
		// 所得税查询
		String year = period.substring(0, 4);
		// 获取科目明细表的数据
		Object[] kmmxobjs = getBaseData(year + "-12", cpvo);
		// 利润表季报数据
		int month = Integer.parseInt(period.substring(5, 7));
		SdsBgVo[] sdsvos = getSdsBgvo(year,month,period, cpvo, kmmxobjs);

		String tips = getsdstips(period, cpvo, year, kmmxobjs, sdsvos);

		return new Object[] {tips, sdsvos };
	}


	private String getsdstips(String period, CorpVO cpvo, String year, Object[] kmmxobjs, SdsBgVo[] sdsvos) {
		// "2017年1-10月应缴企业所得税1245元，去年同期应缴企业所得税1243元，同比等于0元，同比0%"
		String befyear = String.valueOf(Integer.parseInt(year) - 1);
		int month = Integer.parseInt(period.substring(5, 7));
		SdsBgVo[] sdsvos2 = getSdsBgvo(befyear,month,period, cpvo, kmmxobjs);

		Object[] objs1 =getSdsHj(sdsvos, month);
		Object[] objs2 = getSdsHj(sdsvos2, month);
				
		DZFDouble sds1 = (DZFDouble) objs1[0];
		DZFDouble sds2 = (DZFDouble) objs2[0];//getSdsHj(sdsvos2, month);
		DZFDouble ce = SafeCompute.sub(sds1, sds2);
		DZFDouble sl = sds2.doubleValue() == 0 ? DZFDouble.ZERO_DBL : ce.div(sds2).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
		
		return String.format(
				"%s年%s月应缴企业所得税%s元,去年同期应缴企业所得税%s元,同比等于%s元,同比变化率%s",
				new Object[] {year+"",objs1[1]+"",Common.format(sds1),Common.format(sds2),Common.format( ce),sl.setScale(2, DZFDouble.ROUND_HALF_UP).toString()+"%"});
	}


	private Object[] getSdsHj(SdsBgVo[] sdsvos, int month) {
		DZFDouble sds = DZFDouble.ZERO_DBL;
		String monthtips = "1~3";
		if(sdsvos!=null && sdsvos.length>0){
			for(SdsBgVo vo:sdsvos){
				if("第一季度".equals(vo.getMonth()) && month>=1 && month<=3 ){
					sds= SafeCompute.add(sds, new DZFDouble(vo.getSds().replace(",", "")));
					monthtips = "1~3";
				}else if("第二季度".equals(vo.getMonth()) && month>=4 && month<=6){
					sds= SafeCompute.add(sds, new DZFDouble(vo.getSds().replace(",", "")));
					monthtips = "4~6";
				}else if("第三季度".equals(vo.getMonth()) && month>=7 && month<=9 ){
					sds= SafeCompute.add(sds, new DZFDouble(vo.getSds().replace(",", "")));
					monthtips = "7~9";
				}else if("第四季度".equals(vo.getMonth()) && month>=10 && month<=12 ){
					sds= SafeCompute.add(sds, new DZFDouble(vo.getSds().replace(",", "")));
					monthtips = "10~12";
				}
			}
		}
		return new Object[]{sds,monthtips};
	}


	private SdsBgVo[] getSdsBgvo(String year,int month,String period, CorpVO cpvo, Object[] kmmxobjs) {
		Map<String, LrbquarterlyVO[]>  lrbjbs = getLrbQuarterly(cpvo, year+"-12", kmmxobjs);
		String jdstr = getJdFromMonth(month);
		String[] jd = new String[]{"第一季度","第二季度","第三季度","第四季度"};
		SdsBgVo[] sdsvos = new SdsBgVo[4];
		DZFDouble ljsf = DZFDouble.ZERO_DBL;
		for(int i = 0 ;i<jd.length;i++){
			sdsvos[i] = new SdsBgVo();
			sdsvos[i].setMonth(jd[i]);
			LrbquarterlyVO[] jdlrbvos = lrbjbs.get(year+jd[i]);
			//季度税负(所得税）/（本季度营业收入）*100%
			//所得税
			DZFDouble jdsdsvalue =  DZFDouble.ZERO_DBL;
			if(jdlrbvos!=null && jdlrbvos.length>0){
//				jdsdsvalue = gl_qmclserv.getQuarterlySdsShui1(cpvo.getPk_corp(), year+"-"+String.format("%02d", (i+1)*3) , jdlrbvos);
				jdsdsvalue = getSdsJd(jd, i, jdlrbvos, DZFBoolean.FALSE);
			}
			//取营业收入
			DZFDouble yysr = getYysrJd(jd, i, jdlrbvos,DZFBoolean.FALSE);
			DZFDouble jdsf = yysr.doubleValue()==0?DZFDouble.ZERO_DBL:jdsdsvalue.div(yysr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
			sdsvos[i].setJdsf(Common.format(jdsf)+"%");
//			if(jdstr.equals(jd[i])){//最后一年算本年累计
				yysr = getYysrJd(jd, i, jdlrbvos,DZFBoolean.TRUE);
				DZFDouble ljsdsvalue = DZFDouble.ZERO_DBL;
				if(jdlrbvos!=null && jdlrbvos.length>0){
//					ljsdsvalue= gl_qmclserv.getQuarterlySdsShuiYear1(cpvo.getPk_corp(), year+"-"+String.format("%02d", (i+1)*3) , jdlrbvos);
					ljsdsvalue = getSdsJd(jd, i, jdlrbvos, DZFBoolean.TRUE);
				}
				//累计税负(所得税）/（本年营业收入）*100%
				ljsf = yysr.doubleValue()==0? DZFDouble.ZERO_DBL:ljsdsvalue.div(yysr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
//			}
				sdsvos[i].setLjsf(Common.format(ljsf)+"%");
			sdsvos[i].setSds(Common.format(jdsdsvalue));//计算所得税
		}
//		for(SdsBgVo vo:sdsvos){
//			vo.setLjsf(Common.format(ljsf)+"%");
//		}
		return sdsvos;
	}

	private String getJdFromMonth(int month) {
		
		if(month>=1 && month<=3){
			return "第一季度";
		}else if(month>=4 && month<=6){
			return "第二季度";
		}else if(month>=7 && month<=9){
			return "第三季度";
		}else if(month>=10 && month<=12){
			return "第四季度";
		}
		return "";
	}

	private DZFDouble getSdsJd(String[] jd, int i, LrbquarterlyVO[] jdlrbvos,DZFBoolean byear) {
		DZFDouble  yysr = DZFDouble.ZERO_DBL;//营业收入
		if(jdlrbvos!=null && jdlrbvos.length>0){
			for(LrbquarterlyVO votemp: jdlrbvos){
				DZFDouble tempvalue = DZFDouble.ZERO_DBL;
				if(byear!=null && byear.booleanValue()){
					tempvalue = votemp.getBnljje();
				}else{
					if(jd[i].indexOf("第一季度")>=0){
						tempvalue = votemp.getQuarterFirst();
					}else if(jd[i].indexOf("第二季度")>=0){
						tempvalue = votemp.getQuarterSecond();
					}else if(jd[i].indexOf("第三季度")>=0){
						tempvalue = votemp.getQuarterThird();
					}else if(jd[i].indexOf("第四季度")>=0){
						tempvalue = votemp.getQuarterFourth();
					}
				}
				if("减：所得税费用".equals(votemp.getXm())
						|| "减：所得税".equals(votemp.getXm())){
					yysr = SafeCompute.add(yysr, tempvalue);
				}
			}
		}
		return yysr;
	}
	private DZFDouble getYysrJd(String[] jd, int i, LrbquarterlyVO[] jdlrbvos,DZFBoolean byear) {
		DZFDouble  yysr = DZFDouble.ZERO_DBL;//营业收入
		if(jdlrbvos!=null && jdlrbvos.length>0){
			for(LrbquarterlyVO votemp: jdlrbvos){
				DZFDouble tempvalue = DZFDouble.ZERO_DBL;
				if(byear!=null && byear.booleanValue()){
					tempvalue = votemp.getBnljje();
				}else{
					if(jd[i].indexOf("第一季度")>=0){
						tempvalue = votemp.getQuarterFirst();
					}else if(jd[i].indexOf("第二季度")>=0){
						tempvalue = votemp.getQuarterSecond();
					}else if(jd[i].indexOf("第三季度")>=0){
						tempvalue = votemp.getQuarterThird();
					}else if(jd[i].indexOf("第四季度")>=0){
						tempvalue = votemp.getQuarterFourth();
					}
				}
				if(votemp.getXm().indexOf("营业收入")>0){
					yysr = SafeCompute.add(yysr, tempvalue);
				}
			}
		}
		return yysr;
	}
	
	private Map<String, LrbquarterlyVO[]>  getLrbQuarterly(CorpVO cpvo,String period, Object[] kmmxobjs) {
		QueryParamVO paramvo = new QueryParamVO();
		paramvo.setBegindate1(cpvo.getBegindate());
		paramvo.setPk_corp(cpvo.getPk_corp());
		paramvo.setEnddate(DateUtils.getPeriodEndDate(period));
		Map<String, LrbquarterlyVO[]> lrbjb = gl_rep_lrbquarterlyserv.getLRBquarterlyVOs(paramvo, kmmxobjs);
		return lrbjb;
	}
	
	private String getJDCurPeriod(String period){
		String befperiod = period;
		for(int i = 0;i<3;i++){
			if(Integer.parseInt(befperiod.substring(5, 7))%3==0){
				return befperiod;
			}
			befperiod = DateUtils.getPreviousPeriod(befperiod);
		}
		return befperiod;
	}
	
	private String getJDPreperiod(String period){
		String befperiod = period;
		for(int i = 0;i<3;i++){
			befperiod = DateUtils.getPreviousPeriod(befperiod);
			if(Integer.parseInt(befperiod.substring(5, 7))%3==0){
				return befperiod;
			}
		}
		return befperiod;
	}
	
	@Override
	public FkTjBgVo[] queryFktj(DZFDate enddate, CorpVO cpvo) throws DZFWarpException {
		String queryAccountRule = zxkjPlatformService.queryAccountRule(cpvo.getPk_corp());
		Object[] kmmxobjs = getBaseData(DateUtils.getPeriod(enddate), cpvo);
		Object[] fsobjs = gl_rep_fsyebserv.getEveryPeriodFsJyeVOs(cpvo.getBegindate(),enddate, cpvo.getPk_corp(), kmmxobjs,"",null);
		Map<String, List<FseJyeVO>> monthmap = (Map<String, List<FseJyeVO>>) fsobjs[0];
		String period = DateUtils.getPeriod(enddate);
		String befperiod = DateUtils.getPreviousPeriod(period);
		List<FseJyeVO> curr_fsvos = monthmap.get(period);
		List<FseJyeVO> bef_fsvos = monthmap.get(befperiod);
		Integer corpschema = zxkjPlatformService.getAccountSchema(cpvo.getPk_corp());
		if(corpschema.intValue() !=  DzfUtil.SEVENSCHEMA.intValue() && corpschema.intValue() !=  DzfUtil.THIRTEENSCHEMA.intValue()){
			throw new BusinessException("该制度暂不支持,敬请期待");
		}
		//取发生额余额表的数据
		List<FkTjBgVo> reslit = new ArrayList<FkTjBgVo>();
		String gs = "";
		String regex = "(qmye|bnjf|bndf|bqdf|bqjf|sqdf|sqjf)\\(([^\\(\\)]*?)\\)";
		Pattern p = Pattern.compile(regex);
		for (FkTjBgEnum e1 : FkTjBgEnum.values()) {
			FkTjBgVo vo = new FkTjBgVo();
			vo.setZbmc(e1.getZbmc());
			vo.setFxjg(e1.getFxjg());
			//处理有公式的情况
			//特殊处理的情况
			if(e1.getZbmc().equals("流转税申报收入与所得税申报收入不一致")){
			    period = getJDCurPeriod(DateUtils.getPeriod(enddate));
			    befperiod = getJDPreperiod(period);
				DZFDouble sbsr = DZFDouble.ZERO_DBL;
				DZFDouble yysr = DZFDouble.ZERO_DBL;
				DZFDouble sbsr_sq = DZFDouble.ZERO_DBL;
				DZFDouble yysr_sq = DZFDouble.ZERO_DBL;
				if(cpvo.getChargedeptname().equals("一般纳税人")){
					sbsr = zxkjPlatformService.getTaxValue(cpvo, "增值税纳税申报表（适用于增值税一般纳税人）", period, new int[][]{{11,6},{11,12}});
					yysr = zxkjPlatformService.getTaxValue(cpvo, "所得税月(季)度纳税申报表(A类）", period, new int[][]{{6,8}});
					
					sbsr_sq = zxkjPlatformService.getTaxValue(cpvo, "增值税纳税申报表（适用于增值税一般纳税人）", befperiod, new int[][]{{11,6},{11,12}});
					yysr_sq = zxkjPlatformService.getTaxValue(cpvo, "所得税月(季)度纳税申报表(A类）", befperiod, new int[][]{{6,8}});
				}else{
					yysr = zxkjPlatformService.getTaxValue(cpvo, "所得税月(季)度纳税申报表(A类）", period, new int[][]{{6,8}});
					sbsr =  zxkjPlatformService.getTaxValue(cpvo, "增值税纳税申报表", period, new int[][]{{5,4},{5,5},{8,4},{8,5},{13,4},{13,5}});
					
					yysr_sq = zxkjPlatformService.getTaxValue(cpvo, "所得税月(季)度纳税申报表(A类）", befperiod, new int[][]{{6,8}});
					sbsr_sq =  zxkjPlatformService.getTaxValue(cpvo, "增值税纳税申报表", befperiod, new int[][]{{5,4},{5,5},{8,4},{8,5},{13,4},{13,5}});
				}
				if(sbsr.sub(yysr).doubleValue()!=0){
					vo.setFx(1);
				}else{
					vo.setFx(0);
				}
				vo.setFxstr(getFxStr(vo.getFx()));
				vo.setJy(vo.getFx());
				vo.setJystr(getJyStr(vo.getJy()));
				
				//子表数据赋值
				FktjBgBvo bvo1 = new FktjBgBvo();
				bvo1.setName("流转税申报收入");
				bvo1.setBq(Common.format(sbsr));//本期值
				bvo1.setSq(Common.format(sbsr_sq));//上期值
				
				FktjBgBvo bvo2 = new FktjBgBvo();
				bvo2.setName("所得税申报收入");
				bvo2.setBq(Common.format(yysr));//本期值
				bvo2.setSq(Common.format(yysr_sq));//上期值
				vo.setFktjbgbvos(new FktjBgBvo[]{bvo1,bvo2});
			}else{
				gs = putGsVo(curr_fsvos, bef_fsvos, corpschema, gs, p, e1, vo,queryAccountRule);
			}
			reslit.add(vo);
		}
		return reslit.toArray(new FkTjBgVo[0]);
	}

	private String putGsVo(List<FseJyeVO> curr_fsvos, List<FseJyeVO> bef_fsvos, Integer corpschema, String gs,
			Pattern p,  FkTjBgEnum e1, FkTjBgVo vo,String queryAccountRule) {
		Map<String, Object[]> keymap =  new LinkedHashMap<String, Object[]>();
		Matcher m = null;
		String unknowgs = "";
		if(corpschema.intValue() ==  DzfUtil.SEVENSCHEMA.intValue()){//07会计准则
			gs = e1.getGs07();
			unknowgs = e1.getUnknown07();
		}else if(corpschema.intValue() ==  DzfUtil.THIRTEENSCHEMA.intValue()){//13会计准则
			gs = e1.getGs13();
			unknowgs = e1.getUnknown13();
		} 
		//解析公式
		m = p.matcher(gs);
		String key = "";
		while(m.find()){
			key = getNewKey(m.group(0), queryAccountRule);
			gs = gs.replace(m.group(0), key);
			unknowgs= unknowgs.replace(m.group(0), key);
			keymap.put(key, new Object[]{"",DZFDouble.ZERO_DBL});
		}
		//找到map对应的值
		for(Entry<String, Object[]> entry:keymap.entrySet()){
			String en_key = entry.getKey();
			
			//获取新的科目体系
			if(en_key.indexOf("qmye")>=0 || en_key.indexOf("bnjf")>=0 || en_key.indexOf("bndf")>=0
					|| en_key.indexOf("bqjf")>=0 || en_key.indexOf("bqdf")>=0){
				if(curr_fsvos!=null && curr_fsvos.size()>0){
					for(FseJyeVO fvo:curr_fsvos){
						if(en_key.indexOf(fvo.getKmbm())>=0){
							if(en_key.indexOf("qmye")>=0){
								keymap.put(en_key, new Object[]{fvo.getKmmc(),VoUtils.getDZFDouble(SafeCompute.add(fvo.getQmjf(), fvo.getQmdf()))});
							}else if(en_key.indexOf("bnjf")>=0){
								keymap.put(en_key, new Object[]{fvo.getKmmc(),VoUtils.getDZFDouble(fvo.getJftotal())});
							}else if(en_key.indexOf("bndf")>=0){
								keymap.put(en_key, new Object[]{fvo.getKmmc(),VoUtils.getDZFDouble(fvo.getDftotal())});
							}else if(en_key.indexOf("bqjf")>=0){
								keymap.put(en_key, new Object[]{fvo.getKmmc(),VoUtils.getDZFDouble(fvo.getFsjf())});
							}else if(en_key.indexOf("bqdf")>=0){
								keymap.put(en_key, new Object[]{fvo.getKmmc(),VoUtils.getDZFDouble(fvo.getFsdf())} );
							}
						}
					}
				}
			}else if(en_key.indexOf("sqjf")>=0 || en_key.indexOf("sqdf")>=0){
				if(bef_fsvos!=null && bef_fsvos.size()>0){
					for(FseJyeVO fvo:bef_fsvos){
						if (en_key.indexOf(fvo.getKmbm()) >= 0) {
							if (en_key.indexOf("sqjf") >= 0) {
								keymap.put(en_key, new Object[] { fvo.getKmmc(), VoUtils.getDZFDouble(fvo.getFsjf()) });
							} else if (en_key.indexOf("sqdf") >= 0) {
								keymap.put(en_key, new Object[] { fvo.getKmmc(), VoUtils.getDZFDouble(fvo.getFsdf()) });
							}
						}
					}
				}
			}
		}
		List<FktjBgBvo> blists = new ArrayList<FktjBgBvo>();
		DZFDouble bqvalue = DZFDouble.ZERO_DBL;
		DZFDouble sqvalue = DZFDouble.ZERO_DBL;
		Set<String> kmnameset = new HashSet<String>();
		for (Entry<String, Object[]> entry : keymap.entrySet()) {
			unknowgs = unknowgs.replace(entry.getKey(), ((DZFDouble)entry.getValue()[1]).toString());
			gs = gs.replace(entry.getKey(), "("+((DZFDouble)entry.getValue()[1]).toString()+")");
			
			if(curr_fsvos!=null && curr_fsvos.size()>0){//本期值
				for(FseJyeVO fvo:curr_fsvos){
					if(entry.getKey().indexOf(fvo.getKmbm())>=0){
						bqvalue = putGsvo1(entry, fvo);
					}
				}
			}
			if(bef_fsvos!=null && bef_fsvos.size()>0){//上期值
				for(FseJyeVO fvo:bef_fsvos){
					if(entry.getKey().indexOf(fvo.getKmbm())>=0){
						sqvalue = putGsvo1(entry, fvo);
					}
				}
			}
			if(!kmnameset.contains(entry.getValue()[0])){//是否包含科目名称
				if(!StringUtil.isEmpty((String)entry.getValue()[0])){
					FktjBgBvo bvo = new FktjBgBvo();
					bvo.setName((String)entry.getValue()[0]);
					bvo.setBq(Common.format(bqvalue));//本期值
					bvo.setSq(Common.format(sqvalue));//上期值
					blists.add(bvo);
					kmnameset.add((String)entry.getValue()[0]);
				}
			}
		}
		String fxjg = String.valueOf(CalculationUtil.CalculationFormula(gs));//风险结果
		vo.setFx(!StringUtil.isEmpty(gs)?new DZFDouble(fxjg).intValue():2);//风险
		String unmsg = String.valueOf(CalculationUtil.CalculationFormula(unknowgs));
		if("true".equals(unmsg)){
			vo.setFx(2);//
		}
		vo.setFxstr(getFxStr(vo.getFx()));
		vo.setJy(vo.getFx());//建议
		vo.setJystr(getJyStr(vo.getJy()));
		//项目处理
		vo.setFktjbgbvos(blists.toArray(new FktjBgBvo[0]));
		return gs;
	}
	
	private String getNewKey(String key,String queryAccountRule){
		String regEx="[^0-9]";   
		Pattern p = Pattern.compile(regEx);   
		Matcher m = p.matcher(key);   
		String oldkmbm = m.replaceAll("").trim();
		
		String newkmbm = zxkjPlatformService.getNewRuleCode(oldkmbm, DZFConstant.ACCOUNTCODERULE, queryAccountRule);// 销项
		
		key = key.replace(oldkmbm, newkmbm);
		
		return key;
	}


	private DZFDouble putGsvo1(Entry<String, Object[]> entry, FseJyeVO fvo) {
		DZFDouble bqvalue = DZFDouble.ZERO_DBL;
		if(entry.getKey().indexOf("bqjf")>=0){
			bqvalue = VoUtils.getDZFDouble(fvo.getFsjf());
		}else if(entry.getKey().indexOf("bqdf")>=0){
			bqvalue = VoUtils.getDZFDouble(fvo.getFsdf());
		} else if(entry.getKey().indexOf("qmye")>=0){
			if("借".equals(fvo.getFx())){
				bqvalue = VoUtils.getDZFDouble(fvo.getQmjf());
			}else{
				bqvalue = VoUtils.getDZFDouble(fvo.getQmdf());
			} 
		}else if(entry.getKey().indexOf("bnjf")>=0){
			bqvalue = VoUtils.getDZFDouble(fvo.getJftotal());
		}else if(entry.getKey().indexOf("bndf")>=0){
			bqvalue = VoUtils.getDZFDouble(fvo.getDftotal());
		}
		return bqvalue;
	}

	private String getJyStr(Integer jy) {
		if(jy == null){
			return "调整取数方式";
		}
		switch (jy.intValue()) {
		case 0:
			return "赞一个,请继续保持";
		case 1:
			return "及时调账";
		default:
			return "调账取数方式";
		}
	}

	private String getFxStr(Integer fx) {
		if(fx == null){
			return "未知";
		}
		switch (fx.intValue()) {
		case 0:
			return "极低";
		case 1:
			return "爆表";
		default:
			return "未知";
		}
	}


	public static List<String> getJDListPeriod(String period) {
		String year = period.substring(0, 4);
		int month = Integer.parseInt(period.substring(5, 7));
		List<String> list = new ArrayList<String>();
		String periodtemp = "";
		String qj = "";
		for(int i =0;i<3;i++){
			periodtemp = year+"-"+ String.format("%02d", (month-i));
			qj = DateUtils.getPeriodStartDate(periodtemp).toString()+DateUtils.getPeriodEndDate(periodtemp).toString();
			list.add(qj);
			if((month-i)%3 ==1){
				break;
			}
		}
		for(int i =1;i<3;i++){
			if((month+i)>12 || month%3 == 0){
				break;
			}
			
			periodtemp = year+"-"+ String.format("%02d", (month+i));
			qj = DateUtils.getPeriodStartDate(periodtemp).toString()+DateUtils.getPeriodEndDate(periodtemp).toString();
			list.add(qj);
			if((month+i)%3 ==0){
				break;
			}
		}
		
		Collections.sort(list);
		
		return list;
	}


	
	
	private <T> T readJsonValue(String strJSON, Class<T> clazz) throws DZFWarpException {
		try {
			return getObjectMapper().readValue(strJSON, clazz);
		} catch (Exception e) {
			throw new WiseRunException(e);
		}
	}

	private ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {

			@Override
			public void serialize(Object value, JsonGenerator jg, SerializerProvider sp)
					throws IOException, JsonProcessingException {
				jg.writeString("");
			}
		});
		objectMapper.setSerializationInclusion(Include.ALWAYS);
		objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		objectMapper.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, false);
		objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		return objectMapper;
	}

	@Override
	public FkTjSetVo[] query(String pk_corp,DZFDate begdate,DZFDate enddate) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(begdate);
		sp.addParam(enddate);
		FkTjSetVo[] setvos = (FkTjSetVo[]) singleObjectBO.queryByCondition(FkTjSetVo.class,
				"nvl(dr,0)=0 and pk_corp = ? and substr(inspectdate,0,10) >=? and substr(inspectdate,0,10)<=? order by inspectdate desc ", sp);
		if(setvos!=null && setvos.length>0){
			for(FkTjSetVo vo:setvos){
				if(StringUtil.isEmpty(vo.getHy())){
					vo.setHy("");
				}
			}
		}
		return setvos;
	}

	@Override
	public void save(FkTjSetVo vo) throws DZFWarpException {
		// 赋值行业
		CorpVO cpvo = zxkjPlatformService.queryCorpByPk(vo.getPk_corp());

		if (!StringUtil.isEmpty(cpvo.getIndustry())) {
			SQLParameter sp = new SQLParameter();

			String qrysql = "select tradename from  ynt_bd_trade  where pk_trade =? and nvl(dr,0)=0 ";

			sp.addParam(cpvo.getIndustry());

			List<String> tradenames = (List<String>) singleObjectBO.executeQuery(qrysql, sp, new ColumnListProcessor());

			if (tradenames != null && tradenames.size() > 0) {
				vo.setHy(tradenames.get(0));
			}
		}

		singleObjectBO.saveObject(vo.getPk_corp(), vo);

	}
}
