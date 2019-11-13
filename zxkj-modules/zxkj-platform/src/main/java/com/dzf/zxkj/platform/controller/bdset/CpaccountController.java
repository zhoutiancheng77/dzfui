package com.dzf.zxkj.platform.controller.bdset;

import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVOClassify;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.common.IReferenceCheck;
import com.dzf.zxkj.platform.service.sys.IBDCurrencyService;
import com.dzf.zxkj.platform.util.Kmschema;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Slf4j
@RequestMapping("/bdset/gl_cpacckmact")
public class CpaccountController {
    @Autowired
    private ICpaccountService cpaccountService;
    @Autowired
    private IReferenceCheck refchecksrv;
    @Autowired
    private IBDCurrencyService sys_currentserv;

    @GetMapping("query")
    public ReturnData querykm(boolean excludeSealed) {
        Json json = new Json();
        Map<String, List<YntCpaccountVO>> maps = cpaccountService.queryAccountVO(SystemUtil.getLoginUserId(),
                SystemUtil.getLoginCorpId(), excludeSealed);
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
    @PostMapping("/isAllowAdd")
    public ReturnData isAllowAdd(@RequestBody YntCpaccountVO data) {
        Json json = new Json();
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
        cpaccountService.checkBeforeAdd(data.getPk_corp_account(), corpvo);
        json.setSuccess(true);
        json.setMsg("效验成功");
        return ReturnData.ok().data(json);
    }

    @GetMapping("/checkParentRef")
    public ReturnData checkParentRef(String code) {
        Json json = new Json();
        boolean exist = cpaccountService.checkIsQuote(SystemUtil.getLoginCorpId(), code);
        json.setData(exist);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/checkOnEdit")
    public ReturnData checkOnEdit(@RequestBody YntCpaccountVO data) {
        Json json = new Json();
        Map<String, Boolean> checkData = new HashMap<>();
        String corpId = SystemUtil.getLoginCorpId();
        boolean pzRef = cpaccountService.checkIsPzRef(corpId, data.getPk_corp_account());
        boolean qcRef = cpaccountService.checkBeginDataRef(corpId, data.getPk_corp_account());
        boolean parentVerification = cpaccountService.checkParentVerification(corpId, data.getAccountcode());
        if (data.getIswhhs() != null && data.getIswhhs().booleanValue()) {
            boolean currencyRef = cpaccountService.checkCurrencyRef(corpId, data.getPk_corp_account());
            checkData.put("currencyRef", currencyRef);
        }
        checkData.put("ref", pzRef || qcRef);
        checkData.put("parentVerification", parentVerification);
        json.setData(checkData);
        json.setSuccess(true);
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

    @GetMapping("getNewCode")
    public ReturnData<Grid> getNewCode(String code, Integer level) {
        Json json = new Json();
        CorpVO corp = SystemUtil.getLoginCorpVo();
        YntCpaccountVO vo = new YntCpaccountVO();
        vo.setAccountcode(code);
        vo.setAccountlevel(level);
        String newCode = cpaccountService.getNewSubCode(vo, corp);
        json.setSuccess(true);
        json.setData(newCode);
        json.setMsg("获取成功!");
        return ReturnData.ok().data(json);
    }

    // 按公司名称查询
    @GetMapping("queryByPkcorp2")
    public ReturnData queryByPkcorp2(String accindex, String pk_corp) {
        YntCpaccountVOClassify json = new YntCpaccountVOClassify();
        try {
            if (StringUtil.isEmpty(accindex)) {
                json.setStatus(-200);
                json.setSuccess(false);
                json.setMsg("传参数为空！");
            } else {
                CorpVO corp = SystemUtil.getLoginCorpVo();
                if(StringUtil.isEmpty(pk_corp))
                    pk_corp = corp.getPk_corp();
                YntCpaccountVO[] vos = cpaccountService.queryAccountVOSByCorp(
                        pk_corp, Integer.valueOf(accindex));
                json.setStatus(200);
                if (vos == null || vos.length == 0) {
                    json.setRows(new ArrayList<YntCpaccountVO>());
                } else {
                    json.setRows(vos);
                }
                json.setSuccess(true);
                json.setMsg("查询成功!");
            }
        } catch (Exception e) {
            json.setStatus(-200);
            json.setSuccess(false);
            json.setMsg("查询失败!");
        }
        return ReturnData.ok().data(json);
    }

    @GetMapping("getCurrency")
    public ReturnData<Grid> getCurrency() {
        Json json = new Json();
        BdCurrencyVO[] currencies = sys_currentserv.queryCurrency();
        json.setSuccess(true);
        json.setRows(currencies);
        return ReturnData.ok().data(json);
    }

    //科目下级转辅助
    @PostMapping("childrenToAuxiliary")
    public ReturnData childrenToAuxiliary(@RequestBody Map<String, String> param) {
        Json json = new Json();
        String pk_km = param.get("subjectId");
        String pk_fz = param.get("auxiliaryType");
        if (StringUtils.isEmpty(pk_km) || StringUtils.isEmpty(pk_fz)) {
            json.setSuccess(false);
            json.setMsg("科目下级转换辅助失败，参数为空");
        } else {
            String userid = SystemUtil.getLoginUserId();
            String pk_corp = SystemUtil.getLoginCorpId();
            cpaccountService.saveKmzhuanFz(userid, pk_corp, pk_km, pk_fz);
            json.setSuccess(true);
            json.setMsg("科目下级转换辅助成功");
        }
        return ReturnData.ok().data(json);
    }

    // 封存
    @PostMapping("/seal")
    public ReturnData seal(@RequestBody YntCpaccountVO accvo) {
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        accvo.setPk_corp(pk_corp);
        // 只能从末级科目依次封存
        if (!accvo.getIsleaf().booleanValue()) {
            throw new BusinessException("请从末级节点依次操作！");
        }
        // 封存
        cpaccountService.updateSeal(accvo);
        json.setMsg("封存成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/unseal")
    public ReturnData unseal(@RequestBody YntCpaccountVO data) {
        Json json = new Json();
        data.setPk_corp(SystemUtil.getLoginCorpId());
        if (!data.getIsleaf().booleanValue()) {
            throw new BusinessException("请从末级节点依次操作！");
        }
        cpaccountService.updateUnSeal(data);
        json.setMsg("解除封存成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }
}
