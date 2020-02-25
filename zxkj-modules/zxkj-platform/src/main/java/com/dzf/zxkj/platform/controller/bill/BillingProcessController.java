package com.dzf.zxkj.platform.controller.bill;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.platform.model.bill.BillApplyDetailVo;
import com.dzf.zxkj.platform.model.bill.BillApplyVO;
import com.dzf.zxkj.platform.service.bill.IBillingProcessService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 开票处理
 */
@RestController
@RequestMapping("/bill/billing_process")
@Slf4j
public class BillingProcessController extends BaseController {
    @Autowired
    private IBillingProcessService gl_kpclserv;

    @GetMapping("/query")
    public ReturnData<Grid> query(String customer, String billStatus, Integer page, Integer rows) {
        Grid grid = new Grid();
        Integer status = StringUtils.isEmpty(billStatus) ? null : Integer.valueOf(billStatus);
        List<BillApplyVO> list = gl_kpclserv.query(SystemUtil.getLoginCorpId(), customer, status);
        // list变成数组
        grid.setTotal((long) (list == null ? 0 : list.size()));
        // 分页
        BillApplyVO[] vos = null;
        if (list != null && list.size() > 0) {
            vos = getPagedZZVOs(list.toArray(new BillApplyVO[0]), page, rows);
        }
        grid.setRows(vos == null ? new ArrayList<BillApplyVO>() : Arrays
                .asList(vos));
        grid.setSuccess(true);
        grid.setMsg("查询成功");
        return ReturnData.ok().data(grid);
    }

    @GetMapping("/queryB")
    public ReturnData queryB(@RequestParam String pk_apply) {
        Grid grid = new Grid();
        List<BillApplyDetailVo> detailvos = gl_kpclserv.queryB(pk_apply, SystemUtil.getLoginCorpId());
        grid.setRows(detailvos);
        grid.setSuccess(true);
        grid.setMsg("查询成功");
        return ReturnData.ok().data(grid);
    }


    // 开票
    @PostMapping("/billing")
    public ReturnData billing(@RequestBody BillApplyVO[] bills) {
        Json json = new Json();
        String msg = gl_kpclserv.billing(bills, SystemUtil.getLoginUserId());
        json.setSuccess(true);
        json.setMsg(msg);
        return ReturnData.ok().data(json);
    }

    // 寄出
    @PostMapping("/sentOut")
    public ReturnData sentOut(@RequestBody BillApplyVO[] bills) {
        Json json = new Json();
        String msg = gl_kpclserv.sentOut(bills, SystemUtil.getLoginUserId());
        json.setSuccess(true);
        json.setMsg(msg);
        return ReturnData.ok().data(json);
    }

    // 入账
    @PostMapping("/accounting")
    public ReturnData accounting(@RequestBody BillApplyVO[] bills) {
        Json json = new Json();
        String msg = gl_kpclserv.accounting(bills, SystemUtil.getLoginUserId());
        json.setSuccess(true);
        json.setMsg(msg);
        return ReturnData.ok().data(json);
    }

    // 报税
    @PostMapping("/tax")
    public ReturnData tax(@RequestBody BillApplyVO[] bills) {
        Json json = new Json();
        String msg = gl_kpclserv.tax(bills, SystemUtil.getLoginUserId());
        json.setSuccess(true);
        json.setMsg(msg);
        return ReturnData.ok().data(json);
    }

    private BillApplyVO[] getPagedZZVOs(BillApplyVO[] vos, Integer page, Integer rows) {
        if (page == null) {
            page = 1;
        }
        if (rows == null) {
            rows = 10;
        }
        int beginIndex = rows * (page - 1);
        int endIndex = rows * page;
        if (endIndex >= vos.length) {// 防止endIndex数组越界
            endIndex = vos.length;
        }
        vos = Arrays.copyOfRange(vos, beginIndex, endIndex);
        return vos;
    }
}
