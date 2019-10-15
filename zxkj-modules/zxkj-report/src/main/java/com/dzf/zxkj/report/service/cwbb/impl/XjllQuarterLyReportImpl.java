package com.dzf.zxkj.report.service.cwbb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.query.QueryParamVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.BdtradecashflowVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwbb.IXjllbQuarterlyReport;
import com.dzf.zxkj.report.service.cwbb.IZcFzBReport;
import com.dzf.zxkj.report.service.cwzb.IFsYeReport;
import com.dzf.zxkj.report.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

/**
 * 现金流量季报
 * @author zhangj
 *
 */
@Service("gl_rep_xjlyquarbserv")
@Slf4j
public class XjllQuarterLyReportImpl implements IXjllbQuarterlyReport {

	@Reference(version = "1.0.0")
	private IZxkjPlatformService zxkjPlatformService;

	@Autowired
	private IZcFzBReport gl_rep_zcfzserv;

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private IFsYeReport gl_rep_fsyebserv;
	
	@Override
	public List<XjllquarterlyVo> getXjllQuartervos(QueryParamVO paramvo, String jd) throws DZFWarpException {

		// 获取当前的现金流量数据
		List<XjllquarterlyVo> xjqueryvo = obtainCurrXjll(paramvo, jd);

		// 获取前一年的现金流量数据
		QueryParamVO bef_paramvo = deepClone(paramvo);
		DZFDate bf_date = new DZFDate(DateUtils.getPreviousYear(paramvo.getBegindate1().getMillis()));
		bef_paramvo.setBegindate1(bf_date);
		List<XjllquarterlyVo> bfxjqueryvo = obtainCurrXjll(bef_paramvo, jd);
		// 赋值同期上年累计值
		for (XjllquarterlyVo vo1 : xjqueryvo) {
			if(bfxjqueryvo!=null && bfxjqueryvo.size()>0){
				for (XjllquarterlyVo vo2 : bfxjqueryvo) {
					if (vo1.getXm().equals(vo2.getXm())) {
						vo1.setBf_bnlj(vo2.getBnlj());// 赋值上年累计数
						vo1.setJd1_last(vo2.getJd1());
						vo1.setJd2_last(vo2.getJd2());
						vo1.setJd3_last(vo2.getJd3());
						vo1.setJd4_last(vo2.getJd4());
					}
				}
			}
		}

		return xjqueryvo;
	}

	private List<XjllquarterlyVo> obtainCurrXjll(QueryParamVO paramvo, String jd) {
		DZFDate begdate = null;//开始日期 
		
		DZFDate endate = null;//结束日期
		
		String year = paramvo.getBegindate1().getYear() + "";
		
		CorpVO cpvo = zxkjPlatformService.queryCorpByPk(paramvo.getPk_corp());

		String begstr = null;

		if (year.equals(cpvo.getBegindate().getYear() + "")) {
			begstr = cpvo.getBegindate().toString().substring(0, 7)+"-01";
		} else {
			begstr = year + "-01-01";
		}
		
		if(year.compareTo(cpvo.getBegindate().getYear() + "")<0){//数据不存在
			return null;
		}
		
		String endstr = year + "-12" + "-01";

		begdate = new DZFDate(begstr);

		endate = new DZFDate(endstr);
		
		List<String> periods = ReportUtil.getPeriods(begdate, endate);

		// 查询现金流量的发生数据(每一期的数据)
		Map<String, Map<Float, XjllbVO>> xjllmap = getXjllMapBvos(begdate, endate, periods, paramvo);

		// 资产负债的数据
//		Map<String, ZcFzBVO[]> zcfzmap = getZcfzMap(paramvo, begdate, endate, periods);
		
		Map<String, List<FseJyeVO>> fsmap = getFsYjMap(paramvo, new DZFDate(year + "-01-01"), endate, periods);

		// 根据现金流量+资产负债的数据生成
		List<XjllquarterlyVo> xjqueryvo = genXjJdVO(paramvo, jd, year, periods, xjllmap,cpvo,fsmap);
		
		return xjqueryvo;
	}
	
	private QueryParamVO deepClone(QueryParamVO vo){
		
		ByteArrayOutputStream bo = null ;
		ObjectOutputStream oo =  null;
		ByteArrayInputStream bi = null;
		ObjectInputStream oi = null;
		
		try {
			bo = new ByteArrayOutputStream();

			oo = new ObjectOutputStream(bo);
			
			oo.flush();
			
			oo.writeObject(vo);
			
			bi = new ByteArrayInputStream(bo.toByteArray());
			
			oi = new ObjectInputStream(bi);
			
			return (QueryParamVO) oi.readObject();
		} catch (Exception e) {
			log.error("错误",e);
		}finally {
			
			if (oo != null){
				try {
					oo.close();
				} catch (IOException e) {
//					e.printStackTrace();
				}
			}
			
			if (bo != null){
				try {
					bo.close();
				} catch (IOException e) {
//					e.printStackTrace();
				}
			}
			if(bi!=null){
				try {
					bi.close();
				} catch (IOException e) {
//					e.printStackTrace();
				}
			}
			
			if(oi!=null){
				try {
					oi.close();
				} catch (IOException e) {
//					e.printStackTrace();
				}
			}
		}
		return null;
		
	}

