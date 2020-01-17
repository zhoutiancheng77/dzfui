package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.ColumnCellAttr;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.report.FzYebVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwzb.IFzhsYebReport;
import com.dzf.zxkj.report.utils.ExcelReport1;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.dzf.zxkj.report.utils.SystemUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 * 辅助余额表
 */
@RestController
@RequestMapping("gl_rep_fzyebact")
@SuppressWarnings("all")
@Slf4j
public class AuxiliaryBalanceReportController extends BaseController {
    @Autowired
    private IFzhsYebReport gl_rep_fzyebserv;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    /**
     * 查询辅助余额表数据
     */
    @GetMapping("/queryAction")
    public ReturnData queryAction(@RequestParam Map<String, String> param) {
        Grid grid = new Grid();

        KmReoprtQueryParamVO queryParam = JsonUtils.convertValue(param, KmReoprtQueryParamVO.class);

        CorpVO corpVo = SystemUtil.getLoginCorpVo();
        DZFDate beginDate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(corpVo.getBegindate()));
        if (beginDate.after(queryParam.getBegindate1())) {
            throw new BusinessException("开始日期不能在建账日期(" + DateUtils.getPeriod(beginDate) + ")前!");
        }
        if (StringUtils.isBlank(queryParam.getPk_corp())) {
            queryParam.setPk_corp(corpVo.getPk_corp());
        }
        // 校验
        checkSecurityData(null, new String[]{queryParam.getPk_corp()},null);

