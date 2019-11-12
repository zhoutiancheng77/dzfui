package com.dzf.zxkj.report.controller.cwbb;

import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.report.LrbquarterlyVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.entity.ReportExcelExportVO;
import com.dzf.zxkj.report.excel.cwbb.LrbQuarterlyExcelField;
import com.dzf.zxkj.report.service.cwbb.ILrbQuarterlyReport;
import com.dzf.zxkj.report.service.cwbb.IZcFzBReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("gl_rep_lrbquarteract")
@Slf4j
public class LrbQuarterlyController extends ReportBaseController {
    @Autowired
    private ILrbQuarterlyReport gl_rep_lrbquarterlyserv;


    /**
     * 查询科目明细数据
     */
    @PostMapping("/queryAction")
    public ReturnData<Grid> queryAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();
        QueryParamVO queryParamvo = getQueryParamVO(queryvo, corpVO);
        try {
            queryParamvo.setRptsource("lrb");
            queryParamvo.setQjq(queryParamvo.getBegindate1().toString().substring(0, 7));
            queryParamvo.setQjz(queryParamvo.getBegindate1().toString().substring(0, 7));
            queryParamvo.setEnddate(queryParamvo.getBegindate1());
            LrbquarterlyVO[] fsejyevos = null;
            fsejyevos = gl_rep_lrbquarterlyserv.getLRBquarterlyVOs(queryParamvo);
            grid.setTotal(fsejyevos == null ? 0 : (long) Arrays.asList(fsejyevos).size());
            grid.setRows(fsejyevos == null ? null : Arrays.asList(fsejyevos));
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setRows(new ArrayList<LrbVO>());
            printErrorLog(grid, e, "查询失败！");
        }

        //日志记录接口
//        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT.getValue(),
//                "利润表季报查询:" + queryParamvo.getBegindate1().toString(), 2);

        return ReturnData.ok().data(grid);
    }


    @Override
    public String getPrintTitleName() {
        return "利 润 表 季 报";
    }


    private LrbVO[] convertLrb(LrbquarterlyVO[] listVo, String qj) {
        if (listVo == null || listVo.length == 0) {
            throw new BusinessException("数据为空");
        }

        String month = qj.substring(5, 7);

        List<LrbVO> reslist = new ArrayList<LrbVO>();

        LrbVO tlrbvo = null;
        for (LrbquarterlyVO quaryvo : listVo) {
            tlrbvo = new LrbVO();

            tlrbvo.setXm(quaryvo.getXm());

            tlrbvo.setBnljje(quaryvo.getBnljje());

            if ("03".equals(month)) {
                tlrbvo.setByje(quaryvo.getQuarterFirst());
            } else if ("06".equals(month)) {
                tlrbvo.setByje(quaryvo.getQuarterSecond());
            } else if ("09".equals(month)) {
                tlrbvo.setByje(quaryvo.getQuarterThird());
            } else {
                tlrbvo.setByje(quaryvo.getQuarterFourth());
            }
            reslist.add(tlrbvo);
        }

        return reslist.toArray(new LrbVO[0]);
    }

    private ZcFzBVO[] getZcfzbData(String qj, String corpIds) {
        IZcFzBReport gl_rep_zcfzserv = (IZcFzBReport) SpringUtils.getBean("gl_rep_zcfzserv");

        ZcFzBVO[] zcfzbvos = gl_rep_zcfzserv.getZCFZBVOs(qj.substring(0, 7), corpIds, "N",
                new String[]{"N", "N", "N", "N"});

        return zcfzbvos;
    }


    //导出Excel
    @PostMapping("export/excel")
    public void excelReport(ReportExcelExportVO excelExportVO, KmReoprtQueryParamVO queryparamvo, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response){

        LrbquarterlyVO[] listVo = JsonUtils.deserialize(excelExportVO.getList(), LrbquarterlyVO[].class);
        String gs=  excelExportVO.getCorpName();
        String qj=  excelExportVO.getTitleperiod();
        Excelexport2003<LrbquarterlyVO> lxs = new Excelexport2003<LrbquarterlyVO>();
        LrbQuarterlyExcelField lrb = new LrbQuarterlyExcelField(excelExportVO.getCurrjd());
        lrb.setLrbvos(listVo);
        lrb.setQj(qj);
        lrb.setCreator(userVO.getUser_name());
        lrb.setCorpName(gs);

        baseExcelExport(response,lxs,lrb);

        //日志记录接口
//        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT.getValue(),
//                "利润表季报导出:"+qj,2);
    }



}
