package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.query.AgeReportQueryVO;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.report.AgeReportResultVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwzb.IAgeDetailReportService;
import com.dzf.zxkj.report.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("gl_rep_zlmxbact")
@Slf4j
public class AgeDetailController {

    @Autowired
    private IAgeDetailReportService gl_rep_zlmxb;

    @Reference
    private IZxkjPlatformService zxkjPlatformService;

    @PostMapping("query")
    public ReturnData<Grid> query (AgeReportQueryVO ageReportQueryVO) {
        Grid json = new Grid();
        try {

            if(StringUtil.isEmptyWithTrim(ageReportQueryVO.getPk_corp())){
                ageReportQueryVO.setPk_corp(SystemUtil.getLoginCorpId());
            }

            CorpVO corpVO = zxkjPlatformService.queryCorpByPk(ageReportQueryVO.getPk_corp());

            if (corpVO.getBegindate().after(ageReportQueryVO.getEnd_date())) {
                throw new BusinessException("截止日期不能早于建账日期");
            }
            ageReportQueryVO.setJz_date(corpVO.getBegindate());
            AgeReportResultVO rs = gl_rep_zlmxb.query(ageReportQueryVO);
            json.setRows(rs);
            json.setSuccess(true);
            json.setMsg("查询成功");
        } catch (Exception e) {
            json.setSuccess(false);
            json.setMsg("查询失败");
        }
        return ReturnData.ok().data(json);
    }
}
