package com.dzf.zxkj.platform.service.taxrpt.shandong.impl;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxPosContrastVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.taxrpt.shandong.EnterpriseIncomeTaxAInitMappingNew;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxConst;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxQcQueryVO;
import com.dzf.zxkj.platform.service.taxrpt.shandong.InitFiledMapParse;
import com.dzf.zxkj.platform.service.taxrpt.shandong.SDTaxConst;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.Base64;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.XMLUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//所得税季度纳税申报表(A类),不区分一般人和小规模
@Service("taxcateserv_10412new")
@Slf4j
public class TaxCategory10412NewServiceImpl extends DefaultTaxCategoryServiceImpl {

//	private static Logger log = Logger.getLogger(WebServiceProxy.class);

	@Override
	protected void setDefaultValue(TaxPosContrastVO vo, String nsrsbh, TaxReportVO reportvo) throws DZFWarpException {
		if ("#row".equals(vo.getVdefaultvalue())) {
			vo.setValue(Integer.toString((vo.getIrow()) + 1));
		} else {
			super.setDefaultValue(vo, nsrsbh, reportvo);
		}
	}

	@Override
	protected void setSpecialValue(TaxPosContrastVO vo) throws DZFWarpException {
		if ("10412001".equals(vo.getReportcode())) {// A200000中华人民共和国企业所得税月（季）度预缴纳税申报表（A类）
			if ("sbqylx".equals(vo.getItemkey())) {// 企业类型
				// 企业类型：0一般企业|1跨地区经营汇总纳税企业总机构|2跨地区经营汇总纳税企业分支机构
				if (!StringUtil.isEmpty(vo.getValue())) {
					if ("一般企业".equals(vo.getValue())) {
						vo.setValue("1");
					} else if ("跨地区经营汇总纳税企业总机构".equals(vo.getValue())) {
						vo.setValue("2");
					} else if ("跨地区经营汇总纳税企业分支机构".equals(vo.getValue())) {
						vo.setValue("3");
					}
				}
			} else if ("yjfs".equals(vo.getItemkey())) {// 预缴方式
				// 预缴方式：1按照实际利润额预缴|2按照上一纳税年度应纳税所得额平均额预缴|3按照税务机关确定的其他方法预缴
				if (!StringUtil.isEmpty(vo.getValue())) {
					if ("按照实际利润额预缴".equals(vo.getValue())) {
						vo.setValue("0");
					} else if ("按照上一纳税年度应纳税所得额平均额预缴".equals(vo.getValue())) {
						vo.setValue("1");
					} else if ("按照税务机关确定的其他方法预缴".equals(vo.getValue())) {
						vo.setValue("2");
					}
				}
			} else if ("sfsyxxwlqy".equals(vo.getItemkey())) {// 是否属于小型微利企业
				setYesOrNo(vo);
			} else if ("sfkjxzxqy".equals(vo.getItemkey())) {// 是否科技型中小企业
				setYesOrNo(vo);
			} else if ("sfgxjsqy".equals(vo.getItemkey())) {// 是否高新技术企业
				setYesOrNo(vo);
			} else if ("sffsjsrgdynssx".equals(vo.getItemkey())) {// 是否技术入股递延纳税事项\
				setYesOrNo(vo);
			} else if ("gjxzhjzhy".equals(vo.getItemkey())) {// 国家限制或禁止行业
				setYesOrNo(vo);
			}
		}
	}

	@Override
	public TaxQcQueryVO getTaxQcQueryVO(TaxReportVO reportvo) throws DZFWarpException {
		TaxQcQueryVO qcvo = super.getTaxQcQueryVO(reportvo);
		qcvo.setYwlx(TaxConst.SERVICE_CODE_QYSDSA);
		qcvo.setXmlType(TaxConst.XMLTYPE_QYSDSA);
		qcvo.setImpl(TaxConst.IMPLVALUE);
		qcvo.setIsSend(DZFBoolean.TRUE);
		qcvo.setYzpzzlDm(TaxConst.CODE_QYSDSA);
		return qcvo;
	}

	protected void setQcvo(TaxQcQueryVO qcvo) {
		qcvo.setIsConQc(DZFBoolean.TRUE);
		qcvo.setYwlx(TaxConst.SERVICE_CODE_QYSDSAINITNEW);
		qcvo.setZgswjDm(TaxConst.SDS_ZGSWJDM);
		qcvo.setZgswkfjDm(TaxConst.SDS_ZGSWKFJDM);
		qcvo.setYzpzzlDm(TaxConst.SDS_YZPZZLDM_A);
		qcvo.setYzpzzldm_and(TaxConst.SDS_YZPZZLDM_AND);
		qcvo.setYzpzzldm_ayjd(TaxConst.SDS_YZPZZLDM_AYJD);
		qcvo.setYzpzzldm_bnd(TaxConst.SDS_YZPZZLDM_BND);
		qcvo.setYzpzzldm_byjd(TaxConst.SDS_YZPZZLDM_BYJD);
		qcvo.setImpl(null);
		InitFiledMapParse intParse = new EnterpriseIncomeTaxAInitMappingNew();
		qcvo.setIntParse(intParse);
	}

	protected String getyjSbbw(CorpVO corpVO, Map objMapReport, SpreadTool spreadtool, TaxReportVO reportvo,
							   TaxQcQueryVO qcvo) {
		String yjsbBwXml = super.getyjSbbw(corpVO, objMapReport, spreadtool, reportvo, qcvo);
		// base64转码
//		System.out.println(yjsbBwXml);
		try {
			yjsbBwXml = Base64.encode(yjsbBwXml.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new BusinessException(e.getMessage());
		}
		return yjsbBwXml;
	}

	protected String getSb_zlbh(TaxReportVO reportvo) {
		return SDTaxConst.TEMPLET_SBZLBH10412;
	}

	@Override
	protected List<String> getNullRowPk(Map<String, List<TaxPosContrastVO>> map) {
		List<String> relist = new ArrayList<>();
		for (Map.Entry<String, List<TaxPosContrastVO>> entry : map.entrySet()) {
			boolean isAllNull = false;
			List<TaxPosContrastVO> clist1 = entry.getValue();
			for (TaxPosContrastVO vo : clist1) {
				if ("10412005".equals(vo.getReportcode())) {// A202000企业所得税汇总纳税分支机构所得税分配表
					// 纳税人识别号 分支机构名称
					if ("fzjgnsrsbh".equals(vo.getItemkey()) || "fzjgmc".equals(vo.getItemkey())) {
						if (StringUtil.isEmpty(vo.getValue())) {
							isAllNull = true;
							break;
						}
					}
				}
			}
			if (isAllNull)
				relist.add(entry.getKey());
		}
		return relist;
	}

	@Override
	protected String getQcXML(CorpVO corpvo, CorpTaxVo taxvo, TaxQcQueryVO qcvo) {
		String yjsbBwXml = XMLUtils.createQcXMLNew1(corpvo.getVsoccrecode(), taxvo.getVstatetaxpwd(),
				Integer.toString(new DZFDate().getYear()), qcvo);
		return yjsbBwXml;
	}
}
