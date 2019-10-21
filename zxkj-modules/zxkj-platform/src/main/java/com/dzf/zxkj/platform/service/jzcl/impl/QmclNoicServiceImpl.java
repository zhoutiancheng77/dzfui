package com.dzf.zxkj.platform.service.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.*;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.exception.ExBusinessException;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.CpcosttransVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.glic.InventoryQcVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.jzcl.*;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.report.NumMnyDetailVO;
import com.dzf.zxkj.platform.model.sys.BdTradeCostTransferVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.glic.IInventoryQcService;
import com.dzf.zxkj.platform.service.jzcl.ICbComconstant;
import com.dzf.zxkj.platform.service.jzcl.IQmclNoicService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.IQueryLastNum;
import com.dzf.zxkj.platform.service.report.impl.YntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.util.Kmschema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 不启用库存部分 期末结转业务逻辑类
 * 
 */
@Service("gl_qmclnoicserv")
public class QmclNoicServiceImpl implements IQmclNoicService {
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private YntBoPubUtil yntBoPubUtil = null;
	@Autowired
	private IVoucherService voucher;
	@Autowired
	private ICpaccountService cpaccountService;
	@Autowired
	private ICbComconstant gl_cbconstant;
	@Autowired
	private IQueryLastNum ic_rep_cbbserv;
	@Autowired
	private IParameterSetService parameterserv;
	@Autowired
	private IInventoryQcService gl_ic_invtoryqcserv;
	@Autowired
	private ICorpService corpService;
	@Autowired
	private IAccountService accService;

	/**
	 * 不启用库存，工业结转
	 * 
	 * 结转辅助生产成本 结转制造费用成本
	 */
	@Override
	public QmclVO jzfuzhusccb(QmclVO qmvo, List<CostForwardVO> listz, String userid, String cbjzCount) {
		setNullToFalse(qmvo);
		CorpVO corpVo = getCorpVOfromQm(qmvo, userid);
		IndustrySave save = new IndustrySave(yntBoPubUtil, singleObjectBO, voucher);
		TzpzHVO billvo = save.createVoucherByqmclVO(qmvo, listz, cbjzCount,userid);
		// 存凭证
		if (billvo != null) {
			voucher.saveVoucher(corpVo, billvo);
		}
		saveNoicQmvoIndustry(corpVo, qmvo, cbjzCount);
		return qmvo;
	}

	private void setNullToFalse(QmclVO qmvo) {
		if (qmvo.getCbjz1() == null) {
			qmvo.setCbjz1(DZFBoolean.FALSE);
		}
		// if (qmvo.getCbjz2() == null) {
		// qmvo.setCbjz2(DZFBoolean.FALSE);
		// }
		if (qmvo.getCbjz3() == null) {
			qmvo.setCbjz3(DZFBoolean.FALSE);
		}
		if (qmvo.getCbjz4() == null) {
			qmvo.setCbjz4(DZFBoolean.FALSE);
		}
		if (qmvo.getCbjz5() == null) {
			qmvo.setCbjz5(DZFBoolean.FALSE);
		}
		if (qmvo.getCbjz6() == null) {
			qmvo.setCbjz6(DZFBoolean.FALSE);
		}
	}

	@Override
	public QmclVO saveWgVoucherNoic(String userid, Map<QmclVO, List<CostForwardInfo>> map, String jztype1,
									String cbjzCount) {
		QmclVO qmclvo = null;
		List<CostForwardInfo> list = null;
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			qmclvo = (QmclVO) entry.getKey();
			list = (List<CostForwardInfo>) entry.getValue();
		}
		if (qmclvo == null) {// ||list==null||list.size()==1
			throw new BusinessException("没有需要成本结转的数据！");
		}
		if (jztype1 == null || jztype1.length() == 0) {
			jztype1 = "3";
		}
		int jztype = Integer.parseInt(jztype1);
		CorpVO corpVo = getCorpVOfromQm(qmclvo, userid);
		CpcosttransVO mbvos[] = getcbmbvos(corpVo.getPk_corp(), jztype);
		// String jfkmid = mbvos[0].getPk_debitaccount();//产成品入库
		String dfkmid = mbvos[0].getPk_creditaccount();// 设置 成本类的 贷方科目 。可以
														// 在成本模板上面设置 上级科目
														// 。即非末级科目
		// String qskmid = mbvos[0].getPk_fillaccount();
		// YntCpaccountVO jfkmvo = cpaccountService.queryById(jfkmid);
		YntCpaccountVO dfkmvo = cpaccountService.queryById(dfkmid);
		//
		String clcode = "";
		String rgcode = "";
		String zzfycode = "";
		if (gl_cbconstant.getFangan_2013().equals(corpVo.getCorptype())) {// 2013
			clcode = gl_cbconstant.getJbcb_zjcl2013(qmclvo.getPk_corp());
			rgcode = gl_cbconstant.getJbcb_zjrg2013(qmclvo.getPk_corp());
			zzfycode = gl_cbconstant.getJbcb_zzfy2013(qmclvo.getPk_corp());
		} else if (gl_cbconstant.getFangan_2007().equals(corpVo.getCorptype())) {// 2007
			clcode = gl_cbconstant.getJbcb_zjcl2007(qmclvo.getPk_corp());
			rgcode = gl_cbconstant.getJbcb_zjrg2007(qmclvo.getPk_corp());
			zzfycode = gl_cbconstant.getJbcb_zzfy2007(qmclvo.getPk_corp());
		}

