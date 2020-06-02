package com.dzf.zxkj.app.controller;

import com.dzf.admin.dzfapp.model.econtract.AppEContQryVO;
import com.dzf.admin.dzfapp.model.result.AppResult;
import com.dzf.admin.dzfapp.service.econtract.IDzfAppEcontractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/dzfapp/econtract")
public class ElectronicContractController {

    @Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
    private IDzfAppEcontractService iDzfAppEcontractService;

    @RequestMapping("/updateSign")
    public AppResult updateSign(AppEContQryVO pamVO) {
        try {
            return iDzfAppEcontractService.confirmSign(pamVO);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    @RequestMapping("/querySignEContDet")
    public AppResult querySignEContDet(AppEContQryVO pamVO) {
        try {
            return iDzfAppEcontractService.querySignEContDet(pamVO);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    @RequestMapping("/queryUnSignEContDet")
    public AppResult queryUnSignEContDet(AppEContQryVO pamVO) {
        try {
            return iDzfAppEcontractService.queryUnSignEContDet(pamVO);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    @RequestMapping("/queryEContract")
    public AppResult queryEContract(AppEContQryVO pamVO) {
        try {
            return iDzfAppEcontractService.queryEContract(pamVO);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }
}
