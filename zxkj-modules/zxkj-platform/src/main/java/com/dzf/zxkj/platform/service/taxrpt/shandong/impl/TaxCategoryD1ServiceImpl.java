package com.dzf.zxkj.platform.service.taxrpt.shandong.impl;

import com.alibaba.fastjson.JSON;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxReportNewQcInitVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.util.taxrpt.TaxRptemptools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

//印花税
@Service("taxcateserv_D1")
@Slf4j
public class TaxCategoryD1ServiceImpl extends DefaultTaxCategoryServiceImpl {

	@Autowired
	private SingleObjectBO sbo;
//	private static Logger log = Logger.getLogger(JsTaxRptServiceImpl.class);

	@Override
	public HashMap<String, Object> getQcData(CorpVO corpvo, TaxReportVO reportvo) throws DZFWarpException {
		HashMap<String, Object> hmQCData = new HashMap<String, Object>();
		getQcFromJsonFile(hmQCData, reportvo);
		return hmQCData;
	}

	private void getQcFromJsonFile(HashMap<String, Object> qcData, TaxReportVO reportvo) {
		TaxReportNewQcInitVO qcVO = null;

		SQLParameter sp = new SQLParameter();
		sp.addParam(reportvo.getPk_corp());
		sp.addParam(reportvo.getPeriod());
		sp.addParam(reportvo.getPk_taxsbzl());
		String condition = " pk_corp = ? and period = ? and pk_taxsbzl = ? ";
		TaxReportNewQcInitVO[] rs = (TaxReportNewQcInitVO[]) sbo.queryByCondition(TaxReportNewQcInitVO.class, condition,
				sp);
		if (rs != null && rs.length > 0) {
			qcVO = rs[0];
		}

		if (qcVO != null) {
			String file = qcVO.getSpreadfile();
			String strJson = TaxRptemptools.readFileString(file);
			try {
				Object json = JSON.parse(strJson);
				qcData.put(reportvo.getSb_zlbh() + "qc", json);
			} catch (Exception e) {
				log.error("解析期初JSON失败", e);
			}
		}
	}
}