		List<YntCpaccountVO> dfkmvos = getMjkmbmVO(dfkmvo.getAccountcode(), corpVo.getPk_corp());
		setNullToFalse(qmclvo);
		savewgcpnoic(qmclvo, list, corpVo, mbvos[0], dfkmvos, cbjzCount, clcode, rgcode, zzfycode,userid);
		return qmclvo;
	}

	// 完工产成品保存
	public void savewgcpnoic(QmclVO qmclvo, List<CostForwardInfo> list, CorpVO corpVo, CpcosttransVO mbvo,
			List<YntCpaccountVO> dfkmvos, String cbjzCount, String clcode, String rgcode, String zzfycode,String userid) {

		NumberForward nf = new NumberForward(yntBoPubUtil, singleObjectBO, ic_rep_cbbserv, voucher,parameterserv);
		nf.saveWgrkVouchernoic(mbvo, qmclvo, corpVo, list, dfkmvos, cbjzCount, clcode, rgcode, zzfycode,userid);
		// 保存期末结转状态
		saveNoicQmvoIndustry(corpVo, qmclvo, cbjzCount);
	}

	@Override
	public List<QMJzsmNoICVO> queryCBJZAccountVOS(String pk_gs, String userid, String jztype1) throws DZFWarpException {
		CorpVO corpVo = getCorpVOfrompk(pk_gs, userid);
		if (jztype1 == null || jztype1.length() == 0) {
			jztype1 = "3";
		}
		int jztype = Integer.parseInt(jztype1);
		// CorpVO corpVo1 =
		// (CorpVO)singleObjectBO.queryByPrimaryKey(CorpVO.class,
		// corpVo.getPk_corp());
		// if(IQmclConstant.z2 == corpVo1.getIcostforwardstyle()){//商贸业销售结转
		// jztype = 3;
		// }
		// if(IQmclConstant.z3 == corpVo1.getIcostforwardstyle()){//工业结转
		// jztype = 1;
		// }
		// String pk_corp = corpVo.getPk_corp();
		CpcosttransVO mbvos[] = getcbmbvos(corpVo.getPk_corp(), jztype);
		List<QMJzsmNoICVO> list = new ArrayList<QMJzsmNoICVO>();
		Map<String, AuxiliaryAccountBVO> map = getAuxiliaryAccount(corpVo.getPk_corp());

		YntCpaccountVO[] accvos = accService.queryByPk(corpVo.getPk_corp());
		for (CpcosttransVO mbvo : mbvos) {
			// CpcosttransVO mbvo = mbvos[0];
			// String jfkmid = mbvo.getPk_debitaccount();
			String dfkmid = mbvo.getPk_creditaccount();
			// String qskmid = mbvo.getPk_fillaccount();
			YntCpaccountVO dfkmvo = cpaccountService.queryById(dfkmid);
			String dfkmbm = dfkmvo.getAccountcode();
			for (YntCpaccountVO vo : accvos) {
				if (vo.getAccountcode().startsWith(dfkmbm)) {// 取贷方科目的下级科目
					if (vo.getIsleaf().booleanValue()) {// 取末级
						if (!StringUtil.isEmpty(vo.getIsfzhs()) && vo.getIsfzhs().charAt(5) == '1') {
							if (map != null && map.size() > 0) {
								for (AuxiliaryAccountBVO bvo : map.values()) {
									QMJzsmNoICVO qmvo = new QMJzsmNoICVO();
									qmvo.setKmid(vo.getPrimaryKey());
									qmvo.setKmbm(vo.getAccountcode() + "_" + bvo.getCode());
									String mc = vo.getAccountname() + "_" + bvo.getName();
									if(!StringUtil.isEmpty(bvo.getSpec())){
										mc = mc + "(" + bvo.getSpec() + ")";
									}
									qmvo.setKmmc(mc);
									qmvo.setFzid(bvo.getPrimaryKey());
									qmvo.setKmfzid(vo.getPrimaryKey() + bvo.getPrimaryKey());
									list.add(qmvo);
								}
							} else {
								QMJzsmNoICVO qmvo = new QMJzsmNoICVO();
								qmvo.setKmid(vo.getPrimaryKey());
								qmvo.setKmbm(vo.getAccountcode());
								qmvo.setKmmc(vo.getAccountname());
								qmvo.setKmfzid(vo.getPrimaryKey());
								list.add(qmvo);
							}
						} else {
							QMJzsmNoICVO qmvo = new QMJzsmNoICVO();
							qmvo.setKmid(vo.getPrimaryKey());
							qmvo.setKmbm(vo.getAccountcode());
							qmvo.setKmmc(vo.getAccountname());
							qmvo.setKmfzid(vo.getPrimaryKey());
							list.add(qmvo);
						}
					}
				}
			}
		}
		if (list != null && list.size() > 0) {
			Collections.sort(list, new Comparator<QMJzsmNoICVO>() {
				@Override
				public int compare(QMJzsmNoICVO o1, QMJzsmNoICVO o2) {
					int i = o1.getKmbm().compareTo(o2.getKmbm());
					return i;
				}
			});
		}
		return list;
	}

	@Override
	public List<QMJzsmNoICVO> queryCBJZqcpzAccountVOS(String pk_gs, String userid, String begindate, String enddate,
			String[] kmbms, String jztype1) throws DZFWarpException {
		CorpVO corpVo = getCorpVOfrompk(pk_gs, userid);
		List<QMJzsmNoICVO> list = new ArrayList<QMJzsmNoICVO>();
		// CorpVO corpVo1 =
		// (CorpVO)singleObjectBO.queryByPrimaryKey(CorpVO.class,
		// corpVo.getPk_corp());
		// if(IQmclConstant.z2 == corpVo1.getIcostforwardstyle()){//商贸业销售结转
		if (jztype1 == null || jztype1.length() == 0) {
			jztype1 = "3";
		}
		int jztype = Integer.parseInt(jztype1);
		// String pk_corp = corpVo.getPk_corp();
		CpcosttransVO mbvos[] = getcbmbvos(corpVo.getPk_corp(), jztype);// 3代表销售模板
																		// 1材料
																		// 2完工
		boolean flag = true;
		Map<String, YntCpaccountVO> ccountMap = accService.queryMapByPk(corpVo.getPk_corp());
		
		String priceStr = parameterserv.queryParamterValueByCode(corpVo.getPk_corp(), IParameterConstants.DZF010);
		int iprice = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		
		for (CpcosttransVO mbvo : mbvos) {
			// CpcosttransVO mbvo = mbvos[0];//取第一个模板
			// String jfkmid = mbvo.getPk_debitaccount();
			String dfkmid = mbvo.getPk_creditaccount();
			String qskmid = mbvo.getPk_fillaccount();
			YntCpaccountVO dfkmvo = cpaccountService.queryById(dfkmid);
			String dfkmbm = "null";
			if (dfkmvo != null) {
				dfkmbm = dfkmvo.getAccountcode();
			}
			YntCpaccountVO qskmvo = cpaccountService.queryById(qskmid);
			String qskmbm = "null";
			if (qskmvo != null) {
				qskmbm = qskmvo.getAccountcode();
			}
			List<QMJzsmNoICVO> listkmbm = null;
			if (flag) {// 明细辅助
				// 销售
				if (jztype == 3) {
					listkmbm = getqcpzkmnew(corpVo.getPk_corp(), userid, dfkmbm, qskmbm, begindate, enddate, kmbms,
							corpVo.getBegindate(), jztype, ccountMap, iprice);
					// 材料
				} else if (jztype == 1) {
					listkmbm = getqcpzkmnew(corpVo.getPk_corp(), userid, dfkmbm, qskmbm, begindate, enddate, kmbms,
							corpVo.getBegindate(), jztype, ccountMap, iprice);
				} else {
					//
					listkmbm = getqcpzkm(corpVo.getPk_corp(), userid, dfkmbm, qskmbm, begindate, enddate, kmbms,
							corpVo.getBegindate(), ccountMap);
				}

				if (listkmbm != null && listkmbm.size() > 0) {
					for (QMJzsmNoICVO vo : listkmbm) {
						DZFDouble qcmny = getDzfDouble(vo.getQcmny());
						DZFDouble bqsrmny = getDzfDouble(vo.getBqsrmny());
						DZFDouble qcnum = getDzfDouble(vo.getQcnum());
						DZFDouble bqsrnum = getDzfDouble(vo.getBqsrnum());
						DZFDouble d1 = SafeCompute.add(qcmny, bqsrmny);
						DZFDouble d2 = SafeCompute.add(qcnum, bqsrnum);
						if (d1.doubleValue() == 0 || d2.doubleValue() == 0) {//这种情况下，估计是有销售单价。
							vo.setBqprice(DZFDouble.ZERO_DBL);
							continue;
						}
						DZFDouble bqprice = SafeCompute.div(d1, d2);
						vo.setBqprice(bqprice);
					}
				}
			} else {
				// 科目
				listkmbm = getqcpzkm(corpVo.getPk_corp(), userid, dfkmbm, qskmbm, begindate, enddate, kmbms,
						corpVo.getBegindate(), ccountMap);
			}
			if (listkmbm != null && listkmbm.size() > 0)
				list.addAll(listkmbm);
		}
		Map<String, QMJzsmNoICVO> result = new HashMap<String, QMJzsmNoICVO>();
		for (QMJzsmNoICVO v : list) {
			String key = v.getKmid() + v.getFzid();
			if (!result.containsKey(key)) {
				result.put(key, v);
			}
		}
		return new ArrayList<QMJzsmNoICVO>(result.values());

		// }
		// return null;
	}

	private CorpVO getCorpVOfromQm(QmclVO qmclvo, String userid) throws DZFWarpException {
		if (qmclvo == null || StringUtil.isEmpty(qmclvo.getPk_corp())) {
			throw new BusinessException("请求公司的数据为空！");
		}
		String corp = qmclvo.getPk_corp();
		CorpVO corpvo = corpService.queryByPk(corp);
		return corpvo;
	}

	private CorpVO getCorpVOfrompk(String pk_gs, String userid) throws DZFWarpException {
		if (StringUtil.isEmpty(pk_gs)) {
			throw new BusinessException("请求公司的数据为空！");
		}
		CorpVO corpvo =corpService.queryByPk(pk_gs);
		return corpvo;
	}

	@Override
	public QmclVO saveToSalejzVoucher(String userid, Map<QmclVO, List<QMJzsmNoICVO>> map, String jztype1,
			String cbjzCount, String xjxcf) throws DZFWarpException {
		QmclVO qmclvo = null;
		List<QMJzsmNoICVO> list = new ArrayList<QMJzsmNoICVO>();
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			qmclvo = (QmclVO) entry.getKey();
			list = (List<QMJzsmNoICVO>) entry.getValue();
		}
		if (qmclvo == null) {// qmclvo==null||list==null||list.size()==0
			throw new BusinessException("没有需要成本结转的数据！");
		}
		List<QMJzsmNoICVO> list1 = new ArrayList<QMJzsmNoICVO>();
		for (QMJzsmNoICVO vo : list) {
			if (vo.getKmid() != null && vo.getKmid().length() > 0) {
				list1.add(vo);
			}
		}
		if (jztype1 == null || jztype1.length() == 0) {
			jztype1 = "3";
		}
		int jztype = Integer.parseInt(jztype1);
		CorpVO corpVo = getCorpVOfromQm(qmclvo, userid);
		//取得当前公司的科目map..zpm
		Map<String, YntCpaccountVO> kmsmap = accService.queryMapByPk(corpVo.getPk_corp());
		CpcosttransVO mbvos[] = getcbmbvos(corpVo.getPk_corp(), jztype);
		for (CpcosttransVO mbvo : mbvos) {
			String jfkmid = mbvo.getPk_debitaccount();
			String dfkmid = mbvo.getPk_creditaccount();
			YntCpaccountVO dfkmvo = cpaccountService.queryById(dfkmid);
			// String qskmid = mbvos[0].getPk_fillaccount();
			YntCpaccountVO jfkmvo = cpaccountService.queryById(jfkmid);
			List<String> listdfkm = getMjkmbms1(dfkmvo.getAccountcode(), corpVo.getPk_corp());
			// List<QMJzsmNoICVO> list1 = new ArrayList<QMJzsmNoICVO>();
			// for(QMJzsmNoICVO vo : list){
			// if(vo.getKmid()!=null && vo.getKmid().length()>0){
			// if(listdfkm.contains(vo.getKmbm())){
			// list1.add(vo);
			// }
			// }
			// }
			setNullToFalse(qmclvo);
			saveSalenoicVoucher(qmclvo, list1, corpVo, mbvo, jfkmvo, cbjzCount, listdfkm, xjxcf,userid,kmsmap);
		}
		return qmclvo;
	}

	/**
	 * 反成本结转
	 */
	@Override
	public QmclVO rollbackCbjzNoic(QmclVO vos, String cbjzCount) throws DZFWarpException {
		updateCheckNj(vos);
		CancelCbjz cbjz = new CancelCbjz(singleObjectBO);
		List<QmclVO> list = cbjz.rollbackCbjzNoic(new QmclVO[] { vos }, cbjzCount);
		return list.get(0);
	}

	@Override
	public QmclVO queryIsXjxcf(String pk_gs, String userid) throws DZFWarpException {
		CorpVO corpVo = getCorpVOfrompk(pk_gs, userid);
		QmclVO vo = new QmclVO();
		// CorpVO corpVo1 = (CorpVO)
		// singleObjectBO.queryByPrimaryKey(CorpVO.class, corpVo.getPk_corp());
		DZFDate JZdate = corpVo.getBegindate();
		if (JZdate == null) {
			throw new BusinessException("公司建账日期为空！");
		}
		String sql = " pk_corp = ? and period = ? and nvl(dr,0)=0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(corpVo.getPk_corp());
		sp.addParam(JZdate.toString().substring(0, 7));
		QmclVO[] qmclvos = (QmclVO[]) singleObjectBO.queryByCondition(QmclVO.class, sql, sp);
		if (qmclvos != null && qmclvos.length > 0) {
			vo = qmclvos[0];
			vo.setJzdate(JZdate);
		}
		return vo;
	}

	@Override
	public List<CostForwardInfo> queryCBJZAccountVOSwg(String pk_gs, String userid, String jztype1)
			throws DZFWarpException {
		CorpVO corpVo = getCorpVOfrompk(pk_gs, userid);
		if (jztype1 == null || jztype1.length() == 0) {
			jztype1 = "3";
		}
		int jztype = Integer.parseInt(jztype1);
		// CorpVO corpVo1 =
		// (CorpVO)singleObjectBO.queryByPrimaryKey(CorpVO.class,
		// corpVo.getPk_corp());
		// String pk_corp = corpVo.getPk_corp();
		CpcosttransVO mbvos[] = getcbmbvos(corpVo.getPk_corp(), jztype);
		CpcosttransVO mbvo = mbvos[0];
		String jfkmid = mbvo.getPk_debitaccount();
		// String dfkmid = mbvo.getPk_creditaccount();
		// String qskmid = mbvo.getPk_fillaccount();
		YntCpaccountVO jfkmvo = cpaccountService.queryById(jfkmid);
		String jfkmbm = jfkmvo.getAccountcode();
		List<CostForwardInfo> list = new ArrayList<CostForwardInfo>();
		YntCpaccountVO[] accvos = accService.queryByPk(corpVo.getPk_corp());
		Map<String, AuxiliaryAccountBVO> map = getAuxiliaryAccount(corpVo.getPk_corp());
		for (YntCpaccountVO vo : accvos) {
			if (vo.getAccountcode().startsWith(jfkmbm)) {// 取jie方科目的下级科目
				if (vo.getIsleaf().booleanValue()) {// 取末级
					if (!StringUtil.isEmpty(vo.getIsfzhs()) && vo.getIsfzhs().charAt(5) == '1') {
						if (map != null && map.size() > 0) {
							for (AuxiliaryAccountBVO bvo : map.values()) {
								CostForwardInfo info1 = new CostForwardInfo();
								info1.setKmid(vo.getPrimaryKey());
								info1.setKmbm(vo.getAccountcode() + "_" + bvo.getCode());
								info1.setKmmc(vo.getAccountname() + "_" + bvo.getName());
								info1.setFzid(bvo.getPrimaryKey());
								info1.setKmfzid(vo.getPrimaryKey() + bvo.getPrimaryKey());
								list.add(info1);
							}
						} else {
							CostForwardInfo info1 = new CostForwardInfo();
							info1.setKmid(vo.getPrimaryKey());
							info1.setKmbm(vo.getAccountcode());
							info1.setKmmc(vo.getAccountname());
							info1.setKmfzid(vo.getPrimaryKey());
							list.add(info1);
						}
					} else {
						CostForwardInfo info1 = new CostForwardInfo();
						info1.setKmid(vo.getPrimaryKey());
						info1.setKmbm(vo.getAccountcode());
						info1.setKmmc(vo.getAccountname());
						info1.setKmfzid(vo.getPrimaryKey());
						list.add(info1);
					}
				}
			}
		}
		if (list != null && list.size() > 0) {
			Collections.sort(list, new Comparator<CostForwardInfo>() {
				@Override
				public int compare(CostForwardInfo o1, CostForwardInfo o2) {
					int i = o1.getKmbm().compareTo(o2.getKmbm());
					return i;
				}
			});
		}
		return list;
	}

	@Override
	public void judgeLastPeriod(String pk_gs, String userid, String qj, String costype) throws DZFWarpException {
		CorpVO corpVo = getCorpVOfrompk(pk_gs, userid);
		String lastPeriod = DateUtils.getPreviousPeriod(qj);
		DZFDate lastPeriodDate = new DZFDate(lastPeriod + "-01");
		DZFDate corpdate = corpVo.getBegindate();
		if (!corpdate.after(lastPeriodDate)) {
			SQLParameter sp = new SQLParameter();
			sp.addParam(lastPeriod);
			sp.addParam(corpVo.getPk_corp());
			String where = " period= ?  and pk_corp= ? and nvl(dr,0)=0 ";
			QmclVO[] dbVOs = (QmclVO[]) singleObjectBO.queryByCondition(QmclVO.class, where, sp);
			if (dbVOs == null || dbVOs.length < 1) {
				throw new BusinessException("上一期" + lastPeriod + "尚未进行成本结转，不能操作！");
			}
			if (dbVOs[0].getIscbjz() == null || !dbVOs[0].getIscbjz().booleanValue()) {
				throw new BusinessException("上一期" + lastPeriod + "尚未进行成本结转，不能操作！");
			}
		}
		//zpm 2018.8.30
//		if (String.valueOf(IQmclConstant.z2).equalsIgnoreCase(costype)) {//商贸
//			checkCbjzmb(corpVo.getPk_corp(), String.valueOf(IQmclConstant.XIAOSHOU_JZ));
//		}
	}

	@Override
	public void checkCbjzmb(String pk_gs, String costype) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_gs);
		sp.addParam(costype);
		String where = " pk_corp= ? and nvl(dr,0)=0 and nvl(jztype,0) = ? ";
		CpcosttransVO[] mbvos = (CpcosttransVO[]) singleObjectBO.queryByCondition(CpcosttransVO.class, where, sp);
		if (mbvos == null || mbvos.length == 0) {// 查找行业级成本结转模板
			throw new BusinessException("当前公司成本结转模板为空！请设置！");
		}
	}

	@Override
	public QmclVO queryById(String pk_qmcl) {
		QmclVO vo = (QmclVO) singleObjectBO.queryVOByID(pk_qmcl, QmclVO.class);
		return vo;
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

	private List<QMJzsmNoICVO> getqcpzkmnew(String pk_corp, String userid, String dfkmbm, String qskmbm,
			String begindate, String enddate, String[] kmbms, DZFDate jzdate, int jztype,
			Map<String, YntCpaccountVO> ccountMap,int iprice) {

		String period = begindate.substring(0, 7);

		List<YntCpaccountVO> clist = new ArrayList<>();
		// 记录取数科目的主键 和名字 为匹配不启用辅助的 发出数量按照名字匹配做准备
		Map<String, String> nameMap = new HashMap<>();
		for (YntCpaccountVO vo : ccountMap.values()) {
			if (vo.getAccountcode().startsWith(dfkmbm)) {// 取科目的下级科目
				if (vo.getIsleaf().booleanValue()) {// 取末级
					clist.add(vo);
					if (!nameMap.containsKey(vo.getAccountname())) {
						nameMap.put(vo.getAccountname(), vo.getPk_corp_account());
					}
				}
			}
		}

		if (clist == null || clist.size() == 0)
			return null;

		List<String> kmlist = new ArrayList<>();
		List<String> mxlist = new ArrayList<>();
		for (YntCpaccountVO vo : clist) {
			String fzhs = vo.getIsfzhs();
			/// 如果库存商品有存货辅助 取 明细 // 如果没有 取科目
			if (!StringUtil.isEmpty(fzhs) && fzhs.charAt(5) == '1') {
				mxlist.add(vo.getPk_corp_account());
			} else {
				kmlist.add(vo.getPk_corp_account());
			}
		}

		// 存货辅助
		Map<String, AuxiliaryAccountBVO> map = getAuxiliaryAccount(pk_corp);
		CorpVO cpvo =corpService.queryByPk(pk_corp);

		List<QMJzsmNoICVO> mxlistvo = getMxListQMJzsmNoICVO(pk_corp, period, mxlist, map, qskmbm, jztype, ccountMap,cpvo, iprice);
		List<QMJzsmNoICVO> kmlistvo = getKmListQMJzsmNoICVO(pk_corp, period, kmlist, map, nameMap, qskmbm, jztype,ccountMap,cpvo);

		if (mxlistvo == null || mxlistvo.size() == 0) {
			return kmlistvo;
		} else {

			if (kmlistvo != null && kmlistvo.size() > 0) {
				for (QMJzsmNoICVO vo : kmlistvo) {
					mxlistvo.add(vo);
				}
			}

		}
		return mxlistvo;
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

	private List<String> getMjkmbms1(String str, String pk_corp) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);

		YntCpaccountVO[] vos = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class,
				" accountcode like '" + str + "%' and pk_corp = ? and nvl(dr,0) = 0 ", sp);
		List<String> list = new ArrayList<String>();
		if (vos != null && vos.length > 0) {

			Map<String, AuxiliaryAccountBVO> map = getAuxiliaryAccount(pk_corp);
			for (YntCpaccountVO vo : vos) {
				if (vo.getIsleaf().booleanValue()) {
					if (!StringUtil.isEmpty(vo.getIsfzhs()) && vo.getIsfzhs().charAt(5) == '1') {
						if (map != null && map.size() > 0) {
							for (AuxiliaryAccountBVO bvo : map.values()) {
								list.add(vo.getAccountcode() + "_" + bvo.getCode());
							}
						} else {
							list.add(vo.getAccountcode());
						}
					} else {
						list.add(vo.getAccountcode());
					}
				}
			}
		}
		return list;
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
	
	private boolean isGl_Chhs(String pk_corp){
		SQLParameter sp12 = new SQLParameter();
		sp12.addParam(pk_corp);
		InventorySetVO[] bodys  = (InventorySetVO[])singleObjectBO.queryByCondition(InventorySetVO.class, " pk_corp = ? and nvl(dr,0) = 0 ", sp12);
		if(bodys == null || bodys.length == 0){
			return false;
		}
		if(InventoryConstant.IC_FZMXHS == bodys[0].getChcbjzfs()//辅助明细
				|| InventoryConstant.IC_CHDLHS == bodys[0].getChcbjzfs()){//存货大类
			return true;
		}
		return false;
	}

	private List<QMJzsmNoICVO> getMxListQMJzsmNoICVO(String pk_corp, String period, List<String> slist,
			Map<String, AuxiliaryAccountBVO> map, String qskm, int jztype, Map<String, YntCpaccountVO> ccountMap,CorpVO cpvo,int iprice) {

		if (slist == null || slist.size() == 0)
			return null;
		//
		DZFDate qrydate = null;
		DZFDate qcdate = gl_ic_invtoryqcserv.queryInventoryQcDate(pk_corp);
		Map<String, FzhsqcVO> qcmap = null;
		boolean isChZzFL = false;
		//启用总账存货，并且启用　　总账存货期初　
		if(IcCostStyle.IC_INVTENTORY.equals(cpvo.getBbuildic())
				&& qcdate != null
				&& period.compareTo(qcdate.toString().substring(0, 7)) >= 0 
				&& isGl_Chhs(pk_corp)//只有明细核算和大类核算的时候，才走此逻辑
				){//所在结转期间大于　总账存货期初启用期间
			/// 期初余额
			qcmap = getZzChqcMx(pk_corp);
			qrydate = qcdate;
			isChZzFL = true;
		}else{
			/// 期初余额
			qcmap = getFzqcMx(pk_corp, slist);
			qrydate = cpvo.getBegindate();
			isChZzFL = false;
		}
		// 当期前期初
		List<NumMnyDetailVO> qclist = queryDetailVOsNoICForJz(period, pk_corp, true, slist,ccountMap,cpvo,qrydate,isChZzFL);
		Map<String, NumMnyDetailVO> qcmap1 = hashlizeObject(qclist, true);
		// 当期
		List<NumMnyDetailVO> srlist = queryDetailVOsNoICForJz(period, pk_corp, false, slist,ccountMap,cpvo,qrydate,isChZzFL);
		Map<String, NumMnyDetailVO> bqmap = hashlizeObject(srlist, true);

		List<String> keylist = getKeyList(qcmap, qcmap1, bqmap, null);
		List<QMJzsmNoICVO> list  = getListQMJzsmNoICVO(keylist, qcmap, qcmap1, bqmap, null, map, ccountMap);

		// 如果明细 在取出对应的发出 当期
		// 如果全都启用存货辅助 并且 取数科目与 贷方科目 有且只有一个末级的时候 取出发出数量
		if (jztype == 3) { // 销售成本结转
			calculateOutNum(list, slist, qskm, pk_corp, period, ccountMap, map,cpvo,iprice,qrydate,isChZzFL);
		}
		return list;

	}

	private List<QMJzsmNoICVO> getKmListQMJzsmNoICVO(String pk_corp, String period, List<String> slist,
			Map<String, AuxiliaryAccountBVO> map, Map<String, String> nameMap, String qskmbm, int jztype,
			Map<String, YntCpaccountVO> ccountMap,CorpVO cpvo) {

		if (slist == null || slist.size() == 0)
			return null;
		/// 期初余额
		Map<String, QcYeVO> qcmap = getQcMx(pk_corp, slist);
		// 当期前期初
		List<NumMnyDetailVO> qclist = queryDetailVOsNoICForJz(period, pk_corp, true, slist,ccountMap,cpvo,cpvo.getBegindate(),false);
		Map<String, NumMnyDetailVO> qcmap1 = hashlizeObject(qclist, false);
		// 当期收入
		List<NumMnyDetailVO> srlist = queryDetailVOsNoICForJz(period, pk_corp, false, slist,ccountMap,cpvo,cpvo.getBegindate(),false);
		Map<String, NumMnyDetailVO> srmap = hashlizeObject(srlist, false);

		List<String> keylist = getKeyList(null, qcmap1, srmap, qcmap);

		List<QMJzsmNoICVO> list = getListQMJzsmNoICVO(keylist, null, qcmap1, srmap, qcmap, map, ccountMap);

		// 根据科目 计算出对应名称的科目的发出数量 按从上导下的顺序匹配科目名称 后面的自动过滤掉
		if (jztype == 3) { // 销售成本结转

			calculateOutNum1(list, slist, nameMap, qskmbm, pk_corp, period, ccountMap,cpvo);
		}
		return list;

	}

	private List<QMJzsmNoICVO> getListQMJzsmNoICVO(List<String> keylist, Map<String, FzhsqcVO> qcmap,
			Map<String, NumMnyDetailVO> qcmap1, Map<String, NumMnyDetailVO> bqmap, Map<String, QcYeVO> qcmap2,
			Map<String, AuxiliaryAccountBVO> map, Map<String, YntCpaccountVO> ccountMap) {
		QMJzsmNoICVO jzvo = null;
		List<QMJzsmNoICVO> list = new ArrayList<>();

		for (String key : keylist) {
			jzvo = new QMJzsmNoICVO();
			String kmid = null;
			String kmbm = null;
			String kmmc = null;
			String fzid = null;
			if (qcmap2 != null && qcmap2.size() > 0) {
				QcYeVO yvo = qcmap2.get(key);

				if (yvo != null) {
					jzvo.setQcmny(yvo.getThismonthqc());
					jzvo.setQcnum(yvo.getMonthqmnum());
					if (StringUtil.isEmpty(kmid)) {
						kmid = yvo.getPk_accsubj();
					}
					if (StringUtil.isEmpty(kmbm)) {
						kmbm = getChangeKmid(kmid, fzid, map, ccountMap, 0);
					}
					if (StringUtil.isEmpty(kmmc)) {
						kmmc = getChangeKmid(kmid, fzid, map, ccountMap, 1);
					}
				}
			}

			if (qcmap != null && qcmap.size() > 0) {
				FzhsqcVO yvo = qcmap.get(key);

				if (yvo != null) {
					jzvo.setQcmny(yvo.getThismonthqc());
					jzvo.setQcnum(yvo.getMonthqmnum());
					if (StringUtil.isEmpty(kmid)) {
						kmid = yvo.getPk_accsubj();
					}
					if (StringUtil.isEmpty(fzid)) {
						fzid = yvo.getFzhsx6();
					}
					if (StringUtil.isEmpty(kmbm)) {
						kmbm = getChangeKmid(kmid, fzid, map, ccountMap, 0);
					}
					if (StringUtil.isEmpty(kmmc)) {
						kmmc = getChangeKmid(kmid, fzid, map, ccountMap, 1);
					}
				}
			}

			if (qcmap1 != null && qcmap1.size() > 0) {
				NumMnyDetailVO vo = qcmap1.get(key);

				if (vo != null) {
					jzvo.setQcmny(SafeCompute.sub(SafeCompute.add(jzvo.getQcmny(), vo.getNmny()), vo.getNdmny()));
					jzvo.setQcnum(SafeCompute.sub(SafeCompute.add(jzvo.getQcnum(), vo.getNnum()), vo.getNdnum()));
					if (StringUtil.isEmpty(kmid)) {
						kmid = vo.getPk_subject();
					}
					if (StringUtil.isEmpty(fzid)) {
						fzid = vo.getFzhsx6();
					}
					if (StringUtil.isEmpty(kmbm)) {
						kmbm = getChangeKmid(kmid, fzid, map, ccountMap, 0);
					}
					if (StringUtil.isEmpty(kmmc)) {
						kmmc = getChangeKmid(kmid, fzid, map, ccountMap, 1);
					}
				}
			}
			if (bqmap != null && bqmap.size() > 0) {
				NumMnyDetailVO vo = bqmap.get(key);

				if (vo != null) {
					jzvo.setBqsrmny(vo.getNmny());
					jzvo.setBqsrnum(vo.getNnum());
					jzvo.setXsnum(vo.getNdnum());//出库数量
					jzvo.setXsmny(vo.getNdmny());//出库金额
					jzvo.setBqfcnum(vo.getNdnum());//出库数量
					jzvo.setZgcgnum(vo.getZgcgnum());
					jzvo.setZgcgmny(vo.getZgcgmny());
					if (StringUtil.isEmpty(kmid)) {
						kmid = vo.getPk_subject();
					}
					if (StringUtil.isEmpty(fzid)) {
						fzid = vo.getFzhsx6();
					}
					if (StringUtil.isEmpty(kmbm)) {
						kmbm = getChangeKmid(kmid, fzid, map, ccountMap, 0);
					}
					if (StringUtil.isEmpty(kmmc)) {
						kmmc = getChangeKmid(kmid, fzid, map, ccountMap, 1);
					}
				}

			}
			jzvo.setFzid(fzid);
			jzvo.setKmid(kmid);
			jzvo.setKmbm(kmbm);
			jzvo.setKmmc(kmmc);
			list.add(jzvo);
		}
		return list;
	}

	private String getChangeKmid(String kmid, String fzid, Map<String, AuxiliaryAccountBVO> map,
			Map<String, YntCpaccountVO> ccountMap, int type) {
		AuxiliaryAccountBVO bvo = null;
		YntCpaccountVO accvo = null;
		String temp = null;
		//zpm ,,如果存货有大类。
		String classify = queryClassifyKm(fzid);
		if(!StringUtil.isEmpty(classify)){
			kmid = classify;
		}
		if (type == 0) {
			accvo = ccountMap.get(kmid);

			if (accvo != null) {
				temp = accvo.getAccountcode();
			}

			if (map != null && map.size() > 0) {
				bvo = map.get(fzid);

				if (bvo != null) {
					temp = temp + "_" + bvo.getCode();
				}
			}
		} else if (type == 1) {
			accvo = ccountMap.get(kmid);

			if (accvo != null) {
				temp = accvo.getAccountname();
			}
			if (map != null && map.size() > 0) {
				bvo = map.get(fzid);

				if (bvo != null) {
					temp = temp + "_" + bvo.getName();
					
					if(!StringUtil.isEmpty(bvo.getSpec())){
						temp = temp + "(" + bvo.getSpec() + ")";
					}
				}
				
				
			}

		} else {

		}

		return temp;

	}

	// 当期发出------辅助明细
	private void calculateOutNum(List<QMJzsmNoICVO> list, List<String> slist, String qskm, String pk_corp,
			String period, Map<String, YntCpaccountVO> ccountMap, Map<String, AuxiliaryAccountBVO> fzmap,CorpVO cpvo,int numjingdu,DZFDate qrydate,boolean isChZzFL) {

		List<String> olist = new ArrayList<>();
		for (YntCpaccountVO vo : ccountMap.values()) {
			if (vo.getAccountcode().startsWith(qskm)) {// 取科目的下级科目
				if (vo.getIsleaf().booleanValue()) {// 取末级
					olist.add(vo.getPrimaryKey());
				}
			}
		}

		if ((olist != null && olist.size() == 1)) {

			List<NumMnyDetailVO> fclist = queryDetailVOsNoICForJz(period, pk_corp, false, olist,ccountMap,cpvo,qrydate,isChZzFL);

			if (fclist == null || fclist.size() == 0) {
				return;
			}

			Map<String,NumMnyDetailVO> fcmap = new HashMap<String,NumMnyDetailVO>();
			Map<String, QMJzsmNoICVO>  listmap = DZfcommonTools.hashlizeObjectByPk(list, new String[]{"fzid"});
			for (NumMnyDetailVO numvo : fclist) {
				if(listmap.containsKey(numvo.getFzhsx6())){
					QMJzsmNoICVO jzvo = listmap.get(numvo.getFzhsx6());
					jzvo.setBqfcnum(SafeCompute.add(jzvo.getBqfcnum(), numvo.getNdnum()));
					//
					jzvo.setXsnum(SafeCompute.add(jzvo.getXsnum(), numvo.getNdnum()));
					jzvo.setXsmny(SafeCompute.add(jzvo.getXsmny(), numvo.getNdmny()));
					jzvo.setZgxsmny(SafeCompute.add(jzvo.getZgxsmny(), numvo.getZgxsmny()));
					jzvo.setZgxsnum(SafeCompute.add(jzvo.getZgxsnum(), numvo.getZgxsnum()));
					//计算平均销售单价
					jzvo.setXsprice(SafeCompute.div(jzvo.getXsmny(), jzvo.getXsnum()).setScale(numjingdu,DZFDouble.ROUND_HALF_UP));
				}else{
					if(fcmap.containsKey(numvo.getFzhsx6())){
						NumMnyDetailVO nvo = fcmap.get(numvo.getFzhsx6());
						nvo.setNdnum(SafeCompute.add(nvo.getNdnum(), numvo.getNdnum()));
						nvo.setNdmny(SafeCompute.add(nvo.getNdmny(), numvo.getNdmny()));
						nvo.setZgxsmny(SafeCompute.add(nvo.getZgxsmny(), numvo.getZgxsmny()));
						nvo.setZgxsnum(SafeCompute.add(nvo.getZgxsnum(), numvo.getZgxsnum()));
						//计算平均销售单价
						nvo.setXsprice(SafeCompute.div(nvo.getNdmny(), nvo.getNdnum()).setScale(numjingdu,DZFDouble.ROUND_HALF_UP));
					}else{
						fcmap.put(numvo.getFzhsx6(), numvo);
						//计算平均销售单价
						numvo.setXsprice(numvo.getNprice());
					}
				}
			}
			

			QMJzsmNoICVO jzvo = null;
			String kmid = null;
			for (NumMnyDetailVO numvo : fcmap.values()) {
				jzvo = new QMJzsmNoICVO();
				if(StringUtil.isEmpty(numvo.getFzhsx6()))
					continue;
				jzvo.setBqfcnum(numvo.getNdnum());
				jzvo.setFzid(numvo.getFzhsx6());
				//查询分类
				kmid = queryClassifyKm(numvo.getFzhsx6());
				if(StringUtil.isEmpty(kmid)){
					kmid = slist.get(0);
				}
				jzvo.setKmid(kmid);
				jzvo.setXsprice(numvo.getXsprice());//发出的单价
				jzvo.setKmbm(getChangeKmid(slist.get(0), numvo.getFzhsx6(), fzmap, ccountMap, 0));
				jzvo.setKmmc(getChangeKmid(slist.get(0), numvo.getFzhsx6(), fzmap, ccountMap, 1));
				jzvo.setZgxsmny(numvo.getZgxsmny());
				jzvo.setZgxsnum(numvo.getZgxsnum());
				list.add(jzvo);
			}
			//

			// String fzhs = vo.getIsfzhs();
			// /// 如果库存商品有存货辅助 取 明细 // 如果没有 取科目
			// if (!StringUtil.isEmpty(fzhs) && fzhs.charAt(5) == '1') {
		}
	}
	
	//查询得到科目，这里不用通过 corpvo 中的bbuildic 字段来区分。
	private String queryClassifyKm(String fzid){
		if(StringUtil.isEmpty(fzid)){
			return null;
		}
		AuxiliaryAccountBVO bvo = (AuxiliaryAccountBVO)singleObjectBO.queryByPrimaryKey(AuxiliaryAccountBVO.class, fzid);
		return bvo.getKmclassify();
	}

	// 当期发出---------科目下级成本结转核算
	private void calculateOutNum1(List<QMJzsmNoICVO> list, List<String> slist, Map<String, String> nameMap,
			String qskmbm, String pk_corp, String period, Map<String, YntCpaccountVO> ccountMap,CorpVO cpvo) {

		Map<String, String> qsnameMap = new HashMap<>();
		List<String> qslist = new ArrayList<>();
		for (YntCpaccountVO vo : ccountMap.values()) {
			if (vo.getAccountcode().startsWith(qskmbm)) {// 取科目的下级科目
				if (vo.getIsleaf().booleanValue()) {// 取末级
					qslist.add(vo.getPk_corp_account());
					if (!qsnameMap.containsKey(vo.getAccountname())) {
						qsnameMap.put(vo.getAccountname(), vo.getPk_corp_account());
					}
				}
			}
		}

		if (qslist != null && qslist.size() > 0) {
			List<NumMnyDetailVO> fclist = queryDetailVOsNoICForJz(period, pk_corp, false, qslist,ccountMap,cpvo,cpvo.getBegindate(),false);

			if (fclist == null || fclist.size() == 0) {
				return;
			}

			Map<String, String> map = new HashMap<String, String>();
			for (QMJzsmNoICVO jzvo : list) {
				String fskmid = qsnameMap.get(jzvo.getKmmc());
				if (StringUtil.isEmpty(fskmid))
					continue;
				for (NumMnyDetailVO numvo : fclist) {
					if (fskmid.equals(numvo.getPk_subject())) {
						jzvo.setBqfcnum(SafeCompute.add(jzvo.getBqfcnum(), numvo.getNdnum()));
						jzvo.setZgxsmny(numvo.getZgxsmny());
						jzvo.setZgxsnum(numvo.getZgxsnum());
						map.put(fskmid, jzvo.getKmmc());
					}
				}
			}

			// 找出只有发生的
			int size = fclist.size();
			for (int i = size - 1; i >= 0; i--) {
				NumMnyDetailVO numvo = fclist.get(i);
				if (numvo != null) {
					String fzid = numvo.getPk_subject();
					if (map.get(fzid) != null) {
						fclist.remove(numvo);
					}
				}
			}

			Map<String, NumMnyDetailVO> fcmap = new HashMap<>();
			QMJzsmNoICVO jzvo = null;
			for (NumMnyDetailVO numvo : fclist) {
				if (StringUtil.isEmpty(numvo.getKmmc())) {
					continue;
				}
				if (numvo.getKmmc().contains("/")) {
					String[] kmmcs = numvo.getKmmc().split("/");
					if (kmmcs == null || kmmcs.length == 0)
						continue;
					String value = numvo.getKmmc().split("/")[kmmcs.length - 1];
					if (StringUtil.isEmpty(value) || StringUtil.isEmpty(nameMap.get(value))) {
						continue;
					}
					YntCpaccountVO accvo = ccountMap.get(nameMap.get(value));
					if (accvo == null)
						continue;
					if (fcmap.containsKey(accvo.getPk_corp_account())) {
						NumMnyDetailVO tempnumvo = fcmap.get(accvo.getPk_corp_account());
						tempnumvo.setNdnum(SafeCompute.add(tempnumvo.getNdnum(), numvo.getNdnum()));
						tempnumvo.setZgxsmny(SafeCompute.add(tempnumvo.getZgxsmny(), numvo.getZgxsmny()));
						tempnumvo.setZgxsnum(SafeCompute.add(tempnumvo.getZgxsnum(), numvo.getZgxsnum()));
						fcmap.put(accvo.getPk_corp_account(), tempnumvo);
					} else {
						fcmap.put(accvo.getPk_corp_account(), numvo);
					}
					

				}
			}
			for (Map.Entry<String, NumMnyDetailVO> entry : fcmap.entrySet()) {
				YntCpaccountVO accvo = ccountMap.get(entry.getKey());
				if (accvo == null)
					continue;
				NumMnyDetailVO tempnumvo  = entry.getValue();
				jzvo = new QMJzsmNoICVO();
				jzvo.setBqfcnum(tempnumvo.getNdnum());
				jzvo.setZgxsmny(tempnumvo.getZgxsmny());
				jzvo.setZgxsnum(tempnumvo.getZgxsnum());
				jzvo.setKmmc(accvo.getAccountname());
				jzvo.setKmbm(accvo.getAccountcode());
				jzvo.setKmid(accvo.getPk_corp_account());
				list.add(jzvo);
			}

		}
	}

	/// 取有期初余额或有当期凭证发生的科目
	private List<QMJzsmNoICVO> getqcpzkm(String pk_corp, String userid, String dfkmbm, String qskmbm, String begindate,
			String enddate, String[] kmbms, DZFDate JZDATE, Map<String, YntCpaccountVO> ccountMap) {
		// String accountrule = cpaccountService.queryAccountRule(pk_corp);
//		QueryMxNoIC mx = new QueryMxNoIC(singleObjectBO, cpaccountService);
		List<String> list = new ArrayList<String>();
		List<NumMnyDetailVO> listnm = null;
//				mx.querymx(begindate, enddate, pk_corp, userid, new QueryParamVO(), "", true,
//				null, JZDATE);
		for (NumMnyDetailVO vo : listnm) {
			if (vo.getZy() != null && vo.getZy().equals("期初余额")) {
				if (vo.getNymny() != null && vo.getNymny().doubleValue() != 0) {
					if (!list.contains(vo.getKmbm())) {
						list.add(vo.getKmbm());
					}

				}
			}
			if (vo.getPzhhid() != null) {
				if (!list.contains(vo.getKmbm())) {
					list.add(vo.getKmbm());
				}
			}
		}
		List<String> dfmjkmbm = new ArrayList<String>();
		List<String> dfqskmbm = new ArrayList<String>();
		List<String> kms = new ArrayList<String>();
		if (kmbms != null) {
			for (String kmbm : kmbms) {
				if (kmbm != null && kmbm.length() > 0 && !kmbm.equals("null")) {
					kms.add(kmbm);
				}
			}
		}
		Map<String, YntCpaccountVO> map1 = new HashMap<String, YntCpaccountVO>();
		if (kms != null && kms.size() > 0) {
			dfmjkmbm = kms;
		} else {
			map1 = getmjkmbm(dfkmbm, userid, pk_corp, ccountMap);
			dfmjkmbm = new ArrayList<String>(map1.keySet());
			// dfmjkmbm = getmjkmbm(dfkmbm,userid,pk_corp);//贷方科目末级科目
		}
		dfqskmbm.addAll(dfmjkmbm);
		Map<String, YntCpaccountVO> map2 = getmjkmbm(qskmbm, userid, pk_corp, ccountMap);
		List<String> qskmbms = new ArrayList<String>(map2.keySet());
		// List<String> qskmbms = getmjkmbm(qskmbm,userid,pk_corp);//取数科目末级科目
		dfqskmbm.addAll(qskmbms);
		list.retainAll(dfqskmbm);// 取两个集合的交集
		// 一个编码对应一个QMJzsmNoICVO
		Map<String, QMJzsmNoICVO> map = new HashMap<String, QMJzsmNoICVO>();
		for (String str : list) {
			map.put(str, new QMJzsmNoICVO());
		}
		List<QMJzsmNoICVO> listqmvo = new ArrayList<QMJzsmNoICVO>();
		for (NumMnyDetailVO vo : listnm) {
			if (list.contains(vo.getKmbm())) {
				if (vo.getZy() != null) {
					QMJzsmNoICVO qmvo = map.get(vo.getKmbm());
					if (vo.getZy().equals("期初余额")) {
						qmvo.setQcmny(getDzfDouble(vo.getNymny()));
						qmvo.setQcnum(getDzfDouble(vo.getNynum()));
					}
					if (vo.getZy().equals("本期合计")) {
						qmvo.setBqsrnum(getDzfDouble(vo.getNnum()));
						qmvo.setBqsrmny(getDzfDouble(vo.getNmny()));
						// qmvo.setBqfcnum(getDzfDouble(vo.getNdnum()));
						qmvo.setBqfcnum(DZFDouble.ZERO_DBL);
					}
					qmvo.setKmid(vo.getPk_subject());
					qmvo.setKmbm(vo.getKmbm());
					qmvo.setKmmc(vo.getKmmc().substring(vo.getKmmc().indexOf("/") + 1));
					// qmvo.setBqprice();
				}
			}
		}
		for (String str : list) {
			listqmvo.add(map.get(str));
		}
		// 计算商品的加权平均单价=（期初金额+本期收入金额）/(期初数量+本月收入数量）。
		for (QMJzsmNoICVO vo : listqmvo) {
			DZFDouble qcmny = getDzfDouble(vo.getQcmny());
			DZFDouble bqsrmny = getDzfDouble(vo.getBqsrmny());
			DZFDouble qcnum = getDzfDouble(vo.getQcnum());
			DZFDouble bqsrnum = getDzfDouble(vo.getBqsrnum());
			DZFDouble d1 = SafeCompute.add(qcmny, bqsrmny);
			DZFDouble d2 = SafeCompute.add(qcnum, bqsrnum);
			if (d1.doubleValue() == 0 || d2.doubleValue() == 0) {
				vo.setBqprice(DZFDouble.ZERO_DBL);
				continue;
			}
			DZFDouble bqprice = SafeCompute.div(d1, d2);
			vo.setBqprice(bqprice);
			// for(String qskm : qskmbms){
			// String mjbm = getkmmjbm(qskm,accountrule);
			// if(vo.getKmbm().endsWith(mjbm)){
			// if(map.get(qskm)!=null){
			// vo.setBqfcnum(getDzfDouble(map.get(qskm).getBqfcnum()));
			// }
			// }
			// }
			// 默认取0
			vo.setBqfcnum(DZFDouble.ZERO_DBL);
		}
		if (kms != null && kms.size() > 0) {
			dfmjkmbm = kms;
			map1 = getmjkmbm(dfkmbm, userid, pk_corp, ccountMap);
		} else {
			if (!dfkmbm.equals(qskmbm)) {
				list.removeAll(qskmbms);
			}
			dfmjkmbm = list;
		}
		List<QMJzsmNoICVO> lists = new ArrayList<QMJzsmNoICVO>();
		for (QMJzsmNoICVO vo : listqmvo) {
			if (dfmjkmbm.contains(vo.getKmbm())) {
				lists.add(vo);
			}
		}
		Iterator iter = map1.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			YntCpaccountVO val = (YntCpaccountVO) entry.getValue();
			if (!list.contains(key) && dfmjkmbm.contains(key)) {
				QMJzsmNoICVO qmvo = new QMJzsmNoICVO();
				qmvo.setKmid(val.getPrimaryKey());
				qmvo.setKmbm(val.getAccountcode());
				qmvo.setKmmc(val.getAccountname());
				lists.add(qmvo);
			}
		}
		return lists;
	}

	private DZFDouble getDzfDouble(DZFDouble d) {
		return d == null ? DZFDouble.ZERO_DBL : d;
	}

	private String getkmmjbm(String kmbm, String accountrule) {// 根据编码规则
																// 获取科目末级的编码
		String rules[] = accountrule.split("/");
		String sss = "";
		int a = 0;
		for (String s : rules) {
			int b = Integer.parseInt(s);
			a += b;
			if (a == kmbm.length()) {
				sss = kmbm.substring(a - b, a);
				break;
			}
		}
		return sss;
	}
	
	/**
	 * 查询明细数据 不启用库存
	 */
	private List<NumMnyDetailVO> queryDetailVOsNoIC(String period, String pk_corp, 
			boolean isqc, List<String> slist,DZFDate jzdate) {
		StringBuffer sf = new StringBuffer();
		SQLParameter pa = new SQLParameter();
		pa.addParam(pk_corp);
		sf.append(" select tb.pk_accsubj pk_subject, ");
		sf.append(" tb.kmmchie  kmmc, ");
		sf.append(" tb.vcode  kmbm, ");
		sf.append(" th.period qj, ");
		sf.append(" th.doperatedate opdate, ");
		sf.append(" th.pzh, ");
		sf.append(" th.pk_tzpz_h pzhhid, ");
		sf.append(" tb.zy, ");
		sf.append(" tb.nnumber,");
		sf.append(" tb.nprice, ");
		sf.append(" tb.jfmny jfmny, ");
		sf.append(" tb.dfmny dfmny, ");
		sf.append(" tb.vdirect,");
		sf.append(" ct.measurename jldw ,");
		sf.append(" ct.accountlevel accountlevel, ");
		sf.append(
				" tb.fzhsx1,tb.fzhsx2,tb.fzhsx3,tb.fzhsx4,tb.fzhsx5,tb.fzhsx6,tb.fzhsx7,tb.fzhsx8,tb.fzhsx9,tb.fzhsx10 ");
		sf.append(" from ynt_tzpz_b tb ");
		sf.append(" join ynt_tzpz_h th on tb.pk_tzpz_h = th.pk_tzpz_h ");
		sf.append(" join ynt_cpaccount ct on tb.pk_accsubj = ct.pk_corp_account ");
		sf.append(" where th.pk_corp = ? ");
		sf.append(" and nvl(th.dr, 0) = 0 and nvl(tb.dr, 0) = 0 and nvl(ct.dr,0)=0 ");
		sf.append(" and nvl(ct.isnum,'N') = 'Y'  ");

		if (isqc) {
			sf.append(" and th.period <?");
			pa.addParam(period);
		} else {
			sf.append(" and th.period =?");
			pa.addParam(period);
		}
		sf.append(" and  th.doperatedate >= ? ");//增加查询条件建账日期
		pa.addParam(jzdate);
		sf.append(" and " + SqlUtil.buildSqlForIn(" tb.pk_accsubj", slist.toArray(new String[slist.size()])));
		sf.append(" order by tb.vcode, pk_accsubj,opdate,pzh ");
		//
		List<NumMnyDetailVO> list = (List<NumMnyDetailVO>) singleObjectBO.executeQuery(sf.toString(), pa,
				new BeanListProcessor(NumMnyDetailVO.class));
		if(list == null || list.isEmpty()){
			return new ArrayList<NumMnyDetailVO> ();
		}
		return list;
	}
	
	/**
	 * 查询明细数据 不启用库存(成本结转处理)
	 */
	public List<NumMnyDetailVO> queryDetailVOsNoICForJz(String period, String pk_corp, 
			boolean isqc, List<String> slist,Map<String, YntCpaccountVO> ccountMap,CorpVO corpvo,DZFDate jzdate,boolean isChZzFL)//isChZzFL是否总账存货分类
			throws DZFWarpException {
		List<NumMnyDetailVO> list = queryDetailVOsNoIC(period, pk_corp, isqc, slist, jzdate);
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				NumMnyDetailVO v = list.get(i);
				//判断入出库
				YntCpaccountVO vo = ccountMap.get(v.getPk_subject());
//				if(isChZzFL){//总账存货。。
					if((Kmschema.isKcspbm(corpvo.getCorptype(), vo.getAccountcode()) //库存商品
							|| Kmschema.isYclbm(corpvo.getCorptype(), vo.getAccountcode()))//原材料科目
							&& vo.getIsnum()!= null && vo.getIsnum().booleanValue()//启用数量
							)
					{//入库
						if(judgeDirect(v) ==0){//借方
							v.setNnum(v.getNnumber());
							v.setNmny(v.getJfmny());
							if(!isqc&&v.getNnumber()!=null&& v.getNnumber().doubleValue()>0&&v.getJfmny()!=null&& v.getJfmny().doubleValue()>0){
								v.setZgcgnum(v.getNnumber());
								v.setZgcgmny(v.getJfmny());
							}
						}
						else if(judgeDirect(v) ==1){//贷方  zpm ,2018.9.5///出库    注意  1405 蓝字 ，就认为是出库。和500101 处理方式不一样。
							//是根据 科目来查询结果的。
							//slist ，查询往期。就应该是统计 [成本类的出库]  ，，，slist 存的就是 科目。
							if(isqc){//往期
								v.setNdnum(v.getNnumber());
								v.setNdmny(v.getDfmny());
							}else{//本期
								//查询本期，，应该不统计 [成本类的出库] ，，但此刻就是成本结转。因此不用考虑的。
								//但也有特殊情况，自己手工结转了一部分。
								//但也有特殊情况，自己手工结转了一部分。因此加上，大部分情况没有
								TzpzBVO[] bodys = queryTZpzBvos(pk_corp,v.getPzhhid());///
								//非成本类的出库///////
								if(!Kmschema.ischengbenpz(corpvo,bodys)){//这个千万不能启用了。
									//这块处理有问题。
									//两种情况。1是蓝冲。2是费用出库。（比如说买了一个包子吃了）
									//有损益类的，就认为是出库。
									//无损益类的，就认为是蓝冲。
									if(Kmschema.isSunYipz(corpvo,bodys)){///这种是费用出库。
//										v.setNdnum(v.getNnumber());
//										v.setNdmny(v.getDfmny());
										//////////////---------------------------------------注意这里和凭证保存那里处理的不一样的。2018.12.20号 zpm
										v.setNnum(SafeCompute.multiply(v.getNnumber(), new DZFDouble(-1)));
										v.setNmny(SafeCompute.multiply(v.getDfmny(), new DZFDouble(-1)));
									}else{//非损益类 的，，，这种是调整账务。
										v.setNnum(SafeCompute.multiply(v.getNnumber(), new DZFDouble(-1)));
										v.setNmny(SafeCompute.multiply(v.getDfmny(), new DZFDouble(-1)));
									}
								}
							}
						}
					}else if(Kmschema.isshouru(corpvo.getCorptype(), vo.getAccountcode())//收入类科目
							&& vo.getIsnum()!= null && vo.getIsnum().booleanValue()//启用数量
							)
					{//统计本期出库的
						TzpzBVO[] bodys = queryTZpzBvos(pk_corp,v.getPzhhid());
						//统计收入类，肯定是在本期//但也有特殊情况，自己手工结转了一部分。因此加上，大部分情况没有
						//非本年利润的凭证，本期还没开始做。就是统计本期的。统计本期出库的
						//但也有特殊情况，自己手工结转了一部分。
						if(!Kmschema.isbennianlirunpz(corpvo,bodys)){
							if(judgeDirect(v) ==1){//贷方
								v.setNdnum(v.getNnumber());
								v.setNdmny(v.getDfmny());
								if(!isqc&&v.getNnumber()!=null&& v.getNnumber().doubleValue()>0&&v.getDfmny()!=null&& v.getDfmny().doubleValue()>0){
									v.setZgxsnum(v.getNnumber());
									v.setZgxsmny(v.getDfmny());
								}
							}else if(judgeDirect(v) ==0){//借方///////////////注意   500101 ,蓝字 ，要乘以 －1 ，变成红冲。和1405 处理方式不一样。
								v.setNdnum(SafeCompute.multiply(v.getNnumber(), new DZFDouble(-1)));
								v.setNdmny(SafeCompute.multiply(v.getJfmny(), new DZFDouble(-1)));
							}
						}
					}
//				}else{//不启用总账存货，，或者是 启用总账存货 的 不核算存货。
//					if(vo.getIsnum()!= null && vo.getIsnum().booleanValue()//启用数量
//							)
//					{//入库 1405 1403
//						if(judgeDirect(v) ==0){//借方
//							if(Kmschema.isshouru(corpvo.getCorptype(), vo.getAccountcode())){//收入类科目 在借方
//								//统计收入类，肯定是在本期//但也有特殊情况，自己手工结转了一部分。因此加上，大部分情况没有
//								TzpzBVO[] bodys = queryTZpzBvos(pk_corp,v.getPzhhid());
//								if(!Kmschema.isbennianlirunpz(corpvo,bodys)){//非本年利润的凭证，本期还没开始做。就是统计本期的。统计本期出库的
//									//但也有特殊情况，自己手工结转了一部分。因此加上，大部分情况没有
//									v.setNdnum(SafeCompute.multiply(v.getNnumber(), new DZFDouble(-1)));
//									v.setNdmny(SafeCompute.multiply(v.getJfmny(), new DZFDouble(-1)));
//								}
//							}else{
//								v.setNnum(v.getNnumber());
//								v.setNmny(v.getJfmny());
//							}
//						}
//						//统计本期出库的 5001 6001
//						else if(judgeDirect(v) ==1){//贷方，
//							v.setNdnum(v.getNnumber());
//							v.setNdmny(v.getDfmny());
//						}
//					}
//				}
			}
		}
		return list;
	}
	//判断借贷方
	private int judgeDirect(NumMnyDetailVO v){
		if(v == null)
			return -1;
		//这个字段，有时候不好使。这里即使用借贷方判断即可。2018.9.27
//		if(v.getVdirect() != null)
//			return v.getVdirect().intValue();
		if(v.getJfmny() != null && v.getJfmny().doubleValue() != 0)
			return 0;
		if(v.getDfmny()!=null && v.getDfmny().doubleValue() != 0)
			return 1;
		return -1;
	}
	
	private TzpzBVO[] queryTZpzBvos(String pk_corp,String hid){
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(hid);
		TzpzBVO[] bodys = (TzpzBVO[])singleObjectBO.queryByCondition(TzpzBVO.class, 
				" pk_corp = ? and nvl(dr,0) = 0  and pk_tzpz_h = ?  ", sp);
		return bodys;
		
	}

	private Map<String, YntCpaccountVO> getmjkmbm(String dfkmbm, String userid, String pk_corp,
			Map<String, YntCpaccountVO> ccountMap) {
		Map<String, YntCpaccountVO> map = new HashMap<String, YntCpaccountVO>();
		for (YntCpaccountVO vo : ccountMap.values()) {
			if (vo.getAccountcode().startsWith(dfkmbm)) {// 取科目的下级科目
				if (vo.getIsleaf().booleanValue()) {// 取末级
					map.put(vo.getAccountcode(), vo);
				}
			}
		}
		return map;
	}

	/**
	 * 已经年结不能反操作
	 * 
	 * @param vos
	 */
	private void updateCheckNj(QmclVO vos) {
		SQLParameter sp = new SQLParameter();
		String qmjzsqlwhere = "select * from YNT_QMJZ  where nvl(dr,0)=0 and pk_corp  = ? and period =  ? ";
		sp.addParam(vos.getPk_corp());
		sp.addParam(vos.getPeriod().substring(0, 4) + "-12");
		List<QmJzVO> qmjzlist = (List<QmJzVO>) singleObjectBO.executeQuery(qmjzsqlwhere, sp,
				new BeanListProcessor(QmJzVO.class));
		if (qmjzlist != null && qmjzlist.size() > 0) {
			if (qmjzlist.get(0).getJzfinish() != null && qmjzlist.get(0).getJzfinish().booleanValue()) {
				throw new BusinessException("公司已经年结,不能反操作!");
			}
		}
	}

	// 查询年期初数据
	private Map<String, FzhsqcVO> getFzqcMx(String pk_corp, List<String> slist) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" select qc.* from ynt_fzhsqc qc ");
		sf.append(" join ynt_cpaccount ct ");
		sf.append(" on qc.pk_accsubj = ct.pk_corp_account ");
		sf.append(
				" where  qc.pk_corp = ? and nvl(qc.dr,0) = 0 and  (nvl(ct.isnum,'N') = 'Y' or nvl(qc.monthqmnum,0) != 0) and nvl(ct.isfzhs,'0000000000')!='0000000000' ");
		sf.append(" and " + SqlUtil.buildSqlForIn(" qc.pk_accsubj", slist.toArray(new String[slist.size()])));

		// 这个考虑的时候，没有考虑外币情况
		// QcYeVO[] vos =
		// (QcYeVO[])singleObjectBO.queryByCondition(QcYeVO.class,
		// sf.toString(), sp);
		List<FzhsqcVO> list = (List<FzhsqcVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(FzhsqcVO.class));
		FzhsqcVO[] vos = (FzhsqcVO[]) list.toArray(new FzhsqcVO[list.size()]);
		Map<String, FzhsqcVO> map3 = hashlizeObject(vos);
		return map3;
	}
	
	private Map<String,AuxiliaryAccountBVO> queryFzhsBVOs(String pk_corp){
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam("000001000000000000000006");
		AuxiliaryAccountBVO[] bodyvos = (AuxiliaryAccountBVO[])
				singleObjectBO.queryByCondition(AuxiliaryAccountBVO.class, " pk_corp = ? and pk_auacount_h = ? and nvl(dr,0) = 0  ", sp);
		if(bodyvos == null || bodyvos.length == 0){
			return new HashMap<String,AuxiliaryAccountBVO>();
		}
		Map<String, AuxiliaryAccountBVO> map = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(bodyvos), new String[]{"pk_auacount_b"});
		return map;
	}
	
	private YntCpaccountVO queryChkms(String pk_corp){
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam("1405");///这里先用1405 ，毕竟其它的并不是1405 ...像民间、事业单位什么的。1243什么的。//下版在用　Kmschema 进行调整吧。
		YntCpaccountVO[] bodyvos = (YntCpaccountVO[])
				singleObjectBO.queryByCondition(YntCpaccountVO.class, " pk_corp = ? and accountcode = ? and nvl(dr,0) = 0  ", sp);
		if(bodyvos == null || bodyvos.length == 0){
			return null;
		}
		return bodyvos[0];
	}
	
	/**
	 * 查询总账存货 期初明细
	 * @param 
	 */
	private Map<String, FzhsqcVO> getZzChqcMx(String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		InventoryQcVO[] bodyvos = (InventoryQcVO[])singleObjectBO.queryByCondition(InventoryQcVO.class, " pk_corp = ? and nvl(dr,0) = 0 ", sp);
		if(bodyvos == null || bodyvos.length == 0){
			return new HashMap<String, FzhsqcVO>();
		}
		SQLParameter sp12 = new SQLParameter();
		sp12.addParam(pk_corp);
		InventorySetVO[] setvos  = (InventorySetVO[])singleObjectBO.queryByCondition(InventorySetVO.class, " pk_corp = ? and nvl(dr,0) = 0 ", sp12);
		if(InventoryConstant.IC_CHDLHS == setvos[0].getChcbjzfs()){//存货大类
			Map<String,AuxiliaryAccountBVO> fzhsmap = queryFzhsBVOs(pk_corp);
			for(InventoryQcVO vo : bodyvos){
				AuxiliaryAccountBVO fzbvo = fzhsmap.get(vo.getPk_inventory());
				if(fzbvo != null){
					if(StringUtil.isEmpty(fzbvo.getKmclassify())){
						throw new BusinessException("存货辅助项目："+fzbvo.getName()+"，存货大类为空");
					}
					vo.setVdef1(fzbvo.getKmclassify());
				}
			}
		}else if(InventoryConstant.IC_FZMXHS == setvos[0].getChcbjzfs()){//辅助明细
			YntCpaccountVO cpaccountvo = queryChkms(pk_corp);
			if(cpaccountvo == null || cpaccountvo.getIsleaf() == null || !cpaccountvo.getIsleaf().booleanValue()){
				throw new BusinessException("库存商品一级科目必须为末级");
			}
			for(InventoryQcVO vo : bodyvos){
				vo.setVdef1(cpaccountvo.getPk_corp_account());
			}
		}
		List<FzhsqcVO> list = new ArrayList<FzhsqcVO>();
		for(InventoryQcVO vo : bodyvos){
			FzhsqcVO qc = new FzhsqcVO();
			qc.setPk_accsubj(vo.getVdef1());//先用这个记录科目id
			qc.setFzhsx6(vo.getPk_inventory());
			qc.setPk_corp(vo.getPk_corp());
			qc.setThismonthqc(vo.getThismonthqc());
			qc.setMonthqmnum(vo.getMonthqmnum());
			list.add(qc);
		}
		FzhsqcVO[] vos = (FzhsqcVO[]) list.toArray(new FzhsqcVO[list.size()]);
		Map<String, FzhsqcVO> map3 = hashlizeObject(vos);
		return map3;
	}

	private Map<String, FzhsqcVO> hashlizeObject(FzhsqcVO[] vos) {
		Map<String, FzhsqcVO> result = new HashMap<String, FzhsqcVO>();
		if (vos == null || vos.length == 0) {
			return result;
		}
		FzhsqcVO vo = null;
		for (FzhsqcVO v : vos) {
			String key = v.getPk_accsubj() + v.getFzhsx6();
			if (!result.containsKey(key)) {
				result.put(key, v);
			} else {
				vo = result.get(key);
				if (vo != null) {
					vo.setMonthqmnum(SafeCompute.add(vo.getMonthqmnum(), v.getMonthqmnum()));
					vo.setThismonthqc(SafeCompute.add(vo.getThismonthqc(), v.getThismonthqc()));
				}
				result.put(key, vo);
			}
		}
		return result;
	}

	public List<YntCpaccountVO> getMjkmbmVO(String str, String pk_corp) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		YntCpaccountVO[] vos = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class,
				" accountcode like '" + str + "%' and pk_corp = ? and nvl(dr,0) = 0 ", sp);
		List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>();
		if (vos != null && vos.length > 0) {
			for (YntCpaccountVO vo : vos) {
				if (vo.getIsleaf().booleanValue()) {
					list.add(vo);
				}
			}
		}
		Collections.sort(list, new Comparator<YntCpaccountVO>() {
			public int compare(YntCpaccountVO arg0, YntCpaccountVO arg1) {
				return arg0.getAccountcode().compareTo(arg1.getAccountcode());
			}
		});
		return list;
	}

	private Map<String, NumMnyDetailVO> hashlizeObject(List<NumMnyDetailVO> list, boolean ismx) {
		Map<String, NumMnyDetailVO> result = new HashMap<String, NumMnyDetailVO>();
		if (list == null || list.size() == 0) {
			return result;
		}
		NumMnyDetailVO vo = null;
		for (NumMnyDetailVO v : list) {
			String key = null;
			if (ismx) {
				key = v.getPk_subject() + v.getFzhsx6();
			} else {
				key = v.getPk_subject();
			}

			if (!result.containsKey(key)) {
				result.put(key, v);
			} else {
				vo = result.get(key);
				if (vo != null) {
					v.setNmny(SafeCompute.add(vo.getNmny(), v.getNmny()));
					v.setNnum(SafeCompute.add(vo.getNnum(), v.getNnum()));
					v.setNdmny(SafeCompute.add(vo.getNdmny(), v.getNdmny()));
					v.setNdnum(SafeCompute.add(vo.getNdnum(), v.getNdnum()));
					v.setZgcgmny(SafeCompute.add(vo.getZgcgmny(), v.getZgcgmny()));
					v.setZgcgnum(SafeCompute.add(vo.getZgcgnum(), v.getZgcgnum()));
				}
				result.put(key, v);
			}
		}
		return result;
	}

	private List<String> getKeyList(Map<String, FzhsqcVO> qcmap, Map<String, NumMnyDetailVO> qcmap1,
			Map<String, NumMnyDetailVO> srmap, Map<String, QcYeVO> qcmap2) {

		List<String> list = new ArrayList<>();

		if (qcmap != null && qcmap.size() > 0) {
			for (String key : qcmap.keySet()) {
				if (!list.contains(key))
					list.add(key);
			}
		}

		if (qcmap1 != null && qcmap1.size() > 0) {
			for (String key : qcmap1.keySet()) {
				if (!list.contains(key))
					list.add(key);
			}
		}
		if (srmap != null && srmap.size() > 0) {
			for (String key : srmap.keySet()) {
				if (!list.contains(key))
					list.add(key);
			}
		}

		if (qcmap2 != null && qcmap2.size() > 0) {
			for (String key : qcmap2.keySet()) {
				if (!list.contains(key))
					list.add(key);
			}
		}

		return list;

	}

	// 查询年期初数据
	public Map<String, QcYeVO> getQcMx(String pk_corp, List<String> slist) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" select qc.* from ynt_qcye qc ");
		sf.append(" join ynt_cpaccount ct ");
		sf.append(" on qc.pk_accsubj = ct.pk_corp_account ");
		sf.append(
				" where  qc.pk_corp = ? and nvl(qc.dr,0) = 0 and  (nvl(ct.isnum,'N') = 'Y' or nvl(qc.monthqmnum,0) != 0) ");
		sf.append(" and " + SqlUtil.buildSqlForIn(" qc.pk_accsubj", slist.toArray(new String[slist.size()])));
		List<QcYeVO> list = (List<QcYeVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(QcYeVO.class));
		QcYeVO[] vos = (QcYeVO[]) list.toArray(new QcYeVO[list.size()]);
		Map<String, QcYeVO> map3 = hashlizeObject(vos);
		return map3;
	}

	public Map<String, QcYeVO> hashlizeObject(QcYeVO[] vos) {
		Map<String, QcYeVO> result = new HashMap<String, QcYeVO>();
		if (vos == null || vos.length == 0) {
			return result;
		}
		String key = null;
		QcYeVO vo = null;
		for (QcYeVO v : vos) {
			key = v.getPk_accsubj();
			if (!result.containsKey(key)) {
				result.put(key, v);
			} else {
				vo = result.get(key);
				if (vo != null) {
					vo.setMonthqmnum(SafeCompute.add(vo.getMonthqmnum(), v.getMonthqmnum()));
					vo.setThismonthqc(SafeCompute.add(vo.getThismonthqc(), v.getThismonthqc()));
				}
				result.put(key, vo);
			}
		}
		return result;
	}

	/**
	 * 不启用库存，，工业结转
	 * 
	 * @param corpVo
	 * @param 'vo'
	 * @param cbjzCount
	 * @return
	 * @throws BusinessException
	 */
	public QmclVO saveNoicQmvoIndustry(CorpVO corpVo, QmclVO qmvo, String cbjzCount) throws BusinessException {
		CostForward cf = new CostForward(yntBoPubUtil, singleObjectBO, voucher);
		cf.onCostForwardCheck(corpVo, qmvo, singleObjectBO);
		// 成本结转处理
		if ("1".equals(cbjzCount)) {
			qmvo.setCbjz1(DZFBoolean.TRUE);
		}
		// else if("2".equals(cbjzCount)){
		// qmvo.setCbjz2(DZFBoolean.TRUE);
		// }
		else if ("3".equals(cbjzCount)) {
			qmvo.setCbjz3(DZFBoolean.TRUE);
		} else if ("4".equals(cbjzCount)) {
			qmvo.setCbjz4(DZFBoolean.TRUE);
		} else if ("5".equals(cbjzCount)) {
			qmvo.setCbjz5(DZFBoolean.TRUE);
		} else if ("6".equals(cbjzCount)) {
			qmvo.setCbjz6(DZFBoolean.TRUE);
		}
		if (qmvo.getCbjz1().booleanValue() && qmvo.getCbjz3().booleanValue() && qmvo.getCbjz4().booleanValue()
				&& qmvo.getCbjz5().booleanValue() && qmvo.getCbjz6().booleanValue()) {
			qmvo.setIscbjz(DZFBoolean.TRUE);
		} else {
			qmvo.setIscbjz(DZFBoolean.FALSE);
		}
		// 保存期末结转VO
		if (StringUtil.isEmpty(qmvo.getPrimaryKey())) {
			qmvo = (QmclVO) singleObjectBO.saveObject(qmvo.getPk_corp(), qmvo);
		} else {// "cbjz2",
			singleObjectBO.update(qmvo,
					new String[] { "xsxjxcf", "cbjz1", "cbjz3", "cbjz4", "cbjz5", "cbjz6", "iscbjz" });
		}
		return qmvo;
	}

	public QmclVO saveQmclNoicSale(QmclVO qmvo, DZFBoolean isxjxcf) {
		qmvo.setIscbjz(DZFBoolean.TRUE);
		if (StringUtil.isEmpty(qmvo.getPrimaryKey())) {
			qmvo = (QmclVO) singleObjectBO.saveObject(qmvo.getPk_corp(), qmvo);
		} else {
			singleObjectBO.update(qmvo, new String[] { "xsxjxcf", "iscbjz" });
		}
		return qmvo;
	}

	public QmclVO saveSalenoicVoucher(QmclVO qmclvo, List<QMJzsmNoICVO> list, CorpVO corpVo, CpcosttransVO mbvo,
			YntCpaccountVO jfkmvo, String cbjzCount, List<String> listdfkm, String xjxcf,String userid,Map<String, YntCpaccountVO> kmsmap) throws BusinessException {
		// CorpVO corpVo1 =
		// (CorpVO)singleObjectBO.queryByPrimaryKey(CorpVO.class,
		// corpVo.getPk_corp());
		DZFBoolean isxjxcf = DZFBoolean.FALSE;
		if (!StringUtil.isEmpty(xjxcf) && "Y".equals(xjxcf)) {
			isxjxcf = DZFBoolean.TRUE;
		}
		//// 保存期末处理
		qmclvo.setXsxjxcf(isxjxcf);// 先进先出法
		// qmclvo.setClxjxcf(isxjxcf);//这个字段不知道什么意思
		if (IQmclConstant.z3 == corpVo.getIcostforwardstyle()) {// 工业
			saveNoicQmvoIndustry(corpVo, qmclvo, cbjzCount);
		} else if (IQmclConstant.z2 == corpVo.getIcostforwardstyle()) {// 商贸
			saveQmclNoicSale(qmclvo, isxjxcf);
		}
		// 处理暂估数据
		//
		TempDataTransFer datafer = new TempDataTransFer();//zpm 2019.9.12 没有采购，没有销售，连续暂估，但销售单价进行调整
		TempInvtoryVO zgvos[] = qmclvo.getZgdata();
		if (zgvos != null && zgvos.length > 0) {
			List<TempInvtoryVO> zglist = new ArrayList<TempInvtoryVO>();
			for (TempInvtoryVO zgvo : zgvos) {
				if (listdfkm.contains(zgvo.getKmbm())) {
					zglist.add(zgvo);
				}
			}
			ZgVoucher zg = new ZgVoucher(gl_cbconstant, singleObjectBO, yntBoPubUtil,parameterserv);
			if (zglist != null && zglist.size() > 0) {
				TzpzHVO billvo = zg.createPzvosNoIC(qmclvo, zglist.toArray(new TempInvtoryVO[0]), cbjzCount,userid,kmsmap);
				TzpzHVO nextvo = zg.queryNextcodeNoIC(qmclvo, billvo);
				voucher.saveVoucher(corpVo, billvo);
				voucher.saveVoucher(corpVo, nextvo);
			}
			datafer.setZglist(zglist);
			datafer.setZgdataisave(true);
		}

		NumberForward nf = new NumberForward(yntBoPubUtil, singleObjectBO, ic_rep_cbbserv, voucher,parameterserv);
		ExBusinessException ex = new ExBusinessException("");
		List<TempInvtoryVO> zlist = nf.numberForwardNoIC(mbvo, qmclvo, corpVo, list, jfkmvo, cbjzCount, listdfkm,
				isxjxcf,userid,datafer);
		if (zlist != null && zlist.size() > 0) {
			if (ex.getLmap() == null) {
				Map<String, List<TempInvtoryVO>> map = new HashMap<String, List<TempInvtoryVO>>();
				map.put(qmclvo.getPk_corp(), zlist);
				ex.setLmap(map);
			} else {
				ex.getLmap().put(qmclvo.getPk_corp(), zlist);
			}
			throw ex;
		}
		return qmclvo;
	}
	
	public List<TempInvtoryVO> getZgDataByCBB(QmclVO qmclvo, 
			List<QMJzsmNoICVO> list, 
			CorpVO corpVo, 
			int jztype,
			String cbjzCount, 
			DZFBoolean isxjxcf,
			String userid,
			Map<String, YntCpaccountVO> kmsmap) throws DZFWarpException{
		
		List<TempInvtoryVO> invtoryList = null;
		CpcosttransVO mbvos[] = getcbmbvos(corpVo.getPk_corp(), jztype);
		for (CpcosttransVO mbvo : mbvos) {
			String jfkmid = mbvo.getPk_debitaccount();
			String dfkmid = mbvo.getPk_creditaccount();
			YntCpaccountVO dfkmvo = cpaccountService.queryById(dfkmid);
			YntCpaccountVO jfkmvo = cpaccountService.queryById(jfkmid);
			
			List<String> listdfkm = getMjkmbms1(dfkmvo.getAccountcode(), corpVo.getPk_corp());
			NumberForward nf = new NumberForward(yntBoPubUtil, singleObjectBO, ic_rep_cbbserv, voucher,parameterserv);
			invtoryList = nf.getReportZGData(qmclvo, corpVo, list, jfkmvo, cbjzCount, listdfkm, isxjxcf, userid);
			if(invtoryList != null && invtoryList.size() > 0)
				break;
		}
		
		return invtoryList;
	}
}