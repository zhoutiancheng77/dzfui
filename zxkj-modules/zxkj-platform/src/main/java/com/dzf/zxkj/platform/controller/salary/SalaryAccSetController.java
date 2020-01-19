package com.dzf.zxkj.platform.controller.salary;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.gzgl.SalaryAccSetVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryKmDeptVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.gzgl.ISalaryAccSetService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/*
 * 工资科目设置
 */
@RestController
@RequestMapping("/salary/gl_gzkmszact2")
@Slf4j
public class SalaryAccSetController extends BaseController {

    /**
     *
     */

    @Autowired
    private ISalaryAccSetService gl_gzkmszserv = null;

    @GetMapping("/query")
    public ReturnData<Json> query(@MultiRequestBody CorpVO corpVO) {

        Json json = new Json();
        SalaryAccSetVO vo = gl_gzkmszserv.queryTable(corpVO.getPk_corp());
        json.setRows(vo);
        json.setMsg("查询成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/save")
    public ReturnData<Json> save(@RequestBody Map<String, String> map) {

        Json json = new Json();
        String szdata = map.get("szdata");
        if (DZFValueCheck.isEmpty(szdata)) {
            throw new BusinessException("数据为空,保存失败!");
        }
        String pk_corp = SystemUtil.getLoginCorpId();
        SalaryAccSetVO vo = JsonUtils.deserialize(szdata, SalaryAccSetVO.class);
        if (vo == null) {
            throw new BusinessException("数据为空,保存失败!");
        }
        if (DZFValueCheck.isEmpty(vo.getPk_corp())) {
            vo.setPk_corp(pk_corp);
        }
        checkSecurityData(new SalaryAccSetVO[]{vo},null,null, !StringUtil.isEmpty(vo.getPk_salaryaccset()));
        vo = gl_gzkmszserv.save(vo);
        json.setRows(vo);
        json.setMsg("保存成功");
        json.setSuccess(true);
		writeLogRecord(LogRecordEnum.OPE_KJ_SALARY, "工资科目设置保存", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/saveGroup")
    public ReturnData<Json> saveGroup(@MultiRequestBody CorpVO corpVO) {

        Json json = new Json();
        SalaryAccSetVO vo = gl_gzkmszserv.saveGroupVO(corpVO.getPk_corp());
        json.setRows(vo);
        json.setMsg("获取入账科目成功");
        json.setSuccess(true);
		writeLogRecord(LogRecordEnum.OPE_KJ_SALARY, "工资科目设置获取入账科目", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/queryFyKm")
    public ReturnData<Json> queryFyKm(@MultiRequestBody CorpVO corpVO) {

        Json json = new Json();
        SalaryKmDeptVO[] vos = gl_gzkmszserv.queryFykm(corpVO.getPk_corp());
        json.setRows(vos);
        json.setMsg("查询成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/saveFykm")
    public ReturnData<Json> saveFykm(@RequestBody Map<String, String> map) {

        Json json = new Json();
        String kmdata = map.get("kmdata");
        if (DZFValueCheck.isEmpty(kmdata)) {
            throw new BusinessException("数据为空,保存失败!");
        }
        SalaryKmDeptVO[] vos = JsonUtils.deserialize(kmdata, SalaryKmDeptVO[].class);
        if (vos == null || vos.length == 0) {
            throw new BusinessException("数据为空,保存失败!");
        }
        String pk_corp = SystemUtil.getLoginCorpId();
        SalaryKmDeptVO[] vos1 = gl_gzkmszserv.saveFykm(pk_corp, vos);
        json.setRows(vos);
        json.setMsg("保存部门费用科目成功");
        json.setSuccess(true);
		writeLogRecord(LogRecordEnum.OPE_KJ_SALARY, "工资科目设置部门费用科目", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }
}
