package com.dzf.zxkj.report.controller.cwbb;

import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.report.SrzcBVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.entity.ReportExcelExportVO;
import com.dzf.zxkj.report.excel.cwbb.SrzcExcelField;
import com.dzf.zxkj.report.service.cwbb.ISrzcReport;
import com.itextpdf.text.DocumentException;
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
@RequestMapping("gl_rep_srzcbact")
@Slf4j
public class SrzcController extends ReportBaseController {

    @Autowired
    private ISrzcReport gl_rep_srzcserv;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;


    @PostMapping("/queryAction")
    public ReturnData queryAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {

        Grid grid = new Grid();
        QueryParamVO queryParamvo = getQueryParamVO(queryvo, corpVO);
        try {
            // 校验
            checkSecurityData(null, new String[]{queryParamvo.getPk_corp()},null);
            String begindate = queryParamvo.getBegindate1().toString()
                    .substring(0, 7);
            String ishajz = "N";
            if (queryParamvo.getIshasjz() != null
                    && queryParamvo.getIshasjz().booleanValue()) {
                ishajz = "Y";
            }
            int year = new DZFDate(begindate + "-01").getYear();
            queryParamvo.setQjq(begindate);
            queryParamvo.setQjz(begindate);
            queryParamvo.setIshasjz(new DZFBoolean(ishajz));
            queryParamvo.setIshassh(DZFBoolean.TRUE);
            queryParamvo.setXswyewfs(DZFBoolean.FALSE);

            //开始日期应该在建账日期前
            checkPowerDate(queryParamvo, corpVO);

            SrzcBVO[] ywvos = gl_rep_srzcserv.queryVos(queryParamvo);

            log.info("查询成功！");
            grid.setTotal((long) (ywvos == null ? 0 : ywvos.length));
            grid.setRows(ywvos == null ? new ArrayList<SrzcBVO>() : Arrays.asList(ywvos));
            grid.setMsg("查询成功");
            grid.setSuccess(true);

        } catch (Exception e) {
            grid.setRows(new ArrayList<SrzcBVO>());
            printErrorLog(grid, e, "查询失败！");
        }

        //日志记录接口
        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT,
                "收入支出表查询:" + queryParamvo.getBegindate1().toString().substring(0, 7), ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);

    }

    //导出Excel
    @PostMapping("export/excel")
    public void excelReport(@MultiRequestBody ReportExcelExportVO excelExportVO, @MultiRequestBody KmReoprtQueryParamVO queryparamvo, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {
        // 校验
        checkSecurityData(null, new String[]{queryparamvo.getPk_corp()},null);
        SrzcBVO[] listVo = JsonUtils.deserialize(excelExportVO.getList(), SrzcBVO[].class);//
        String gs = excelExportVO.getCorpName();
        String qj = excelExportVO.getTitleperiod();

        Excelexport2003<SrzcBVO> lxs = new Excelexport2003<SrzcBVO>();
        SrzcExcelField field = new SrzcExcelField();
        field.setExpvos(listVo);
        field.setQj(qj);
        field.setCreator(userVO.getUser_name());
        field.setCorpName(gs);

        baseExcelExport(response, lxs, field);

    }

    /**
     * 打印操作
     */
    @PostMapping("print")
    public void printAction(@RequestParam Map<String, String> pmap1, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response) {
        try {

            PrintParamVO printParamVO = JsonUtils.deserialize(JsonUtils.serialize(pmap1), PrintParamVO.class);
            QueryParamVO queryparamvo = JsonUtils.deserialize(JsonUtils.serialize(pmap1), QueryParamVO.class);
            // 校验
            checkSecurityData(null, new String[]{queryparamvo.getPk_corp()},null);
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            String strlist = printParamVO.getList();
            if (strlist == null) {
                return;
            }
            SrzcBVO[] bodyvos = JsonUtils.deserialize(strlist, SrzcBVO[].class);
            Map<String, String> tmap = new LinkedHashMap<String, String>();//声明一个map用来存前台传来的设置参数
            tmap.put("公司", printParamVO.getCorpName());
            tmap.put("期间", printParamVO.getTitleperiod());
            printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), bodyvos, "收 入 支 出 表",
                    new String[]{"xm", "monnum", "yearnum"},
                    new String[]{"项目", "本月数", "本年累计数"},
                    new int[]{5, 2, 2}, 20, pmap, tmap);
        } catch (DocumentException e) {
            log.error("打印错误", e);
        } catch (IOException e) {
            log.error("打印错误", e);
        }
    }
}
