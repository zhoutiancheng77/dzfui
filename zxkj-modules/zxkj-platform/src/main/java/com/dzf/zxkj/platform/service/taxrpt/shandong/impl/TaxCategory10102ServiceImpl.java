package com.dzf.zxkj.platform.service.taxrpt.shandong.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.TaxPosContrastVO;
import com.dzf.zxkj.platform.model.tax.TaxReportDetailVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.tax.TaxRptTempletVO;
import com.dzf.zxkj.platform.model.tax.chk.TaxRptChk10102_shandong;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxConst;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxQcQueryVO;
import com.dzf.zxkj.platform.model.taxrpt.shandong.VatSmallInitMapping;
import com.dzf.zxkj.platform.service.taxrpt.shandong.InitFiledMapParse;
import com.dzf.zxkj.platform.service.taxrpt.shandong.SDTaxConst;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.ParseXml;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.XMLUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;

//增值税小规模纳税人申报表季报
@Service("taxcateserv_10102")
public class TaxCategory10102ServiceImpl extends DefaultTaxCategoryServiceImpl {
 
	@Autowired
	private SingleObjectBO sbo;

	@Override
	protected void setDefaultValue(TaxPosContrastVO vo, String nsrsbh, TaxReportVO reportvo) throws DZFWarpException {

		if ("#row".equals(vo.getVdefaultvalue())) {
			vo.setValue(Integer.toString((vo.getIrow() - 3)));
		} else {
			super.setDefaultValue(vo, nsrsbh, reportvo);
		}
	}

	@Override
	protected TaxPosContrastVO[] filterData(TaxPosContrastVO[] vos,HashMap<String, TaxRptTempletVO> hmTemplet) throws DZFWarpException {
		// 不处理的业签
		String[] expCodes = new String[] { "JDCLSC", "JDCLXS", "DLQY", "FJSSB" };
		vos = filterData(vos, expCodes,hmTemplet);
		return vos;
	}

	@Override
	protected void setSpecialValue(TaxPosContrastVO vo) throws DZFWarpException {
		if ("10102004".equals(vo.getReportcode())) {// 增值税减免税申报明细表
			if ("hmc".equals(vo.getItemkey())) {// 减税行名称 免税行名称
				int row = vo.getIrow();
				if (row == 16 || row == 7) {
					if ("合计".equals(vo.getValue()))
						vo.setValue("hj001");
					else
						setSpecialNum(vo);
				} else if (row == 17) {
					if ("出口免税".equals(vo.getValue()))
						vo.setValue("CKMS001");
					else
						setSpecialNum(vo);
				} else if (row == 18) {
					if ("其中：跨境服务".equals(vo.getValue()))
						vo.setValue("QZKJFW001");
					else
						setSpecialNum(vo);
				} else {
					setSpecialNum(vo);
				}
			}
		}
	}

	protected String getSb_zlbh(TaxReportVO reportvo) {
		return SDTaxConst.TEMPLET_SBZLBH10102;
	}

	@Override
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

	@Override
	protected List<String> getNullRowPk(Map<String, List<TaxPosContrastVO>> map) {
		List<String> relist = new ArrayList<>();
		for (Entry<String, List<TaxPosContrastVO>> entry : map.entrySet()) {
			boolean isAllNull = false;
			List<TaxPosContrastVO> clist1 = entry.getValue();
			for (TaxPosContrastVO vo : clist1) {
				if ("10102004".equals(vo.getReportcode())) {// 增值税减免税申报明细表
					if ("hmc".equals(vo.getItemkey())) {// 行名称
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

	@Override
	public TaxQcQueryVO getTaxQcQueryVO(TaxReportVO reportvo) throws DZFWarpException {
		TaxQcQueryVO qcvo = super.getTaxQcQueryVO(reportvo);
		qcvo.setYwlx(TaxConst.SERVICE_CODE_ZZSXGM);
		qcvo.setXmlType(TaxConst.XMLTYPE_ZZSXGM);
		qcvo.setIsSend(DZFBoolean.TRUE);
		qcvo.setYzpzzlDm(TaxConst.CODE_ZZSXGM);
		return qcvo;
	}

	protected void setQcvo(TaxQcQueryVO qcvo) {
		qcvo.setIsConQc(DZFBoolean.TRUE);
		qcvo.setYwlx(TaxConst.SERVICE_CODE_ZZSXGMINIT);
		qcvo.setSchemaLocation(TaxConst.ZZSXGSCHEMALOCATION);
		qcvo.setType(TaxConst.XMLTYPE_ZZSXGMQC);
		InitFiledMapParse intParse = new VatSmallInitMapping();
		qcvo.setIntParse(intParse);
	}

	public String[] getCondition(String pk_taxreport, UserVO userVO, TaxReportVO reportvo) throws DZFWarpException {
		List<String> listCondition = new ArrayList<String>();

		String[] sacondition = TaxRptChk10102_shandong.saCheckCondition;
		// 读取报表内容
		SQLParameter params = new SQLParameter();
		params.addParam(reportvo.getPk_taxreport());
		TaxReportDetailVO[] vos = (TaxReportDetailVO[]) sbo.queryByCondition(TaxReportDetailVO.class, "pk_taxreport=?",
				params);
		HashMap<String, TaxReportDetailVO> hmDetail = new HashMap<String, TaxReportDetailVO>();
		for (TaxReportDetailVO detailvo : vos) {
			hmDetail.put(detailvo.getReportname().trim(), detailvo);
		}

		// 排除公式中含有没有显示报表的公式

		lab1: for (String condition : sacondition) {
			String[] saReportname = getReportNameFromCondition(condition);
			for (String reportname : saReportname) {
				if (hmDetail.containsKey(reportname) == false) {
					continue lab1;
				}
			}
			listCondition.add(condition);
		}
		return listCondition.toArray(new String[0]);
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
			String key = null;
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
								String text = (String) ParseXml.getValueByName((DefaultElement) item, entry.getValue());
								if (text == null)
									continue;
								if ("yjxxGrid".equals(voName) ) {
									DZFDouble old = (DZFDouble) initData.get(entry.getKey());
									initData.put(entry.getKey(), SafeCompute.add(old, new DZFDouble(text)));
//									System.out.println("业签" + voName + "，行列数：" +index + "，字段：" + entry.getKey() + "，值："+  initData.get(entry.getKey()));
								} else {
									key = entry.getKey();
									if ("bsrxm".equals(key) || "cwfzrxm".equals(key) || "fddbrxm".equals(key)
											|| "bsrlxdh".equals(key)) {
										initData.put(entry.getKey(), text);
									} else {
										initData.put(entry.getKey(), new DZFDouble(text));
									}

								}

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
	
	@Override
	protected String getQcXML(CorpVO corpvo, CorpTaxVo taxvo, TaxQcQueryVO qcvo) {
		String yjsbBwXml = XMLUtils.createQcXMLNew1(corpvo.getVsoccrecode(), taxvo.getVstatetaxpwd(),
				Integer.toString(new DZFDate().getYear()), qcvo);
//		String yjsbBwXml = super.getQcXML(corpvo, taxvo, qcvo);
		return yjsbBwXml;
	}
}
