package com.dzf.zxkj.platform.controller.sys;

import cn.jiguang.common.utils.StringUtils;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.sys.IBDCorpService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/sys/sys_corpact")
@Slf4j
public class BDCorpController {

    @Autowired
    private IBDCorpService sys_corpserv;
    @Autowired
    private IUserService userServiceImpl;

    /**
     * 启用总账存货
     */
    @PostMapping("/updateStGenledic")
    public ReturnData<Json> updateStGenledic(@RequestBody Map<String, String> param) {

        String corps = param.get("corps");
        String ly = param.get("ly");
        Json json = new Json();
        CorpVO[] corpVOs = StringUtils.isEmpty(corps)
                ? null : JsonUtils.deserialize(corps, CorpVO[].class);
        boolean kjflag = String.valueOf(ISysConstants.SYS_2).equals(ly);//是否来源在线会计

        if (corpVOs.length > 0) {
            try {
                String pk_corp = SystemUtil.getLoginCorpId();
                StringBuilder statusMsg = new StringBuilder();
                String unitname = "";
                Set<String> nnmnc = getKJPowerSet(kjflag);
                for (CorpVO corpVO : corpVOs) {
                    try {
                        unitname = corpVO.getUnitname();
                        CorpVO corp = sys_corpserv.queryByID(corpVO.getPk_corp());
                        if (corp == null) {
                            statusMsg.append(corpVO.getUnitname()).append("不存在，或已被删除！<br>");
                            continue;
                        } else if (kjflag) {
                            if (!nnmnc.contains(pk_corp)) {
                                statusMsg.append("没有").append(corpVO.getUnitname()).append("的权限<br>");
                                continue;
                            }
                        } else {
                            if (!pk_corp.equals(corp.getFathercorp())) {
                                statusMsg.append("没有").append(unitname).append("的权限<br>");
                                continue;
                            }
                            if (corp.getIsseal() != null && corp.getIsseal().booleanValue()) {
                                statusMsg.append(unitname).append("已停止服务，不允许此操作！<br>");
                                continue;
                            }
                        }
                        sys_corpserv.updateStGenledic(corp);
                    } catch (Exception e) {
                        if (e instanceof BusinessException) {
                            statusMsg.append(unitname).append(e.getMessage()).append("<br>");
                        } else {
                            log.error("总账存货启用失败", e);
                            statusMsg.append(unitname).append("总账存货启用失败<br>");
                        }
                    }
                }
                String msg = statusMsg.toString();
                if (msg.length() > 0) {
                    json.setSuccess(false);
                    json.setMsg(msg);
                } else {
                    json.setSuccess(true);
                    json.setMsg("总账存货启用成功");
                }
                if (corpVOs.length == 1)
                    json.setRows(corpVOs[0]);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                json.setSuccess(false);
                json.setMsg(e instanceof BusinessException ? e.getMessage() : "总账存货启用失败");
            }
        } else {
            json.setSuccess(false);
            json.setMsg("总账存货启用失败");
        }
        return ReturnData.ok().data(json);
    }

    private Set<String> getKJPowerSet(boolean kjflag) {
        Set<String> nnmnc = null;
        if (kjflag)
            nnmnc = userServiceImpl.querypowercorpSet(SystemUtil.getLoginUserId());

        return nnmnc;
    }

