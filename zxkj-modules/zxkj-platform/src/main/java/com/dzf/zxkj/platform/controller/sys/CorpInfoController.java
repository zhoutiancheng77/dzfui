package com.dzf.zxkj.platform.controller.sys;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.image.ImageCommonPath;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("sys_corpinfo")
@Slf4j
public class CorpInfoController extends BaseController {

    @Autowired
    private ICorpService corpService;

    @RequestMapping("getCorpInfoByHomepage")
    public ReturnData getCorpInfoByHomepage(@MultiRequestBody CorpVO corpVo, @MultiRequestBody UserVO userVo) {
        Json json = new Json();
        try {
            String userid = SystemUtil.getLoginUserId();
            CorpVO fatherCorp =corpService.queryByPk(corpVo.getFathercorp());
            Map<String, Object> info = new HashMap<>();
            info.put("RULE", "4-2-2-2");
            info.put("CURRENCYID", IGlobalConstants.RMB_currency_id);
            info.put("StartDate", "");
            info.put("EndDate", "");
            info.put("isPicSrh", Boolean.FALSE);
            info.put("login_corp_id", corpVo.getPk_corp());
            info.put("UserName", userVo.getUser_name());
            info.put("RealName", userVo.getUser_name());
            info.put("vchPeopleId", userid);
            info.put("ischannel", fatherCorp.getIschannel()!=null && fatherCorp.getIschannel().booleanValue()?"Y":"N");
            info.put("isic", IcCostStyle.IC_ON.equals(corpVo.getBbuildic())?"Y":"N");
            info.put("iszongzhangch", IcCostStyle.IC_INVTENTORY.equals(corpVo.getBbuildic())?"Y":"N");
            info.put("Mobile", "");
            info.put("myCompany", corpVo.getUnitname());
            info.put("chname", corpVo.getChargedeptname() == null ? "" : corpVo.getChargedeptname());
            info.put("VchCount", 0);
            info.put("LoginDate", SystemUtil.getLoginDate());
            info.put("picRootPath", ImageCommonPath.getDataCenterPhotoPath());
            info.put("ctype", corpVo.getCorptype());
            info.put("jzdate", corpVo.getBegindate().toString());
            info.put("serverDate", new DZFDate().toString());
            String skinCode = userVo.getKjskin();
            if (!"lan".equals(skinCode) && !"hei".equals(skinCode)) {
                skinCode = "lan";
            }
            info.put("skincode", skinCode);
            json.setData(info);
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "获取公司信息失败");
            log.error("获取公司信息失败", e);
        }
        return ReturnData.ok().data(json);
    }
}
