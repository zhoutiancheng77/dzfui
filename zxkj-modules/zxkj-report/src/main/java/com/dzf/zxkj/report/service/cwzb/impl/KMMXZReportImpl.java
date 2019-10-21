package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;
import com.dzf.zxkj.platform.model.bdset.GxhszVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.jzcl.KMQMJZVO;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.model.report.KmQmJzExtVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwzb.IKMMXZReport;
import com.dzf.zxkj.report.service.cwzb.IKmMxZReportForWb;
import com.dzf.zxkj.report.utils.BeanUtils;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.dzf.zxkj.report.utils.VoUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 科目明细查询后台类
 * 
 * @author zhangj
 * 
 */
@Service("gl_rep_kmmxjserv")
@SuppressWarnings("all")
public class KMMXZReportImpl implements IKMMXZReport {
	@Autowired
	private SingleObjectBO singleObjectBO = null;

	@Autowired
	private IKmMxZReportForWb gl_rep_kmmxjservwb = null;

	@Reference(version = "2.0.0")
	private IZxkjPlatformService zxkjPlatformService;

	private KmQmJzExtVO getKey(HashMap<String, HashMap<String, KmQmJzExtVO>> map,
							   String pk_accsubj, String period) {
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
			m.put(period, v);
		}
		return v;
	}

	public KmMxZVO[] getKMMXZVOs(QueryParamVO vo, Object[] qryobjs) throws DZFWarpException {
		Object[] obj ;
		if(qryobjs == null ||  qryobjs.length ==0){
			obj = getKMMXZVOs1(vo, false);
		}else{
			obj = qryobjs;
		}
		List[] lists = (List[]) obj[0];
		int len = lists == null ? 0 : lists.length;
		KmMxZVO[] kvos = (KmMxZVO[]) ((len > 0) ? lists[0].toArray(new KmMxZVO[0]) : null);
		/** 重新排序 */
		if(kvos!=null && kvos.length > 0){
			Arrays.sort(kvos, new Comparator<KmMxZVO>() {
				private int get(KmMxZVO o1) {
					int i = 0;
					if (o1.getZy() == null)
						i = 2;
					else if (o1.getZy().equals("期初余额") && ReportUtil.bSysZy(o1))
						i = 1;
					else if (o1.getZy().equals("本月合计")  && ReportUtil.bSysZy(o1))
						i = 3;
					else if (o1.getZy().equals("本年累计")  && ReportUtil.bSysZy(o1))
						i = 4;
					else
						i = 2;
					return i;
				}

				public int compare(KmMxZVO o1, KmMxZVO o2) {
					int i = o1.getKmbm().substring(0, 4).compareTo(o2.getKmbm().substring(0, 4));
					if (i == 0) {
						String s1 = o1.getRq().substring(0, 7);
						String s2 = o2.getRq().substring(0, 7);
						int i1 = s1.length();
						int i2 = s2.length();
						if (i1 == i2) {
							i = s1.compareTo(s2);
						} else {
							int i0 = Math.min(i1, i2);
							if (i0 != i1) {
								s1 = s1.substring(0, i0);
							}
							if (i0 != i2) {
								s2 = s2.substring(0, i0);
							}
							i = s1.compareTo(s2);
						}

					}
					if (i == 0) {
						if (o1.getKmbm().length() == o2.getKmbm().length()) {
							if (o1.getKmbm().equals(o2.getKmbm())) {
								Integer i1 = get(o1);
								Integer i2 = get(o2);
								i = i1.compareTo(i2);
							} else {
								i = o1.getKmbm().compareTo(o2.getKmbm());
							}
						} else {
							i = o1.getKmbm().compareTo(o2.getKmbm());
						}
					}

					return i;
				}
			});
		}
		return putPageValue(kvos, vo.getCjq(), vo.getCjz(),obj , vo.getBegindate1(),vo.getEnddate(), vo.getKms_last() , vo.getIsleaf());
	}

	/**
	 * 给map赋值--同时根据层级过滤，不能写后台，容易出问题
	 *
	 * @param resvo
	 */
	private KmMxZVO[] putPageValue(KmMxZVO[] resvo, Integer cjq, Integer cjz, Object[] obj, DZFDate begdate, DZFDate enddate, String kmslast, DZFBoolean isleaf) {

		Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>)obj[1];

		HashMap<String, HashMap<String,List<KmMxZVO>>> vecmap = (HashMap<String, HashMap<String,List<KmMxZVO>>>)obj[2];

		List<KmMxZVO> listkm = new ArrayList<KmMxZVO>();
		cjq = cjq ==null?1:cjq;
		cjz = cjz ==null?6:cjz;
		YntCpaccountVO cpavo = null;
		if (resvo != null && resvo.length > 0 ) {
			for (KmMxZVO zvo : resvo) {
				zvo.setRq(zvo.getRq() + (zvo.getDay() == null ? "" : zvo.getDay()));
				/** 判断层级 */
				if (zvo.getLevel().intValue() >= cjq.intValue() && (cjz == null || zvo.getLevel().intValue() <= cjz.intValue()) ) {
					/** 判断是否末级 */
					cpavo = mp.get(zvo.getPk_accsubj().substring(0, 24));
					/** 如果是否末级是Y */
					if(isleaf!=null &&isleaf.booleanValue()){
						if(cpavo.getIsleaf()!=null && cpavo.getIsleaf().booleanValue()){
							listkm.add(zvo);
						}
					}else{
						listkm.add(zvo);
					}
				}
			}
		}

		List<KmMxZVO> listkmres = new ArrayList<KmMxZVO>();
		for(KmMxZVO zvo:listkm){
			YntCpaccountVO cpacorpvo = null;
			cpacorpvo =  mp.get(zvo.getPk_accsubj().substring(0, 24));
			zvo.setKmfullname(cpacorpvo!=null?cpacorpvo.getFullname():zvo.getKm());
			listkmres.add(zvo);
			String key1 = zvo.getRq().substring(0, 7);
			String key2 = zvo.getKmbm();
			HashMap<String, List<KmMxZVO>> submap = vecmap.get(key1);
			if("期初余额".equals(zvo.getZy())  && ReportUtil.bSysZy(zvo) ){
				DZFDouble ye = DZFDouble.getUFDouble(zvo.getYe());
				if(submap!=null && submap.keySet().size()>0){
					List<KmMxZVO> sortlist = new ArrayList<KmMxZVO>();
					List<KmMxZVO> listsubtemp = null;
					for(String keytemp:submap.keySet()){
						listsubtemp = submap.get(keytemp);
						if(keytemp.equals(key2)){
							continue;
						}else if(keytemp.startsWith(key2)){
							for(KmMxZVO subzvo:listsubtemp){
								if(StringUtil.isEmpty(subzvo.getPk_accsubj())){
									continue;
								}
								sortlist.add(subzvo);
							}
						}
					}
					if(sortlist.size()>0){
						/** 集合排序 */
						KmMxZVO[] kmmxzvos= sortlist.toArray(new KmMxZVO[0]);
						/** 同一个凭证号合在一起 */
						Arrays.sort(kmmxzvos, new Comparator<KmMxZVO>() {
							@Override
							public int compare(KmMxZVO o1, KmMxZVO o2) {
								int i = 0;
								DZFDate d1 = new DZFDate(o1.getRq());
								DZFDate d2 = new DZFDate(o2.getRq());

								i = d1.toString().compareTo(d2.toString());
								if(i == 0 && !StringUtil.isEmpty(o1.getPzh()) && !StringUtil.isEmpty(o2.getPzh())){
									i = o1.getPzh().compareTo(o2.getPzh());
								}
								return i;
							}
						});
						List<KmMxZVO> resmxlist = new ArrayList<KmMxZVO>();
						DZFBoolean isconpzh = DZFBoolean.TRUE;
						for(KmMxZVO subzvo:kmmxzvos){
							 cpacorpvo =  mp.get(zvo.getPk_accsubj().substring(0, 24));
							if(cpacorpvo.getDirection()!=null && cpacorpvo.getDirection().intValue() ==0){
								ye = ye.add(DZFDouble.getUFDouble(subzvo.getJf())).sub(DZFDouble.getUFDouble(subzvo.getDf()));
							}else if(cpacorpvo.getDirection()!=null && cpacorpvo.getDirection().intValue() ==1){
								ye = ye.add(DZFDouble.getUFDouble(subzvo.getDf())).sub(DZFDouble.getUFDouble(subzvo.getJf()));
							}
							 isconpzh = DZFBoolean.TRUE;

							if(isconpzh.booleanValue()){
								KmMxZVO tempsub = new KmMxZVO();
								BeanUtils.copyProperties(subzvo, tempsub);
								tempsub.setKmbm(zvo.getKmbm());
								tempsub.setKm(zvo.getKm());
								tempsub.setFx(cpacorpvo.getDirection().intValue()==0?"借":"贷");
								tempsub.setPk_accsubj(zvo.getPk_accsubj());
								tempsub.setYe(ye);
								resmxlist.add(tempsub);
							}
						}
						for(KmMxZVO zvotemp: resmxlist){
							 cpacorpvo =  mp.get(zvo.getPk_accsubj().substring(0, 24));
							 zvotemp.setKmfullname(cpacorpvo.getFullname());
							listkmres.add(zvotemp);
						}
					}
				}
			}
		}
		return listkmres.toArray(new KmMxZVO[0]);
	}

	public QcYeVO[] getLastVO(QcYeVO[] qcyeVOs, Map<String, YntCpaccountVO> mpz) {
		if (qcyeVOs == null || qcyeVOs.length == 0 || mpz == null
				|| mpz.size() == 0)
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
			QcYeVO vs =null;
			for (YntCpaccountVO v : c) {
				 vs = new QcYeVO();
				vs.setPk_accsubj(v.getPk_corp_account());
				list.add(vs);
			}
		}
		return list.toArray(new QcYeVO[0]);
	}

	public Object[] getKMMXZVOs1(QueryParamVO vo, boolean b)
			throws DZFWarpException {
		Object[] obj = null;
		if(!StringUtil.isEmptyWithTrim(vo.getPk_currency())){
			obj= gl_rep_kmmxjservwb.getKmMxZVOs2(vo);
		}else{
			obj=getKMMXZVOs2(vo);
		}
		List[] lists = (List[]) obj[0];
		b = false;
		/** 结果集为一个list,按照科目、日期排序 */
		if (b == false) {
			List<KmMxZVO> list = null;
			list = new ArrayList<KmMxZVO>();
			int len = lists == null ? 0 : lists.length;
			for (int j = 0; j < len; j++) {
				if (lists[j] != null) {
					/** 添加期间过滤--优化 */
					if(lists[j]!=null && lists[j].size()>0){
						if(!bStartPeriodBefore((KmMxZVO)(lists[j].get(0)), vo.getBegindate1())){
							list.addAll(lists[j]);
						}
					}
					lists[j] = null;
				}
			}

			KmMxZVO[] kvos = list.toArray(new KmMxZVO[0]);
			list = null;
			Arrays.sort(kvos, new Comparator<KmMxZVO>() {
				private int get(KmMxZVO o1) {
					int i = 0;
					if (o1.getZy() == null)
						i = 2;
					else if (o1.getZy().equals("期初余额")  && ReportUtil.bSysZy(o1))
						i = 1;
					else if (o1.getZy().equals("本月合计")  && ReportUtil.bSysZy(o1))
						i = 3;
					else if (o1.getZy().equals("本年累计")  && ReportUtil.bSysZy(o1) )
						i = 4;
					else
						i = 2;
					return i;
				}

				public int compare(KmMxZVO o1, KmMxZVO o2) {
					int i = o1.getKmbm().compareTo(o2.getKmbm());
					if (i == 0) {
						String s1 = o1.getRq().substring(0, 7);
						String s2 = o2.getRq().substring(0, 7);
						int i1 = s1.length();
						int i2 = s2.length();
						if (i1 == i2) {
							i = s1.compareTo(s2);
						} else {
							int i0 = Math.min(i1, i2);
							if (i0 != i1) {
								s1 = s1.substring(0, i0);
							}
							if (i0 != i2) {
								s2 = s2.substring(0, i0);
							}
							i = s1.compareTo(s2);
						}

					}
					if (i == 0) {
						Integer i1 = get(o1);
						Integer i2 = get(o2);
						i = i1.compareTo(i2);
					}
					return i;
				}
			});
			list = new ArrayList<KmMxZVO>();

			list.addAll(Arrays.asList(kvos));

			/** 循环赋值科目层级(发生额余额表用) */
			Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>) obj[1];
			for (KmMxZVO zvo : list) {
				zvo.setLevel(mp.get(zvo.getPk_accsubj()).getAccountlevel());
			}

			kvos = null;

			/** 科目末级过滤 */
			List<KmMxZVO> reslist = new ArrayList<KmMxZVO>();
			String kmslast = vo.getKms_last();
			if(kmslast!=null && kmslast.trim().length() == 0){
				kmslast = null;
			}

			String[] strs = null;
			if(!StringUtil.isEmpty(vo.getKms())){
				strs=vo.getKms().split(",");
			}

			for(KmMxZVO mxvo:list){
				mxvo.setComparecode(mxvo.getKmbm());/** 排序的编码 */
				if(kmslast == null ||
						(kmslast!=null && (kmslast.compareTo(mxvo.getKmbm())>=0) || mxvo.getKmbm().startsWith(kmslast))){//是否包含末级
					if(strs!=null && strs.length > 0){
						List<String> listtemp = Arrays.asList(strs);
						if(listtemp.contains(mxvo.getKmbm())){
							mxvo.setPk_currency(vo.getPk_currency());
							reslist.add(mxvo);
						}
					}else{
						mxvo.setPk_currency(vo.getPk_currency());
						reslist.add(mxvo);
					}
				}

			}

			obj[0] = new List[] {reslist};
		}
		return obj;
	}

	private Object[] getKMMXZVOs2(QueryParamVO vo) throws DZFWarpException {
		boolean b = false;
		DZFDate begindate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(new DZFDate(vo.getBegindate1().getYear() + "-01-01")));
		CorpVO corpvo=zxkjPlatformService.queryCorpByPk(vo.getPk_corp());
		DZFDate corpdate =corpvo.getBegindate();

		/** 如果查询的日期在建账日期前，则弹出提示框 */
		if (vo.getEnddate() != null && vo.getBegindate1() != null && vo.getEnddate().before(vo.getBegindate1())) {
			throw new BusinessException("查询开始日期，应该在查询结束日期前!");
		}
		if ((vo.getEnddate() != null && vo.getEnddate().before(corpdate))) {
			if (vo.getBegindate1().getYear() < corpdate.getYear()) {
				throw new BusinessException("建账日期不在查询期间内！");
			}
		}
		/**  开始日期之前最大结账期 */
		String jz_max_period = getMAXPeriod(vo.getPk_corp(), begindate);
		String min_period = DateUtils.getPeriod(begindate);
		/** 科目查询条件 */
		String kmwhere = getKmTempTable(vo);
		HashMap<String, HashMap<String, KmQmJzExtVO>> map = new HashMap<String, HashMap<String, KmQmJzExtVO>>();
		String pk_corp = vo.getPk_corp();
		KMQMJZVO[] kmqmjzvos = null;
		DZFDate qcstart = null;
		DZFDate qcend = null;
		int len = 0;
		KMQMJZVO kv = null;
		Map<String, YntCpaccountVO> mp = getKM(pk_corp, kmwhere);
		/** 期初数据（key= 科目key +N个辅助项目key(1~9)） */
		HashMap<String, KmMxZVO> qcfzmap= new HashMap<String, KmMxZVO>();
		/** 期初数据（key= 科目key +N个辅助项目key(1~9)） */
		HashMap<String, DZFDouble[]> corpbegqcmap= new HashMap<String, DZFDouble[]>();
		/** 发生数据（key= 科目key +N个辅助项目key(1~9)） */
		HashMap<String, List<KmMxZVO>> fsfzmap = new HashMap<String, List<KmMxZVO>>();

		Map<String, AuxiliaryAccountBVO> fzmap =  getFzXm(pk_corp);
		/** 期初余额 */
		if (jz_max_period == null || corpdate.after(begindate)) {
			SQLParameter parameterqc = new SQLParameter();
			parameterqc.addParam(vo.getPk_corp());
			List<QcYeVO> listqcye = (List<QcYeVO>) singleObjectBO.retrieveByClause(QcYeVO.class, " nvl(dr,0)=0 and pk_corp=?   ", parameterqc);
			QcYeVO[] qcyeVOs = listqcye.toArray(new QcYeVO[0]);
			qcyeVOs = getLastVO(qcyeVOs, mp);
			qcend = begindate.getDateBefore(1);
			len = qcyeVOs == null ? 0 : qcyeVOs.length;
			QcYeVO v = null;
			if(mp!=null && mp.size()>0){
				for(String keytemp:mp.keySet()){
					getKey(map, keytemp, min_period);
				}
			}
			DZFDouble thismonqc = null;
			DZFDouble thismonqm = null;
			DZFDouble thismonqc1 = null;
			DZFDouble yearqc = null;
			for (int i = 0; i < len; i++) {
				v = qcyeVOs[i];
				kv = getKey(map, v.getPk_accsubj(), min_period);
				thismonqc = VoUtils.getDZFDouble(kv.getThismonthqc());
				thismonqm = VoUtils.getDZFDouble(kv.getThismonthqm());
				if (v.getPk_accsubj().equals(kv.getPk_accsubj())) {
					if (!StringUtil.isEmpty(v.getPk_currency())
							&& v.getPk_currency().equals(kv.getPk_currency())) {
						continue;
					}
				}
				if (corpdate.after(begindate)) {
					 yearqc = VoUtils.getDZFDouble(v.getYearqc());
					 kv.setThismonthqc(thismonqc.add(yearqc));
				} else {
					 thismonqc1 = VoUtils.getDZFDouble(v.getThismonthqc() );
					 kv.setThismonthqc(thismonqc.add(thismonqc1));
					 kv.setThismonthqm(thismonqm.add(thismonqc1));
				}
			}
			qcyeVOs = null;
			parameterqc.clearParams();
			parameterqc.addParam(vo.getPk_corp());
			if(vo.getSfzxm()!=null && vo.getSfzxm().booleanValue()){
				FzhsqcVO[] fzhsqcvos =  (FzhsqcVO[]) singleObjectBO.queryByCondition(FzhsqcVO.class," nvl(dr,0)=0 and pk_corp = ?  ", parameterqc);
				putQCFzMap(fzhsqcvos,mp,qcfzmap,corpbegqcmap,fzmap,begindate,corpdate,vo.getRptsource());//期初map 赋值
			}
		}
		/** 已结账 */
		else if (min_period.compareTo(jz_max_period) > 0) {
			String kmwhere1 = "(SELECT a.pk_corp_account FROM ynt_cpaccount a where 1=1 "+ kmwhere + ")";
			SQLParameter sp=  new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(jz_max_period);
			kmqmjzvos = (KMQMJZVO[]) singleObjectBO.queryByCondition(KMQMJZVO.class, "nvl(dr,0)=0 and  pk_accsubj in "+kmwhere1+" and pk_corp=?  and period= ?     and nvl(dr,0)=0  order by period", sp);
			qcend = begindate.getDateBefore(1);
			qcstart = new DZFDate(jz_max_period + "-01");
			qcstart = new DZFDate(new Date(qcstart.toDate().getYear(), qcstart.toDate().getMonth() + 1, 1));
			len = kmqmjzvos == null ? 0 : kmqmjzvos.length;
			KMQMJZVO v = null;
			if(mp!=null && mp.size()>0){
				for(String keytemp:mp.keySet()){
					getKey(map, keytemp, min_period);
				}
			}
			DZFDouble thisqcvalue1 =null;
			DZFDouble thisqmvalue1 =null;
			DZFDouble thisqmvalue2 =null;
			for (int i = 0; i < len; i++) {
				v = kmqmjzvos[i];
				kv = getKey(map, v.getPk_accsubj(), min_period);
				thisqcvalue1 = VoUtils.getDZFDouble(kv.getThismonthqc() );
				thisqmvalue1 = VoUtils.getDZFDouble(kv.getThismonthqm() );
				thisqmvalue2 = VoUtils.getDZFDouble(v.getThismonthqm() );
				kv.setThismonthqc(thisqcvalue1.add(thisqmvalue2));
				kv.setThismonthqm(thisqmvalue1.add(thisqmvalue2));
			}

			if(vo.getSfzxm()!=null && vo.getSfzxm().booleanValue()){
				/** 期初数据赋值*/
				putNJQCFzMap(kmqmjzvos,mp,qcfzmap,fzmap,begindate,corpdate,vo.getRptsource());
			}
			kmqmjzvos = null;
			upTotal(min_period, mp, map);

		}
		else {/** 已结账 */
			String kmwhere1 = "(SELECT a.pk_corp_account FROM ynt_cpaccount a where 1=1  " + kmwhere + ")";
			SQLParameter parameterqc = new SQLParameter();
			parameterqc.addParam(pk_corp);
			String nextperiod =String.valueOf((Integer.parseInt(jz_max_period.substring(0, 4))+1))  ;
			parameterqc.addParam(nextperiod+"-12");
			parameterqc.addParam(nextperiod+"-12");
			List<KMQMJZVO> listqcye = (List<KMQMJZVO>) singleObjectBO .retrieveByClause(KMQMJZVO.class, " nvl(dr,0)=0 and  pk_accsubj in "+kmwhere1+" and pk_corp=? and period>=? and period<=?     and nvl(dr,0)=0  order by period ", parameterqc);
			kmqmjzvos = listqcye.toArray(new KMQMJZVO[0]);
			len = kmqmjzvos == null ? 0 : kmqmjzvos.length;
			KMQMJZVO v = null;
			if(mp!=null && mp.size()>0){
				for(String keytemp:mp.keySet()){
					getKey(map, keytemp, nextperiod+"-01");
				}
			}
			DZFDouble thisqcvalue1 =null;
			DZFDouble thisqmvalue1 =null;
			DZFDouble thisqcvalue2 =null;
			for (int i = 0; i < len; i++) {
				v = kmqmjzvos[i];
				kv = getKey(map, v.getPk_accsubj(),nextperiod+"-01");
				 thisqcvalue1 =  VoUtils.getDZFDouble(kv.getThismonthqc());
				 thisqmvalue1 =  VoUtils.getDZFDouble(kv.getThismonthqm());
				 thisqcvalue2 =  VoUtils.getDZFDouble(v.getThismonthqc());
				kv.setThismonthqc(thisqcvalue1.add(thisqcvalue2));
				kv.setThismonthqm(thisqmvalue1.add(thisqcvalue2));
			}
			if(vo.getSfzxm()!=null && vo.getSfzxm().booleanValue()){
				putNJQCFzMap(kmqmjzvos,mp,qcfzmap,fzmap,begindate,corpdate,vo.getRptsource());//期初数据赋值
			}
			kmqmjzvos = null;
			upTotal(nextperiod+"-01", mp, map);
		}
		DZFDouble ufd = null;

		boolean bb = qcstart != null || qcend != null;
		if (bb && qcstart != null && qcend != null) {
			bb = qcstart.compareTo(qcend) < 0;
		}

		if (bb) {// 期初
			List<KmZzVO> vec0 = null;
			vec0 = getQCKmFSByPeriod(pk_corp, vo.getIshasjz(), vo.getIshassh(), qcstart, qcend, kmwhere);
			sumFsToQC(min_period, mp, map, vec0);

			if(vo.getSfzxm()!=null && vo.getSfzxm().booleanValue()){
				putFsToQCMap(vec0,mp,qcfzmap,fzmap);
			}
		}
		HashMap<String, HashMap<String, List<KmMxZVO>>> fsmap = new HashMap<String, HashMap<String, List<KmMxZVO>>>();
		List<KmMxZVO> vec = null;
		if (corpdate.after(begindate)) {
			vec = getKmFSByPeriodQC(pk_corp, vo.getIshasjz(), vo.getIshassh(),
					begindate, vo.getEnddate(), kmwhere);
		} else {
			vec = getKmFSByPeriod(pk_corp, vo.getIshasjz(), vo.getIshassh(),
					begindate, vo.getEnddate(), kmwhere);
		}
		/** 如果是利润表则过滤掉期间损益的凭证 */
		vec = sourceHandle(vo,vec,mp,corpvo);
		/** 把vec的值 变成一个map赋值用 */
		Map<String,HashMap<String,List<KmMxZVO>>> vecmap = new HashMap<String,HashMap<String,List<KmMxZVO>>>();
		String keytemp1 = null;
		String keytemp2 = null;
		List<KmMxZVO> listtemp =null;
		HashMap<String, List<KmMxZVO>> submap = null;

		StringBuffer key = null;
		String tempkey = null;
		KmMxZVO fzmxzvo = null;
		for(KmMxZVO mxvo:vec){
			keytemp1 = mxvo.getRq().substring(0,7);
			keytemp2 = mp.get(mxvo.getKm()).getAccountcode();
			if(vecmap.containsKey(keytemp1)){
				if(vecmap.get(keytemp1).containsKey(keytemp2)){
					vecmap.get(keytemp1).get(keytemp2).add(mxvo);
				}else{
					 listtemp = new ArrayList<KmMxZVO>();
					listtemp.add(mxvo);
					vecmap.get(keytemp1).put(keytemp2, listtemp);
				}
			}else{
				listtemp = new ArrayList<KmMxZVO>();
				listtemp.add(mxvo);
				submap = new HashMap<String, List<KmMxZVO>>();
				submap.put(keytemp2, listtemp);
				vecmap.put(keytemp1, submap);
			}

			if(vo.getSfzxm()!=null && vo.getSfzxm().booleanValue()){
				key = new StringBuffer();
				key.append(mxvo.getKm()+"_");

				for (int i = 1; i < 11; i++) {
					tempkey = (String) mxvo.getAttributeValue("fzhsx"+i);
					if(!StringUtil.isEmpty(tempkey) && !"0".equals(tempkey)){
						key.append(tempkey+"_");
					}
				}

				key = new StringBuffer(key.subSequence(0, key.length()-1));
				/** 辅助项目处理 */
				if(key.length()>24){
					fzmxzvo= new KmMxZVO();
					BeanUtils.copyProperties(mxvo, fzmxzvo);
					fzmxzvo.setPk_accsubj(key.toString());
					if(fsfzmap.containsKey(key.toString())){
						fsfzmap.get(key.toString()).add(fzmxzvo);
					}else{
						List<KmMxZVO> listtempvo = new ArrayList<KmMxZVO>();
						listtempvo.add(fzmxzvo);
						fsfzmap.put(key.toString(), listtempvo);
					}
				}
			}
		}

		len = vec == null ? 0 : vec.size();
		KmMxZVO vv = null;
		List<String> lsp = null;
		int len1 = 0;
		Set<String>  kmparentlist = new HashSet<String>();
		for (int i = 0; i < len; i++) {
			vv = vec.get(i);
			kmparentlist.add(vv.getKm() + "_" + DateUtils.getPeriod(new DZFDate(vv.getRq())) );
			lsp = ReportUtil.getKmParent(mp, vv.getKm());
			len1 = lsp == null ? 0 : lsp.size();
			kv = getKey(map, vv.getKm(),
					DateUtils.getPeriod(new DZFDate(vv.getRq())));
			ufd = VoUtils.getDZFDouble(kv.getJffse());
			ufd = ufd.add(VoUtils.getDZFDouble(vv.getJf()));
			kv.setJffse(ufd);

			ufd = VoUtils.getDZFDouble(kv.getDffse());
			ufd = ufd.add(VoUtils.getDZFDouble(vv.getDf()));
			kv.setDffse(ufd);
			put(fsmap, vv);


			for (int j = 0; j < len1; j++) {
				kv =  getKey(map, lsp.get(j),vv.getRq().substring(0, 7));
				ufd = VoUtils.getDZFDouble(kv.getJffse());
				ufd = ufd.add(VoUtils.getDZFDouble(vv.getJf()));
				kv.setJffse(ufd);

				ufd = VoUtils.getDZFDouble(kv.getDffse());
				ufd = ufd.add(VoUtils.getDZFDouble(vv.getDf()));
				kv.setDffse(ufd);

				kmparentlist.add(lsp.get(j) + "_" + vv.getRq().substring(0, 7));
			}

		}
		vec = null;
		List[] ls = tzDate(map, ReportUtil.getPeriods(begindate, vo.getEnddate()), mp, fsmap,kmparentlist, vo.getXswyewfs(),
				b,vo.getIshowfs(),vo.getIsnomonthfs() ,vo.getBtotalyear());
		return new Object[] { ls, mp ,vecmap,qcfzmap,fsfzmap,corpbegqcmap };

	}

	private List<KmMxZVO> sourceHandle(QueryParamVO vo, List<KmMxZVO> vec,Map<String, YntCpaccountVO> map,
			CorpVO cpvo) {

		YntCpaccountVO accoutvo = null;
		if(!StringUtil.isEmpty(vo.getRptsource())){
			/** 利润表过滤损益结转的凭证 */
			if("lrb".equals(vo.getRptsource())){
				List<KmMxZVO> tt = new ArrayList<KmMxZVO>();
				Set<String> filterpk = filterKmFromLrb(vec.toArray(new KmMxZVO[0]), map, cpvo);
				//过滤凭证号期间
				YntCpaccountVO cpavo = null;
				for(KmMxZVO mxvo:vec){
					/** 过滤期初*/
					if(mxvo.getBqc()!=null && mxvo.getBqc().booleanValue()){
						cpavo = map.get(mxvo.getKm());
						if(cpavo!=null){
							if(cpavo.getDirection() == 0){
								mxvo.setDf(DZFDouble.ZERO_DBL);
							}else if(cpavo.getDirection() == 1){
								mxvo.setJf(DZFDouble.ZERO_DBL);
							}
						}
						tt.add(mxvo);
					}else{/** 过滤发生 */
						if(!filterpk.contains(mxvo.getPk_tzpz_h())){
							tt.add(mxvo);
						}
					}
				}
				return tt;
			}
		}
		return vec;
	}

	private Set<String> filterKmFromLrb(SuperVO[] vec, Map<String, YntCpaccountVO> map, CorpVO cpvo) {
		YntCpaccountVO accoutvo;
		Set<String> filterpk= new HashSet<String>();
		/** 先找到对应的凭证号和期间 */
		String pk_tzpz_h = "";
		String km = "";
		if(vec!=null && vec.length>0){
			for(SuperVO mxvo:vec){
				pk_tzpz_h = (String) mxvo.getAttributeValue("pk_tzpz_h");
				km = (String) mxvo.getAttributeValue("km");
				if(!StringUtil.isEmpty(pk_tzpz_h) && !StringUtil.isEmpty(km)){
					accoutvo = map.get(km);
					if(accoutvo == null){
						continue;
					}
					String startkm = "";
					if("00000100AA10000000000BMD".equals(cpvo.getCorptype())){//13
						startkm = "3103";
					}else if("00000100AA10000000000BMF".equals(cpvo.getCorptype())){//07
						startkm = "4103";
					}else if("00000100000000Ig4yfE0005".equals(cpvo.getCorptype())){//企业会计制度
						startkm = "3131";
					}else{
						break;
					}
					if(accoutvo.getAccountcode().startsWith(startkm)){
						filterpk.add(pk_tzpz_h);
					}
				}
			}
		}
		return filterpk;
	}

	private void put(HashMap<String, HashMap<String, List<KmMxZVO>>> fsmap,
			KmMxZVO vo) {
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

	public Map<String, YntCpaccountVO> getKM(String pk_corp, String kmwhere)
			throws DZFWarpException {
		/** 个性化设置vo */
		GxhszVO myselfset = zxkjPlatformService.queryGxhszVOByPkCorp(pk_corp);
		/** 科目现在方式，默认显示本级 */
		Integer subjectShow  = myselfset.getSubjectShow();
		kmwhere = "(SELECT a.pk_corp_account FROM ynt_cpaccount a where (1=1)   " + kmwhere + ")";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_corp);
		String corpsql = "select * from ynt_cpaccount where nvl(dr,0)=0 and  pk_corp=? and pk_corp_account in " + kmwhere + "";
		List<YntCpaccountVO> bdacorplist = (List<YntCpaccountVO>) singleObjectBO.executeQuery(corpsql, parameter, new BeanListProcessor(YntCpaccountVO.class));
		Collections.sort(bdacorplist, new Comparator<YntCpaccountVO>() {
			@Override
			public int compare(YntCpaccountVO o1, YntCpaccountVO o2) {
				if(StringUtil.isEmpty(o1.getAccountcode())){
					return -1;
				}
				return o1.getAccountcode().compareTo(o2.getAccountcode());
			}
		});
		YntCpaccountVO[] kmVOs = bdacorplist.toArray(new YntCpaccountVO[0]);
		int len = kmVOs == null ? 0 : kmVOs.length;
		Map<String, YntCpaccountVO> mp = new HashMap<String, YntCpaccountVO>();
		for (int i = 0; i < len; i++) {
			/** 显示本级*/
			if(subjectShow.intValue() == 0){

			}else if(subjectShow.intValue() == 1){/** 显示一级+本级 */
				String firstname = "";
				/** 非一级 */
				if (kmVOs[i].getAccountcode().length() > 4){
					for(YntCpaccountVO cpaccVo1 : kmVOs){
						if(cpaccVo1.getAccountcode().equals(kmVOs[i].getAccountcode().substring(0, 4))){
							firstname = cpaccVo1.getAccountname() +"_";
							break;
						}
					}
				}
				kmVOs[i].setAccountname(firstname+kmVOs[i].getAccountname());
			}else {/** 逐级显示 */
				StringBuffer parentfullname = new StringBuffer();
				for(YntCpaccountVO cpaccVo1 : kmVOs){
					if(kmVOs[i].getAccountcode().startsWith(cpaccVo1.getAccountcode())
							&&  ((kmVOs[i].getAccountcode().length() - cpaccVo1.getAccountcode().length())==2
							|| (kmVOs[i].getAccountcode().length() - cpaccVo1.getAccountcode().length())==3 )){
						parentfullname.append(cpaccVo1.getAccountname() + "_" );
						break;
					}
				}
				kmVOs[i].setAccountname(parentfullname.toString() + kmVOs[i].getAccountname());
			}
			mp.put(kmVOs[i].getPk_corp_account(), kmVOs[i]);
		}
		return mp;
	}

	public String getKmTempTable(QueryParamVO vo) {
		String pk_corp = vo.getPk_corp();
		String kms = vo.getKms();
		String kmsx = vo.getKmsx();
		String kms_first = vo.getKms_first();
		String kms_last = vo.getKms_last();
		List<String> firstlevelkms = vo.getFirstlevelkms();
		StringBuffer wherpartfirst = new StringBuffer();
		StringBuffer  kmwhere =new StringBuffer();
		/** 查询所有科目，总账只查询一级科目 */
		String jcSql = "";
		if (vo.getIsLevel().booleanValue()) {
			jcSql = " and accountlevel=1 ";
			if (vo.getIsmj().booleanValue()) {
				/** 只查询末级 */
				jcSql = " and isleaf='Y' ";

			} else if (vo.getLevelq() != null && vo.getLevelz() != null) {
				/** 查询级次 */
				jcSql = " and ( accountlevel>=" + vo.getLevelq() + " and accountlevel<=" + vo.getLevelz() + " ) ";
			}
		}
		String sx="";
		/** 查询所有科目 */
		if (kmsx != null && !"".equals(kmsx)) {
			if (kmsx.indexOf(",") > 0) {
				String kmsxs = kmsx.substring(0, kmsx.length() - 1);
				sx = " and  a.pk_corp='" + pk_corp + "'" + jcSql + " and a.accountkind in (" + kmsxs + ") and nvl(a.dr,0)=0 ";
			} else {
				sx = " and  a.pk_corp='" + pk_corp + "'" + jcSql + " and a.accountkind =" + kmsx + " and nvl(a.dr,0)=0";
			}
		} else {
			sx = " and  a.pk_corp='" + pk_corp + "'" + jcSql + " and nvl(a.dr,0)=0";
		}
		if(kms!=null && kms.length()>0){
			if (kms.indexOf(",") > 0) {
				StringBuffer wherpart1 = new StringBuffer();
				for (int i = 0; i < vo.getKmcodelist().size(); i++) {
					wherpart1.append(" a.accountcode like '" + vo.getKmcodelist().get(i) + "%' or");
				}
				kmwhere.append( " and  (" + wherpart1.toString().substring(0, wherpart1.length() - 2) + ")" + " and nvl(a.dr,0)=0" );
			} else  {/** 查询单个科目 */
				StringBuffer wherpart1 = new StringBuffer();
				wherpart1.append("  a.accountcode like '" + kms + "%' ");
				kmwhere.append(  " and (" + wherpart1.toString() + ")" + " and nvl(a.dr,0)=0"  );
			}
		}
		if(kms_first!=null && kms_first.length() > 0 ){
			wherpartfirst.append("  a.accountcode >= '" + kms_first + "' ");
			kmwhere.append(  " and (" + wherpartfirst.toString() + ")" + " and nvl(a.dr,0)=0");
		}
		if(!StringUtil.isEmpty(kms_last)){
			kmwhere.append(" and substr(a.accountcode,0,4)<='"+kms_last.substring(0, 4)+"'");
		}

		if (firstlevelkms != null && firstlevelkms.size() > 0) {
			kmwhere.append("  and "
					+ SqlUtil.buildSqlForIn(" substr(a.accountcode,0,4)", firstlevelkms.toArray(new String[0])));
		}

		return kmwhere.toString()+sx;
	}

	public List<KmZzVO> getQCKmFSByPeriod(String pk_corp, DZFBoolean ishasjz,
			DZFBoolean ishassh, DZFDate start, DZFDate end, String kmwhere)
			throws DZFWarpException {
		if (start == null && end == null)
			return null;
		StringBuffer sb = new StringBuffer();
		sb.append(" select  b.jfmny   as jf , b.dfmny as df, b.pk_accsubj as km ,h.pk_tzpz_h as pk_tzpz_h,");
		sb.append(" b.fzhsx1, b.fzhsx2,b.fzhsx3,b.fzhsx4,b.fzhsx5,");
	    /** 启用库存  存货作为辅助核算 */
		sb.append(" case when b.fzhsx6 is null then b.pk_inventory else b.fzhsx6 end fzhsx6, ");
		sb.append(" b.fzhsx7,b.fzhsx8,b.fzhsx9,b.fzhsx10 ");
		sb.append(" from ynt_tzpz_b b  ");
		sb.append(" inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h ");
		sb.append(" inner join  ynt_cpaccount a  on b.pk_accsubj=a.pk_corp_account ");
		sb.append("  where nvl(b.dr,0)=0 and nvl(h.dr,0)=0 ");
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
			sb.append("   ").append(kmwhere);
		}

		sb.append(" and h.pk_corp='" + pk_corp + "'");

		SQLParameter parameter = new SQLParameter();
		List<KmZzVO> reslist = (List<KmZzVO>) singleObjectBO.executeQuery(sb.toString(), parameter,
				new BeanListProcessor(KmZzVO.class));
		Collections.sort(reslist, new Comparator<KmZzVO>() {
			@Override
			public int compare(KmZzVO o1, KmZzVO o2) {
				if(StringUtil.isEmpty(o1.getPk_accsubj())){
					return -1;
				}
				return o1.getPk_accsubj().compareTo(o2.getPk_accsubj());
			}
		});
		return reslist;
	}

	public List<KmMxZVO> getKmFSByPeriod(String pk_corp, DZFBoolean ishasjz,
			DZFBoolean ishassh, DZFDate start, DZFDate end, String kmwhere)
			throws DZFWarpException {
		SQLParameter parameter = new SQLParameter();
		String sql1 = getQuerySqlByPeriod(start, end, kmwhere, pk_corp,
				ishasjz, ishassh,parameter);
		ArrayList result1 = (ArrayList) singleObjectBO.executeQuery(sql1,
				parameter, new BeanListProcessor(KmMxZVO.class));
		VOUtil.ascSort(result1, new String[]{"rq","pzh","rowno"});
		List<KmMxZVO> vec_details = new ArrayList<KmMxZVO>();
		/**  累计借方 */
		DZFDouble ljJF = DZFDouble.ZERO_DBL;
		/** 累计贷方 */
		DZFDouble ljDF = DZFDouble.ZERO_DBL;
		if (result1 != null && !result1.isEmpty()) {
			KmMxZVO vo = null;
			for (Object o : result1) {
				vo = (KmMxZVO) o;
				vo.setJf(vo.getJf() == null ? DZFDouble.ZERO_DBL : vo.getJf());
				vo.setDf(vo.getDf() == null ? DZFDouble.ZERO_DBL : vo.getDf());
				ljJF = ljJF.add(vo.getJf());
				ljDF = ljDF.add(vo.getDf());
				if ("0".equals(vo.getFx())) {
					vo.setFx("借");
				} else {
					vo.setFx("贷");
				}
				vec_details.add(vo);
			}
		}
		return vec_details;
	}

	private String getMAXPeriod(String pk_corp, DZFDate enddate)
			throws DZFWarpException {
		String period = DateUtils.getPeriod(enddate);
		String sql = "select max(period) from YNT_QMJZ where pk_corp='"
				+ pk_corp + "' and period<'" + period
				+ "' and nvl(jzfinish,'N')='Y' and nvl(dr,0)=0 ";
		SQLParameter parameter = new SQLParameter();
		Object[] obj = (Object[]) singleObjectBO.executeQuery(sql, parameter,new ArrayProcessor());
		if (obj != null && obj.length > 0) {
			String i = (String) obj[0];
			return i;
		}
		return null;
	}

	private List[] tzDate(HashMap<String, HashMap<String, KmQmJzExtVO>> map,
			List<String> period, Map<String, YntCpaccountVO> mp,
			HashMap<String, HashMap<String, List<KmMxZVO>>> fsmap,
			Set<String>  kmparentlist ,
			DZFBoolean xswyewfs, boolean isfs,DZFBoolean ishowfs,DZFBoolean isnomonthfs,DZFBoolean btotalyear ) {
		if(period == null || period.size() == 0){
			throw new BusinessException("区间不正确,请检查");
		}

		String showperiod = period.get(0);
		int len = period == null ? 0 : period.size();
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
		for (int i = 0; i < len1; i++) {
			m = map.get(keys[i]);
			ufd = DZFDouble.ZERO_DBL;
			int count = 0;
			Map<String ,DZFDouble> yearmapjf = new HashMap<String ,DZFDouble>();
			Map<String ,DZFDouble> yearmapdf = new HashMap<String ,DZFDouble>();
			for (int j = 0; j < len; j++) {
				p = period.get(j);
				month = new DZFDate(p + "-01").getMonth();

				v1 = m.get(p);
				if (v1 == null) {
					v1 = KmQmJzExtVO.newInstance();
					v1.setPk_accsubj(keys[i]);
					v1.setPeriod(p);
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

					v1.setThismonthqm(ufd);
					v1.setLjjffse(ufdljjf);
					v1.setLjdffse(ufdljdf);
				}
				if (v1.getPeriod().compareTo(showperiod) >= 0) {
					if (lists[j] == null)
						lists[j] = new ArrayList<KmMxZVO>();
					list = lists[j];
					if(ishowfs!=null && !ishowfs.booleanValue()){/** 无发生不显示 */
						if((v1.getJffse()== null || v1.getJffse().doubleValue() == 0)
								&& (v1.getDffse()== null || v1.getDffse().doubleValue() == 0)
								){
							if((isnomonthfs!=null && isnomonthfs.booleanValue())
									|| !kmparentlist.contains(v1.getPk_accsubj()  + "_" + v1.getPeriod())){
								continue;
							}
						}
					}
					addMxvo(isfs ? listr : list, v1, mp, fsmap, kmparentlist ,
							xswyewfs,isnomonthfs,btotalyear,yearmapjf,yearmapdf);
				}
			}
		}
		/** 科目结果集过滤 */
		return isfs ? new List[] { listr } : lists;
	}




	private List[] sortbycode(List[] l) {
		int len = l == null ? 0 : l.length;
		int len1 = 0;
		List ls = null;
		List ls1 = null;
		KmMxZVO kvo = null;
		Map<String, List<KmMxZVO>> m = new HashMap<String, List<KmMxZVO>>();
		for (int i = 0; i < len; i++) {
			ls = l[i];
			len1 = ls == null ? 0 : ls.size();
			m = new HashMap<String, List<KmMxZVO>>();
			for (int j = 0; j < len1; j++) {
				kvo = (KmMxZVO) ls.get(j);
				ls1 = m.get(kvo.getKmbm());

				if (ls1 == null) {
					ls1 = new ArrayList<KmMxZVO>();
					m.put(kvo.getKmbm(), ls1);
				}
				ls1.add(kvo);
			}
			String[] strs = m.keySet().toArray(new String[0]);
			Arrays.sort(strs);
			len1 = strs.length;
			if (ls != null)
				ls.clear();
			for (int j = 0; j < len1; j++) {

				ls1 = m.get(strs[j]);

				ls.addAll(ls1);
			}
		}

		return l;
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
		Integer fxleaf =null;
		Integer pafx = null;
		for (int i = 0; i < len; i++) {
			v = vec0.get(i);
			kv = getKey(map, v.getKm(), min_period);
			kmpk = v.getKm();
			plist = ReportUtil.getKmParent(mp, kmpk);
			fxleaf = mp.get(kmpk).getDirection();
			ufd = kv.getThismonthqc();
			if (ufd == null)
				ufd = DZFDouble.ZERO_DBL;
			ntemp = mp.get(v.getKm()).getDirection();
			if (ntemp == 0) {
				ufd1 = VoUtils.getDZFDouble(v.getJf()).sub(VoUtils.getDZFDouble(v.getDf()));
				ufd = ufd.add(ufd1);
			} else {
				ufd1 = VoUtils.getDZFDouble(v.getDf()).sub(VoUtils.getDZFDouble(v.getJf()));
				ufd = ufd.add(ufd1);
			}
			kv.setThismonthqc(ufd);
			kv.setThismonthqm(ufd);
			len1 = plist == null ? 0 : plist.size();
			for (int j = 0; j < len1; j++) {
				kmpk = plist.get(j);
				kv = getKey(map, kmpk, min_period);
				pafx =  mp.get(kmpk).getDirection();
				ufd = VoUtils.getDZFDouble(kv.getThismonthqc());
				if(pafx.intValue() != fxleaf.intValue()){
					ufd = ufd.sub(VoUtils.getDZFDouble(ufd1));
				}else{
					ufd = ufd.add(VoUtils.getDZFDouble(ufd1));
				}
				kv.setThismonthqc(ufd);

				ufd = VoUtils.getDZFDouble(kv.getThismonthqm());
				if(pafx.intValue() != fxleaf.intValue()){
					ufd = ufd.sub(VoUtils.getDZFDouble(ufd1));
				}else{
					ufd = ufd.add(VoUtils.getDZFDouble(ufd1));
				}
				kv.setThismonthqm(ufd);
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
			plist = ReportUtil.getKmParent(mp, kmpk);
			if (plist == null)
				continue;
			len = plist == null ? 0 : plist.size();
			for (int i = 0; i < len; i++) {
				kmpk = plist.get(i);
				kvo = getKey(map, kmpk, min_period);
				ufd = VoUtils.getDZFDouble(kvo.getThismonthqc());
				/** 判断下方向，如果方向不一样不是加应该是减 */
				Integer pafx = mp.get(kmpk).getDirection();
				if(pafx.intValue() != fxleaf.intValue()){
					ufd = ufd.sub(VoUtils.getDZFDouble(kvoleaf.getThismonthqc()));
				}else{
					ufd = ufd.add(VoUtils.getDZFDouble(kvoleaf.getThismonthqc()));
				}
				kvo.setThismonthqc(ufd);

				ufd = VoUtils.getDZFDouble(kvo.getThismonthqm());
				if(pafx.intValue() != fxleaf.intValue()){
					ufd = ufd.sub(VoUtils.getDZFDouble(kvoleaf.getThismonthqm()));
				}else{
					ufd = ufd.add(VoUtils.getDZFDouble(kvoleaf.getThismonthqm()));
				}

				kvo.setThismonthqm(ufd);
			}

		}

	}

	private boolean bStartPeriodBefore(KmMxZVO kvo,DZFDate showStartDate){
		showStartDate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(showStartDate));
		DZFDate ufd = null;
		String code = "";
			code = kvo.getRq();
			if (code != null && code.length() == 7)
				code = code + "-01";
			ufd = new DZFDate(code);
			if (ufd.before(showStartDate))
				return true;
			return false;
	}

	private void addMxvo(List<KmMxZVO> list, KmQmJzExtVO jzvo,
			Map<String, YntCpaccountVO> mp,
			HashMap<String, HashMap<String, List<KmMxZVO>>> fsmap,Set<String>  kmparentlist ,
			DZFBoolean xswyewfs,DZFBoolean isnomonthfs,DZFBoolean btotalyear,
			Map<String ,DZFDouble> yearmapjf ,Map<String ,DZFDouble> yearmapdf) {
		xswyewfs = xswyewfs  == null ?DZFBoolean.TRUE:xswyewfs;
		KmMxZVO vo1 = new KmMxZVO();
		vo1.setRq(jzvo.getPeriod());
		DZFDate datevalue = new DZFDate(jzvo.getPeriod() + "-01");
		vo1.setPzh(null);
		YntCpaccountVO km = mp.get(jzvo.getPk_accsubj());
		/** 根据科目层级来过滤科目 */
		StringBuffer nullstr = new StringBuffer();
		vo1.setKm(nullstr.toString() + km.getAccountname());
		vo1.setPk_accsubj(jzvo.getPk_accsubj());
		vo1.setLevel(km.getAccountlevel());
		vo1.setDay("-01");
		vo1.setKmbm(km.getAccountcode());
		vo1.setZy("期初余额");
		vo1.setBsyszy(DZFBoolean.TRUE);
		vo1.setJf(DZFDouble.ZERO_DBL);
		vo1.setDf(DZFDouble.ZERO_DBL);
		if (0 == km.getDirection()) {
			vo1.setFx("借");
		} else {
			vo1.setFx("贷");
		}
		vo1.setYe(jzvo.getThismonthqc());
		/** 倒数第二行：日期（空值）、凭证号（空值）、摘要（固定值：本月合计）、借方（本月借方发生额累计）、贷方（本月贷方发生额累计）、方向（根据科目方向显示）、余额（借：期初+借方累计-贷方累计；贷：期初+贷方累计-借方累计；）-------------------------*/
		KmMxZVO vo_2 = new KmMxZVO();

		vo_2.setRq(jzvo.getPeriod());
		vo_2.setDay("-" + datevalue.getDaysMonth());
		vo_2.setPzh(null);
		vo_2.setKm(nullstr.toString() + km.getAccountname());
		vo_2.setPk_accsubj(km.getPk_corp_account());
		vo_2.setKmbm(km.getAccountcode());
		vo_2.setZy("本月合计");
		vo_2.setBsyszy(DZFBoolean.TRUE);
		vo_2.setJf(jzvo.getJffse());
		vo_2.setDf(jzvo.getDffse());
		vo_2.setYe(jzvo.getThismonthqm());
		if (0 == km.getDirection()) {
			vo_2.setFx("借");
		} else {
			vo_2.setFx("贷");
		}
		KmMxZVO vo3 = new KmMxZVO();
		vo3.setRq(jzvo.getPeriod());
		vo3.setDay("-" + datevalue.getDaysMonth());
		vo3.setPzh(null);
		vo3.setKm(nullstr.toString() + km.getAccountname());
		vo3.setPk_accsubj(km.getPk_corp_account());
		vo3.setKmbm(km.getAccountcode());
		vo3.setZy("本年累计");
		vo3.setBsyszy(DZFBoolean.TRUE);
		if(btotalyear !=null && btotalyear.booleanValue()){
			/** 借方赋值 */
			DZFDouble tempjf = DZFDouble.ZERO_DBL;
			DZFDouble tempdf = DZFDouble.ZERO_DBL;
			String befperiod = String.valueOf(new Integer(jzvo.getPeriod().substring(0, 4))-1);
			if(yearmapjf.containsKey(befperiod)){
				tempjf = yearmapjf.get(befperiod);
				vo3.setJf(SafeCompute.sub(jzvo.getLjjffse(), tempjf));
			}else{
				vo3.setJf(jzvo.getLjjffse());
			}
			yearmapjf.put(jzvo.getPeriod().substring(0, 4), jzvo.getLjjffse());

			/** 贷方赋值 */
			if(yearmapdf.containsKey(befperiod)){
				tempdf = yearmapdf.get(befperiod);
				vo3.setDf(SafeCompute.sub(jzvo.getLjdffse(), tempdf));
			}else{
				vo3.setDf(jzvo.getLjdffse());
			}
			yearmapdf.put(jzvo.getPeriod().substring(0, 4), jzvo.getLjdffse());
		}else{
			vo3.setJf(jzvo.getLjjffse());
			vo3.setDf(jzvo.getLjdffse());
		}
		vo3.setYe(jzvo.getThismonthqm());
		if (0 == km.getDirection()) {
			vo3.setFx("借");
		} else {
			vo3.setFx("贷");
		}

		boolean isShow = false;
		if (xswyewfs.booleanValue()) {
			/**  需要显示无余额无发生*/
			if (vo1.getYe().doubleValue() == 0
					 && vo_2.getJf().doubleValue() == 0
					 && vo_2.getDf().doubleValue() == 0
					)  {
				if( (isnomonthfs == null || !isnomonthfs.booleanValue())  &&(
						kmparentlist.contains(vo1.getPk_accsubj() + "_" + jzvo.getPeriod())) ){
					isShow = true;
				}else{
					isShow = false;
				}
			} else {
				isShow = true;
			}
		} else {
			/** 不需要显示无余额无发生 */
			isShow = true;
		}
		List<KmMxZVO> list1 = null;
		HashMap<String, List<KmMxZVO>> mm = fsmap.get(km.getPk_corp_account());
		if (mm == null)
			list1 = new ArrayList<KmMxZVO>();
		else
			list1 = mm.get(jzvo.getPeriod());
		Integer direction = mp.get(km.getPk_corp_account()).getDirection();
		DZFDouble uft = null;
		DZFDouble ufd = vo1.getYe();
		if (isShow) {
			list.add(vo1);
			if (list1 != null)
				for (KmMxZVO vo : list1) {
					vo.setKm(nullstr.toString() + km.getAccountname());
					vo.setPk_accsubj(km.getPk_corp_account());
					vo.setKmbm(km.getAccountcode());
					if (direction == 0) {
						uft = vo.getJf();
						if (uft == null)
							uft = DZFDouble.ZERO_DBL;
						ufd = ufd.add(uft);
						uft = vo.getDf();
						if (uft == null)
							uft = DZFDouble.ZERO_DBL;
						ufd = ufd.sub(uft);
						vo.setYe(ufd);
					} else {
						uft = vo.getDf();
						if (uft == null)
							uft = DZFDouble.ZERO_DBL;
						ufd = ufd.add(uft);
						uft = vo.getJf();
						if (uft == null)
							uft = DZFDouble.ZERO_DBL;
						ufd = ufd.sub(uft);
						vo.setYe(ufd);
					}
					list.add(vo);
				}
			list.add(vo_2);
			list.add(vo3);
		}
	}

	protected String getQuerySqlByPeriod(DZFDate start, DZFDate end,
			String kmwhere, String pk_corp, DZFBoolean ishasjz,
			DZFBoolean ishassh ,SQLParameter parameter) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select h.period as qj,h.doperatedate as rq,h.pzh as pzh, a.accountcode,a.pk_corp_account as km ,b.zy ,");
		sb.append("        b.pk_currency as bz ,b.jfmny as jf ,b.dfmny as df,a.direction as fx, b.pk_tzpz_h ,b.pk_tzpz_b ,");
		sb.append("        b.fzhsx1,  b.fzhsx2,  b.fzhsx3, b.fzhsx4, b.fzhsx5,");
	    /**  启用库存  存货作为辅助核算 */
		sb.append(" case when b.fzhsx6 is null then b.pk_inventory else b.fzhsx6 end fzhsx6, ");
		sb.append("      b.fzhsx7,  b.fzhsx8, b.fzhsx9, b.fzhsx10,b.rowno as rowno");
		sb.append(" from ynt_tzpz_b b  ");
		sb.append(" inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h  ");
		sb.append(" inner join  ynt_cpaccount a  on b.pk_accsubj=a.pk_corp_account ");
		sb.append(" where nvl(h.dr,0)=0 and nvl(b.dr,0)=0 ");
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

		if (ishasjz.booleanValue()) {
			/** 不包含未记账，即只查询已记账的*/
			sb.append(" and h.ishasjz='Y' ");
			sb.append(" and h.vbillstatus=1 ");
		}
		return sb.toString();
	}


	public List<KmMxZVO> getKmFSByPeriodQC(String pk_corp, DZFBoolean ishasjz, DZFBoolean ishassh, DZFDate start, DZFDate end, String kmwhere)
			throws DZFWarpException {
		SQLParameter parameter = new SQLParameter();
		String sql1 = getQuerySqlByPeriodForQC(start, end, kmwhere, pk_corp,
				ishasjz, ishassh,parameter );
		ArrayList result1 = (ArrayList) singleObjectBO.executeQuery(sql1, parameter, new BeanListProcessor(KmMxZVO.class));

		List<KmMxZVO> vec_details = new ArrayList<KmMxZVO>();
		/** 累计借方 */
		DZFDouble ljJF = DZFDouble.ZERO_DBL;
		/** 累计贷方 */
		DZFDouble ljDF = DZFDouble.ZERO_DBL;
		if (result1 != null && !result1.isEmpty()) {
			KmMxZVO vo = null;
			for (Object o : result1) {
				vo = (KmMxZVO) o;
				if((vo.getJf() ==null || vo.getJf().doubleValue() ==0 ) && (vo.getDf() ==null || vo.getDf().doubleValue() ==0 )){
					continue;
				}
				vo.setJf(vo.getJf() == null ? DZFDouble.ZERO_DBL : vo.getJf());
				vo.setDf(vo.getDf() == null ? DZFDouble.ZERO_DBL : vo.getDf());
				ljJF = ljJF.add(vo.getJf());
				ljDF = ljDF.add(vo.getDf());
				if ("0".equals(vo.getFx())) {
					vo.setFx("借");
				} else {
					vo.setFx("贷");
				}
				vec_details.add(vo);
			}
		}
		return vec_details;
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
	protected String getQuerySqlByPeriodForQC(DZFDate start, DZFDate end, String kmwhere, String pk_corp, DZFBoolean ishasjz,
			DZFBoolean ishassh,SQLParameter parameter) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from  ");
		sb.append("  (select h.period as qj,h.doperatedate as rq,h.pzh as pzh, ");
		sb.append("    a.accountcode,a.pk_corp_account as km ,b.zy ,");
		sb.append("    b.pk_currency as bz ,b.jfmny as jf ,b.dfmny as df , ");
		sb.append("    a.direction as fx,b.pk_tzpz_h , b.pk_tzpz_b ,");
		sb.append("    b.fzhsx1,  b.fzhsx2,  b.fzhsx3, b.fzhsx4, b.fzhsx5,");
		/**   启用库存  存货作为辅助核算 */
		sb.append(" case when b.fzhsx6 is null then b.pk_inventory else b.fzhsx6  end fzhsx6, ");
		sb.append("      b.fzhsx7,  b.fzhsx8, b.fzhsx9, b.fzhsx10,'N' as bqc ");
		sb.append("    from ynt_tzpz_b b ");
		sb.append("    inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h  " );
		sb.append("    inner join  ynt_cpaccount a  on b.pk_accsubj=a.pk_corp_account  ");
		sb.append("    where nvl(h.dr,0)=0 and nvl(b.dr,0)=0  ");
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

		if (ishasjz.booleanValue()) {
			/** 不包含未记账，即只查询已记账的 */
			sb.append(" and h.ishasjz='Y' ");
			sb.append(" and h.vbillstatus=1 ");
		}
		sb.append(" order by h.doperatedate,  h.pzh asc  ,b.rowno )  b3 ");

		StringBuffer qcyesql = new StringBuffer();
		qcyesql.append(" union all ");
		qcyesql.append(" select substr(b.doperatedate, 1, 4)||'-01' as qj, substr(b.doperatedate, 1, 4)||'-01-01' as rq, ");
		qcyesql.append("  ''as pzh,a.accountcode,a.pk_corp_account as km,'' as zy,'' as  bz, ");
		qcyesql.append("  b.yearjffse as jf, b.yeardffse as df, ");
		qcyesql.append("  a.direction as fx ,'' as  pk_tzpz_h , '' as pk_tzpz_b , ");
		qcyesql.append("  '0' as fzhsx1,  '0' as fzhsx2, '0' as fzhsx3, '0' as fzhsx4, '0' as fzhsx5,");
		qcyesql.append("  '0' as fzhsx6,  '0' as fzhsx7,  '0' as fzhsx8, '0' as fzhsx9, '0' as fzhsx10,'Y' as bqc");
		qcyesql.append("  from YNT_QCYE b inner join ynt_cpaccount a  on b.pk_accsubj = a.pk_corp_account and a.isleaf='Y' ");
		qcyesql.append("  where (1=1)  ");
		qcyesql.append(   kmwhere );
		qcyesql.append("  and b.pk_corp='"+pk_corp +"'");
		qcyesql.append("  and nvl(b.dr,0)=0  ");
		return sb.toString() + qcyesql.toString();
	}

	@Override
	public YntCpaccountVO[] getkm_first(String kms_first,String pk_corp) {
		SQLParameter sp = new SQLParameter();
		String kmsql ="select  * from ynt_cpaccount where pk_corp =? and accountcode like '"+kms_first+"%' and nvl(dr,0)=0 order by accountcode";
		sp.addParam(pk_corp);
		List<YntCpaccountVO> cpalist = (List<YntCpaccountVO>) singleObjectBO.executeQuery(kmsql, sp,new  BeanListProcessor(YntCpaccountVO.class));
		HashMap<String, YntCpaccountVO> cpamap = new HashMap<String, YntCpaccountVO>();
		for(YntCpaccountVO cpavo:cpalist){
			cpamap.put(cpavo.getPk_corp_account(), cpavo);
		}
		return cpalist.toArray(new YntCpaccountVO[0]);
	}

	@SuppressWarnings("unused")
	@Override
	public KmMxZVO[] getKMMXZConFzVOs(QueryParamVO vo,Object[] qryobjs) throws DZFWarpException {
		Object[] obj ;
		if(qryobjs == null ||  qryobjs.length ==0){
			obj = getKMMXZVOs1(vo, false);
		}else{
			obj = qryobjs;
		}
		List[] lists = (List[]) obj[0];
		int len = lists == null ? 0 : lists.length;

		List<KmMxZVO> mxlists = len == 0 ? null : lists[0];

		if(vo.getSfzxm()!=null && vo.getSfzxm().booleanValue()){

			HashMap<String, KmMxZVO> qcfzmap= (HashMap<String, KmMxZVO>)obj[3];

			HashMap<String, List<KmMxZVO>> fsfzmap = (HashMap<String, List<KmMxZVO>>)obj[4];

			HashMap<String, DZFDouble[]> corpbegqcmap = (HashMap<String,DZFDouble[]>)obj[5];

			Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>)obj[1];

			List<KmMxZVO> fzkmmxvos=getResultVos(qcfzmap, fsfzmap,corpbegqcmap,
					ReportUtil.getPeriods(vo.getBegindate1(), vo.getEnddate()), mp,vo.getPk_corp(),
					vo.getIshowfs(),vo.getKms_last(),vo.getBtotalyear());

			if(fzkmmxvos!=null && fzkmmxvos.size()>0){
				for(KmMxZVO votemp:fzkmmxvos){
					mxlists.add(votemp);
				}
			}
		}

		if(mxlists == null ||  mxlists.size() == 0){
			throw new BusinessException("暂无数据");
		}

		KmMxZVO[] kvos = mxlists.toArray(new KmMxZVO[0]);

		/** 排序科目+辅助明细数据 */
		Arrays.sort(kvos, new Comparator<KmMxZVO>() {
			private int get(KmMxZVO o1) {
				int i = 0;
				if (o1.getZy() == null)
					i = 2;
				else if (o1.getZy().equals("期初余额")  && ReportUtil.bSysZy(o1))
					i = 1;
				else if (o1.getZy().equals("本月合计")  && ReportUtil.bSysZy(o1))
					i = 3;
				else if (o1.getZy().equals("本年累计")  && ReportUtil.bSysZy(o1) )
					i = 4;
				else
					i = 2;
				return i;
			}

			public int compare(KmMxZVO o1, KmMxZVO o2) {
				int i = o1.getKmbm().compareTo(o2.getKmbm());
				if (i == 0) {
					String s1 = o1.getRq().substring(0, 7);
					String s2 = o2.getRq().substring(0, 7);
					int i1 = s1.length();
					int i2 = s2.length();
					if (i1 == i2) {
						i = s1.compareTo(s2);
					} else {
						int i0 = Math.min(i1, i2);
						if (i0 != i1) {
							s1 = s1.substring(0, i0);
						}
						if (i0 != i2) {
							s2 = s2.substring(0, i0);
						}
						i = s1.compareTo(s2);
					}
				}
				return i;
			}
		});
		return putPageValue(kvos, vo.getCjq(), vo.getCjz(),obj , vo.getBegindate1(),vo.getEnddate(), vo.getKms_last() , vo.getIsleaf());
	}

	/**
	 * 查询对应的期末的结果集(添加期初+发生，同时按照日期排序，计算本期和本年累计)
	 *
	 */
	public List<KmMxZVO> getResultVos(Map<String, KmMxZVO> qcmapvos, Map<String, List<KmMxZVO>> fsmapvos,
			HashMap<String, DZFDouble[]> corpbegqcmap,
			List<String> periods, Map<String , YntCpaccountVO> kmmap,
			String pk_corp,DZFBoolean ishowfs,String kmslast,DZFBoolean btotalyear) throws DZFWarpException {
		List<KmMxZVO> reslistvos = new ArrayList<KmMxZVO>();
		Map<String,List<KmMxZVO>> resmap = new HashMap<String,List<KmMxZVO>>();
		Map<String,AuxiliaryAccountBVO> fzmap = getFzXm(pk_corp);
		Map<String, AuxiliaryAccountHVO> fzhmap= new HashMap<String, AuxiliaryAccountHVO>();
		AuxiliaryAccountHVO[]  fzhvos = zxkjPlatformService.queryHByPkCorp(pk_corp);
		if(fzhvos!=null && fzhvos.length>0){
			for(AuxiliaryAccountHVO hvo:fzhvos){
				fzhmap.put(hvo.getPk_auacount_h(), hvo);
			}
		}

		CorpVO cpvo = zxkjPlatformService.queryCorpByPk(pk_corp);
		String begperiod = DateUtils.getPeriod(cpvo.getBegindate());
		Map<String, InventoryVO> invmap = getInventory(pk_corp);
		if(qcmapvos!=null && qcmapvos.size()>0){
			for(String key:qcmapvos.keySet()){
				resmap.put(key, new ArrayList<KmMxZVO>());
			}
		}

		if(fsmapvos!=null && fsmapvos.size()>0){
			for(String key:fsmapvos.keySet()){
				if(resmap.containsKey(key)){
					for(KmMxZVO tempvo:fsmapvos.get(key)){
						resmap.get(key).add(tempvo);
					}
				}else{
					List<KmMxZVO> templist = fsmapvos.get(key);
					resmap.put(key, templist);
				}
			}
		}

		/** 对每个map进行日期的排序 */
		String[] keys = null;
		StringBuffer code = new StringBuffer();
		StringBuffer comparecode = new StringBuffer();
		StringBuffer fzlbcode = new StringBuffer();//包含的辅助类别code
		StringBuffer name = new StringBuffer();
		int level = 0;
		String fx = null;
		/** 无发生无余额不显示 */
		if(resmap.size()>0){
			AuxiliaryAccountBVO axuvo  = null;
			InventoryVO invo =null;
			for(String key:resmap.keySet()){
				List<KmMxZVO> listtemp = resmap.get(key);
				keys = key.split("_");
				code.setLength(0);/** 清空字符串 */
				comparecode.setLength(0);
				fzlbcode.setLength(0);
				name.setLength(0);
				/** 赋值名称，和编码 */
				for(int i=0;i<keys.length;i++){
					if(i == 0){//科目
						level =kmmap.get(keys[i]).getAccountlevel();
						if(kmmap.get(keys[i]).getDirection()!=null && kmmap.get(keys[i]).getDirection() == 1){
							fx = "贷";
						}else{
							fx="借";
						}
						code.append(kmmap.get(keys[i]).getAccountcode()+"_");
						comparecode.append(kmmap.get(keys[i]).getAccountcode()+"_");
					}else{//辅助项目
						axuvo = fzmap.get(keys[i]);
						if(axuvo != null){
							code.append(axuvo.getCode()+"_");
							if(fzhmap.get( axuvo.getPk_auacount_h())!=null){
								comparecode.append(axuvo.getCode()+"("+fzhmap.get( axuvo.getPk_auacount_h()).getCode()+")_");
								fzlbcode.append(fzhmap.get( axuvo.getPk_auacount_h()).getCode()+",");
							}else{
								comparecode.append(axuvo.getCode()+"_");
							}
							name.append(axuvo.getName() +"_");
						}else{
							invo = invmap.get(keys[i]);
							if(invo != null){
								code.append(invo.getCode()+"_");
								comparecode.append(invo.getCode()+"(6)"+"_");
								name.append(invo.getName() +"_");
								fzlbcode.append(6+",");
							}
						}
					}
				}
				code = new StringBuffer(code.substring(0, code.length()-1));
				comparecode = new StringBuffer(comparecode.substring(0, comparecode.length()-1));
				if(!StringUtil.isEmpty(name.toString()))
					name = new StringBuffer(name.substring(0, name.length()-1));


				for(String period:periods){
					KmMxZVO qckmmx = new KmMxZVO();
					qckmmx.setPk_accsubj(key);
					qckmmx.setRq(DateUtils.getPeriodStartDate(period).toString());
					if(period.compareTo(begperiod)<0){
						qckmmx.setYe(corpbegqcmap.get(key)==null? DZFDouble.ZERO_DBL:corpbegqcmap.get(key)[0]);
						qckmmx.setYbye(corpbegqcmap.get(key)==null? DZFDouble.ZERO_DBL:corpbegqcmap.get(key)[1]);
					}else{
						qckmmx.setYe(qcmapvos.get(key) == null?DZFDouble.ZERO_DBL:qcmapvos.get(key).getYe());
						qckmmx.setYbye(qcmapvos.get(key) == null?DZFDouble.ZERO_DBL:qcmapvos.get(key).getYbye());

					}
					qckmmx.setZy("期初余额");
					qckmmx.setBsyszy(DZFBoolean.TRUE);
					listtemp.add(qckmmx);

					KmMxZVO monthkmmx = new KmMxZVO();
					monthkmmx.setPk_accsubj(key);
					monthkmmx.setRq(DateUtils.getPeriodEndDate(period).toString());
					monthkmmx.setZy("本月合计");
					monthkmmx.setBsyszy(DZFBoolean.TRUE);
					listtemp.add(monthkmmx);

					KmMxZVO yearkmmx = new KmMxZVO();
					yearkmmx.setPk_accsubj(key);
					yearkmmx.setRq(DateUtils.getPeriodEndDate(period).toString());
					yearkmmx.setZy("本年累计");
					yearkmmx.setBsyszy(DZFBoolean.TRUE);
					listtemp.add(yearkmmx);
				}

				KmMxZVO[] mxvos = listtemp.toArray(new KmMxZVO[0]);
				KmMxZVO[] reskmmxvos =mxvos  ;

				/** 赋值科目信息 */
				for(KmMxZVO vo:reskmmxvos){
					if(!StringUtil.isEmpty(name.toString()))
						vo.setKm(name.toString());
					vo.setKmbm(code.toString());
					vo.setComparecode(comparecode.toString());
					vo.setFzlbcode(fzlbcode.toString());
					vo.setLevel(level);
				}
				/** 排序辅助明细数据 */
				Arrays.sort(reskmmxvos, new Comparator<KmMxZVO>() {
					private int get(KmMxZVO o1) {
						int i = 0;
						if (o1.getZy() == null)
							i = 2;
						else if (o1.getZy().equals("期初余额")  && ReportUtil.bSysZy(o1) )
							i = 1;
						else if (o1.getZy().equals("本月合计")  && ReportUtil.bSysZy(o1))
							i = 3;
						else if (o1.getZy().equals("本年累计")  && ReportUtil.bSysZy(o1) )
							i = 4;
						else
							i = 2;
						return i;
					}
					
					public int compare(KmMxZVO o1, KmMxZVO o2) {
						int i = 0;
						if(i ==0){
							i = o1.getRq().compareTo(o2.getRq());
						}
						if(i == 0){
							Integer o1value = get(o1);
							Integer o2value = get(o2);
							i = o1value.compareTo(o2value);
						}
					    if(i == 0){
					    	String pzh1 = o1.getPzh() == null ? "99999" : o1.getPzh();
					    	String pzh2 = o2.getPzh() == null ? "99999" : o2.getPzh();
					    	i = pzh1.compareTo(pzh2);
					    }
						return i;
					}
				});
				
				/** 循环数据，同时赋值 */
				DZFDouble sumvalue = DZFDouble.ZERO_DBL;
				if(qcmapvos.get(key)!=null){
					sumvalue = sumvalue.add(getDzfDouble(qcmapvos.get(key).getYe()));
				}
				DZFDouble jfmonthvalue =  DZFDouble.ZERO_DBL;
				DZFDouble dfmonthvalue =  DZFDouble.ZERO_DBL;
				DZFDouble jfyearvalue = qcmapvos.get(key)!=null ? qcmapvos.get(key).getJf():DZFDouble.ZERO_DBL;
				DZFDouble dfyearvalue = qcmapvos.get(key)!=null ? qcmapvos.get(key).getDf() :DZFDouble.ZERO_DBL;
				Map<String, DZFDouble> jfyearmap = new HashMap<String, DZFDouble>();
				Map<String, DZFDouble> dfyearmap = new HashMap<String, DZFDouble>();
				jfyearmap.put(begperiod.substring(0, 4), jfyearvalue);
				dfyearmap.put(begperiod.substring(0, 4), dfyearvalue);
				
				
				/** ---------原币 --------*/
				DZFDouble ybsumvalue = DZFDouble.ZERO_DBL;
				if(qcmapvos.get(key)!=null){
					ybsumvalue = ybsumvalue.add(getDzfDouble(qcmapvos.get(key).getYbye()));
				}
				DZFDouble ybjfmonthvalue =  DZFDouble.ZERO_DBL;
				DZFDouble ybdfmonthvalue =  DZFDouble.ZERO_DBL;
				DZFDouble ybjfyearvalue = qcmapvos.get(key)!=null ? qcmapvos.get(key).getYbjf():DZFDouble.ZERO_DBL;
				DZFDouble ybdfyearvalue = qcmapvos.get(key)!=null ? qcmapvos.get(key).getYbdf():DZFDouble.ZERO_DBL;
				Map<String, DZFDouble> ybjfyearmap = new HashMap<String, DZFDouble>();
				Map<String, DZFDouble> ybdfyearmap = new HashMap<String, DZFDouble>();
				ybjfyearmap.put(begperiod.substring(0, 4), ybjfyearvalue);
				ybdfyearmap.put(begperiod.substring(0, 4), ybdfyearvalue);
				
				List<KmMxZVO> listemp2 = new ArrayList<KmMxZVO>();
				List<KmMxZVO> listemp3 = new ArrayList<KmMxZVO>();
				/** 重新计算*/
				DZFDouble tempvalue = DZFDouble.ZERO_DBL;
				DZFDouble ybtempvalue = DZFDouble.ZERO_DBL;
				for(KmMxZVO tempvo:reskmmxvos){
					if(tempvo.getRq().compareTo(begperiod)>=0){
						if(jfyearmap.containsKey(tempvo.getRq().substring(0, 4))){
							tempvalue =jfyearmap.get(tempvo.getRq().substring(0, 4)) ;
							jfyearmap.put(tempvo.getRq().substring(0, 4), SafeCompute.add(tempvo.getJf(), tempvalue));
						}else{
							jfyearmap.put(tempvo.getRq().substring(0, 4), tempvo.getJf() ==null?DZFDouble.ZERO_DBL:tempvo.getJf());
						}
						/** ----原币借方 ------------*/
						if(ybjfyearmap.containsKey(tempvo.getRq().substring(0, 4))){
							ybtempvalue =ybjfyearmap.get(tempvo.getRq().substring(0, 4)) ;
							ybjfyearmap.put(tempvo.getRq().substring(0, 4), SafeCompute.add(tempvo.getYbjf(), ybtempvalue));
						}else{
							ybjfyearmap.put(tempvo.getRq().substring(0, 4), tempvo.getYbjf() ==null?DZFDouble.ZERO_DBL:tempvo.getYbjf());
						}
						if(dfyearmap.containsKey(tempvo.getRq().substring(0, 4))){
							tempvalue =dfyearmap.get(tempvo.getRq().substring(0, 4)) ;
							dfyearmap.put(tempvo.getRq().substring(0, 4), SafeCompute.add(tempvo.getDf(), tempvalue));
						}else{
							dfyearmap.put(tempvo.getRq().substring(0, 4), tempvo.getDf() ==null?DZFDouble.ZERO_DBL:tempvo.getDf());
						}
						/**--------原币贷方---------*/
						if(ybdfyearmap.containsKey(tempvo.getRq().substring(0, 4))){
							ybtempvalue =ybdfyearmap.get(tempvo.getRq().substring(0, 4)) ;
							ybdfyearmap.put(tempvo.getRq().substring(0, 4), SafeCompute.add(tempvo.getYbdf(), ybtempvalue));
						}else{
							ybdfyearmap.put(tempvo.getRq().substring(0, 4), tempvo.getYbdf() ==null?DZFDouble.ZERO_DBL:tempvo.getYbdf());
						}
						jfyearvalue = SafeCompute.add(jfyearvalue, tempvo.getJf());
						dfyearvalue = SafeCompute.add(dfyearvalue, tempvo.getDf());
						
						/**-----原币----------*/
						ybjfyearvalue = SafeCompute.add(ybjfyearvalue, tempvo.getYbjf());
						ybdfyearvalue = SafeCompute.add(ybdfyearvalue, tempvo.getYbdf());
						if(!ReportUtil.bSysZy(tempvo) || !"期初余额".equals(tempvo.getZy())){
							if(fx.equals("借")){
								sumvalue = sumvalue.add(getDzfDouble(tempvo.getJf())).sub(getDzfDouble(tempvo.getDf()));
								ybsumvalue = ybsumvalue.add(getDzfDouble(tempvo.getYbjf())).sub(getDzfDouble(tempvo.getYbdf()));
							}else{
								sumvalue = sumvalue.add(getDzfDouble(tempvo.getDf())).sub(getDzfDouble(tempvo.getJf()));
								
								ybsumvalue = ybsumvalue.add(getDzfDouble(tempvo.getYbdf())).sub(getDzfDouble(tempvo.getYbjf()));
							}
						}
						if(!periods.contains(tempvo.getRq().substring(0, 7))){
							continue;
						}
						jfmonthvalue = SafeCompute.add(jfmonthvalue, tempvo.getJf());
						dfmonthvalue = SafeCompute.add(dfmonthvalue, tempvo.getDf());
						ybjfmonthvalue = SafeCompute.add(ybjfmonthvalue, tempvo.getYbjf());
						ybdfmonthvalue = SafeCompute.add(ybdfmonthvalue, tempvo.getYbdf());
						if("本月合计".equals(tempvo.getZy()) && ReportUtil.bSysZy(tempvo)){
							tempvo.setJf(jfmonthvalue);
							tempvo.setDf(dfmonthvalue);
							tempvo.setYbjf(ybjfmonthvalue);
							tempvo.setYbdf(ybdfmonthvalue);
							jfmonthvalue = DZFDouble.ZERO_DBL;
							dfmonthvalue = DZFDouble.ZERO_DBL;
							ybjfmonthvalue = DZFDouble.ZERO_DBL;
							ybdfmonthvalue = DZFDouble.ZERO_DBL;
						}
						if("本年累计".equals(tempvo.getZy()) && ReportUtil.bSysZy(tempvo) ){
							if(btotalyear!=null && btotalyear.booleanValue()){
								tempvo.setJf(jfyearmap.get(tempvo.getRq().substring(0, 4)));
								tempvo.setDf(dfyearmap.get(tempvo.getRq().substring(0, 4)));
								tempvo.setYbjf(ybjfyearmap.get(tempvo.getRq().substring(0, 4)));
								tempvo.setYbdf(ybdfyearmap.get(tempvo.getRq().substring(0, 4)));
							}else{
								tempvo.setJf(jfyearvalue);
								tempvo.setDf(dfyearvalue);
								tempvo.setYbjf(ybjfyearvalue);
								tempvo.setYbdf(ybdfyearvalue);
							}
						}
						tempvo.setYe(sumvalue);
						tempvo.setYbye(ybsumvalue);
					}
					tempvo.setFx(fx);
					tempvo.setDay(tempvo.getRq().substring(7));
					tempvo.setRq(tempvo.getRq().substring(0, 7));
					listemp2.add(tempvo);
				}

				HashMap<String, List<KmMxZVO>> periodmap = new HashMap<String, List<KmMxZVO>>();
				
				for(KmMxZVO zvo:listemp2){
					String rq = zvo.getRq().substring(0, 7);
				    if(periodmap.containsKey(rq)){
				    	periodmap.get(rq).add(zvo);
				    }else{
				    	List<KmMxZVO> temp = new ArrayList<KmMxZVO>();
				    	temp.add(zvo);
				    	periodmap.put(rq, temp);
				    }
				}
				
				for(String str:periods){
					List<KmMxZVO> temp =periodmap.get(str);
					DZFBoolean isfs= DZFBoolean.FALSE;
					DZFBoolean isyefs= DZFBoolean.FALSE;//有余额无发生
					
					for(KmMxZVO zvo:temp){
						if(getDzfDouble(zvo.getJf()).doubleValue()!=0 || getDzfDouble(zvo.getDf()).doubleValue() !=0
								|| getDzfDouble(zvo.getYe()).doubleValue()!=0 ){
						     isfs = DZFBoolean.TRUE;
						}
						if((getDzfDouble(zvo.getJf()).doubleValue()!=0 || getDzfDouble(zvo.getDf()).doubleValue() !=0)
								&& !StringUtil.isEmpty(zvo.getPk_tzpz_b())
								){
							isyefs = DZFBoolean.TRUE;
						}
					}
					
					if(isfs.booleanValue()){
						if((ishowfs!=null && !ishowfs.booleanValue() && isyefs.booleanValue() )
								|| (ishowfs == null || ishowfs.booleanValue() ) ){
							for(KmMxZVO zvo:temp){
								listemp3.add(zvo);
							}
						}
					}
				}
				resmap.put(key, listemp3);
			}
			/** 重新循环赋值*/
			for(String key:resmap.keySet()){
				List<KmMxZVO> listtemp = resmap.get(key);
				if(!(StringUtil.isEmpty(kmslast) || (
						kmslast!=null && (kmslast.compareTo(kmmap.get(key.split("_")[0]).getAccountcode())>=0 
						|| kmmap.get(key.split("_")[0]).getAccountcode().startsWith(kmslast)) ))){
					continue;
				}
				if(listtemp.size()>0){
					for(KmMxZVO mxvo:listtemp){
						reslistvos.add(mxvo);
					}
				}
			}
		}
		return reslistvos;
	}
	
	/**
	 * 获取辅助项目的key
	 * @param pk_corp
	 * @return
	 */
    private Map<String, AuxiliaryAccountBVO> getFzXm(String pk_corp) {
    	Map<String, AuxiliaryAccountBVO> resmap = new HashMap<String, AuxiliaryAccountBVO>();
    	SQLParameter sp = new SQLParameter();
    	sp.addParam(pk_corp);
    	AuxiliaryAccountBVO[] bvos = zxkjPlatformService.queryBByFzlb(pk_corp, null);
    	if(bvos!=null && bvos.length>0){
        	for(AuxiliaryAccountBVO bvo:bvos){
        		resmap.put(bvo.getPrimaryKey(), bvo);
        	}
        }    
 		return resmap;
	}
    
    /**
	 * 获取辅助项目的key(存货)
	 * @param pk_corp
	 * @return
	 */
    private Map<String, InventoryVO> getInventory(String pk_corp) {
    	Map<String, InventoryVO> resmap = new HashMap<String, InventoryVO>();
    	SQLParameter sp = new SQLParameter();
    	sp.addParam(pk_corp);
    	InventoryVO[] bvos =  (InventoryVO[]) singleObjectBO.queryByCondition(InventoryVO.class, "nvl(dr,0)=0 and pk_corp = ?", sp);
        if(bvos!=null && bvos.length>0){
        	for(InventoryVO bvo:bvos){
        		resmap.put(bvo.getPrimaryKey(), bvo);
        	}
        }    
 		return resmap;
	}
    private DZFDouble getDzfDouble(DZFDouble value){
		if(value == null){
			return DZFDouble.ZERO_DBL;
		}else{
			return value;
		}
	}
    
    /**
	 * 发生的数据添加的期初里面
	 * @param vec0
	 * @param mp
	 * @param qcfzmap
	 * @param fzmap
	 */
	private void putFsToQCMap(List<KmZzVO> vec0, Map<String, YntCpaccountVO> mp, HashMap<String, KmMxZVO> qcfzmap,
			Map<String, AuxiliaryAccountBVO> fzmap) {
		if(vec0 == null || vec0.size() == 0){
			return;
		}
		KmMxZVO mxzvo = null;
		YntCpaccountVO accountvo = null;
		DZFDouble tempvalue = DZFDouble.ZERO_DBL;
		String fzhsx= null;
		StringBuffer key  = null;
		for(KmZzVO zzvo:vec0){
			key = new StringBuffer();
			key.append(zzvo.getKm()+"_");
			accountvo = mp.get(zzvo.getKm());
			for(int i=1;i<11;i++){
				fzhsx = (String) zzvo.getAttributeValue("fzhsx"+i);
				if(!StringUtil.isEmpty(fzhsx)){
					key.append(fzhsx+"_");
				}
			}
			
			key = new StringBuffer(key.substring(0, key.length()-1));
			
			if(key.length() ==24){
				continue;
			}
			if(qcfzmap.containsKey(key.toString())){
				mxzvo = qcfzmap.get(key.toString());
				if(accountvo.getDirection() == 0){
					mxzvo.setFx("借");
					tempvalue =  SafeCompute.sub(zzvo.getJf(), zzvo.getDf());
				}else{
					mxzvo.setFx("贷");
					tempvalue =  SafeCompute.sub(zzvo.getDf(), zzvo.getJf());
				}
				mxzvo.setYe(SafeCompute.add(mxzvo.getYe(), tempvalue));
			}else{
				mxzvo = new KmMxZVO();
				if(accountvo.getDirection() == 0){
					mxzvo.setFx("借");
					tempvalue =  SafeCompute.sub(zzvo.getJf(), zzvo.getDf());
				}else{
					mxzvo.setFx("贷");
					tempvalue =  SafeCompute.sub(zzvo.getDf(), zzvo.getJf());
				}
				mxzvo.setPk_accsubj(key.toString());
				mxzvo.setYe(tempvalue);
				qcfzmap.put(key.toString(), mxzvo);
			}
		}
	}
	
	/**
	 * 年结账数据赋值
	 * @param kmqmjzvos
	 * @param mp
	 * @param qcfzmap
	 * @param fzmap
	 */
	private void putNJQCFzMap(KMQMJZVO[] kmqmjzvos, Map<String, YntCpaccountVO> mp, HashMap<String, KmMxZVO> qcfzmap,
			Map<String, AuxiliaryAccountBVO> fzmap,DZFDate begindate,DZFDate corpdate,String rptsource) {
		if(kmqmjzvos == null  || kmqmjzvos.length == 0){
			return;
		}
		FzhsqcVO[] fzhsqcvos = new FzhsqcVO[kmqmjzvos.length];
		for(int i=0;i<kmqmjzvos.length;i++){
			fzhsqcvos[i] = new FzhsqcVO();
			fzhsqcvos[i].setPk_accsubj(kmqmjzvos[i].getPk_accsubj());
			fzhsqcvos[i].setThismonthqc(kmqmjzvos[i].getThismonthqm());
			for(int k =1;k<11;k++){
				fzhsqcvos[i].setAttributeValue("fzhsx"+k, kmqmjzvos[i].getAttributeValue("fzhsx"+k));
			}
		}
		putQCFzMap(fzhsqcvos, mp, qcfzmap,null, fzmap,begindate,corpdate,rptsource);
	}
	
	/**
     * 期末辅助项目赋值
     * @param fzhsqcvos
     * @param mp
     * @param qcfzmap
     */
	private void putQCFzMap(FzhsqcVO[] fzhsqcvos, Map<String, YntCpaccountVO> mp, HashMap<String, KmMxZVO> qcfzmap ,
			HashMap<String, DZFDouble[]> corpbegqcmapfzmap,
			Map<String,AuxiliaryAccountBVO> fzmap
			,DZFDate begindate,DZFDate corpdate,String rptsource
			) {
		if(fzhsqcvos!=null && fzhsqcvos.length>0){
			StringBuffer sb = null;
			StringBuffer sbcode = null;
			StringBuffer sbname = null;
			KmMxZVO tempkmmx = null;
			YntCpaccountVO tempkmvo =  null;
			for(FzhsqcVO qcvo:fzhsqcvos){
				sb = new StringBuffer();
				sbcode = new StringBuffer();
				sbname = new StringBuffer();
				sb.append(qcvo.getPk_accsubj()+"_");
				if(mp.get(qcvo.getPk_accsubj()) == null){
					continue;
				}
				tempkmvo = mp.get(qcvo.getPk_accsubj());
				if("lrb".equals(rptsource)){
					if(0 == tempkmvo.getDirection()){
						qcvo.setYeardffse(DZFDouble.ZERO_DBL);
					}else{
						qcvo.setYearjffse(DZFDouble.ZERO_DBL);
					}
				}
				sbcode.append(tempkmvo.getAccountcode()+"_");
				sbname.append(tempkmvo.getAccountname()+"_");
				for(int i=1;i<11;i++){
					String key = (String) qcvo.getAttributeValue("fzhsx"+i);
					if(!StringUtil.isEmpty(key)){
						sb.append(key+"_");
						if(fzmap.get(key) !=  null){
							sbcode.append(fzmap.get(key).getCode());
							sbname.append(fzmap.get(key).getName());
						}
					}
				}
				sb=new StringBuffer(sb.substring(0, sb.length()-1));
				/** 科目的长度不处理 */
				if(sb.length() ==24){
					continue;
				}
				DZFDouble qcmny = qcvo.getThismonthqc() == null?DZFDouble.ZERO_DBL:qcvo.getThismonthqc() ;
				if(!qcfzmap.containsKey(sb.toString())){
					tempkmmx =  new KmMxZVO();
					tempkmmx.setPk_accsubj(sb.toString());
					if(begindate.getYear() == corpdate.getYear()  && corpdate.getMonth() != 1){ 
						tempkmmx.setJf(qcvo.getYearjffse());
						tempkmmx.setDf(qcvo.getYeardffse());
					}
					if(0 == tempkmvo.getDirection()){
						tempkmmx.setFx("借");
					}else{
						tempkmmx.setFx("贷");
					}
					tempkmmx.setYe(qcmny);
					qcfzmap.put(sb.toString(), tempkmmx);
				}else{
					tempkmmx =  qcfzmap.get(sb.toString());
					if(0 == tempkmvo.getDirection()){
						qcmny = SafeCompute.add( tempkmmx.getYe(),qcmny);
						tempkmmx.setJf(SafeCompute.add(tempkmmx.getJf(),qcmny));
					}else{
						qcmny=SafeCompute.add(tempkmmx.getYe(),qcmny.multiply(-1));
					}
					tempkmmx.setYe(qcmny);
				}
				
				if(corpbegqcmapfzmap!=null){
					corpbegqcmapfzmap.put(sb.toString(), new DZFDouble[]{qcvo.getYearqc(),DZFDouble.ZERO_DBL});
				}
			}
		}
	}

}
