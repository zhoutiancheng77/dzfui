package com.dzf.zxkj.report.service.cwbb.impl;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.qcset.YntXjllqcyePageVO;
import com.dzf.zxkj.platform.model.report.XjllMxvo;
import com.dzf.zxkj.platform.model.report.XjllbVO;
import com.dzf.zxkj.platform.model.report.XjllquarterlyVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwbb.IXjllbQuarterlyReport;
import com.dzf.zxkj.report.service.cwbb.IXjllbReport;
import com.dzf.zxkj.report.service.cwzb.IFsYeReport;
import com.dzf.zxkj.report.utils.CompanyXjlReportService;
import com.dzf.zxkj.report.utils.PopularXjlReportService;
import com.dzf.zxkj.report.utils.Qy07XjlReportService;
import com.dzf.zxkj.report.utils.XQy13XjlReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 现金流量表
 * 
 * @author gejw
 *
 */
@Service("gl_rep_xjlybserv")
@Slf4j
public class XjllbReportImpl implements IXjllbReport {

	@Reference
	private IZxkjPlatformService zxkjPlatformService;

	private XjllReportService xjllReportService = null;

	public XjllReportService getXjllReportService() {
		return xjllReportService;
	}

	@Autowired
	private IFsYeReport gl_rep_fsyebserv;

	@Autowired
	public void setXjllReportService(XjllReportService xjllReportService) {
		this.xjllReportService = xjllReportService;
	}

	@Override
	public XjllbVO[] query(QueryParamVO vo) throws DZFWarpException {
		return xjllReportService.getXjllbVOs(vo.getQjq(), vo.getPk_corp());
	}

	@Override
	public XjllMxvo[] getXJllMX(String period, String pk_corp, String hc) throws DZFWarpException {
		return xjllReportService.getXJllMX(period, pk_corp, hc);
	}

	@Override
	public Map<String, XjllbVO[]> queryEveryPeriod(QueryParamVO vo) throws DZFWarpException {
		Map<String, XjllbVO[]> map = xjllReportService.getXjllbVOsPeriods(vo.getQjq(),vo.getQjz(), vo.getPk_corp());
		return map;
	}

	@Override
	public XjllbVO[] getXjllDataForCwBs(String qj, String corpIds, String qjlx) throws DZFWarpException {
		if ("0".equals(qjlx)) {
			CorpVO cpvo = zxkjPlatformService.queryCorpByPk(corpIds);
			IXjllbReport gl_rep_xjlybserv = (IXjllbReport) SpringUtils.getBean("gl_rep_xjlybserv");
			QueryParamVO paramvo = new QueryParamVO();
			paramvo.setPk_corp(corpIds);
			paramvo.setQjq(qj.substring(0, 7));
			paramvo.setQjz(qj.substring(0, 7));
			paramvo.setIshasjz(DZFBoolean.FALSE);
			paramvo.setBegindate1(DateUtils.getPeriodStartDate(qj.substring(0, 7)));
			XjllbVO[] xjllbvos = gl_rep_xjlybserv.query(paramvo);
			
			//如果是一般企业则取上年数据
			Integer corpschema = zxkjPlatformService.getAccountSchema(corpIds);
			if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {//07科目体系(一般企业) 需要查询上年同期数
				//查询上年数据
				String bef_period = DateUtils.getPreviousYearPeriod(qj.substring(0, 7));
				if(DateUtils.getPeriod(cpvo.getBegindate()).compareTo(bef_period)<=0){//公司建账日期在查询日期后时
					paramvo = new QueryParamVO();
					paramvo.setPk_corp(corpIds);
					paramvo.setQjq(bef_period);
					paramvo.setQjz(bef_period);
					paramvo.setIshasjz(DZFBoolean.FALSE);
					paramvo.setBegindate1(DateUtils.getPeriodStartDate(bef_period));
					XjllbVO[] bef_xjllbvos = gl_rep_xjlybserv.query(paramvo);
					for(XjllbVO xjllvo:xjllbvos){
						for(XjllbVO lastxjllvo:bef_xjllbvos){
							if(!StringUtil.isEmpty(xjllvo.getXm())
									&& xjllvo.getXm().equals(lastxjllvo.getXm())){
								xjllvo.setBqje_last(lastxjllvo.getBqje());
								xjllvo.setSqje_last(lastxjllvo.getSqje());
								break;
							}
						}
					}
				}
			}
			return xjllbvos;
		} else {
			IXjllbQuarterlyReport gl_rep_xjlyquarbserv = (IXjllbQuarterlyReport) SpringUtils
					.getBean("gl_rep_xjlyquarbserv");
			QueryParamVO paramvo = new QueryParamVO();
			paramvo.setBegindate1(DateUtils.getPeriodStartDate(qj.substring(0, 7)));
			paramvo.setPk_corp(corpIds);
			String[] resvalue = getJdValue(paramvo.getBegindate1());
			List<XjllquarterlyVo> xjllbvos_jd = gl_rep_xjlyquarbserv.getXjllQuartervos(paramvo, resvalue[1]);
			XjllbVO[] xjllbvos = converXjll(xjllbvos_jd, qj);
			return xjllbvos;
		}
	}
	
