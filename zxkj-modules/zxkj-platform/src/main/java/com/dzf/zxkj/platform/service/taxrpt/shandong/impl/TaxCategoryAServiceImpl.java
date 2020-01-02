package com.dzf.zxkj.platform.service.taxrpt.shandong.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.tax.TaxPosContrastVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.tax.TaxRptTempletVO;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxConst;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxQcQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


//企业所得税年度汇算清缴
@Service("taxcateserv_A")
public class TaxCategoryAServiceImpl extends DefaultTaxCategoryServiceImpl {

	@Autowired
	private SingleObjectBO sbo;

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

	protected void checkData(TaxPosContrastVO vo, String nsrsbh) throws DZFWarpException {
		if ("AAAAAAA".equals(vo.getReportcode())) {// 封面
			if ("nsrsbh".equals(vo.getItemkey())) {// 纳税人识别号
				if (isItemEmpty(vo.getValue())) {
					throw new BusinessException("表编码AAAAAAA中纳税人识别号不能空");
				} else {
					if (!vo.getValue().equals(nsrsbh)) {
						throw new BusinessException("表编码AAAAAAA中封面纳税人识别号与当前公司税人识别号不符");
					}
				}
			} else if ("skssqq".equals(vo.getItemkey())) {// 税款所属期间起
				if (isItemEmpty(vo.getValue())) {
					throw new BusinessException("表编码AAAAAAA中税款所属期间起不能空");
				} else {
					String yearstart = (new DZFDate().getYear() - 1) + "0101";
					if (!vo.getValue().equals(yearstart)) {
						throw new BusinessException("表编码AAAAAAA中税款所属期间起不是前一年公历1月1日");
					}
				}
			} else if ("skssqz".equals(vo.getItemkey())) {// 税款所属期间止
				if (isItemEmpty(vo.getValue())) {
					throw new BusinessException("表编码AAAAAAA中税款所属期间止不能为空");
				} else {
					String yearend = (new DZFDate().getYear() - 1) + "1231";
					if (!vo.getValue().equals(yearend)) {
						throw new BusinessException("表编码AAAAAAA中税款所属期间止不是前一年公历12月31日");
					}
				}
			}
		} else if ("A000000".equals(vo.getReportcode())) {// A000000企业基础信息表
			if ("hznsqy".equals(vo.getItemkey())) {// 101汇总纳税企业
				if (isItemEmpty(vo.getValue())) {
					throw new BusinessException("表编码A000000中101汇总纳税企业不能为空");
				}
			} else if ("zczb".equals(vo.getItemkey())) {// 102注册资本（万元）
				if (isItemEmpty(vo.getValue()) || "0".equals(vo.getValue())) {
					throw new BusinessException("表编码A000000中102注册资本（万元）不能为空或零");
				}
			} else if ("cyrs".equals(vo.getItemkey())) {// 104从业人数
				if (isItemEmpty(vo.getValue()) || "0".equals(vo.getValue())) {
					throw new BusinessException("表编码A000000表编码A000000101中104从业人数不能为空或零");
				}
			} else if ("zcze".equals(vo.getItemkey())) {// 105资产总额（万元）
				if (isItemEmpty(vo.getValue()) || "0".equals(vo.getValue())) {
					throw new BusinessException("表编码A000000中105资产总额（万元）不能为空或零");
				}
			} else if ("jwzzkgjmqy".equals(vo.getItemkey())) {// 106境外中资控股居民企业
				if (isItemEmpty(vo.getValue())) {
					throw new BusinessException("表编码A000000中106境外中资控股居民企业不能为空");
				}
			} else if ("csgjfxzhjzhy".equals(vo.getItemkey())) {// 107纳税人从事国家限制或禁止行业
				if (isItemEmpty(vo.getValue())) {
					throw new BusinessException("表编码A000000,107纳税人从事国家限制或禁止行业不能为空");
				}
			} else if ("czjwgljy".equals(vo.getItemkey())) {// 108存在境外关联交易
				if (isItemEmpty(vo.getValue())) {
					throw new BusinessException("表编码A000000中108存在境外关联交易不能为空");
				}
			} else if ("ssgs".equals(vo.getItemkey())) {// 109上市公司
				if (isItemEmpty(vo.getValue())) {
					throw new BusinessException("表编码A000000中109上市公司不能为空");
				}
			} else if ("kjdacfd".equals(vo.getItemkey())) {// 202会计档案的存放地
				if (isItemEmpty(vo.getValue())) {
					throw new BusinessException("表编码A000000中202会计档案的存放地不能为空");
				}
			} else if ("kjhsrjmc".equals(vo.getItemkey())) {// 203会计核算软件
				if (isItemEmpty(vo.getValue())) {
					throw new BusinessException("表编码A000000中203会计核算软件不能为空");
				}
			} else if ("kjjzbb".equals(vo.getItemkey())) {// 204记账本位币
				if (isItemEmpty(vo.getValue())) {
					throw new BusinessException("表编码A000000中204记账本位币不能为空");
				}
			} else if ("kjzchgjsffsbh".equals(vo.getItemkey())) {// 205会计政策和估计是否发生变化
				if (isItemEmpty(vo.getValue())) {
					throw new BusinessException("表编码A000000中205会计政策和估计是否发生变化不能为空");
				}
			} else if ("gdzczjff".equals(vo.getItemkey())) {// 206固定资产折旧方法
				if (isItemEmpty(vo.getValue())) {
					throw new BusinessException("表编码A000000中206固定资产折旧方法不能为空");
				}
			} else if ("chcbjjff".equals(vo.getItemkey())) {// 207存货成本计价方法
				if (isItemEmpty(vo.getValue())) {
					throw new BusinessException("表编码A000000中207存货成本计价方法不能为空");
				}
			} else if ("hzsshsff".equals(vo.getItemkey())) {// 208坏账损失核算方法
				if (isItemEmpty(vo.getValue())) {
					throw new BusinessException("表编码A000000中208坏账损失核算方法不能为空");
				}
			} else if ("sdsjsff".equals(vo.getItemkey())) {// 209所得税计算方法
				if (isItemEmpty(vo.getValue())) {
					throw new BusinessException("表编码A000000中209所得税计算方法不能为空");
				}
			} else if ("R12C2".equals(vo.getFromcell())) {// 201适用的会计准则或会计制度
				// if (isItemEmpty(vo.getValue())) {
				// throw new BusinessException("201适用的会计准则或会计制度不能为空");
				// }
			} else if ("gdmc".equals(vo.getItemkey())) {// 股东名称
				if (isItemEmpty(vo.getValue())) {
					if (vo.getFromcell().startsWith("R1"))
						throw new BusinessException("表编码A000000中第一行股东名称不能为空");
				}
			} else if ("sfzjzlDm".equals(vo.getItemkey())) {// 证件种类
				if (isItemEmpty(vo.getValue())) {
					if (vo.getFromcell().startsWith("R1"))
						throw new BusinessException("表编码A000000中第一行证件种类不能为空");
				}
			} else if ("zjhm".equals(vo.getItemkey())) {// 证件号码
				if (isItemEmpty(vo.getValue())) {
					if (vo.getFromcell().startsWith("R1"))
						throw new BusinessException("表编码A000000中第一行证件号码不能为空");
				}
			} else if ("jjxz".equals(vo.getItemkey())) {// 经济性质
				if (isItemEmpty(vo.getValue())) {
					if (vo.getFromcell().startsWith("R1"))
						throw new BusinessException("表编码A000000中第一行经济性质不能为空");
				}
			} else if ("tzbl".equals(vo.getItemkey())) {// 投资比例
				if (isItemEmpty(vo.getValue()) || "0".equals(vo.getValue())) {
					if (vo.getFromcell().startsWith("R1"))
						throw new BusinessException("表编码A000000中第一行投资比例不能为空或零");
				}
			} else if ("gjdqDm".equals(vo.getItemkey())) {// 国籍（注册地址）
				if (isItemEmpty(vo.getValue())) {
					if (vo.getFromcell().startsWith("R1"))
						throw new BusinessException("表编码A000000中第一行国籍（注册地址）不能为空");
				}
			} else if ("btznsrmc".equals(vo.getItemkey())) {// 被投资者名称
				if (isItemEmpty(vo.getValue())) {
					if (vo.getFromcell().startsWith("R1"))
						throw new BusinessException("表编码A000000中第一行被投资者名称不能为空");
				}
			} else if ("nsrsbh".equals(vo.getItemkey())) {// 纳税人识别号
				if (isItemEmpty(vo.getValue())) {
					if (vo.getFromcell().startsWith("R1"))
						throw new BusinessException("表编码A000000中第一行纳税人识别号不能为空");
				}
			} else if ("tzze".equals(vo.getItemkey())) {// 投资金额
				if (isItemEmpty(vo.getValue()) || "0".equals(vo.getValue())) {
					if (vo.getFromcell().startsWith("R1"))
						throw new BusinessException("表编码A000000中第一行投资金额不能为空或零");
				}
			} else if ("zcdz".equals(vo.getItemkey())) {// 注册地址
				if (isItemEmpty(vo.getValue())) {
					if (vo.getFromcell().startsWith("R1"))
						throw new BusinessException("表编码A000000中第一行注册地址不能为空");
				}
			}
		}
	}

