package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.MapProcessor;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.report.service.cwzb.IKMMXZReport;
import com.dzf.zxkj.report.service.cwzb.IKmHzReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.dzf.model.gl.gl_bdset.YntCpaccountVO;

/**
 * 科目汇总表
 * 
 * @author
 * 
 */
@Service("gl_rep_kmhzbserv")
@SuppressWarnings("all")
public class KmHzReportImpl implements IKmHzReport {

	@Autowired
	private IKMMXZReport gl_rep_kmmxjserv;

	@Autowired
	private SingleObjectBO singleObjectBO = null;

	public List<Object> getKMHzVOs(QueryParamVO vo) throws DZFWarpException {
		List<Object> res = new ArrayList<Object>();
		/** 数据校验 */
		validateData(vo);
		/** 查询发生额余额表  */
		FseJyeVO[] resvos = getFsJyeVOs1(vo);
		/** 查询凭证数，附件数 */
		if (resvos != null && resvos.length > 0) {
			queryTotalNum(vo, res);
			res.add(resvos);
		}
		return res;
	}

	private void validateData(QueryParamVO vo) {
		DZFDate begindate = vo.getBegindate1();
		DZFDate enddate = vo.getEnddate();
		if (begindate == null || enddate == null) {
			throw new BusinessException("查询开始日期，结束日期不能为空!");
		}
		if (begindate.after(enddate)) {
			throw new BusinessException("查询开始日期，应在结束日期之前!");
		}
	}

	/**
	 * 查询凭证数，附件数
	 * 
	 * @param vo
	 * @param res
	 */
	private void queryTotalNum(QueryParamVO vo, List res) {
		String qjq = StringUtil.isEmpty(vo.getQjq()) ? DateUtils.getPeriod(vo.getBegindate1()) : vo.getQjq();
		DZFDate qjqDate = DateUtils.getPeriodStartDate(qjq);
		String qjz = StringUtil.isEmpty(vo.getQjz()) ? DateUtils.getPeriod(vo.getEnddate()) : vo.getQjz();
		DZFDate qjzDate = DateUtils.getPeriodEndDate(qjz);
		StringBuffer countpz = new StringBuffer();
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(vo.getPk_corp());
		parameter.addParam(qjqDate);
		parameter.addParam(qjzDate);
		countpz.append(" select count(1) as total, nvl(sum(nbills),0) as bills  ");
		countpz.append(" from ynt_tzpz_h  h ");
		countpz.append(" where nvl(dr,0)= 0 and h.pk_corp=? ");
		countpz.append(" and h.doperatedate >=? and h.doperatedate <=? ");
		if (vo.getIshassh() != null && vo.getIshasjz().booleanValue()) {
			countpz.append(" and nvl(h.ishasjz,'N')='Y' ");
		}
		Map<String, BigDecimal> counts = (Map<String, BigDecimal>) singleObjectBO.executeQuery(countpz.toString(),
				parameter, new MapProcessor());
		if (counts != null) {
			res.add(counts.get("total").intValue());
			res.add(counts.get("bills").intValue());
		} else {
			res.add(Integer.valueOf(0));
			res.add(Integer.valueOf(0));
		}
	}

