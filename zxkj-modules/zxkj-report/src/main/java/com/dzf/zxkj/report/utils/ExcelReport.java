package com.dzf.zxkj.report.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Slf4j
public class ExcelReport {
    public byte[] exportExcel(String title, List<String> headers, List<String> fields, List array,
                              OutputStream out, String pattern, String gs, String qj) {
        // 格式化header数据
        Map<String, List<String>> headermap = new HashMap<String, List<String>>();
        String[] temps = null;
        for (String str : headers) {
            if (str.indexOf("_") > 0) {
                temps = str.split("_");
                if (headermap.containsKey(temps[0])) {
                    headermap.get(temps[0]).add(temps[1]);
                } else {
                    List<String> listemp = new ArrayList<String>();
                    listemp.add(temps[1]);
                    headermap.put(temps[0], listemp);
                }
            }
        }

        HSSFWorkbook workbook = new HSSFWorkbook();
        try {
            int index = 4;
            HSSFSheet sheet = workbook.createSheet(title);
            // 行宽
            sheet.setDefaultColumnWidth(15);
            // 生成一个样式
            HSSFCellStyle style = workbook.createCellStyle();
            HSSFCellStyle st = workbook.createCellStyle();
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
            // font.setColor(HSSFColor.VIOLET.index);
            font.setFontHeightInPoints((short) 12);
            font.setBold(true); // 加粗
            // 把字体应用到当前的样式
            style.setFont(font);

            HSSFCellStyle style1 = workbook.createCellStyle();
            HSSFFont f = workbook.createFont();
            f.setFontHeightInPoints((short) 20); // 字号
            f.setBold(true); // 加粗
            style1.setFont(f);
            style1.setAlignment(HorizontalAlignment.CENTER); // 水平居中
            style1.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中

            HSSFCellStyle style4 = workbook.createCellStyle(); // 表头样式
            HSSFFont f2 = workbook.createFont();
            f2.setFontHeightInPoints((short) 12); // 字号
            f2.setBold(true); // 加粗
            style4.setFont(f2);
            style4.setAlignment(HorizontalAlignment.CENTER);
            style4.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
            // style4.setAlignment(HorizontalAlignment.LEFT); //居左
            int headerlength = headers.size();
            int fieldlength = fields.size();
            // 合并标题
            HSSFRow rowtitle = sheet.createRow(0);
            HSSFCell celltitle = rowtitle.createCell(0);
            celltitle.setCellValue(title);
            celltitle.setCellStyle(style1);
            if (fieldlength - 1 != 2) {
                CellRangeAddress CellRangeAddress = new CellRangeAddress(0, (short) 2, 0, (short) (fieldlength - 1));
                sheet.addMergedRegion(CellRangeAddress); // 合并标题
            }

            HSSFRow row_names = sheet.createRow(3);
            HSSFFont f1 = workbook.createFont();
            f1.setFontHeightInPoints((short) 12); // 字号
            f1.setBold(true); // 加粗
            if (((fieldlength - 1) / 3) != 0) {
                sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, (fieldlength - 1) / 3));
            }
            if (((fieldlength - 1) / 3) + 1 != ((fieldlength - 1) * 2 / 3)) {
                sheet.addMergedRegion(new CellRangeAddress(3, 3, ((fieldlength - 1) / 3) + 1, (fieldlength - 1) * 2 / 3));
            }
            if (((fieldlength - 1) * 2 / 3) + 1 != fieldlength - 1) {
                sheet.addMergedRegion(new CellRangeAddress(3, 3, ((fieldlength - 1) * 2 / 3) + 1, fieldlength - 1));
            }
            HSSFCellStyle hssfCellStyle = workbook.createCellStyle();
            hssfCellStyle.setFont(f1);
            hssfCellStyle.setAlignment(HorizontalAlignment.LEFT);
            HSSFCell hssfCell = row_names.createCell(0);
            hssfCell.setCellValue("公司：" + gs);
            hssfCell.setCellStyle(hssfCellStyle);
            hssfCell = row_names.createCell(((fieldlength - 1) / 3) + 1);
            hssfCellStyle = workbook.createCellStyle();
            hssfCellStyle.setFont(f1);
            hssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
            hssfCell.setCellValue("期间：" + qj);
            hssfCell.setCellStyle(hssfCellStyle);
            hssfCell = row_names.createCell(((fieldlength - 1) * 2 / 3) + 1);
            hssfCellStyle = workbook.createCellStyle();
            hssfCellStyle.setFont(f1);
            hssfCellStyle.setAlignment(HorizontalAlignment.RIGHT);
            hssfCell.setCellValue("单位：元");
            hssfCell.setCellStyle(hssfCellStyle);