	private static boolean isItemEmpty(String value) {
		if (StringUtil.isEmpty(value) || "**".equals(value)) {
			return true;
		}
		return false;
	}

	protected void setDefaultValue(TaxPosContrastVO vo, String nsrsbh, TaxReportVO reportvo) throws DZFWarpException {
		if ("#yearstart".equals(vo.getVdefaultvalue())) {
			String yearstart = (new DZFDate().getYear() - 1) + "0101";
			vo.setValue(yearstart);
		} else if ("#yearend".equals(vo.getVdefaultvalue())) {
			String yearend = (new DZFDate().getYear() - 1) + "1231";
			vo.setValue(yearend);
		} else {
			super.setDefaultValue(vo, nsrsbh, reportvo);
		}
	}

	protected TaxPosContrastVO[] filterData(TaxPosContrastVO[] vos,HashMap<String, TaxRptTempletVO> hmTemplet) throws DZFWarpException {
		// 不处理的业签
		String[] expCodes = new String[] { "A105081o", "A200600", "A200700" };

		// 需要特殊处理的 业签
		String[] specialCodes = new String[] { "A200220", "A200250" };
		vos = filterData(vos, expCodes,hmTemplet);
		vos = specialData(vos, specialCodes);
		return vos;
	}

	private static TaxPosContrastVO[] specialData(TaxPosContrastVO[] vos, String[] specialCodes) {

		if (specialCodes == null || specialCodes.length == 0) {
			return vos;
		}
		List<String> slist = Arrays.asList(specialCodes);

		List<TaxPosContrastVO> list1 = null;// 特殊处理vo
		Map<String, List<TaxPosContrastVO>> map = new HashMap<>();
		List<TaxPosContrastVO> tlist = new ArrayList<>();//

		for (TaxPosContrastVO vo : vos) {
			if (slist.contains(vo.getReportcode())) {
				if (map.containsKey(vo.getReportcode())) {
					list1 = map.get(vo.getReportcode());
				} else {
					list1 = new ArrayList<>();
				}
				list1.add(vo);
				map.put(vo.getReportcode(), list1);
			} else {
				tlist.add(vo);
			}
		}

		for (String code : slist) {

			List<TaxPosContrastVO> list = map.get(code);
			if (list == null || list.size() == 0)
				continue;

			if ("A200220".equals(code)) { // 企业重组所得税特殊性税务处理报告表(股权收购)

				// 主键业签 00000100000000uKlafx00VX
				TaxPosContrastVO bvoclone = null;
				String pk_parent = null;
				for (TaxPosContrastVO vo : list) {
					if ("00000100000000uKlafx00VX".equals(vo.getPk_taxtemplet_sd_pos())) {
						tlist.add(vo);
						TaxPosContrastVO headclone = (TaxPosContrastVO) vo.clone();
						pk_parent = UUID.randomUUID().toString();
						headclone.setPk_taxtemplet_sd_pos(pk_parent);
						tlist.add(headclone);
					} else if (!"00000100000000uKlafx00VX".equals(vo.getPk_parent())) {
						tlist.add(vo);
					} else {
						tlist.add(vo);
						bvoclone = (TaxPosContrastVO) vo.clone();
						bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
						bvoclone.setPk_parent(pk_parent);
						if ("gqzrfnssbh".equals(vo.getItemkey())) {
							bvoclone.setFromcell("R8C2");
						} else if ("gqzrfmc".equals(vo.getItemkey())) {
							bvoclone.setFromcell("R7C2");
						} else if ("gqzrfsszgswjgqc".equals(vo.getItemkey())) {
							bvoclone.setFromcell("R7C6");
						} else if ("zrgqbl".equals(vo.getItemkey())) {
							bvoclone.setFromcell("R7C4");
						} else if ("fgqzfdydzczrsdhss".equals(vo.getItemkey())) {
							bvoclone.setFromcell("R20C5");
						}
						tlist.add(bvoclone);
					}
				}
			} else if ("A200250".equals(code)) {// 企业重组所得税特殊性税务处理报告表(企业分立)

				// 主键业签 00000100000000uKlafx00YE
				TaxPosContrastVO bvoclone = null;
				String pk_parent = null;
				for (TaxPosContrastVO vo : list) {

					if ("00000100000000uKlafx00YE".equals(vo.getPk_taxtemplet_sd_pos())) {
						tlist.add(vo);
						TaxPosContrastVO headclone = (TaxPosContrastVO) vo.clone();
						pk_parent = UUID.randomUUID().toString();
						headclone.setPk_taxtemplet_sd_pos(pk_parent);
						tlist.add(headclone);
					} else if (!"00000100000000uKlafx00YE".equals(vo.getPk_parent())) {
						tlist.add(vo);
					} else {
						tlist.add(vo);
						bvoclone = (TaxPosContrastVO) vo.clone();
						bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
						bvoclone.setPk_parent(pk_parent);
						if ("nsrsbh".equals(vo.getItemkey())) {
							bvoclone.setFromcell("R18C1");
						} else if ("nsrmc".equals(vo.getItemkey())) {
							bvoclone.setFromcell("R17C1");
						} else if ("swjgmc".equals(vo.getItemkey())) {
							bvoclone.setFromcell("R17C4");
						} else if ("jsjczc".equals(vo.getItemkey())) {
							bvoclone.setFromcell("R20C1");
						} else if ("jsjcfz".equals(vo.getItemkey())) {
							bvoclone.setFromcell("R20C3");
						} else if ("jsjcjzc".equals(vo.getItemkey())) {
							bvoclone.setFromcell("R20C4");
						} else if ("gyjzzc".equals(vo.getItemkey())) {
							bvoclone.setFromcell("R21C1");
						} else if ("gyjzfz".equals(vo.getItemkey())) {
							bvoclone.setFromcell("R21C3");
						} else if ("gyjzjzc".equals(vo.getItemkey())) {
							bvoclone.setFromcell("R21C4");
						}
						tlist.add(bvoclone);
					}
				}
			}
		}
		return tlist.toArray(new TaxPosContrastVO[tlist.size()]);
	}

