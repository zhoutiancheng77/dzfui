package com.dzf.zxkj.app.service.report.impl;

import com.dzf.zxkj.app.model.nssb.NssbBean;
import com.dzf.zxkj.app.model.resp.bean.ReportBeanVO;
import com.dzf.zxkj.app.pub.constant.IVersionConstant;
import com.dzf.zxkj.app.service.report.INssbDMOService;
import com.dzf.zxkj.app.utils.ReportUtil;
import com.dzf.zxkj.app.utils.VoUtils;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.Common;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.workbench.BsWorkbenchVO;
import com.dzf.zxkj.report.service.IRemoteReportService;
import com.dzf.zxkj.report.service.IZxkjRemoteAppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 纳税申报查询
 * 
 * @author zhangj
 *
 */
@Slf4j
@Service("nssbDMOService")
public class NssbDMOServiceImpl implements INssbDMOService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
	private IRemoteReportService iRemoteReportService;
	@Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
	private IZxkjRemoteAppService iZxkjRemoteAppService;

	public List qryNssbDataYear(ReportBeanVO reportBean) throws DZFWarpException {

		List reslist = new ArrayList();

		DZFDate enddate = new DZFDate();// 截止时间

		String year = reportBean.getYear();
		
		if(!year.equals(enddate.getYear()+"")){//非当年到12月份
			enddate = new DZFDate(year +"-12-01");
		}

		// 需要区别公司性质
		CorpVO cpvo = iZxkjRemoteAppService.queryByPk(reportBean.getPk_corp());

		DZFDate begindate = DateUtils.getPeriodStartDate(year + "-01");

		DZFDate corpdate = cpvo.getBegindate();

		if(Integer.parseInt(year)<corpdate.getYear()){
			throw new BusinessException("查询日期在建账日期前!");
		}

		if (begindate.after(enddate)) {
			return reslist;// 暂无数据
		}

		// 计算数据
		if(reportBean.getVersionno()!=null && reportBean.getVersionno() <= IVersionConstant.VERSIONNO324){
			qryNssb(reportBean, begindate, enddate, reslist, cpvo);
		}else if(reportBean.getVersionno()>= IVersionConstant.VERSIONNO325){
			qryNssb1( begindate, enddate, reslist, cpvo);
		}
		return reslist;
	}
	
	public List qryNssbDataPeriod(String pk_corp, String period) throws DZFWarpException {
		
		List reslist = new ArrayList();
		
		DZFDate begindate = DateUtils.getPeriodStartDate(period);//开始日期

		DZFDate enddate = DateUtils.getPeriodEndDate(period);// 截止时间

		// 需要区别公司性质
		CorpVO cpvo = iZxkjRemoteAppService.queryByPk(pk_corp);

		DZFDate corpdate = cpvo.getBegindate();

		if(enddate.before(corpdate)){
			throw new BusinessException("查询日期在建账日期前!");
		}

		// 计算数据
		qryNssb1(begindate, enddate, reslist, cpvo);
		
		return reslist;
	}
	
	private void qryNssb1(DZFDate begindate, DZFDate enddate, List listres, CorpVO cpvo) {
		// 获取纳税工作台的数据
		Map<String, BsWorkbenchVO> nsmap = new HashMap<String, BsWorkbenchVO>();
		qryNsGztData(cpvo, nsmap);
		putResList(listres, begindate, enddate, cpvo, nsmap);
	}

	private void putResList(List listres, DZFDate begindate, DZFDate enddate, CorpVO cpvo,
			Map<String, BsWorkbenchVO> nsmap) {
		
		Map<String, NssbBean> mapres_temp = new LinkedHashMap<String, NssbBean>();
		// 查询一年的数据
		String newrule = iZxkjRemoteAppService.queryAccountRule(cpvo.getPk_corp());
		String year = enddate.getYear() + "";
		List<String> periods = ReportUtil.getPeriods(begindate, enddate);

		// 查询全部数据
		Object[] objres = iRemoteReportService.getYearFsJyeVOs(year, cpvo.getPk_corp(), null,"");

		Map<String, List<FseJyeVO>> monthmap = (Map<String, List<FseJyeVO>>) objres[0];

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
		BsWorkbenchVO nsvo = null;
		Map<String, FseJyeVO> fsmap = null;
		String title = "";
		for (int i = periods.size() - 1; i >= 0; i--){
			String str = periods.get(i);
			NssbBean nssbbean = new NssbBean();
			List<NssbBean.NssbContent> tlist = new ArrayList<NssbBean.NssbContent>();
			title = sdf.format(DateUtils.getPeriodStartDate(str).getMillis());
			DZFDouble hj = DZFDouble.ZERO_DBL;// 合计
			nsvo = nsmap.get(str);//纳税工作台
			fsmap = getFsMap(monthmap, str);//本期发生数据
			// 如果状态是申报成功则赋值
			hj = calMny(nsvo, "addStatus", "addpaidTax", "增值税","zzs", tlist, hj,str,cpvo,mapres_temp);
			hj = calMny(nsvo, "exciseStatus", "excisepaidTax", "消费税","xfs", tlist, hj,str,cpvo,mapres_temp);// 消费税
			hj = calMny(nsvo, "culturalStatus", "culturalpaidTax", "文化事业建设税", "whsyjss",tlist, hj,str,cpvo,mapres_temp);// 文化事业建设税
			hj = calMny(nsvo, "cityStatus", "citypaidTax", "城建税", "cjs",tlist, hj,str,cpvo,mapres_temp);// 城建税
			hj = calMny(nsvo, "educaStatus", "educapaidTax", "教育费附加","jyffj", tlist, hj,str,cpvo,mapres_temp);// 教育费附加
			hj = calMny(nsvo, "localEducaStatus", "localEducapaidTax", "地方教育费附加","dfjyf", tlist, hj,str,cpvo,mapres_temp);// 地方教育费附加
			hj = calMny(nsvo, "personStatus", "personpaidTax", "个人所得税","grsds", tlist, hj,str,cpvo,mapres_temp);// 个人所得税
			hj = calMny(nsvo, "incomeStatus", "incomepaidTax", "企业所得税","qysds", tlist, hj,str,cpvo,mapres_temp);// 企业所得税
			// 合计
			addHj(tlist, hj);
			//本期留底
			addBqld(tlist, newrule, fsmap,cpvo);

			nssbbean.setRq(title);
			nssbbean.setContent(tlist.toArray(new NssbBean.NssbContent[0]));
			
			mapres_temp.put(title, nssbbean);
		}
		
		//每个月数据进行合并，同时
		if (mapres_temp != null && mapres_temp.size() > 0) {
			for(Map.Entry<String, NssbBean> entry:mapres_temp.entrySet()){
				listres.add(entry.getValue());
			}
		}
	}

	private Map<String, FseJyeVO> getFsMap(Map<String, List<FseJyeVO>> monthmap, String str) {
		Map<String, FseJyeVO> fsmap = null;
		List<FseJyeVO> tfsvos = null;
		fsmap = new HashMap<>();
		tfsvos = monthmap.get(str);
		for (FseJyeVO vo : tfsvos) {
			fsmap.put(vo.getKmbm(), vo);
		}
		return fsmap;
	}

	private void addHj(List<NssbBean.NssbContent> tlist, DZFDouble hj) {
		NssbBean.NssbContent bean = new NssbBean.NssbContent();
		bean.setName("合计");
		bean.setValue(Common.format(hj));
		bean.setCode("hj");
		tlist.add(bean);
	}

	private void addBqld(List<NssbBean.NssbContent> tlist, String newrule,
						 Map<String, FseJyeVO> fsmap, CorpVO cpvo) {
		if("小规模纳税人".equals(cpvo.getChargedeptname())){
			return;
		}
		DZFDouble bqld= DZFDouble.ZERO_DBL;
		bqld = getData(fsmap, "222109", newrule, DZFBoolean.TRUE);// 本期留底
		if(bqld.doubleValue()<0){//如果是负数则取的是绝对值
			bqld = bqld.multiply(-1);
		}else{
			bqld = DZFDouble.ZERO_DBL;
		}
		NssbBean.NssbContent beanld = new NssbBean.NssbContent();
		beanld.setName("本期留抵");
		beanld.setCode("bqld");
		beanld.setValue(Common.format(bqld));
		tlist.add(beanld);
	}

	private DZFDouble calMny(BsWorkbenchVO nsvo, String zt_column,
			String mny_column, String name,String code, List<NssbBean.NssbContent> tlist,
			DZFDouble hj,String period,CorpVO cpvo,Map<String, NssbBean> mapres_temp) {
		DZFDouble res = DZFDouble.ZERO_DBL;
		if (nsvo != null) {//纳税信息
			Integer zt = (Integer) nsvo.getAttributeValue(zt_column);// 状态
			DZFDouble mny = (DZFDouble) nsvo.getAttributeValue(mny_column);// 金额

//			if (zt != null && (zt.intValue() == 4 || zt.intValue() == 99)) {//已填写
				res = VoUtils.getDZFDouble(mny);// 增值税
//			}
		}
		
		String chargedeptname = cpvo.getChargedeptname();
		Integer month = Integer.parseInt(period.substring(5, 7));
		String append = "";
		if(!"grsds".equals(code) && !"hj".equals(code)){
			if ("小规模纳税人".equals(chargedeptname) && month.intValue()%3!=0) {//非季度最后一个月份则不写入
				//找到对应的季度数据
				nssbTotalJdValue(code, period, mapres_temp, res);
				return hj;//本身的合计
			} else if("小规模纳税人".equals(chargedeptname)){
			append = "("+(month-2)+"-"+month+"月"+")";
			}
		}
		
		NssbBean.NssbContent bean = new NssbBean.NssbContent();
		bean.setName(name+append);//名称
		bean.setCode(code);//编码
		bean.setValue(Common.format(res));
		tlist.add(bean);
		hj = SafeCompute.add(hj, res);//合计数
		
		return hj;
	}

	private void nssbTotalJdValue(String code, String period, Map<String, NssbBean> mapres_temp, DZFDouble res) {
		String year_jd = period.substring(0, 4);
		String month_jd = String.format("%02d", (Integer.parseInt(period.substring(5, 7))/3+1)*3) +"";
		String period_jd = year_jd + "年"+month_jd + "月";
		NssbBean jd_nssb = mapres_temp.get(period_jd);
		if(jd_nssb!=null ){
			if(jd_nssb.getContent()!=null && jd_nssb.getContent().length>0){
				for(NssbBean.NssbContent content:jd_nssb.getContent()){
					if(content.getCode().equals(code)){
						content.setValue(Common.format(new DZFDouble(content.getValue()).add(res)));
					}else if(content.getCode().equals("hj")){
						content.setValue(Common.format(new DZFDouble(content.getValue()).add(res)));
					}
				}
			}
		}
	}

	private void qryNsGztData(CorpVO cpvo, Map<String, BsWorkbenchVO> nsmap) {

		SQLParameter sp = new SQLParameter();
		sp.addParam(cpvo.getPk_corp());
		List<BsWorkbenchVO> nsvos = (ArrayList<BsWorkbenchVO>) singleObjectBO.executeQuery(
				"select * from nsworkbench where nvl(dr,0)=0 and pk_corp =?  order by period ", sp,new BeanListProcessor(BsWorkbenchVO.class));
		if (nsvos != null && nsvos.size() > 0) {
			//计算合计值
			BsWorkbenchVO  vo = null;
			BsWorkbenchVO befvo = null;
			for (int i = 0; i < nsvos.size(); i++) {
				vo = nsvos.get(i);
				Integer month = Integer.parseInt(vo.getPeriod().substring(5, 7));
				nsmap.put(vo.getPeriod(), vo);
			}
		}
	}
	

	/**
	 * 应交的纳税申报，已经不用(废弃)
	 * @param reportBean
	 * @param begindate
	 * @param enddate
	 * @param listres
	 * @param cpvo
	 */
	private void qryNssb(ReportBeanVO reportBean, DZFDate begindate,  DZFDate enddate, List listres, CorpVO cpvo) {
		List listrestemp = new ArrayList();
		// 查询一年的数据
		String newrule = iZxkjRemoteAppService.queryAccountRule(cpvo.getPk_corp());
		String year = enddate.getYear()+"";
		List<String> periods = ReportUtil.getPeriods(begindate, enddate);

		// 查询全部数据
		Object[] objres = iRemoteReportService.getYearFsJyeVOs(year, reportBean.getPk_corp(),null,"");
		
		Map<String,List<FseJyeVO>> monthmap =(Map<String, List<FseJyeVO>>) objres[0];
		
		int count = 0;//外层循环次数
		int count1 = 0;//内层循环次数
		
		String chargedeptname = cpvo.getChargedeptname();
		
		if ("一般纳税人".equals(chargedeptname)) {//月报
			count = periods.size() ;//外层循环的次数
			count1 = 1;
		} else {
			count =periods.size() % 3 ==0 ? periods.size() / 3:((periods.size() / 3)+1);
			count1 = 3;
		}
		
		Map<String,FseJyeVO> fsmap = null;
		List<FseJyeVO> tfsvos = null;
		for(int i=0;i<count;i++){
			Map<String,String> tmap = new HashMap<String,String>();
			String title = year+"年";
			StringBuffer month= new StringBuffer();
			DZFDouble zzs = DZFDouble.ZERO_DBL;//增值税
			DZFDouble cjs = DZFDouble.ZERO_DBL;//城建税
			DZFDouble jyffj = DZFDouble.ZERO_DBL;//教育费附加
			DZFDouble dfjyf = DZFDouble.ZERO_DBL;//地方教育费
			DZFDouble grsds = DZFDouble.ZERO_DBL;//个人所得税
			DZFDouble sds = DZFDouble.ZERO_DBL;//所得税
			DZFDouble hj = DZFDouble.ZERO_DBL;//合计
			DZFDouble bqld = DZFDouble.ZERO_DBL;//本期留底
			for (int j = 0; j < count1; j++) {
				if((count1 * (i) + j)>=periods.size()){
					continue;
				}
				String period = periods.get(count1 * (i) + j);
				if (j == 0) {
					month.append(Integer.parseInt(period.substring(5, 7)) + "-");
				} else if (j == count1 - 1) {
					month.append(Integer.parseInt(period.substring(5, 7)) + "-");
				}

				tfsvos = monthmap.get(period);

				if (tfsvos == null || tfsvos.size() == 0) {
					continue;
				}
				fsmap = new HashMap<>();

				for (FseJyeVO vo : tfsvos) {
					fsmap.put(vo.getKmbm(), vo);
				}
				zzs = SafeCompute.add(zzs, getData(fsmap,"222109",newrule,DZFBoolean.FALSE));//增值税
				
				cjs = SafeCompute.add(cjs, getData(fsmap,"222102",newrule,DZFBoolean.FALSE));//城建税
				
				jyffj = SafeCompute.add(jyffj, getData(fsmap,"222103",newrule,DZFBoolean.FALSE));//教育费附加
				
				dfjyf = SafeCompute.add(dfjyf, getData(fsmap,"222104",newrule,DZFBoolean.FALSE));//地方教育费
				
				grsds = SafeCompute.add(grsds, getData(fsmap,"222105",newrule,DZFBoolean.FALSE));//个人所得税
				
				sds =  SafeCompute.add(sds, getData(fsmap,"222106",newrule,DZFBoolean.FALSE));//所得税
				
				hj = zzs.add(cjs).add(jyffj).add(dfjyf).add(grsds).add(sds);
				
				bqld =  getData(fsmap,"222109",newrule,DZFBoolean.TRUE);//本期留底
			}
			if("一般纳税人".equals(chargedeptname)){
				tmap.put("title", title+ month.substring(0, month.length()-1)+"月");
			}else{
				String jdmonth = "";
				switch (i) {
				case 0:
					jdmonth= "一";
					break;
				case 1:
					jdmonth= "二";
					break;
				case 2:
					jdmonth= "三";
					break;
				case 3:
					jdmonth= "四";
					break;
				default:
					break;
				}
				tmap.put("title", "第"+(jdmonth)+"季度");
			}
			tmap.put("zzs", Common.format(zzs));
			tmap.put("cjs", Common.format(cjs));
			tmap.put("jyffj", Common.format(jyffj));
			tmap.put("dfjyf", Common.format(dfjyf));
			tmap.put("grsds", Common.format(grsds));
			tmap.put("sds", Common.format(sds));
			tmap.put("hj", Common.format(hj));
			tmap.put("bqld", Common.format(bqld));
			listrestemp.add(tmap);
		}
		//倒叙排列
		for(int i=listrestemp.size()-1;i>=0;i--){
			listres.add(listrestemp.get(i));
		}
	}

	private DZFDouble getData(Map<String, FseJyeVO> fsmap, String km
			, String newrule, DZFBoolean bye) {
		String newkm = formatkm(newrule, km);
		FseJyeVO vo = fsmap.get(newkm);
		
		if(vo ==null){
			return DZFDouble.ZERO_DBL;
		}
		
		if("借".equals(vo.getFx())){
			if(bye!=null && bye.booleanValue()){
				return VoUtils.getDZFDouble(vo.getQmjf());
			}
			return VoUtils.getDZFDouble(vo.getFsjf());
		}else{
			if(bye!=null && bye.booleanValue()){
				return VoUtils.getDZFDouble(vo.getQmdf());
			}
			return VoUtils.getDZFDouble(vo.getFsdf());
		}
	}

	private String formatkm(String newrule, String km) {
		String kmnew = iZxkjRemoteAppService.getNewRuleCode(km, DZFConstant.ACCOUNTCODERULE, newrule);
		return kmnew;
	}
}
