package com.dzf.zxkj.platform.service.taxrpt.shandong.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.IDzfServiceConst;
import com.dzf.zxkj.common.constant.ITaxRptStandFeeConst;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.tree.BDTreeCreator;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.BondedSetVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.TaxPosContrastVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.tax.TaxRptTempletVO;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxConst;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxQcQueryVO;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxVOTreeStrategy;
import com.dzf.zxkj.platform.model.zncs.DZFBalanceBVO;
import com.dzf.zxkj.platform.service.fct.IFctpubService;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.sys.IVersionMngService;
import com.dzf.zxkj.platform.service.taxrpt.ITaxRptService;
import com.dzf.zxkj.platform.service.taxrpt.shandong.ITaxCategoryService;
import com.dzf.zxkj.platform.service.taxrpt.shandong.InitFiledMapParse;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;
import com.dzf.zxkj.platform.service.zncs.image.IBalanceService;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.ParseXml;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.XMLUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("taxcateserv_default")
public class DefaultTaxCategoryServiceImpl implements ITaxCategoryService {

	// private static Logger log = Logger.getLogger(WebServiceProxy.class);

	@Autowired
	private IFctpubService pubFctService;

	@Autowired
	private ITaxRptService taxRptservice_default;

	@Autowired
	private SingleObjectBO sbo;
	@Autowired
	protected IBDCorpTaxService sys_corp_tax_serv;

	// 校验excel中数据项信息
	protected void checkData(TaxPosContrastVO vo) throws DZFWarpException {

	}

