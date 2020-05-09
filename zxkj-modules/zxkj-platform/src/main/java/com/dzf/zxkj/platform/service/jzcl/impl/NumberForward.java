package com.dzf.zxkj.platform.service.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.constant.IcConst;
import com.dzf.zxkj.common.entity.ITradeInfo;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.CpcosttransVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.IcbalanceVO;
import com.dzf.zxkj.platform.model.icset.IntradeoutVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.jzcl.*;
import com.dzf.zxkj.platform.model.pzgl.InvCurentVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.icreport.IQueryLastNum;
import com.dzf.zxkj.platform.service.icset.IInventoryService;
import com.dzf.zxkj.platform.service.icset.IQcService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.util.Kmschema;
import com.dzf.zxkj.platform.util.VoUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 商贸成本结转
 *
 */
public class NumberForward {

	private IYntBoPubUtil yntBoPubUtil = null;

	private SingleObjectBO singleObjectBO;

	private IQueryLastNum ic_rep_cbbserv;

	private IVoucherService voucher;

	private IParameterSetService parameterserv;

	private IAccountService accountService;

	public NumberForward(IYntBoPubUtil yntBoPubUtil, SingleObjectBO singleObjectBO, IQueryLastNum ic_rep_cbbserv,
						 IVoucherService voucher, IParameterSetService parameterserv, IAccountService accountService) {
		this.yntBoPubUtil = yntBoPubUtil;
		this.singleObjectBO = singleObjectBO;
		this.ic_rep_cbbserv = ic_rep_cbbserv;
		this.voucher = voucher;
		this.parameterserv = parameterserv;
		this.accountService = accountService;
	}

	/**
	 *
	 * @param gsmbVOs
	 *            公司模板
	 * @param vo
	 *            期末处理vo
	 * @param corpVo
	 *            公司
	 * @param mapinv
	 *            库存商品
	 * @return
	 * @throws DZFWarpException
	 */
	public List<TempInvtoryVO> numberForward(ITradeInfo[] gsmbVOs, QmclVO vo, CorpVO corpVo,
											 Map<String, TempInvtoryVO> mapinv, String userid) throws DZFWarpException {
		String pk_corp = corpVo.getPk_corp();
		// 最新库存
		Map<String, IcbalanceVO> map = ic_rep_cbbserv.queryLastBanlanceVOs_byMap1(getPeroidDZFDate(vo).toString(),
				pk_corp, null, true);
		List<TzpzHVO> listtzpz = new ArrayList<TzpzHVO>();
		// 暂估数据
		List<InvCurentVO> listinv = queryInvCurentVOs(gsmbVOs, vo, pk_corp);
		// mapinv 库存商品
		//支持部分暂估
		if (vo.getZgdata() == null || vo.getZgdata().length ==0) {
			List<TempInvtoryVO> listsss = check(listinv, map, mapinv);
			if (listsss != null && listsss.size() > 0) {
				return listsss;
			}
		}

		Map<String, List<InvCurentVO>> mapivn = DZfcommonTools.hashlizeObject(listinv, new String[] { "pk_accsubj" });
		for (ITradeInfo gsmbVO : gsmbVOs) {
			// 借方科目
			String jfkm = gsmbVO.getPk_debitaccount();
			// 贷方科目
			String dfkm = gsmbVO.getPk_creditaccount();
			// 取数科目
			String qskm = gsmbVO.getPk_fillaccount(); // 主营业务收入

			// 摘要
			String zy = getJzPzZy(gsmbVO.getAbstracts(), vo);
			//
			List<InvCurentVO> pzHVOs = mapivn.get(qskm);
			// 自动结转完成，即不生成结转凭证
			if (pzHVOs != null && pzHVOs.size() > 0) {
				TzpzHVO headVO = bulidTZBillVO(vo, jfkm, dfkm, zy, pzHVOs, map, corpVo, userid);
				if (headVO != null)
					listtzpz.add(headVO);
			}
		}

		// 生成凭证保存
		savevourcher(corpVo, listtzpz);
		return null;
	}

	private String getJzPzZy(String zy, QmclVO vo) {
		if (StringUtil.isEmpty(zy)) {
			return null;
		}
		if (zy.indexOf("X") >= 0 && zy.indexOf("月") >= 0) {
			String zy1 = zy.substring(0, zy.indexOf("X"));
			String zy2 = zy.substring(zy.indexOf("月"));
			String month = vo.getPeriod().substring(5);// 2016-01
			month = month.startsWith("0") ? month.substring(1) : month;
			zy = zy1 + month + zy2;
		}
		return zy;

	}

	/**
	 * 库存生成凭证流程 成本结转
	 *
	 * @param gsmbVOs
	 *            公司模板
	 * @param vo
	 *            期末处理vo
	 * @param corpVo
	 *            公司
	 * @param 'mapinv'
	 *            库存商品
	 * @return
	 * @throws DZFWarpException
	 */
	public List<TempInvtoryVO> numberJizhuanmode2(ITradeInfo[] gsmbVOs, QmclVO vo, CorpVO corpVo, String userid,Map<String, YntCpaccountVO> accmap)
			throws DZFWarpException {
		String pk_corp = corpVo.getPk_corp();

		// 本月销售成本及数量
		Map<String, IcbalanceVO> map = ic_rep_cbbserv.queryLastBanlanceVOs_byMap3(getPeroidDZFDate(vo).toString(),
				pk_corp, null, true);

		List<TzpzHVO> listtzpz = new ArrayList<TzpzHVO>();
		// 查询出库单 按照存货主键分装 库存单据 成本
		List<InvCurentVO> listinv = getInvCurentVOsByQskmIsNull(vo, pk_corp);

		//支持部分暂估
		if (vo.getZgdata() == null || vo.getZgdata().length == 0) {
			List<TempInvtoryVO> listsss = queryZgVOs(corpVo, vo);

			if (listsss != null && listsss.size() > 0) {
				return listsss;
			}
		}

		if (listinv != null && listinv.size() > 0) {


			for (ITradeInfo gsmbVO : gsmbVOs) {
				// 借方科目
				String jfkm = gsmbVO.getPk_debitaccount();
				// 贷方科目
				String dfkm = gsmbVO.getPk_creditaccount();
				List<String> dfkmlist = getDfkm(dfkm, pk_corp, accmap);
				// 摘要
				String zy = getJzPzZy(gsmbVO.getAbstracts(), vo);
				TzpzHVO headVO = bulidpzBillMode2(vo, jfkm, zy, listinv, map, corpVo, userid, accmap, dfkmlist);
				if (headVO != null) {
					listtzpz.add(headVO);
					// 回写对应的成本
					updateSaleCost(listinv, pk_corp);
				}
			}
		}

		// 生成凭证保存
		savevourcher(corpVo, listtzpz);

		return null;
	}

	private List<String> getDfkm(String accid, String pk_corp, Map<String, YntCpaccountVO> accmap) {
		List<String> list = new ArrayList<String>();

		if (accmap == null || accmap.isEmpty()) {
			return list;
		}
		YntCpaccountVO acc = accmap.get(accid);
		if (acc == null)
			return list;

		for (Iterator i = accmap.values().iterator(); i.hasNext();) {
			YntCpaccountVO accvo = (YntCpaccountVO) i.next();
			if(accvo.getAccountcode().startsWith(acc.getAccountcode())){
				list.add(accvo.getPk_corp_account());
			}
		}
		return list;
	}


	private void updateSaleCost(List<InvCurentVO> pzHVOs, String pk_corp) {
		List<IntradeoutVO> outlist = new ArrayList<>();
		for (InvCurentVO va : pzHVOs) {
			IntradeoutVO v = new IntradeoutVO();
			v.setNcost(va.getNdef4()==null? DZFDouble.ZERO_DBL:va.getNdef4());
			v.setAttributeValue("vdef1", va.getNdef5()==null?DZFDouble.ZERO_DBL:va.getNdef5());
			v.setPk_ictradeout(va.getPk_ictradeout());
			outlist.add(v);
		}

		singleObjectBO.updateAry(outlist.toArray(new IntradeoutVO[outlist.size()]), new String[] { "vdef1", "ncost" });
	}

