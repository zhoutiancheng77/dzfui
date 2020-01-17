package com.dzf.zxkj.report.controller.cwbb;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.ColumnCellAttr;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.report.CwgyInfoVO;
import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.service.cwbb.ICwgyInfoReport;
import com.dzf.zxkj.report.utils.SystemUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("gl_rep_cwgyinfoact")
@Slf4j
public class CwgyInfoController extends ReportBaseController {

    @Autowired
    private ICwgyInfoReport gl_rep_cwgyinfoserv;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

//    @Autowired
//    private IUserService iuserService;

    /**
     * 查询
     */
    @GetMapping("/queryAction")
    public ReturnData<Grid> queryAction(QueryParamVO queryParamVO) {
        Grid grid = new Grid();
        QueryParamVO queryParamvo = getQueryParamVO(queryParamVO);
        try {
            // 校验
            checkSecurityData(null, new String[]{queryParamvo.getPk_corp()},null);
            queryParamvo.setQjq(queryParamvo.getBegindate1().toString().substring(0, 7));
            queryParamvo.setQjz(queryParamvo.getBegindate1().toString().substring(0, 7));
            queryParamvo.setEnddate(queryParamvo.getBegindate1());
            CwgyInfoVO[] fsejyevos = null;
//            Set<String> nnmnc = iuserService.querypowercorpSet(getLoginUserid());
//            String corp = queryParamvo.getPk_corp();
//            if (nnmnc == null || !nnmnc.contains(corp)) {
//                throw new BusinessException("不包含该公司。");
//            }

            int curyear = new DZFDate().getYear();
            int conyear = queryParamvo.getBegindate1().getYear();
            if (conyear > curyear) {
                throw new BusinessException("超出当前年份,请重新选择!");
            }
            fsejyevos = gl_rep_cwgyinfoserv.getCwgyInfoVOs(queryParamvo);

            grid.setTotal(fsejyevos == null ? 0 : (long) Arrays.asList(fsejyevos).size());
            grid.setRows(fsejyevos == null ? null : Arrays.asList(fsejyevos));
            grid.setSuccess(true);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<LrbVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "查询失败！");
        }

