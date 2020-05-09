package com.dzf.zxkj.platform.controller.bill;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.IInvoiceApplyConstant;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bill.BillApplyDetailVo;
import com.dzf.zxkj.platform.model.bill.BillApplyVO;
import com.dzf.zxkj.platform.model.bill.InvoiceApplyVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.bill.IBillingProcessService;
import com.dzf.zxkj.platform.service.bill.IInvoiceApplyService;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.dzf.zxkj.platform.util.zncs.ICaiFangTongConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
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
    @Autowired
    private IInvoiceApplyService invoiceserv;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @GetMapping("/query")
    public ReturnData<Grid> query(@RequestParam Map<String, String> param) {
        Grid grid = new Grid();
        try {
            BillApplyVO appvo = JsonUtils.convertValue(param, BillApplyVO.class);
            Integer page = StringUtil.isEmpty(param.get("page")) ? 1 : Integer.parseInt(param.get("page"));
            Integer rows = StringUtil.isEmpty(param.get("rows")) ? 100: java.lang.Integer.parseInt(param.get("rows"));

            checkSecurityData(null, new String[]{appvo.getPk_corp()}, null);

            String userid = SystemUtil.getLoginUserId();
            String pk_corp = SystemUtil.getLoginCorpId();

            List<BillApplyVO> list = gl_kpclserv.query(userid, pk_corp, appvo);
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
            printErrorLog(grid, e, "查询失败");
        }

        return ReturnData.ok().data(grid);
    }

    @GetMapping("/queryB")
    public ReturnData queryB(@RequestParam String pk_apply) {
        Grid grid = new Grid();
        try {
            List<BillApplyDetailVo> detailvos = gl_kpclserv.queryB(pk_apply, SystemUtil.getLoginCorpId());
            grid.setRows(detailvos);
            grid.setSuccess(true);
            grid.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
        }
        return ReturnData.ok().data(grid);
    }

    @PostMapping("/impExcel")
    public ReturnData<Json> impExcel(HttpServletRequest request){
        String userid = SystemUtil.getLoginUserId();
        Json json = new Json();
        json.setSuccess(false);
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile file = multipartRequest.getFile("impfile");
            if(file == null){
                throw new BusinessException("请选择导入文件!");
            }
            String fileName = file.getOriginalFilename();
            String fileType = null;
            if (!StringUtil.isEmpty(fileName)) {
                fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
            }
            String pk_corp = multipartRequest.getParameter("corpid");
            if(StringUtil.isEmpty(pk_corp)){
                throw new BusinessException("公司为空,请检查");
            }
            //
            BillApplyVO paramvo = new BillApplyVO();
            StringBuffer msg = new StringBuffer();
            gl_kpclserv.saveImp(file.getInputStream(), paramvo, pk_corp, fileType, userid, msg);
            json.setHead(paramvo);
            json.setMsg("导入成功");
            json.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException ? e.getMessage() : "导入失败!");
        }
        return ReturnData.ok().data(json);
    }

    private CorpVO getCorpVO(String pk_corp){
        CorpVO corp = zxkjPlatformService.queryCorpByPk(pk_corp);

        return corp;
    }

    // 开票
    @PostMapping("/dealkp")
    public ReturnData dealkp(@RequestBody BillApplyVO[] bills, @MultiRequestBody UserVO userVO) {
        Json json = new Json();
        try {
//            BillApplyVO[] bills = getBillsFromRequest();
            StringBuffer msg = new StringBuffer();
//            UserVO userVO = getLoginUserInfo();
            boolean flag = true;
            int errorCount = 0;
            boolean  iscontinue = true;
            InvoiceApplyVO invoicevo = null;
            CorpVO corpvo = getCorpVO(bills[0].getPk_corp());
            for(BillApplyVO vo : bills){
                try {
                    //首先判断是不是来源于手机端
                    Integer source = vo.getSourcetype();
                    if(source == null || source == IInvoiceApplyConstant.KP_SOURCE_0){
                        try {
                            flag = gl_kpclserv.createBilling(vo, userVO);
                            msg.append(String.format("<font color='#2ab30f'><p>企业主提交的开票申请(申请时间)%s开票成功</p></font>",
                                    vo.getDapplydate()));
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            errorCount++;
                            if(e instanceof BusinessException){
                                msg.append(String.format("<font color='red'><p>企业主提交的开票申请(申请时间)%s开票失败,失败原因:%s</p></font>",
                                        vo.getDapplydate(), e.getMessage()));
                            }else {
                                msg.append(String.format("<font color='red'><p>企业主提交的开票申请(申请时间)%s开票失败</p></font>",
                                        vo.getDapplydate()));
                            }
                        }

                    }else if(iscontinue){

                        if(invoicevo == null){
                            invoicevo = invoiceserv.queryByGs(bills[0].getPk_corp());
                        }
                        if(invoicevo == null || invoicevo.getIstatus() != IInvoiceApplyConstant.APPLY_STATUS_5){
                            errorCount++;
                            iscontinue = false;
                            msg.append(String.format("<font color='red'><p>%s未开通开票功能，请开通后再提交开票</p></font>",
                                    corpvo.getUnitname()));
                            continue;
                        }

                        //判断走蓝票逻辑还是红票逻辑
                        Integer type = vo.getFptype();
                        if(type == Integer.parseInt(ICaiFangTongConstant.FPLX_1)){
                            flag = gl_kpclserv.createKp(vo, userVO);
                        }else if(type == Integer.parseInt(ICaiFangTongConstant.FPLX_2)){
                            flag = gl_kpclserv.createHc(vo, userVO);
                        }else{
                            throw new BusinessException("发票类型不合法,请检查");
                        }

//                		A公司（申请时间）2020-4-22 16:29:00 提交开票失败，失败原因:XXX
                        if(flag){
                            msg.append(String.format("<font color='#2ab30f'><p>%s(申请时间)%s提交开票成功</p></font>",
                                    corpvo.getUnitname(), vo.getDapplydate()));
                        }else{
                            msg.append(String.format("<font color='red'><p>%s(申请时间)%s提交开票失败</p></font>",
                                    corpvo.getUnitname(), vo.getDapplydate()));
                        }
                    }


                }catch(Exception e){
                    log.error(e.getMessage(), e);
                    errorCount++;
                    if(e instanceof BusinessException){
                        msg.append(String.format("<font color='red'><p>%s(申请时间)%s提交开票失败,失败原因:%s</p></font>",
                                corpvo.getUnitname(), vo.getDapplydate(), e.getMessage()));
                    }else {
                        msg.append(String.format("<font color='red'><p>%s(申请时间)%s提交开票失败</p></font>",
                                corpvo.getUnitname(), vo.getDapplydate()));
                    }

                }

            }
            json.setSuccess(errorCount > 0 ? false : true);
            json.setMsg(msg.toString());
        } catch (Exception e) {
            printErrorLog(json, e, "开票失败");
        }

        return ReturnData.ok().data(json);
    }

    @PostMapping("/saveR")
    public ReturnData saveR(@MultiRequestBody String bills, @MultiRequestBody String chreson, @MultiRequestBody UserVO userVO, HttpServletRequest request){
        Json json = new Json();
        try {
            BillApplyVO[] billList = JsonUtils.deserialize(bills, BillApplyVO[].class);
//            String reason = request.getParameter("chreson");
            String reason = chreson;
            if(StringUtil.isEmpty(reason)){
                throw new BusinessException("请录入冲红原因");
            }

            CorpVO corpvo = getCorpVO(billList[0].getPk_corp());
            StringBuffer msg = new StringBuffer();
//            UserVO userVO = getLoginUserInfo();
            int errorCount = 0;
            for(BillApplyVO vo : billList){
                try {
                    vo.setRedreason(reason);
                    vo = gl_kpclserv.saveHcBill(vo, userVO);

                    msg.append(String.format("<font color='#2ab30f'><p>%s(申请时间)%s保存冲红原因成功</p></font>",
                            corpvo.getUnitname(), vo.getDapplydate()));
                }catch(Exception e){
                    log.error(e.getMessage(), e);
                    errorCount++;
                    if(e instanceof BusinessException){
                        msg.append(String.format("<font color='red'><p>%s(申请时间)%s保存冲红原因失败,失败原因:%s</p></font>",
                                corpvo.getUnitname(), vo.getDapplydate(), e.getMessage()));
                    }else {
                        msg.append(String.format("<font color='red'><p>%s(申请时间)%s保存冲红原因失败</p></font>",
                                corpvo.getUnitname(), vo.getDapplydate()));
                    }

                }

            }
            json.setSuccess(errorCount > 0 ? false : true);
            json.setMsg(msg.toString());
        } catch (Exception e) {
            printErrorLog(json, e, "冲红失败");
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("/saveRAndK")
    public ReturnData saveRAndK(@MultiRequestBody String bills, @MultiRequestBody String chreson,
                                @MultiRequestBody UserVO userVO, HttpServletRequest request){
        Json json = new Json();
        try {
            BillApplyVO[] billList = JsonUtils.deserialize(bills, BillApplyVO[].class);
            String reason = chreson;
//            String reason = request.getParameter("chreson");
            if(StringUtil.isEmpty(reason)){
                throw new BusinessException("请录入冲红原因");
            }

            StringBuffer msg = new StringBuffer();
//            UserVO userVO = getLoginUserInfo();
            boolean flag = true;
            int errorCount = 0;

            InvoiceApplyVO invoicevo = invoiceserv.queryByGs(billList[0].getPk_corp());
            if(invoicevo == null || invoicevo.getIstatus() != IInvoiceApplyConstant.APPLY_STATUS_5){
                throw new BusinessException("当前公司未开通开票功能，请开通后再提交开票");
            }
            CorpVO corpvo = getCorpVO(billList[0].getPk_corp());
            for(BillApplyVO vo : billList){
                try {
                    vo.setRedreason(reason);
                    vo = gl_kpclserv.saveHcBill(vo, userVO);
                    flag = gl_kpclserv.createHc(vo, userVO);

                    if(flag){
                        msg.append(String.format("<font color='#2ab30f'><p>%s(申请时间)%s提交冲红成功</p></font>",
                                corpvo.getUnitname(), vo.getDapplydate()));
                    }else{
                        msg.append(String.format("<font color='red'><p>%s(申请时间)%s提交冲红失败</p></font>",
                                corpvo.getUnitname(), vo.getDapplydate()));
                    }
                }catch(Exception e){
                    log.error(e.getMessage(), e);
                    errorCount++;
                    if(e instanceof BusinessException){
                        msg.append(String.format("<font color='red'><p>%s(申请时间)%s提交冲红失败,失败原因:%s</p></font>",
                                corpvo.getUnitname(), vo.getDapplydate(), e.getMessage()));
                    }else {
                        msg.append(String.format("<font color='red'><p>%s(申请时间)%s提交冲红失败</p></font>",
                                corpvo.getUnitname(), vo.getDapplydate()));
                    }

                }

            }
            json.setSuccess(errorCount > 0 ? false : true);
            json.setMsg(msg.toString());
        } catch (Exception e) {
            printErrorLog(json, e, "冲红失败");
        }

        return ReturnData.ok().data(json);
    }

    // 开票
    @PostMapping("/dealHc")
    public ReturnData dealHc(@RequestBody BillApplyVO[] bills, @MultiRequestBody UserVO userVO) {
        Json json = new Json();
        try {
//            BillApplyVO[] bills = getBillsFromRequest();
            StringBuffer msg = new StringBuffer();
//            UserVO userVO = getLoginUserInfo();
            boolean flag = true;
            int errorCount = 0;
            CorpVO corpvo = getCorpVO(bills[0].getPk_corp());
            for(BillApplyVO vo : bills){
                try {
                    flag = gl_kpclserv.createHc(vo, userVO);
                    if(flag){
                        msg.append(String.format("<font color='#2ab30f'><p>%s(申请时间)%s提交冲红成功</p></font>",
                                corpvo.getUnitname(), vo.getDapplydate()));
                    }else{
                        msg.append(String.format("<font color='red'><p>%s(申请时间)%s提交冲红失败</p></font>",
                                corpvo.getUnitname(), vo.getDapplydate()));
                    }
                }catch(Exception e){
                    log.error(e.getMessage(), e);
                    errorCount++;
                    if(e instanceof BusinessException){
                        msg.append(String.format("<font color='red'><p>%s(申请时间)%s提交冲红失败,失败原因:%s</p></font>",
                                corpvo.getUnitname(), vo.getDapplydate(), e.getMessage()));
                    }else {
                        msg.append(String.format("<font color='red'><p>%s(申请时间)%s提交冲红失败</p></font>",
                                corpvo.getUnitname(), vo.getDapplydate()));
                    }

                }

            }
            json.setSuccess(errorCount > 0 ? false : true);
            json.setMsg(msg.toString());
        } catch (Exception e) {
            printErrorLog(json, e, "冲红失败");
        }

        return ReturnData.ok().data(json);
    }

    // 删除
    @PostMapping("/delete")
    public ReturnData delete(@RequestBody BillApplyVO[] bills, @MultiRequestBody UserVO userVO) {
        Json json = new Json();
        try {
//            BillApplyVO[] bills = getBillsFromRequest();
//            UserVO userVO = getLoginUserInfo();

            StringBuffer msg = new StringBuffer();
            int errorCount = 0;
            CorpVO corpvo = getCorpVO(bills[0].getPk_corp());

            for(BillApplyVO vo : bills){
                try {
                    gl_kpclserv.delete(vo, userVO);
                    msg.append(String.format("<font color='#2ab30f'><p>%s(申请时间)%s提交删除成功</p></font>",
                            corpvo.getUnitname(), vo.getDapplydate()));
                }catch(Exception e){
                    log.error(e.getMessage(), e);
                    errorCount++;
                    if(e instanceof BusinessException){
                        msg.append(String.format("<font color='red'><p>%s(申请时间)%s提交删除失败,失败原因:%s</p></font>",
                                corpvo.getUnitname(), vo.getDapplydate(), e.getMessage()));
                    }else {
                        msg.append(String.format("<font color='red'><p>%s(申请时间)%s提交删除失败</p></font>",
                                corpvo.getUnitname(), vo.getDapplydate()));
                    }

                }

            }
            json.setSuccess(errorCount > 0 ? false : true);
            json.setMsg(msg.toString());
        } catch (Exception e) {
            printErrorLog(json, e, "删除失败");
        }

        return ReturnData.ok().data(json);
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
