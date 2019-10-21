package com.dzf.zxkj.report.controller.cwbb;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.report.YyFpVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwbb.IYyFpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("gl_rep_yyfpact")
public class YyfpController {

    @Autowired
    private IYyFpService yyfpser;

    @Reference(version = "2.0.0")
    private IZxkjPlatformService zxkjPlatformService;

    @RequestMapping("query")
    public ReturnData query(QueryParamVO queryParamVO, @MultiRequestBody CorpVO corpVO){

        Grid grid = new Grid();
        try {

            if (queryParamVO.getPk_corp() == null || queryParamVO.getPk_corp().trim().length() == 0) {
                // 如果编制单位为空则取当前默认公司
                queryParamVO.setPk_corp(corpVO.getPk_corp());
            }

            List<YyFpVO> list = yyfpser.queryList(queryParamVO);
            grid.setSuccess(true);
            grid.setRows(list);
            grid.setMsg("查询成功");
        } catch (DZFWarpException e) {
            log.error("操作失败", e);
            grid.setMsg("操作失败");
        }
        return ReturnData.ok().data(grid);
    }

}
