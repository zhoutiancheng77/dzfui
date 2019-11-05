package com.dzf.zxkj.platform.service.gzgl;

import com.alibaba.fastjson.JSONArray;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.UserVO;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.OutputStream;
import java.util.Map;
public interface ISalaryReportExcel {

	// 地区名 用于导出纳税申报的文件名
	String getAreaName(CorpTaxVo corptaxvo);

	// 纳税申报导出字段配置
	Map<Integer, String> exportExcelFieldColumn(Sheet sheets1, String billtype);

	// 纳税申报导出
	byte[] exportExcel(JSONArray array, OutputStream out, String billtype, CorpTaxVo corptaxvo, UserVO loginUserVO)
			throws Exception;

	// 人员信息导出
	byte[] expPerson(JSONArray array, OutputStream out, String billtype) throws Exception;

	// 纳税申报导出起始行
	int getExpStartrow();

	// 纳税申报导出起始单元格
	int getExpCellrow();

	// 纳税申报导出模板
	String getExcelModelName(String billtype);

	// 纳税申报导出起始行
	int getImpStartrow();

		// 纳税申报导出起始单元格
	int getImpCellrow();
		
	// 工资表导入方法
	SalaryReportVO[] impExcel(String filepath, Sheet sheets1, String opdate, String billtype, CorpTaxVo corptaxvo) throws BusinessException;

	// 字段名称行
	int getTitleColumnRow();

}
