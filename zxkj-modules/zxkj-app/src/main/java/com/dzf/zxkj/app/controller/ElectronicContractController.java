package com.dzf.zxkj.app.controller;

import com.dzf.admin.dzfapp.model.econtract.AppEContQryVO;
import com.dzf.admin.dzfapp.model.result.AppResult;
import com.dzf.admin.dzfapp.service.econtract.IDzfAppEcontractService;
import com.dzf.zxkj.app.service.pub.IUserPubService;
import com.dzf.zxkj.app.utils.AppkeyUtil;
import com.dzf.zxkj.platform.model.sys.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/dzfapp/econtract")
public class ElectronicContractController {

    @Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
    private IDzfAppEcontractService iDzfAppEcontractService;
    @Autowired
    private IUserPubService userPubService;
    @RequestMapping("/updateSign")
    public AppResult updateSign(@RequestParam Map<String,Object> param) {
        try {

            return iDzfAppEcontractService.confirmSign(changeParamvo(param));
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return  new AppResult(-100,null,e.getMessage());
        }
    }

    @RequestMapping("/querySignEContDet")
    public AppResult querySignEContDet(@RequestParam Map<String,Object> param) {
        try {

            return iDzfAppEcontractService.querySignEContDet(changeParamvo(param));
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return  new AppResult(-100,null,e.getMessage());
        }
    }

    @RequestMapping("/queryUnSignEContDet")
    public AppResult queryUnSignEContDet(@RequestParam Map<String,Object> param) {
        try {

            return iDzfAppEcontractService.queryUnSignEContDet(changeParamvo(param));
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return  new AppResult(-100,null,e.getMessage());
        }
    }

    @RequestMapping("/queryEContract")
    public AppResult queryEContract(@RequestParam Map<String,Object> param) {
        try {

         return iDzfAppEcontractService.queryEContract(changeParamvo(param));
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return  new AppResult(-100,null,e.getMessage());
        }
    }

    private AppEContQryVO changeParamvo( Map<String,Object> param){
        AppEContQryVO pamVO= new AppEContQryVO();
        AppkeyUtil.setAppValue(param,pamVO );
        UserVO uservo = userPubService.queryUserVOId((String)param.get("account_id"));
        pamVO.setCuserid(uservo.getCuserid());
        return pamVO;
    }
}
