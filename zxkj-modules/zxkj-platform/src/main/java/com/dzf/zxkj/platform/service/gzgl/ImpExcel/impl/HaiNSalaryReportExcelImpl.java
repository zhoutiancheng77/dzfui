package com.dzf.zxkj.platform.service.gzgl.ImpExcel.impl;

import com.dzf.zxkj.base.exception.BusinessException;
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

@Service("salaryservice_hainan")
public class HaiNSalaryReportExcelImpl extends DefaultSalaryReportExcelImpl {

	private int startRow = 4;

	@Override
	public int getImpStartrow() {

		return startRow;
	}

	@Override
	public int getImpCellrow() {
		int cellrow = 5;
		return cellrow;
	}

	@Override
	public String getAreaName(CorpTaxVo corpvo) {
		return "海南省";
	}

	@Override
	public SalaryReportVO[] impExcel(String filepath, Sheet sheets1, String qj, String billtype, CorpTaxVo corpvo)
			throws BusinessException {

		SalaryReportVO[] vos = super.impExcel(filepath, sheets1, qj, billtype,corpvo);

		List<SalaryReportVO> clist = new ArrayList<>();
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

			Map<String, Integer> map1 = getCommonGzTableHeadTiTle(sheets1, 3);

			String[] CODESIMP = new String[] { "ygbm", "zjlx", "zjbm", "ygname", "vproject", "qj", "yfgz", "yanglaobx",
					"yiliaobx", "shiyebx", "zfgjj" };

			String[] CQJSIMP = new String[] { "序号", "证件类型", "证件号码", "姓名", "所得项目", "所得期间起", "收入额", "养老保险金额", "医疗保险金额",
					"失业保险金额", "住房公积金金额" };
			Map<String, String> jsimp = getMapColumn(null, CQJSIMP, CODESIMP);

			map = getColumnMap(map1, jsimp);

			if (map.size() == jsimp.size()) {
				startRow = 4;
				return map;
			}

			startRow = 1;
			map1 = getCommonGzTableHeadTiTle(sheets1, 0);
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
