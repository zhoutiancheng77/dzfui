package com.dzf.zxkj.platform.controller.taxrpt;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.service.taxrpt.ITaxDeclarationService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/taxrpt/taxDeclarAction")
@Slf4j
public class TaxDeclarationController {

    @Autowired
    private ITaxDeclarationService taxDeclarationService;
    @Autowired
    private IUserService userService;

    /**
     * 获取公司待填报类型列表
     */
    @GetMapping("/queryTypeList")
    public ReturnData<Grid> getTypeList(String pk_corp) {
        Grid grid = new Grid();
        try {
            DZFDate nowDate = new DZFDate();
            String period = DateUtils.getPeriod(nowDate);
            UserVO uservo = SystemUtil.getLoginUserVo();
            List<TaxReportVO> list = taxDeclarationService.initGetTypeList(pk_corp, uservo,
                    period, uservo.getCuserid(), nowDate.toString());
            if (list != null && list.size() > 0) {
                grid.setRows(list);
                grid.setTotal(Long.valueOf(list.size()));
                grid.setSuccess(true);
                String res = taxDeclarationService.qryTaxReportValid(list, pk_corp);
                if(StringUtil.isEmpty(res)){
                    res = "数据加载成功";
                }
                grid.setMsg(res);
            } else {
                grid.setSuccess(false);
                grid.setMsg("当前期间没有可申报的税种，或没有维护纳税信息！");
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "数据加载失败");
        }

        return ReturnData.ok().data(grid);
    }

