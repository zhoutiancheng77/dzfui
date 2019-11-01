package com.dzf.zxkj.report.controller.cwbb;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.report.CwgyInfoVO;
import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.report.service.cwbb.ICwgyInfoReport;
import com.dzf.zxkj.report.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping("gl_rep_cwgyinfoact")
@Slf4j
public class CwgyInfoController {

    @Autowired
    private ICwgyInfoReport gl_rep_cwgyinfoserv;

//    @Autowired
//    private IUserService iuserService;

    /**
     * 查询
     */
    @GetMapping("/queryAction")
    public ReturnData<Grid> queryAction(@MultiRequestBody QueryParamVO queryParamVO) {
        Grid grid = new Grid();
        QueryParamVO queryParamvo = getQueryParamVO(queryParamVO);
        try {
            queryParamvo.setQjq(queryParamvo.getBegindate1().toString().substring(0, 7));
            queryParamvo.setQjz(queryParamvo.getBegindate1().toString().substring(0, 7));
            queryParamvo.setEnddate(queryParamvo.getBegindate1());
            CwgyInfoVO[] fsejyevos = null;
//            Set<String> nnmnc = iuserService.querypowercorpSet(getLoginUserid());
//            String corp = queryParamvo.getPk_corp();
//            if (nnmnc == null || !nnmnc.contains(corp)) {
//                throw new BusinessException("不包含该公司。");
//            }

            int curyear = new DZFDate().getYear();
            int conyear = queryParamvo.getBegindate1().getYear();
            if (conyear > curyear) {
                throw new BusinessException("超出当前年份,请重新选择!");
            }
            fsejyevos = gl_rep_cwgyinfoserv.getCwgyInfoVOs(queryParamvo);

            grid.setTotal(fsejyevos == null ? 0 : (long) Arrays.asList(fsejyevos).size());
            grid.setRows(fsejyevos == null ? null : Arrays.asList(fsejyevos));
            grid.setSuccess(true);

        } catch (Exception e) {
            grid.setRows(new ArrayList<LrbVO>());
//            printErrorLog(grid, log, e, "查询失败！");
        }

        // 日志记录接口
//        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT.getValue(), "财务概要信息查询:" + queryParamvo.getBegindate1().toString(),
//                2);

        return ReturnData.ok().data(grid);
    }

    public QueryParamVO getQueryParamVO(QueryParamVO paramvo) {
        if (paramvo.getPk_corp() == null || paramvo.getPk_corp().trim().length() == 0) {
            // 如果编制单位为空则取当前默认公司
            String corpVo = SystemUtil.getLoginCorpId();
            paramvo.setPk_corp(corpVo);
        }
        return paramvo;
    }

}
