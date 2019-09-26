package com.dzf.zxkj.platform.util;

import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.report.XjllbVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Map;

public interface MoreWorkBookKj2013Excel {
    Workbook createWorkBookZcfzKj2013(Map<String, ZcFzBVO> vOMap, Map<String, String> taxaxVoMap) throws Exception;
    Workbook createWorkBookLrbKj2013(Map<String, LrbVO> vOMap, Map<String, String> taxaxVoMap) throws Exception;
    Workbook createWorkBookXjllKj2013(Map<String, XjllbVO> vOMap, Map<String, String> taxaxVoMap) throws Exception;
}