	private String[] getJdValue(DZFDate date){
		String month = date.toString().substring(5, 7);
		String res = null;
		String count = "0";
		if(month.equals("02") || month.equals("01") || month.equals("03") ){
			res = "第一季度";
			count ="1";
		}else if(month.equals("04") || month.equals("05") || month.equals("06") ){
			res = "第二季度";
			count ="2";
		}else if(month.equals("07") || month.equals("08") || month.equals("09") ){
			res = "第三季度";
			count ="3";
		}else if(month.equals("10") || month.equals("11") || month.equals("12") ){
			res = "第四季度";
			count ="4";
		}
		return new String[]{res,count};
	}

	
	private XjllbVO[] converXjll(List<XjllquarterlyVo> xjllbjb, String qj) {
		if (xjllbjb == null || xjllbjb.size() == 0) {
			throw new BusinessException("现金流量季报数据为空");
		}

		List<XjllbVO> list = new ArrayList<>();

		String month = qj.substring(5, 7);

		XjllbVO xjllvo;
		for (XjllquarterlyVo jbvo : xjllbjb) {
			xjllvo = new XjllbVO();
			xjllvo.setXm(jbvo.getXm());
			xjllvo.setHc(jbvo.getHc());
			xjllvo.setSqje(jbvo.getBnlj());

			if ("03".equals(month)) {
				xjllvo.setBqje(jbvo.getJd1());
				xjllvo.setBqje_last(jbvo.getJd1_last());
			} else if ("06".equals(month)) {
				xjllvo.setBqje(jbvo.getJd2());
				xjllvo.setBqje_last(jbvo.getJd2_last());
			} else if ("09".equals(month)) {
				xjllvo.setBqje(jbvo.getJd3());
				xjllvo.setBqje_last(jbvo.getJd3_last());
			} else {
				xjllvo.setBqje(jbvo.getJd4());
				xjllvo.setBqje_last(jbvo.getJd4_last());
			}
			xjllvo.setSqje(jbvo.getBnlj());
			xjllvo.setSqje_last(jbvo.getBf_bnlj());

			list.add(xjllvo);
		}

		return list.toArray(new XjllbVO[0]);
	}

