package com.dzf.zxkj.platform.controller.bdset;

import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.common.IReferenceCheck;
import com.dzf.zxkj.platform.util.Kmschema;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/bdset/gl_cpacckmact")
public class CpaccountController {
    @Autowired
    private ICpaccountService cpaccountService;
    @Autowired
    private IReferenceCheck refchecksrv;

    @GetMapping("query")
    public ReturnData querykm(String isShowFC) {
        Json json = new Json();
        Map<String, List<YntCpaccountVO>> maps = cpaccountService.queryAccountVO(SystemUtil.getLoginUserId(),
                SystemUtil.getLoginCorpId(), isShowFC);
        if (maps != null) {
            json.setRows(maps);
            json.setSuccess(true);
            json.setMsg("查询成功！");
        } else {
            json.setSuccess(false);
            json.setMsg("查询失败！");
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("save")
    public ReturnData save(@RequestBody YntCpaccountVO accvo) {
        Json json = new Json();
        CorpVO corpvo = SystemUtil.getLoginCorpVo();
        accvo.setPk_corp(corpvo.getPk_corp());
        accvo.setPk_corp_accountschema(corpvo.getCorptype());
        accvo.setCoperatorid(SystemUtil.getLoginUserId());
        accvo.setDoperatedate(SystemUtil.getLoginDate().substring(0, 10));
        accvo.setIssyscode(DZFBoolean.FALSE);
        if (!accvo.getIsnum().booleanValue()) {
            accvo.setMeasurename(null);
        }
        if (accvo.getAccountname() != null) {
            // 过滤名称中的空白字符
            accvo.setAccountname(accvo.getAccountname().replaceAll("\\s", ""));
            if (accvo.getAccountname().length() > 50) {
                throw new BusinessException("科目名称超过50个字符!");
            }
        }
        //// 库存模式。 1、代表 由库存推总账。
        //// 0、或者没有代表由总账推库存。
        // 针对老模式库存依旧控制。1403、1405必须为末级科目。
        if (IcCostStyle.IC_ON.equals(corpvo.getBbuildic())) {
            if (corpvo.getIbuildicstyle() == null || corpvo.getIbuildicstyle() == 0) {
                if (Kmschema.isYclbm(corpvo.getCorptype(), accvo.getAccountcode())//原材料
                        || Kmschema.isKcspbm(corpvo.getCorptype(), accvo.getAccountcode())) {//库存商品
                    throw new BusinessException("启用进销存!原材料和库存商品不允许增加下级科目!");
                }
            }
        }
        //启用数量校验
        if (accvo.getIsnum() != null && accvo.getIsnum().booleanValue()) {
            String inv = String.valueOf(accvo.getIsfzhs().charAt(5));
            if ("0".equals(inv) && StringUtils.isBlank(accvo.getMeasurename())) {
                //没有启用存货辅助 并且 计量单位为空，报错。
                throw new BusinessException("请设置具体的计量单位!");
            }
        }
        accvo = cpaccountService.saveNew(accvo);
        json.setMsg("新增成功");
        json.setRows(accvo);
        json.setSuccess(true);
        json.setStatus(200);
        return ReturnData.ok().data(json);
    }

    @PostMapping("update")
    public ReturnData update(@RequestBody YntCpaccountVO data) {
        Json json = new Json();
        // 已在service校验id
        CorpVO corpvo = SystemUtil.getLoginCorpVo();
        data.setPk_corp(corpvo.getPk_corp());
        data.setCoperatorid(SystemUtil.getLoginUserId());
        data.setDoperatedate(SystemUtil.getLoginDate().substring(0, 10));
        if (data.getAccountname() != null) {
            // 过滤名称中的空白字符
            data.setAccountname(data.getAccountname().replaceAll("\\s", ""));
            if (data.getAccountname().length() > 50) {
                throw new BusinessException("科目名称超过50个字符!");
            }
        }
        //启用数量校验
        if (data.getIsnum() != null && data.getIsnum().booleanValue()) {
            String inv = String.valueOf(data.getIsfzhs().charAt(5));
            if ("0".equals(inv) && StringUtils.isBlank(data.getMeasurename())) {
                //没有启用存货辅助 并且 计量单位为空，报错。
                throw new BusinessException("请设置具体的计量单位!");
            }
        }
        cpaccountService.update(data, corpvo);

        json.setMsg("修改成功");
        json.setSuccess(true);
        json.setStatus(200);
        return ReturnData.ok().data(json);
    }

    @PostMapping("delete")
    public ReturnData delete(@RequestBody YntCpaccountVO data) {
        Json json = new Json();
        data.setPk_corp(SystemUtil.getLoginCorpId());
        if (!StringUtils.isEmpty(data.getPrimaryKey())) {
            refchecksrv.isReferencedRefmsg("ynt_cpaccount", data.getPrimaryKey());
        }
        cpaccountService.deleteInfovo(data);
        json.setMsg("删除成功");
        json.setSuccess(true);
        json.setStatus(200);
        return ReturnData.ok().data(json);
    }

    //新增科目 进行父级检查
    public ReturnData preCheckForAddKM(@RequestBody YntCpaccountVO data) {
        Json json = new Json();
        //保存操作预检查
        //onSaveNew.action
        CorpVO corpvo = SystemUtil.getLoginCorpVo();
        //// 库存模式。 1、代表 由库存推总账。
        //// 0、或者没有代表由总账推库存。
        // 针对老模式库存依旧控制。1403、1405必须为末级科目。
        if (IcCostStyle.IC_ON.equals(corpvo.getBbuildic())) {
            if (corpvo.getIbuildicstyle() == null || corpvo.getIbuildicstyle() == 0) {
                if (Kmschema.isYclbm(corpvo.getCorptype(), data.getAccountcode())//原材料
                        || Kmschema.isKcspbm(corpvo.getCorptype(), data.getAccountcode())) {//库存商品
                    throw new BusinessException("启用进销存!原材料和库存商品不允许增加下级科目!");
                }
            }
        }

        //效验级别
        cpaccountService.perCheckForAddKeMu(data, corpvo);
        try {
            // 获取新编码
            String newCode = cpaccountService.getNewSubCode(data, corpvo);
            data.setAccountcode(newCode);
            json.setData(data);
        } catch (Exception e) {
            log.error("获取科目新编码失败", e);
        }

        json.setSuccess(true);
        json.setMsg("效验成功");
        return ReturnData.ok().data(json);
    }

    @GetMapping("/queryByPz")
    public ReturnData queryByPz() {
        Grid grid = new Grid();
        YntCpaccountVO[] vos = cpaccountService
                .queryAccountByPz(SystemUtil.getLoginCorpId());
        if (vos != null && vos.length > 0) {
            grid.setRows(Arrays.asList(vos));
        }
        grid.setSuccess(true);
        grid.setMsg("查询成功!");
        return ReturnData.ok().data(grid);
    }

    @PostMapping("queryAccountRule")
    public ReturnData<Grid> queryAccountRule(@MultiRequestBody CorpVO corpVO) {
        Grid<String> json = new Grid();
        String codeRule = cpaccountService.queryAccountRule(corpVO.getPk_corp());
        json.setMsg(codeRule);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/queryByPkCorp")
    public ReturnData<Grid> queryByPkCorp(String pk_corp) {
        Grid grid = new Grid();
        YntCpaccountVO[] vos = cpaccountService
                .queryAccountByPz(pk_corp);
        if (vos != null && vos.length > 0) {
            grid.setRows(Arrays.asList(vos));
        }
        grid.setSuccess(true);
        grid.setMsg("查询成功!");
        return ReturnData.ok().data(grid);
    }
}
