package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.yscs.DzfpscReqBVO;
import com.dzf.zxkj.report.service.cwzb.IZzsmxService;
import com.dzf.zxkj.report.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("gl_rep_zzsmxact")
@Slf4j
public class ZzsMxController {
    @Autowired
    private IZzsmxService zzsmxService;

    /**
     * 查询科目明细数据
     */
    @PostMapping("query")
    public ReturnData<Grid> queryAction(QueryParamVO queryParamVO) {
        Grid grid = new Grid();
        if (StringUtil.isEmptyWithTrim(queryParamVO.getPk_corp())) {
            queryParamVO.setPk_corp(SystemUtil.getLoginCorpId());
        }
        try {
            long total = zzsmxService.getZzsmxCount(queryParamVO);
            grid.setTotal(total);
            List<DzfpscReqBVO> listvos = new ArrayList<DzfpscReqBVO>();
            if (total > 0) {
                listvos = zzsmxService.getZzsmx(queryParamVO);
            }
            grid.setRows(listvos);
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setSuccess(false);
            grid.setMsg("查询失败！");
            log.error("查询失败！", e);
        }
        return ReturnData.ok().data(grid);
    }


    /**
     * 生成总账凭证
     */
    @PostMapping("generateZzpz")
    public ReturnData<Grid> generateZzpz(QueryParamVO queryParamVO, String busdatas, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO) {

        Grid grid = new Grid();
        try {
            if (StringUtil.isEmptyWithTrim(queryParamVO.getPk_corp())) {
                queryParamVO.setPk_corp(SystemUtil.getLoginCorpId());
            }

            if (StringUtil.isEmpty(busdatas)) {
                throw new BusinessException("明细数据为空！");
            }

            DzfpscReqBVO[] bvos = JsonUtils.deserialize(busdatas, DzfpscReqBVO[].class);
            List<DzfpscReqBVO> listvos = zzsmxService.saveAsVoucher(corpVO, userVO, queryParamVO, Arrays.asList(bvos));

            long total = queryParamVO.getRows();
            grid.setTotal(total);

            grid.setRows(listvos);
            grid.setSuccess(true);
        } catch (Exception e) {
            if ("-150".equals(e.getMessage())) {
                grid.setMsg("-150");
            }
            grid.setSuccess(false);
            grid.setMsg("生成总账凭证失败！");
            log.error("生成总账凭证失败！", e);
        }
        return ReturnData.ok().data(grid);
    }
}