	private List<XjllquarterlyVo> genXjJdVO(QueryParamVO paramvo, String jd, String year, List<String> periods,
			Map<String, Map<Float, XjllbVO>> xjllmap,CorpVO cpvo,Map<String, List<FseJyeVO>> fsmap) {
		List<XjllbVO[]> xjllbvos = new ArrayList<XjllbVO[]>();
		Map<String,XjllbVO[]> xjperiodmap = new HashMap<String,XjllbVO[]>();
		List<XjllquarterlyVo> xjqueryvo = new ArrayList<XjllquarterlyVo>();
		XjllbVO[] firsttemp = null;
		//当前传入公司对应的会计科目方案
		String pk_trade_accountschema = zxkjPlatformService.getCurrentCorpAccountSchema(cpvo.getPk_corp()) ;
		YntCpaccountVO[] cpavos = zxkjPlatformService.queryByPk(cpvo.getPk_corp());
		for(String period:periods){
			List<FseJyeVO> fslist = fsmap.get(period);
			Map<String, FseJyeVO> fsmap_period = new HashMap<String, FseJyeVO>();
			if(fslist!=null && fslist.size()>0){
				fsmap_period = ReportUtil.conVertMapFs(fslist);
			}
			XjllbVO[] bvostemp = getXjllbVOs(period, cpvo, xjllmap.get(period), pk_trade_accountschema,cpavos,fsmap_period);
			xjllbvos.add(bvostemp);
			xjperiodmap.put(period, bvostemp);
			
			if(firsttemp == null){
				firsttemp = bvostemp;
			}
		}
		for(XjllbVO bvo :firsttemp){
			XjllquarterlyVo quarvo = new XjllquarterlyVo();
			quarvo.setXm(bvo.getXm());
			quarvo.setHc(StringUtil.isEmpty(bvo.getHc()) ? "" : bvo.getHc());
			quarvo.setHc_id(bvo.getHc_id());
			xjqueryvo.add(quarvo);
		}
		
		//取每个季度的数据合计
		String bnqcstr =null;
		for(int i=1;i<=Integer.parseInt(jd);i++){
			int qcfirst = -1;
			for(int k =1;k<=3;k++){
				int valuetemp =((i-1)*3)+k;
				String period = year+"-"+(valuetemp<10?"0"+valuetemp:valuetemp);
				if(periods.contains(period)){
					if(qcfirst == -1){
						qcfirst = k;
					}
					if(bnqcstr == null){
						bnqcstr = period;
					}
					XjllbVO[] bvos = xjperiodmap.get(period);
					for(XjllbVO bvo:bvos){
						if(StringUtil.isEmpty(bvo.getHc())){
							bvo.setHc("");
						}
						for(XjllquarterlyVo quarvo:xjqueryvo){
							if(bvo.getXm().equals(quarvo.getXm()) && bvo.getHc().equals(quarvo.getHc())){
								if(bvo.getXm().equals("五、期初现金余额")  || bvo.getXm().equals("　　加：期初现金及现金等价物余额")){
									if(k == qcfirst){
										quarvo.setAttributeValue("jd"+i, bvo.getBqje());
									}
									if(period.equals(bnqcstr)){
										quarvo.setBnlj(bvo.getSqje());
									}
								} else if(bvo.getXm().equals("六、期末现金余额") || bvo.getXm().equals("六、期末现金及现金等价物余额")){
									if(k == 3){
										quarvo.setAttributeValue("jd"+i, bvo.getBqje());
									}
								}else{
									quarvo.setAttributeValue("jd"+i, SafeCompute.add((DZFDouble)quarvo.getAttributeValue("jd"+i) , bvo.getBqje()));
								}
								if(!(bvo.getXm().equals("五、期初现金余额")  || bvo.getXm().equals("　　加：期初现金及现金等价物余额") )){
									quarvo.setBnlj(bvo.getSqje());
								}
							}
						}
					}
				}
			}
		}
		//非当前季度则赋值上一季度的值
		DZFDate begdate = cpvo.getBegindate();
		String qcjd = "";
		if (Integer.parseInt(year) == begdate.getYear() && begdate.getMonth() > 3){//非第一季度
			if(begdate.getMonth()>=10){
				qcjd = "3";//取第三季度
			}else if(begdate.getMonth()>=7){
				qcjd = "2";//取第二季度
			}else if(begdate.getMonth()>=4){
				qcjd = "1";//取第一季度
			}
			DZFDouble qcjdvalue = DZFDouble.ZERO_DBL;
			DZFDouble begjdvalue ;
			for(XjllquarterlyVo vo1:xjqueryvo){
				 begjdvalue = DZFDouble.ZERO_DBL;
				if(!vo1.getXm().equals("五、期初现金余额")  &&
						!vo1.getXm().equals("　　加：期初现金及现金等价物余额")
						&& !vo1.getXm().equals("六、期末现金余额")  &&
						!vo1.getXm().equals("六、期末现金及现金等价物余额")){
					for(int i =Integer.parseInt(qcjd)+1;i<=4;i++){
						begjdvalue = SafeCompute.add(begjdvalue,(DZFDouble) vo1.getAttributeValue("jd"+i));
					}
					qcjdvalue = SafeCompute.sub(vo1.getBnlj(), begjdvalue);
					vo1.setAttributeValue("jd"+qcjd, qcjdvalue);
				}
			}
		}
		return xjqueryvo;
	}
	
