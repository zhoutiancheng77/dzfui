package com.dzf.zxkj.platform.service.taxrpt.shandong.impl;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxPosContrastVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.taxrpt.shandong.EnterpriseIncomeTaxAInitMapping;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxConst;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxQcQueryVO;
import com.dzf.zxkj.platform.service.taxrpt.shandong.InitFiledMapParse;
import com.dzf.zxkj.platform.service.taxrpt.shandong.SDTaxConst;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.Base64;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


//所得税季度纳税申报表(A类),不区分一般人和小规模
@Service("taxcateserv_10412")
public class TaxCategory10412ServiceImpl extends DefaultTaxCategoryServiceImpl {
 
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
		if ("10412001".equals(vo.getReportcode())) {// 所得税月(季)度纳税申报表(A类）
			if ("sfsyxxwlqy".equals(vo.getItemkey())) {// 是否属于小型微利企业
				setYesOrNo(vo);
			}
		} else if ("10412002".equals(vo.getReportcode())) {// 不征税收入和税基类减免应纳税所得额明细表（附表1）
			if ("mssrqt1".equals(vo.getItemkey())) {// 免税收入其他1(减免性质代码)
				setSpecialNum1(vo);
			} else if ("mssrqt2".equals(vo.getItemkey())) {// 免税收入其他2(减免性质代码)
				setSpecialNum1(vo);
			} else if ("qt1".equals(vo.getItemkey())) {// 其他1(减免性质代码)
				setSpecialNum1(vo);
			} else if ("qt2".equals(vo.getItemkey())) {// 其他2(减免性质代码)
				setSpecialNum1(vo);
			} else if ("qt3".equals(vo.getItemkey())) {// 其他3(减免性质代码)
				setSpecialNum1(vo);
			} else if ("jjsrqt".equals(vo.getItemkey())) {// 减计收入其他(减免性质代码)
				setSpecialNum1(vo);
			} else if ("sdjmqt1".equals(vo.getItemkey())) {// 所得减免其他1(减免性质代码)
				setSpecialNum1(vo);
			} else if ("sdjmqt2".equals(vo.getItemkey())) {// 所得减免其他2(减免性质代码)
				setSpecialNum1(vo);
			}
		} else if ("10412003".equals(vo.getReportcode())) {// 固定资产加速折旧(扣除)明细表（附表2）
			if ("hmc".equals(vo.getItemkey())) {// 行名称
				setSpecialNum(vo);
			} else if ("hyDm".equals(vo.getItemkey())) {// 行业代码
				setSpecialNum(vo);
			} else if ("qyLx".equals(vo.getItemkey())) {// 企业类型
				setSpecialNum(vo);
			}
		} else if ("10412004".equals(vo.getReportcode())) {// 减免所得税优惠明细表(附表3)
			if ("qtzxyhqt1".equals(vo.getItemkey())) {// 其他专项优惠其他1(减免性质代码)
				setSpecialNum1(vo);
			} else if ("qtzxyhqt2".equals(vo.getItemkey())) {// 其他专项优惠其他2(减免性质代码)
				setSpecialNum1(vo);
			}
		} else if ("10412006".equals(vo.getReportcode())) {// 居民企业参股外国企业信息报告表
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
		qcvo.setYwlx(TaxConst.SERVICE_CODE_QYSDSA);
		qcvo.setXmlType(TaxConst.XMLTYPE_QYSDSA);
		qcvo.setImpl(TaxConst.IMPLVALUE);
		qcvo.setIsSend(DZFBoolean.TRUE);
		qcvo.setYzpzzlDm(TaxConst.CODE_QYSDSA);
		return qcvo;
	}

	protected void setQcvo(TaxQcQueryVO qcvo) {
		qcvo.setIsConQc(DZFBoolean.TRUE);
		qcvo.setYwlx(TaxConst.SERVICE_CODE_QYSDSAINIT);
		InitFiledMapParse intParse = new EnterpriseIncomeTaxAInitMapping();
		qcvo.setIntParse(intParse);
	}

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
		return SDTaxConst.TEMPLET_SBZLBH10412;
	}

	// 转换中间有分隔符的字符
	private void setSpecialNum1(TaxPosContrastVO vo) {
		if (!StringUtil.isEmpty(vo.getValue())) {
			if (vo.getValue().contains("|")) {
				int index = vo.getValue().indexOf("|");
				String value = vo.getValue().substring(0, index);
				if (!StringUtil.isEmpty(value)) {
					vo.setValue(value.trim());
				}
			}
		}
	}

	@Override
	protected List<String> getNullRowPk(Map<String, List<TaxPosContrastVO>> map) {
		List<String> relist = new ArrayList<>();
		for (Map.Entry<String, List<TaxPosContrastVO>> entry : map.entrySet()) {
			boolean isAllNull = false;
			List<TaxPosContrastVO> clist1 = entry.getValue();
			for (TaxPosContrastVO vo : clist1) {
				if ("10412006".equals(vo.getReportcode())) {// 居民企业参股外国企业信息报告表
					// 持股股东中文名称 中国居民个人姓名 被收购股份类型 被处置股份类型
					if ("cggdzwmc".equals(vo.getItemkey()) || "zgmjgrxm".equals(vo.getItemkey())
							|| "bsggflx".equals(vo.getItemkey()) || "bczgflx".equals(vo.getItemkey())) {
						if (StringUtil.isEmpty(vo.getValue())) {
							isAllNull = true;
							break;
						}
					}
				} else if ("10412005".equals(vo.getReportcode())) {// 汇总纳税分支机构所得税分配表
					if ("fzjgnsrsbh".equals(vo.getItemkey())) {// 分支机构纳税人识别号
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
