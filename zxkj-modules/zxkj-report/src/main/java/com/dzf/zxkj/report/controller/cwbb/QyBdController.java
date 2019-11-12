package com.dzf.zxkj.report.controller.cwbb;

import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.ColumnCellAttr;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.report.QyBdVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.excel.cwbb.QyBdExcelField;
import com.dzf.zxkj.report.print.cwbb.QyBdPdfField;
import com.dzf.zxkj.report.service.cwbb.IQyBdService;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("gl_rep_qybdact")
@Slf4j
public class QyBdController extends ReportBaseController {
    @Autowired
    private IQyBdService qybdser;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @RequestMapping("query")
    public ReturnData<Grid> query(QueryParamVO queryParamVO, @MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();
        try {
            if (queryParamVO.getPk_corp() == null || queryParamVO.getPk_corp().trim().length() == 0) {
                // 如果编制单位为空则取当前默认公司
                queryParamVO.setPk_corp(corpVO.getPk_corp());
            }
            List<QyBdVO> list = qybdser.queryList(queryParamVO);
            grid.setSuccess(true);
            grid.setRows(list);
            grid.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(grid);
    }

    @PostMapping("export/excel")
    public void export(String list, String corpName, String period, @MultiRequestBody UserVO userVO, HttpServletResponse response) throws IOException {
        QyBdVO[] listVo = JsonUtils.deserialize(list, QyBdVO[].class);

        Excelexport2003<QyBdVO> lxs = new Excelexport2003<QyBdVO>();
        QyBdExcelField qyBdExcelField = new QyBdExcelField();
        qyBdExcelField.setYwhdvos(listVo);
        qyBdExcelField.setQj(period);
        qyBdExcelField.setCreator(userVO.getCuserid());
        qyBdExcelField.setCorpName(corpName);


        baseExcelExport(response,lxs,qyBdExcelField);
    }

    @PostMapping("print/pdf")
    public void print(String corpName, String period, PrintParamVO printParamVO, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response) {
        try {
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            QyBdVO[] bodyvos = JsonUtils.deserialize(printParamVO.getList(), QyBdVO[].class);
            ColumnCellAttr[] columncellattrvos = QyBdPdfField.getColumnCellList();
            //初始化表头
            Map<String, String> tmap = new LinkedHashMap<>();// 声明一个map用来存前台传来的设置参数
            tmap.put("公司", corpName);
            tmap.put("期间", period);
            tmap.put("单位", "元");

            printReporUtil.setIscross(DZFBoolean.FALSE);//是否横向

            if ("2".equals(printParamVO.getType())) {//B5显示12f
                printReporUtil.setLineheight(12f);//设置行高
            }
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(printParamVO.getFont()), Font.NORMAL));//设置表头字体
            //初始化表体列编码和列名称
            printReporUtil.printReport(bodyvos, QyBdPdfField.name, Arrays.asList(columncellattrvos), 18, printParamVO.getType(), pmap, tmap);
        } catch (DocumentException e) {
            log.error("打印错误", e);
        } catch (IOException e) {
            log.error("打印错误", e);
        }
    }
}
