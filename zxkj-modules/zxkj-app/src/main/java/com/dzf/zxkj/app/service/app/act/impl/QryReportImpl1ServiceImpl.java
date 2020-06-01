package com.dzf.zxkj.app.service.app.act.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dzf.zxkj.app.dao.app.IQryReportDao;
import com.dzf.zxkj.app.model.report.AppFzChVo;
import com.dzf.zxkj.app.model.report.AppFzYeVo;
import com.dzf.zxkj.app.model.report.AppFzmxVo;
import com.dzf.zxkj.app.model.report.ZqVo;
import com.dzf.zxkj.app.model.resp.bean.ReportBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ReportResBean;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.pub.constant.IVersionConstant;
import com.dzf.zxkj.app.service.IStockQryService;
import com.dzf.zxkj.app.service.app.act.IQryReport1Service;
import com.dzf.zxkj.app.service.report.INssbDMOService;
import com.dzf.zxkj.app.utils.AppCheckValidUtils;
import com.dzf.zxkj.app.utils.PageUtil;
import com.dzf.zxkj.app.utils.ReportUtil;
import com.dzf.zxkj.app.utils.VoUtils;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.Common;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.workbench.BsWorkbenchVO;
import com.dzf.zxkj.report.service.IRemoteReportService;
import com.dzf.zxkj.report.service.IZxkjRemoteAppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 查询报表接口1
 *
 * @author liangjy
 *
 */
@Slf4j
@Service("orgreport1")
public class QryReportImpl1ServiceImpl implements IQryReport1Service {

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private INssbDMOService nssbDMOService;
	@Autowired
	private IStockQryService stockQryService;
	@Autowired
	private IQryReportDao orgreport_dao;
//	@Autowired
//	private ILrbReport gl_rep_lrbserv;

	//	@Autowired
//	private YntBoPubUtil yntBoPubUtil = null;
//

//
//	@Autowired
//	private IFzhsYebReport gl_rep_fzyebserv;
//
//	@Autowired
//	private getAllFzKmmxVos gl_rep_fzkmmxjrptserv;
//

//
//	@Autowired
//	private ICwgyInfoReport gl_rep_cwgyinfoserv;
	@Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
	private IZxkjRemoteAppService iZxkjRemoteAppService;
	@Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
	private IRemoteReportService iRemoteReportService;


