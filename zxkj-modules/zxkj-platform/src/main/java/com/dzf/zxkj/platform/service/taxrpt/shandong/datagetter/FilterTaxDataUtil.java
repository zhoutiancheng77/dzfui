package com.dzf.zxkj.platform.service.taxrpt.shandong.datagetter;

import com.dzf.zxkj.common.constant.TaxRptConst;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.tax.TaxPosContrastVO;

import java.util.*;

public class FilterTaxDataUtil {

	public static TaxPosContrastVO[] filterData(TaxPosContrastVO[] vos) {

		if (vos == null || vos.length == 0) {
			return null;
		}
		String[] expCodes = null;
		String[] specialCodes = null;
		if (TaxRptConst.SB_ZLBH_SETTLEMENT.equals(vos[0].getSbzlbh())) { // 企业所得税年报
			// 不处理的业签
			expCodes = new String[] { "A105081o", "A200600", "A200700" };

			// 需要特殊处理的 业签
			specialCodes = new String[] { "A200220", "A200250" };
			vos = filterData(vos, expCodes);
			vos = specialData(vos, specialCodes);
		} else if (TaxRptConst.SB_ZLBH10102.equals(vos[0].getSbzlbh())) {// 增值税小规模
			// 不处理的业签
			expCodes = new String[] { "JDCLSC", "JDCLXS", "DLQY", "FJSSB" };
			vos = filterData(vos, expCodes);
			vos = specialData(vos, specialCodes);
		} else if (TaxRptConst.SB_ZLBH10101.equals(vos[0].getSbzlbh())) {// 增值税一般纳税人
			vos = filterData1(vos);
		} else if (TaxRptConst.SB_ZLBHC2.equals(vos[0].getSbzlbh())) {// 一般企业财报
			vos = filterData1(vos);
		} else if (TaxRptConst.SB_ZLBHC1.equals(vos[0].getSbzlbh())) {// 小企业财报
			vos = filterData1(vos);
		}

		return vos;
	}

	private static TaxPosContrastVO[] filterData(TaxPosContrastVO[] vos, String[] expCodes) {

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
					tlist.add(vo);
				}
			}
		}
		return tlist.toArray(new TaxPosContrastVO[tlist.size()]);

	}

	private static TaxPosContrastVO[] filterData1(TaxPosContrastVO[] vos) {

		List<TaxPosContrastVO> tlist = new ArrayList<>();//

		for (TaxPosContrastVO vo : vos) {
			if (StringUtil.isEmpty(vo.getReportcode())) {
				tlist.add(vo);
			} else {
				if (!vo.getReportcode().startsWith("N")) {
					tlist.add(vo);
				}
			}
		}
		return tlist.toArray(new TaxPosContrastVO[tlist.size()]);

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

}