    /***
     * 获取SpreadJs 数据Sr
     */
    @GetMapping("/getSpreadJsData")
    public ReturnData<Json> getSpreadJsData(String pk_taxreport, String pk_corp, String readonly) {

        Boolean isReadonly = (readonly != null && readonly.toLowerCase().equals("y"));
        Json json = new Json();
        try {

            //校验当前公司权限
            Set<String> nnmnc = userService.querypowercorpSet(SystemUtil.getLoginUserId());
            if (!nnmnc.contains(pk_corp)) {
                throw new BusinessException("当前操作人，不包含该公司权限");
            }

            String spreadjson = taxDeclarationService.getSpreadJSData(pk_taxreport,
                    SystemUtil.getLoginUserVo(), null, isReadonly);

            json.setData(spreadjson);
            json.setStatus(200);
            json.setSuccess(true);
            json.setMsg("数据加载成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException ? e.getMessage() : "获取模板数据失败");
        }

        return ReturnData.ok().data(json);
    }

    /**
     * 重算
     */
    @SuppressWarnings({ "resource", "deprecation" })
    @PostMapping("/onRecal")
    public ReturnData<Json> onRecal(@RequestBody Map<String, String> param) {
        Json rsjson = new Json();
        try {
            String pk_taxreport = param.get("pk_taxreport");
            String pk_corp = param.get("pk_corp");
            String jsonString = param.get("jsonString");
            String reportname = param.get("reportname");
            String calall = param.get("calall");

            //校验当前公司权限
            Set<String> nnmnc = userService.querypowercorpSet(SystemUtil.getLoginUserId());
            if (!nnmnc.contains(pk_corp)) {
                throw new BusinessException("当前操作人，不包含该公司权限");
            }

            String spreadjson = taxDeclarationService.onRecal(jsonString, pk_taxreport, SystemUtil.getLoginUserVo(),
                    pk_corp, reportname,(calall != null && calall.equals("Y")), "");

            rsjson.setData(spreadjson);
            rsjson.setStatus(200);
            rsjson.setSuccess(true);
            rsjson.setMsg("重算成功!");

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            rsjson.setSuccess(false);
            rsjson.setMsg(e instanceof BusinessException ? e.getMessage() : "重算失败");
            rsjson.setStatus(-100);

        }finally {

        }

        return ReturnData.ok().data(rsjson);
    }

    @GetMapping("/getCondition")
    public ReturnData getCondition(String pk_taxreport) {
        Json rsjson = new Json();
        try {

            String[] saConditions = taxDeclarationService.getCondition(pk_taxreport, SystemUtil.getLoginUserVo());

            rsjson.setData(saConditions);
            rsjson.setStatus(200);
            rsjson.setSuccess(true);
            rsjson.setMsg("查询成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            rsjson.setSuccess(true);
            rsjson.setMsg(e instanceof BusinessException ? e.getMessage() : "查询检查条件失败");
            rsjson.setStatus(-100);
        }
        return ReturnData.ok().data(rsjson);
    }

    @SuppressWarnings("deprecation")
    @PostMapping("/saveReport")
    public ReturnData<Json> saveReport(@RequestBody Map<String, String> param) {
        String pk_taxreport = param.get("pk_taxreport");
        String corpid = param.get("pk_corp");
        String jsonString = param.get("jsonString");

        Json rsjson = new Json();
        try {
            //校验当前公司权限
            Set<String> nnmnc = userService.querypowercorpSet(SystemUtil.getLoginUserId());
            if (!nnmnc.contains(corpid)) {
                throw new BusinessException("当前操作人，不包含该公司权限");
            }

            String message = taxDeclarationService.saveReport(pk_taxreport, corpid, jsonString,
                    SystemUtil.getLoginUserVo(), SystemUtil.getLoginDate(), null);

            rsjson.setStatus(200);
            rsjson.setSuccess(true);
//			if(message.startsWith("重庆报税")){
            if(!StringUtil.isEmpty(message)){
                rsjson.setMsg("警告:"+message);
            }else{
                rsjson.setMsg("保存成功!");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            rsjson.setSuccess(false);
            rsjson.setMsg(e instanceof BusinessException ? e.getMessage() : "保存失败");
            rsjson.setStatus(-100);
        }

        finally {
        }

        return ReturnData.ok().data(rsjson);
    }

    /**
     * 删除
     */
    @PostMapping("/onDelete")
    public ReturnData<Json> onDelete(@RequestBody Map<String, String> param) {
        Json rsjson = new Json();
        String period = "";
        String sbname = "";
        try {
            String pk_taxreport = param.get("pk_taxreport");
            String corpid = param.get("pk_corp");
            period = param.get("period");
            sbname = param.get("sbname");

            //校验当前公司权限
            Set<String> nnmnc = userService.querypowercorpSet(SystemUtil.getLoginUserId());
            if (!nnmnc.contains(corpid)) {
                throw new BusinessException("当前操作人，不包含该公司权限");
            }

            taxDeclarationService.processDelete(pk_taxreport, corpid,
                    SystemUtil.getLoginDate(), SystemUtil.getLoginUserVo(), "");
            rsjson.setStatus(200);
            rsjson.setSuccess(true);
            rsjson.setMsg("删除成功!");
//            writeLogRecord(LogRecordEnum.OPE_KJ_TAX.getValue(),
//                    "所属期间为" + period + "的税表" + sbname + "删除成功", ISysConstants.SYS_2);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            rsjson.setSuccess(true);
            rsjson.setMsg(e instanceof BusinessException ? e.getMessage() : "删除失败!");
            rsjson.setStatus(-100);

        }

        return ReturnData.ok().data(rsjson);
    }

    /**
     * 批量填写
     */
    @PostMapping("/batWrite")
    public ReturnData<Json> batWrite(@RequestBody Map<String, String> param, HttpServletRequest request){
        Json json = new Json();
        try {
            String pk_corp = param.get("pk_corp");
            //校验当前公司权限
            Set<String> nnmnc = userService.querypowercorpSet(SystemUtil.getLoginUserId());
            if (!nnmnc.contains(pk_corp)) {
                throw new BusinessException("当前操作人，不包含该公司权限");
            }
            //获取cookie
            Cookie[] cookies  = request.getCookies();
            String info = taxDeclarationService.saveBatWriteInfo(pk_corp,
                    SystemUtil.getLoginUserVo(), SystemUtil.getLoginDate(), cookies);
            json.setStatus(200);
            json.setSuccess(true);
            if(StringUtil.isEmpty(info)){
                json.setSuccess(false);
                info = "没有待填写的报表";
            }
            json.setMsg(info);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException ? e.getMessage() : "填写失败");
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("/sendTaxReport")
    public ReturnData<Json> sendTaxReport(@RequestBody Map<String, String> param) {

        Json json = new Json();
        try {
            String pk_taxreport = param.get("pk_taxreport");
            String corpid = param.get("pk_corp");
            if (StringUtil.isEmpty(pk_taxreport))//|| StringUtil.isEmpty(pk_taxtypelistdetail)
                throw new BusinessException("纳税申报信息出错");

            TaxReportVO reportvo = (TaxReportVO)taxDeclarationService.processSendTaxReport(SystemUtil.getLoginCorpVo(),
                    SystemUtil.getLoginUserVo(), corpid, pk_taxreport);
            json.setSuccess(true);
            json.setMsg("上报成功");
            json.setData(reportvo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException ? e.getMessage() : "上报失败");
        }
        return ReturnData.ok().data(json);
    }
}