	@Override
	public ReportResBean qrySfYj(String pk_corp, String year) throws DZFWarpException {

		ReportResBean bean = new ReportResBean();

		if (StringUtil.isEmpty(pk_corp)) {
			throw new BusinessException("公司信息为空");
		}

		// 获取公司信息
		CorpVO cpvo = iZxkjRemoteAppService.queryByPk(pk_corp);

		Object[] bgs = iRemoteReportService.queryZzsBg(year + "-12", cpvo);

		if (bgs == null || bgs.length == 0) {
			throw new BusinessException("暂无数据");
		}

		ZzsBgVo[] bgvos = (ZzsBgVo[]) bgs[1];

		if (bgvos == null || bgvos.length == 0) {
			throw new BusinessException("暂无数据");
		}

		// 去掉%
		for (ZzsBgVo bgvo : bgvos) {
			if (!StringUtil.isEmpty(bgvo.getSf())) {
				bgvo.setSf(bgvo.getSf().replace("%", ""));
			}
			if (!StringUtil.isEmpty(bgvo.getLjsf())) {
				bgvo.setLjsf(bgvo.getLjsf().replace("%", ""));
			}
		}

		bean.setSfyjx(StringUtil.isEmpty(cpvo.getDef19()) ? "1.00" : cpvo.getDef19() + "");

		bean.setZzsbg(bgvos);// 增值税报告数据

		return bean;
	}
	@Override
	public ResponseBaseBeanVO qrynssb(ReportBeanVO reportBean) throws DZFWarpException {
		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();

		List listres = new ArrayList();

		// 需要区别公司性质
		CorpVO cpvo = iZxkjRemoteAppService.queryByPk(reportBean.getPk_corp());

		String chargedeptname = cpvo.getChargedeptname();

		if ("一般纳税人".equals(chargedeptname)) {
			bean.setChargedeptname("1");// 一般纳税人
		} else {
			bean.setChargedeptname("0");// 小规模纳税人
		}

		try {

			listres = nssbDMOService.qryNssbDataYear(reportBean);

			bean.setRescode(IConstant.DEFAULT);

			bean.setResmsg(listres.toArray());
		} catch (Exception e) {
			bean.setRescode(IConstant.FIRDES);
			if (e instanceof BusinessException) {
				bean.setResmsg(e.getMessage());
			} else {
				bean.setResmsg("查询出错!");
			}
			log.error(e.getMessage(), e);
		}

		return bean;
	}
	@Override
	public ReportResBean qryFzye(String pk_corp, String period, String fzlb, int page, int rows,Integer versionno)
			throws DZFWarpException {
		ReportResBean bean = new ReportResBean();
		if (StringUtil.isEmpty(fzlb) || StringUtil.isEmpty(pk_corp) || StringUtil.isEmpty(period)) {
			throw new BusinessException("参数信息为空");
		}
		CorpVO corpvo = iZxkjRemoteAppService.queryByPk(pk_corp);
		//创建vo
		KmReoprtQueryParamVO vo = createFzparamVo(pk_corp, period, fzlb);
		if ("1".equals(fzlb)
				|| ("2".equals(fzlb) && IVersionConstant.VERSIONNO328.intValue() >= versionno.intValue())) {//查询辅助余额表
			putKhAndGys(fzlb, bean, vo);
		} else if ("3".equals(fzlb)) {// 职员
			putZy(pk_corp, period, bean, corpvo, vo);
		}else if("4".equals(fzlb)
				|| ("2".equals(fzlb) && versionno.intValue() >= IVersionConstant.VERSIONNO328.intValue())
		){//项目
			putXm(fzlb,bean,vo);
		} else if ("6".equals(fzlb)) {// 存货
			if (versionno < IVersionConstant.VERSIONNO330) {
				putCh(page, rows, bean, vo);
			} else if (versionno >= IVersionConstant.VERSIONNO330) {
				if (period.compareTo(DateUtils.getPeriod(corpvo.getBegindate())) < 0) {
					throw new BusinessException("查询日期不能建账日期之前");
				}
				AppFzChVo appvo = stockQryService.getStockResvo(DateUtils.getPeriodStartDate(period).toString(), DateUtils.getPeriodEndDate(period).toString(), pk_corp);
				bean.setTotalcount(appvo.getSl() == null ? "0" : appvo.getSl().toString());// 合计数量
				bean.setTotalmny(appvo.getJe() == null ? "0" : appvo.getJe().toString());// 合计余额
				AppFzChVo.AppFzMx1[] fzkmmxvo1 = appvo.getFzmxvos1();
				if (fzkmmxvo1!=null && fzkmmxvo1.length>0) {
					Arrays.sort(fzkmmxvo1, new Comparator<AppFzChVo.AppFzMx1>() {
						@Override
						public int compare(AppFzChVo.AppFzMx1 o1, AppFzChVo.AppFzMx1 o2) {
							return o1.getCode().compareTo(o2.getCode());
						}
					});
					bean.setFzmxvos1(Arrays.asList(fzkmmxvo1));
				}else {
					bean.setFzmxvos1(new ArrayList<AppFzChVo.AppFzMx1>());
				}


			}
		}else {
			throw new BusinessException("该类型暂不支持");
		}
		return bean;
	}
	private KmReoprtQueryParamVO createFzparamVo(String pk_corp, String period, String fzlb) {
		KmReoprtQueryParamVO vo = new KmReoprtQueryParamVO();
		vo.setCjq(1);
		vo.setCjz(6);
		vo.setBegindate1(DateUtils.getPeriodStartDate(period));
		vo.setEnddate(DateUtils.getPeriodEndDate(period));
		vo.setQjq(period);
		vo.setQjz(period);
		vo.setPk_corp(pk_corp);
		vo.setIshasjz(DZFBoolean.TRUE);
		vo.setFzlb(fzlb);// 辅助类别
		vo.setXswyewfs(DZFBoolean.TRUE);
		vo.setIshowfs(DZFBoolean.TRUE);
		vo.setXskm(DZFBoolean.FALSE);
		return vo;
	}
	private void putKhAndGys(String fzlb, ReportResBean bean, KmReoprtQueryParamVO vo) {
		List<FzYebVO> list = iRemoteReportService.getFzYebVOs(vo);
		if (list == null || list.size() == 0) {
			throw new BusinessException("暂无数据");
		}
		List<AppFzYeVo> applist = new ArrayList<AppFzYeVo>();
		AppFzYeVo appfzvo = null;
		DZFDouble yetotal = DZFDouble.ZERO_DBL;
		for (FzYebVO fzyevo : list) {
			appfzvo = new AppFzYeVo();
			appfzvo.setMc(fzyevo.getFzhsxName());
			DZFDouble qmye = DZFDouble.ZERO_DBL;
			if ("2".equals(fzlb)) {
				qmye = SafeCompute.sub(fzyevo.getQmyedf(), fzyevo.getQmyejf());
			} else if ("1".equals(fzlb)) {
				qmye = SafeCompute.sub(fzyevo.getQmyejf(), fzyevo.getQmyedf());
			}
			appfzvo.setYe(Common.format(qmye));
			appfzvo.setBqfs(Common.format(fzlb.equals("1") ? SafeCompute.sub(fzyevo.getBqfsjf(), fzyevo.getBqfsdf())
					: SafeCompute.sub(fzyevo.getBqfsdf(), fzyevo.getBqfsjf())));
			applist.add(appfzvo);
			yetotal = SafeCompute.add(yetotal, qmye);// 余额合计
		}
		bean.setTotalmny(Common.format(yetotal));
		bean.setFzyelist(applist);
	}
	private void putZy(String pk_corp, String period, ReportResBean bean, CorpVO corpvo, KmReoprtQueryParamVO vo) {
		vo.setIshasjz(DZFBoolean.FALSE);
		vo.setFzlb("000001000000000000000003");// 辅助类别
		vo.setXskm(DZFBoolean.TRUE);
		vo.setBegindate1(DateUtils.getPeriodStartDate(period));
		Object[] objs=  iRemoteReportService.getFzkmmxVos(vo,DZFBoolean.FALSE );//.getAllFzKmmxVos(vo);
		List<FzKmmxVO> rsfzvos = (List<FzKmmxVO>) objs[0];
		Map<String, List<FzKmmxVO>> map = new LinkedHashMap<String, List<FzKmmxVO>>();
		if(rsfzvos!=null && rsfzvos.size()>0){
			for(FzKmmxVO mxvo:rsfzvos){
				if(map.containsKey(mxvo.getFzxm())){
					map.get(mxvo.getFzxm()).add(mxvo);
				}else{
					List<FzKmmxVO> t_list = new ArrayList<FzKmmxVO>();
					t_list.add(mxvo);
					map.put(mxvo.getFzxm(), t_list);
				}
			}
		}
		Map<String,List<FzKmmxVO>> fzkmmxmap = new LinkedHashMap<String,List<FzKmmxVO>>();
		//获取职员过滤信息
		getZyFilterList(pk_corp, corpvo, map, fzkmmxmap);
		//赋值合计值
		List<AppFzYeVo> fzyelist = new ArrayList<AppFzYeVo>();
		if(fzkmmxmap!=null && fzkmmxmap.size()>0){
			for(Entry<String, List<FzKmmxVO>> entry:fzkmmxmap.entrySet()){
				AppFzYeVo fzyevo  = new AppFzYeVo();
				DZFDouble bqfsjf = DZFDouble.ZERO_DBL;
				DZFDouble bqfsdf = DZFDouble.ZERO_DBL;
				DZFDouble bqye = DZFDouble.ZERO_DBL;//默认是借方，如果是负数，则变成贷方
				if(entry.getValue()!=null && entry.getValue().size()>0){
					//赋值明细数据
					List<AppFzmxVo> appfzmxlist = new ArrayList<AppFzmxVo>();
					for(FzKmmxVO mxvo:entry.getValue()){
						if("本月合计".equals(mxvo.getZy())){
							bqfsjf = SafeCompute.add(bqfsjf, mxvo.getJf());
							bqfsdf = SafeCompute.add(bqfsdf, mxvo.getDf());
							if("借".equals(mxvo.getFx())){
								bqye = SafeCompute.add(bqye, mxvo.getYe());
							}else{
								bqye = SafeCompute.sub(bqye, mxvo.getYe());
							}
						}else{//非本月合计的则不添加到明细中
							AppFzmxVo  fzmxvo = new AppFzmxVo();
							fzmxvo.setRq(mxvo.getRq());
							fzmxvo.setYe(Common.format(VoUtils.getDZFDouble(SafeCompute.add(mxvo.getJf(), mxvo.getDf()))));
							if(mxvo.getJf()!=null && mxvo.getJf().doubleValue()!=0){
								fzmxvo.setFx("0");//借方
							}else{
								fzmxvo.setFx("1");//贷方
							}
							appfzmxlist.add(fzmxvo);
						}
						//按照日期排序fzmxvo
						Collections.sort(appfzmxlist, new Comparator<AppFzmxVo>() {
							@Override
							public int compare(AppFzmxVo vo1, AppFzmxVo vo2) {
								if(StringUtil.isEmpty(vo1.getRq())){
									return 0;
								}
								if(StringUtil.isEmpty(vo2.getRq())){
									return 0;
								}
								return vo1.getRq().compareTo(vo2.getRq());
							}

						});
					}
					fzyevo.setMxvos(appfzmxlist.toArray(new AppFzmxVo[0]));
				}
				fzyevo.setMc(entry.getKey());
				fzyevo.setBqfsjf(Common.format(bqfsjf));//发生借方
				fzyevo.setBqfsdf(Common.format(bqfsdf));//发生贷方
				fzyevo.setYe(Common.format(bqye.abs()));//本期余额(取绝对值)
				if(bqye.doubleValue()>0){
					fzyevo.setFx("0");//借方
				}else if(bqye.doubleValue()<0) {
					fzyevo.setFx("1");//贷方
				}else {
					fzyevo.setFx("2");//平
				}
				fzyelist.add(fzyevo);
			}
		}
		bean.setFzyelist(fzyelist);
	}
	private void putXm(String fzlb, ReportResBean bean, KmReoprtQueryParamVO vo) {
		vo.setXskm(DZFBoolean.TRUE);
		List<FzYebVO> list = iRemoteReportService.getFzYebVOs(vo);
		if (list == null || list.size() == 0) {
			throw new BusinessException("暂无数据");
		}
		List<AppFzYeVo> applist = new ArrayList<AppFzYeVo>();
		AppFzYeVo appfzvo = null;
		AppFzmxVo appfzmxvo= null;
		String column_value = "";
		DZFDouble yetotal = DZFDouble.ZERO_DBL;
		if("2".equals(fzlb)){
			column_value = "fzhsx2";
		}else if("4".equals(fzlb)){
			column_value = "fzhsx4";
		}
		for (FzYebVO fzyevo : list) {
			if(StringUtil.isEmpty(fzyevo.getPk_acc())){//这是添加辅助
				appfzvo = new AppFzYeVo();
				appfzvo.setMc(fzyevo.getFzhsxName());
				appfzvo.setFzid((String)fzyevo.getAttributeValue(column_value));
				applist.add(appfzvo);
			}else{
				for (AppFzYeVo yevo : applist) {
					if (yevo.getFzid().equals((String) fzyevo.getAttributeValue(column_value))) {
						List<AppFzmxVo> mxlist = new ArrayList<AppFzmxVo>();
						if (yevo.getMxvos() != null && yevo.getMxvos().length > 0) {
							Collections.addAll(mxlist, yevo.getMxvos());
						}
						appfzmxvo = new AppFzmxVo();
						appfzmxvo.setMc(fzyevo.getFzhsxName());
						DZFDouble qmye = SafeCompute.add(fzyevo.getQmyejf(), fzyevo.getQmyedf());
						appfzmxvo.setYe(Common.format(qmye));
						appfzmxvo.setBqfsjf(Common.format(fzyevo.getBqfsjf()));
						appfzmxvo.setBqfsdf(Common.format(fzyevo.getBqfsdf()));
						mxlist.add(appfzmxvo);
						yevo.setMxvos(mxlist.toArray(new AppFzmxVo[0]));
						yetotal = SafeCompute.add(yetotal, qmye);
					}
				}
			}
		}
		bean.setTotalmny(Common.format(yetotal));
		bean.setFzyelist(applist);
	}
	private void putCh(int page, int rows, ReportResBean bean, KmReoprtQueryParamVO vo) {
		vo.setIshasjz(DZFBoolean.FALSE);
		vo.setFzlb("000001000000000000000006");// 辅助类别
		Map<String, List<FzKmmxVO>> map = iRemoteReportService.getAllFzKmmxVos(vo);
		if (map == null || map.size() == 0) {
			throw new BusinessException("暂无数据");
		}
		List<AppFzYeVo> appfzyelist = new ArrayList<AppFzYeVo>();
		List<FzKmmxVO> list = null;
		AppFzYeVo appfzyevo = null;
		AppFzmxVo appfzmxvo = null;
		DZFDouble total_ye = DZFDouble.ZERO_DBL;// 合计金额
		DZFDouble total_count = DZFDouble.ZERO_DBL;// 合计数量
		for (Entry<String, List<FzKmmxVO>> entry : map.entrySet()) {
			list = entry.getValue();
			appfzyevo = new AppFzYeVo();
			String mc = "";
			List<AppFzmxVo> fzmxlist = new ArrayList<AppFzmxVo>();
			if (list != null && list.size() > 0) {
				for (FzKmmxVO fzkmmxvo : list) {
					mc = fzkmmxvo.getFzname();
					DZFDouble jetemp = DZFDouble.ZERO_DBL;
					DZFDouble counttemp = DZFDouble.ZERO_DBL;
					if (!fzkmmxvo.getZy().equals("期初余额") && !fzkmmxvo.getZy().equals("本月合计")
							&& !fzkmmxvo.getZy().equals("本年累计")) {
						appfzmxvo = new AppFzmxVo();
						appfzmxvo.setRq(fzkmmxvo.getRq());
						if (fzkmmxvo.getJfnum() != null && fzkmmxvo.getJfnum().doubleValue() != 0) {
							counttemp = fzkmmxvo.getJfnum();
						} else {
							counttemp = fzkmmxvo.getDfnum();
						}
						appfzmxvo.setCount(replayNullToFh(counttemp));
						if (fzkmmxvo.getJf() != null && fzkmmxvo.getJf().doubleValue() != 0) {
							appfzmxvo.setFzlx("0");// 采购
							appfzmxvo.setFzlx_mc("采");
							jetemp = fzkmmxvo.getJf();
						} else if (fzkmmxvo.getDf() != null && fzkmmxvo.getDf().doubleValue() != 0) {
							appfzmxvo.setFzlx("1");// 销售
							appfzmxvo.setFzlx_mc("售");
							jetemp = fzkmmxvo.getDf();
						}
						appfzmxvo.setYe(Common.format(jetemp));// 金额
						appfzmxvo.setDj(getDj(jetemp, counttemp));// 单价 反算出来
						fzmxlist.add(appfzmxvo);
					}
				}
				// 添加剩余
				appfzmxvo = new AppFzmxVo();
				appfzmxvo.setFzlx("2");// 剩余
				appfzmxvo.setRq("剩余");
				appfzmxvo.setFzlx_mc("余");
				appfzmxvo.setCount(replayNullToFh(list.get(list.size() - 1).getYenum()));
				appfzmxvo.setDj(getDj(list.get(list.size() - 1).getYe(), list.get(list.size() - 1).getYenum()));
				appfzmxvo.setYe(Common.format(list.get(list.size() - 1).getYe()));
				fzmxlist.add(appfzmxvo);
				total_ye = SafeCompute.add(total_ye, list.get(list.size() - 1).getYe());// 余额
				total_count = SafeCompute.add(total_count, list.get(list.size() - 1).getYenum());// 数量
			} else {
				continue;
			}
			appfzyevo.setMc(mc);
			appfzyevo.setMxvos(fzmxlist.toArray(new AppFzmxVo[0]));
			appfzyelist.add(appfzyevo);// 余额信息
			bean.setTotalcount(Common.format(total_count));// 合计数量
			bean.setTotalmny(Common.format(total_ye));// 合计余额
			bean.setFzyelist(appfzyelist);
		}
		List res = PageUtil.paginationVOs(bean.getFzyelist(), page, rows);
		bean.setFzyelist(res);
	}

