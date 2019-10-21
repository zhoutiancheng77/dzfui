package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DZFCollectionUtil;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.model.report.VoucherFseQryVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwbb.IRptSetService;
import com.dzf.zxkj.report.service.cwzb.IFsYeReport;
import com.dzf.zxkj.report.service.cwzb.IKMMXZReport;
import com.dzf.zxkj.report.utils.BeanUtils;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.dzf.zxkj.report.utils.VoUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

//import com.dzf.model.gl.gl_bdset.YntCpaccountVO;

/**
 * 发生及余额表
 * 
 * @author JasonLiu
 * 
 */
@Service("gl_rep_fsyebserv")
@SuppressWarnings("all")
public class FsJyeBReportImpl implements IFsYeReport {

	@Reference
	private IZxkjPlatformService zxkjPlatformService;

	@Autowired
	private SingleObjectBO singleObjectBO = null;

	@Autowired
	private IKMMXZReport gl_rep_kmmxjserv;



	public FseJyeVO[] getFsJyeVOs(QueryParamVO vo, Integer direction) throws DZFWarpException {
		Object[] obj = getFsJyeVOs1(vo);
		FseJyeVO[] resvos = (FseJyeVO[]) obj[0];
		if (resvos == null || resvos.length == 0) {
			return resvos;
		} else {
			DZFDouble qcjf =null;
			DZFDouble qcdf =null;
			DZFDouble qmjf =null;
			DZFDouble qmdf =null;
			for (FseJyeVO resvo : resvos) {
				 qcjf = VoUtils.getDZFDouble(resvo.getQcjf());
				 qcdf = VoUtils.getDZFDouble(resvo.getQcdf());
				 qmjf = VoUtils.getDZFDouble(resvo.getQmjf() );
				 qmdf = VoUtils.getDZFDouble(resvo.getQmdf() );
				/** 默认一方是空的 不然 有问题 */
				if (direction != null && direction.intValue() == 0) {
					if (qcjf.doubleValue() < 0) {
						resvo.setQcdf(qcjf.multiply(-1));
						resvo.setQcjf(null);
					}
					if (qcdf.doubleValue() < 0) {
						resvo.setQcjf(qcdf.multiply(-1));
						resvo.setQcdf(null);
					}

					if (qmjf.doubleValue() < 0) {
						resvo.setQmdf(qmjf.multiply(-1));
						resvo.setQmjf(null);
					}

					if (qmdf.doubleValue() < 0) {
						resvo.setQmjf(qmdf.multiply(-1));
						resvo.setQmdf(null);
					}
				}
			}
			return resvos;
		}
	}

	/**
	 *
	 * @param pk_corp
	 * @param period
	 * @param direction
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public Map<String, FseJyeVO> getFsJyeVOs(String pk_corp, String period, Integer direction) throws DZFWarpException {
		QueryParamVO vo = new QueryParamVO();
		vo.setSfzxm(DZFBoolean.TRUE);
		vo.setPk_corp(pk_corp);
		vo.setQjq(period);
		vo.setQjz(period);
		vo.setIshasjz(DZFBoolean.FALSE);
		vo.setIshassh(DZFBoolean.TRUE);
		vo.setCjq(1);
		vo.setCjz(6);

		FseJyeVO[] fvos = this.getFsJyeVOs(vo, 1);

		if (fvos == null || fvos.length == 0) {
			return new HashMap<>();
		}

		Map<String, FseJyeVO> fseJyeVOMap = Arrays.asList(fvos).stream().collect(Collectors.toMap(FseJyeVO::getPk_km, v -> v, (k1, k2) -> k1));

		return fseJyeVOMap;
	}

	public Object[] getFsJyeVOs1(QueryParamVO vo) throws DZFWarpException {
		String qjq = vo.getQjq();
		String qjz = vo.getQjz();
		DZFDate beginDate = vo.getBegindate1();
		qjq = StringUtil.isEmpty(qjq) ? beginDate.getYear() + "-" + beginDate.getStrMonth() : qjq;
		DZFDate edndate = vo.getEnddate();
		if(beginDate!=null && edndate!=null && beginDate.after(edndate)){
			throw new BusinessException("开始日期应在结束日期前!");
		}
		qjz = StringUtil.isEmpty(qjz) ? edndate.getYear() + "-" + edndate.getStrMonth() : qjz;
		vo.setBegindate1(new DZFDate(qjq + "-01"));
		vo.setEnddate(new DZFDate(qjz + "-10"));
		Map<String, YntCpaccountVO> mp = null;
		List<FseJyeVO> ls = new ArrayList<FseJyeVO>();
		
		Object[] obj = gl_rep_kmmxjserv.getKMMXZVOs1(vo, false);
		
		/** 处理科目明细数据 */
		mp = handleKmmxVOs(obj,vo, qjq, qjz, ls);