        // 日志记录接口
        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT, "财务概要信息查询:" + queryParamvo.getBegindate1().toString(),
                ISysConstants.SYS_2);

        return ReturnData.ok().data(grid);
    }

    public QueryParamVO getQueryParamVO(QueryParamVO paramvo) {
        if (paramvo.getPk_corp() == null || paramvo.getPk_corp().trim().length() == 0) {
            // 如果编制单位为空则取当前默认公司
            String corpVo = SystemUtil.getLoginCorpId();
            paramvo.setPk_corp(corpVo);
        }
        return paramvo;
    }

    /**
     * 打印操作
     */
    @PostMapping("print/pdf")
    public void printAction(@RequestParam Map<String, String> pmap1,
                            @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response) {
        try {
            // 校验
            checkSecurityData(null, new String[]{corpVO.getPk_corp()},null);
            PrintParamVO printParamVO = JsonUtils.deserialize(JsonUtils.serialize(pmap1), PrintParamVO.class);
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            Map<String, String> params = printReporUtil.getPrintMap(printParamVO);

            String strlist = params.get("list");
            String type = params.get("type");
            String pageOrt = params.get("pageOrt");
            String left = params.get("left");
            String top = params.get("top");
            String printdate = params.get("printdate");
            String font = params.get("font");
            String pageNum = params.get("pageNum");
            Map<String, String> pmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
            pmap.put("type", type);
            pmap.put("pageOrt", pageOrt);
            pmap.put("left", left);
            pmap.put("top", top);
            pmap.put("printdate", printdate);
            pmap.put("font", font);
            pmap.put("pageNum", pageNum);
            if (strlist == null) {
                return;
            }

//            JSONArray array = (JSONArray) JSON.parseArray(strlist);
//            Map<String, String> bodymapping = FieldMapping.getFieldMapping(new CwgyInfoVO());
//            CwgyInfoVO[] bodyvos = DzfTypeUtils.cast(array, bodymapping, CwgyInfoVO[].class,
//                    JSONConvtoJAVA.getParserConfig());
            CwgyInfoVO[] bodyvos = JsonUtils.deserialize(strlist, CwgyInfoVO[].class);
            Map<String, String> tmap = new LinkedHashMap<String, String>();// 声明一个map用来存前台传来的设置参数
            tmap.put("公司", printParamVO.getCorpName());
            tmap.put("期间", printParamVO.getTitleperiod());
            tmap.put("单位", "元");
            formatData(bodyvos);

            String[] columnames = new String[] { "行次", "项目分类", "项目", "本年累计金额", "本期金额", "金额", "同比变化", "金额", "同比变化", "环比变化" };
            String[] columnkeys = new String[] { "hs", "xmfl", "xm", "bnljje", "bnljbl", "byje", "bybl", "byhb" };

            LinkedList<ColumnCellAttr> columnlist = new LinkedList<>();
            for (int i = 0; i < columnames.length; i++) {
                ColumnCellAttr attr = new ColumnCellAttr();
                attr.setColumname(columnames[i]);
                if (i == 0 || i == 1 || i == 2) {
                    attr.setRowspan(2);
                } else if (i == 3) {
                    attr.setColspan(2);
                } else if (i == 4) {
                    attr.setColspan(3);
                }
                columnlist.add(attr);
            }

            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(font), Font.NORMAL));// 设置表头字体
            if (type.equals("1"))
                printReporUtil.printGroup(new HashMap<String, List<SuperVO>>(), bodyvos, "财 务 概 要 信 息", columnkeys, columnames,
                        columnlist, new int[] { 2, 3, 5, 3, 3, 3, 3, 3 }, 0, null, pmap, tmap);// A4纸张打印
            else if (type.equals("2")) {
                printReporUtil.printB5(new HashMap<String, List<SuperVO>>(), bodyvos, "财 务 概 要 信 息", columnkeys, columnames,
                        columnlist, new int[] { 2, 3, 5, 3, 3, 3, 3, 3 }, 0, null, pmap, tmap);
            }
        } catch (DocumentException e) {
            log.error("打印失败", e);
        } catch (IOException e) {
            log.error("打印失败", e);
        }
    }

    private void formatData(CwgyInfoVO[] bodyvos ) {
        int len = bodyvos.length;
        DZFDouble stemp =DZFDouble.ZERO_DBL;
        for(int index = 0;index<len;index++){
            CwgyInfoVO vo = bodyvos[index];
            if (index == 2 || index == 6 || index == 17|| index == 18||
                    index == 19|| index == 20|| index == 21|| index == 22|| index == 23||
                    index == 24|| index == 25|| index == 26) {
                if (vo.getByje() !=null) {
                    stemp=vo.getByje().setScale(2, DZFDouble.ROUND_HALF_UP);
                    vo.setSbyje(String.format("%1$,.2f",stemp.doubleValue())+"%");
                }
                if (vo.getBnljje() !=null) {
                    stemp=vo.getBnljje().setScale(2, DZFDouble.ROUND_HALF_UP);
                    vo.setSbnljje(String.format("%1$,.2f",stemp.doubleValue())+"%");
                }
            }
            if(index<=26){
                if (vo.getBybl() !=null) {
                    stemp=vo.getBybl().setScale(2, DZFDouble.ROUND_HALF_UP);
                    vo.setSbybl(String.format("%1$,.2f",stemp.doubleValue())+"%");
                }
                if (vo.getByhb() !=null) {
                    stemp=vo.getByhb().setScale(2, DZFDouble.ROUND_HALF_UP);
                    vo.setSbyhb(String.format("%1$,.2f",stemp.doubleValue())+"%");
                }
                if (vo.getBnljbl() !=null) {
                    stemp=vo.getBnljbl().setScale(2, DZFDouble.ROUND_HALF_UP);
                    vo.setSbnljbl(String.format("%1$,.2f",stemp.doubleValue())+"%");
                }
            }
        }
    }

    // 导出Excel
    @PostMapping("export/excel")
    public void excelReport(@MultiRequestBody PrintParamVO printParamVO,
                            @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response) {
//        HttpServletRequest request = getRequest();
//        request.getParameterNames();

//        String strlist = getRequest().getParameter("list");
//        JSONArray array = (JSONArray) JSON.parseArray(strlist);
//        Map<String, String> bodymapping = FieldMapping.getFieldMapping(new CwgyInfoVO());
//        CwgyInfoVO[] listVo = DzfTypeUtils.cast(array, bodymapping, CwgyInfoVO[].class,
//                JSONConvtoJAVA.getParserConfig());
        // 校验
        checkSecurityData(null, new String[]{corpVO.getPk_corp()},null);
        String strlist = printParamVO.getList();
        CwgyInfoVO[] listVo = JsonUtils.deserialize(strlist, CwgyInfoVO[].class);
        String gs = printParamVO.getCorpName();
        String qj = printParamVO.getTitleperiod();
        formatData(listVo);
//        HttpServletResponse response = getResponse();
        OutputStream toClient = null;
        ServletOutputStream servletOutputStream = null;
        try {

            String[] headers = new String[] { "行次", "项目分类", "项目", "金额", "同比变化", "金额", "同比变化", "环比变化"  };
            String[] headers2 = new String[] { "本年累计金额", "本期金额" };
            int[] colspans = new int[]{2, 3};
            String[] fields = new String[] { "hs", "xmfl", "xm", "bnljje", "bnljbl", "byje", "bybl", "byhb" };

            response.reset();
            String date = DateUtils.getDate(new Date());
            String fileName = "财务概要信息-" + qj + ".xls";
            String formattedName = URLEncoder.encode(fileName, "UTF-8");
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + fileName + ";filename*=UTF-8''" + formattedName);
            servletOutputStream = response.getOutputStream();
            toClient = new BufferedOutputStream(servletOutputStream);
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            byte[] length = exportExcelKhywl("财务概要信息", headers, headers2, colspans, fields, listVo, toClient, "", gs, qj);
            String srt2 = new String(length, "UTF-8");
            response.addHeader("Content-Length", srt2);
            toClient.flush();
            servletOutputStream.flush();
        } catch (IOException e) {
            log.error("excel导出错误", e);
        } finally {
            try {
                if (toClient != null) {
                    toClient.close();
                }
            } catch (IOException e) {
                log.error("excel导出错误", e);
            }
            try {
                if (servletOutputStream != null) {
                    servletOutputStream.close();
                }
            } catch (IOException e) {
                log.error("错误",e);
            }
        }
        // 日志记录接口
        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT, "财务概要信息导出:" + qj, 2);
    }

    private byte[] exportExcelKhywl(String title, String[] headers, String[] headers2, int[] colspans, String[] fields,
                                    CwgyInfoVO[] listVo, OutputStream out, String pattern, String gs, String qj) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        try {
            int index = 5;
            HSSFSheet sheet = workbook.createSheet(title);
            // 行宽
            sheet.setDefaultColumnWidth(15);
            // 生成一个样式
            HSSFCellStyle st = workbook.createCellStyle();
            st.setBorderBottom(BorderStyle.THIN);
            st.setBorderBottom(BorderStyle.THIN);
            st.setBorderLeft(BorderStyle.THIN);
            st.setBorderRight(BorderStyle.THIN);
            st.setBorderTop(BorderStyle.THIN);
            st.setAlignment(HorizontalAlignment.RIGHT);

            HSSFCellStyle st1 = workbook.createCellStyle();
            st1.setBorderBottom(BorderStyle.THIN);
            st1.setBorderLeft(BorderStyle.THIN);
            st1.setBorderRight(BorderStyle.THIN);
            st1.setBorderTop(BorderStyle.THIN);
            st1.setAlignment(HorizontalAlignment.LEFT);

            // 设置这些样式
            HSSFCellStyle style = workbook.createCellStyle();
            style.setFillForegroundColor(HSSFColor.WHITE.index);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            // 生成一个字体
            HSSFFont font = workbook.createFont();
            font.setFontHeightInPoints((short) 12);// 字号
//            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
            // 把字体应用到当前的样式
            style.setFont(font);

            HSSFCellStyle style1 = workbook.createCellStyle();
            HSSFFont f = workbook.createFont();
            f.setFontHeightInPoints((short) 20);// 字号
//            f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
            style1.setFont(f);
            style1.setAlignment(HorizontalAlignment.CENTER);// 内容左右居中
            style1.setVerticalAlignment(VerticalAlignment.CENTER);// 内容上下居中
            // 、

            int headerlength = headers.length;
            int fieldlength = fields.length;
            // 合并标题
            HSSFRow rowtitle = sheet.createRow(0);
            HSSFCell celltitle = rowtitle.createCell(0);
            celltitle.setCellValue(title);
            celltitle.setCellStyle(style1);
            sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, (fieldlength - 1)));// 合并标题

            // 合并期间、公司行
            HSSFRow rowtitle1 = sheet.createRow(3);
            HSSFCell celltitle1 = rowtitle1.createCell(0);
            celltitle1.setCellValue("公司：" + gs);
            HSSFCellStyle style3 = workbook.createCellStyle();
            style3.setFont(font);
            style3.setVerticalAlignment(VerticalAlignment.CENTER);
            celltitle1.setCellStyle(style3);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, (fieldlength / 2 - 1)));

            HSSFCell celltitle2 = rowtitle1.createCell(fieldlength / 2);
            celltitle2.setCellValue("时间：" + qj);
            HSSFCellStyle style4 = workbook.createCellStyle();
            style4.setFont(font);
            style4.setAlignment(HorizontalAlignment.RIGHT);
            style4.setVerticalAlignment(VerticalAlignment.CENTER);
            celltitle2.setCellStyle(style4);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, (fieldlength / 2), (fieldlength - 1)));
            // end 合并期间、公司行

            HSSFRow row = sheet.createRow(index);

            // 第一行标题行
            HSSFRow rowtitle1m = sheet.createRow(4);
            for (int i = 0; i < fieldlength; i++) {
                HSSFCell celltitle1m = rowtitle1m.createCell(i);
                celltitle1m.setCellStyle(style);
            }

            HSSFCellStyle stylegsm = workbook.createCellStyle();// 表头样式
            stylegsm.cloneStyleFrom(style);
            // stylegsm.setAlignment(HSSFCellStyle.ALIGN_LEFT);

            int colIndex = 0;
            for (; colIndex < 3; colIndex++) {
                HSSFCell celltitle1m = rowtitle1m.createCell(colIndex);
                celltitle1m.setCellValue(new HSSFRichTextString(headers[colIndex]));
                celltitle1m.setCellStyle(stylegsm);
            }

            for (int i = 0; i < headers2.length; i++) {
                HSSFCell celltitle1m = rowtitle1m.createCell(i*2+3);
                celltitle1m.setCellValue(new HSSFRichTextString(headers2[i]));
                celltitle1m.setCellStyle(stylegsm); // 居中
                int mergeEnd = colIndex + colspans[i] - 1;
                sheet.addMergedRegion(new CellRangeAddress(4, 4, colIndex, mergeEnd));
                colIndex = mergeEnd + 1;
            }

            if (headerlength != fieldlength) {
                index++;
            }
            for (int i = 0; i < headerlength; i++) {
                {
                    HSSFCell cell1 = row.createCell(i);
                    cell1.setCellValue(new HSSFRichTextString(headers[i]));
                    cell1.setCellStyle(style);
                }
            }
            sheet.addMergedRegion(new CellRangeAddress(4, 5, 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(4, 5, 1, 1));
            sheet.addMergedRegion(new CellRangeAddress(4, 5, 2, 2));
            HSSFCellStyle style2 = null;
            short color = 9;
            for (int m = 0; m < listVo.length; m++) {
                HSSFRow row1 = sheet.createRow(m + index + 1);
                // Map<String, Object> map = (Map<String, Object>) array.get(i);
                SuperVO t = listVo[m];
                for (int n = 0; n < fields.length; n++) {
                    style2 = createTitleStyle3(workbook, color);
                    HSSFCell cell = row1.createCell(n);
                    String fieldName = fields[n];
                    try {
                        cell.setCellStyle(style2);
                        Object value = t.getAttributeValue(fieldName);
                        String textValue = null;
                        if (!fieldName.equals("hs") && !fieldName.equals("xmfl") && !fieldName.equals("xm")) {
                            DZFDouble bValue = (DZFDouble) value;
                            if (bValue == null || bValue.doubleValue() == 0.0D) {
                                cell.setCellValue("");
                            } else {
                                //设置小数位 2位
                                bValue = bValue.setScale(2, DZFDouble.ROUND_HALF_UP);
                                textValue = bValue.toString();
                                String svalue =(String)t.getAttributeValue("s"+fieldName);
                                if(!StringUtil.isEmpty(svalue)){
                                    textValue=svalue;
                                }
                                if ("10".equals(t.getAttributeValue("hs")) && StringUtil.isEmpty((String)t.getAttributeValue("xm"))) {
                                    if (fieldName.equals("bnljje")) {
                                        if (t.getAttributeValue("bnljje") != null) {
                                            if (t.getAttributeValue("bnljje").equals(new DZFDouble(100))) {
                                                HSSFRichTextString richString = new HSSFRichTextString("销项发票");
                                                cell.setCellValue(richString);
                                                HSSFCellStyle rightstyle = getDecimalFormatStyle(2, workbook, color);
                                                cell.setCellStyle(rightstyle);
                                            }
                                        }
                                    } else if (fieldName.equals("byje")) {
                                        if (t.getAttributeValue("byje") != null) {
                                            if (t.getAttributeValue("byje").equals(new DZFDouble(101))) {
                                                HSSFRichTextString richString = new HSSFRichTextString("进项发票");
                                                cell.setCellValue(richString);
                                                HSSFCellStyle rightstyle = getDecimalFormatStyle(2, workbook, color);
                                                cell.setCellStyle(rightstyle);
                                            }
                                        }
                                    } else {
                                        cell.setCellValue(textValue);
                                        HSSFCellStyle rightstyle = getDecimalFormatStyle(2, workbook, color);
                                        cell.setCellStyle(rightstyle);
                                    }

                                } else {
                                    cell.setCellValue(textValue);
                                    HSSFCellStyle rightstyle = getDecimalFormatStyle(2, workbook, color);
                                    cell.setCellStyle(rightstyle);
                                }
                            }
                        } else {
                            textValue = value != null ? value.toString() : null;
                            HSSFRichTextString richString = new HSSFRichTextString(textValue);
                            cell.setCellValue(richString);
                        }

                        if (fieldName.equals("hs") || fieldName.equals("xmfl") || fieldName.equals("xm")) {
                            // style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);//
                            // 内容左右居中
                            style2.setAlignment(HorizontalAlignment.LEFT);
                            style2.setVerticalAlignment(VerticalAlignment.CENTER);// 内容上下居中
                            cell.setCellStyle(style2);
                        }

                    } catch (Exception e) {
                        log.error("字段格式转换出错", e);
                    }
                }

                int row2 = t.getAttributeValue("row") == null ? 0 : (Integer) t.getAttributeValue("row");
                int rowspan = t.getAttributeValue("rowspan") == null ? 0 : (Integer) t.getAttributeValue("rowspan");

                if (rowspan > 0) {
                    int lastRow = 6 + row2 + rowspan - 1;
                    sheet.addMergedRegion(new CellRangeAddress(6 + row2, lastRow, 0, 0));
                    sheet.addMergedRegion(new CellRangeAddress(6 + row2, lastRow, 1, 1));
                }

                int col = t.getAttributeValue("col") == null ? 0 : (Integer) t.getAttributeValue("col");
                int colspan = t.getAttributeValue("colspan") == null ? 0 : (Integer) t.getAttributeValue("colspan");
                if (colspan > 0) {
                    if(colspan ==7){
                        sheet.addMergedRegion(new CellRangeAddress(col+6, col+6, 0, 6));
                    }else if(colspan ==4){
                        sheet.addMergedRegion(new CellRangeAddress(col+6, col+6, 3, 6));
                    }else if(colspan ==2){
                        sheet.addMergedRegion(new CellRangeAddress(col+6, col+6, 3, 4));
                        sheet.addMergedRegion(new CellRangeAddress(col+6, col+6, 5, 6));
                    }
                }

            }
            try {
                workbook.write(out);
            } catch (IOException e) {
                throw new WiseRunException(e);
            }
        } catch (Exception e) {
            log.error("文件导出", e);
        }
        return workbook.getBytes();
    }

    private HSSFCellStyle createTitleStyle3(HSSFWorkbook workbook, short color) {
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(color);
        style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);//(short) 1
        style2.setBorderBottom(BorderStyle.THIN);//(short) 1
        style2.setBorderLeft(BorderStyle.THIN);//(short) 1
        style2.setBorderRight(BorderStyle.THIN);//(short) 1
        style2.setBorderTop(BorderStyle.THIN);//(short) 1
        style2.setAlignment(HorizontalAlignment.RIGHT);//(short) 1
        HSSFFont font2 = workbook.createFont();
        style2.setFont(font2);
        return style2;
    }

    private HSSFCellStyle getDecimalFormatStyle(int digit, HSSFWorkbook workbook, short color) {

        if (map == null)
            map = new ConcurrentHashMap();
        if (!map.containsKey(digit)) {
            String style = "#,##0";
            for (int i = 0; i < digit; i++) {
                if (i == 0)
                    style = (new StringBuilder(String.valueOf(style))).append(".").toString();
                style = (new StringBuilder(String.valueOf(style))).append("0").toString();
            }

            HSSFCellStyle rightstyle = createTitleStyle4(workbook, color);
            HSSFDataFormat fmt = workbook.createDataFormat();
            rightstyle.setDataFormat(fmt.getFormat(style));
            map.put(digit, rightstyle);
        }
        return (HSSFCellStyle) map.get(digit);
    }

    private Map map;

    private HSSFCellStyle createTitleStyle4(HSSFWorkbook workbook, short color) {
        HSSFCellStyle rightstyle = createTitleStyle3(workbook, color);

        rightstyle.setAlignment(HorizontalAlignment.RIGHT);//(short) 3
        return rightstyle;
    }
}
