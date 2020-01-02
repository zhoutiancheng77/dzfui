package com.dzf.zxkj.platform.service.taxrpt.shandong.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.constant.TaxRptConst;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.tree.BDTreeCreator;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxPosContrastVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.tax.TaxRptTempletVO;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxConst;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxQcQueryVO;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxVOTreeStrategy;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.taxrpt.shandong.ITaxReportService;
import com.dzf.zxkj.platform.service.taxrpt.shandong.datagetter.CheckValueData;
import com.dzf.zxkj.platform.service.taxrpt.shandong.datagetter.DefaultValueGetter;
import com.dzf.zxkj.platform.service.taxrpt.shandong.datagetter.SpecialValueGetter;
import com.dzf.zxkj.platform.service.taxrpt.shandong.datagetter.TaxTempletVOGetter;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("taxReportSer")
public class TaxReportServiceImpl implements ITaxReportService {

//	private static Logger log = Logger.getLogger(TaxReportServiceImpl.class);

	@Autowired
	private SingleObjectBO sbo;
	@Autowired
	private IBDCorpTaxService sys_corp_tax_serv;

	// 发送增值税小规模
	@Override
	public Object sendTaxReport(CorpVO corpVO, Map objMapReport, SpreadTool spreadtool, TaxReportVO reportvo)
			throws DZFWarpException {

		checkPeriod(reportvo);
		CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(corpVO.getPk_corp());
		String nsrsbh = corpVO.getVsoccrecode();
		String vstatetaxpwd = taxvo.getVstatetaxpwd();

		if (StringUtil.isEmpty(nsrsbh)) {
			throw new BusinessException("纳税人识别号不能为空");
		}

		if (StringUtil.isEmpty(vstatetaxpwd)) {
			throw new BusinessException("纳税密码不能为空");
		}
		HashMap<String, String> map = yzdlToSD(nsrsbh, vstatetaxpwd);

		if (map == null || map.size() == 0 || map.get(TaxConst.RETURN_ITEMKEY_NO) == null) {
			throw new BusinessException("中税登录验证失败");
		}

		if (!"0".equals(map.get(TaxConst.RETURN_ITEMKEY_NO))) {
			throw new BusinessException("中税登录验证失败:" + map.get(TaxConst.RETURN_ITEMKEY_OBJ));
		}

		if (!"0000".equals(map.get(TaxConst.RETURN_CODE))) {
			throw new BusinessException("中税登录验证失败:" + map.get(TaxConst.RETURN_MSG));
		}

		TaxPosContrastVO[] vos = createTaxPosContrastVOS(corpVO, objMapReport, spreadtool, reportvo, nsrsbh);
		if (vos == null || vos.length == 0)
			throw new BusinessException("上报数据出错!");

		TaxPosContrastVO voss = (TaxPosContrastVO) BDTreeCreator.createTree(vos, new TaxVOTreeStrategy());
		vos = (TaxPosContrastVO[]) voss.getChildren();

		String ywlx = TaxConst.SERVICE_CODE_ZZSXGM;

		TaxQcQueryVO qcvo = new TaxQcQueryVO();
		String period = DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
		qcvo.setGdslxDm("1");

		String xmlType = null;
		String impl = null;
		if (TaxRptConst.SB_ZLBH10101.equalsIgnoreCase(reportvo.getSb_zlbh())) {
			ywlx = TaxConst.SERVICE_CODE_ZZSYBNSR;
			xmlType = TaxConst.XMLTYPE_ZZSYBNSR;
		} else if (TaxRptConst.SB_ZLBH10102.equalsIgnoreCase(reportvo.getSb_zlbh())) {
			ywlx = TaxConst.SERVICE_CODE_ZZSXGM;
			xmlType = TaxConst.XMLTYPE_ZZSXGM;
		} else if (TaxRptConst.SB_ZLBH_SETTLEMENT.equalsIgnoreCase(reportvo.getSb_zlbh())) {
			ywlx = TaxConst.SERVICE_CODE_HSQJ;
			xmlType = TaxConst.XMLTYPE_HSQJ;
		} else if (TaxRptConst.SB_ZLBHC1.equalsIgnoreCase(reportvo.getSb_zlbh())) {
			ywlx = TaxConst.SERVICE_CODE_XQYKJZZLCB;
			xmlType = TaxConst.XMLTYPE_XQYKJZZLCB;
			qcvo.setSssqQ(DateUtils.getPeriodStartDate(period).toString());
			qcvo.setSssqZ(DateUtils.getPeriodEndDate(period).toString());
		} else if (TaxRptConst.SB_ZLBHC2.equalsIgnoreCase(reportvo.getSb_zlbh())) {
			ywlx = TaxConst.SERVICE_CODE_YBQYCB;
			xmlType = TaxConst.XMLTYPE_YBQYCB;
			qcvo.setSssqQ(DateUtils.getPeriodStartDate(period).toString());
			qcvo.setSssqZ(DateUtils.getPeriodEndDate(period).toString());
		} else if (TaxRptConst.SB_ZLBH10412.equalsIgnoreCase(reportvo.getSb_zlbh())) {
			ywlx = TaxConst.SERVICE_CODE_QYSDSA;
			xmlType = TaxConst.XMLTYPE_QYSDSA;
			impl = TaxConst.IMPLVALUE;
			qcvo.setSssqQ(DateUtils.getPeriodStartDate(period).toString());
			qcvo.setSssqZ(DateUtils.getPeriodEndDate(period).toString());
		} else if (TaxRptConst.SB_ZLBH10413.equalsIgnoreCase(reportvo.getSb_zlbh())) {
			ywlx = TaxConst.SERVICE_CODE_QYSDSB;
			xmlType = TaxConst.XMLTYPE_QYSDSB;
			impl = TaxConst.IMPLVALUE;
			qcvo.setSssqQ(DateUtils.getPeriodStartDate(period).toString());
			qcvo.setSssqZ(DateUtils.getPeriodEndDate(period).toString());
		}
		String yjsbBwXml = XMLUtils.createBusinessXML(vos, xmlType);
		String supplier = TaxParamUtils.SUPPLIER;
		yjsbBwXml = yjsbBwXml.replace("**", "");
		yjsbBwXml = yjsbBwXml.replace("——", "");
		// yjsbBwXml = yjsbBwXml.replace(">", "&gt;");
		// yjsbBwXml = yjsbBwXml.replaceAll("\\s*", "");
		//// System.out.println(yjsbBwXml);

		if (TaxRptConst.SB_ZLBHC1.equalsIgnoreCase(reportvo.getSb_zlbh())) {
			yjsbBwXml = XMLUtils.createCbXML(nsrsbh, Integer.toString(new DZFDate().getYear()), qcvo, yjsbBwXml);
		} else if (TaxRptConst.SB_ZLBHC2.equalsIgnoreCase(reportvo.getSb_zlbh())) {
			yjsbBwXml = XMLUtils.createCbXML(nsrsbh, Integer.toString(new DZFDate().getYear()), qcvo, yjsbBwXml);
		} else if (TaxRptConst.SB_ZLBH10412.equalsIgnoreCase(reportvo.getSb_zlbh())) {

			// base64转码
			try {
				yjsbBwXml = Base64.encode(yjsbBwXml.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new BusinessException(e.getMessage());
			}
		} else if (TaxRptConst.SB_ZLBH10413.equalsIgnoreCase(reportvo.getSb_zlbh())) {
			// base64转码
			try {
//				System.out.println(yjsbBwXml);
				yjsbBwXml = Base64.encode(yjsbBwXml.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new BusinessException(e.getMessage());
			}
		}
		yjsbBwXml = XMLUtils.createScBwXml(yjsbBwXml, ywlx, map.get(TaxConst.RETURN_ITEMKEY_SESSIONID),
				new DZFDate().toString(), nsrsbh, vstatetaxpwd, impl,map.get(TaxConst.RETURN_ITEMKEY_DJXH));
		// String yjsbBwXml = ParseXml.readTxtFile("src/yjsb_zzsybnsr.xml");

//		System.out.println(yjsbBwXml);
//		log.info("报文:\n" + yjsbBwXml);
		// 私钥加密过程
		String sign = yjsbBwXml + supplier;
		String cipher = CreateSignUtils.getSign(sign, true);
//		log.info("加密后sign密码:\n" + cipher);

		String reStrYz = WebServiceProxy.yjsbBw(nsrsbh, supplier, ywlx, yjsbBwXml, cipher,
				map.get(TaxConst.RETURN_ITEMKEY_TOKEN));

		HashMap<String, String> map1 = ParseJsonData.getJsonData(reStrYz);
//		System.out.println(reStrYz);
		if (map1 == null || map1.size() == 0 || map1.get(TaxConst.RETURN_ITEMKEY_NO) == null) {
			throw new BusinessException("中税上报失败");
		}

		if (!"0".equals(map1.get(TaxConst.RETURN_ITEMKEY_NO))) {
			if (StringUtil.isEmpty(map1.get(TaxConst.RETURN_ITEMKEY_OBJ))) {
				throw new BusinessException("中税上报失败:" + map1.get(TaxConst.RETURN_ITEMKEY_MESSAGE));
			} else {
				throw new BusinessException("中税上报失败:" + map1.get(TaxConst.RETURN_ITEMKEY_OBJ));
			}
		}

		if (!"0000".equals(map.get(TaxConst.RETURN_CODE))) {
			throw new BusinessException("中税上报失败:" + map.get(TaxConst.RETURN_MSG));
		}
//		System.out.println(map1.get(TaxConst.RETURN_ITEMKEY_MESSAGE));
		return null;
	}

	// 验证登录
	private HashMap<String, String> yzdlToSD(String nsrsbh, String vstatetaxpwd) throws DZFWarpException {

		// String nsrsbh = corpVO.getVsoccrecode();
		// String nsrsbh = "371325751788249";
		String ywlx = TaxConst.SERVICE_CODE_NSRDLYZ;

		String supplier = TaxParamUtils.SUPPLIER;

		// 加密内容，格式为 业务报文xml+ supplier
		String yzBwXml = XMLUtils.createYzdlXML(nsrsbh, vstatetaxpwd);

		yzBwXml = XMLUtils.createScBwXml(yzBwXml, ywlx, null, new DZFDate().toString(), nsrsbh, vstatetaxpwd, null,null);
		String sign = yzBwXml + supplier;
//		log.info("----yzBwXml----验证报文-:\n" + yzBwXml);
		// 私钥加密过程
		String cipher = CreateSignUtils.getSign(sign, true);
//		log.info("加密后sign密码:\n" + cipher);
		String reStrYz = WebServiceProxy.yzNsrxx(nsrsbh, supplier, ywlx, yzBwXml, cipher);
//		log.info("验证返回结果" + reStrYz);

		HashMap<String, String> map = ParseJsonData.getJsonData(reStrYz);
		// testQC(nsrsbh, vstatetaxpwd, map);
		return map;
	}

	/**
	 * 创建纳税申报信息
	 * 
	 * @param pk_taxtypelistdetail
	 * @return
	 */
	private TaxPosContrastVO[] createTaxPosContrastVOS(CorpVO corpVO, Map objMapReport, SpreadTool spreadtool,
			TaxReportVO reportvo, String nsrsbh) {

		// String pk_taxtypelistdetail = detailvo.getPk_taxreportdetail();

		String sb_zlbh = reportvo.getSb_zlbh();
		String location = reportvo.getLocation();
		SQLParameter params = new SQLParameter();
		params.addParam(sb_zlbh);
		TaxRptTempletVO[] templetvos = (TaxRptTempletVO[]) sbo.queryByCondition(TaxRptTempletVO.class,
				"nvl(dr,0)=0 and rtrim(location)='" + location + "' and sb_zlbh=?", params);

		if (templetvos == null || templetvos.length == 0)
			throw new BusinessException("纳税申报模板信息出错");

		HashMap<String, TaxRptTempletVO> hmTemplet = new HashMap<String, TaxRptTempletVO>();
		for (TaxRptTempletVO vo : templetvos) {
			hmTemplet.put(vo.getPk_taxrpttemplet(), vo);
		}
		TaxTempletVOGetter getter = new TaxTempletVOGetter(sbo);
		List<TaxPosContrastVO> list = getter.getTaxPosVO(sb_zlbh);

		if (list == null || list.size() == 0)
			return null;

		for (TaxPosContrastVO vo : list) {
			String vdefaultvalue = vo.getVdefaultvalue();

			if (!StringUtil.isEmpty(vdefaultvalue)) {
				// 默认值处理
				vdefaultvalue = DefaultValueGetter.getDefaultValue(vo, nsrsbh);
				vo.setValue(vdefaultvalue);
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
					String value = SpecialValueGetter.getSpecialValue(vo);
					vo.setValue(value);
				}
				continue;
			}

			String[] spos = fromcell.split("C");
			int iRow = Integer.parseInt(spos[0].substring(1, spos[0].length()));
			int iColumn = Integer.parseInt(spos[1]);
			TaxRptTempletVO tvo = hmTemplet.get(vo.getPk_taxtemplet());
			if (tvo == null)
				throw new BusinessException("纳税申报模板信息出错");

			Object o = spreadtool.getCellValue(objMapReport, tvo.getReportname(), iRow, iColumn);
			if (o != null) {
				String value = o.toString();
				if (StringUtil.isEmpty(value) || " ".equalsIgnoreCase(value) || "——".equalsIgnoreCase(value)) {
					continue;
				}
//				log.info("单元格:" + fromcell + "的内容" + value);
				// if (NumberValidationUtils.isRealNumber(value)) {
				vo.setValue(value);
				// } else {
				// throw new BusinessException(
				// tvo.getReportname() + "第" + (iRow + 1) + "行,第" + (iColumn +
				// 1) + "列内容格式不对,内容为" + value);
				// }
				if (vo.getIsspecial() != null && vo.getIsspecial().booleanValue()) {
					// 特殊字段处理
					value = SpecialValueGetter.getSpecialValue(vo);
					vo.setValue(value);
				}
				// System.out.println("reportcode:" + vo.getReportcode() +
				// ",name:"
				// + vo.getItemname() + ",key:"
				// + vo.getItemkey() + ",fromcell:" + vo.getFromcell() +
				// ",value:" +
				// vo.getValue());
			}
		}
		for (TaxPosContrastVO vo : list) {
			CheckValueData.checkData(vo, nsrsbh);
		}
		return list.toArray(new TaxPosContrastVO[list.size()]);
	}

	private void checkPeriod(TaxReportVO reportvo) {

		if (StringUtil.isEmpty(reportvo.getPeriodfrom())) {
			throw new BusinessException("申报起止时间不能为空");
		}
		if (TaxRptConst.SB_ZLBH_SETTLEMENT.equalsIgnoreCase(reportvo.getSb_zlbh())) {// 年度企业所得税

			boolean showYearInTax = Integer.valueOf(DateUtils.getPeriod(new DZFDate()).substring(5)) > 5;
			if (showYearInTax)
				throw new BusinessException("本年度6月份后不能再上报年度企业所得税");

			int year = Integer.parseInt(reportvo.getPeriodfrom().substring(0, 4));
			int curyear = Integer.parseInt(new DZFDate().toString().substring(0, 4));

			if (year != curyear - 1) {
				throw new BusinessException("当前上报[" + (curyear - 1) + "]期间的数据");
			}

		} else if (TaxRptConst.SB_ZLBH10101.equalsIgnoreCase(reportvo.getSb_zlbh())) {// 增值税一般纳税人

			int year = Integer.parseInt(reportvo.getPeriodfrom().substring(0, 4));
			String prePeriod = DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
			DZFDate predate = DateUtils.getPeriodEndDate(prePeriod);
			int lastperiodyear = predate.getYear();
			if (year != lastperiodyear) {
				throw new BusinessException("当前上报[" + prePeriod + "]期间的数据");
			}

			int month = new DZFDate(reportvo.getPeriodto()).getMonth();
			int lastperiodmonth = predate.getMonth();
			if (month != lastperiodmonth) {
				throw new BusinessException("当前上报[" + prePeriod + "]期间的数据");
			}
		} else if (TaxRptConst.SB_ZLBH10102.equalsIgnoreCase(reportvo.getSb_zlbh())) {// 增值税小规模
			int year = Integer.parseInt(reportvo.getPeriodfrom().substring(0, 4));

			String prePeriod = DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
			int lastperiodyear = new DZFDate(getQuarterStartDate(prePeriod)).getYear();
			if (year != lastperiodyear) {
				throw new BusinessException("上报增值税小规模所属年度不对");
			}

			// int month = new DZFDate(reportvo.getPeriodfrom()).getMonth();
			// int lastperiodmonth = new
			// DZFDate(getQuarterStartDate(prePeriod)).getMonth();
			// if (month != lastperiodmonth) {
			// throw new BusinessException("上报增值税小规模所属季度不对");
			// }
		} else if (TaxRptConst.SB_ZLBHC1.equalsIgnoreCase(reportvo.getSb_zlbh())) {// 财报小企业
		} else if (TaxRptConst.SB_ZLBHC2.equalsIgnoreCase(reportvo.getSb_zlbh())) {// 财报一般
		} else if (TaxRptConst.SB_ZLBH10412.equalsIgnoreCase(reportvo.getSb_zlbh())) {// 企业所得税A
		} else if (TaxRptConst.SB_ZLBH10413.equalsIgnoreCase(reportvo.getSb_zlbh())) {// 企业所得税B
		} else {
			throw new BusinessException("当前报表类型不支持上报");
		}

	}

	private String getQuarterStartDate(String yearmonth) {
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

}
