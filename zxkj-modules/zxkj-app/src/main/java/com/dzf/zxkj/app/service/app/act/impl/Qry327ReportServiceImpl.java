package com.dzf.zxkj.app.service.app.act.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dzf.zxkj.app.dao.app.IQryReportDao;
import com.dzf.zxkj.app.model.report.CwglInfoAppVO;
import com.dzf.zxkj.app.model.report.ZcZkInfoAppVo;
import com.dzf.zxkj.app.model.resp.bean.ReportBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.service.app.act.IQryReportService;
import com.dzf.zxkj.app.utils.ReportUtil;
import com.dzf.zxkj.app.utils.SourceSysEnum;
import com.dzf.zxkj.app.utils.VoUtils;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.CwgyInfoVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.workbench.BsWorkbenchVO;
import com.dzf.zxkj.report.service.IRemoteReportService;
import com.dzf.zxkj.report.service.IZxkjRemoteAppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 324版本报表查询
 * 
 * @author zhangj
 *
 */
@Slf4j
@Service("org327reportService")
public class Qry327ReportServiceImpl extends QryReportAbstract implements IQryReportService {

    @Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
    private IRemoteReportService iRemoteReportService;
    @Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
    private IZxkjRemoteAppService iZxkjRemoteAppService;
    @Autowired
    private SingleObjectBO singleObjectBO;
	
	@Autowired
	private IQryReportDao orgreport_dao;

	public ResponseBaseBeanVO qryMonthRep(ReportBeanVO qryDailyBean) throws DZFWarpException {
		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();

		List reslist;
		try {
			reslist = new ArrayList();

			String pk_corp = qryDailyBean.getPk_corp();
			
			CorpVO cpvo = iZxkjRemoteAppService.queryByPk(pk_corp);

			String[] peirods = getQryperiod(qryDailyBean);

			if (StringUtil.isEmpty(peirods[0]) || StringUtil.isEmpty(peirods[1])) {
				throw new BusinessException("查询区间不能为空!");
			}
			String year = peirods[0].substring(0, 4);

			//本年数据
//			Object[] obj = gl_rep_fsyebserv.getYearFsJyeVOs(year, pk_corp, null);
			Object[] obj = iRemoteReportService.getEveryPeriodFsJyeVOs(
					cpvo.getBegindate(), DateUtils.getPeriodEndDate(year+"-12"), cpvo.getPk_corp(), null,"",null);

			putdjpz(peirods[0], peirods[1], qryDailyBean, reslist);// 单据凭证

			putKped(peirods[0], peirods[1], qryDailyBean, reslist, obj, pk_corp);// 开票信息赋值

			getCwgyxx(peirods[0], peirods[1], pk_corp, reslist, obj,qryDailyBean);// 财务概要信息


			bean.setRescode(IConstant.DEFAULT);

			bean.setResmsg(reslist.toArray());
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			bean.setRescode(IConstant.FIRDES);
			if (e instanceof BusinessException) {
				bean.setResmsg("获取数据失败:" + e.getMessage());
			} else {
				bean.setResmsg("获取数据失败!");
			}
		}
		return bean;
	}

