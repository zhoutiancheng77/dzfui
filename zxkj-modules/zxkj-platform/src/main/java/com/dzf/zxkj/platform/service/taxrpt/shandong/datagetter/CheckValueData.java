package com.dzf.zxkj.platform.service.taxrpt.shandong.datagetter;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.TaxRptConst;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.tax.TaxPosContrastVO;

public class CheckValueData {

	public static void checkData(TaxPosContrastVO vo, String nsrsbh) {

		if (TaxRptConst.SB_ZLBH_SETTLEMENT.equals(vo.getSbzlbh())) { // 企业所得税年报
			checkA(vo, nsrsbh);
		} else if (TaxRptConst.SB_ZLBH10102.equals(vo.getSbzlbh())) {// 增值税小规模

		}
	}

	private static void checkA(TaxPosContrastVO vo, String nsrsbh) {

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
}
