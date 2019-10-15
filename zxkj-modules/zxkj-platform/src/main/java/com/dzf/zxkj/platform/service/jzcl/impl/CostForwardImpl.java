package com.dzf.zxkj.platform.service.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.CpcosttransVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.jzcl.CostForwardInfo;
import com.dzf.zxkj.platform.model.jzcl.CostForwardVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.sys.BdTradeCostTransferVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.jzcl.ICbComconstant;
import com.dzf.zxkj.platform.service.jzcl.IndustryForward;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.report.service.IZxkjReportService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("gl_industryserv")
public class CostForwardImpl implements IndustryForward {

	private SingleObjectBO singleObjectBO;

	private ICpaccountService cpaccountService;

	@Autowired
	private IAccountService accountService;

	public ICpaccountService getCpaccountService() {
		return cpaccountService;
	}

	@Autowired
	public void setCpaccountService(ICpaccountService cpaccountService) {
		this.cpaccountService = cpaccountService;
	}

	@Reference(version = "1.0.0")
	private IZxkjReportService zxkjReportService;
	@Autowired
	private IYntBoPubUtil yntBoPubUtil;

	private ICbComconstant gl_cbconstant;

	public ICbComconstant getGl_cbconstant() {
		return gl_cbconstant;
	}

	@Autowired
	public void setGl_cbconstant(ICbComconstant gl_cbconstant) {
		this.gl_cbconstant = gl_cbconstant;
	}

