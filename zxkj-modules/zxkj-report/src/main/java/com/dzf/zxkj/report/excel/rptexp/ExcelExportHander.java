package com.dzf.zxkj.report.excel.rptexp;

import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public abstract class ExcelExportHander {

    private CorpVO cpvo;
    private String qj;
    private String qjlx;

    //税款所属期起、止日期
    private Date beginDate;
    private Date endDate;

    private String areaType;
    private String corpType;
    private boolean isMoreDoc;

    public String getAreaType() {
        return areaType;
    }

    public String getCorpType() {
        return corpType;
    }

    public boolean isMoreDoc() {
        return isMoreDoc;
    }

    public String getNsrsbh() {
        return cpvo != null && cpvo.vsoccrecode != null ? cpvo.vsoccrecode : "";
    }

    public String getNsrmc() {
        return cpvo != null && cpvo.unitname != null ? cpvo.unitname : "";
    }

    public String getCurrDate() {
        return getCurrDate("yyyy-MM-dd");
    }

    /**
     * 取当前系统日期时间
     * @param fmtstr
     * @return
     */
    public String getCurrDate(String fmtstr) {
        SimpleDateFormat df = new SimpleDateFormat(fmtstr);
        return df.format(new Date());
    }

    public Date getInnerBeginDate() {
        return beginDate;
    }

    public Date getInnerEndDate() {
        return endDate;
    }

    public String getBeginDate() {
        return getBeginDate("yyyy-MM-dd");
    }

    public String getEndDate() {
        return getEndDate("yyyy-MM-dd");
    }

    public String getBeginDate(String fmtstr) {
        SimpleDateFormat df = new SimpleDateFormat(fmtstr);
        return df.format(beginDate);
    }

    public String getEndDate(String fmtstr) {
        SimpleDateFormat df = new SimpleDateFormat(fmtstr);
        return df.format(endDate);
    }

    public void setCpvo(CorpVO cpvo) {
        this.cpvo = cpvo;
    }

    public void setQj(String qj) {
        this.qj = qj;
        setPeriod();
    }

    public void setQjlx(String qjlx) {
        this.qjlx = qjlx;
        setPeriod();
    }

    private void setPeriod() {
        if (StringUtil.isEmpty(qj) || StringUtil.isEmpty(qjlx))
            return;
        if (qj.length() != 10){
            qj = qj +"-01";
        }


        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = formatter.parse(qj);
        } catch (Exception e) {
            return;
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        calendar.setTime(date);
        int curMonth = calendar.get(Calendar.MONTH);
        //年
        if (qjlx.equals("2")) {
            if (curMonth == Calendar.DECEMBER) { //只处理12月
                //year + "-01-01" ～ year + "-12-31"
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                beginDate = calendar.getTime(); //date.AddMonths(1 - date.Month).AddDays(1 - date.Day)
            }
        }
        //季
        else if (qjlx.equals("1")) {
            if (curMonth == Calendar.MARCH || curMonth == Calendar.JUNE || curMonth == Calendar.SEPTEMBER || curMonth == Calendar.DECEMBER) { //只处理3、6、9、12（季末）
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 2);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                beginDate = calendar.getTime(); //date.AddMonths(-2).AddDays(1 - date.Day)
            }
        }
        //月
        else if (qjlx.equals("0")) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            beginDate = calendar.getTime(); //date.AddDays(1 - date.Day)
        }
        endDate = date;
    }

    private boolean isLegalDate(String sDate) {
        int legalLen = 10;
        if ((sDate == null) || (sDate.length() != legalLen)) {
            return false;
        }

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = formatter.parse(sDate);
            return sDate.equals(formatter.format(date));
        } catch (Exception e) {
            return false;
        }
    }

    public String getBeginQj() {
        if (!isLegalDate(this.qj)) {
            return "";
        }
        String month = this.qj.substring(5, 7);

        if (!StringUtil.isEmpty(qjlx) && "1".equals(qjlx)) {
            switch (month) {
                case "03":
                    month = "01";
                    break;
                case "06":
                    month = "04";
                    break;
                case "09":
                    month = "07";
                    break;
                case "12":
                    month = "10";
                    break;
            }
        }
        String year = this.qj.substring(0, 4);
        return year + "-" + month + "-01";
    }

    public String getEndQj() {
        return this.qj;
    }

    protected String trim(String str) {
        if (StringUtil.isEmpty(str)) {
            return "";
        }

        str = str.replaceAll("　| |：|\\([^\\(^\\)]*\\)|:|\\（[^\\（^\\）]*\\）|“-”|“-”|\n", "").replaceAll("  ", "").trim();
        if (str.indexOf("、") != -1) {
            str = str.substring(str.indexOf("、") + 1, str.length());
        }

        return str.replaceAll(" ", "");
    }

    /**
     * 设置地区、单/多Excel、会计制度等
     * @param areaType
     * @param corpType
     */
    public void init(String areaType, String corpType) {
        //地区
        this.areaType = areaType;
        //多excel的地区：浙江、上海、河南、厦门、内蒙古、山西、陕西、新疆、宁波、江西等
        List<String> moreDocArea = Arrays.asList("2", "5", "8", "16", "22", "25","26", "27", "29", "30");
        //是否多文件
        this.isMoreDoc = moreDocArea.contains(areaType);
        //会计制度（企业会计准则、小企业会计准则等）
        this.corpType = corpType;
    }

    public Map<String, String> handTaxVoArrKJQJ(SuperVO[] taxvos) {
        boolean isZcfz = taxvos instanceof ZcfzTaxVo[];
        Map<String, String> taxVoMap = new HashMap<>();
        for (SuperVO taxVo : taxvos) {
            if (!isZcfz) { //利润表、现金流量表
                Object vname = taxVo.getAttributeValue("vname");
                Object hc_ref = taxVo.getAttributeValue("hc_ref");
                if (vname != null && hc_ref != null) {
                    taxVoMap.put(trim(vname.toString()), hc_ref.toString());
                }
            } else { //资产负债表，每行生成两份字段映射
                Object zcname = taxVo.getAttributeValue("zcname");
                Object zchc_ref = taxVo.getAttributeValue("zchc_ref");
                if (zcname != null && zchc_ref != null) {
                    taxVoMap.put(trim(zcname.toString()), zchc_ref.toString());
                }
                Object fzname = taxVo.getAttributeValue("fzname");
                Object fzhc_ref = taxVo.getAttributeValue("fzhc_ref");
                if (fzname != null && fzhc_ref != null) {
                    taxVoMap.put(trim(fzname.toString()), fzhc_ref.toString());
                }
            }
        }
        return taxVoMap;
    }

    private Map<String, String> handLrbTaxVoArrKJQJ(LrbTaxVo[] lrbtaxvos) {
        Map<String, String> lrbTaxVoMap = new HashMap();
        for (LrbTaxVo lrbTaxVo : lrbtaxvos) {
            if (lrbTaxVo.getVname() != null && lrbTaxVo.getHc_ref() != null) {
                lrbTaxVoMap.put(trim(lrbTaxVo.getVname()), lrbTaxVo.getHc_ref().toString());
            }
        }
        return lrbTaxVoMap;
    }

    private Map<String, String> handZcfzTaxVoArrKJQJ(ZcfzTaxVo[] zcfztaxvos) {
        Map<String, String> zcfzTaxVoMap = new HashMap<>();

        for (ZcfzTaxVo zcfzTaxVo : zcfztaxvos) {
            if (zcfzTaxVo.getZcname() != null && zcfzTaxVo.getZchc_ref() != null) {
                zcfzTaxVoMap.put(trim(zcfzTaxVo.getZcname()), zcfzTaxVo.getZchc_ref().toString());
            }
            if (zcfzTaxVo.getFzname() != null && zcfzTaxVo.getFzhc_ref() != null) {
                zcfzTaxVoMap.put(trim(zcfzTaxVo.getFzname()), zcfzTaxVo.getFzhc_ref().toString());
            }
        }
        return zcfzTaxVoMap;
    }

    public Map<String, String> handXjllTaxVoArrKJQJ(XjllTaxVo[] xjlltaxvos) {
        Map<String, String> XjllTaxVoMap = new HashMap<>();

        for (XjllTaxVo xjllTaxVo : xjlltaxvos) {
            if (xjllTaxVo.getVname() != null && xjllTaxVo.getHc_ref() != null) {
                XjllTaxVoMap.put(trim(xjllTaxVo.getVname()), xjllTaxVo.getHc_ref().toString());
            }
        }
        return XjllTaxVoMap;
    }

    public Map<String, Workbook> handle(String corptype, LrbTaxVo[] lrbtaxvos, ZcfzTaxVo[] zcfztaxvos, XjllTaxVo[] xjlltaxvos, LrbVO[] lrbvos, XjllbVO[] xjllbvos, ZcFzBVO[] zcFzBVOS) {

        Map<String, LrbVO> lrbVOMap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(lrbvos), new String[]{"hs"});
        Map<String, XjllbVO> xjllbVOMap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(xjllbvos), new String[]{"hc"});
        Map<String, ZcFzBVO> zcFzBVOMap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(zcFzBVOS), new String[]{"hc1"});
        zcFzBVOMap.putAll(DZfcommonTools.hashlizeObjectByPk(Arrays.asList(zcFzBVOS), new String[]{"hc2"}));

        switch (corptype) {
            case "00000100AA10000000000BMD":
                return handleKJQJ2013(handLrbTaxVoArrKJQJ(lrbtaxvos), handZcfzTaxVoArrKJQJ(zcfztaxvos), handXjllTaxVoArrKJQJ(xjlltaxvos), lrbVOMap, xjllbVOMap, zcFzBVOMap);
            case "00000100AA10000000000BMF":
                return handleKJQJ2007(handLrbTaxVoArrKJQJ(lrbtaxvos), handZcfzTaxVoArrKJQJ(zcfztaxvos), handXjllTaxVoArrKJQJ(xjlltaxvos), lrbVOMap, xjllbVOMap, zcFzBVOMap);
            default:
                return new HashMap();
        }
    }

    protected Map<String, Workbook> handleKJQJ2013(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap, Map<String, ZcFzBVO> zcFzBVOMap) {
        Map<String, Workbook> workBookMap = new HashMap<>();
        try {
            //湖北导出的报表文件名，需按税局模板的文件名来，才能成功导入进税局。如：CWBB_XQYKJZZ_V2.0.xls（小企业）、CWBB_QYKJZZ_YBQY_V1.0.xls（一般企业）
            if (this instanceof MoreWorkBookKj2013Excel) {
                MoreWorkBookKj2013Excel moreWorkBookKj2013Excel = (MoreWorkBookKj2013Excel) this;
                workBookMap.put("小企业会计准则利润表", moreWorkBookKj2013Excel.createWorkBookLrbKj2013(lrbVOMap, lrbTaxVoMap));
                workBookMap.put("小企业会计准则资产负债表", moreWorkBookKj2013Excel.createWorkBookZcfzKj2013(zcFzBVOMap, zcfzTaxVoMap));
                workBookMap.put("小企业会计准则现金流量表", moreWorkBookKj2013Excel.createWorkBookXjllKj2013(xjllbVOMap, xjllTaxVoMap));
            } else if (this instanceof OneWorkBookKj2013Excel) {
                OneWorkBookKj2013Excel oneWorkBookKj2013Excel = (OneWorkBookKj2013Excel) this;
                Workbook workbook = oneWorkBookKj2013Excel.createWorkBookKj2013(lrbTaxVoMap, zcfzTaxVoMap, xjllTaxVoMap, lrbVOMap, xjllbVOMap, zcFzBVOMap);
                String xlsname = areaType.equals("7") ? "CWBB_XQYKJZZ_V2.0" : "小企业会计准则财务报表报送与信息采集";
                workBookMap.put(xlsname, workbook);
            }
        } catch (Exception e) {
            log.error("小企业会计准则税局报表导出异常！", e);
        }

        return workBookMap;
    }

    protected Map<String, Workbook> handleKJQJ2007(Map<String, String> lrbTaxVoMap, Map<String, String> zcfzTaxVoMap, Map<String, String> xjllTaxVoMap, Map<String, LrbVO> lrbVOMap, Map<String, XjllbVO> xjllbVOMap, Map<String, ZcFzBVO> zcFzBVOMap) {
        Map<String, Workbook> workBookMap = new HashMap<>();
        try {
            //湖北导出的报表文件名，需按税局模板的文件名来，才能成功导入进税局。如：CWBB_XQYKJZZ_V2.0.xls（小企业）、CWBB_QYKJZZ_YBQY_V1.0.xls（一般企业）
            if (this instanceof MoreWorkBookKj2007Excel) {
                MoreWorkBookKj2007Excel moreWorkBookKj2007Excel = (MoreWorkBookKj2007Excel) this;
                workBookMap.put("企业会计准则利润表", moreWorkBookKj2007Excel.createWorkBookLrbKj2007(lrbVOMap, lrbTaxVoMap));
                workBookMap.put("企业会计准则资产负债表", moreWorkBookKj2007Excel.createWorkBookZcfzKj2007(zcFzBVOMap, zcfzTaxVoMap));
                workBookMap.put("企业会计准则现金流量表", moreWorkBookKj2007Excel.createWorkBookXjllKj2007(xjllbVOMap, xjllTaxVoMap));
            }

            if (this instanceof OneWorkBookKj2007Excel) {
                OneWorkBookKj2007Excel oneWorkBookKj2007Excel = (OneWorkBookKj2007Excel) this;
                Workbook workbook = oneWorkBookKj2007Excel.createWorkBookKj2007(lrbTaxVoMap, zcfzTaxVoMap, xjllTaxVoMap, lrbVOMap, xjllbVOMap, zcFzBVOMap);
                String xlsname = areaType.equals("7") ? "CWBB_QYKJZZ_YBQY_V1.0" : "企业会计准则财务报表报送与信息采集";
                workBookMap.put(xlsname, workbook);
            }
        } catch (Exception e) {
            log.error("企业会计准则税局报表导出异常！", e);
        }

        return workBookMap;
    }

    /**
     * 生成Excel报表
     * 通用处理，不区分会计制度和报表
     * llh
     * 不同会计制度（企业会计准则、小企业会计准则）的主要区别是Excel模板文件不同，可以统一处理；
     * 利润表、现金流量表的生成逻辑一样，资产负债表稍有不同（分左右两大栏），也可以考虑统一处理。
     *
     * @param corptype
     * @param lrbtaxvos
     * @param zcfztaxvos
     * @param xjlltaxvos
     * @param lrbvos
     * @param xjllbvos
     * @param zcFzBVOS
     * @return
     */
    public Map<String, Workbook> handleCommon(String corptype, LrbTaxVo[] lrbtaxvos, ZcfzTaxVo[] zcfztaxvos, XjllTaxVo[] xjlltaxvos, LrbVO[] lrbvos, XjllbVO[] xjllbvos, ZcFzBVO[] zcFzBVOS) {
        //数据
        Map<String, SuperVO> lrbVOMap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(lrbvos), new String[]{"hs"});
        Map<String, SuperVO> xjllbVOMap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(xjllbvos), new String[]{"hc"});
        Map<String, SuperVO> zcFzBVOMap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(zcFzBVOS), new String[]{"hc1"});
        zcFzBVOMap.putAll(DZfcommonTools.hashlizeObjectByPk(Arrays.asList(zcFzBVOS), new String[]{"hc2"}));
        //字段映射
        Map<String, String> lrbTaxVoMap = handTaxVoArrKJQJ(lrbtaxvos);
        Map<String, String> zcfzTaxVoMap = handTaxVoArrKJQJ(zcfztaxvos);
        Map<String, String> xjllTaxVoMap = handTaxVoArrKJQJ(xjlltaxvos);

        String kjzd = getKjzdNameById(corptype); //小企业会计准则、企业会计准则等
        Map<String, Workbook> workBookMap = new HashMap<>();
        try {
            CommonExcelProcess excelCreator = (CommonExcelProcess) this;
            if (isMoreDoc) {
                //创建单表（多文件）
                Workbook rptBook = excelCreator.createOneRptBook(CwbbType.ZCFZB, zcFzBVOMap, zcfzTaxVoMap);
                if (rptBook != null)
                    workBookMap.put(kjzd + "资产负债表", rptBook);
                rptBook = excelCreator.createOneRptBook(CwbbType.LRB, lrbVOMap, lrbTaxVoMap);
                if (rptBook != null)
                    workBookMap.put(kjzd + "利润表", rptBook);
                rptBook = excelCreator.createOneRptBook(CwbbType.XJLLB, xjllbVOMap, xjllTaxVoMap);
                if (rptBook != null)
                    workBookMap.put(kjzd + "现金流量表", rptBook);
            } else {
                //创建单文件（多表）
                Workbook rptBook = excelCreator.createFullRptBook(lrbTaxVoMap, zcfzTaxVoMap, xjllTaxVoMap, zcFzBVOMap, lrbVOMap, xjllbVOMap);
                if (rptBook != null)
                    workBookMap.put(kjzd + "财务报表报送与信息采集", rptBook);

            }
        } catch (Exception e) {
            log.error(kjzd + "税局报表导出异常！", e);
        }

        return workBookMap;
    }

    public static String getKjzdNameById(String schemaId) {
        switch (schemaId) {
            case "00000100AA10000000000BMD": //02-小企业2013小会计
                return "小企业会计准则";
            case "00000100AA10000000000BMF": //04-小企业2007新会计
                return "企业会计准则";
            case "00000100AA10000000000BMQ": //05-民间非盈利组织会计
                return "民间非营利组织会计制度";
            case "00000100000000Ig4yfE0003": //06-事业单位会计
                return "事业单位会计制度";
            case "00000100000000Ig4yfE0005": //08-企业会计制度
                return "企业会计制度";
            default:
                return "";
        }
    }

    protected void handleZcfzbSheet(Sheet sheet, Map<String, String> zcfzTaxVoMap, Map<String, ZcFzBVO> zcFzBVOMap, Integer rowBegin, Integer[] nums, String[] fields) {

        for (int rowIndex = rowBegin; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            String zcname = trim(row.getCell(nums[0]) != null ? row.getCell(nums[0]).getStringCellValue() : "");
            String fzname = trim(row.getCell(nums[3]) != null ? row.getCell(nums[3]).getStringCellValue() : "");
            if (!StringUtil.isEmpty(zcname) && zcfzTaxVoMap.containsKey(zcname) && zcfzTaxVoMap.get(zcname) != null) {
                String hc_ref = zcfzTaxVoMap.get(zcname);
                if (zcFzBVOMap.containsKey(hc_ref)) {
                    ZcFzBVO zcFzBVO = zcFzBVOMap.get(hc_ref);
                    Object qmye1 = zcFzBVO.getAttributeValue(fields[0]);
                    if (qmye1 != null) {
                        row.getCell(nums[1]).setCellValue(new DZFDouble(Double.parseDouble(qmye1.toString()), 2).doubleValue());
                    }
                    Object ncye1 = zcFzBVO.getAttributeValue(fields[1]);
                    if (ncye1 != null) {
                        row.getCell(nums[2]).setCellValue(new DZFDouble(Double.parseDouble(ncye1.toString()), 2).doubleValue());
                    }
                }
            }

            if (!StringUtil.isEmpty(fzname) && zcfzTaxVoMap.containsKey(fzname) && zcfzTaxVoMap.get(fzname) != null) {
                String hc_ref = zcfzTaxVoMap.get(fzname);
                if (zcFzBVOMap.containsKey(hc_ref)) {
                    ZcFzBVO zcFzBVO = zcFzBVOMap.get(hc_ref);
                    Object qmye2 = zcFzBVO.getAttributeValue(fields[2]);
                    if (qmye2 != null) {
                        row.getCell(nums[4]).setCellValue(new DZFDouble(Double.parseDouble(qmye2.toString()), 2).doubleValue());
                    }
                    Object ncye2 = zcFzBVO.getAttributeValue(fields[3]);
                    if (ncye2 != null) {
                        row.getCell(nums[5]).setCellValue(new DZFDouble(Double.parseDouble(ncye2.toString()), 2).doubleValue());
                    }
                }
            }
        }
    }

    protected void handleLrbSheet(Sheet sheet, Map<String, String> lrbTaxVoMap, Map<String, LrbVO> lrbVOMap, Integer rowBegin, Integer[] nums, String[] fields) {
        for (int rowIndex = rowBegin; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            String xmm = trim(row.getCell(nums[0]) != null ? row.getCell(nums[0]).getStringCellValue() : "");
            if (!StringUtil.isEmpty(xmm) && lrbTaxVoMap.containsKey(xmm)) {
                String hc_ref = lrbTaxVoMap.get(xmm);
                if (lrbVOMap.containsKey(hc_ref)) {
                    LrbVO lrbVO = lrbVOMap.get(hc_ref);

                    Object value1 = lrbVO.getAttributeValue(fields[0]);

                    if (value1 != null) {
                        row.getCell(nums[1]).setCellValue(new DZFDouble(Double.parseDouble(value1.toString()), 2).doubleValue());
                    }
                    Object value2 = lrbVO.getAttributeValue(fields[1]);
                    if (value2 != null) {
                        row.getCell(nums[2]).setCellValue(new DZFDouble(Double.parseDouble(value2.toString()), 2).doubleValue());
                    }
                }
            }
        }
    }

    protected void handleXjllSheet(Sheet sheet, Map<String, String> xjllTaxVoMap, Map<String, XjllbVO> xjllbVOMap, Integer rowBegin, Integer[] nums, String[] fields) {
        for (int rowIndex = rowBegin; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            String xmm = trim(row.getCell(nums[0]) != null ? row.getCell(nums[0]).getStringCellValue() : "");

            if ("补充资料".equals(xmm)) {
                break;
            }

            if (!StringUtil.isEmpty(xmm) && xjllTaxVoMap.containsKey(xmm)) {
                String hc_ref = xjllTaxVoMap.get(xmm).toString();
                if (xjllbVOMap.containsKey(hc_ref)) {
                    XjllbVO xjllbVO = xjllbVOMap.get(hc_ref);
                    Object value1 = xjllbVO.getAttributeValue(fields[0]);
                    Object value2 = xjllbVO.getAttributeValue(fields[1]);
                    if (value1 != null) {
                        row.getCell(nums[1]).setCellValue(new DZFDouble(Double.parseDouble(value1.toString()), 2).doubleValue());
                    }
                    if (value2 != null) {
                        row.getCell(nums[2]).setCellValue(new DZFDouble(Double.parseDouble(value2.toString()), 2).doubleValue());
                    }
                }
            }
        }
    }

    /**
     * 填写报表单元格
     * 通用方法（资产负债表表、利润表、现金流量表）
     *
     * @param sheet
     * @param rptTaxVoMap
     * @param rptVOMap
     * @param rowBegin
     * @param nums
     * @param fields
     */
    protected void handleSheet(Sheet sheet, Map<String, String> rptTaxVoMap, Map<String, SuperVO> rptVOMap, Integer rowBegin, Integer[] nums, String[] fields) {
        int count = nums.length > 3 ? 2 : 1;
        for (int rowIndex = rowBegin; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null)
                continue;

            //利润表和现金流量表一行只有一个项目，只需要查看nums[0]一列；资产负债表每行有两个项目，需检查nums[0]、nums[3]两列
            for (int i = 0; i < count; i++) {
                String xmm = trim(row.getCell(nums[i * 3]) != null ? row.getCell(nums[i * 3]).getStringCellValue() : ""); //getNumericCellValue
                if (!StringUtil.isEmpty(xmm) && rptTaxVoMap.containsKey(xmm)) {
                    String hc_ref = rptTaxVoMap.get(xmm);
                    if (rptVOMap.containsKey(hc_ref)) {
                        SuperVO rptVO = rptVOMap.get(hc_ref);
                        Object value1 = rptVO.getAttributeValue(fields[i * 2]);
                        if (value1 != null) {
                            row.getCell(nums[i * 3 + 1]).setCellValue(new DZFDouble(Double.parseDouble(value1.toString()), 2).doubleValue());
                        }
                        Object value2 = rptVO.getAttributeValue(fields[i * 2 + 1]);
                        if (value2 != null) {
                            row.getCell(nums[i * 3 + 2]).setCellValue(new DZFDouble(Double.parseDouble(value2.toString()), 2).doubleValue());
                        }
                    }
                }
            }
        }
    }

    /**
     * 给Excel单元格赋值。按A1、B2的格式指定单元格位置
     *
     * @param sheet
     * @param tocell
     * @param value
     */
    public void putValue(Sheet sheet, String tocell, Object value) {
        //如：B5，指R5C2，即rowno=4，colno=1。更复杂的如：AZ37，指行37列52
        int rowno = 0, colno = 0;
        char c;
        for (int i = 0; i < tocell.length(); i++) {
            c = tocell.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                colno += colno * 26 + (c - 'A' + 1);
            } else {
                rowno += rowno * 10 + (c - '0');
            }
        }
        rowno--;
        colno--;

        sheet.getRow(rowno).getCell(colno).setCellValue(value != null ? value.toString() : "");
    }
}