	@Override
	public List<YntXjllqcyePageVO> bulidXjllQcData(List<YntXjllqcyePageVO> listvo, String pk_corp)
			throws DZFWarpException {
		if (listvo == null || listvo.size() == 0) {
			return listvo;
		}

		Map<Float, XjllbVO> map = new LinkedHashMap<Float, XjllbVO>();
		
		if (DzfUtil.SEVENSCHEMA.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) { // 07方案
			new Qy07XjlReportService(zxkjPlatformService).getXjllB2007Xm(map);
		} else if (DzfUtil.THIRTEENSCHEMA.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) { // 13方案
			new XQy13XjlReportService(zxkjPlatformService).getXjllB2013Xm(map);
		} else if (DzfUtil.POPULARSCHEMA.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) {// 民间
			new PopularXjlReportService(gl_rep_fsyebserv).getXjllbXmVos(map);
		} else if (DzfUtil.COMPANYACCOUNTSYSTEM.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) {// 企业会计制度
			new CompanyXjlReportService(gl_rep_fsyebserv).getXjllXmVos(map);
		}

		// 季度map
		Map<String, DZFDouble[]> jdmap = new HashMap<String, DZFDouble[]>();
		// YntXjllqcyePageVO 转换成 XjllbVO
		for (YntXjllqcyePageVO vo : listvo) {
			vo.setProjectname("　　" +vo.getProjectname());
			jdmap.put("hc("+vo.getVcode()+")", new DZFDouble[] { vo.getQ1(), vo.getQ2(), vo.getQ3(), vo.getQ4(),vo.getNmny() });
		}
		if (map.size() > 0) {
			for (Entry<Float, XjllbVO> entry : map.entrySet()) {
				if(StringUtil.isEmpty(entry.getValue().getHc())){
					entry.getValue().setHc(""+entry.getValue().getRowno());
				}
				YntXjllqcyePageVO vo = new YntXjllqcyePageVO();
				vo.setVcode(entry.getValue().getHc());
				vo.setVname(entry.getValue().getXm());
				vo.setProjectname(entry.getValue().getXm());
				vo.setIsSet("1");// 不可编辑
				String formula = entry.getValue().getFormula();
				if (!StringUtil.isEmpty(formula)) {
					DZFDouble[] values = execFormula(formula, jdmap);
					vo.setQ1(values[0]);
					vo.setQ2(values[1]);
					vo.setQ3(values[2]);
					vo.setQ4(values[3]);
					vo.setNmny(values[4]);
//					vo.setNmny(values[0].add(values[1]).add(values[2]).add(values[3]));
					jdmap.put("hc("+entry.getValue().getHc()+")",values);
				}
				listvo.add(vo);
			}
		}
		
		//按照编码排序
		listvo.sort(new Comparator<YntXjllqcyePageVO>() {
			@Override
			public int compare(YntXjllqcyePageVO o1, YntXjllqcyePageVO o2) {
				Float f1 = Float.parseFloat(o1.getVcode() == null ? "0" : o1.getVcode()) ;
				Float f2 = Float.parseFloat(o2.getVcode() == null ? "0" : o2.getVcode());
				return f1.compareTo(f2) ;
			}
		});
		return listvo;
	}
	
	private DZFDouble[] execFormula(String formula, Map<String, DZFDouble[]> jdmap) {
		DZFDouble[] ress = new DZFDouble[] { DZFDouble.ZERO_DBL, DZFDouble.ZERO_DBL, DZFDouble.ZERO_DBL,
				DZFDouble.ZERO_DBL,DZFDouble.ZERO_DBL };
		String splitstr = "\\+|\\-";
		String[] strs = formula.split(splitstr);
		//四个季度的公式
		String[] formulas = new String[]{formula,formula,formula,formula,formula};
		if (strs != null && strs.length > 0) {
			for (int i = 0; i < formulas.length; i++) {
				for (String str : strs) {
					DZFDouble[] jdvalues = jdmap.get(str.trim());
					if (jdvalues == null) {
						jdvalues = new DZFDouble[] { DZFDouble.ZERO_DBL, DZFDouble.ZERO_DBL, DZFDouble.ZERO_DBL,
								DZFDouble.ZERO_DBL, DZFDouble.ZERO_DBL, };
					}
					formulas[i] = formulas[i].replace(str, jdvalues[i] == null ? "0" : jdvalues[i].toString());
				}
				ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");
				log.info("公式:" + formulas[i]);
				try {
					Object value = jse.eval(formulas[i]);
					ress[i] = new DZFDouble(value.toString());
				} catch (ScriptException e) {
					throw new WiseRunException(e);
				}
			}
		}

		return ress;
	}

}
