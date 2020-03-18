package com.dzf.zxkj.platform.controller.bill;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bill.InvoiceApplyVO;
import com.dzf.zxkj.platform.service.bill.IInvoiceApplyService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 开票申请
 */
@RestController
@RequestMapping("/bill/invoice_apply")
@Slf4j
public class InvoiceApplyController extends BaseController {

    @Autowired
    private IInvoiceApplyService invoiceApplyService;

    @GetMapping("/query")
    public ReturnData<Grid> query(int page, int rows, InvoiceApplyVO paramvo) {
        Grid grid = new Grid();
        try {
            List<InvoiceApplyVO> list = invoiceApplyService.query(SystemUtil.getLoginUserId(), page, rows, paramvo);
            grid.setSuccess(true);
            grid.setTotal(Long.valueOf(list.size()));
            grid.setRows(list);
            grid.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
            log.error("查询失败", e);
        }

        return ReturnData.ok().data(grid);
    }
    @PostMapping("/save")
    public ReturnData<Grid> save(@RequestBody Map<String, String> map) {
        Grid grid = new Grid();

        try {
            String corps = map.get("corps");
            if(StringUtil.isEmpty(corps)){
                throw new BusinessException("参数为空,请检查");
            }
            String[] gss = corps.split(",");
            String userid = SystemUtil.getLoginUserId();
            String pk_corp = SystemUtil.getLoginCorpId();

            checkSecurityData(null, gss, userid);

            List<InvoiceApplyVO> list = invoiceApplyService.save(userid, pk_corp, gss);
            grid.setSuccess(true);
            grid.setTotal(Long.valueOf(list.size()));
            grid.setRows(list);
            grid.setMsg("新增成功");
        } catch (Exception e) {
            printErrorLog(grid, e, "新增失败");
            log.error("新增失败", e);
        }

        return ReturnData.ok().data(grid);
    }

    @PostMapping("/apply")
    public ReturnData<Grid> apply(@RequestBody InvoiceApplyVO[] vos) {
        Grid grid = new Grid();

        try {
            if(vos == null || vos.length == 0){
                throw new BusinessException("参数为空,请检查");
            }
            String userid = SystemUtil.getLoginUserId();
            String pk_corp = SystemUtil.getLoginCorpId();

            checkSecurityData(vos, null, userid);

            invoiceApplyService.saveApply(userid, pk_corp, vos);
            grid.setSuccess(true);
            grid.setMsg("申请成功");
        } catch (Exception e) {
            printErrorLog(grid, e, "申请失败");
            log.error("申请失败", e);
        }

        return ReturnData.ok().data(grid);
    }

}
