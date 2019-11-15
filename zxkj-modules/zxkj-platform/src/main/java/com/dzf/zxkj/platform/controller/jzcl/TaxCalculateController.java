package com.dzf.zxkj.platform.controller.jzcl;

import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.jzcl.SurTaxTemplate;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.SurtaxArchiveVO;
import com.dzf.zxkj.platform.model.tax.SurtaxVO;
import com.dzf.zxkj.platform.model.tax.TaxCalculateVO;
import com.dzf.zxkj.platform.service.tax.ITaxCalculateArchiveService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/end-process/tax-calculate")
@Slf4j
public class TaxCalculateController {
    @Autowired
    private ITaxCalculateArchiveService gl_taxarchive;

    @GetMapping("/query")
    public ReturnData query(String period) {
        Json json = new Json();
        TaxCalculateVO tax = gl_taxarchive.getTax(SystemUtil.getLoginCorpId(), period, false);
        json.setRows(tax);
        json.setSuccess(true);
        json.setMsg("加载成功");
        return ReturnData.ok().data(json);
    }

    @GetMapping("/reFetchData")
    public ReturnData reFetchData(String period) {
        Json json = new Json();
        TaxCalculateVO tax = gl_taxarchive.getTax(SystemUtil.getLoginCorpId(), period, true);
        json.setRows(tax);
        json.setSuccess(true);
        json.setMsg("取数成功");
        return ReturnData.ok().data(json);
    }

    @PostMapping("/save")
    public ReturnData save(@RequestBody TaxCalculateVO taxVO) {
        Json json = new Json();
        gl_taxarchive.saveTax(taxVO,
                SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId());
        json.setRows(taxVO);
        json.setSuccess(true);
        json.setMsg("保存成功");
        return ReturnData.ok().data(json);
    }

    @PostMapping("/createVoucher")
    public ReturnData createVoucher(@RequestBody Map<String, String> param) {
        Json json = new Json();
        String taxJson = param.get("taxInfo");
        String taxType = param.get("taxType");
        TaxCalculateVO taxVO = JsonUtils.deserialize(taxJson, TaxCalculateVO.class);
        gl_taxarchive.createVoucher(taxVO, Integer.valueOf(taxType),
                SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId());
        json.setSuccess(true);
        json.setRows(taxVO);
        json.setMsg("生成成功");
        return ReturnData.ok().data(json);
    }

    @PostMapping("/createVoucherByOtherTax")
    public ReturnData createVoucherByOtherTax(@RequestBody SurtaxVO taxVO) {
        Json json = new Json();
        gl_taxarchive.createVoucherByOtherTax(taxVO, SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId());
        json.setSuccess(true);
        json.setRows(taxVO);
        json.setMsg("生成成功");
        return ReturnData.ok().data(json);
    }

    @GetMapping("/getTaxAmount")
    public ReturnData getTaxAmount(String period) {
        Json json = new Json();
        CorpVO corpVO = SystemUtil.getLoginCorpVo();
        List<FseJyeVO> rs = gl_taxarchive.getTaxAmount(corpVO, period);
        if (rs != null && rs.size() > 0) {
            json.setSuccess(true);
            json.setRows(rs);
        } else {
            json.setSuccess(false);
            json.setRows(new ArrayList<FseJyeVO>());
            json.setMsg("查询数据为空");
        }
        return ReturnData.ok().data(json);
    }

    @GetMapping("/getOtherTaxArchives")
    public ReturnData getOtherTaxArchives() {
        Json json = new Json();
        SurtaxArchiveVO[] rs = gl_taxarchive.getOtherTaxArchives();
        json.setSuccess(true);
        json.setRows(rs);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/querySurtaxTemplate")
    public ReturnData querySurtaxTemplate() {
        Json json = new Json();
        SurTaxTemplate[] rs = gl_taxarchive.querySurtaxTemplate(SystemUtil.getLoginCorpId());
        json.setSuccess(true);
        json.setRows(rs);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/saveSurtaxTemplate")
    public ReturnData saveSurtaxTemplate(@RequestBody SurTaxTemplate temp) {
        Json json = new Json();
        temp = gl_taxarchive.saveSurtaxTemplate(SystemUtil.getLoginCorpId(), temp);
        json.setSuccess(true);
        json.setMsg("保存成功");
        json.setRows(temp);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/saveOtherTax")
    public ReturnData saveOtherTax(@RequestBody SurtaxVO vo) {
        Json json = new Json();
        vo = gl_taxarchive.saveOtherTax(vo, vo.getPk_corp(), vo.getPeriod(), SystemUtil.getLoginCorpId());
        json.setSuccess(true);
        json.setMsg("保存成功");
        json.setRows(vo);
        return ReturnData.ok().data(json);
    }
}
