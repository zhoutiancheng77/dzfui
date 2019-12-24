package com.dzf.zxkj.platform.service.taxrpt.shandong.datagetter;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.TaxRptConst;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.tax.TaxPosContrastVO;

import java.util.*;

public class TaxTempletVOGetter {

	private SingleObjectBO sbo;

	public TaxTempletVOGetter(SingleObjectBO sbo) {
		this.sbo = sbo;
	}

	public List<TaxPosContrastVO> getTaxPosVO(String sb_zlbh) {

		List<TaxPosContrastVO> list = null;
		if (TaxRptConst.SB_ZLBH_SETTLEMENT.equals(sb_zlbh)) { // 企业所得税年报
			list = getTaxPosVOA(sb_zlbh);
		} else if (TaxRptConst.SB_ZLBH10102.equals(sb_zlbh)) {// 增值税小规模
			list = getTaxPosVO10102(sb_zlbh);
		} else if (TaxRptConst.SB_ZLBH10101.equals(sb_zlbh)) {// 增值税一般纳税人
			list = getTaxPosVO10101(sb_zlbh);
		} else if (TaxRptConst.SB_ZLBHC1.equals(sb_zlbh)) {// 小企业财报
			list = getTaxPosVOC1(sb_zlbh);
		} else if (TaxRptConst.SB_ZLBHC2.equals(sb_zlbh)) {// 一般企业财报
			list = getTaxPosVOC2(sb_zlbh);
		} else if (TaxRptConst.SB_ZLBH10412.equals(sb_zlbh)) {// 企业所得税A
			list = getTaxPosV10412(sb_zlbh);
		} else if (TaxRptConst.SB_ZLBH10413.equals(sb_zlbh)) {// 企业所得税B
			list = getTaxPosV10413(sb_zlbh);
		}
		return list;
	}