	private void getZcZk(String period, String pk_corp, List reslist) {

		Object[] objs = iRemoteReportService.getZCFZBVOsConMsg(period, pk_corp, "N", new String[] { "N", "N", "N", "N" });

		DZFDouble ldzc_qm = DZFDouble.ZERO_DBL;
		DZFDouble ldzc_nc = DZFDouble.ZERO_DBL;
		DZFDouble fldzc_qm = DZFDouble.ZERO_DBL;
		DZFDouble fldzc_nc = DZFDouble.ZERO_DBL;
		DZFDouble zchj_qm = DZFDouble.ZERO_DBL;
		DZFDouble zchj_nc = DZFDouble.ZERO_DBL;
		if (objs != null && objs.length > 0) {
			ZcFzBVO[] kmmxvos = (ZcFzBVO[]) objs[0];
			if (kmmxvos != null && kmmxvos.length > 0) {
				for (ZcFzBVO bvo : kmmxvos) {
					if (bvo.getZc().indexOf("非流动资产合计") >= 0) {
						fldzc_qm = bvo.getQmye1();
						fldzc_nc = bvo.getNcye1();
					} else if (bvo.getZc().indexOf("流动资产合计") >= 0) {
						ldzc_qm = bvo.getQmye1();
						ldzc_nc = bvo.getNcye1();
					} else if (bvo.getZc().indexOf("资产总计") >= 0) {
						zchj_qm = bvo.getQmye1();
						zchj_nc = bvo.getNcye1();
					}
				}
			}
		}

		ZcZkInfoAppVo zczkvo1 = new ZcZkInfoAppVo();
		zczkvo1.setXm("流动资产");
		zczkvo1.setNcje(Common.format(ldzc_nc));
		zczkvo1.setQmje(Common.format(ldzc_qm));

		ZcZkInfoAppVo zczkvo2 = new ZcZkInfoAppVo();
		zczkvo2.setXm("非流动资产");
		zczkvo2.setNcje(Common.format(fldzc_nc));
		zczkvo2.setQmje(Common.format(fldzc_qm));

		ZcZkInfoAppVo zczkvo3 = new ZcZkInfoAppVo();
		zczkvo3.setXm("资产合计");
		zczkvo3.setNcje(Common.format(zchj_nc));
		zczkvo3.setQmje(Common.format(zchj_qm));

		List zczk = new ArrayList();
		zczk.add(zczkvo1);
		zczk.add(zczkvo2);
		zczk.add(zczkvo3);
		reslist.add(zczk);
	}

	private void putKped(String period_begin, String period_end, ReportBeanVO qryDailyBean, List reslist, Object[] obj,
			String pk_corp) {

		if (period_begin.equals(period_end)) {// 一般人没有这个选项
			return;
		}

		Map<String, List<FseJyeVO>> monthmap = (Map<String, List<FseJyeVO>>) obj[0];

		Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>) obj[1];

		Map<String, Object> kpmap = new HashMap<String, Object>();

		int begin = Integer.parseInt(period_begin.substring(5, 7));

		int end = Integer.parseInt(period_end.substring(5, 7));

		Integer corpschema = iZxkjRemoteAppService.getAccountSchema(pk_corp);

		String year = period_begin.substring(0, 4);

		DZFDouble total = DZFDouble.ZERO_DBL;
		for (int i = begin; i <= end; i++) {
			String period = year + "-" + String.format("%02d", i);
			List<FseJyeVO> listfs = monthmap.get(period);
			for (FseJyeVO jyevo : listfs) {
				if (corpschema == DzfUtil.SEVENSCHEMA.intValue() && jyevo.getKmbm().equals("6001")) {// 07
					total = SafeCompute.add(total, jyevo.getFsdf());
					break;
				} else if (corpschema == DzfUtil.THIRTEENSCHEMA.intValue() && jyevo.getKmbm().equals("5001")) {// 13科目
					total = SafeCompute.add(total, jyevo.getFsdf());
					break;
				}
			}
		}

		DZFDouble sykped = SafeCompute.sub(new DZFDouble(300000), total).doubleValue() <= 0 ? DZFDouble.ZERO_DBL
				: SafeCompute.sub(new DZFDouble(300000), total);
		DZFDouble ynse = total.multiply(0.03);
		DZFDouble jmse = ynse.doubleValue() > new DZFDouble(300000).multiply(0.03).doubleValue() ? DZFDouble.ZERO_DBL : ynse;
		DZFDouble ybtse = total.doubleValue() <= new DZFDouble(300000).doubleValue() ? DZFDouble.ZERO_DBL : total.multiply(0.03);
		kpmap.put("sykped", Common.format(sykped));// 剩余开票额度
		kpmap.put("kpsr", Common.format(total));// 开票收入
		kpmap.put("ynse", Common.format(ynse));// 应纳税额
		kpmap.put("jmse", Common.format(jmse));// 减免税额
		kpmap.put("ybtse", Common.format(ybtse));// 应补退税额

