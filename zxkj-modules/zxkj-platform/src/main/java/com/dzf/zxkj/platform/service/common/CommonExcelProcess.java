package com.dzf.zxkj.platform.service.common;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.platform.model.report.CwbbType;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Map;

public interface CommonExcelProcess {
    //创建单张报表（多报表文件）
    Workbook createOneRptBook(CwbbType cwbbType, Map<String, SuperVO> vOMap, Map<String, String> taxVoMap) throws Exception;
    //创建完整财务报表（单报表文件）
    Workbook createFullRptBook(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, SuperVO> zcfzbVOMap, Map<String, SuperVO> lrbVOMap, Map<String, SuperVO> xjllbVOMap) throws Exception;
}