        List<FzYebVO> fzyevoList = gl_rep_fzyebserv.getFzYebVOs(queryParam);
        if (fzyevoList != null && fzyevoList.size() > 0) {
            grid.setSuccess(true);
            grid.setTotal(fzyevoList == null ? 0 : (long) fzyevoList.size());
            grid.setRows(fzyevoList == null ? new ArrayList<FzYebVO>() : fzyevoList);
        } else {
            grid.setSuccess(false);
            grid.setRows(new ArrayList<FzYebVO>());
            grid.setMsg("查询为空!");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                "辅助余额表查询:" + queryParam.getBegindate1().toString().substring(0, 7)
                        + "-" + queryParam.getEnddate().toString().substring(0, 7));
        return ReturnData.ok().data(grid);
    }

    @PostMapping("print/pdf")
    public void printPdf(@RequestParam Map<String, String> pmap1, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response) {
        PrintParamVO printParamVO = JsonUtils.deserialize(JsonUtils.serialize(pmap1), PrintParamVO.class);
        KmReoprtQueryParamVO queryparamvo = JsonUtils.deserialize(JsonUtils.serialize(pmap1), KmReoprtQueryParamVO.class);
        // 校验
        checkSecurityData(null, new String[]{queryparamvo.getPk_corp()},null);
        PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
        try {
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            /** 是否横向 */
            printReporUtil.setIscross(DZFBoolean.TRUE);
            FzYebVO[] bodyvos = JsonUtils.deserialize(printParamVO.getList(), FzYebVO[].class);
            printReporUtil.setDefaultValue(printParamVO.getShowbm(), bodyvos);
            /** 声明一个map用来存前台传来的设置参数 */
            Map<String, String> tmap = new LinkedHashMap<>();
            tmap.put("公司", bodyvos[0].getGs());
            tmap.put("期间", bodyvos[0].getTitlePeriod());
            tmap.put("单位", new ReportUtil(zxkjPlatformService).getCurrencyByPk(bodyvos[0].getPk_currency()));
            /** 设置表头字体*/
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));

            String titlename = "辅助余额表";
            if ("Y".equals(printParamVO.getShowlb()) && !StringUtil.isEmpty(printParamVO.getFzlb_name())) {
                titlename = printParamVO.getFzlb_name() + titlename;
            }
            printReporUtil.setLineheight(22f);
            if (!StringUtil.isEmpty(queryparamvo.getPk_currency()) && !DzfUtil.PK_CNY.equals(queryparamvo.getPk_currency())) {
                printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), bodyvos, titlename,
                        getWbColumn(printParamVO.getShowbm()), getWbColumnName(printParamVO.getShowbm()), getWbColumnWidth(printParamVO.getShowbm()), 0, printParamVO.getType(), pmap, tmap);
            } else {
                printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), bodyvos, titlename,
                        getColumn(printParamVO.getShowbm()), getColumnName(printParamVO.getShowbm()), getColumnWidth(printParamVO.getShowbm()), 0, printParamVO.getType(), pmap, tmap);
            }
        } catch (DocumentException e) {
            log.error("打印错误", e);
        } catch (IOException e) {
            log.error("打印错误", e);
        }
    }

    private String[] getColumn(String showbm) {
        if ("Y".equals(showbm)) {
            return new String[]{"fzhsxCode", "fzhsxName", "qcyejf", "qcyedf", "bqfsjf", "bqfsdf", "bnljjf", "bnljdf",
                    "qmyejf", "qmyedf"};
        } else {
            return new String[]{"fzhsxName", "qcyejf", "qcyedf", "bqfsjf", "bqfsdf", "bnljjf", "bnljdf",
                    "qmyejf", "qmyedf"};
        }
    }

    private int[] getWbColumnWidth(String showbm) {
        if ("Y".equals(showbm)) {
            return new int[]{2, 5, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
        } else {
            return new int[]{5, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
        }
    }

    private int[] getColumnWidth(String showbm) {
        if ("Y".equals(showbm)) {
            return new int[]{4, 5, 3, 3, 3, 3, 3, 3, 3, 3};
        } else {
            return new int[]{5, 3, 3, 3, 3, 3, 3, 3, 3};
        }
    }

    private String[] getColumnName(String showbm) {
        if ("Y".equals(showbm)) {
            return new String[]{"编码", "名称", "期初余额_借方", "期初余额_贷方", "本期发生额_借方", "本期发生额_贷方", "本年累计发生额_借方",
                    "本年累计发生额_贷方", "期末余额_借方", "期末余额_贷方"};
        } else {
            return new String[]{"名称", "期初余额_借方", "期初余额_贷方", "本期发生额_借方", "本期发生额_贷方", "本年累计发生额_借方",
                    "本年累计发生额_贷方", "期末余额_借方", "期末余额_贷方"};
        }
    }

    private String[] getWbColumnName(String showbm) {
        if ("Y".equals(showbm)) {
            return new String[]{"编码", "名称", "期初余额_借方(原币)", "期初余额_借方(本位币)",
                    "期初余额_贷方(原币)", "期初余额_贷方(本位币)",
                    "本期发生额_借方(原币)", "本期发生额_借方(本位币)",
                    "本期发生额_贷方(原币)", "本期发生额_贷方(本位币)",
                    "本年累计发生额_借方(原币)", "本年累计发生额_借方(本位币)",
                    "本年累计发生额_贷方(原币)", "本年累计发生额_贷方(本位币)",
                    "期末余额_借方(原币)", "期末余额_借方(本位币)",
                    "期末余额_贷方(原币)", "期末余额_贷方(本位币)"};
        } else {
            return new String[]{"名称", "期初余额_借方(原币)", "期初余额_借方(本位币)",
                    "期初余额_贷方(原币)", "期初余额_贷方(本位币)",
                    "本期发生额_借方(原币)", "本期发生额_借方(本位币)",
                    "本期发生额_贷方(原币)", "本期发生额_贷方(本位币)",
                    "本年累计发生额_借方(原币)", "本年累计发生额_借方(本位币)",
                    "本年累计发生额_贷方(原币)", "本年累计发生额_贷方(本位币)",
                    "期末余额_借方(原币)", "期末余额_借方(本位币)",
                    "期末余额_贷方(原币)", "期末余额_贷方(本位币)"};
        }
    }

    private String[] getWbColumn(String showbm) {
        if ("Y".equals(showbm)) {
            return new String[]{"fzhsxCode", "fzhsxName", "ybqcyejf", "qcyejf", "ybqcyedf", "qcyedf",
                    "ybbqfsjf", "bqfsjf", "ybbqfsdf", "bqfsdf",
                    "ybbnljjf", "bnljjf", "ybbnljdf", "bnljdf",
                    "ybqmyejf", "qmyejf", "ybqmyedf", "qmyedf"};
        } else {
            return new String[]{"fzhsxName", "ybqcyejf", "qcyejf", "ybqcyedf", "qcyedf",
                    "ybbqfsjf", "bqfsjf", "ybbqfsdf", "bqfsdf",
                    "ybbnljjf", "bnljjf", "ybbnljdf", "bnljdf",
                    "ybqmyejf", "qmyejf", "ybqmyedf", "qmyedf"};
        }
    }

    /**
     * 导出Excel
     */
    @PostMapping("/export/excel")
    public void excelReport(@MultiRequestBody ColumnCellAttr[] columncellattrvos, @MultiRequestBody KmReoprtQueryParamVO queryparamvo, @RequestParam Map<String, String> param, HttpServletResponse response) {
        FzYebVO[] listVo = JsonUtils.deserialize(queryparamvo.getList(), FzYebVO[].class);
        // 校验
        checkSecurityData(null, new String[]{queryparamvo.getPk_corp()},null);
        ExcelReport1 ex = new ExcelReport1();
        ex.setCurrency(new ReportUtil(zxkjPlatformService).getCurrencyByPk(queryparamvo.getPk_currency()));
        Map<String, String> map = getExpFieldMap(columncellattrvos);
        String[] enFields = new String[map.size()];
        String[] cnFields = new String[map.size()];
        /** 填充普通字段数组 */
        int count = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            enFields[count] = entry.getKey();
            cnFields[count] = entry.getValue();
            count++;
        }
        List<FzYebVO> list = new ArrayList<FzYebVO>();
        for (FzYebVO vo : listVo) {
            list.add(vo);
        }
        OutputStream toClient = null;
        try {
            response.reset();
            String date = DateUtils.getDate(new Date());
            String fileName = "辅助余额表-" + ReportUtil.formatQj(queryparamvo.getPeriod()) + ".xls";
            String formattedName = URLEncoder.encode(fileName, "UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName + ";filename*=UTF-8''" + formattedName);

            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            byte[] length = ex.exportExcel("辅助余额表", cnFields, enFields, list, queryparamvo.getCorpname(), queryparamvo.getPeriod(), toClient, zxkjPlatformService);

            toClient.flush();
            response.getOutputStream().flush();
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
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("excel导出错误", e);
            }
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                "辅助余额表导出:" + queryparamvo.getBegindate1().toString().substring(0, 7)
                        + "-" + queryparamvo.getEnddate().toString().substring(0, 7), ISysConstants.SYS_2);

    }

    private Map<String, String> getExpFieldMap(ColumnCellAttr[] cellattrs) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (int i = 0; i < cellattrs.length; i++) {
            map.put(cellattrs[i].getColumn(), cellattrs[i].getColumname());
        }
        return map;
    }
}