	// 设置中间表默认字段的 默认值
	protected void setDefaultValue(TaxPosContrastVO vo, String nsrsbh, TaxReportVO reportvo) throws DZFWarpException {
		if ("#curdate".equals(vo.getVdefaultvalue())) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			String dateString = formatter.format(new DZFDate().toDate());
			vo.setValue(dateString);
		} else if ("#curtime".equals(vo.getVdefaultvalue())) {
			String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
			vo.setValue(dateString);
		} else if ("#nsrsbh".equals(vo.getVdefaultvalue())) {
			vo.setValue(nsrsbh);
		} else if ("#skssqq".equals(vo.getVdefaultvalue())) {

			// if (reportvo.getPeriodtype() != null &&
			// reportvo.getPeriodtype().intValue() == 0) {
			// // 月
			// String period =
			// DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
			// vo.setValue(DateUtils.getPeriodStartDate(period).toString());
			// } else if (reportvo.getPeriodtype() != null &&
			// reportvo.getPeriodtype().intValue() == 1) {
			// // 季度
			// String period = DateUtils.getPeriod(new DZFDate());
			// vo.setValue(getQuarterStartDate(period));
			// // vo.setValue("2017-10-01");
			// }
			vo.setValue(reportvo.getPeriodfrom());

		} else if ("#skssqz".equals(vo.getVdefaultvalue())) {

			// if (reportvo.getPeriodtype() != null &&
			// reportvo.getPeriodtype().intValue() == 0) {
			// // 月
			// String period =
			// DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
			// vo.setValue(DateUtils.getPeriodEndDate(period).toString());
			// } else if (reportvo.getPeriodtype() != null &&
			// reportvo.getPeriodtype().intValue() == 1) {
			// // 季度
			// String period = DateUtils.getPeriod(new DZFDate());
			// vo.setValue(getQuarterEndDate(period));
			// // vo.setValue("2017-12-31");
			// }
			vo.setValue(reportvo.getPeriodto());
		} else if ("#djxh".equals(vo.getVdefaultvalue())) {

			vo.setValue(vo.getDjxh());
		} else {
			vo.setValue(vo.getVdefaultvalue());
		}
	}

	// 季度起始日期
	protected String getQuarterStartDate(String yearmonth) {
		int iYear = Integer.parseInt(yearmonth.substring(0, 4));
		int iMonth = Integer.parseInt(yearmonth.substring(5, 7));
		switch (iMonth) {
		case 1:
		case 2:
		case 3: {
			return "" + (iYear - 1) + "-10-01";
		}
		case 4:
		case 5:
		case 6: {
			return "" + iYear + "-01-01";
		}
		case 7:
		case 8:
		case 9: {
			return "" + iYear + "-04-01";
		}
		case 10:
		case 11:
		case 12: {
			return "" + iYear + "-07-01";
		}
		}
		return null;

	}

	// 季度结束日期
	protected String getQuarterEndDate(String yearmonth) {
		int iYear = Integer.parseInt(yearmonth.substring(0, 4));
		int iMonth = Integer.parseInt(yearmonth.substring(5, 7));
		switch (iMonth) {
		case 1:
		case 2:
		case 3: {
			return "" + (iYear - 1) + "-12-31";
		}
		case 4:
		case 5:
		case 6: {
			return "" + iYear + "-03-31";
		}
		case 7:
		case 8:
		case 9: {
			return "" + iYear + "-06-30";
		}
		case 10:
		case 11:
		case 12: {
			return "" + iYear + "-09-30";
		}
		}
		return null;

	}

	// 过滤掉不需要上报业签数据
	protected TaxPosContrastVO[] filterData(TaxPosContrastVO[] vos, HashMap<String, TaxRptTempletVO> hmTemplet)
			throws DZFWarpException {
		List<TaxPosContrastVO> tlist = new ArrayList<>();//

		for (TaxPosContrastVO vo : vos) {
			if (StringUtil.isEmpty(vo.getReportcode())) {
				tlist.add(vo);
			} else {
				if (!vo.getReportcode().startsWith("N")) {
					if ("QTXX".equals(vo.getReportcode())) {
						tlist.add(vo);
					} else if (hmTemplet.containsKey(vo.getPk_taxtemplet())) {
						tlist.add(vo);
					}
				}
			}
		}
		return tlist.toArray(new TaxPosContrastVO[tlist.size()]);
	}

	// 过滤掉不需要上报业签数据（按照指定的编码）
	protected TaxPosContrastVO[] filterData(TaxPosContrastVO[] vos, String[] expCodes,
			HashMap<String, TaxRptTempletVO> hmTemplet) {

		if (expCodes == null || expCodes.length == 0) {
			return vos;
		}
		List<String> rlist = Arrays.asList(expCodes);
		List<TaxPosContrastVO> tlist = new ArrayList<>();//

		for (TaxPosContrastVO vo : vos) {
			if (StringUtil.isEmpty(vo.getReportcode())) {
				tlist.add(vo);
			} else {
				if (!rlist.contains(vo.getReportcode())) {
					if ("QTXX".equals(vo.getReportcode())) {
						tlist.add(vo);
					} else if (hmTemplet.containsKey(vo.getPk_taxtemplet())) {
						tlist.add(vo);
					}
				}
			}
		}
		return tlist.toArray(new TaxPosContrastVO[tlist.size()]);

	}

	// 处理一些特殊字段 比如 是 转换成 Y
	protected void setSpecialValue(TaxPosContrastVO vo) throws DZFWarpException {
	}

	// 获取中间的数据 并字段对动态行进行增加处理
	protected List<TaxPosContrastVO> getTaxPosVO(String sb_zlbh, HashMap<String, TaxRptTempletVO> hmTemplet)
			throws DZFWarpException {
		TaxPosContrastVO[] vos = (TaxPosContrastVO[]) sbo.queryByCondition(TaxPosContrastVO.class,
				" nvl(dr,0) = 0  and  sbzlbh = '" + sb_zlbh + "'", null);
		if (vos == null || vos.length == 0)
			throw new BusinessException("纳税申报对照信息出错");

		Collections.sort(Arrays.asList(vos), new Comparator<TaxPosContrastVO>() {
			@Override
			public int compare(TaxPosContrastVO o1, TaxPosContrastVO o2) {
				int i = o1.getRowno().compareTo(o2.getRowno());
				return i;
			}
		});

		// 过滤掉一些不需要处理业签的数据
		vos = filterData(vos, hmTemplet);

		List<TaxPosContrastVO> tlist = new ArrayList<>();// 固定行数据
		List<TaxPosContrastVO> dlist = new ArrayList<>();// 动态行数据
		List<TaxPosContrastVO> dzlist = null;// 动态行子表数据

		Map<String, List<TaxPosContrastVO>> tmap = new HashMap<>();// 需要循环的
																	// 生成动态行的数据
		ArrayList<String> slist = new ArrayList<>();
		for (TaxPosContrastVO vo : vos) {
			if (slist.contains(vo.getPk_parent())) {

				if (tmap.containsKey(vo.getPk_parent())) {
					dzlist = tmap.get(vo.getPk_parent());
				} else {
					dzlist = new ArrayList<>();
				}
				dzlist.add(vo);
				tmap.put(vo.getPk_parent(), dzlist);
			} else {
				if (vo.getIsdynamic() != null && vo.getIsdynamic().booleanValue()) {
					slist.add(vo.getPk_taxtemplet_sd_pos());
					dlist.add(vo);
				} else {
					if (!tmap.containsKey(vo.getPk_parent())) {// 过滤掉动态行
						tlist.add(vo);
					}
				}
			}
		}

		for (TaxPosContrastVO vo : dlist) {
			String pk_parent = vo.getPk_taxtemplet_sd_pos();
			dzlist = tmap.get(pk_parent);

			if (dzlist == null || dzlist.size() == 0) {
				throw new BusinessException("纳税申报动态行子信息出错,报表编号为" + vo.getReportcode());
			}
			int beginrow = vo.getIbeginrow();
			int rowcount = vo.getIrowcount();

			for (int i = 0; i < rowcount; i++) {

				TaxPosContrastVO headclone = (TaxPosContrastVO) vo.clone();
				String pkid = UUID.randomUUID().toString();
				headclone.setPk_taxtemplet_sd_pos(pkid);
				tlist.add(headclone);
				TaxPosContrastVO bvoclone = null;
				for (TaxPosContrastVO bvo : dzlist) {

					bvoclone = (TaxPosContrastVO) bvo.clone();
					if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
						bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
					} else if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("R")) { // 行固定
						bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
						bvoclone.setIcol(i);
					}
					bvoclone.setIrow(i);
					bvoclone.setPk_parent(pkid);
					bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
					tlist.add(bvoclone);
				}
			}
		}
		return tlist;
	}

	// 设置组装报文时需要的一些参数
	protected TaxQcQueryVO getTaxQcQueryVO(TaxReportVO reportvo) throws DZFWarpException {
		TaxQcQueryVO qcvo = new TaxQcQueryVO();
		qcvo.setGdslxDm("1");

		// if (reportvo.getPeriodtype() != null &&
		// reportvo.getPeriodtype().intValue() == 0) {
		// // 月
		// String period = DateUtils.getPreviousPeriod(DateUtils.getPeriod(new
		// DZFDate()));
		// qcvo.setSssqQ(DateUtils.getPeriodStartDate(period).toString());
		// qcvo.setSssqZ(DateUtils.getPeriodEndDate(period).toString());
		// } else if (reportvo.getPeriodtype() != null &&
		// reportvo.getPeriodtype().intValue() == 1) {
		// // 季度
		// String period = DateUtils.getPeriod(new DZFDate());
		// qcvo.setSssqQ(getQuarterStartDate(period));
		// qcvo.setSssqZ(getQuarterEndDate(period));
		// }

		qcvo.setSssqQ(reportvo.getPeriodfrom());
		qcvo.setSssqZ(reportvo.getPeriodto());
		return qcvo;
	}

	// 转换中间有分隔符的字符
	protected void setSpecialNum(TaxPosContrastVO vo) {
		if (!StringUtil.isEmpty(vo.getValue())) {
			if (vo.getValue().contains("|")) {
				int index = vo.getValue().indexOf("|");
				String value = vo.getValue().substring(0, index);
				if (!StringUtil.isEmpty(value)) {
					vo.setValue(value.trim());
				}
			} else if (vo.getValue().contains("_")) {
				String value = vo.getValue().split("_")[0];
				if (!StringUtil.isEmpty(value)) {
					vo.setValue(value.trim());
				}
			} else if (vo.getValue().contains("-")) {
				String value = vo.getValue().split("-")[0];
				if (!StringUtil.isEmpty(value)) {
					vo.setValue(value.trim());
				}
			}
		}
	}

	// 转换是为Y
	protected void setYesOrNo(TaxPosContrastVO vo) {
		if (!StringUtil.isEmpty(vo.getValue())) {
			if ("是".equals(vo.getValue())) {
				vo.setValue("Y");
			} else {
				vo.setValue("N");
			}
		}
	}

	// 转换是为0
	protected void setZeroOrOne(TaxPosContrastVO vo) {
		if (!StringUtil.isEmpty(vo.getValue())) {
			if ("是".equals(vo.getValue())) {
				vo.setValue("0");
			} else {
				vo.setValue("1");
			}
		}
	}

	// 转换是为1
	protected void setOneOrZero(TaxPosContrastVO vo) {
		if (!StringUtil.isEmpty(vo.getValue())) {
			if ("是".equals(vo.getValue())) {
				vo.setValue("1");
			} else {
				vo.setValue("0");
			}
		}
	}

	// 设置日期
	protected void setDateData(TaxPosContrastVO vo) {
		try {
			if (StringUtil.isEmpty(vo.getValue())) {
				return;
			}
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			Date date = null;
			StringTokenizer st = new StringTokenizer(vo.getValue(), "-/.");
			if (st.countTokens() == 3) {
				DZFDate ddate = new DZFDate(vo.getValue());
				date = ddate.toDate();
			} else {
				date = formatter.parse(vo.getValue());
			}
			String dateString = formatter.format(date);
			vo.setValue(dateString);
		} catch (Exception e) {
			throw new BusinessException(
					"请检查表编码" + vo.getReportcode() + ",字段名" + vo.getItemname() + ",日期是否满足要求" + vo.getValue());
		} finally {

		}
	}

	// 向中税发送报文
	public Object sendTaxReport(CorpVO corpVO, Map objMapReport, SpreadTool spreadtool, TaxReportVO reportvo,
								UserVO userVO) throws DZFWarpException {
		TaxQcQueryVO qcvo = getTaxQcQueryVO(reportvo);

		if (qcvo == null || qcvo.getIsSend() == null || !qcvo.getIsSend().booleanValue()) {
			if(StringUtil.isEmpty(qcvo.getNoSendReason())){
				throw new BusinessException("当前税种不支持上报！");
			}else{
				throw new BusinessException(qcvo.getNoSendReason());
			}
		}
		// 验证登录
		corpVO = (CorpVO) sbo.queryByPrimaryKey(CorpVO.class, corpVO.getPk_corp());
		CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(corpVO.getPk_corp());
		HashMap<String, String> map = WebServiceProxy.yzdlToSD(corpVO, taxvo);
//		 HashMap<String, String> map = new HashMap<>();
		qcvo.setDjxh(map.get(TaxConst.RETURN_ITEMKEY_DJXH));
		// 组装报文
		String yjsbBwXml = getyjSbbw(corpVO, objMapReport, spreadtool, reportvo, qcvo);
		// System.out.println(yjsbBwXml);
		// 上报数据
		// 扣费
		doCharge(corpVO, reportvo, userVO);
		map = WebServiceProxy.yjsbToSD(corpVO, taxvo, map, yjsbBwXml, qcvo, 0);
		// System.out.println(map.get(TaxConst.RETURN_ITEMKEY_MESSAGE));
		return null;
	}

	// 收费
	private void doCharge(CorpVO corpVO, TaxReportVO reportvo, UserVO userVO) {

		// 如果是加盟商 不走扣费
		if (corpVO.getIschannel() != null && corpVO.getIschannel().booleanValue()) {
			return;
		}

		IVersionMngService verionMng = (IVersionMngService) SpringUtils.getBean("sys_funnodeversionserv");
		// 是否收费
		DZFBoolean isCharge = verionMng.isChargeByProduct(reportvo.getPk_corp(), IDzfServiceConst.DzfServiceProduct_03);
		if (isCharge.booleanValue()) {
			IBalanceService chargeService = (IBalanceService) SpringUtils.getBean("balanceServImpl");
			String period = reportvo.getPeriodto().toString().substring(0, 7);
			// 是否已收费
			DZFBoolean charged = chargeService.isAlreadyConsumption(IDzfServiceConst.DzfServiceProduct_03, period,
					reportvo.getPk_corp());
			if (!charged.booleanValue()) {
				DZFDouble price = null;
				// if ("一般纳税人".equals(corpVO.getChargedeptname())) {
				// price = new
				// DZFDouble(ITaxRptStandFeeConst.TaxRptStandFee_YBR);
				// } else {
				// price = new
				// DZFDouble(ITaxRptStandFeeConst.TaxRptStandFee_XGM);
				// }
				DZFDate dzfdate = new DZFDate(period + "-15");
				String wtcorp = pubFctService.getAthorizeFactoryCorp(dzfdate, reportvo.getPk_corp());
				String pkcorp = corpVO.getPk_corp();
				if (!StringUtil.isEmpty(wtcorp)) {// 是否委托会计工厂
					pkcorp = wtcorp;
				} else {
					pkcorp = queryCascadeCorps(corpVO.getPk_corp());// 代帐公司
				}
				// 一键报税设置取金额
				SQLParameter param = new SQLParameter();
				DZFDate date = new DZFDate();
				param.addParam(pkcorp);
				param.addParam(date.toString());
				param.addParam(date.toString());
				BondedSetVO[] vos = (BondedSetVO[]) sbo.queryByCondition(BondedSetVO.class,
						"pk_corp = ? and nvl(dr,0) = 0 and begindate <=? and enddate >=?", param);
				if (vos != null && vos.length > 0) {
					if ("一般纳税人".equals(corpVO.getChargedeptname())) {
						price = vos[0].getGeneralamount();
					} else {
						price = vos[0].getScaleamount();
					}
				} else {
					if ("一般纳税人".equals(corpVO.getChargedeptname())) {
						price = new DZFDouble(ITaxRptStandFeeConst.TaxRptStandFee_YBR);
					} else {
						price = new DZFDouble(ITaxRptStandFeeConst.TaxRptStandFee_XGM);
					}
				}

				DZFBalanceBVO bvo = new DZFBalanceBVO();
				bvo.setPeriod(period);
				// 使用数量
				bvo.setChangedcount(price);
				// 减少
				bvo.setIsadd(1);
				bvo.setPk_corp(reportvo.getPk_corp());
				bvo.setPk_dzfservicedes(IDzfServiceConst.DzfServiceProduct_03);
				bvo.setPk_user(userVO.getCuserid());

				// 查询委托公司
				// String wtcorp = pubFctService.getAthorizeFactoryCorp(dzfdate,
				// reportvo.getPk_corp());
				if (StringUtil.isEmpty(wtcorp)) {
					chargeService.consumption(bvo);// 扣费
				} else {
					// 扣委托公司费用
					chargeService.consumptionByFct(bvo, dzfdate);
				}
			}
		}
	}

	// 组装报文
	protected String getyjSbbw(CorpVO corpVO, Map objMapReport, SpreadTool spreadtool, TaxReportVO reportvo,
			TaxQcQueryVO qcvo) {
		// 查询字段中间关系表数据
		TaxPosContrastVO[] vos = createTaxPosContrastVOS(corpVO, objMapReport, spreadtool, reportvo, qcvo.getDjxh());
		if (vos == null || vos.length == 0)
			throw new BusinessException("上报数据出错!");

		TaxPosContrastVO voss = (TaxPosContrastVO) BDTreeCreator.createTree(vos, new TaxVOTreeStrategy());
		vos = (TaxPosContrastVO[]) voss.getChildren();
		// 组装报文
		String yjsbBwXml = XMLUtils.createBusinessXML(vos, qcvo.getXmlType());
		yjsbBwXml = yjsbBwXml.replace("**", "");
		yjsbBwXml = yjsbBwXml.replace("——", "");
		yjsbBwXml = yjsbBwXml.replace("*", "0");
		return yjsbBwXml;
	}

	// 查询上报系统模板 暂时月报 季报共用一个模板 如果需要拆分 对应的 中间表也需要拆分
	protected HashMap<String, TaxRptTempletVO> getTempletVO(String sb_zlbh, String pk_corp, String period) {
		// 查询字段中间关系表数据
		StringBuffer strb = new StringBuffer();
		strb.append(" select  t.*   from ynt_tax_sbzl l");
		strb.append(" join ynt_taxrpttemplet t on l.pk_taxsbzl = t.pk_taxsbzl ");
		strb.append("  where t.location = '山东'  and l.sbcode || l.sbzq = ? ");
		strb.append("  and nvl(l.dr,0)= 0 and  nvl(t.dr,0)= 0 ");

		SQLParameter params = new SQLParameter();
		params.addParam(sb_zlbh);

		List<TaxRptTempletVO> list = (List<TaxRptTempletVO>) sbo.executeQuery(strb.toString(), params,
				new BeanListProcessor(TaxRptTempletVO.class));
		//
		// List<TaxRptTempletVO> list
		// =taxRptservice_default.queryRptTempletVOs(pk_corp, period);
		if (list == null || list.size() == 0)
			throw new BusinessException("纳税申报模板信息出错");

		//

		HashMap<String, TaxRptTempletVO> hmTemplet = new HashMap<String, TaxRptTempletVO>();
		for (TaxRptTempletVO vo : list) {
			hmTemplet.put(vo.getPk_taxrpttemplet(), vo);
		}
		return hmTemplet;

	}

	//
	protected String getSb_zlbh(TaxReportVO reportvo) {
		return reportvo.getSb_zlbh();
	}

	/**
	 * 创建纳税申报信息
	 * 
	 * @param corpVO
	 * @return
	 */
	protected TaxPosContrastVO[] createTaxPosContrastVOS(CorpVO corpVO, Map objMapReport, SpreadTool spreadtool,
			TaxReportVO reportvo, String djxh) {
		String sb_zlbh = getSb_zlbh(reportvo);
		HashMap<String, TaxRptTempletVO> hmTemplet = getTempletVO(sb_zlbh, corpVO.getPk_corp(), reportvo.getPeriod());

		List<TaxPosContrastVO> list = getTaxPosVO(sb_zlbh, hmTemplet);

		if (list == null || list.size() == 0)
			return null;

		for (TaxPosContrastVO vo : list) {
			String vdefaultvalue = vo.getVdefaultvalue();
			vo.setDjxh(djxh);
			if (reportvo.getPeriodtype() != null) {
				vo.setYjntype(reportvo.getPeriodtype().toString());
			}
			if (!StringUtil.isEmpty(vdefaultvalue)) {
				// 默认值处理
				setDefaultValue(vo, corpVO.getVsoccrecode(), reportvo);
			}

			// 按照单元格取数
			String reportcode = vo.getReportcode();
			if (StringUtil.isEmpty(reportcode)) {
				continue;
			}

			String fromcell = vo.getFromcell();
			if (StringUtil.isEmpty(fromcell)) {
				if (vo.getIsspecial() != null && vo.getIsspecial().booleanValue()) {
					// 特殊字段处理
					setSpecialValue(vo);
				}
				continue;
			}

			String[] spos = fromcell.split("C");
			int iRow = Integer.parseInt(spos[0].substring(1, spos[0].length()));
			int iColumn = Integer.parseInt(spos[1]);
			TaxRptTempletVO tvo = hmTemplet.get(vo.getPk_taxtemplet());
			if (tvo == null) {
				continue;
				// throw new BusinessException("纳税申报模板信息出错");
			}

			Object o = spreadtool.getCellValue(objMapReport, tvo.getReportname(), iRow, iColumn);
			if (o != null) {
				String value = o.toString();
				if (" ".equalsIgnoreCase(value) || "——".equalsIgnoreCase(value)) {
					continue;
				}
				vo.setValue(value);
			}
			if (vo.getIsspecial() != null && vo.getIsspecial().booleanValue()) {
				// 特殊字段处理
				setSpecialValue(vo);
			}
		}

		// 校验数据信息
		for (TaxPosContrastVO vo : list) {
			checkData(vo);
		}

		list = filterNullRow(list);

		return list.toArray(new TaxPosContrastVO[list.size()]);
	}

	protected List<TaxPosContrastVO> filterNullRow(List<TaxPosContrastVO> list) throws DZFWarpException {
		List<TaxPosContrastVO> list1 = new ArrayList<>();
		List<String> slist = new ArrayList<>();

		for (TaxPosContrastVO vo : list) {
			if (vo.getIsdynamic() != null && vo.getIsdynamic().booleanValue()) {
				slist.add(vo.getPk_taxtemplet_sd_pos());
			}
		}

		Map<String, List<TaxPosContrastVO>> map = new HashMap<>();

		List<TaxPosContrastVO> clist = null;
		for (TaxPosContrastVO vo : list) {
			if (slist.contains(vo.getPk_parent())) {
				if (map.containsKey(vo.getPk_parent())) {
					clist = map.get(vo.getPk_parent());
				} else {
					clist = new ArrayList<>();
				}
				clist.add(vo);
				map.put(vo.getPk_parent(), clist);
			}
		}
		List<String> relist = getNullRowPk(map);
		for (TaxPosContrastVO vo : list) {
			if (!relist.contains(vo.getPk_parent()) && !relist.contains(vo.getPk_taxtemplet_sd_pos())) {
				list1.add(vo);
			}
		}

		return list1;
	}

	protected List<String> getNullRowPk(Map<String, List<TaxPosContrastVO>> map) {
		List<String> relist = new ArrayList<>();
		return relist;
	}

	// 获取期初数据
	public HashMap<String, Object> getQcData(CorpVO corpvo, TaxReportVO reportvo) throws DZFWarpException {

		HashMap<String, Object> hmQCData = new HashMap<String, Object>();
		CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(corpvo.getPk_corp());
		if (StringUtil.isEmpty(corpvo.getVsoccrecode()) || StringUtil.isEmpty(taxvo.getVstatetaxpwd())
				|| corpvo.getVsoccrecode().length() == 1) {
			return hmQCData;
		}
		TaxQcQueryVO qcvo = getTaxQcQueryVO(reportvo);
		// 更新期初
		setQcvo(qcvo);
		if (qcvo == null || qcvo.getIsConQc() == null || !qcvo.getIsConQc().booleanValue()) {
			return hmQCData;
		}
		// 验证登录
		HashMap<String, String> map = WebServiceProxy.yzdlToSD(corpvo, taxvo);
		qcvo.setDjxh(map.get(TaxConst.RETURN_ITEMKEY_DJXH));
		// 拼接报文
		String yjsbBwXml = getQcXML(corpvo, taxvo, qcvo);
		// 提交报文
		map = WebServiceProxy.yjsbToSD(corpvo, taxvo, map, yjsbBwXml, qcvo, 1);
		String bodyxml = ParseXml.getBodyXml(map.get("xml"));
		// log.info("税号：" + corpvo.getVsoccrecode() +
		// "----yzBwXml----返回的期初报文-:\n" + bodyxml);
		// System.out.println(bodyxml);
		// 解析报文 转换成期初数据
		if (StringUtil.isEmpty(bodyxml)) {
			throw new BusinessException("期初返回报文异常");
		}
		parseInitData(hmQCData, bodyxml, qcvo.getIntParse());

		return hmQCData;
	}

	// 设置期初数据 参数
	protected void setQcvo(TaxQcQueryVO qcvo) {

	}

	/**
	 * 解析期初数据为Map
	 * 
	 * @param initData
	 * @param bodyxml
	 * @return
	 */
	protected void parseInitData(Map<String, Object> initData, String bodyxml, InitFiledMapParse intParse) {

		if (intParse == null)
			return;
		try {
			Document document = DocumentHelper.parseText(bodyxml);

			String[] fields = intParse.getFields();
			for (String voName : fields) {
				Element elem = ParseXml.getElementByName((DefaultElement) document.getRootElement(), voName);
				if (elem == null)
					continue;
				List elementList = elem.content();
				if (elementList == null || elementList.size() == 0)
					continue;

				String indexName = intParse.getIndexField(voName);
				for (Iterator it = elementList.iterator(); it.hasNext();) {
					Object item = it.next();
					if (item instanceof DefaultElement) {
						String index = ParseXml.getValueByName((DefaultElement) item, indexName);
						Map<String, String> nameMap = intParse.getNameMap(voName + "--" + index);
						if (nameMap != null) {
							for (Entry<String, String> entry : nameMap.entrySet()) {
								String text = (String) ParseXml.getValueByName((DefaultElement) item, entry.getValue());
								if (text == null)
									continue;
								initData.put(entry.getKey(), new DZFDouble(text));
								// System.out.println("业签" + voName + "，行列数：" +
								// index + "，字段：" + entry.getKey() + "，值："
								// + initData.get(entry.getKey()));
							}
						}
					}
				}
			}
		} catch (DocumentException e) {
			throw new BusinessException(e.getMessage());
		}
	}

	@Override
	public String[] getCondition(String pk_taxreport, UserVO userVO, TaxReportVO reportvo) throws DZFWarpException {
		List<String> listCondition = new ArrayList<String>();
		return listCondition.toArray(new String[0]);
	}

	protected String[] getReportNameFromCondition(String condition) {
		List<String> listreportname = new ArrayList<String>();

		String regex = "([^!\\(:=><\\+\\-\\*/]*?)\\!";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(condition);

		while (m.find()) {

			String sname = m.group(1).trim();
			if (listreportname.contains(sname) == false) {
				listreportname.add(sname);
			}
		}
		return listreportname.toArray(new String[0]);
	}

	@Override
	public void queryDeclareStatus(CorpVO corpvo, CorpTaxVo taxvo, TaxReportVO reportvo) throws DZFWarpException {
		TaxQcQueryVO qcvo = getTaxQcQueryVO(reportvo);
		// if (StringUtil.isEmpty(reportvo.getSpreadfile()) ||
		// !"101".equals(reportvo.getSbzt_dm()))
		// return;
		// long startTime1 = System.currentTimeMillis(); // 获取开始时间
		if (qcvo == null || qcvo.getIsSend() == null || !qcvo.getIsSend().booleanValue()) {
			return;
		}
		qcvo.setYwlx(TaxConst.SERVICE_CODE_ZT);
		qcvo.setImpl(null);

		// 验证登录
		// long startTime = System.currentTimeMillis(); // 获取开始时间
		HashMap<String, String> map = WebServiceProxy.yzdlToSD(corpvo, taxvo);
		// long endTime = System.currentTimeMillis(); // 获取结束时间
		// String time = TimeFormatUtil.formatTime(endTime - startTime);
		// System.out.println(reportvo.getSbname() +"验证登录时间"+time);
		// 拼接报文
		String yjsbBwXml = XMLUtils.createSbztXML(corpvo.getVsoccrecode(), taxvo.getVstatetaxpwd(),
				Integer.toString(new DZFDate().getYear()), qcvo);
		// System.out.println(yjsbBwXml);
		// 提交报文
		// startTime = System.currentTimeMillis(); // 获取开始时间
		map = WebServiceProxy.yjsbToSD(corpvo, taxvo, map, yjsbBwXml, qcvo, 2);
		// endTime = System.currentTimeMillis(); // 获取结束时间
		// time = TimeFormatUtil.formatTime(endTime - startTime);
		// System.out.println(reportvo.getSbname() +"提交报文时间"+time);
		String bodyxml = ParseXml.getBodyXml(map.get("xml"));
		// System.out.println(bodyxml);
		// log.info("税号：" + corpvo.getVsoccrecode() +
		// "----yzBwXml----返回的税种状态-:\n" + bodyxml);
		// System.out.println(bodyxml);
		// 解析报文 转换成期初数据
		if (StringUtil.isEmpty(bodyxml)) {
			throw new BusinessException("返回的税种状态报文异常");
		}
		HashMap<String, String> vmap = new HashMap<>();
		ParseXml.readXml(bodyxml, vmap);
		String bbzt = vmap.get("bbzt");
		updateSBTZSD(bbzt, reportvo);
		// long endTime1 = System.currentTimeMillis(); // 获取开始时间
		// time = TimeFormatUtil.formatTime(endTime1 - startTime1);
		// System.out.println(reportvo.getSbname() +"上报时间"+time);
	}

	private void updateSBTZSD(String bbzt, TaxReportVO reportvo) {

		String sbzt_dm = null;
		String remark = null;
		if (StringUtil.isEmpty(bbzt)) {
			return;
		} else if ("2".equals(bbzt)) {
			// 申报成功
			sbzt_dm = "4";// 申报成功
		} else if ("12".equals(bbzt)) {
			// 提请作废已批准
			sbzt_dm = "5";// 作废
		} else if ("3".equals(bbzt)) {
			// 写入征管系统失败
			remark = "写入征管系统失败";
			sbzt_dm = "3";// 申报失败
		} else if ("6".equals(bbzt)) {
			// 票表税比对失败
			sbzt_dm = "3";// 申报失败
			remark = "票表税比对失败";
		} else {
			return;
		}

		reportvo.setSbzt_dm(sbzt_dm);
		reportvo.setRemark(remark);
		sbo.update(reportvo, new String[] { "sbzt_dm", "remark" });
		String sql = "update ynt_taxreportdetail set sbzt_dm = ? where pk_corp = ? and  pk_taxreport = ? and nvl(dr, 0) = 0";
		SQLParameter sp = new SQLParameter();
		sp.addParam(reportvo.getSbzt_dm());
		sp.addParam(reportvo.getPk_corp());
		sp.addParam(reportvo.getPk_taxreport());
		sbo.executeUpdate(sql, sp);
	}

	/**
	 * 级联查询总公司
	 * 
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	private String queryCascadeCorps(String pk_corp) throws DZFWarpException {
		if (StringUtil.isEmpty(pk_corp))
			return null;
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String sql = "select pk_corp from bd_corp  start with pk_corp = ? connect by  pk_corp = prior  fathercorp and nvl(dr,0) = 0";
		List<CorpVO> list = (List<CorpVO>) sbo.executeQuery(sql, sp, new BeanListProcessor(CorpVO.class));
		if (list != null && list.size() >= 2) {
			return list.get(list.size() - 2).getPk_corp();
		}
		return null;

	}

	protected String getQcXML(CorpVO corpvo, CorpTaxVo taxvo, TaxQcQueryVO qcvo) {
		String yjsbBwXml = XMLUtils.createQcXML(corpvo.getVsoccrecode(), taxvo.getVstatetaxpwd(),
				Integer.toString(new DZFDate().getYear()), qcvo);
		return yjsbBwXml;
	}

}
