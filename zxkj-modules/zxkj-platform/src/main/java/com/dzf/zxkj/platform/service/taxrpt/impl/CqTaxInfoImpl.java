package com.dzf.zxkj.platform.service.taxrpt.impl;

import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.PeriodType;
import com.dzf.zxkj.common.constant.TaxRptConst;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.tree.BDTreeCreator;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.config.TaxCqtcConfig;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.*;
import com.dzf.zxkj.platform.model.tax.cqtc.*;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxConst;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxVOTreeStrategy;
import com.dzf.zxkj.platform.service.taxrpt.ICqTaxInfoService;
import com.dzf.zxkj.platform.service.taxrpt.ITaxBalaceCcrService;
import com.dzf.zxkj.platform.service.taxrpt.shandong.datagetter.CheckValueData;
import com.dzf.zxkj.platform.service.taxrpt.shandong.datagetter.DefaultValueGetter;
import com.dzf.zxkj.platform.service.taxrpt.shandong.datagetter.SpecialValueGetter;
import com.dzf.zxkj.platform.service.taxrpt.shandong.datagetter.TaxTempletVOGetter;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;
import com.dzf.zxkj.platform.util.taxrpt.TaxReportPath;
import com.dzf.zxkj.platform.util.taxrpt.cqtc.CQXMLUtil;
import com.dzf.zxkj.platform.util.taxrpt.cqtc.deal.DESHelper;
import com.dzf.zxkj.platform.util.taxrpt.shandong.deal.XMLUtils;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service("cq_taxInfoService")
@Slf4j
public class CqTaxInfoImpl implements ICqTaxInfoService {
	@Autowired
	private SingleObjectBO sbo;
	@Autowired
	private ITaxBalaceCcrService taxbalancesrv;
	@Autowired
	private static TaxCqtcConfig cqtcConfig;

	@Override
	public String processSendTaxReport(CQTaxReportVO taxInfo) throws DZFWarpException {
		SQLParameter params = new SQLParameter();
		params.addParam(taxInfo.getPk_corp());
		params.addParam(taxInfo.getPeriod() + "01");
		String condition = "nvl(dr,0)=0 and pk_corp = ? and PERIODFROM = ?";
		TaxReportVO[] reportvo = (TaxReportVO[]) sbo.queryByCondition(TaxReportVO.class, condition, params);
		if (reportvo == null) {
			throw new BusinessException("纳税申报信息出错");
		}
		String condition_detail = "nvl(dr,0)=0 and pk_taxreport=? ";
		params = new SQLParameter();
		params.addParam(reportvo[0].getPk_taxreport());
		TaxReportDetailVO[] detailvo = (TaxReportDetailVO[]) sbo.queryByCondition(TaxReportDetailVO.class,
				condition_detail, params);
		if (detailvo == null) {
			throw new BusinessException("纳税申报信息出错");
		}
		int isbzt_dm = Integer.parseInt(reportvo[0].getSbzt_dm());
		if (!(isbzt_dm == TaxRptConst.iSBZT_DM_UnSubmit || isbzt_dm == TaxRptConst.iSBZT_DM_AcceptFailute
				|| isbzt_dm == TaxRptConst.iSBZT_DM_ReportFailute || isbzt_dm == TaxRptConst.iSBZT_DM_ReportCancel)) {
			throw new BusinessException("报表的申报状态是" + TaxRptConst.getSBzt_mc(isbzt_dm) + ", 不能重复申报");
		}

		if (StringUtil.isEmpty(detailvo[0].getSpreadfile())) {
			throw new BusinessException("纳税申报表未填写");
		}

		Map objMapReport = readJsonValue(readFileString(detailvo[0].getSpreadfile()), LinkedHashMap.class);

		SpreadTool spreadtool = new SpreadTool(taxbalancesrv);

		return sendTaxReport(taxInfo, objMapReport, spreadtool, reportvo[0]);
	}

	public String sendTaxReport(CQTaxReportVO taxInfo, Map objMapReport, SpreadTool spreadtool, TaxReportVO reportvo)
			throws DZFWarpException {

		String nsrsbh = taxInfo.getNsrsbh();
		TaxPosContrastVO[] vos = createTaxPosContrastVOS(objMapReport, spreadtool, reportvo, nsrsbh);
		TaxPosContrastVO voss = (TaxPosContrastVO) BDTreeCreator.createTree(vos, new TaxVOTreeStrategy());
		vos = (TaxPosContrastVO[]) voss.getChildren();
		String yjsbBwXml = XMLUtils.createBusinessXML(vos, TaxConst.XMLTYPE_HSQJ);
		// throw new BusinessException("当前地区不支持上报。");
		return yjsbBwXml;
	}

	/**
	 * 创建纳税申报信息
	 * 
	 * @param
	 * @return
	 */
	private TaxPosContrastVO[] createTaxPosContrastVOS(Map objMapReport, SpreadTool spreadtool, TaxReportVO reportvo,
			String nsrsbh) {

		// String pk_taxtypelistdetail = detailvo.getPk_taxreportdetail();

		String sbzlpk = reportvo.getPk_taxsbzl();
		String location = "重庆";
		SQLParameter params = new SQLParameter();
		params.addParam(sbzlpk);
		TaxRptTempletVO[] templetvos = (TaxRptTempletVO[]) sbo.queryByCondition(TaxRptTempletVO.class,
				"nvl(dr,0)=0 and rtrim(location)='" + location + "' and pk_taxsbzl=?", params);

		if (templetvos == null || templetvos.length == 0)
			throw new BusinessException("纳税申报模板信息出错");

		HashMap<String, TaxRptTempletVO> hmTemplet = new HashMap<String, TaxRptTempletVO>();
		for (TaxRptTempletVO vo : templetvos) {
			hmTemplet.put(vo.getPk_taxrpttemplet(), vo);
		}
		TaxTempletVOGetter getter = new TaxTempletVOGetter(sbo);
		List<TaxPosContrastVO> list = getter.getTaxPosVO(reportvo.getSb_zlbh());

		if (list == null || list.size() == 0)
			return null;

		for (TaxPosContrastVO vo : list) {
			String vdefaultvalue = vo.getVdefaultvalue();

			if (!StringUtil.isEmpty(vdefaultvalue)) {
				// 默认值处理
				vdefaultvalue = DefaultValueGetter.getDefaultValue(vo, nsrsbh);
				vo.setValue(vdefaultvalue);
			}

			// 按照单元格取数
			String reportcode = vo.getReportcode();
			if (StringUtil.isEmpty(reportcode)) {
				continue;
			}

			String fromcell = vo.getFromcell();
			if (StringUtil.isEmpty(fromcell)) {
				if (vo.getIsspecial() != null && vo.getIsspecial().booleanValue()) {
					// 特殊字段处理
					String value = SpecialValueGetter.getSpecialValue(vo);
					vo.setValue(value);
				}
				continue;
			}

			String[] spos = fromcell.split("C");
			int iRow = Integer.parseInt(spos[0].substring(1, spos[0].length()));
			int iColumn = Integer.parseInt(spos[1]);
			TaxRptTempletVO tvo = hmTemplet.get(vo.getPk_taxtemplet());
			if (tvo == null)
				throw new BusinessException("纳税申报模板信息出错");

			Object o = spreadtool.getCellValue(objMapReport, tvo.getReportname(), iRow, iColumn);
			if (o != null) {
				String value = o.toString();
				if (StringUtil.isEmpty(value) || " ".equalsIgnoreCase(value) || "——".equalsIgnoreCase(value)) {
					continue;
				}
				log.info("单元格:" + fromcell + "的内容" + value);
				// if (NumberValidationUtils.isRealNumber(value)) {
				vo.setValue(value);
				// } else {
				// throw new BusinessException(
				// tvo.getReportname() + "第" + (iRow + 1) + "行,第" + (iColumn +
				// 1) + "列内容格式不对,内容为" + value);
				// }
				if (vo.getIsspecial() != null && vo.getIsspecial().booleanValue()) {
					// 特殊字段处理
					value = SpecialValueGetter.getSpecialValue(vo);
					vo.setValue(value);
				}
				// System.out.println("reportcode:" + vo.getReportcode() +
				// ",name:"
				// + vo.getItemname() + ",key:"
				// + vo.getItemkey() + ",fromcell:" + vo.getFromcell() +
				// ",value:" +
				// vo.getValue());
			}
		}
		for (TaxPosContrastVO vo : list) {
			CheckValueData.checkData(vo, nsrsbh);
		}
		return list.toArray(new TaxPosContrastVO[list.size()]);
	}

