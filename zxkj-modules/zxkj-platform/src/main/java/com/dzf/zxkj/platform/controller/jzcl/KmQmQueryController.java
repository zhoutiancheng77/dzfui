package com.dzf.zxkj.platform.controller.jzcl;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.jzcl.KMQMJZVO;
import com.dzf.zxkj.platform.service.jzcl.IKmQmQueryService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("gl_kmjzqueryact")
@Slf4j
public class KmQmQueryController extends BaseController {

    @Autowired
    private IKmQmQueryService gl_kmqmqueryserv;

    @PostMapping("query")
    public ReturnData<Grid> queryAction(@MultiRequestBody QueryParamVO queryParamvo) {
        Grid grid = new Grid();
        try {
            queryParamvo.setUserid(SystemUtil.getLoginUserId());
            queryParamvo.setPk_corp(SystemUtil.getLoginCorpId());
            List<KMQMJZVO> kmlistvo = gl_kmqmqueryserv.query(queryParamvo);
            KMQMJZVO[] kmqmjzvos = kmlistvo.toArray(new KMQMJZVO[0]);
            grid.setTotal((long) (kmqmjzvos == null ? 0 : kmqmjzvos.length));
            grid.setSuccess(true);
            grid.setRows(kmlistvo);
        } catch (Exception e) {
            grid.setTotal((long) 0);
            if (e instanceof BusinessException) {
                grid.setMsg(e.getMessage());
            } else {
                grid.setMsg("查询失败!");
                log.error("查询失败!", e);
            }

            grid.setSuccess(false);
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_SETTLE, "年末结账查询:"+queryParamvo.getEnddate().getYear(), ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }


}