		reslist.add(kpmap);
	}

	private void putdjpz(String period_begin, String period_end, ReportBeanVO qryDailyBean, List reslist) {

		Map<String, Object> djzjmap = new HashMap<String, Object>();
		String pk_corp = qryDailyBean.getPk_corp();

		DZFDate startdate = DateUtils.getPeriodStartDate(period_begin);

		DZFDate enddate = DateUtils.getPeriodEndDate(period_end);

		djzjmap.put("djpz", "单据凭证");
		djzjmap.put("scdj", "" + orgreport_dao.qryMonthPics(pk_corp, startdate, enddate) + "张");
		djzjmap.put("kjpz", "" + orgreport_dao.qryMonthVouchers(pk_corp, startdate, enddate) + "张");

		putNsZt(pk_corp, startdate, enddate, "isptx", "spzt", djzjmap);// 收票状态

		putNsZt(pk_corp, startdate, enddate, "taxstatecopy", "cszt", djzjmap);// 抄税状态

		putNsZt(pk_corp, startdate, enddate, "taxstateclean", "qkzt", djzjmap);// 清卡状态

		putNsZt(pk_corp, startdate, enddate, "ipzjjzt", "pzjj", djzjmap);// 凭证交接状态

		reslist.add(djzjmap);
	}

	private void putNsZt(String pk_corp, DZFDate startdate, DZFDate enddate, String column, String putcolumn,
			Map<String, Object> djzjmap) {
		List<String> periods = ReportUtil.getPeriods(startdate, enddate);
		String wherepard = SqlUtil.buildSqlForIn("period", periods.toArray(new String[0]));
		SQLParameter sp = new SQLParameter();
		String qrysql = "select * from nsworkbench where nvl(dr,0)=0 and pk_corp = ? and " + wherepard
				+ " order by period ";
		sp.addParam(pk_corp);
		List<BsWorkbenchVO> bsworklist = (List<BsWorkbenchVO>) singleObjectBO.executeQuery(qrysql, sp,
				new BeanListProcessor(BsWorkbenchVO.class));
		int first = Integer.parseInt(periods.get(0).substring(5, 7));
		if (bsworklist == null || bsworklist.size() == 0) {
			djzjmap.put(putcolumn + "num", 1);
			djzjmap.put(putcolumn, first + "月未完成");
		}
		if (bsworklist != null && bsworklist.size() > 0) {
			for (BsWorkbenchVO vo : bsworklist) {
				int month = Integer.parseInt(vo.getPeriod().substring(5, 7));
				if (vo.getAttributeValue(column) != null && (Integer) vo.getAttributeValue(column) == 1) {
					djzjmap.put(putcolumn + "num", 0);
					djzjmap.put(putcolumn, month + "月已完成");
				} else {
					djzjmap.put(putcolumn + "num", 1);
					djzjmap.put(putcolumn, month + "月未完成");
					break;
				}
			}
		}

	}

	private String[] getQryperiod(ReportBeanVO qryDailyBean) {
		String jd = qryDailyBean.getJd();

		String year = qryDailyBean.getYear();

		String period_begin = "";

		String period_end = "";

		if (!StringUtil.isEmpty(jd)) {
			if (StringUtil.isEmpty(year)) {
				throw new BusinessException("查询年度不能为空!");
			}
			if ("1".equals(jd)) {
				period_begin = year + "-01";
				period_end = year + "-03";
			} else if ("2".equals(jd)) {
				period_begin = year + "-04";
				period_end = year + "-06";
			} else if ("3".equals(jd)) {
				period_begin = year + "-07";
				period_end = year + "-09";
			} else if ("4".equals(jd)) {
				period_begin = year + "-10";
				period_end = year + "-12";
			}
		} else if (!StringUtil.isEmpty(qryDailyBean.getPeriod())) {
			period_begin = qryDailyBean.getPeriod();
			period_end = qryDailyBean.getPeriod();
		}
		return new String[] { period_begin, period_end };
	}

	private void getCwgyxx(String period_begin, String period_end, String pk_corp, List reslist, Object[] fsobj,ReportBeanVO qryDailyBean ) {

		String year = period_begin.substring(0, 4);
		int begin = Integer.parseInt(period_begin.substring(5, 7));
		int end = Integer.parseInt(period_end.substring(5, 7));
		Map<String, CwgyInfoVO[]> cwmap = iRemoteReportService.getCwgyInfoVOs(year, pk_corp, fsobj);
		CorpVO cpvo = iZxkjRemoteAppService.queryByPk(pk_corp);
		if (cwmap != null && cwmap.size() > 0) {
			String period = "";
			CwgyInfoVO[] cwvos = null;
			Map<String, DZFDouble> bqdb = new HashMap<String, DZFDouble>();// 本期
			Map<String, DZFDouble> bndb = new HashMap<String, DZFDouble>();// 本年累计
			Map<String, DZFDouble> bqdb_pre = new HashMap<String, DZFDouble>();// 去年本期
			Map<String, DZFDouble> bndb_pre = new HashMap<String, DZFDouble>();// 去年本年累计

			List ylqk = new ArrayList();// 盈利状态
			List sxqk = new ArrayList();// 三项
			List ysyfqk = new ArrayList();// 应收应付
			List zjzg = new ArrayList();// 资金状况
			List sjzk = new ArrayList();// 税金状态
			List sryj = getSryj(period_end,fsobj,pk_corp);// 收入预警
			List ylnl = new ArrayList();// 盈利能力
			List yynl = new ArrayList();// 运营能力

			DZFDouble d = DZFDouble.ZERO_DBL;
			for (int i = begin; i <= end; i++) {
				period = year + "-" + String.format("%02d", i);
				cwvos = cwmap.get(period);
				CwglInfoAppVO tempvo = null;
				if (cwvos != null && cwvos.length > 0) {
					for (CwgyInfoVO vo : cwvos) {
						tempvo = new CwglInfoAppVO();
						if (!bqdb.containsKey(vo.getXm())) {
							bqdb.put(vo.getXm(), VoUtils.getDZFDouble(vo.getByje()));
						} else {
							bqdb.put(vo.getXm(), SafeCompute.add(bqdb.get(vo.getXm()), vo.getByje()));
						}
						//去年本期
						if (bqdb_pre.containsKey(vo.getXm())) {
							bqdb_pre.put(vo.getXm(), SafeCompute.add(bqdb_pre.get(vo.getXm()), vo.getByje_pre()));
						} else {
							bqdb_pre.put(vo.getXm(), VoUtils.getDZFDouble(vo.getByje_pre()));
						}

						// 本年累计计算
						bndb.put(vo.getXm(), VoUtils.getDZFDouble(vo.getBnljje()));
						//去年本年累计
						bndb_pre.put(vo.getXm(), VoUtils.getDZFDouble(vo.getBnljje_pre()));

						tempvo.setXm(vo.getXm());
						if ("营业收入净利率".equals(vo.getXm())) {
							DZFDouble jlr = bqdb.get("净利润") == null ? DZFDouble.ZERO_DBL : bqdb.get("净利润");
							DZFDouble yysr = bqdb.get("营业收入") == null ? DZFDouble.ZERO_DBL : bqdb.get("营业收入");
							d = jlr.div(yysr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setByje((d.doubleValue() == 0 ? "0.00" : d.doubleValue()) + "%");

							jlr = bndb.get("净利润") == null ? DZFDouble.ZERO_DBL : bndb.get("净利润");
							yysr = bndb.get("营业收入") == null ? DZFDouble.ZERO_DBL : bndb.get("营业收入");
							d = jlr.div(yysr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setBnljje((d.doubleValue() == 0 ? "0.00" : d.doubleValue()) + "%");
						} else if ("费用比率".equals(vo.getXm())) {
							DZFDouble yysr = bqdb.get("营业收入") == null ? DZFDouble.ZERO_DBL : bqdb.get("营业收入");
							DZFDouble xsfy = bqdb.get("销售费用") == null ? DZFDouble.ZERO_DBL : bqdb.get("销售费用");
							DZFDouble glfy = bqdb.get("管理费用") == null ? DZFDouble.ZERO_DBL : bqdb.get("管理费用");
							DZFDouble cwfy = bqdb.get("财务费用") == null ? DZFDouble.ZERO_DBL : bqdb.get("财务费用");
							d = (xsfy.add(glfy).add(cwfy)).div(yysr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setByje((d.doubleValue() == 0 ? "0.00" : d.doubleValue()) + "%");

							yysr = bndb.get("营业收入") == null ? DZFDouble.ZERO_DBL : bndb.get("营业收入");
							xsfy = bndb.get("销售费用") == null ? DZFDouble.ZERO_DBL : bndb.get("销售费用");
							glfy = bndb.get("管理费用") == null ? DZFDouble.ZERO_DBL : bndb.get("管理费用");
							cwfy = bndb.get("财务费用") == null ? DZFDouble.ZERO_DBL : bndb.get("财务费用");

							d = (xsfy.add(glfy).add(cwfy)).div(yysr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setBnljje((d.doubleValue() == 0 ? "0.00" : d.doubleValue()) + "%");
						} else if ("增值税税负率".equals(vo.getXm())) {
							DZFDouble zzs = bqdb.get("增值税") == null ? DZFDouble.ZERO_DBL : bqdb.get("增值税");
							DZFDouble yysr = bqdb.get("营业收入") == null ? DZFDouble.ZERO_DBL : bqdb.get("营业收入");
							d = zzs.div(yysr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setByje((d.doubleValue() == 0 ? "0.00" : d.doubleValue()) + "%");

							zzs = bndb.get("增值税") == null ? DZFDouble.ZERO_DBL : bndb.get("增值税");
							yysr = bndb.get("营业收入") == null ? DZFDouble.ZERO_DBL : bndb.get("营业收入");
							d = zzs.div(yysr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setBnljje((d.doubleValue() == 0 ? "0.00" : d.doubleValue()) + "%");
						} else if("净利率".equals(vo.getXm())){
							DZFDouble jlr = bqdb.get("净利润") == null ? DZFDouble.ZERO_DBL:bqdb.get("净利润");
							DZFDouble yysr = bqdb.get("主营业务收入") == null ? DZFDouble.ZERO_DBL : bqdb.get("主营业务收入");
							
							d = jlr.div(yysr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setByje((d.doubleValue() == 0 ? "0.00" : d.doubleValue()) + "%");
							
						    jlr = bndb.get("净利润") == null ? DZFDouble.ZERO_DBL:bndb.get("净利润");
						    yysr = bndb.get("主营业务收入") == null ? DZFDouble.ZERO_DBL : bndb.get("主营业务收入");
							d = jlr.div(yysr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setBnljje((d.doubleValue() == 0 ? "0.00" : d.doubleValue()) + "%");
						}else  if("毛利率".equals(vo.getXm())){
							DZFDouble yysr = bqdb.get("主营业务收入") == null ? DZFDouble.ZERO_DBL : bqdb.get("主营业务收入");
							DZFDouble yycb = bqdb.get("主营业成本") == null ? DZFDouble.ZERO_DBL:bqdb.get("主营业成本");
							//毛利率 = （主营业务收入-主营业成本）/主营业务收入*100
							d = (yysr.sub(yycb)).div(yysr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setByje((d.doubleValue() == 0 ? "0.00" : d.doubleValue()) + "%");
							
							yysr = bndb.get("主营业务收入") == null ? DZFDouble.ZERO_DBL : bndb.get("主营业务收入");
							yycb = bndb.get("主营业成本") == null ? DZFDouble.ZERO_DBL:bndb.get("主营业成本");
							//毛利率 = （主营业务收入-主营业成本）/主营业务收入*100
							d = (yysr.sub(yycb)).div(yysr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setBnljje((d.doubleValue() == 0 ? "0.00" : d.doubleValue() )+ "%");
						}else if("收入增长率".equals(vo.getXm())){
							// 收入增长率：(营业收入增长额/上年营业收入总额)×100%
							DZFDouble yysr = bqdb.get("营业收入") == null ? DZFDouble.ZERO_DBL : bqdb.get("营业收入");
							DZFDouble yysr_pre = bqdb_pre.get("营业收入") == null ? DZFDouble.ZERO_DBL : bqdb_pre.get("营业收入");
							
							d = (yysr.sub(yysr_pre)).div(yysr_pre).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setByje((d.doubleValue() == 0 ? "0.00" : d.doubleValue() )+ "%");
							
							//本年
							yysr = bndb.get("营业收入") == null ? DZFDouble.ZERO_DBL : bndb.get("营业收入");
							yysr_pre = bndb_pre.get("营业收入") == null ? DZFDouble.ZERO_DBL : bndb_pre.get("营业收入");
							d = (yysr.sub(yysr_pre)).div(yysr_pre).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setBnljje((d.doubleValue() == 0 ? "0.00" : d.doubleValue() )+ "%");
							
						} else if("净利润增长率".equals(vo.getXm())){
							//  本期净利润增长率：(当期净利润-上期净利润)/上期净利润*100%
							DZFDouble yysr = bqdb.get("净利润") == null ? DZFDouble.ZERO_DBL : bqdb.get("净利润");
							DZFDouble yysr_pre = bqdb_pre.get("净利润") == null ? DZFDouble.ZERO_DBL : bqdb_pre.get("净利润");
							
							d = (yysr.sub(yysr_pre)).div(yysr_pre).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setByje((d.doubleValue() == 0 ? "0.00" : d.doubleValue() )+ "%");
							
							//本年
							yysr = bndb.get("净利润") == null ? DZFDouble.ZERO_DBL : bndb.get("净利润");
							yysr_pre = bndb_pre.get("净利润") == null ? DZFDouble.ZERO_DBL : bndb_pre.get("净利润");
							d = (yysr.sub(yysr_pre)).div(yysr_pre).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setBnljje((d.doubleValue() == 0 ? "0.00" : d.doubleValue() )+ "%");
						} else {
							tempvo.setByje(Common.format(bqdb.get(vo.getXm())));
							tempvo.setBnljje(Common.format(bndb.get(vo.getXm())));
						}
						if (i == end) {
							if ("盈利状况".equals(vo.getXmfl())) {
								ylqk.add(tempvo);
							} else if ("三项费用".equals(vo.getXmfl())) {
								sxqk.add(tempvo);
							} else if ("应收应付".equals(vo.getXmfl())) {
								ysyfqk.add(tempvo);
							} else if ("资金状况".equals(vo.getXmfl())) {
								zjzg.add(tempvo);
							} else if ("税金状况".equals(vo.getXmfl())) {
								sjzk.add(tempvo);
							} else if("盈利能力".equals(vo.getXmfl())){//如果是季度的则不考虑
								ylnl.add(tempvo);
							} else if("发展能力".equals(vo.getXmfl()) && !"净资产收益率".equals(vo.getXm()) ){
								yynl.add(tempvo);
							}
						}
					}
				}
			}

			if (begin != end){//开始不等于结束，默认就是小规模纳税人
				//计算比例
				CwglInfoAppVO sryjvo = calScale(sryj);
				reslist.add(sryjvo);//收入预警
			}
			if (SourceSysEnum.SOURCE_SYS_WX_APPLET.getValue().equals(qryDailyBean.getSourcesys())) {
				getZcZk(period_end, pk_corp, reslist);
			}
			reslist.add(ylqk.toArray(new CwglInfoAppVO[0]));
			reslist.add(sxqk.toArray(new CwglInfoAppVO[0]));
			reslist.add(ysyfqk.toArray(new CwglInfoAppVO[0]));// 应收应付
			reslist.add(zjzg.toArray(new CwglInfoAppVO[0]));// 资金
			reslist.add(sjzk.toArray(new CwglInfoAppVO[0]));// 税金
			reslist.add(ylnl.toArray(new  CwglInfoAppVO[0]));//盈利能力
			reslist.add(yynl.toArray(new  CwglInfoAppVO[0]));//运营能力
		}

	}

	private List getSryj(String period_end, Object[] fsobj, String pk_corp) {
		DZFDouble sum = DZFDouble.ZERO_DBL;//收入合计
		DZFDouble yjsx = new DZFDouble("5000000");//预警上线
		
		List<CwglInfoAppVO> list = new ArrayList<CwglInfoAppVO>();
		String newrule = iZxkjRemoteAppService.queryAccountRule(pk_corp);
		Integer corpschema = iZxkjRemoteAppService.getAccountSchema(pk_corp);
		String srkmbm = "";
		if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {// 2007会计准则
			srkmbm = iZxkjRemoteAppService.getNewRuleCode("600101", DZFConstant.ACCOUNTCODERULE, newrule);
		}else if (corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {// 2013会计准则
			srkmbm = iZxkjRemoteAppService.getNewRuleCode("500101", DZFConstant.ACCOUNTCODERULE, newrule);
		}else if(corpschema == DzfUtil.CAUSESCHEMA.intValue()  //事业
				|| corpschema == DzfUtil.POPULARSCHEMA.intValue()){//民间
			srkmbm = "";
			yjsx = DZFDouble.ZERO_DBL;
		}
	
		// 获取连续十二个月的收入合计
		Map<String, List<FseJyeVO>> monthmap = (Map<String, List<FseJyeVO>>) fsobj[0];
	
		DZFDate endate = DateUtils.getPeriodEndDate(period_end);
		String period_beg = endate.getYear() - 1 + "-" + period_end.substring(5, 7);
		if (monthmap != null && monthmap.size() > 0) {
			for (Entry<String, List<FseJyeVO>> entry : monthmap.entrySet()) {
				if (entry.getKey().compareTo(period_beg) >= 0 && entry.getKey().compareTo(period_end) <= 0) {
					if (entry.getValue() != null && entry.getValue().size() > 0) {
						for (FseJyeVO vo : entry.getValue()) {
							if(!StringUtil.isEmpty(vo.getKmbm()) && srkmbm.equals(vo.getKmbm())){
								sum = SafeCompute.add(sum, vo.getFsdf());
							}
						}
					}
				}
			}
		}
		CwglInfoAppVO vo1 = new CwglInfoAppVO();
		vo1.setXm("连续12月收入");
		vo1.setBnljje(sum.toString());
		CwglInfoAppVO vo2 = new CwglInfoAppVO();
		vo2.setXm("预警上限");
		vo2.setBnljje(yjsx.toString());
		CwglInfoAppVO vo3 = new CwglInfoAppVO();
		vo3.setXm("本月还可新增收入");
		DZFDouble ce = SafeCompute.sub(yjsx, sum);
		vo3.setBnljje(ce.doubleValue() < 0 ? "0" : ce.toString());
		list.add(vo1);
		list.add(vo2);
		list.add(vo3);
		return list;
	}

	private CwglInfoAppVO calScale(List sryj) {
		CwglInfoAppVO vo = new CwglInfoAppVO();
		if (sryj != null && sryj.size() > 0) {
			DZFDouble maxvalue = DZFDouble.ZERO_DBL;
			DZFDouble tvlaue = DZFDouble.ZERO_DBL;
			for (int i = 0; i < sryj.size(); i++) {
				tvlaue = new DZFDouble(((CwglInfoAppVO) sryj.get(i)).getBnljje());
				if (tvlaue.sub(maxvalue).doubleValue() > 0) {
					maxvalue = tvlaue;
				}
			}
			for (int i = 1; i <= sryj.size(); i++) {
				tvlaue = new DZFDouble(((CwglInfoAppVO) sryj.get(i-1)).getBnljje());
				vo.setAttributeValue("yjxm"+i, ((CwglInfoAppVO)sryj.get(i-1)).getXm());
				vo.setAttributeValue("yjvalue"+i, Common.format(((CwglInfoAppVO)sryj.get(i-1)).getBnljje()));
				vo.setAttributeValue("scale"+i,Common.format(maxvalue.doubleValue()==0?"0.00":tvlaue.div(maxvalue).setScale(2, DZFDouble.ROUND_HALF_UP)));
			}
		}
		
		return vo;
	}

}
