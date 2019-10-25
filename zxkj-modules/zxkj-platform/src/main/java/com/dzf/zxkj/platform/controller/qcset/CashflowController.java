package com.dzf.zxkj.platform.controller.qcset;

import com.dzf.zxkj.common.entity.ConditionVO;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.bdset.BdtradecashflowVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.qcset.IQcxjlyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("gl_invcashfolw")
@Slf4j
public class CashflowController {

    @Autowired
    private IQcxjlyService qcxjlyService;

    /**
     * 现金流项目参照
     */
    @PostMapping("query")
    public ReturnData<Grid> qryPrjInv(String fx, @MultiRequestBody CorpVO corpVO, String sort, String order) {
        Grid grid = new Grid();
        try {
            ConditionVO[] cds = new ConditionVO[2];
            cds[0] = new ConditionVO("pk_trade_accountschema", null, corpVO.getCorptype());
            if ("0".equals(fx)) {
                cds[1] = new ConditionVO("direction", null, 0);
            } else if ("1".equals(fx)) {
                cds[1] = new ConditionVO("direction", null, 1);
            }
            /*
             * if(data != null && data.getDirection() != null &&
             * data.getDirection() != -1){ cds[1]=new
             * ConditionVO("direction",null,data.getDirection()); }
             */
            List<BdtradecashflowVO> list = qcxjlyService.queryWithCondtion(BdtradecashflowVO.class, cds, sort,
                    order);
            log.info("查询成功！");
            // grid.setTotal(totalrow.longValue());
            grid.setRows(list == null ? new ArrayList<BdtradecashflowVO>() : list);
            grid.setSuccess(true);
            grid.setMsg("查询成功");
        } catch (Exception e) {
             grid.setSuccess(false);
             grid.setMsg("查询出错");
             log.error("查询出错");
        }
        return ReturnData.ok().data(grid);
    }

}
