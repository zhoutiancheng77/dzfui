package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.report.service.cwzb.IKMMXZReport;
import com.dzf.zxkj.report.service.cwzb.IKMZZReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 科目总账
 * 
 * @author JasonLiu
 * 
 */
@Service("gl_rep_kmzjserv")
public class KMZZReportImpl implements IKMZZReport {

	@Autowired
	private IKMMXZReport gl_rep_kmmxjserv;
	
	/**
	 * 科目总账
	 */
	public KmZzVO[] getKMZZVOs(QueryParamVO vo, Object[] kmmx_objs) throws DZFWarpException {
		vo.setIsLevel(DZFBoolean.FALSE);
		/** 所选公司的建账日期 */
		Object[] obj = null;
		
		if(kmmx_objs == null || kmmx_objs.length ==0 ){
			obj = gl_rep_kmmxjserv.getKMMXZVOs1(vo, false);
		}else{
			obj = kmmx_objs;
		}
		
		List[] liststemp = (List[]) obj[0];
		
		List<KmMxZVO> mxlists = liststemp[0];
		
		if(vo.getSfzxm()!=null && vo.getSfzxm().booleanValue()){
			HashMap<String, KmMxZVO> qcfzmap= (HashMap<String, KmMxZVO>)obj[3];
			
			HashMap<String, List<KmMxZVO>> fsfzmap = (HashMap<String, List<KmMxZVO>>)obj[4];
			
			Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>)obj[1];
			
			HashMap<String, DZFDouble[]> corpbegqcmap = (HashMap<String, DZFDouble[]>)obj[5];
			
			List<KmMxZVO> fzkmmxvos=gl_rep_kmmxjserv.getResultVos(qcfzmap, fsfzmap,
					corpbegqcmap, ReportUtil.getPeriods(vo.getBegindate1(), vo.getEnddate()),
					mp,vo.getPk_corp(),vo.getIshowfs(),vo.getKms_last(),vo.getBtotalyear());
			
			if(fzkmmxvos!=null && fzkmmxvos.size()>0){
				for(KmMxZVO votemp:fzkmmxvos){
					mxlists.add(votemp);
				}
			}
		}
		
		/** 重新排序 */
		obj[0]= new List[]{mxlists};
		
