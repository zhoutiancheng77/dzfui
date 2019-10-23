package com.dzf.zxkj.report.controller.cwbb;

import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.service.cwbb.ILrbReport;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("gl_rep_lrbact")
@Slf4j
public class LrbController extends ReportBaseController {

    @Autowired
    private ILrbReport gl_rep_lrbserv;

    @Reference
    private IZxkjPlatformService zxkjPlatformService;

    /**
     * 查询科目明细数据
     */
    @PostMapping("/queryAction")
    public ReturnData queryAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();
        QueryParamVO queryParamvo = getQueryParamVO(queryvo, corpVO);
        try {
            /** 来源利润表 */
            queryParamvo.setRptsource("lrb");
            queryParamvo.setQjz(queryParamvo.getQjq());
            queryParamvo.setBegindate1(DateUtils.getPeriodStartDate(queryParamvo.getQjq()));
            queryParamvo.setEnddate(queryParamvo.getBegindate1());
            LrbVO[] fsejyevos = null;
            /** 开始日期应该在建账日期前,检查权限 */
            checkPowerDate(queryParamvo, corpVO);
            fsejyevos = gl_rep_lrbserv.getLRBVOs(queryParamvo);

            grid.setTotal(fsejyevos == null ? 0 : (long) Arrays.asList(fsejyevos).size());
            grid.setRows(fsejyevos == null ? null : Arrays.asList(fsejyevos));
            grid.setSuccess(true);
            grid.setMsg("查询成功");

        } catch (Exception e) {
            grid.setRows(new ArrayList<LrbVO>());
            printErrorLog(grid, e, "查询失败！");
        }

        /** 日志记录接口 */
//        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT.getValue(),
//                "利润表查询:"+DateUtils.getPeriod(queryParamvo.getBegindate1()),ISysConstants.SYS_2);

        return ReturnData.ok().data(grid);
    }

    @PostMapping("/queryCenterAction")
    public ReturnData<Grid> queryCenterAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();
        QueryParamVO queryParamvo = getQueryParamVO(queryvo, corpVO);
        try {
            queryParamvo.setRptsource("lrb");
            queryParamvo.setQjq(DateUtils.getPeriod(queryParamvo.getBegindate1()));
            queryParamvo.setQjz(DateUtils.getPeriod(queryParamvo.getBegindate1()));
            queryParamvo.setEnddate(queryParamvo.getBegindate1());
            LrbVO[] fsejyevos = null;

            /** 开始日期应该在建账日期前,检查权限 */
            checkPowerDate(queryParamvo, corpVO);
            fsejyevos = gl_rep_lrbserv.getLRBVOs(queryParamvo);

            grid.setTotal(fsejyevos == null ? 0 : (long) Arrays.asList(fsejyevos).size());
            grid.setRows(fsejyevos == null ? null : Arrays.asList(fsejyevos));
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setRows(new ArrayList<LrbVO>());
            printErrorLog(grid, e, "查询失败！");
        }

        /** 日志记录接口 */
//        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT.getValue(),
//                "分部利润表查询",ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);

    }


    /**
     * 查询一年的数据
     */
    @PostMapping("/queryYearAction")
    public ReturnData<Grid> queryYearAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();
        try {
            QueryParamVO queryParamvo = getQueryParamVO(queryvo, corpVO);
            int year = queryParamvo.getBegindate1().getYear();
            Map<String, List<LrbVO>> maplist = gl_rep_lrbserv.getYearLrbMap(String.valueOf(year), queryParamvo.getPk_corp(), "", null, queryParamvo.getIshasjz());
            /** map转换成list */
            List<LrbVO> reslrb = new ArrayList<LrbVO>();
            for (Map.Entry<String, List<LrbVO>> entry : maplist.entrySet()) {
                for (LrbVO lrbvo : entry.getValue()) {
                    if (lrbvo.getXm() != null &&
                            ("一、营业收入".equals(lrbvo.getXm())
                                    || "三、利润总额（亏损总额以“-”填列）".equals(lrbvo.getXm())
                                    || "三、利润总额（亏损总额以“-”号填列）".equals(lrbvo.getXm())
                                    || "减：所得税费用".equals(lrbvo.getXm()))
                            || "减：所得税".equals(lrbvo.getXm())
                    ) {
                        reslrb.add(lrbvo);
                    }
                }
            }
            grid.setRows(reslrb);
            grid.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败！");
        }
        return ReturnData.ok().data(grid);
    }


    @Override
    public String getPrintTitleName() {
        return "利 润 表";
    }


    @PostMapping("/queryQuarterlySdsShui")
    public ReturnData<Json> queryQuarterlySdsShui(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        Json grid = new Json();
        try {
            QueryParamVO queryParamvo = getQueryParamVO(queryvo, corpVO);
            /** 开始日期应该在建账日期前,检查权限 */
            checkPowerDate(queryParamvo, corpVO);
            DZFDouble sds = zxkjPlatformService.getQuarterlySdsShui(queryParamvo.getPk_corp(),
                    DateUtils.getPeriod(queryParamvo.getBegindate1()));
            grid.setData(sds);
            grid.setSuccess(true);
            grid.setMsg("查询所得税成功！");
        } catch (Exception e) {
            grid.setRows(new ArrayList<LrbVO>());
            printErrorLog(grid, e, "查询所得税失败！");
        }

        /** 日志记录接口 */
//        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT.getValue(), "所得税查询", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);

    }

}
