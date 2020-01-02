package com.dzf.zxkj.platform.service.taxrpt.shandong.impl;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxPosContrastVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxConst;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxQcQueryVO;
import com.dzf.zxkj.platform.model.taxrpt.shandong.VatOrdinaryInitMapping;
import com.dzf.zxkj.platform.service.taxrpt.shandong.InitFiledMapParse;
import com.dzf.zxkj.platform.service.taxrpt.shandong.SDTaxConst;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.Base64;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//文化事业建设费
@Service("taxcateserv_10601")
public class TaxCategory10601ServiceImpl extends DefaultTaxCategoryServiceImpl {

	@Override
	protected void setDefaultValue(TaxPosContrastVO vo, String nsrsbh, TaxReportVO reportvo) throws DZFWarpException {
		if ("#row".equals(vo.getVdefaultvalue())) {
			vo.setValue(Integer.toString((vo.getIrow())));
		} else {
			super.setDefaultValue(vo, nsrsbh, reportvo);
		}
	}

	@Override
	protected void setSpecialValue(TaxPosContrastVO vo) throws DZFWarpException {

		if ("10601003".equals(vo.getReportcode())) {// 应税服务扣除项目清单
			if ("pzzlDm1".equals(vo.getItemkey())) {// 凭证种类代码
				setSpecialNum(vo);
			}
		}
	}

	@Override
	public TaxQcQueryVO getTaxQcQueryVO(TaxReportVO reportvo) throws DZFWarpException {
		TaxQcQueryVO qcvo = super.getTaxQcQueryVO(reportvo);
		qcvo.setYwlx(TaxConst.SERVICE_CODE_WHSYJSF);
		qcvo.setXmlType(TaxConst.XMLTYPE_WHSYJSF);
		qcvo.setImpl(TaxConst.IMPLVALUE);
		qcvo.setIsSend(DZFBoolean.FALSE);
		qcvo.setNoSendReason("文化事业建设费，系统正在调整中，暂不支持申报，请到山东电子税局平台自行申报，给您带来的不便，敬请谅解。");
		qcvo.setYzpzzlDm(TaxConst.CODE_WHSYJSF);
		return qcvo;
	}

	protected void setQcvo(TaxQcQueryVO qcvo) {
		qcvo.setYwlx(TaxConst.SERVICE_CODE_WHSYJSFINIT);
		qcvo.setIsConQc(DZFBoolean.FALSE);
		InitFiledMapParse intParse = new VatOrdinaryInitMapping();
		qcvo.setIntParse(intParse);
	}

	@Override
	protected String getyjSbbw(CorpVO corpVO, Map objMapReport, SpreadTool spreadtool, TaxReportVO reportvo,
							   TaxQcQueryVO qcvo) {
		String yjsbBwXml = super.getyjSbbw(corpVO, objMapReport, spreadtool, reportvo, qcvo);
		// base64转码
		try {
			yjsbBwXml = Base64.encode(yjsbBwXml.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new BusinessException(e.getMessage());
		}
		return yjsbBwXml;
	}

	protected String getSb_zlbh(TaxReportVO reportvo) {
		return SDTaxConst.TEMPLET_SBZLBH10601;
	}

	@Override
	protected List<String> getNullRowPk(Map<String, List<TaxPosContrastVO>> map) {
		List<String> relist = new ArrayList<>();
		for (Map.Entry<String, List<TaxPosContrastVO>> entry : map.entrySet()) {
			boolean isAllNull = false;
			List<TaxPosContrastVO> clist1 = entry.getValue();
			for (TaxPosContrastVO vo : clist1) {
				if ("10601003".equals(vo.getReportcode())) {// 应税服务减除项目清单附表
					// 持股股东中文名称 中国居民个人姓名 被收购股份类型 被处置股份类型
					if ("kpfnsrsbh".equals(vo.getItemkey())) {// 开票方纳税人识别号
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
}
