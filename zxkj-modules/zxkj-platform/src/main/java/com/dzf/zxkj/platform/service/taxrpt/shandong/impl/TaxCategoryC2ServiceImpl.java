package com.dzf.zxkj.platform.service.taxrpt.shandong.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.utils.VOSortUtils;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.tree.BDTreeCreator;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxPosContrastVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.tax.TaxRptTempletVO;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxConst;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxQcQueryVO;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxVOTreeStrategy;
import com.dzf.zxkj.platform.service.taxrpt.shandong.SDTaxConst;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.XMLUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

//财报-一般企业季报
@Service("taxcateserv_C2")
public class TaxCategoryC2ServiceImpl extends DefaultTaxCategoryServiceImpl {

	@Autowired
	private SingleObjectBO sbo;

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
				// if (!StringUtil.isEmpty(vo.getValue())) {
				// String value = vo.getValue();
				// int rowno = Integer.parseInt(value);
				// if (rowno == 3) {
				// vo.setValue("35");
				// }
				// if (rowno > 3) {
				// vo.setValue(Integer.toString(rowno - 1));
				// }
				// rowno = Integer.parseInt(value);
				// if (rowno > 19) {
				// if (rowno == 20) {
				// vo.setValue("33");
				// } else {
				// vo.setValue(Integer.toString(rowno - 1));
				// if (rowno > 28) {
				// if (rowno == 29) {
				// vo.setValue("34");
				// } else {
				// vo.setValue(Integer.toString(rowno - 2));
				// }
				// }
				// }
				// } else {
				//
				// }
				// }
			}
		} else if ("C2002".equals(vo.getReportcode())) {// 利润表
			if ("ewbhxh".equals(vo.getItemkey())) {// 二维表行序号
//				if (!StringUtil.isEmpty(vo.getValue())) {
//					String value = vo.getValue();
//					int rowno = Integer.parseInt(value);
//					if (rowno > 12) {
//						if (rowno == 13) {
//							vo.setValue("21");
//						} else {
//							vo.setValue(Integer.toString(rowno - 1));
//							if (rowno > 18) {
//								vo.setValue(Integer.toString(rowno + 3));
//								if (rowno > 29) {
//									vo.setValue(Integer.toString(rowno - 12));
//								}
//							}
//						}
//					}
//				}
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
		// 查询字段中间关系表数据
		TaxPosContrastVO[] vos = createTaxPosContrastVOS(corpVO, objMapReport, spreadtool, reportvo, qcvo.getDjxh());

		VOSortUtils.ascSort(vos, new String[] { "rowno" });
		if (vos == null || vos.length == 0)
			throw new BusinessException("上报数据出错!");

		TaxPosContrastVO voss = (TaxPosContrastVO) BDTreeCreator.createTree(vos, new TaxVOTreeStrategy());
		vos = (TaxPosContrastVO[]) voss.getChildren();
		// 组装报文
		String yjsbBwXml = XMLUtils.createBusinessXML(vos, qcvo.getXmlType());
		yjsbBwXml = yjsbBwXml.replace("**", "");
		yjsbBwXml = yjsbBwXml.replace("——", "");
		// System.out.println(yjsbBwXml);
		yjsbBwXml = XMLUtils.createCbXML(corpVO.getVsoccrecode(), Integer.toString(new DZFDate().getYear()), qcvo,
				yjsbBwXml);
		return yjsbBwXml;
	}

	protected String getSb_zlbh(TaxReportVO reportvo) {
		return SDTaxConst.TEMPLET_SBZLBHC2;
	}

	// 获取中间的数据 并字段对动态行进行增加处理
	protected List<TaxPosContrastVO> getTaxPosVO(String sb_zlbh,HashMap<String, TaxRptTempletVO> hmTemplet) throws DZFWarpException {
		TaxPosContrastVO[] vos = (TaxPosContrastVO[]) sbo.queryByCondition(TaxPosContrastVO.class,
				" nvl(dr,0) = 0  and  sbzlbh = '" + sb_zlbh + "'", null);
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
		vos = filterData(vos,hmTemplet);

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
			// 资产负债表 插入了行次 变化较大 重新编写逻辑
			if ("00000100000001IH0P5Q0009".equals(pk_parent)) {// 资产负债表
				for (int i = 0; i < rowcount; i++) {
					TaxPosContrastVO headclone = (TaxPosContrastVO) vo.clone();
					String pkid = UUID.randomUUID().toString();
					headclone.setPk_taxtemplet_sd_pos(pkid);
					TaxPosContrastVO bvoclone = null;
					if (i < 2) {
						tlist.add(headclone);
						for (TaxPosContrastVO bvo : dzlist) {
							bvoclone = (TaxPosContrastVO) bvo.clone();
							if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
								bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
							} else if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("R")) { // 行固定
								bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
							}
							bvoclone.setIrow(i);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					} else if (i == 2) {
					} else if (i < 10) {
						tlist.add(headclone);
						for (TaxPosContrastVO bvo : dzlist) {
							bvoclone = (TaxPosContrastVO) bvo.clone();
							if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
								bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
							} else if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("R")) { // 行固定
								bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
							}
							bvoclone.setIrow(i - 1);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					} else if (i < 11) {
						tlist.add(headclone);
						for (TaxPosContrastVO bvo : dzlist) {
							bvoclone = (TaxPosContrastVO) bvo.clone();

							if ("资产项目名称".equals(bvo.getItemname()) || "期末余额_资产".equals(bvo.getItemname())
									|| "年初余额_资产".equals(bvo.getItemname())) {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R" + (beginrow + i + 1) + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i + 1));
								}
							} else {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
								}
							}
							bvoclone.setIrow(i - 1);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					} else if (i < 13) {
						if (i == 12) {
							tlist.add(headclone);
							for (TaxPosContrastVO bvo : dzlist) {
								bvoclone = (TaxPosContrastVO) bvo.clone();
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
								}
								bvoclone.setIrow(i - 2);
								bvoclone.setPk_parent(pkid);
								bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
								tlist.add(bvoclone);
							}
						}
					} else if (i < 18) {
						tlist.add(headclone);
						for (TaxPosContrastVO bvo : dzlist) {
							bvoclone = (TaxPosContrastVO) bvo.clone();
							if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
								bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
							} else if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("R")) { // 行固定
								bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
							}
							bvoclone.setIrow(i - 2);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					} else if (i < 21) {
						tlist.add(headclone);
						for (TaxPosContrastVO bvo : dzlist) {
							bvoclone = (TaxPosContrastVO) bvo.clone();
							if ("资产项目名称".equals(bvo.getItemname()) || "期末余额_资产".equals(bvo.getItemname())
									|| "年初余额_资产".equals(bvo.getItemname())) {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
								}
							} else {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R" + (beginrow + i + 2) + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i + 2));
								}
							}
							bvoclone.setIrow(i - 2);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					} else if (i < 27) {
						tlist.add(headclone);
						for (TaxPosContrastVO bvo : dzlist) {
							bvoclone = (TaxPosContrastVO) bvo.clone();
							if ("资产项目名称".equals(bvo.getItemname()) || "期末余额_资产".equals(bvo.getItemname())
									|| "年初余额_资产".equals(bvo.getItemname())) {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
								}
							} else {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R" + (beginrow + i + 3) + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i + 3));
								}
							}
							bvoclone.setIrow(i - 2);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					} else if (i < 29) {
						tlist.add(headclone);
						for (TaxPosContrastVO bvo : dzlist) {
							bvoclone = (TaxPosContrastVO) bvo.clone();
							if ("资产项目名称".equals(bvo.getItemname()) || "期末余额_资产".equals(bvo.getItemname())
									|| "年初余额_资产".equals(bvo.getItemname())) {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
								}
							} else {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R" + (beginrow + i + 6) + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i + 6));
								}
							}
							bvoclone.setIrow(i - 2);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					} else if (i < 32) {
						tlist.add(headclone);
						for (TaxPosContrastVO bvo : dzlist) {
							bvoclone = (TaxPosContrastVO) bvo.clone();
							if ("资产项目名称".equals(bvo.getItemname()) || "期末余额_资产".equals(bvo.getItemname())
									|| "年初余额_资产".equals(bvo.getItemname())) {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
								}
							} else {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R" + (beginrow + i + 7) + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i + 7));
								}
							}
							bvoclone.setIrow(i - 2);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					} else if (i == 32) {
						tlist.add(headclone);
						for (TaxPosContrastVO bvo : dzlist) {
							bvoclone = (TaxPosContrastVO) bvo.clone();
							if ("资产项目名称".equals(bvo.getItemname()) || "期末余额_资产".equals(bvo.getItemname())
									|| "年初余额_资产".equals(bvo.getItemname())) {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
								}
							} else {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell(null);
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(null);
								}
							}
							bvoclone.setIrow(i - 2);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					} else if (i == 33) {
						tlist.add(headclone);
						for (TaxPosContrastVO bvo : dzlist) {
							bvoclone = (TaxPosContrastVO) bvo.clone();
							if ("资产项目名称".equals(bvo.getItemname()) || "期末余额_资产".equals(bvo.getItemname())
									|| "年初余额_资产".equals(bvo.getItemname())) {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
								}
							} else {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R44" + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C44");
								}
							}
							bvoclone.setIrow(i - 2);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					} else if (i == 34) {
						tlist.add(headclone);
						for (TaxPosContrastVO bvo : dzlist) {
							bvoclone = (TaxPosContrastVO) bvo.clone();
							if ("资产项目名称".equals(bvo.getItemname()) || "期末余额_资产".equals(bvo.getItemname())
									|| "年初余额_资产".equals(bvo.getItemname())) {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
								}
							} else {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R28" + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C28");
								}
							}
							bvoclone.setIrow(i - 2);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					} else if (i == 35) {
						tlist.add(headclone);
						for (TaxPosContrastVO bvo : dzlist) {
							bvoclone = (TaxPosContrastVO) bvo.clone();
							if ("资产项目名称".equals(bvo.getItemname()) || "期末余额_资产".equals(bvo.getItemname())
									|| "年初余额_资产".equals(bvo.getItemname())) {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R" + (beginrow + i) + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i));
								}
							} else {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R40" + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C40");
								}
							}
							bvoclone.setIrow(i - 2);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					} else if (i == 36) {
						tlist.add(headclone);
						for (TaxPosContrastVO bvo : dzlist) {
							bvoclone = (TaxPosContrastVO) bvo.clone();
							if ("资产项目名称".equals(bvo.getItemname()) || "期末余额_资产".equals(bvo.getItemname())
									|| "年初余额_资产".equals(bvo.getItemname())) {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R7" + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C7");
								}
							} else {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R7" + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C7");
								}
							}
							bvoclone.setIrow(i - 2);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					} else if (i == 37) {
						tlist.add(headclone);
						for (TaxPosContrastVO bvo : dzlist) {
							bvoclone = (TaxPosContrastVO) bvo.clone();
							if ("资产项目名称".equals(bvo.getItemname()) || "期末余额_资产".equals(bvo.getItemname())
									|| "年初余额_资产".equals(bvo.getItemname())) {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R15" + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C15");
								}
							} else {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R16" + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C16");
								}
							}
							bvoclone.setIrow(i - 2);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					} else if (i < 40) {
						tlist.add(headclone);
						for (TaxPosContrastVO bvo : dzlist) {
							bvoclone = (TaxPosContrastVO) bvo.clone();
							if ("资产项目名称".equals(bvo.getItemname()) || "期末余额_资产".equals(bvo.getItemname())
									|| "年初余额_资产".equals(bvo.getItemname())) {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell(null);
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(null);
								}
							} else {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R" + (beginrow + i - 20) + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i - 20));
								}
							}
							bvoclone.setIrow(i - 2);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					} else {
						tlist.add(headclone);
						for (TaxPosContrastVO bvo : dzlist) {
							bvoclone = (TaxPosContrastVO) bvo.clone();
							if ("资产项目名称".equals(bvo.getItemname()) || "期末余额_资产".equals(bvo.getItemname())
									|| "年初余额_资产".equals(bvo.getItemname())) {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell(null);
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(null);
								}
							} else {
								if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
									bvoclone.setFromcell("R" + (beginrow + i - 10) + bvo.getFromcell());
								} else if (!StringUtil.isEmpty(bvo.getFromcell())
										&& bvo.getFromcell().startsWith("R")) { // 行固定
									bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i - 10));
								}
							}
							bvoclone.setIrow(i - 2);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					}
				}
			} else if ("00000100000001IH0P5Q000P".equals(pk_parent)) {// 利润表
				for (int i = 0; i < rowcount; i++) {

					if(i<10){
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
					}else if(i<12){
						
					}else if(i<15){
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
							bvoclone.setIrow(i-2);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					}else if(i<18){
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
							bvoclone.setIrow(i-1);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					}else if(i<21){
						TaxPosContrastVO headclone = (TaxPosContrastVO) vo.clone();
						String pkid = UUID.randomUUID().toString();
						headclone.setPk_taxtemplet_sd_pos(pkid);
						tlist.add(headclone);
						TaxPosContrastVO bvoclone = null;
						for (TaxPosContrastVO bvo : dzlist) {

							bvoclone = (TaxPosContrastVO) bvo.clone();
							if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
								bvoclone.setFromcell("R" + (beginrow + i+13) + bvo.getFromcell());
							} else if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("R")) { // 行固定
								bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i+13));
								bvoclone.setIcol(i);
							}
							bvoclone.setIrow(i-1);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					}else if(i<22){
					}else if(i<33){
						TaxPosContrastVO headclone = (TaxPosContrastVO) vo.clone();
						String pkid = UUID.randomUUID().toString();
						headclone.setPk_taxtemplet_sd_pos(pkid);
						tlist.add(headclone);
						TaxPosContrastVO bvoclone = null;
						for (TaxPosContrastVO bvo : dzlist) {

							bvoclone = (TaxPosContrastVO) bvo.clone();
							if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
								bvoclone.setFromcell("R" + (beginrow + i-2) + bvo.getFromcell());
							} else if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("R")) { // 行固定
								bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i-2));
								bvoclone.setIcol(i-1);
							}
							bvoclone.setIrow(i-1);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					}else if(i<35){
						TaxPosContrastVO headclone = (TaxPosContrastVO) vo.clone();
						String pkid = UUID.randomUUID().toString();
						headclone.setPk_taxtemplet_sd_pos(pkid);
						tlist.add(headclone);
						TaxPosContrastVO bvoclone = null;
						for (TaxPosContrastVO bvo : dzlist) {

							bvoclone = (TaxPosContrastVO) bvo.clone();
							if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
								bvoclone.setFromcell("R" + (beginrow + i-23) + bvo.getFromcell());
							} else if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("R")) { // 行固定
								bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i-23));
								bvoclone.setIcol(i-1);
							}
							bvoclone.setIrow(i-1);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					}else{
						TaxPosContrastVO headclone = (TaxPosContrastVO) vo.clone();
						String pkid = UUID.randomUUID().toString();
						headclone.setPk_taxtemplet_sd_pos(pkid);
						tlist.add(headclone);
						TaxPosContrastVO bvoclone = null;
						for (TaxPosContrastVO bvo : dzlist) {

							bvoclone = (TaxPosContrastVO) bvo.clone();
							if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("C")) {// 列固定
								bvoclone.setFromcell("R" + (beginrow + i-17) + bvo.getFromcell());
							} else if (!StringUtil.isEmpty(bvo.getFromcell()) && bvo.getFromcell().startsWith("R")) { // 行固定
								bvoclone.setFromcell(bvo.getFromcell() + "C" + (beginrow + i-17));
								bvoclone.setIcol(i-1);
							}
							bvoclone.setIrow(i-1);
							bvoclone.setPk_parent(pkid);
							bvoclone.setPk_taxtemplet_sd_pos(UUID.randomUUID().toString());
							tlist.add(bvoclone);
						}
					}
				}
			} else {
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
		}
		return tlist;
	}

}
