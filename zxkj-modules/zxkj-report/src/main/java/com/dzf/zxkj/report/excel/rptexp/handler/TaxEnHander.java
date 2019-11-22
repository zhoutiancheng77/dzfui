package com.dzf.zxkj.report.excel.rptexp.handler;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import com.dzf.zxkj.report.excel.rptexp.CopySheetUtil;
import com.dzf.zxkj.report.excel.rptexp.ResourceUtil;
import com.dzf.zxkj.report.excel.rptexp.enums.ExportTemplateEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TaxEnHander{

    private String period;

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    private String[] monthEn = {"January","February","March","April","May","June","July","August","September","October","November","December"};

    public Map<String, Workbook> handle(List<ZcFzBVO[]> listZcfzBvos, Map<String, String> en_message, String corpName, List<LrbVO[]> listLrbBvos){

        Map<String, Workbook> workbookMap = new HashMap<>();

        Workbook workbook_zcfz = null;
        Workbook workbook_lrb = null;
        try {
            workbook_zcfz = WorkbookFactory.create(ResourceUtil.get(ExportTemplateEnum.EN, ResourceUtil.ResourceEnum.KJ2013ZCFZ).getInputStream());
            workbook_lrb = WorkbookFactory.create(ResourceUtil.get(ExportTemplateEnum.EN, ResourceUtil.ResourceEnum.KJ2013LR).getInputStream());
            Sheet sheet = workbook_zcfz.getSheetAt(0);

            for(int i = 0; i < listZcfzBvos.size(); i++){
                ZcFzBVO[] zcFzBVO = listZcfzBvos.get(i);
                String month = period.substring(5,7);
                Sheet newSheet = workbook_zcfz.createSheet(monthEn[Integer.parseInt(month)-1]);
                CopySheetUtil.copySheets(newSheet,sheet);

                Row rowHead = newSheet.getRow(1);
                String headContent = rowHead.getCell(0).getStringCellValue();
                headContent = headContent.replaceAll("Company：","Company："+corpName).replaceAll("Period ：","Period ："+period);
                rowHead.getCell(0).setCellValue(headContent);
                for (int rowIndex = 4; rowIndex < newSheet.getPhysicalNumberOfRows(); rowIndex++) {
                    Row row = newSheet.getRow(rowIndex);
                    String zcName = trimSpace(row.getCell(0) != null ? row.getCell(0).getStringCellValue() : "");
                    String fzName = trimSpace(row.getCell(8) != null ? row.getCell(8).getStringCellValue() : "");
                    if(!StringUtil.isEmpty(zcName) && en_message.containsKey(zcName)){
                        String name_zhcn = trimSpace(en_message.get(zcName));
                        ZcFzBVO zcFzBVO1 = queryVo(zcFzBVO, name_zhcn);
                        if(zcFzBVO1 != null){
                            if(row.getCell(6) != null && zcFzBVO1.getQmye1() != null && !zcFzBVO1.getQmye1().equals(DZFDouble.ZERO_DBL)){
                                row.getCell(6).setCellValue(zcFzBVO1.getQmye1().doubleValue());
                            }
                            if(row.getCell(7) != null && zcFzBVO1.getNcye1() != null && !zcFzBVO1.getNcye1().equals(DZFDouble.ZERO_DBL)){
                                row.getCell(7).setCellValue(zcFzBVO1.getNcye1().doubleValue());
                            }
                        }
                    }

                    if(!StringUtil.isEmpty(fzName) && en_message.containsKey(fzName)){
                        String name_zhcn = trimSpace(en_message.get(fzName));
                        ZcFzBVO zcFzBVO1 = queryVo(zcFzBVO, name_zhcn);
                        if(zcFzBVO1 != null){
                            if(row.getCell(10) != null && zcFzBVO1.getQmye2() != null && !zcFzBVO1.getQmye2().equals(DZFDouble.ZERO_DBL)){
                                row.getCell(10).setCellValue(zcFzBVO1.getQmye2().doubleValue());
                            }
                            if(row.getCell(11) != null && zcFzBVO1.getNcye2() != null && !zcFzBVO1.getNcye2().equals(DZFDouble.ZERO_DBL)){
                                row.getCell(11).setCellValue(zcFzBVO1.getNcye2().doubleValue());
                            }
                        }
                    }
                }
            }
            workbook_zcfz.removeSheetAt(0);
            workbookMap.put("BALANCE SHEET",workbook_zcfz);

            //利润表
            sheet = workbook_lrb.getSheetAt(0);

            for(int i = 0; i < listLrbBvos.size(); i++){
                LrbVO[] lrbVOs = listLrbBvos.get(i);
                String month = period.substring(5,7);
                Sheet newSheet = workbook_lrb.createSheet(monthEn[Integer.parseInt(month)-1]);
                CopySheetUtil.copySheets(newSheet,sheet);

                Row rowHead = newSheet.getRow(1);
                String headContent = rowHead.getCell(0).getStringCellValue();
                headContent = headContent.replaceAll("Company：","Company："+corpName).replaceAll("Period ：","Period ："+period);
                rowHead.getCell(0).setCellValue(headContent);
                for (int rowIndex = 3; rowIndex < newSheet.getPhysicalNumberOfRows(); rowIndex++) {
                    Row row = newSheet.getRow(rowIndex);
                    String name = trimSpace(row.getCell(0) != null ? row.getCell(0).getStringCellValue() : "");

                    if(!StringUtil.isEmpty(name) && en_message.containsKey(name)){
                        String name_zhcn = trimSpace(en_message.get(name));
                        LrbVO lrbVO = queryLrbVo(lrbVOs, name_zhcn);
                        if(lrbVO != null){
                            if(row.getCell(2) != null && lrbVO.getByje() != null && !lrbVO.getByje().equals(DZFDouble.ZERO_DBL)){
                                row.getCell(2).setCellValue(lrbVO.getByje().doubleValue());
                            }
                            if(row.getCell(3) != null && lrbVO.getBnljje() != null && !lrbVO.getBnljje().equals(DZFDouble.ZERO_DBL)){
                                row.getCell(3).setCellValue(lrbVO.getBnljje().doubleValue());
                            }
                        }
                    }
                }
            }
            workbook_lrb.removeSheetAt(0);
            workbookMap.put("INCOME STATEMENT",workbook_lrb);



        }catch (Exception e) {
            log.error("英文报表导出异常！",e);
//            e.printStackTrace();
        }
        return workbookMap;
    }

    private String trimSpace(String str){
        if(StringUtil.isEmpty(str)){
            return "";
        }
        str = str.replaceAll("：|\\([^\\(^\\)]*\\)|:|\\（[^\\（^\\）]*\\）|“-”|“-”|\n","").trim().replaceAll(": ","");
        if(str.indexOf("、") != -1){
            str = str.substring(str.indexOf("、")+1, str.length());
        }
        return str.toLowerCase().replaceAll(" ","").replaceAll("　","").replaceAll("'","").replaceAll("`","");
    }

    private ZcFzBVO queryVo(ZcFzBVO[] zcFzBVO, String name_zhcn){
        for(ZcFzBVO zcFzBVO1 : zcFzBVO){
            if(name_zhcn.equalsIgnoreCase(trimSpace(zcFzBVO1.getZc())) || name_zhcn.equalsIgnoreCase(trimSpace(zcFzBVO1.getFzhsyzqy()))){
                return zcFzBVO1;
            }
        }
        return null;
    }

    private LrbVO queryLrbVo(LrbVO[] lrbBVOs, String name_zhcn){
        for(LrbVO lrbVO : lrbBVOs){
            if(name_zhcn.equalsIgnoreCase(trimSpace(lrbVO.getXm()))){
                return lrbVO;
            }
        }
        return null;
    }
}
