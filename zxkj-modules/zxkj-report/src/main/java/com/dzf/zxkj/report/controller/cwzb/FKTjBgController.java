package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.common.base.BaseController;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.report.FkTjSetVo;
import com.dzf.zxkj.report.query.FktjQueryParam;
import com.dzf.zxkj.report.service.cwzb.IFkTjBgService;
import com.dzf.zxkj.report.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping("gl_rep_fktjbgact")
@Slf4j
public class FKTjBgController extends BaseController {
    @Autowired
    private IFkTjBgService gl_fktjbgserv;

    @PostMapping("query")
    public ReturnData<Grid> query(@MultiRequestBody FktjQueryParam queryParam) {
        Grid grid = new Grid();
        try {
            FkTjSetVo[] setvos = gl_fktjbgserv.query(SystemUtil.getLoginCorpId(),queryParam.getBegindate(),queryParam.getEnddate());
            grid.setMsg("查询成功");
            grid.setTotal((long)(setvos==null ? 0:setvos.length));
            grid.setSuccess(true);
            grid.setRows(setvos!=null? Arrays.asList(setvos):new ArrayList<FkTjSetVo>());
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(grid);
    }

}