    /**
     * 停用总账存货
     */
    @PostMapping("/updateSpGenledic")
    public ReturnData<Json> updateSpGenledic(@RequestBody Map<String, String> param) {
        String corps = param.get("corps");
        String ly = param.get("ly");

        Json json = new Json();
        CorpVO[] corpVOs = StringUtils.isEmpty(corps)
                ? null : JsonUtils.deserialize(corps, CorpVO[].class);
        boolean kjflag = String.valueOf(ISysConstants.SYS_2).equals(ly);//是否来源在线会计

        if (corpVOs.length > 0) {
            try {
                String pk_corp = SystemUtil.getLoginCorpId();
                StringBuilder statusMsg = new StringBuilder();
                String unitname = "";
                Set<String> nnmnc = getKJPowerSet(kjflag);
                for (CorpVO corpVO : corpVOs) {
                    try {
                        unitname = corpVO.getUnitname();
                        CorpVO corp = sys_corpserv.queryByID(corpVO.getPk_corp());
                        if (corp == null) {
                            statusMsg.append(unitname).append("不存在，或已被删除！<br>");
                            continue;
                        } else if (kjflag) {
                            if (!nnmnc.contains(pk_corp)) {
                                statusMsg.append("没有").append(corpVO.getUnitname()).append("的权限<br>");
                                continue;
                            }
                        } else {
                            if (!pk_corp.equals(corp.getFathercorp())) {
                                statusMsg.append("没有").append(unitname).append("的权限！<br>");
                                continue;
                            }
                            if (corp.getIsseal() != null && corp.getIsseal().booleanValue()) {
                                statusMsg.append(unitname).append("已停止服务，不允许此操作！<br>");
                                continue;
                            }
                        }
                        sys_corpserv.updateSpGenledic(corp);
                    } catch (Exception e) {
                        if (e instanceof BusinessException) {
                            statusMsg.append(unitname).append(e.getMessage()).append("<br>");
                        } else {
                            log.error("总账存货停用失败", e);
                            statusMsg.append(unitname).append("总账存货停用失败<br>");
                        }
                    }
                }
                String msg = statusMsg.toString();
                if (msg.length() > 0) {
                    json.setSuccess(false);
                    json.setMsg(msg);
                } else {
                    json.setSuccess(true);
                    json.setMsg("总账存货停用成功");
                }
                if (corpVOs.length == 1)
                    json.setRows(corpVOs[0]);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                json.setSuccess(false);
                json.setMsg(e instanceof BusinessException
                        ? e.getMessage() : "总账存货停用失败");
            }
        } else {
            json.setSuccess(false);
            json.setMsg("总账存货停用失败");
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 更新固定资产
     */
    @PostMapping("/updateHflag")
    public ReturnData<Json> updateHflag(@RequestBody Map<String, String> param) {
        String corps = param.get("corps");
        String ly = param.get("ly");

        Json json = new Json();
        CorpVO[] corpVOs = StringUtils.isEmpty(corps)
                ? null : JsonUtils.deserialize(corps, CorpVO[].class);
        boolean kjflag = String.valueOf(ISysConstants.SYS_2).equals(ly);//是否来源在线会计
        try {
            String pk_corp = SystemUtil.getLoginCorpId();
            StringBuilder statusMsg = new StringBuilder();
            Set<String> nnmnc = getKJPowerSet(kjflag);
            for (CorpVO corpVO : corpVOs) {
                try {
                    // 判断操作用户是否为当前登录公司
                    CorpVO corp = sys_corpserv.queryByID(corpVO.getPk_corp());
                    if (corp == null) {
                        statusMsg.append(corpVO.getUnitname()).append("不存在，或已被删除！<br>");
                    } else if (kjflag) {
                        if (!nnmnc.contains(pk_corp)) {
                            statusMsg.append("没有").append(corpVO.getUnitname()).append("的权限<br>");
                            continue;
                        }
                    } else if (!pk_corp.equals(corp.getFathercorp())) {
                        statusMsg.append("没有").append(corpVO.getUnitname()).append("的权限<br>");
                    }
                    if (corpVO.getBusibegindate() == null) {
                        corpVO.setBusibegindate(new DZFDate(SystemUtil.getLoginDate()));
                    }
                    sys_corpserv.updateHflagSer(corpVO);
                } catch (Exception e) {
                    if (e instanceof BusinessException) {
                        statusMsg.append(corpVO.getUnitname()).append(e.getMessage()).append("<br>");
                    } else {
                        log.error("固定资产启用失败", e);
                        statusMsg.append(corpVO.getUnitname()).append("固定资产启用失败<br>");
                    }
                }
            }
            json.setSuccess(true);
            String msg = statusMsg.toString();
            if (msg.length() > 0) {
                json.setSuccess(false);
                json.setMsg(msg);
            } else {
                json.setSuccess(true);
                json.setMsg("固定资产启用成功");
            }

            if (corpVOs.length == 1)
                json.setRows(corpVOs[0]);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException
                    ? e.getMessage() : "启用失败");
        }

        return ReturnData.ok().data(json);
    }

    /**
     * 取消启用固定资产
     */
    @PostMapping("/updateTyHflag")
    public ReturnData<Json> updateTyHflag(@RequestBody Map<String, String> param) {
        String corps = param.get("corps");
        String ly = param.get("ly");

        Json json = new Json();
        CorpVO[] corpVOs = StringUtils.isEmpty(corps)
                ? null : JsonUtils.deserialize(corps, CorpVO[].class);
        boolean kjflag = String.valueOf(ISysConstants.SYS_2).equals(ly);//是否来源在线会计

        if (corpVOs.length > 0) {
            try {
                String pk_corp = SystemUtil.getLoginCorpId();
                StringBuilder statusMsg = new StringBuilder();
                String unitname = "";
                Set<String> nnmnc = getKJPowerSet(kjflag);
                for (CorpVO corpVO : corpVOs) {
                    try {
                        unitname = corpVO.getUnitname();
                        CorpVO corp = sys_corpserv.queryByID(corpVO.getPk_corp());
                        if (corp == null) {
                            statusMsg.append(unitname).append("不存在，或已被删除！<br>");
                            continue;
                        } else if (kjflag) {
                            if (!nnmnc.contains(pk_corp)) {
                                statusMsg.append("没有").append(corpVO.getUnitname()).append("的权限<br>");
                                continue;
                            }
                        } else {
                            if (!pk_corp.equals(corp.getFathercorp())) {
                                statusMsg.append("没有").append(unitname).append("的权限！<br>");
                                continue;
                            }
                        }
                        corpVO.setBusibegindate(null);
                        corpVO.setHoldflag(DZFBoolean.FALSE);
                        sys_corpserv.updateHflagTy(corpVO);
                    } catch (Exception e) {
                        if (e instanceof BusinessException) {
                            statusMsg.append(unitname).append(e.getMessage()).append("<br>");
                        } else {
                            log.error("固定资产停用失败", e);
                            statusMsg.append(unitname).append("固定资产停用失败<br>");
                        }
                    }
                }
                String msg = statusMsg.toString();
                if (msg.length() > 0) {
                    json.setSuccess(false);
                    json.setMsg(msg);
                } else {
                    json.setSuccess(true);
                    json.setMsg("固定资产停用成功");
                }
                if (corpVOs.length == 1)
                    json.setRows(corpVOs[0]);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                json.setSuccess(false);
                json.setMsg(e instanceof BusinessException
                        ? e.getMessage() : "停用失败");
            }
        } else {
            json.setSuccess(false);
            json.setMsg("停用失败");
        }
        return ReturnData.ok().data(json);
    }

    // 更新库存
    @PostMapping("/updateBuildic")
    public ReturnData<Json> updateBuildic(@RequestBody Map<String, String> param) {
        String corps = param.get("corps");
        String ly = param.get("ly");

        Json json = new Json();
        CorpVO[] corpVOs = StringUtils.isEmpty(corps)
                ? null : JsonUtils.deserialize(corps, CorpVO[].class);
        boolean kjflag = String.valueOf(ISysConstants.SYS_2).equals(ly);//是否来源在线会计

        if (corpVOs.length > 0) {
            try {
                String pk_corp = SystemUtil.getLoginCorpId();
                StringBuilder statusMsg = new StringBuilder();
                String unitname = "";
                Set<String> nnmnc = getKJPowerSet(kjflag);
                for (CorpVO corpVO : corpVOs) {
                    try {
                        unitname = corpVO.getUnitname();
                        CorpVO corp = sys_corpserv.queryByID(corpVO.getPk_corp());
                        if (corp == null) {
                            statusMsg.append(unitname).append("不存在，或已被删除！<br>");
                            continue;
                        } else if (kjflag) {
                            if (!nnmnc.contains(pk_corp)) {
                                statusMsg.append("没有").append(corpVO.getUnitname()).append("的权限<br>");
                                continue;
                            }
                        } else if (!pk_corp.equals(corp.getFathercorp())) {
                            statusMsg.append("没有").append(unitname).append("的权限<br>");
                            continue;
                        }
                        if (corpVO.getIcbegindate() == null) {
                            corpVO.setIcbegindate(new DZFDate(SystemUtil.getLoginDate()));
                        }
                        sys_corpserv.updateBuildicSer(corpVO);
                    } catch (Exception e) {
                        if (e instanceof BusinessException) {
                            statusMsg.append(unitname).append(e.getMessage()).append("<br>");
                        } else {
                            log.error("库存启用失败", e);
                            statusMsg.append(unitname).append("库存启用失败<br>");
                        }
                    }
                }
                String msg = statusMsg.toString();
                if (msg.length() > 0) {
                    json.setSuccess(false);
                    json.setMsg(msg);
                } else {
                    json.setSuccess(true);
                    json.setMsg("库存启用成功");
                }
                if (corpVOs.length == 1)
                    json.setRows(corpVOs[0]);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                json.setSuccess(false);
                json.setMsg(e instanceof BusinessException
                        ? e.getMessage() : "库存启用失败");
            }
        } else {
            json.setSuccess(false);
            json.setMsg("库存启用失败");
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 取消启用库存
     */
    @PostMapping("/updateTyBuildic")
    public ReturnData<Json> updateTyBuildic(@RequestBody Map<String, String> param) {
        String corps = param.get("corps");
        String ly = param.get("ly");

        Json json = new Json();
        CorpVO[] corpVOs = StringUtils.isEmpty(corps)
                ? null : JsonUtils.deserialize(corps, CorpVO[].class);
        boolean kjflag = String.valueOf(ISysConstants.SYS_2).equals(ly);//是否来源在线会计

        if (corpVOs.length > 0) {
            try {
                String pk_corp = SystemUtil.getLoginCorpId();
                StringBuilder statusMsg = new StringBuilder();
                String unitname = "";
                Set<String> nnmnc = getKJPowerSet(kjflag);
                for (CorpVO corpVO : corpVOs) {
                    try {
                        unitname = corpVO.getUnitname();
                        CorpVO corp = sys_corpserv.queryByID(corpVO.getPk_corp());
                        if (corp == null) {
                            statusMsg.append(unitname).append("不存在，或已被删除！<br>");
                            continue;
                        } else if (kjflag) {
                            if (!nnmnc.contains(pk_corp)) {
                                statusMsg.append("没有").append(corpVO.getUnitname()).append("的权限<br>");
                                continue;
                            }
                        } else {
                            if (!pk_corp.equals(corp.getFathercorp())) {
                                statusMsg.append("没有").append(unitname).append("的权限！<br>");
                                continue;
                            }
                        }
                        corpVO.setIcbegindate(null);
                        corpVO.setBbuildic(IcCostStyle.IC_OFF);
                        sys_corpserv.updateBuildicTy(corpVO);
                    } catch (Exception e) {
                        if (e instanceof BusinessException) {
                            statusMsg.append(unitname).append(e.getMessage()).append("<br>");
                        } else {
                            log.error("库存停用失败", e);
                            statusMsg.append(unitname).append("库存停用失败<br>");
                        }
                    }
                }
                String msg = statusMsg.toString();
                if (msg.length() > 0) {
                    json.setSuccess(false);
                    json.setMsg(msg);
                } else {
                    json.setSuccess(true);
                    json.setMsg("库存停用成功");
                }
                if (corpVOs.length == 1)
                    json.setRows(corpVOs[0]);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                json.setSuccess(false);
                json.setMsg(e instanceof BusinessException
                        ? e.getMessage() : "库存停用失败");
            }
        } else {
            json.setSuccess(false);
            json.setMsg("库存停用失败");
        }
        return ReturnData.ok().data(json);
    }

    @GetMapping("/getTaxWarningRate")
    public ReturnData<Json> getTaxWarningRate(String corpId) {
        Json json = new Json();
        if (StringUtils.isEmpty(corpId)) {
            corpId = SystemUtil.getLoginCorpId();
        }
        DZFDouble rate = sys_corpserv.getTaxWarningRate(corpId);
        json.setData(rate);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/updateTaxWarningRate")
    public ReturnData<Json> updateTaxWarningRate(@RequestBody Map<String, String> data) {
        Json json = new Json();
        String rate = data.get("rate");
        String corpId = data.get("corpId");
        if (StringUtils.isEmpty(corpId)) {
            corpId = SystemUtil.getLoginCorpId();
        }
        sys_corpserv.updateTaxradio(corpId, rate);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }
}
