package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.query.AgeReportQueryVO;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.report.AgeReportResultVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.service.cwzb.IAgeBalanceReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("gl_rep_zlyebact")
@Slf4j
public class AgeBalanceController {

    @Autowired
    private IAgeBalanceReportService gl_rep_zlyeb;

    @PostMapping("/query")
    public ReturnData<Json> query(AgeReportQueryVO param, @MultiRequestBody CorpVO corpVO) {

        if (param.getFzlb() != null && param.getFzlb() > 0) {
            param.setAuaccount_type("fzhsx" + param.getFzlb());
        }
        if (corpVO.getBegindate().after(param.getEnd_date())) {
            return ReturnData.error().message("截止日期不能早于建账日期");
        }

        param.setJz_date(corpVO.getBegindate());

        Json json = new Json();
        try {
            AgeReportResultVO rs = gl_rep_zlyeb.query(param);
            json.setRows(rs);
            json.setSuccess(true);
            json.setMsg("查询成功");
        } catch (Exception e) {
            json.setSuccess(false);
            json.setMsg("查询失败");
            log.error(e.getMessage());
        }
        return ReturnData.ok().data(json);
    }
}