	/**
	 * 第一步 查询当期结转前累计
	 */
	public List<CostForwardVO> queryIndustCFVO(QmclVO vo, boolean isgybool) throws DZFWarpException {
		String fzcb = null;
		String zzfy = null;
		CorpVO corpVo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, vo.getPk_corp());
		if (gl_cbconstant.getFangan_2013().equals(corpVo.getCorptype())) {// 2013
			fzcb = gl_cbconstant.getFzcb2013(vo.getPk_corp());// 400102 辅助生产成本
																// 2013
			zzfy = gl_cbconstant.getZzfy2013();// 4101 制造费用
		} else if (gl_cbconstant.getFangan_2007().equals(corpVo.getCorptype())) {// 2007
			fzcb = gl_cbconstant.getFzcb2007(vo.getPk_corp());// 500102 辅助生产成本
																// 2007
			zzfy = gl_cbconstant.getZzfy2007();// 5101 制造费用
		}
		SQLParameter sp = new SQLParameter();
		if (!isgybool) {// 启用库存工业 isgybool = false
			sp.addParam(fzcb);
			sp.addParam(zzfy);
		}
		// sp.addParam(fzcb);
		// sp.addParam(zzfy);
		sp.addParam(vo.getPk_corp());
		StringBuffer sf1 = new StringBuffer();
		sf1.append(
				" select h.pk_corp,t.pk_corp_account as pk_accsubj,t.accountcode as vcode,t.accountname as vname, sum(b.jfmny) as jfmny,  ");
		sf1.append("  sum(b.dfmny) as dfmny from ynt_tzpz_b b join ynt_tzpz_h h on b.pk_tzpz_h = h.pk_tzpz_h ");
		sf1.append("  join ynt_cpaccount t on b.pk_accsubj = t.pk_corp_account ");
		if (isgybool) {
			sf1.append("  where (t.accountcode  like '" + fzcb + "%' or t.accountcode like '" + zzfy
					+ "%' ) and t.isleaf = 'Y' and h.pk_corp = ? ");
		} else {
			sf1.append("  where (t.accountcode = ? or t.accountcode = ? ) and h.pk_corp = ? ");
		}
		sf1.append("  and h.doperatedate like '" + vo.getPeriod() + "%' ");
		sf1.append("  and nvl(h.dr,0)=0 and nvl(b.dr,0) = 0 and nvl(t.dr,0) = 0");
		sf1.append("  group by h.pk_corp,t.pk_corp_account,t.accountcode,t.accountname ");
		List<CostForwardVO> result = (List<CostForwardVO>) singleObjectBO.executeQuery(sf1.toString(), sp,
				new BeanListProcessor(CostForwardVO.class));
		return result;
	}

	private List<CostForwardInfo> queryCostInvtorys(QmclVO vo) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getPk_corp());
		sp.addParam("1405");
		StringBuffer sf = new StringBuffer();
		sf.append(" select pk_inventory,code as vcode,name as vname from ynt_inventory t1 ");
		sf.append(" join ynt_cpaccount t2 on t1.pk_subject = t2.pk_corp_account ");
		sf.append(" where t1.pk_corp = ? and nvl(t1.dr,0)=0  and  t2.accountcode = ? ");
		sf.append(" and rownum<=10 ");//随机返回10条记录
		List<CostForwardInfo> result = (List<CostForwardInfo>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(CostForwardInfo.class));
		return result;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	/**
	 * 第二步 结转所有辅助生产成本至制造费用
	 */
	public CostForwardVO createsecZJVO(String pk_corp, CostForwardVO oldv, boolean isjf, String zzfycode)
			throws DZFWarpException {
		CostForwardVO vo = new CostForwardVO();
		vo.setZy("结转所有辅助生产成本至制造费用");
		if (isjf) {// 借方 [制造费用]
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(zzfycode);
			String where = " pk_corp = ? and nvl(dr,0)=0 and accountcode = ? ";
			YntCpaccountVO[] accountvo = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class, where,
					sp);
			vo.setPk_accsubj(accountvo[0].getPk_corp_account());
			vo.setVcode(accountvo[0].getAccountcode());
			vo.setVname(accountvo[0].getAccountname());
			vo.setJfmny(oldv.getJfmny());
		} else {// 贷方[辅助生产成本]
			vo.setPk_accsubj(oldv.getPk_accsubj());
			vo.setVcode(oldv.getVcode());
			vo.setVname(oldv.getVname());
			vo.setDfmny(oldv.getJfmny());
		}
		vo.setVnote(oldv.getVnote());
		vo.setPk_corp(oldv.getPk_corp());
		return vo;
	}

	/**
	 * 第三步 结转所有制造费用到生产成本
	 */
	public CostForwardVO createthirdZJVO(String pk_corp, CostForwardVO oldv, boolean isjf, String zzfycode)
			throws DZFWarpException {
		CostForwardVO vo = new CostForwardVO();
		vo.setZy("结转所有制造费用到生产成本");
		if (isjf) {// 借方 [制造费用]
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(zzfycode);
			String where = " pk_corp = ? and nvl(dr,0)=0 and accountcode = ? ";
			YntCpaccountVO[] accountvo = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class, where,
					sp);
			vo.setPk_accsubj(accountvo[0].getPk_corp_account());
			vo.setVcode(accountvo[0].getAccountcode());
			vo.setVname(accountvo[0].getAccountname());
			vo.setJfmny(oldv.getJfmny());
		} else {// 贷方[辅助生产成本]
			vo.setPk_accsubj(oldv.getPk_accsubj());
			vo.setVcode(oldv.getVcode());
			vo.setVname(oldv.getVname());
			vo.setDfmny(oldv.getJfmny());
		}
		vo.setVnote(oldv.getVnote());
		vo.setPk_corp(oldv.getPk_corp());
		return vo;
	}

	@Override
	public boolean is2007(String pk_corp) throws DZFWarpException {
		boolean is2007 = true;
		CorpVO corpVO = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
		if (gl_cbconstant.getFangan_2013().equals(corpVO.getCorptype())) {// 2013
			is2007 = false;
		} else if (gl_cbconstant.getFangan_2007().equals(corpVO.getCorptype())) {// 2007
			is2007 = true;
		}
		return is2007;
	}

	/**
	 * 第四步 查询当前期初、本期发生信息 查询当前存货信息
	 */
	public List<CostForwardInfo> queryIndustQCInvtory(QmclVO vo) throws DZFWarpException {
		// 查询期初数据
		CorpVO corpVO = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, vo.getPk_corp());
		CostForwardInfo info = new CostForwardInfo();

		String pk_corp = vo.getPk_corp();
		String jbcb_cailiao = null;
		String jbcb_rengong = null;
		String jbcb_zzfy = null;
		if (gl_cbconstant.getFangan_2013().equals(corpVO.getCorptype())) {// 2013
			jbcb_cailiao = gl_cbconstant.getJbcb_zjcl2013(vo.getPk_corp());
			jbcb_rengong = gl_cbconstant.getJbcb_zjrg2013(vo.getPk_corp());
			jbcb_zzfy = gl_cbconstant.getJbcb_zzfy2013(vo.getPk_corp());
		} else if (gl_cbconstant.getFangan_2007().equals(corpVO.getCorptype())) {// 2007
			jbcb_cailiao = gl_cbconstant.getJbcb_zjcl2007(vo.getPk_corp());
			jbcb_rengong = gl_cbconstant.getJbcb_zjrg2007(vo.getPk_corp());
			jbcb_zzfy = gl_cbconstant.getJbcb_zzfy2007(vo.getPk_corp());
		}
		// 查询期初数据
		FseJyeVO[] fsejyevos = null;
		QueryParamVO quvo = new QueryParamVO();
		quvo.setPk_corp(pk_corp);
		quvo.setBegindate1(new DZFDate(vo.getPeriod() + "-01"));
		quvo.setEnddate(new DZFDate(vo.getPeriod() + "-10"));
		quvo.setXswyewfs(DZFBoolean.TRUE);
		quvo.setXsyljfs(DZFBoolean.TRUE);
		quvo.setIshasjz(DZFBoolean.FALSE);
		fsejyevos = zxkjReportService.getFsJyeVOs(quvo, 1);
		if (fsejyevos == null) {
			return new ArrayList<CostForwardInfo>();
		}
		for (FseJyeVO fsvo : fsejyevos) {
			if (jbcb_cailiao.equals(fsvo.getKmbm())) {
				if (fsvo.getQcjf() != null && fsvo.getQcjf().doubleValue() >= 0) {
					info.setNcailiao_qc(fsvo.getQcjf());
				}
				if (fsvo.getQcdf() != null && fsvo.getQcdf().doubleValue() >= 0) {
					info.setNcailiao_qc(fsvo.getQcdf().multiply(-1));
				}
			}
			if (jbcb_rengong.equals(fsvo.getKmbm())) {
				if (fsvo.getQcjf() != null && fsvo.getQcjf().doubleValue() >= 0) {
					info.setNrengong_qc(fsvo.getQcjf());
				}
				if (fsvo.getQcdf() != null && fsvo.getQcdf().doubleValue() >= 0) {
					info.setNrengong_qc(fsvo.getQcdf().multiply(-1));
				}
			}
			if (jbcb_zzfy.equals(fsvo.getKmbm())) {
				if (fsvo.getQcjf() != null && fsvo.getQcjf().doubleValue() >= 0) {
					info.setNzhizao_qc(fsvo.getQcjf());
				}
				if (fsvo.getQcdf() != null && fsvo.getQcdf().doubleValue() >= 0) {
					info.setNzhizao_qc(fsvo.getQcdf().multiply(-1));
				}
			}
		}
		
		// 查询本期发生
		StringBuffer sf = new StringBuffer();
		sf.append(" select t.accountcode,sum(nvl(b.jfmny, 0) - nvl(b.dfmny, 0)) mny ");
		sf.append(" from  ynt_tzpz_b b ");
		sf.append(" join ynt_tzpz_h h on b.pk_tzpz_h = h.pk_tzpz_h ");
		sf.append(" join ynt_cpaccount t on b.pk_accsubj = t.pk_corp_account ");
		sf.append(" where (t.accountcode like '"+jbcb_cailiao+"%' or t.accountcode like '"+jbcb_rengong+"%' or t.accountcode like '"+jbcb_zzfy+"%' ) and h.pk_corp = ? ");
		sf.append(" and h.doperatedate like '" + vo.getPeriod() + "%'  ");
		sf.append(" and nvl(h.dr,0)=0 and nvl(b.dr,0) = 0 and nvl(t.dr,0) = 0 ");
		sf.append(" group by accountcode ");
		SQLParameter sp = new SQLParameter();