		FseJyeVO[] vos = null;
		if (ls != null && ls.size() > 0) {
			vos = ls.toArray(new FseJyeVO[0]);
		}
		return new Object[] { vos, mp };
	}

	private Map<String, YntCpaccountVO> handleKmmxVOs(Object[] obj ,QueryParamVO vo, String qjq, String qjz, List<FseJyeVO> ls) {
		Map<String, YntCpaccountVO> mp;
		
		HashMap<String, KmMxZVO> mapQcfs = new HashMap<String, KmMxZVO>();

		List<KmMxZVO> kvoList = ((List[]) obj[0])[0];
		mp = (Map<String, YntCpaccountVO>) obj[1];
		
		if(kvoList.size() > 0 && vo.getSfzxm()!=null && vo.getSfzxm().booleanValue()){
			HashMap<String, KmMxZVO> qcfzmap= (HashMap<String, KmMxZVO>)obj[3];
			HashMap<String, List<KmMxZVO>> fsfzmap = (HashMap<String, List<KmMxZVO>>)obj[4];
			HashMap<String, DZFDouble[]> corpbegqcmap = (HashMap<String, DZFDouble[]>)obj[5];
			List<KmMxZVO> fzkmmxvos = gl_rep_kmmxjserv.getResultVos(qcfzmap, fsfzmap,corpbegqcmap, 
					ReportUtil.getPeriods(vo.getBegindate1(), vo.getEnddate()), mp,vo.getPk_corp(),vo.getIshowfs(),vo.getKms_last(),vo.getBtotalyear());
			if(fzkmmxvos!=null && fzkmmxvos.size()>0){
				kvoList.addAll(fzkmmxvos);
			}
		}
		KmMxZVO[] kvos = kvoList.toArray(new KmMxZVO[0]);
		/** 排序listvo*/
		if(kvos != null && kvos.length>0){
			sortFs(kvos);
		}
		
		obj = null;
		int len = kvos == null ? 0 : kvos.length;

		String kmid = "";
		YntCpaccountVO kmv = null;
		DZFDouble ufd = null;
		FseJyeVO fvo = null;
		DZFDouble jfqcfs = null;
		DZFDouble dfqcfs = null;
		DZFDouble ybjfqcfs = DZFDouble.ZERO_DBL;
		DZFDouble ybdfqcfs = DZFDouble.ZERO_DBL;
		KmMxZVO kmqcfs = null;
		for (int i = 0; i < len; i++) {
			KmMxZVO kmmx = kvos[i];
			 jfqcfs = DZFDouble.ZERO_DBL;
			 dfqcfs = DZFDouble.ZERO_DBL;
			 /** 不是当前期间的则不进行计算 */
			 if(qjz.compareTo(kmmx.getRq().substring(0, 7))<0){
				 continue;
			 }
			if (mapQcfs != null && mapQcfs.size() > 0) {
				 kmqcfs = (KmMxZVO) mapQcfs.get(kmmx.getPk_accsubj());
				if (kmqcfs != null) {
					jfqcfs = VoUtils.getDZFDouble(kmqcfs.getJf());
					dfqcfs = VoUtils.getDZFDouble(kmqcfs.getDf());
					
					ybjfqcfs = VoUtils.getDZFDouble(kmqcfs.getYbjf());
					ybdfqcfs = VoUtils.getDZFDouble(kmqcfs.getYbdf());
				}
			}
			if (i == 0 || kmid.equals(kmmx.getPk_accsubj()) == false) {
				if (i > 0 && kmid.equals(kmmx.getPk_accsubj()) == false)
					ls.add(fvo);
				kmid = kmmx.getPk_accsubj();
				fvo = new FseJyeVO();
				if(StringUtil.isEmpty(fvo.getPk_currency()) && !StringUtil.isEmpty(vo.getPk_currency())){
					fvo.setPk_currency(vo.getPk_currency());
				}
				if(StringUtil.isEmpty(fvo.getCurrency()) && !StringUtil.isEmpty(vo.getCurrency())){
					fvo.setCurrency(vo.getCurrency());
				}
				fvo.setFsjf(kmmx.getJf());
				fvo.setFsdf(kmmx.getDf());
				fvo.setYbfsjf(kmmx.getYbjf());//原币
				fvo.setYbfsdf(kmmx.getYbdf());//原币贷方
				fvo.setFzlbcode(kmmx.getFzlbcode());//辅助类别code
				fvo.setAlevel(kmmx.getLevel());
				kmv = mp.get(kmid.split("_")[0]);
				String kmmc = kmid.length() > 24 ? kmv.getAccountname() + "_" + kmmx.getKm() : kmmx.getKm();
				fvo.setKmbm(kmmx.getKmbm());
				if(kmid.length()>24){
					fvo.setPk_km_parent(kmid.substring(0, 24));
				}else{
					fvo.setPk_km_parent(ReportUtil.getKmDirectParent(mp, kmmx.getPk_accsubj().substring(0, 24)));
				}
				fvo.setKmmc(kmmc);
				fvo.setPk_km(kmmx.getPk_accsubj());
				fvo.setKmlb(String.valueOf(kmv.getAccountkind()));
				fvo.setFx(kmmx.getFx());
			}
			if ("本月合计".equals(kmmx.getZy()) && ReportUtil.bSysZy(kmmx)) {
				ufd = kmmx.getJf();
				fvo.setEndfsjf(ufd);
				if (ufd != null)
					fvo.setFsjf(SafeCompute.add(fvo.getFsjf(), ufd));
				
				ufd = kmmx.getDf();
				fvo.setEndfsdf(ufd);
				if (ufd != null)
					fvo.setFsdf(SafeCompute.add(fvo.getFsdf(), ufd));
				
				/** ------------原币------------*/
				ufd = kmmx.getYbjf();
				fvo.setYbendfsjf(ufd);
				if(ufd!=null){
					fvo.setYbfsjf(SafeCompute.add(fvo.getYbfsjf(), ufd));
				}
				
				ufd= kmmx.getYbdf();
				fvo.setYbendfsdf(ufd);
				if(ufd!=null){
					fvo.setYbfsdf(SafeCompute.add(fvo.getYbfsdf(), ufd));
				}
				
				fvo.setEndrq(kmmx.getRq());
				
			} else if ( "期初余额".equals(kmmx.getZy()) && ReportUtil.bSysZy(kmmx) && kmmx.getRq().substring(0, 7).equals(qjq)) {
				if (kmmx.getFx().equals("借")) {
					fvo.setQcjf(kmmx.getYe());
					fvo.setYbqcjf(kmmx.getYbye());//原币
				} else{
					fvo.setQcdf(kmmx.getYe());
					fvo.setYbqcdf(kmmx.getYbye());//原币
				}
			} else if ("本年累计".equals(kmmx.getZy()) && ReportUtil.bSysZy(kmmx) && kmmx.getRq().substring(0, 7).equals(qjz)) {
				if (kmmx.getFx().equals("借")) {
					fvo.setQmjf(kmmx.getYe());
					
					fvo.setYbqmjf(kmmx.getYbye());//原币贷方
				} else {
					fvo.setQmdf(kmmx.getYe());
					
					fvo.setYbqmdf(kmmx.getYbye());//原币贷方
				}
			}
			if("本月合计".equals(kmmx.getZy()) && ReportUtil.bSysZy(kmmx) && kmmx.getRq().substring(0, 7).equals(qjz)){
				fvo.setLastmfsjf(fvo.getFsjf());
				fvo.setLastmfsdf(fvo.getFsdf());
			}else if( "期初余额".equals(kmmx.getZy()) && ReportUtil.bSysZy(kmmx) && kmmx.getRq().substring(0, 7).equals(qjz)){
				if (kmmx.getFx().equals("借")) {
					fvo.setLastmqcjf(kmmx.getYe());
				}else{
					fvo.setLastmqcdf(kmmx.getYe());
				}
			}
			
			if("本年累计".equals(kmmx.getZy())&& ReportUtil.bSysZy(kmmx) && kmmx.getRq().substring(0, 4).equals(qjz.substring(0,4))){
				fvo.setJftotal(SafeCompute.add(kmmx.getJf(), jfqcfs));
				fvo.setDftotal(SafeCompute.add(kmmx.getDf(), dfqcfs));
				
				fvo.setYbjftotal(SafeCompute.add(kmmx.getYbjf(), ybjfqcfs));
				fvo.setYbdftotal(SafeCompute.add(kmmx.getYbdf(), ybdfqcfs));
			}
		}
		if (fvo != null) {
			ls.add(fvo);
		}
		return mp;
	}

	private void sortFs(KmMxZVO[] kvos) {
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
				int i = o1.getKmbm().compareTo(o2.getKmbm());
				if(!StringUtil.isEmpty(o1.getPk_accsubj())
						&& !StringUtil.isEmpty(o2.getPk_accsubj())
						){/** 加一个科目排序(有可能编码一样) */
					if(i == 0 && !o1.getPk_accsubj().equals(o2.getPk_accsubj())){
						i = o1.getPk_accsubj().compareTo(o2.getPk_accsubj());
					} 
				}
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

				if (i == 0) {
					String pzh1 = o1.getPzh() == null ? "99999" : o1.getPzh();
					String pzh2 = o2.getPzh() == null ? "99999" : o2.getPzh();
					i = pzh1.compareTo(pzh2);
				}
				return i;
			}
		});
	}

	public HashMap<String, KmMxZVO> getKmFSByPeriodQC(QueryParamVO queryVo,
			String kmwhere) throws DZFWarpException {
		SQLParameter sqlparameter = new SQLParameter();
		sqlparameter.addParam(queryVo.getPk_corp());
		String corpsql = "select begindate from bd_corp where isnull(dr,0)=0 and  pk_corp= ? ";
		DZFDate corpdate = new DZFDate((String)singleObjectBO.executeQuery(corpsql, sqlparameter, new ColumnProcessor()));
		

		if (corpdate.getYear() > queryVo.getBegindate1().getYear() || (corpdate.getYear() == queryVo.getBegindate1().getYear() && corpdate
						.getMonth() > queryVo.getBegindate1().getMonth())) {
		} else {
			SQLParameter parameter = new SQLParameter();
			String sql1 = getQuerySqlByPeriodForQC(kmwhere,
					queryVo.getPk_corp(),parameter);
			List<KmMxZVO> result1 = (List<KmMxZVO>) singleObjectBO.executeQuery(sql1, parameter, new BeanListProcessor(KmMxZVO.class));
			HashMap<String, KmMxZVO> map = new HashMap<String, KmMxZVO>();

			if (result1 != null && !result1.isEmpty()) {
				KmMxZVO[] vos = (KmMxZVO[]) result1.toArray(new KmMxZVO[0]);
				for (KmMxZVO vo : vos) {
					if ("0".equals(vo.getFx())) {
						vo.setFx("借");
					} else {
						vo.setFx("贷");
					}
					map.put(vo.getPk_accsubj(), vo);
				}
			}
			return map;
		}
		return null;

	}

	protected String getQuerySqlByPeriodForQC(String kmwhere, String pk_corp,SQLParameter parameter) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select a.accountcode kmbm, a.pk_corp_account as pk_accsubj,sum(isnull(b.yearjffse,0)) as jf,sum(isnull(b.yeardffse,0)) as df,a.direction as fx");
		sql.append(" from YNT_QCYE b inner join ynt_cpaccount a on b.pk_accsubj = a.pk_corp_account");
		sql.append(" where ").append(kmwhere).append( " and b.pk_corp= ? and isnull(b.dr,0)=0 ");
		sql.append(" and (isnull(b.yearjffse,0) <>0 or isnull(b.yeardffse,0) <> 0)");
		sql.append(" group by  a.pk_corp_account,a.accountcode, a.direction");
		return sql.toString();
	}

	@Override
	public Object[] getYearFsJyeVOs(String year, String pk_corp,Object[] qryobjs,String rptsource) throws DZFWarpException {
		CorpVO cpvo = zxkjPlatformService.queryCorpByPk(pk_corp);
		QueryParamVO vo = new QueryParamVO();
		String qjq = year+"-01";//年初
		String qjz = year+"-12";//年末
		DZFDate beginDate =new DZFDate(qjq + "-01");
		qjq = StringUtil.isEmpty(qjq) ? beginDate.getYear() + "-" + beginDate.getStrMonth() : qjq;

		DZFDate edndate =new DZFDate(qjz+"-01");
		qjz = StringUtil.isEmpty(qjz) ? edndate.getYear() + "-" + edndate.getStrMonth() : qjz;
		vo.setBegindate1(new DZFDate(qjq + "-01"));
		vo.setEnddate(new DZFDate(qjz + "-10"));
		vo.setXswyewfs(DZFBoolean.FALSE);
		vo.setXsyljfs(DZFBoolean.TRUE);
		vo.setIshasjz(DZFBoolean.FALSE);
		vo.setPk_corp(pk_corp);
		if(qryobjs== null || qryobjs.length == 0){
			if("zcfz".equals(rptsource)){
				vo.setFirstlevelkms(rptsetser.queryZcfzKmFromDaima(cpvo.getPk_corp(),null,null));
			}else if("lrb".equals(rptsource)){
				vo.setFirstlevelkms(rptsetser.queryLrbKmsFromDaima(cpvo.getPk_corp(),null));
			}
		}
		vo.setRptsource(rptsource);
		
		List<String> periods = ReportUtil.getPeriods(beginDate, edndate);
		
		return getEveryMonthFs(periods, pk_corp, vo,qryobjs);
	}

	/**
	 * 获取每个月的发生数据
	 * @param year
	 * @param pk_corp
	 * @param vo
	 * @return
	 */
	private Object[] getEveryMonthFs(List<String> periods , String pk_corp, QueryParamVO vo,Object[] qryobjs) {
		Object[] obj = null;
		if(qryobjs == null || qryobjs.length ==0){
			obj = gl_rep_kmmxjserv.getKMMXZVOs1(vo, false);
		}else{
			obj = qryobjs;
		}
		HashMap<String, KmMxZVO> mapQcfs = new HashMap<String, KmMxZVO>();
		List<KmMxZVO> kvoList = ((List[]) obj[0])[0];
		String kmwhere = gl_rep_kmmxjserv.getKmTempTable(vo);
		Map<String, YntCpaccountVO> mp =gl_rep_kmmxjserv.getKM(pk_corp, kmwhere);
		
		if(kvoList.size() > 0 && vo.getSfzxm()!=null && vo.getSfzxm().booleanValue()){
			HashMap<String, KmMxZVO> qcfzmap= (HashMap<String, KmMxZVO>)obj[3];
			HashMap<String, List<KmMxZVO>> fsfzmap = (HashMap<String, List<KmMxZVO>>)obj[4];
			HashMap<String, DZFDouble[]> corpbegqcmap = (HashMap<String, DZFDouble[]>)obj[5];
			List<KmMxZVO> fzkmmxvos = gl_rep_kmmxjserv.getResultVos(qcfzmap,
					fsfzmap,corpbegqcmap, ReportUtil.getPeriods(vo.getBegindate1(),
							vo.getEnddate()), mp,vo.getPk_corp(),vo.getIshowfs(),vo.getKms_last(),vo.getBtotalyear());
			if(fzkmmxvos!=null && fzkmmxvos.size()>0){
				kvoList.addAll(fzkmmxvos);
			}
		}
		KmMxZVO[] kvos = kvoList.toArray(new KmMxZVO[0]);
		int len = kvos == null ? 0 : kvos.length;
		
		String kmid = "";
		YntCpaccountVO kmv = null;
		DZFDouble ufd = null;
		FseJyeVO fvo = null;

		//取每个月的期初/发生/期末余额
		//循环12次
		Map<String,List<FseJyeVO>> monthmap = new HashMap<String,List<FseJyeVO>>();
		String rq =null;
		DZFDouble jfqcfs =null;
		DZFDouble dfqcfs = null;
		KmMxZVO kmqcfs = null;
		String qjvalue =null;
		List<FseJyeVO> reslist =null;
		for(int k=0;k<periods.size();k++){
			monthmap.put(periods.get(k),new ArrayList<FseJyeVO>());
			List<FseJyeVO> ls = new ArrayList<FseJyeVO>();
			for (int i = 0; i < len; i++) {
				rq = periods.get(k);
				if(!kvos[i].getRq().substring(0,7).equals(rq)){
					continue;
				}
				jfqcfs = DZFDouble.ZERO_DBL;
				dfqcfs = DZFDouble.ZERO_DBL;
				if (mapQcfs != null && mapQcfs.size() > 0) {
					 kmqcfs = (KmMxZVO) mapQcfs.get(kvos[i].getPk_accsubj());
					if (kmqcfs != null) {
						jfqcfs = kmqcfs.getJf() == null ? DZFDouble.ZERO_DBL : kmqcfs.getJf();
						dfqcfs = kmqcfs.getDf() == null ? DZFDouble.ZERO_DBL : kmqcfs.getDf();
					}
				}
				if (i == 0 || kmid.equals(kvos[i].getPk_accsubj()) == false) {
					if (i > 0 && kmid.equals(kvos[i].getPk_accsubj()) == false) {
						if (fvo != null) {
							ls.add(fvo);
						}
					}
					kmid =  kvos[i].getPk_accsubj();
					fvo = new FseJyeVO();
					fvo.setFsjf(kvos[i].getJf());
					fvo.setFsdf(kvos[i].getDf());
					fvo.setAlevel(kvos[i].getLevel());
					fvo.setRq(kvos[i].getRq().substring(0,7));
					kmv = mp.get(kmid.split("_")[0]);
					fvo.setDirection(kmv.getDirection());
					String kmmc = kmid.length() > 24 ? kmv.getAccountname() + "_" + kvos[i].getKm() : kvos[i].getKm();
					fvo.setKmbm(kvos[i].getKmbm());
					fvo.setKmmc(kmmc);
					fvo.setPk_km(kvos[i].getPk_accsubj());
					fvo.setKmlb(String.valueOf(kmv.getAccountkind()));
					fvo.setFx(kvos[i].getFx());
				}
				if ("本月合计".equals(kvos[i].getZy()) && ReportUtil.bSysZy(kvos[i])) {
					ufd = kvos[i].getJf();
					fvo.setEndfsjf(ufd);
					if (ufd != null)
						fvo.setFsjf(SafeCompute.add(fvo.getFsjf(), ufd) );
					ufd = kvos[i].getDf();
					fvo.setEndfsdf(ufd);
					if (ufd != null)
						fvo.setFsdf(SafeCompute.add(fvo.getFsdf(), ufd));
					fvo.setEndrq(kvos[i].getRq());
				} else if ("期初余额".equals(kvos[i].getZy()) && ReportUtil.bSysZy(kvos[i]) ) {
					if (kvos[i].getFx().equals("借")) {
						fvo.setQcjf(kvos[i].getYe());
					} else
						fvo.setQcdf(kvos[i].getYe());
				} else if ("本年累计".equals(kvos[i].getZy())  && ReportUtil.bSysZy(kvos[i]) ) {
					if (kvos[i].getFx().equals("借")) {
						fvo.setQmjf(kvos[i].getYe());
					} else {
						fvo.setQmdf(kvos[i].getYe());
					}
					fvo.setJftotal(SafeCompute.add(kvos[i].getJf(), jfqcfs));
					fvo.setDftotal(SafeCompute.add(kvos[i].getDf(), dfqcfs));
				}
				
				if("本月合计".equals(kvos[i].getZy()) && ReportUtil.bSysZy(kvos[i]) && kvos[i].getRq().substring(0, 7).equals(periods.get(k))){
					fvo.setLastmfsjf(fvo.getFsjf());
					fvo.setLastmfsdf(fvo.getFsdf());
				}else if( "期初余额".equals(kvos[i].getZy()) && ReportUtil.bSysZy(kvos[i]) && kvos[i].getRq().substring(0, 7).equals(periods.get(k))){
					if (kvos[i].getFx().equals("借")) {
						fvo.setLastmqcjf(kvos[i].getYe());
					}else{
						fvo.setLastmqcdf(kvos[i].getYe());
					}
				}
			}
			if (fvo != null) {
				ls.add(fvo);
			}
			FseJyeVO[] vos = null;/** 每个月的期初期末余额 */
			if (ls != null && ls.size() > 0) {
				vos = ls.toArray(new FseJyeVO[0]);
			}
			if(vos!=null){
				for(FseJyeVO fsevo:vos){
					 qjvalue =fsevo.getRq(); 
					if(!monthmap.containsKey(qjvalue)){
						reslist = new ArrayList<FseJyeVO>();
						reslist.add(fsevo);
						monthmap.put(qjvalue, reslist);
					}else{
						reslist =monthmap.get(qjvalue);
						reslist.add(fsevo);
						monthmap.put(qjvalue, reslist);
					}
				}
			} 
		}

		Object[] objs = new Object[2];
		objs[0]= monthmap;
		objs[1]= mp;
		return objs;
	}
	@Override
	public Object[] getYearFsJyeVOsLrbquarter(String year, String pk_corp,DZFDate corpdate,DZFBoolean ishasjz,String rptsource ) throws DZFWarpException {
		CorpVO cpvo = zxkjPlatformService.queryCorpByPk(pk_corp);
		QueryParamVO vo = new QueryParamVO();
		int yearint = Integer.parseInt(year);
		String qjq  = "";
		String qjz ="";
		if(corpdate.getYear()==yearint){
			qjq = year+ ((corpdate.getMonth()<10) ? "-0"+corpdate.getMonth():"-"+corpdate.getMonth());//年初
			qjz = year+"-12";//年末
		}
		if(corpdate.getYear()>yearint){
			throw new BusinessException("建账日期应在查询日期之前！");
		}
		if(corpdate.getYear()<yearint){
			qjq = year+"-01";//年初
			qjz = year+"-12";//年末
		}
		DZFDate beginDate =new DZFDate(qjq + "-01");
		qjq = StringUtil.isEmpty(qjq) ? beginDate.getYear() + "-" + beginDate.getStrMonth() : qjq;

		DZFDate edndate =new DZFDate(qjz+"-01");
		qjz = StringUtil.isEmpty(qjz) ? edndate.getYear() + "-" + edndate.getStrMonth() : qjz;
		vo.setBegindate1(new DZFDate(qjq + "-01"));
		vo.setEnddate(new DZFDate(qjz + "-10"));
		vo.setXswyewfs(DZFBoolean.FALSE);
		vo.setXsyljfs(DZFBoolean.TRUE);
		vo.setIshasjz(ishasjz);
		vo.setPk_corp(pk_corp);
		if("zcfz".equals(rptsource)){
			vo.setFirstlevelkms(rptsetser.queryZcfzKmFromDaima(cpvo.getPk_corp(),null,null));
		}else if("lrb".equals(rptsource)){
			vo.setFirstlevelkms(rptsetser.queryLrbKmsFromDaima(cpvo.getPk_corp(),null));
		}
		vo.setRptsource(rptsource);
		
		KmMxZVO[]  obj = gl_rep_kmmxjserv.getKMMXZVOs(vo,null);
		HashMap<String, KmMxZVO> mapQcfs = new HashMap<String, KmMxZVO>();
		KmMxZVO[] kvos = obj;
		String kmwhere = gl_rep_kmmxjserv.getKmTempTable(vo);
		Map<String, YntCpaccountVO> mp =gl_rep_kmmxjserv.getKM(pk_corp, kmwhere);
		obj = null;
		int len = kvos == null ? 0 : kvos.length;
		List<FseJyeVO> ls = new ArrayList<FseJyeVO>();

		String kmid = "";
		YntCpaccountVO kmv = null;
		DZFDouble ufd = null;
		FseJyeVO fvo = null;

		/** 取每个月的期初/发生/期末余额 */
		/** 循环12次 */
		Map<String,List<FseJyeVO>> monthmap = new HashMap<String,List<FseJyeVO>>();
		String rq =null;
		DZFDouble jfqcfs = null;
		DZFDouble dfqcfs = null;
		KmMxZVO kmqcfs =null;
		String qjvalue =null;
		List<FseJyeVO> reslist =null;
		for(int k=1;k<13;k++){
			for (int i = 0; i < len; i++) {
				 rq = year+(k>9?"-"+k:("-0"+k));
				if(!kvos[i].getRq().substring(0,7).equals(rq)){
					continue;
				}
				 jfqcfs = DZFDouble.ZERO_DBL;
				 dfqcfs = DZFDouble.ZERO_DBL;
				if (mapQcfs != null && mapQcfs.size() > 0) {
					 kmqcfs = (KmMxZVO) mapQcfs.get(kvos[i].getPk_accsubj());
					if (kmqcfs != null) {
						jfqcfs = kmqcfs.getJf() == null ? DZFDouble.ZERO_DBL : kmqcfs.getJf();
						dfqcfs = kmqcfs.getDf() == null ? DZFDouble.ZERO_DBL : kmqcfs.getDf();
					}
				}
				if (i == 0 || kmid.equals(kvos[i].getPk_accsubj()) == false) {
					if (i > 0 && kmid.equals(kvos[i].getPk_accsubj()) == false){
						if(fvo!=null){
							ls.add(fvo);
						}
					}
					kmid = kvos[i].getPk_accsubj();
					fvo = new FseJyeVO();
					fvo.setFsjf(kvos[i].getJf());
					fvo.setFsdf(kvos[i].getDf());
					fvo.setAlevel(kvos[i].getLevel());
					fvo.setRq(kvos[i].getRq().substring(0,7));
					kmv = mp.get(kmid);
					fvo.setDirection(kmv.getDirection());
					fvo.setKmbm(kmv.getAccountcode());
					fvo.setKmmc(kmv.getAccountname());
					fvo.setPk_km(kmv.getPk_corp_account());
					fvo.setKmlb(String.valueOf(kmv.getAccountkind()));
					fvo.setFx(kvos[i].getFx());
				}
				if ("本月合计".equals(kvos[i].getZy()) && ReportUtil.bSysZy(kvos[i])) {
					ufd = kvos[i].getJf();
					fvo.setEndfsjf(ufd);
					if (ufd != null)
						fvo.setFsjf(fvo.getFsjf().add(ufd));
					ufd = kvos[i].getDf();
					fvo.setEndfsdf(ufd);
					if (ufd != null)
						fvo.setFsdf(fvo.getFsdf().add(ufd));
					fvo.setEndrq(kvos[i].getRq());
				} else if ("期初余额".equals(kvos[i].getZy()) && ReportUtil.bSysZy(kvos[i]) ) {
					if (kvos[i].getFx().equals("借")) {
						fvo.setQcjf(kvos[i].getYe());
					} else
						fvo.setQcdf(kvos[i].getYe());
				} else if ("本年累计".equals(kvos[i].getZy()) && ReportUtil.bSysZy(kvos[i]) ) {
					if (kvos[i].getFx().equals("借")) {
						fvo.setQmjf(kvos[i].getYe());
					} else {
						fvo.setQmdf(kvos[i].getYe());
					}
					fvo.setJftotal(kvos[i].getJf().add(jfqcfs));
					fvo.setDftotal(kvos[i].getDf().add(dfqcfs));
				}
			}
			if (fvo != null) {
				ls.add(fvo);
			}
			FseJyeVO[] vos = null;//每个月的期初期末余额
			if (ls != null && ls.size() > 0) {
				vos = ls.toArray(new FseJyeVO[0]);
			}
			if(vos!=null){
				for(FseJyeVO fsevo:vos){
					 qjvalue =fsevo.getRq(); 
					if(!monthmap.containsKey(qjvalue)){
						reslist = new ArrayList<FseJyeVO>();
						reslist.add(fsevo);
						monthmap.put(qjvalue, reslist);
					}else{
						reslist =monthmap.get(qjvalue);
						reslist.add(fsevo);
						monthmap.put(qjvalue, reslist);
					}
				}
			}
		}

		Object[] objs = new Object[2];
		objs[0]= monthmap;
		objs[1]= mp;
		return objs;
	}
	
	@Autowired
	private IRptSetService rptsetser;

	@Override
	public Object[] getEveryPeriodFsJyeVOs(DZFDate startdate, DZFDate enddate, String pk_corp,Object[] objs,String rptsource,DZFBoolean ishasjz) throws DZFWarpException {
		
		CorpVO cpvo = zxkjPlatformService.queryCorpByPk(pk_corp);
		/** 当前年从1月份开始查询 */
 		if(cpvo.getBegindate().getYear() == startdate.getYear()){
 			startdate =  new DZFDate(cpvo.getBegindate().getYear()+"-01"+"-01");
		}
		
		QueryParamVO vo = new QueryParamVO();
		/** 年初 */
		String qjq = DateUtils.getPeriod(startdate);
		/** 年末 */
		String qjz = DateUtils.getPeriod(enddate);
		DZFDate beginDate =new DZFDate(qjq + "-01");
		qjq = StringUtil.isEmpty(qjq) ? beginDate.getYear() + "-" + beginDate.getStrMonth() : qjq;

		DZFDate edndate =new DZFDate(qjz+"-01");
		qjz = StringUtil.isEmpty(qjz) ? edndate.getYear() + "-" + edndate.getStrMonth() : qjz;
		
		vo.setBegindate1(new DZFDate(qjq + "-01"));
		vo.setEnddate(new DZFDate(qjz + "-10"));
		vo.setXswyewfs(DZFBoolean.FALSE);
		vo.setXsyljfs(DZFBoolean.TRUE);
		vo.setIshasjz(ishasjz == null ?DZFBoolean.FALSE:ishasjz);
		/** 本年累计 */
		vo.setBtotalyear(DZFBoolean.TRUE);
		if(objs == null || objs.length == 0){
			/** 资产负债 */
			if("zcfz".equals(rptsource)){
				vo.setFirstlevelkms(rptsetser.queryZcfzKmFromDaima(cpvo.getPk_corp(),null,null));
			}else if("lrb".equals(rptsource)){
				vo.setFirstlevelkms(rptsetser.queryLrbKmsFromDaima(cpvo.getPk_corp(),null));
			}
		}
		vo.setRptsource(rptsource);
		
		vo.setPk_corp(pk_corp);
		vo.setSfzxm(DZFBoolean.TRUE);
		vo.setQjq(qjq);
		vo.setQjz(qjz);
		
		List<String> periods =ReportUtil.getPeriods(startdate, enddate);
		
		return getEveryMonthFs(periods, pk_corp, vo,objs);
	}

	@Override
	public FseJyeVO[] getBetweenPeriodFs(DZFDate begdate, DZFDate enddate,
			Map<String,List<FseJyeVO>> periodfsmap) throws DZFWarpException {
		
		List<FseJyeVO>  result = new ArrayList<FseJyeVO>();
		
		/** 按照科目来记录 */
		Map<String,FseJyeVO> kmmap = new HashMap<String,FseJyeVO>();
		
		List<String> periods = ReportUtil.getPeriods(begdate, enddate);
		
		for(String period:periods){
			
			List<FseJyeVO> periodlist = periodfsmap.get(period);
			
			if(periodlist == null){
			   continue;
			}
			for(int i=0;i<periodlist.size();i++){
				if(kmmap.containsKey(periodlist.get(i).getKmbm())){
					FseJyeVO tempvo = kmmap.get(periodlist.get(i).getKmbm());
					tempvo.setFsjf(SafeCompute.add(tempvo.getFsjf(), periodlist.get(i).getFsjf()));
					tempvo.setFsdf(SafeCompute.add(tempvo.getFsdf(), periodlist.get(i).getFsdf()));
					tempvo.setQmjf(periodlist.get(i).getQmjf());
					tempvo.setQmdf(periodlist.get(i).getQmdf());
					tempvo.setJftotal(periodlist.get(i).getJftotal());
					tempvo.setDftotal(periodlist.get(i).getDftotal());
					tempvo.setEndfsjf(periodlist.get(i).getEndfsjf());
					tempvo.setEndfsdf(periodlist.get(i).getEndfsdf());
					tempvo.setLastmfsjf(periodlist.get(i).getFsjf());
					tempvo.setLastmfsdf(periodlist.get(i).getFsdf());
					tempvo.setLastmqcjf(periodlist.get(i).getQcjf());
					tempvo.setLastmqcdf(periodlist.get(i).getQcdf());
				}else{
					FseJyeVO tempvo = new FseJyeVO();
					BeanUtils.copyNotNullProperties(periodlist.get(i), tempvo);
					tempvo.setQcjf(DZFDouble.ZERO_DBL);
					tempvo.setQcdf(DZFDouble.ZERO_DBL);
					if(periodlist.get(i).getRq().substring(0, 7).equals(DateUtils.getPeriod(begdate))){
						tempvo.setQcjf(periodlist.get(i).getQcjf());
						tempvo.setQcdf(periodlist.get(i).getQcdf());
					}
					tempvo.setLastmfsjf(periodlist.get(i).getFsjf());
					tempvo.setLastmfsdf(periodlist.get(i).getFsdf());
					tempvo.setLastmqcjf(periodlist.get(i).getQcjf());
					tempvo.setLastmqcdf(periodlist.get(i).getQcdf());
					kmmap.put(periodlist.get(i).getKmbm(), tempvo);
				}
			}
		}
		
		for(Map.Entry<String, FseJyeVO> entry:kmmap.entrySet()){
			result.add(entry.getValue());
		}
		
		
		return result.toArray(new FseJyeVO[0]);
	}

	@Override
	public Map<String,Map<String,Double>> getVoucherFseQryVOListByPkCorpAndKmBetweenPeriod(String pk_corp, YntCpaccountVO[] yntCpaccountVOS, String beginPeriod, String endPeriod) throws DZFWarpException {
		Map<String,String> codeMap = Arrays.asList(yntCpaccountVOS).stream().collect(Collectors.toMap(YntCpaccountVO::getPk_corp_account, YntCpaccountVO::getAccountcode,(k1, k2)->k1));
		List<String> accountCodeList = Arrays.asList(yntCpaccountVOS).stream().map(t->t.getAccountcode()).collect(Collectors.toList());
		//构建sql where条件 REGEXP_LIKE
		String codeLikeStr = createRegexpLikeCondition(accountCodeList);
		//查询发生额
		List<VoucherFseQryVO> voucherFseQryVOS = queryFseByCorpAndKmbmAndPeriod(beginPeriod, endPeriod, pk_corp, codeLikeStr);

		Map<String,Map<String,Double>> result = new HashMap<>();
        //处理返回结果  [科目编码(String):[期间(例：2019-01):发生额(Double)]]
		for(YntCpaccountVO yntCpaccountVO : yntCpaccountVOS){
			List<VoucherFseQryVO> voucherFseQryList = voucherFseQryVOS.stream().filter(vo -> vo.getVcode().startsWith(yntCpaccountVO.getAccountcode())).collect(Collectors.toList());
			if(!voucherFseQryList.isEmpty()){
				Map<String, Double>  rs = voucherFseQryList.stream().collect(Collectors.groupingBy(VoucherFseQryVO::getPeriod, Collectors.summingDouble(VoucherFseQryVO::getMnyValue)));
				result.put(yntCpaccountVO.getAccountcode(),rs);
			}
		}

		return result;
	}

	/**
	 *
	 * @param kmbmList 科目编码集合
	 * @return 编码过滤条件
	 * @description 创建sql where RegexpLike查询条件
	 */
	private String createRegexpLikeCondition(List<String> kmbmList) {

		if(DZFCollectionUtil.isEmpty(kmbmList)){
			return null;
		}
		kmbmList.sort((s1, s2) -> s1.compareTo(s2));
		Set<String> codeSet = new HashSet<>();
		int parentIndex = 0;
		codeSet.add(kmbmList.get(0));
		for (int i = 1; i < kmbmList.size(); i++) {
			String kmbm = kmbmList.get(i);
			if (kmbm.startsWith(kmbmList.get(parentIndex))) {
				continue;
			} else {
				codeSet.add(kmbm);
				parentIndex = i;
			}
		}
		StringBuilder sb = new StringBuilder("'(^")
				.append(StringUtils.join(codeSet, "|"))
				.append(")'");
		return sb.toString();
	}

	/**
	 * @param previousYearPeriod 起始区间
	 * @param endPeriod 结束区间
	 * @param pk_corp 公司编码
	 * @param codeLikeStr 编码过滤条件
	 * @return { 凭证发生额 }
	 * @description 根据时间区间、公司编码、科目编码查询发生额
	 */
	private List<VoucherFseQryVO> queryFseByCorpAndKmbmAndPeriod(String previousYearPeriod, String endPeriod, String pk_corp, String codeLikeStr) {
		SQLParameter sp = new SQLParameter();

		StringBuffer sf = new StringBuffer();
		sf.append("select b.pk_corp, sum(decode(c.direction, '0', b.jfmny, '1', b.dfmny, b.jfmny)) as mny, b.vcode, h.period, c.direction");
		sf.append(" from ynt_tzpz_b b  join ynt_tzpz_h h on b.pk_tzpz_h = h.pk_tzpz_h  join ynt_cpaccount c on b.pk_accsubj = c.pk_corp_account ");
		sf.append(" where 1 = 1 ");
		if(!StringUtil.isEmpty(codeLikeStr)){
			sf.append(" and  regexp_like(b.vcode,"+codeLikeStr+")");
		}
		if(!StringUtil.isEmpty(previousYearPeriod)){
			sp.addParam(previousYearPeriod);
			sf.append(" and h.period > ?");
		}
		if(!StringUtil.isEmpty(endPeriod)){
			sp.addParam(endPeriod);
			sf.append(" and h.period <= ?");
		}
		sf.append(" and nvl(h.dr, 0) = 0");
		sf.append(" and nvl(b.dr, 0) = 0");
		sf.append("	and nvl(c.dr, 0) = 0");
		if(!StringUtil.isEmpty(pk_corp)){
			sp.addParam(pk_corp);
			sf.append(" and h.pk_corp = ?");
		}
		sf.append("group by b.pk_corp, b.vcode, h.period, c.direction");
		return (List<VoucherFseQryVO>)singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(VoucherFseQryVO.class));
	}

	@Override
	public Object[] getYearFsJyeVOs(String year, String pk_corp, String xmmcid,Object[] objs,String rptsource,DZFBoolean ishasjz) throws DZFWarpException {
		QueryParamVO vo = new QueryParamVO();
		/** 年初 */
		String qjq = year+"-01";
		/** 年末 */
		String qjz = year+"-12";
		DZFDate beginDate =new DZFDate(qjq + "-01");
		qjq = StringUtil.isEmpty(qjq) ? beginDate.getYear() + "-" + beginDate.getStrMonth() : qjq;

		DZFDate edndate =new DZFDate(qjz+"-01");
		qjz = StringUtil.isEmpty(qjz) ? edndate.getYear() + "-" + edndate.getStrMonth() : qjz;
		vo.setBegindate1(new DZFDate(qjq + "-01"));
		vo.setEnddate(new DZFDate(qjz + "-10"));
		vo.setXswyewfs(DZFBoolean.FALSE);
		vo.setXsyljfs(DZFBoolean.TRUE);
		vo.setIshasjz(ishasjz == null  ? DZFBoolean.FALSE:ishasjz);
		vo.setPk_corp(pk_corp);
		vo.setRptsource(rptsource);
		if(!StringUtil.isEmpty(xmmcid)){
			vo.setSfzxm(DZFBoolean.TRUE);
		}
		
		List<String> periods = ReportUtil.getPeriods(beginDate, edndate);
		
		return getEveryMonthFs(periods, pk_corp, vo,objs);
	}

	@Override
	public FseJyeVO[] getFsJyeVOs(QueryParamVO vo, Object[] qryobjs) throws DZFWarpException {
		Object[] obj = getFsJyeVOs1(vo,qryobjs);
		FseJyeVO[] resvos = (FseJyeVO[]) obj[0];
		return resvos;
	}

	@Override
	public Object[] getFsJyeVOs1(QueryParamVO vo, Object[] qryobjs) throws DZFWarpException {
		String qjq = vo.getQjq();
		String qjz = vo.getQjz();
		DZFDate beginDate = vo.getBegindate1();
		qjq = StringUtil.isEmpty(qjq) ? beginDate.getYear() + "-" + beginDate.getStrMonth() : qjq;
		DZFDate edndate = vo.getEnddate();
		if(beginDate!=null && edndate!=null && beginDate.after(edndate)){
			throw new BusinessException("开始日期应在结束日期前!");
		}
		qjz = StringUtil.isEmpty(qjz) ? edndate.getYear() + "-" + edndate.getStrMonth() : qjz;
		vo.setBegindate1(new DZFDate(qjq + "-01"));
		vo.setEnddate(new DZFDate(qjz + "-10"));
		Map<String, YntCpaccountVO> mp = null;
		List<FseJyeVO> ls = new ArrayList<FseJyeVO>();
		
		Object[] obj = null;
		if(qryobjs!=null){
			obj = qryobjs;
		}else{
			obj = gl_rep_kmmxjserv.getKMMXZVOs1(vo, false);
		}
		
		mp = handleKmmxVOs(obj,vo, qjq, qjz, ls);//处理科目明细数据

		FseJyeVO[] vos = null;
		if (ls != null && ls.size() > 0) {
			vos = ls.toArray(new FseJyeVO[0]);
		}
		return new Object[] { vos, mp };
	}
}
