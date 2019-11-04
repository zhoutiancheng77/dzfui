package com.dzf.zxkj.report.controller.cwbb;

import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.report.SrzcBVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.service.cwbb.ISrzcReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping("gl_rep_srzcbact")
@Slf4j
public class SrzcController extends ReportBaseController {

    @Autowired
    private ISrzcReport gl_rep_srzcserv;

    @PostMapping("/queryAction")
    public ReturnData queryAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {

        Grid grid = new Grid();
        QueryParamVO queryParamvo = getQueryParamVO(queryvo,corpVO);
        try {
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
            checkPowerDate(queryParamvo,corpVO);

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
//        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT.getValue(),
//                "收入支出表查询:" + queryParamvo.getBegindate1().toString().substring(0, 7), ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);

    }


}