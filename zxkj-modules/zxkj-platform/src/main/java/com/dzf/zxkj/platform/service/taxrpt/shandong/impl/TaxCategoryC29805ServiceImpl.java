package com.dzf.zxkj.platform.service.taxrpt.shandong.impl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxPosContrastVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxConst;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxQcQueryVO;
import com.dzf.zxkj.platform.service.taxrpt.shandong.SDTaxConst;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.XMLUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

//财报-企业会计制度
@Service("taxcateserv_C29805")
public class TaxCategoryC29805ServiceImpl extends DefaultTaxCategoryServiceImpl {

	protected void setDefaultValue(TaxPosContrastVO vo, String nsrsbh, TaxReportVO reportvo) throws DZFWarpException {
		if ("#row".equals(vo.getVdefaultvalue())) {
			vo.setValue(Integer.toString((vo.getIrow()) + 1));
		} else {
			super.setDefaultValue(vo, nsrsbh, reportvo);
		}
	}

	protected void setSpecialValue(TaxPosContrastVO vo) throws DZFWarpException {
		if ("C2001".equals(vo.getReportcode())) {// 资产负债表
			if ("ewbhxh".equals(vo.getItemkey())) {// 二维表行序号
				if (!StringUtil.isEmpty(vo.getValue())) {
					String value = vo.getValue();
					int rowno = Integer.parseInt(value);
					if (rowno > 19) {
						if (rowno == 20) {
							vo.setValue("33");
						} else {
							vo.setValue(Integer.toString(rowno - 1));
							if (rowno > 28) {
								if (rowno == 29) {
									vo.setValue("34");
								} else {
									vo.setValue(Integer.toString(rowno - 2));
								}
							}
						}
					}
				}
			}
		} else if ("C2002".equals(vo.getReportcode())) {// 利润表
			if ("ewbhxh".equals(vo.getItemkey())) {// 二维表行序号
				if (!StringUtil.isEmpty(vo.getValue())) {
					String value = vo.getValue();
					int rowno = Integer.parseInt(value);
					if (rowno > 12) {
						if (rowno == 13) {
							vo.setValue("21");
						} else {
							vo.setValue(Integer.toString(rowno - 1));
							if (rowno > 18) {
								vo.setValue(Integer.toString(rowno + 3));
								if (rowno > 29) {
									vo.setValue(Integer.toString(rowno - 12));
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public TaxQcQueryVO getTaxQcQueryVO(TaxReportVO reportvo) throws DZFWarpException {
		TaxQcQueryVO qcvo = super.getTaxQcQueryVO(reportvo);
		qcvo.setYwlx(TaxConst.SERVICE_CODE_YBQYCB);
		qcvo.setXmlType(TaxConst.XMLTYPE_YBQYCB);
		qcvo.setIsSend(DZFBoolean.TRUE);
		qcvo.setYzpzzlDm(TaxConst.CODE_YBQYCB);
		return qcvo;
	}

	@Override
	protected String getyjSbbw(CorpVO corpVO, Map objMapReport, SpreadTool spreadtool, TaxReportVO reportvo,
							   TaxQcQueryVO qcvo) {
		String yjsbBwXml = super.getyjSbbw(corpVO, objMapReport, spreadtool, reportvo, qcvo);
		yjsbBwXml = XMLUtils.createCbXML(corpVO.getVsoccrecode(), Integer.toString(new DZFDate().getYear()), qcvo,
				yjsbBwXml);
		return yjsbBwXml;
	}

	protected String getSb_zlbh(TaxReportVO reportvo) {
		return SDTaxConst.TEMPLET_SBZLBHC2;
	}
}
