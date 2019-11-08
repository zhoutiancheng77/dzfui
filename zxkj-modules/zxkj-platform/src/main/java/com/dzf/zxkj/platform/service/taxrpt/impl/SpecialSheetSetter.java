package com.dzf.zxkj.platform.service.taxrpt.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.lang.*;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxReportDetailVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;
import com.dzf.zxkj.platform.util.QueryDeCodeUtils;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class SpecialSheetSetter {

	private SingleObjectBO sbo;
	private SpreadTool spreadtool;
	private ICorpService corpService;

	public SpecialSheetSetter(SingleObjectBO sbo, SpreadTool spreadtool, ICorpService corpService) {
		this.sbo = sbo;
		this.spreadtool = spreadtool;
		this.corpService = corpService;
	}

	public void setSpecialSheetDefaultValue(TaxReportVO reportvo, Map objMapRet)
			throws JsonParseException, JsonMappingException, IOException {

		if ("A".equals(reportvo.getSb_zlbh())) { // 企业所得税年报
			setSpecialValueA(reportvo, objMapRet);
		}
	}

	private void setSpecialValueA(TaxReportVO reportvo, Map objMapRet)
			throws JsonParseException, JsonMappingException, IOException {

		Map lastObjMap = getLastPeriodMapData(reportvo);

		LinkedHashMap hmsheets = (LinkedHashMap) objMapRet.get("sheets");
		Iterator iter = hmsheets.entrySet().iterator();

		// 保留下的报表hashmap
//		HashMap<String, LinkedHashMap> hmReports = new HashMap<String, LinkedHashMap>();
		// 遍历报表
//		List<String> listDeleteSheet = new ArrayList<String>();

		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			// 报表名称
			String reportname = ((String) entry.getKey()).trim();

			if ("封面".equals(reportname)) {

				int[][] pos = new int[][] { { 6, 2, }, { 6, 5 }, { 9, 2 }, { 11, 2, }, { 23, 3 }, { 23, 7 }, { 26, 0, },
						{ 26, 3 }, { 26, 6 }, { 30, 1 }, { 30, 4 }, { 30, 7 }, { 32, 3 }, { 33, 1 }, { 33, 5 },
						{ 33, 7 } };
				int len = pos.length;
				for (int i = 0; i < len; i++) {
					Object value = null;
					if (lastObjMap != null && lastObjMap.size() > 0) {
						value = spreadtool.getCellValue(lastObjMap, reportname, pos[i][0], pos[i][1]);
						if (value instanceof Integer) {
							value = Integer.toString((Integer) value);
						} else if (value instanceof DZFDouble) {
							value = ((DZFDouble) value).toString();
						} else if (value instanceof DZFDate) {
							value = ((DZFDate) value).toString();
						} else if (value instanceof DZFBoolean) {
							value = ((DZFBoolean) value).toString();
						} else if (value instanceof DZFDateTime) {
							value = ((DZFDateTime) value).toString();
						} else if (value instanceof DZFTime) {
							value = ((DZFTime) value).toString();
						}
					}

					if (pos[i][0] == 6 && pos[i][1] == 2) {// 税款所属期间：
						spreadtool.setCellValue(objMapRet, reportname, pos[i][0], pos[i][1], reportvo.getPeriodfrom());
					} else if (pos[i][0] == 6 && pos[i][1] == 5) {// 税款所属期间：
						spreadtool.setCellValue(objMapRet, reportname, pos[i][0], pos[i][1], reportvo.getPeriodto());
					} else if (pos[i][0] == 9 && pos[i][1] == 2) {// 纳税人识别号：
						if (!StringUtil.isEmpty((String) value)) {
							spreadtool.setCellValue(objMapRet, reportname, pos[i][0], pos[i][1], (String) value);
						} else {
							CorpVO corpvo = corpService.queryByPk(reportvo.getPk_corp());
							if (!StringUtil.isEmpty(corpvo.getVsoccrecode())) {
								spreadtool.setCellValue(objMapRet, reportname, pos[i][0], pos[i][1],
										corpvo.getVsoccrecode());
							} else {
								corpvo = (CorpVO) sbo.queryByPrimaryKey(CorpVO.class, reportvo.getPk_corp());
								if (corpvo != null) {
									if (!StringUtil.isEmpty(corpvo.getVsoccrecode())) {
										spreadtool.setCellValue(objMapRet, reportname, pos[i][0], pos[i][1],
												corpvo.getVsoccrecode());
									}
								}
							}
						}
					} else if (pos[i][0] == 11 && pos[i][1] == 2) {// 纳税人名称：
						if (!StringUtil.isEmpty((String) value)) {
							spreadtool.setCellValue(objMapRet, reportname, pos[i][0], pos[i][1], (String) value);
						} else {
							CorpVO corpvo = corpService.queryByPk(reportvo.getPk_corp());
							if (!StringUtil.isEmpty(corpvo.getUnitname())) {
								spreadtool.setCellValue(objMapRet, reportname, pos[i][0], pos[i][1],
										corpvo.getUnitname());
							}
						}
					} else if (pos[i][0] == 23 && pos[i][1] == 3) {// 法定代表人（签章）:
						if (!StringUtil.isEmpty((String) value)) {
							spreadtool.setCellValue(objMapRet, reportname, pos[i][0], pos[i][1], (String) value);
						} else {
							CorpVO corpvo = corpService.queryByPk(reportvo.getPk_corp());
							CorpVO[] corpvos = (CorpVO[]) QueryDeCodeUtils.decKeyUtils(new String[] { "legalbodycode" },
									new CorpVO[] { corpvo }, 1);
							if (!StringUtil.isEmpty(corpvos[0].getLegalbodycode())) {
								spreadtool.setCellValue(objMapRet, reportname, pos[i][0], pos[i][1],
										corpvos[0].getLegalbodycode());
							} else {
								corpvo = (CorpVO) sbo.queryByPrimaryKey(CorpVO.class, reportvo.getPk_corp());
								if (corpvo != null) {
									corpvos = (CorpVO[]) QueryDeCodeUtils.decKeyUtils(new String[] { "legalbodycode" },
											new CorpVO[] { corpvo }, 1);
									if (!StringUtil.isEmpty(corpvos[0].getLegalbodycode())) {
										spreadtool.setCellValue(objMapRet, reportname, pos[i][0], pos[i][1],
												corpvos[0].getLegalbodycode());
									}
								}
							}
						}
					} else {
						if (!StringUtil.isEmpty((String) value)) {
							spreadtool.setCellValue(objMapRet, reportname, pos[i][0], pos[i][1], (String) value);
						}
					}
				}
			}
		}
	}

	// 取得上期数据
	private Map getLastPeriodMapData(TaxReportVO reportvo)
			throws JsonParseException, JsonMappingException, IOException {

		int lastyear = new DZFDate(reportvo.getPeriodto()).getYear() - 1;
		SQLParameter params = new SQLParameter();
		String condition = " nvl(dr,0)=0  and pk_corp =? and location =? and sb_zlbh =? and  periodfrom =? and periodto =? ";
		params.addParam(reportvo.getPk_corp());
		params.addParam(reportvo.getLocation());
		params.addParam(reportvo.getSb_zlbh());
		params.addParam(lastyear + "-01-01");
		params.addParam(lastyear + "-12-31");
		TaxReportVO[] vos1 = (TaxReportVO[]) sbo.queryByCondition(TaxReportVO.class, condition, params);

		if (vos1 == null || vos1.length == 0)
			return null;

		params.clearParams();
		params.addParam(vos1[0].getPk_taxreport());
		params.addParam("A");
		TaxReportDetailVO[] vos = (TaxReportDetailVO[]) sbo.queryByCondition(TaxReportDetailVO.class,
				"nvl(dr,0)=0 and pk_taxreport=? and sb_zlbh=? order by orderno", params);

		if (vos == null || vos.length == 0)
			return null;

		Map lastObjMap = null;
		if (StringUtil.isEmpty(vos[0].getSpreadfile())) {
			return null;
		}
		File f = new File(vos[0].getSpreadfile());
		if (f.exists() && f.isFile()) {
			String strJson = readFileString(vos[0].getSpreadfile());
			ObjectMapper objectMapper = getObjectMapper();
			lastObjMap = (Map) objectMapper.readValue(strJson, HashMap.class);

		} else {
			return null;
		}
		return lastObjMap;

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

	private String readFileString(String filepath) throws DZFWarpException {
		String sReturn = null;
		File f = new File(filepath);
		if (f.exists() && f.isFile()) {
			int byteread = 0;
			int bytesum = 0;

			FileInputStream inStream = null;
			ByteOutputStream bos = null;
			try {
				inStream = new FileInputStream(f);

				bos = new ByteOutputStream();
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					bos.write(buffer, 0, byteread);
				}
				bos.flush();
				byte[] bs = bos.toByteArray();
				sReturn = new String(bs, "utf-8");
			} catch (Exception e) {
				throw new WiseRunException(e);
			} finally {
				if (inStream != null) {
					try {
						inStream.close();
					} catch (IOException ioe) {
						log.error("错误",ioe);
					}
				}
				if (bos != null)
					bos.close();
			}
		}

		return sReturn;
	}

}