            HSSFRow row = sheet.createRow(index);
            HSSFRow row2 = null;
            if (headerlength != fieldlength) {
                index++;
                row2 = sheet.createRow(index);
            }
            int newxtvalue = 0;
            for (int i = 0; i < headerlength; i++) {
                if (headerlength != fieldlength) {
                    HSSFCell cell1 = row.createCell(i);
                    HSSFCell cell2 = row2.createCell(i);
                    if (i < 7) {
                        CellRangeAddress region1 = new CellRangeAddress(4, 5, i, i);
                        sheet.addMergedRegion(region1);
                        cell1.setCellValue(new HSSFRichTextString(headers.get(i)));
                    }
                    if (i == 7) {
                        cell1.setCellValue(new HSSFRichTextString(headers.get(i)));
                        cell2.setCellValue(new HSSFRichTextString(headers.get(i + 1).split("_")[1]));
                        CellRangeAddress region1 = new CellRangeAddress(4, 4,7,
                                (headermap.get(headers.get(i)).size() + 6));
                        if((headermap.get(headers.get(i)).size() + 6) != 7){
                            sheet.addMergedRegion(region1);
                        }
                        newxtvalue = headermap.get(headers.get(i)).size();
                    }

                    if (i == (newxtvalue + 7) && (i + 1) < headers.size()) {
                        cell1.setCellValue(new HSSFRichTextString(headers.get(i + 1)));
                        cell2.setCellValue(new HSSFRichTextString(headers.get(i + 2).split("_")[1]));
                        CellRangeAddress region1 = new CellRangeAddress(4, 4, (newxtvalue + 7),(fieldlength - 1));
                        if((newxtvalue + 7) != (fieldlength - 1)){
                            sheet.addMergedRegion(region1);
                        }
                    }
                    if (i < fieldlength) {
                        if (i > 7 && i < (newxtvalue + 7)) {
                            cell2.setCellValue(new HSSFRichTextString(headers.get(i + 1).split("_")[1]));
                        } else if (i >= (newxtvalue + 7) && i != (newxtvalue + 7)) {
                            cell2.setCellValue(new HSSFRichTextString(headers.get(i + 2).split("_")[1]));
                        }
                        cell2.setCellStyle(style);
                        cell1.setCellStyle(style);
                    }
                } else {
                    HSSFCell cell1 = row.createCell(i);
                    cell1.setCellValue(new HSSFRichTextString(headers.get(i)));
                    cell1.setCellStyle(style);
                }
            }
            for (int i = 0; i < array.size(); i++) {
                HSSFRow row1 = sheet.createRow(i + index + 1);
                Map<String, String> map = (Map<String, String>) array.get(i);
                Set<String> keySet = map.keySet();
                keySet.remove("pk_tzpz_h");
                keySet.remove("pk_accsubj");
                int count = 0;
                for (String key : fields) {
                    try {
                        HSSFCell cell = row1.createCell(count);
                        HSSFRichTextString richString = new HSSFRichTextString(map.get(key));
                        cell.setCellValue(richString);
                        if (!key.equals("rq") && !key.equals("zy") && !key.equals("fx") && !key.equals("pzh")) {
                            cell.setCellStyle(st);
                        } else {
                            cell.setCellStyle(st1);
                        }
                        count++;
                    } catch (SecurityException e) {
                        // e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        // e.printStackTrace();
                    } catch (Exception e) {
                        // e.printStackTrace();
                    } finally {
                        // 清理资源
                    }
                }
            }

            try {
                workbook.write(out);
            } catch (IOException e) {
                // e.printStackTrace();
            }
        } catch (Exception e) {
            log.error("错误", e);
        }
        return workbook.getBytes();

    }
}