	protected void setSpecialValue(TaxPosContrastVO vo) throws DZFWarpException {
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

	protected List<TaxPosContrastVO> getTaxPosVO(String sb_zlbh,HashMap<String, TaxRptTempletVO> hmTemplet) throws DZFWarpException {
		TaxPosContrastVO[] vos = (TaxPosContrastVO[]) sbo.queryByCondition(TaxPosContrastVO.class,
				" nvl(dr,0) = 0  and  sbzlbh = 'A'", null);
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
		vos =filterData(vos,hmTemplet);

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
				if ("A107020".equals(vo.getReportcode())) {// A107020所得减免优惠明细表
					if (i == 3 || i == 10 || i == 19) {
						continue;
					}
				} else if ("A200220".equals(vo.getReportcode())) {// 企业重组所得税特殊性税务处理报告表(股权收购)
					// 项目明细信息Grid // 股权转让方信息-实际取得股权及其他资产
					if (i == 3 || i == 4) {
						if ("00000100000000uKlafx00Vl".equals(vo.getPk_taxtemplet_sd_pos())
								|| "00000100000000uKlafx00Ve".equals(vo.getPk_taxtemplet_sd_pos())) {
							continue;
						}
					}
				}

				TaxPosContrastVO headclone = (TaxPosContrastVO) vo.clone();
				String pkid = UUID.randomUUID().toString();
				headclone.setPk_taxtemplet_sd_pos(pkid);
				tlist.add(headclone);
				TaxPosContrastVO bvoclone = null;
				for (TaxPosContrastVO bvo : dzlist) {

					bvoclone = (TaxPosContrastVO) bvo.clone();
					if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {
						if ("A105081".equals(bvo.getReportcode())) {
							if ("hyDm".equals(bvo.getItemkey()) || bvo.getItemkey().equals("ewbhmc")) {
								if (i == 1 || i == 8) {
									bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
								} else {
									bvoclone.setFromcell(null);
								}
							} else {
								bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
							}
						} else {
							bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
						}
					} else {
						// continue;
					}
					bvoclone.setPk_parent(pkid);
					bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
					tlist.add(bvoclone);
				}
			}
		}
		return tlist;
	}

	@Override
	public TaxQcQueryVO getTaxQcQueryVO(TaxReportVO reportvo) throws DZFWarpException {
		TaxQcQueryVO qcvo = super.getTaxQcQueryVO(reportvo);
		qcvo.setYwlx(TaxConst.SERVICE_CODE_HSQJ);
		qcvo.setXmlType(TaxConst.XMLTYPE_HSQJ);
		return qcvo;
	}
}
