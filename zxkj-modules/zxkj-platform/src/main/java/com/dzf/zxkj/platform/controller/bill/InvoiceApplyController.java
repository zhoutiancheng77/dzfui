package com.dzf.zxkj.platform.controller.bill;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.IInvoiceApplyConstant;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bill.InvoiceApplyVO;
import com.dzf.zxkj.platform.model.piaotong.PiaoTongResVO;
import com.dzf.zxkj.platform.service.bill.IInvoiceApplyService;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.dzf.zxkj.platform.util.zncs.CommonXml;
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
            List<InvoiceApplyVO> list = invoiceApplyService.query(SystemUtil.getLoginUserId());
            grid.setSuccess(true);
            grid.setTotal(Long.valueOf(list.size()));
            grid.setRows(list);
            grid.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
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
        }

        return ReturnData.ok().data(grid);
    }

    @PostMapping("/apply")
    public ReturnData<Grid> apply(@RequestBody Map<String, String> map) {
        Grid grid = new Grid();

        try {
            String body = map.get("body");
            if(StringUtil.isEmpty(body)){
                throw new BusinessException("参数为空,请检查");
            }
            InvoiceApplyVO[] vos = JsonUtils.deserialize(body, InvoiceApplyVO[].class);

            String userid = SystemUtil.getLoginUserId();
            String pk_corp = SystemUtil.getLoginCorpId();

            checkSecurityData(vos, null, userid);

            int errorCount = 0;
            String temp;
            StringBuffer msg = new StringBuffer();
            for(InvoiceApplyVO vo : vos){
                try {
                    temp = checkApply(vo);
                    if(!StringUtil.isEmpty(temp)){
                        errorCount++;
                        msg.append(String.format("<font color='red'><p>%s申请失败:%s</p></font>",
                                vo.getUnitname(), temp));
                        continue;
                    }
                    PiaoTongResVO resvo = invoiceApplyService.saveApply(userid, vo);
                    String code = resvo.getCode();
                    if(CommonXml.rtnsucccode.equals(code)){
                        msg.append(String.format("<font color='#2ab30f'><p>%s申请成功</p></font>",
                                vo.getUnitname()));
                    }else{
                        msg.append(String.format("<font color='#2ab30f'><p>%s申请失败:%s</p></font>",
                                vo.getUnitname(), resvo.getMsg()));
                    }

                } catch (Exception e) {
                    errorCount++;
                    log.error(e.getMessage(), e);
                    if(e instanceof BusinessException){
                        msg.append(String.format("<font color='red'><p>%s申请失败:%s</p></font>",
                                vo.getUnitname(), e.getMessage()));
                    }else{
                        msg.append(String.format("<font color='red'><p>%s申请失败</p></font>",
                                vo.getUnitname()));
                    }

                }

            }
            grid.setSuccess(errorCount > 0 ? false : true);
            grid.setMsg(msg.toString());
        } catch (Exception e) {
            printErrorLog(grid, e, "申请失败");
        }

        return ReturnData.ok().data(grid);
    }

    private String checkApply(InvoiceApplyVO vo){

        Integer status = vo.getIstatus();
        if(!(status == IInvoiceApplyConstant.APPLY_STATUS_0
                || status == IInvoiceApplyConstant.APPLY_STATUS_4
                || status == IInvoiceApplyConstant.APPLY_STATUS_7)){

            return "状态为未申请的客户才允许进行开通";
        }

        String[][] types = {
                {"unitname", "客户名称"}, {"vsoccrecode", "纳税人识别号"},
                {"legalbodycode", "法人名称"}, {"linkman2", "联系人名称"},
                {"email1", "联系人邮箱"}, {"phone1", "联系人手机号"},
                {"vprovcode", "地区编码"}, {"vprovince", "所属地区"},

        };

        Object value;
        String errstr = "";
        for(String arr[] : types){
            value = vo.getAttributeValue(arr[0]);
            if(value == null){
                errstr += "," + arr[1];
            }
        }
        Integer ftype = vo.getFiletype();
        if(ftype == null || ftype != IInvoiceApplyConstant.FILETYPE_2){//营业执照副本
            errstr += "," + "营业执照副本";
        }

        if(!StringUtil.isEmpty(errstr)){
            errstr = errstr.substring(1) + "字段不允许为空,请检查";
        }

        return errstr;
    }

}
