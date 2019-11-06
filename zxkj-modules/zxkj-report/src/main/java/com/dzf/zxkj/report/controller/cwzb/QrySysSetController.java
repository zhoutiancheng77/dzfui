package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.qcset.QcYeCurrency;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("gl_rep_qrysysset")
@Slf4j
public class QrySysSetController extends ReportBaseController {

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    /**
     * 获取公司科目
     */
    @GetMapping("querySubjectRef")
    public ReturnData<Grid> getSubjectRef(@RequestParam("corpid") String pk_corp) {
        if (StringUtil.isEmpty(pk_corp)) {
            pk_corp = SystemUtil.getLoginCorpId();
        }
        Grid grid = new Grid();
        try {
            YntCpaccountVO[] bvos = zxkjPlatformService.queryByPk(pk_corp);
            grid.setRows(bvos);
            grid.setSuccess(true);
        } catch (Exception e) {
            log.error("科目查询失败:", e);
            grid.setRows(new ArrayList<AuxiliaryAccountHVO>());
            printErrorLog(grid, e, "科目查询失败!");
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 获取辅助类别参照
     */
    @GetMapping("queryCurrency")
    public ReturnData<Grid> getCurrency(@RequestParam("corpid") String pk_corp) {
        if (StringUtil.isEmpty(pk_corp)) {
            pk_corp = SystemUtil.getLoginCorpId();
        }
        Grid grid = new Grid();
        try {
            QcYeCurrency[] bvos = zxkjPlatformService.queryCurrencyByPkCorp(pk_corp);
            bvos[0].setPk_currency("");//综合本位币去掉
            grid.setRows(bvos);
            grid.setSuccess(true);
        } catch (Exception e) {
            log.error("币种查询失败:", e);
            grid.setRows(new ArrayList<AuxiliaryAccountHVO>());
            printErrorLog(grid, e, "币种查询失败!");
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 获取辅助类别参照
     */
    @GetMapping("queryFzLb")
    public ReturnData<Grid> getFzLb(@RequestParam("corpid") String pk_corp) {
        if (StringUtil.isEmpty(pk_corp)) {
            pk_corp = SystemUtil.getLoginCorpId();
        }
        Grid grid = new Grid();
        try {
            AuxiliaryAccountHVO[] bvos = zxkjPlatformService.queryHByPkCorp(pk_corp);
            grid.setRows(bvos);
            grid.setSuccess(true);
        } catch (Exception e) {
            log.error("辅助类别查询失败:", e);
            grid.setRows(new ArrayList<AuxiliaryAccountHVO>());
            printErrorLog(grid, e, "辅助类别查询失败!");
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 获取辅助项目参照
     */
    @GetMapping("queryFzxm")
    public ReturnData<Grid> getFzxm(@RequestParam("corpid") String pk_corp) {
        if (StringUtil.isEmpty(pk_corp)) {
            pk_corp = SystemUtil.getLoginCorpId();
        }
        Grid grid = new Grid();
        try {
            AuxiliaryAccountBVO[] bvos = zxkjPlatformService.queryAllB(pk_corp);
            grid.setRows(bvos);
            grid.setSuccess(true);
        } catch (Exception e) {
            log.error("辅助项目查询失败:", e);
            grid.setRows(new ArrayList<AuxiliaryAccountHVO>());
            printErrorLog(grid, e, "辅助项目查询失败!");
        }
        return ReturnData.ok().data(grid);
    }





}
