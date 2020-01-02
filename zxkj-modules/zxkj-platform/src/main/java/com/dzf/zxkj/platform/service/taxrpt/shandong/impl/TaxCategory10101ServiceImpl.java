package com.dzf.zxkj.platform.service.taxrpt.shandong.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.TaxPosContrastVO;
import com.dzf.zxkj.platform.model.tax.TaxReportDetailVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.tax.TaxRptTempletVO;
import com.dzf.zxkj.platform.model.tax.chk.TaxRptChk10101_shandong;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxConst;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxQcQueryVO;
import com.dzf.zxkj.platform.model.taxrpt.shandong.VatOrdinaryInitMapping;
import com.dzf.zxkj.platform.service.taxrpt.shandong.InitFiledMapParse;
import com.dzf.zxkj.platform.service.taxrpt.shandong.SDTaxConst;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.XMLUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


//增值税一般纳税人申报表月报	
@Service("taxcateserv_10101")
public class TaxCategory10101ServiceImpl extends DefaultTaxCategoryServiceImpl {

	@Autowired
	private SingleObjectBO sbo;

	protected void setDefaultValue(TaxPosContrastVO vo, String nsrsbh, TaxReportVO reportvo) throws DZFWarpException {

		if ("#row".equals(vo.getVdefaultvalue())) {//
			vo.setValue(Integer.toString((vo.getIrow())));
		} else if ("#col".equals(vo.getVdefaultvalue())) {
			vo.setValue(Integer.toString((vo.getIcol())));
		} else {
			super.setDefaultValue(vo, nsrsbh, reportvo);
		}
	}

