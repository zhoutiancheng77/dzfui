package com.dzf.zxkj.app.controller;

import com.dzf.admin.dzfapp.model.econtract.AppSealVO;
import com.dzf.admin.dzfapp.model.result.AppResult;
import com.dzf.admin.dzfapp.service.econtract.IDzfAppSealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/dzfapp/seal")
public class ElectronicSealController {

    @Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
    private IDzfAppSealService dzfAppSealService;

    @RequestMapping("/haveSealStatus")
    public AppResult<Boolean> haveSealStatus(AppSealVO sealVO) {
        try {
            return dzfAppSealService.haveSealStatus(sealVO);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;

    }

    @RequestMapping("/updateSealStatus")
    public AppResult updateSealStatus(AppSealVO sealVO) {
        try {
            return dzfAppSealService.confirmSealStatus(sealVO);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    @RequestMapping("/getCorpkSeals")
    public AppResult getCorpkSeals(AppSealVO sealVO) {
        try {
            return dzfAppSealService.getCorpkSeals(sealVO);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    @RequestMapping("/savePersonSign")
    public AppResult savePersonSign(AppSealVO sealVO) {
        try {
            return dzfAppSealService.confirmPersonSign(sealVO, null);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    @RequestMapping("/getSealImg")
    public AppResult<byte[]> getSealImg(AppSealVO sealVO) {
        try {
            return dzfAppSealService.getSealImg(sealVO);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }
}
