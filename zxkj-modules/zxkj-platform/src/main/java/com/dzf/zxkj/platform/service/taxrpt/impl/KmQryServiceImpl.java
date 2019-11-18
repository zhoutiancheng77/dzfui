package com.dzf.zxkj.platform.service.taxrpt.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.ArrayProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.jzcl.KMQMJZVO;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.model.report.KmQmJzExtVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.taxrpt.IKmQryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 查询科目期末余额的值
 * 
 * @author zhangj
 *
 */
@Service("kmQryService")
@SuppressWarnings("all")
public class KmQryServiceImpl implements IKmQryService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private ICorpService corpService;

	@Override
	public Map<String, List<KmQmJzExtVO>> resmapvos(String[] corppks, String[] kms, String period)
			throws DZFWarpException {
		if (StringUtil.isEmpty(period)) {
			throw new BusinessException("期间不能为空!");
		}

		if (corppks == null || corppks.length == 0) {
			throw new BusinessException("公司不能为空");
		}

		StringBuffer corpsql = new StringBuffer();

		for (String corpstr : corppks) {
			corpsql.append("'" + corpstr + "',");
		}
		// 查询期初(结账金额+期间前的发生额)
		Map<String, List<KmQmJzExtVO>>  resmap =  getReportMny(corppks, period,Arrays.asList(kms));

		return resmap;
	}

	/**
	 * 获取科目对应的金额
	 * @param corppks
	 * @param period
	 * @param kmlist
	 * @return
	 */
	private Map<String, List<KmQmJzExtVO>> getReportMny(String[] corppks, String period,List<String> kmlist) {
		Map<String, List<KmQmJzExtVO>> resmap = new HashMap<String, List<KmQmJzExtVO>>();
		//1.1、查询客户科目信息
		Map<String, Map<String, YntCpaccountVO>> kmcorpmaps = getKm(corppks);
		String corpwherepart = SqlUtil.buildSqlForIn("pk_corp", corppks);//先根据公司循环查询期初，科目期末结账数据
		//1.2、查询期初余额
		Map<String ,List<QcYeVO>> qcyemap = getQCMap(corpwherepart);
		//1.3、期末结账时生成科目的期初、期末数
		Map<String ,Map<String,List<KMQMJZVO>>> qmjzmap = getQmJzMap(corpwherepart);
		DZFDate begindate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(new DZFDate(period.substring(0, 4) + "-01-01")));
		//1.4、取年结的最大期间
		Map<String ,String> maxperiodmap = getMaxMap(corpwherepart,begindate);
		for (String pk_corp : corppks) {
			DZFDate enddate = DateUtils.getPeriodEndDate(period);
			CorpVO corpvo = corpService.queryByPk(pk_corp);
			DZFDate corpdate = corpvo.getBegindate();//建账日期
			if(corpdate == null){
				continue;
			}
			if(period.compareTo(DateUtils.getPeriod(corpvo.getBegindate())) < 0){
				continue;
			}
			
			String jz_max_period = maxperiodmap.get(begindate.toString());// 开始日期之前最大结账期
			String min_period = DateUtils.getPeriod(begindate);
			KMQMJZVO[] KMQMJZVOs = null;
			DZFDate qcstart = null;
			DZFDate qcend = null;
			int len = 0;
			String key = null;
			KMQMJZVO kv = null;
			//2.1获取该客户科目信息
			Map<String, YntCpaccountVO> mp = kmcorpmaps.get(pk_corp);
			HashMap<String, HashMap<String, KmQmJzExtVO>> map = new HashMap<String, HashMap<String, KmQmJzExtVO>>();
			if (jz_max_period == null || corpdate.after(begindate)) {// 期初余额
				SQLParameter parameterqc = new SQLParameter();
				parameterqc.addParam(pk_corp);
				List<QcYeVO> listqcye = qcyemap.get(pk_corp);
				QcYeVO[] qcyeVOs = null; 
				if(listqcye != null && listqcye.size() > 0){
					qcyeVOs = listqcye.toArray(new QcYeVO[0]);
				}
				//2.2获取期初余额
				qcyeVOs = getLastVO(qcyeVOs, mp);
				qcend = begindate.getDateBefore(1);// begindate;
				len = qcyeVOs == null ? 0 : qcyeVOs.length;
				QcYeVO v = null;
				for (int i = 0; i < len; i++) {
					v = qcyeVOs[i];
					//2.3期末结账时生成科目的期初、期末数
					kv = getKey(map, v.getPk_accsubj(), min_period,mp);
					DZFDouble thismonqc = kv.getThismonthqc() == null ? DZFDouble.ZERO_DBL : kv.getThismonthqc();
					DZFDouble thismonqm = kv.getThismonthqm() == null ? DZFDouble.ZERO_DBL : kv.getThismonthqm();
					if (v.getPk_accsubj().equals(kv.getPk_accsubj())) {
						if (v.getPk_currency() == kv.getPk_currency()) {
							continue;
						}
					}
					if (corpdate.after(begindate)) {
						DZFDouble yearqc = v.getYearqc() == null ? DZFDouble.ZERO_DBL : v.getYearqc();
						kv.setThismonthqc(thismonqc.add(yearqc));
					} else {
						DZFDouble thismonqc1 = v.getThismonthqc() == null ? DZFDouble.ZERO_DBL : v.getThismonthqc();
						kv.setThismonthqc(thismonqc.add(thismonqc1));
						kv.setThismonthqm(thismonqm.add(thismonqc1));
					}
				}
				qcyeVOs = null;
			} else if (min_period.compareTo(jz_max_period) > 0) {// 已结账
				SQLParameter sp = new SQLParameter();
				sp.addParam(pk_corp);
				sp.addParam(jz_max_period);
				KMQMJZVOs = qmjzmap.get(pk_corp).get(jz_max_period).toArray(new KMQMJZVO[0]);
				qcend = begindate.getDateBefore(1);
				qcstart = new DZFDate(jz_max_period + "-01");
				qcstart = new DZFDate(new Date(qcstart.toDate().getYear(), qcstart.toDate().getMonth() + 1, 1));
				len = KMQMJZVOs == null ? 0 : KMQMJZVOs.length;
				KMQMJZVO v = null;
				for (int i = 0; i < len; i++) {
					v = KMQMJZVOs[i];
					kv = getKey(map, v.getPk_accsubj(), min_period,mp);
					DZFDouble thisqcvalue1 = kv.getThismonthqc() == null ? DZFDouble.ZERO_DBL : kv.getThismonthqc();
					DZFDouble thisqmvalue1 = kv.getThismonthqm() == null ? DZFDouble.ZERO_DBL : kv.getThismonthqm();
					DZFDouble thisqmvalue2 = v.getThismonthqm() == null ? DZFDouble.ZERO_DBL : v.getThismonthqm();
					kv.setThismonthqc(thisqcvalue1.add(thisqmvalue2));
					kv.setThismonthqm(thisqmvalue1.add(thisqmvalue2));
				}
				KMQMJZVOs = null;
				upTotal(min_period, mp, map);
			} else {// 已结账
				SQLParameter parameterqc = new SQLParameter();
				parameterqc.addParam(pk_corp);
				String nextperiod = String.valueOf((Integer.parseInt(jz_max_period.substring(0, 4)) + 1));
				parameterqc.addParam(nextperiod + "-12");
				parameterqc.addParam(nextperiod + "-12");
				List<KMQMJZVO> listqcye = qmjzmap.get(pk_corp).get(nextperiod + "-12");
				KMQMJZVOs = listqcye.toArray(new KMQMJZVO[0]);
				len = KMQMJZVOs == null ? 0 : KMQMJZVOs.length;
				KMQMJZVO v = null;
				for (int i = 0; i < len; i++) {
					v = KMQMJZVOs[i];
					kv = getKey(map, v.getPk_accsubj(), nextperiod + "-01",mp);
					DZFDouble thisqcvalue1 = kv.getThismonthqc() == null ? DZFDouble.ZERO_DBL : kv.getThismonthqc();
					DZFDouble thisqmvalue1 = kv.getThismonthqm() == null ? DZFDouble.ZERO_DBL : kv.getThismonthqm();
					DZFDouble thisqcvalue2 = v.getThismonthqc() == null ? DZFDouble.ZERO_DBL : v.getThismonthqc();
					kv.setThismonthqc(thisqcvalue1.add(thisqcvalue2));
					kv.setThismonthqm(thisqmvalue1.add(thisqcvalue2));
				}
				KMQMJZVOs = null;
				upTotal(nextperiod + "-01", mp, map);
			}
			DZFDouble ufd = null;
			boolean bb = qcstart != null || qcend != null;
			if (bb && qcstart != null && qcend != null) {
				bb = qcstart.compareTo(qcend) < 0;
			}
			if (bb) {// 期初
				List<KmZzVO> vec0 = null;
				vec0 = getQCKmFSByPeriod(pk_corp, DZFBoolean.FALSE, DZFBoolean.TRUE, qcstart, qcend, "");
				sumFsToQC(min_period, mp, map, vec0);
			}

			HashMap<String, HashMap<String, List<KmMxZVO>>> fsmap = new HashMap<String, HashMap<String, List<KmMxZVO>>>();
			List<KmMxZVO> vec = null;
			if (corpdate.getYear() > begindate.getYear()
					|| (corpdate.getYear() == begindate.getYear() && corpdate.getMonth() > begindate.getMonth())) {
				vec = getKmFSByPeriodQC(pk_corp, begindate, enddate, " ");
			} else {
				vec = getKmFSByPeriod(pk_corp, begindate, enddate, " ");
			}
			len = vec == null ? 0 : vec.size();
			KmMxZVO vv = null;
			List<String> lsp = null;
			int len1 = 0;
			for (int i = 0; i < len; i++) {
				vv = vec.get(i);
				lsp = getKmParent(mp, vv.getKm());
				len1 = lsp == null ? 0 : lsp.size();
				kv = getKey(map, vv.getKm(), DateUtils.getPeriod(new DZFDate(vv.getRq())),mp);
				ufd = kv.getJffse() == null ? DZFDouble.ZERO_DBL : kv.getJffse();
				ufd = SafeCompute.add(ufd, vv.getJf());
				kv.setJffse(ufd);

				ufd = kv.getDffse() == null ? DZFDouble.ZERO_DBL : kv.getDffse();
				ufd = SafeCompute.add(ufd, vv.getDf());
				kv.setDffse(ufd);
				put(fsmap, vv);
				
				for (int j = 0; j < len1; j++) {
					kv = getKey(map, lsp.get(j), DateUtils.getPeriod(new DZFDate(vv.getRq())),mp);
					ufd = kv.getJffse() == null ? DZFDouble.ZERO_DBL : kv.getJffse();
					ufd = SafeCompute.add(ufd, vv.getJf());
					kv.setJffse(ufd);

					ufd = kv.getDffse() == null ? DZFDouble.ZERO_DBL : kv.getDffse();
					ufd = SafeCompute.add(ufd, vv.getDf());
					kv.setDffse(ufd);
				}
			}
			vec = null;
			List<KmQmJzExtVO> ls = tzDate(map, getPeriods(begindate, enddate).toArray(new String[0]), mp, fsmap);
			
			List<KmQmJzExtVO> ls1 = new ArrayList<KmQmJzExtVO>();
			for(KmQmJzExtVO vo:ls){
				String periodtemp = vo.getPeriod() ;
				if (periodtemp.equals(period) && kmlist.contains(vo.getKmbm())) {
					ls1.add(vo);
				}
			}
			resmap.put(pk_corp, ls1);
		}
		return resmap;
	}
	
	/**
	 * 期末结账时生成科目的期初、期末数
	 * @param corpwherepart
	 * @return
	 */
	private Map<String,Map<String, List<KMQMJZVO>>> getQmJzMap(String corpwherepart) {
		StringBuffer qmjzsql = new StringBuffer();
		qmjzsql.append( "select * from ynt_kmqmjz " );
		qmjzsql.append( " where nvl(dr,0)=0 and  "+corpwherepart);
		List<KMQMJZVO> qclistres =  (List<KMQMJZVO>) singleObjectBO.executeQuery(qmjzsql.toString(), new SQLParameter(), new BeanListProcessor(KMQMJZVO.class));
		Map<String,Map<String, List<KMQMJZVO>>> mapres = new HashMap<String,Map<String, List<KMQMJZVO>>>();
		if(qclistres!=null && qclistres.size()>0){
			for(KMQMJZVO qyvo:qclistres){
				if(mapres.containsKey(qyvo.getPk_corp())){
					if(mapres.get(qyvo.getPk_corp()).containsKey(qyvo.getPeriod())){
						mapres.get(qyvo.getPk_corp()).get(qyvo.getPeriod()).add(qyvo);
					}else{
						List<KMQMJZVO> listtemp = new ArrayList<KMQMJZVO>();
						listtemp.add(qyvo);
						mapres.get(qyvo.getPk_corp()).put(qyvo.getPeriod(), listtemp);
					}
				}else{
					Map<String, List<KMQMJZVO>> maptemp = new HashMap<String, List<KMQMJZVO>>();
					List<KMQMJZVO> listtemp = new ArrayList<KMQMJZVO>();
					listtemp.add(qyvo);
					maptemp.put(qyvo.getPrimaryKey(), listtemp);
					mapres.put(qyvo.getPk_corp(), maptemp);
				}
			}
		}
		return mapres;
	}

	/**
	 * 查询期初余额
	 * @param corpwherepart
	 * @return
	 */
	private Map<String, List<QcYeVO>> getQCMap(String corpwherepart) {
		StringBuffer qcsql = new StringBuffer();
		qcsql.append( "select * from ynt_qcye " );
		qcsql.append( " where nvl(dr,0)=0 and  "+corpwherepart);
		List<QcYeVO> qclistres =  (List<QcYeVO>) singleObjectBO.executeQuery(qcsql.toString(), new SQLParameter(), new BeanListProcessor(QcYeVO.class));
		Map<String,List<QcYeVO>> mapres = new HashMap<String,List<QcYeVO>>();
		if(qclistres!=null && qclistres.size()>0){
			for(QcYeVO qyvo:qclistres){
				if(mapres.containsKey(qyvo.getPk_corp())){
					mapres.get(qyvo.getPk_corp()).add(qyvo);
				}else{
					List<QcYeVO> listtemp = new ArrayList<QcYeVO>();
					listtemp.add(qyvo);
					mapres.put(qyvo.getPk_corp(), listtemp);
				}
			}
		}
		return mapres;
	}

	/**
	 * 获取两个日期之间的期间数
	 * @param begindate
	 * @param enddate
	 * @return
	 */
	private List<String> getPeriods(DZFDate begindate, DZFDate enddate) {
		List<String> vec_periods = new ArrayList<String>();
		int nb = begindate.getYear() * 12 + begindate.getMonth();
		int ne = enddate.getYear() * 12 + enddate.getMonth();
		int year = 0;
		int month = 0;
		for (int i = nb; i <= ne; i++) {
			month = i % 12;
			year = i / 12;
			if (month == 0) {
				month = 12;
				year -= 1;
			}
			vec_periods.add(year + "-" + (month < 10 ? "0" + month : month));
		}
		return vec_periods;
	}

	private List<KmQmJzExtVO> tzDate(HashMap<String, HashMap<String, KmQmJzExtVO>> map, String[] period,
			Map<String, YntCpaccountVO> mp, HashMap<String, HashMap<String, List<KmMxZVO>>> fsmap ) {
		String showperiod = "";
		if(period != null && period.length > 0){
			showperiod = period[0];
		}
		int len = period == null ? 0 : period.length;
		String p = null;
		KmQmJzExtVO v1 = null;
		DZFDouble ufd = null;
		DZFDouble ufdljjf = null;
		DZFDouble ufdljdf = null;
		DZFDouble uft = null;
		int direction = 0;
		int len1 = map.keySet().size();
		List[] lists = new List[len];
		List<KmMxZVO> list = null;
		List<KmMxZVO> listr = new ArrayList<KmMxZVO>();
		String[] keys = map.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		HashMap<String, KmQmJzExtVO> m = null;
		int month = 0;
		YntCpaccountVO kmvo = null;
		//其实只是有一个期间的
		List<KmQmJzExtVO> extvo = new ArrayList<KmQmJzExtVO>();
		for (int i = 0; i < len1; i++) {
			m = map.get(keys[i]);
			ufd = DZFDouble.ZERO_DBL;
			int count = 0;
			for (int j = 0; j < len; j++) {
				p = period[j];
				month = new DZFDate(p + "-01").getMonth();
				v1 = m.get(p);
				if (v1 == null) {
					v1 = KmQmJzExtVO.newInstance();
					v1.setPk_accsubj(keys[i]);
					v1.setPeriod(p);
					if(mp.get(keys[i])!=null){
						v1.setKmbm(mp.get(keys[i]).getAccountcode());
					}
					m.put(p, v1);
				} else {
					count++;
				}
				if (count == 1) {
					ufd = v1.getThismonthqc();
					if (ufd == null)
						ufd = DZFDouble.ZERO_DBL;
					count++;
				}

				v1.setThismonthqc(ufd);
				kmvo = mp.get(v1.getPk_accsubj());
				if (kmvo == null)
					continue;
				direction = kmvo.getDirection();
				if (j == 0) {
					ufdljjf = DZFDouble.ZERO_DBL;
					ufdljdf = DZFDouble.ZERO_DBL;
				}
				if (ufdljjf == null) {
					ufdljjf = DZFDouble.ZERO_DBL;
				}
				if (ufdljdf == null) {
					ufdljdf = DZFDouble.ZERO_DBL;
				}
				if (direction == 0) {
					uft = v1.getJffse();
					if (uft == null)
						uft = DZFDouble.ZERO_DBL;
					ufdljjf = uft.add(ufdljjf);
					ufd = ufd.add(uft);
					uft = v1.getDffse();
					if (uft == null)
						uft = DZFDouble.ZERO_DBL;
					ufdljdf = uft.add(ufdljdf);
					ufd = ufd.sub(uft);
					Map<String, List<KmMxZVO>> resmap1 = fsmap.get(keys[i]);
					if(resmap1!=null && resmap1.keySet().size()>0){
						List<KmMxZVO> listres = resmap1.get(p);
						DZFDouble tempdou = DZFDouble.ZERO_DBL;
						if(listres!=null && listres.size()>0){
							for(KmMxZVO zvo:listres){
								tempdou =tempdou.add(zvo.getJf());
							}
							v1.setJffse(tempdou);
						}
					}
					v1.setThismonthqm(ufd);
					v1.setLjjffse(ufdljjf);
					v1.setLjdffse(ufdljdf);
				} else {
					uft = v1.getDffse();
					if (uft == null)
						uft = DZFDouble.ZERO_DBL;
					ufdljdf = uft.add(ufdljdf);
					ufd = ufd.add(uft);
					uft = v1.getJffse();
					if (uft == null)
						uft = DZFDouble.ZERO_DBL;
					ufdljjf = uft.add(ufdljjf);
					ufd = ufd.sub(uft);
					Map<String, List<KmMxZVO>> resmap1 = fsmap.get(keys[i]);
					if(resmap1!=null && resmap1.keySet().size()>0){
						List<KmMxZVO> listres = resmap1.get(p);
						DZFDouble tempdou = DZFDouble.ZERO_DBL;
						if(listres!=null && listres.size()>0){
							for(KmMxZVO zvo:listres){
								tempdou =tempdou.add(zvo.getDf());
							}
						}
						v1.setDffse(tempdou);
					}
					v1.setThismonthqm(ufd);
					v1.setLjjffse(ufdljjf);
					v1.setLjdffse(ufdljdf);

				}
			}
			
			for(String periodtemp:m.keySet()){
				extvo.add(m.get(periodtemp));
			}
		}
		
		// 科目结果集过滤
		return  extvo;
	}
	

	private void put(HashMap<String, HashMap<String, List<KmMxZVO>>> fsmap, KmMxZVO vo) {
		HashMap<String, List<KmMxZVO>> obj = fsmap.get(vo.getKm());
		if (obj == null) {
			obj = new HashMap<String, List<KmMxZVO>>();
			fsmap.put(vo.getKm(), obj);
		}
		String p = DateUtils.getPeriod(new DZFDate(vo.getRq()));
		List<KmMxZVO> l = obj.get(p);
		if (l == null) {
			l = new ArrayList<KmMxZVO>();
			obj.put(p, l);
		}
		l.add(vo);
	}

	@SuppressWarnings("rawtypes")
	public List<KmMxZVO> getKmFSByPeriod(String pk_corp, DZFDate start, DZFDate end, String kmwhere)
			throws DZFWarpException {
		SQLParameter parameter = new SQLParameter();
		String sql1 = getQuerySqlByPeriod(start, end, kmwhere, pk_corp, parameter);
		ArrayList result1 = (ArrayList) singleObjectBO.executeQuery(sql1, parameter,
				new BeanListProcessor(KmMxZVO.class));

		List<KmMxZVO> vec_details = new ArrayList<KmMxZVO>();
		DZFDouble ljJF = DZFDouble.ZERO_DBL;// 累计借方
		DZFDouble ljDF = DZFDouble.ZERO_DBL;// 累计贷方
		if (result1 != null && !result1.isEmpty()) {
			for (Object o : result1) {
				KmMxZVO vo = (KmMxZVO) o;
				vo.setJf(vo.getJf() == null ? DZFDouble.ZERO_DBL : vo.getJf());
				vo.setDf(vo.getDf() == null ? DZFDouble.ZERO_DBL : vo.getDf());
				ljJF = ljJF.add(vo.getJf());
				ljDF = ljDF.add(vo.getDf());
				if ("0".equals(vo.getFx())) {
					// 借方
					vo.setFx("借");
				} else {
					// 贷方
					vo.setFx("贷");
				}
				vec_details.add(vo);
			}
		}
		return vec_details;
	}

	protected String getQuerySqlByPeriod(DZFDate start, DZFDate end, String kmwhere, String pk_corp,
			SQLParameter parameter) {
		StringBuffer sb = new StringBuffer();
		sb.append("select h.period as qj,  ") ;
		sb.append("       h.doperatedate as rq,  ") ; 
		sb.append("       h.pzh as pzh,  ") ; 
		sb.append("       a.accountcode,  ") ; 
		sb.append("       a.pk_corp_account as km,  ") ; 
		sb.append("       b.zy,  ") ; 
		sb.append("       b.pk_currency as bz,  ") ; 
		sb.append("       b.jfmny as jf,  ") ; 
		sb.append("       b.dfmny as df,  ") ; 
		sb.append("       a.direction as fx,  ") ; 
		sb.append("       b.pk_tzpz_h  ") ; 
		sb.append("  from ynt_tzpz_b b  ") ; 
		sb.append(" inner join ynt_tzpz_h h on b.pk_tzpz_h = h.pk_tzpz_h  ") ; 
		sb.append(" inner join ynt_cpaccount a on b.pk_accsubj = a.pk_corp_account  ") ; 
		sb.append(" where nvl(h.dr, 0) = 0  ") ; 
		sb.append("   and nvl(b.dr, 0) = 0  ");
		Date dd = start.toDate();
		dd = new Date(dd.getYear(), dd.getMonth(), 1);
		DZFDate d1 = new DZFDate(dd);
		sb.append(" and h.doperatedate>='").append(d1);
		dd = end.toDate();
		dd = new Date(dd.getYear(), dd.getMonth(), end.getDaysMonth());
		d1 = new DZFDate(dd);
		sb.append("' and h.doperatedate<='").append(d1).append("'");

		if (!StringUtil.isEmptyWithTrim(kmwhere)) {
			sb.append("  ").append(kmwhere);
		}
		parameter.addParam(pk_corp);
		sb.append(" and h.pk_corp= ?");

		sb.append(" order by h.pzh asc ");
		return sb.toString();
	}

	/**
	 * 包含期初
	 * 
	 * @param start
	 * @param end
	 * @param kmwhere
	 * @param pk_corp
	 * @param ishasjz
	 * @param ishassh
	 * @return
	 */
	protected String getQuerySqlByPeriodForQC(DZFDate start, DZFDate end, String kmwhere, String pk_corp,
			SQLParameter parameter) {
		StringBuffer sb = new StringBuffer();

		sb.append(
				"select * from (select h.period as qj,h.doperatedate as rq,h.pzh as pzh, a.accountcode,a.pk_corp_account as km ,b.zy ,b.pk_currency as bz ,b.jfmny as jf ,b.dfmny as df ,a.direction as fx,b.pk_tzpz_h ");
		sb.append(
				" from ynt_tzpz_b b inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h inner join  ynt_cpaccount a  on b.pk_accsubj=a.pk_corp_account where nvl(h.dr,0)=0 and nvl(b.dr,0)=0 ");
		Date dd = start.toDate();
		dd = new Date(dd.getYear(), dd.getMonth(), 1);
		DZFDate d1 = new DZFDate(dd);
		sb.append(" and h.doperatedate>='").append(d1);
		dd = end.toDate();
		dd = new Date(dd.getYear(), dd.getMonth(), end.getDaysMonth());
		d1 = new DZFDate(dd);
		sb.append("' and h.doperatedate<='").append(d1).append("'");

		if (!StringUtil.isEmptyWithTrim(kmwhere)) {
			sb.append(" ").append(kmwhere);
		}
		parameter.addParam(pk_corp);
		sb.append(" and h.pk_corp=?");

		sb.append(" order by h.pzh asc  )  b3 ");

		String qcyesql = "union all  select substr(b.doperatedate, 1, 4)||'-01' as qj, substr(b.doperatedate, 1, 4)||'-01-01' as rq, ''as pzh,a.accountcode,a.pk_corp_account as km,'' as zy,'' as  bz,"
				+ " b.yearjffse as jf, b.yeardffse as df,"
				+ " 1 as fx ,'' as  pk_tzpz_h from YNT_QCYE b inner join ynt_cpaccount a  on b.pk_accsubj = a.pk_corp_account and a.isleaf='Y' where (1=1) "
				+ kmwhere + " and b.pk_corp='" + pk_corp + "' and nvl(b.dr,0)=0 ";

		return sb.toString() + qcyesql.toString();
	}

	public List<KmMxZVO> getKmFSByPeriodQC(String pk_corp, DZFDate start, DZFDate end, String kmwhere)
			throws DZFWarpException {
		SQLParameter parameter = new SQLParameter();
		String sql1 = getQuerySqlByPeriodForQC(start, end, kmwhere, pk_corp, parameter);
		ArrayList result1 = (ArrayList) singleObjectBO.executeQuery(sql1, parameter,
				new BeanListProcessor(KmMxZVO.class));

		List<KmMxZVO> vec_details = new ArrayList<KmMxZVO>();
		DZFDouble ljJF = DZFDouble.ZERO_DBL;// 累计借方
		DZFDouble ljDF = DZFDouble.ZERO_DBL;// 累计贷方
		if (result1 != null && !result1.isEmpty()) {
			for (Object o : result1) {
				KmMxZVO vo = (KmMxZVO) o;

				if (vo.getJf() == null && vo.getDf() == null) {
					continue;
				}
				vo.setJf(vo.getJf() == null ? DZFDouble.ZERO_DBL : vo.getJf());
				vo.setDf(vo.getDf() == null ? DZFDouble.ZERO_DBL : vo.getDf());
				ljJF = ljJF.add(vo.getJf());
				ljDF = ljDF.add(vo.getDf());
				if ("0".equals(vo.getFx())) {
					// 借方
					vo.setFx("借");

					// vo.setYe(ye.add(ljJF).sub(ljDF)) ;

				} else {
					// 贷方
					vo.setFx("贷");
					// vo.setYe(ye.add(ljDF).sub(ljJF)) ;
				}

				vec_details.add(vo);
			}
		}
		return vec_details;
	}

	public List<KmZzVO> getQCKmFSByPeriod(String pk_corp, DZFBoolean ishasjz, DZFBoolean ishassh, DZFDate start,
			DZFDate end, String kmwhere) throws DZFWarpException {
		if (start == null && end == null)
			return null;
		StringBuffer sb = new StringBuffer();
		sb.append(" select sum(b.jfmny) as jf ,sum(b.dfmny) as df, b.pk_accsubj as km ");
		sb.append(
				" from ynt_tzpz_b b inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h inner join  ynt_cpaccount a  on b.pk_accsubj=a.pk_corp_account where nvl(b.dr,0)=0 ");
		Date dd = null;
		if (start != null) {
			dd = start.toDate();
			dd = new Date(dd.getYear(), dd.getMonth(), 1);
			DZFDate d1 = new DZFDate(dd);
			sb.append(" and h.doperatedate>='").append(d1).append("'");
		}
		if (end != null) {
			dd = end.toDate();
			dd = new Date(dd.getYear(), dd.getMonth(), end.getDaysMonth());
			DZFDate d1 = new DZFDate(dd);
			sb.append(" and h.doperatedate<='").append(d1).append("'");
		}
		if (!StringUtil.isEmptyWithTrim(kmwhere)) {
			// sb.append(" and b.pk_accsubj in ").append(kmwhere) ;//zpm
			sb.append("   ").append(kmwhere);
		}

		sb.append(" and h.pk_corp='" + pk_corp + "'");

		if (ishasjz.booleanValue()) {
			// 不包含未记账，即只查询已记账的
			sb.append(" and h.ishasjz='Y' ");
			sb.append(" and h.vbillstatus=1 ");
		}
		sb.append(" group by b.pk_accsubj ");
		sb.append(" order by b.pk_accsubj ");
		SQLParameter parameter = new SQLParameter();

		return (List<KmZzVO>) singleObjectBO.executeQuery(sb.toString(), parameter,
				new BeanListProcessor(KmZzVO.class));

	}

	/**
	 * 通过发生补足部分科目期初数据，先生成当前科目期初，之后向上累加发生
	 * @param min_period
	 * @param mp
	 * @param map
	 * @param vec0
	 */
	private void sumFsToQC(String min_period, Map<String, YntCpaccountVO> mp,
			HashMap<String, HashMap<String, KmQmJzExtVO>> map, List<KmZzVO> vec0) {
		int len = vec0 == null ? 0 : vec0.size();
		int len1 = 0;
		KmZzVO v = null;
		int ntemp = 0;
		KmQmJzExtVO kv = null;
		DZFDouble ufd = null;
		DZFDouble ufd1 = null;
		List<String> plist = null;
		String kmpk = null;
		for (int i = 0; i < len; i++) {
			v = vec0.get(i);
			kv = getKey(map, v.getKm(), min_period,mp);
			// getKmParent(mp, v.getPk_accsubj());
			kmpk = v.getKm();
			plist = getKmParent(mp, kmpk);
			if(mp.get(kmpk) != null){
				Integer fxleaf = mp.get(kmpk).getDirection();
				ufd = kv.getThismonthqc();
				if (ufd == null)
					ufd = DZFDouble.ZERO_DBL;
				ntemp = mp.get(v.getKm()).getDirection();
				if (ntemp == 0) {
					ufd1 = SafeCompute.sub(v.getJf(), v.getDf());
					ufd = ufd.add(ufd1);
				} else {
					ufd1 = SafeCompute.sub(v.getDf(), v.getJf());
					ufd = ufd.add(ufd1);
				}
				kv.setThismonthqc(ufd);
				kv.setThismonthqm(ufd);
				len1 = plist == null ? 0 : plist.size();
				for (int j = 0; j < len1; j++) {
					kmpk = plist.get(j);
					kv = getKey(map, kmpk, min_period,mp);
					Integer pafx = mp.get(kmpk).getDirection();
					ufd = kv.getThismonthqc() == null ? DZFDouble.ZERO_DBL : kv.getThismonthqc();
					if (pafx.intValue() != fxleaf.intValue()) {
						ufd = SafeCompute.sub(ufd, ufd1);
					} else {
						ufd = SafeCompute.add(ufd, ufd1);
					}
					kv.setThismonthqc(ufd);

					ufd = kv.getThismonthqm() == null ? DZFDouble.ZERO_DBL : kv.getThismonthqm();
					if (pafx.intValue() != fxleaf.intValue()) {
						ufd = SafeCompute.sub(ufd, ufd1);
					} else {
						ufd = SafeCompute.add(ufd, ufd1);
					}
					kv.setThismonthqm(ufd);
				}
			}
			

		}

	}

	private void upTotal(String min_period, Map<String, YntCpaccountVO> mp,
			HashMap<String, HashMap<String, KmQmJzExtVO>> map) {
		List<YntCpaccountVO> leaflist = new ArrayList<YntCpaccountVO>();
		for (YntCpaccountVO YntCpaccountVO : mp.values()) {
			if (YntCpaccountVO.getIsleaf().booleanValue()) {
				leaflist.add(YntCpaccountVO);
			}
		}
		KmQmJzExtVO kvoleaf = null;
		KmQmJzExtVO kvo = null;
		List<String> plist = null;
		DZFDouble ufd = null;
		String kmpk = null;
		int len = 0;
		HashMap<String, KmQmJzExtVO> hm = null;
		for (YntCpaccountVO bvo : leaflist) {
			kmpk = bvo.getPk_corp_account();
			hm = map.get(kmpk);
			if (hm == null)
				continue;
			kvoleaf = hm.get(min_period);
			Integer fxleaf = mp.get(kmpk).getDirection();
			plist = getKmParent(mp, kmpk);
			if (plist == null)
				continue;
			len = plist == null ? 0 : plist.size();
			for (int i = 0; i < len; i++) {
				kmpk = plist.get(i);
				kvo = getKey(map, kmpk, min_period,mp);
				ufd = kvo.getThismonthqc() == null ? DZFDouble.ZERO_DBL : kvo.getThismonthqc();
				// 判断下方向，如果方向不一样不是加应该是减
				Integer pafx = mp.get(kmpk).getDirection();
				if (pafx.intValue() != fxleaf.intValue()) {
					ufd = SafeCompute.sub(ufd, kvoleaf.getThismonthqc());
				} else {
					ufd = SafeCompute.add(ufd, kvoleaf.getThismonthqc());
				}
				kvo.setThismonthqc(ufd);

				ufd = kvo.getThismonthqm() == null ? DZFDouble.ZERO_DBL : kvo.getThismonthqm();
				if (pafx.intValue() != fxleaf.intValue()) {
					ufd = SafeCompute.sub(ufd, kvoleaf.getThismonthqm());
				} else {
					ufd = SafeCompute.add(ufd, kvoleaf.getThismonthqm());
				}

				kvo.setThismonthqm(ufd);
			}

		}

	}

	/**
	 * 获取科目的上级科目
	 * @param mp
	 * @param kmpk
	 * @return
	 */
	private List<String> getKmParent(Map<String, YntCpaccountVO> mp, String kmpk) {
		YntCpaccountVO vo1 = mp.get(kmpk);
		List<String> ls = new ArrayList<String>();
		for (YntCpaccountVO vo : mp.values()) {
			String code = (vo1 == null || vo1.getAccountcode() == null) ? "" : vo1.getAccountcode();
			if (code.startsWith(vo.getAccountcode())
					&& code.length() > vo.getAccountcode().length()) {
				ls.add(vo.getPk_corp_account());
			}
		}
		return ls;
	}

	/**
	 * 期末结账时生成科目的期初、期末数
	 * @param map
	 * @param pk_accsubj
	 * @param period
	 * @param mp
	 * @return
	 */
	private KmQmJzExtVO getKey(HashMap<String, HashMap<String, KmQmJzExtVO>> map, String pk_accsubj, String period,
			Map<String, YntCpaccountVO> mp) {
		HashMap<String, KmQmJzExtVO> m = map.get(pk_accsubj);
		if (m == null) {
			m = new HashMap<String, KmQmJzExtVO>();
			map.put(pk_accsubj, m);
		}
		KmQmJzExtVO v = m.get(period);
		if (v == null) {
			v = KmQmJzExtVO.newInstance();
			v.setPeriod(period);
			v.setPk_accsubj(pk_accsubj);
			if (mp.get(pk_accsubj) != null) {
				v.setKmbm(mp.get(pk_accsubj).getAccountcode());
			}
			m.put(period, v);
		}
		return v;
	}

	/**
	 * 获取期初余额
	 * @param qcyeVOs
	 * @param mpz
	 * @return
	 */
	public QcYeVO[] getLastVO(QcYeVO[] qcyeVOs, Map<String, YntCpaccountVO> mpz) {
		if (qcyeVOs == null || qcyeVOs.length == 0 || mpz == null || mpz.size() == 0)
			return null;
		Map<String, YntCpaccountVO> mp = new HashMap<String, YntCpaccountVO>();
		mp.putAll(mpz);
		List<QcYeVO> list = new ArrayList<QcYeVO>();
		for (QcYeVO c : qcyeVOs) {
			String pksubj = c.getPk_accsubj();
			mp.remove(pksubj);
			list.add(c);
		}
		Collection<YntCpaccountVO> c = mp.values();
		if (c != null && c.size() > 0) {
			for (YntCpaccountVO v : c) {
				QcYeVO vs = new QcYeVO();
				vs.setPk_accsubj(v.getPk_corp_account());
				list.add(vs);
			}
		}
		return list.toArray(new QcYeVO[0]);
	}

	/**
	 * 查询客户科目信息
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, Map<String, YntCpaccountVO>> getKm(String[] pk_corp) throws DZFWarpException {

		Map<String, Map<String, YntCpaccountVO>> reskm = new HashMap<String, Map<String, YntCpaccountVO>>();
		StringBuffer corpstr = new StringBuffer();
		for (String corp : pk_corp) {
			corpstr.append("'" + corp + "',");
		}
		YntCpaccountVO[] yntkms = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class,
				" nvl(dr,0)=0 and pk_corp in (" + corpstr.substring(0, corpstr.length() - 1) + ")", new SQLParameter());
		for (YntCpaccountVO kmtemp : yntkms) {
			if (reskm.containsKey(kmtemp.getPk_corp())) {
				Map<String, YntCpaccountVO> tempmap = reskm.get(kmtemp.getPk_corp());
				tempmap.put(kmtemp.getPk_corp_account(), kmtemp);
				reskm.put(kmtemp.getPk_corp(), tempmap);
			} else {
				Map<String, YntCpaccountVO> tempmap = new HashMap<String, YntCpaccountVO>();
				tempmap.put(kmtemp.getPk_corp_account(), kmtemp);
				reskm.put(kmtemp.getPk_corp(), tempmap);
			}
		}
		return reskm;
	}

	/**
	 * 取年结的最大期间
	 * @param corpwhere
	 * @param enddate
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String,String> getMaxMap(String corpwhere, DZFDate enddate) throws DZFWarpException {
		String period = DateUtils.getPeriod(enddate);
		StringBuffer sql= new StringBuffer();
		sql.append( " select pk_corp ,max(period) from (" );
		sql.append( "   select period,pk_corp from ynt_qmjz " );
		sql.append( "   where period<'" + period + "'   and "+ corpwhere +" and nvl(jzfinish,'N')='Y' and nvl(dr,0)=0 ) group by pk_corp  ");
		List<Object[]> obj =  (ArrayList<Object[]>) singleObjectBO.executeQuery(sql.toString(), new SQLParameter(), new ArrayListProcessor());

		Map<String,String> maxmap = new HashMap<String,String>();
		if (obj != null && obj.size() > 0) {
			for(Object[] objtemp:obj){
				maxmap.put((String)objtemp[0], (String)objtemp[1]);
			}
		}
		return maxmap;
	}
	
	private String getMAXPeriod(String pk_corp, DZFDate enddate) throws DZFWarpException {
		String period = DateUtils.getPeriod(enddate);
		String sql = "select max(period) from YNT_QMJZ where pk_corp='" + pk_corp + "' and period<'" + period
				+ "' and nvl(jzfinish,'N')='Y' and nvl(dr,0)=0 ";
		SQLParameter parameter = new SQLParameter();
		Object[] obj = (Object[]) singleObjectBO.executeQuery(sql, parameter, new ArrayProcessor());

		if (obj != null && obj.length > 0) {
			String i = (String) obj[0];
			return i;
		}
		return null;
	}

}

