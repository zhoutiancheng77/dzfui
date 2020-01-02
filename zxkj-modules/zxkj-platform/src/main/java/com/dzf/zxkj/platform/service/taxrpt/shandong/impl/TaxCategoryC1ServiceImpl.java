package com.dzf.zxkj.platform.service.taxrpt.shandong.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxPosContrastVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.tax.TaxRptTempletVO;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxConst;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxQcQueryVO;
import com.dzf.zxkj.platform.service.taxrpt.shandong.SDTaxConst;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.XMLUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

//财报-小企业季报
@Service("taxcateserv_C1")
public class TaxCategoryC1ServiceImpl extends DefaultTaxCategoryServiceImpl {

	@Autowired
	private SingleObjectBO sbo;

	protected void setDefaultValue(TaxPosContrastVO vo, String nsrsbh, TaxReportVO reportvo) throws DZFWarpException {

		if ("#row".equals(vo.getVdefaultvalue())) {
			vo.setValue(Integer.toString((vo.getIrow()) + 1));
		} else {
			super.setDefaultValue(vo, nsrsbh, reportvo);
		}
	}

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

	@Override
	public TaxQcQueryVO getTaxQcQueryVO(TaxReportVO reportvo) throws DZFWarpException {
		TaxQcQueryVO qcvo = super.getTaxQcQueryVO(reportvo);
		qcvo.setYwlx(TaxConst.SERVICE_CODE_XQYKJZZLCB);
		qcvo.setXmlType(TaxConst.XMLTYPE_XQYKJZZLCB);
		qcvo.setIsSend(DZFBoolean.TRUE);
		qcvo.setYzpzzlDm(TaxConst.CODE_XQYKJZZLCB);
		return qcvo;
	}

	@Override
	protected String getyjSbbw(CorpVO corpVO, Map objMapReport, SpreadTool spreadtool, TaxReportVO reportvo,
							   TaxQcQueryVO qcvo) {
		String yjsbBwXml = super.getyjSbbw(corpVO, objMapReport, spreadtool, reportvo, qcvo);
		yjsbBwXml = XMLUtils.createCbXML(corpVO.getVsoccrecode(), Integer.toString(new DZFDate().getYear()), qcvo,
				yjsbBwXml);
		return yjsbBwXml;
	}

	protected String getSb_zlbh(TaxReportVO reportvo) {
		return SDTaxConst.TEMPLET_SBZLBHC1;
	}
}
