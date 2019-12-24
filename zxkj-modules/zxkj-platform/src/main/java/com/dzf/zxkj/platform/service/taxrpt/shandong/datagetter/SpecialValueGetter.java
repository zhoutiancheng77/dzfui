package com.dzf.zxkj.platform.service.taxrpt.shandong.datagetter;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.TaxRptConst;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.tax.TaxPosContrastVO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class SpecialValueGetter {

	private static Map<String, String> map = null;
	static {
		if (map == null) {
			map = new HashMap<>();
			map.put("10-企业会计制度", "0");
			map.put("20-小企业会计准则", "0");
			map.put("41-事业单位会计准则-事业单位会计制度", "0");
			map.put("42-事业单位会计准则-科学事业单位会计制度", "1");
			map.put("43-事业单位会计准则-医院会计制度", "2");
			map.put("44-事业单位会计准则-高等学校会计制度", "3");
			map.put("45-事业单位会计准则-中小学校会计制度", "4");
			map.put("46-事业单位会计准则-彩票机构会计制度", "5");
			map.put("51-企业会计准则-一般企业", "0");
			map.put("52-企业会计准则-银行", "1");
			map.put("53-企业会计准则-证券", "2");
			map.put("54-企业会计准则-保险", "3");
			map.put("55-企业会计准则-担保", "4");
			map.put("60-民间非营利组织会计制度", "0");
			map.put("70-村集体经济组织会计制度", "0");
			map.put("80-农民专业合作社财务会计制度（试行）", "0");
			map.put("99-其他", "0");

		}
	}

	public static String getSpecialValue(TaxPosContrastVO vo) {

		if (TaxRptConst.SB_ZLBH_SETTLEMENT.equals(vo.getSbzlbh())) { // 企业所得税年报
			getSpecialValueA(vo);
		} else if (TaxRptConst.SB_ZLBH10102.equals(vo.getSbzlbh())) {// 增值税小规模
			getSpecialValue10102(vo);
		} else if (TaxRptConst.SB_ZLBH10101.equals(vo.getSbzlbh())) {// 增值税一般纳税人
			getSpecialValue10101(vo);
		} else if (TaxRptConst.SB_ZLBHC2.equals(vo.getSbzlbh())) {// 一般企业财报
			getSpecialValueC2(vo);
		} else if (TaxRptConst.SB_ZLBH10412.equals(vo.getSbzlbh())) {// 企业所得税A
			getSpecialValue10412(vo);
		} else if (TaxRptConst.SB_ZLBH10413.equals(vo.getSbzlbh())) {// 企业所得税B
			getSpecialValue10413(vo);
		}
		return vo.getValue();
	}

	private static void getSpecialValueA(TaxPosContrastVO vo) {
		if ("A000000".equals(vo.getReportcode())) {// A000000企业基础信息表
			if ("hznsqy".equals(vo.getItemkey())) {// 101汇总纳税企业
				if (!StringUtil.isEmpty(vo.getValue())) {

					if ("0".equals(vo.getValue().substring(0, 1))) {
						vo.setValue("2");
					} else if ("1".equals(vo.getValue().substring(0, 1))) {
						vo.setValue("0");
					} else if ("2".equals(vo.getValue().substring(0, 1))) {
						vo.setValue("1");
					}
				}
			} else if ("jwzzkgjmqy".equals(vo.getItemkey())) {// 106境外中资控股居民企业
				if (!StringUtil.isEmpty(vo.getValue())) {
					vo.setValue(vo.getValue().substring(0, 1));
				}
			} else if ("csgjfxzhjzhy".equals(vo.getItemkey())) {// 107纳税人从事国家限制或禁止行业
				if (!StringUtil.isEmpty(vo.getValue())) {
					vo.setValue(vo.getValue().substring(0, 1));
				}
			} else if ("czjwgljy".equals(vo.getItemkey())) {// 108存在境外关联交易
				if (!StringUtil.isEmpty(vo.getValue())) {
					vo.setValue(vo.getValue().substring(0, 1));
				}
			} else if ("ssgs".equals(vo.getItemkey())) {// 109上市公司
				if (!StringUtil.isEmpty(vo.getValue())) {
					vo.setValue(vo.getValue().substring(0, 1));
				}
			} else if ("qykjzz".equals(vo.getItemkey())) {// 企业会计准则 一般企业 银行 证券
															// 保险 担保
				if (!StringUtil.isEmpty(vo.getValue())) {
					if (vo.getValue().contains("企业会计准则-")) {
						vo.setValue(map.get(vo.getValue()));
					} else {
						vo.setValue("**");
					}
				}
			} else if ("xqykjzz".equals(vo.getItemkey())) {// 小企业会计准则
				if (!StringUtil.isEmpty(vo.getValue())) {
					if ("20-小企业会计准则".equals(vo.getValue())) {
						vo.setValue(map.get(vo.getValue()));
					} else {
						vo.setValue("**");
					}
				}
			} else if ("qykjzd".equals(vo.getItemkey())) {// 企业会计制度
				if (!StringUtil.isEmpty(vo.getValue())) {
					if ("10-企业会计制度".equals(vo.getValue())) {
						vo.setValue(map.get(vo.getValue()));
					} else {
						vo.setValue("**");
					}
				}
			} else if ("sydwkjzz".equals(vo.getItemkey())) {// 事业单位会计准则 事业单位会计制度
															// 科学事业单位会计制度 医院会计制度
															// 高等学校会计制度 中小学校会计制度
															// 彩票机构会计制度
				if (!StringUtil.isEmpty(vo.getValue())) {
					if (vo.getValue().contains("事业单位会计准则")) {
						vo.setValue(map.get(vo.getValue()));
					} else {
						vo.setValue("**");
					}
				}
			} else if ("mjfylzzkjzd".equals(vo.getItemkey())) {// 民间非营利组织会计制度
					if (!StringUtil.isEmpty(vo.getValue())) {
						if ("60-民间非营利组织会计制度".equals(vo.getValue())) {
							vo.setValue(map.get(vo.getValue()));
						} else {
							vo.setValue("**");
						}
					}
			} else if ("cjtjjzzkjzd".equals(vo.getItemkey())) {// 村集体经济组织会计制度
					if (!StringUtil.isEmpty(vo.getValue())) {
						if ("70-村集体经济组织会计制度".equals(vo.getValue())) {
							vo.setValue(map.get(vo.getValue()));
						} else {
							vo.setValue("**");
						}
					}
			} else if ("nmzyhzscwkjzd".equals(vo.getItemkey())) {// 农民专业合作社财务会计制度（试行
				if (!StringUtil.isEmpty(vo.getValue())) {
					if ("80-农民专业合作社财务会计制度（试行）".equals(vo.getValue())) {
						vo.setValue(map.get(vo.getValue()));
					} else {
						vo.setValue("**");
					}
				}
			} else if ("kjzdqt".equals(vo.getItemkey())) {// 其他
				if (!StringUtil.isEmpty(vo.getValue())) {
					if ("99-其他".equals(vo.getValue())) {
						vo.setValue(map.get(vo.getValue()));
					} else {
						vo.setValue("**");
					}
				}
			} else if ("kjjzbb".equals(vo.getItemkey())) {// 204记账本位币
				if (!StringUtil.isEmpty(vo.getValue())) {

					if ("Y".equals(vo.getValue().substring(0, 1))) {
						vo.setValue("0");
					} else {
						vo.setValue("9");
					}
				}
			} else if ("kjzchgjsffsbh".equals(vo.getItemkey())) {// 205会计政策和估计是否发生变化
					if (!StringUtil.isEmpty(vo.getValue())) {
						vo.setValue(vo.getValue().substring(0, 1));
					}
			} else if ("hzsshsff".equals(vo.getItemkey())) {// 208坏账损失核算方法
				if (!StringUtil.isEmpty(vo.getValue())) {
					if ("Y".equals(vo.getValue().substring(0, 1))) {
						vo.setValue("0");
					} else {
						vo.setValue("1");
					}
				}
			} else if ("sdsjsff".equals(vo.getItemkey())) {// 209所得税计算方法
				if (!StringUtil.isEmpty(vo.getValue())) {
					if ("2".equals(vo.getValue().substring(0, 1))) {
						vo.setValue("9");
					} else {
						vo.setValue(vo.getValue().substring(0, 1));
					}
				}
			} else if ("sfzjzlDm".equals(vo.getItemkey())) {// 证件种类
				setSpecialNum(vo);
			} else if ("jjxz".equals(vo.getItemkey())) {// 经济性质
				setSpecialNum(vo);
			} else if ("gjdqDm".equals(vo.getItemkey())) {// 国籍（注册地址）
				setSpecialNum(vo);
			} else if ("kjjzbb".equals(vo.getItemkey())) {// 204记账本位币
				if (!StringUtil.isEmpty(vo.getValue())) {
					vo.setValue(vo.getValue().substring(0, 1));
				}
			}
		} else if ("A200400".equals(vo.getReportcode())) {// 居民企业资产（股权）划转特殊性税务处理申报表
			if ("jdfs".equals(vo.getItemkey())) {// 借/贷方式“0”借“1”贷
				if (!StringUtil.isEmpty(vo.getValue())) {
					if ("借".equals(vo.getValue())) {
						vo.setValue("0");
					} else if ("贷".equals(vo.getValue())) {
						vo.setValue("1");
					} else {
						vo.setValue("**");
					}

				}
			} else if ("hzwcr".equals(vo.getItemkey())) {// 划转完成日
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			} else if ("kgqssj".equals(vo.getItemkey())) {// 100%控股起始时间
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			} else if ("hcfcgqssj".equals(vo.getItemkey())) {// 划出方持股起始时间
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			} else if ("hcfcgqssj".equals(vo.getItemkey())) {// 划入方持股起始时间
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			} else if ("mgs".equals(vo.getItemkey())) {// 母公司是：“1”划出方;"2"划入方
				if (!StringUtil.isEmpty(vo.getValue())) {
					if ("划出方".equals(vo.getValue())) {
						vo.setValue("1");
					} else if ("划入方".equals(vo.getValue())) {
						vo.setValue("2");
					}
				}
			} else if (vo.getItemkey().equals("hzsfgx")) {// 划转双方关系,“1”关系1：100%直接控制的母子公司，“关系2：受同一或相同多家居民企业100%直接控制”关系2：受同一或相同多家居民企业100%直接控制
				if (StringUtil.isEmpty(vo.getValue())) {
					throw new BusinessException("请检查表编码" + vo.getReportcode() + ",划转双方关系没有选择");
				}
			}
		} else if ("A106000".equals(vo.getReportcode())) {// 居民企业资产（股权）划转特殊性税务处理申报表
			if (vo.getItemkey().startsWith("nd")) {//
				if (!StringUtil.isEmpty(vo.getValue())) {
					vo.setValue(vo.getValue().substring(0, 4));
				}
			}
		} else if ("A107050".equals(vo.getReportcode())) {// 居民企业资产（股权）划转特殊性税务处理申报表
			if (vo.getItemkey().startsWith("nd") || vo.getItemkey().equals("bnd")) {//
				if (!StringUtil.isEmpty(vo.getValue())) {
					vo.setValue(vo.getValue().substring(0, 4));
				}
			}
		} else if ("A107011".equals(vo.getReportcode())) {
			if (vo.getItemkey().equals("tzxz")) {// 投资性质
				setSpecialNum(vo);
			} else if (vo.getItemkey().equals("btzlrfpqrjejdsj")) {// 被投资企业做出利润分配或转股决定时间
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			}
		} else if ("A107012".equals(vo.getReportcode())) {
			if (vo.getItemkey().equals("zyzhlyrdzsqdsj")) {// _《资源综合利用认定证书》取得时间
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			} else if (vo.getItemkey().equals("zyzhlyrdzsyxq")) {// 《资源综合利用认定证书》有效期起
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			} else if (vo.getItemkey().equals("zyzhlyrdzsyxqz")) {// 《资源综合利用认定证书》有效期止
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			} else if (vo.getItemkey().equals("syyhmllb")) {// 属于《资源综合利用企业所得税优惠目录》类别
				if (!StringUtil.isEmpty(vo.getValue())) {
					vo.setValue("0" + vo.getValue().split("-")[0]);
				}
			} else if (vo.getItemkey().equals("yhmlgddbz")) {// 《资源综合利用企业所得税优惠目录》规定的标准
				setSpecialNum(vo);
			}
		} else if ("A107041".equals(vo.getReportcode())) {// A107041高新技术企业优惠情况及明细表
			if (vo.getItemkey().equals("sffszdaqzlsg")) {// 是否发生重大安全事故
				setZeroOrOne(vo);
			} else if (vo.getItemkey().equals("sfyhjwfwgxw")) {// 是否环境等违法、违规行为
				setZeroOrOne(vo);
			} else if (vo.getItemkey().equals("sffstpsxw")) {// 是否发生偷骗税行为
				setZeroOrOne(vo);
			} else if (vo.getItemkey().equals("gxjsqyzsqdsj")) {// 高新技术企业证书取得时间
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			}
		} else if ("A107042".equals(vo.getReportcode())) {// A107042软件、集成电路企业优惠情况及明细表
			if (vo.getItemkey().equals("qyclrq")) {// 企业成立日期
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			} else if (vo.getItemkey().equals("rjqyzsqerq")) {// 软件企业证书取得日期
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			}
		} else if ("A108000".equals(vo.getReportcode())) {// A108000境外所得税收抵免明细表
			if (vo.getItemkey().equals("gjdq")) {// 国家（地区）
				setSpecialNum(vo);
			}
		} else if ("A108010".equals(vo.getReportcode())) {// A108010境外所得纳税调整后所得明细表
			if (vo.getItemkey().equals("gjdq")) {// 国家（地区）
				setSpecialNum(vo);
			}
		} else if ("A108020".equals(vo.getReportcode())) {// A108020境外分支机构弥补亏损明细表
			if (vo.getItemkey().equals("gjdq")) {// 国家（地区）
				setSpecialNum(vo);
			}
		} else if ("A108030".equals(vo.getReportcode())) {// A108030跨年度结转抵免境外所得税明细表
			if (vo.getItemkey().equals("gjdq")) {// 国家（地区）
				setSpecialNum(vo);
			}
		} else if ("A105080".equals(vo.getReportcode())) {//
			if (vo.getItemkey().equals("nstzyy")) {// 调整原因||调整原因
				if (!StringUtil.isEmpty(vo.getValue())) {
					// 折旧年限、折旧方法、计提原值
					String value = vo.getValue();
					value = value.replace("折旧年限", "A");
					value = value.replace("折旧方法", "B");
					value = value.replace("计提原值", "C");
					value = value.replace("、", ",");
					vo.setValue(value);
				}
			}
		} else if ("A200300".equals(vo.getReportcode())) {//
			if (vo.getItemkey().equals("sfwglqy")) {// 与投资方是否为关联企业||与投资方是否为关联企业,Y是,N否
				setZeroOrOne(vo);
			} else if (vo.getItemkey().equals("jsjcsfbcbb")) {// 被收购企业原有各项资产和负债的计税基础是否保持不变
				setZeroOrOne(vo);
			}
		} else if ("A200500".equals(vo.getReportcode())) {// 受控外国企业信息报告表
			if (vo.getItemkey().equals("skwgqygctj")) {// 三、受控外国企业构成条件
				if (!StringUtil.isEmpty(vo.getValue())) {
					if ("持股比例".equals(vo.getValue())) {
						vo.setValue("01");
					} else if ("实质控制".equals(vo.getValue())) {
						vo.setValue("02");
					}
				}
			} else if (vo.getItemkey().equals("skwgqysfzgjswzjzddfdslgj")) {// 受控外国企业是否在国家税务总局指定的非低税率国家
				setYesOrNo(vo);
			} else if (vo.getItemkey().equals("skwgqyndlrsfbgy500wyrmb")) {// 受控外国企业年度利润是否不高于500万元人民币
				setYesOrNo(vo);
			} else if (vo.getItemkey().equals("skwgqyzyqdjjjyhdsd")) {// 受控外国企业主要取得积极经营活动所得
				setYesOrNo(vo);
			} else if (vo.getItemkey().equals("skwgqynsndq")) {// 受控外国企业纳税年度起
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			} else if (vo.getItemkey().equals("skwgqynsndz")) {// 受控外国企业纳税年度止
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			} else if (vo.getItemkey().equals("cyskwgqyzgjmgdzjqssj")) {// 持有受控外国企业股份的中国居民股东直接起始时间
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			} else if (vo.getItemkey().equals("cyskwgqyzgjmgdzjzzsj")) {// 持有受控外国企业股份的中国居民股东直接终止时间
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			} else if (vo.getItemkey().equals("cyskwgqyzgjmgdjjqssj")) {// 持有受控外国企业股份的中国居民股东持股信息间接起始时间
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			} else if (vo.getItemkey().equals("cyskwgqyzgjmgdjjzzsj")) {// 持有受控外国企业股份的中国居民股东持股信息间接终止时间
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			} else if (vo.getItemkey().equals("skwgqyclsj")) {// 受控外国企业成立时间
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			} else if (vo.getItemkey().equals("skwgqyjzbwhb")) {// 受控外国企业记账本位货币
				if (!StringUtil.isEmpty(vo.getValue())) {
					vo.setValue(vo.getValue().split("-")[0]);
				}
			}

		} else if ("A200200".equals(vo.getReportcode())) {// 企业重组所得税特殊性税务处理报告表
			if (vo.getItemkey().equals("flxsgb")) {// 法律形式改变 码值为1
				if (!StringUtil.isEmpty(vo.getValue())) {
					if ("法律形式改变".equals(vo.getValue())) {
						vo.setValue("1");
					} else {
						vo.setValue("**");
					}
				}
			} else if (vo.getItemkey().equals("zwcz")) {// 债务重组,债务人码值为2 债权人码值为3
				if (!StringUtil.isEmpty(vo.getValue())) {
					if ("债务人".equals(vo.getValue())) {
						vo.setValue("2");
					} else if ("债权人".equals(vo.getValue())) {
						vo.setValue("3");
					} else {
						vo.setValue("**");
					}
				}
			} else if (vo.getItemkey().equals("gqsg")) {// 股权收购
														// 收购方码值为4，转让方码值5，被收购企业码值6
				if (!StringUtil.isEmpty(vo.getValue())) {
					if ("收购方（股权收购）".equals(vo.getValue())) {
						vo.setValue("4");
					} else if ("转让方（股权收购）".equals(vo.getValue())) {
						vo.setValue("5");
					} else if ("被收购企业".equals(vo.getValue())) {
						vo.setValue("6");
					} else {
						vo.setValue("**");
					}
				}
			} else if (vo.getItemkey().equals("zcsg")) {// 资产收购 收购方码值为7，转让方码值为8
				if (!StringUtil.isEmpty(vo.getValue())) {
					if ("收购方（资产收购）".equals(vo.getValue())) {
						vo.setValue("7");
					} else if ("转让方（资产收购）".equals(vo.getValue())) {
						vo.setValue("8");
					} else {
						vo.setValue("**");
					}
				}
			} else if (vo.getItemkey().equals("hb")) {// 合并
														// 合并企业码值为9，被合并企业码值为10被合并企业股东11
				if (!StringUtil.isEmpty(vo.getValue())) {
					if ("合并企业".equals(vo.getValue())) {
						vo.setValue("9");
					} else if ("被合并企业".equals(vo.getValue())) {
						vo.setValue("10");
					} else if ("被合并企业股东".equals(vo.getValue())) {
						vo.setValue("11");
					} else {
						vo.setValue("**");
					}
				}
			} else if (vo.getItemkey().equals("fenl")) {// 分立 分立企业码值12 被分立企业码值13
				if (!StringUtil.isEmpty(vo.getValue())) {
					if ("分立企业".equals(vo.getValue())) {
						vo.setValue("12");
					} else if ("被分立企业".equals(vo.getValue())) {
						vo.setValue("13");
					} else if ("被分立企业股东".equals(vo.getValue())) {
						vo.setValue("14");
					} else {
						vo.setValue("**");
					}
				}
			} else if (vo.getItemkey().equals("tsxswcltjsymd")) {// 具有合理的商业目的，且不以减少、免除或者推迟缴纳税款为主要目的。
				setOneOrZero(vo);
			} else if (vo.getItemkey().equals("tsxswcltjbsghbflbz")) {// 被收购、合并或分立部分的资产或股权比例符合规定的比例
				setOneOrZero(vo);
			} else if (vo.getItemkey().equals("tsxswcltjqyczhd")) {// 企业重组后的连续12个月内不改变重组资产原来的实质性经营
				setOneOrZero(vo);
			} else if (vo.getItemkey().equals("tsxswcltjczjydjbz")) {// 重组交易对价中涉及股权支付金额符合规定
				setOneOrZero(vo);
			} else if (vo.getItemkey().equals("tsxswcltjgqzf")) {// 企业重组中取得股权支付的原主要股东，在重组后连续12个月内，不得转让所取得的股权。
				setOneOrZero(vo);
			} else if (vo.getItemkey().equals("czri")) {// 重组日
				setYesOrNo(vo);
			}
		} else if ("A200240".equals(vo.getReportcode())) {// 企业重组所得税特殊性税务处理报告表（合并）

			if (vo.getItemkey().equals("sftykzbxyzwdjdhb")) {// 是否为同一控制下且不需要支付对价的合并
				setSpecialNum(vo);
			}
		} else if ("A200210".equals(vo.getReportcode())) {// 企业重组所得税特殊性税务处理报告表

			if (vo.getItemkey().equals("zwczfs")) {// 债务重组方式
				if (!StringUtil.isEmpty(vo.getValue())) {
					if ("重组所得超过应纳所得额50％".equals(vo.getValue())) {
						vo.setValue("1");
					} else {
						vo.setValue("2");
					}
				}
			}
		} else if ("A200220".equals(vo.getReportcode())) {// 企业重组所得税特殊性税务处理报告表(股权收购)

			if (vo.getItemkey().equals("sfwglqy")) {// 与投资方是否为关联企业||与投资方是否为关联企业
				setZeroOrOne(vo);
			} else if (vo.getItemkey().equals("jsjcsfbcbb")) {// 被收购企业原有各项资产和负债的计税基础是否保持不变
				setZeroOrOne(vo);
			} else if (vo.getItemkey().equals("gqzrhtsxr")) {// 股权转让合同（协议）生效日
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			} else if (vo.getItemkey().equals("gsbgdjr")) {// 股权收购方所收购股权的工商变更登记日
				if (!StringUtil.isEmpty(vo.getValue())) {
					setDateData(vo);
				}
			}
		} else if ("A105081".equals(vo.getReportcode())) {// A105081固定资产加速折旧、扣除明细表(新版本)

			if (vo.getItemkey().equals("hyDm")) {// 是否存在资产收购涉及项目所得的税收优惠承继
				setSpecialNum(vo);
			} else if (vo.getItemkey().equals("qyLx")) {
				setSpecialNum(vo);
			}
		} else if ("A107040".equals(vo.getReportcode())) {// A107040减免所得税优惠明细表(新版本)

			if (vo.getItemkey().equals("qt1")) {// 其他1(减免性质代码)
				setSpecialNum(vo);
			} else if (vo.getItemkey().equals("qt2")) {// 其他2(减免性质代码)
				setSpecialNum(vo);
			} else if (vo.getItemkey().equals("qt3")) {// 其他3(减免性质代码)
				setSpecialNum(vo);
			}
		}
	}

	private static void setDateData(TaxPosContrastVO vo) {
		try {
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

	private static void getSpecialValue10102(TaxPosContrastVO vo) {
		if ("10102004".equals(vo.getReportcode())) {// 增值税减免税申报明细表
			if ("hmc".equals(vo.getItemkey())) {// 减税行名称 免税行名称
				setSpecialNum(vo);
			}
		}
	}

	private static void getSpecialValue10101(TaxPosContrastVO vo) {
		if ("10101021".equals(vo.getReportcode())) {// 增值税减免税申报明细表
			if ("hmc".equals(vo.getItemkey())) {// 减税行名称 免税行名称
				setSpecialNum(vo);
			}
		} else if ("10101009".equals(vo.getReportcode())) {// 成品油购销存情况明细表
			if ("ypxh".equals(vo.getItemkey())) {// 油品型号
				setSpecialNum(vo);
			}
		} else if ("10101002".equals(vo.getReportcode())) {// 增值税纳税申报表附列资料（一）
			// 13a(原13行)填写13,13b行填写20,13c行填写21,营改增新增9b行填写22)
			if ("ewbhxh".equals(vo.getItemkey())) {
				if ("9a".equals(vo.getValue())) {
					vo.setValue("9");
				} else if ("9b".equals(vo.getValue())) {
					vo.setValue("22");
				} else if ("13a ".equals(vo.getValue())) {
					vo.setValue("13");
				} else if ("13b ".equals(vo.getValue())) {
					vo.setValue("20");
				} else if ("13c ".equals(vo.getValue())) {
					vo.setValue("21");
				} else if ("4a".equals(vo.getValue())) {
					vo.setValue("23");
				} else if ("4b".equals(vo.getValue())) {
					vo.setValue("4");
				}
			}
		} else if ("10101003".equals(vo.getReportcode())) {// 增值税纳税申报表附列资料（二）
			if ("ewbhxh".equals(vo.getItemkey())) {
				String value = vo.getValue();
				int rowno = Integer.parseInt(value);
				if (rowno > 7) {
					if (rowno == 8) {
						vo.setValue("37");
					} else {
						vo.setValue(Integer.toString(rowno - 1));
					}

				}
			}
		}
	}

	private static void getSpecialValueC2(TaxPosContrastVO vo) {
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

	private static void getSpecialValue10412(TaxPosContrastVO vo) {
		if ("10412001".equals(vo.getReportcode())) {// 所得税月(季)度纳税申报表(A类）
			if ("sfsyxxwlqy".equals(vo.getItemkey())) {// 是否属于小型微利企业
				setYesOrNo(vo);
			}
		} else if ("10412002".equals(vo.getReportcode())) {// 不征税收入和税基类减免应纳税所得额明细表（附表1）
			if ("mssrqt1".equals(vo.getItemkey())) {// 免税收入其他1(减免性质代码)
				setSpecialNum(vo);
			} else if ("mssrqt2".equals(vo.getItemkey())) {// 免税收入其他2(减免性质代码)
				setSpecialNum(vo);
			} else if ("qt1".equals(vo.getItemkey())) {// 其他1(减免性质代码)
				setSpecialNum(vo);
			} else if ("qt2".equals(vo.getItemkey())) {// 其他2(减免性质代码)
				setSpecialNum(vo);
			} else if ("qt3".equals(vo.getItemkey())) {// 其他3(减免性质代码)
				setSpecialNum(vo);
			}
		} else if ("10412003".equals(vo.getReportcode())) {// 固定资产加速折旧(扣除)明细表（附表2）
			if ("hmc".equals(vo.getItemkey())) {// 行名称
				setSpecialNum(vo);
			}
		} else if ("10412004".equals(vo.getReportcode())) {// 减免所得税优惠明细表(附表3)
			if ("qtzxyhqt1".equals(vo.getItemkey())) {// 其他专项优惠其他1(减免性质代码)
				setSpecialNum(vo);
			} else if ("qtzxyhqt2".equals(vo.getItemkey())) {// 其他专项优惠其他2(减免性质代码)
				setSpecialNum(vo);
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

	private static void getSpecialValue10413(TaxPosContrastVO vo) {
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

	private static void setSpecialNum(TaxPosContrastVO vo) {
		if (!StringUtil.isEmpty(vo.getValue())) {
			if (vo.getValue().contains("|")) {
				int index = vo.getValue().indexOf("|");
				vo.setValue(vo.getValue().substring(0, index));
			} else if (vo.getValue().contains("_")) {
				vo.setValue(vo.getValue().split("_")[0]);
			} else if (vo.getValue().contains("-")) {
				vo.setValue(vo.getValue().split("-")[0]);
			}
		}
	}

	private static void setYesOrNo(TaxPosContrastVO vo) {
		if (!StringUtil.isEmpty(vo.getValue())) {
			if ("是".equals(vo.getValue())) {
				vo.setValue("Y");
			} else {
				vo.setValue("N");
			}
		}
	}

	private static void setZeroOrOne(TaxPosContrastVO vo) {
		if (!StringUtil.isEmpty(vo.getValue())) {
			if ("是".equals(vo.getValue())) {
				vo.setValue("0");
			} else {
				vo.setValue("1");
			}
		}
	}

	private static void setOneOrZero(TaxPosContrastVO vo) {
		if (!StringUtil.isEmpty(vo.getValue())) {
			if ("是".equals(vo.getValue())) {
				vo.setValue("1");
			} else {
				vo.setValue("0");
			}
		}
	}

}