	/**
	 * 库存新模式，取出库数据
	 */
	private List<InvCurentVO> getInvCurentVOsByQskmIsNull(QmclVO vo, String pk_corp) {

		StringBuffer sf1 = new StringBuffer();
		sf1.append(
				" select distinct y.*, t.accountcode,t.accountname,y.pk_subject,y.pk_ictradeout,h.isback,h.dbillid from ynt_ictradeout y ");
		sf1.append("  join ynt_cpaccount t on y.pk_subject = t.pk_corp_account ");
		sf1.append("  join ynt_ictrade_h h on y.pk_ictrade_h = h.pk_ictrade_h ");
		sf1.append("  where y.pk_corp = ? and nvl(h.isback,'N')='N' ");
		sf1.append("  and y.dbilldate like '" + vo.getPeriod() + "%' ");
		sf1.append("  and nvl(t.dr,0) = 0 and nvl(y.dr,0) = 0  and nvl(h.dr,0) = 0 ");
		// 取销售出库数据
		sf1.append("  and nvl(y.cbusitype,'46') = ? ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(IcConst.XSTYPE);
		@SuppressWarnings("unchecked")
		List<InvCurentVO> pzHVOs = (List<InvCurentVO>) singleObjectBO.executeQuery(sf1.toString(), sp,
				new ResultSetProcessor() {
					@Override
					public Object handleResultSet(java.sql.ResultSet rs) throws SQLException {
						List<InvCurentVO> list = new ArrayList<InvCurentVO>();
						String pk_corp = null;
						String pk_tzpz_h = null;
						String pk_tzpz_b = null;
						String pk_accsubj = null;
						String pk_inventory = null;
						String accountcode = null;
						String accountname = null;
						String pk_ictradeout = null;
						String isback = null;
						String dbillid = null;
						BigDecimal nnumber = null;
						while (rs.next()) {
							pk_corp = rs.getString("pk_corp");
							pk_tzpz_h = rs.getString("pk_voucher");
							pk_tzpz_b = rs.getString("pk_voucher_b");
							pk_accsubj = rs.getString("pk_subject");
							pk_inventory = rs.getString("pk_inventory");
							accountcode = rs.getString("accountcode");
							accountname = rs.getString("accountname");
							nnumber = rs.getBigDecimal("nnum");
							pk_ictradeout = rs.getString("pk_ictradeout");
							isback = rs.getString("isback");
							dbillid = rs.getString("dbillid");
							InvCurentVO env = new InvCurentVO();
							env.setPk_tzpz_b(pk_tzpz_b);
							env.setPk_corp(pk_corp);
							env.setPk_tzpz_h(pk_tzpz_h);
							env.setPk_accsubj(pk_accsubj);
							env.setPk_inventory(pk_inventory);
							env.setPk_ictradeout(pk_ictradeout);
							env.setAccountcode(accountcode);
							env.setAccountname(accountname);
							env.setDbillid(dbillid);
							env.setInvname(null);
							if (nnumber == null) {
								env.setNnumber(DZFDouble.ZERO_DBL);
							} else {
								env.setNnumber(new DZFDouble(nnumber.doubleValue()));
							}
							env.setDr(0);
							env.setIsback(new DZFBoolean(isback));
							list.add(env);
						}

						return list;
					}
				});
		Collections.sort(pzHVOs, new Comparator<InvCurentVO>() {
			@Override
			public int compare(InvCurentVO o1, InvCurentVO o2) {
				int i = o1.getDbillid().compareTo(o2.getDbillid());
				return i;
			}
		});
		return pzHVOs;

	}

	// private List<InvCurentVO> getInvCurentVOsByQskm(String qushu, QmclVO vo,
	// String pk_corp) {
	// StringBuffer sf1 = new StringBuffer();
	// sf1.append(" select distinct
	// y.*,b.vcode,b.vname,b.pk_accsubj,y.pk_ictradeout from ynt_ictradeout y
	// ");
	//
	// sf1.append(" join ynt_tzpz_h h on y.pk_voucher = h.pk_tzpz_h ");
	// sf1.append(" join ynt_tzpz_b b on h.pk_tzpz_h = b.pk_tzpz_h ");
	// sf1.append(" join ynt_cpaccount t on b.pk_accsubj = t.pk_corp_account ");
	// sf1.append(" where h.pk_corp = ? ");
	// sf1.append(" and b.pk_accsubj in " + qushu);
	// sf1.append(" and h.doperatedate like '" + vo.getPeriod() + "%' ");
	// sf1.append(" and nvl(h.dr,0)=0 and nvl(b.dr,0) = 0 and nvl(t.dr,0) = 0
	// and nvl(y.dr,0) = 0 ");
	// // 取销售出库数据
	// sf1.append(" and nvl(y.cbusitype,'46') = ? ");
	// SQLParameter sp = new SQLParameter();
	// sp.addParam(pk_corp);
	// sp.addParam(IcConst.XSTYPE);
	// @SuppressWarnings("unchecked")
	// List<InvCurentVO> pzHVOs = (List<InvCurentVO>)
	// singleObjectBO.executeQuery(sf1.toString(), sp,
	// new ResultSetProcessor() {
	// @Override
	// public Object handleResultSet(java.sql.ResultSet rs) throws SQLException
	// {
	// List<InvCurentVO> list = new ArrayList<InvCurentVO>();
	// String pk_corp = null;
	// String pk_tzpz_h = null;
	// String pk_tzpz_b = null;
	// String pk_accsubj = null;
	// String pk_inventory = null;
	// String accountcode = null;
	// String accountname = null;
	// String pk_ictradeout = null;
	// BigDecimal nnumber = null;
	//
	// while (rs.next()) {
	// pk_corp = rs.getString("pk_corp");
	// pk_tzpz_h = rs.getString("pk_voucher");
	// pk_tzpz_b = rs.getString("pk_voucher_b");
	// pk_accsubj = rs.getString("pk_accsubj");
	// pk_inventory = rs.getString("pk_inventory");
	// accountcode = rs.getString("vcode");
	// accountname = rs.getString("vname");
	// nnumber = rs.getBigDecimal("nnum");
	// pk_ictradeout = rs.getString("pk_ictradeout");
	// InvCurentVO env = new InvCurentVO();
	// env.setPk_tzpz_b(pk_tzpz_b);
	// env.setPk_corp(pk_corp);
	// env.setPk_tzpz_h(pk_tzpz_h);
	// env.setPk_accsubj(pk_accsubj);
	// env.setPk_inventory(pk_inventory);
	// env.setPk_ictradeout(pk_ictradeout);
	// env.setAccountcode(accountcode);
	// env.setAccountname(accountname);
	// env.setInvname(null);
	// env.setNnumber(new DZFDouble(nnumber.doubleValue()));
	// env.setDr(0);
	// list.add(env);
	// }
	//
	// return list;
	// }
	// });
	// return pzHVOs;
	// }

	/**
	 * 判断是否有暂估数据
	 */
	private List<InvCurentVO> queryInvCurentVOs(ITradeInfo[] gsmbVOs, QmclVO vo, String pk_corp) {
		String qushu = getQushukmin(gsmbVOs);
		StringBuffer sf1 = new StringBuffer();
		sf1.append(" select y.* from ynt_subinvtory y ");
		sf1.append("  join ynt_tzpz_b b on b.pk_tzpz_b = y.pk_tzpz_b ");
		sf1.append("  join ynt_tzpz_h h on b.pk_tzpz_h = h.pk_tzpz_h ");
		sf1.append("  join ynt_cpaccount t on b.pk_accsubj = t.pk_corp_account ");
		sf1.append("  where b.pk_accsubj in " + qushu + " and h.pk_corp = ? ");
		sf1.append("  and h.doperatedate like '" + vo.getPeriod() + "%' ");
		sf1.append("  and nvl(h.dr,0)=0 and nvl(b.dr,0) = 0 and nvl(t.dr,0) = 0 and nvl(y.dr,0) = 0 ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		List<InvCurentVO> pzHVOs = (List<InvCurentVO>) singleObjectBO.executeQuery(sf1.toString(), sp,
				new BeanListProcessor(InvCurentVO.class));
		return pzHVOs;
	}

	private String getQushukmin(ITradeInfo[] gsmbVOs) {
		if (gsmbVOs == null || gsmbVOs.length == 0) {
			return null;
		}
		StringBuffer sf = new StringBuffer();
		sf.append("(");
		int num = 0;
		for (int i = 0; i < gsmbVOs.length; i++) {
			String qskm = gsmbVOs[i].getPk_fillaccount();
			if (!StringUtil.isEmpty(qskm)) {
				if (num == 0)
					sf.append("'" + qskm + "'");
				else
					sf.append(",'" + qskm + "'");
				num++;
			}

		}
		if (num == 0)
			return null;
		sf.append(")");
		return sf.toString();
	}

	/**
	 * 加权结转
	 */
	public DZFDouble calcAvgnum(DZFDouble num1, DZFDouble sum1, DZFDouble num2) {
		if (num1 != null && num2 != null && num1.doubleValue() == num2.doubleValue())
			return sum1;
		DZFDouble sum2 = SafeCompute.div(SafeCompute.multiply(sum1, num2), num1);
		sum2 = sum2.setScale(2, DZFDouble.ROUND_HALF_UP);
		return sum2;
	}

	/**
	 * 查询库存商品VO
	 */
	public Map<String, TempInvtoryVO> queryInventoryVO(String pk_corp) {
		StringBuffer sf = new StringBuffer();
		sf.append(" select t1.name invname,t1.code spbm,t1.invspec spgg,t2.accountname kmname,t2.accountcode kmbm,t1.pk_inventory pk_invtory from ynt_inventory t1 ");
		sf.append(" join ynt_cpaccount t2 on t1.pk_subject = t2.pk_corp_account ");
		sf.append(" and  t1.pk_corp = t2.pk_corp and nvl(t1.dr,0) = 0 and nvl(t2.dr,0) = 0 ");
		sf.append(" where t1.pk_corp = ? ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		List<TempInvtoryVO> list = (List<TempInvtoryVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(TempInvtoryVO.class));
		Map<String, TempInvtoryVO> map = DZfcommonTools.hashlizeObjectByPk(list, new String[] { "pk_invtory" });
		return map;
	}

	/**
	 *
	 * @param cvos
	 *            //暂估数据
	 * @param map//最新库存
	 * @param maptmp
	 *            库存商品
	 * @return
	 * @throws DZFWarpException
	 */
	public List<TempInvtoryVO> check(List<InvCurentVO> cvos, Map<String, IcbalanceVO> map,
									 Map<String, TempInvtoryVO> maptmp) throws DZFWarpException {
		Map<String, TempInvtoryVO> maps = new HashMap<String, TempInvtoryVO>();
		Map<String, DZFDouble> netmap = new HashMap<String, DZFDouble>();
		if (cvos != null && cvos.size() > 0) {
			for (InvCurentVO cvo : cvos) {
				String pk_inventory = cvo.getPk_inventory();
				IcbalanceVO balancevo = map.get(pk_inventory);
				if (balancevo == null)
					balancevo = new IcbalanceVO();
				// 库存数量
				DZFDouble icbanlancenum = balancevo.getNnum() == null ? DZFDouble.ZERO_DBL : balancevo.getNnum();
				DZFDouble salenum = cvo.getNnumber();// 销售数量
				if (netmap.containsKey(pk_inventory)) {
					DZFDouble hj = SafeCompute.add(netmap.get(pk_inventory), salenum);
					netmap.put(pk_inventory, hj);
				} else {
					netmap.put(pk_inventory, salenum);
				}
				DZFDouble salehjnum = netmap.get(pk_inventory);
				DZFDouble ce = SafeCompute.sub(icbanlancenum, salehjnum);
				if (ce.doubleValue() < 0) {
					if (maps.containsKey(pk_inventory)) {
						TempInvtoryVO v = maps.get(pk_inventory);
						v.setNnumber(SafeCompute.multiply(ce, new DZFDouble(-1)));
						v.setNnumber_old(SafeCompute.multiply(ce, new DZFDouble(-1)));
					} else {
						TempInvtoryVO vo = new TempInvtoryVO();
						TempInvtoryVO vo1 = maptmp.get(pk_inventory);
						if (vo1 != null) {
							vo.setSpbm(vo1.getSpbm());
							vo.setInvname(vo1.getInvname());

							vo.setKmname(vo1.getKmname()+"_"+vo1.getInvname());
							if(!StringUtil.isEmpty(vo1.getSpgg())){
								vo.setKmname(vo1.getKmname()+"_"+vo1.getSpgg());
							}
							vo.setKmbm(vo1.getKmbm()+"_"+vo1.getSpbm());
						}
						vo.setPk_invtory(pk_inventory);
						vo.setNnumber(SafeCompute.multiply(ce, new DZFDouble(-1)));
						vo.setNnumber_old(SafeCompute.multiply(ce, new DZFDouble(-1)));
						maps.put(pk_inventory, vo);
					}
				}
			}
		}
		// 处理库存为负的情况
		List<TempInvtoryVO> zlist = getIcBancesub(maps, map, maptmp);
		List<TempInvtoryVO> zlist1 = new ArrayList<TempInvtoryVO>(maps.values());
		zlist1.addAll(zlist);
		return zlist1;
	}

	// 处理库存map中数量为负的情况
	private List<TempInvtoryVO> getIcBancesub(Map<String, TempInvtoryVO> maps1, Map<String, IcbalanceVO> map,
											  Map<String, TempInvtoryVO> maptmp) {
		if (maps1 != null && maps1.size() > 0) {
			for (String s : maps1.keySet()) {
				map.remove(s);
			}
		}
		Map<String, TempInvtoryVO> maps = new HashMap<String, TempInvtoryVO>();
		Iterator<String> it1 = map.keySet().iterator();
		while (it1.hasNext()) {
			String pk_inventory = it1.next();
			IcbalanceVO balancevo = map.get(pk_inventory);
			if (balancevo != null && balancevo.getNnum() != null && balancevo.getNnum().doubleValue() < 0) {
				TempInvtoryVO vo = new TempInvtoryVO();
				TempInvtoryVO vo1 = maptmp.get(pk_inventory);
				if (vo1 != null) {
					vo.setSpbm(vo1.getSpbm());
					vo.setInvname(vo1.getInvname());

					vo.setKmname(vo1.getKmname()+"_"+vo1.getInvname());
					if(!StringUtil.isEmpty(vo1.getSpgg())){
						vo.setKmname(vo1.getKmname()+"_"+vo1.getSpgg());
					}
					vo.setKmbm(vo1.getKmbm()+"_"+vo1.getSpbm());
				}
				vo.setPk_invtory(pk_inventory);
				vo.setNnumber(SafeCompute.multiply(balancevo.getNnum(), new DZFDouble(-1)));
				vo.setNnumber_old(SafeCompute.multiply(balancevo.getNnum(), new DZFDouble(-1)));
				maps.put(pk_inventory, vo);
			}
		}
		return new ArrayList<TempInvtoryVO>(maps.values());
	}

	private void savevourcher(CorpVO corpVo, List<TzpzHVO> listtzpz) {
		if (listtzpz == null || listtzpz.size() == 0)
			return;
		for (TzpzHVO s : listtzpz) {
			voucher.saveVoucher(corpVo, s);
		}
	}

	private YntCpaccountVO queryNewCpaccountVO(String accountcode, String pk_corp, String pk_invtory) {
		YntCpaccountVO vo = null;
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_invtory);
		String sql = " select pk_accsubj from  ynt_tzpz_b where pk_corp = ? and nvl(dr,0) = 0 and pk_inventory = ? "
				+ " and vcode like '" + accountcode + "%' ";
		String cs = (String) singleObjectBO.executeQuery(sql, sp, new ResultSetProcessor() {
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				String cs = null;
				if (rs.next()) {
					cs = rs.getString("pk_accsubj");
				}
				return cs;
			}
		});
		if (StringUtil.isEmpty(cs)) {
			// 取下面末级科目
			sql = " select * from ynt_cpaccount where pk_corp = ? " + " and accountcode like '" + accountcode
					+ "%' and isleaf = 'Y' and nvl(dr,0) =0 ";
			sp.clearParams();
			sp.addParam(pk_corp);
			List<YntCpaccountVO> list = (List<YntCpaccountVO>) singleObjectBO.executeQuery(sql, sp,
					new BeanListProcessor(YntCpaccountVO.class));
			if (list != null && list.size() > 0) {
				vo = list.get(0);
			}
		} else {
			vo = (YntCpaccountVO) singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, cs);
		}
		return vo;

	}

	/**
	 * 保存结转凭证--库存新模式，不暂估
	 */
	private TzpzHVO bulidpzBillMode2(QmclVO vo, String jfkm, String abstracts, List<InvCurentVO> pzHVOs,
									 Map<String, IcbalanceVO> map, CorpVO corpVo, String userid,Map<String, YntCpaccountVO> accmap,List<String> dfkmlist) throws DZFWarpException {
		DZFDouble ye = DZFDouble.ZERO_DBL;

		YntCpaccountVO jvo = accmap.get(jfkm);

		if (jvo == null)
			throw new BusinessException("借方科目不能为空！");

		YntCpaccountVO dvo = null;
		// checkIcStockHand(vo, pzHVOs, map);
		// 贷方科目可能选择的非末级。因为新版库存 ，要求为非末级
		// 计算贷方
//		List<String> chpks = new ArrayList<String>();
		Map<String, TzpzBVO>  pzmap = new HashMap<>();
		String priceStr = parameterserv.queryParamterValueByCode(corpVo.getPk_corp(), IParameterConstants.DZF010);
		int iprice = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		IcbalanceVO balancevo= null;
		for (InvCurentVO cvo : pzHVOs) {

			// 工业成本结转
//			if (IQmclConstant.z3 == corpVo.getIcostforwardstyle()) {
			//存货档案上的核算科目
			if(!dfkmlist.contains(cvo.getPk_accsubj())){
				continue;
			}
			dvo = accmap.get(cvo.getPk_accsubj());
			if (dvo == null)
				throw new BusinessException("贷方科目不能为空！");

//			}
			if (dvo.getIsleaf() == null || !dvo.getIsleaf().booleanValue()) {
                // 以下逻辑有问题 暂时注释掉  生成末级科目交由凭证处理 （凭证保存会处理成临时凭证）
//				dvo = queryNewCpaccountVO(dvo.getAccountcode(), cvo.getPk_corp(), cvo.getPk_inventory());
			}
			balancevo = map.get(cvo.getPk_inventory());

			if (balancevo == null)
				balancevo = new IcbalanceVO();

			DZFDouble df = calcAvgnum(balancevo.getNnum(), balancevo.getNcost(), cvo.getNnumber());
			DZFDouble price = SafeCompute.div(df, cvo.getNnumber());
			price = price.setScale(iprice, DZFDouble.ROUND_HALF_UP);
			TzpzBVO dfbodyVO = new TzpzBVO();
			dfbodyVO.setPk_accsubj(dvo.getPk_corp_account());// 贷方科目
			dfbodyVO.setVcode(dvo.getAccountcode());
			dfbodyVO.setVname(dvo.getAccountname());
			if (df == null ||df.doubleValue()==0) {
				continue;
			} else {
				dfbodyVO.setDfmny(df);
			}
			dfbodyVO.setYbdfmny(df);
			dfbodyVO.setZy(abstracts);// 摘要
			if (dvo.getIsfzhs().charAt(5) == '1') {
				dfbodyVO.setPk_inventory(cvo.getPk_inventory());
			}
			if (dvo.getIsnum() != null && dvo.getIsnum().booleanValue()) {
				dfbodyVO.setNnumber(cvo.getNnumber());
				dfbodyVO.setNprice(price);
			}
			if(df != null){
				df=df.setScale(2, DZFDouble.ROUND_HALF_UP);
				cvo.setNdef4(df);
			}else{
				cvo.setNdef4(DZFDouble.ZERO_DBL);
			}

			cvo.setNdef5(price);

			// 币种，默认人民币
			dfbodyVO.setPk_currency(DzfUtil.PK_CNY);
			dfbodyVO.setNrate(new DZFDouble(1));
			dfbodyVO.setPk_corp(corpVo.getPk_corp());
			ye = SafeCompute.add(ye, df);

			//
			String key = dfbodyVO.getPk_accsubj()+"&"+dfbodyVO.getPk_inventory()+"&"+(cvo.getNnumber() == null || cvo.getNnumber().doubleValue()>=0?"正":"负");
			if (pzmap.containsKey(key)) {
				TzpzBVO vo1 = pzmap.get(key);
				DZFDouble df1 = SafeCompute.add(VoUtils.getDZFDouble(vo1.getDfmny()), df);
				vo1.setDfmny(df1);
				vo1.setYbdfmny(df1);
				if (dvo.getIsnum() != null && dvo.getIsnum().booleanValue()) {
					DZFDouble nnum = SafeCompute.add(VoUtils.getDZFDouble(vo1.getNnumber()), cvo.getNnumber());
					vo1.setNnumber(nnum);
					DZFDouble price1 = SafeCompute.div(df1, nnum);
					price1 = price1.setScale(iprice, DZFDouble.ROUND_HALF_UP);
					vo1.setNprice(price1);
				}
				pzmap.put(key, vo1);
			} else {
				pzmap.put(key, dfbodyVO);
			}

			DZFDouble ybje = calcAvgnum(balancevo.getNnum(), balancevo.getNymny(), cvo.getNnumber());
			balancevo.setNnum(SafeCompute.sub(balancevo.getNnum(), cvo.getNnumber()));
			balancevo.setNcost(SafeCompute.sub(balancevo.getNcost(), df));
			balancevo.setNymny(SafeCompute.sub(balancevo.getNymny(), ybje));
			balancevo.setPk_subject(cvo.getPk_accsubj());// liangyi
		}
		DZFDouble jfnmny = DZFDouble.ZERO_DBL;
		DZFDouble df = DZFDouble.ZERO_DBL;
		DZFDouble num = DZFDouble.ZERO_DBL;
		List<TzpzBVO> list1 = new ArrayList<TzpzBVO>();
		for (TzpzBVO vo1 : pzmap.values()) {
			df = vo1.getDfmny();
			if(df != null){
				df=df.setScale(2, DZFDouble.ROUND_HALF_UP);
			}
			vo1.setDfmny(df);
			vo1.setYbjfmny(df);
			num = vo1.getNnumber();
//			if (df != null && !df.equals(DZFDouble.ZERO_DBL) && num != null && !num.equals(DZFDouble.ZERO_DBL)) {
			list1.add(vo1);
			jfnmny = SafeCompute.add(jfnmny, df);
//			}
		}

		if (list1 == null || list1.size() == 0)
			return null;
		if (jfnmny.compareTo(DZFDouble.ZERO_DBL) != 0) {
			TzpzBVO jfbodyVO = new TzpzBVO();
			jfbodyVO.setPk_accsubj(jfkm);// 借方科目
			jfbodyVO.setVcode(jvo.getAccountcode());
			jfbodyVO.setVname(jvo.getAccountname());
			jfbodyVO.setJfmny(jfnmny);
			jfbodyVO.setYbjfmny(jfnmny);
			jfbodyVO.setZy(abstracts);// 摘要
			// 币种，默认人民币
			jfbodyVO.setPk_currency(yntBoPubUtil.getCNYPk());
			jfbodyVO.setNrate(new DZFDouble(1));
			jfbodyVO.setPk_corp(corpVo.getPk_corp());
			list1.add(0, jfbodyVO);
		}
		// 生成结转凭证
		TzpzHVO headVO = new TzpzHVO();
		headVO.setPk_corp(vo.getPk_corp());
		headVO.setPzlb(0);// 凭证类别：记账
		headVO.setJfmny(jfnmny);
		headVO.setDfmny(jfnmny);
		headVO.setCoperatorid(userid);
		headVO.setIshasjz(DZFBoolean.FALSE);
		DZFDate nowDatevalue = getPeroidDZFDate(vo);
		headVO.setDoperatedate(nowDatevalue);
		headVO.setPzh(yntBoPubUtil.getNewVoucherNo(vo.getPk_corp(), nowDatevalue));
		headVO.setVbillstatus(8);// 默认自由态
		// 记录单据来源
		headVO.setSourcebillid(vo.getPk_qmcl());
		headVO.setSourcebilltype(IBillTypeCode.HP34);
		headVO.setPeriod(vo.getPeriod());
		headVO.setVyear(Integer.valueOf(vo.getPeriod().substring(0, 4)));
		headVO.setIsfpxjxm(new DZFBoolean("N"));

		headVO.setChildren(list1.toArray(new TzpzBVO[0]));

		return headVO;
	}


	public List<TempInvtoryVO> queryZgVOs(CorpVO corpvo, QmclVO qmvo) throws DZFWarpException {

		String period = qmvo.getPeriod();
		// 查询该期间期末处理数据
		String pk_corp = corpvo.getPk_corp();
		List<String> corppks = new ArrayList<String>();
		corppks.add(pk_corp);
		DZFDate icdate = corpvo.getIcbegindate();
		if(icdate == null)
			throw new BusinessException("启用库存日期不能为空");
		String icperiod= DateUtils.getPeriod(icdate);

		Map<String, IcbalanceVO> map = ic_rep_cbbserv
				.queryLastBanlanceVOs_byMap1(DateUtils.getPeriodEndDate(period).toString(), pk_corp, null, true);// 根据单据日期查询
		if (map == null || map.size() == 0) {
			return null;
		}
		Map<String, IcbalanceVO> lastmap  = null;
		if(icperiod.equals(period)){
			IQcService iservice  = (IQcService) SpringUtils.getBean("ic_qcserv");
			List<IcbalanceVO> list = iservice.quyerInfovoic(pk_corp);
			lastmap =DZfcommonTools.hashlizeObjectByPk(list, new String[]{"pk_inventory"});

		}else{
			lastmap = ic_rep_cbbserv.queryLastBanlanceVOs_byMap1(
					DateUtils.getPeriodEndDate(DateUtils.getPreviousPeriod(period)).toString(), pk_corp, null, true);// 根据单据日期查询上期结存
		}

		String begindate = DateUtils.getPeriodStartDate(period).toString();
		String enddate = DateUtils.getPeriodEndDate(period).toString();

		List<IcbalanceVO> outlist = queryCurrentPeriodSaleOut(begindate, enddate, pk_corp);
		Map<String, IcbalanceVO> outMap = DZfcommonTools.hashlizeObjectByPk(outlist, new String[] { "pk_inventory" });

		List<IcbalanceVO> inlist = queryCurrentPeriodPurchIn(begindate, enddate, pk_corp);
		Map<String, IcbalanceVO> inMap = DZfcommonTools.hashlizeObjectByPk(inlist, new String[] { "pk_inventory" });
		//
		List<IcbalanceVO> inlist_sub = queryCurrentPeriodPurchIn_sub(begindate, enddate, pk_corp);
		Map<String, IcbalanceVO> inMap_sub = DZfcommonTools.hashlizeObjectByPk(inlist_sub, new String[] { "pk_inventory" });
//		Map<String, IcbalanceVO> inMap = new HashMap<>();
		IInventoryService ic_invserv  = (IInventoryService) SpringUtils.getBean("ic_inventoryserv");
		List<InventoryVO> invlist = ic_invserv.query(pk_corp);
		Map<String, InventoryVO> tempMap = DZfcommonTools.hashlizeObjectByPk(invlist, new String[] { "pk_inventory" });
		List<TempInvtoryVO> list = new ArrayList<>();
		IcbalanceVO zgvo = null;

		String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
		int iprice = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		String numStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF009);
		int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);

		for (Entry<String, IcbalanceVO> entry : map.entrySet()) {
			IcbalanceVO balvo = entry.getValue();

			if (balvo == null || balvo.getNnum() == null || balvo.getNnum().doubleValue() >= 0)
				continue;
			TempInvtoryVO vo = new TempInvtoryVO();
			vo.setId(UUID.randomUUID().toString());
			if (tempMap != null && tempMap.size() > 0) {
				if (tempMap.get(balvo.getPk_inventory()) != null)
					vo.setKmid(tempMap.get(balvo.getPk_inventory()).getPk_subject());
			}
			vo.setPk_invtory(balvo.getPk_inventory());
			vo.setSpbm(balvo.getInventorycode());
			vo.setInvname(balvo.getInventoryname());
			String xh = balvo.getInvtype();

			if (StringUtil.isEmpty(xh)) {
				vo.setSpgg(balvo.getInvspec());
			} else {
				if (StringUtil.isEmpty(balvo.getInvspec())) {
					vo.setSpgg("(" + xh + ")");
				} else {
					vo.setSpgg(balvo.getInvspec() + "(" + xh + ")");
				}
			}
			vo.setKmname(balvo.getPk_subjectname()+"_"+vo.getInvname());

			if(!StringUtil.isEmpty(vo.getSpgg())){
				vo.setKmname(vo.getKmname()+"_"+vo.getSpgg());
			}

			vo.setKmbm(balvo.getPk_subjectcode()+"_"+vo.getSpbm());
			// 设置数量精度
			DZFDouble numm = balvo.getNnum();
			if (numm != null) {
				numm = numm.setScale(num, DZFDouble.ROUND_HALF_UP);
			} else {
				numm = DZFDouble.ZERO_DBL;
			}


			vo.setJldw(balvo.getMeasurename());
			vo.setPk_gs(pk_corp);
			vo.setGsname(corpvo.getUnitname());
			vo.setPeriod(period);
			vo.setNnumber_old(numm.abs());
			vo.setNnumber(numm.abs());
			zgvo = getZgvo(balvo.getPk_inventory(), lastmap, outMap, inMap,inMap_sub);
			if (zgvo != null) {
				vo.setNprice(zgvo.getNprice().setScale(iprice, DZFDouble.ROUND_HALF_UP));
			}
			vo.setNmny(SafeCompute.multiply(vo.getNnumber(), vo.getNprice()).setScale(2, DZFDouble.ROUND_HALF_UP));
			list.add(vo);
		}

		return list;
	}

	private IcbalanceVO getZgvo(String pk_inventory, Map<String, IcbalanceVO> lastmap, Map<String, IcbalanceVO> outMap,
								Map<String, IcbalanceVO> inMap,Map<String, IcbalanceVO> inMap_sub) {
		// 暂估单价优先取上期结存单价（成本表）、无上期结存取本期购入平均价（该存货入库单未税金
		// 额相加/数量合计）、无本期购入，取本期发出平均销售价（该存货出库单未税金额相加/数量合计）
		IcbalanceVO zgvo = lastmap.get(pk_inventory);

		DZFDouble nprice = DZFDouble.ZERO_DBL;
		if (zgvo != null) {
			if (zgvo != null) {
				if(zgvo.getNnum() !=null && zgvo.getNnum().doubleValue()>0
						&& zgvo.getNcost() !=null && zgvo.getNcost().doubleValue()>0 ){
					nprice = getZgprice(zgvo, true);
					zgvo.setNymny(zgvo.getNcost());
				}

			}
		}

		if (nprice.doubleValue() <= 0) {
			zgvo = inMap.get(pk_inventory);
			if (zgvo != null) {
				nprice = getZgprice(zgvo, false);
			}
		}
		//zpm2019.9.20
		if(nprice == null || nprice.doubleValue() == 0){
			zgvo = inMap_sub.get(pk_inventory);
			if (zgvo != null) {
				nprice = getZgprice(zgvo, false);
			}
		}

		if (nprice.doubleValue() <= 0) {
			zgvo = outMap.get(pk_inventory);
			if (zgvo != null) {
				nprice = getZgprice(zgvo, false);
			}
		}
		if (zgvo != null) {
			zgvo.setNprice(nprice);
		}
		return zgvo;
	}

	private DZFDouble getZgprice(IcbalanceVO zgvo, boolean isqc) {

		DZFDouble nmny = DZFDouble.ZERO_DBL;
		if (isqc) {
			nmny = zgvo.getNcost();
		} else {
			nmny = zgvo.getNymny();
		}
		DZFDouble nprice = SafeCompute.div(nmny, zgvo.getNnum());
		return nprice;
	}

	private List<IcbalanceVO> queryCurrentPeriodSaleOut(String lastdate, String currentenddate, String pk_corp)
			throws DZFWarpException {
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" select sum(nnum) nnum ,sum(nymny) nymny,pk_inventory from ( ");
		// 出库
		sf.append("  (select out1.pk_inventory,nnum as nnum ,nymny as nymny from ynt_ictradeout out1 ");
		sf.append("   where out1.dbilldate >= ? and out1.dbilldate <= ? and out1.pk_corp =  ? and nvl(out1.dr,0)=0  and nvl(out1.nnum,0)>0 and nvl(out1.nymny,0)>0 ");
		sp.addParam(lastdate);
		sp.addParam(currentenddate);
		sp.addParam(pk_corp);
		sf.append("  )) group by pk_inventory ");
		List<IcbalanceVO> ancevos = (List<IcbalanceVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(IcbalanceVO.class));
		return ancevos;
	}

	private List<IcbalanceVO> queryCurrentPeriodPurchIn(String lastdate, String currentenddate, String pk_corp)
			throws DZFWarpException {
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" select sum(nnum) nnum ,sum(nymny) nymny,pk_inventory from ( ");
		sf.append("  (select in1.pk_inventory,nnum,nymny from ynt_ictradein in1 ");
		sf.append("   where in1.dbilldate >= ? and in1.dbilldate <= ? and in1.pk_corp = ? and nvl(in1.dr,0)=0 and nvl(in1.nnum,0)>0 and nvl(in1.nymny,0)>0 ");
		sp.addParam(lastdate);
		sp.addParam(currentenddate);
		sp.addParam(pk_corp);
		sf.append("  )");

		sf.append("  ) group by pk_inventory");
		List<IcbalanceVO> ancevos = (List<IcbalanceVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(IcbalanceVO.class));
		return ancevos;
	}

	private List<IcbalanceVO> queryCurrentPeriodPurchIn_sub(String lastdate, String currentenddate, String pk_corp)
			throws DZFWarpException {
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" select sum(nnum) nnum ,sum(nymny) nymny,pk_inventory from ( ");
		sf.append("  (select in1.pk_inventory,nnum,nymny from ynt_ictradein in1 ");
		sf.append("   where in1.dbilldate >= ? and in1.dbilldate <= ? and in1.pk_corp = ? and nvl(in1.dr,0)=0 and nvl(in1.nnum,0)<0 and nvl(in1.nymny,0)<0 ");
		sp.addParam(lastdate);
		sp.addParam(currentenddate);
		sp.addParam(pk_corp);
		sf.append("  )");

		sf.append("  ) group by pk_inventory");
		List<IcbalanceVO> ancevos = (List<IcbalanceVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(IcbalanceVO.class));
		return ancevos;
	}

	/**
	 * 保存结转凭证
	 */
	private TzpzHVO bulidTZBillVO(QmclVO vo, String jfkm, String dfkm, String abstracts, List<InvCurentVO> pzHVOs,
								  Map<String, IcbalanceVO> map, CorpVO corpVo, String userid) throws DZFWarpException {
		List<TzpzBVO> list = new ArrayList<TzpzBVO>();
		DZFDouble ye = new DZFDouble(0.0);
		YntCpaccountVO jvo = (YntCpaccountVO) singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, jfkm);
		YntCpaccountVO dvo = (YntCpaccountVO) singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, dfkm);
		// 计算贷方
		String priceStr = parameterserv.queryParamterValueByCode(corpVo.getPk_corp(), IParameterConstants.DZF010);
		int iprice = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		List<String> chpks = new ArrayList<String>();
		IcbalanceVO balancevo = null;
		for (InvCurentVO cvo : pzHVOs) {
			balancevo = map.get(cvo.getPk_inventory());
			if(balancevo == null)
				balancevo = new IcbalanceVO();
			DZFDouble df = calcAvgnum(balancevo.getNnum(), balancevo.getNcost(), cvo.getNnumber());
			DZFDouble price = SafeCompute.div(df, cvo.getNnumber());
			price = price.setScale(iprice, DZFDouble.ROUND_HALF_UP);
			TzpzBVO dfbodyVO = new TzpzBVO();
			dfbodyVO.setPk_accsubj(dfkm);// 贷方科目
			dfbodyVO.setVcode(dvo.getAccountcode());
			dfbodyVO.setVname(dvo.getAccountname());
			dfbodyVO.setDfmny(df);
			if (df.equals(DZFDouble.ZERO_DBL)) {
				return null;
			}
			dfbodyVO.setYbdfmny(df);
			dfbodyVO.setZy(abstracts);// 摘要
			dfbodyVO.setPk_inventory(cvo.getPk_inventory());
			dfbodyVO.setNnumber(cvo.getNnumber());
			dfbodyVO.setNprice(price);
			// 币种，默认人民币
			dfbodyVO.setPk_currency(DzfUtil.PK_CNY);
			dfbodyVO.setNrate(new DZFDouble(1));
			dfbodyVO.setPk_corp(corpVo.getPk_corp());
			ye = SafeCompute.add(ye, df);
			if (chpks.contains(cvo.getPk_inventory())) {
				for (TzpzBVO vo1 : list) {
					if (vo1.getPk_inventory().equals(cvo.getPk_inventory())) {
						DZFDouble df1 = SafeCompute.add(VoUtils.getDZFDouble(vo1.getDfmny()), df);
						vo1.setDfmny(df1);
						vo1.setYbdfmny(df1);
						DZFDouble nnum = SafeCompute.add(VoUtils.getDZFDouble(vo1.getNnumber()), cvo.getNnumber());
						vo1.setNnumber(nnum);
						DZFDouble price1 = SafeCompute.div(df1, nnum);
						price1 = price1.setScale(iprice, DZFDouble.ROUND_HALF_UP);
						vo1.setNprice(price1);
					}
				}
			} else {
				list.add(dfbodyVO);
			}

			DZFDouble ybje = calcAvgnum(balancevo.getNnum(), balancevo.getNymny(), cvo.getNnumber());
			balancevo.setNnum(SafeCompute.sub(balancevo.getNnum(), cvo.getNnumber()));
			balancevo.setNcost(SafeCompute.sub(balancevo.getNcost(), df));
			balancevo.setNymny(SafeCompute.sub(balancevo.getNymny(), ybje));
			balancevo.setPk_subject(cvo.getPk_accsubj());// liangyi

			chpks.add(cvo.getPk_inventory());
		}
		//
		TzpzBVO jfbodyVO = new TzpzBVO();
		jfbodyVO.setPk_accsubj(jfkm);// 借方科目
		jfbodyVO.setVcode(jvo.getAccountcode());
		jfbodyVO.setVname(jvo.getAccountname());
		jfbodyVO.setJfmny(ye);
		jfbodyVO.setYbjfmny(ye);
		jfbodyVO.setZy(abstracts);// 摘要
		// 币种，默认人民币
		jfbodyVO.setPk_currency(DzfUtil.PK_CNY);
		jfbodyVO.setNrate(new DZFDouble(1));
		jfbodyVO.setPk_corp(corpVo.getPk_corp());
		list.add(0, jfbodyVO);
		// 生成结转凭证
		TzpzHVO headVO = new TzpzHVO();
		headVO.setPk_corp(vo.getPk_corp());
		headVO.setPzlb(0);// 凭证类别：记账
		headVO.setJfmny(ye);
		headVO.setDfmny(ye);
		headVO.setCoperatorid(userid);
		headVO.setIshasjz(DZFBoolean.FALSE);
		// DZFDate nowDate = DZFDate.getDate(new
		// Long(InvocationInfoProxy.getInstance().getDate())) ;
		// headVO.setDoperatedate(nowDate) ;
		DZFDate nowDatevalue = getPeroidDZFDate(vo);
		headVO.setDoperatedate(nowDatevalue);
		headVO.setPzh(yntBoPubUtil.getNewVoucherNo(vo.getPk_corp(), nowDatevalue));
		headVO.setVbillstatus(8);// 默认自由态
		// 记录单据来源
		headVO.setSourcebillid(vo.getPk_qmcl());
		headVO.setSourcebilltype(IBillTypeCode.HP34);
		headVO.setPeriod(vo.getPeriod());
		headVO.setVyear(Integer.valueOf(vo.getPeriod().substring(0, 4)));
		headVO.setIsfpxjxm(new DZFBoolean("N"));

		headVO.setChildren(list.toArray(new TzpzBVO[0]));

		return headVO;
	}

	public DZFDate getPeroidDZFDate(QmclVO vo) {
		DZFDate period = new DZFDate(vo.getPeriod() + "-01");
		period = new DZFDate(vo.getPeriod() + "-" + period.getDaysMonth());
		return period;
	}

	private Map<String, List<TzpzBVO>> getPZVOS(String pk_corp, String date, boolean bool) {
		Map<String, List<TzpzBVO>> map = new HashMap<String, List<TzpzBVO>>();
		String sql = " select tb.* from ynt_tzpz_b tb join ynt_tzpz_h th on tb.pk_tzpz_h = th.pk_tzpz_h where th.period < ? and tb.pk_corp = ? and nvl(tb.dr,0) = 0 order by  th.doperatedate ,th.pzh ";
		if (bool) {
			sql = " select tb.* from ynt_tzpz_b tb join ynt_tzpz_h th on tb.pk_tzpz_h = th.pk_tzpz_h where th.period = ? and tb.pk_corp = ? and nvl(tb.dr,0) = 0  order by  th.doperatedate ,th.pzh ";
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(date);
		sp.addParam(pk_corp);
		List<TzpzBVO> bvos = (List<TzpzBVO>) singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(TzpzBVO.class));
		for (TzpzBVO bvo : bvos) {
			String key = bvo.getPk_accsubj() + bvo.getFzhsx6();
			if (map.containsKey(key)) {
				List<TzpzBVO> templist = map.get(key);
				templist.add(bvo);
			} else {
				List<TzpzBVO> templist = new ArrayList<TzpzBVO>();
				templist.add(bvo);
				map.put(key, templist);
			}
		}
		return map;
	}

	private Map<String, List<TzpzBVO>> getPeriodMap(String date, String pk_corp) {
		Map<String, List<TzpzBVO>> map = new HashMap<String, List<TzpzBVO>>();
		String sql = " select tb.*,th.period from ynt_tzpz_b tb join ynt_tzpz_h th on tb.pk_tzpz_h = th.pk_tzpz_h where th.period <= ? and tb.pk_corp = ? and nvl(tb.dr,0) = 0 order by  th.doperatedate ,th.pzh ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(date);
		sp.addParam(pk_corp);
		// singleObjectBO.executeQuery(sql, sp,new ResultSetProcessor(){
		//
		// @Override
		// public Object handleResultSet(ResultSet arg0) throws SQLException {
		// // TODO Auto-generated method stub
		// return null;
		// }});
		List<TzpzBVO> bvos = (List<TzpzBVO>) singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(TzpzBVO.class));
		for (TzpzBVO bvo : bvos) {
			if (map.containsKey(bvo.getPeriod())) {
				List<TzpzBVO> templist = map.get(bvo.getPeriod());
				templist.add(bvo);
			} else {
				List<TzpzBVO> templist = new ArrayList<TzpzBVO>();
				templist.add(bvo);
				map.put(bvo.getPeriod(), templist);
			}
		}
		return map;
	}

	private Map<String, QcYeVO> getQCVOS(String pk_corp) {
		Map<String, QcYeVO> map = new HashMap<String, QcYeVO>();
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String sf = " pk_corp = ? and nvl(dr,0) = 0 and monthqmnum != 0 ";
		QcYeVO[] qcvos = (QcYeVO[]) singleObjectBO.queryByCondition(QcYeVO.class, sf, sp);
		for (QcYeVO vo : qcvos) {
			String key = vo.getPk_accsubj() + vo.getFzhsx6();
			if (map.containsKey(key)) {
			} else {
				map.put(key, vo);
			}
		}
		return map;
	}

	/**
	 * 其它地方调用暂估数据，目前wzn 调用。2018.8.30
	 */
	public List<TempInvtoryVO> getReportZGData(QmclVO qmclvo, CorpVO corpVo, List<QMJzsmNoICVO> list1,
											   YntCpaccountVO jfkmvo, String cbjzCount, List<String> listdfkm, DZFBoolean isxjxcf, String userid) {
		List<TzpzBVO> listTzpzBVO = new ArrayList<TzpzBVO>();
		Transfervo fervo = new Transfervo();
		fervo.setYe(DZFDouble.ZERO_DBL);
		fervo.setZy("");
		// 暂时传个空 TempDataTransFer
		List<TempInvtoryVO> zglist = getZGData(fervo, listTzpzBVO, qmclvo, corpVo, list1, jfkmvo, cbjzCount, listdfkm,
				isxjxcf, userid, new TempDataTransFer());
		return zglist;
	}

	public List<TempInvtoryVO> numberForwardNoIC(CpcosttransVO mbvo, QmclVO qmclvo, CorpVO corpVo,
												 List<QMJzsmNoICVO> list1, YntCpaccountVO jfkmvo, String cbjzCount, List<String> listdfkm,
												 DZFBoolean isxjxcf, String userid, TempDataTransFer datafer) {
		List<TzpzBVO> listTzpzBVO = new ArrayList<TzpzBVO>();
		Transfervo fervo = new Transfervo();
		fervo.setYe(DZFDouble.ZERO_DBL);
		fervo.setZy(mbvo.getAbstracts());
		List<TempInvtoryVO> zglist = getZGData(fervo, listTzpzBVO, qmclvo, corpVo, list1, jfkmvo, cbjzCount, listdfkm,
				isxjxcf, userid,datafer);
		if (zglist != null && zglist.size() > 0) {
			return zglist;
		}
		DZFDouble ye = fervo.getYe();
		String zy = fervo.getZy();
		saveCBJZVoucher(listTzpzBVO, ye, jfkmvo, zy, corpVo, qmclvo, userid, cbjzCount);
		return null;
	}

	private List<TempInvtoryVO> getZGData(Transfervo transfervo, List<TzpzBVO> listTzpzBVO, QmclVO qmclvo,
										  CorpVO corpVo, List<QMJzsmNoICVO> list1, YntCpaccountVO jfkmvo, String cbjzCount, List<String> listdfkm,
										  DZFBoolean isxjxcf, String userid,TempDataTransFer datafer) {
		String priceStr = parameterserv.queryParamterValueByCode(qmclvo.getPk_corp(), IParameterConstants.DZF010);
		Integer pricejingdu = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);

		List<TempInvtoryVO> zglist = new ArrayList<TempInvtoryVO>();
		String zy = transfervo.getZy();
		DZFDouble ye = transfervo.getYe();

		zy = getJzPzZy(zy, qmclvo);
