package com.dzf.zxkj.app.service.app.act.impl;

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
import com.dzf.zxkj.base.framework.DataSourceFactory;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 324版本报表查询
 * @author zhangj
 *
 */
@Slf4j
@Service("org324reportService")
public class Qry324ReportServiceImpl extends QryReportAbstract implements IQryReportService {

	@Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
	private IRemoteReportService iRemoteReportService;
	@Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
	private IZxkjRemoteAppService iZxkjRemoteAppService;
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	public ResponseBaseBeanVO qryMonthRep(ReportBeanVO qryDailyBean) throws DZFWarpException {
		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
		
		List reslist;
		try {
			reslist = new ArrayList();
			
			String pk_corp = qryDailyBean.getPk_corp();
			
			String[] peirods = getQryperiod(qryDailyBean);
			
			if(StringUtil.isEmpty(peirods[0])
					|| StringUtil.isEmpty(peirods[1])){
				throw new BusinessException("查询区间不能为空!");
			}
			String year = peirods[0].substring(0, 4);
			
			Object[] obj = iRemoteReportService.getYearFsJyeVOs(year, pk_corp,null,"");
			
			putdjpz(peirods[0],peirods[1],qryDailyBean,reslist);//单据凭证
			
			putKped(peirods[0],peirods[1],qryDailyBean,reslist,obj,pk_corp);//开票信息赋值
			
			getCwgyxx(peirods[0],peirods[1],pk_corp,reslist,obj);//财务概要信息
			
			if(SourceSysEnum.SOURCE_SYS_WX_APPLET.getValue().equals(qryDailyBean.getSourcesys())){
				getZcZk(peirods[1],pk_corp,reslist);
			}
			
			bean.setRescode(IConstant.DEFAULT);
			
			bean.setResmsg(reslist.toArray());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			bean.setRescode(IConstant.FIRDES);
			if(e instanceof BusinessException){
				bean.setResmsg("获取数据失败:"+e.getMessage());
			}else{
				bean.setResmsg("获取数据失败!");
			}
		}
		return bean;
	}
	