//		sp.addParam(jbcb_cailiao);
//		sp.addParam(jbcb_rengong);
//		sp.addParam(jbcb_zzfy);
		sp.addParam(vo.getPk_corp());
		List<CostForwardInfo> result = (List<CostForwardInfo>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(CostForwardInfo.class));
		if (result != null && result.size() > 0) {
			for (CostForwardInfo z : result) {
				if (z.getAccountcode() .startsWith(jbcb_cailiao)) {
					info.setNcailiao_fs(SafeCompute.add(info.getNcailiao_fs(), z.getMny()));
				} else if (z.getAccountcode() .startsWith(jbcb_rengong)) {
					info.setNrengong_fs(SafeCompute.add(info.getNrengong_fs(), z.getMny()));
				} else if (z.getAccountcode() .startsWith(jbcb_zzfy)) {
					info.setNzhizao_fs(SafeCompute.add(info.getNzhizao_fs(), z.getMny()));
				}
			}
		}
		List<CostForwardInfo> listz = new ArrayList<CostForwardInfo>();
		info.setVcode("账面金额");
		listz.add(info);
		CostForwardInfo  infoclone =(CostForwardInfo)info.clone();
		infoclone.setVcode("待分配金额");
		listz.add(infoclone);
		// 查询当期存货
		List<CostForwardInfo> zlist = queryCostInvtorys(vo);
		if (zlist != null && zlist.size() > 0)
			listz.addAll(zlist);
		return listz;
	}

	/**
	 * 第四步 查询当前期初、本期发生信息 查询当前存货信息
	 */
	public List<CostForwardInfo> queryIndustQCInvtoryNOIC(QmclVO vo, String jztype1) throws DZFWarpException {
		CorpVO corpVO = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, vo.getPk_corp());
		// 获取借方科目所有下级
		if (jztype1 == null || jztype1.length() == 0) {
			jztype1 = "3";
		}
		int jztype = Integer.parseInt(jztype1);
		String pk_corp = vo.getPk_corp();
		CpcosttransVO mbvos[] = getcbmbvos(pk_corp, jztype);
		CpcosttransVO mbvo = mbvos[0];
		String jfkmid = mbvo.getPk_debitaccount();
		YntCpaccountVO jfkmvo = cpaccountService.queryById(jfkmid);
		String jfkmbm = jfkmvo.getAccountcode();
		// Map<String,YntCpaccountVO> jfkmmj = getmjkmbm(jfkmbm,"",pk_corp);
		// String dfkmid = mbvo.getPk_creditaccount();
		// YntCpaccountVO dfkmvo = cpaccountService.queryById(dfkmid);
		// String dfkmbm = dfkmvo.getAccountcode();
		// Map<String,YntCpaccountVO> dfkmmj = getmjkmbm(dfkmbm,"",pk_corp);
		// List<String> dfkmmjbms = new ArrayList<String>(dfkmmj.keySet());

		String jbcb_cailiao = null;
		String jbcb_rengong = null;
		String jbcb_zzfy = null;
		if (gl_cbconstant.getFangan_2013().equals(corpVO.getCorptype())) {// 2013
			jbcb_cailiao = gl_cbconstant.getJbcb_zjcl2013(vo.getPk_corp());
			jbcb_rengong = gl_cbconstant.getJbcb_zjrg2013(vo.getPk_corp());
			jbcb_zzfy = gl_cbconstant.getJbcb_zzfy2013(vo.getPk_corp());
		} else if (gl_cbconstant.getFangan_2007().equals(corpVO.getCorptype())) {// 2007
			jbcb_cailiao = gl_cbconstant.getJbcb_zjcl2007(vo.getPk_corp());
			jbcb_rengong = gl_cbconstant.getJbcb_zjrg2007(vo.getPk_corp());
			jbcb_zzfy = gl_cbconstant.getJbcb_zzfy2007(vo.getPk_corp());
		}
		// 查询期初数据
		FseJyeVO[] fsejyevos = null;
		QueryParamVO quvo = new QueryParamVO();
		quvo.setPk_corp(pk_corp);
		quvo.setBegindate1(new DZFDate(vo.getPeriod() + "-01"));
		quvo.setEnddate(new DZFDate(vo.getPeriod() + "-10"));
		quvo.setXswyewfs(DZFBoolean.TRUE);
		quvo.setXsyljfs(DZFBoolean.TRUE);
		quvo.setIshasjz(DZFBoolean.FALSE);
		fsejyevos = zxkjReportService.getFsJyeVOs(quvo, 1);
		CostForwardInfo info = new CostForwardInfo();
		if (fsejyevos == null) {
			return new ArrayList<CostForwardInfo>();
		}
		for (FseJyeVO fsvo : fsejyevos) {
			if (jbcb_cailiao.equals(fsvo.getKmbm())) {
				if (fsvo.getQcjf() != null && fsvo.getQcjf().doubleValue() >= 0) {
					info.setNcailiao_qc(fsvo.getQcjf());
				}
				if (fsvo.getQcdf() != null && fsvo.getQcdf().doubleValue() >= 0) {
					info.setNcailiao_qc(fsvo.getQcdf().multiply(-1));
				}
			}
			if (jbcb_rengong.equals(fsvo.getKmbm())) {
				if (fsvo.getQcjf() != null && fsvo.getQcjf().doubleValue() >= 0) {
					info.setNrengong_qc(fsvo.getQcjf());
				}
				if (fsvo.getQcdf() != null && fsvo.getQcdf().doubleValue() >= 0) {
					info.setNrengong_qc(fsvo.getQcdf().multiply(-1));
				}
			}
			if (jbcb_zzfy.equals(fsvo.getKmbm())) {
				if (fsvo.getQcjf() != null && fsvo.getQcjf().doubleValue() >= 0) {
					info.setNzhizao_qc(fsvo.getQcjf());
				}
				if (fsvo.getQcdf() != null && fsvo.getQcdf().doubleValue() >= 0) {
					info.setNzhizao_qc(fsvo.getQcdf().multiply(-1));
				}
			}
		}
		// 发生
		StringBuffer sf = new StringBuffer();
		sf.append(" select t.accountcode,sum(nvl(b.jfmny, 0) - nvl(b.dfmny, 0)) mny ");
		sf.append(" from  ynt_tzpz_b b ");
		sf.append(" join ynt_tzpz_h h on b.pk_tzpz_h = h.pk_tzpz_h ");
		sf.append(" join ynt_cpaccount t on b.pk_accsubj = t.pk_corp_account ");
		sf.append(" where (t.accountcode like '"+jbcb_cailiao+"%' or t.accountcode like '"+jbcb_rengong+"%' or t.accountcode like '"+jbcb_zzfy+"%' ) and h.pk_corp = ? ");
		sf.append(" and h.doperatedate like '" + vo.getPeriod() + "%'  ");
		sf.append(" and nvl(h.dr,0)=0 and nvl(b.dr,0) = 0 and nvl(t.dr,0) = 0 ");
		sf.append(" group by accountcode ");
		SQLParameter sp = new SQLParameter();
