package com.dzf.zxkj.platform.controller.zcgl;

import com.dzf.zxkj.common.base.BaseController;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.sys.BdAssetCategoryVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zcgl.AssetQueryCdtionVO;
import com.dzf.zxkj.platform.model.zcgl.ZcdzVO;
import com.dzf.zxkj.platform.service.zcgl.IZczzdzReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/am/am_rep_zczzdzbact")
@Slf4j
public class AssetsGlComprReportController extends BaseController {

    @Autowired
    private IZczzdzReportService zczzdzReportService;

    @PostMapping("/save")
    public ReturnData<Grid> query(@MultiRequestBody AssetQueryCdtionVO qryVO, @MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();
        try {

            ZcdzVO[] zcmxvos = null;
            if (qryVO != null) {
                if (qryVO.getStart_date() == null) {
                    qryVO.setStart_date(new DZFDate());
                }
                String period = DateUtils.getPeriod(qryVO.getStart_date());
                zcmxvos = zczzdzReportService.queryAssetCheckVOs(corpVO.getPk_corp(), period);

            }
            if (zcmxvos != null && zcmxvos.length > 0) {
                grid.setTotal((long) zcmxvos.length);
                grid.setRows(Arrays.asList(zcmxvos));
            }
            grid.setSuccess(true);
//            writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL.getValue(),"资产总账对账表查询", ISysConstants.SYS_2);
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
        }

//        writeJson(grid);

        return ReturnData.ok().data(grid);
    }

}
