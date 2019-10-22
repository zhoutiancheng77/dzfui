package com.dzf.zxkj.platform.controller.bdset;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.ISynCpaccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * 标准科目
 * @Author: zpm
 * @Description:
 * @Date:Created by 2019/10/21
 * @Modified By:
 */
@RestController
@RequestMapping("/bdset/gl_synccpacckmact")
@Slf4j
public class SynCpaccountController {

    @Autowired
    private ISynCpaccountService gl_syncpacckmserv;

    /**
     * 查询行业数据
     */
    @GetMapping("/queryHy")
    public ReturnData<Grid> queryHy(@MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();
        try {
            YntCpaccountVO[] vos = gl_syncpacckmserv.getHyKMVOS(corpVO.getPk_corp());
            if (vos == null || vos.length == 0) {
                grid.setTotal(0L);
            } else {
                grid.setTotal(Long.valueOf(vos.length));
                grid.setRows(Arrays.asList(vos));
            }
            grid.setSuccess(true);
            grid.setMsg("查询成功!");
        } catch (Exception e) {
            grid.setMsg("查询失败");
            if(e instanceof BusinessException){
                grid.setMsg(e.getMessage());
            }
            grid.setSuccess(false);
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 查询公司数据
     */
    @PostMapping("/queryGs")
    public ReturnData<Grid> queryGs(@MultiRequestBody("addlist") YntCpaccountVO[] addvos,@MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();
        try {
            YntCpaccountVO[] cpavos = gl_syncpacckmserv.getGsKmVOS(corpVO.getPk_corp(), addvos);
            if (cpavos != null && cpavos.length > 0) {
                grid.setRows(Arrays.asList(cpavos));
            }
            grid.setSuccess(true);
            grid.setMsg("查询成功!");
        } catch (Exception e) {
            grid.setMsg("查询失败");
            if(e instanceof BusinessException){
                grid.setMsg(e.getMessage());
            }
            grid.setSuccess(false);
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(grid);
    }


    /**
     * 同步科目数据
     */
    @PostMapping("/save")
    public ReturnData<Grid> save(@MultiRequestBody("list") YntCpaccountVO[] bodyvos, @MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();
        try {
            if(bodyvos != null && bodyvos.length > 0){
                String reslist = gl_syncpacckmserv.saveCpacountVOS(bodyvos,corpVO.getPk_corp());
                grid.setSuccess(true);
                grid.setMsg(reslist);
            } else {
                grid.setMsg("同步失败");
                grid.setSuccess(false);
            }
        } catch (Exception e) {
            grid.setMsg("同步失败");
            if(e instanceof BusinessException){
                grid.setMsg(e.getMessage());
            }
            grid.setSuccess(false);
            log.error("同步失败", e);
        }
        return ReturnData.ok().data(grid);
    }
}