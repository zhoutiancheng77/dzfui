package com.dzf.zxkj.platform.controller.sys;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.platform.model.sys.BDTradeVO;
import com.dzf.zxkj.platform.service.sys.IBDTradeService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sys/sys_hyact")
@Slf4j
public class BdTradeController {

    private IBDTradeService sys_hyserv;

    /**
     * 参照查询
     */
    @GetMapping("/queryRef")
    public ReturnData<Grid> queryRef(BDTradeVO paramvo) {
        Grid grid = new Grid();
        try {
            paramvo.setPk_corp(SystemUtil.getLoginCorpId());
            paramvo.setTradecode(paramvo.getTradename());
            List<BDTradeVO> list = sys_hyserv.queryRef(paramvo);
            if (list != null && list.size() > 0) {
                for (BDTradeVO bvo : list) {
                    if (bvo.getChildren() != null && bvo.getChildren().length > 0) {
                        bvo.setState("closed");
                    }
                }
                // grid.setRows(Arrays.asList(vos));
                grid.setRows(list);
                grid.setTotal((long) (list.size()));
            } else {
                grid.setRows(null);
                grid.setTotal(Long.valueOf(0));
            }
            grid.setSuccess(true);
            grid.setMsg("查询成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "查询失败");
        }

        return ReturnData.ok().data(grid);
    }
}