	/**
	 * 获取现金流量的发生数据
	 * @param begdate
	 * @param endate
	 * @param periods
	 * @param paramvo
	 */
	private Map<String,Map<Float, XjllbVO>> getXjllMapBvos(DZFDate begdate, DZFDate endate, List<String> periods, QueryParamVO paramvo) {
		Map<String,Map<Float, XjllbVO>> xjllmap = new HashMap<String,Map<Float, XjllbVO>>();
		//当前传入公司对应的会计科目方案
		String pk_corp = paramvo.getPk_corp();
		
		List<String> peirods = ReportUtil.getPeriods(begdate, endate);
		
		Map<Float, XjllbVO> map1=new HashMap<Float, XjllbVO>();
		
		getEveryMonthXjmap(xjllmap,map1,pk_corp,String.valueOf(begdate.getYear()),peirods);
		
		//发生的数据拼接
		SQLParameter sp = new SQLParameter();
		StringBuffer where = new StringBuffer();
		where.append(" select  substr(xj.doperatedate,0,7), aa.itemcode as pk_xjllxm ,   ");
		where.append(" sum(nvl(xj.nmny,0)) f1 ");
		where.append(" from ynt_xjll xj  ");
		where.append(" inner join ynt_tdcashflow aa on  xj.pk_xjllxm = aa.pk_trade_cashflow ");
		where.append(" where  nvl(aa.dr,0)=0 and  substr(xj.doperatedate,0,4) = ? and ");
		where.append("   xj.pk_corp=?  and nvl(xj.dr,0)=0  ");
		where.append(" group by aa.itemcode,substr(xj.doperatedate,0,7)  ");
		sp.addParam(begdate.getYear());
		sp.addParam(pk_corp);
		List<Object[]> list=(List<Object[]>) singleObjectBO.executeQuery(where.toString(), sp, new ArrayListProcessor());
		
		String period1 = null;
		for(int i=0;i<list.size();i++){
			period1 = (String) list.get(i)[0];
			Float itemcode = (Float.valueOf((String)list.get(i)[1])) ;
			DZFDouble value =list.get(i)[2]== null?DZFDouble.ZERO_DBL: new DZFDouble((BigDecimal)list.get(i)[2]);
			if(xjllmap.get(period1)!=null){
				xjllmap.get(period1).get(itemcode).setBqje(value);
			}
		}
		
		//计算本年累计金额
		for(int i=peirods.size()-1;i>=0;i--){
			String period = peirods.get(i);
			Map<Float, XjllbVO> xjbvomap = xjllmap.get(period);
			for(Entry<Float, XjllbVO> entry:xjbvomap.entrySet()){
				Float key = entry.getKey();
				XjllbVO value = entry.getValue();
				DZFDouble sumqcvalue = value.getSqje() == null? DZFDouble.ZERO_DBL:value.getSqje();
				DZFDouble sumbqvalue = DZFDouble.ZERO_DBL;
				for(String str:peirods){
					if(str.compareTo(period)<=0){
						XjllbVO valuetemp = xjllmap.get(str).get(key);
						sumbqvalue = SafeCompute.add(valuetemp.getBqje(), sumbqvalue);
					}
				}
				value.setSqje(SafeCompute.add(sumqcvalue, sumbqvalue));
			}
		}
		return xjllmap;
	}

