package com.dzf.zxkj.platform.controller.sys;

import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.bdset.PrintSettingVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.bdset.IPrintSettingService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("gl_printsetting")
@Slf4j
public class PrintSettingController {
    @Autowired
    private IPrintSettingService gl_print_setting_serv;

    @GetMapping("/query")
    public ReturnData<Json> query(@RequestParam String nodeName, String corpId) {
        Json json = new Json();
        if (StringUtils.isEmpty(corpId)) {
            corpId = SystemUtil.getLoginCorpId();
        }
        PrintSettingVO vo = gl_print_setting_serv.query(corpId, SystemUtil.getLoginUserId(), nodeName);
        json.setData(vo);
        json.setMsg("查询成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("query")
    public ReturnData<Grid> query(@MultiRequestBody String nodeName,
                                  @MultiRequestBody String pk_corp,
                                  @MultiRequestBody CorpVO corpVO,
                                  @MultiRequestBody UserVO userVO) {
        Grid<PrintSettingVO> json = new Grid();
        try {
            if (StringUtil.isEmpty(pk_corp)) {
                pk_corp = corpVO.getPk_corp();
            }
            PrintSettingVO vo = gl_print_setting_serv.query(pk_corp, userVO.getCuserid(), nodeName);
            json.setRows(vo);
            json.setMsg("查询成功");
            json.setSuccess(true);
        } catch (Exception e) {
            json.setMsg("查询失败");
            json.setSuccess(false);
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("save")
    public ReturnData<Grid> save(@RequestBody PrintSettingVO printSettingVO) {
        Grid json = new Grid();
        try {
            setDefaultValue(printSettingVO);
            gl_print_setting_serv.save(printSettingVO);
            json.setMsg("保存成功");
            json.setRows(printSettingVO);
            json.setSuccess(true);
        } catch (Exception e) {
            json.setMsg("保存打印设置失败！");
            json.setSuccess(false);
            log.error("保存打印设置失败！", e);
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 针对多个设置字段使用，主要用于报表
     * @param printSettingVO
     * @return
     */
    @PostMapping("saveMulColumn")
    public ReturnData<Grid> saveMulColumn(@RequestBody PrintSettingVO printSettingVO) {
        Grid json = new Grid();
        try {
            setDefaultValue(printSettingVO);
            gl_print_setting_serv.saveMulColumn(printSettingVO);
            json.setMsg("保存成功");
            json.setRows(printSettingVO);
            json.setSuccess(true);
        } catch (Exception e) {
            json.setMsg("保存打印设置失败！");
            json.setSuccess(false);
            log.error("保存打印设置失败！", e);
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 设置默认值
     * @param printSettingVO
     */
    private void setDefaultValue(@RequestBody PrintSettingVO printSettingVO) {
        String loginCorp = SystemUtil.getLoginCorpId();
        if (StringUtil.isEmpty(printSettingVO.getCorpids())) {
            printSettingVO.setCorpids(loginCorp);
        }
        if (StringUtil.isEmpty(printSettingVO.getPk_corp())) {
            printSettingVO.setPk_corp(loginCorp);
        }
        printSettingVO.setCuserid(SystemUtil.getLoginUserId());
    }
}
