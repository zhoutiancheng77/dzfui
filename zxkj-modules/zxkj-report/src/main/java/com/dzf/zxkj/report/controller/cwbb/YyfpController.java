package com.dzf.zxkj.report.controller.cwbb;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.ColumnCellAttr;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.report.YyFpVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.entity.ReportExcelExportVO;
import com.dzf.zxkj.report.excel.cwbb.YyFpExcelField;
import com.dzf.zxkj.report.print.cwbb.YyFpPdfField;
import com.dzf.zxkj.report.service.cwbb.IYyFpService;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("gl_rep_yyfpact")
public class YyfpController extends ReportBaseController {

    @Autowired
    private IYyFpService yyfpser;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @GetMapping("query")
//    @LogRecord(type = LogRecordEnum.OPE_KJ_CWREPORT, msg = "查询盈余分配表")
    public ReturnData<Grid> query(QueryParamVO queryParamVO, @MultiRequestBody CorpVO corpVO) {

        Grid grid = new Grid();
        try {
            if (queryParamVO.getPk_corp() == null || queryParamVO.getPk_corp().trim().length() == 0) {
                // 如果编制单位为空则取当前默认公司
                queryParamVO.setPk_corp(corpVO.getPk_corp());
            }

            // 校验
            checkSecurityData(null, new String[]{queryParamVO.getPk_corp()},null);
            List<YyFpVO> list = yyfpser.queryList(queryParamVO);
            grid.setSuccess(true);
            grid.setRows(list);
            grid.setMsg("查询成功");
        } catch (DZFWarpException e) {
            printErrorLog(grid, e, "查询失败");
            log.error("查询失败", e);
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT,"盈余分配表查询"+queryParamVO.getBegindate1().toString().substring(0,7), ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    @PostMapping("export/excel")
    public void export(@MultiRequestBody ReportExcelExportVO excelExportVO, @MultiRequestBody UserVO userVO, @MultiRequestBody KmReoprtQueryParamVO queryparamvo, HttpServletResponse response) throws IOException {
        YyFpVO[] listVo = JsonUtils.deserialize(excelExportVO.getList(), YyFpVO[].class);

        Excelexport2003<YyFpVO> lxs = new Excelexport2003<YyFpVO>();
        YyFpExcelField yhd = new YyFpExcelField();
        yhd.setZeroshownull(!queryparamvo.getBshowzero().booleanValue());
        yhd.setYwhdvos(listVo);
        yhd.setQj(excelExportVO.getPeriod());
        yhd.setCreator(userVO.getCuserid());
        yhd.setCorpName(excelExportVO.getCorpName());
        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT,"盈余分配表导出:"+excelExportVO.getPeriod().substring(0,7), ISysConstants.SYS_2);
        baseExcelExport(response,lxs,yhd);

    }

    @PostMapping("print")
    public void print(@RequestParam Map<String, String> pmap1, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response) {

        try {
            // 校验
            checkSecurityData(null, new String[]{corpVO.getPk_corp()},null);
            PrintParamVO printParamVO = JsonUtils.deserialize(JsonUtils.serialize(pmap1), PrintParamVO.class);
            QueryParamVO queryparamvo = JsonUtils.deserialize(JsonUtils.serialize(pmap1), QueryParamVO.class);
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            YyFpVO[] bodyvos = JsonUtils.deserialize(printParamVO.getList(), YyFpVO[].class);
            ColumnCellAttr[] columncellattrvos = YyFpPdfField.getColumnCellList();
            //初始化表头
            Map<String, String> tmap = new LinkedHashMap<>();// 声明一个map用来存前台传来的设置参数
            tmap.put("公司", printParamVO.getCorpName());
            tmap.put("期间", printParamVO.getPeriod());
            tmap.put("单位", "元");

            printReporUtil.setIscross(DZFBoolean.FALSE);//是否横向

            if ("2".equals(printParamVO.getType())) {//B5显示12f
                printReporUtil.setLineheight(12f);//设置行高
            }

            printReporUtil.setBshowzero(queryparamvo.getBshowzero());
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(printParamVO.getFont()), Font.NORMAL));//设置表头字体
            //初始化表体列编码和列名称
            printReporUtil.printReport(bodyvos, "盈余分配表", Arrays.asList(columncellattrvos), 18, printParamVO.getType(), pmap, tmap);
            writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT,"盈余分配表打印:"+printParamVO.getPeriod().substring(0,7), ISysConstants.SYS_2);
        } catch (DocumentException e) {
            log.error("打印错误", e);
        } catch (IOException e) {
            log.error("打印错误", e);
        }
    }

}