//		sp.addParam(jbcb_cailiao);
//		sp.addParam(jbcb_rengong);
//		sp.addParam(jbcb_zzfy);
		sp.addParam(vo.getPk_corp());
		List<CostForwardInfo> result = (List<CostForwardInfo>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(CostForwardInfo.class));
		if (result != null && result.size() > 0) {
			for (CostForwardInfo z : result) {
				if (z.getAccountcode() .startsWith(jbcb_cailiao)) {
					info.setNcailiao_fs(SafeCompute.add(info.getNcailiao_fs(), z.getMny()));
				} else if (z.getAccountcode() .startsWith(jbcb_rengong)) {
					info.setNrengong_fs(SafeCompute.add(info.getNrengong_fs(), z.getMny()));
				} else if (z.getAccountcode() .startsWith(jbcb_zzfy)) {
					info.setNzhizao_fs(SafeCompute.add(info.getNzhizao_fs(), z.getMny()));
				}
			}
		}
		List<CostForwardInfo> listz = new ArrayList<CostForwardInfo>();
		listz.add(info);
		// 查询上期结转凭证

		sf.setLength(0);
		sf.append(" select b.pk_accsubj as kmid,b.fzhsx6 as fzid ");
		sf.append(" from  ynt_tzpz_b b ");
		sf.append(" join ynt_tzpz_h h on b.pk_tzpz_h = h.pk_tzpz_h ");
		sf.append(" join ynt_qmcl l  on  h.sourcebillid = l.pk_qmcl ");
		sf.append(" where b.vcode like ? and h.pk_corp = ? ");
		sf.append(" and nvl(h.dr,0)=0 and nvl(b.dr,0) = 0 and nvl(l.dr,0) = 0 ");
		sf.append(" and sourcebilltype=?  and cbjzCount = ?  and  l.period = ? ");

		sp.clearParams();
		sp.addParam(jfkmbm + "%");
		sp.addParam(vo.getPk_corp());
		sp.addParam(IBillTypeCode.HP34);
		sp.addParam("5");
		sp.addParam(DateUtils.getPreviousPeriod(vo.getPeriod()));
		List<CostForwardInfo> result1 = (List<CostForwardInfo>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(CostForwardInfo.class));

		if (result1 != null && result1.size() > 0) {
			Map<String, AuxiliaryAccountBVO> map = getAuxiliaryAccount(corpVO.getPk_corp());
			Map<String, YntCpaccountVO> cmap = accountService.queryMapByPk(corpVO.getPk_corp());
			for (CostForwardInfo cinfo : result1) {
				YntCpaccountVO cvo = cmap.get(cinfo.getKmid());
				if (cvo == null)
					continue;
				if (!StringUtil.isEmpty(cvo.getIsfzhs()) && cvo.getIsfzhs().charAt(5) == '1') {
					if (map != null && map.size() > 0) {
						AuxiliaryAccountBVO bvo = map.get(cinfo.getFzid());
						if(bvo != null){
							cinfo.setKmbm(cvo.getAccountcode() + "_" + bvo.getCode());
							cinfo.setKmmc(cvo.getAccountname() + "_" + bvo.getName());
							cinfo.setKmfzid(vo.getPrimaryKey()+cinfo.getFzid());
						}else{
							cinfo.setKmbm(cvo.getAccountcode());
							cinfo.setKmmc(cvo.getAccountname());
							cinfo.setKmfzid(vo.getPrimaryKey());
						}
					} else {
						cinfo.setKmbm(cvo.getAccountcode());
						cinfo.setKmmc(cvo.getAccountname());
						cinfo.setKmfzid(vo.getPrimaryKey());
					}
				} else {
					cinfo.setKmbm(cvo.getAccountcode());
					cinfo.setKmmc(cvo.getAccountname());
					cinfo.setKmfzid(vo.getPrimaryKey());
				}
				listz.add(cinfo);
			}
		}
		return listz;
	}

	private Map<String, YntCpaccountVO> getmjkmbm(String dfkmbm, String userid, String pk_corp) {
		Map<String, YntCpaccountVO> map = new HashMap<String, YntCpaccountVO>();
		YntCpaccountVO[] accvos = accountService.queryByPk(pk_corp);
		for (YntCpaccountVO vo : accvos) {
			if (vo.getAccountcode().startsWith(dfkmbm)) {// 取科目的下级科目
				if (vo.getIsleaf().booleanValue()) {// 取末级
					map.put(vo.getAccountcode(), vo);
				}
			}
		}
		return map;
	}

	private Map<String, AuxiliaryAccountBVO> getAuxiliaryAccount(String pk_corp) {

		Map<String, AuxiliaryAccountBVO> map = new HashMap<String, AuxiliaryAccountBVO>();
		if (StringUtil.isEmpty(pk_corp)) {
			return map;
		}

		String condition = " nvl(dr,0) = 0  and  pk_auacount_h = ?  and pk_corp = ? ";
		SQLParameter sp = new SQLParameter();

		sp.addParam(AuxiliaryConstant.ITEM_INVENTORY);
		sp.addParam(pk_corp);
		AuxiliaryAccountBVO[] vos = (AuxiliaryAccountBVO[]) singleObjectBO.queryByCondition(AuxiliaryAccountBVO.class,
				condition, sp);

		if (vos == null || vos.length == 0) {
			return null;
		}

		for (AuxiliaryAccountBVO vo : vos) {
			map.put(vo.getPk_auacount_b(), vo);
		}
		return map;
	}

	private CpcosttransVO[] getcbmbvos(String pk_corp, Integer jztype) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(jztype);
		String where = " pk_corp= ? and nvl(dr,0)=0 and nvl(jztype,0) = ? ";
		CpcosttransVO[] mbvos = (CpcosttransVO[]) singleObjectBO.queryByCondition(CpcosttransVO.class, where, sp);
		if (mbvos == null || mbvos.length == 0) {// 查找行业级成本结转模板
			// 将行业成本结转翻译成公司级模板
			mbvos = translateCpcosttransVO(pk_corp);
			if (mbvos == null || mbvos.length == 0) {
				throw new BusinessException("当前公司、行业成本结转模板都为空！请设置！");
			}
		}
		return mbvos;

	}

	/**
	 * 将行业成本结转翻译成公司级模板
	 */
	public CpcosttransVO[] translateCpcosttransVO(String pk_corp) throws DZFWarpException {
		CpcosttransVO[] vos = null;
		// try{
		String where = " pk_trade_accountschema in (select corptype from bd_corp where pk_corp='" + pk_corp
				+ "' and nvl(dr,0)=0 )  and nvl(dr,0)=0 ";
		BdTradeCostTransferVO[] hymbVOs = (BdTradeCostTransferVO[]) singleObjectBO
				.queryByCondition(BdTradeCostTransferVO.class, where, new SQLParameter());
		if (hymbVOs == null || hymbVOs.length < 1) {
			throw new BusinessException("公司及行业未设置成本结转模板，请检查！");
		}
		vos = new CpcosttransVO[hymbVOs.length];
		for (int i = 0; i < hymbVOs.length; i++) {
			BdTradeCostTransferVO hymbVO = hymbVOs[i];
			// 借方科目
			String jfkm = hymbVO.getPk_debitaccount();
			// 根据行业会计科目主键找到公司会计科目主键
			jfkm = yntBoPubUtil.getCorpAccountPkByTradeAccountPk(jfkm, pk_corp);

			// 贷方科目
			String dfkm = hymbVO.getPk_creditaccount();
			dfkm = yntBoPubUtil.getCorpAccountPkByTradeAccountPk(dfkm, pk_corp);

			// 取数科目
			String qskm = hymbVO.getPk_fillaccount();
			qskm = yntBoPubUtil.getCorpAccountPkByTradeAccountPk(qskm, pk_corp);

			vos[i] = new CpcosttransVO();
			vos[i].setPk_debitaccount(jfkm);
			vos[i].setPk_creditaccount(dfkm);
			vos[i].setPk_fillaccount(qskm);
			vos[i].setAbstracts(hymbVO.getAbstracts());
		}
		// }catch(Exception e){
		// throw new BusinessException(e.getMessage());
		// }
		return vos;
	}

	/**
	 * 第五步
	 */
	@Override
	public CostForwardVO createfiveZJVO(String pk_corp, boolean isjf, String accode, DZFDouble mny, DZFDouble nnum,
										String pk_invtory, String invname) throws DZFWarpException {
		if (mny == null || mny.doubleValue() == 0)
			return null;
		CostForwardVO vo = new CostForwardVO();
		vo.setZy("本月完工分配材料、人工、制造费用");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(accode);
		if (isjf) {// 借方 [数量、存货]
			String where = " pk_corp = ? and nvl(dr,0)=0 and accountcode = ? ";
			YntCpaccountVO[] accountvo = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class, where,
					sp);
			vo.setPk_accsubj(accountvo[0].getPk_corp_account());
			vo.setVcode(accountvo[0].getAccountcode());
			vo.setVname(accountvo[0].getAccountname());
			vo.setJfmny(mny);
			vo.setPk_inventory(pk_invtory);
			vo.setInvname(invname);
			vo.setNnum(nnum);
		} else {// 贷方
			String where = " pk_corp = ? and nvl(dr,0)=0 and accountcode like '"+accode+"%' and nvl(isleaf,'N') ='Y' ";
			sp.clearParams();
			sp.addParam(pk_corp);
			YntCpaccountVO[] accountvo = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class, where,
					sp);
			List<YntCpaccountVO> list =Arrays.asList(accountvo);
			Collections.sort(list, new Comparator<YntCpaccountVO>() {
				public int compare(YntCpaccountVO arg0, YntCpaccountVO arg1) {
					return arg0.getAccountcode().compareTo(arg1.getAccountcode());
				}
			});
			vo.setPk_accsubj(list.get(0).getPk_corp_account());
			vo.setVcode(list.get(0).getAccountcode());
			vo.setVname(list.get(0).getAccountname());
			vo.setDfmny(mny);
		}
		vo.setPk_corp(pk_corp);
		return vo;
	}

}