	private void getZcZk(String period, String pk_corp, List reslist) {

		Object[] objs = iRemoteReportService.getZCFZBVOsConMsg(period, pk_corp, "N", new String[]{"N","N","N","N"});

		DZFDouble ldzc_qm = DZFDouble.ZERO_DBL;
		DZFDouble ldzc_nc = DZFDouble.ZERO_DBL;
		DZFDouble fldzc_qm = DZFDouble.ZERO_DBL;
		DZFDouble fldzc_nc = DZFDouble.ZERO_DBL;
		DZFDouble zchj_qm = DZFDouble.ZERO_DBL;
		DZFDouble zchj_nc = DZFDouble.ZERO_DBL;
		if(objs!=null && objs.length>0){
			ZcFzBVO[] kmmxvos = (ZcFzBVO[]) objs[0];
			if(kmmxvos!=null && kmmxvos.length>0){
				for(ZcFzBVO bvo:kmmxvos){
					if(bvo.getZc().indexOf("非流动资产合计")>=0){
						fldzc_qm = bvo.getQmye1();
						fldzc_nc = bvo.getNcye1();
					}else if(bvo.getZc().indexOf("流动资产合计")>=0){
						ldzc_qm = bvo.getQmye1();
						ldzc_nc = bvo.getNcye1();
					}else if(bvo.getZc().indexOf("资产总计")>=0){
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
		
//		zczk.put("ldzc_qm",Common.format(ldzc_qm) );
//		zczk.put("ldzc_nc",Common.format(ldzc_nc) );
//		zczk.put("fldzc_qm",Common.format(fldzc_qm) );
//		zczk.put("fldzc_nc",Common.format(fldzc_nc) );
//		zczk.put("zchj_qm",Common.format(zchj_qm) );
//		zczk.put("zchj_nc",Common.format(zchj_nc) );
		List zczk = new ArrayList();
		zczk.add(zczkvo1);
		zczk.add(zczkvo2);
		zczk.add(zczkvo3);
		reslist.add(zczk);
//		reslist.add(zczk);
	}

	private void putKped(String period_begin, String period_end, ReportBeanVO qryDailyBean, List reslist,Object[] obj,String pk_corp) {
		
		if(period_begin.equals(period_end)){//一般人没有这个选项
			return;
		}
		
		Map<String, List<FseJyeVO>> monthmap = (Map<String, List<FseJyeVO>>) obj[0];
		
		Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>) obj[1];
		
		Map<String,Object> kpmap = new HashMap<String,Object>();
		
		int begin = Integer.parseInt(period_begin.substring(5, 7));
		
		int end = Integer.parseInt(period_end.substring(5, 7));
		
		Integer corpschema = iZxkjRemoteAppService.getAccountSchema(pk_corp);
		
		String year = period_begin.substring(0, 4);
		
		DZFDouble total = DZFDouble.ZERO_DBL;
		for(int i = begin;i<=end;i++){
			String period = year+"-" + String.format("%02d", i);
			List<FseJyeVO> listfs = monthmap.get(period);
			for(FseJyeVO jyevo :listfs){
				if(corpschema == DzfUtil.SEVENSCHEMA.intValue()
						&& jyevo.getKmbm().equals("6001")){//07
					total = SafeCompute.add(total, jyevo.getFsdf());
					break;
				}else if(corpschema == DzfUtil.THIRTEENSCHEMA.intValue() 
						&& jyevo.getKmbm().equals("5001")){//13科目
					total = SafeCompute.add(total, jyevo.getFsdf());
					break;
				}
			}
		}
		
		DZFDouble sykped  = SafeCompute.sub(new DZFDouble(90000), total).doubleValue()<=0?DZFDouble.ZERO_DBL:SafeCompute.sub(new DZFDouble(90000), total);
		DZFDouble ynse = total.multiply(0.03);
		DZFDouble jmse = ynse.doubleValue()>2700?DZFDouble.ZERO_DBL:ynse;
		DZFDouble ybtse = total.doubleValue()<=90000 ? DZFDouble.ZERO_DBL:total.multiply(0.03);
		kpmap.put("sykped", Common.format(sykped));//剩余开票额度
		kpmap.put("kpsr", Common.format(total));//开票收入
		kpmap.put("ynse", Common.format(ynse));//应纳税额
		kpmap.put("jmse", Common.format(jmse));//减免税额
		kpmap.put("ybtse", Common.format(ybtse));//应补退税额
		
		reslist.add(kpmap);
	}

	private void putdjpz(String period_begin,String period_end, ReportBeanVO qryDailyBean, List reslist) {
		
		Map<String,Object> djzjmap = new HashMap<String,Object>();
		String pk_corp = qryDailyBean.getPk_corp();
		
		DZFDate startdate = DateUtils.getPeriodStartDate(period_begin);
		
		DZFDate enddate = DateUtils.getPeriodEndDate(period_end);
		
		djzjmap.put("djpz", "单据凭证");
		djzjmap.put("scdj", "" + qryMonthPics(pk_corp, startdate, enddate) + "张");
		djzjmap.put("kjpz", "" + qryMonthVouchers(pk_corp, startdate, enddate) + "张");

		putNsZt(pk_corp, startdate, enddate, "isptx", "spzt", djzjmap);// 收票状态

		putNsZt(pk_corp, startdate, enddate, "taxstatecopy", "cszt", djzjmap);// 抄税状态

		putNsZt(pk_corp, startdate, enddate, "taxstateclean", "qkzt", djzjmap);// 清卡状态

		putNsZt(pk_corp, startdate, enddate, "ipzjjzt", "pzjj", djzjmap);// 凭证交接状态
		
		reslist.add(djzjmap);
	}
	
	private void putNsZt(String pk_corp,DZFDate startdate,DZFDate enddate,String column,String putcolumn,Map<String,Object> djzjmap ) {
		List<String> periods = 	ReportUtil.getPeriods(startdate, enddate);
		String wherepard = SqlUtil.buildSqlForIn("period", periods.toArray(new String[0]));
		SQLParameter sp = new SQLParameter();
		String qrysql = "select * from nsworkbench where nvl(dr,0)=0 and pk_corp = ? and "+ wherepard+" order by period ";
		sp.addParam(pk_corp);
		List<BsWorkbenchVO> bsworklist = (List<BsWorkbenchVO>) singleObjectBO.executeQuery(qrysql, sp,
				new BeanListProcessor(BsWorkbenchVO.class));
		int first  = Integer.parseInt( periods.get(0).substring(5, 7));
		if(bsworklist == null || bsworklist.size() == 0){
			djzjmap.put(putcolumn+"num", 1); 
			djzjmap.put(putcolumn,  first+"月未完成" );
		}
		if(bsworklist!=null && bsworklist.size()>0){
			for(BsWorkbenchVO vo:bsworklist){
				int month = Integer.parseInt(vo.getPeriod().substring(5, 7));
				if(vo.getAttributeValue(column) != null
						&& (Integer) vo.getAttributeValue(column) == 1){
					djzjmap.put(putcolumn+"num", 0); 
					djzjmap.put(putcolumn,  month+"月已完成" ); 
				}else{
					djzjmap.put(putcolumn+"num", 1); 
					djzjmap.put(putcolumn,  month+"月未完成" ); 
					break;
				}
			}
		}
		
	}
	
	
	/**
	 * 汇总月上传图片数量
	 * @param pk_corp
	 * @param startdate
	 * @param enddate
	 * @return
	 * @throws DZFWarpException
	 */
	private int qryMonthPics(String pk_corp,DZFDate startdate,DZFDate enddate) throws DZFWarpException{
		int monthPics = 0;
		 String picSql = "select count(pk_image_library) dr from ynt_image_group gr"
				 +" inner join ynt_image_library lb on gr.pk_image_group=lb.pk_image_group"
				 +" where gr.pk_corp = ? AND gr.doperatedate between ? AND ?"
				 +" and (nvl(gr.dr, 0) = 0) ";
		 SingleObjectBO sbo=new SingleObjectBO(DataSourceFactory.getDataSource(null, pk_corp));
			SQLParameter sp=new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(startdate);
			sp.addParam(enddate);
		 ArrayList al = (ArrayList) sbo.executeQuery(picSql,sp, new ArrayListProcessor());
		 if(al!= null&& al.size()>0){
			 monthPics = Integer.parseInt(((Object[])al.get(0))[0].toString());
		 }
		 return monthPics;
	}
	
	/**
	 * 汇总月凭证数量
	 * @param pk_corp
	 * @param startdate
	 * @param enddate
	 * @return
	 * @throws DZFWarpException
	 */
	private int qryMonthVouchers(String pk_corp,DZFDate startdate,DZFDate enddate) throws DZFWarpException{
		
		int vouchs = 0;
		String vouSql = "select count(h.pk_tzpz_h) dr from ynt_tzpz_h h"
				 +" where h.pk_corp = ? AND h.doperatedate between ? AND ? and nvl(h.dr,0)=0";
		 SingleObjectBO sbo=new SingleObjectBO(DataSourceFactory.getDataSource(null, pk_corp));
			SQLParameter sp=new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(startdate);
			sp.addParam(enddate);
		 ArrayList al = (ArrayList) sbo.executeQuery(vouSql,sp, new ArrayListProcessor());
		 if(al!= null&& al.size()>0){
			 vouchs = Integer.parseInt(((Object[])al.get(0))[0].toString());
		 }
		 return vouchs;
	}

	private String[] getQryperiod(ReportBeanVO qryDailyBean){
		String jd = qryDailyBean.getJd();

		String year = qryDailyBean.getYear();
		
		String period_begin = "";
		
		String period_end = "";
		
		if(!StringUtil.isEmpty(jd)){
			if(StringUtil.isEmpty(year)){
				throw new BusinessException("查询年度不能为空!");
			}
			if("1".equals(jd)){
				period_begin = year + "-01";
				period_end = year + "-03";
			}else if("2".equals(jd)){
				period_begin = year + "-04";
				period_end = year + "-06";
			}else if("3".equals(jd)){
				period_begin = year + "-07";
				period_end = year + "-09";
			}else if("4".equals(jd)){
				period_begin = year + "-10";
				period_end = year + "-12";
			}
		}else if(!StringUtil.isEmpty(qryDailyBean.getPeriod())){
			period_begin = qryDailyBean.getPeriod();
			period_end = qryDailyBean.getPeriod();
		}
		return new String[]{period_begin,period_end};
	}

	private void getCwgyxx(String period_begin,String period_end, String pk_corp, List reslist,Object[] fsobj ) {
		
		String year = period_begin.substring(0, 4);
		int begin = Integer.parseInt(period_begin.substring(5, 7));
		int end = Integer.parseInt(period_end.substring(5, 7));
		Map<String, CwgyInfoVO[]> cwmap = iRemoteReportService.getCwgyInfoVOs(year, pk_corp,fsobj);
		CorpVO cpvo = iZxkjRemoteAppService.queryByPk(pk_corp);
		if(cwmap!=null && cwmap.size()>0){
			String period = "";
			CwgyInfoVO[] cwvos = null;
			Map<String,DZFDouble> bqdb =  new HashMap<String,DZFDouble>();//本期
			Map<String,DZFDouble> bndb =  new HashMap<String,DZFDouble>();//本年累计
			
			List ylqk = new ArrayList();//盈利状态
			List sxqk = new ArrayList();//三项
			List ysyfqk = new ArrayList();//应收应付
			List zjzg = new ArrayList();//资金状况
			List sjzk = new ArrayList();//税金状态
			List sryj = new ArrayList();//收入预警
			
			DZFDouble d = DZFDouble.ZERO_DBL;
			for (int i=begin;i<=end;i++) {
				period = year +"-" +String.format("%02d", i);
				cwvos = cwmap.get(period);
				CwglInfoAppVO tempvo = null;
				if(cwvos!=null && cwvos.length>0){
					for(CwgyInfoVO vo:cwvos){
						tempvo = new CwglInfoAppVO();
						if(bqdb.containsKey(vo.getXm())){
							bqdb.put(vo.getXm(), SafeCompute.add(bqdb.get(vo.getXm()),vo.getByje() ));
						}else{
							bqdb.put(vo.getXm(), VoUtils.getDZFDouble(vo.getByje()));
						}
						
						//本年累计计算
						bndb.put(vo.getXm(), VoUtils.getDZFDouble(vo.getBnljje()));
						
						tempvo.setXm(vo.getXm());
						if("营业收入净利率".equals(vo.getXm())){
							DZFDouble jlr = bqdb.get("净利润") == null? DZFDouble.ZERO_DBL:bqdb.get("净利润") ;
							DZFDouble yysr = bqdb.get("营业收入") == null? DZFDouble.ZERO_DBL:bqdb.get("营业收入") ;
							d = jlr.div(yysr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setByje((d.doubleValue() ==0 ? "0":d.doubleValue()+"%"));
							
							
							jlr = bndb.get("净利润") == null? DZFDouble.ZERO_DBL:bndb.get("净利润") ;
							yysr = bndb.get("营业收入") == null? DZFDouble.ZERO_DBL:bndb.get("营业收入") ;
							d = jlr.div(yysr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setBnljje((d.doubleValue() ==0 ? "0":d.doubleValue()+"%"));
						}else if("费用比率".equals(vo.getXm())){
							DZFDouble yysr =  bqdb.get("营业收入")== null? DZFDouble.ZERO_DBL:bqdb.get("营业收入");
							DZFDouble xsfy = bqdb.get("销售费用")== null? DZFDouble.ZERO_DBL:bqdb.get("销售费用");
							DZFDouble glfy = bqdb.get("管理费用")== null? DZFDouble.ZERO_DBL:bqdb.get("管理费用");
							DZFDouble cwfy = bqdb.get("财务费用")== null? DZFDouble.ZERO_DBL:bqdb.get("财务费用");
							d = (xsfy.add(glfy).add(cwfy)).div(yysr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setByje((d.doubleValue() ==0 ? "0":d.doubleValue()+"%"));
						
							yysr = bndb.get("营业收入") == null ? DZFDouble.ZERO_DBL : bndb.get("营业收入");
							xsfy = bndb.get("销售费用") == null ? DZFDouble.ZERO_DBL : bndb.get("销售费用");
							glfy = bndb.get("管理费用") == null ? DZFDouble.ZERO_DBL : bndb.get("管理费用");
							cwfy = bndb.get("财务费用") == null ? DZFDouble.ZERO_DBL : bndb.get("财务费用");
							
							d = (xsfy.add(glfy).add(cwfy)).div(yysr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setBnljje((d.doubleValue() ==0 ? "0":d.doubleValue()+"%"));
						} else if("增值税税负率".equals(vo.getXm())){
							DZFDouble zzs = bqdb.get("增值税") == null? DZFDouble.ZERO_DBL:bqdb.get("增值税");
							DZFDouble yysr =  bqdb.get("营业收入")== null? DZFDouble.ZERO_DBL:bqdb.get("营业收入");
							d = zzs.div(yysr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setByje((d.doubleValue() ==0 ? "0":d.doubleValue()+"%"));
							
							zzs = bndb.get("增值税") == null ? DZFDouble.ZERO_DBL : bndb.get("增值税");
							yysr = bndb.get("营业收入") == null ? DZFDouble.ZERO_DBL : bndb.get("营业收入");
							d = zzs.div(yysr).multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP);
							tempvo.setBnljje((d.doubleValue() ==0 ? "0":d.doubleValue()+"%"));
						}else{
							tempvo.setByje(Common.format(bqdb.get(vo.getXm())));
							tempvo.setBnljje(Common.format(bndb.get(vo.getXm())));
						}
						
						if(i == end){
							if("盈利状况".equals(vo.getXmfl())){
								ylqk.add(tempvo); 
							}else if("三项费用".equals(vo.getXmfl())){
								sxqk.add(tempvo);
							}else if("应收应付".equals(vo.getXmfl())){
								ysyfqk.add(tempvo);
							}else if("资金状况".equals(vo.getXmfl())){
								zjzg.add(tempvo);
							}else if("税金状况".equals(vo.getXmfl())){
								sjzk.add(tempvo);
							}else if("收入预警".equals(vo.getXmfl())){
								sryj.add(tempvo);
							}
						}
					}
				}
			}
			
			reslist.add(ylqk.toArray(new CwglInfoAppVO[0]));
			reslist.add(sxqk.toArray(new CwglInfoAppVO[0]));
			reslist.add(ysyfqk.toArray(new CwglInfoAppVO[0]));//应收应付
			reslist.add(zjzg.toArray(new CwglInfoAppVO[0]));//资金
			reslist.add(sjzk.toArray(new CwglInfoAppVO[0]));//税金
		}
		
	}

	
}
