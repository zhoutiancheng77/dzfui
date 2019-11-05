package com.dzf.zxkj.platform.service.gzgl.ImpExcel.impl;

import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.enums.SalaryTypeEnum;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("salaryservice_henan")
public class HnSalaryReportExcelImpl extends DefaultSalaryReportExcelImpl {

	private int startRow = 10;

	@Override
	public int getImpStartrow() {

		return startRow;
	}

	@Override
	public String getAreaName(CorpTaxVo corpvo) {
		return "河南省";
	}

	protected String expPersonFileName() {
		String fileName = "personal_information_henan";
		return fileName;
	}

	protected Map<Integer, String> getExpPersonColumnMap(Sheet sheets1, String billtype) {

		int iBegin = getTitleColumnRow();
		Map<String, Integer> map1 = getCommonGzTableHeadTiTle(sheets1, iBegin);

		String[] CODESJSEXP = new String[] { "ygbm", "ygname", "zjlx", "zjbm", "varea", "ryzt", "sfyg", "sfgy",
				"vphone", "lhdate" };

		String[] SDJSEXP = new String[] { "工号", "*姓名", "*证照类型", "*证照号码", "*国籍(地区)", "*人员状态", "*是否残疾烈属孤老", "*是否雇员",
				"*联系电话", "来华时间" };

		Map<String, String> jsimp = getMapColumn(null, SDJSEXP, CODESJSEXP);
		Map<Integer, String> map = getColumnMap(map1, jsimp);

		return map;
	}

	@Override
	public SalaryReportVO[] impExcel(String filepath, Sheet sheets1, String qj, String billtype, CorpTaxVo corpvo)
			throws BusinessException {

		SalaryReportVO[] vos = super.impExcel(filepath, sheets1, qj, billtype,corpvo);

		List<SalaryReportVO> clist = new ArrayList<SalaryReportVO>();
		for (SalaryReportVO vo : vos) {
			if (!StringUtil.isEmpty(vo.getYgbm()) || !StringUtil.isEmpty(vo.getZjbm())) {
				clist.add(vo);
			}
		}
		return clist.toArray(new SalaryReportVO[clist.size()]);
	}

	protected Map<Integer, String> getColumnMap(Sheet sheets1, String billtype,CorpTaxVo corpvo) {

		Map<Integer, String> map = new HashMap<>();
		if (!billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue())) {
			startRow = 1;
			map = super.getColumnMap(sheets1, billtype,corpvo);
		} else {
			Map<String, Integer> map1 = getCommonGzTableHeadTiTle(sheets1, 4);

			Map<String, Integer> map2 = getCommonGzTableHeadTiTle(sheets1, 5);
			if (map2 == null || map2.size() == 0) {

			} else {
				for (Map.Entry<String, Integer> entry : map2.entrySet()) {
					map1.put(entry.getKey(), entry.getValue());
				}
			}

			String[] CODESIMP = new String[] { "zjlx", "zjbm", "ygname", "vproject", "qj", "yfgz", "yanglaobx",
					"yiliaobx", "shiyebx", "zfgjj" };

			String[] CQJSIMP = new String[] { "身份证件类型", "身份证件号码", "姓名", "所得项目", "所得期间", "收入额", "基本养老保险费", "基本医疗保险费",
					"失业保险费", "住房公积金", };
			Map<String, String> jsimp = getMapColumn(null, CQJSIMP, CODESIMP);

			map = getColumnMap(map1, jsimp);

			if (map.size() == jsimp.size()) {
				startRow = 7;
				return map;
			}

			startRow = 1;
			int iBegin = getTitleColumnRow();
			map1 = getCommonGzTableHeadTiTle(sheets1, iBegin);
			if (map1 == null || map1.size() == 0) {
				throw new BusinessException(getAreaName(corpvo) + "地区导入文件格式不正确，请下载模板后重新导入！");
			}

			jsimp = getJsImportMap(billtype);
			map = getColumnMap(map1, jsimp);

			if (map.size() == jsimp.size())
				return map;

			jsimp = getJsExportMap(billtype);
			map = getColumnMap(map1, jsimp);

			if (map.size() == jsimp.size())
				return map;

			boolean isMatch = false;
			jsimp = getDzfImportMap(billtype);
			map = getColumnMap(map1, jsimp);
			// 记录大账房自己导入类型
			if (map.size() == jsimp.size()) {
				startRow = 1;
				map.put(100000, "1");
				isMatch = true;
			}

			if (map == null || map.size() == 0) {
				throw new BusinessException(getAreaName(corpvo) + "地区导入文件格式不正确，请下载模板后重新导入！");
			}

			if (!isMatch) {
				throw new BusinessException(getAreaName(corpvo) + "地区导入文件格式不正确，请下载模板后重新导入！");
			}
		}

		return map;
	}
}