	private String readFileString(String filepath) throws DZFWarpException {

		String sReturn = null;
		if (filepath.startsWith("*")) {
			try {
				byte[] bytes = ((FastDfsUtil) SpringUtils.getBean("connectionPool")).downFile(filepath.substring(1));

				if (bytes != null && bytes.length > 0) {
					sReturn = new String(bytes, "utf-8");
				}
				return sReturn;
			} catch (Exception e) {
				throw new WiseRunException(e);
			}

		}
		return sReturn;
	}

	private <T> T readJsonValue(String strJSON, Class<T> clazz) throws DZFWarpException {
		try {
			return getObjectMapper().readValue(strJSON, clazz);
		} catch (Exception e) {
			throw new WiseRunException(e);
		}
	}

	private ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {

			@Override
			public void serialize(Object value, JsonGenerator jg, SerializerProvider sp)
					throws IOException, JsonProcessingException {
				jg.writeString("");
			}
		});
		objectMapper.setSerializationInclusion(Include.ALWAYS);
		objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		objectMapper.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, false);
		objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		return objectMapper;
	}

	/**
	 * 根据行业编码获取行业主键
	 * 
	 * @return
	 */
	private String getTradePk(String tradecode) {
		if (StringUtil.isEmpty(tradecode)) {
			return tradecode;
		}
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select pk_trade from ynt_bd_trade where pk_corp = '000001' and nvl(dr,0) = 0");
		sql.append(" and tradecode = ?");
		sp.addParam(tradecode);
		String pk_trade = sbo.executeQuery(sql.toString(), sp, new ColumnProcessor()).toString();
		return pk_trade;
	}

	/**
	 * 保存期初报表
	 * 
	 * @param initvo
	 * @Param userVO return pk_taxreportinitvo
	 * @throws DZFWarpException
	 */
	@Override
	public void saveInitReport(CqtcParamVO initvo) throws DZFWarpException {

		initvo.setPeriod(new DZFDate().toString().substring(0, 7));

		Map<String, TaxTypeSBZLVO> zlmap = getSbzlMapbycodezq();
		TaxTypeSBZLVO vo = zlmap.get(initvo.getSb_zlbh() + "," + initvo.getPeriodtype());
		if (vo == null)
			return;

		TaxDeclarationServiceImpl taximpl = new TaxDeclarationServiceImpl();

		TaxReportNewQcInitVO qcvo = queryReportQcByInit(initvo, vo.getPk_taxsbzl());

		if (qcvo == null) {
			qcvo = buildReportQcByInit(initvo, vo.getPk_taxsbzl());
		} else if (!StringUtil.isEmpty(qcvo.getSpreadfile())) {
			taximpl.delTaxReportFile(qcvo.getSpreadfile());
		}
		String fileName = TaxReportPath.taxReportPath + qcvo.getPk_corp() + "_" + qcvo.getPeriod() + "_"
				+ initvo.getSb_zlbh() + "_" + initvo.getPeriodtype() + ".ssjson";

//		String fileStr = OFastJSON.toJSONString(initvo.getMessage());
		String fileStr = JsonUtils.serialize(initvo.getMessage());
		String id = taximpl.uploadTaxReportFile(fileStr, fileName);

		qcvo.setSpreadfile(id);

		sbo.saveObject(qcvo.getPk_corp(), qcvo);

	}

	private TaxReportNewQcInitVO queryReportQcByInit(CqtcParamVO paramvo, String pk_taxsbzl) {
		String sql = "Select * From ynt_taxreportnewqcinit t Where"
				+ " t.pk_corp = ? and t.period = ? and t.pk_taxsbzl = ? and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(paramvo.getPk_corp());
		sp.addParam(paramvo.getPeriod());
		sp.addParam(pk_taxsbzl);

		TaxReportNewQcInitVO queryvo = (TaxReportNewQcInitVO) sbo.executeQuery(sql, sp,
				new BeanProcessor(TaxReportNewQcInitVO.class));

		return queryvo;
	}

	private TaxReportNewQcInitVO buildReportQcByInit(CqtcParamVO paramvo, String pk_taxsbzl) {
		TaxReportNewQcInitVO vo = new TaxReportNewQcInitVO();
		vo.setPk_corp(paramvo.getPk_corp());
		vo.setPeriod(paramvo.getPeriod());
		vo.setSb_zlbh(paramvo.getSb_zlbh());
		vo.setCoperatorid("");// 暂时不穿操作人
		vo.setPeriodtype(paramvo.getPeriodtype());
		vo.setPk_taxsbzl(pk_taxsbzl);
		vo.setDr(0);
		vo.setDoperatedate(new DZFDate());

		return vo;
	}

	public String uploadTaxReportFile(String file, String filename) {

		String id = null;
		try {
			id = ((FastDfsUtil) SpringUtils.getBean("connectionPool")).upload(file.getBytes(), filename,
					new HashMap<String, String>());
		} catch (Exception e) {
			throw new BusinessException("文件名:" + filename + "上传失败");
		}

		if (!StringUtil.isEmpty(id)) {
			id = "*" + id.substring(1);
		} else {
			throw new BusinessException("获取文件id失败!");
		}

		return id;
	}

	/**
	 * 以普通字符串方式保存spreadJS的json报表文件
	 * 
	 * @param filename
	 * @param data
	 * @throws DZFWarpException
	 */
	private void saveFile(String filename, String data) throws DZFWarpException {

		File f = new File(filename);
		if (f.exists()) {
			f.delete();
		}
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(f);
			fos.write(data.getBytes("utf-8"));
			fos.flush();

		} catch (IOException e) {
			throw new WiseRunException(e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					log.error("错误",e);
				}
			}
		}
	}

	private List<TaxTypeSBZLVO> queryTypeSBZLVOs() {
		SQLParameter sp = new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		TaxTypeSBZLVO[] vos = (TaxTypeSBZLVO[]) sbo.queryByCondition(TaxTypeSBZLVO.class,
				" nvl(dr,0) =0 and pk_corp = ? ", sp);
		if (vos == null || vos.length == 0)
			return null;
		List<TaxTypeSBZLVO> ltax = new ArrayList<TaxTypeSBZLVO>(Arrays.asList(vos));
		return ltax;
	}

	/**
	 * 按编号、申报周期，确定唯一值
	 * 
	 * @return
	 */
	private Map<String, TaxTypeSBZLVO> getSbzlMapbycodezq() {
		List<TaxTypeSBZLVO> zlist = queryTypeSBZLVOs();
		//
		Map<String, TaxTypeSBZLVO> zlmap = DZfcommonTools.hashlizeObjectByPk(zlist, new String[] { "sbcode", "sbzq" });
		return zlmap;
	}

	
	@Override
	public String saveTaxInfo(List<CorpVO> corp_list) throws DZFWarpException {
		if (corp_list == null || corp_list.size() == 0)
			return null;
		Map<String, TaxTypeSBZLVO> zlmap = getSbzlMapbycodezq();
		StringBuffer resMessage = new StringBuffer();
		CqtcRequestVO cqtcvo = new CqtcRequestVO();
		for (CorpVO corpvo : corp_list) {
			String corp_name = CodeUtils1.deCode(corpvo.getUnitname());
			try {
				String xml = CQXMLUtil.createQcXML(corpvo.getVsoccrecode());
				String result = doPost(xml, "utf-8");
				// String result = txt2String("D:/test.txt");
				String pk_corp = corpvo.getPk_corp();	
				List<CqtcRequestVO> result_list = CQXMLUtil.getResultList(result);
				String fileStr = "";
				String key = "";
			
				for (int i = 0; i < result_list.size(); i++) {
					if (!StringUtil.isEmpty(pk_corp)) {
						// 调用更新纳税人信息接口
						cqtcvo = result_list.get(i);
						if(!StringUtil.isEmpty(cqtcvo.getError_message())){
							resMessage.append(corp_name+" ：更新失败，" + cqtcvo.getError_message() +"\r\n");
							break;
						}
						updateTaxpayer10102(cqtcvo, corpvo);
						List<CorpTaxRptVO> reslist = updateTypeList(cqtcvo, corpvo);
						
						if (cqtcvo.getMessage_zzs0() != null && cqtcvo.getZzs0().booleanValue()) {
							// 增值税一般纳税人期初（月报无季报）
							fileStr = cqtcvo.getMessage_zzs0();
							key = TaxRptConst.SB_ZLBH10101 + "," + PeriodType.monthreport;
							saveInitVO(fileStr, pk_corp, zlmap.get(key));
						}
						if (cqtcvo.getMessage_zzs1() != null && !StringUtil.isEmpty(cqtcvo.getZzsyjb())
								&& cqtcvo.getZzsyjb().equals("30") && cqtcvo.getZzs1().booleanValue()) {
							// 增值税小规模纳税人季报期初
							fileStr = cqtcvo.getMessage_zzs1();
							key = TaxRptConst.SB_ZLBH10102 + "," + PeriodType.jidureport;
							saveInitVO(fileStr, pk_corp, zlmap.get(key));
						}
						if (cqtcvo.getMessage_zzs1() != null && !StringUtil.isEmpty(cqtcvo.getZzsyjb())
								&& cqtcvo.getZzsyjb().equals("20") && cqtcvo.getZzs1().booleanValue()) {
							// 增值税小规模纳税人月报期初
							fileStr = result_list.get(i).getMessage_zzs1();
							key = TaxRptConst.SB_ZLBH1010201 + "," + PeriodType.monthreport;
							saveInitVO(fileStr, pk_corp, zlmap.get(key));
						}
						if (cqtcvo.getMessage_sds0() != null && !StringUtil.isEmpty(cqtcvo.getSdsyjb())
								&& cqtcvo.getSdsyjb().equals("30") && cqtcvo.getSds0().booleanValue()) {
							// 企业所得税A季报期初
							fileStr = result_list.get(i).getMessage_sds0();
							key = TaxRptConst.SB_ZLBH10412 + "," + PeriodType.jidureport;
							saveInitVO(fileStr, pk_corp, zlmap.get(key));
						}
						if (cqtcvo.getMessage_sds0() != null && !StringUtil.isEmpty(cqtcvo.getSdsyjb())
								&& cqtcvo.getSdsyjb().equals("20") && cqtcvo.getSds0().booleanValue()) {
							// 企业所得税A月报期初
							fileStr = cqtcvo.getMessage_sds0();
							key = TaxRptConst.SB_ZLBH10412 + "," + PeriodType.monthreport;
							saveInitVO(fileStr, pk_corp, zlmap.get(key));
						}
						if (cqtcvo.getMessage_sds1() != null && !StringUtil.isEmpty(cqtcvo.getSdsyjb())
								&& cqtcvo.getSdsyjb().equals("30") && cqtcvo.getSds1().booleanValue()) {
							// 企业所得税B季报期初
							fileStr = result_list.get(i).getMessage_sds1();
							key = TaxRptConst.SB_ZLBH10413 + "," + PeriodType.jidureport;
							saveInitVO(fileStr, pk_corp, zlmap.get(key));
						}
						if (cqtcvo.getMessage_sds1() != null && !StringUtil.isEmpty(cqtcvo.getSdsyjb())
								&& cqtcvo.getSdsyjb().equals("20") && cqtcvo.getSds1().booleanValue()) {
							// 企业所得税B月报期初
							fileStr = cqtcvo.getMessage_sds1();
							key = TaxRptConst.SB_ZLBH10413 + "," + PeriodType.monthreport;
							saveInitVO(fileStr, pk_corp, zlmap.get(key));
						}
						resMessage.append(corp_name + "：更新成功"+"\r\n");

					}

				}

			} catch (Exception e) {
				log.info(e.getMessage());
				resMessage.append(corp_name + "：更新失败"+"\r\n");
			}
		}
		return resMessage.toString();
	}
	
	public List<CorpTaxRptVO> updateTypeList(CqtcRequestVO vo, CorpVO corpvo) throws DZFWarpException {
		// 清除勾选的报表
		String sql = "delete from ynt_taxrpt where pk_corp=?";
		SQLParameter params = new SQLParameter();
		params.addParam(corpvo.getPk_corp());
		sbo.executeUpdate(sql, params);
		List<CorpTaxRptVO> taxlist = getTaxReport(vo, corpvo);
		sbo.insertVOArr(corpvo.getPk_corp(), taxlist.toArray(new CorpTaxRptVO[0]));
		return taxlist;
	}

	private List<CorpTaxRptVO> getTaxReport(CqtcRequestVO vo, CorpVO corpvo) {
		// 企业所得税：总分机构 减免所得税优惠明细表(附表 3)、所得税月(季)度纳税申报表(A 类）、汇总纳税分支机构所得税分配表
		// 其他：总分机构 减免所得税优惠明细表(附表 3)、所得税月(季)度纳税申报表(A 类）
		List<CorpTaxRptVO> taxlist = new ArrayList<CorpTaxRptVO>();
		SQLParameter params = new SQLParameter();
		StringBuffer sb = new StringBuffer();
		sb.append("select a.*,b.sbzq from ynt_taxrpttemplet a inner join ynt_tax_sbzl b "
				+ "on a.pk_taxsbzl = b.pk_taxsbzl where nvl(a.dr,0)=0 and rtrim(a.location)=? order by  a.sb_zlbh");
		params.addParam("重庆");
		List<TaxRptTempletVO> votemplets = (List<TaxRptTempletVO>) sbo.executeQuery(sb.toString(), params,
				new BeanListProcessor(TaxRptTempletVO.class));
		for (TaxRptTempletVO tvo : votemplets) {
			CorpTaxRptVO taxvo = new CorpTaxRptVO();
			taxvo.setPk_taxrpttemplet(tvo.getPk_taxrpttemplet());
			taxvo.setTaxrptcode(tvo.getReportcode());
			taxvo.setTaxrptname(tvo.getReportname());
			taxvo.setPk_corp(corpvo.getPk_corp());
			String sb_zlbh = tvo.getSb_zlbh();
			if (corpvo.getChargedeptname().equals("小规模纳税人")) {
				if (vo.getZzsyjb().equals("20") && sb_zlbh.equals(TaxRptConst.SB_ZLBH1010201)
						&& vo.getZzs1().booleanValue()) {// 增值税月报
					if (tvo.getReportcode().equals("1010201001")) {
						taxlist.add(taxvo);
					} else if (tvo.getReportcode().equals("1010201004") && !StringUtil.isEmpty(vo.getJmdm())) {
						taxlist.add(taxvo);
					}
				} else if (!StringUtil.isEmpty(vo.getZzsyjb()) && vo.getZzsyjb().equals("30")
						&& sb_zlbh.equals(TaxRptConst.SB_ZLBH10102) && vo.getZzs1().booleanValue()) {// 增值税季报
					if (tvo.getReportcode().equals("10102001")) {
						taxlist.add(taxvo);
					} else if (tvo.getReportcode().equals("10102004") && !StringUtil.isEmpty(vo.getJmdm())) {
						taxlist.add(taxvo);
					}
				} else if (!StringUtil.isEmpty(vo.getSdsyjb()) && vo.getSdsyjb().equals("20")
						&& vo.getSdszs().equals("100") && sb_zlbh.equals(TaxRptConst.SB_ZLBH10412)
						&& vo.getSds0().booleanValue() && tvo.getSbzq() == PeriodType.monthreport) {// 所得税A月报
					if (tvo.getReportcode().equals("10412001")|| tvo.getReportcode().equals("10412002") || tvo.getReportcode().equals("10412003")|| tvo.getReportcode().equals("10412004")) {
						taxlist.add(taxvo);
					} else if (tvo.getReportcode().equals("10412005") && !StringUtil.isEmpty(vo.getZfjglx())
							&& (vo.getZfjglx().equals("2") || vo.getZfjglx().equals("3"))) {// 总分机构
						taxlist.add(taxvo);
					}
				} else if (!StringUtil.isEmpty(vo.getSdsyjb()) && vo.getSdsyjb().equals("30")
						&& vo.getSdszs().equals("100") && sb_zlbh.equals(TaxRptConst.SB_ZLBH10412)
						&& vo.getSds0().booleanValue() && tvo.getSbzq() == PeriodType.jidureport) {// 所得税A季报
					if (tvo.getReportcode().equals("10412001") || tvo.getReportcode().equals("10412002") || tvo.getReportcode().equals("10412003")|| tvo.getReportcode().equals("10412004")) {
						taxlist.add(taxvo);
					} else if (tvo.getReportcode().equals("10412005") && !StringUtil.isEmpty(vo.getZfjglx())
							&& (vo.getZfjglx().equals("2") || vo.getZfjglx().equals("3"))) {// 总分机构
						taxlist.add(taxvo);
					}
				} else if (!StringUtil.isEmpty(vo.getSdsyjb()) && vo.getSdsyjb().equals("20")
						&& vo.getSdszs().equals("400") && sb_zlbh.equals(TaxRptConst.SB_ZLBH10413)
						&& vo.getSds0().booleanValue() && tvo.getSbzq() == PeriodType.monthreport) {// 所得税B月报
					if (tvo.getReportcode().equals("10413001")) {
						taxlist.add(taxvo);
					}
				} else if (!StringUtil.isEmpty(vo.getSdsyjb()) && vo.getSdsyjb().equals("30")
						&& vo.getSdszs().equals("400") && sb_zlbh.equals(TaxRptConst.SB_ZLBH10413)
						&& vo.getSds0().booleanValue() && tvo.getSbzq() == PeriodType.jidureport) {// 所得税B季报
					if (tvo.getReportcode().equals("10413001")) {
						taxlist.add(taxvo);
					}
				} else if (TaxRptConst.KJQJ_2013.equals(corpvo.getCorptype()) && vo.getZzsyjb().equals("30")&& vo.getZzs1().booleanValue()
						&& sb_zlbh.equals(TaxRptConst.SB_ZLBHC1) && tvo.getSbzq() == PeriodType.jidureport
						&& (tvo.getReportcode().equals("C1001") || tvo.getReportcode().equals("C1002"))) {// 2013
																											// //
																											// C1
					taxlist.add(taxvo);
				} else if (TaxRptConst.KJQJ_2007.equals(corpvo.getCorptype()) && vo.getZzsyjb().equals("30")&& vo.getZzs1().booleanValue()
						&& sb_zlbh.equals(TaxRptConst.SB_ZLBHC2) && tvo.getSbzq() == PeriodType.jidureport
						&& (tvo.getReportcode().equals("C2001") || tvo.getReportcode().equals("C2002"))) {// 2007//
																											// //
																											// C2
					taxlist.add(taxvo);
				} else if (TaxRptConst.KJQJ_2013.equals(corpvo.getCorptype()) && vo.getZzsyjb().equals("20")&& vo.getZzs1().booleanValue()
						&& sb_zlbh.equals(TaxRptConst.SB_ZLBHC1) && tvo.getSbzq() == PeriodType.monthreport
						&& (tvo.getReportcode().equals("C1001") || tvo.getReportcode().equals("C1002"))) {// 2013
																											// //
																											// C1
					taxlist.add(taxvo);
				} else if (TaxRptConst.KJQJ_2007.equals(corpvo.getCorptype()) && vo.getZzsyjb().equals("20")&& vo.getZzs1().booleanValue()
						&& sb_zlbh.equals(TaxRptConst.SB_ZLBHC2) && tvo.getSbzq() == PeriodType.monthreport
						&& (tvo.getReportcode().equals("C2001") || tvo.getReportcode().equals("C2002"))) {// 2007//
																											// //
																											// C2
					taxlist.add(taxvo);
				} else if (TaxRptConst.KJQJ_QYKJZD.equals(corpvo.getCorptype()) && vo.getZzsyjb().equals("30")  &&vo.getZzs0().booleanValue()
						&& sb_zlbh.equals(TaxRptConst.SB_ZLBH29805) && tvo.getSbzq() == PeriodType.jidureport
						&& (tvo.getReportcode().equals("29805001") || tvo.getReportcode().equals("29805002"))) {// 29805//
																											// //
																											// 29805
					taxlist.add(taxvo);
				} else if (TaxRptConst.KJQJ_QYKJZD.equals(corpvo.getCorptype())&& vo.getZzsyjb().equals("20") && vo.getZzs0().booleanValue()
						&& sb_zlbh.equals(TaxRptConst.SB_ZLBH29805) && tvo.getSbzq() == PeriodType.monthreport
						&& (tvo.getReportcode().equals("29805001") || tvo.getReportcode().equals("29805002"))) {// 29805//
																											// //
																											//29805
					taxlist.add(taxvo);
				}else if (vo.getZzs1().booleanValue() && sb_zlbh.equals(TaxRptConst.SB_ZLBH50102)
						&&vo.getZzsyjb().equals("20")&& tvo.getReportcode().equals("50102001") && tvo.getSbzq() == PeriodType.monthreport) {
					taxlist.add(taxvo);
				} else if (vo.getZzs1().booleanValue() && sb_zlbh.equals(TaxRptConst.SB_ZLBH50102)
						&& vo.getZzsyjb().equals("30") && tvo.getReportcode().equals("50102001")
						&& tvo.getSbzq() == PeriodType.jidureport) {
					taxlist.add(taxvo);
				}
			} else {// 一般纳税人
				if (sb_zlbh.equals(TaxRptConst.SB_ZLBH10101) && vo.getZzs0().booleanValue()) {// 一般纳税人增值税月报无季报
					if (tvo.getReportcode().equals("10101001") || tvo.getReportcode().equals("10101002")
							|| tvo.getReportcode().equals("10101003") || tvo.getReportcode().equals("10101004")
							|| tvo.getReportcode().equals("10101005") || tvo.getReportcode().equals("10101023")) {
						taxlist.add(taxvo);
					} else if (tvo.getReportcode().equals("10101004") && !StringUtil.isEmpty(vo.getJmdm())) {
						taxlist.add(taxvo);
					}
				} else if (!StringUtil.isEmpty(vo.getSdsyjb()) && vo.getSdsyjb().equals("20")
						&& vo.getSdszs().equals("100") && sb_zlbh.equals("1041201") && vo.getSds0().booleanValue()
						&& tvo.getSbzq() == PeriodType.monthreport) {// 所得税A月报
					if (tvo.getReportcode().equals("10412001")|| tvo.getReportcode().equals("10412002") || tvo.getReportcode().equals("10412003") || tvo.getReportcode().equals("10412004")) {
						taxlist.add(taxvo);
					} else if (tvo.getReportcode().equals("10412005") && !StringUtil.isEmpty(vo.getZfjglx())
							&& (vo.getZfjglx().equals("2") || vo.getZfjglx().equals("3"))) {// 总分机构
						taxlist.add(taxvo);
					}
				} else if (!StringUtil.isEmpty(vo.getSdsyjb()) && vo.getSdsyjb().equals("30")
						&& vo.getSdszs().equals("100") && sb_zlbh.equals(TaxRptConst.SB_ZLBH10412)
						&& vo.getSds0().booleanValue() && tvo.getSbzq() == PeriodType.jidureport) {// 所得税A季报
					if (tvo.getReportcode().equals("10412001") || tvo.getReportcode().equals("10412002") || tvo.getReportcode().equals("10412003")|| tvo.getReportcode().equals("10412004")) {
						taxlist.add(taxvo);
					} else if (tvo.getReportcode().equals("10412005") && !StringUtil.isEmpty(vo.getZfjglx())
							&& (vo.getZfjglx().equals("2") || vo.getZfjglx().equals("3"))) {// 总分机构
						taxlist.add(taxvo);
					}
				} else if (!StringUtil.isEmpty(vo.getSdsyjb()) && vo.getSdsyjb().equals("20")
						&& vo.getSdszs().equals("400") && sb_zlbh.equals(TaxRptConst.SB_ZLBH10413)
						&& vo.getSds0().booleanValue() && tvo.getSbzq() == PeriodType.monthreport) {// 所得税B月报
					if (tvo.getReportcode().equals("10413001")) {
						taxlist.add(taxvo);
					}
				} else if (!StringUtil.isEmpty(vo.getSdsyjb()) && vo.getSdsyjb().equals("30")
						&& vo.getSdszs().equals("400") && sb_zlbh.equals(TaxRptConst.SB_ZLBH10413)
						&& vo.getSds0().booleanValue() && tvo.getSbzq() == PeriodType.jidureport) {// 所得税B季报
					if (tvo.getReportcode().equals("10413001")) {
						taxlist.add(taxvo);
					}
				} else if (TaxRptConst.KJQJ_2013.equals(corpvo.getCorptype()) && vo.getZzsyjb().equals("30")  &&vo.getZzs0().booleanValue()
						&& sb_zlbh.equals(TaxRptConst.SB_ZLBHC1) && tvo.getSbzq() == PeriodType.jidureport
						&& (tvo.getReportcode().equals("C1001") || tvo.getReportcode().equals("C1002"))) {// 2013
																											// //
																											// C1
					taxlist.add(taxvo);
				} else if (TaxRptConst.KJQJ_2013.equals(corpvo.getCorptype()) && vo.getZzsyjb().equals("20")&& vo.getZzs0().booleanValue()
						&& sb_zlbh.equals(TaxRptConst.SB_ZLBHC1) && tvo.getSbzq() == PeriodType.monthreport
						&& (tvo.getReportcode().equals("C1001") || tvo.getReportcode().equals("C1002"))) {// 2013
																											// //
																											// C1
					taxlist.add(taxvo);
				} else if (TaxRptConst.KJQJ_2007.equals(corpvo.getCorptype()) && vo.getZzsyjb().equals("30")  &&vo.getZzs0().booleanValue()
						&& sb_zlbh.equals(TaxRptConst.SB_ZLBHC2) && tvo.getSbzq() == PeriodType.jidureport
						&& (tvo.getReportcode().equals("C2001") || tvo.getReportcode().equals("C2002"))) {// 2007//
																											// //
																											// C2
					taxlist.add(taxvo);
				} else if (TaxRptConst.KJQJ_2007.equals(corpvo.getCorptype())&& vo.getZzsyjb().equals("20") && vo.getZzs0().booleanValue()
						&& sb_zlbh.equals(TaxRptConst.SB_ZLBHC2) && tvo.getSbzq() == PeriodType.monthreport
						&& (tvo.getReportcode().equals("C2001") || tvo.getReportcode().equals("C2002"))) {// 2007//
																											// //
																											// C2
					taxlist.add(taxvo);
				}else if (TaxRptConst.KJQJ_QYKJZD.equals(corpvo.getCorptype()) && vo.getZzsyjb().equals("30")  &&vo.getZzs0().booleanValue()
						&& sb_zlbh.equals(TaxRptConst.SB_ZLBH29805) && tvo.getSbzq() == PeriodType.jidureport
						&& (tvo.getReportcode().equals("29805001") || tvo.getReportcode().equals("29805002"))) {// 29805//
																											// //
																											// 29805
					taxlist.add(taxvo);
				} else if (TaxRptConst.KJQJ_QYKJZD.equals(corpvo.getCorptype())&& vo.getZzsyjb().equals("20") && vo.getZzs0().booleanValue()
						&& sb_zlbh.equals(TaxRptConst.SB_ZLBH29805) && tvo.getSbzq() == PeriodType.monthreport
						&& (tvo.getReportcode().equals("29805001") || tvo.getReportcode().equals("29805002"))) {// 29805//
																											// //
																											//29805
					taxlist.add(taxvo);
				}  else if (vo.getZzs0().booleanValue() && sb_zlbh.equals(TaxRptConst.SB_ZLBH50101)
						&& vo.getZzsyjb().equals("20")&& tvo.getReportcode().equals("50101001") && tvo.getSbzq() == PeriodType.monthreport) {
					taxlist.add(taxvo);
				} else if (vo.getZzs0().booleanValue() && sb_zlbh.equals(TaxRptConst.SB_ZLBH50101)
						&& vo.getZzsyjb().equals("30") && tvo.getReportcode().equals("50101001")
						&& tvo.getSbzq() == PeriodType.jidureport) {
					taxlist.add(taxvo);
				}
			}

		}
		return taxlist;
	}

	// 更新小规模纳税人信息
	private void updateTaxpayer10102(CqtcRequestVO vo, CorpVO corpVo) {
		String[] updateAttr = new String[] { "ikjzc", "unitname", "vsoccrecode", "legalbodycode", "postaddr",
				"industry", "phone1", "destablishdate", "taxlevytype" };
		CQTaxInfoVO taxInfo = new CQTaxInfoVO();
		taxInfo.setPrimaryKey(corpVo.getPrimaryKey());
		taxInfo.setUnitname(CodeUtils1.enCode(vo.getTaxname())); // 纳税人名称
		taxInfo.setVsoccrecode(vo.getTaxno()); // 纳税人识别号
		taxInfo.setRegion(vo.getDjxh());						  
		taxInfo.setPostaddr(vo.getZcdz()); // 注册地址
		taxInfo.setTaxlevytype(change2DZFZsfs(vo.getSdszs())); // 征收方式
		if (!StringUtil.isEmpty(vo.getNsrzg()) && (vo.getNsrzg().equals("0101") || vo.getNsrzg().equals("0102"))) {
			taxInfo.setChargedeptname(change2DZFChargedeptname(vo.getNsrzg())); // 公司性质
																				// --纳税人资格
	updateAttr = new String[] { "ikjzc", "unitname", "vsoccrecode", "legalbodycode", "postaddr",
					"chargedeptname", "industry", "phone1", "destablishdate", "taxlevytype","region" };
		}
		taxInfo.setIkjzc(change2DZFKjzc(vo.getKjzd())); // 会计制度
		taxInfo.setPhone1(vo.getDh()); // 电话
		if (!StringUtil.isEmpty(vo.getKysj())) {
			taxInfo.setDestablishdate(new DZFDate(vo.getKysj())); // 开业设立时间
		}
		if (!StringUtil.isEmpty(vo.getFr())) {
			taxInfo.setLegalbodycode(CodeUtils1.enCode(vo.getFr())); // 法人
		}
		if (!StringUtil.isEmpty(vo.getHy())) {
			taxInfo.setIndustry(getTradePk(vo.getHy())); // 行业名称
		}
		sbo.update(taxInfo, updateAttr);
	}

	// 转换成大账房公司性质
	private String change2DZFChargedeptname(String chargedeptCode) {
		if ("0101".equals(chargedeptCode)) {
			return "一般纳税人";
		} else if ("0102".equals(chargedeptCode)) {
			return "小规模纳税人";
		}
		return null;
	}

	// 转换成大账房征收方式
	private Integer change2DZFZsfs(String zsfs) {
		if ("100".equals(zsfs)) {// 查账征收
			return 1;
		} else if ("400".equals(zsfs)) {// 核定征收
			return 0;
		}
		return null;
	}

	// 转换成大账房会计政策编码
	private Integer change2DZFKjzc(String kjzc) {
		if ("102".equals(kjzc)) { // 小企业会计准则
			return 1;
		} else if ("101".equals(kjzc)) { // 企业会计准则
			return 0;
		} else if ("201".equals(kjzc)) {// 企业会计制度
			return 2;
		}
		return null;
	}

	private void saveInitVO(String fileStr, String pk_corp, TaxTypeSBZLVO vo) {
		if (vo == null)
			return;
		String sb_zlbh = vo.getSbcode();
		int periodType = vo.getSbzq();
		String period = "";
		if (periodType == PeriodType.monthreport) {
			period = new DZFDate(new DZFDate().toString().substring(0, 7) + "-01").getDateBefore(1).toString()
					.substring(0, 7);
		} else if (periodType == PeriodType.jidureport) {
			period = getQuarterStartDate(new DZFDate().toString().substring(0, 7));
		}
		CqtcParamVO param = new CqtcParamVO();
		param.setPk_corp(pk_corp);
		param.setPeriod(period);
		param.setPeriodtype(periodType);
		param.setSb_zlbh(sb_zlbh);

		String pk_taxsbzl = vo.getPk_taxsbzl();
		TaxDeclarationServiceImpl taximpl = new TaxDeclarationServiceImpl();

		TaxReportNewQcInitVO qcvo = queryReportQcByInit(param, pk_taxsbzl);

		if (qcvo == null) {
			qcvo = buildReportQcByInit(param, pk_taxsbzl);
		} else if (!StringUtil.isEmpty(qcvo.getSpreadfile())) {
			taximpl.delTaxReportFile(qcvo.getSpreadfile());
		}
		String fileName = TaxReportPath.taxReportPath + qcvo.getPk_corp() + "_" + qcvo.getPeriod() + "_" + sb_zlbh + "_"
				+ periodType + ".ssjson";
		String id = taximpl.uploadTaxReportFile(fileStr, fileName);

		qcvo.setSpreadfile(id);

		sbo.saveObject(qcvo.getPk_corp(), qcvo);

	}

	protected String getQuarterStartDate(String yearmonth) {
		int iYear = Integer.parseInt(yearmonth.substring(0, 4));
		int iMonth = Integer.parseInt(yearmonth.substring(5, 7));
		switch (iMonth) {
		case 1:
		case 2:
		case 3: {
			return "" + (iYear - 1) + "-12";
		}
		case 4:
		case 5:
		case 6: {
			return "" + iYear + "-03";
		}
		case 7:
		case 8:
		case 9: {
			return "" + iYear + "-06";
		}
		case 10:
		case 11:
		case 12: {
			return "" + iYear + "-09";
		}
		}
		return null;

	}

	private static String doPost(String xml, String charset) {
//		String url = CqtcPropertyUtils.getProperties().getProperty("url");
		String url = cqtcConfig.url;
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			httpClient = HttpClientBuilder.create().build();
			httpPost = new HttpPost(url);
			/*
			 * RequestConfig requestConfig =
			 * RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(
			 * 5000).build(); httpPost.setConfig(requestConfig);
			 */
			DESHelper des = new DESHelper();
			// 设置参数
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("SEND_XML", des.encrypt(xml, "dzftcfwj".getBytes())));
			if (list.size() > 0) {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
				httpPost.setEntity(entity);
			}
			HttpResponse response = (HttpResponse) httpClient.execute(httpPost);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
					result = des.decrypt(result, "dzftcfwj".getBytes());
					log.error("doPost result diagram :"+  result);	
				}
			}
		} catch (Exception ex) {
			log.info(ex.getMessage());
			throw new BusinessException("调用报税接口异常请重试");
		}
		return result;
	}

	@Override
	public void saveSbzt10102(CorpVO corpvo) throws DZFWarpException {
		if (corpvo == null)
			return;
		// 请求重庆天畅申报状态接口
		String xml = CQXMLUtil.createSbztReqXML(corpvo.getVsoccrecode());
		//非重庆渠道申报请求
		String XMLOtherCHN =CQXMLUtil.createSbztReqXML_OtherCHN(corpvo.getVsoccrecode(),corpvo.getRegion());
		List<CqtcSbtzResultVO> list = null;
		try {
			String result = doPost(xml, "utf-8");
			// String result = CQXMLUtil.createSbztResultXML();.
			list = CQXMLUtil.getSbztResultList(result);
			
			if(XMLOtherCHN !=null){
				//暂时不向上抛出报告登记序号的异常情况
				try {
					String result_OtherCHN =doPost(XMLOtherCHN, "utf-8"); 
					// String result = CQXMLUtil.createSbztResultXML();
					List<CqtcSbtzResultVO>  templist = CQXMLUtil.getSbztResultList(result_OtherCHN);
					mergeSet(list,templist);
					
				} catch (Exception e){
					log.error("根据登记序号查询状态出现异常：" + e.getMessage(),e);
				}
			}
		} catch (Exception e) {
			log.error("获取申报状态数据失败：" + e.getMessage(),e);
			throw new BusinessException("获取申报状态失败!" + e.getMessage());
		}

		String nowDate = new DZFDate().toString();
		String yearmonth = nowDate.substring(0, 7);
		int year = Integer.valueOf(yearmonth.substring(0, 4)) - 1; //上一年，用于年报
		String sQueryPeriod = new DZFDate(yearmonth + "-01").getDateBefore(1).toString().substring(0, 7);
		TaxReportVO param = new TaxReportVO();
		ArrayList taxreport_list = new ArrayList();
		if (list != null && list.size() > 0) {
			for (CqtcSbtzResultVO vo : list) {
				param.setPk_corp(corpvo.getPk_corp());
				param.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
				param.setSbzt_dm(change2DZFSbzt(vo.getSbzt()) + "");
				if ("zzs0".equals(vo.getSzdm())) { // 增值税一般纳税人
					param.setSb_zlbh(TaxRptConst.SB_ZLBH10101);
					// 更新申报状态
					taxreport_list = updateRptSbzt(param, taxreport_list);
				} else if ("zzs1".equals(vo.getSzdm())) { // 增值税小规模纳税人
					param.setSb_zlbh(TaxRptConst.SB_ZLBH10102); // (包含季报、月报)
					taxreport_list = updateRptSbzt(param, taxreport_list);
				} else if ("sds0".equals(vo.getSzdm())) { // 所得税A类
					// if ("小规模纳税人".equals(corpVO.getChargedeptname())) {
					// param.setSb_zlbh(TaxRptConst.SB_ZLBH10412);
					// } else { // 一般纳税人
					// param.setSb_zlbh(TaxRptConst.SB_ZLBH10412);
					// }
					param.setSb_zlbh(TaxRptConst.SB_ZLBH10412);
					taxreport_list = updateRptSbzt(param, taxreport_list);
				} else if ("sds1".equals(vo.getSzdm())) { // 所得税B类
					// if ("小规模纳税人".equals(corpVO.getChargedeptname())) {
					// param.setSb_zlbh(TaxRptConst.SB_ZLBH10412);
					// } else { // 一般纳税人
					// param.setSb_zlbh(TaxRptConst.SB_ZLBH10412);
					// }
					param.setSb_zlbh(TaxRptConst.SB_ZLBH10413);
					taxreport_list = updateRptSbzt(param, taxreport_list);
				}else if ("cwbb6".equals(vo.getSzdm())) {// 小企业财报
					// if ("小规模纳税人".equals(corpVO.getChargedeptname())) {
					// param.setSb_zlbh(CqtcZLBHConst.SB_ZLBHC1);
					// } else { // 一般纳税人
					// param.setSb_zlbh(CqtcZLBHConst.SB_ZLBHC3);
					// }
					param.setSb_zlbh(TaxRptConst.SB_ZLBHC1);// 小企业季报
					taxreport_list = updateRptSbzt(param, taxreport_list);
				} else if ("cwbb1".equals(vo.getSzdm())) {// 一般企业财报
					// if ("小规模纳税人".equals(corpVO.getChargedeptname())) {
					// param.setSb_zlbh(CqtcZLBHConst.SB_ZLBHC2);
					// } else { // 一般纳税人
					// param.setSb_zlbh(CqtcZLBHConst.SB_ZLBHC4);
					// }
					param.setSb_zlbh(TaxRptConst.SB_ZLBHC2);// 一般企业季报
					taxreport_list = updateRptSbzt(param, taxreport_list);
				} else if ("cwbbnb6".equals(vo.getSzdm())) {// 小企业年报
					// if ("小规模纳税人".equals(corpVO.getChargedeptname())) {
					// param.setSb_zlbh(CqtcZLBHConst.SB_ZLBHC2);
					// } else { // 一般纳税人
					// param.setSb_zlbh(CqtcZLBHConst.SB_ZLBHC4);
					// }
					param.setPeriodto(year + "-12-31");
					param.setSb_zlbh(TaxRptConst.SB_ZLBH39806);// 小企业年报
					taxreport_list = updateRptSbzt(param, taxreport_list);
				} else if ("cwbbnb1".equals(vo.getSzdm())) {// 一般企业年报
					// if ("小规模纳税人".equals(corpVO.getChargedeptname())) {
					// param.setSb_zlbh(CqtcZLBHConst.SB_ZLBHC2);
					// } else { // 一般纳税人
					// param.setSb_zlbh(CqtcZLBHConst.SB_ZLBHC4);
					// }
					param.setPeriodto(year + "-12-31");
					param.setSb_zlbh(TaxRptConst.SB_ZLBH39801);// 一般企业年报
					taxreport_list = updateRptSbzt(param, taxreport_list);
				}else {
					log.error("无税种代码的报表  note:" + vo.getNote());
				}
			}
		}
		TaxReportVO vo = new TaxReportVO();
		vo.setPk_corp(corpvo.getPk_corp());
		vo.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
		updateRptSbztForNothing(vo, taxreport_list);

	}
	
	/**
	 * 合并list集合
	 * @param list
	 * @param templist
	 */
	private void mergeSet(List<CqtcSbtzResultVO> list, List<CqtcSbtzResultVO> templist) {
		
		Map<String,String> tempMap = new HashMap<String,String>();
		for (int i = 0; i < list.size(); i++) {
			tempMap.put(list.get(i).getSzdm(), null);
		}
		
		if (!templist.isEmpty()){
			for (int i = 0; i < templist.size(); i++) {
				if(!tempMap.keySet().contains(templist.get(i).getSzdm())){
					list.add(templist.get(i));
				}
			}
		}
		
	}

	// 更新申报表的主子表状态
	private ArrayList updateRptSbzt(TaxReportVO param, ArrayList taxreport_list) {
		String condition = " pk_corp=? and nvl(dr,0)=0 and instr(sb_zlbh,?)=1 and periodto=? ";
		SQLParameter params = new SQLParameter();
		params.addParam(param.getPk_corp());
		params.addParam(param.getSb_zlbh());
		params.addParam(param.getPeriodto());
		TaxReportVO[] rpts = (TaxReportVO[]) sbo.queryByCondition(TaxReportVO.class, condition, params);
		if (rpts == null || rpts.length == 0) {
			throw new BusinessException("无对应纳税申报表，更新申报状态失败!");
		}
		TaxReportVO reportvo = rpts[0];
		reportvo.setSbzt_dm(param.getSbzt_dm());
		sbo.update(reportvo, new String[] { "sbzt_dm" });
		StringBuffer sb = new StringBuffer();
		sb.append("update ynt_taxreportdetail set sbzt_dm = ? where pk_taxreport = ? and nvl(dr, 0) = 0 ");
		params.clearParams();
		params.addParam(param.getSbzt_dm());
		params.addParam(reportvo.getPk_taxreport());
		sbo.executeUpdate(sb.toString(), params);
		taxreport_list.add(reportvo.getPk_taxreport());
		return taxreport_list;
	}
	
	
	
	
	//本接口不维护的税种状态
    private static String sb_szbm  = new StringBuilder().append("('"+TaxRptConst.SB_ZLBH50101+"'").append(",'"+TaxRptConst.SB_ZLBH50102+"'").append(
    		",'"+TaxRptConst.SB_ZLBHGS+"'").append(",'"+TaxRptConst.SB_ZLBHD1+"')").toString();
    
	private void updateRptSbztForNothing(TaxReportVO param, ArrayList taxreport_list) {
		SQLParameter params = new SQLParameter();
		//String condition = " pk_corp=?  and nvl(dr,0)=0 and Sbzt_dm = ? and sb_zlbh not in( ? )  ";
		String condition = " pk_corp=?  and nvl(dr,0)=0  and sb_zlbh not in   " + sb_szbm;
		params.addParam(param.getPk_corp());
		/*params.addParam(TaxRptConst.iSBZT_DM_ReportSuccess);*/
		String periodto = param.getPeriodto();
		String periodYear = (Integer.valueOf(periodto.substring(0, 4)) - 1) + "-12-31";
		DZFDate currentDate = new DZFDate();
		if(currentDate.getMonth() > 5){
			condition += " and periodto = ? ";
			params.addParam(periodto);
		}else{
			condition += " and (periodto = ? or periodto = ? ) ";
			params.addParam(periodto);
			params.addParam(periodYear);
		}
		
		TaxReportVO[] rpts = (TaxReportVO[]) sbo.queryByCondition(TaxReportVO.class, condition, params);
		for (int i = 0; i < rpts.length; i++) {
			TaxReportVO reportvo = rpts[i];
			if (taxreport_list == null || taxreport_list.size() == 0 || (taxreport_list != null
					&& taxreport_list.size() > 0 && !taxreport_list.contains(reportvo.getPk_taxreport()))) {
				reportvo.setSbzt_dm("101");
				sbo.update(reportvo, new String[] { "sbzt_dm" });
				StringBuffer sb = new StringBuffer();
				sb.append("update ynt_taxreportdetail set sbzt_dm = ? where pk_taxreport = ? and nvl(dr, 0) = 0 ");
				params.clearParams();
				params.addParam(TaxRptConst.iSBZT_DM_UnSubmit);
				params.addParam(reportvo.getPk_taxreport());
				sbo.executeUpdate(sb.toString(), params);
			}
		}

	}

	// 转为DZF申报状态
	private Integer change2DZFSbzt(String sbzt) {
		if ("3".equals(sbzt)) { // 失败
			return 3;
		} else if ("2".equals(sbzt)) { // 成功
			return 4;
		} else if ("1".equals(sbzt)) { // 受理中
			return 0;
		}
		return null;
	}

	public static String txt2String(String fileName) {
		BufferedReader br = null;
		StringBuffer sb = null;
		String res = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "utf-8")); // 这里可以控制编码
			sb = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception e) {
			}
		}
		if (sb != null) {
			res = sb.toString();
		}
		return res;
	}
}
