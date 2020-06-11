package com.dzf.zxkj.platform.controller.tax;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.tax.SynTaxInfoVO;
import com.dzf.zxkj.platform.service.tax.ISynTaxInfoService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/sys/sys_synTaxInfo")
@Slf4j
public class SynTaxInfoController extends BaseController {
    @Autowired
    private ISynTaxInfoService sys_synTaxInfoImple;

    /**
     * 更新纳税人信息
     */
    @PostMapping("/updateTaxCorpVos")
    public ReturnData<Json> updateTaxCorpVos(@RequestBody Map<String, String> param) {
        Json json = new Json();
        try {
            // SynTaxInfoVO[] corps = this.getFromArrayParam();
            String corps = param.get("corps");
            SynTaxInfoVO[] infovos = JsonUtils.deserialize(corps, SynTaxInfoVO[].class);
            if (infovos == null || infovos.length == 0) {
                throw new BusinessException("参数信息不完整，请检查");
            }
            this.sys_synTaxInfoImple.updateTaxCorpVos(SystemUtil.getLoginUserVo(), infovos);
            json.setSuccess(true);
            json.setData(infovos);
            json.setMsg("同步成功");
            writeLogRecord(LogRecordEnum.OPE_KJ_ZTXX, "税务同步客户数据", ISysConstants.SYS_0);
        } catch (Exception e) {
            printErrorLog(json, e, "同步失败");
            json.setSuccess(false);
        }

        return ReturnData.ok().data(json);
    }

    /**
     * 回写税种报表核定信息及个体户标志等
     */
    @PostMapping("/updateTaxCorpBodys")
    public ReturnData<Json> updateTaxCorpBodys(@RequestBody Map<String, String> param) {
        Json json = new Json();
        try {
            String corps = param.get("corps");
            SynTaxInfoVO[] infovos = JsonUtils.deserialize(corps, SynTaxInfoVO[].class);
            if (infovos == null || infovos.length == 0) {
                throw new BusinessException("参数信息不完整，请检查");
            }
            this.sys_synTaxInfoImple.updateTaxCorpBodys(SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserVo(), infovos);
            json.setSuccess(true);
            json.setData(infovos);
            json.setMsg("同步成功");
            writeLogRecord(LogRecordEnum.OPE_KJ_ZTXX, "税务同步报表数据", ISysConstants.SYS_0);
        } catch (Exception e) {
            printErrorLog(json, e, "同步失败");
            json.setSuccess(false);
        }

        return ReturnData.ok().data(json);
    }
}