	/**
	 * 职员过滤科目
	 * @param pk_corp
	 * @param corpvo
	 * @param map
	 * @param fzkmmxmap
	 */
	private void getZyFilterList(String pk_corp, CorpVO corpvo, Map<String, List<FzKmmxVO>> map,
								 Map<String,List<FzKmmxVO>> fzkmmxmap) {
		// 过滤其他应收，其他应付下的数据
		YntCpaccountVO[] cpavos = iZxkjRemoteAppService.queryYCVoByPk(pk_corp);
		if(cpavos!=null && cpavos.length>0){
			//获取id信息
			String[] kmids = getKmIdFromKmfa(corpvo, cpavos);
			if(kmids.length == 0){
				throw new BusinessException("科目不存在");
			}
			String key = "";
			List<FzKmmxVO> value = null;
			for(Entry<String, List<FzKmmxVO>> entry:map.entrySet()){
				key = entry.getKey();
				value = entry.getValue();
				boolean bcontain = false;
				for(String str:kmids){
					if(key.indexOf(str)>0){
						bcontain = true;
					}
				}
				if (bcontain){
					if(value!=null && value.size()>0){
						for(FzKmmxVO fzmxvo:value){
							if("期初余额".equals(fzmxvo.getZy()) || "本年累计".equals(fzmxvo.getZy())){
								continue;
							}
							if(fzkmmxmap.containsKey(fzmxvo.getFzname())){//是否包含
								fzkmmxmap.get(fzmxvo.getFzname()).add(fzmxvo);
							}else{
								List<FzKmmxVO> value_temp = new ArrayList<FzKmmxVO>();
								value_temp.add(fzmxvo);
								fzkmmxmap.put(fzmxvo.getFzname(), value_temp);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 把空和0.00转换成-
	 *
	 * @return
	 */
	private String replayNullToFh(DZFDouble value) {
		if (value != null && value.doubleValue() != 0) {
			return Common.format(value);
		}
		return "-";
	}

	private String getDj(DZFDouble ye, DZFDouble yenum) {
		if (yenum != null && yenum.doubleValue() != 0) {
			return replayNullToFh(ye.div(yenum));
		}
		return "-";
	}

	private String[] getKmIdFromKmfa(CorpVO corpvo, YntCpaccountVO[] cpavos) {
		List<String> keylist = new ArrayList<String>();
		if ("00000100AA10000000000BMF".equals(corpvo.getCorptype())
				|| "00000100AA10000000000BMD".equals(corpvo.getCorptype())) {// 07
			// 13企业制度
			putCodeId(cpavos, "1221", keylist);// 其他应收
			putCodeId(cpavos, "2241", keylist);//其他应付
		} else if ("00000100000000Ig4yfE0003".equals(corpvo.getCorptype())) {// 事业单位
			putCodeId(cpavos, "1215", keylist);//
			putCodeId(cpavos, "2305", keylist);//
		} else if ("00000100AA10000000000BMQ".equals(corpvo.getCorptype())) {// 民间
			putCodeId(cpavos, "1122", keylist);
			putCodeId(cpavos, "2209", keylist);
		} else if ("00000100000000Ig4yfE0005".equals(corpvo.getCorptype())) {// 企业会计制度
			putCodeId(cpavos, "1133", keylist);//
			putCodeId(cpavos, "2181", keylist);
		}
		return keylist.toArray(new String[0]);
	}

	private void putCodeId(YntCpaccountVO[] cpavos,String code,List<String> keys) {
		for(YntCpaccountVO cpavo:cpavos){
			if(cpavo.getAccountcode().startsWith(code) && cpavo.getIsleaf() !=null && cpavo.getIsleaf().booleanValue()){
				keys.add(cpavo.getPk_corp_account());
			}
		}
//		return codemap.get(code)!=null ? codemap.get(code).getPk_corp_account() :"";
	}

		@Override
	public ReportResBean qryZqrl(String pk_corp, String period) throws DZFWarpException {
		ReportResBean bean = new ReportResBean();
		String year = period.substring(0, 4);
		int month = Integer.parseInt(period.substring(5, 7));
		Map<String, String> zqmap = new HashMap<String, String>();
		List<ZqVo> zqlist = getDefaultZqList(year, month);
		if (StringUtil.isEmpty(pk_corp)) {
			throw new BusinessException("公司信息不存在");
		}

		if (StringUtil.isEmpty(period)) {
			throw new BusinessException("期间不存在");
		}
		// 报税日期
		SQLParameter sp = new SQLParameter();
		// 赋值报税日期
		putBsrq(period, zqlist, sp);
		// 送票日期
		sp.clearParams();
		// 送票，抄税，清卡
		putQkBsCsRq(period, pk_corp, zqlist, sp);
		// list转map
		if (zqlist.size() > 0) {
			for (ZqVo vo : zqlist) {
				if (zqmap.containsKey(vo.getZqlx())) {
					zqmap.put(vo.getZqlx(), zqmap.get(vo.getZqlx()) + "," + vo.getJzrq());
				} else {
					zqmap.put(vo.getZqlx(), vo.getJzrq());
				}
			}
		}
		Collections.sort(zqlist, new Comparator<ZqVo>() {
			@Override
			public int compare(ZqVo o1, ZqVo o2) {
				return o1.getJzrq().compareTo(o2.getJzrq());
			}
		});
		bean.setZqmap(zqmap);
		bean.setZqlist(zqlist);
		return bean;
	}
	private List<ZqVo> getDefaultZqList(String year, int month) {

		List<ZqVo> list = new ArrayList<ZqVo>();

		ZqVo spvo = new ZqVo();
		spvo.setRq(month + "月" + "5日");
		spvo.setZqlx("1");// 送票
		spvo.setContent("送票截止日");
		spvo.setJzrq(getFormatDate(year, month, 5));
		spvo.setTitle("本月" + spvo.getContent() + "到" + spvo.getRq());
		list.add(spvo);

		ZqVo qkvo = new ZqVo();
		qkvo.setRq(month + "月" + "15日");
		qkvo.setZqlx("2");// 清卡
		qkvo.setContent("清卡截止日");
		qkvo.setJzrq(getFormatDate(year, month, 15));
		qkvo.setTitle("本月" + qkvo.getContent() + "到" + qkvo.getRq());
		list.add(qkvo);

		ZqVo csvo = new ZqVo();
		csvo.setRq(month + "月" + "5日");
		csvo.setZqlx("3");//
		csvo.setContent("抄税截止日");
		csvo.setJzrq(getFormatDate(year, month, 5));
		csvo.setTitle("本月" + csvo.getContent() + "到" + csvo.getRq());
		list.add(csvo);

		return list;
	}
	private String getFormatDate(String year, int month, int day) {
		return year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
	}
	private void putBsrq(String period, List<ZqVo> zqlist, SQLParameter sp) {
		String zqsql1 = "select  imonth|| '月' || ibeginday || '日至'|| iendday||'日',  vtaxtype,imonth,iendday from ynt_impose_period where nvl(dr,0)=0 and iyear = ? and imonth = ? ";
		sp.addParam(period.substring(0, 4));
		sp.addParam(Integer.parseInt(period.substring(5, 7)));
		List<Object[]> zqobjs1 = (List<Object[]>) singleObjectBO.executeQuery(zqsql1, sp, new ArrayListProcessor());
		// 赋值报税日期
		if (zqobjs1 != null && zqobjs1.size() > 0) {
			for (Object[] objs : zqobjs1) {
				ZqVo zqvo = new ZqVo();
				if (objs[1] == null || objs[2] == null || objs[3] == null) {// 空不处理
					continue;
				}
				zqvo.setRq((String) objs[0]);
				String content = (String) objs[1];
				zqvo.setContent("报税日期");
				if (!StringUtil.isEmpty(content)) {
					String[] contents = content.split(",");
					zqvo.setSzs(contents);// 税种信息
				}
				zqvo.setZqlx("0");// 报税
				int month = ((BigDecimal) objs[2]).intValue();
				int day = ((BigDecimal) objs[3]).intValue();
				zqvo.setJzrq(getFormatDate(period.substring(0, 4), month, day));// 截止日期
				zqlist.add(zqvo);
			}
		}
	}
	// 送票，抄税，清卡
	private void putQkBsCsRq(String period, String pk_corp, List<ZqVo> zqlist, SQLParameter sp) {
		if (AppCheckValidUtils.isEmptyCorp(pk_corp)) {
			return;
		}
		String zqsql2 = "select iendday,iremindtype from ynt_remindset  where nvl(dr,0)=0 and iremindtype in(?,?,?) and  pk_corpk = ? ";
		// 提醒类型 38：送票；39：抄税；40：清卡；
		// 抄税日期
		// 清卡日期
		sp.addParam(38);
		sp.addParam(39);
		sp.addParam(40);
		sp.addParam(pk_corp);
		List<Object[]> zqobjs2 = (List<Object[]>) singleObjectBO.executeQuery(zqsql2, sp, new ArrayListProcessor());
		if (zqobjs2 != null && zqobjs2.size() > 0) {
			BigDecimal iendday = null;
			BigDecimal iremindtype = null;
			for (Object[] obj : zqobjs2) {
				iendday = (BigDecimal) obj[0];
				iremindtype = (BigDecimal) obj[1];
				if (iendday == null || iremindtype == null) {
					continue;
				}
				for (ZqVo zqvo : zqlist) {
					if (iremindtype.intValue() == 38 && "1".equals(zqvo.getZqlx())) {// 送票
						zqvo.setRq(zqvo.getRq().substring(0, zqvo.getRq().indexOf("月") + 1) + iendday + "日");
						zqvo.setJzrq(period + "-" + String.format("%02d", iendday.intValue()));
						zqvo.setTitle("本月" + zqvo.getContent() + "到" + zqvo.getRq());
					} else if (iremindtype.intValue() == 39 && "3".equals(zqvo.getZqlx())) {// 抄税
						zqvo.setRq(zqvo.getRq().substring(0, zqvo.getRq().indexOf("月") + 1) + iendday + "日");
						zqvo.setJzrq(period + "-" + String.format("%02d", iendday.intValue()));
						zqvo.setTitle("本月" + zqvo.getContent() + "到" + zqvo.getRq());
					} else if (iremindtype.intValue() == 40 && "2".equals(zqvo.getZqlx())) {// 清卡
						zqvo.setRq(zqvo.getRq().substring(0, zqvo.getRq().indexOf("月") + 1) + iendday + "日");
						zqvo.setJzrq(period + "-" + String.format("%02d", iendday.intValue()));
						zqvo.setTitle("本月" + zqvo.getContent() + "到" + zqvo.getRq());// 全称
					}
				}
			}
		}
	}

	@Override
	public String queryNsgzt(String pk_corp, String period) throws DZFWarpException {
		if(AppCheckValidUtils.isEmptyCorp(pk_corp)){
			return "";
		}
		CorpVO cpvo =  iZxkjRemoteAppService.queryByPk(pk_corp);
		if(cpvo.getBegindate() == null){//尚未建账
			return "";
		}

		List<BsWorkbenchVO> reslist = orgreport_dao.queryNsgzt(pk_corp, period);
		if(reslist == null  || reslist.size() == 0){
			return "待送票";
		}
		//从上到下依次处理
		String[][] zts = new String[][]{
			{"ipzjjzt","已完成"},
			{"taxstateclean","待凭证交接"},
			{"taxstatecopy","待清卡"},
			{"isptx","待抄税"}
			};
		for(String[] str:zts){
			if(reslist.get(0).getAttributeValue(str[0]) != null && (Integer) reslist.get(0).getAttributeValue(str[0]) == 1){
				return str[1];
			}
		}
		return  "待送票";
	}


	@Override
	public int queryLibnum(String pk_corp, DZFDate begindate, DZFDate enddate) throws DZFWarpException {
		if(AppCheckValidUtils.isEmptyCorp(pk_corp)){
			return 0;
		}

		CorpVO cpvo = iZxkjRemoteAppService.queryByPk(pk_corp);
		if(cpvo.getBegindate() == null){//尚未建账
			return 0;
		}
		return orgreport_dao.qryMonthPics(pk_corp, begindate, enddate);
	}

	@Override
	public int queryPzNum(String pk_corp, DZFDate begindate, DZFDate enddate) throws DZFWarpException {
		if(AppCheckValidUtils.isEmptyCorp(pk_corp)){
			return 0;
		}
		return orgreport_dao.qryMonthVouchers(pk_corp, begindate, enddate);
	}
	@Override
	public Map<String, DZFDouble> queryCwzb(String pk_corp, String period) throws DZFWarpException {
		Map<String, DZFDouble> resmap = new HashMap<String, DZFDouble>();
		if(AppCheckValidUtils.isEmptyCorp(pk_corp)){
			return resmap;
		}
		CorpVO cpvo =  iZxkjRemoteAppService.queryByPk(pk_corp);
		if(cpvo.getBegindate() == null){//尚未建账
			return resmap;
		}

		try {
			QueryParamVO paramVO = new QueryParamVO();
			paramVO.setRptsource("lrb");//来源利润表
			paramVO.setQjq(period);
			paramVO.setQjz(period);
			paramVO.setEnddate(DateUtils.getPeriodEndDate(period));
			paramVO.setBegindate1(DateUtils.getPeriodStartDate(period));
			paramVO.setIshasjz(DZFBoolean.FALSE);
			paramVO.setPk_corp(pk_corp);
			//查询利润表数据
			LrbVO[] lrbvos =  iRemoteReportService.getLRBVOs(paramVO);

			if(lrbvos!=null && lrbvos.length>0){
				for(LrbVO vo:lrbvos){
					if(ReportUtil.bsrxm(vo)){//收入
						if(resmap.containsKey("sr")){
							resmap.put("sr", SafeCompute.add(resmap.get("sr"), vo.getByje()));
						}else{
							resmap.put("sr", VoUtils.getDZFDouble(vo.getByje()));
						}
					}else if(ReportUtil.bjlrxm(vo)){//利润
						if(resmap.containsKey("jlr")){
							resmap.put("jlr", SafeCompute.add(resmap.get("jlr"),vo.getByje()));
						}else{
							resmap.put("jlr", VoUtils.getDZFDouble(vo.getByje()));
						}
					}else if(ReportUtil.bfyxm(vo)){//费用
						if(resmap.containsKey("fy")){
							resmap.put("fy", SafeCompute.add(resmap.get("fy"),vo.getByje()));
						}else{
							resmap.put("fy", VoUtils.getDZFDouble(vo.getByje()));
						}
					}else if(ReportUtil.bzcxm(vo)){//支出
						if(resmap.containsKey("zc")){
							resmap.put("zc",  SafeCompute.add(resmap.get("zc"),vo.getByje()));
						}else{
							resmap.put("zc", VoUtils.getDZFDouble(vo.getByje()));
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("错误",e);
		}
		return resmap;
	}


	@Override
	public Map<String, CwgyInfoVO> getCwgyMap(String pk_corp, String period) throws DZFWarpException {
		Map<String, CwgyInfoVO> resmap = new HashMap<String, CwgyInfoVO>();
		if(AppCheckValidUtils.isEmptyCorp(pk_corp)){
			return resmap;
		}
		CorpVO cpvo =  iZxkjRemoteAppService.queryByPk(pk_corp);
		if(cpvo.getBegindate() == null){//尚未建账
			return resmap;
		}

		try {
			QueryParamVO paramVO = new QueryParamVO();
			paramVO.setQjq(period);
			paramVO.setQjz(period);
			paramVO.setEnddate(DateUtils.getPeriodEndDate(period));
			paramVO.setBegindate1(DateUtils.getPeriodStartDate(period));
			paramVO.setIshasjz(DZFBoolean.FALSE);
			paramVO.setPk_corp(pk_corp);
			CwgyInfoVO[] infovos = iRemoteReportService.getCwgyInfoVOs(paramVO);
			if(infovos!=null && infovos.length>0){
				for(CwgyInfoVO vo:infovos){
					resmap.put(vo.getXmfl() +"_"+ vo.getXm(), vo);
				}
			}
		} catch (Exception e) {
			log.error("错误",e);
		}

		return resmap;
	}













//
//
//	@Override
//	public ResponseBaseBeanVO qrySalarAction(ReportBeanVO reportBean) throws DZFWarpException {
//
//		int page = reportBean.getPage();
//
//		int row = reportBean.getRows();
//
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//
//		if (AppCheckValidUtils.isEmptyCorp(reportBean.getPk_corp())) {
//			throw new BusinessException("公司不能为空!");
//		}
//		if (StringUtil.isEmpty(reportBean.getAccount())) {
//			throw new BusinessException("账号不能为空!");
//		}
//
//		StringBuffer qrysql = new StringBuffer();
//
//		// qrysql.append(" select * from (");
//		qrysql.append(" select a.*,ROWNUM rn from  ynt_salaryreport a ");
//		qrysql.append(" where nvl(a.dr,0)=0 ");
//		qrysql.append("  and a.zjbm_en in ( ");
//		qrysql.append("  select distinct b.zjbm_en from ynt_salaryreport b ");
//		qrysql.append("  where nvl(b.dr,0)=0 and b.vphone = ? and b.pk_corp = ? )  ");
//		qrysql.append(" and a.pk_corp = ?  ");
//		qrysql.append(" order by a.qj desc ");
//		// qrysql.append(" ) tt where tt.rn>=? and tt.rn<=? ");
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(reportBean.getAccount());
//		sp.addParam(reportBean.getPk_corp());
//		sp.addParam(reportBean.getPk_corp());
//		// sp.addParam((page-1)*row+1);
//		// sp.addParam(page*row);
//
//		List<SalaryReportVO> salarlist = (List<SalaryReportVO>) singleObjectBO.executeQuery(qrysql.toString(), sp,
//				new BeanListProcessor(SalaryReportVO.class));
//
//		SuperVO[] salrptvos_t = (SuperVO[]) PageUtil.paginationVOs(salarlist.toArray(new SalaryReportVO[0]), page, row);
//
//		// 数据转换
//		SalaryReportVO[] salrptvos = new SalaryReportVO[salrptvos_t.length];
//
//		for (int i = 0; i < salrptvos_t.length; i++) {
//			salrptvos[i] = (SalaryReportVO) salrptvos_t[i];
//		}
//
//		// 数据加密
//		SalaryCipherHandler.handlerSalaryVO(salrptvos, 1);
//
//		List reslist = new ArrayList();
//
//		if (salrptvos != null && salrptvos.length > 0) {
//			Map<String, String> maptmp = null;
//			String year = "";
//			String month = "";
//			for (SalaryReportVO vo : salrptvos) {
//				maptmp = new HashMap<String, String>();
//				year = vo.getQj().substring(0, 4);
//				month = vo.getQj().substring(5, 7);
//				maptmp.put("id", vo.getPrimaryKey());// 主键
//				maptmp.put("qj", year + "年" + month + "月");
//				maptmp.put("yfgz", Common.format(vo.getYfgz()));// 应发工资
//				maptmp.put("sfgz", Common.format(vo.getSfgz()));// 实发工资
//				reslist.add(maptmp);
//			}
//		}
//
//		bean.setRescode(IConstant.DEFAULT);
//		bean.setResmsg(reslist.toArray());
//
//		return bean;
//	}
//
//	@Override
//	public ResponseBaseBeanVO qrySalarDetail(ReportBeanVO reportBean) throws DZFWarpException {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//
//		String id = reportBean.getId();
//
//		if (StringUtil.isEmpty(id)) {
//			throw new BusinessException("参数不能为空!");
//		}
//
//		SalaryReportVO rptvo = (SalaryReportVO) singleObjectBO.queryByPrimaryKey(SalaryReportVO.class, id);
//
//		SalaryCipherHandler.handlerSalaryVO(new SalaryReportVO[] { rptvo }, 1);
//		if (rptvo == null) {
//			throw new BusinessException("信息不存在!");
//		}
//
//		Map<String, String> mapvalue = new HashMap<String, String>();
//
//		mapvalue = new HashMap<String, String>();
//		String year = rptvo.getQj().substring(0, 4);
//		String month = rptvo.getQj().substring(5, 7);
//		String title = year + "年" + month + "月实发工资";
//		String shuilv = (rptvo.getShuilv() == null ? "0" : Common.format(rptvo.getShuilv())) + "%";
//		mapvalue.put("title", title);
//		mapvalue.put("id", rptvo.getPrimaryKey());// 主键
//		mapvalue.put("qj", rptvo.getQj());
//		mapvalue.put("yfgz", Common.format(rptvo.getYfgz()));// 应发工资
//		mapvalue.put("sfgz", Common.format(rptvo.getSfgz()));// 实发工资
//		mapvalue.put("yanglaobx", Common.format(rptvo.getYanglaobx()));
//		mapvalue.put("yiliaobx", Common.format(rptvo.getYiliaobx()));
//		mapvalue.put("shiyebx", Common.format(rptvo.getShiyebx()));
//		mapvalue.put("zfgjj", Common.format(rptvo.getZfgjj()));
//		mapvalue.put("ynssde", Common.format(rptvo.getYnssde()));
//		mapvalue.put("shuilv", shuilv);
//		mapvalue.put("grsds", Common.format(rptvo.getGrsds()));
//
//		bean.setRescode(IConstant.DEFAULT);
//		bean.setResmsg(mapvalue);
//
//		return bean;
//	}
//

//
//	public NssbBean qryNssbOnePeriod(String pk_corp, String period) throws DZFWarpException {
//
//		if (AppCheckValidUtils.isEmptyCorp(pk_corp)) {
//			throw new BusinessException("公司不能为空");
//		}
//
//		if (StringUtil.isEmpty(period)) {
//			throw new BusinessException("期间不能为空");
//		}
//
//		List listres = new ArrayList();
//
//		NssbDMO dmo = new NssbDMO();
//
//		listres = dmo.qryNssbDataPeriod(pk_corp, period);
//
//		if (listres == null || listres.size() == 0) {
//			throw new BusinessException("暂无数据");
//		}
//
//		return (NssbBean) listres.get(0);
//	}
//
//	@Override
//	public NssbBean getYjNsData(String pk_corp, String period) throws DZFWarpException {
//
//		if (AppCheckValidUtils.isEmptyCorp(pk_corp)) {
//			throw new BusinessException("公司不能为空");
//		}
//
//		if (StringUtil.isEmpty(period)) {
//			throw new BusinessException("期间不能为空");
//		}
//
//		CorpVO cpvo = CorpCache.getInstance().get("", pk_corp);
//		String year = period.substring(0, 4);
//		String month = period.substring(5, 7);
//		String period1 = year + "-" + month;
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(pk_corp);
//		sp.addParam(period1);
//		Integer befmonth = getBefMonth(month);
//		sp.addParam(period1);// 1月份的数据
//		BsWorkbenchVO[] nsvo = (BsWorkbenchVO[]) singleObjectBO.queryByCondition(BsWorkbenchVO.class,
//				"nvl(dr,0)=0 and pk_corp = ? and period <= ? and period >= ?", sp);
//		BsWorkbenchVO resvo = null;
//		if (nsvo != null && nsvo.length > 0) {
//			resvo = nsvo[0];
//		} else {
//			resvo = new BsWorkbenchVO();
//		}
//		// 计算报表数据
//		NssbBean nssbbean = new NssbBean();
//		nssbbean.setRq(period);
//		if (resvo.getItaxconfstate() == null || resvo.getItaxconfstate().intValue() == 0) {// 已确认的
//			nssbbean.setConfirm("2");// 待确认
//		} else if (resvo.getItaxconfstate().intValue() == 1) {// 已确认
//			nssbbean.setConfirm("0");
//		} else if (resvo.getItaxconfstate().intValue() == 2) {// 有异议
//			nssbbean.setConfirm("1");
//		}
//		List<NssbContent> child = new ArrayList<NssbContent>();
//		DZFDouble total = DZFDouble.ZERO_DBL;
//		total = putNssbContent(child, resvo, "addTax", "zzs", "增值税", total, cpvo, period1);
//		total = putNssbContent(child, resvo, "exciseTax", "xfs", "消费税", total, cpvo, period1);
//		total = putNssbContent(child, resvo, "culturalTax", "whsyjss", "文化事业建设税", total, cpvo, period1);
//		total = putNssbContent(child, resvo, "cityTax", "cjs", "城建税", total, cpvo, period1);
//		total = putNssbContent(child, resvo, "educaTax", "jyffj", "教育费附加", total, cpvo, period1);
//		total = putNssbContent(child, resvo, "localEducaTax", "dfjyf", "地方教育费附加", total, cpvo, period1);
//		total = putNssbContent(child, resvo, "personTax", "grsds", "个人所得税", total, cpvo, period1);
//		total = putNssbContent(child, resvo, "incomeTax", "qysds", "企业所得税", total, cpvo, period1);
//		total = putNssbContent(child, resvo, "XXX", "hj", "合计", total, cpvo, period1);
//		nssbbean.setContent(child.toArray(new NssbContent[0]));
//		return nssbbean;
//	}
//
//	private Integer getBefMonth(String month) {
//		Integer begmonth = 0;
//		for (int i = Integer.parseInt(month); i > 0; i--) {
//			if (i % 3 == 1) {
//				begmonth = i;
//				break;
//			}
//		}
//		return begmonth;
//	}
//
//	private DZFDouble putNssbContent(List<NssbContent> child, BsWorkbenchVO resvo, String column, String code,
//			String name, DZFDouble value, CorpVO cpvo, String period) {
//		NssbContent content = new NssbContent();
//
//		String chargedeptname = cpvo.getChargedeptname();
//		Integer month = Integer.parseInt(period.substring(5, 7));
//		String append = "";
//		if (!"grsds".equals(code) && !"hj".equals(code)) {
//			if ("小规模纳税人".equals(chargedeptname) && month.intValue() % 3 != 0) {// 非季度最后一个月份则不写入
//				return value;
//			} else if ("小规模纳税人".equals(chargedeptname)) {
//				append = "(" + (month - 2) + "-" + month + "月" + ")";
//			}
//		}
//		DZFDouble mny = resvo.getAttributeValue(column) == null ? DZFDouble.ZERO_DBL
//				: (DZFDouble) resvo.getAttributeValue(column);
//		content.setName(name + append);
//		content.setCode(code);
//		if (code.equals("hj")) {
//			content.setValue(Common.format(value));
//		} else {
//			content.setValue(Common.format(mny));
//			value = SafeCompute.add(value, mny);
//		}
//		child.add(content);
//
//		return value;
//	}
//
//	/**
//	 * 查询利润情况
//	 */
//	@Override
//	public List<LrbYearBeanVo> getLrbYearVo(String pk_corp, String year) throws DZFWarpException {
//		// 查询一年的利润表信息
//		Map<String, List<LrbVO>> lrbmap = gl_rep_lrbserv.getYearLrbMap(year, pk_corp, "", null,null);
//
//		if (lrbmap == null || lrbmap.size() == 0) {
//			throw new BusinessException("暂无数据");
//		}
//
//
//		List<LrbYearBeanVo> list = new ArrayList<LrbYearBeanVo>();
//		LrbYearBeanVo tempvo = null;
//		List<LrbVO> lrblist = null;
//		for (Entry<String, List<LrbVO>> entry : lrbmap.entrySet()) {
//
//			tempvo = new LrbYearBeanVo();
//
//			tempvo.setRq(entry.getKey().substring(5) + "月");
//
//			lrblist = entry.getValue();
//
//			DZFDouble jlr = DZFDouble.ZERO_DBL;// 净利润
//			DZFDouble sr = DZFDouble.ZERO_DBL;// 收入
//			DZFDouble zc = DZFDouble.ZERO_DBL;// 支出
//
//			if (list != null && lrblist.size() > 0) {
//				for (LrbVO lrbvo : lrblist) {
//					if (ReportUtil.bsrxm(lrbvo)) {// //自营业收入+公允价值损益+投资收益+营业外
//						sr = SafeCompute.add(sr, lrbvo.getByje());
//					} else if (ReportUtil.bjlrxm(lrbvo)) {
//						jlr = SafeCompute.add(jlr, lrbvo.getByje());
//					} else if (ReportUtil.bfyxm(lrbvo)) {//// 营业支出+营业外支出+税金及附加+销售费用+管理费用+财务费用+所得税费用
//						zc = SafeCompute.add(zc, lrbvo.getByje());
//					}
//				}
//			}
//
//			tempvo.setJlr(Common.format(jlr));
//			tempvo.setSr(Common.format(sr));
//			tempvo.setZc(Common.format(zc));
//
//			list.add(tempvo);
//		}
//
//		Collections.sort(list, new Comparator<LrbYearBeanVo>() {
//			@Override
//			public int compare(LrbYearBeanVo vo1, LrbYearBeanVo vo2) {
//				return vo2.getRq().compareTo(vo1.getRq());
//			}
//
//		});
//
//		return list;
//	}
//
//
//





}