//		boolean xjxcf = isxjxcf.booleanValue();
		// 计算贷方
//		Map<String, List<TzpzBVO>> map = getPZVOS(qmclvo.getPk_corp(), qmclvo.getPeriod(), false);
//		Map<String, List<TzpzBVO>> maptm = getPZVOS(qmclvo.getPk_corp(), qmclvo.getPeriod(), true);
//		Map<String, List<TzpzBVO>> mapperiod = getPeriodMap(qmclvo.getPeriod(), qmclvo.getPk_corp());
//		Map<String, QcYeVO> qcmap = getQCVOS(qmclvo.getPk_corp());
		for (QMJzsmNoICVO cvo : list1) {
			//存在多个结转模板  只出里当前模板的对应科目的数据  20190716
			if (!listdfkm.contains(cvo.getKmbm())) {
				continue;
			}
			DZFDouble df = DZFDouble.ZERO_DBL;
			DZFDouble price = DZFDouble.ZERO_DBL;
			if (StringUtil.isEmpty(cvo.getFzid())) {
				cvo.setFzid(null);
			}
			// 【单到冲回本月暂估】情况，本月的暂估是由上月暂估生成的
			TzpzBVO tzpzbvo = calcNonumhasMny(cvo, zy, corpVo.getPk_corp(),datafer);
			if (tzpzbvo != null) {
				listTzpzBVO.add(tzpzbvo);
				ye = SafeCompute.add(ye, tzpzbvo.getDfmny());
				continue;
			}
			// 红冲上月的情况,红冲本月不处理。
			if (cvo.getBqfcnum() != null && cvo.getBqfcnum().doubleValue() < 0) {


				TzpzBVO pzbvo = calcRedNumMny(corpVo, cvo, zy, corpVo.getPk_corp(), qmclvo, pricejingdu);
				if (pzbvo != null) {
					listTzpzBVO.add(pzbvo);
					ye = SafeCompute.add(ye, pzbvo.getDfmny());

					DZFDouble temp = SafeCompute.sub(VoUtils.getDZFDouble(cvo.getBqfcnum()),
							SafeCompute.add(VoUtils.getDZFDouble(cvo.getQcnum()), VoUtils.getDZFDouble(cvo.getBqsrnum())));
					//
					if (!datafer.isZgdataisave() && temp.doubleValue() > 0) {
						TempInvtoryVO zgvo =createZgVO(cvo, temp, pricejingdu);
						zglist.add(zgvo);
					}
					continue;
				}
			}
			//
//			if (xjxcf) {
//				DZFDouble qcnum = VoUtils.getDZFDouble(cvo.getQcnum());
//				DZFDouble bqsrnum = VoUtils.getDZFDouble(cvo.getBqsrnum());
//				DZFDouble total = SafeCompute.add(qcnum, bqsrnum);
//				DZFDouble bqfcnum = VoUtils.getDZFDouble(cvo.getBqfcnum());
//				if (bqfcnum.doubleValue() < qcnum.doubleValue()) {// 上查
//					List<TzpzBVO> bvolist = map.get(cvo.getKmid() + cvo.getFzid());
//					if (bvolist != null && bvolist.size() > 0) {
//						List<TzpzBVO> templist = new ArrayList<TzpzBVO>();
//						DZFDouble num = DZFDouble.ZERO_DBL;
//						DZFDouble mny = DZFDouble.ZERO_DBL;
//						for (int i = bvolist.size() - 1; i >= 0; i--) {
//							TzpzBVO bvo = bvolist.get(i);
//							if (bvo.getJfmny() != null && bvo.getJfmny().doubleValue() != 0) {
//								templist.add(bvo);
//								if (num.doubleValue() >= qcnum.doubleValue()) {
//									break;
//								}
//								num = SafeCompute.add(num, VoUtils.getDZFDouble(bvo.getNnumber()));
//								mny = SafeCompute.add(mny, VoUtils.getDZFDouble(bvo.getJfmny()));
//								if (num.doubleValue() >= qcnum.doubleValue()) {
//									break;
//								}
//							}
//						}
//						if (num.doubleValue() < qcnum.doubleValue()) {// 需要从期初取数
//							DZFDouble tempnum = SafeCompute.sub(qcnum, num);
//							// QcYeVO qcvo = qcmap.get(cvo.getKmid());
//							// if (qcvo != null) {
//							DZFDouble qcnum1 = cvo.getQcnum();
//							DZFDouble qcmny1 = cvo.getQcmny();
//							DZFDouble prices = SafeCompute.div(VoUtils.getDZFDouble(qcmny1), qcnum1);
//							if (tempnum.doubleValue() < bqfcnum.doubleValue()) {
//								df = SafeCompute.add(df, SafeCompute.multiply(prices, tempnum));
//								for (int i = templist.size() - 1; i >= 0; i--) {
//									TzpzBVO bvo = templist.get(i);
//									tempnum = SafeCompute.add(tempnum, bvo.getNnumber());
//									if (tempnum.doubleValue() < bqfcnum.doubleValue()) {
//										// df = SafeCompute.add(df,
//										// SafeCompute.multiply(bvo.getJfmny(),
//										// bvo.getNnumber()));
//										df = SafeCompute.add(df, bvo.getJfmny());
//									} else {
//										DZFDouble pricess = SafeCompute.div(VoUtils.getDZFDouble(bvo.getJfmny()),
//												VoUtils.getDZFDouble(bvo.getNnumber()));
//										DZFDouble d = SafeCompute.sub(tempnum, bqfcnum);
//										DZFDouble ddd = SafeCompute.sub(bvo.getNnumber(), d);
//										df = SafeCompute.add(df, SafeCompute.multiply(pricess, ddd));
//										break;
//									}
//								}
//							} else {
//								df = SafeCompute.add(df, SafeCompute.multiply(prices, bqfcnum));
//							}
//							// }
//						} else {
//							DZFDouble tempnum = SafeCompute.sub(num, qcnum);
//							DZFDouble temp = DZFDouble.ZERO_DBL;
//							for (int i = templist.size() - 1; i >= 0; i--) {
//								TzpzBVO bvo = templist.get(i);
//								DZFDouble d = SafeCompute.sub(templist.get(templist.size() - 1).getNnumber(), tempnum);
//								if (i == templist.size() - 1) {
//									temp = SafeCompute.add(temp, d);
//									if (d.doubleValue() < bqfcnum.doubleValue()) {
//										DZFDouble pricess = SafeCompute.div(VoUtils.getDZFDouble(bvo.getJfmny()),
//												VoUtils.getDZFDouble(bvo.getNnumber()));
//										df = SafeCompute.add(df, SafeCompute.multiply(pricess, d));
//									} else {
//										DZFDouble pricess = SafeCompute.div(VoUtils.getDZFDouble(bvo.getJfmny()),
//												VoUtils.getDZFDouble(bvo.getNnumber()));
//										df = SafeCompute.add(df, SafeCompute.multiply(pricess, bqfcnum));
//										break;
//									}
//								} else {
//									temp = SafeCompute.add(temp, bvo.getNnumber());
//									if (temp.doubleValue() < bqfcnum.doubleValue()) {
//										// df = SafeCompute.add(df,
//										// SafeCompute.multiply(bvo.getJfmny(),
//										// bvo.getNnumber()));
//										df = SafeCompute.add(df, bvo.getJfmny());
//									} else {
//										DZFDouble pricess = SafeCompute.div(VoUtils.getDZFDouble(bvo.getJfmny()),
//												VoUtils.getDZFDouble(bvo.getNnumber()));
//										DZFDouble dd = SafeCompute.sub(temp, bqfcnum);
//										DZFDouble ddd = SafeCompute.sub(bvo.getNnumber(), dd);
//										df = SafeCompute.add(df, SafeCompute.multiply(pricess, ddd));
//										break;
//									}
//								}
//							}
//						}
//					} else {// 直接从期初取数
//						// QcYeVO qcvo = qcmap.get(cvo.getKmid() +
//						// cvo.getFzid());
//						// if (qcvo != null) {
//						DZFDouble qcnum1 = cvo.getQcnum();
//						DZFDouble qcmny1 = cvo.getQcmny();
//						DZFDouble prices = SafeCompute.div(VoUtils.getDZFDouble(qcmny1), qcnum1);
//						df = SafeCompute.add(df, SafeCompute.multiply(prices, bqfcnum));
//						// }
//					}
//				}
//				if (bqfcnum.doubleValue() >= qcnum.doubleValue() && bqfcnum.doubleValue() <= total.doubleValue()) {// 期初加当月凭证数
//					// DZFDouble qcmny = VoUtils.getDZFDouble(cvo.getQcmny());
//					DZFDouble qcmny = getqcmny(cvo, mapperiod, qcmap);
//					df = SafeCompute.add(df, qcmny);
//					DZFDouble num = SafeCompute.sub(bqfcnum, qcnum);
//					List<TzpzBVO> bvolist = maptm.get(cvo.getKmid() + cvo.getFzid());
//					if (bvolist != null && bvolist.size() > 0) {
//						DZFDouble temp = DZFDouble.ZERO_DBL;
//						DZFDouble tempnum = DZFDouble.ZERO_DBL;
//						DZFDouble ltzeronum = DZFDouble.ZERO_DBL;
//						int count = 0;
//						List<TzpzBVO> templist = new ArrayList<TzpzBVO>();
//						for (int i = 0; i < bvolist.size(); i++) {
//							TzpzBVO bvo = bvolist.get(i);
//							if (bvo.getJfmny() != null && bvo.getJfmny().doubleValue() < 0) {
//								ltzeronum = SafeCompute.add(ltzeronum, bvo.getNnumber());// 小于0的数量
//							}
//						}
//						for (int i = 0; i < bvolist.size(); i++) {
//							TzpzBVO bvo = bvolist.get(i);
//							if (bvo.getJfmny() != null && bvo.getJfmny().doubleValue() > 0) {
//
//								tempnum = SafeCompute.add(tempnum, VoUtils.getDZFDouble(bvo.getNnumber()));
//								if (tempnum.doubleValue() > ltzeronum.doubleValue()) {
//									if (count == 0) {
//										bvo.setNnumber(SafeCompute.sub(tempnum, ltzeronum));
//										bvo.setJfmny(SafeCompute.multiply(SafeCompute.sub(tempnum, ltzeronum),
//												VoUtils.getDZFDouble(bvo.getNprice())));
//									}
//									templist.add(bvo);
//									count++;
//								}
//							}
//
//						}
//						for (int i = 0; i < templist.size(); i++) {
//							TzpzBVO bvo = templist.get(i);
//							if (bvo.getJfmny() != null && bvo.getJfmny().doubleValue() != 0) {
//								temp = SafeCompute.add(temp, VoUtils.getDZFDouble(bvo.getNnumber()));
//								if (temp.doubleValue() < num.doubleValue()) {
//									// df = SafeCompute.add(df,
//									// SafeCompute.multiply(bvo.getJfmny(),
//									// bvo.getNnumber()));
//									df = SafeCompute.add(df, bvo.getJfmny());
//								} else {
//									DZFDouble pricess = SafeCompute.div(VoUtils.getDZFDouble(bvo.getJfmny()),
//											VoUtils.getDZFDouble(bvo.getNnumber()));
//									DZFDouble dd = SafeCompute.sub(temp, num);
//									DZFDouble ddd = SafeCompute.sub(bvo.getNnumber(), dd);
//									df = SafeCompute.add(df, SafeCompute.multiply(pricess, ddd));
//									break;
//								}
//							}
//						}
//					}
//				}
//				if (bqfcnum.doubleValue() > total.doubleValue()) {// 暂估
//					DZFDouble qcmny = getqcmny(cvo, mapperiod, qcmap);
//					df = SafeCompute.add(df, qcmny);
//					List<TzpzBVO> bvolist = maptm.get(cvo.getKmid() + cvo.getFzid());
//					DZFDouble bqsrmny = getbqsrmny(bvolist, cvo);
//					df = SafeCompute.add(df, bqsrmny);
//					DZFDouble temp = SafeCompute.sub(bqfcnum, total);
//					if (qmclvo.getZgdata() != null && qmclvo.getZgdata().length > 0) {
//						TempInvtoryVO[] zgdata = qmclvo.getZgdata();
//						for (TempInvtoryVO zgvo : zgdata) {
//							boolean flag = StringUtil.isEmpty(cvo.getFzid()) ? cvo.getKmid().equals(zgvo.getKmid())
//									: cvo.getKmid().equals(zgvo.getKmid()) && cvo.getFzid().equals(zgvo.getFzid());
//							if (flag) {
//								DZFDouble zgprice = SafeCompute.div(zgvo.getNmny(), zgvo.getNnumber());
//								df = SafeCompute.add(df, SafeCompute.multiply(zgprice, temp));
//							}
//						}
//					}
//				}
//				price = SafeCompute.div(df, bqfcnum);
//			} else {
			df = SafeCompute.multiply(VoUtils.getDZFDouble(cvo.getBqprice()),
					VoUtils.getDZFDouble(cvo.getBqfcnum()));
			price = cvo.getBqprice();
			if (qmclvo.getZgdata() != null && qmclvo.getZgdata().length > 0) {
				TempInvtoryVO[] zgdata = qmclvo.getZgdata();
				for (TempInvtoryVO zgvo : zgdata) {
					boolean flag = StringUtil.isEmpty(cvo.getFzid()) ? cvo.getKmid().equals(zgvo.getKmid())
							: cvo.getKmid().equals(zgvo.getKmid()) && cvo.getFzid().equals(zgvo.getFzid());

					if (flag) {
						DZFDouble totalmny = SafeCompute.add(VoUtils.getDZFDouble(cvo.getQcmny()),
								VoUtils.getDZFDouble(cvo.getBqsrmny()));
						totalmny = SafeCompute.add(totalmny, VoUtils.getDZFDouble(zgvo.getNmny()));
						DZFDouble totalnum = SafeCompute.add(VoUtils.getDZFDouble(cvo.getQcnum()),
								VoUtils.getDZFDouble(cvo.getBqsrnum()));
						DZFDouble zgnum = DZFDouble.ZERO_DBL;
						if(zgvo.getNnumber_old().compareTo(zgvo.getNnumber())>=0){
							// 按照数量全部暂估   金额部分暂估 重新计算单价
							zgnum = zgvo.getNnumber_old();
						}else{
							zgnum = zgvo.getNnumber();
						}
						totalnum = SafeCompute.add(totalnum, VoUtils.getDZFDouble(zgnum));
						price = SafeCompute.div(VoUtils.getDZFDouble(totalmny), VoUtils.getDZFDouble(totalnum));
						df = SafeCompute.multiply(VoUtils.getDZFDouble(price),
								VoUtils.getDZFDouble(cvo.getBqfcnum()));
						// 将暂估数量放到本期发生数量里面去
						cvo.setBqsrnum(SafeCompute.add(cvo.getBqsrnum(),zgnum));
					}

				}
			}
//			}
			boolean flag = false;
			if (df.equals(DZFDouble.ZERO_DBL)) {
				// 这个地方，发出数量0，，购入数量为 负的 temp > 0
				DZFDouble temp = SafeCompute.sub(VoUtils.getDZFDouble(cvo.getBqfcnum()),
						SafeCompute.add(VoUtils.getDZFDouble(cvo.getQcnum()), VoUtils.getDZFDouble(cvo.getBqsrnum())));
				// temp.doubleValue()>0&&!flag
				// cvo.getBqfcnum() != null && cvo.getBqfcnum().doubleValue()>0
				if (temp.doubleValue() > 0 && !flag) {
					if (qmclvo.getZgdata() == null || qmclvo.getZgdata().length == 0) {
						// TempInvtoryVO zgvo = new TempInvtoryVO();
						// zgvo.setKmid(cvo.getKmid());
						// zgvo.setKmbm(cvo.getKmbm());
						// zgvo.setKmname(cvo.getKmmc());
						// zgvo.setNnumber_old(cvo.getBqfcnum());
						// zgvo.setNnumber(cvo.getBqfcnum());
						// zglist.add(zgvo);
						TempInvtoryVO zgvo =createZgVO(cvo, temp, pricejingdu);
						zglist.add(zgvo);
					}
				}
				// if(qmclvo.getZgdata() != null && qmclvo.getZgdata().length >
				// 0){
				// TempInvtoryVO [] zgdata = qmclvo.getZgdata();
				// for(TempInvtoryVO zgvo : zgdata){
				// if(cvo.getKmid().equals(zgvo.getKmid())){
				// df = zgvo.getNmny();
				// price = SafeCompute.div(VoUtils.getDZFDouble(df),
				// VoUtils.getDZFDouble(zgvo.getNnumber()));
				// dfbodyVO.setNnumber(zgvo.getNnumber());
				// flag = true;
				// }
				// }
				// }
				if (!flag) {
					// DZFDouble temp =
					// SafeCompute.sub(VoUtils.getDZFDouble(cvo.getBqfcnum()),
					// SafeCompute.add(VoUtils.getDZFDouble(cvo.getQcnum()),
					// VoUtils.getDZFDouble(cvo.getBqsrnum())));
					// if(temp.doubleValue()>0){
					// TempInvtoryVO zgvo = new TempInvtoryVO();
					// zgvo.setKmid(cvo.getKmid());
					// zgvo.setKmbm(cvo.getKmbm());
					// zgvo.setKmname(cvo.getKmmc());
					// zgvo.setNnumber_old(temp);
					// zgvo.setNnumber(temp);
					// zglist.add(zgvo);
					// }
					continue;
				}
			}
			DZFDouble temp = SafeCompute.sub(VoUtils.getDZFDouble(cvo.getBqfcnum()),
					SafeCompute.add(VoUtils.getDZFDouble(cvo.getQcnum()), VoUtils.getDZFDouble(cvo.getBqsrnum())));
			if (!datafer.isZgdataisave() && temp.doubleValue() > 0 && !flag) {
				TempInvtoryVO zgvo =createZgVO(cvo, temp, pricejingdu);
				zglist.add(zgvo);
			}
			TzpzBVO dfbodyVO = new TzpzBVO();
			dfbodyVO.setPk_accsubj(cvo.getKmid());// 贷方科目
			dfbodyVO.setVcode(cvo.getKmbm());
			dfbodyVO.setVname(cvo.getKmmc());
			dfbodyVO.setNnumber(cvo.getBqfcnum());
			dfbodyVO.setFzhsx6(cvo.getFzid()); // 存货辅助
			df = df.setScale(2, DZFDouble.ROUND_HALF_UP);
			dfbodyVO.setDfmny(df);
			dfbodyVO.setYbdfmny(df);
			dfbodyVO.setZy(zy);// 摘要
			// dfbodyVO.setPk_inventory(cvo.getPk_inventory());
			// dfbodyVO.setNnumber(cvo.getBqfcnum());
			if(price != null){
				dfbodyVO.setNprice(price.setScale(pricejingdu, DZFDouble.ROUND_HALF_UP));
			}

			// 币种，默认人民币
			dfbodyVO.setPk_currency(DzfUtil.PK_CNY);
			dfbodyVO.setNrate(new DZFDouble(1));
			dfbodyVO.setPk_corp(corpVo.getPk_corp());
			if (listdfkm.contains(dfbodyVO.getVcode())) {
				// 有出库才进行结转 不出库不结转
				if (cvo.getBqfcnum() != null && cvo.getBqfcnum().doubleValue() != 0) {
					listTzpzBVO.add(dfbodyVO);
					ye = SafeCompute.add(ye, df);
				}
			}
		}
		transfervo.setYe(ye);
		return zglist;
	}


	private TempInvtoryVO createZgVO(QMJzsmNoICVO cvo,DZFDouble temp,int  pricejingdu) {
		TempInvtoryVO zgvo = new TempInvtoryVO();
		zgvo.setKmid(cvo.getKmid());
		zgvo.setKmbm(cvo.getKmbm());
		zgvo.setKmname(cvo.getKmmc());
		zgvo.setNnumber_old(temp);
		zgvo.setNnumber(temp);
		DZFDouble price1 = setPrice(cvo, pricejingdu);
		zgvo.setNprice(price1);// 暂估单价
		DZFDouble mny = SafeCompute.multiply(temp, price1);
		mny = mny.setScale(2, DZFDouble.ROUND_HALF_UP);
		zgvo.setNmny(mny);// 暂估金额
		zgvo.setFzid(cvo.getFzid());
		return zgvo;
	}

	/**
	 * 连续红冲
	 * @param cvo
	 * @return
	 */
	//增加连续暂估的情况，然后修改了暂估单价的情况，需要回冲。zpm 2019.9.11，当月1号的回冲上月暂估数据。
	private boolean isGoonHongChong(QMJzsmNoICVO cvo,TempDataTransFer datafer){
		if(datafer.isZgdataisave() && cvo.getBqsrmny() != null && cvo.getBqsrmny().doubleValue() < 0
				&& cvo.getBqsrnum() != null && cvo.getBqsrnum().doubleValue() < 0
				&& (cvo.getBqfcnum() == null || cvo.getBqfcnum().doubleValue() == 0)){
			return true;
		}
		return false;
	}

	private boolean isNoNumhasMny(QMJzsmNoICVO cvo){
		if (cvo.getBqsrmny() != null && cvo.getBqsrmny().doubleValue() != 0
				&& (cvo.getBqfcnum() == null || cvo.getBqfcnum().doubleValue() == 0)
				&& (cvo.getBqsrnum() == null || cvo.getBqsrnum().doubleValue() == 0)) {
			return true;
		}
		return false;
	}
	/**
	 * 计算有金额的，无数量的。[单到冲回]
	 *
	 * @param cvo
	 * @return
	 */
	private TzpzBVO calcNonumhasMny(QMJzsmNoICVO cvo, String zy, String pk_corp,TempDataTransFer datafer) {
		if (cvo == null)
			return null;
		if(isGoonHongChong(cvo,datafer)){
			List<TempInvtoryVO> zglist = datafer.getZglist();
			if(zglist == null || zglist.size() == 0)
				return null;
			TempInvtoryVO lastempvo = null;
			for(TempInvtoryVO tempvo : zglist){
				if(cvo.getKmid()!=null && cvo.getKmid().equals(tempvo.getKmid())
						&& ((!StringUtil.isEmpty(cvo.getFzid())  && cvo.getFzid().equals(tempvo.getFzid()))
						|| (StringUtil.isEmpty(cvo.getFzid()) && StringUtil.isEmpty(tempvo.getFzid())))){
					lastempvo = tempvo;
					break;
				}
			}
			if(lastempvo == null)
				return null;
			String kmid = cvo.getKmid();
			YntCpaccountVO kmvo = (YntCpaccountVO)singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, kmid);
			if(kmvo != null && kmvo.getIsnum() != null && kmvo.getIsnum().booleanValue()){//必须增加启用数量
				TzpzBVO dfbodyVO = new TzpzBVO();
				dfbodyVO.setPk_accsubj(cvo.getKmid());// 贷方科目
				dfbodyVO.setVcode(cvo.getKmbm());
				dfbodyVO.setVname(cvo.getKmmc());
				dfbodyVO.setNnumber(cvo.getBqfcnum());
				dfbodyVO.setFzhsx6(cvo.getFzid()); // 存货辅助
				//DZFDouble df = cvo.getBqsrmny().setScale(2, DZFDouble.ROUND_HALF_UP);
				DZFDouble df = SafeCompute.add(lastempvo.getNmny(), cvo.getBqsrmny()).setScale(2, DZFDouble.ROUND_HALF_UP);
				dfbodyVO.setDfmny(df);
				dfbodyVO.setYbdfmny(df);
				dfbodyVO.setZy(zy);// 摘要
				// dfbodyVO.setPk_inventory(cvo.getPk_inventory());
				// dfbodyVO.setNnumber(cvo.getBqfcnum());
				dfbodyVO.setNprice(DZFDouble.ZERO_DBL);
				// 币种，默认人民币
				dfbodyVO.setPk_currency(DzfUtil.PK_CNY);
				dfbodyVO.setNrate(new DZFDouble(1));
				dfbodyVO.setPk_corp(pk_corp);
				return dfbodyVO;
			}
		}else if (isNoNumhasMny(cvo)) {
			String kmid = cvo.getKmid();
			YntCpaccountVO kmvo = (YntCpaccountVO)singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, kmid);
			if(kmvo != null && kmvo.getIsnum() != null && kmvo.getIsnum().booleanValue()){//必须增加启用数量
				TzpzBVO dfbodyVO = new TzpzBVO();
				dfbodyVO.setPk_accsubj(cvo.getKmid());// 贷方科目
				dfbodyVO.setVcode(cvo.getKmbm());
				dfbodyVO.setVname(cvo.getKmmc());
				dfbodyVO.setNnumber(cvo.getBqfcnum());
				dfbodyVO.setFzhsx6(cvo.getFzid()); // 存货辅助
				DZFDouble df = cvo.getBqsrmny().setScale(2, DZFDouble.ROUND_HALF_UP);
				dfbodyVO.setDfmny(df);
				dfbodyVO.setYbdfmny(df);
				dfbodyVO.setZy(zy);// 摘要
				// dfbodyVO.setPk_inventory(cvo.getPk_inventory());
				// dfbodyVO.setNnumber(cvo.getBqfcnum());
				dfbodyVO.setNprice(DZFDouble.ZERO_DBL);
				// 币种，默认人民币
				dfbodyVO.setPk_currency(DzfUtil.PK_CNY);
				dfbodyVO.setNrate(new DZFDouble(1));
				dfbodyVO.setPk_corp(pk_corp);
				return dfbodyVO;
			}
		}
		return null;
	}

	/**
	 * 查询上月成本结转的数据，不能通过
	 */
	private DZFDouble queryPrePeriodJzData(CorpVO corpvo, String preperiod, String pk_corp, String pk_inventory,
										   String pk_accsubj) {
		DZFDouble preprice = null;
		if (StringUtil.isEmptyWithTrim(preperiod) || StringUtil.isEmptyWithTrim(pk_corp)
				|| StringUtil.isEmptyWithTrim(pk_inventory) || StringUtil.isEmptyWithTrim(pk_accsubj))
			return preprice;
		StringBuffer sbf = new StringBuffer();
		sbf.append(" select distinct h.pk_tzpz_h,h.period from ynt_tzpz_h h  ");
		sbf.append(" join ynt_tzpz_b b on h.pk_tzpz_h = b.pk_tzpz_h  ");
		sbf.append(" where b.fzhsx6 = ? and h.pk_corp = ? and h.period < ? and b.pk_accsubj = ?  ");
		sbf.append(" and nvl(h.dr,0) = 0 and nvl(b.dr,0) = 0 and b.nprice > 0 ");
		sbf.append(" order by period desc ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_inventory);
		sp.addParam(pk_corp);
		sp.addParam(preperiod);
		sp.addParam(pk_accsubj);
		List<TzpzHVO> listhvo = (List<TzpzHVO>) singleObjectBO.executeQuery(sbf.toString(), sp,
				new BeanListProcessor(TzpzHVO.class));
		if (listhvo == null || listhvo.size() == 0)
			return preprice;
		sbf.setLength(0);
		sbf.append(" select * from ynt_tzpz_b bb where bb.pk_tzpz_h in ");
		sbf.append(" (select distinct h.pk_tzpz_h from ynt_tzpz_h h ");
		sbf.append(" join ynt_tzpz_b b on h.pk_tzpz_h = b.pk_tzpz_h ");
		sbf.append(" where b.fzhsx6 = ? and h.pk_corp = ? and h.period < ? and b.pk_accsubj = ?  ");
		sbf.append(" and nvl(h.dr,0) = 0 and nvl(b.dr,0) = 0 and b.nprice > 0 ) ");
		List<TzpzBVO> list = (List<TzpzBVO>) singleObjectBO.executeQuery(sbf.toString(), sp,
				new BeanListProcessor(TzpzBVO.class));
		if (list == null || list.size() == 0)
			return preprice;
		Map<String, List<TzpzBVO>> map = DZfcommonTools.hashlizeObject(list, new String[] { "pk_tzpz_h" });
		for (TzpzHVO hvo : listhvo) {
			List<TzpzBVO> blist = map.get(hvo.getPk_tzpz_h());
			if (blist != null && blist.size() > 0) {
				hvo.setChildren(blist.toArray(new TzpzBVO[0]));
			}
		}
		for (TzpzHVO hvo : listhvo) {
			TzpzBVO[] bodyvos = (TzpzBVO[]) hvo.getChildren();
			if (bodyvos != null && bodyvos.length > 0 && Kmschema.ischengbenpz(corpvo, bodyvos)) {
				List<TzpzBVO> zlist = new ArrayList<TzpzBVO>(Arrays.asList(bodyvos));
				preprice = getChengbenJzPrice(zlist, pk_inventory, pk_accsubj);
				if (preprice != null)
					break;
			}
		}
		// 还是没有，就从辅助期初里面找
		if (preprice == null) {
			sbf.setLength(0);
			sbf.append(" select * from ynt_fzhsqc qc where qc.pk_corp = ?  ");
			sbf.append(" and qc.pk_accsubj = ? and qc.fzhsx6 = ? and nvl(qc.dr,0) = 0  ");
			sp.clearParams();
			sp.addParam(pk_corp);
			sp.addParam(pk_accsubj);
			sp.addParam(pk_inventory);
			List<FzhsqcVO> qclist = (List<FzhsqcVO>) singleObjectBO.executeQuery(sbf.toString(), sp,
					new BeanListProcessor(FzhsqcVO.class));
			if (qclist != null && qclist.size() > 0) {
				FzhsqcVO fzhsvo = qclist.get(0);
				DZFDouble monthqmnum = fzhsvo.getMonthqmnum();
				DZFDouble thismonthqc = fzhsvo.getThismonthqc();
				if (monthqmnum != null && monthqmnum.doubleValue() != 0 && thismonthqc != null
						&& thismonthqc.doubleValue() != 0) {
					preprice = SafeCompute.div(thismonthqc, monthqmnum);
					if (preprice != null && preprice.doubleValue() > 0) {
						// donothing，本来这里要处理精度的，后面已经处理了精度，这里就不做任何事情。
					} else {
						preprice = null;
					}
				}
			}
		}
		return preprice;
	}

	private DZFDouble getChengbenJzPrice(List<TzpzBVO> zlist, String pk_inventory, String pk_accsubj) {
		DZFDouble preprice = null;
		for (TzpzBVO bvo : zlist) {
			if (pk_inventory.equals(bvo.getFzhsx6()) && pk_accsubj.equals(bvo.getPk_accsubj())
					&& bvo.getNprice() != null && bvo.getNprice().doubleValue() > 0 && bvo.getDfmny() != null
					&& bvo.getDfmny().doubleValue() != 0) {
				preprice = bvo.getNprice();
				break;
			}
		}
		return preprice;
	}

	/**
	 * 计算红冲的，冲回的
	 *
	 * @param cvo
	 * @return
	 */
	private TzpzBVO calcRedNumMny(CorpVO corpvo, QMJzsmNoICVO cvo, String zy, String pk_corp, QmclVO qmclvo,
								  Integer pricejingdu) {
		if (cvo == null)
			return null;
		// 取上期成本结转凭证的,如果销售退回 上上上个月的，一直往上找，直到找到。如果还是没有，就去辅助期初里面找。
		if (cvo.getBqfcnum() != null && cvo.getBqfcnum().doubleValue() < 0) {
			String nowperiod = qmclvo.getPeriod();
			DZFDouble nprice = queryPrePeriodJzData(corpvo, nowperiod, pk_corp, cvo.getFzid(), cvo.getKmid());
			if (nprice != null) {
				TzpzBVO dfbodyVO = new TzpzBVO();
				dfbodyVO.setPk_accsubj(cvo.getKmid());// 贷方科目
				dfbodyVO.setVcode(cvo.getKmbm());
				dfbodyVO.setVname(cvo.getKmmc());
				dfbodyVO.setNnumber(cvo.getBqfcnum());
				dfbodyVO.setFzhsx6(cvo.getFzid()); // 存货辅助
				nprice = nprice.setScale(pricejingdu, DZFDouble.ROUND_HALF_UP);// 精度
				dfbodyVO.setNprice(nprice);
				DZFDouble df = SafeCompute.multiply(cvo.getBqfcnum(), nprice);
				df = df.setScale(2, DZFDouble.ROUND_HALF_UP);
				dfbodyVO.setDfmny(df);
				dfbodyVO.setYbdfmny(df);
				dfbodyVO.setZy(zy);// 摘要
				// 币种，默认人民币
				dfbodyVO.setPk_currency(DzfUtil.PK_CNY);
				dfbodyVO.setNrate(new DZFDouble(1));
				dfbodyVO.setPk_corp(pk_corp);
				return dfbodyVO;
			}
		}
		return null;
	}

	// if (listdfkm.contains(dfbodyVO.getVcode())) {
	// // 有出库才进行结转 不出库不结转
	// if (cvo.getBqfcnum() != null && cvo.getBqfcnum().doubleValue()!=0) {
	// listTzpzBVO.add(dfbodyVO);
	// ye = SafeCompute.add(ye, df);
	// }
	// }

	private void saveCBJZVoucher(List<TzpzBVO> listTzpzBVO, DZFDouble ye, YntCpaccountVO jfkmvo, String zy,
								 CorpVO corpVo, QmclVO qmclvo, String userid, String cbjzCount) {
		if (listTzpzBVO == null || listTzpzBVO.size() == 0) {
			return;
		}
		//zpm 2019.1.21
		boolean isfalg = false;
		//remove掉。2019.9.18 zpm
		for(int i = listTzpzBVO.size()-1;i>=0;i--){
			if(listTzpzBVO.get(i).getDfmny() == null
					|| listTzpzBVO.get(i).getDfmny().doubleValue() == 0){
				listTzpzBVO.remove(i);
			}
		}

		if (listTzpzBVO == null || listTzpzBVO.size() == 0) {
			return;
		}

		for(TzpzBVO bvo :listTzpzBVO ){
			DZFDouble result = SafeCompute.add(bvo.getJfmny(), bvo.getDfmny());
			if(result != null && result.doubleValue() != 0 ){
				isfalg = true;
				break;
			}
		}
		if(!isfalg){//说明是空凭证。
			return;
		}
		//zpm 注掉
		//这种情况。也要生成凭证。
		//比如说
		//借 成本   0
		//贷 商品A    8个   800
		//贷 商品B   -6个  -800
		//此种情况也需要生成凭证 ，这是两个不同的存货。
		//-----------------------------------------
		//如果为同一个存货，由于合并。
		//借 成本   0
		//贷 商品A   8个    800
		//贷 商品A   -8个  -800
		//合并后
		//借 成本   0
		//贷 商品A   0个    0，这种情况无法成本分摊。建议此种情况不与处理了。

//		if (ye != null && ye.doubleValue() == 0) {// 空结
//			return;
//		}
		zy = getJzPzZy(zy, qmclvo);
		TzpzBVO jfbodyVO = new TzpzBVO();
		jfbodyVO.setPk_accsubj(jfkmvo.getPrimaryKey());// 借方科目
		jfbodyVO.setVcode(jfkmvo.getAccountcode());
		jfbodyVO.setVname(jfkmvo.getAccountname());
		jfbodyVO.setJfmny(ye);
		jfbodyVO.setYbjfmny(ye);
		jfbodyVO.setZy(zy);// 摘要
		// 币种，默认人民币
		jfbodyVO.setPk_currency(yntBoPubUtil.getCNYPk());
		jfbodyVO.setNrate(new DZFDouble(1));
		jfbodyVO.setPk_corp(corpVo.getPk_corp());
		listTzpzBVO.add(0, jfbodyVO);
		// 生成结转凭证
		TzpzHVO headVO = new TzpzHVO();
		headVO.setPk_corp(qmclvo.getPk_corp());
		headVO.setPzlb(0);// 凭证类别：记账
		headVO.setJfmny(ye);
		headVO.setDfmny(ye);
		headVO.setCoperatorid(userid);
		headVO.setIshasjz(DZFBoolean.FALSE);
		// DZFDate nowDate = DZFDate.getDate(new
		// Long(InvocationInfoProxy.getInstance().getDate())) ;
		// headVO.setDoperatedate(nowDate) ;
		DZFDate nowDatevalue = getPeroidDZFDate(qmclvo);
		headVO.setDoperatedate(nowDatevalue);
		headVO.setPzh(yntBoPubUtil.getNewVoucherNo(qmclvo.getPk_corp(), nowDatevalue));
		headVO.setVbillstatus(8);// 默认自由态
		// 记录单据来源
		headVO.setSourcebillid(qmclvo.getPk_qmcl());
		headVO.setCbjzCount(cbjzCount);
		headVO.setSourcebilltype(IBillTypeCode.HP34);
		headVO.setPeriod(qmclvo.getPeriod());
		headVO.setVyear(Integer.valueOf(qmclvo.getPeriod().substring(0, 4)));
		headVO.setIsfpxjxm(new DZFBoolean("N"));
		headVO.setNbills(0);
		headVO.setChildren(listTzpzBVO.toArray(new TzpzBVO[0]));
		//
		voucher.saveVoucher(corpVo, headVO);
		// 成本分摊结果,在凭证保存动作里面进行分摊。这里取消。删除.zpm// 2018 10 12
		// gl_smcbftserv.saveCBFt(headVO, corpVo);
	}

	private DZFDouble setPrice(QMJzsmNoICVO cvo, int pricejingdu) {
		DZFDouble price = null;
		if (cvo == null)
			return price;
		if (cvo.getQcmny() != null && cvo.getQcnum() != null && cvo.getQcnum().doubleValue() > 0&& cvo.getQcmny().doubleValue() > 0) {
			return SafeCompute.div(cvo.getQcmny(), cvo.getQcnum()).setScale(pricejingdu, DZFDouble.ROUND_HALF_UP);// 取上期平均单价
		} else if (cvo.getZgcgmny() != null && cvo.getZgcgmny().doubleValue() != 0 && cvo.getZgcgnum() != null && cvo.getZgcgnum().doubleValue() != 0) {
			return SafeCompute.div(cvo.getZgcgmny(), cvo.getZgcgnum()).setScale(pricejingdu, DZFDouble.ROUND_HALF_UP);// 取本期平均单价
		} else if(cvo.getBqprice() != null && cvo.getBqprice().doubleValue() > 0 ){//直接取本期平均单价。zpm 2019.9.19
			return cvo.getBqprice();
		} else if (cvo.getZgxsmny() != null && cvo.getZgxsmny().doubleValue() != 0 && cvo.getZgxsnum() != null && cvo.getZgxsnum().doubleValue() != 0) {
			return SafeCompute.div(cvo.getZgxsmny(), cvo.getZgxsnum()).setScale(pricejingdu, DZFDouble.ROUND_HALF_UP);// 取销售单价
		}
		return null;
	}

	private DZFDouble getbqsrmny(List<TzpzBVO> list, QMJzsmNoICVO cvo) {
		if (list == null || list.size() == 0) {
			return DZFDouble.ZERO_DBL;
		}
		DZFDouble ltzeronum = DZFDouble.ZERO_DBL;
		DZFDouble ltzeromny = DZFDouble.ZERO_DBL;
		DZFDouble tempnum = DZFDouble.ZERO_DBL;
		DZFDouble tempmny = DZFDouble.ZERO_DBL;
		DZFDouble qc = DZFDouble.ZERO_DBL;
		int count = 0;
		for (TzpzBVO bvo : list) {
			boolean flag = StringUtil.isEmpty(bvo.getFzhsx6()) ? bvo.getPk_accsubj().equals(cvo.getKmid())
					: bvo.getPk_accsubj().equals(cvo.getKmid()) && bvo.getFzhsx6().equals(cvo.getFzid());

			if (flag) {
				if (bvo.getJfmny() != null && bvo.getJfmny().doubleValue() < 0) {
					ltzeronum = SafeCompute.add(ltzeronum, bvo.getNnumber());// 小于0的数量
					ltzeromny = SafeCompute.add(ltzeromny, bvo.getJfmny());// 小于0的金额
				}
			}
		}

		for (TzpzBVO bvo : list) {
			boolean flag = StringUtil.isEmpty(bvo.getFzhsx6()) ? bvo.getPk_accsubj().equals(cvo.getKmid())
					: bvo.getPk_accsubj().equals(cvo.getKmid()) && bvo.getFzhsx6().equals(cvo.getFzid());
			if (flag) {
				if (bvo.getJfmny() != null && bvo.getJfmny().doubleValue() > 0) {
					tempnum = SafeCompute.add(tempnum, VoUtils.getDZFDouble(bvo.getNnumber()));
					tempmny = SafeCompute.add(tempmny, VoUtils.getDZFDouble(bvo.getJfmny()));
					// 计算本期入的数量 金额 需要考虑下 先进 先出 冲抵 一次冲抵
					// 暂估数量 -6个 采购 2个 采购 3 个 采购 5个 累计采购 超过6个时 按照档笔的采购价格 算期初
					if (tempnum.doubleValue() > ltzeronum.abs().doubleValue()) {
						if (count == 0) {
							DZFDouble utd = SafeCompute.sub(tempnum, ltzeronum.abs());
							DZFDouble d1 = SafeCompute.multiply(utd, VoUtils.getDZFDouble(bvo.getNprice()));// 计算剩余采购金额
							DZFDouble cxje = SafeCompute.sub(tempmny, d1);// 计算差价
							DZFDouble cj = SafeCompute.add(cxje, ltzeromny);
							qc = SafeCompute.add(qc, cj);
							break;
						}
						count++;
					}
				}
			}
		}

		return SafeCompute.sub(cvo.getBqsrmny(), qc);

	}

	private DZFDouble getqcmny(QMJzsmNoICVO cvo, Map<String, List<TzpzBVO>> map, Map<String, QcYeVO> qcmap) {
		// TODO Auto-generated method stub
		if (cvo.getQcnum() == null || cvo.getQcnum().doubleValue() == 0) {
			return DZFDouble.ZERO_DBL;
		}
		Iterator iter = map.entrySet().iterator();
		DZFDouble qc = DZFDouble.ZERO_DBL;
		// DZFDouble qcnum = cvo.getQcnum();

		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			// Object key = entry.getKey();
			List<TzpzBVO> list = (List<TzpzBVO>) entry.getValue();
			DZFDouble ltzeronum = DZFDouble.ZERO_DBL;
			DZFDouble ltzeromny = DZFDouble.ZERO_DBL;
			DZFDouble tempnum = DZFDouble.ZERO_DBL;
			DZFDouble tempmny = DZFDouble.ZERO_DBL;
			int count = 0;
			for (TzpzBVO bvo : list) {
				boolean flag = StringUtil.isEmpty(bvo.getFzhsx6()) ? bvo.getPk_accsubj().equals(cvo.getKmid())
						: bvo.getPk_accsubj().equals(cvo.getKmid()) && bvo.getFzhsx6().equals(cvo.getFzid());
				if (flag) {
					if (bvo.getJfmny() != null && bvo.getJfmny().doubleValue() < 0) {
						ltzeronum = SafeCompute.add(ltzeronum, bvo.getNnumber());// 小于0的数量
						ltzeromny = SafeCompute.add(ltzeromny, bvo.getJfmny());// 小于0的金额
					}
				}
			}
			for (TzpzBVO bvo : list) {
				boolean flag = StringUtil.isEmpty(bvo.getFzhsx6()) ? bvo.getPk_accsubj().equals(cvo.getKmid())
						: bvo.getPk_accsubj().equals(cvo.getKmid()) && bvo.getFzhsx6().equals(cvo.getFzid());
				if (flag) {
					if (bvo.getJfmny() != null && bvo.getJfmny().doubleValue() > 0) {
						tempnum = SafeCompute.add(tempnum, VoUtils.getDZFDouble(bvo.getNnumber()));
						tempmny = SafeCompute.add(tempmny, VoUtils.getDZFDouble(bvo.getJfmny()));
						if (tempnum.doubleValue() > ltzeronum.doubleValue()) {
							if (count == 0) {
								DZFDouble utd = SafeCompute.sub(tempnum, ltzeronum);
								DZFDouble d1 = SafeCompute.multiply(utd, VoUtils.getDZFDouble(bvo.getNprice()));
								DZFDouble cxje = SafeCompute.sub(tempmny, d1);
								DZFDouble cj = SafeCompute.add(cxje, ltzeromny);
								qc = SafeCompute.add(qc, cj);
								break;
							}
							count++;
						}
					}
				}
			}
			// for (TzpzBVO bvo : list) {
			// if(bvo.getPk_accsubj().equals(cvo.getKmid())){
			// if(bvo.getJfmny()!=null && bvo.getJfmny().doubleValue()>0){
			// tempmny = SafeCompute.add(tempmny,
			// VoUtils.getDZFDouble(bvo.getJfmny()));
			// }
			// }
			// }

		}
		return SafeCompute.sub(cvo.getQcmny(), qc);
	}

	/**
	 * 不启用库存，完工入库，凭证保存
	 *
	 *
	 */
	public void saveWgrkVouchernoic(CpcosttransVO mbvo, QmclVO qmclvo, CorpVO corpVo, List<CostForwardInfo> list1,
									List<YntCpaccountVO> dfkmvos, String cbjzCount, String clcode, String rgcode, String zzfycode,
									String userid) {
		// int a = 1;
		if (list1 == null) {
			return;
		}
		if (list1.size() == 1) {
			if (list1.get(0).getKmid() == null || "".equals(list1.get(0).getKmid())) {
				return;
			}
		}
		List<TzpzBVO> listTzpzBVO = new ArrayList<TzpzBVO>();
		List<TzpzBVO> dflistTzpzBVO = new ArrayList<TzpzBVO>();
		String zy = mbvo.getAbstracts();
		zy = getJzPzZy(zy, qmclvo);

		// Map<String, AuxiliaryAccountBVO> map =
		// getAuxiliaryAccount(corpVo.getPk_corp());
		//
		// int s = (list1.size()-1)*2;

		Map<String, YntCpaccountVO> accmap =accountService.queryMapByPk(corpVo.getPk_corp());

		List<String>  list = new ArrayList<>();
		DZFDouble ye = DZFDouble.ZERO_DBL;
		for (CostForwardInfo info : list1) {
			if (info.getKmid() == null || info.getKmbm().equals("")) {
				TzpzBVO dfbodyVO = null;
				for (YntCpaccountVO kmvo : dfkmvos) {

					dfbodyVO = new TzpzBVO();
					dfbodyVO.setPk_accsubj(kmvo.getPrimaryKey());// 借方科目
					dfbodyVO.setVcode(kmvo.getAccountcode());
					dfbodyVO.setVname(kmvo.getAccountname());

					if(!list.contains(clcode)){
						if (kmvo.getAccountcode().startsWith(clcode)) {
							dfbodyVO.setDfmny(info.getNcailiao_wg());
							dfbodyVO.setYbdfmny(info.getNcailiao_wg());
							list.add(clcode);
						}
					}
					if(!list.contains(rgcode)){
						if (kmvo.getAccountcode().startsWith(rgcode)) {
							dfbodyVO.setDfmny(info.getNrengong_wg());
							dfbodyVO.setYbdfmny(info.getNrengong_wg());
							list.add(rgcode);
						}
					}
					if(!list.contains(zzfycode)){
						if (kmvo.getAccountcode().startsWith(zzfycode)) {
							dfbodyVO.setDfmny(info.getNzhizao_wg());
							dfbodyVO.setYbdfmny(info.getNzhizao_wg());
							list.add(zzfycode);
						}
					}
					dfbodyVO.setZy(zy);// 摘要
					// 币种，默认人民币
					dfbodyVO.setPk_currency(DzfUtil.PK_CNY);
					dfbodyVO.setNrate(new DZFDouble(1));
					dfbodyVO.setPk_corp(corpVo.getPk_corp());
					if (dfbodyVO.getDfmny() != null && dfbodyVO.getDfmny().doubleValue() != 0) {
						dflistTzpzBVO.add(dfbodyVO);
					}

				}
			} else {

				TzpzBVO jfbodyVO = new TzpzBVO();
				jfbodyVO.setPk_accsubj(info.getKmid());// 借方科目
				jfbodyVO.setVcode(info.getKmbm());
				jfbodyVO.setVname(info.getKmmc());

				YntCpaccountVO account = accmap.get(info.getKmid());
				if(account != null){
					if (account.getIsfzhs().charAt(5) == '1') {
						jfbodyVO.setFzhsx6(info.getFzid());
					}
				}

				jfbodyVO.setJfmny(SafeCompute.add(info.getNcailiao_wg(),
						SafeCompute.add(info.getNrengong_wg(), info.getNzhizao_wg())));
				if (jfbodyVO.getJfmny() == null || jfbodyVO.getJfmny().doubleValue() == 0) {
					continue;
				}
				ye = SafeCompute.add(ye, jfbodyVO.getJfmny());
				jfbodyVO.setYbjfmny(SafeCompute.add(info.getNcailiao_wg(),
						SafeCompute.add(info.getNrengong_wg(), info.getNzhizao_wg())));
				jfbodyVO.setNnumber(info.getNnum_wg());
				jfbodyVO.setNprice(SafeCompute.div(jfbodyVO.getJfmny(), info.getNnum_wg()));
				jfbodyVO.setZy(zy);// 摘要
				// 币种，默认人民币
				jfbodyVO.setPk_currency(yntBoPubUtil.getCNYPk());
				jfbodyVO.setNrate(new DZFDouble(1));
				jfbodyVO.setPk_corp(corpVo.getPk_corp());
				listTzpzBVO.add(jfbodyVO);
			}
		}
		// 生成结转凭证
		TzpzHVO headVO = new TzpzHVO();
		headVO.setPk_corp(qmclvo.getPk_corp());
		headVO.setPzlb(0);// 凭证类别：记账
		headVO.setJfmny(ye);
		headVO.setDfmny(ye);
		headVO.setCoperatorid(userid);
		headVO.setIshasjz(DZFBoolean.FALSE);
		// DZFDate nowDate = DZFDate.getDate(new
		// Long(InvocationInfoProxy.getInstance().getDate())) ;
		// headVO.setDoperatedate(nowDate) ;
		DZFDate nowDatevalue = getPeroidDZFDate(qmclvo);
		headVO.setDoperatedate(nowDatevalue);
		headVO.setPzh(yntBoPubUtil.getNewVoucherNo(qmclvo.getPk_corp(), nowDatevalue));
		headVO.setVbillstatus(8);// 默认自由态
		// 记录单据来源
		headVO.setSourcebillid(qmclvo.getPk_qmcl());
		headVO.setCbjzCount(cbjzCount);
		headVO.setSourcebilltype(IBillTypeCode.HP34);
		headVO.setPeriod(qmclvo.getPeriod());
		headVO.setVyear(Integer.valueOf(qmclvo.getPeriod().substring(0, 4)));
		headVO.setIsfpxjxm(new DZFBoolean("N"));
		listTzpzBVO.addAll(dflistTzpzBVO);
		headVO.setChildren(listTzpzBVO.toArray(new TzpzBVO[0]));

		if (ye.doubleValue() == 0) {// 空结
			return;
		}
		voucher.saveVoucher(corpVo, headVO);
	}

	class Transfervo {
		DZFDouble ye;
		String zy;

		public DZFDouble getYe() {
			return ye;
		}

		public void setYe(DZFDouble ye) {
			this.ye = ye;
		}

		public String getZy() {
			return zy;
		}

		public void setZy(String zy) {
			this.zy = zy;
		}
	}
}
