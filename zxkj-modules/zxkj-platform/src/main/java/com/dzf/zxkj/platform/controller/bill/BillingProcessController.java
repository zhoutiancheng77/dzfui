package com.dzf.zxkj.platform.controller.bill;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bill.BillApplyDetailVo;
import com.dzf.zxkj.platform.model.bill.BillApplyVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.bill.IBillingProcessService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    public ReturnData<Grid> query(@RequestParam Map<String, String> param) {
        Grid grid = new Grid();
        try {
            BillApplyVO appvo = JsonUtils.convertValue(param, BillApplyVO.class);
            Integer page = StringUtil.isEmpty(param.get("page")) ? 1 : Integer.parseInt(param.get("page"));
            Integer rows = StringUtil.isEmpty(param.get("rows"))?100: java.lang.Integer.parseInt(param.get("rows"));

            checkSecurityData(null, new String[]{appvo.getPk_corp()}, null);
            List<BillApplyVO> list = gl_kpclserv.query(SystemUtil.getLoginCorpId(), appvo);
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
        } catch (Exception e) {
            log.error(e.getMessage());
            grid.setSuccess(false);
            grid.setRows(new ArrayList<BillApplyVO>());
            grid.setMsg("查询失败:"+e.getMessage());
        }

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

    // 开票
    @PostMapping("/dealkp")
    public ReturnData dealkp(@RequestBody BillApplyVO[] bills, @MultiRequestBody UserVO userVO) {
        Json json = new Json();
        try {
            checkSecurityData(bills, null, null);
            StringBuffer msg = new StringBuffer();
            boolean flag;
            int fail = 0;
            for(BillApplyVO vo : bills){
                flag = gl_kpclserv.createKp(vo, userVO, msg);
                if(!flag){
                    fail++;
                }
            }
            if(fail > 0){
                msg.append("成功" + (bills.length - fail) + ",失败" + fail);
            }else{
                msg.append("开票成功");
            }
            json.setSuccess(true);
            json.setMsg(msg.toString());
        }catch (Exception e) {
            log.error("错误", e.getMessage());
            json.setSuccess(false);
            json.setMsg("开票失败");
        }

        return ReturnData.ok().data(json);
    }

    // 开票
    @PostMapping("/dealHc")
    public ReturnData dealHc(@RequestBody BillApplyVO[] bills, @MultiRequestBody UserVO userVO) {
        Json json = new Json();
        try {
            checkSecurityData(bills, null, null);
            StringBuffer msg = new StringBuffer();
            boolean flag;
            int fail = 0;
            for(BillApplyVO vo : bills){
                flag = gl_kpclserv.createHc(vo, userVO, msg);
                if(!flag){
                    fail++;
                }
            }
            if(fail > 0){
                msg.append("成功" + (bills.length - fail) + ",失败" + fail);
            }else{
                msg.append("开票成功");
            }
            json.setSuccess(true);
            json.setMsg(msg.toString());
        }catch (Exception e) {
            log.error("错误", e.getMessage());
            json.setSuccess(false);
            json.setMsg("开票失败");
        }

        return ReturnData.ok().data(json);
    }

    // 删除
    @PostMapping("/delete")
    public ReturnData delete(@RequestBody BillApplyVO[] bills, @MultiRequestBody UserVO userVO) {
        Json json = new Json();
        try {
            checkSecurityData(bills, null, null);
            StringBuffer msg = new StringBuffer();
            boolean flag;
            int fail = 0;
            for(BillApplyVO vo : bills){
                flag = gl_kpclserv.delete(vo, userVO, msg);
                if(!flag){
                    fail++;
                }
            }
            if(fail > 0){
                msg.append("成功" + (bills.length - fail) + ",失败" + fail);
            }else{
                msg.append("删除成功");
            }
            json.setSuccess(true);
            json.setMsg(msg.toString());
        }catch (Exception e) {
            log.error("错误", e.getMessage());
            json.setSuccess(false);
            json.setMsg("删除失败");
        }

        return ReturnData.ok().data(json);
    }
}
