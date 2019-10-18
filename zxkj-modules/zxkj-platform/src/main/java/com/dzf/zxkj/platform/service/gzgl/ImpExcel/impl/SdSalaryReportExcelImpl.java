package com.dzf.zxkj.platform.service.gzgl.ImpExcel.impl;

import com.alibaba.fastjson.JSONArray;
import com.dzf.zxkj.common.enums.SalaryTypeEnum;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.UserVO;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.Map;

@Service("salaryservice_shandong")
public class SdSalaryReportExcelImpl extends DefaultSalaryReportExcelImpl {

	protected Map<String, String> getExportMap(String billtype) {
		Map<String, String> map = null;
		if (billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue())) {
			String[] CODESJSEXP = new String[] { "ygbm", "zjlx", "zjbm", "ygname", "yfgz", "start", "end", "yanglaobx",
					"yiliaobx", "shiyebx", "zfgjj", "yxkcfy", "kcxmhj", "jcfy", "zykcdjze", "kcjjcxmhj", "ynssde",
					"shuilv", "quickdeduction", "grsds", "jmse" };

			String[] SDJSEXP = new String[] { "工号", "证照类型", "证照号码", "姓名", "收入额", "所得期间起", "所得期间止", "基本养老保险费", "基本医疗保险费",
					"失业保险费", "住房公积金", "允许扣除的税费", "税前扣除项目合计", "减除费用", "准予扣除的捐赠额", "扣除及减除项目合计", "应纳税所得额", "税率", "速算扣除数",
					"应纳税额", "减免税额" };

			map = getMapColumn(null, SDJSEXP, CODESJSEXP);
		} else {
			map = super.getExportMap(billtype);
		}
		return map;
	}

	@Override
	public byte[] exportExcel(JSONArray array, OutputStream out, String billtype, CorpTaxVo corptaxvo,
			UserVO loginUserVO) throws Exception {
		
		for (Object obj : array) {
			Map jobj = (Map) obj;
			Object o9 = jobj.get("qj");
			Object o = jobj.get("ynssde");
			DZFDouble dou = new DZFDouble(o.toString());
			double kcnum =getkcnum(billtype, (String)o9);
			double Quickdeduction = getQuickdeduction(dou.doubleValue(), kcnum, billtype, (String)o9);
			jobj.put("quickdeduction", Quickdeduction);

			jobj.put("yxkcfy", DZFDouble.ZERO_DBL);
			jobj.put("jcfy", new DZFDouble(kcnum));
			jobj.put("zykcdjze", DZFDouble.ZERO_DBL);
			jobj.put("jmse", DZFDouble.ZERO_DBL);

			Object o1 = jobj.get("yanglaobx");
			DZFDouble dou1 = getDZFDoubleIsNull(o1);

			Object o2 = jobj.get("yiliaobx");
			DZFDouble dou2 = getDZFDoubleIsNull(o2);

			Object o3 = jobj.get("shiyebx");
			DZFDouble dou3 = getDZFDoubleIsNull(o3);

			Object o4 = jobj.get("zfgjj");
			DZFDouble dou4 = getDZFDoubleIsNull(o4);

			Object o5 = jobj.get("yxkcfy");
			DZFDouble dou5 = getDZFDoubleIsNull(o5);

			jobj.put("kcxmhj",
					SafeCompute.add(dou1, SafeCompute.add(dou2, SafeCompute.add(dou3, SafeCompute.add(dou4, dou5)))));

			Object o6 = jobj.get("kcxmhj");
			DZFDouble dou6 = getDZFDoubleIsNull(o6);

			Object o7 = jobj.get("jcfy");
			DZFDouble dou7 = getDZFDoubleIsNull(o7);

			Object o8 = jobj.get("zykcdjze");
			DZFDouble dou8 = getDZFDoubleIsNull(o8);
			jobj.put("kcjjcxmhj", SafeCompute.add(dou6, SafeCompute.add(dou7, dou8)));

			if (o9 != null) {
				jobj.put("start", DateUtils.getPeriodStartDate(o9.toString()));
				jobj.put("end", DateUtils.getPeriodEndDate(o9.toString()));
			}
		}
		return super.exportExcel(array, out, billtype, corptaxvo, loginUserVO);
	}

	@Override
	public String getExcelModelName(String billtype) {
		String excelName = "salary_NSSBreport_shandong.xls";
		if (!billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue())) {
			excelName = super.getExcelModelName(billtype);
		}
		return excelName;
	}

	@Override
	public String getAreaName(CorpTaxVo corpvo) {
		return "山东省";
	}

}