	private FseJyeVO[] getFsJyeVOs1(QueryParamVO vo) throws DZFWarpException {
		String qjq = vo.getQjq();
		String qjz = vo.getQjz();
		DZFDate begDate = vo.getBegindate1();
		qjq = StringUtil.isEmpty(qjq) ? begDate.getYear() + "-" + begDate.getStrMonth() : qjq;

		DZFDate endDate = vo.getEnddate();

		qjz = StringUtil.isEmpty(qjz) ? endDate.getYear() + "-" + endDate.getStrMonth() : qjz;
		vo.setBegindate1(new DZFDate(qjq + "-01"));
		vo.setEnddate(new DZFDate(qjz + "-10"));
		vo.setXswyewfs(DZFBoolean.TRUE);
		vo.setXsyljfs(DZFBoolean.TRUE);
		Object[] obj = gl_rep_kmmxjserv.getKMMXZVOs1(vo, false);
		/** 后续没有使用，暂时注释，减少不必要的查询，提高效率 */
		HashMap<String, KmMxZVO> mapQcfs = new HashMap<String, KmMxZVO>();

		KmMxZVO[] kvos = (KmMxZVO[]) ((List[]) obj[0])[0].toArray(new KmMxZVO[0]);

		Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>) obj[1];
		obj = null;
		int len = kvos == null ? 0 : kvos.length;
		List<FseJyeVO> ls = new ArrayList<FseJyeVO>();
		String kmid = "";
		YntCpaccountVO kmv = null;
		DZFDouble ufd = null;
		FseJyeVO fvo = null;
		int bs = 0;
		DZFDouble jfqcfs = DZFDouble.ZERO_DBL;
		DZFDouble dfqcfs = DZFDouble.ZERO_DBL;
		for (int i = 0; i < len; i++) {
			jfqcfs = DZFDouble.ZERO_DBL;
			dfqcfs = DZFDouble.ZERO_DBL;
			if (mapQcfs != null && mapQcfs.size() > 0) {
				KmMxZVO kmqcfs = (KmMxZVO) mapQcfs.get(kvos[i].getPk_accsubj());
				if (kmqcfs != null) {
					jfqcfs = kmqcfs.getJf() == null ? DZFDouble.ZERO_DBL : kmqcfs.getJf();
					dfqcfs = kmqcfs.getDf() == null ? DZFDouble.ZERO_DBL : kmqcfs.getDf();
				}
			}
			if (i == 0 || kmid.equals(kvos[i].getPk_accsubj()) == false) {
				if (i > 0 && kmid.equals(kvos[i].getPk_accsubj()) == false)
					ls.add(fvo);
				kmid = kvos[i].getPk_accsubj();
				fvo = new FseJyeVO();
				fvo.setFsjf(kvos[i].getJf());
				fvo.setFsdf(kvos[i].getDf());
				kmv = mp.get(kmid);
				bs = 0;
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
				fvo.setBs(bs);
			} else if (kvos[i].getZy() != null && kvos[i].getZy().equals("期初余额") && ReportUtil.bSysZy(kvos[i])
					&& kvos[i].getRq().equals(qjq)) {
				if (kvos[i].getFx().equals("借")) {
					fvo.setQcjf(kvos[i].getYe());
				} else
					fvo.setQcdf(kvos[i].getYe());
			} else if (kvos[i].getZy() != null && kvos[i].getZy().equals("本年累计") && ReportUtil.bSysZy(kvos[i])
					&& kvos[i].getRq().equals(qjz)) {
				if (kvos[i].getFx().equals("借")) {
					fvo.setQmjf(kvos[i].getYe());
				} else {
					fvo.setQmdf(kvos[i].getYe());
				}
				fvo.setJftotal(kvos[i].getJf().add(jfqcfs));
				fvo.setDftotal(kvos[i].getDf().add(dfqcfs));
			}
			if (kvos[i].getPzh() != null && kvos[i].getPzh().trim().length() > 0) {
				bs++;
			}
		}
		if (fvo != null) {
			ls.add(fvo);
		}

		/** 下级科目的笔数合计到上级 */
		List<FseJyeVO> reslist = new ArrayList<FseJyeVO>();
		YntCpaccountVO account = null;
		DZFDouble jffs = null;
		DZFDouble dffs = null;
		Integer cjq = null;
		Integer cjz = null;
		/** 根据层级过滤 */
		for (FseJyeVO fjvo : ls) {
			account = mp.get(fjvo.getPk_km());
			cjq = vo.getCjq() == null ? 1 : vo.getCjq();
			cjz = vo.getCjz() == null ? 6 : vo.getCjz();// 默认为6
			/** 如果借贷发生是空的话 则continue */
			jffs = fjvo.getFsjf();
			dffs = fjvo.getFsdf();
			fjvo.setAlevel(account.getAccountlevel());
			if ((jffs == null || jffs.doubleValue() == 0) && (dffs == null || dffs.doubleValue() == 0)) {
				continue;
			}
			if (account.getAccountlevel() != null && cjq != null && cjz != null) {
				if (cjz > 0) {
					if (account.getAccountlevel().intValue() >= cjq && account.getAccountlevel().intValue() <= cjz) {
						reslist.add(fjvo);
					}
				} else {
					if (account.getAccountlevel().intValue() >= cjq) {
						reslist.add(fjvo);
					}
				}
			}
		}

		FseJyeVO[] vos = null;
		if (reslist != null && reslist.size() > 0) {
			vos = reslist.toArray(new FseJyeVO[0]);
		}
		return vos;
	}

}