	private void getEveryMonthXjmap(Map<String,Map<Float, XjllbVO>> xjllmap,Map<Float, XjllbVO> map1,
			String pk_corp,String year,List<String> peirods ) {
		 
		StringBuffer where = new StringBuffer();
		
		String pk_trade_accountschema = zxkjPlatformService.getCurrentCorpAccountSchema(pk_corp) ;
		 where.append("  nvl(dr,0)=0 and pk_trade_accountschema=? ");
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_trade_accountschema);
		BdtradecashflowVO[] flow1VOs = (BdtradecashflowVO[])singleObjectBO.queryByCondition(BdtradecashflowVO.class, where.toString(),sp) ;
		int len=flow1VOs==null?0:flow1VOs.length;
		BdtradecashflowVO cfvo=null;
		XjllbVO vo = null;
		for (int i = 0; i < len; i++) {
			cfvo=flow1VOs[i];
			vo=new XjllbVO();
			vo.setXm("　　"+cfvo.getItemname());
			vo.setHc(cfvo.getItemcode()) ;
			vo.setRowno(Float.parseFloat(cfvo.getItemcode()));
			map1.put(vo.getRowno(), vo);
		}
		
		where = new StringBuffer();
		where.append(" select sum(nvl(Nmny,0)) nmny, aa.itemcode as pk_project  "); 
		where.append(" from ynt_xjllqcye ");
		where.append(" inner join bd_corp on ynt_xjllqcye.pk_corp =bd_corp.pk_corp  ");
		where.append(" inner join ynt_tdcashflow aa on ynt_xjllqcye.pk_project = aa.pk_trade_cashflow ");
		where.append(" where ynt_xjllqcye.pk_corp=? and nvl(ynt_xjllqcye.dr,0)=0  ");
		where.append("       and substr(bd_corp.begindate,0,4)=?  ");
		where.append(" group by aa.itemcode     ");
		sp.clearParams();
		sp.addParam(pk_corp);
		sp.addParam(year);
		List list=(List) singleObjectBO.executeQuery(where.toString(), sp, new BeanListProcessor(XjllQcyeVO.class));
		len=list==null?0:list.size();
		XjllQcyeVO xqcvo=null;
		for (int i = 0; i < len; i++) {
			xqcvo=(XjllQcyeVO) list.get(i);
			vo=map1.get(Float.parseFloat(xqcvo.getPk_project()));
			vo.setSqje(xqcvo.getNmny());
		}
		
		
		for(String str:peirods){
			Map<Float, XjllbVO> maptemp = new HashMap<Float, XjllbVO>();
			for(Entry<Float, XjllbVO> entry:map1.entrySet()){
				XjllbVO temp = new XjllbVO();
				BeanUtils.copyNotNullProperties(entry.getValue(), temp);
				maptemp.put(entry.getKey(),temp);
			}
			xjllmap.put(str, maptemp);
		}
	}

	/**
	 * 现金流量报表取数
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public XjllbVO[] getXjllbVOs(String period , CorpVO cpvo,
			Map<Float, XjllbVO> map,String pk_trade_accountschema,
			YntCpaccountVO[] cpavos,Map<String, FseJyeVO> fsmap) throws  DZFWarpException{
		String pk_corp = cpvo.getPk_corp();
		if(DzfUtil.SEVENSCHEMA.intValue()  ==  zxkjPlatformService.getAccountSchema(pk_corp)){ //07方案
			return  new Qy07XjlReportService(zxkjPlatformService).getXJLLB2007VOs1(map , period , pk_corp,cpvo.getBegindate(),cpvo.getCorptype(), pk_trade_accountschema,fsmap) ;
		}else if(DzfUtil.THIRTEENSCHEMA.intValue()  ==  zxkjPlatformService.getAccountSchema(pk_corp)){ //13方案
			return new XQy13XjlReportService(zxkjPlatformService).getXJLLB2013VOs1(map ,period , pk_corp,cpvo.getBegindate(),cpvo.getCorptype(), pk_trade_accountschema,fsmap) ;
		}else if(DzfUtil.POPULARSCHEMA.intValue()  ==  zxkjPlatformService.getAccountSchema(pk_corp)){//民间
			return  new PopularXjlReportService(gl_rep_fsyebserv).getXjlBvos(map, period, cpvo.getPk_corp(),cpvo.getCorptype(), pk_trade_accountschema,cpavos);
		}else if(DzfUtil.COMPANYACCOUNTSYSTEM.intValue()  ==  zxkjPlatformService.getAccountSchema(pk_corp)){//企业会计准则
			return  new CompanyXjlReportService(gl_rep_fsyebserv).getXjlBvos(map, period, pk_corp, pk_trade_accountschema);
		}else{
			throw new BusinessException("该制度暂不支持现金流量表,敬请期待!");
		}
	}
	
	private Map<String, List<FseJyeVO>> getFsYjMap(QueryParamVO paramvo, DZFDate begdate, DZFDate endate, List<String> periods){

		Object[] objs = gl_rep_fsyebserv.getEveryPeriodFsJyeVOs(begdate, endate, 
				paramvo.getPk_corp(), null, "", DZFBoolean.FALSE);

		Map<String, List<FseJyeVO>> fsmap = (Map<String, List<FseJyeVO>>) objs[0];
		
		Map<String, List<FseJyeVO>> fsmapres = new HashMap<String, List<FseJyeVO>>();
		
		List<FseJyeVO> periodfs = null;

		ZcFzBVO[] zcfzbvos = null;

		for (String period : periods) {
			periodfs = fsmap.get(period);

			if (periodfs == null) {
				continue;
			}

			// 从年初开始取
			FseJyeVO[] restemp = gl_rep_fsyebserv.getBetweenPeriodFs(
					DateUtils.getPeriodStartDate(period.substring(0, 4) + "-01"), new DZFDate(period + "-01"), fsmap);

			fsmapres.put(period, Arrays.asList(restemp));
		}
		
		return fsmapres;
	}

	private Map<String,ZcFzBVO[]> getZcfzMap(QueryParamVO paramvo, DZFDate begdate, DZFDate endate, List<String> periods) {
		periods = ReportUtil.getPeriods(begdate, endate);
		//查询资产负债的数据(一年)
		List<ZcFzBVO[]> zcfzbvos =  gl_rep_zcfzserv.getZcfzVOs(begdate,endate, paramvo.getPk_corp(), 
				"N", new String[]{"N","N","N","N","N"},null);
		
		//转换成map数据
		Map<String,ZcFzBVO[]> zcfzbvo = new HashMap<String,ZcFzBVO[]>();
		for(int i=0;i<periods.size();i++){
			zcfzbvo.put(periods.get(i), zcfzbvos.get(i));
		}
		
		return zcfzbvo;
	}

	//以后的现金流量，就用这个，之前的现金流量，代码太冗余了!
//	private XjllbVO[] getXJLLB2007VOs1(Map<Float, XjllbVO> map,String period , String pk_corp,String pk_trade_accountschema,
//			Map<String,ZcFzBVO[]> zcfzmap) throws DZFWarpException{
//		DZFDouble[] ds=null;
//		 XjllbVO vo0 = new XjllbVO() ;
//		vo0.setXm("一、经营活动产生的现金流量：") ;
//		vo0.setRowno(1f);
//		vo0.setHc("1");
//		map.put(vo0.getRowno(), vo0);//vec.add(vo0) ;
//		
//		ds= getSumXJLLB(map,2,4);
//		DZFDouble bqje1to3 = ds[0];
//		DZFDouble bnje1to3 = ds[1];
//		XjllbVO vo4 = new XjllbVO() ;
//		vo4.setXm("　　经营活动现金流入小计") ;
//		vo4.setHc("5") ;
//		vo4.setRowno(5);
//		vo4.setBqje(bqje1to3) ;
//		vo4.setSqje(bnje1to3) ;
//		map.put(vo4.getRowno(), vo4);//vec.add(vo4) ;
//		
//		//查询行号为5-8的现金流量项目
//		ds= getSumXJLLB(map,6,9);
//		DZFDouble bqje5to8 = ds[0];
//		DZFDouble bnje5to8 = ds[1];
//		XjllbVO vo9 = new XjllbVO() ;
//		vo9.setXm("　　经营活动现金流出小计") ;
//		vo9.setHc("10") ;
//		vo9.setRowno(10);
//		vo9.setBqje(bqje5to8) ;
//		vo9.setSqje(bnje5to8) ;
//		map.put(vo9.getRowno(), vo9);//vec.add(vo9) ;
//		
//		XjllbVO vo10 = new XjllbVO() ;
//		vo10.setXm("　　经营活动产生的现金流量净额") ;
//		vo10.setHc("11") ;
//		vo10.setRowno(11);
//		vo10.setBqje(vo4.getBqje().sub(vo9.getBqje())) ;
//		vo10.setSqje(vo4.getSqje().sub(vo9.getSqje())) ;
//		map.put(vo10.getRowno(), vo10);//vec.add(vo10) ;
//		
//		XjllbVO voEr = new XjllbVO() ;
//		voEr.setXm("二、投资活动产生的现金流量：") ;	
//		voEr.setRowno(12f);
//		voEr.setHc("12");
//		map.put(voEr.getRowno(), voEr);//vec.add(voEr) ;
//		
//				
//		ds= getSumXJLLB(map,13,17);
//		DZFDouble bqje11to15 = ds[0];
//		DZFDouble bnje11to15 = ds[1];
//		XjllbVO vo16 = new XjllbVO() ;
//		vo16.setXm("　　投资活动现金流入小计") ;
//		vo16.setHc("18") ;
//		vo16.setRowno(18);
//		vo16.setBqje(bqje11to15) ;
//		vo16.setSqje(bnje11to15) ;
//		map.put(vo16.getRowno(), vo16);//vec.add(vo16) ;
//		
//				
//		ds= getSumXJLLB(map,19,22);
//		DZFDouble bqje17to20 = ds[0];
//		DZFDouble bnje17to20 = ds[1];
//		XjllbVO vo21 = new XjllbVO() ;
//		vo21.setXm("　　投资活动现金流出小计") ;
//		vo21.setHc("23") ;
//		vo21.setRowno(23);
//		vo21.setBqje(bqje17to20) ;
//		vo21.setSqje(bnje17to20) ;
//		map.put(vo21.getRowno(), vo21);//vec.add(vo21) ;
//		
//		XjllbVO vo22 = new XjllbVO() ;
//		vo22.setXm("　　投资活动产生的现金流量净额") ;
//		vo22.setHc("24") ;
//		vo22.setRowno(24);
//		vo22.setBqje(vo16.getBqje().sub(vo21.getBqje())) ;
//		vo22.setSqje(vo16.getSqje().sub(vo21.getSqje())) ;
//		map.put(vo22.getRowno(), vo22);//vec.add(vo22) ;
//		
//		XjllbVO voSan = new XjllbVO() ;
//		voSan.setXm("三、筹资活动产生的现金流量：") ;
//		voSan.setRowno(25f);
//		voSan.setHc("25");
//		map.put(voSan.getRowno(), voSan);//vec.add(voSan) ;
//		
//			
//		ds= getSumXJLLB(map,26,28);
//		DZFDouble bqje23to25 = ds[0];
//		DZFDouble bnje23to25 = ds[1];
//		XjllbVO vo26 = new XjllbVO() ;
//		vo26.setXm("　　筹资活动现金流入小计") ;
//		vo26.setHc("29") ;
//		vo26.setRowno(29);
//		vo26.setBqje(bqje23to25) ;
//		vo26.setSqje(bnje23to25) ;
//		map.put(vo26.getRowno(), vo26);//vec.add(vo26) ;
//		
//				
//		ds= getSumXJLLB(map,30,32);
//		DZFDouble bqje27to29 = ds[0];
//		DZFDouble bnje27to29 = ds[1];
//		XjllbVO vo30 = new XjllbVO() ;
//		vo30.setXm("　　筹资活动现金流出小计") ;
//		vo30.setHc("33") ;
//		vo30.setRowno(33);
//		vo30.setBqje(bqje27to29) ;
//		vo30.setSqje(bnje27to29) ;
//		map.put(vo30.getRowno(), vo30);//vec.add(vo30) ;
//		
//		XjllbVO vo31 = new XjllbVO() ;
//		vo31.setXm("　　筹资活动产生的现金流量净额") ;
//		vo31.setHc("34") ;
//		vo31.setRowno(34);
//		vo31.setBqje(vo26.getBqje().sub(vo30.getBqje())) ;
//		vo31.setSqje(vo26.getSqje().sub(vo30.getSqje())) ;
//		map.put(vo31.getRowno(), vo31);//vec.add(vo31) ;
//		
//		XjllbVO voSi = new XjllbVO() ;
//		voSi.setXm("四、汇率变动对现金及现金等价物的影响") ;	
//		voSi.setHc("35") ;
//		voSi.setRowno(35);
//		map.put(voSi.getRowno(), voSi);//vec.add(voSi) ;
//		
//		XjllbVO voWu = new XjllbVO() ;
//		voWu.setXm("五、现金及现金等价物净增加额") ;	
//		voWu.setHc("36") ;
//		voWu.setRowno(36);
//		voWu.setBqje(vo10.getBqje().add(vo22.getBqje()).add(vo31.getBqje())) ;
//		voWu.setSqje(vo10.getSqje().add(vo22.getSqje()).add(vo31.getSqje())) ;
//		map.put(voWu.getRowno(), voWu);//vec.add(voWu) ;
//		
//		//加：期初现金及现金等价物余额=库存现金、银行存款、其他货币资金的期初余额累计，前面三个科目的本年发生额累计(借方累计-贷方累计)
//		XjllbVO vo34 = new XjllbVO() ;
//		vo34.setXm("　　加：期初现金及现金等价物余额") ;	
//		vo34.setHc("37") ;
//		vo34.setRowno(37);
//		//取库存现金、银行存款、其他货币资金这三个科目的期初
//		//如果查询月份小于建账日期
//		CorpVO corpvo=CorpCache.getInstance().get(null, pk_corp);
//		DZFDate corpdate = new DZFDate(DateUtils.getPeriod(corpvo.getBegindate()) +"-01");
//		DZFDate currdate = new DZFDate(period+"-01");
//		ZcFzBVO[]  zfzcbvosn = zcfzmap.get(period);
//		vo34.setSqje(SafeCompute.add(zfzcbvosn[1].getNcye1(), zfzcbvosn[2].getNcye1()) );
//		vo34.setBqje(SafeCompute.add(zfzcbvosn[1].getQcye1(),  zfzcbvosn[2].getQcye1()));
//		map.put(vo34.getRowno(), vo34);
//		
//		
//		XjllbVO voLiu = new XjllbVO() ;
//		voLiu.setXm("六、期末现金及现金等价物余额") ;	
//		voLiu.setHc("38") ;
//		voLiu.setRowno(38);
//		if(currdate.before(corpdate)){
//			voLiu.setBqje(voWu.getBqje().add(vo34.getBqje())) ;
//			voLiu.setSqje(voWu.getSqje().add(vo34.getSqje())) ;
//		}else{
//			voLiu.setBqje(SafeCompute.add(zfzcbvosn[1].getQmye1(),  zfzcbvosn[2].getQmye1()));
//			voLiu.setSqje(SafeCompute.add(zfzcbvosn[1].getQmye1(),  zfzcbvosn[2].getQmye1()));
//		}
//		map.put(voLiu.getRowno(), voLiu);//vec.add(voLiu) ;
//		
//		XjllbVO[] xvos=new XjllbVO[map.size()];
//		Float[] fs=	(Float[])map.keySet().toArray(new Float[0]);
//		Arrays.sort(fs);
//		int len=map.size();
//		for (int i = 0; i < len; i++) {
//			xvos[i]=map.get(fs[i]);
//			if(!StringUtil.isEmpty(xvos[i].getHc())){
//				xvos[i].setHc_id("XJLL-"+ String.format("%03d", Integer.parseInt(xvos[i].getHc())));
//			}
//		}
//		map=null;
//		
//		return xvos;
//	 }
	
//	private XjllbVO[] getXJLLB2013VOs1(Map<Float, XjllbVO> map ,String period , String pk_corp,String pk_trade_accountschema,
//			Map<String,ZcFzBVO[]> zcfzmap) throws DZFWarpException{
//		DZFDouble[] ds=null;
//		 XjllbVO vo0 = new XjllbVO() ;
//		vo0.setXm("一、经营活动产生的现金流量：") ;
//		vo0.setRowno(0.5f);
//		map.put(vo0.getRowno(), vo0);
//		
//		//查询行号为2-3的现金流量项目
//		ds= getSumXJLLB(map,1,2);
//		DZFDouble bqje1to3 = ds[0];
//		DZFDouble bnje1to3 = ds[1];
//		
//		
//		//查询行号为4-7的现金流量项目			
//		ds= getSumXJLLB(map,3,6);
//		DZFDouble bqje4to6 = ds[0];;
//		DZFDouble bnje4to6 = ds[1] ;	
//		
//		XjllbVO vo7 = new XjllbVO() ;
//		vo7.setXm("　　经营活动产生的现金流量净额") ;
//		vo7.setHc("7") ;
//		vo7.setRowno(7);
//		vo7.setBqje(bqje1to3.sub(bqje4to6)) ;
//		vo7.setSqje(bnje1to3.sub(bnje4to6)) ;
//		map.put(vo7.getRowno(),vo7);
//				
//		XjllbVO voEr = new XjllbVO() ;
//		voEr.setXm("二、投资活动产生的现金流量") ;
//		voEr.setRowno(7.5f);
//		map.put(voEr.getRowno(),voEr);
//		
//		ds= getSumXJLLB(map,8,10);
//		DZFDouble bqje8to10 = ds[0];;
//		DZFDouble bnje8to10 = ds[1] ;	
//			
//		ds= getSumXJLLB(map,11,12);
//		DZFDouble bqje11to12 = ds[0];;
//		DZFDouble bnje11to12 = ds[1] ;	
//		XjllbVO vo13 = new XjllbVO() ;
//		vo13.setXm("　　投资活动产生的现金流量净额") ;
//		vo13.setHc("13") ;
//		vo13.setRowno(13);
//		vo13.setBqje(bqje8to10.sub(bqje11to12)) ;
//		vo13.setSqje(bnje8to10.sub(bnje11to12)) ;
//		map.put(vo13.getRowno(),vo13);
//		
//		
//		XjllbVO voSan = new XjllbVO() ;
//		voSan.setXm("三、筹资活动产生的现金流量：") ;
//		voSan.setRowno(13.5f);
//		map.put(voSan.getRowno(),voSan);
//		
//				
//		ds= getSumXJLLB(map,14,15);
//		DZFDouble bqje14to15 = ds[0];;
//		DZFDouble bnje14to15 = ds[1] ;	
//				
//		ds= getSumXJLLB(map,16,18);
//		DZFDouble bqje16to18 = ds[0];;
//		DZFDouble bnje16to18 = ds[1] ;	
//		XjllbVO vo19 = new XjllbVO() ;
//		vo19.setXm("　　筹资活动产生的现金流量净额") ;
//		vo19.setHc("19") ;
//		vo19.setRowno(19);
//		vo19.setBqje(bqje14to15.sub(bqje16to18)) ;
//		vo19.setSqje(bnje14to15.sub(bnje16to18)) ;
//		map.put(vo19.getRowno(),vo19);//vec.add(vo19) ;
//		
//		XjllbVO voSi = new XjllbVO() ;
//		voSi.setXm("四、现金净增加额") ;
//		voSi.setHc("20") ;
//		voSi.setRowno(20);
//		voSi.setBqje(vo7.getBqje().add(vo13.getBqje()).add(vo19.getBqje())) ;
//		voSi.setSqje(vo7.getSqje().add(vo13.getSqje()).add(vo19.getSqje())) ;
//		map.put(voSi.getRowno(),voSi);//vec.add(voSi) ;
//		
//		XjllbVO voWu = new XjllbVO() ;
//		voWu.setXm("五、期初现金余额") ;
//		voWu.setHc("21") ;
//		voWu.setRowno(21);
//		//取库存现金、银行存款、其他货币资金这三个科目的期初
//		//如果查询月份小于建账日期
//		CorpVO corpvo=CorpCache.getInstance().get(null, pk_corp);
//		DZFDate corpdate = new DZFDate(DateUtils.getPeriod(corpvo.getBegindate()) +"-01");
//		DZFDate currdate = new DZFDate(period+"-01");
//		ZcFzBVO[]  zfzcbvosn = zcfzmap.get(period);
//		if(DateUtils.getPeriod(corpdate).equals(period) || currdate.before(corpdate)){
//			voWu.setSqje(zfzcbvosn[1].getNcye1());
//			voWu.setBqje(zfzcbvosn[1].getQcye1());
//		}else {
//			voWu.setSqje(zfzcbvosn[1].getNcye1() );
//			voWu.setBqje(zfzcbvosn[1].getQcye1());
//		}
//		
//		
//		//取科目的期初值
//		map.put(voWu.getRowno(),voWu);//vec.add(voWu) ;
//		
//		XjllbVO voLiu = new XjllbVO() ;
//		voLiu.setXm("六、期末现金余额") ;
//		voLiu.setHc("22") ;
//		voLiu.setRowno(22);
//		if(currdate.before(corpdate)){
//			voLiu.setBqje(DZFDouble.getUFDouble(voSi.getBqje()).add(DZFDouble.getUFDouble(voWu.getBqje()))) ;
//			voLiu.setSqje(DZFDouble.getUFDouble(voSi.getSqje()).add(DZFDouble.getUFDouble(voWu.getSqje()) )) ;
//		}else{
//			voLiu.setBqje(zfzcbvosn[1].getQmye1()) ;
//			voLiu.setSqje(zfzcbvosn[1].getQmye1()) ;
//		}
//		map.put(voLiu.getRowno(),voLiu);//vec.add(voLiu) ;
//		XjllbVO[] xvos=new XjllbVO[map.size()];
//		Float[] fs=	(Float[])map.keySet().toArray(new Float[0]);
//		Arrays.sort(fs);
//		int len=map.size();
//		for (int i = 0; i < len; i++) {
//			xvos[i]=map.get(fs[i]);
//			if(!StringUtil.isEmpty(xvos[i].getHc())){
//				xvos[i].setHc_id("XJLL-"+ String.format("%03d", Integer.parseInt(xvos[i].getHc())));
//			}
//		}
//		map=null;
//		return xvos;
//	 }
	
//	private DZFDouble[] getSumXJLLB(Map<Float, XjllbVO> map,float s,float e){
//		DZFDouble bqje = DZFDouble.ZERO_DBL;
//		DZFDouble bnje = DZFDouble.ZERO_DBL;
//		XjllbVO xvo=null;
//		for (float i = s; i <=e; i+=1) {
//			xvo=map.get(i);
//			bqje=bqje.add(DZFDouble.getUFDouble(xvo.getBqje()));
//			bnje=bnje.add(DZFDouble.getUFDouble(xvo.getSqje()));
//		}
//		return new DZFDouble[]{bqje,bnje};
//	}
	
}
