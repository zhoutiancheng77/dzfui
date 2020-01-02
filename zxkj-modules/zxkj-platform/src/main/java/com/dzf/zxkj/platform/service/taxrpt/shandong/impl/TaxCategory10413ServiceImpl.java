package com.dzf.zxkj.platform.service.taxrpt.shandong.impl;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxPosContrastVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.taxrpt.shandong.EnterpriseIncomeTaxBInitMapping;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxConst;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxQcQueryVO;
import com.dzf.zxkj.platform.service.taxrpt.shandong.InitFiledMapParse;
import com.dzf.zxkj.platform.service.taxrpt.shandong.SDTaxConst;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.Base64;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.ParseXml;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//所得税季度纳税申报表(B类),不区分一般人和小规模
@Service("taxcateserv_10413")
public class TaxCategory10413ServiceImpl extends DefaultTaxCategoryServiceImpl {
 
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
		if ("10413001".equals(vo.getReportcode())) {// 主表
			if ("sfsyxxwlqy".equals(vo.getItemkey())) {// 是否属于小型微利企业
				setYesOrNo(vo);
			} else if ("gjxzhjzhy".equals(vo.getItemkey())) {// 国家限制和禁止行业
				setYesOrNo(vo);
			}
		} else if ("10413002".equals(vo.getReportcode())) {// 居民企业参股外国企业信息报告表
			if ("cglx".equals(vo.getItemkey())) {// 持股类型
				setSpecialNum(vo);
			} else if ("qyfeqsrq".equals(vo.getItemkey())) {// 权益份额的起始日期
				setDateData(vo);
			} else if ("qyfeqsrq".equals(vo.getItemkey())) {// 权益份额的起始日期
				setDateData(vo);
			} else if ("sfzjlx".equals(vo.getItemkey())) {// 身份证件类型
				setSpecialNum(vo);
			} else if ("rzrqq".equals(vo.getItemkey())) {// 任职日期起
				setDateData(vo);
			} else if ("rzrqz".equals(vo.getItemkey())) {// 任职日期止
				setDateData(vo);
			} else if ("bsggflx".equals(vo.getItemkey())) {// 被收购股份类型
				setSpecialNum(vo);
			} else if ("jyrq".equals(vo.getItemkey())) {// 交易日期
				setDateData(vo);
			} else if ("bczgflx".equals(vo.getItemkey())) {// 被处置股份类型
				setSpecialNum(vo);
			} else if ("czrq".equals(vo.getItemkey())) {// 处置日期
				setDateData(vo);
			}
		}
	}

	@Override
	public TaxQcQueryVO getTaxQcQueryVO(TaxReportVO reportvo) throws DZFWarpException {
		TaxQcQueryVO qcvo = super.getTaxQcQueryVO(reportvo);
		qcvo.setYwlx(TaxConst.SERVICE_CODE_QYSDSB);
		qcvo.setXmlType(TaxConst.XMLTYPE_QYSDSB);
		qcvo.setImpl(TaxConst.IMPLVALUE);
		qcvo.setIsSend(DZFBoolean.TRUE);
		qcvo.setYzpzzlDm(TaxConst.CODE_QYSDSB);
		return qcvo;
	}

	protected void setQcvo(TaxQcQueryVO qcvo) {
		qcvo.setIsConQc(DZFBoolean.TRUE);
		qcvo.setYwlx(TaxConst.SERVICE_CODE_QYSDSBINIT);
		InitFiledMapParse intParse = new EnterpriseIncomeTaxBInitMapping();
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
		return SDTaxConst.TEMPLET_SBZLBH10413;
	}
	
	@Override
	protected List<String> getNullRowPk(Map<String, List<TaxPosContrastVO>> map) {
		List<String> relist = new ArrayList<>();
		for (Entry<String, List<TaxPosContrastVO>> entry : map.entrySet()) {
			boolean isAllNull = false;
			List<TaxPosContrastVO> clist1 = entry.getValue();
			for (TaxPosContrastVO vo : clist1) {
				if ("10413002".equals(vo.getReportcode())) {// 居民企业参股外国企业信息报告表
					// 持股股东中文名称 中国居民个人姓名 被收购股份类型 被处置股份类型
					if ("cggdzwmc".equals(vo.getItemkey()) || "zgmjgrxm".equals(vo.getItemkey())
							|| "bsggflx".equals(vo.getItemkey()) || "bczgflx".equals(vo.getItemkey())) {
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
	
	/**
	 * 解析期初数据为Map
	 * 
	 * @param data
	 * @param reportvo
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
								String text = (String) ParseXml.getValueByName((DefaultElement)((DefaultElement) item).getParent(), entry.getValue());
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
}