	@Override
	protected void setSpecialValue(TaxPosContrastVO vo) throws DZFWarpException {
		if ("10101021".equals(vo.getReportcode())) {// 增值税减免税申报明细表
			if ("hmc".equals(vo.getItemkey())) {// 减税行名称 免税行名称
				if("合计".equals(vo.getValue())){
					if("R7C0".equals(vo.getFromcell())){
						vo.setValue("hj001");
					}
				}else{
					setSpecialNum(vo);
				}
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
		} else if ("10101024".equals(vo.getReportcode())) {// 营改增税负分析测算明细表
			if ("ysxmdmjmc".equals(vo.getItemkey())) {// 应税项目代码及名称
				setSpecialNum(vo);
			}
		}
	}

	protected List<TaxPosContrastVO> getTaxPosVO(String sbzlbh,HashMap<String, TaxRptTempletVO> hmTemplet) {
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

			int temp = 0;
			for (int i = 0; i < rowcount; i++) {

				if ("10101001".equals(vo.getReportcode())) {// 增值税纳税申报表（一般纳税人适用）
					if (i == 1 || i == 2 || i == 3) {
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
				}else if ("10101005".equals(vo.getReportcode())) {// 增值税纳税申报表附列资料（四）
					if (i == 5 || i == 6|| i == 7) {
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
						}else if ("10101005".equals(vo.getReportcode())) {// 增值税纳税申报表附列资料（四）
							if (i <5){
								if("bqzce".equals(bvoclone.getItemkey())){
									bvoclone.setFromcell(null);
								}else if("bqkjjdkjxse".equals(bvoclone.getItemkey())){
									bvoclone.setFromcell(null);
								}else if("bqsjjjdkjxse".equals(bvoclone.getItemkey())){
									bvoclone.setFromcell(null);
								}
							}else if(i>7){
								if("bqydjse".equals(bvoclone.getItemkey())){
									bvoclone.setFromcell(null);
								}else if("bqsjdjse".equals(bvoclone.getItemkey())){
									bvoclone.setFromcell(null);
								}else if("qmye".equals(bvoclone.getItemkey())){
									bvoclone.setFromcell("R" + (beginrow + i) + "C7");
								}
							}
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

	@Override
	public TaxQcQueryVO getTaxQcQueryVO(TaxReportVO reportvo) throws DZFWarpException {
		TaxQcQueryVO qcvo = super.getTaxQcQueryVO(reportvo);
		qcvo.setYwlx(TaxConst.SERVICE_CODE_ZZSYBNSR);
		qcvo.setXmlType(TaxConst.XMLTYPE_ZZSYBNSR);
		qcvo.setIsSend(DZFBoolean.TRUE);
		qcvo.setYzpzzlDm(TaxConst.CODE_ZZSYBNSR);
		return qcvo;
	}

	protected void setQcvo(TaxQcQueryVO qcvo) {
		qcvo.setIsConQc(DZFBoolean.TRUE);
		qcvo.setYwlx(TaxConst.SERVICE_CODE_ZZSYBNSRINIT);
		qcvo.setSchemaLocation(TaxConst.ZZSYBNSCHEMALOCATION);
		qcvo.setType(TaxConst.XMLTYPE_ZZSYBNSR);
		InitFiledMapParse intParse = new VatOrdinaryInitMapping();
		qcvo.setIntParse(intParse);
	}

	public String[] getCondition(String pk_taxreport, UserVO userVO, TaxReportVO reportvo) throws DZFWarpException {
		List<String> listCondition = new ArrayList<String>();
		String[] sacondition = TaxRptChk10101_shandong.saCheckCondition;
		// 读取报表内容
		SQLParameter params = new SQLParameter();
		params.addParam(reportvo.getPk_taxreport());
		TaxReportDetailVO[] vos = (TaxReportDetailVO[]) sbo.queryByCondition(TaxReportDetailVO.class,
				"pk_taxreport=? and nvl(dr,0)=0  ",
				params);
		HashMap<String, TaxReportDetailVO> hmDetail = new HashMap<String, TaxReportDetailVO>();
		for (TaxReportDetailVO detailvo : vos) {
			hmDetail.put(detailvo.getReportname().trim(), detailvo);
		}

		// 排除公式中含有没有显示报表的公式

		lab1: for (String condition : sacondition) {
			String[] saReportname = getReportNameFromCondition(condition);
			for (String reportname : saReportname) {
				if (hmDetail.containsKey(reportname.trim()) == false) {
					continue lab1;
				}
			}
			listCondition.add(condition);
		}

		return listCondition.toArray(new String[0]);
	}

	protected String getSb_zlbh(TaxReportVO reportvo) {
		return SDTaxConst.TEMPLET_SBZLBH10101;
	}

	@Override
	protected List<String> getNullRowPk(Map<String, List<TaxPosContrastVO>> map) {
		List<String> relist = new ArrayList<>();
		for (Map.Entry<String, List<TaxPosContrastVO>> entry : map.entrySet()) {
			boolean isAllNull = false;
			List<TaxPosContrastVO> clist1 = entry.getValue();
			for (TaxPosContrastVO vo : clist1) {
				if ("10101009".equals(vo.getReportcode())) {// 成品油购销存数量明细表
					if ("ypxh".equals(vo.getItemkey())) {// 油量型号
						if (StringUtil.isEmpty(vo.getValue())) {
							isAllNull = true;
							break;
						}
					}
				} else if ("10101021".equals(vo.getReportcode())) {// 增值税减免税申报明细表
					if ("hmc".equals(vo.getItemkey())) {// 行名称
						if (StringUtil.isEmpty(vo.getValue())) {
							isAllNull = true;
							break;
						}
					}
				} else if ("10101024".equals(vo.getReportcode())) {// 营改增税负分析测算明细表
					if ("ysxmdmjmc".equals(vo.getItemkey())) {// 应税项目代码及名称
						if (StringUtil.isEmpty(vo.getValue())) {
							isAllNull = true;
							break;
						}
					}
				} else if ("10101007".equals(vo.getReportcode())) {// 代扣代缴税收通用缴款书抵扣清单
					if ("kjrnsrsbh".equals(vo.getItemkey())) {// 扣缴人纳税人识别号
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
	protected String getQcXML(CorpVO corpvo, CorpTaxVo taxvo, TaxQcQueryVO qcvo) {
		String yjsbBwXml = XMLUtils.createQcXMLNew1(corpvo.getVsoccrecode(), taxvo.getVstatetaxpwd(),
				Integer.toString(new DZFDate().getYear()), qcvo);
//		String yjsbBwXml = super.getQcXML(corpvo, taxvo, qcvo);
		return yjsbBwXml;
	}
}
