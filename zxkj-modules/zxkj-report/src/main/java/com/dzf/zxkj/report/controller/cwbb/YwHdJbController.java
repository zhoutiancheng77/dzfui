package com.dzf.zxkj.report.controller.cwbb;

import com.dzf.zxkj.common.model.ColumnCellAttr;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.report.YwHdVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.entity.ReportExcelExportVO;
import com.dzf.zxkj.report.excel.cwbb.YwHdQuarterlyExcelField;
import com.dzf.zxkj.report.service.cwbb.IYwHdReport;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 业务活动季报
 */
@RestController
@RequestMapping("gl_rep_ywhdquarterlybact")
@Slf4j
public class YwHdJbController extends ReportBaseController {

    @Autowired
    private IYwHdReport gl_rep_ywhdserv;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;


    @PostMapping("/queryAction")
    public ReturnData<Grid> queryAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();

        QueryParamVO queryParamvo = getQueryParamVO(queryvo,corpVO);
        try {
            String begindate = DateUtils.getPeriod(queryParamvo.getBegindate1());
            String ishajz = "N";

            if (queryParamvo.getIshasjz() != null && queryParamvo.getIshasjz().booleanValue()) {
                ishajz = "Y";
            }

            Integer month = queryParamvo.getBegindate1().getMonth();

            String startmonth = String.format("%02d", month - 2);

            queryParamvo.setQjq(begindate.substring(0, 4) + "-" + startmonth);
            queryParamvo.setQjz(begindate);
            queryParamvo.setIshasjz(new DZFBoolean(ishajz));
            queryParamvo.setIshassh(DZFBoolean.TRUE);
            queryParamvo.setXswyewfs(DZFBoolean.FALSE);

            // 开始日期应该在建账日期前
            checkPowerDate(queryParamvo,corpVO);

            YwHdVO[] ywvos = gl_rep_ywhdserv.queryYwHdValues(queryParamvo);

            if (ywvos == null || ywvos.length == 0) {
                grid.setMsg("当前数据为空!");
            } else {
                grid.setMsg("查询成功");
            }
            grid.setTotal((long) (ywvos == null ? 0 : ywvos.length));
            grid.setRows(ywvos == null ? new ArrayList<YwHdVO>() : Arrays.asList(ywvos));
            grid.setSuccess(true);

        } catch (Exception e) {
            grid.setRows(new ArrayList<YwHdVO>());
            printErrorLog(grid, e, "查询失败！");
        }
        // 日志记录接口
//        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT.getValue(),
//                "业务活动表查询:" + queryParamvo.getBegindate1().toString().substring(0, 7), ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }


    // 导出Excel
    @PostMapping("export/excel")
    public void excelReport(ReportExcelExportVO excelExportVO, KmReoprtQueryParamVO queryParamvo, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {

        YwHdVO[] listVo = JsonUtils.deserialize(excelExportVO.getList(),YwHdVO[].class);
        String gs = excelExportVO.getCorpName();
        String qj = excelExportVO.getTitleperiod();

        Excelexport2003<YwHdVO> lxs = new Excelexport2003<YwHdVO>();
        YwHdQuarterlyExcelField yhd = new YwHdQuarterlyExcelField();
        yhd.setYwhdvos(listVo);
        yhd.setQj(qj);
        yhd.setCreator(userVO.getCuserid());
        yhd.setCorpName(gs);

        baseExcelExport(response,lxs, yhd);
    }
    @PostMapping("print/pdf")
    public void printAction(String corpName, String period, PrintParamVO printParamVO, QueryParamVO queryparamvo, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response){
        try {
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            String strlist = printParamVO.getList();
            if (StringUtil.isEmpty(strlist)) {
                return;
            }

            YwHdVO[] bodyvos = JsonUtils.deserialize(strlist, YwHdVO[].class);

            List<ColumnCellAttr> columncellattrlist = new ArrayList<ColumnCellAttr>();

            columncellattrlist.add(new ColumnCellAttr("项目",null,null,2,"xm",6));
            columncellattrlist.add(new ColumnCellAttr("行次",null,null,2,"hs",1));
            columncellattrlist.add(new ColumnCellAttr("第三季度",null,3,null,null,0));
            columncellattrlist.add(new ColumnCellAttr("本年累计数",null,3,null,null,0));
            columncellattrlist.add(new ColumnCellAttr(" 非限定性",null,null,null,"monfxdx",1));
            columncellattrlist.add(new ColumnCellAttr(" 限定性",null,null,null,"monxdx",1));
            columncellattrlist.add(new ColumnCellAttr(" 合计",null,null,null,"monhj",1));
            columncellattrlist.add(new ColumnCellAttr(" 非限定性",null,null,null,"yearfxdx",1));
            columncellattrlist.add(new ColumnCellAttr(" 限定性",null,null,null,"yearxdx",1));
            columncellattrlist.add(new ColumnCellAttr(" 合计",null,null,null,"yearhj",1));

            // 初始化表头
            Map<String, String> tmap = new LinkedHashMap<String, String>();// 声明一个map用来存前台传来的设置参数
            tmap.put("公司", printParamVO.getCorpName());
            tmap.put("期间", printParamVO.getTitleperiod());
            tmap.put("单位", "元");
            if (pmap.get("pageOrt").equals("Y")) {
                printReporUtil.setIscross(DZFBoolean.TRUE);// 是否横向
                printReporUtil.setLineheight(18f);// 设置行高
            } else {
                printReporUtil.setIscross(DZFBoolean.FALSE);// 是否横向
            }

            if (pmap.get("type").equals("2")) {// B5显示12f
                printReporUtil.setLineheight(12f);// 设置行高
            }
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体
            // 初始化表体列编码和列名称
            printReporUtil.printReport(bodyvos, "业 务 活 动 季 报",columncellattrlist, 18, pmap.get("type"), pmap, tmap);

        } catch (DocumentException e) {
            log.error("打印错误", e);
        } catch (IOException e) {
            log.error("打印错误", e);
        }
    }
}