		sortVos(obj);
		List[] lists = (List[]) obj[0];
		Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>) obj[1];
		obj = null;
		List<KmZzVO> list = null;
		list = new ArrayList<KmZzVO>();
		int len = lists == null ? 0 : lists.length;
		List<KmMxZVO> ls = null;
		Integer cjq = vo.getCjq();
		Integer cjz = vo.getCjz();
		YntCpaccountVO cpavo= null;
		for (int j = 0; j < len; j++) {
			ls = lists[j];
			if (ls == null)
				continue;
			for (KmMxZVO mxvo : ls) {
				/** 层级过滤 */
				if(mxvo == null || mxvo.getLevel() == null || cjq == null || cjz == null)
					continue;
				if (mxvo.getLevel().intValue() >= cjq.intValue() && (cjz == null || mxvo.getLevel().intValue() <= cjz.intValue())) {
					if (mxvo != null &&  (mxvo.getPzh() == null || mxvo.getPzh().length() == 0)) {
						/** 摘要不能为空，为了过滤期初，本月，本年累计 */
						if(!StringUtil.isEmpty(mxvo.getZy())){
							/** 判断是否末级 */
							cpavo= mp.get(mxvo.getPk_accsubj().substring(0, 24));
							/** 如果是否末级是Y */
							if(vo.getIsleaf()!=null &&vo.getIsleaf().booleanValue()){
								if(cpavo.getIsleaf()!=null && cpavo.getIsleaf().booleanValue()){
									list.add(convert(mxvo, mp, vo));
								}
							}else{
								list.add(convert(mxvo, mp, vo));
							}
						}
					}
				}
			}

		}

		return list.toArray(new KmZzVO[0]);
	}

	/**
	 * 重新排序
	 * 
	 * @param obj
	 */
	private void sortVos(Object[] obj) {
		List[] lists = (List[]) obj[0];
		List<KmMxZVO> list = null;
		list = new ArrayList<KmMxZVO>();
		int len = lists == null ? 0 : lists.length;
		for (int j = 0; j < len; j++) {
			if (lists[j] != null) {
				list.addAll(lists[j]);
				lists[j] = null;
			}
		}
		KmMxZVO[] kvos = list.toArray(new KmMxZVO[0]);
		list = null;
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");  
		java.util.Arrays.sort(kvos, new Comparator<KmMxZVO>() {
			private int get(KmMxZVO o1) {
				int i = 0;
				if (o1.getZy() == null)
					i = 2;
				else if (o1.getZy().equals("期初余额") && ReportUtil.bSysZy(o1))
					i = 1;
				else if (o1.getZy().equals("本月合计") && ReportUtil.bSysZy(o1))
					i = 3;
				else if (o1.getZy().equals("本年累计") && ReportUtil.bSysZy(o1))
					i = 4;
				else
					i = 2;
				return i;
			}

			public int compare(KmMxZVO o1, KmMxZVO o2) {
				int i = 0;
				try {
					String kmbm1 = o1.getComparecode();
					String kmbm2 = o2.getComparecode();
					i = kmbm1.compareTo(kmbm2);//1: 根据编码
					if (i == 0) {//2:根据日期
						String s1 = o1.getRq()==null?"":o1.getRq();
						String s2 = o2.getRq()==null?"":o2.getRq();
						int i1 = s1.length();
						int i2 = s2.length();
						if(i1 ==  7){
							s1 = new DZFDate(o1.getRq()+"-01").toString();
						}
						if(i2 ==7){
							s2 = new DZFDate(o2.getRq()+"-01").toString();
						}
						i = s1.compareTo(s2);
					}
					if (i == 0) {
						if (o1.getComparecode().length() == o2.getComparecode().length()) {
							if (o1.getComparecode().equals(o2.getComparecode())) {
								Integer i1 = get(o1);
								Integer i2 = get(o2);
								i = i1.compareTo(i2);
							} else {
								i = o1.getComparecode().compareTo(o2.getComparecode());
							}
						} else {
							i = o1.getComparecode().compareTo(o2.getComparecode());
						}
					}

				} catch (Throwable e) {
				}
				return i;

			}
		});
		list = new ArrayList<KmMxZVO>();
		list.addAll(java.util.Arrays.asList(kvos));
		kvos = null;

		obj[0] = new List[] { list };

	}

	private KmZzVO convert(KmMxZVO mxvo, Map<String, YntCpaccountVO> mp, QueryParamVO queryParamVO) {
		String levelEmpStr = "";
		KmZzVO vo = new KmZzVO();
		vo.setKm(levelEmpStr + mxvo.getKm());
		if(!StringUtil.isEmpty(mxvo.getPk_accsubj())
				&& mp.containsKey(mxvo.getPk_accsubj().substring(0, 24))){
			vo.setKmfullname(mp.get(mxvo.getPk_accsubj().substring(0, 24)).getFullname());
		}

		vo.setPk_currency(queryParamVO.getPk_currency());
		vo.setCurrency(queryParamVO.getCurrency());
		vo.setPeriod(mxvo.getRq());
		vo.setJf(mxvo.getJf());
		vo.setDf(mxvo.getDf());
		vo.setZy(mxvo.getZy());
		vo.setPk_accsubj(mxvo.getPk_accsubj());
		vo.setFx(mxvo.getFx());
		vo.setYe(mxvo.getYe());
		vo.setKmbm(mxvo.getKmbm());
		vo.setLevel(mxvo.getLevel());
		vo.setDay(mxvo.getDay());
		/** ------原币------ */
		vo.setYbjf(mxvo.getYbjf());
		vo.setYbdf(mxvo.getYbdf());
		vo.setYbye(mxvo.getYbye());
		return vo;
	}
	
}
