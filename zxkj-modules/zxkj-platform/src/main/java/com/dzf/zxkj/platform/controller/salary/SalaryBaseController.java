package com.dzf.zxkj.platform.controller.salary;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.gzgl.SalaryAccSetVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryBaseVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportVO;
import com.dzf.zxkj.platform.service.gzgl.ISalaryBaseService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * 工资基数设置
 */
@RestController
@RequestMapping("/salary/gl_gzbbase")
@Slf4j
public class SalaryBaseController extends BaseController {

    @Autowired
    private ISalaryBaseService gl_gzbbaseserv;

    @GetMapping("/query")
    public ReturnData<Json> query(Integer page, Integer rows, @RequestParam("opdate") String qj,
                                  String pk_corp, String isfenye) {
        Json json = new Json();
        if (StringUtil.isEmpty(qj))
            throw new BusinessException("期间为空");
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司为空");
        }
        if ("Y".equals(isfenye)) {// 分页
            QueryPageVO pagevo = gl_gzbbaseserv.queryBodysBypage(pk_corp, qj, page, rows);
            json.setTotal(Long.valueOf(pagevo.getTotal()));
            json.setRows(pagevo.getPagevos());
        } else {
            SalaryBaseVO[] vos = gl_gzbbaseserv.query(pk_corp, qj);// 查询工资表基数
            if (vos == null || vos.length == 0) {
                vos = new SalaryBaseVO[0];
            }
            json.setRows(vos);
        }
        json.setMsg("查询成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/save")
    public ReturnData<Json> save(@RequestBody Map<String, String> map) {

        Json json = new Json();
        String strArr = map.get("strArr");
        if (DZFValueCheck.isEmpty(strArr)) {
            throw new BusinessException("数据为空");
        }
        String qj = map.get("opdate");
        if (StringUtil.isEmpty(qj))
            throw new BusinessException("期间为空");

        String pk_corp = map.get("pk_corp");
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司为空");
        }
        List<SalaryBaseVO> list = new ArrayList<>();
        SalaryBaseVO vo = JsonUtils.deserialize(strArr, SalaryBaseVO.class);
        vo.setPk_corp(pk_corp);
        checkSecurityData(new SalaryBaseVO[]{vo},null,null, !StringUtil.isEmpty(vo.getPk_sqlarybase()));
        list.add(vo);
        SalaryBaseVO[] vos = gl_gzbbaseserv.save(pk_corp, list, qj);
        json.setMsg("社保公积金保存成功");
        json.setRows(vos);
        json.setSuccess(true);
		writeLogRecord(LogRecordEnum.OPE_KJ_SALARY, "社保公积金保存", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/delete")
    public ReturnData<Json> delete(@RequestBody Map<String, String> map) {

        Json json = new Json();
        String qj = map.get("opdate");
        if (StringUtil.isEmpty(qj))
            throw new BusinessException("期间为空");
        String pk_corp = map.get("ops");
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司为空");
        }
        String ids = map.get("pks");
        if (StringUtil.isEmpty(ids)) {
            throw new BusinessException("删除数据为空");
        }
        checkSecurityData(null,new String[]{pk_corp},null);
        SalaryBaseVO[] vo = gl_gzbbaseserv.delete(pk_corp, ids, qj);
        json.setRows(vo);
        json.setMsg("删除成功");
        json.setSuccess(true);
		writeLogRecord(LogRecordEnum.OPE_KJ_SALARY, "社保公积金删除", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/getSalaryAccSet")
    public ReturnData<Json> getSalaryAccSet(@RequestBody Map<String, String> map) {

        Json json = new Json();
        String pk_corp = map.get("pk_corp");
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司为空");
        }
        String cpersonids = map.get("cpersonids");
        String opdate = map.get("opdate");
        if (StringUtil.isEmpty(opdate)) {
            throw new BusinessException("期间为空");
        }
        SalaryReportVO vos = gl_gzbbaseserv.getSalarySetInfo(pk_corp, cpersonids, opdate);
        json.setMsg("获取工资科目设置成功");
        json.setRows(vos);
        json.setSuccess(true);

        return ReturnData.ok().data(json);
    }

    @PostMapping("/changeNum")
    public ReturnData<Json> changeNum(@RequestBody Map<String, String> map) {

        Json json = new Json();
        String qj = map.get("opdate");
        if (StringUtil.isEmpty(qj))
            throw new BusinessException("期间为空");
        qj = qj.substring(0, 7);
        String ids = map.get("ids");
        String chgdata = map.get("chgdata");
        if (!StringUtil.isEmpty(chgdata)) {
            chgdata = chgdata.replaceAll("data.", "");
            SalaryBaseVO basevo = JsonUtils.deserialize(chgdata, SalaryBaseVO.class);
            SalaryAccSetVO accsetvo = JsonUtils.deserialize(chgdata, SalaryAccSetVO.class);
            String logincorp = SystemUtil.getLoginCorpId();
            String cuserid = SystemUtil.getLoginUserId();
            if (DZFValueCheck.isEmpty(basevo.getPk_corp())) {
                basevo.setPk_corp(logincorp);
            }
            String pk_corp = basevo.getPk_corp();
            checkSecurityData(null,new String[]{pk_corp},null);
            gl_gzbbaseserv.saveChangeNum(pk_corp, cuserid, ids, basevo, accsetvo, qj);
        }
        json.setMsg("调整基数成功");
        json.setSuccess(true);
		writeLogRecord(LogRecordEnum.OPE_KJ_SALARY, "批量调整基数", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/changeNumGroup")
    public ReturnData<Json> changeNumGroup(@RequestBody Map<String, String> map) {

        Json json = new Json();
        String chgdata = map.get("chgdata");
        if (!StringUtil.isEmpty(chgdata)) {
            chgdata = chgdata.replaceAll("data.", "");
            SalaryAccSetVO accsetvo = JsonUtils.deserialize(chgdata, SalaryAccSetVO.class);
            String logincorp = SystemUtil.getLoginCorpId();
            String cuserid = SystemUtil.getLoginUserId();
            gl_gzbbaseserv.saveChangeNumGroup(logincorp, cuserid, accsetvo);
        }
        json.setMsg("统一基数设置成功");
        json.setSuccess(true);
		writeLogRecord(LogRecordEnum.OPE_KJ_SALARY, "统一基数设置", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }
}