	private List<TaxPosContrastVO> getTaxPosVO10101(String sbzlbh) {
		TaxPosContrastVO[] vos = (TaxPosContrastVO[]) sbo.queryByCondition(TaxPosContrastVO.class,
				" nvl(dr,0) = 0  and  sbzlbh = '" + sbzlbh + "' ", null);
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
		vos = FilterTaxDataUtil.filterData(vos);

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

			int temp = 0;
			for (int i = 0; i < rowcount; i++) {

				if ("10101001".equals(vo.getReportcode())) {// 增值税纳税申报表（一般纳税人适用）
					if (i == 1 || i == 2 || i == 3 || i == 6) {
						continue;
					}
				} else if ("10101003".equals(vo.getReportcode())) {// 增值税纳税申报表附列资料（附表二）
					if (i == 13 || i == 26 || i == 39 || i == 14 || i == 27 || i == 40) {
						continue;
					}
				} else if ("10101022".equals(vo.getReportcode())) {// 本期抵扣进项税额结构明细表
					if (i == 1 || i == 29) {
						continue;
					}
				}
				temp++;
				TaxPosContrastVO headclone = (TaxPosContrastVO) vo.clone();
				String pkid = UUID.randomUUID().toString();
				headclone.setPk_taxtemplet_sd_pos(pkid);
				tlist.add(headclone);
				TaxPosContrastVO bvoclone = null;
				for (TaxPosContrastVO bvo : dzlist) {

					bvoclone = (TaxPosContrastVO) bvo.clone();
					if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
						bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
						if ("10101003".equals(vo.getReportcode())) {// 增值税纳税申报表附列资料二（本期进项税额明细）
							if ("se".equals(bvoclone.getItemkey())) {
								if (i > 14 && i < 26)
									bvoclone.setFromcell("R" + (beginrow + i) + "C2");
							}
						} else if ("10101002".equals(vo.getReportcode())) {// 增值税纳税申报表附列资料（一）
							// 13a(原13行)填写13,13b行填写20,13c行填写21,营改增新增9b行填写22)
							// if("ewbhxh".equals(vo.getItemkey())){
							// if("9a".equals(vo.getValue())){
							// vo.setValue("9");
							// }else if("9b".equals(vo.getValue())){
							// vo.setValue("22");
							// }else if("13a".equals(vo.getValue())){
							// vo.setValue("13");
							// }else if("13b".equals(vo.getValue())){
							// vo.setValue("20");
							// }else if("13c".equals(vo.getValue())){
							// vo.setValue("21");
							// }
							// }
						}
					} else if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("R")) { // 行固定
						bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
					}

					bvoclone.setIcol(temp);
					bvoclone.setIrow(temp);
					bvoclone.setPk_parent(pkid);
					bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
					tlist.add(bvoclone);
				}
			}
		}
		return tlist;
	}

	private List<TaxPosContrastVO> getTaxPosVOA(String sbzlbh) {
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
		vos = FilterTaxDataUtil.filterData(vos);

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

	private List<TaxPosContrastVO> getTaxPosVO10102(String sbzlbh) {
		TaxPosContrastVO[] vos = (TaxPosContrastVO[]) sbo.queryByCondition(TaxPosContrastVO.class,
				" nvl(dr,0) = 0  and  sbzlbh = '" + sbzlbh + "'", null);
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
		vos = FilterTaxDataUtil.filterData(vos);

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

				TaxPosContrastVO headclone = (TaxPosContrastVO) vo.clone();
				String pkid = UUID.randomUUID().toString();
				headclone.setPk_taxtemplet_sd_pos(pkid);
				tlist.add(headclone);
				TaxPosContrastVO bvoclone = null;
				for (TaxPosContrastVO bvo : dzlist) {

					bvoclone = (TaxPosContrastVO) bvo.clone();
					if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
						bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
					} else if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("R")) { // 行固定
						bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
						bvoclone.setIcol(i);
					}
					bvoclone.setIrow(beginrow + i);
					bvoclone.setPk_parent(pkid);
					bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
					tlist.add(bvoclone);
				}
			}
		}
		return tlist;
	}

	private List<TaxPosContrastVO> getTaxPosVOC1(String sbzlbh) {
		TaxPosContrastVO[] vos = (TaxPosContrastVO[]) sbo.queryByCondition(TaxPosContrastVO.class,
				" nvl(dr,0) = 0  and  sbzlbh = '" + sbzlbh + "'", null);
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
		vos = FilterTaxDataUtil.filterData(vos);

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

			int index = 0;
			for (int i = 0; i < rowcount; i++) {

				if ("C1003".equals(vo.getReportcode())) {// 现金流量表
					if (i == 0 || i == 8 || i == 15) {
						continue;
					}
				}

				TaxPosContrastVO headclone = (TaxPosContrastVO) vo.clone();
				String pkid = UUID.randomUUID().toString();
				headclone.setPk_taxtemplet_sd_pos(pkid);
				tlist.add(headclone);
				TaxPosContrastVO bvoclone = null;
				for (TaxPosContrastVO bvo : dzlist) {

					bvoclone = (TaxPosContrastVO) bvo.clone();
					if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
						bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
					} else if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("R")) { // 行固定
						bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
						bvoclone.setIcol(i);
					}
					bvoclone.setIrow(index);
					bvoclone.setPk_parent(pkid);
					bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
					tlist.add(bvoclone);
				}
				index++;
			}
		}
		return tlist;
	}

	private List<TaxPosContrastVO> getTaxPosVOC2(String sbzlbh) {
		TaxPosContrastVO[] vos = (TaxPosContrastVO[]) sbo.queryByCondition(TaxPosContrastVO.class,
				" nvl(dr,0) = 0  and  sbzlbh = '" + sbzlbh + "'", null);
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
		vos = FilterTaxDataUtil.filterData(vos);

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

				TaxPosContrastVO headclone = (TaxPosContrastVO) vo.clone();
				String pkid = UUID.randomUUID().toString();
				headclone.setPk_taxtemplet_sd_pos(pkid);
				tlist.add(headclone);
				TaxPosContrastVO bvoclone = null;
				for (TaxPosContrastVO bvo : dzlist) {

					bvoclone = (TaxPosContrastVO) bvo.clone();
					if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
						bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
					} else if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("R")) { // 行固定
						bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
						bvoclone.setIcol(i);
					}
					bvoclone.setIrow(i);
					bvoclone.setPk_parent(pkid);
					bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
					tlist.add(bvoclone);
				}
			}
		}
		return tlist;
	}

	private List<TaxPosContrastVO> getTaxPosV10412(String sbzlbh) {
		TaxPosContrastVO[] vos = (TaxPosContrastVO[]) sbo.queryByCondition(TaxPosContrastVO.class,
				" nvl(dr,0) = 0  and  sbzlbh = '" + sbzlbh + "'", null);
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
		vos = FilterTaxDataUtil.filterData(vos);

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

				TaxPosContrastVO headclone = (TaxPosContrastVO) vo.clone();
				String pkid = UUID.randomUUID().toString();
				headclone.setPk_taxtemplet_sd_pos(pkid);
				tlist.add(headclone);
				TaxPosContrastVO bvoclone = null;
				for (TaxPosContrastVO bvo : dzlist) {

					bvoclone = (TaxPosContrastVO) bvo.clone();
					if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
						bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
					} else if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("R")) { // 行固定
						bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
						bvoclone.setIcol(i);
					}
					bvoclone.setIrow(i);
					bvoclone.setPk_parent(pkid);
					bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
					tlist.add(bvoclone);
				}
			}
		}
		return tlist;
	}

	private List<TaxPosContrastVO> getTaxPosV10413(String sbzlbh) {
		TaxPosContrastVO[] vos = (TaxPosContrastVO[]) sbo.queryByCondition(TaxPosContrastVO.class,
				" nvl(dr,0) = 0  and  sbzlbh = '" + sbzlbh + "'", null);
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
		vos = FilterTaxDataUtil.filterData(vos);

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

				TaxPosContrastVO headclone = (TaxPosContrastVO) vo.clone();
				String pkid = UUID.randomUUID().toString();
				headclone.setPk_taxtemplet_sd_pos(pkid);
				tlist.add(headclone);
				TaxPosContrastVO bvoclone = null;
				for (TaxPosContrastVO bvo : dzlist) {

					bvoclone = (TaxPosContrastVO) bvo.clone();
					if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
						bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
					} else if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("R")) { // 行固定
						bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
						bvoclone.setIcol(i);
					}
					bvoclone.setIrow(i);
					bvoclone.setPk_parent(pkid);
					bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
					tlist.add(bvoclone);
				}
			}
		}
		return tlist;
	}
}
