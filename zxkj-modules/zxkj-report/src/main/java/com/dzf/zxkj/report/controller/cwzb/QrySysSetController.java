package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.ColumnCellAttr;
import com.dzf.zxkj.common.query.ReportPrintParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.ExMultiVO;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.model.report.KmReportDatagridColumn;
import com.dzf.zxkj.platform.model.report.ReportDataGrid;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.entity.ReportExcelExportVO;
import com.dzf.zxkj.report.print.cwzb.MultiColumnPdfField;
import com.dzf.zxkj.report.service.cwzb.IMultiColumnReport;
import com.dzf.zxkj.report.utils.ExcelReport;
import com.dzf.zxkj.report.utils.SystemUtil;
import com.itextpdf.text.BaseColor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

@RestController
@RequestMapping("gl_rep_qrysysset")
@Slf4j
public class QrySysSetController extends ReportBaseController {

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;


    /**
     * 获取辅助类别参照
     */
    @GetMapping("queryCurrency")
    public ReturnData<Grid> getCurrency(@RequestParam("corpid") String pk_corp) {
        if (StringUtil.isEmpty(pk_corp)) {
            pk_corp = SystemUtil.getLoginCorpId();
        }
        Grid grid = new Grid();
        try {
            BdCurrencyVO[] bvos = zxkjPlatformService.queryCurrencyByPkCorp(pk_corp);
            grid.setRows(bvos);
            grid.setSuccess(true);
        } catch (Exception e) {
            log.error("币种查询失败:", e);
            grid.setRows(new ArrayList<AuxiliaryAccountHVO>());
            printErrorLog(grid, e, "币种查询失败!");
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 获取辅助类别参照
     */
    @GetMapping("queryFzLb")
    public ReturnData<Grid> getFzLb(@RequestParam("corpid") String pk_corp) {
        if (StringUtil.isEmpty(pk_corp)) {
            pk_corp = SystemUtil.getLoginCorpId();
        }
        Grid grid = new Grid();
        try {
            AuxiliaryAccountHVO[] bvos = zxkjPlatformService.queryHByPkCorp(pk_corp);
            grid.setRows(bvos);
            grid.setSuccess(true);
        } catch (Exception e) {
            log.error("辅助类别查询失败:", e);
            grid.setRows(new ArrayList<AuxiliaryAccountHVO>());
            printErrorLog(grid, e, "辅助类别查询失败!");
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 获取辅助项目参照
     */
    @GetMapping("queryFzxm")
    public ReturnData<Grid> getFzxm(@RequestParam("corpid") String pk_corp) {
        if (StringUtil.isEmpty(pk_corp)) {
            pk_corp = SystemUtil.getLoginCorpId();
        }
        Grid grid = new Grid();
        try {
            AuxiliaryAccountBVO[] bvos = zxkjPlatformService.queryAllB(pk_corp);
            grid.setRows(bvos);
            grid.setSuccess(true);
        } catch (Exception e) {
            log.error("辅助项目查询失败:", e);
            grid.setRows(new ArrayList<AuxiliaryAccountHVO>());
            printErrorLog(grid, e, "辅助项目查询失败!");
        }
        return ReturnData.ok().data(grid);
    }





}
