package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.entity.ReportExcelExportVO;
import com.dzf.zxkj.report.excel.cwzb.KmzzExcelField;
import com.dzf.zxkj.report.service.cwzb.IKMZZReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("gl_rep_kmzjact")
@Slf4j
public class KmzzController extends ReportBaseController {

    @Autowired
    private IKMZZReport gl_rep_kmzjserv;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    /**
     * 查询科目明细数据
     */
    @PostMapping("/queryAction")
    public ReturnData<Grid> queryAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();
        QueryParamVO queryParamvo = getQueryParamVO(queryvo, corpVO);
        try {
            // 校验
            checkSecurityData(null, new String[]{queryParamvo.getPk_corp()},null);
            KmZzVO[] vos = null;
            /** 验证 查询范围应该在当前登录人的权限范围内 */
            checkPowerDate(queryParamvo, corpVO);
            queryParamvo.setIsnomonthfs(DZFBoolean.TRUE);
            queryParamvo.setBtotalyear(DZFBoolean.TRUE);
            KmZzVO[] kmmxvos = gl_rep_kmzjserv.getKMZZVOs(queryParamvo, null);
            new ReportUtil().updateKFx(kmmxvos);
            /** 如果有期初余额则不显示下面的 */
            List<KmZzVO> listmx = filterQC(kmmxvos);
            vos = listmx.toArray(new KmZzVO[0]);
            grid.setTotal((long) (vos == null ? 0 : vos.length));
            if (vos != null && vos.length > 0) {
                vos = getPagedZZVOs(vos, queryvo.getPage(), queryvo.getRows());
            }
            grid.setRows(vos == null ? new ArrayList<KmZzVO>() : Arrays.asList(vos));
            grid.setSuccess(true);

        } catch (Exception e) {
            grid.setRows(new ArrayList<KmZzVO>());
            printErrorLog(grid, e, "查询失败！");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                "科目总账查询:" + queryParamvo.getBegindate1().toString().substring(0, 7) +
                        "-" + queryParamvo.getEnddate().toString().substring(0, 7), ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    public static List<KmZzVO> filterQC(KmZzVO[] kmmxvos) {
        HashMap<String, DZFBoolean> mapshow = new HashMap<String, DZFBoolean>();
        for (KmZzVO mxzvo : kmmxvos) {
            mapshow.put(mxzvo.getPk_accsubj(), DZFBoolean.FALSE);
        }
        List<KmZzVO> listmx = new ArrayList<KmZzVO>();
        for (KmZzVO mxzvo : kmmxvos) {
            if (mapshow.get(mxzvo.getPk_accsubj()).booleanValue() && "期初余额".equals(mxzvo.getZy())) {
                continue;
            }

            listmx.add(mxzvo);
            if (!mapshow.get(mxzvo.getPk_accsubj()).booleanValue() && "期初余额".equals(mxzvo.getZy())) {
                mapshow.put(mxzvo.getPk_accsubj(), DZFBoolean.TRUE);
            }
        }
        return listmx;
    }

    /**
     * 将查询后的结果分页
     *
     * @param kmmxvos
     * @param page
     * @param rows
     * @return
     */
    public KmZzVO[] getPagedZZVOs(KmZzVO[] kmmxvos, int page, int rows) {
        int beginIndex = rows * (page - 1);
        int endIndex = rows * page;
        if (endIndex >= kmmxvos.length) {// 防止endIndex数组越界
            endIndex = kmmxvos.length;
        }
        kmmxvos = Arrays.copyOfRange(kmmxvos, beginIndex, endIndex);
        return kmmxvos;
    }

    @Override
    public String getPrintTitleName() {
        return "科 目 总 账";
    }

    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    public Map<String, List<SuperVO>> sortMapByKey(Map<String, List<SuperVO>> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, List<SuperVO>> sortMap = new TreeMap<String, List<SuperVO>>(new Comparator<String>() {
            public int compare(String str1, String str2) {
                return str1.compareTo(str2);
            }
        }
        );
        sortMap.putAll(map);

        return sortMap;
    }


    /**
     * 导出Excel
     */
    @PostMapping("export/excel")
    public void excelReport(@MultiRequestBody ReportExcelExportVO excelExportVO, @MultiRequestBody KmReoprtQueryParamVO queryparamvo, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {
        // 校验
        checkSecurityData(null, new String[]{queryparamvo.getPk_corp()},null);
        String gs = excelExportVO.getCorpName();
        String qj = excelExportVO.getTitleperiod();
        String pk_currency = queryparamvo.getPk_currency();
        queryparamvo.setBtotalyear(DZFBoolean.TRUE);
        queryparamvo.setIsnomonthfs(DZFBoolean.TRUE);
        KmZzVO[] kmmxvos = gl_rep_kmzjserv.getKMZZVOs(queryparamvo, null);
        ReportUtil.updateKFx(kmmxvos);
        /** 如果有期初余额则不显示下面的 */
        List<KmZzVO> listmx = filterQC(kmmxvos);
        KmZzVO[] listVo = listmx.toArray(new KmZzVO[0]);//KmzzReportCache.getInstance().get(userid);
        String currencyname = new ReportUtil().getCurrencyDw(queryparamvo.getCurrency());
        String[] periods = new String[]{qj};
        String[] allsheetname = new String[]{"科目总账"};
//        CorpVO qrycorpvo = zxkjPlatformService.queryCorpByPk(queryparamvo.getPk_corp());

        KmzzExcelField field = new KmzzExcelField("科目总账", queryparamvo.getPk_currency(), currencyname, periods, allsheetname, qj, gs);
        List<KmZzVO[]> results = new ArrayList<KmZzVO[]>();
        results.add(listVo);
        field.setAllsheetzcvos(results);

        Excelexport2003<KmZzVO> lxs = new Excelexport2003<KmZzVO>();
        baseExcelExport(response, lxs, field);
        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                "科目总账导出:" + queryparamvo.getBegindate1().toString().substring(0, 7) +
                        "-" + queryparamvo.getEnddate().toString().substring(0, 7), ISysConstants.SYS_2);
    }

    /**
     * 打印操作
     */
    @PostMapping("print")
    public void printAction(@RequestParam Map<String, String> pmap1, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response) {
        try {

            PrintParamVO printParamVO = JsonUtils.deserialize(JsonUtils.serialize(pmap1), PrintParamVO.class);
            KmReoprtQueryParamVO queryparamvo = JsonUtils.deserialize(JsonUtils.serialize(pmap1), KmReoprtQueryParamVO.class);

            // 校验
            checkSecurityData(null, new String[]{queryparamvo.getPk_corp()},null);

            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            String pageOrt = pmap.get("pageOrt");
            String lineHeight = pmap.get("lineHeight");
            String font = pmap.get("font");
            String type = pmap.get("type");
            if (pageOrt.equals("Y")) {
                printReporUtil.setIscross(DZFBoolean.TRUE);//是否横向
            } else {
                printReporUtil.setIscross(DZFBoolean.FALSE);//是否横向
            }
            queryparamvo.setBtotalyear(DZFBoolean.TRUE);
            queryparamvo.setIsnomonthfs(DZFBoolean.TRUE);
            KmZzVO[] bodyvos = reloadNewValue(printParamVO.getTitleperiod(), printParamVO.getCorpName(), printParamVO.getIsPaging(), queryparamvo);
            ReportUtil.updateKFx(bodyvos);
            Map<String, String> tmap = new LinkedHashMap<>();// 声明一个map用来存前台传来的设置参数

            if (bodyvos != null && bodyvos.length > 0) {
                tmap.put("公司", printParamVO.getCorpName());
                tmap.put("期间", printParamVO.getTitleperiod());
                tmap.put("单位", new ReportUtil(zxkjPlatformService).getCurrencyByPk(queryparamvo.getPk_currency()));
            }
            printReporUtil.setLineheight(StringUtil.isEmpty(lineHeight) ? 22f : Float.parseFloat(lineHeight));
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(font), Font.NORMAL));//设置表头字体
            Object[] obj = null;
            /** 需要分页打印 */
            if (bodyvos != null && bodyvos.length > 0 && printParamVO.getIsPaging().equals("Y")) {
                List<SuperVO> mxlist = null;
                Map<String, List<SuperVO>> mxmap = new HashMap<String, List<SuperVO>>();
                String kmfullname;
                Map<String, YntCpaccountVO> cpamap = zxkjPlatformService.queryMapByPk(queryparamvo.getPk_corp());
                for (KmZzVO mxvo : bodyvos) {
                    /** 设置公司名称 */
                    mxvo.setGs(bodyvos[0].getGs());
                    /** map里的key 不包含当前数据科目编码 */
                    if (!mxmap.containsKey(mxvo.getKmbm())) {
                        /**  就 创建一个list  把这条数据 加进去 */
                        mxlist = new ArrayList<SuperVO>();
                        kmfullname = putHeadForKmPage(cpamap, mxvo);
                        mxvo.setKmfullname(kmfullname);
                        mxvo.setTitlePeriod(tmap.get("期间"));
                        mxlist.add(mxvo);
                    } else {
                        /** map里的key 包含当前数据科目编码 */
                        mxlist = mxmap.get(mxvo.getKmbm());
                        mxlist.add(mxvo);
                    }
                    mxmap.put(mxvo.getKmbm(), mxlist);
                }
                /** 排序--根据key排序 */
                mxmap = sortMapByKey(mxmap);
                if (!StringUtil.isEmpty(queryparamvo.getPk_currency()) && !queryparamvo.getPk_currency().equals(DzfUtil.PK_CNY)) {
                    obj = getPrintXm(3);
                } else {
                    obj = getPrintXm(2);
                }
                printReporUtil.printHz(mxmap, new SuperVO[]{}, "*科目总账", (String[]) obj[0],
                        (String[]) obj[1], (int[]) obj[2], (int) obj[3], pmap, tmap);
            } else {/** 不需要分页打印 */
                int len = bodyvos == null ? 0 : bodyvos.length;
                for (int i = 0; i < len; i++) {
                    bodyvos[i].setKm(bodyvos[i].getKmbm() + "_" + bodyvos[i].getKm());
                }
                if (!StringUtil.isEmpty(queryparamvo.getPk_currency()) && !queryparamvo.getPk_currency().equals(DzfUtil.PK_CNY)) {
                    obj = getPrintXm(1);
                } else {
                    obj = getPrintXm(0);
                }
                printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), bodyvos, "科目总账",
                        (String[]) obj[0], (String[]) obj[1], (int[]) obj[2], (int) obj[3], pmap, tmap);
                for (int i = 0; i < len; i++) {
                    bodyvos[i].setKm(bodyvos[i].getKm().substring(bodyvos[i].getKm().lastIndexOf('_') + 1));
                }
            }
            writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                    "科目总账打印:" +  printParamVO.getTitleperiod(), ISysConstants.SYS_2);
        } catch (DocumentException e) {
            log.error("错误", e);
        } catch (IOException e) {
            log.error("错误", e);
        }
    }

    /**
     * 分页打印 设置科目(标题用)和科目全称(head使用 和公司，期间并列)
     *
     * @param cpamap
     * @param mxvo
     * @return
     */
    private String putHeadForKmPage(Map<String, YntCpaccountVO> cpamap, KmZzVO mxvo) {
        String kmfullname;
        if (cpamap.containsKey(mxvo.getPk_accsubj())) {
            mxvo.setKm(cpamap.get(mxvo.getPk_accsubj()).getAccountname());
        }
        if (mxvo.getPk_accsubj() != null
                && (mxvo.getPk_accsubj()).length() > 24) {//默认有辅助项目
            kmfullname = mxvo.getKmfullname() + "/" + mxvo.getKm() + "(" + mxvo.getKmbm() + ")";
        } else {
            kmfullname = mxvo.getKmfullname() + "(" + mxvo.getKmbm() + ")";
        }
        return kmfullname;
    }

    public Object[] getPrintXm(int type) {
        Object[] obj = new Object[4];
        switch (type) {
            case 3:
                obj[0] = new String[]{"period", "zy", "ybjf", "jf", "ybdf", "df", "fx", "ybye", "ye"};
                obj[1] = new String[]{"期间", "摘要", "借方_原币", "借方_本位币", "贷方_原币", "贷方_本位币", "方向", "余额_原币", "余额_本位币"};
                obj[2] = new int[]{2, 5, 3, 3, 3, 3, 1, 3, 3};
                obj[3] = 20;
                break;
            case 2:
                obj[0] = new String[]{"period", "zy", "jf", "df", "fx", "ye"};
                obj[1] = new String[]{"期间", "摘要", "借方", "贷方", "方向", "余额"};
                obj[2] = new int[]{2, 5, 3, 3, 1, 3};
                obj[3] = 20;
                break;
            case 1:
                obj[0] = new String[]{"km", "period", "zy", "ybjf", "jf", "ybdf", "df", "fx", "ybye", "ye"};
                obj[1] = new String[]{"科目", "期间", "摘要", "借方_原币", "借方_本位币", "贷方_原币", "贷方_本位币", "方向", "余额_原币", "余额_本位币"};
                obj[2] = new int[]{7, 2, 5, 3, 3, 3, 3, 1, 3, 3};
                obj[3] = 20;
                break;
            case 0:
                obj[0] = new String[]{"km", "period", "zy", "jf", "df", "fx", "ye"};
                obj[1] = new String[]{"科目", "期间", "摘要", "借方", "贷方", "方向", "余额"};
                obj[2] = new int[]{7, 2, 5, 3, 3, 1, 3};
                obj[3] = 20;
                break;
            default:
                break;
        }
        return obj;
    }

    public KmZzVO[] reloadNewValue(String titlePeriod, String gs, String isPaging, QueryParamVO queryParamvo) {
        queryParamvo.setBtotalyear(DZFBoolean.TRUE);
        KmZzVO[] bodyvos = gl_rep_kmzjserv.getKMZZVOs(queryParamvo, null);
        bodyvos = filterQC(bodyvos).toArray(new KmZzVO[0]);
        bodyvos[0].setIsPaging(isPaging);
        bodyvos[0].setGs(gs);
        bodyvos[0].setTitlePeriod(titlePeriod);
        return bodyvos;
    }

}
